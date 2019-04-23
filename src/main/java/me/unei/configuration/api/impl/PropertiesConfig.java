package me.unei.configuration.api.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.Set;

import me.unei.configuration.SavedFile;
import me.unei.configuration.api.IFlatPropertiesConfiguration;
import me.unei.configuration.api.UntypedFlatStorage;
import me.unei.configuration.api.exceptions.FileFormatException;
import me.unei.configuration.plugin.UneiConfiguration;

public class PropertiesConfig extends UntypedFlatStorage<PropertiesConfig> implements IFlatPropertiesConfiguration
{
	public static final String PROP_FILE_EXT = ".properties";
	public static final String PROP_TMP_EXT = ".tmp";
	
	private Properties data;
	
	public PropertiesConfig(SavedFile file)
	{
		super(file);
		
		this.data = new Properties();
		
		this.init();
	}
	
	public PropertiesConfig(File folder, String fileName)
	{
		this(new SavedFile(folder, fileName, PropertiesConfig.PROP_FILE_EXT));
	}
	
	public void save()
	{
		if (!this.canAccess()) {
			return;
		}
		if (this.file.getFile() == null) {
			return;
		}
        File tmp = new File(this.file.getFolder(), this.file.getFullName() + PropertiesConfig.PROP_TMP_EXT);
		try
		{
			if (tmp.exists()) {
				tmp.delete();
			}
            UneiConfiguration.getInstance().getLogger().fine("Writing Properties to file " + getFileName() + "...");
            Writer w = new OutputStreamWriter(new FileOutputStream(tmp), StandardCharsets.UTF_8);
			data.store(w, null);
			w.flush();
			w.close();
            if (this.file.getFile().exists()) {
                UneiConfiguration.getInstance().getLogger().finer("Replacing already present file " + getFileName() + ".");
                this.file.getFile().delete();
            }
            tmp.renameTo(this.file.getFile());
            tmp.delete();
            UneiConfiguration.getInstance().getLogger().fine("Successfully written.");
		}
		catch (IOException e)
		{
            UneiConfiguration.getInstance().getLogger().warning("An error occured while saving Properties file " + getFileName() + ":");
			e.printStackTrace();
		}
	}
	
	public void reload() throws FileFormatException
	{
		if (!this.canAccess()) {
			return;
		}
		if (this.file.getFile() == null) {
			return;
		}
		if (!this.file.getFile().exists()) {
			this.save();
			return;
		}
		try
		{
			data.clear();
            UneiConfiguration.getInstance().getLogger().fine("Reading Properties from file " + getFileName() + "...");
            Reader r = new InputStreamReader(new FileInputStream(file.getFile()), StandardCharsets.UTF_8);
			data.load(r);
			r.close();
            UneiConfiguration.getInstance().getLogger().fine("Successfully read.");
		}
		catch (IOException e)
		{
            UneiConfiguration.getInstance().getLogger().warning("An error occured while loading Properties file " + getFileName() + ":");
			e.printStackTrace();
		}
		catch (IllegalArgumentException e)
		{
			throw new FileFormatException("Java Properties", file.getFile(), "", e);
		}
	}
	
	public boolean contains(String key)
	{
		return data.containsKey(key);
	}
	
	public Set<String> getKeys()
	{
		return data.stringPropertyNames();
	}
	
	public String get(String key, String defaultValue)
	{
		return data.getProperty(key, defaultValue);
	}
	
	public void setString(String key, String value)
	{
		if (!this.canAccess()) {
			return;
		}
		if (value == null) {
			this.remove(key);
			return;
		}
		data.setProperty(key, value);
	}
	
	public String getString(String key)
	{
		return data.getProperty(key);
	}
	
	public void remove(String key)
	{
		if (!this.canAccess()) {
			return;
		}
		this.data.remove(key);
	}
	
	@Override
	public String toString() {
		return "PropertiesConfig=" + this.data.toString();
	}
}