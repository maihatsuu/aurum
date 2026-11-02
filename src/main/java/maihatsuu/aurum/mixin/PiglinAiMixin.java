package maihatsuu.aurum.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import maihatsuu.aurum.Aurum;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(PiglinAi.class)
public abstract class PiglinAiMixin {
	/**
	 * Single gate for bartering: covers right-click hand-over (canAdmire), ground pickup (wantsToPickup)
	 * and the barter-vs-keep decision when admiring ends (stopHoldingOffHandItem).
	 */
	@ModifyReturnValue(method = "isBarterCurrency", at = @At("RETURN"))
	private static boolean aurum$acceptHighTierCurrency(boolean original, ItemStack stack) {
		return original || stack.is(Aurum.HIGH_TIER_BARTER_CURRENCY);
	}

	/**
	 * Vanilla clears the offhand before calling getBarterResponseItems(piglin), so the bartered stack
	 * is only available as a local at this call site.
	 */
	@WrapOperation(
			method = "stopHoldingOffHandItem",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/piglin/PiglinAi;getBarterResponseItems(Lnet/minecraft/world/entity/monster/piglin/Piglin;)Ljava/util/List;")
	)
	private static List<ItemStack> aurum$rollHighTierTable(Piglin piglin, Operation<List<ItemStack>> original,
			@Local ItemStack bartered) {
		if (!bartered.is(Aurum.HIGH_TIER_BARTER_CURRENCY) || !(piglin.level() instanceof ServerLevel level)) {
			return original.call(piglin);
		}
		// Looked up every time so datapack overrides and /reload always apply.
		LootTable table = level.getServer().reloadableRegistries().getLootTable(Aurum.HIGH_TIER_BARTER_LOOT);
		LootParams params = new LootParams.Builder(level)
				.withParameter(LootContextParams.THIS_ENTITY, piglin)
				.create(LootContextParamSets.PIGLIN_BARTER);
		return table.getRandomItems(params);
	}
}
