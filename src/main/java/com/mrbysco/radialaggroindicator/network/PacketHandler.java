package com.mrbysco.radialaggroindicator.network;

import com.mrbysco.radialaggroindicator.AggroIndicatorMod;
import com.mrbysco.radialaggroindicator.network.message.AggroFinishedPacket;
import com.mrbysco.radialaggroindicator.network.message.IndicateAggroPacket;
import com.mrbysco.radialaggroindicator.network.message.RemoveAggroPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {

	private static final String PROTOCOL_VERSION = "1";
	public static SimpleChannel CHANNEL;

	private static int id = 0;

	public static void init() {
		CHANNEL = NetworkRegistry.ChannelBuilder
				.named(new ResourceLocation(AggroIndicatorMod.MOD_ID, "main"))
				.networkProtocolVersion(() -> PROTOCOL_VERSION)
				.clientAcceptedVersions(PROTOCOL_VERSION::equals)
				.serverAcceptedVersions(PROTOCOL_VERSION::equals)
				.simpleChannel();

		CHANNEL.messageBuilder(IndicateAggroPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(IndicateAggroPacket::encode)
				.decoder(IndicateAggroPacket::decode)
				.consumerMainThread(IndicateAggroPacket::handle)
				.add();

		CHANNEL.messageBuilder(RemoveAggroPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(RemoveAggroPacket::encode)
				.decoder(RemoveAggroPacket::decode)
				.consumerMainThread(RemoveAggroPacket::handle)
				.add();

		CHANNEL.messageBuilder(AggroFinishedPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
				.encoder(AggroFinishedPacket::encode)
				.decoder(AggroFinishedPacket::decode)
				.consumerMainThread(AggroFinishedPacket::handle)
				.add();
	}
}
