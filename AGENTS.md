# AGENTS.md — mcMMO Repository Guide

> Actionable reference for AI coding agents and new contributors.
> Start with [Build & Test](#build--test), then read [Architecture](#architecture) and [Agent Rules](#agent-rules).

---

## Build & Test

```bash
mvn clean install                  # full build (produces target/mcMMO.jar)
mvn -DskipTests package            # fast iteration
mvn test                           # JUnit 5 (Surefire, Mockito javaagent in pom.xml)
mvn test -DtrimStackTrace=false    # full stack traces on failure
```

SQL tests use **Testcontainers** — a running Docker daemon is required for `mvn test` to pass fully. If Docker is unavailable, SQL-related tests will fail but all other tests will still run.

Deploy locally: copy `target/mcMMO.jar` into a Spigot/Paper server's `plugins/` directory and restart.

---

## Architecture

**Entrypoint**: `mcMMO.java` — plugin lifecycle, config loading, manager initialization, listener and runnable registration.

**Managers** are static singletons on the main class (`DatabaseManager`, `ChunkManager`, `UpgradeManager`). This is legacy; do not create new static singletons.

**Database**: `DatabaseManagerFactory` selects `FlatFileDatabaseManager` or `SQLDatabaseManager` from config. Extension hook: `setCustomDatabaseManagerClass(...)`.

**Commands**: most use Bukkit `CommandExecutor` via `CommandRegistrationManager`. A handful use ACF (`CommandManager.java`), but ACF is unmaintained upstream. **Use Bukkit registration for all new commands.** Declare in `src/main/resources/plugin.yml`.

**Configs & Locales**: resource files in `src/main/resources/`, loaded by classes in `com.gmail.nossr50.config`. Locale files are filtered in `pom.xml` and loaded by `LocaleLoader`.

**Event Handling**: listeners under `listeners/` (Block, Player, Entity, Inventory, World), registered in `mcMMO.java`.

**Scheduling**: `runnables/*` classes scheduled from the main plugin. All scheduling **must** use FoliaLib (`mcMMO.p.getFoliaLib().getScheduler()`), never the raw Bukkit scheduler.

---

## Core Concepts

### Player Data Model

| Type | Scope | Purpose | Access |
|------|-------|---------|--------|
| `PlayerProfile` | Offline-capable | Skill levels, XP, cooldowns, unique data | Database load |
| `McMMOPlayer` | Online only | Profile + party, abilities, tool modes, skill managers, XP bars | `UserManager.getPlayer(player)` |

Use `PlayerProfile` for offline operations. Use `McMMOPlayer` only when the player is online.

### Retro Mode vs Standard Mode

| | Retro (default) | Standard |
|---|---|---|
| Skill range | 0–1,000 (some to 10,000) | 0–100 (some to 1,000) |
| Leveling feel | Frequent small level-ups | Infrequent large level-ups |
| Time to max | Same | Same |

Controlled by `GeneralConfig.getIsRetroMode()` / `mcMMO.isRetroModeEnabled()`. XP formulas: `FormulaManager`. Rank thresholds: `skillranks.yml` (separate Retro/Standard entries). **Nearly all level-dependent logic branches on this flag** — always account for both modes.

### Config Reload Policy

- Configs are loaded **once** during `onEnable()` and treated as immutable. Runtime `/reload` is unsupported.
- **Exception**: `mcmmoreloadlocale` reloads locale strings at runtime via `LocaleLoader`. Be aware locale values can change mid-run.

### Event System & Plugin Interop

- mcMMO fires **custom Bukkit events** (`events/` — experience, skills, party, chat, items) and listens to its own events via `SelfListener` (e.g., level-up → unlock notifications, scoreboard updates).
- **Always respect other plugins**: after firing an event, check `isCancelled()` and honor modifications to event data before continuing.
- The `api/` package provides static facades (`ExperienceAPI`, `PartyAPI`, `AbilityAPI`, `SkillAPI`, `ChatAPI`, `DatabaseAPI`) for third-party integration. Legacy but API-stable — deprecate before changing signatures.

### Configurability

mcMMO is highly configurable with many off-by-default features (scoreboards, hardcore mode, diminished returns). Always check `GeneralConfig` and `*Config` classes for feature toggles and respect them.

---

## Platform & Compatibility

| Dimension | Value |
|-----------|-------|
| Platforms | Spigot, Paper, Folia (`folia-supported: true` in `plugin.yml`) |
| Minecraft versions | 1.20.5 to latest |
| Java | 17 (moving to 25) |
| API preference | Spigot API for compatibility; Paper API only when explicitly needed |

### Version-Compatibility Utilities

Static classes that abstract MC version differences via reflection and pre-computed lookups:

| Class | Purpose |
|-------|---------|
| `MaterialMapStore` | O(1) material category checks via `HashSet<String>` maps filled at startup |
| `AttributeMapper` | Reflection-based `Attribute` constant resolution across MC versions |
| `SoundRegistryUtils` | Reflection-based `Sound` registry lookup with Paper/Spigot fallback |
| `EnchantmentMapper` | Version-varying enchantment resolution |
| `ItemUtils`, `SkillUtils`, `PotionUtil` | Static helpers for items, skills, potions |

Some compat code may be outdated from older MC version support. Remove when the minimum supported version bumps.

---

## Performance & Thread Safety

- **Performance is the #1 priority.** mcMMO runs on high-player-count servers. Prefer pre-computed lookups and caches over runtime calculations.
- **Folia support requires thread safety.** Use `ConcurrentHashMap`, `ConcurrentHashMap.newKeySet()`, or Guava `MapMaker().weakKeys()` for shared mutable state. Reference implementations: `StringUtils` (concurrent caches), `TransientEntityTracker` (concurrent player→summon maps), `MobMetadataUtils` (weak-keyed concurrent map).
- **All scheduling must use FoliaLib** — never the raw Bukkit scheduler. See `scheduleTasks()` in `mcMMO.java` and `SaveTimerTask` for patterns.

---

## Code Style

- **Effective Java 3rd Edition** is the guiding reference. Supplement with modern Java idioms when it offers improvements.
- **Modern Java**: use records, pattern matching, text blocks, enhanced `switch`, `Stream`, `Optional`. Most existing code is pre-Java 8 legacy — do not imitate old patterns. Modernize when touching old code.
- **Functional style preferred**: use Stream API, `Optional`, lambdas, and method references when they improve clarity. Be judicious — **code clarity is the top priority.** If an imperative loop reads more clearly than a stream pipeline, use the loop.
- **Annotations**: `@NotNull` / `@Nullable` (from `org.jetbrains.annotations`) on parameters, return types, and fields.
- **`final`**: prefer on local variables, parameters, and fields.
- **Formatting**: break method parameters across lines for readability; never exceed ~120 columns.
- **Comments**: explain *why*, not *what*. Use Javadoc on public and package-visible methods.
- **Logging**: use `mcMMO.p.getLogger()` or `LogUtils`. Never `System.out` or `e.printStackTrace()`.

---

## Project Conventions

- **Plugin reference**: `mcMMO.p` — legacy global static. Use in existing code, but do not create new static singletons.
- **Feature gating**: runtime toggles from `GeneralConfig` and `*Config` classes, loaded in `mcMMO.onEnable()`.
- **Shading**: `pom.xml` uses `maven-shade-plugin` with relocations (Kyori, ACF, bStats, FoliaLib). New dependencies may need shading and relocation.
- **Locale caching**: `StringUtils` and `LocaleLoader` use `ConcurrentHashMap` for Folia thread safety.
- **External integrations** (optional, via PluginManager): PlaceholderAPI, ProtocolLib, WorldGuard, HealthBar, ProjectKorra.
- **API stability**: mcMMO is a dependency for many plugins. Never remove or change public method signatures without first deprecating them and providing alternatives.

---

## Known Tech Debt

> These areas are functional but fragile. Do not treat them as examples of good design.

- **Alchemy skill**: complex `potions.yml` / `PotionConfig` loading. `PotionConfigGenerator` is dead code. Incrementally improved but remains fragile.
- **Party system**: popular feature with legacy internals needing modernization.
- **Static singletons**: `mcMMO.p` and static manager fields. Deprecate with replacements before removing.
- **Listener classes**: `BlockListener`, `PlayerListener`, `EntityListener` — least refactored legacy code. Large methods, deep nesting, repeated boilerplate.
- **SkillCommand hierarchy**: `SkillCommand` base class and ~19 subclasses use an over-engineered generic locale-template output system. Stores mutable `mmoPlayer` state in `onCommand()` (not thread-safe). Low-priority refactor target — follow the existing pattern when modifying.
- **Permissions + RankUtils**: `Permissions.java` is called on nearly every player action. Many per-ability methods (e.g., `skullSplitter()`, `dodge()`) are redundant — generic `isSubSkillEnabled()` already exists and should be preferred. `Permissions.canUseSubSkill()` is the correct compound check: it verifies both **permission** (`isSubSkillEnabled`) and **level-based unlock** (`RankUtils.hasUnlockedSubskill`). `RankUtils` serves a distinct purpose — it checks a player's skill level against rank thresholds from `skillranks.yml` to determine unlock status and current rank. The tech debt is in `RankUtils`' internals: it uses a lazily-initialized `HashMap` (not thread-safe for Folia), has fragile null-returning call chains via `UserManager.getPlayer()`, and the `count` field for unlock notification scheduling is a static mutable. Future cleanup should consolidate the per-ability permission wrappers into the generic path and make `RankUtils` thread-safe.

## Codebase Patterns

> Established conventions that are not ideal but must be followed for consistency. Prefer these patterns when modifying existing code.

- **Skill class split**: each skill has a stateless static utility (`Axes.java`) and a stateful per-player `SkillManager` subclass (`AxesManager.java`). Confusing but established — follow the pattern.
- **Config loading**: two strategies coexist. `AutoUpdateLegacyConfigLoader` auto-merges missing keys from templates. `BukkitConfig` subclasses (`RepairConfig`, `SalvageConfig`, `TreasureConfig`, `FishingTreasureConfig`) must **never** auto-update — they respect user customization. Verify which strategy applies before making changes.
- **Over-engineered abstractions**: single-implementation interfaces and unnecessary indirection. Being simplified as found — prefer direct, simple designs.

---

## Common Changes

| Task | Steps |
|------|-------|
| Add a command | Create `CommandExecutor` in `commands/`, register in `CommandRegistrationManager`, declare in `src/main/resources/plugin.yml`. **Do not use ACF.** |
| Add DB behavior | Implement in `database/`, expose via `DatabaseManagerFactory` |
| Add config option | Add the key + default value to the appropriate `src/main/resources/*.yml` file, then expose it via a public method in the corresponding `*Config.java` class (e.g., `GeneralConfig`). New standalone config classes are rarely needed. |
| Add a listener | Add the handler method to an existing listener in `listeners/` (Block, Player, Entity, Inventory, World). New listener classes are rarely needed — the existing ones cover broad categories. |

---

## Key Files

All paths relative to `src/main/java/com/gmail/nossr50/` unless noted otherwise.

| File | Purpose |
|------|---------|
| `mcMMO.java` | Plugin lifecycle |
| `pom.xml` *(project root)* | Build, shading, relocations, resource filtering |
| `src/main/resources/plugin.yml` *(project root)* | Plugin metadata, commands, permissions |
| `util/commands/CommandRegistrationManager.java` | Bukkit command registration (primary) |
| `commands/CommandManager.java` | ACF setup (legacy, limited use) |
| `database/DatabaseManagerFactory.java` | DB selection and extension hook |
| `datatypes/player/McMMOPlayer.java` | Online player state |
| `datatypes/player/PlayerProfile.java` | Offline-capable player data |
| `util/MaterialMapStore.java` | Pre-computed material category lookups |
| `util/AttributeMapper.java` | Version-safe attribute resolution |
| `util/sounds/SoundRegistryUtils.java` | Version-safe sound lookups |
| `util/TransientEntityTracker.java` | Thread-safe entity tracking (`ConcurrentHashMap`) |
| `util/MobMetadataUtils.java` | Thread-safe mob metadata (weak-keyed concurrent map) |
| `util/experience/FormulaManager.java` | XP formulas, Retro/Standard caching |
| `util/Permissions.java` | Permission checks (critical, legacy) |
| `util/skills/RankUtils.java` | Subskill rank lookups, unlock checks |
| `commands/skills/SkillCommand.java` | Abstract skill command base (over-engineered) |
| `listeners/SelfListener.java` | Self-listening for mcMMO API events |
| `events/` | Custom Bukkit events (experience, skills, party, chat, items) |
| `api/` | Public API facades for third-party plugins |
| `src/main/resources/skillranks.yml` *(project root)* | Skill rank thresholds (Retro/Standard) |
| `src/main/resources/locale/` *(project root)* | Localization files |
| `src/test/java/` *(project root)* | Unit tests |

---

## PR Expectations

- **One concern per PR.** Each pull request should address a single feature, fix, or refactor. Do not bundle unrelated changes — if two systems are modified, they must be directly dependent on each other.
- **Code clarity above all.** Code should be readable, well-named, and self-explanatory. See [Code Style](#code-style).
- **Include unit tests** for new or changed behavior when feasible.
- **Keep PRs small and reviewable.** Smaller diffs are easier to review and less likely to introduce regressions.

---

## Agent Rules

1. **Do not imitate old code.** Code with git blame before 2020 is not exemplary. Use modern Java 17+ patterns.
2. **API stability is mandatory.** Deprecate before removing public methods — mcMMO is a widely-used dependency.
3. **Performance first.** Avoid allocations in hot paths. Use pre-computed lookups and caches.
4. **Thread safety is required.** mcMMO runs on Folia. Use `ConcurrentHashMap` or equivalent for shared mutable state.
5. **Use FoliaLib for scheduling.** Never use the Bukkit scheduler directly.
6. **Always add unit tests** for new or changed code.
7. **Respect config toggles.** Check `GeneralConfig` and `*Config` classes before implementing feature-dependent logic.
8. **Respect event cancellation.** After firing events, check `isCancelled()` and honor modifications from other plugins.
9. **Use existing static managers** (`mcMMO.p`, etc.) when touching existing code, but do not create new static singletons.
10. **Shade new dependencies.** Follow existing relocation patterns in `pom.xml`.
