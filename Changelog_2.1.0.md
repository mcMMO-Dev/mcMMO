# **Version 2.1.0+**

### Please use Spigot or Paper!

* **mcMMO is now built against Spigot-API instead of Bukkit**

### New Level Scaling

* mcMMO now features an optional 1-100 scaling mode!
* This is on by default for new installs, if you are upgrading mcMMO you will be put into Retro Mode instead (1-1000) scaling.
* The two scaling modes are the same, the changes are completely cosmetic!
* Skill Requirements in the config file will be multiplied by 10 if you are using Retro mode unless the setting has Retro in its name, keep this in mind if you need to edit anything!

### WorldGuard Support

* Added support for WorldGuard regions!
* `mcmmo` region flag turns on or off a players ability to use anything related to mcMMO other than commands
* `mcmmo-xp` region flag turns on or off a player's ability to gain XP
* These flags default to on unless you specify otherwise

### World Blacklist

* You can now disable mcMMO completely for specific worlds via `world_blacklist.txt` in /plugins/mcMMO/
* This file appears once mcMMO has been run at least once
* Every line of this file should be the name of a world where you don't want mcMMO to be enabled!

### Rank System

* Skills that are not yet unlocked will show up as `???` until learned
* Many skills now make use of a rank system!
* Rank level requirements are modified in `skillranks.yml`
* Woodcutting's Double Drop subskill is now named Harvest Lumber
* Archery's Skill Shot now uses a rank system
* Swords' Bleed now uses a rank system
* Swords' Counter Attack now uses a rank system
* Axe's Axe Mastery now uses a rank system
* Axe's Impact now uses a rank system
* Herbalism's Farmer's Diet now uses a rank system
* Herbalism's Green Thumb now uses a rank system
* Shake now uses a rank system
* Flux Mining now uses a rank system
* Removed traps from fishing
* Dodge now uses a rank system
* Arrow Retrieval now uses a rank system
* Axes' Critical Strikes now uses a rank system
* Axes' Greater Impact now uses a rank system
* Taming's Beast Lore now uses a rank system
* Taming's Gore now uses a rank system
* Taming's Call of the Wild now uses a rank system
* Taming's Pummel now uses a rank system
* Green Thumb now uses a rank system
* Farmer's Diet & Fisherman's Diet now use a rank system

### mcMMO Chat Alerts

* mcMMO no longer spams your chat!
* Most messages are sent to your action bar instead!
* Completely configurable! You can have mcMMO spam your chat again if you want!
* You can configure it so that messages will be sent to your action bar AND your chat system!
* Improved some of the messages sent to the player regarding the Chimaera Wing

### Localization File

* The descriptions of a few skills have changed
* Locale files now support & codes for colors and formatting!
* Added locale strings for new Woodcutting abilities
* Added locale strings for mcchatspy command
* Added locale strings for JSON integration
* Added locale strings for Taming's Pummel SubSkill
* Added locale strings for Unarmed's Block Cracker SubSkill
* Removed localizations with the following codes for being almost empty: `id`, `HR_hr`, `et_EE`, `lv`, `lt`, `no`, `pl_PL`, `pt_PT`, `tr_TR`
* Removed redundant information from some skill names and descriptions `en_US` (other locales will need to be updated)
* SubSkill locale keys are now located at `{ParentSkill}.SubSkill.SubSkillName`
* Super Abilities no longer have `(ABILITY)` in their `Skill.Effect` strings

### UI

* Certain elements of mcMMO's UI have been restyled
* Skills can now be clicked on and hovered over for more information!
* Added links to mcMMO related websites to various commands
* Customizeable and optional XP Bars
* Added the tagline "Overhaul Era" to various locations until 3.0.0 comes out
* Added option to disable the new URL links to `config.yml`

### Sounds

* Volume and Pitch of sounds can now be configured in the new `sounds.yml` file

### Super Ability Changes

* Skill Super Abilities now use a rank system, the default rank to unlock is level 5
* Activating Super abilities plays a sound (other plays can hear this)
* Ability Lengths now have a default skill cap at which they stop increasing in length
* Setting the cap to 0 removes it!
* Configurable in `advanced.yml` (endurance perks extend this limit)

### Skills

* mcMMO now notifies you when you progress in a skill!
* Excavation Treasure Hunter is renamed to Archaeology
* Readying a tool for a super ability now plays a sound
* Skill Unlock Notifications have sounds
* Stripping wood and right clicking on stripped wood will no longer ready your Axe
* Added new skill 'Understanding The Art' which adds nothing new but tracks previously hidden benefits of raising a skill
* Tool alerts now are sent to the Action Bar
* Super Ability activation alerts are now sent to the Action Bar
* Almost all Skill-related messages are now sent to the Action Bar
* Added some missing information to skill stats
* Swords no longer require blocking with a shield to trigger counter attacks
* Sword's Bleed has been renamed to Rupture
* Sword's Rupture no longer has an internal hard coded limit
* Sword's Serrated Strikes now uses your Rupture rank to determine the damage/ticks for its bleed effect.
* Sword's Rupture now ticks four times as fast
* Sword's Rupture now refreshes bleed duration instead of adding duration when applying bleed to the same target
* Sword's Rupture will now deal lethal damage
* Sword's Rupture now reaches its max proc chance at level 20 (200 in Retro)
* Sword's Rupture now has a max chance to proc of 33% instead of 70%
* Sword's Rupture now deals 50% more damage at above Rank 3 and can last much longer!
 * The base damage for Bleed has been increased as well (update your `advanced.yml` admins)
