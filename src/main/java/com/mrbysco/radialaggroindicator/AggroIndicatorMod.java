package com.mrbysco.radialaggroindicator;

import com.mojang.logging.LogUtils;
import com.mrbysco.radialaggroindicator.config.IndicatorConfig;
import com.mrbysco.radialaggroindicator.network.PacketHandler;
import com.mrbysco.radialaggroindicator.network.message.IndicateAggroPayload;
import com.mrbysco.radialaggroindicator.network.message.RemoveAggroPayload;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

@Mod(AggroIndicatorMod.MOD_ID)
public class AggroIndicatorMod {
	public static final String MOD_ID = "radialaggro";
	public static final Logger LOGGER = LogUtils.getLogger();

	public static final TagKey<EntityType<?>> UNFADING = TagKey.create(Registries.ENTITY_TYPE, modLoc("unfading"));
	public static final TagKey<EntityType<?>> BLACKLIST = TagKey.create(Registries.ENTITY_TYPE, modLoc("blacklist"));

	public AggroIndicatorMod(IEventBus eventBus, Dist dist, ModContainer container) {
		container.registerConfig(ModConfig.Type.COMMON, IndicatorConfig.commonSpec);

		eventBus.addListener(PacketHandler::setupPackets);

		NeoForge.EVENT_BUS.register(this);

		if (dist.isClient()) {
			container.registerConfig(ModConfig.Type.CLIENT, IndicatorConfig.clientSpec);
			container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
		}
	}

	// List to keep track of entities that have already been indicated as aggroed to prevent spamming packets for the same entity
	private static final List<Integer> knownAggroEntities = new ArrayList<>();

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onAggro(LivingChangeTargetEvent event) {
		if (event.getEntity() instanceof Mob mob && !mob.level().isClientSide()) {
			if (event.getNewAboutToBeSetTarget() instanceof Player player) {
				if (IndicatorConfig.COMMON.initialAggro.get() && mob.getTarget() == player) return;
				if (isInvalid(mob.getType())) return;

				if (player instanceof ServerPlayer serverPlayer && !(player instanceof FakePlayer)) {
					if (knownAggroEntities.contains(mob.getId())) {
						return;
					}
					int duration = mob.getType().is(UNFADING) ? Integer.MAX_VALUE : IndicatorConfig.COMMON.indicatorDuration.get();
					PacketDistributor.sendToPlayer(serverPlayer, new IndicateAggroPayload(mob.getId(), duration));
					knownAggroEntities.add(mob.getId());
				}
			} else {
				if (event.getNewAboutToBeSetTarget() == null && mob.getTarget() instanceof Player player) {
					if (player instanceof ServerPlayer serverPlayer && !(player instanceof FakePlayer)) {
						PacketDistributor.sendToPlayer(serverPlayer, new RemoveAggroPayload(mob.getId()));
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

	public static Identifier modLoc(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
