package me.unei.configuration.plugin;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Logger;

public final class UneiConfiguration implements IPlugin
{
	private static UneiConfiguration Instance;
	
	private final IPlugin source;
	
	public UneiConfiguration(IPlugin plugin)
	{
		this.source = plugin;
	}
	
	
	@Override
	public void onLoad()
	{
		UneiConfiguration.Instance = this;
	}
	
	@Override
	public void onEnable()
	{
		this.getLogger().info("Enabling UNEI Configuration API...");
	}
	
	@Override
	public void onDisable()
	{
		this.getLogger().info("Disabling UNEI Configuration API...");
	}
	

	@Override
	public File getDataFolder() {
		return source.getDataFolder();
	}

	@Override
	public Logger getLogger() {
		return source.getLogger();
	}

	@Override
	public InputStream getResource(String path) {
		return source.getResource(path);
	}

	@Override
	public IPlugin.Type getType() {
		return source.getType();
	}
	
	public static UneiConfiguration getMe() {
		return UneiConfiguration.Instance;
	}
}