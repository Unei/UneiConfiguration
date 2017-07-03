package me.unei.configuration.plugin;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public final class Standalone implements IPlugin
{
	private final UneiConfiguration plugin;
	
	private final Logger logger;
	
	public Standalone()
	{
		plugin = new UneiConfiguration(this);
		
		this.logger = new MyLogger(plugin);
	}
	
	public void onLoad()
	{
		plugin.onLoad();
	}
	
	public void onEnable()
	{
		plugin.onEnable();
	}
	
	public void onDisable()
	{
		plugin.onDisable();
	}
	
	public File getDataFolder()
	{
		return new File("UneiConfiguration");
	}
	
	public InputStream getResource(String path)
	{
		return getClass().getClassLoader().getResourceAsStream(path);
	}
	
	public Logger getLogger()
	{
		return this.logger;
	}
	
	public IPlugin.Type getType()
	{
		return IPlugin.Type.STANDALONE;
	}
	
	private static final class MyLogger extends Logger
	{
		private String myName;
		
		public MyLogger(IPlugin plugin)
		{
			super(plugin.getClass().getCanonicalName(), null);
			this.myName = "[UneiConfiguration] ";
			setParent(Logger.getGlobal());
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