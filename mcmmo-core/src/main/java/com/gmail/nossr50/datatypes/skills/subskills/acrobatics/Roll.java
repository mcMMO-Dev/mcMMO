package com.gmail.nossr50.datatypes.skills.subskills.acrobatics;

import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.player.BukkitMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.random.RandomChanceSkill;
import com.gmail.nossr50.util.skills.SkillActivationType;
import com.gmail.nossr50.util.sounds.SoundType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

public class Roll extends AcrobaticsSubSkill {

    private final mcMMO pluginRef;

    public Roll(mcMMO pluginRef) {
        super(pluginRef, "Roll", EventPriority.HIGHEST, SubSkillType.ACROBATICS_ROLL);
        this.pluginRef = pluginRef;
    }

    protected static double calculateModifiedRollDamage(double damage, double damageThreshold) {
        return Math.max(damage - damageThreshold, 0.0);
    }

    /**
     * Executes the interaction between this subskill and Minecraft
     *
     * @param event the vector of interaction
     * @return true if interaction wasn't cancelled
     */
    @Override
    public boolean doInteraction(Event event, mcMMO plugin) {
        //TODO: Go through and API this up

        /*
         * Roll is a SubSkill which allows players to negate fall damage from certain heights with sufficient Acrobatics skill and luck
         * Roll is activated when a player takes damage from a fall
         * If a player holds shift, they double their odds at a successful roll and upon success are told they did a graceful roll.
         */

        //Casting
        EntityDamageEvent entityDamageEvent = (EntityDamageEvent) event;

        //Make sure a real player was damaged in this event
        if (!pluginRef.getEventManager().isRealPlayerDamaged(entityDamageEvent))
            return false;

        switch (entityDamageEvent.getCause()) {
            case FALL:

                //Grab the player
                BukkitMMOPlayer mcMMOPlayer = pluginRef.getEventManager().getMcMMOPlayer(entityDamageEvent.getEntity());

                if (mcMMOPlayer == null)
                    break;

                /*
                 * Check for success
                 */
                Player player = (Player) ((EntityDamageEvent) event).getEntity();
                if (canRoll(player)) {
                    entityDamageEvent.setDamage(rollCheck(player, mcMMOPlayer, entityDamageEvent.getDamage()));

                    //Check getDamage() instead of getFinalDamage()
                    //Prevent unintended invincibility if Absorption covers all of the damage
                    if (entityDamageEvent.getDamage() == 0) {
                        entityDamageEvent.setCancelled(true);
                        return true;
                    }
                }
                break;

            default:
                break;
        }

        return false;
    }

    /**
     * Grabs the permission node for this skill
     *
     * @return permission node address
     */
    @Override
    public String getPermissionNode() {
        return ("mcmmo.ability." + getPrimaryKeyName() + "." + getConfigKeyName()).toLowerCase();
    }

    /**
     * Checks if a player has permission to use this skill
     *
     * @param player target player
     * @return true if player has permission
     */
    @Override
    public boolean hasPermission(Player player) {
        return pluginRef.getPermissionTools().isSubSkillEnabled(player, this);
    }

    /**
     * Adds detailed stats specific to this skill
     *
     * @param componentBuilder target component builder
     * @param player           target player
     */
    @Override
    public void addStats(ComponentBuilder componentBuilder, Player player) {
        String rollChance, rollChanceLucky, gracefulRollChance, gracefulRollChanceLucky;

        /* Values related to the player */
        PlayerProfile playerProfile = pluginRef.getUserManager().getPlayer(player).getProfile();
        float skillValue = playerProfile.getSkillLevel(getPrimarySkill());
        boolean isLucky = pluginRef.getPermissionTools().lucky(player, getPrimarySkill());

        String[] rollStrings = pluginRef.getRandomChanceTools().calculateAbilityDisplayValues(SkillActivationType.RANDOM_LINEAR_100_SCALE_WITH_CAP, player, SubSkillType.ACROBATICS_ROLL);
        rollChance = rollStrings[0];
        rollChanceLucky = rollStrings[1];

        /*
         * Graceful is double the odds of a normal roll
         */
        String[] gracefulRollStrings = pluginRef.getRandomChanceTools().calculateAbilityDisplayValuesCustom(SkillActivationType.RANDOM_LINEAR_100_SCALE_WITH_CAP, player, SubSkillType.ACROBATICS_ROLL, 2.0D);
        gracefulRollChance = gracefulRollStrings[0];
        gracefulRollChanceLucky = gracefulRollStrings[1];

        /*
         *   Append the messages
         */

        /*componentBuilder.append(pluginRef.getLocaleManager().getString("Effects.Template", pluginRef.getLocaleManager().getString("Acrobatics.Effect.2"), pluginRef.getLocaleManager().getString("Acrobatics.Effect.3")));
        componentBuilder.append("\n");*/

        //Acrobatics.SubSkill.Roll.Chance
        componentBuilder.append(pluginRef.getLocaleManager().getString("Acrobatics.SubSkill.Roll.Chance", rollChance) + (isLucky ? pluginRef.getLocaleManager().getString("Perks.Lucky.Bonus", rollChanceLucky) : ""));
        componentBuilder.append("\n");
        componentBuilder.append(pluginRef.getLocaleManager().getString("Acrobatics.SubSkill.Roll.GraceChance", gracefulRollChance) + (isLucky ? pluginRef.getLocaleManager().getString("Perks.Lucky.Bonus", gracefulRollChanceLucky) : ""));
        //Activation Tips
        componentBuilder.append("\n").append(pluginRef.getLocaleManager().getString("JSON.Hover.Tips")).append("\n");
        componentBuilder.append(getTips());
        componentBuilder.append("\n");
        //Advanced

        //Lucky Notice
        if (isLucky) {
            componentBuilder.append(pluginRef.getLocaleManager().getString("JSON.JWrapper.Perks.Header"));
            componentBuilder.append("\n");
            componentBuilder.append(pluginRef.getLocaleManager().getString("JSON.JWrapper.Perks.Lucky", "33"));
        }

    }

