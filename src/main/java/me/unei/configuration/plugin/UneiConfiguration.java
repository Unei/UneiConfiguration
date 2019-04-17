package me.unei.configuration.plugin;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Logger;

import me.unei.configuration.api.ConfigurationsImpl;
import me.unei.configuration.api.fs.FSUtilsImpl;
import me.unei.configuration.reflection.NMSReflection;

public final class UneiConfiguration extends me.unei.configuration.UneiConfiguration implements IPlugin {

	/**
	 * NEVER USE THIS.
	 */
    private static UneiConfiguration Instance = null;
    
    private final IPlugin source;

    /**
     * <p><b>Please, do not use this if you don't known EXACTLY what you are doing</b></p>
     * 
     * @param plugin The real plugin instance.
     */
    public UneiConfiguration(IPlugin plugin) {
    	setInstance();
    	ConfigurationsImpl.init();
    	FSUtilsImpl.init();
        this.source = plugin;
        NMSReflection.doNothing();
        UneiConfiguration.Instance = this;
    }

    @Override
	public void onLoad() {
        this.getLogger().fine("Loading UNEI Configuration API...");

        if (NMSReflection.canUseNMS()) {
            this.getLogger().fine("NMS classes detected !");
            this.getLogger().fine("NMS Version = " + NMSReflection.getVersion());
        }
    }

    @Override
	public void onEnable() {
        this.getLogger().fine("Enabling UNEI Configuration API...");
    }

    @Override
	public void onDisable() {
        this.getLogger().fine("Disabling UNEI Configuration API...");
    }

    @Override
	public File getDataFolder() {
        return source.getDataFolder();
    }

    @Override
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

    @Override
	public InputStream getResource(String path) {
        return source.getResource(path);
    }

    @Override
	public IBasicPlugin.Type getType() {
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
    
    public Updater getUpdater() {
    	return Updater.getUpdater(this);
    }
    
    public void checkVerionAsync(IUpdater.ICallback callback) {
    	getUpdater().checkVersionAsync(callback);
    }
    
    public IUpdater.Result checkVersion() {
    	return getUpdater().checkVersion();
    }
    
    public static Updater getTheUpdater() {
    	return Updater.getUpdater(UneiConfiguration.getInstance());
    }
    
    public static void checkMyVersionAsync(Updater.Callback callback) {
    	UneiConfiguration.getTheUpdater().checkVersionAsync(callback);
    }
    
    @Deprecated
    public static void checkMyVersionAsync(final Updater.UpdateCheckCallback callback) {
    	UneiConfiguration.getTheUpdater().checkVersionAsync(new Updater.Callback() {
    		@Override
			public void run(IUpdater up, IUpdater.Result res)
    		{
    			callback.run(res == IUpdater.Result.UPDATE_AVAILABLE);
			}
		});
    }
    
    public static IUpdater.Result checkMyVersion() {
    	return UneiConfiguration.getTheUpdater().checkVersion();
    }
}
