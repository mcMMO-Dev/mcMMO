package com.gmail.nossr50.datatypes.items;

import com.gmail.nossr50.mcMMO;

import java.util.HashSet;
import java.util.Objects;

/**
 * This type contains data and rules which govern equating equivalency between one item and another in Minecraft.
 *
 * In mcMMO it is sometimes needed to match one item to another, but items contain a lot of metadata
 * Some of this metadata is important, some of it isn't, and whether something is important or not when
 * considering two items to be similar enough to be considered matching items is not strictly defined and is in fact different from server to server.
 *
 * mcMMO employs a flexible system where users can define which properties of an items metadata (often its NBT)
 * are important for matching and mcMMO will respect those properties when comparing one item to another.
 *
 * If a user does not define a property as being important for matching mcMMO will ignore that property when matching
 * two or more items, even if that property is not equivalent between the items. This type will contain information
 * on which properties are to be considered important for matching purposes.
 *
 * The main goal of this system is accommodate for the vast majority of custom item modifications a server can employ,
 * these custom items are often defined in irregular ways server to server, thus why this type was made.
 *
 * In summary, this type serves several purposes...
 * 1) Abstract away platform specific implementations of MC Items
 * 2) Contain information about an item and which properties of said item that are considered important and thus will be used to equate equivalency to another item when doing comparisons
 */
public class CustomItemTarget implements CustomItemMatching {

    private MMOItem item; //Abstract representation of the item
    private HashSet<ItemMatchProperty> itemMatchProperties; //Item properties used for matching

    public CustomItemTarget(MMOItem item) {
        this.item = item;
        itemMatchProperties = new HashSet<>();
    }

    public CustomItemTarget(MMOItem item, HashSet<ItemMatchProperty> itemMatchProperties) {
        this.item = item;
        this.itemMatchProperties = itemMatchProperties;
    }

    public MMOItem getItem() {
        return item;
    }

    public HashSet<ItemMatchProperty> getItemMatchProperties() {
        return itemMatchProperties;
    }

    /**
     * Determines whether or not an item matches this one
     * Behaviours for matching can vary based on the implementation
     * @param otherItem target item to compare itself to
     * @return true if this item matches the target item
     */
    @Override
    public boolean isMatch(MMOItem otherItem) {
        //First compare the basic things that need to match between each item
        if(item.equals(otherItem)) {
            for(ItemMatchProperty itemMatchProperty : itemMatchProperties) {
                if(!mcMMO.getNbtManager().hasNBT(otherItem.getRawNBT().getNbtData(), itemMatchProperty.getNbtData()))
                    return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomItemTarget)) return false;
        CustomItemTarget that = (CustomItemTarget) o;
        return getItem().equals(that.getItem()) &&
                getItemMatchProperties().equals(that.getItemMatchProperties());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getItem(), getItemMatchProperties());
    }
}
