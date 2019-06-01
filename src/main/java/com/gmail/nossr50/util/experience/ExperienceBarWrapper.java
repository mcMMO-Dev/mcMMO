package com.gmail.nossr50.util.experience;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.StringUtils;
import com.gmail.nossr50.util.player.PlayerLevelUtils;
import org.bukkit.Server;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * A visual representation of a players skill level progress for a PrimarySkillType
 */
public class ExperienceBarWrapper {

    protected final McMMOPlayer mcMMOPlayer;
    private final PrimarySkillType primarySkillType; //Primary Skill
    private BossBar bossBar;
    private int lastLevelUpdated;

    /*
     * This is stored to help optimize updating the title
     */
    protected String niceSkillName;
    protected String title;

    public ExperienceBarWrapper(PrimarySkillType primarySkillType, McMMOPlayer mcMMOPlayer) {
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
        if(mcMMO.getConfigManager().getConfigLeveling().getEarlyGameBoost().isEnableEarlyGameBoost() && PlayerLevelUtils.qualifiesForEarlyGameBoost(mcMMOPlayer, primarySkillType)) {
                return LocaleLoader.getString("XPBar.Template.EarlyGameBoost");
        } else if(mcMMO.getConfigManager().getConfigLeveling().getConfigExperienceBars().isMoreDetailedXPBars())
            return LocaleLoader.getString("XPBar.Complex.Template", LocaleLoader.getString("XPBar."+niceSkillName, getLevel()), getCurrentXP(), getMaxXP(), getPowerLevel(), getPercentageOfLevel());

        return LocaleLoader.getString("XPBar." + niceSkillName, getLevel(), getCurrentXP(), getMaxXP(), getPowerLevel(), getPercentageOfLevel());
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
        if(mcMMO.getConfigManager().getConfigLeveling().getEarlyGameBoost().isEnableEarlyGameBoost() && PlayerLevelUtils.qualifiesForEarlyGameBoost(mcMMOPlayer, primarySkillType)) {
           setColor(BarColor.YELLOW);
        } else {
            setColor(mcMMO.getConfigManager().getConfigLeveling().getConfigExperienceBars().getXPBarColor(primarySkillType));
        }

        //Every time progress updates we need to check for a title update
        if (getLevel() != lastLevelUpdated || mcMMO.getConfigManager().getConfigLeveling().isMoreDetailedXPBars()) {
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
                mcMMO.getConfigManager().getConfigLeveling().getXPBarColor(primarySkillType),
                mcMMO.getConfigManager().getConfigLeveling().getXPBarStyle(primarySkillType));
        bossBar.addPlayer(mcMMOPlayer.getPlayer());
    }
}
