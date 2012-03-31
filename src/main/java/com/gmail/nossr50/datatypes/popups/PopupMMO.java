package com.gmail.nossr50.datatypes.popups;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericPopup;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.config.LoadProperties;
import com.gmail.nossr50.datatypes.PlayerProfile;
import com.gmail.nossr50.datatypes.buttons.ButtonEscape;
import com.gmail.nossr50.datatypes.buttons.ButtonHUDStyle;
import com.gmail.nossr50.datatypes.buttons.ButtonPartyToggle;

public class PopupMMO extends GenericPopup {

    private ButtonHUDStyle HUDButton = null;
    private ButtonPartyToggle PartyButton = null;
    private ButtonEscape EscapeButton = null;
    private GenericLabel mcMMO_label = new GenericLabel();
    private GenericLabel tip_escape = new GenericLabel();
    private int center_x = 427 / 2;
    private int center_y = 240 / 2;

    public PopupMMO(Player player, PlayerProfile PP, mcMMO plugin) {

        //240, 427 are the bottom right
        mcMMO_label.setText(ChatColor.GOLD + "~mcMMO Menu~"); //TODO: Needs more locale
        mcMMO_label.setX(center_x - 35);
        mcMMO_label.setY((center_y / 2) - 20);
        mcMMO_label.setDirty(true);

        tip_escape.setText(ChatColor.GRAY + "Press ESCAPE to exit!"); //TODO: Needs more locale
        tip_escape.setX(mcMMO_label.getX() - 15);
        tip_escape.setY(mcMMO_label.getY() + 10);
        tip_escape.setDirty(true);

        HUDButton = new ButtonHUDStyle(PP);
        HUDButton.setX(center_x - (HUDButton.getWidth() / 2));
        HUDButton.setY(center_y / 2);
        HUDButton.setDirty(true);

        if (LoadProperties.partybar) {
            PartyButton = new ButtonPartyToggle(PP);
            PartyButton.setX(center_x - (PartyButton.getWidth() / 2));
            PartyButton.setY((center_y / 2) + PartyButton.getHeight());
            PartyButton.setDirty(true);
            this.attachWidget(plugin, PartyButton);
        }

        EscapeButton = new ButtonEscape();
        EscapeButton.setX(center_x - (EscapeButton.getWidth() / 2));
        EscapeButton.setY((center_y / 2) + (HUDButton.getHeight() * 2) + 5);
        EscapeButton.setDirty(true);

        this.attachWidget(plugin, HUDButton);
        this.attachWidget(plugin, mcMMO_label);
        this.attachWidget(plugin, tip_escape);
        this.attachWidget(plugin, EscapeButton);

        this.setDirty(true);
    }

    public void updateButtons(PlayerProfile PP) {
        HUDButton.updateText(PP);
        PartyButton.updateText(PP);
        this.setDirty(true);
    }
}
