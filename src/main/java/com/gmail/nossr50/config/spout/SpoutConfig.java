package com.gmail.nossr50.config.spout;

import org.getspout.spoutapi.keyboard.Keyboard;

import com.gmail.nossr50.config.ConfigLoader;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.datatypes.spout.huds.HudType;

public class SpoutConfig extends ConfigLoader {
    private static SpoutConfig instance;

    private SpoutConfig() {
        super("spout.yml");
    }

    public static SpoutConfig getInstance() {
        if (instance == null) {
            instance = new SpoutConfig();
        }

        return instance;
    }

    @Override
    protected void loadKeys() {
        // Setup default HUD
    }

    public HudType getDefaultHudType() {
        try {
            return HudType.valueOf(config.getString("Spout.HUD.Default", "STANDARD").toUpperCase().trim());
        }
        catch (IllegalArgumentException ex) {
            return HudType.STANDARD;
        }
    }

    public boolean getShowPowerLevel() { return config.getBoolean("HUD.Show_Power_Level", true); }
    public Keyboard getMenuKey() { 
        try {
            return Keyboard.valueOf(config.getString("Menu.Key", "KEY_M").toUpperCase().trim());
        }
        catch (IllegalArgumentException ex) {
            return Keyboard.KEY_M;
        }
    }

    /* XP Bar */
    public boolean getXPBarEnabled() { return config.getBoolean("XP.Bar.Enabled", true); }
    public void setXPBarEnabled(boolean enabled) { config.set("XP.Bar.Enabled", enabled); }

    public boolean getXPBarIconEnabled() { return config.getBoolean("XP.Icon.Enabled", true); }
    public int getXPBarXPosition() { return config.getInt("XP.Bar.X_POS", 95); }
    public int getXPBarYPosition() { return config.getInt("XP.Bar.Y_POS", 6); }
    public int getXPIconXPosition() { return config.getInt("XP.Icon.X_POS", 78); }
    public int getXPIconYPosition() { return config.getInt("XP.Icon.Y_POS", 2); }

    /* HUD Colors */
    public double getRetroHUDXPBorderRed() { return config.getDouble("HUD.Retro.Colors.Border.RED", 0.0); }
    public double getRetroHUDXPBorderGreen() { return config.getDouble("HUD.Retro.Colors.Border.GREEN", 0.0); }
    public double getRetroHUDXPBorderBlue() { return config.getDouble("HUD.Retro.Colors.Border.BLUE", 0.0); }
    public double getRetroHUDXPBackgroundRed() { return config.getDouble("HUD.Retro.Colors.Background.RED", 0.75); }
    public double getRetroHUDXPBackgroundGreen() { return config.getDouble("HUD.Retro.Colors.Background.GREEN", 0.75); }
    public double getRetroHUDXPBackgroundBlue() { return config.getDouble("HUD.Retro.Colors.Background.BLUE", 0.75); }

    public double getRetroHUDRed(SkillType skill) { return config.getDouble("HUD.Retro.Colors." + skill.toString().toLowerCase() +".RED", 0.3); }
    public double getRetroHUDGreen(SkillType skill) { return config.getDouble("HUD.Retro.Colors." + skill.toString().toLowerCase() +".RED", 0.3); }
    public double getRetroHUDBlue(SkillType skill) { return config.getDouble("HUD.Retro.Colors." + skill.toString().toLowerCase() +".RED", 0.3); }

    /* Notification Tiers */
    public int getNotificationTier1() { return config.getInt("Notifications.Tier1", 200); }
    public int getNotificationTier2() { return config.getInt("Notifications.Tier2", 400); }
    public int getNotificationTier3() { return config.getInt("Notifications.Tier3", 600); }
    public int getNotificationTier4() { return config.getInt("Notifications.Tier4", 800); }
}
