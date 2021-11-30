package me.viiral.primeitemfilter;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum ItemCategory {
    GEAR("Equipment", Material.DIAMOND_CHESTPLATE),
    POTION("Potion Supplies", Material.POTION),
    REDSTONE("Raiding", Material.REDSTONE),
    FOOD("Food", Material.BREAD),
    VALUABLES("Specialty", Material.MOB_SPAWNER),
    ORES("Ores", Material.DIAMOND_ORE),
    OTHERS("Other Blocks", Material.SAND);

    String itemName;
    Material iconMaterial;

    public static ItemCategory getFromItemName(String name) {
        name = ChatColor.stripColor((String)name);
        for (ItemCategory cat : ItemCategory.values()) {
            if (!cat.getItemName().equals(name)) continue;
            return cat;
        }
        return null;
    }

    private ItemCategory(String itemName, Material iconMaterial) {
        this.itemName = itemName;
        this.iconMaterial = iconMaterial;
    }

    public String getItemName() {
        return this.itemName;
    }

    public Material getIconMaterial() {
        return this.iconMaterial;
    }
}
