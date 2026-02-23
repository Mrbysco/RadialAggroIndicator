package com.mrbysco.radialaggroindicator.network.message;

import com.mrbysco.radialaggroindicator.AggroIndicatorMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

/**
 * Tell the client that an entity has lost aggro and the indicator should be removed.
 */
public record RemoveAggroPayload(int entityID) implements CustomPacketPayload {
	public static final StreamCodec<FriendlyByteBuf, RemoveAggroPayload> CODEC = StreamCodec.composite(
			ByteBufCodecs.INT,
			RemoveAggroPayload::entityID,
			RemoveAggroPayload::new);
	public static final CustomPacketPayload.Type<RemoveAggroPayload> ID = new CustomPacketPayload.Type<>(AggroIndicatorMod.modLoc("remove_aggro"));

	@NotNull
	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}
