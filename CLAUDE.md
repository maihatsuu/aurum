# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Aurum is a Fabric mod for Minecraft 1.21.1 (Java 21, Mojang official mappings, mod id `aurum`, package `maihatsuu.aurum`). Its single goal is to let **piglins accept a gold block as barter currency** and roll a separate, higher-tier loot table. The design doc is `../piglin-barter-mod-plan.md`; read it before implementing features. Loot pool numbers are being drafted in `../Gold Block Bartering Loot Tables.xlsx`.

The repo is currently the unmodified Fabric example-mod template (`ExampleMixin`, `ExampleClientMixin`, placeholder `fabric.mod.json` description/authors/contact). Replace those as real code lands.

## Commands

```bash
./gradlew build          # compile + remap; jar in build/libs/
./gradlew runServer      # dedicated dev server (run/ directory)
./gradlew runClient      # dev client
./gradlew genSources     # decompile MC sources for browsing/verifying method names
```

There is no test suite configured. CI (`.github/workflows/build.yml`) just runs `./gradlew build`.

## Architecture / intended design

- **Server-side only.** No client code is needed; vanilla clients must be able to join. Keep logic in `src/main`. The `src/client` source set (from Loom `splitEnvironmentSourceSets()`) and `aurum.client.mixins.json` exist only because of the template. They can be removed, along with the `client` entrypoint and client mixin config in `fabric.mod.json`.
- **Hook points are in `net.minecraft.world.entity.monster.piglin.PiglinAi`** (Mojang names):
  - `isBarterCurrency(ItemStack)`: extend to accept items in the tag `aurum:high_tier_barter_currency` (contains `minecraft:gold_block`).
  - `getBarterResponseItems` / `stopHoldingOffHandItem`: pick the loot table by the offhand item. Use `aurum:gameplay/piglin_bartering_high_tier` for the currency tag and vanilla `BuiltInLootTables.PIGLIN_BARTERING` for everything else.
  - Check that `pickUpItem` and `mobInteract` send gold blocks down the barter path (offhand + admire), not the "loved item kept in inventory" path. Gold blocks are already in `ItemTags.PIGLIN_LOVED`.
  - Verify these names against 1.21.1 sources (`genSources`) before writing mixins. The plan explicitly flags them as unverified.
- **Data-driven tuning.** The currency is an item tag (`data/aurum/tags/item/high_tier_barter_currency.json`) and the loot is a loot table (`data/aurum/loot_table/gameplay/piglin_bartering_high_tier.json`; 1.21 uses singular `loot_table`/`item` folder names). Look up the loot table by `ResourceKey` on every barter; never cache it, so datapack overrides and `/reload` take effect. An empty table is valid (the piglin takes the block and gives nothing).
- **Mixin compatibility.** Other mods also mix into `PiglinAi`. Use `@Inject`, `@ModifyReturnValue`, or `@WrapOperation` (MixinExtras ships with Fabric Loader), never `@Overwrite`. Mixin configs set `defaultRequire: 1`, so an injector that matches nothing fails at startup.
- **Invariants to keep:** baby piglins and brutes don't barter; vanilla barter advancements still trigger; only one block is taken from a stack.
- **Config (later):** a simple JSON config with an on/off toggle (plus cooldown settings if one is added). Cloth Config/YACL and Mod Menu are optional extras only.

Suggested build order (from the plan): currency acceptance with the vanilla table, then a separate table, then verify a datapack override + `/reload`, then fill and tune the pool, then edge cases/cooldown, then config.
