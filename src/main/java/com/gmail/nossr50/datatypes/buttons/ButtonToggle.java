package com.gmail.nossr50.datatypes.buttons;

import org.getspout.spoutapi.gui.GenericButton;

public class ButtonToggle extends GenericButton{

    public ButtonToggle(String text1, String text2, String tooltip) {
        this.setText(text1 + text2);
        this.setTooltip(tooltip);
        this.setWidth(120);
        this.setHeight(20);
        this.setDirty(true);
    }

    public void updateText(String text1, String text2) {
        this.setText(text1 + text2);
        this.setDirty(true);
    }
}
