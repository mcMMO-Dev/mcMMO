package com.gmail.nossr50.datatypes.skills.subskills.acrobatics;

import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.player.NotificationManager;
import com.gmail.nossr50.util.player.UserManager;
import com.gmail.nossr50.util.random.Probability;
import com.gmail.nossr50.util.random.ProbabilityUtil;
import com.gmail.nossr50.util.skills.PerksUtils;
import com.gmail.nossr50.util.skills.RankUtils;
import com.gmail.nossr50.util.skills.SkillUtils;
import com.gmail.nossr50.util.sounds.SoundManager;
import com.gmail.nossr50.util.sounds.SoundType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.Locale;

public class Roll extends AcrobaticsSubSkill {


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

        if (entityDamageEvent.getCause() == EntityDamageEvent.DamageCause.FALL) {//Grab the player
            McMMOPlayer mcMMOPlayer = EventUtils.getMcMMOPlayer(entityDamageEvent.getEntity());

            if (mcMMOPlayer == null)
                return false;

            /*
             * Check for success
             */
            Player player = (Player) ((EntityDamageEvent) event).getEntity();
            if (canRoll(player)) {
                entityDamageEvent.setDamage(rollCheck(player, mcMMOPlayer, entityDamageEvent.getFinalDamage()));

                if (entityDamageEvent.getFinalDamage() == 0) {
                    entityDamageEvent.setCancelled(true);
                    return true;
                }
            } else if(mcMMO.p.getSkillTools().doesPlayerHaveSkillPermission(player, PrimarySkillType.ACROBATICS)) {
                //Give XP Anyways
                SkillUtils.applyXpGain(mcMMOPlayer, getPrimarySkill(), calculateRollXP(player, ((EntityDamageEvent) event).getFinalDamage(), false), XPGainReason.PVE);
            }
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
        return ("mcmmo.ability."+getPrimaryKeyName()+"."+getConfigKeyName()).toLowerCase(Locale.ENGLISH);
    }

    /**
     * Checks if a player has permission to use this skill
     *
     * @param player target player
     * @return true if player has permission
     */
    @Override
    public boolean hasPermission(Player player) {
        return Permissions.isSubSkillEnabled(player, getSubSkillType());
    }

