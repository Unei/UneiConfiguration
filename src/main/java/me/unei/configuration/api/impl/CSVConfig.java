package me.unei.configuration.api.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Charsets;

import me.unei.configuration.SavedFile;
import me.unei.configuration.SerializerHelper;
import me.unei.configuration.api.IFlatConfiguration;
import me.unei.configuration.api.UntypedFlatStorage;
import me.unei.configuration.api.exceptions.FileFormatException;
import me.unei.configuration.plugin.UneiConfiguration;

public class CSVConfig extends UntypedFlatStorage<CSVConfig> implements IFlatConfiguration
{
	public static final String CSV_FILE_EXT = ".csv";
	public static final String CSV_TMP_EXT = ".tmp";
	
	private Map<String, Object> data = new HashMap<String, Object>();
	
	public CSVConfig(File folder, String fileName)
	{
		super(new SavedFile(folder, fileName, CSVConfig.CSV_FILE_EXT));
		
		this.init();
	}
	
	public void save() {
		if (!this.canAccess()) {
			return;
		}
		if (this.file.getFile() == null) {
			return;
		}
		File tmp = new File(file.getFolder(), file.getFileName() + CSVConfig.CSV_TMP_EXT);
		UneiConfiguration.getInstance().getLogger().fine("Writing CSV data to file " + getFileName() + "...");
		try {
			Writer w = new OutputStreamWriter(new FileOutputStream(tmp), Charsets.UTF_8);
			SerializerHelper.writeCSV(w, Arrays.asList("Key", "Value"), data);
			w.flush();
			w.close();
			if (file.getFile().exists()) {
				UneiConfiguration.getInstance().getLogger().finer("Replacing already present file " + getFileName() + ".");
				file.getFile().delete();
			}
			tmp.renameTo(file.getFile());
			tmp.delete();
			UneiConfiguration.getInstance().getLogger().fine("Successfully written.");
		} catch (IOException e) {
			UneiConfiguration.getInstance().getLogger().warning("An error occured while saving CSV file " + getFileName() + ":");
			e.printStackTrace();
		}
	}
	
	public void reload() throws FileFormatException {
		if (!this.canAccess()) {
			return;
		}
		if (!this.file.getFile().exists()) {
			this.save();
			return;
		}
		try {
			UneiConfiguration.getInstance().getLogger().fine("Reading CSV from file " + getFileName() + "...");
			Reader r = new InputStreamReader(new FileInputStream(file.getFile()), Charsets.UTF_8);
			List<String> names = new ArrayList<String>();
			Map<String, Object> tmpData = SerializerHelper.readCSV(r, names);
			r.close();
			if (tmpData != null && !tmpData.isEmpty()) {
				data.clear();
				for (Entry<String, Object> entry : tmpData.entrySet()) {
					data.put(entry.getKey(), entry.getValue());
				}
			}
			UneiConfiguration.getInstance().getLogger().fine("Successfully read.");
		} catch (IOException e) {
			UneiConfiguration.getInstance().getLogger().warning("An error occured while loading CSV file " + getFileName() + ":");
			e.printStackTrace();
		}
	}
	
	public Set<String> getKeys() {
		return this.data.keySet();
	}
	
	public boolean contains(String key) {
		return this.data.containsKey(key);
	}
	
	public Object get(String key) {
		return this.data.get(key);
	}
	
	public void set(String key, Object value) {
		if (!this.canAccess()) {
			return;
		}
		if (value == null) {
			data.remove(key);
		} else {
        	if (value instanceof Double) {
        		if (((Double)value).isInfinite() || ((Double)value).isNaN()) {
        			data.put(key, value.toString());
        		} else {
        			data.put(key, value);
        		}
        	} else if (value instanceof Float) {
        		if (((Float)value).isInfinite() || ((Float)value).isNaN()) {
        			data.put(key, value.toString());
        		} else {
        			data.put(key, value);
        		}
        	} else {
        		data.put(key, value);
        	}
		}
	}
	
	public void remove(String key) {
		set(key, null);
	}
	
	@Override
	public String toString() {
		return "CSVConfig=" + this.data.toString();
	}
}