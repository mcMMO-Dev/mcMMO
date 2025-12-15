# mcMMO Placeholders

This document describes all placeholders available in mcMMO, including the new placeholders added to facilitate the creation of menus and more detailed ranking systems.

## Available Skills

All mcMMO skills can be used in the placeholders:
- `acrobatics`
- `alchemy`
- `archery`
- `axes`
- `excavation`
- `fishing`
- `herbalism`
- `mining`
- `repair`
- `salvage`
- `smelting`
- `swords`
- `taming`
- `unarmed`
- `woodcutting`
- `crossbows`
- `tridents`
- `maces`

---

## Skill Placeholders (Per Skill)

The following placeholders are available for **all skills** listed above:

### Skill Level
**Syntax:** `%mcmmo_level_<skill>%`

Returns the player's current level in the specified skill.

**Examples:**
- `%mcmmo_level_mining%` - Returns Mining level (e.g., "75")
- `%mcmmo_level_swords%` - Returns Swords level
- `%mcmmo_level_woodcutting%` - Returns Woodcutting level

### Current Skill XP
**Syntax:** `%mcmmo_xp_<skill>%`

Returns the current amount of XP the player has in the skill.

**Examples:**
- `%mcmmo_xp_mining%` - Returns current Mining XP (e.g., "1245")
- `%mcmmo_xp_fishing%` - Returns current Fishing XP

### XP Needed for Next Level
**Syntax:** `%mcmmo_xp_needed_<skill>%`

Returns the total amount of XP needed to reach the next level.

**Examples:**
- `%mcmmo_xp_needed_mining%` - Returns XP needed (e.g., "2000")
- `%mcmmo_xp_needed_swords%` - Returns XP needed for next level

### XP Remaining for Next Level
**Syntax:** `%mcmmo_xp_remaining_<skill>%`

Returns how much XP is still needed to level up.

**Examples:**
- `%mcmmo_xp_remaining_mining%` - Returns remaining XP (e.g., "755")
- `%mcmmo_xp_remaining_fishing%` - XP needed for the next level

### Skill Rank Position
**Syntax:** `%mcmmo_rank_<skill>%`

Returns the player's position in the leaderboard for that skill.

**Examples:**
- `%mcmmo_rank_mining%` - Returns "12" if the player is in 12th place
- `%mcmmo_rank_swords%` - Position in Swords ranking

### Skill XP Rate
**Syntax:** `%mcmmo_xprate_<skill>%`

Returns the XP multiplier the player has for that skill (based on permissions).

**Examples:**
- `%mcmmo_xprate_mining%` - Returns "2.0" if they have double XP
- `%mcmmo_xprate_fishing%` - Returns "1.5" if they have 1.5x XP

---

## Power Level Placeholders (General)

### Total Power Level
**Syntax:** `%mcmmo_power_level%`

Returns the player's total power level (sum of all skill levels).

**Example:**
- `%mcmmo_power_level%` - Returns "850"

### Power Level Cap
**Syntax:** `%mcmmo_power_level_cap%`

Returns the maximum power level cap configured on the server.

**Example:**
- `%mcmmo_power_level_cap%` - Returns "2000"

### Global XP Rate
**Syntax:** `%mcmmo_xprate%`

Returns the global XP multiplier configured on the server.

**Example:**
- `%mcmmo_xprate%` - Returns "1.5"

### XP Event Active
**Syntax:** `%mcmmo_is_xp_event_active%`

Returns whether an XP event is active on the server.

**Return:**
- `true` - If there is an active XP event
- `false` - If there is no event

---

## Party Placeholders

### Is in Party
**Syntax:** `%mcmmo_in_party%`

Checks if the player is in a party.

**Return:**
- `true` - If in a party
- `false` - If not in a party

### Party Name
**Syntax:** `%mcmmo_party_name%`

Returns the player's party name (empty if not in any).

**Example:**
- `%mcmmo_party_name%` - Returns "Adventurers"

### Is Party Leader
**Syntax:** `%mcmmo_is_party_leader%`

Checks if the player is the party leader.

**Return:**
- `true` - If they are the leader
- `false` - If they are not the leader or not in a party

### Party Leader Name
**Syntax:** `%mcmmo_party_leader%`

Returns the name of the party leader.

**Example:**
- `%mcmmo_party_leader%` - Returns "Steve"

### Party Size
**Syntax:** `%mcmmo_party_size%`

Returns the number of members in the party.

**Example:**
- `%mcmmo_party_size%` - Returns "5"

---

## Leaderboard/McTop Placeholders

### Get Value/Level at Position X (✨ NEW)

Returns the value (level or power level) of the player at position X in the ranking.

