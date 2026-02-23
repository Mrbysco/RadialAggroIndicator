package com.mrbysco.radialaggroindicator.network.message;

import com.mrbysco.radialaggroindicator.AggroIndicatorMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

/**
 * Tells the server that the client has finished processing the aggro indication, allowing for new indications for the same entity to be sent if needed.
 */
public record AggroFinishedPayload(int entityID) implements CustomPacketPayload {
	public static final StreamCodec<FriendlyByteBuf, AggroFinishedPayload> CODEC = StreamCodec.composite(
			ByteBufCodecs.INT,
			AggroFinishedPayload::entityID,
			AggroFinishedPayload::new);
	public static final Type<AggroFinishedPayload> ID = new Type<>(AggroIndicatorMod.modLoc("aggro_finished"));

	@NotNull
	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}
