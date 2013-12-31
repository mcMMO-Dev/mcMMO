package com.gmail.nossr50.datatypes.spout.buttons;

import org.getspout.spoutapi.gui.GenericButton;

public class McMMOButton extends GenericButton {
    private Slot slot;

    public McMMOButton(String text, String toolTip) {
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
