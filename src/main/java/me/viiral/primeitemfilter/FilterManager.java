// Decompiled with: CFR 0.151
// Class Version: 8
package me.viiral.primeitemfilter;

import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.viiral.primeitemfilter.utils.ItemBuilder;
import me.viiral.primeitemfilter.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class FilterManager {
    ConcurrentHashMap<UUID, Filter> itemFilters = new ConcurrentHashMap();
    private static Gson gson = new GsonBuilder().create();
    private static File jsonFile;
    ConcurrentHashMap<ItemCategory, List<ItemStack>> filterableItems = new ConcurrentHashMap();

    public Inventory getCategoryMenu(Player player) {
        Inventory inventory = Bukkit.createInventory(null, (int)9, (String)"Item Filter Categories");
        int index = 1;
        Filter filter = this.getItemFilters().get(player.getUniqueId());
        HashMap<ItemCategory, List<ItemStack>> filteredItemMap = null;
        if (filter != null) {
            filteredItemMap = filter.getItemMap();
        }
        for (ItemCategory category : ItemCategory.values()) {
            int amount = 1;
            ArrayList<String> lore = Lists.newArrayList();
            if (filteredItemMap != null && filteredItemMap.get((Object)category) != null) {
                amount = filteredItemMap.get((Object)category).size();
                lore.add("");
                lore.add(ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "Filtered Items (" + ChatColor.WHITE + amount + ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + ")");
                for (ItemStack item : filteredItemMap.get((Object)category)) {
                    lore.add(ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "* " + ChatColor.WHITE + Utils.getItemName(item));
                }
            }
            inventory.setItem(index++, ItemBuilder.buildItem(category.getIconMaterial(), amount, (short)0, ChatColor.AQUA + ChatColor.BOLD.toString() + category.getItemName(), lore));
        }
        return inventory;
    }

    public ItemCategory getCategoryFromItem(ItemStack item, Material itemMaterial) {
        short durability = item.getDurability();
        for (Map.Entry<ItemCategory, List<ItemStack>> entries : this.filterableItems.entrySet()) {
            for (ItemStack filterableItem : entries.getValue()) {
                boolean countDura;
                boolean bl = countDura = itemMaterial != Material.MOB_SPAWNER && itemMaterial != Material.MONSTER_EGG && itemMaterial != Material.POTION;
                if (itemMaterial != filterableItem.getType() || countDura && filterableItem.getDurability() != durability) continue;
                return entries.getKey();
            }
        }
        return null;
    }

    public Inventory getFilterInventory(ItemCategory category, Player player) {
        Inventory inv = Bukkit.createInventory(null, (int) Utils.getInventorySize(this.filterableItems.get((Object)category).size()), (String)("Item Filter: " + category.getItemName()));
        Filter filter = this.itemFilters.get(player.getUniqueId());
        for (ItemStack item : this.filterableItems.get((Object)category)) {
            inv.addItem(new ItemStack[]{this.getFilterMenuItem(item, filter)});
        }
        return inv;
    }

    public ItemStack getFilterMenuItem(ItemStack item, Filter filter) {
        ArrayList<String> lore = Lists.newArrayList();
        boolean contains = filter != null && filter.canPickupItem(item, null);
        lore.add((contains ? ChatColor.GREEN + ChatColor.BOLD.toString() + "WILL" : ChatColor.RED + ChatColor.BOLD.toString() + "WILL NOT") + ChatColor.GRAY + " be picked up");
        lore.add(ChatColor.GRAY + "when item filter is " + ChatColor.GRAY + ChatColor.UNDERLINE.toString() + "enabled.");
        ChatColor color = contains ? ChatColor.GREEN : ChatColor.RED;
        item = ItemBuilder.buildItem(item.getType(), item.getDurability(), color + Utils.getItemName(item), lore);
        if (contains) {
            item = Utils.addGlow(item);
        }
        return item;
    }

    private void setCategoryItems(ItemCategory cat, List<ItemStack> items) {
        this.filterableItems.put(cat, items);
    }

    private void setCategoryMaterials(ItemCategory cat, List<Material> materials) {
        ArrayList<ItemStack> items = Lists.newArrayList();
        for (Material mat : materials) {
            items.add(new ItemStack(mat, 1));
        }
        this.filterableItems.put(cat, items);
    }

    public void loadFilterItems() {
        this.setCategoryItems(ItemCategory.GEAR, Lists.newArrayList(new ItemStack(Material.IRON_HELMET), new ItemStack(Material.GOLD_HELMET), new ItemStack(Material.DIAMOND_HELMET), new ItemStack(Material.LEATHER_HELMET), new ItemStack(Material.IRON_CHESTPLATE), new ItemStack(Material.GOLD_CHESTPLATE), new ItemStack(Material.DIAMOND_CHESTPLATE), new ItemStack(Material.LEATHER_CHESTPLATE), new ItemStack(Material.IRON_LEGGINGS), new ItemStack(Material.GOLD_LEGGINGS), new ItemStack(Material.DIAMOND_LEGGINGS), new ItemStack(Material.LEATHER_LEGGINGS), new ItemStack(Material.IRON_BOOTS), new ItemStack(Material.GOLD_BOOTS), new ItemStack(Material.DIAMOND_BOOTS), new ItemStack(Material.LEATHER_BOOTS), new ItemStack(Material.IRON_SWORD), new ItemStack(Material.GOLD_SWORD), new ItemStack(Material.DIAMOND_SWORD), new ItemStack(Material.IRON_AXE), new ItemStack(Material.GOLD_AXE), new ItemStack(Material.DIAMOND_AXE), new ItemStack(Material.BOW), new ItemStack(Material.IRON_HOE), new ItemStack(Material.GOLD_HOE), new ItemStack(Material.DIAMOND_HOE), new ItemStack(Material.ARROW), new ItemStack(Material.ENDER_PEARL), new ItemStack(Material.POTION), new ItemStack(Material.SKULL_ITEM, 1, (short) 3)));
        this.setCategoryItems(ItemCategory.REDSTONE, Lists.newArrayList(new ItemStack(Material.REDSTONE), new ItemStack(Material.REDSTONE_LAMP_OFF), new ItemStack(Material.REDSTONE_TORCH_ON), new ItemStack(Material.REDSTONE_ORE), new ItemStack(Material.REDSTONE_COMPARATOR), new ItemStack(Material.DIODE), new ItemStack(Material.LAVA_BUCKET), new ItemStack(Material.WATER_BUCKET), new ItemStack(Material.TNT), new ItemStack(Material.WEB), new ItemStack(Material.SPONGE), new ItemStack(Material.ICE), new ItemStack(Material.PACKED_ICE), new ItemStack(Material.DISPENSER), new ItemStack(Material.DROPPER), new ItemStack(Material.MONSTER_EGG, 1, (short) 50), new ItemStack(Material.PISTON_BASE), new ItemStack(Material.PISTON_STICKY_BASE), new ItemStack(Material.HOPPER), new ItemStack(Material.FLINT_AND_STEEL)));
        this.setCategoryMaterials(ItemCategory.POTION, Lists.newArrayList(Material.getMaterial((int)375), Material.getMaterial((int)378), Material.getMaterial((int)370), Material.getMaterial((int)341), Material.getMaterial((int)372), Material.getMaterial((int)382), Material.getMaterial((int)353), Material.getMaterial((int)348), Material.getMaterial((int)89), Material.getMaterial((int)396), Material.getMaterial((int)117), Material.getMaterial((int)373), Material.getMaterial((int)374), Material.getMaterial((int)111), Material.getMaterial((int)391), Material.getMaterial((int)406), Material.getMaterial((int)40), Material.getMaterial((int)260), Material.getMaterial((int)39), Material.getMaterial((int)351), Material.getMaterial((int)394), Material.getMaterial((int)322)));
        this.setCategoryMaterials(ItemCategory.FOOD, Lists.newArrayList(Material.getMaterial((int)352), Material.getMaterial((int)391), Material.getMaterial((int)86), Material.getMaterial((int)103), Material.getMaterial((int)360), Material.getMaterial((int)363), Material.getMaterial((int)364), Material.getMaterial((int)338), Material.getMaterial((int)81), Material.getMaterial((int)319), Material.getMaterial((int)320), Material.getMaterial((int)322), Material.getMaterial((int)361), Material.getMaterial((int)362), Material.getMaterial((int)295)));
        this.setCategoryMaterials(ItemCategory.VALUABLES, Lists.newArrayList(Material.MOB_SPAWNER, Material.getMaterial((int)383), Material.getMaterial((int)421), Material.getMaterial((int)54), Material.getMaterial((int)146), Material.getMaterial((int)130), Material.getMaterial((int)339), Material.getMaterial((int)395), Material.getMaterial((int)384), Material.getMaterial((int)76), Material.getMaterial((int)340), Material.getMaterial((int)351), Material.getMaterial((int)50), Material.getMaterial((int)388), Material.getMaterial((int)378), Material.getMaterial((int)322), Material.getMaterial((int)264), Material.BONE, Material.ENDER_PEARL, Material.BLAZE_ROD, Material.NETHER_STAR, Material.FIREWORK_CHARGE));
        this.setCategoryItems(ItemCategory.ORES, Lists.newArrayList(new ItemStack(Material.getMaterial((int)15)), new ItemStack(Material.getMaterial((int)265)), new ItemStack(Material.getMaterial((int)42)), new ItemStack(Material.getMaterial((int)14)), new ItemStack(Material.getMaterial((int)266)), new ItemStack(Material.getMaterial((int)41)), new ItemStack(Material.getMaterial((int)73)), new ItemStack(Material.getMaterial((int)331)), new ItemStack(Material.getMaterial((int)152)), new ItemStack(Material.getMaterial((int)21)), new ItemStack(Material.getMaterial((int)351), 1, (short) 4), new ItemStack(Material.getMaterial((int)22)), new ItemStack(Material.getMaterial((int)16)), new ItemStack(Material.getMaterial((int)263)), new ItemStack(Material.getMaterial((int)173)), new ItemStack(Material.getMaterial((int)129)), new ItemStack(Material.getMaterial((int)388)), new ItemStack(Material.getMaterial((int)133)), new ItemStack(Material.getMaterial((int)56)), new ItemStack(Material.getMaterial((int)264)), new ItemStack(Material.getMaterial((int)57))));
        this.setCategoryMaterials(ItemCategory.OTHERS, Lists.newArrayList(Material.SAND, Material.GRAVEL, Material.GREEN_RECORD, Material.DRAGON_EGG));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void loadFilters() {
        this.loadFilterItems();
        long start = System.currentTimeMillis();
        try {
            if (!PrimeItemFilter.get().getDataFolder().exists()) {
                PrimeItemFilter.get().getDataFolder().mkdir();
            }
            if (!(jsonFile = new File(PrimeItemFilter.get().getDataFolder(), "playerFilters.json")).exists()) {
                jsonFile.createNewFile();
            }
            BufferedReader reader = new BufferedReader(new FileReader(jsonFile));
            try {
                String line;
                StringBuilder build = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    build.append(line);
                }
                this.itemFilters = (ConcurrentHashMap)gson.fromJson(build.toString(), new TypeToken<ConcurrentHashMap<UUID, Filter>>(){}.getType());
                if (this.itemFilters == null) {
                    this.itemFilters = new ConcurrentHashMap();
                }
                System.out.println("Took " + (System.currentTimeMillis() - start) + "ms to load " + this.itemFilters.size() + " player filters.");
            }
            finally {
                if (Collections.singletonList(reader).get(0) != null) {
                    reader.close();
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveFilters() {
        try {
            String map = gson.toJson(this.itemFilters);
            FileWriter writer = new FileWriter(jsonFile);
            try {
                writer.write(map);
                writer.flush();
            }
            finally {
                if (Collections.singletonList(writer).get(0) != null) {
                    writer.close();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ConcurrentHashMap<UUID, Filter> getItemFilters() {
        return this.itemFilters;
    }
}
