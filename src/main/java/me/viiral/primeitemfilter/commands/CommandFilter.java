package me.viiral.primeitemfilter.commands;

import me.viiral.primeitemfilter.Filter;
import me.viiral.primeitemfilter.FilterManager;
import me.viiral.primeitemfilter.PrimeItemFilter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandFilter
        implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player)sender;
        if (args.length == 1) {
            FilterManager manager = PrimeItemFilter.get().getFilterManager();
            if (args[0].equalsIgnoreCase("toggle")) {
                Filter filter;
                Filter filter2 = filter = manager.getItemFilters().containsKey(player.getUniqueId()) ? manager.getItemFilters().get(player.getUniqueId()) : new Filter();
                if (!manager.getItemFilters().containsKey(player.getUniqueId())) {
                    manager.getItemFilters().put(player.getUniqueId(), filter);
                }
                filter.setEnabled(!filter.isEnabled());
                player.sendMessage((filter.isEnabled() ? ChatColor.GREEN : ChatColor.RED) + ChatColor.BOLD.toString() + "(!) Item Filter " + (filter.isEnabled() ? ChatColor.GREEN + ChatColor.BOLD.toString() + ChatColor.UNDERLINE.toString() + "Enabled" : ChatColor.RED + ChatColor.BOLD.toString() + ChatColor.UNDERLINE.toString() + "Disabled"));
                if (!filter.isEnabled()) {
                    PrimeItemFilter.get().getItemListener().alerted.remove(player.getUniqueId());
                }
                if (filter.isEnabled()) {
                    player.sendMessage(ChatColor.GRAY + "Use " + ChatColor.GRAY + ChatColor.UNDERLINE.toString() + "/filter edit" + ChatColor.GRAY + " to edit the items you will pickup.");
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("edit")) {
                player.openInventory(PrimeItemFilter.get().getFilterManager().getCategoryMenu(player));
                return true;
            }
        }
        player.sendMessage("");
        player.sendMessage(ChatColor.AQUA + ChatColor.BOLD.toString() + "Item Filter Commands");
        player.sendMessage(ChatColor.WHITE + "/filter toggle");
        player.sendMessage(ChatColor.GRAY + "Toggle item pickup filter.");
        player.sendMessage("");
        player.sendMessage(ChatColor.WHITE + "/filter edit");
        player.sendMessage(ChatColor.GRAY + "Edit items you pickup while the item filter is enabled.");
        player.sendMessage("");
        return true;
    }
}
