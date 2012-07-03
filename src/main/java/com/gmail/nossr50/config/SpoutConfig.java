package com.gmail.nossr50.config;

import com.gmail.nossr50.datatypes.HudType;

public class SpoutConfig extends ConfigLoader {
    private static SpoutConfig instance;
    public HudType defaultHudType;

    private SpoutConfig() {
        super("spout.yml");
        loadKeys();
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
        String temp = config.getString("Spout.HUD.Default", "STANDARD");

        for (HudType hudType : HudType.values()) {
            if (hudType.toString().equalsIgnoreCase(temp.toString())) {
                defaultHudType = hudType;
                break;
            }
        }

        if (defaultHudType == null) {
            defaultHudType = HudType.STANDARD;
        }
    }

    public boolean getShowPowerLevel() { return config.getBoolean("HUD.Show_Power_Level", true); }
    public String getMenuKey() { return config.getString("Menu.Key", "KEY_M"); }

    /* XP Bar */
    public boolean getXPBarEnabled() { return config.getBoolean("XP.Bar.Enabled", true); }
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

    public double getRetroHUDAcrobaticsRed() { return config.getDouble("HUD.Retro.Colors.Acrobatics.RED", 0.3); }
    public double getRetroHUDAcrobaticsGreen() { return config.getDouble("HUD.Retro.Colors.Acrobatics.GREEN", 0.3); }
    public double getRetroHUDAcrobaticsBlue() { return config.getDouble("HUD.Retro.Colors.Acrobatics.BLUE", 0.75); }
    public double getRetroHUDArcheryRed() { return config.getDouble("HUD.Retro.Colors.Archery.RED", 0.3); }
    public double getRetroHUDArcheryGreen() { return config.getDouble("HUD.Retro.Colors.Archery.GREEN", 0.3); }
    public double getRetroHUDArcheryBlue() { return config.getDouble("HUD.Retro.Colors.Archery.BLUE", 0.75); }
    public double getRetroHUDAxesRed() { return config.getDouble("HUD.Retro.Colors.Axes.RED", 0.3); }
    public double getRetroHUDAxesGreen() { return config.getDouble("HUD.Retro.Colors.Axes.GREEN", 0.3); }
    public double getRetroHUDAxesBlue() { return config.getDouble("HUD.Retro.Colors.Axes.BLUE", 0.75); }
    public double getRetroHUDExcavationRed() { return config.getDouble("HUD.Retro.Colors.Excavation.RED", 0.3); }
    public double getRetroHUDExcavationGreen() { return config.getDouble("HUD.Retro.Colors.Excavation.GREEN", 0.3); }
    public double getRetroHUDExcavationBlue() { return config.getDouble("HUD.Retro.Colors.Excavation.BLUE", 0.75); }
    public double getRetroHUDHerbalismRed() { return config.getDouble("HUD.Retro.Colors.Herbalism.RED", 0.3); }
    public double getRetroHUDHerbalismGreen() { return config.getDouble("HUD.Retro.Colors.Herbalism.GREEN", 0.3); }
    public double getRetroHUDHerbalismBlue() { return config.getDouble("HUD.Retro.Colors.Herbalism.BLUE", 0.75); }
    public double getRetroHUDMiningRed() { return config.getDouble("HUD.Retro.Colors.Mining.RED", 0.3); }
    public double getRetroHUDMiningGreen() { return config.getDouble("HUD.Retro.Colors.Mining.GREEN", 0.3); }
    public double getRetroHUDMiningBlue() { return config.getDouble("HUD.Retro.Colors.Mining.BLUE", 0.75); }
    public double getRetroHUDRepairRed() { return config.getDouble("HUD.Retro.Colors.Repair.RED", 0.3); }
    public double getRetroHUDRepairGreen() { return config.getDouble("HUD.Retro.Colors.Repair.GREEN", 0.3); }
    public double getRetroHUDRepairBlue() { return config.getDouble("HUD.Retro.Colors.Repair.BLUE", 0.75); }
    public double getRetroHUDSwordsRed() { return config.getDouble("HUD.Retro.Colors.Swords.RED", 0.3); }
    public double getRetroHUDSwordsGreen() { return config.getDouble("HUD.Retro.Colors.Swords.GREEN", 0.3); }
    public double getRetroHUDSwordsBlue() { return config.getDouble("HUD.Retro.Colors.Swords.BLUE", 0.75); }
    public double getRetroHUDTamingRed() { return config.getDouble("HUD.Retro.Colors.Taming.RED", 0.3); }
    public double getRetroHUDTamingGreen() { return config.getDouble("HUD.Retro.Colors.Taming.GREEN", 0.3); }
    public double getRetroHUDTamingBlue() { return config.getDouble("HUD.Retro.Colors.Taming.BLUE", 0.75); }
    public double getRetroHUDUnarmedRed() { return config.getDouble("HUD.Retro.Colors.Unarmed.RED", 0.3); }
    public double getRetroHUDUnarmedGreen() { return config.getDouble("HUD.Retro.Colors.Unarmed.GREEN", 0.3); }
    public double getRetroHUDUnarmedBlue() { return config.getDouble("HUD.Retro.Colors.Unarmed.BLUE", 0.75); }
    public double getRetroHUDWoodcuttingRed() { return config.getDouble("HUD.Retro.Colors.Woodcutting.RED", 0.3); }
    public double getRetroHUDWoodcuttingGreen() { return config.getDouble("HUD.Retro.Colors.Woodcutting.GREEN", 0.3); }
    public double getRetroHUDWoodcuttingBlue() { return config.getDouble("HUD.Retro.Colors.Woodcutting.BLUE", 0.75); }
    public double getRetroHUDFishingRed() { return config.getDouble("HUD.Retro.Colors.Fishing.RED", 0.3); }
    public double getRetroHUDFishingGreen() { return config.getDouble("HUD.Retro.Colors.Fishing.GREEN", 0.3); }
    public double getRetroHUDFishingBlue() { return config.getDouble("HUD.Retro.Colors.Fishing.BLUE", 0.75); }
}
