package com.gmail.nossr50.skills.salvage.salvageables;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;


public class SimpleSalvageableManager implements SalvageableManager {
    private HashMap<Material, Salvageable> salvageables;

    public SimpleSalvageableManager() {
        this(55);
    }

    public SimpleSalvageableManager(int salvageablesSize) {
        this.salvageables = new HashMap<Material, Salvageable>(salvageablesSize);
    }

    @Override
    public void registerSalvageable(Salvageable salvageable) {
        Material item = salvageable.getItemMaterial();
        salvageables.put(item, salvageable);
    }

    @Override
    public void registerSalvageables(List<Salvageable> salvageables) {
        for (Salvageable salvageable : salvageables) {
            registerSalvageable(salvageable);
        }
    }

    @Override
    public boolean isSalvageable(Material type) {
        return salvageables.containsKey(type);
    }

    @Override
    public boolean isSalvageable(ItemStack itemStack) {
        return isSalvageable(itemStack.getType());
    }

    @Override
    public Salvageable getSalvageable(Material type) {
        return salvageables.get(type);
    }
}
