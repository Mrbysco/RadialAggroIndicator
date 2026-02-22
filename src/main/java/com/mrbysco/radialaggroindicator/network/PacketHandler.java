package com.mrbysco.radialaggroindicator.network;

import com.mrbysco.radialaggroindicator.AggroIndicatorMod;
import com.mrbysco.radialaggroindicator.network.handler.ClientPayloadHandler;
import com.mrbysco.radialaggroindicator.network.handler.ServerPayloadHandler;
import com.mrbysco.radialaggroindicator.network.message.AggroFinishedPayload;
import com.mrbysco.radialaggroindicator.network.message.IndicateAggroPayload;
import com.mrbysco.radialaggroindicator.network.message.RemoveAggroPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class PacketHandler {

	public static void setupPackets(final RegisterPayloadHandlersEvent event) {
		final PayloadRegistrar registrar = event.registrar(AggroIndicatorMod.MOD_ID);

		registrar.playToClient(IndicateAggroPayload.ID, IndicateAggroPayload.CODEC, ClientPayloadHandler.getInstance()::handleIndicateAggro);
		registrar.playToClient(RemoveAggroPayload.ID, RemoveAggroPayload.CODEC, ClientPayloadHandler.getInstance()::handleRemoveAggro);
		registrar.playToServer(AggroFinishedPayload.ID, AggroFinishedPayload.CODEC, ServerPayloadHandler.getInstance()::handleFinished);
	}
}
