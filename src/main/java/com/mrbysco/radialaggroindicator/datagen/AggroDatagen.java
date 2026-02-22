package com.mrbysco.radialaggroindicator.datagen;

import com.mrbysco.radialaggroindicator.datagen.client.AggroLanguageProvider;
import com.mrbysco.radialaggroindicator.datagen.server.AggroEntityTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class AggroDatagen {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
		ExistingFileHelper helper = event.getExistingFileHelper();

		generator.addProvider(event.includeClient(), new AggroLanguageProvider(packOutput));

		generator.addProvider(event.includeServer(), new AggroEntityTagsProvider(packOutput, lookupProvider, helper));
	}
}
