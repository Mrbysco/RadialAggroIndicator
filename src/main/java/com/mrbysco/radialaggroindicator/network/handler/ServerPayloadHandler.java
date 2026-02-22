package com.mrbysco.radialaggroindicator.network.handler;

import com.mrbysco.radialaggroindicator.AggroIndicatorMod;
import com.mrbysco.radialaggroindicator.network.message.AggroFinishedPayload;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerPayloadHandler {
	public static final ServerPayloadHandler INSTANCE = new ServerPayloadHandler();

	public static ServerPayloadHandler getInstance() {
		return INSTANCE;
	}

	/**
	 * Handle the AggroFinishedPayload, which tells the server that the client has finished processing the aggro indication for a specific entity, allowing for new indications for the same entity to be sent if needed.
	 *
	 * @param payload The payload containing the entity ID for which the client has finished processing the aggro indication.
	 * @param context The payload context, used for enqueuing work on the server thread and handling exceptions.
	 */
	public void handleFinished(final AggroFinishedPayload payload, final IPayloadContext context) {
		context.enqueueWork(() -> {
					//Execute craft if button is pressed
					if (context.player() != null) {
						Player player = context.player();
						Entity entity = player.level().getEntity(payload.entityID());
						if (entity != null) {
							AggroIndicatorMod.finishAggro(payload.entityID());
						}
					}
				})
				.exceptionally(e -> {
					// Handle exception
					context.disconnect(Component.translatable("radialaggro.networking.aggro_finished.failed", e.getMessage()));
					return null;
				});
	}
}
