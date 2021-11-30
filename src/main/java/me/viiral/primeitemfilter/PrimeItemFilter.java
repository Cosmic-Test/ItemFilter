package me.viiral.primeitemfilter;

import me.viiral.primeitemfilter.commands.CommandFilter;
import me.viiral.primeitemfilter.listeners.ItemFilterListener;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class PrimeItemFilter extends JavaPlugin {

    private static PrimeItemFilter instance;
    private FilterManager filterManager;
    private ItemFilterListener itemListener;

    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
        this.filterManager = new FilterManager();
        this.filterManager.loadFilters();
        this.itemListener = new ItemFilterListener();
        this.getServer().getPluginManager().registerEvents((Listener)this.itemListener, (Plugin)this);
        this.getCommand("filter").setExecutor((CommandExecutor)new CommandFilter());
    }

    public void onDisable() {
        this.filterManager.saveFilters();
    }

    public static PrimeItemFilter get() {
        return instance;
    }

    public FilterManager getFilterManager() {
        return this.filterManager;
    }

    public ItemFilterListener getItemListener() {
        return this.itemListener;
    }
}
