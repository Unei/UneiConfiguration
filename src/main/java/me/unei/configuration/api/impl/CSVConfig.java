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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Charsets;

import me.unei.configuration.SavedFile;
import me.unei.configuration.SerializerHelper;
import me.unei.configuration.api.IFlatCSVConfiguration;
import me.unei.configuration.api.UntypedFlatStorage;
import me.unei.configuration.api.exceptions.FileFormatException;
import me.unei.configuration.api.exceptions.UnexpectedClassException;
import me.unei.configuration.plugin.UneiConfiguration;

public class CSVConfig extends UntypedFlatStorage<CSVConfig> implements IFlatCSVConfiguration
{
	public static final String CSV_FILE_EXT = ".csv";
	public static final String CSV_TMP_EXT = ".tmp";
	
	private static final List<String> DEFAULT_HEADER_LINE = Collections.unmodifiableList(Arrays.asList("Key", "Value"));
	
	private Map<String, Object> data = new HashMap<String, Object>();
	private List<String> keyLine;
	
	public CSVConfig(SavedFile file)
	{
		super(file);
		
		this.keyLine = new ArrayList<String>();
		this.resetHeaderLine();
		
		this.init();
	}
	
	public CSVConfig(File folder, String fileName)
	{
		this(new SavedFile(folder, fileName, CSVConfig.CSV_FILE_EXT));
	}
	
	public void save() throws UnexpectedClassException {
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
			SerializerHelper.writeCSV(w, keyLine, data);
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
			Map<String, Object> tmpData = SerializerHelper.readCSV(r, keyLine);
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
	
	public List<String> getHeaderLine() {
		return this.keyLine;
	}
	
	public void resetHeaderLine() {
		this.keyLine.clear();
		for (String elem : CSVConfig.DEFAULT_HEADER_LINE) {
			this.keyLine.add(elem);
		}
	}
	
	public Set<String> getKeys() {
		return this.data.keySet();
	}
	
	public boolean contains(String key) {
		return this.data.containsKey(key);
	}
	
	public void setString(String key, String value) {
		if (!this.canAccess()) {
			return;
		}
		if (value == null) {
			this.remove(key);
			return;
		}
		data.put(key, value);
	}
	
	public void setList(String key, List<String> value) {
		if (!this.canAccess()) {
			return;
		}
		if (value == null) {
			this.remove(key);
			return;
		}
		data.put(key, value);
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getList(String key) {
		if (!data.containsKey(key) || data.get(key) == null) {
			return Collections.emptyList();
		}
		if (!(data.get(key) instanceof List)) {
			return Collections.emptyList();
		}
		try {
			return (List<String>)data.get(key);
		} catch (ClassCastException e) {
			return Collections.emptyList();
		}
	}
	
	public String getString(String key) {
		if (!data.containsKey(key) || data.get(key) == null) {
			return "";
		}
		return data.get(key).toString();
	}
	
	public void remove(String key) {
		if (!this.canAccess()) {
			return;
		}
		data.remove(key);
	}
	
	@Override
	public String toString() {
		return "CSVConfig=" + this.data.toString();
	}
}