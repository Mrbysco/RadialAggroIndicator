package com.mrbysco.radialaggroindicator.network.message;

import com.mrbysco.radialaggroindicator.AggroIndicatorMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Tell the client that an entity has become aggressive towards them, prompting the client to display the aggro indicator for that entity.
 */
public record IndicateAggroPayload(int entityID, int durationTicks) implements CustomPacketPayload {
	public static final StreamCodec<FriendlyByteBuf, IndicateAggroPayload> CODEC = StreamCodec.composite(
			ByteBufCodecs.INT,
			IndicateAggroPayload::entityID,
			ByteBufCodecs.INT,
			IndicateAggroPayload::durationTicks,
			IndicateAggroPayload::new);
	public static final CustomPacketPayload.Type<IndicateAggroPayload> ID = new CustomPacketPayload.Type<>(AggroIndicatorMod.modLoc("indicate_aggro"));

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return ID;
	}

}
