# AGENTS.md: mcMMO Repository Guide

> Actionable reference for AI coding agents and new contributors.
> Start with [Build & Test](#build--test), then read [Architecture](#architecture) and [Agent Rules](#agent-rules).

---

## Build & Test

```bash
mvn clean install                  # full build (produces target/mcMMO.jar)
mvn -DskipTests package            # fast iteration
mvn test                           # JUnit 5 (Surefire, Mockito javaagent in pom.xml); no Docker needed
mvn test -Psql-tests               # also runs @Tag("docker") suites; requires a running Docker daemon
mvn test -Dtest=SomeTest           # one class; tag exclusions still apply
```

**Database changes must be tested with Docker.** Any change to database code (the `database/` package: `SQLDatabaseManager`, `FlatFileDatabaseManager`, `DatabaseManager`, conversions, queries, schema and upgrades) must pass `mvn test -Psql-tests` locally before it counts as done. Docker is required for this, not optional: the SQL suites run against real MySQL and MariaDB containers, and plain `mvn test` skips them. If Docker is not running, start it; if it cannot be started, report the change as untested rather than done. CI running the suites later does not replace running them yourself.

Test tags are excluded through `surefire.excludedGroups` in `pom.xml` (default `skip,stress,docker`). Profiles: `sql-tests` (adds `docker`), `stress-tests` (adds `stress`), `all-heavy-tests` (both).

- `docker`: `SQLDatabaseManagerTest` and `LeaderboardPlaceholderSqlIntegrationTest` (Testcontainers MySQL 8.0 and MariaDB 10.11, started once per JVM by `SharedSqlContainers`), plus `FlatFileDatabaseManagerTest`, which needs no Docker but carries the tag. Plain `mvn test` never runs it: use `-Psql-tests` when touching FlatFile code.
- `stress`: `McMMORegionBackupStoreStressTest`.
- CI (`.github/workflows/maven.yml`) runs `mvn verify -B -Psql-tests` on JDK 17, only when `src/**`, `pom.xml` or the workflow changes. It is a backstop, not a substitute for running the Docker suites locally.
- Stack traces are already untrimmed (`trimStackTrace=false` in `pom.xml`).

Deploy locally: copy `target/mcMMO.jar` into a Spigot/Paper server's `plugins/` directory and restart.

---

## Architecture

**Entrypoint**: `mcMMO.java`: plugin lifecycle, config loading, manager initialization, listener and runnable registration.

**Managers** are static singletons on the main class (`DatabaseManager`, `ChunkManager`, `UpgradeManager`). This is legacy; do not create new static singletons.

**Database**: `DatabaseManagerFactory` selects `FlatFileDatabaseManager` or `SQLDatabaseManager` from config. Extension hook: `setCustomDatabaseManagerClass(...)` (third-party plugins supply their own `DatabaseManager`).

**Commands**: most are Bukkit `CommandExecutor`s wired from the `COMMAND_SPECS` table in `CommandRegistrationManager`; skill commands (`/mining`, ...) are wired by a loop over `PrimarySkillType`. Three use ACF via `CommandManager.java` (`/mmopower`, `/adminchat`, `/partychat`), and ACF is unmaintained upstream. **Use Bukkit registration for all new commands.** Declare every command in `src/main/resources/plugin.yml`; `CommandRegistrationManagerTest` fails when `plugin.yml` and the wiring drift apart.

**Configs & Locales**: resource files in `src/main/resources/`, loaded by classes in `com.gmail.nossr50.config` and by `LocaleLoader`. See [Codebase Patterns](#codebase-patterns) and [Locales](#locales).

**Event Handling**: listeners under `listeners/` (`BlockListener`, `PlayerListener`, `EntityListener`, `InventoryListener`, `WorldListener`, `ChunkListener`, `SelfListener`), registered in `mcMMO.registerEvents()`.

**Scheduling**: tasks in `runnables/` extend `CancellableRunnable` and are scheduled through FoliaLib (`mcMMO.p.getFoliaLib().getScheduler()`), never the raw Bukkit scheduler. Work that touches an entity or block uses `runAtEntity*` / `runAtLocation*` (that region's thread on Folia); `runNextTick`, `runLater` and `runTimer` run on Folia's global region thread.

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

Controlled by `GeneralConfig.getIsRetroMode()` / `mcMMO.isRetroModeEnabled()`. XP formulas: `FormulaManager`. Rank thresholds: `skillranks.yml` (separate Retro/Standard entries). **Nearly all level-dependent logic branches on this flag**: always account for both modes.

### Config Reload Policy

- Configs are loaded **once** during `onEnable()`. Server `/reload` and plugin hot-reload are unsupported; do not add code for them.
- `/mcmmoreloadlocale` (alias `/mcreloadlocale`) swaps locale strings at runtime via `LocaleLoader`, so locale values can change mid-run.
- A few runtime setters change config state in memory: `/xprate` (`ExperienceConfig.setExperienceGainsGlobalMultiplier`) and the hardcore/vampirism setters in `GeneralConfig`.

### Event System & Plugin Interop

- mcMMO fires **custom Bukkit events** (`events/`: experience, skills, party, chat, items) and listens to its own events via `SelfListener` (e.g., level-up → unlock notifications, scoreboard updates).
- **Always respect other plugins**: after firing an event, check `isCancelled()` and honor modifications to event data before continuing.
- The `api/` package provides static facades (`ExperienceAPI`, `PartyAPI`, `AbilityAPI`, `SkillAPI`, `ChatAPI`, `DatabaseAPI`, `LevelUpCommandAPI`) for third-party integration. Legacy but API-stable: deprecate before changing signatures.

### Configurability

mcMMO is highly configurable with many off-by-default features (scoreboards, hardcore mode, diminished returns). Always check `GeneralConfig` and `*Config` classes for feature toggles and respect them.

---

## Platform & Compatibility

| Dimension | Value |
|-----------|-------|
| Platforms | Spigot, Paper, Folia (`folia-supported: true` in `plugin.yml`) |
| Minecraft versions | 1.20.5 to latest (`api-version: 1.20.5`) |
| Compile API | `spigot-api` 1.20.5 (`spigot.version` in `pom.xml`) |
| Java | 17 (moving to 25) |
| API preference | Spigot API for compatibility; Paper API only when explicitly needed |

Anything added to Bukkit after 1.20.5 does not exist at compile time. Reach it through reflection or the utilities below, and gate version-specific behavior with `mcMMO.getMinecraftGameVersion().isAtLeast(major, minor, patch)`.

### Version-Compatibility Utilities

Utilities that abstract MC version differences via reflection and pre-computed lookups:

| Class | Purpose |
|-------|---------|
| `MaterialMapStore` | O(1) material category checks. Materials are stored as lowercase key strings (`"pale_oak_leaves"`) in `HashSet<String>`s filled at startup, so blocks newer than the compile API need no `Material` constant |
| `AttributeMapper` | Reflection-based `Attribute` constant resolution across MC versions |
| `SoundRegistryUtils` | Reflection-based `Sound` registry lookup with Paper/Spigot fallback |
| `EnchantmentMapper` | Version-varying enchantment resolution |
| `ItemUtils`, `SkillUtils`, `PotionUtil` | Static helpers for items, skills, potions |

Some compat code may be outdated from older MC version support. Remove when the minimum supported version bumps.

---

## Performance & Thread Safety

- **Performance is the #1 priority.** mcMMO runs on high-player-count servers. Prefer pre-computed lookups and caches over runtime calculations.
- **Folia support requires thread safety.** Use `ConcurrentHashMap`, `ConcurrentHashMap.newKeySet()`, or Guava `MapMaker().weakKeys()` for shared mutable state. Reference implementations: `StringUtils` (concurrent caches), `TransientEntityTracker` (concurrent player→summon maps), `MobMetadataUtils` (weak-keyed concurrent map).
- **All scheduling must use FoliaLib**, never the raw Bukkit scheduler. See `scheduleTasks()` in `mcMMO.java` and `SaveTimerTask` for patterns.

---

## Code Style

- **Effective Java 3rd Edition** is the guiding reference. Supplement with modern Java idioms when it offers improvements.
- **Modern Java**: use records, pattern matching, text blocks, enhanced `switch`, `Stream`, `Optional`. Most existing code is pre-Java 8 legacy; do not imitate old patterns. Modernize when touching old code.
- **Functional style preferred**: use Stream API, `Optional`, lambdas, and method references when they improve clarity. Be judicious: **code clarity is the top priority.** If an imperative loop reads more clearly than a stream pipeline, use the loop.
- **Annotations**: `@NotNull` / `@Nullable` (from `org.jetbrains.annotations`) on parameters, return types, and fields.
- **`final`**: prefer on local variables, parameters, and fields.
- **Formatting**: break method parameters across lines for readability; never exceed ~120 columns.
- **Imports**: static imports first, one blank line, then all other imports in a single alphabetical block. No wildcards.
- **Comments**: explain *why*, not *what*. Use Javadoc on public and package-visible methods.
- **Logging**: use `mcMMO.p.getLogger()` or `LogUtils`. Never `System.out` or `e.printStackTrace()`.

---

## Testing

- Tests mock the Bukkit API with plain Mockito (no MockBukkit) and assert with AssertJ. Match the existing style: `@Nested` groups, behavior names (`xShouldYWhenZ`), and `// Given - ...`, `// When - ...`, `// Then - ...` comments.
- **`MMOTestEnvironment`** is the base class for anything needing a plugin: call `mockBaseEnvironment(logger)` in `@BeforeEach` and `cleanUpStaticMocks()` in `@AfterEach`. It mocks `mcMMO.p`, the main configs, `Permissions`, `RankUtils`, `UserManager`, `Bukkit` and a temp data folder, and stubs `getMinecraftGameVersion().isAtLeast(...)` to `true`. Its FoliaLib mock runs only `runNextTick` and `runAtEntity` inline; other scheduler calls do nothing unless the test stubs them.
- All test classes share one Surefire JVM. An unclosed `MockedStatic` makes every later `mockStatic` of that class on the thread fail.
- **Registry-backed types** (`PotionEffectType`, `Enchantment`, anything resolved through `Bukkit.getRegistry`): call `TestRegistryBootstrap.bootstrap(mockedBukkit)` right after `mockStatic(Bukkit.class)` and before touching them (`MMOTestEnvironment` already does). Class initialization happens once per JVM, so a miss poisons the class and surfaces as `NoClassDefFoundError` in unrelated later tests, often only in CI's test order. Stub keys through `TestRegistryBootstrap.registryFor(type)`; those stubs outlive the test.
- Mockito static mocks are thread-local: code running on another thread sees the real statics, so a concurrency test can pass without ever hitting the mocks.

---

## Project Conventions

- **Plugin reference**: `mcMMO.p` is a legacy global static. Use in existing code, but do not create new static singletons.
- **Feature gating**: runtime toggles from `GeneralConfig` and `*Config` classes, loaded in `mcMMO.onEnable()`.
- **Shading**: `maven-shade-plugin` bundles only the artifacts listed in its `<artifactSet><includes>` and relocates them under `com.gmail.nossr50.mcmmo` (Kyori, ACF, bStats, FoliaLib, scoreboard-library, Tomcat JDBC). A new runtime dependency must be added to that list and relocated, or it is missing from the jar.
- **Locale caching**: `LocaleLoader` (immutable snapshot with a `ConcurrentHashMap` cache) and `StringUtils` (`ConcurrentHashMap` caches) are Folia-safe; keep them that way.
- **External integrations** (optional, detected via PluginManager): PlaceholderAPI, WorldGuard, HealthBar (disables mcMMO's health bars), ProjectKorra (startup warning; zero-damage combat events are skipped). ProtocolLib is declared in `plugin.yml` and `pom.xml`, but no code uses it.
- **API stability**: mcMMO is a dependency for many plugins. Never remove or change public method signatures without first deprecating them and providing alternatives.

### Locales

- Files: `src/main/resources/locale/locale_*.properties`, UTF-8 with raw characters (no `\u` escapes), Maven-filtered like the YAML resources.
- Lookup order: the server's `locale_override.properties`, then the configured locale, then `locale_en_US.properties`. A key missing everywhere renders as `!Key!` and logs a warning. Add new keys to `locale_en_US.properties` only; other locales fall back until translated.
- Removing a key: delete it from every `locale_*.properties` in the same change.
- Arguments are `MessageFormat` placeholders (`{0}`); `LocaleLoader` escapes single quotes, so write apostrophes normally. Use `&X` color codes like the surrounding entries.
- Skill names in messages come from `SkillTools.getLocalizedSkillName` ("Mining"). `getHeaderBannerSkillName` (all caps) is only for headers, scoreboards and skill command matching.

### Changelog

`Changelog.txt` is plain text: newest `Version x.x.xxx` block first, matching the `pom.xml` `<version>` without `-SNAPSHOT`, one change per four-space-indented line. `(API)` lines go near the end of the block and `(Codebase)` lines last; longer explanations go in a `NOTES:` section referenced by `(See notes)`. Credit contributors with `(Thanks name)` and never drop existing credits. Full rules: `.github/skills/mcmmo-changelog-writing/SKILL.md`.

Other task-specific agent skills (bug-report investigation, versioned API checks, Spigot/Paper implementation lookup, inventory API decisions) live in `.github/skills/*/SKILL.md`. Read the matching one before such a task.

---

## Known Tech Debt

> These areas are functional but fragile. Do not treat them as examples of good design.

- **Alchemy skill**: complex `potions.yml` / `PotionConfig` loading. `PotionConfigGenerator` (under `src/util/`, not compiled) is dead code. Incrementally improved but remains fragile.
- **Party system**: popular feature with legacy internals needing modernization.
- **Static singletons**: `mcMMO.p` and static manager fields. Deprecate with replacements before removing.
- **Listener classes**: `BlockListener`, `PlayerListener`, `EntityListener` are the least refactored legacy code. Large methods, deep nesting, repeated boilerplate.
- **SkillCommand hierarchy**: `SkillCommand` base class and ~19 subclasses use an over-engineered generic locale-template output system. Stores mutable `mmoPlayer` state in `onCommand()` (not thread-safe). Low-priority refactor target; follow the existing pattern when modifying.
- **Permissions**: `Permissions.java` is called on nearly every player action. Many per-ability methods (e.g., `skullSplitter()`, `dodge()`) are redundant; prefer the generic `isSubSkillEnabled()`. `Permissions.canUseSubSkill()` is the correct compound check: it verifies both **permission** (`isSubSkillEnabled`) and **level-based unlock** (`RankUtils.hasUnlockedSubskill`, which compares skill level against `skillranks.yml` thresholds). Future cleanup should consolidate the per-ability wrappers into the generic path.

## Codebase Patterns

> Established conventions that are not ideal but must be followed for consistency. Prefer these patterns when modifying existing code.

- **Skill class split**: each skill has a stateless static utility (`Axes.java`) and a stateful per-player `SkillManager` subclass (`AxesManager.java`). Confusing but established; follow the pattern.
- **Config loading**: config classes extend `BukkitConfig`, which by default copies keys missing from the user's file in from the jar template at startup. `RepairConfig`, `SalvageConfig`, `TreasureConfig` and `FishingTreasureConfig` pass `copyDefaults=false` so entries an admin deleted stay deleted; a new key there reaches existing installs only through the getter's fallback value. `PotionConfig` extends `LegacyConfigLoader` directly. `AutoUpdateLegacyConfigLoader` has no subclasses.
- **Config memoization**: hot getters cache values in nullable fields that `loadKeys()` resets (e.g., `GeneralConfig.powerLevelCap`). A setter that changes `config` must update its field too (see `ExperienceConfig.setExperienceGainsGlobalMultiplier`).
- **Over-engineered abstractions**: single-implementation interfaces and unnecessary indirection. Being simplified as found; prefer direct, simple designs.

---

## Common Changes

| Task | Steps |
|------|-------|
| Add a command | Create a `CommandExecutor` in `commands/`, add a `spec(...)` entry to `COMMAND_SPECS` in `CommandRegistrationManager` (description key `Commands.Description.<name>`), declare it in `src/main/resources/plugin.yml`. **Do not use ACF.** |
| Add DB behavior | Add the method to the `DatabaseManager` interface with a `default` body so third-party managers keep working (see `getStoredUsersWithUUIDs`), then implement it in both `FlatFileDatabaseManager` and `SQLDatabaseManager`. Run `mvn test -Psql-tests` (Docker required). |
| Add config option | Add the key + default value to the appropriate `src/main/resources/*.yml` file, then expose it via a public getter that passes the same default in the corresponding `*Config.java` class (e.g., `GeneralConfig`). New standalone config classes are rarely needed. Dead keys are deleted from the default YAML outright; deprecation cycles are for the Java API only. |
| Add a listener | Add the handler method to an existing listener in `listeners/`. New listener classes are rarely needed; the existing ones cover broad categories. |
| Support new MC blocks/items | Add lowercase key strings to the relevant `MaterialMapStore` sets and XP / bonus-drop entries to `experience.yml` / `config.yml`. Never reference `Material` constants newer than the compile API. |

---

## Key Files

All paths relative to `src/main/java/com/gmail/nossr50/` unless noted otherwise.

| File | Purpose |
|------|---------|
| `mcMMO.java` | Plugin lifecycle |
| `pom.xml` *(project root)* | Build, shading, relocations, resource filtering, test tag profiles |
| `.github/workflows/maven.yml` *(project root)* | CI build and test |
| `src/main/resources/plugin.yml` *(project root)* | Plugin metadata, commands, permissions |
| `util/commands/CommandRegistrationManager.java` | Bukkit command registration (primary) |
| `commands/CommandManager.java` | ACF setup (legacy, limited use) |
| `config/BukkitConfig.java` | Base class for config loading and default-key copying |
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
| `Changelog.txt` *(project root)* | Release notes |
| `src/test/java/com/gmail/nossr50/MMOTestEnvironment.java` *(project root)* | Shared test harness |
| `src/test/java/com/gmail/nossr50/TestRegistryBootstrap.java` *(project root)* | Bukkit `Registry` bootstrap for tests |

---

## PR Expectations

- **One concern per PR.** Each pull request should address a single feature, fix, or refactor. Do not bundle unrelated changes: if two systems are modified, they must be directly dependent on each other.
- **Code clarity above all.** Code should be readable, well-named, and self-explanatory. See [Code Style](#code-style).
- **Include unit tests** for new or changed behavior when feasible.
- **Keep PRs small and reviewable.** Smaller diffs are easier to review and less likely to introduce regressions.

---

## Agent Rules

1. **Do not imitate old code.** Code with git blame before 2020 is not exemplary. Use modern Java 17+ patterns.
2. **API stability is mandatory.** Deprecate before removing public methods; mcMMO is a widely-used dependency.
3. **Performance first.** Avoid allocations in hot paths. Use pre-computed lookups and caches.
4. **Thread safety is required.** mcMMO runs on Folia. Use `ConcurrentHashMap` or equivalent for shared mutable state.
5. **Use FoliaLib for scheduling.** Never use the Bukkit scheduler directly.
6. **Always add unit tests** for new or changed code.
7. **Run the Docker suites for database changes.** `mvn test -Psql-tests` with Docker running is mandatory for any change to database code; see [Build & Test](#build--test).
8. **Respect config toggles.** Check `GeneralConfig` and `*Config` classes before implementing feature-dependent logic.
9. **Respect event cancellation.** After firing events, check `isCancelled()` and honor modifications from other plugins.
10. **Use existing static managers** (`mcMMO.p`, etc.) when touching existing code, but do not create new static singletons.
11. **Shade new dependencies.** Add them to the shade `artifactSet` and follow the existing relocation patterns in `pom.xml`.
