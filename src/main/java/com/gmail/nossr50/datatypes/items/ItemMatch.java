package com.gmail.nossr50.datatypes.items;

import com.gmail.nossr50.mcMMO;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
public class ItemMatch<T extends MMOItem<?>> implements DefinedMatch<MMOItem<T>> {

    private T item; //Abstract representation of the item
    private Set<ItemMatchProperty> itemMatchProperties; //Item properties used for matching

    public ItemMatch(T item) {
        this.item = item;
        itemMatchProperties = new HashSet<>();
    }

    public ItemMatch(T item, Set<ItemMatchProperty> itemMatchProperties) {
        this.item = item;
        this.itemMatchProperties = itemMatchProperties;
    }

    /**
     * Gets the item held by this ItemMatch
     * @return the item used for matching
     */
    public T getItem() {
        return item;
    }

    /**
     * Get the item match properties of this ItemMatch
     * @return the item match properties
     */
    public Set<ItemMatchProperty> getItemMatchProperties() {
        return itemMatchProperties;
    }

    /**
     * Determines whether or not an item matches this one
     * Behaviours for matching can vary based on the implementation
     * @param otherItem target item to compare itself to
     * @return true if this item matches the target item
     */
    @Override
    public boolean isMatch(MMOItem<T> otherItem) {
        if(hasStrictMatching()) {
            return isStrictMatch(otherItem);
        } else {
            return isUnstrictMatch(otherItem);
        }
    }

    /**
     * Compare this item to another while comparing specific nbt tags for matching values, if all values are found and match it is considered a strict match
     * @param otherItem item to strictly match
     * @return true if the items are considered a match
     */
    private boolean isStrictMatch(MMOItem<T> otherItem) {
        for(ItemMatchProperty itemMatchProperty : itemMatchProperties) {
            if(!mcMMO.getNbtManager().hasNBT(otherItem.getRawNBT().getNbtData(), itemMatchProperty.getNbtData())) {
                return false;
            }
        }

        //All item match properties were found and matched
        return true;
    }

    /**
     * Compare this item to another only by namespace key
     * @param otherItem item to compare namespace keys with
     * @return true if the items share namespace keys
     */
    private boolean isUnstrictMatch(MMOItem<T> otherItem) {
        if(otherItem.getNamespaceKey().equalsIgnoreCase(item.getNamespaceKey())) {
            return true;
        }

        //Namespace didn't match reject item
        return false;
    }

    /**
     * If this item has strict matching or loose match
     * Solely determined by having any item match properties present
     * @return true if this item target has strict matching
     */
    public boolean hasStrictMatching() {
        return itemMatchProperties.size() > 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemMatch)) return false;
        ItemMatch<?> itemMatch = (ItemMatch<?>) o;
        return getItem().equals(itemMatch.getItem()) &&
                getItemMatchProperties().equals(itemMatch.getItemMatchProperties());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getItem(), getItemMatchProperties());
    }
}