    @Override
    public boolean isSuperAbility() {
        return false;
    }

    @Override
    public boolean isActiveUse() {
        return true;
    }

    @Override
    public boolean isPassive() {
        return true;
    }

    private boolean canRoll(Player player) {
        return pluginRef.getRankTools().hasUnlockedSubskill(player, SubSkillType.ACROBATICS_ROLL) && pluginRef.getPermissionTools().isSubSkillEnabled(player, SubSkillType.ACROBATICS_ROLL);
    }

    /**
     * Handle the damage reduction and XP gain from the Roll ability
     *
     * @param damage The amount of damage initially dealt by the event
     * @return the modified event damage if the ability was successful, the original event damage otherwise
     */
    private double rollCheck(Player player, BukkitMMOPlayer mcMMOPlayer, double damage) {

        int skillLevel = mcMMOPlayer.getSkillLevel(getPrimarySkill());

        if (player.isSneaking()) {
            return gracefulRollCheck(player, mcMMOPlayer, damage, skillLevel);
        }

        double modifiedDamage = calculateModifiedRollDamage(damage, pluginRef.getConfigManager().getConfigAcrobatics().getRollDamageThreshold());

        if (!isFatal(player, modifiedDamage)
                && pluginRef.getRandomChanceTools().isActivationSuccessful(SkillActivationType.RANDOM_LINEAR_100_SCALE_WITH_CAP, SubSkillType.ACROBATICS_ROLL, player)) {
            pluginRef.getNotificationManager().sendPlayerInformation(player, NotificationType.SUBSKILL_MESSAGE, "Acrobatics.Roll.Text");
            pluginRef.getSoundManager().sendCategorizedSound(player, player.getLocation(), SoundType.ROLL_ACTIVATED, SoundCategory.PLAYERS);

            if (!pluginRef.getConfigManager().getConfigExploitPrevention().getConfigSectionExploitAcrobatics().isPreventAcrobaticsAbuse())
                pluginRef.getSkillTools().applyXpGain(mcMMOPlayer, getPrimarySkill(), calculateRollXP(player, damage, true), XPGainReason.PVE);
            else if (!isExploiting(player) && mcMMOPlayer.getAcrobaticsManager().canGainRollXP())
                pluginRef.getSkillTools().applyXpGain(mcMMOPlayer, getPrimarySkill(), calculateRollXP(player, damage, true), XPGainReason.PVE);

            addFallLocation(player);
            return modifiedDamage;
        } else if (!isFatal(player, damage)) {
            if (!pluginRef.getConfigManager().getConfigExploitPrevention().getConfigSectionExploitAcrobatics().isPreventAcrobaticsAbuse())
                pluginRef.getSkillTools().applyXpGain(mcMMOPlayer, getPrimarySkill(), calculateRollXP(player, damage, true), XPGainReason.PVE);
            else if (!isExploiting(player) && mcMMOPlayer.getAcrobaticsManager().canGainRollXP())
                pluginRef.getSkillTools().applyXpGain(mcMMOPlayer, getPrimarySkill(), calculateRollXP(player, damage, false), XPGainReason.PVE);
        }

        addFallLocation(player);
        return damage;
    }

    private int getActivationChance(BukkitMMOPlayer mcMMOPlayer) {
        return pluginRef.getPerkUtils().handleLuckyPerks(mcMMOPlayer.getPlayer(), getPrimarySkill());
    }

