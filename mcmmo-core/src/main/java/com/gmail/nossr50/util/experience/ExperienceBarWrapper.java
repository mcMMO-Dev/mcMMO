package com.gmail.nossr50.util.experience;

import com.gmail.nossr50.datatypes.player.BukkitMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.StringUtils;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * A visual representation of a players skill level progress for a PrimarySkillType
 */
public class ExperienceBarWrapper {

    protected final BukkitMMOPlayer mcMMOPlayer;
    private final PrimarySkillType primarySkillType; //Primary Skill
    private BossBar bossBar;
    private int lastLevelUpdated;
    private final mcMMO pluginRef;

    /*
     * This is stored to help optimize updating the title
     */
    protected String niceSkillName;
    protected String title;

    public ExperienceBarWrapper(mcMMO pluginRef, PrimarySkillType primarySkillType, BukkitMMOPlayer mcMMOPlayer) {
        this.pluginRef = pluginRef;
        this.mcMMOPlayer = mcMMOPlayer;
        this.primarySkillType = primarySkillType;
        title = "";
        lastLevelUpdated = 0;

        //These vars are stored to help reduce operations involving strings
        niceSkillName = StringUtils.getCapitalized(primarySkillType.toString());

        //Create the bar
        initBar();
    }

    private void initBar() {
        title = getTitleTemplate();
        createBossBar();
    }

    public void updateTitle() {
        title = getTitleTemplate();
        bossBar.setTitle(title);
    }

    private String getTitleTemplate() {
        //If they are using extra details
        if(pluginRef.getConfigManager().getConfigLeveling().getEarlyGameBoost().isEnableEarlyGameBoost() && pluginRef.getPlayerLevelTools().qualifiesForEarlyGameBoost(mcMMOPlayer, primarySkillType)) {
                return pluginRef.getLocaleManager().getString("XPBar.Template.EarlyGameBoost");
        } else if(pluginRef.getConfigManager().getConfigLeveling().getConfigExperienceBars().isMoreDetailedXPBars())
            return pluginRef.getLocaleManager().getString("XPBar.Complex.Template", pluginRef.getLocaleManager().getString("XPBar."+niceSkillName, getLevel()), getCurrentXP(), getMaxXP(), getPowerLevel(), getPercentageOfLevel());

        return pluginRef.getLocaleManager().getString("XPBar." + niceSkillName, getLevel(), getCurrentXP(), getMaxXP(), getPowerLevel(), getPercentageOfLevel());
    }

    private int getLevel() {
        return mcMMOPlayer.getSkillLevel(primarySkillType);
    }

    private int getCurrentXP() {
        return mcMMOPlayer.getSkillXpLevel(primarySkillType);
    }

    private int getMaxXP() {
        return mcMMOPlayer.getXpToLevel(primarySkillType);
    }

    private int getPowerLevel() {
        return mcMMOPlayer.getPowerLevel();
    }

    private int getPercentageOfLevel() {
        return (int) (mcMMOPlayer.getProgressInCurrentSkillLevel(primarySkillType) * 100);
    }

    public String getTitle() {
        return bossBar.getTitle();
    }

    public void setTitle(String s) {
        bossBar.setTitle(s);
    }

    public BarColor getColor() {
        return bossBar.getColor();
    }

    public void setColor(BarColor barColor) {
        bossBar.setColor(barColor);
    }

    public BarStyle getStyle() {
        return bossBar.getStyle();
    }

    public void setStyle(BarStyle barStyle) {
        bossBar.setStyle(barStyle);
    }

    public double getProgress() {
        return bossBar.getProgress();
    }

    public void setProgress(double v) {
        //Clamp Values
        if (v < 0)
            bossBar.setProgress(0.0D);

        else if (v > 1)
            bossBar.setProgress(1.0D);
        else
            bossBar.setProgress(v);

        //Check player level
        if(pluginRef.getConfigManager().getConfigLeveling().getEarlyGameBoost().isEnableEarlyGameBoost() && pluginRef.getPlayerLevelTools().qualifiesForEarlyGameBoost(mcMMOPlayer, primarySkillType)) {
           setColor(BarColor.YELLOW);
        } else {
            setColor(pluginRef.getConfigManager().getConfigLeveling().getConfigExperienceBars().getXPBarColor(primarySkillType));
        }

        //Every time progress updates we need to check for a title update
        if (getLevel() != lastLevelUpdated || pluginRef.getConfigManager().getConfigLeveling().isMoreDetailedXPBars()) {
            updateTitle();
            lastLevelUpdated = getLevel();
        }
    }

    public List<Player> getPlayers() {
        return bossBar.getPlayers();
    }

    public boolean isVisible() {
        return bossBar.isVisible();
    }

    public void hideExperienceBar() {
        bossBar.setVisible(false);
    }

    public void showExperienceBar() {
        bossBar.setVisible(true);
    }

    /*public NamespacedKey getKey()
    {
        return bossBar
    }*/

    private void createBossBar() {
        bossBar = mcMMOPlayer.getPlayer().getServer().createBossBar(title,
                pluginRef.getConfigManager().getConfigLeveling().getXPBarColor(primarySkillType),
                pluginRef.getConfigManager().getConfigLeveling().getXPBarStyle(primarySkillType));
        bossBar.addPlayer(mcMMOPlayer.getPlayer());
    }
}
