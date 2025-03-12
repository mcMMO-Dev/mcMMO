package com.gmail.nossr50.datatypes.skills.subskills.acrobatics;

import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.experience.XPGainReason;
import com.gmail.nossr50.datatypes.interactions.NotificationType;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.datatypes.skills.SubSkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.EventUtils;
import com.gmail.nossr50.util.ItemUtils;
import com.gmail.nossr50.util.Permissions;
import com.gmail.nossr50.util.random.Probability;
import com.gmail.nossr50.util.random.ProbabilityUtil;
import com.gmail.nossr50.util.skills.RankUtils;
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

import static com.gmail.nossr50.util.player.NotificationManager.sendPlayerInformation;
import static com.gmail.nossr50.util.random.ProbabilityUtil.getSubSkillProbability;
import static com.gmail.nossr50.util.skills.SkillUtils.applyXpGain;
import static com.gmail.nossr50.util.sounds.SoundManager.sendCategorizedSound;

public class Roll extends AcrobaticsSubSkill {


    public static final String GRACEFUL_ROLL_ACTIVATED_LOCALE_STR_KEY = "Acrobatics.Ability.Proc";
    public static final String ROLL_ACTIVATED_LOCALE_KEY = "Acrobatics.Roll.Text";

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
        final EntityDamageEvent entityDamageEvent = (EntityDamageEvent) event;

        //Make sure a real player was damaged in this event
        if (!EventUtils.isRealPlayerDamaged(entityDamageEvent)
                || !entityDamageEvent.isApplicable(EntityDamageEvent.DamageModifier.MAGIC))
            return false;

