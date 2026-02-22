package com.mrbysco.radialaggroindicator.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class IndicatorConfig {
	public static class Common {
		public final ModConfigSpec.BooleanValue initialAggro;
		public final ModConfigSpec.IntValue indicatorDuration;
		public final ModConfigSpec.BooleanValue hideInView;
		public final ModConfigSpec.BooleanValue invertBlacklist;

		Common(ModConfigSpec.Builder builder) {
			//General settings
			builder.comment("General settings")
					.push("general");

			initialAggro = builder
					.comment("If true the indicator will be shown when a mob first targets the player (default: false)")
					.define("initialAggro", false);

			indicatorDuration = builder
					.comment("The duration in ticks that the aggro indicator will be shown for (default: 200, 10 seconds)")
					.defineInRange("indicatorDuration", 200, 1, Integer.MAX_VALUE);

			hideInView = builder
					.comment("If true the indicator will be hidden when the entity is within the player's field of view (default: false)")
					.define("hideInView", false);

			invertBlacklist = builder
					.comment("If true the blacklist will be inverted to a whitelist, meaning only entities in the list will show the indicator (default: false)")
					.define("invertBlacklist", false);

			builder.pop();
		}
	}

	public static final ModConfigSpec commonSpec;
	public static final Common COMMON;

	static {
		final Pair<Common, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Common::new);
		commonSpec = specPair.getRight();
		COMMON = specPair.getLeft();
	}

	public static class Client {

		public final ModConfigSpec.DoubleValue radiusScale;
		public final ModConfigSpec.ConfigValue<String> symbol;
		public final ModConfigSpec.IntValue symbolColorRed;
		public final ModConfigSpec.IntValue symbolColorGreen;
		public final ModConfigSpec.IntValue symbolColorBlue;
		public final ModConfigSpec.BooleanValue symbolRotationLock;
		public final ModConfigSpec.DoubleValue symbolScale;
		public final ModConfigSpec.BooleanValue fadeIn;
		public final ModConfigSpec.BooleanValue fadeOut;

		Client(ModConfigSpec.Builder builder) {
			//Client settings
			builder.comment("Client settings")
					.push("client");

			radiusScale = builder
					.comment("The scale of the radius of the aggro indicator. Higher values will make the indicator appear further away from the center of the screen (default: 1.0F)")
					.defineInRange("radiusScale", 1.0D, 0.01D, 1D);

			symbol = builder
					.comment("The symbol used for the aggro indicator. This can be any single character or a string of characters (default: \"^\")")
					.define("symbol", "^");

			symbolColorRed = builder
					.comment("The red value of the symbol color rgb (default: 255)")
					.defineInRange("symbolColorRed", 255, 0, 255);

			symbolColorGreen = builder
					.comment("The green value of the symbol color rgb (default: 0)")
					.defineInRange("symbolColorGreen", 0, 0, 255);

			symbolColorBlue = builder
					.comment("The blue value of the symbol color rgb (default: 0)")
					.defineInRange("symbolColorBlue", 0, 0, 255);

			symbolRotationLock = builder
					.comment("If true the symbol will not rotate to match the direction of the entity, it will always be upright (default: false)")
					.define("symbolRotationLock", false);

			symbolScale = builder
					.comment("The scale of the symbol used for the aggro indicator. Higher values will make the symbol larger (default: 2.0D)")
					.defineInRange("symbolScale", 2.0D, 0.01D, 10D);

			fadeIn = builder
					.comment("If true the indicator will fade in when it appears (default: true)")
					.define("fadeIn", true);

			fadeOut = builder
					.comment("If true the indicator will fade out when it disappears (default: true)")
					.define("fadeOut", true);

			builder.pop();
		}
	}

	public static final ModConfigSpec clientSpec;
	public static final Client CLIENT;

	static {
		final Pair<Client, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Client::new);
		clientSpec = specPair.getRight();
		CLIENT = specPair.getLeft();
	}
}
