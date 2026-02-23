package com.mrbysco.radialaggroindicator.client;

import com.mrbysco.radialaggroindicator.AggroIndicatorMod;
import com.mrbysco.radialaggroindicator.config.IndicatorConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import org.joml.Matrix3x2fStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EventBusSubscriber(value = Dist.CLIENT, modid = AggroIndicatorMod.MOD_ID)
public class HudHandler {
	protected static final List<HudHandler.AggroIndicator> activeIndicators = new ArrayList<>();

	/**
	 * Adds a new aggro indicator for the given entity with the specified duration in ticks.
	 * If an indicator for the same entity already exists, it will not be added again.
	 *
	 * @param entity The entity that has aggroed the player, for which the indicator should be displayed.
	 * @param ticks  The duration in ticks for which the indicator should be displayed. After this duration, the indicator will be removed automatically.
	 */
	public static void addIndicator(Entity entity, int ticks) {
		// Only add if the entity type isn't already in the list
		if (activeIndicators.stream().noneMatch(indicator -> indicator.entityId() == entity.getId())) {
			activeIndicators.add(new AggroIndicator(entity.getId(), entity, ticks));
		}
	}

	/**
	 * Removes the indicator for the given entity ID. If fadeOut is enabled, it will start fading out instead of immediately removing.
	 *
	 * @param entityId The ID of the entity whose indicator should be removed.
	 */
	public static void removeIndicator(int entityId) {
		if (IndicatorConfig.CLIENT.fadeOut.get()) {
			activeIndicators.stream().filter(indicator -> indicator.entityId() == entityId)
					.findFirst()
					.ifPresent(indicator -> indicator.setTicks(20));
		} else {
			activeIndicators.removeIf(indicator -> indicator.entityId() == entityId);
		}
	}

	@SubscribeEvent
	public static void onRegisterOverlay(RegisterGuiLayersEvent event) {
		event.registerBelowAll(AggroIndicatorMod.modLoc("aggro_indicator"), HudHandler::onRenderOverlay);
	}