**Syntax:**
- `%mcmmo_mctop_<skill>:<position>%` - For a specific skill
- `%mcmmo_mctop_overall:<position>%` - For overall ranking (power level)

**Examples:**
- `%mcmmo_mctop_mining:1%` - Returns the Mining level of the 1st place player
- `%mcmmo_mctop_swords:5%` - Returns the Swords level of the 5th place player
- `%mcmmo_mctop_overall:1%` - Returns the power level of the 1st place player

### Get Player Name at Position X (✨ NEW)

Returns the player's name at position X in the ranking.

**Syntax:**
- `%mcmmo_mctop_name_<skill>:<position>%` - For a specific skill
- `%mcmmo_mctop_name_overall:<position>%` - For overall ranking

**Examples:**
- `%mcmmo_mctop_name_mining:1%` - Returns the name of the 1st place player in Mining
- `%mcmmo_mctop_name_swords:10%` - Returns the name of the 10th place player in Swords
- `%mcmmo_mctop_name_overall:1%` - Returns the name of the 1st place player in overall ranking

### Get Your Overall Rank Position (✨ NEW)

Returns the player's position in the overall ranking (power level).

**Syntax:**
- `%mcmmo_rank_overall%`

**Example:**
- `%mcmmo_rank_overall%` - Returns "15" if the player is in 15th place

---

## Level Check Placeholder (✨ NEW)

Checks if the player has reached the required level in a specific skill.

**Syntax:**
- `%mcmmo_checklevel_<skill>:<level>%`

**Return:**
- `✔` - If the player has the required level or higher
- `✘` - If the player does NOT have the required level

**Examples:**
- `%mcmmo_checklevel_mining:50%` - Returns ✔ if player has Mining level 50+, otherwise ✘
- `%mcmmo_checklevel_swords:100%` - Returns ✔ if player has Swords level 100+, otherwise ✘
- `%mcmmo_checklevel_woodcutting:25%` - Returns ✔ if player has Woodcutting level 25+, otherwise ✘

---

## Available Skills Reference

All mcMMO skills that can be used in the placeholders above:
- `acrobatics` - Acrobatics
- `alchemy` - Alchemy
- `archery` - Archery
- `axes` - Axes
- `excavation` - Excavation
- `fishing` - Fishing
- `herbalism` - Herbalism
- `mining` - Mining
- `repair` - Repair
- `salvage` - Salvage
- `smelting` - Smelting
- `swords` - Swords
- `taming` - Taming
- `unarmed` - Unarmed
- `woodcutting` - Woodcutting
- `crossbows` - Crossbows
- `tridents` - Tridents
- `maces` - Maces

---

## Menu Usage Examples

### Top 10 Mining Menu
```yaml
display:
  name: "&6Top 10 - Mining"
  lore:
    - "&71st - %mcmmo_mctop_name_mining:1% &f- &e%mcmmo_mctop_mining:1%"
    - "&72nd - %mcmmo_mctop_name_mining:2% &f- &e%mcmmo_mctop_mining:2%"
    - "&73rd - %mcmmo_mctop_name_mining:3% &f- &e%mcmmo_mctop_mining:3%"
    - "&74th - %mcmmo_mctop_name_mining:4% &f- &e%mcmmo_mctop_mining:4%"
    - "&75th - %mcmmo_mctop_name_mining:5% &f- &e%mcmmo_mctop_mining:5%"
```

### Overall Ranking Menu
```yaml
display:
  name: "&6Overall Ranking - Power Level"
  lore:
    - "&7Your position: &e#%mcmmo_rank_overall%"
    - ""
    - "&6Top 3:"
    - "&e1st - %mcmmo_mctop_name_overall:1% &f- &6%mcmmo_mctop_overall:1%"
    - "&e2nd - %mcmmo_mctop_name_overall:2% &f- &6%mcmmo_mctop_overall:2%"
    - "&e3rd - %mcmmo_mctop_name_overall:3% &f- &6%mcmmo_mctop_overall:3%"
```

### Requirement Check Menu
```yaml
display:
  name: "&6Unlock Special Ability"
  lore:
    - "&7Requirements:"
    - "&7Mining Level 50: %mcmmo_checklevel_mining:50%"
    - "&Swords Level 75: %mcmmo_checklevel_swords:75%"
    - "&7Woodcutting Level 30: %mcmmo_checklevel_woodcutting:30%"
```

## Technical Notes

- Placeholders work with PlaceholderAPI
- Data is obtained directly from the mcMMO database
- PlaceholderAPI cache is used to optimize queries
- Invalid positions (less than 1 or greater than total players) return empty string
- Invalid levels or non-existent skills return ✘ for checklevel
