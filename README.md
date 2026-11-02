# Aurum

A small Fabric mod for Minecraft 1.21.1. Piglins accept a **block of gold** as a barter, and give loot from a new, higher-tier loot table.

## How it works

- Throw a gold block to an adult piglin, or right-click one while holding gold blocks. Like a gold ingot, exactly one block is taken.
- The piglin admires it, then throws loot from `aurum:gameplay/piglin_bartering_high_tier` instead of keeping the block.
- Gold ingots still use the vanilla bartering table. Baby piglins and piglin brutes still don't barter.

The mod is **server-side only**. On a dedicated server, players can join with an unmodded client. It also works in singleplayer.

## Default loot

| Item | Count | Weight | Chance |
|---|---|---|---|
| Gilded Blackstone | 6–12 | 40 | 44.4% |
| Ghast Tear | 4–8 | 20 | 22.2% |
| Magma Cream | 4–8 | 20 | 22.2% |
| Ancient Debris | 1 | 3 | 3.3% |
| Wither Skeleton Skull | 1 | 3 | 3.3% |
| Lava Bucket | 1 | 2 | 2.2% |
| Enchanted Golden Apple | 1 | 1 | 1.1% |
| Music Disc (Pigstep) | 1 | 1 | 1.1% |

## Customizing with a datapack

Both the currency and the loot are plain data files, and a datapack can override them at the same paths:

- **Currency:** `data/aurum/tags/item/high_tier_barter_currency.json` (an item tag; the default is `minecraft:gold_block`).
- **Loot:** `data/aurum/loot_table/gameplay/piglin_bartering_high_tier.json` (a standard `minecraft:barter` loot table).

Changes apply after `/reload`. If the loot table is empty, the piglin takes the block and gives nothing back.

## Requirements

- Minecraft 1.21.1
- Fabric Loader 0.20.1+
- Fabric API
- Java 21

## Building

```bash
./gradlew build       # jar in build/libs/
./gradlew runClient   # launch a dev client
```

## License

CC0-1.0. See [LICENSE](LICENSE).
