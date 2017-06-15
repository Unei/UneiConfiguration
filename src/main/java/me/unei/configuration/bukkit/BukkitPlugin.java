package me.unei.configuration.bukkit;

import org.bukkit.plugin.java.JavaPlugin;

import me.unei.configuration.plugin.IPlugin;
import me.unei.configuration.plugin.UneiConfiguration;

public final class BukkitPlugin extends JavaPlugin implements IPlugin
{
	private final UneiConfiguration plugin;
	
	public BukkitPlugin()
	{
		plugin = new UneiConfiguration(this);
	}
	
	@Override
	public void onLoad()
	{
		plugin.onLoad();
	}
	
	@Override
	public void onEnable()
	{
		plugin.onEnable();
	}
	
	@Override
	public void onDisable()
	{
		plugin.onDisable();
	}

	@Override
	public IPlugin.Type getType()
	{
		return IPlugin.Type.BUKKIT;
	}
}