	/**
	 * Displays an aggro arrow at pointing towards the entity that aggroed the player. The arrow will disappear after 4 seconds or when the entity dies.
	 *
	 * @param guiGraphics  The GuiGraphics instance, used to draw the arrow and other graphics on the screen.
	 * @param deltaTracker The DeltaTracker instance, used to track time between frames for smooth animations and timing.
	 */
	private static void onRenderOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
		Minecraft minecraft = Minecraft.getInstance();
		ClientLevel level = minecraft.level;
		if (level != null) {
			Player player = minecraft.player;
			if (player == null) return;

			for (AggroIndicator indicator : activeIndicators) {
				if (!indicator.isAlive()) continue;
				if (IndicatorConfig.COMMON.hideInView.get() && indicator.hideVisible(minecraft.levelRenderer.getCapturedFrustum()))
					return;

				renderIndicator(guiGraphics, minecraft, player, indicator);
			}
		}
	}

	/**
	 * Renders a single aggro indicator arrow for the given entity.
	 *
	 * @param guiGraphics Graphics instance used for rendering.
	 * @param minecraft   Minecraft instance for font access.
	 * @param player      The client player.
	 * @param indicator   The aggro indicator to render.
	 */
	private static void renderIndicator(GuiGraphics guiGraphics, Minecraft minecraft,
	                                    Player player, AggroIndicator indicator) {
		final Entity target = indicator.getEntity();
		final int tickCount = indicator.tickCount();
		final int ticks = indicator.ticks();

		double dx = target.getX() - player.getX();
		double dz = target.getZ() - player.getZ();

		double angle = calculateScreenAngle(player, dx, dz);

		int centerX = guiGraphics.guiWidth() / 2;
		int centerY = guiGraphics.guiHeight() / 2;

		double scale = IndicatorConfig.CLIENT.radiusScale.get();

		float baselineFactor = 0.625f;
		int radius = (int) (centerY * baselineFactor * scale);

		int arrowX = (int) (centerX + Math.sin(angle) * radius);
		int arrowY = (int) (centerY - Math.cos(angle) * radius);

		final int red = IndicatorConfig.CLIENT.symbolColorRed.get();
		final int green = IndicatorConfig.CLIENT.symbolColorGreen.get();
		final int blue = IndicatorConfig.CLIENT.symbolColorBlue.get();
		int color = ARGB.color(
				calculateAlpha(dx, dz, ticks, tickCount),
				red, green, blue
		);

		drawArrow(guiGraphics, minecraft, arrowX, arrowY, angle, color);
	}

	/**
	 * Calculates the screen-space angle from the player to a world-space offset.
	 *
	 * @param player The player.
	 * @param dx     X distance to target.
	 * @param dz     Z distance to target.
	 * @return Angle in radians for screen-space rotation.
	 */
	private static double calculateScreenAngle(Player player, double dx, double dz) {
		double yawRad = Math.toRadians(player.getYRot());

		double cos = Math.cos(-yawRad);
		double sin = Math.sin(-yawRad);

		// Rotate into camera space
		double localX = dx * cos - dz * sin;
		double localZ = dx * sin + dz * cos;

		return Math.atan2(-localX, localZ);
	}

	/**
	 * Calculates arrow transparency based on distance.
	 * Uses smoothstep for a softer fade curve.
	 *
	 * @param dx        X distance to target.
	 * @param dz        Z distance to target.
	 * @param ticks     Remaining ticks for the indicator.
	 * @param tickCount Total ticks the indicator was created with.
	 * @return Alpha value (0-255).
	 */
	private static int calculateAlpha(double dx, double dz, int ticks, int tickCount) {
		final boolean fadeIn = IndicatorConfig.CLIENT.fadeIn.get();
		final boolean fadeOut = IndicatorConfig.CLIENT.fadeOut.get();

		double dist = Math.sqrt(dx * dx + dz * dz);

		double maxDistance = 40.0;
		double fade = 1.0 - Math.min(dist / maxDistance, 1.0);

		// Smoothstep fade
		fade = fade * fade * (3.0 - 2.0 * fade);

		double alphaMultiplier = 1.0;

		int fadeTicks = 20;

		// Fade In (first 20 ticks)
		if (fadeIn && ticks < fadeTicks) {
			alphaMultiplier *= (double) ticks / fadeTicks;
		}

		// Fade Out (last 20 ticks)
		if (fadeOut && ticks > tickCount - fadeTicks) {
			int ticksRemaining = tickCount - ticks;
			alphaMultiplier *= (double) ticksRemaining / fadeTicks;
		}
		return (int) (255 * fade * alphaMultiplier);
	}

	/**
	 * Draws the arrow at the given screen position with rotation.
	 *
	 * @param guiGraphics Graphics context.
	 * @param minecraft   Minecraft instance.
	 * @param x           Screen X position.
	 * @param y           Screen Y position.
	 * @param angle       Rotation angle in radians.
	 * @param color       ARGB color.
	 */
	private static void drawArrow(GuiGraphics guiGraphics, Minecraft minecraft,
	                              int x, int y, double angle, int color) {
		Matrix3x2fStack poseStack = guiGraphics.pose();
		poseStack.pushMatrix();

		poseStack.translate(x, y);
		boolean rotationLock = IndicatorConfig.CLIENT.symbolRotationLock.get();
		if (!rotationLock) {
			poseStack.rotate((float) angle);
		}

		float scale = IndicatorConfig.CLIENT.symbolScale.get().floatValue();
		if (scale != 1.0F) {
			poseStack.scale(scale);
		}

		String symbol = IndicatorConfig.CLIENT.symbol.get();
		guiGraphics.drawString(minecraft.font, Component.literal(symbol), -3, -3, color);

		poseStack.popMatrix();
	}

	protected static final class AggroIndicator {
		private final int entityId;
		private final Entity entity;
		private int tickCount;
		private int ticks;

		public AggroIndicator(int entityId, Entity entity, int ticks) {
			this.entityId = entityId;
			this.entity = entity;

			this.tickCount = ticks;
			this.ticks = ticks;
		}

		public int entityId() {
			return entityId;
		}

		@SuppressWarnings("BooleanMethodIsAlwaysInverted")
		public boolean isAlive() {
			return entity.isAlive();
		}

		public ResourceKey<Level> dimension() {
			return entity.level().dimension();
		}

		public Entity getEntity() {
			return entity;
		}

		public int tickCount() {
			return tickCount;
		}

		public int ticks() {
			return ticks;
		}

		public void decrementTicks() {
			this.ticks--;
		}

		public void setTicks(int ticks) {
			if (ticks > 0) {
				this.tickCount = ticks * 2;
			}
			this.ticks = ticks;
		}

		public boolean hideVisible(Frustum frustum) {
			boolean hideVisible = false;
			if (IndicatorConfig.COMMON.hideInView.get()) {
				if (frustum.isVisible(entity.getBoundingBox())) {
					hideVisible = true;
				}
			}
			return hideVisible;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this) return true;
			if (obj == null || obj.getClass() != this.getClass()) return false;
			var that = (AggroIndicator) obj;
			return Objects.equals(this.entity.getType(), that.entity.getType()) &&
					this.entityId == that.entityId &&
					this.tickCount == that.tickCount &&
					this.ticks == that.ticks;
		}

		@Override
		public int hashCode() {
			return Objects.hash(entityId, entity.getType(), tickCount, ticks);
		}

		@Override
		public String toString() {
			return "AggroIndicator[" +
					"entityId=" + entityId + ", " +
					"entity=" + entity.getType() + ", " +
					"tickCount=" + tickCount + ", " +
					"ticks=" + ticks + ']';
		}
	}
}
