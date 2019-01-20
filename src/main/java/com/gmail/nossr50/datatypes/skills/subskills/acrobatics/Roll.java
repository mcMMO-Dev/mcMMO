package com.gmail.nossr50.datatypes.skills.subskills.acrobatics;

import com.gmail.nossr50.config.AdvancedConfig;
import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.datatypes.skills.XPGainReason;
import com.gmail.nossr50.datatypes.skills.subskills.interfaces.RandomChance;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.skills.acrobatics.Acrobatics;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.Misc;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.skills.PerksUtils;
import com.gmail.nossr50.util.skills.SkillActivationType;
import com.gmail.nossr50.util.skills.SkillUtils;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

public class Roll extends AcrobaticsSubSkill implements RandomChance {
    private int fallTries = 0;
    protected Location lastFallLocation;

    public Roll() {
        super("Roll", EventPriority.HIGHEST, SubSkillType.ACROBATICS_ROLL);
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
        if(!EventUtils.isRealPlayerDamaged(entityDamageEvent))
            return false;

        switch (entityDamageEvent.getCause()) {
            case FALL:

                //Grab the player
                McMMOPlayer mcMMOPlayer = EventUtils.getMcMMOPlayer(entityDamageEvent.getEntity());

                /*
                 * Check for success
                 */
                Player player = (Player) ((EntityDamageEvent) event).getEntity();
                if (canRoll(player)) {
                    if(Permissions.isSubSkillEnabled(player, SubSkillType.ACROBATICS_ROLL))
                    entityDamageEvent.setDamage(rollCheck(player, mcMMOPlayer, entityDamageEvent.getDamage()));

                    if (entityDamageEvent.getFinalDamage() == 0) {
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
        return ("mcmmo.ability."+getPrimaryKeyName()+"."+getConfigKeyName()).toLowerCase();
    }

    /**
     * Checks if a player has permission to use this skill
     *
     * @param player target player
     * @return true if player has permission
     */
    @Override
    public boolean hasPermission(Player player) {
        return Permissions.isSubSkillEnabled(player, this);
    }

    /**
     * Adds detailed stats specific to this skill
     *
     * @param componentBuilder target component builder
     * @param player target player
     */
    @Override
    public void addStats(ComponentBuilder componentBuilder, Player player) {
        String rollChance, rollChanceLucky, gracefulRollChance, gracefulRollChanceLucky;

        /* Values related to the player */
        PlayerProfile playerProfile = UserManager.getPlayer(player).getProfile();
        float skillValue = playerProfile.getSkillLevel(getPrimarySkill());
        boolean isLucky = Permissions.lucky(player, getPrimarySkill());

        String[] rollStrings = SkillUtils.calculateAbilityDisplayValues(skillValue, SubSkillType.ACROBATICS_ROLL, isLucky);
        rollChance = rollStrings[0];
        rollChanceLucky = rollStrings[1];

        /*
         * Graceful is double the odds of a normal roll
         */
        String[] gracefulRollStrings = SkillUtils.calculateAbilityDisplayValuesCustom(skillValue,
                SubSkillType.ACROBATICS_ROLL,
                isLucky,
                AdvancedConfig.getInstance().getMaxBonusLevel(this) / 2,
                AdvancedConfig.getInstance().getMaxChance(this));
        gracefulRollChance = gracefulRollStrings[0];
        gracefulRollChanceLucky = gracefulRollStrings[1];

        /*
         *   Append the messages
         */

        /*componentBuilder.append(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Acrobatics.Effect.2"), LocaleLoader.getString("Acrobatics.Effect.3")));
        componentBuilder.append("\n");*/

        //Acrobatics.SubSkill.Roll.Chance
        componentBuilder.append(LocaleLoader.getString("Acrobatics.SubSkill.Roll.Chance", rollChance) + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", rollChanceLucky) : ""));
        componentBuilder.append("\n");
        componentBuilder.append(LocaleLoader.getString("Acrobatics.SubSkill.Roll.GraceChance", gracefulRollChance) + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", gracefulRollChanceLucky) : ""));
        //Activation Tips
        componentBuilder.append("\n").append(LocaleLoader.getString("JSON.Hover.Tips")).append("\n");
        componentBuilder.append(getTips());
        componentBuilder.append("\n");
        //Advanced

        //Lucky Notice
        if(isLucky)
        {
            componentBuilder.append(LocaleLoader.getString("JSON.JWrapper.Perks.Header"));
            componentBuilder.append("\n");
            componentBuilder.append(LocaleLoader.getString("JSON.JWrapper.Perks.Lucky", "33"));
        }

    }

    /**
     * Gets the maximum chance for this interaction to succeed
     *
     * @return maximum chance for this outcome to succeed
     */
    @Override
    public double getRandomChanceMaxChance() {
        return AdvancedConfig.getInstance().getMaxChance(this);
    }

    /**
     * The maximum bonus level for this skill
     * This is when the skills level no longer increases the odds of success
     * For example, setting this to 25 will mean the RandomChance success chance no longer grows after 25
     *
     * @return the maximum bonus from skill level for this skill
     */
    @Override
    public int getRandomChanceMaxBonus() {
        return AdvancedConfig.getInstance().getMaxBonusLevel(this);
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
        return !isExploiting(player) && Permissions.isSubSkillEnabled(player, SubSkillType.ACROBATICS_ROLL);
    }

    /**
     * Handle the damage reduction and XP gain from the Roll ability
     *
     * @param damage The amount of damage initially dealt by the event
     * @return the modified event damage if the ability was successful, the original event damage otherwise
     */
    private double rollCheck(Player player, McMMOPlayer mcMMOPlayer, double damage) {

        int skillLevel = mcMMOPlayer.getSkillLevel(getPrimarySkill());

        if (player.isSneaking()) {
            return gracefulRollCheck(player, mcMMOPlayer, damage, skillLevel);
        }

        double modifiedDamage = calculateModifiedRollDamage(damage, Acrobatics.rollThreshold);

        if (!isFatal(player, modifiedDamage) && SkillUtils.isActivationSuccessful(SkillActivationType.RANDOM_LINEAR_100_SCALE_WITH_CAP, SubSkillType.ACROBATICS_ROLL, player, getPrimarySkill(), skillLevel, getActivationChance(mcMMOPlayer))) {
            NotificationManager.sendPlayerInformation(player, NotificationType.SUBSKILL_MESSAGE, "Acrobatics.Roll.Text");
            //player.sendMessage(LocaleLoader.getString("Acrobatics.Roll.Text"));

            if (!SkillUtils.cooldownExpired((long) mcMMOPlayer.getTeleportATS(), Config.getInstance().getXPAfterTeleportCooldown())) {
                SkillUtils.applyXpGain(mcMMOPlayer, getPrimarySkill(), calculateRollXP(player, damage, true), XPGainReason.PVE);
            }

            return modifiedDamage;
        }
        else if (!isFatal(player, damage)) {
            if (!SkillUtils.cooldownExpired((long) mcMMOPlayer.getTeleportATS(), Config.getInstance().getXPAfterTeleportCooldown())) {
                SkillUtils.applyXpGain(mcMMOPlayer, getPrimarySkill(), calculateRollXP(player, damage, false), XPGainReason.PVE);
            }
        }

        lastFallLocation = player.getLocation();

        return damage;
    }

    private int getActivationChance(McMMOPlayer mcMMOPlayer) {
        return PerksUtils.handleLuckyPerks(mcMMOPlayer.getPlayer(), getPrimarySkill());
    }

    /**
     * Handle the damage reduction and XP gain from the Graceful Roll ability
     *
     * @param damage The amount of damage initially dealt by the event
     * @return the modified event damage if the ability was successful, the original event damage otherwise
     */
    private double gracefulRollCheck(Player player, McMMOPlayer mcMMOPlayer, double damage, int skillLevel) {
        double modifiedDamage = calculateModifiedRollDamage(damage, Acrobatics.gracefulRollThreshold);

        if (!isFatal(player, modifiedDamage)
                && SkillUtils.isActivationSuccessfulCustom(player,
                this,
                AdvancedConfig.getInstance().getMaxChance(SubSkillType.ACROBATICS_ROLL),
                AdvancedConfig.getInstance().getMaxBonusLevel(SubSkillType.ACROBATICS_ROLL) / 2)) //This effectively makes it so you reach the max chance for success at half the requirements of roll's max chance (which would make graceful roll twice as likely per skill level)
        {
            NotificationManager.sendPlayerInformation(player, NotificationType.SUBSKILL_MESSAGE, "Acrobatics.Ability.Proc");
            SkillUtils.applyXpGain(mcMMOPlayer, getPrimarySkill(), calculateRollXP(player, damage, true), XPGainReason.PVE);

            return modifiedDamage;
        }
        else if (!isFatal(player, damage)) {
            SkillUtils.applyXpGain(mcMMOPlayer, getPrimarySkill(), calculateRollXP(player, damage, false), XPGainReason.PVE);
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
        if (!Config.getInstance().getAcrobaticsPreventAFK()) {
            return false;
        }

        if (player.getInventory().getItemInMainHand().getType() == Material.ENDER_PEARL || player.isInsideVehicle()) {
            return true;
        }

        Location fallLocation = player.getLocation();
        int maxTries = Config.getInstance().getAcrobaticsAFKMaxTries();

        boolean sameLocation = (lastFallLocation != null && Misc.isNear(lastFallLocation, fallLocation, 2));

        fallTries = sameLocation ? Math.min(fallTries + 1, maxTries) : Math.max(fallTries - 1, 0);
        lastFallLocation = fallLocation;

        return fallTries + 1 > maxTries;
    }

    private float calculateRollXP(Player player, double damage, boolean isRoll) {
        ItemStack boots = player.getInventory().getBoots();
        float xp = (float) (damage * (isRoll ? Acrobatics.rollXpModifier : Acrobatics.fallXpModifier));

        if (boots != null && boots.containsEnchantment(Enchantment.PROTECTION_FALL)) {
            xp *= Acrobatics.featherFallXPModifier;
        }

        return xp;
    }

    protected static double calculateModifiedRollDamage(double damage, double damageThreshold) {
        return Math.max(damage - damageThreshold, 0.0);
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
        player.sendMessage(LocaleLoader.getString("Commands.MmoInfo.Stats",
                            LocaleLoader.getString("Acrobatics.SubSkill.Roll.Stats", getStats(player)[0], getStats(player)[1])));

        //Mechanics
        player.sendMessage(LocaleLoader.getString("Commands.MmoInfo.Mechanics"));
        player.sendMessage(getMechanics());
    }

    /**
     * Returns a collection of strings about how a skill works
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
        double rollChanceHalfMax, graceChanceHalfMax, maxBonusLevel, curve, damageThreshold, chancePerLevel;

        curve = AdvancedConfig.getInstance().getMaxChance(this);
        maxBonusLevel = (double) AdvancedConfig.getInstance().getMaxBonusLevel(this);

        //Chance
        rollChanceHalfMax   = 100 * SkillUtils.getChanceOfSuccess(maxBonusLevel / 2, maxBonusLevel, curve);
        graceChanceHalfMax  = 100 * SkillUtils.getChanceOfSuccess(maxBonusLevel / 2, maxBonusLevel, curve / 2);
        damageThreshold     = AdvancedConfig.getInstance().getRollDamageThreshold();

        chancePerLevel = (1/curve) * maxBonusLevel;


        return LocaleLoader.getString("Acrobatics.SubSkill.Roll.Mechanics", rollChanceHalfMax, graceChanceHalfMax, maxBonusLevel, chancePerLevel, damageThreshold, damageThreshold * 2);
    }

    /**
     * Get an array of various stats for a player
     *
     * @param player target player
     * @return stat array for target player for this skill
     */
    @Override
    public Double[] getStats(Player player)
    {
        double curve, maxBonusLevel, playerChanceRoll, playerChanceGrace;

        curve = AdvancedConfig.getInstance().getMaxChance(this);
        maxBonusLevel = (double) AdvancedConfig.getInstance().getMaxBonusLevel(this);

        playerChanceRoll        = 100 * SkillUtils.getChanceOfSuccess(UserManager.getPlayer(player).getSkillLevel(getPrimarySkill()), maxBonusLevel, curve);
        playerChanceGrace       = 100 * SkillUtils.getChanceOfSuccess(UserManager.getPlayer(player).getSkillLevel(getPrimarySkill()), maxBonusLevel, curve / 2);

        Double[] stats = { playerChanceRoll, playerChanceGrace};
        return stats;
    }
}