    /**
     * Adds detailed stats specific to this skill
     *
     * @param componentBuilder target component builder
     * @param player target player
     */
    @Override
    public void addStats(TextComponent.Builder componentBuilder, Player player) {
        String rollChance, rollChanceLucky, gracefulRollChance, gracefulRollChanceLucky;

        /* Values related to the player */
        PlayerProfile playerProfile = UserManager.getPlayer(player).getProfile();
        float skillValue = playerProfile.getSkillLevel(getPrimarySkill());
        boolean isLucky = Permissions.lucky(player, getPrimarySkill());

        String[] rollStrings = ProbabilityUtil.getRNGDisplayValues(player, SubSkillType.ACROBATICS_ROLL);
        rollChance = rollStrings[0];
        rollChanceLucky = rollStrings[1];

        /*
         * Graceful is double the odds of a normal roll
         */
        Probability probability = getRollProbability(player);
        Probability gracefulProbability = Probability.ofValue(probability.getValue() * 2);
        String[] gracefulRollStrings = ProbabilityUtil.getRNGDisplayValues(gracefulProbability);
        gracefulRollChance = gracefulRollStrings[0];
        gracefulRollChanceLucky = gracefulRollStrings[1];

        /*
         *   Append the messages
         */

        /*componentBuilder.append(LocaleLoader.getString("Effects.Template", LocaleLoader.getString("Acrobatics.Effect.2"), LocaleLoader.getString("Acrobatics.Effect.3")));
        componentBuilder.append("\n");*/

        //Acrobatics.SubSkill.Roll.Chance
        componentBuilder.append(Component.text(LocaleLoader.getString("Acrobatics.SubSkill.Roll.Chance", rollChance) + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", rollChanceLucky) : "")));
        componentBuilder.append(Component.newline());
        componentBuilder.append(Component.text(LocaleLoader.getString("Acrobatics.SubSkill.Roll.GraceChance", gracefulRollChance) + (isLucky ? LocaleLoader.getString("Perks.Lucky.Bonus", gracefulRollChanceLucky) : "")));
        //Activation Tips
        componentBuilder.append(Component.newline()).append(Component.text(LocaleLoader.getString("JSON.Hover.Tips"))).append(Component.newline());
        componentBuilder.append(Component.text(getTips()));
        componentBuilder.append(Component.newline());
        //Advanced

        //Lucky Notice
        if(isLucky)
        {
            componentBuilder.append(Component.text(LocaleLoader.getString("JSON.JWrapper.Perks.Header")));
            componentBuilder.append(Component.newline());
            componentBuilder.append(Component.text(LocaleLoader.getString("JSON.JWrapper.Perks.Lucky", "33")));
        }

    }

    @NotNull
    private Probability getRollProbability(Player player) {
        return ProbabilityUtil.getSubSkillProbability(SubSkillType.ACROBATICS_ROLL, player);
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
        return RankUtils.hasUnlockedSubskill(player, SubSkillType.ACROBATICS_ROLL) && Permissions.isSubSkillEnabled(player, SubSkillType.ACROBATICS_ROLL);
    }

    /**
     * Handle the damage reduction and XP gain from the Roll ability
     *
     * @param damage The amount of damage initially dealt by the event
     * @return the modified event damage if the ability was successful, the original event damage otherwise
     */
    @VisibleForTesting
    public double rollCheck(Player player, McMMOPlayer mcMMOPlayer, double damage) {

        int skillLevel = mcMMOPlayer.getSkillLevel(getPrimarySkill());

        if (player.isSneaking()) {
            return gracefulRollCheck(player, mcMMOPlayer, damage, skillLevel);
        }

        double modifiedDamage = calculateModifiedRollDamage(damage, mcMMO.p.getAdvancedConfig().getRollDamageThreshold());

        if (!isFatal(player, modifiedDamage)
                && ProbabilityUtil.isSkillRNGSuccessful(SubSkillType.ACROBATICS_ROLL, player)) {
            NotificationManager.sendPlayerInformation(player, NotificationType.SUBSKILL_MESSAGE, "Acrobatics.Roll.Text");
            SoundManager.sendCategorizedSound(player, player.getLocation(), SoundType.ROLL_ACTIVATED, SoundCategory.PLAYERS);
            //player.sendMessage(LocaleLoader.getString("Acrobatics.Roll.Text"));

            //if (!SkillUtils.cooldownExpired((long) mcMMOPlayer.getTeleportATS(), Config.getInstance().getXPAfterTeleportCooldown())) {
            if(!isExploiting(player) && mcMMOPlayer.getAcrobaticsManager().canGainRollXP())
                SkillUtils.applyXpGain(mcMMOPlayer, getPrimarySkill(), calculateRollXP(player, damage, true), XPGainReason.PVE);
            //}

            addFallLocation(player);
            return modifiedDamage;
        }
        else if (!isFatal(player, damage)) {
            //if (!SkillUtils.cooldownExpired((long) mcMMOPlayer.getTeleportATS(), Config.getInstance().getXPAfterTeleportCooldown())) {
            if(!isExploiting(player) && mcMMOPlayer.getAcrobaticsManager().canGainRollXP())
                SkillUtils.applyXpGain(mcMMOPlayer, getPrimarySkill(), calculateRollXP(player, damage, false), XPGainReason.PVE);
            //}
        }

        addFallLocation(player);
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
        double modifiedDamage = calculateModifiedRollDamage(damage, mcMMO.p.getAdvancedConfig().getRollDamageThreshold() * 2);

        Probability gracefulProbability = getGracefulProbability(player);

        if (!isFatal(player, modifiedDamage)
                //TODO: Graceful isn't sending out an event
                && ProbabilityUtil.isStaticSkillRNGSuccessful(PrimarySkillType.ACROBATICS, player, gracefulProbability))
        {
            NotificationManager.sendPlayerInformation(player, NotificationType.SUBSKILL_MESSAGE, "Acrobatics.Ability.Proc");
            SoundManager.sendCategorizedSound(player, player.getLocation(), SoundType.ROLL_ACTIVATED, SoundCategory.PLAYERS,0.5F);
            if(!isExploiting(player) && mcMMOPlayer.getAcrobaticsManager().canGainRollXP())
                SkillUtils.applyXpGain(mcMMOPlayer, getPrimarySkill(), calculateRollXP(player, damage, true), XPGainReason.PVE);

            addFallLocation(player);
            return modifiedDamage;
        }
        else if (!isFatal(player, damage)) {
            if(!isExploiting(player) && mcMMOPlayer.getAcrobaticsManager().canGainRollXP())
                SkillUtils.applyXpGain(mcMMOPlayer, getPrimarySkill(), calculateRollXP(player, damage, false), XPGainReason.PVE);
            
            addFallLocation(player);
        }

        return damage;
    }

    @NotNull
    public static Probability getGracefulProbability(Player player) {
        double gracefulOdds = ProbabilityUtil.getSubSkillProbability(SubSkillType.ACROBATICS_ROLL, player).getValue() * 2;
        return Probability.ofValue(gracefulOdds);
    }

    /**
     * Check if the player is "farming" Acrobatics XP using
     * exploits in the game.
     *
     * @return true if exploits are detected, false otherwise
     */
    private boolean isExploiting(Player player) {
        if (!ExperienceConfig.getInstance().isAcrobaticsExploitingPrevented()) {
            return false;
        }

        McMMOPlayer mcMMOPlayer = UserManager.getPlayer(player);

        if (ItemUtils.hasItemInEitherHand(player, Material.ENDER_PEARL) || player.isInsideVehicle()) {
            if(mcMMOPlayer.isDebugMode()) {
                mcMMOPlayer.getPlayer().sendMessage("Acrobatics XP Prevented: Ender Pearl or Inside Vehicle");
            }
            return true;
        }

        if(UserManager.getPlayer(player).getAcrobaticsManager().hasFallenInLocationBefore(getBlockLocation(player)))
        {
            if(mcMMOPlayer.isDebugMode()) {
                mcMMOPlayer.getPlayer().sendMessage("Acrobatics XP Prevented: Fallen in location before");
            }

            return true;
        }

        return false; //NOT EXPLOITING
    }

    private float calculateRollXP(Player player, double damage, boolean isRoll) {
        //Clamp Damage to account for insane DRs
        damage = Math.min(20, damage);

        ItemStack boots = player.getInventory().getBoots();
        float xp = (float) (damage * (isRoll ? ExperienceConfig.getInstance().getRollXPModifier() : ExperienceConfig.getInstance().getFallXPModifier()));

        if (boots != null && boots.containsEnchantment(mcMMO.p.getEnchantmentMapper().getFeatherFalling())) {
            xp *= ExperienceConfig.getInstance().getFeatherFallXPModifier();
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
                            LocaleLoader.getString("Acrobatics.SubSkill.Roll.Stats", getStats(player))));

        //Mechanics
        player.sendMessage(LocaleLoader.getString("Commands.MmoInfo.Mechanics"));
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
        //4 = damage threshold when rolling
        //5 = damage threshold when rolling with grace
        //6 = half of level where maximum bonus is reached
        /*
        Roll:
            # ChanceMax: Maximum chance of rolling when on <MaxBonusLevel> or higher
            # MaxBonusLevel: On this level or higher, the roll chance will not go higher than <ChanceMax>
            # DamageThreshold: The max damage a player can negate with a roll
            ChanceMax: 100.0
            MaxBonusLevel: 100
            DamageThreshold: 7.0
         */

        return "Under Construction: This will work in a future update.";
//
//        double rollChanceHalfMax, graceChanceHalfMax, damageThreshold, chancePerLevel;
//
//        //Chance to roll at half max skill
//        RandomChanceSkill rollHalfMaxSkill = new RandomChanceSkill(null, subSkillType);
//        int halfMaxSkillValue = mcMMO.p.getAdvancedConfig().getMaxBonusLevel(SubSkillType.ACROBATICS_ROLL)/2;
//        rollHalfMaxSkill.setSkillLevel(halfMaxSkillValue);
//
//        //Chance to graceful roll at full skill
//        RandomChanceSkill rollGraceHalfMaxSkill = new RandomChanceSkill(null, subSkillType);
//        rollGraceHalfMaxSkill.setSkillLevel(halfMaxSkillValue * 2); //Double the effective odds
//
//        //Chance to roll per level
//        RandomChanceSkill rollOneSkillLevel = new RandomChanceSkill(null, subSkillType);
//        rollGraceHalfMaxSkill.setSkillLevel(1); //Level 1 skill
//
//        //Chance Stat Calculations
//        rollChanceHalfMax       = RandomChanceUtil.getRandomChanceExecutionChance(rollHalfMaxSkill);
//        graceChanceHalfMax      = RandomChanceUtil.getRandomChanceExecutionChance(rollGraceHalfMaxSkill);
//        damageThreshold         = mcMMO.p.getAdvancedConfig().getRollDamageThreshold();
//
//        chancePerLevel          = RandomChanceUtil.getRandomChanceExecutionChance(rollOneSkillLevel);
//
//        double maxLevel         = mcMMO.p.getAdvancedConfig().getMaxBonusLevel(SubSkillType.ACROBATICS_ROLL);
//
//        return LocaleLoader.getString("Acrobatics.SubSkill.Roll.Mechanics", rollChanceHalfMax, graceChanceHalfMax, maxLevel, chancePerLevel, damageThreshold, damageThreshold * 2,halfMaxSkillValue);
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
        double playerChanceRoll = ProbabilityUtil.getSubSkillProbability(subSkillType, player).getValue();
        double playerChanceGrace = playerChanceRoll * 2;

        double gracefulOdds = ProbabilityUtil.getSubSkillProbability(subSkillType, player).getValue() * 2;

        return new Double[]{ playerChanceRoll, playerChanceGrace };
    }

    public void addFallLocation(@NotNull Player player)
    {
        UserManager.getPlayer(player).getAcrobaticsManager().addLocationToFallMap(getBlockLocation(player));
    }

    public @NotNull Location getBlockLocation(@NotNull Player player)
    {
        return player.getLocation().getBlock().getLocation();
    }
}