    /**
     * Handle the damage reduction and XP gain from the Graceful Roll ability
     *
     * @param damage The amount of damage initially dealt by the event
     * @return the modified event damage if the ability was successful, the original event damage otherwise
     */
    private double gracefulRollCheck(Player player, BukkitMMOPlayer mcMMOPlayer, double damage, int skillLevel) {
        double modifiedDamage = calculateModifiedRollDamage(damage, pluginRef.getConfigManager().getConfigAcrobatics().getRollDamageThreshold() * 2);

        RandomChanceSkill rcs = new RandomChanceSkill(pluginRef, player, subSkillType);
        rcs.setSkillLevel(rcs.getSkillLevel() * 2); //Double the effective odds

        if (!isFatal(player, modifiedDamage)
                && pluginRef.getRandomChanceTools().checkRandomChanceExecutionSuccess(rcs)) {
            pluginRef.getNotificationManager().sendPlayerInformation(player, NotificationType.SUBSKILL_MESSAGE, "Acrobatics.Ability.Proc");
            pluginRef.getSoundManager().sendCategorizedSound(player, player.getLocation(), SoundType.ROLL_ACTIVATED, SoundCategory.PLAYERS, 0.5F);

            if (!pluginRef.getConfigManager().getConfigExploitPrevention().getConfigSectionExploitAcrobatics().isPreventAcrobaticsAbuse())
                pluginRef.getSkillTools().applyXpGain(mcMMOPlayer, getPrimarySkill(), calculateRollXP(player, damage, true), XPGainReason.PVE);
            else if (!isExploiting(player) && mcMMOPlayer.getAcrobaticsManager().canGainRollXP())
                pluginRef.getSkillTools().applyXpGain(mcMMOPlayer, getPrimarySkill(), calculateRollXP(player, damage, true), XPGainReason.PVE);

            addFallLocation(player);
            return modifiedDamage;
        } else if (!isFatal(player, damage)) {
            if (!pluginRef.getConfigManager().getConfigExploitPrevention().getConfigSectionExploitAcrobatics().isPreventAcrobaticsAbuse())
                pluginRef.getSkillTools().applyXpGain(mcMMOPlayer, getPrimarySkill(), calculateRollXP(player, damage, true), XPGainReason.PVE);
            else if (!isExploiting(player) && mcMMOPlayer.getAcrobaticsManager().canGainRollXP())
                pluginRef.getSkillTools().applyXpGain(mcMMOPlayer, getPrimarySkill(), calculateRollXP(player, damage, false), XPGainReason.PVE);

            addFallLocation(player);
        }

        return damage;
    }

    /**
     * Check if the player is "farming" Acrobatics XP using
     * exploits in the game.
     *
     * @return true if exploits are detected, false otherwise
     */
    private boolean isExploiting(Player player) {
        if (!pluginRef.getConfigManager().getConfigExploitPrevention().getConfigSectionExploitAcrobatics().isPreventAcrobaticsAbuse()) {
            return false;
        }

        BukkitMMOPlayer mcMMOPlayer = pluginRef.getUserManager().getPlayer(player);

        if (pluginRef.getItemTools().hasItemInEitherHand(player, Material.ENDER_PEARL) || player.isInsideVehicle()) {
            if(mcMMOPlayer.isDebugMode()) {
                mcMMOPlayer.getPlayer().sendMessage("Acrobatics XP Prevented: Ender Pearl or Inside Vehicle");
            }
            return true;
        }

        //Teleport CD
        if (System.currentTimeMillis() < pluginRef.getUserManager().getPlayer(player).getTeleportATS())
            return true;

        if (pluginRef.getUserManager().getPlayer(player).getAcrobaticsManager().hasFallenInLocationBefore(getBlockLocation(player)))
        {
            if(mcMMOPlayer.isDebugMode()) {
                mcMMOPlayer.getPlayer().sendMessage("Acrobatics XP Prevented: Fallen in location before");
            }

            return true;
        }

        return false;
    }

    private float calculateRollXP(Player player, double damage, boolean isRoll) {
        //Clamp Damage to account for insane DRs
        damage = Math.min(40, damage);

        ItemStack boots = player.getInventory().getBoots();
        float xp = (float) (damage * (isRoll ? pluginRef.getConfigManager().getConfigExperience().getRollXP() : pluginRef.getConfigManager().getConfigExperience().getFallXP()));

        if (boots != null && boots.containsEnchantment(Enchantment.PROTECTION_FALL)) {
            xp *= pluginRef.getConfigManager().getConfigExperience().getFeatherFallMultiplier();
        }

        return xp;
    }

    private boolean isFatal(Player player, double damage) {
        return player.getHealth() - damage <= 0;
    }

