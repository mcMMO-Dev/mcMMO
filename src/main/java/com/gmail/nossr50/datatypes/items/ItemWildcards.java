package com.gmail.nossr50.datatypes.items;

import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a series of items that are all acceptable inputs for a behaviour
 * Wildcards have a unique name that defines them, no two wildcards should share the same name.
 * The name is important as it is used to identify the wildcard to the player.
 *
 * One example of the purpose of this Datatype:
 *
 * As an example, in Repair users are allowed to define a repair cost that can specify wildcards
 * One such example is wood planks, many variants exist in Minecraft and they can all be used to repair Wooden tools
 *
 * ItemWildcards is a flexible datatype and won't be used just for Repair, but at the time of writing it was created
 *  to solve a problem with Repair. Given its flexible nature it can be used for many purposes.
 *
 */
public class ItemWildcards {

    private String wildcardName;
    private HashSet<CustomItemTarget> itemTargets;

    public ItemWildcards(String wildcardName, HashSet<CustomItemTarget> itemTargets) {
        this.wildcardName = wildcardName;
        this.itemTargets = itemTargets;
    }

    public int getItemCount() {
        return itemTargets.size();
    }

    public HashSet<ItemStack> getItemTargets() {
        return itemTargets;
    }

    public void setItemTargets(HashSet<CustomItemTarget> itemTargets) {
        this.itemTargets = itemTargets;
    }

    public String getWildcardName() {
        return wildcardName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemWildcards)) return false;
        ItemWildcards that = (ItemWildcards) o;
        return getWildcardName().equals(that.getWildcardName()) &&
                getItemTargets().equals(that.getItemTargets());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getWildcardName(), getItemTargets());
    }
}
