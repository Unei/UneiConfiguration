package me.unei.configuration.bukkit;

import org.bukkit.plugin.java.JavaPlugin;

import me.unei.configuration.plugin.IBasicPlugin;
import me.unei.configuration.plugin.IPlugin;
import me.unei.configuration.plugin.UneiConfiguration;

public final class BukkitPlugin extends JavaPlugin implements IPlugin {

    private final UneiConfiguration plugin;

    public BukkitPlugin() {
        plugin = new UneiConfiguration(this);
    }

    @Override
    public void onLoad() {
        plugin.onLoad();
    }

    @Override
    public void onEnable() {
    	@SuppressWarnings("unused")
    	org.bstats.bukkit.MetricsLite metrics = new org.bstats.bukkit.MetricsLite(this);
    	
        plugin.onEnable();
    }

    @Override
    public void onDisable() {
        plugin.onDisable();
    }
    
    public IBasicPlugin.Type getType() {
        return IBasicPlugin.Type.BUKKIT;
    }
}