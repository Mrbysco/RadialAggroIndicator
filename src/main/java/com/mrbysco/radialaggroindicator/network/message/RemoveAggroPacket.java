package com.mrbysco.radialaggroindicator.network.message;

import com.mrbysco.radialaggroindicator.network.ClientPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Tell the client that an entity has lost aggro and the indicator should be removed.
 */
public class RemoveAggroPacket {
	private final int entityID;

	public RemoveAggroPacket(int id) {
		this.entityID = id;
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeInt(entityID);
	}

	public static RemoveAggroPacket decode(final FriendlyByteBuf packetBuffer) {
		return new RemoveAggroPacket(packetBuffer.readInt());
	}

	public void handle(Supplier<NetworkEvent.Context> context) {
		NetworkEvent.Context ctx = context.get();
		if (ctx.getDirection().getReceptionSide().isClient()) {
			DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
					ClientPacketHandler.removeAggro(entityID));
		}
		ctx.setPacketHandled(true);
	}

}
