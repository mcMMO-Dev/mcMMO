package com.gmail.nossr50.datatypes;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.GenericGradient;
import org.getspout.spoutapi.gui.GenericTexture;
import org.getspout.spoutapi.gui.RenderPriority;
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.gmail.nossr50.Users;
import com.gmail.nossr50.m;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.spout.SpoutStuff;

public class HUDmmo {
    private int center_x = 427 / 2;

    private String playerName = null;

    private Widget xpbar = null;
    private GenericGradient xpfill = null;
    private GenericGradient xpbg = null;
    private GenericGradient xpicon_bg = null;
    private GenericGradient xpicon_border = null;
    private GenericTexture xpicon = null;

    private mcMMO plugin = (mcMMO) Bukkit.getServer().getPluginManager().getPlugin("mcMMO");

    public HUDmmo(Player player) {
        playerName = player.getName();
        initializeHUD(player);
    }

    /**
     * Initialize the HUD.
     *
     * @param player Player whose HUD to initialize
     */
    public void initializeHUD(Player player) {
        HUDType type = Users.getProfile(player).getHUDType();
        SpoutPlayer sPlayer = SpoutManager.getPlayer(player);

        switch (type) {
            case RETRO:
                initializeXpBarDisplayRetro(sPlayer);
                break;

            case STANDARD:
                initializeXpBarDisplayStandard(sPlayer);
                break;

            case SMALL:
                initializeXpBarDisplaySmall(sPlayer);
                break;

            case DISABLED:
                break;

            default:
                break;
        }
    }

    /**
     * Update the XP bar.
     *
     * @param type Type of XP bar
     * @param player Player whose XP bar to update
     */
    public void updateXpBarDisplay(HUDType type, Player player) {
        PlayerProfile PP = Users.getProfile(player);

        switch (type) {
        case RETRO:
            updateXpBarRetro(player, PP);
            break;

        case STANDARD:
            updateXpBarStandard(player, PP);
            break;

        case SMALL:
            updateXpBarStandard(player, PP);
            break;

        case DISABLED:
            break;

        default:
            break;
        }
    }

    /**
     * Reset a player's HUD.
     */
    public void resetHUD() {
        SpoutPlayer sPlayer = SpoutStuff.getSpoutPlayer(playerName);

        if (sPlayer != null) {
            sPlayer.getMainScreen().removeWidgets(plugin);

            //Reset the objects
            xpbar = null;
            xpfill = null;
            xpbg = null;
            xpicon = null;

            sPlayer.getMainScreen().setDirty(true);
        }
    }

    /**
     * Initialize Retro XP bar.
     *
     * @param sPlayer Player to initialize XP bar for
     */
    private void initializeXpBarDisplayRetro(SpoutPlayer sPlayer) {
        Color border = new Color((float) LoadProperties.xpborder_r, (float) LoadProperties.xpborder_g, (float) LoadProperties.xpborder_b, 1f);
        Color green = new Color(0f, 1f, 0f, 1f);
        Color background = new Color((float) LoadProperties.xpbackground_r, (float) LoadProperties.xpbackground_g, (float) LoadProperties.xpbackground_b, 1f);
        Color darkbg = new Color(0.2f, 0.2f, 0.2f, 1f);

        xpicon = new GenericTexture();
        xpbar = new GenericGradient();
        xpfill = new GenericGradient();
        xpbg = new GenericGradient();

        xpicon_bg = new GenericGradient();
        xpicon_border = new GenericGradient();

        xpicon_bg.setBottomColor(darkbg);
        xpicon_bg.setTopColor(darkbg);
        xpicon_bg.setWidth(4);
        xpicon_bg.setHeight(4);
        xpicon_bg.setPriority(RenderPriority.High);
        xpicon_bg.setX(142);
        xpicon_bg.setY(10);
        xpicon_bg.setDirty(true);

        xpicon_border.setBottomColor(border);
        xpicon_border.setTopColor(border);
        xpicon_border.setWidth(6);
        xpicon_border.setHeight(6);
        xpicon_border.setPriority(RenderPriority.Highest);
        xpicon_border.setX(141);
        xpicon_border.setY(9);
        xpicon_border.setDirty(true);

        xpicon.setWidth(6);
        xpicon.setHeight(6);
        xpicon.setX(141);
        xpicon.setY(9);
        xpicon.setPriority(RenderPriority.Normal);
        xpicon.setDirty(true);
        xpicon.setUrl("Icon_r.png");

        xpbar.setWidth(128);
        xpbar.setHeight(4);
        xpbar.setX(149);
        xpbar.setY(10);
        ((GenericGradient) xpbar).setBottomColor(border);
        ((GenericGradient) xpbar).setTopColor(border);
        xpbar.setPriority(RenderPriority.Highest);
        xpbar.setDirty(true);

        xpfill.setWidth(0);
        xpfill.setHeight(2);
        xpfill.setX(150);
        xpfill.setY(11);
        xpfill.setBottomColor(green);
        xpfill.setTopColor(green);
        xpfill.setPriority(RenderPriority.Lowest);
        xpfill.setDirty(true);

        xpbg.setWidth(126);
        xpbg.setHeight(2);
        xpbg.setX(150);
        xpbg.setY(11);
        xpbg.setBottomColor(background);
        xpbg.setTopColor(background);
        xpbg.setPriority(RenderPriority.Low);
        xpbg.setDirty(true);

        if (LoadProperties.xpbar) {
            sPlayer.getMainScreen().attachWidget(plugin, xpbar);
            sPlayer.getMainScreen().attachWidget(plugin, xpfill);
            sPlayer.getMainScreen().attachWidget(plugin, xpbg);

            if (LoadProperties.xpicon) {
                sPlayer.getMainScreen().attachWidget(plugin, xpicon);
                sPlayer.getMainScreen().attachWidget(plugin, xpicon_bg);
                sPlayer.getMainScreen().attachWidget(plugin, xpicon_border);
            }
        }

        sPlayer.getMainScreen().setDirty(true);
    }

