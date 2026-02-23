package com.mrbysco.radialaggroindicator.datagen;

import com.mrbysco.radialaggroindicator.datagen.client.AggroLanguageProvider;
import com.mrbysco.radialaggroindicator.datagen.server.AggroEntityTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber
public class AggroDatagen {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent.Client event) {
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

		generator.addProvider(true, new AggroLanguageProvider(packOutput));

		generator.addProvider(true, new AggroEntityTagsProvider(packOutput, lookupProvider));
	}
}
