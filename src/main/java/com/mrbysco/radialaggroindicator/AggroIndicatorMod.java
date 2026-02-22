package com.mrbysco.radialaggroindicator;

import com.mojang.logging.LogUtils;
import com.mrbysco.radialaggroindicator.config.IndicatorConfig;
import com.mrbysco.radialaggroindicator.network.PacketHandler;
import com.mrbysco.radialaggroindicator.network.message.IndicateAggroPacket;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.PacketDistributor;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

@Mod(AggroIndicatorMod.MOD_ID)
public class AggroIndicatorMod {
	public static final String MOD_ID = "radialaggro";
	public static final Logger LOGGER = LogUtils.getLogger();

	public static final TagKey<EntityType<?>> UNFADING = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(MOD_ID, "unfading"));
	public static final TagKey<EntityType<?>> BLACKLIST = TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation(MOD_ID, "blacklist"));

	public AggroIndicatorMod() {
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		ModLoadingContext context = ModLoadingContext.get();
		context.registerConfig(ModConfig.Type.COMMON, IndicatorConfig.commonSpec);
		context.registerConfig(ModConfig.Type.CLIENT, IndicatorConfig.clientSpec);

		eventBus.addListener(this::onCommonSetup);

		MinecraftForge.EVENT_BUS.register(this);
	}

	private void onCommonSetup(final FMLCommonSetupEvent event) {
		PacketHandler.init();
	}

	// List to keep track of entities that have already been indicated as aggroed to prevent spamming packets for the same entity
	private static final List<Integer> knownAggroEntities = new ArrayList<>();

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onAggro(LivingChangeTargetEvent event) {
		if (event.getEntity() instanceof Mob mob && !mob.level().isClientSide()) {
			if (event.getNewTarget() instanceof Player player) {
				if (IndicatorConfig.COMMON.initialAggro.get() && mob.getTarget() == event.getOriginalTarget()) return;
				if (isInvalid(mob.getType())) return;

				if (player instanceof ServerPlayer serverPlayer && !(player instanceof FakePlayer)) {
					if (knownAggroEntities.contains(mob.getId())) {
						return;
					}
					int duration = mob.getType().is(UNFADING) ? Integer.MAX_VALUE : IndicatorConfig.COMMON.indicatorDuration.get();
					PacketHandler.CHANNEL.send(
							PacketDistributor.PLAYER.with(() -> serverPlayer),
							new IndicateAggroPacket(mob.getId(), duration)
					);
					knownAggroEntities.add(mob.getId());
				}
			} else {
				if (event.getNewTarget() == null && mob.getTarget() instanceof Player player) {
					if (player instanceof ServerPlayer serverPlayer && !(player instanceof FakePlayer)) {
						PacketHandler.CHANNEL.send(
								PacketDistributor.PLAYER.with(() -> serverPlayer),
								new IndicateAggroPacket(mob.getId(), 0)
						);
						knownAggroEntities.removeIf(id -> id == mob.getId());
					}
				}
			}
		}
	}

	private boolean isInvalid(EntityType<?> entityType) {
		boolean isBlacklisted = entityType.is(BLACKLIST);
		return IndicatorConfig.COMMON.invertBlacklist.get() != isBlacklisted;
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onLeave(PlayerEvent.PlayerLoggedOutEvent event) {
		LOGGER.info("Clearing known aggro entities");
		knownAggroEntities.clear();
	}

	/**
	 * Removes the entity ID from the list of known aggro entities, allowing it to be indicated again in the future if it aggroes the player again.
	 *
	 * @param entityID The ID of the entity that has finished aggroing the player and should be removed from the known aggro entities list.
	 */
	public static void finishAggro(int entityID) {
		knownAggroEntities.removeIf(id -> id == entityID);
	}
}