        if (entityDamageEvent.getCause() == EntityDamageEvent.DamageCause.FALL) {
            final McMMOPlayer mmoPlayer = EventUtils.getMcMMOPlayer(entityDamageEvent.getEntity());

            if (mmoPlayer == null)
                return false;

            /*
             * Check for success
             */
            
            if (canRoll(mmoPlayer)) {
                final RollResult rollResult
                        = rollCheck(mmoPlayer, entityDamageEvent);
                if (rollResult == null) {
                    // no-op - fall was fatal or otherwise did not get processed
                    return false;
                }

                // Roll happened, reduce damage, send messages and XP
                if (rollResult.isRollSuccess()) {
                    // Clear out the damage from falling so that way our modified damage doesn't get re-reduced by protection/feather falling
                    entityDamageEvent.setDamage(0);
                    // Send the damage is MAGIC so that it cuts through all resistances
                    // This is fine because we considered protection/featherfalling in the rollCheck method
                    entityDamageEvent.setDamage(EntityDamageEvent.DamageModifier.MAGIC, rollResult.getModifiedDamage());

                    if (entityDamageEvent.getFinalDamage() == 0) {
                        entityDamageEvent.setCancelled(true);
                    }

                    final String key
                            = rollResult.isGraceful() ? GRACEFUL_ROLL_ACTIVATED_LOCALE_STR_KEY : ROLL_ACTIVATED_LOCALE_KEY;
                    sendPlayerInformation(mmoPlayer.getPlayer(), NotificationType.SUBSKILL_MESSAGE, key);
                    sendCategorizedSound(mmoPlayer.getPlayer(), mmoPlayer.getPlayer().getLocation(),
                            SoundType.ROLL_ACTIVATED, SoundCategory.PLAYERS,0.5F);
                }

                if (!rollResult.isExploiting() && rollResult.getXpGain() > 0) {
                    applyXpGain(mmoPlayer, getPrimarySkill(), rollResult.getXpGain(), XPGainReason.PVE);
                }

                // Player didn't die, so add the location to the list
                addFallLocation(mmoPlayer);
                return true;
            // We give Acrobatics XP for fall damage even if they haven't unlocked roll
            } else if (mcMMO.p.getSkillTools().doesPlayerHaveSkillPermission(mmoPlayer.getPlayer(), PrimarySkillType.ACROBATICS)) {
                //Give XP
                applyXpGain(mmoPlayer, getPrimarySkill(), calculateRollXP(mmoPlayer, ((EntityDamageEvent) event).getFinalDamage(), false), XPGainReason.PVE);
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
     * @param mmoPlayer target player
     */
    @Override
    public void addStats(TextComponent.Builder componentBuilder, McMMOPlayer mmoPlayer) {
        String rollChance, rollChanceLucky, gracefulRollChance, gracefulRollChanceLucky;

        /* Values related to the player */
        float skillValue = mmoPlayer.getSkillLevel(getPrimarySkill());
        boolean isLucky = Permissions.lucky(mmoPlayer.getPlayer(), getPrimarySkill());

        String[] rollStrings = ProbabilityUtil.getRNGDisplayValues(mmoPlayer, SubSkillType.ACROBATICS_ROLL);
        rollChance = rollStrings[0];
        rollChanceLucky = rollStrings[1];

        /*
         * Graceful is double the odds of a normal roll
         */
        Probability probability = getRollProbability(mmoPlayer);
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
        if (isLucky) {
            componentBuilder.append(Component.text(LocaleLoader.getString("JSON.JWrapper.Perks.Header")));
            componentBuilder.append(Component.newline());
            componentBuilder.append(Component.text(LocaleLoader.getString("JSON.JWrapper.Perks.Lucky", "33")));
        }

    }

    @NotNull
    private Probability getRollProbability(McMMOPlayer mmoPlayer) {
        return getSubSkillProbability(SubSkillType.ACROBATICS_ROLL, mmoPlayer);
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

    @VisibleForTesting
    public boolean canRoll(McMMOPlayer mmoPlayer) {
        return RankUtils.hasUnlockedSubskill(mmoPlayer.getPlayer(), SubSkillType.ACROBATICS_ROLL)
                && Permissions.isSubSkillEnabled(mmoPlayer.getPlayer(), SubSkillType.ACROBATICS_ROLL);
    }

    /**
     * Handle the damage reduction and XP gain from the Roll / Graceful Roll ability
     *
     * @param entityDamageEvent the event to modify in the event of roll success
     * @return the modified event damage if the ability was successful, the original event damage otherwise
     */
    @VisibleForTesting
    public RollResult rollCheck(McMMOPlayer mmoPlayer, EntityDamageEvent entityDamageEvent) {
        double baseDamage = entityDamageEvent.getFinalDamage();
        final boolean isGraceful = mmoPlayer.getPlayer().isSneaking();
        final RollResult.Builder rollResultBuilder
                = new RollResult.Builder(entityDamageEvent, isGraceful);
        final Probability probability
                = isGraceful ? getGracefulProbability(mmoPlayer) : getNonGracefulProbability(mmoPlayer);

        double modifiedDamage = calculateModifiedRollDamage(baseDamage,
                mcMMO.p.getAdvancedConfig().getRollDamageThreshold() * 2);
        rollResultBuilder.modifiedDamage(modifiedDamage);

        boolean isExploiting = isPlayerExploitingAcrobatics(mmoPlayer);
        rollResultBuilder.exploiting(isExploiting);
        // They Rolled
        if (!isFatal(mmoPlayer, modifiedDamage)
                && ProbabilityUtil.isStaticSkillRNGSuccessful(PrimarySkillType.ACROBATICS, mmoPlayer, probability)) {
            rollResultBuilder.rollSuccess(true);
            rollResultBuilder.exploiting(isExploiting);
            final boolean canGainXp = mmoPlayer.getAcrobaticsManager().canGainRollXP();

            if (!isExploiting && canGainXp) {
                rollResultBuilder.xpGain((int) calculateRollXP(mmoPlayer, baseDamage, true));
            }

            return rollResultBuilder.build();
        // They did not roll, but they also did not die so reward XP as appropriate
        } else if (!isFatal(mmoPlayer, baseDamage)) {
            rollResultBuilder.rollSuccess(false);
            final boolean canGainXp = mmoPlayer.getAcrobaticsManager().canGainRollXP();
            if (!isExploiting && canGainXp) {
                rollResultBuilder.xpGain((int) calculateRollXP(mmoPlayer, baseDamage, false));
            }

            return rollResultBuilder.build();
        }
        // Fall was fatal return null
        return null;
    }

    @NotNull
    public static Probability getGracefulProbability(McMMOPlayer mmoPlayer) {
        double gracefulOdds = getSubSkillProbability(SubSkillType.ACROBATICS_ROLL, mmoPlayer).getValue() * 2;
        return Probability.ofValue(gracefulOdds);
    }

    public static Probability getNonGracefulProbability(McMMOPlayer mmoPlayer) {
        double gracefulOdds = getSubSkillProbability(SubSkillType.ACROBATICS_ROLL, mmoPlayer).getValue();
        return Probability.ofValue(gracefulOdds);
    }

    /**
     * Check if the player is "farming" Acrobatics XP using
     * exploits in the game.
     *
     * @return true if exploits are detected, false otherwise
     */
    private boolean isPlayerExploitingAcrobatics(McMMOPlayer mmoPlayer) {
        if (!ExperienceConfig.getInstance().isAcrobaticsExploitingPrevented()) {
            return false;
        }

        if (ItemUtils.hasItemInEitherHand(mmoPlayer.getPlayer(), Material.ENDER_PEARL) || mmoPlayer.getPlayer().isInsideVehicle()) {
            if (mmoPlayer.isDebugMode()) {
                mmoPlayer.getPlayer().sendMessage("Acrobatics XP Prevented: Ender Pearl or Inside Vehicle");
            }
            return true;
        }

        if (mmoPlayer.getAcrobaticsManager().hasFallenInLocationBefore(getBlockLocation(mmoPlayer))) {
            if (mmoPlayer.isDebugMode()) {
                mmoPlayer.getPlayer().sendMessage("Acrobatics XP Prevented: Fallen in location before");
            }

            return true;
        }

        return false; //NOT EXPLOITING
    }

    private float calculateRollXP(McMMOPlayer mmoPlayer, double damage, boolean isRoll) {
        //Clamp Damage to account for insane DRs
        damage = Math.min(20, damage);

        ItemStack boots = mmoPlayer.getPlayer().getInventory().getBoots();
        float xp = (float) (damage * (isRoll ? ExperienceConfig.getInstance().getRollXPModifier() : ExperienceConfig.getInstance().getFallXPModifier()));

        if (boots != null && boots.containsEnchantment(mcMMO.p.getEnchantmentMapper().getFeatherFalling())) {
            xp *= ExperienceConfig.getInstance().getFeatherFallXPModifier();
        }

        return xp;
    }

    protected static double calculateModifiedRollDamage(double damage, double damageThreshold) {
        return Math.max(damage - damageThreshold, 0.0);
    }

    private boolean isFatal(McMMOPlayer mmoPlayer, double damage) {
        return mmoPlayer.getPlayer().getHealth() - damage <= 0;
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
     * @param mmoPlayer the target player
     */
    @Override
    public void printInfo(McMMOPlayer mmoPlayer) {
        //Header
        super.printInfo(mmoPlayer);

        //Start the description string.
        //player.sendMessage(getDescription());
        //Player stats
        mmoPlayer.getPlayer().sendMessage(LocaleLoader.getString("Commands.MmoInfo.Stats",
                            LocaleLoader.getString("Acrobatics.SubSkill.Roll.Stats", getStats(mmoPlayer))));

        //Mechanics
        mmoPlayer.getPlayer().sendMessage(LocaleLoader.getString("Commands.MmoInfo.Mechanics"));
        mmoPlayer.getPlayer().sendMessage(getMechanics());
    }

    @Override
    public String getMechanics() {
        return "Under Construction: This will work in a future update.";
    }

    /**
     * Get an array of various stats for a player
     *
     * @param mmoPlayer target player
     * @return stat array for target player for this skill
     */
    @Override
    public Double[] getStats(McMMOPlayer mmoPlayer) {
        double playerChanceRoll = getSubSkillProbability(subSkillType, mmoPlayer).getValue();
        double playerChanceGrace = playerChanceRoll * 2;

        return new Double[]{ playerChanceRoll, playerChanceGrace };
    }

    public void addFallLocation(@NotNull McMMOPlayer mmoPlayer) {
        mmoPlayer.getAcrobaticsManager().addLocationToFallMap(getBlockLocation(mmoPlayer));
    }

    public @NotNull Location getBlockLocation(@NotNull McMMOPlayer mmoPlayer) {
        return mmoPlayer.getPlayer().getLocation().getBlock().getLocation();
    }
}
