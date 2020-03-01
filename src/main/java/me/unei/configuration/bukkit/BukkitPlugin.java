package me.unei.configuration.bukkit;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.plugin.java.JavaPlugin;

import me.unei.configuration.plugin.IBasicPlugin;
import me.unei.configuration.plugin.IPlugin;
import me.unei.configuration.plugin.UneiConfiguration;

public final class BukkitPlugin extends JavaPlugin implements IPlugin {

	private final UneiConfiguration plugin;

	private final Map<String, Integer> dependers = new HashMap<>();
	private static final Integer ONE = Integer.valueOf(1);

	public BukkitPlugin() {
		plugin = new UneiConfiguration(this);
	}

	@Override
	public void onLoad() {
		plugin.onLoad();
	}

	@Override
	public void onEnable() {
		org.bstats.bukkit.Metrics metrics = new org.bstats.bukkit.Metrics(this);

		metrics.addCustomChart(new org.bstats.bukkit.Metrics.AdvancedPie("usingPlugins", () -> {
			Map<String, Integer> result = BukkitPlugin.this.dependers;
			return result;
		}));

		plugin.onEnable();
	}

	@Override
	public void onDisable() {
		plugin.onDisable();
	}

	@Override
	public void registerStatsPlName(String name) {
		this.dependers.put(name, BukkitPlugin.ONE);
	}

	public IBasicPlugin.Type getType() {
		return IBasicPlugin.Type.BUKKIT;
	}
}
