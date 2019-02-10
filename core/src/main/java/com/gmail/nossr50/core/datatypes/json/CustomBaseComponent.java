package com.gmail.nossr50.core.datatypes.json;

import net.md_5.bungee.api.chat.BaseComponent;

public class CustomBaseComponent extends BaseComponent {
    @Override
    public BaseComponent duplicate() {
        return this;
    }
}
