package com.gmail.nossr50.datatypes.buttons;

import org.getspout.spoutapi.gui.GenericButton;

public class McmmoButton extends GenericButton {
    private Slot slot;

    public McmmoButton(String text, String toolTip) {
        this.setText(text);
        this.setTooltip(toolTip);
    }
    
    public void connect(Slot slot) {
        this.slot = slot;
    }

    public void activate() {
        slot.activate();
    }

    public interface Slot {
        public void activate();
    }
}