    /**
     * Initialize Standard XP bar.
     *
     * @param sPlayer Player to initialize XP bar for
     */
    public void initializeXpBarDisplayStandard(SpoutPlayer sPlayer) {
        if (LoadProperties.xpbar) {
            xpbar = new GenericTexture();

            ((GenericTexture) xpbar).setUrl("xpbar_inc000.png");

            xpbar.setX(LoadProperties.xpbar_x);
            xpbar.setY(LoadProperties.xpbar_y);
            xpbar.setHeight(8);
            xpbar.setWidth(256);

            sPlayer.getMainScreen().attachWidget(plugin, xpbar);

            if (LoadProperties.xpicon) {
                xpicon = new GenericTexture();

                xpicon.setUrl("Icon.png");
                xpicon.setHeight(16);
                xpicon.setWidth(32);
                xpicon.setX(LoadProperties.xpicon_x);
                xpicon.setY(LoadProperties.xpicon_y);
                xpicon.setDirty(true);

                sPlayer.getMainScreen().attachWidget(plugin, xpicon);
            }
        }

        sPlayer.getMainScreen().setDirty(true);
    }

    /**
     * Initialize Small XP bar.
     *
     * @param sPlayer Player to initialize XP bar for
     */
    private void initializeXpBarDisplaySmall(SpoutPlayer sPlayer) {
        if (LoadProperties.xpbar) {
            xpbar = new GenericTexture();

            ((GenericTexture)xpbar).setUrl("xpbar_inc000.png");
            xpbar.setX(center_x - 64);
            xpbar.setY(LoadProperties.xpbar_y);
            xpbar.setHeight(4);
            xpbar.setWidth(128);

            sPlayer.getMainScreen().attachWidget(plugin, xpbar);

            if (LoadProperties.xpicon) {
                xpicon = new GenericTexture();

                xpicon.setUrl("Icon.png");
                xpicon.setHeight(8);
                xpicon.setWidth(16);
                xpicon.setX(center_x - (8 + 64));
                xpicon.setY(LoadProperties.xpicon_y + 2);
                xpicon.setDirty(true);

                sPlayer.getMainScreen().attachWidget(plugin, xpicon);
            }
        }

        sPlayer.getMainScreen().setDirty(true);
    }

    /**
     * Update XP bar for Standard & Small styles.
     *
     * @param player Player whose XP bar to update
     * @param PP Profile of the given player
     */
    private void updateXpBarStandard(Player player, PlayerProfile PP) {
        if (!LoadProperties.xpbar) {
            return;
        }

        SkillType theType = getType(PP);

        if (theType == null) { //Can this ever actually BE null? (Yes, it's null when the player has just logged in. It's not null when they gain XP in anything)
            return;
        }

        ((GenericTexture) xpicon).setUrl(m.getCapitalized(theType.toString()) + ".png");
        xpicon.setDirty(true);

        ((GenericTexture) xpbar).setUrl(getUrlBar(getXpInc(PP.getSkillXpLevel(theType), PP.getXpToLevel(theType), HUDType.STANDARD)));
        xpbar.setDirty(true);

        SpoutManager.getPlayer(player).getMainScreen().setDirty(true);
    }

