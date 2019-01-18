package com.gmail.nossr50.util.experience;

import com.gmail.nossr50.config.experience.ExperienceConfig;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.gmail.nossr50.util.StringUtils;
import org.bukkit.boss.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * A visual representation of a players skill level progress for a PrimarySkillType
 */
public class ExperienceBar {

    private final PrimarySkillType primarySkillType; //Primary Skill
    protected String experienceBarTitle; //Name Shown Above XP Bar
    protected BarColor barColor; //Color of the XP Bar
    protected BarStyle barStyle;
    protected Player player;
    protected double progress;
    protected boolean isVisible;

    public ExperienceBar(PrimarySkillType primarySkillType, Player player)
    {
        this.player = player;
        this.primarySkillType = primarySkillType;
        experienceBarTitle = StringUtils.getCapitalized(primarySkillType.getName());
        barColor = ExperienceConfig.getInstance().getExperienceBarColor(primarySkillType);
        barStyle = BarStyle.SOLID;
        isVisible = false;
        progress = 0.0D;
    }

    public String getTitle() {
        return experienceBarTitle;
    }

    public void setTitle(String s) {
        experienceBarTitle = s;
    }

    public BarColor getColor() {
        return barColor;
    }

    public void setColor(BarColor barColor) {
        this.barColor = barColor;
    }


    public BarStyle getStyle() {
        //TODO: Add config for style
        return barStyle;
    }


    public void setStyle(BarStyle barStyle) {
        this.barStyle = barStyle;
    }


    public void removeFlag(BarFlag barFlag) {
        //Do nothing
    }


    public void addFlag(BarFlag barFlag) {
        //Do nothing
    }


    public boolean hasFlag(BarFlag barFlag) {
        return false;
    }


    public void setProgress(double v) {
        //Clamp progress between 0.00 -> 1.00

        if(v < 0)
            progress = 0.0D;
        else if(progress > 1)
            progress = 1.0D;
        else progress = v;
    }


    public double getProgress() {
        return progress;
    }


    public void addPlayer(Player player) {
        //Do nothing
    }


    public void removePlayer(Player player) {
        //Do nothing
    }


    public void removeAll() {
        //Do nothing
    }


    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        players.add(player);
        return players;
    }


    public void setVisible(boolean b) {
        isVisible = b;

        if(isVisible)
            showExperienceBar();
    }


    public boolean isVisible() {
        return isVisible;
    }

    public void showExperienceBar()
    {
        player.getServer().createBossBar()
    }

    /**
     * @deprecated
     */

    public void show() {
        //Do nothing
    }

    /**
     * @deprecated
     */

    public void hide() {
        //Do nothing
    }
}
