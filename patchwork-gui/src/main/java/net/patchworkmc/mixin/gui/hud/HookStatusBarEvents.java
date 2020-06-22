/*
 * Minecraft Forge, Patchwork Project
 * Copyright (c) 2016-2020, 2019-2020
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.patchworkmc.mixin.gui.hud;

import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.AIR;
import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.ARMOR;
import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.FOOD;
import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.HEALTH;
import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.HEALTHMOUNT;

import java.util.Random;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraftforge.client.event.net.minecraftforge.client.ForgeIngameGui;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

import net.patchworkmc.impl.gui.PatchworkGui;

@Mixin(InGameHud.class)
public abstract class HookStatusBarEvents extends DrawableHelper {
	@Shadow
	protected abstract PlayerEntity getCameraPlayer();

	@Shadow
	private int ticks;

	@Shadow
	@Final
	private Random random;

	@Shadow
	private int scaledWidth;

	@Shadow
	private int scaledHeight;

	@Shadow
	@Final
	private MinecraftClient client;

	@Shadow
	protected abstract LivingEntity getRiddenEntity();

	@Shadow
	protected abstract int method_1744(LivingEntity livingEntity);

	@Shadow
	protected abstract int method_1733(int i);

	@Shadow
	private long field_2032;

	@Shadow
	private int field_2014;

	@Shadow
	private long field_2012;

	@Shadow
	private int field_2033;

	/**
	 * Forge changes the order of rendering armor and health.
	 *
	 * <p>The Forge system for this is sane at a high level, but much of the internal code is still confusing Vanilla GUI code
	 * with customizable heights hacked in.</p>
	 * @author TheGlitch76
	 */
	@Overwrite
	private void renderStatusBars() {
		if (ForgeIngameGui.renderHealth) {
			renderHealth(this.scaledWidth, this.scaledHeight);
		}

		if (ForgeIngameGui.renderArmor) {
			renderArmor(this.scaledWidth, this.scaledHeight);
		}

		if (ForgeIngameGui.renderFood) {
			renderFood(this.scaledWidth, this.scaledHeight);
		}

		if (ForgeIngameGui.renderHealthMount) {
			renderHealthMount(this.scaledWidth, this.scaledHeight);
		}

		if (ForgeIngameGui.renderAir) {
			renderAir(this.scaledWidth, this.scaledHeight);
		}
	}

	@Unique
	public void renderHealth(int width, int height) {
		bind(GUI_ICONS_LOCATION);

		if (PatchworkGui.pre(HEALTH)) {
			return;
		}

		client.getProfiler().push("health");
		GlStateManager.enableBlend();

		PlayerEntity player = (PlayerEntity) this.client.getCameraEntity();
		int health = MathHelper.ceil(player.getHealth());
		boolean highlight = field_2032 > (long) ticks && (field_2032 - (long) ticks) / 3L % 2L == 1L;

		if (health < this.field_2014 && player.timeUntilRegen > 0) {
			this.field_2012 = Util.getMeasuringTimeMs();
			this.field_2032 = this.ticks + 20L;
		} else if (health > this.field_2014 && player.timeUntilRegen > 0) {
			this.field_2012 = Util.getMeasuringTimeMs();
			this.field_2032 = this.ticks + 10L;
		}

		if (Util.getMeasuringTimeMs() - this.field_2012 > 1000L) {
			this.field_2014 = health;
			this.field_2033 = health;
			this.field_2012 = Util.getMeasuringTimeMs();
		}

		this.field_2014 = health;
		int healthLast = this.field_2033;

		EntityAttributeInstance attrMaxHealth = player.getAttributeInstance(EntityAttributes.MAX_HEALTH);
		float healthMax = (float) attrMaxHealth.getValue();
		float absorption = MathHelper.ceil(player.getAbsorptionAmount());

		int healthRows = MathHelper.ceil((healthMax + absorption) / 2.0F / 10.0F);
		int rowHeight = Math.max(10 - (healthRows - 2), 3);

		this.random.setSeed(ticks * 312871L);

		int left = width / 2 - 91;
		int top = height - ForgeIngameGui.left_height;
		ForgeIngameGui.left_height += (healthRows * rowHeight);

		if (rowHeight != 10) {
			ForgeIngameGui.left_height += 10 - rowHeight;
		}

		int regen = -1;

		if (player.hasStatusEffect(StatusEffects.REGENERATION)) {
			regen = ticks % 25;
		}

		final int TOP = 9 * (client.world.getLevelProperties().isHardcore() ? 5 : 0);
		final int BACKGROUND = (highlight ? 25 : 16);
		int margin = 16;

		if (player.hasStatusEffect(StatusEffects.POISON)) {
			margin += 36;
		} else if (player.hasStatusEffect(StatusEffects.WITHER)) {
			margin += 72;
		}

		float absorptionRemaining = absorption;

		for (int i = MathHelper.ceil((healthMax + absorption) / 2.0F) - 1; i >= 0; --i) {
			int row = MathHelper.ceil((float) (i + 1) / 10.0F) - 1;
			int x = left + i % 10 * 8;
			int y = top - row * rowHeight;

			if (health <= 4) {
				y += random.nextInt(2);
			}

			if (i == regen) {
				y -= 2;
			}

			blit(x, y, BACKGROUND, TOP, 9, 9);

			if (highlight) {
				if (i * 2 + 1 < healthLast) {
					blit(x, y, margin + 54, TOP, 9, 9); //6
				} else if (i * 2 + 1 == healthLast) {
					blit(x, y, margin + 63, TOP, 9, 9); //7
				}
			}

			if (absorptionRemaining > 0.0F) {
				if (absorptionRemaining == absorption && absorption % 2.0F == 1.0F) {
					blit(x, y, margin + 153, TOP, 9, 9); //17
					absorptionRemaining -= 1.0F;
				} else {
					blit(x, y, margin + 144, TOP, 9, 9); //16
					absorptionRemaining -= 2.0F;
				}
			} else {
				if (i * 2 + 1 < health) {
					blit(x, y, margin + 36, TOP, 9, 9); //4
				} else if (i * 2 + 1 == health) {
					blit(x, y, margin + 45, TOP, 9, 9); //5
				}
			}
		}

		GlStateManager.disableBlend();
		client.getProfiler().pop();
		PatchworkGui.post(HEALTH);
	}

	@Unique
	protected void renderArmor(int width, int height) {
		if (PatchworkGui.pre(ARMOR)) {
			return;
		}

		client.getProfiler().push("armor");

		GlStateManager.enableBlend();
		int left = width / 2 - 91;
		int top = height - ForgeIngameGui.left_height;

		int level = client.player.getArmor();

		for (int i = 1; level > 0 && i < 20; i += 2) {
			if (i < level) {
				blit(left, top, 34, 9, 9, 9);
			} else if (i == level) {
				blit(left, top, 25, 9, 9, 9);
			} else {
				blit(left, top, 16, 9, 9, 9);
			}

			left += 8;
		}

		ForgeIngameGui.left_height += 10;

		GlStateManager.disableBlend();
		client.getProfiler().pop();
		PatchworkGui.post(ARMOR);
	}

	@Unique
	public void renderFood(int width, int height) {
		if (PatchworkGui.pre(FOOD)) {
			return;
		}

		client.getProfiler().push("food");

		GlStateManager.enableBlend();
		int left = width / 2 + 91;
		int top = height - ForgeIngameGui.right_height;
		ForgeIngameGui.right_height += 10;

		HungerManager manager = client.player.getHungerManager();
		int level = manager.getFoodLevel();

		for (int i = 0; i < 10; ++i) {
			int idx = i * 2 + 1;
			int x = left - i * 8 - 9;
			int y = top;
			int icon = 16;
			byte background = 0;

			if (client.player.hasStatusEffect(StatusEffects.HUNGER)) {
				icon += 36;
				background = 13;
			}

			if (manager.getSaturationLevel() <= 0.0F && ticks % (level * 3 + 1) == 0) {
				y = top + (random.nextInt(3) - 1);
			}

			blit(x, y, 16 + background * 9, 27, 9, 9);

			if (idx < level) {
				blit(x, y, icon + 36, 27, 9, 9);
			} else if (idx == level) {
				blit(x, y, icon + 45, 27, 9, 9);
			}
		}

		GlStateManager.disableBlend();
		client.getProfiler().pop();
		PatchworkGui.post(FOOD);
	}

	@Unique
	protected void renderHealthMount(int width, int height) {
		PlayerEntity player = (PlayerEntity) client.getCameraEntity();
		Entity vehicle = player.getVehicle();

		if (!(vehicle instanceof LivingEntity)) {
			return;
		}

		bind(GUI_ICONS_LOCATION);

		if (PatchworkGui.pre(HEALTHMOUNT)) {
			return;
		}

		int left_align = width / 2 + 91;

		client.getProfiler().swap("mountHealth");
		GlStateManager.enableBlend();
		LivingEntity mount = (LivingEntity) vehicle;
		int health = (int) Math.ceil(mount.getHealth());
		float healthMax = mount.getMaximumHealth();
		int hearts = (int) (healthMax + 0.5F) / 2;

		if (hearts > 30) {
			hearts = 30;
		}

		final int MARGIN = 52;
		final int HALF = MARGIN + 45;
		final int FULL = MARGIN + 36;

		for (int heart = 0; hearts > 0; heart += 20) {
			int top = height - ForgeIngameGui.right_height;

			int rowCount = Math.min(hearts, 10);
			hearts -= rowCount;

			for (int i = 0; i < rowCount; ++i) {
				int x = left_align - i * 8 - 9;
				blit(x, top, MARGIN, 9, 9, 9);

				if (i * 2 + 1 + heart < health) {
					blit(x, top, FULL, 9, 9, 9);
				} else if (i * 2 + 1 + heart == health) {
					blit(x, top, HALF, 9, 9, 9);
				}
			}

			ForgeIngameGui.right_height += 10;
		}

		GlStateManager.disableBlend();
		PatchworkGui.post(HEALTHMOUNT);
	}

	@Unique
	protected void renderAir(int width, int height) {
		if (PatchworkGui.pre(AIR)) {
			return;
		}

		client.getProfiler().push("air");
		GlStateManager.enableBlend();
		PlayerEntity player = (PlayerEntity) this.client.getCameraEntity();

		int left = width / 2 + 91;
		int top = height - ForgeIngameGui.right_height;

		int air = player.getAir();

		if (player.isInFluid(FluidTags.WATER) || air < 300) {
			int full = MathHelper.ceil((double) (air - 2) * 10.0D / 300.0D);
			int partial = MathHelper.ceil((double) air * 10.0D / 300.0D) - full;

			for (int i = 0; i < full + partial; ++i) {
				blit(left - i * 8 - 9, top, (i < full ? 16 : 25), 18, 9, 9);
			}

			ForgeIngameGui.right_height += 10;
		}

		GlStateManager.disableBlend();
		client.getProfiler().pop();
		PatchworkGui.post(AIR);
	}

	@Unique
	private void bind(Identifier identifier) {
		client.getTextureManager().bindTexture(identifier);
	}
}
