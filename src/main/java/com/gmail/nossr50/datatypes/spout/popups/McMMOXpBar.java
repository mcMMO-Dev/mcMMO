package com.gmail.nossr50.datatypes.spout.popups;

import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.GenericGradient;
import org.getspout.spoutapi.gui.GenericTexture;
import org.getspout.spoutapi.gui.InGameHUD;
import org.getspout.spoutapi.gui.RenderPriority;
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.spout.SpoutConfig;
import com.gmail.nossr50.datatypes.player.PlayerProfile;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.datatypes.spout.huds.HudType;
import com.gmail.nossr50.util.StringUtils;

public class McMMOXpBar {
    private SpoutPlayer spoutPlayer;
    private Widget xpBar;

    private GenericGradient xpFill;
    private GenericGradient xpBackground;
    private GenericGradient xpIconBackground;
    private GenericGradient xpIconBorder;
    private GenericTexture  xpIcon;

    public McMMOXpBar(SpoutPlayer spoutPlayer, HudType hudType) {
        this.spoutPlayer = spoutPlayer;

        switch (hudType) {
            case RETRO:
                initializeXpBarRetro();
                break;

            case STANDARD:
                initializeXpBarStandard();
                break;

            case SMALL:
                initializeXpBarSmall();
                break;

            case DISABLED:
                break;

            default:
                break;
        }

        spoutPlayer.getMainScreen().setDirty(true);
    }

    public void removeWidgets() {
        InGameHUD inGameHud = spoutPlayer.getMainScreen();

        if (xpBar != null) {
            inGameHud.removeWidget(xpBar);
        }

        if (xpFill != null) {
            inGameHud.removeWidget(xpFill);
        }

        if (xpBackground != null) {
            inGameHud.removeWidget(xpBackground);
        }

        if (xpIconBackground != null) {
            inGameHud.removeWidget(xpIconBackground);
        }

        if (xpIconBorder != null) {
            inGameHud.removeWidget(xpIconBorder);
        }

        if (xpIcon != null) {
            inGameHud.removeWidget(xpIcon);
        }
    }

    /**
     * Initialize Retro XP bar.
     */
    private void initializeXpBarRetro() {
        Color border     = new Color((float) SpoutConfig.getInstance().getRetroHUDXPBorderRed(), (float) SpoutConfig.getInstance().getRetroHUDXPBorderGreen(), (float) SpoutConfig.getInstance().getRetroHUDXPBorderBlue(), 1f);
        Color green      = new Color(0f, 1f, 0f, 1f);
        Color background = new Color((float) SpoutConfig.getInstance().getRetroHUDXPBackgroundRed(), (float) SpoutConfig.getInstance().getRetroHUDXPBackgroundGreen(), (float) SpoutConfig.getInstance().getRetroHUDXPBackgroundBlue(), 1f);

        xpBar        = new GenericGradient();
        xpFill       = new GenericGradient();
        xpBackground = new GenericGradient();

        xpBar.setWidth(128);
        xpBar.setHeight(4);
        xpBar.setX(149);
        xpBar.setY(10);
        ((GenericGradient) xpBar).setBottomColor(border);
        ((GenericGradient) xpBar).setTopColor(border);
        xpBar.setPriority(RenderPriority.Highest);
        spoutPlayer.getMainScreen().attachWidget(mcMMO.p, xpBar);

        xpFill.setWidth(0);
        xpFill.setHeight(2);
        xpFill.setX(150);
        xpFill.setY(11);
        xpFill.setBottomColor(green);
        xpFill.setTopColor(green);
        xpFill.setPriority(RenderPriority.Lowest);
        spoutPlayer.getMainScreen().attachWidget(mcMMO.p, xpFill);

        xpBackground.setWidth(126);
        xpBackground.setHeight(2);
        xpBackground.setX(150);
        xpBackground.setY(11);
        xpBackground.setBottomColor(background);
        xpBackground.setTopColor(background);
        xpBackground.setPriority(RenderPriority.Low);
        spoutPlayer.getMainScreen().attachWidget(mcMMO.p, xpBackground);

        if (SpoutConfig.getInstance().getXPBarIconEnabled()) {
            Color darkbg = new Color(0.2f, 0.2f, 0.2f, 1f);

            xpIconBackground = new GenericGradient();
            xpIconBorder     = new GenericGradient();
            xpIcon           = new GenericTexture();

            xpIconBackground.setBottomColor(darkbg);
            xpIconBackground.setTopColor(darkbg);
            xpIconBackground.setWidth(4);
            xpIconBackground.setHeight(4);
            xpIconBackground.setPriority(RenderPriority.High);
            xpIconBackground.setX(142);
            xpIconBackground.setY(10);
            spoutPlayer.getMainScreen().attachWidget(mcMMO.p, xpIconBackground);

            xpIconBorder.setBottomColor(border);
            xpIconBorder.setTopColor(border);
            xpIconBorder.setWidth(6);
            xpIconBorder.setHeight(6);
            xpIconBorder.setPriority(RenderPriority.Highest);
            xpIconBorder.setX(141);
            xpIconBorder.setY(9);
            spoutPlayer.getMainScreen().attachWidget(mcMMO.p, xpIconBorder);

            xpIcon.setWidth(6);
            xpIcon.setHeight(6);
            xpIcon.setX(141);
            xpIcon.setY(9);
            xpIcon.setPriority(RenderPriority.Normal);
            xpIcon.setUrl("Icon_r.png");
            spoutPlayer.getMainScreen().attachWidget(mcMMO.p, xpIcon);
        }
    }

