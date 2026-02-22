package com.mrbysco.radialaggroindicator.network;

import com.mrbysco.radialaggroindicator.client.HudHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;

public class ClientPacketHandler {
	/**
	 * Handle the packet sent by the server when an entity gains aggro. This will add an indicator to the HUD for the entity.
	 *
	 * @param entityId The ID of the entity that has gained aggro.
	 * @param ticks    The duration in ticks for which the indicator should be displayed. After this duration, the indicator will be removed automatically.
	 */
	public static void handleAggro(int entityId, int ticks) {
		Minecraft minecraft = Minecraft.getInstance();
		ClientLevel level = minecraft.level;
		if (level == null) return;

		Entity entity = level.getEntity(entityId);
		if (entity == null) return;

		HudHandler.addIndicator(entity, ticks);
	}

	/**
	 * Handle the packet sent by the server when an entity loses aggro. This will remove the indicator from the HUD for the entity.
	 *
	 * @param entityId The ID of the entity that has lost aggro.
	 */
	public static void removeAggro(int entityId) {
		HudHandler.removeIndicator(entityId);
	}
}
