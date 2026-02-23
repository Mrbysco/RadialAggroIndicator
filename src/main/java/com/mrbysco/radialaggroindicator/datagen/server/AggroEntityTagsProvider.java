package com.mrbysco.radialaggroindicator.datagen.server;

import com.mrbysco.radialaggroindicator.AggroIndicatorMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class AggroEntityTagsProvider extends EntityTypeTagsProvider {
	public AggroEntityTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
		super(output, provider, AggroIndicatorMod.MOD_ID);
	}

	@Override
	protected void addTags(@NotNull HolderLookup.Provider provider) {
		this.tag(AggroIndicatorMod.UNFADING);
		this.tag(AggroIndicatorMod.BLACKLIST);
	}
}
