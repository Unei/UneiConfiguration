package me.unei.configuration.plugin;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Logger;

public interface IPlugin {

    public void onLoad();

    public void onEnable();

    public void onDisable();

    public File getDataFolder();

    public Logger getLogger();

    public InputStream getResource(String path);

    public IPlugin.Type getType();

    public static enum Type {
        BUKKIT, BUNGEECORD, SPONGE, FORGE;
    }
}