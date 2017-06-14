package me.unei.configuration;

import org.bukkit.plugin.java.JavaPlugin;

public class UneiConfiguration extends JavaPlugin
{
	private static UneiConfiguration Instance;
	
	@Override
	public void onLoad()
	{
		UneiConfiguration.Instance = this;
	}
	
	@Override
	public void onEnable()
	{
		// Code ...
	}
	
	@Override
	public void onDisable()
	{
		// Code ...
	}
	
	public static UneiConfiguration getMe()
	{
		return UneiConfiguration.Instance;
	}
}