    /**
     * Gets the number of ranks for this subskill, 0 for no ranks
     *
     * @return the number of ranks for this subskill, 0 for no ranks
     */
    @Override
    public int getNumRanks() {
        return 0;
    }

    /**
     * Prints detailed info about this subskill to the player
     *
     * @param player the target player
     */
    @Override
    public void printInfo(Player player) {
        //Header
        super.printInfo(player);

        //Start the description string.
        //player.sendMessage(getDescription());
        //Player stats
        player.sendMessage(pluginRef.getLocaleManager().getString("Commands.MmoInfo.Stats",
                pluginRef.getLocaleManager().getString("Acrobatics.SubSkill.Roll.Stats", getStats(player)[0], getStats(player)[1])));

        //Mechanics
        player.sendMessage(pluginRef.getLocaleManager().getString("Commands.MmoInfo.Mechanics"));
        player.sendMessage(getMechanics());
    }

    /**
     * Returns a collection of strings about how a skill works
     * Used in the MMO Info command
     *
     * @return
     */
    @Override
    public String getMechanics() {
        //Vars passed to locale
        //0 = chance to roll at half max level
        //1 = chance to roll with grace at half max level
        //2 = level where maximum bonus is reached
        //3 = additive chance to succeed per level
        /*
        Roll:
            # ChanceMax: Maximum chance of rolling when on <MaxBonusLevel> or higher
            # MaxBonusLevel: On this level or higher, the roll chance will not go higher than <ChanceMax>
            # DamageThreshold: The max damage a player can negate with a roll
            ChanceMax: 100.0
            MaxBonusLevel: 100
            DamageThreshold: 7.0
         */
        double rollChanceHalfMax, graceChanceHalfMax, damageThreshold, chancePerLevel;

        //Chance to roll at half max skill
        RandomChanceSkill rollHalfMaxSkill = new RandomChanceSkill(pluginRef, null, subSkillType);
        int halfMaxSkillValue = pluginRef.isRetroModeEnabled() ? 500 : 50;
        rollHalfMaxSkill.setSkillLevel(halfMaxSkillValue);

        //Chance to graceful roll at full skill
        RandomChanceSkill rollGraceHalfMaxSkill = new RandomChanceSkill(pluginRef, null, subSkillType);
        rollGraceHalfMaxSkill.setSkillLevel(halfMaxSkillValue * 2); //Double the effective odds

        //Chance to roll per level
        RandomChanceSkill rollOneSkillLevel = new RandomChanceSkill(pluginRef, null, subSkillType);
        rollGraceHalfMaxSkill.setSkillLevel(1); //Level 1 skill

        //Chance Stat Calculations
        rollChanceHalfMax = pluginRef.getRandomChanceTools().getRandomChanceExecutionChance(rollHalfMaxSkill);
        graceChanceHalfMax = pluginRef.getRandomChanceTools().getRandomChanceExecutionChance(rollGraceHalfMaxSkill);
        damageThreshold = pluginRef.getConfigManager().getConfigAcrobatics().getRollDamageThreshold();

        chancePerLevel = pluginRef.getRandomChanceTools().getRandomChanceExecutionChance(rollOneSkillLevel);

        double maxLevel = pluginRef.getDynamicSettingsManager().getSkillMaxBonusLevel(SubSkillType.ACROBATICS_ROLL);

        return pluginRef.getLocaleManager().getString("Acrobatics.SubSkill.Roll.Mechanics", rollChanceHalfMax, graceChanceHalfMax, maxLevel, chancePerLevel, damageThreshold, damageThreshold * 2);
    }

    /**
     * Get an array of various stats for a player
     *
     * @param player target player
     * @return stat array for target player for this skill
     */
    @Override
    public Double[] getStats(Player player) {
        double playerChanceRoll, playerChanceGrace;

        RandomChanceSkill roll = new RandomChanceSkill(pluginRef, player, getSubSkillType());
        RandomChanceSkill graceful = new RandomChanceSkill(pluginRef, player, getSubSkillType());

        graceful.setSkillLevel(graceful.getSkillLevel() * 2); //Double odds

        //Calculate
        playerChanceRoll = pluginRef.getRandomChanceTools().getRandomChanceExecutionChance(roll);
        playerChanceGrace = pluginRef.getRandomChanceTools().getRandomChanceExecutionChance(graceful);

        Double[] stats = {playerChanceRoll, playerChanceGrace}; //DEBUG
        return stats;
    }

    public void addFallLocation(Player player) {
        pluginRef.getUserManager().getPlayer(player).getAcrobaticsManager().addLocationToFallMap(getBlockLocation(player));
    }

    public Location getBlockLocation(Player player) {
        return player.getLocation().getBlock().getLocation();
    }
}