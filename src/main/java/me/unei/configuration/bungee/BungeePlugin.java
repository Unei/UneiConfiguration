package me.unei.configuration.bungee;

import java.io.InputStream;

import me.unei.configuration.plugin.IPlugin;
import me.unei.configuration.plugin.UneiConfiguration;
import net.md_5.bungee.api.plugin.Plugin;

public final class BungeePlugin extends Plugin implements IPlugin
{
	private final UneiConfiguration plugin;
	
	public BungeePlugin()
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
	public InputStream getResource(String path)
	{
		return this.getResourceAsStream(path);
	}

	@Override
	public IPlugin.Type getType()
	{
		return IPlugin.Type.BUNGEECORD;
	}
}