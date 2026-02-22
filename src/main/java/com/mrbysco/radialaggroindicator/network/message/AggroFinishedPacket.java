package com.mrbysco.radialaggroindicator.network.message;

import com.mrbysco.radialaggroindicator.AggroIndicatorMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Tells the server that the client has finished processing the aggro indication, allowing for new indications for the same entity to be sent if needed.
 */
public class AggroFinishedPacket {
	private final int entityID;

	public AggroFinishedPacket(int id) {
		this.entityID = id;
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeInt(entityID);
	}

	public static AggroFinishedPacket decode(final FriendlyByteBuf packetBuffer) {
		return new AggroFinishedPacket(packetBuffer.readInt());
	}

	public void handle(Supplier<NetworkEvent.Context> context) {
		NetworkEvent.Context ctx = context.get();
		if (ctx.getDirection().getReceptionSide().isServer() && ctx.getSender() != null) {
			ServerPlayer player = ctx.getSender();
			Entity entity = player.serverLevel().getEntity(entityID);
			if (entity != null) {
				AggroIndicatorMod.finishAggro(entityID);
			}
		}
		ctx.setPacketHandled(true);
	}

}
