package com.gmail.nossr50.config.hocon.skills.repair;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class RepairWildcard {

    private String wildcardName;
    private Set<ItemStack> matchingItems;

    public RepairWildcard(String wildcardName, Set<ItemStack> matchingItems) {
        this.wildcardName = wildcardName;
        this.matchingItems = matchingItems;
    }

    public Set<ItemStack> getMatchingItems() {
        return matchingItems;
    }

    public void setMatchingItems(Set<ItemStack> matchingItems) {
        this.matchingItems = matchingItems;
    }

    public String getWildcardName() {
        return wildcardName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RepairWildcard)) return false;
        RepairWildcard that = (RepairWildcard) o;
        return getWildcardName().equals(that.getWildcardName()) &&
                getMatchingItems().equals(that.getMatchingItems());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getWildcardName(), getMatchingItems());
    }
}