    /**
     * Initialize Standard XP bar.
     */
    private void initializeXpBarStandard() {
        xpBar = new GenericTexture();

        ((GenericTexture) xpBar).setUrl("xpbar_inc000.png");
        xpBar.setX(SpoutConfig.getInstance().getXPBarXPosition());
        xpBar.setY(SpoutConfig.getInstance().getXPBarYPosition());
        xpBar.setHeight(8);
        xpBar.setWidth(256);
        xpBar.setPriority(RenderPriority.Lowest);
        spoutPlayer.getMainScreen().attachWidget(mcMMO.p, xpBar);

        if (SpoutConfig.getInstance().getXPBarIconEnabled()) {
            xpIcon = new GenericTexture();

            xpIcon.setUrl("Icon.png");
            xpIcon.setHeight(16);
            xpIcon.setWidth(32);
            xpIcon.setX(SpoutConfig.getInstance().getXPIconXPosition());
            xpIcon.setY(SpoutConfig.getInstance().getXPIconYPosition());
            xpIcon.setPriority(RenderPriority.High);
            spoutPlayer.getMainScreen().attachWidget(mcMMO.p, xpIcon);
        }
    }

    /**
     * Initialize Small XP bar.
     */
    private void initializeXpBarSmall() {
        xpBar = new GenericTexture();

        ((GenericTexture) xpBar).setUrl("xpbar_inc000.png");
        xpBar.setX(427 / 2 - 64);
        xpBar.setY(SpoutConfig.getInstance().getXPBarYPosition());
        xpBar.setHeight(4);
        xpBar.setWidth(128);
        xpBar.setPriority(RenderPriority.Lowest);
        spoutPlayer.getMainScreen().attachWidget(mcMMO.p, xpBar);

        if (SpoutConfig.getInstance().getXPBarIconEnabled()) {
            xpIcon = new GenericTexture();

            xpIcon.setUrl("Icon.png");
            xpIcon.setHeight(8);
            xpIcon.setWidth(16);
            xpIcon.setX(427 / 2 - (8 + 64));
            xpIcon.setY(SpoutConfig.getInstance().getXPIconYPosition() + 2);
            xpIcon.setPriority(RenderPriority.High);
            spoutPlayer.getMainScreen().attachWidget(mcMMO.p, xpIcon);
        }
    }

    /**
     * Update the XP bar.
     *
     * @param skillType The skill last used
     * @param playerProfile The profile of the player whose XP bar should be updated
     */
    public void update(SkillType skillType, PlayerProfile playerProfile) {
        switch (playerProfile.getHudType()) {
            case RETRO:
                updateXpBarRetro(skillType, playerProfile);
                break;

            case STANDARD:
            case SMALL:
                updateXpBarStandard(skillType, playerProfile);
                break;

            case DISABLED:
                break;

            default:
                break;
        }
    }

    /**
     * Update XP bar for Standard & Small styles.
     *
     * @param skillType The skill last used
     * @param playerProfile The profile of the player whose XP bar should be updated
     */
    private void updateXpBarStandard(SkillType skillType, PlayerProfile playerProfile) {
        xpIcon.setUrl(StringUtils.getCapitalized(skillType.toString()) + ".png");

        ((GenericTexture) xpBar).setUrl(getUrlBar(getXpInc(playerProfile.getSkillXpLevel(skillType), playerProfile.getXpToLevel(skillType), HudType.STANDARD)));

        spoutPlayer.getMainScreen().setDirty(true);
    }

    /**
     * Update XP bar for Retro styles.
     *
     * @param skillType The skill last used
     * @param playerProfile The profile of the player whose XP bar should be updated
     */
    private void updateXpBarRetro(SkillType skillType, PlayerProfile playerProfile) {
        Color color = getRetroColor(skillType);

        xpIcon.setUrl(StringUtils.getCapitalized(skillType.toString()) + "_r.png");

        xpFill.setBottomColor(color);
        xpFill.setTopColor(color);
        xpFill.setWidth(getXpInc(playerProfile.getSkillXpLevel(skillType), playerProfile.getXpToLevel(skillType), HudType.RETRO));

        spoutPlayer.getMainScreen().setDirty(true);
    }

    private static Color getRetroColor(SkillType type) {
        return new Color((float) SpoutConfig.getInstance().getRetroHUDRed(type), (float) SpoutConfig.getInstance().getRetroHUDGreen(type), (float) SpoutConfig.getInstance().getRetroHUDBlue(type), 1f);
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

    private static Integer getXpInc(int skillXp, int xpToLevel, HudType hudType) {
        double percentage = (double) skillXp / xpToLevel;
        double inc;

        switch (hudType) {
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
}
