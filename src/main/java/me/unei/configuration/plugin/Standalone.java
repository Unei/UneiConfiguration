package me.unei.configuration.plugin;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * The Standalone API class.
 */
public final class Standalone implements IPlugin
{
	private final UneiConfiguration plugin;
	
	private final Logger logger;

	private final File dataFolder;

	/**
	 * Initiate the standalone UneiConfiguration with a specific data folder.
	 * 
	 * @param dataFolder The data folder to use by default.
	 */
	public Standalone(final File dataFolder)
	{
		this.dataFolder = dataFolder;
		
		this.logger = new MyLogger(UneiConfiguration.class);
		
		plugin = new UneiConfiguration(this);
		
		this.onLoad();
	}

	/**
	 * Initiate the standalone UneiConfiguration.
	 * 
	 * <p><b>Warning:</b> as long as {@link UneiConfiguration#getInstance()} will return a Standalone instance,
	 * you don't need to call this constructor.</p>
	 */
	public Standalone()
	{
		this.dataFolder = new File("UneiConfiguration");
		
		this.logger = new MyLogger(UneiConfiguration.class);
		
		plugin = new UneiConfiguration(this);
		
		this.onLoad();
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
	public void registerStatsPlName(String name)
	{
		/* Doing nothing, no statistics analysis here... */
	}
	
	@Override
	public File getDataFolder()
	{
		return this.dataFolder;
	}
	
	@Override
	public InputStream getResource(String path)
	{
		return getClass().getClassLoader().getResourceAsStream(path);
	}
	
	@Override
	public Logger getLogger()
	{
		return this.logger;
	}
	
	@Override
	public IBasicPlugin.Type getType()
	{
		return IBasicPlugin.Type.STANDALONE;
	}
	
	private static final class MyLogger extends Logger
	{
		private String myName;
		
		public MyLogger(Class<? extends IBasicPlugin> plugin)
		{
			super(plugin.getCanonicalName(), null);
			this.myName = "[UneiConfiguration] ";
			setLevel(Level.ALL);
		}
		
		@Override
		public void log(LogRecord logRecord)
		{
			/*if (logRecord.getLevel().intValue() < Level.INFO.intValue())
			{
				logRecord.setLevel(Level.INFO);
			}*/
			logRecord.setMessage(myName + logRecord.getMessage());
			super.log(logRecord);
		}
	}
}