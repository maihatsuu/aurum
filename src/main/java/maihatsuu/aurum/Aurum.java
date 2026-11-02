package maihatsuu.aurum;

import net.fabricmc.api.ModInitializer;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootTable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Aurum implements ModInitializer {
	public static final String MOD_ID = "aurum";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	/** Items piglins accept as high-tier barter currency (gold block by default). */
	public static final TagKey<Item> HIGH_TIER_BARTER_CURRENCY =
			TagKey.create(Registries.ITEM, id("high_tier_barter_currency"));

	/** Loot table rolled when a piglin finishes admiring high-tier currency. Looked up on every barter. */
	public static final ResourceKey<LootTable> HIGH_TIER_BARTER_LOOT =
			ResourceKey.create(Registries.LOOT_TABLE, id("gameplay/piglin_bartering_high_tier"));

	@Override
	public void onInitialize() {
		AurumConfig.load();
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
}
