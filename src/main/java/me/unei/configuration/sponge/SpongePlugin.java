package me.unei.configuration.sponge;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.logging.Logger;

import com.google.inject.Inject;

import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;

import me.unei.configuration.plugin.IPlugin;
import me.unei.configuration.plugin.UneiConfiguration;

@Plugin(id = SpongePlugin.PluginID, name = "UneiConfigurationAPI", version = "0.0.1", description = "A configuration Sponge API",
		authors = {"JësFot", "Au2001"})
public class SpongePlugin implements IPlugin
{
	public static final String PluginID = "uneiconfiguration";
	
	private final UneiConfiguration plugin;
	
	@Inject
	@ConfigDir(sharedRoot = false)
	private Path configDir;
	
	private Logger logger;
	
	public SpongePlugin()
	{
		this.plugin = new UneiConfiguration(this);
		this.logger = Logger.getLogger("UneiConfiguration");
	}
	
	@Listener
	public void onPreInit(GamePreInitializationEvent event)
	{
		//
	}
	
	public void onLoad()
	{
		this.plugin.onLoad();
	}
	
	public void onEnable()
	{
		this.plugin.onEnable();
	}
	
	public void onDisable()
	{
		this.plugin.onDisable();
	}
	
	public Logger getLogger()
	{
		return this.logger;
	}
	
	public File getDataFolder()
	{
		return this.configDir.toFile();
	}
	
	public IPlugin.Type getType()
	{
		return IPlugin.Type.SPONGE;
	}
	
	public InputStream getResource(String name)
	{
		return getClass().getClassLoader().getResourceAsStream(name);
	}
}