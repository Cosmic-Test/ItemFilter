package me.viiral.primeitemfilter.listeners;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import me.viiral.primeitemfilter.Filter;
import me.viiral.primeitemfilter.FilterManager;
import me.viiral.primeitemfilter.ItemCategory;
import me.viiral.primeitemfilter.PrimeItemFilter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public class ItemFilterListener
        implements Listener {
    public Set<UUID> alerted = new HashSet<UUID>();

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player)event.getPlayer();
        if (event.getInventory().getName().startsWith("Item Filter:") && player.hasMetadata("filter_edit")) {
            player.removeMetadata("filter_edit", (Plugin)PrimeItemFilter.get());
            Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)PrimeItemFilter.get(), () -> {
                player.openInventory(PrimeItemFilter.get().getFilterManager().getCategoryMenu((Player)event.getPlayer()));
                player.playSound(player.getLocation(), Sound.BAT_TAKEOFF, 0.3f, 1.4f);
            }, 1L);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getName().startsWith("Item Filter:")) {
            Material type;
            event.setCancelled(true);
            ItemStack item = event.getCurrentItem();
            Material material = type = item == null ? null : item.getType();
            if (item == null || type == Material.AIR) {
                return;
            }
            FilterManager manager = PrimeItemFilter.get().getFilterManager();
            if (event.getRawSlot() + 1 > event.getInventory().getSize()) {
                return;
            }
            Player player = (Player)event.getWhoClicked();
            Filter filter = manager.getItemFilters().computeIfAbsent(player.getUniqueId(), e -> new Filter());
            if (filter.canPickupItem(item, type)) {
                filter.removeItemFromFilter(item);
                player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.3f, 1.4f);
            } else {
                filter.addFilterItem(item);
                player.playSound(player.getLocation(), Sound.ANVIL_LAND, 0.3f, 1.4f);
            }
            event.getInventory().setItem(event.getRawSlot(), manager.getFilterMenuItem(item, filter));
            player.updateInventory();
        } else if (event.getInventory().getName().equals("Item Filter Categories")) {
            event.setCancelled(true);
            ItemStack item = event.getCurrentItem();
            if (item == null || item.getType() == Material.AIR) {
                return;
            }
            if (event.getRawSlot() + 1 > event.getInventory().getSize()) {
                return;
            }
            FilterManager manager = PrimeItemFilter.get().getFilterManager();
            ItemCategory category = ItemCategory.getFromItemName(item.getItemMeta().getDisplayName());
            if (category == null) {
                return;
            }
            Player player = (Player)event.getWhoClicked();
            player.setMetadata("filter_edit", (MetadataValue)new FixedMetadataValue((Plugin)PrimeItemFilter.get(), (Object)""));
            player.openInventory(manager.getFilterInventory(category, (Player)event.getWhoClicked()));
            player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0f, 2.0f);
        }
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onPLayerCommand(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().startsWith("/filter")) {
            event.setMessage(event.getMessage().replaceFirst("/filter", "/filters"));
        }
        if (event.getMessage().equals("/ft")) {
            event.setMessage("/filters toggle");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.alerted.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Material type;
        Player player = event.getPlayer();
        ItemStack item = event.getItem().getItemStack();
        Material material = type = item == null ? null : item.getType();
        if (item == null || type == Material.AIR) {
            return;
        }
        FilterManager manager = PrimeItemFilter.get().getFilterManager();
        Filter filter = manager.getItemFilters().get(player.getUniqueId());
        if (filter != null && filter.isEnabled() && !filter.canPickupItem(item, type)) {
            if (!this.alerted.contains(player.getUniqueId())) {
                player.sendMessage(ChatColor.YELLOW + ChatColor.BOLD.toString() + "(!) " + ChatColor.YELLOW + "You cannot loot some items because you have an Item Pickup /filter enabled.");
                player.sendMessage(ChatColor.GRAY + "Use /filter toggle to disable your Item Filter.");
                this.alerted.add(player.getUniqueId());
            }
            event.setCancelled(true);
        }
    }

    public Set<UUID> getAlerted() {
        return this.alerted;
    }
}
