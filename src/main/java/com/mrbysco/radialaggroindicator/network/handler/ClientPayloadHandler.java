package com.mrbysco.radialaggroindicator.network.handler;

import com.mrbysco.radialaggroindicator.client.HudHandler;
import com.mrbysco.radialaggroindicator.network.message.IndicateAggroPayload;
import com.mrbysco.radialaggroindicator.network.message.RemoveAggroPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientPayloadHandler {
	private static final ClientPayloadHandler INSTANCE = new ClientPayloadHandler();

	public static ClientPayloadHandler getInstance() {
		return INSTANCE;
	}

	/**
	 * Handle the IndicateAggroPayload, which tells the client to display the aggro indicator for a specific entity for a certain duration.
	 *
	 * @param payload The payload containing the entity ID and duration for which to display the aggro indicator.
	 * @param context The payload context, used for enqueuing work on the client thread and handling exceptions.
	 */
	public void handleIndicateAggro(final IndicateAggroPayload payload, final IPayloadContext context) {
		context.enqueueWork(() -> {
					Minecraft minecraft = Minecraft.getInstance();
					ClientLevel level = minecraft.level;
					if (level == null) return;

					Entity entity = level.getEntity(payload.entityID());
					if (entity == null) return;

					HudHandler.addIndicator(entity, payload.durationTicks());
				})
				.exceptionally(e -> {
					// Handle exception
					context.disconnect(Component.translatable("radialaggro.networking.indicate_aggro.failed", e.getMessage()));
					return null;
				});
	}

	/**
	 * Handle the RemoveAggroPacket, which tells the client to remove the aggro indicator for a specific entity.
	 *
	 * @param payload The payload containing the entity ID for which to remove the aggro indicator.
	 * @param context The payload context, used for enqueuing work on the client thread and handling exceptions.
	 */
	public void handleRemoveAggro(final RemoveAggroPayload payload, final IPayloadContext context) {
		context.enqueueWork(() -> {
					HudHandler.removeIndicator(payload.entityID());
				})
				.exceptionally(e -> {
					// Handle exception
					context.disconnect(Component.translatable("radialaggro.networking.remove_aggro.failed", e.getMessage()));
					return null;
				});
	}
}
