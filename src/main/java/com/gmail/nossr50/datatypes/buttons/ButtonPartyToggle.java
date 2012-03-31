package com.gmail.nossr50.datatypes.buttons;

import com.gmail.nossr50.datatypes.PlayerProfile;

public class ButtonPartyToggle extends ButtonToggle {

    public ButtonPartyToggle(PlayerProfile PP) {
        super("Party HUD: ", ((Boolean) PP.getPartyHUD()).toString(), "Toggle the Party HUD!"); //TODO: Needs more locale
    }

    public void updateText(PlayerProfile PP) {
        super.updateText("Party HUD: ", ((Boolean) PP.getPartyHUD()).toString()); //TODO: Needs more locale
    }
}
