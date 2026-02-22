package com.mrbysco.radialaggroindicator.network.message;

import com.mrbysco.radialaggroindicator.network.ClientPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Tell the client that an entity has become aggressive towards them, prompting the client to display the aggro indicator for that entity.
 */
public class IndicateAggroPacket {
	private final int entityID;
	private final int durationTicks;

	public IndicateAggroPacket(int id, int durationTicks) {
		this.entityID = id;
		this.durationTicks = durationTicks;
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeInt(entityID);
		buf.writeInt(durationTicks);
	}

	public static IndicateAggroPacket decode(final FriendlyByteBuf packetBuffer) {
		return new IndicateAggroPacket(packetBuffer.readInt(), packetBuffer.readInt());
	}

	public void handle(Supplier<NetworkEvent.Context> context) {
		NetworkEvent.Context ctx = context.get();
		if (ctx.getDirection().getReceptionSide().isClient()) {
			DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
					ClientPacketHandler.handleAggro(entityID, durationTicks));
		}
		ctx.setPacketHandled(true);
	}

}