* Sword's Rupture no longer triggers invincibility frames when damaging your opponent
* Sword's Rupture now plays a sound
* Taming's Gore now uses Rupture Rank 1 for its DoT calculations
* Furnaces now give XP to the last person to modify their inventory instead of the first person to open them
* Rolling now plays a sound (Graceful Roll has a different sound :) )
* Acrobatics' Roll exploit detection was tweaked to still allow for Roll to trigger even if it rewards no XP
* Acrobatics' Roll & Gracefull Roll are now considered the same skill (both mechanics are still there)
* Some skill level rank requirements have changed

### Experience

* Skills now start at level 1 (configurable in advanced.yml)
* Starting an XP event will now use the title API (toggle this in `advanced.yml`)
* The XP values of fish are now based on their rarity and have been drastically changed
* Coral (blocks) now give Mining XP
* Coral (plants) now give Herbalism XP
* Blue Ice now gives Mining XP
* Dolphins now give combat XP
* Drowned mobs now count towards combat XP
* Added missing mushroom blocks for XP
* You can now set guaranteed minimum values for XP gained if diminishing returns are enabled, this value defaults to 5% (`experience.yml`)

### Bug Fixes

* Fixed the bug where mob names would be replaced by hearts
* Fixed a bug where Rupture would apply an incorrect amount of bleed ticks
* Fixed bug where XP rate could be a negative number
* Fixed a bug where you could set a players levels into the negative and bad things would happen
* Fixed an edge case bug where Blast Mining wouldn't inform the player that it was on cooldown

### Plugin Compatibility

* mcMMO now fires new custom events relating to changes it makes to player scoreboards, plugin authors can listen to these events to improve compatibility

### Exploit Fixes

* Prevented exploits involving blocks made from entities (snowmen, etc..)
* Chimaera Wing now tracks cooldowns between sessions for players (no more disconnect abuse)
* Tridents will no longer be considered unarmed
* Prevented exploits involving 2 high herbs and chorus flowers
* Vastly Improved the Acrobatics exploit detection checks

### Parties

* Parties can now have size limits (configurable in `config.yml`), party size is unlimited by default
* You can now turn on Friendly Fire for parties in `config.yml`
* Party member list will only include members of the party that you can see (aren't vanished)

### Scoreboards

* Scoreboards are now disabled by default since most of their functionality is handled better by XP bars.
* Added toggle to disable all scoreboards (previously you had to disable them one by one)
* You can turn them back on in `config.yml`
* You can have XP bars and scoreboards on at the same time

### MySQL

* Added support for SSL for MySQL/MariaDB in config.yml (On by default)
* mcMMO no longer spams your console if you are not using SSL for your MySQL server
* You can now inspect offline players
* When converting from MySQL to flatfile mcMMO will now properly include all users in the conversion process

### Admins

* Added ability for admins to spy on party chat (off unless toggled on) /mcchatspy
* The Debug stick can now tell you about properties of a block related to Excavation

### API

* Detailed guide to API changes is available at http://api.mcmmo.org
* Added many missing `SubSkills` to `SubSkillType` class
* Moved a lot of methods from `SkillCommand` to `SkillUtils`
* `SkillType` is now `PrimarySkillType`
* `SecondarySkill` is now `SubSkillType`
* `AbilityType` is now `SuperAbilityType`
* `SecondaryAbilityEvent` is now `SubSkillEvent`
* `SubSkillType` has had many helpful methods added to it
* `GREEN_THUMB_PLANT` & `GREEN_THUMB_BLOCK` are replaced by `GREEN_THUMB`

### Permissions

* Removed all mob health bar permissions, this is no longer a per-player setting.
* Added permission node `mcmmo.commands.mcchatspy` & `mcmmo.commands.mcchatspy.others`
* Added `mcmmo.commands.mmoinfo` for the new `mmoinfo`/`mcinfo` command
* Added permission nodes for Harvest Lumber, Splinter, Nature's Bounty, and Bark Surgeon
* Call of the wild now uses `mcmmo.ability.taming.callofthewild` instead of `mcmmo.ability.taming.callofthewild.all`
* Replaced the old Double Drop permission node for woodcutting with a new Harvest Lumber permission node
* Fast Food Service permission node renamed to `mcmmo.ability.taming.fastfoodservice`
* Counter Attack permission node renamed to `mcmmo.ability.swords.counterattack`
* Arrow Deflect permission node renamed to `mcmmo.ability.unarmed.arrowdeflect`
* Iron Arm Style permission node renamed to `mcmmo.ability.unarmed.ironarmstyle`

### Commands

* Added new info command `/mmoinfo` or `/mcinfo`
* Added toggle command `/mcchatspy`
* `/mcMMO help` no longer displays the other/special commands category to players lacking permissions
* Removed the `mobhealthbar` command, this is no longer a per-player setting.

### Misc Config Changes

* Removed `SkillShot`'s `IncreaseLevel` & `IncreasePercentage` (replaced by `RankDamageMultiplier`)
* Removed `AxeMastery`'s `MaxBonus` & `MaxBonusLevel` (replaced by `RankDamageMultiplier`)
* `Unarmed.IronArm` in `advanced.yml` is now `Unarmed.IronArmStyle`
* `Unarmed.Deflect` in `advanced.yml` is now `Unarmed.ArrowDeflect`
* `Swords.Counter` in `advanced.yml` is now `Swords.CounterAttack`
* `Archery.Retrieve` in `advanced.yml` is now `Archery.ArrowRetrieval`
* `Axes.CriticalHit` in `advanced.yml` is now `Axes.CriticalStrikes`
* Archery's Skill Shot now uses `RankDamageMultiplier` for its damage bonus calculations
* Axe's Axe mastery now uses `RankDamageMultiplier` for its damage bonus calculations

### Misc Changes

* Removed everything involving the kraken
* Code cleanup in a lot of places... unfortunately there is still much left to do!
