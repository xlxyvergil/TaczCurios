# TaczCurios v1.3.0

## ✨ Curio Fusion Upgrade
A Warframe-inspired curio upgrade system. Use **CoreFusion** materials to upgrade curios, with stats scaling by level.
- Max levels by rarity: Common 5 / Uncommon 8 / Rare 10 / Epic 12
- Cost formula: `EBC × (2^level - 1)`, EBC increases with rarity
- Stat growth: `base × (1 + level × 0.8)`
- New **Fusion Vessel**: store/withdraw CoreFusion, decompose unwanted curios
- Tooltip shows real-time upgraded stats

## 🔧 System Refactoring
- **Player data migrated to Capability**: fixed progress loss on death/dimension switch
- **Achievement system rewrite**: unified progress logic, entity tag support for kill detection
- **Curio effect refactor**: per-instance stat calculation (fusion level aware)
- **Weapon restriction system**: unified validation, more precise weapon type matching

## 🐛 Bug Fixes
- Fusion level calculation, recipe tags, document encoding, data sync, and more

## ⚙️ Configuration
- Full set of configurable fusion upgrade options (max levels, cost multiplier, stat growth rate, etc.)
