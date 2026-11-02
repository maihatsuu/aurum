# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Aurum is a Fabric mod for Minecraft 1.21.1 (Java 21, Mojang official mappings, mod id `aurum`, package `maihatsuu.aurum`). Piglins accept a **gold block** as barter currency and roll a separate, higher-tier loot table. The original design doc is `../piglin-barter-mod-plan.md` (outside the repo). Loot numbers were drafted in `../Gold Block Bartering Loot Tables.xlsx`. In that sheet Excel turned count ranges into dates, e.g. 46120 = "4-8".

## Commands

```bash
./gradlew build          # compile + remap; jar in build/libs/
./gradlew runClient      # dev client (run/ directory, gitignored)
./gradlew runServer      # dev dedicated server
./gradlew genSources     # decompile MC sources for verifying method names
```

There is no test suite. CI (`.github/workflows/build.yml`) runs `./gradlew build`. A successful build does **not** validate mixin targets. They're only checked when `PiglinAi` loads at runtime (for example when a piglin spawns), and `defaultRequire: 1` makes a missed injector crash the game.

## Architecture

The mod is server-side only. There is a single source set (the template's `src/client` was removed), so vanilla clients can join.

All behaviour lives in `mixin/PiglinAiMixin.java`, with two hooks into `net.minecraft.world.entity.monster.piglin.PiglinAi`:

1. **`@ModifyReturnValue` on `isBarterCurrency`.** This is the single gate vanilla uses for right-click hand-over (`canAdmire` → `mobInteract`), ground pickup (`wantsToPickup`/`pickUpItem`), and the barter-vs-keep decision in `stopHoldingOffHandItem`. Returning true for the tag `aurum:high_tier_barter_currency` gives you all three. Vanilla already takes one item at a time, and already excludes babies (`isAdult`) and brutes (which use `PiglinBruteAi`).
2. **`@WrapOperation` on the `getBarterResponseItems(Piglin)` call inside `stopHoldingOffHandItem`.** Vanilla clears the offhand *before* it calls `getBarterResponseItems`, so the bartered stack is only available as a local at that call site (captured with MixinExtras `@Local`). A high-tier stack rolls `Aurum.HIGH_TIER_BARTER_LOOT`, looked up in `reloadableRegistries()` on every barter so datapack overrides and `/reload` apply. Don't cache it.

Tuning is done in data, not code. The tag is `data/aurum/tags/item/high_tier_barter_currency.json` and the table is `data/aurum/loot_table/gameplay/piglin_bartering_high_tier.json` (type `minecraft:barter`; 1.21 uses singular folder names). An empty table is valid: the piglin takes the block and gives nothing.

There is no config file. To disable or retune the feature, change the tag or the table with a datapack (an empty tag turns the feature off).

Other mods also mix into `PiglinAi`. Use `@Inject`, `@ModifyReturnValue`, or `@WrapOperation`, never `@Overwrite`.
