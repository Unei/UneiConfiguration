package me.unei.configuration.plugin;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Logger;

import me.unei.configuration.reflection.NMSReflection;

public final class UneiConfiguration implements IPlugin {

    private static UneiConfiguration Instance = null;

    private final IPlugin source;

    @SuppressWarnings("unused")
    private boolean forceNbtUneiLib = false;

    public UneiConfiguration(IPlugin plugin) {
        this.source = plugin;
        NMSReflection.doNothing();
        UneiConfiguration.Instance = this;
    }

    public void onLoad() {
        this.getLogger().fine("Loading UNEI Configuration API...");

        if (NMSReflection.canUseNMS()) {
            this.getLogger().fine("NMS detected for NBT");
        } else {
            this.getLogger().fine("Using Unei's NBTLib");
        }
    }

    public void onEnable() {
        this.getLogger().fine("Enabling UNEI Configuration API...");
    }

    public void onDisable() {
        this.getLogger().fine("Disabling UNEI Configuration API...");
    }

    public File getDataFolder() {
        return source.getDataFolder();
    }

    public Logger getLogger() {
        return source.getLogger();
    }

    public InputStream getResource(String path) {
        return source.getResource(path);
    }

    public IPlugin.Type getType() {
        return source.getType();
    }

    public static UneiConfiguration getInstance() {
        return UneiConfiguration.Instance;
    }
}