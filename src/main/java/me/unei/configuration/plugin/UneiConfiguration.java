package me.unei.configuration.plugin;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Logger;

import me.unei.configuration.reflection.NMSReflection;

public final class UneiConfiguration implements IPlugin {

	/**
	 * NEVER USE THIS.
	 */
    private static UneiConfiguration Instance = null;
    
    private final IPlugin source;

    public UneiConfiguration(IPlugin plugin) {
        this.source = plugin;
        NMSReflection.doNothing();
        UneiConfiguration.Instance = this;
    }

    public void onLoad() {
        this.getLogger().fine("Loading UNEI Configuration API...");

        if (NMSReflection.canUseNMS()) {
            this.getLogger().fine("NMS classes detected !");
            this.getLogger().fine("NMS Version = " + NMSReflection.getVersion());
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
    	try
    	{
    		return source.getLogger();
    	}
    	catch (AbstractMethodError t)
    	{
    		return Logger.getLogger("UneiConfiguration");
    	}
    }

    public InputStream getResource(String path) {
        return source.getResource(path);
    }

    public IPlugin.Type getType() {
        return source.getType();
    }
    
    /**
     * This method is safe as long as the {@link Standalone} class is present.
     * 
     * @return A not null instance of {@link UneiConfiguration}.
     */
    public static UneiConfiguration getInstance() {
    	if (UneiConfiguration.Instance == null) {
    		new Standalone();
    	}
        return UneiConfiguration.Instance;
    }
    
    public static Updater getUpdater() {
    	return Updater.getUpdater(UneiConfiguration.getInstance());
    }
    
    public static void checkVersionAsync(Updater.Callback callback) {
    	UneiConfiguration.getUpdater().checkVersionAsync(callback);
    }
    
    @Deprecated
    public static void checkVersionAsync(Updater.UpdateCheckCallback callback) {
    	UneiConfiguration.getUpdater().checkVersionAsync(new Updater.Callback() {
    		public void run(Updater up, Updater.Result res)
    		{
    			callback.run(res == Updater.Result.UPDATE_AVAILABLE);
			}
		});
    }
    
    public static Updater.Result checkVersion() {
    	return UneiConfiguration.getUpdater().checkVersion();
    }
}
