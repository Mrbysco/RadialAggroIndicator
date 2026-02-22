package com.mrbysco.radialaggroindicator.datagen.client;

import com.mrbysco.radialaggroindicator.AggroIndicatorMod;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.jetbrains.annotations.Nullable;

public class AggroLanguageProvider extends LanguageProvider {

	public AggroLanguageProvider(PackOutput packOutput) {
		super(packOutput, AggroIndicatorMod.MOD_ID, "en_us");
	}

	@Override
	protected void addTranslations() {

	}

	/**
	 * Add the translation for a config entry
	 *
	 * @param path        The path of the config entry
	 * @param name        The name of the config entry
	 * @param description The description of the config entry (optional in case of targeting "title" or similar entries that have no tooltip)
	 */
	private void addConfig(String path, String name, @Nullable String description) {
		this.add(AggroIndicatorMod.MOD_ID + ".configuration." + path, name);
		if (description != null && !description.isEmpty())
			this.add(AggroIndicatorMod.MOD_ID + ".configuration." + path + ".tooltip", description);
	}
}
