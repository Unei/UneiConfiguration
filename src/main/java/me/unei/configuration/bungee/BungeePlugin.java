package me.unei.configuration.bungee;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import me.unei.configuration.plugin.IBasicPlugin;
import me.unei.configuration.plugin.IPlugin;
import me.unei.configuration.plugin.UneiConfiguration;
import net.md_5.bungee.api.plugin.Plugin;

public final class BungeePlugin extends Plugin implements IPlugin {

	private final UneiConfiguration plugin;

	private final Map<String, Integer> dependers = new HashMap<>();
	private static final Integer ONE = Integer.valueOf(1);

	public BungeePlugin() {
		plugin = new UneiConfiguration(this);
	}

	@Override
	public void onLoad() {
		plugin.onLoad();
	}

	@Override
	public void onEnable() {
		org.bstats.bungeecord.Metrics metrics = new org.bstats.bungeecord.Metrics(this);

		metrics.addCustomChart(new org.bstats.bungeecord.Metrics.AdvancedPie("usingPlugins", () -> {
			Map<String, Integer> result = BungeePlugin.this.dependers;
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
		this.dependers.put(name, BungeePlugin.ONE);
	}

	public InputStream getResource(String path) {
		return this.getResourceAsStream(path);
	}

	public IBasicPlugin.Type getType() {
		return IBasicPlugin.Type.BUNGEECORD;
	}
}
