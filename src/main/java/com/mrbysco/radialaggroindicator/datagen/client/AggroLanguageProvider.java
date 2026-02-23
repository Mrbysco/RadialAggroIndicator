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
		addConfig("general", "General", "General Settings");
		addConfig("initialAggro", "Initial Aggro", "If true the indicator will be shown when a mob first targets the player");
		addConfig("indicatorDuration", "Indicator Duration", "The duration in ticks that the aggro indicator will be shown for");
		addConfig("hideInView", "Hide in View", "If true the indicator will be hidden when the entity is within the player's field of view");
		addConfig("invertBlacklist", "Invert Blacklist", "If true the blacklist will be inverted to a whitelist, meaning only entities in the list will show the indicator");

		addConfig("client", "Client", "Client Settings");
		addConfig("radiusScale", "Radius Scale", "The scale of the radius of the aggro indicator. Higher values will make the indicator appear further away from the center of the screen");
		addConfig("symbol", "Symbol", "The symbol used for the aggro indicator. This can be any single character or a string of characters");
		addConfig("symbolColorRed", "Symbol Color Red", "The red value of the symbol color rgb");
		addConfig("symbolColorGreen", "Symbol Color Green", "The green value of the symbol color rgb");
		addConfig("symbolColorBlue", "Symbol Color Blue", "The blue value of the symbol color rgb");
		addConfig("symbolRotationLock", "Symbol Rotation Lock", "If true the symbol will not rotate to match the direction of the entity, it will always be upright");
		addConfig("symbolScale", "Symbol Scale", "The scale of the symbol used for the aggro indicator. Higher values will make the symbol larger");
		addConfig("fadeIn", "Fade In", "If true the indicator will fade in when it appears");
		addConfig("fadeOut", "Fade Out", "If true the indicator will fade out when it disappears");
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
