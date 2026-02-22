package com.mrbysco.radialaggroindicator.client;

import com.mrbysco.radialaggroindicator.AggroIndicatorMod;
import com.mrbysco.radialaggroindicator.network.PacketHandler;
import com.mrbysco.radialaggroindicator.network.message.AggroFinishedPacket;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = AggroIndicatorMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientHandler {

	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.isPaused()) return;
		if (event.phase == TickEvent.Phase.START) return;
		if (mc.player == null || mc.level == null) return;
		HudHandler.activeIndicators.removeIf(indicator -> {
			if (indicator.ticks() <= 0 || !indicator.isAlive() ||
					mc.level.dimension() != indicator.dimension()
			) {
				if (mc.getConnection() != null) {
					PacketHandler.CHANNEL.send(PacketDistributor.SERVER.noArg(),
							new AggroFinishedPacket(indicator.entityId())
					);
				}
				return true;
			}
			return false;
		});
		HudHandler.activeIndicators.forEach(HudHandler.AggroIndicator::decrementTicks);
	}
}
