package me.viiral.primeitemfilter;

import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Filter {
    boolean enabled;
    HashMap<Integer, Set<Short>> filteredItems = new HashMap();

    public boolean canPickupItem(ItemStack item, Material knownMaterial) {
        Set<Short> dataAllowed = this.filteredItems.get(knownMaterial != null ? knownMaterial.getId() : item.getTypeId());
        Material type = item.getType();
        String name = type.name();
        if (dataAllowed != null) {
            if (name.endsWith("_HELMET") || name.endsWith("_CHESTPLATE") || name.endsWith("_LEGGINGS") || name.endsWith("_BOOTS") || name.endsWith("_SWORD") || name.endsWith("_AXE") || name.endsWith("BOW") || name.endsWith("_HOE")) {
                return true;
            }
            if (type == Material.MOB_SPAWNER) {
                return true;
            }
            if (type == Material.POTION) {
                return true;
            }
            if (type == Material.MONSTER_EGG) {
                return true;
            }
            return dataAllowed.contains(item.getDurability());
        }
        if ((name.startsWith("RECORD_") || type == Material.GOLD_RECORD) && this.filteredItems.containsKey(Material.GREEN_RECORD.getId())) {
            return true;
        }
        ItemCategory categoryBelondsToo = PrimeItemFilter.get().getFilterManager().getCategoryFromItem(item, type);
        return categoryBelondsToo == null && this.filteredItems.containsKey(Material.DRAGON_EGG.getId());
    }

    public HashMap<ItemCategory, List<ItemStack>> getItemMap() {
        HashMap<ItemCategory, List<ItemStack>> retr = new HashMap<ItemCategory, List<ItemStack>>();
        FilterManager filterManager = PrimeItemFilter.get().getFilterManager();
        for (Map.Entry<Integer, Set<Short>> entry : this.getFilteredItems().entrySet()) {
            for (short dura : entry.getValue()) {
                ItemStack item = new ItemStack(Material.getMaterial((int)entry.getKey()), 1, dura);
                for (ItemCategory category : ItemCategory.values()) {
                    if (!filterManager.filterableItems.get((Object)category).contains(item)) continue;
                    List<ItemStack> items = retr.get((Object)category);
                    if (items == null) {
                        items = Lists.newArrayList();
                    }
                    if (!items.contains(item)) {
                        items.add(item);
                    }
                    retr.put(category, items);
                }
            }
        }
        return retr;
    }

    public void addFilterItem(ItemStack item) {
        if ((item.getType() == Material.MOB_SPAWNER || item.getType() == Material.POTION || item.getType() == Material.MONSTER_EGG) && this.filteredItems.containsKey(item.getTypeId())) {
            return;
        }
        Set<Short> shorts = this.filteredItems.get(item.getTypeId());
        if (shorts == null) {
            shorts = new HashSet<Short>();
        }
        if (!shorts.contains(item.getDurability())) {
            shorts.add(item.getDurability());
            this.filteredItems.put(item.getTypeId(), shorts);
        }
    }

    public void removeItemFromFilter(ItemStack item) {
        Set<Short> shorts = this.filteredItems.get(item.getTypeId());
        if (shorts == null) {
            return;
        }
        if (shorts.contains(item.getDurability())) {
            shorts.remove(item.getDurability());
        }
        if (shorts.size() == 0) {
            this.filteredItems.remove(item.getTypeId());
            return;
        }
        this.filteredItems.put(item.getTypeId(), shorts);
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public HashMap<Integer, Set<Short>> getFilteredItems() {
        return this.filteredItems;
    }
}
