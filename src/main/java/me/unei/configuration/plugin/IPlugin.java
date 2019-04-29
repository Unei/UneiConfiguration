package me.unei.configuration.plugin;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Logger;

public interface IPlugin extends IBasicPlugin {

    /**
     * Called when the plugin has just been loaded, after the plugin is loaded but before it has been enabled.
     * When multiple plugins are loaded, the onLoad() for all plugins is called before any onEnable() is called.
     *
     * @see org.bukkit.plugin.Plugin#onLoad()
     * @see net.md_5.bungee.api.plugin.Plugin#onLoad()
     */
    public void onLoad();

    /**
     * Called when this plugin is enabled.
     *
     * @see org.bukkit.plugin.Plugin#onEnable()
     * @see net.md_5.bungee.api.plugin.Plugin#onEnable()
     */
    public void onEnable();

    /**
     * Called when this plugin is disabled.
     *
     * @see org.bukkit.plugin.Plugin#onDisable()
     * @see net.md_5.bungee.api.plugin.Plugin#onDisable()
     */
    public void onDisable();

    /**
     * Returns the folder that the plugin data's files are located in. The folder may not yet exist.
     *
     * @see org.bukkit.plugin.Plugin#getDataFolder()
     * @see net.md_5.bungee.api.plugin.Plugin#getDataFolder()
     *
     * @return the data folder of this plugin
     */
    public File getDataFolder();

    /**
     * Returns the plugin logger associated with this server's logger. The returned logger automatically tags all log messages with the plugin's name.
     *
     * @see org.bukkit.plugin.Plugin#getLogger()
     * @see net.md_5.bungee.api.plugin.Plugin#getLogger()
     *
     * @return Logger associated with this plugin
     */
    public Logger getLogger();

    /**
     * Gets an embedded resource in this plugin, within the jar or container.
     * Care must be taken to close the returned stream.
     *
     * @see org.bukkit.plugin.Plugin#getResource(String)
     * @see net.md_5.bungee.api.plugin.Plugin#getResourceAsStream(String)
     *
     * @param path the full path name of this resource
     *
     * @return the stream for getting this resource, or null if it does not exist
     */
    public InputStream getResource(String path);

    /**
     * Returns the type of plugin this is.
     *
     * @return the type of plugin this is
     */
    public IBasicPlugin.Type getType();
    
    public void registerStatsPlName(String name);
}