    /**
     * Update XP bar for Retro styles.
     *
     * @param player Player whose XP bar to update
     * @param PP Profile of the given player
     */
    private void updateXpBarRetro(Player player, PlayerProfile PP) {
        if (!LoadProperties.xpbar) {
            return;
        }

        SkillType theType = getType(PP);

        if (theType == null) { //Can this ever actually BE null? (Yes, it's null when the player has just logged in. It's not null when they gain XP in anything)
            return;
        }

        Color color = getRetroColor(theType);

        xpicon.setUrl(m.getCapitalized(theType.toString()) + "_r.png");

        xpfill.setBottomColor(color);
        xpfill.setTopColor(color);
        xpfill.setWidth(getXpInc(PP.getSkillXpLevel(theType), PP.getXpToLevel(theType), HUDType.RETRO));
        xpfill.setDirty(true);

        SpoutManager.getPlayer(player).getMainScreen().setDirty(true);
    }

    private static Color getRetroColor(SkillType type) {
        switch (type) {
            case ACROBATICS:
                return new Color((float) LoadProperties.acrobatics_r, (float) LoadProperties.acrobatics_g, (float) LoadProperties.acrobatics_b, 1f);

            case ARCHERY:
                return new Color((float) LoadProperties.archery_r, (float) LoadProperties.archery_g, (float) LoadProperties.archery_b, 1f);

            case AXES:
                return new Color((float) LoadProperties.axes_r, (float) LoadProperties.axes_g, (float) LoadProperties.axes_b, 1f);

            case EXCAVATION:
                return new Color((float) LoadProperties.excavation_r, (float) LoadProperties.excavation_g, (float) LoadProperties.excavation_b, 1f);

            case HERBALISM:
                return new Color((float) LoadProperties.herbalism_r, (float) LoadProperties.herbalism_g, (float) LoadProperties.herbalism_b, 1f);

            case MINING:
                return new Color((float) LoadProperties.mining_r, (float) LoadProperties.mining_g, (float) LoadProperties.mining_b, 1f);

            case REPAIR:
                return new Color((float) LoadProperties.repair_r, (float) LoadProperties.repair_g, (float) LoadProperties.repair_b, 1f);

            case SWORDS:
                return new Color((float) LoadProperties.swords_r, (float) LoadProperties.swords_g, (float) LoadProperties.swords_b, 1f);

            case TAMING:
                return new Color((float) LoadProperties.taming_r, (float) LoadProperties.taming_g, (float) LoadProperties.taming_b, 1f);

            case UNARMED:
                return new Color((float) LoadProperties.unarmed_r, (float) LoadProperties.unarmed_g, (float) LoadProperties.unarmed_b, 1f);

            case WOODCUTTING:
                return new Color((float) LoadProperties.woodcutting_r, (float) LoadProperties.woodcutting_g, (float) LoadProperties.woodcutting_b, 1f);

            case FISHING:
                return new Color((float) LoadProperties.fishing_r, (float) LoadProperties.fishing_g, (float) LoadProperties.fishing_b, 1f);

            default:
                return new Color(0.3f, 0.3f, 0.75f, 1f);
        }
    }

    private static String getUrlBar(Integer number) {
        char[] num = number.toString().toCharArray();

        switch (num.length) {
        case 1:
            return "xpbar_inc00" + number + ".png";

        case 2:
            return "xpbar_inc0" + number + ".png";

        default:
            return "xpbar_inc" + number + ".png";
        }
    }

    private static Integer getXpInc(int skillxp, int xptolevel, HUDType hud) {
        double percentage = (double) skillxp / xptolevel;
        double inc;

        switch (hud) {
        case RETRO:
            inc = 0.0079365079365079;
            break;

        case STANDARD:
            inc = 0.0039370078740157;
            break;

        default:
            return 1;
        }

        return (int) (percentage / inc);
    }

    private static SkillType getType(PlayerProfile PP) {
        if (PP.getXpBarLocked()) {
            return PP.getSkillLock();
        }
        else {
            return PP.getLastGained();
        }
    }
}
