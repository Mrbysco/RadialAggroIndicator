package com.mrbysco.radialaggroindicator.client;

import com.mrbysco.radialaggroindicator.AggroIndicatorMod;
import com.mrbysco.radialaggroindicator.network.message.AggroFinishedPayload;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

@EventBusSubscriber(value = Dist.CLIENT, modid = AggroIndicatorMod.MOD_ID)
public class ClientHandler {

	@SubscribeEvent
	public static void onClientTick(ClientTickEvent.Post event) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.isPaused()) return;
		if (mc.player == null || mc.level == null) return;
		HudHandler.activeIndicators.removeIf(indicator -> {
			if (indicator.ticks() <= 0 || !indicator.isAlive() ||
					mc.level.dimension() != indicator.dimension()
			) {
				if (mc.getConnection() != null) {
					ClientPacketDistributor.sendToServer(new AggroFinishedPayload(indicator.entityId()));
				}
				return true;
			}
			return false;
		});
		HudHandler.activeIndicators.forEach(HudHandler.AggroIndicator::decrementTicks);
	}
}
