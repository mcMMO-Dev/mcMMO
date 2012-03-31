package com.gmail.nossr50.datatypes.buttons;

import com.gmail.nossr50.datatypes.PlayerProfile;

public class ButtonHUDStyle extends ButtonToggle {

    public ButtonHUDStyle(PlayerProfile PP) {
        super("HUD Type: ", PP.getHUDType().toString(), "Change your HUD style!"); //TODO: Needs more locale
    }

    public void updateText(PlayerProfile PP) {
        super.updateText("HUD Type: ", PP.getHUDType().toString()); //TODO: Needs more locale
    }
}
