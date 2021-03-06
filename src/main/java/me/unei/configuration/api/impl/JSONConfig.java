package me.unei.configuration.api.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Map.Entry;

import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonWriter;

import me.unei.configuration.SavedFile;
import me.unei.configuration.api.IConfiguration;
import me.unei.configuration.api.IJSONConfiguration;
import me.unei.configuration.api.UntypedStorage;
import me.unei.configuration.api.Configuration;
import me.unei.configuration.api.Configurations.ConfigurationType;
import me.unei.configuration.api.exceptions.FileFormatException;
import me.unei.configuration.api.exceptions.NoFieldException;
import me.unei.configuration.api.exceptions.NotImplementedException;
import me.unei.configuration.api.fs.IPathNavigator.PathSymbolsType;
import me.unei.configuration.api.fs.PathComponent;
import me.unei.configuration.api.fs.PathNavigator;
import me.unei.configuration.formats.Storage;
import me.unei.configuration.formats.StorageType;
import me.unei.configuration.formats.StringHashMap;
import me.unei.configuration.formats.Storage.Key;
import me.unei.configuration.formats.StorageConverter;
import me.unei.configuration.plugin.UneiConfiguration;

public final class JSONConfig extends UntypedStorage<JSONConfig> implements IJSONConfiguration {

	public static final String JSON_FILE_EXT = ".json";
	public static final String JSON_TMP_EXT = ".tmp";
	private static final Gson GSON;

	static {
		GsonBuilder builder = new GsonBuilder();
		builder = builder.serializeNulls().serializeSpecialFloatingPointValues();
		// builder = builder.enableComplexMapKeySerialization();
		// builder = builder.setLenient().setPrettyPrinting();
		builder = builder.setPrettyPrinting();
		GSON = builder.create();
	}

	private Storage<Object> data = null;

	final Storage<Object> getData() {
		if (data == null) {
			data = new StringHashMap<Object>();
		}
		return data;
	}

	public JSONConfig(SavedFile file) {
		this(file, PathSymbolsType.BUKKIT);
	}

	public JSONConfig(SavedFile file, PathSymbolsType symType) {
		super(file, symType);

		this.init();
	}

	public JSONConfig(File folder, String fileName) {
		this(folder, fileName, PathSymbolsType.BUKKIT);
	}

	public JSONConfig(File folder, String fileName, PathSymbolsType symType) {
		this(new SavedFile(folder, fileName, JSONConfig.JSON_FILE_EXT), symType);
	}

	public JSONConfig(String data, PathSymbolsType symType) {
		super(new SavedFile(), symType);

		this.init();
		this.loadFromString(data);
	}

	public JSONConfig(String data) {
		this(data, PathSymbolsType.BUKKIT);
	}

	private JSONConfig(JSONConfig p_parent, String p_nodeName) {
		super(p_parent, p_nodeName);

		this.updateNode();
	}

	private JSONConfig(JSONConfig p_parent, int p_nodeIndex) {
		super(p_parent, p_nodeIndex);

		this.updateNode();
	}

	@Override
	public ConfigurationType getConfigurationType() {
		return ConfigurationType.JSON;
	}

	public static JSONConfig getForPath(File folder, String fileName, String path, PathSymbolsType symType) {
		return JSONConfig.getForPath(new JSONConfig(folder, fileName, symType), path);
	}

	public static JSONConfig getForPath(File folder, String fileName, String path) {
		return JSONConfig.getForPath(new JSONConfig(folder, fileName), path);
	}

	public static JSONConfig getForPath(JSONConfig root, String path) {
		if (root == null) {
			return null;
		}
		return root.getSubSection(path);
	}

	@Override
	public JSONConfig getRoot() {
		return (JSONConfig) super.getRoot();
	}

	@Override
	public JSONConfig getChild(String name) {
		if (!this.canAccess()) {
			return null;
		}

		if (name == null || name.isEmpty()) {
			return this;
		}
		JSONConfig child = super.findInChildrens(new Key(name));

		if (child != null) {
			child.parent = this;
			return child;
		}
		return new JSONConfig(this, name);
	}

	@Override
	public StorageType getType() {
		return (this.data != null) ? this.data.getStorageType() : StorageType.UNDEFINED;
	}

	@Override
	protected void updateFromParent() {
		if (this.parent != null && this.parent.data != null) {

			if (this.parent.getData().getStorageType() != StorageType.UNDEFINED) {
				Object me = this.parent.data.get(Key.of(this.parent.getType(), nodeAtomicIndex, nodeName));
				Storage<Object> tmp = StorageConverter.allocateBest(me, null, null);

				if (tmp != null) {
					this.data = tmp;
				} else {
					this.data = new StringHashMap<Object>();
				}
				this.parent.data.set(Key.of(this.parent.getType(), nodeAtomicIndex, nodeName), this.data);
			}
		}
	}

	@Override
	public void setType(StorageType type) {
		if (!this.canAccess()) {
			return;
		}
		throw new NotImplementedException();
	}

	private JSONConfig getParentObj(PathComponent.PathComponentsList path) {
		PathNavigator<JSONConfig> pn = new PathNavigator<JSONConfig>(this);
		PathComponent.PathComponentsList pathList = PathNavigator.cleanPath(path);
		pathList.removeLast();

		if (!pn.followPath(pathList)) {
			return this;
		}
		return pn.getCurrentNode();
	}

	private Storage<Object> getParentMap(PathComponent.PathComponentsList path) {
		JSONConfig dir;
		PathNavigator<JSONConfig> pn = new PathNavigator<JSONConfig>(this);
		PathComponent.PathComponentsList pathList = PathNavigator.cleanPath(path);
		pathList.removeLast();

		if (!pn.followPath(pathList)) {
			return data;
		}
		dir = pn.getCurrentNode();
		return dir.data;
	}

	@Override
	public void save() {
		if (!this.canAccess()) {
			return;
		}

		if (this.parent != null) {
			this.parent.save();
			return;
		}

		if (this.file.getFile() == null) {
			return;
		}
		File tmp = new File(this.file.getFolder(), this.file.getFullName() + JSONConfig.JSON_TMP_EXT);
		UneiConfiguration.getInstance().getLogger().fine("Writing JSON to file " + getFileName() + "...");

		try {
			Writer w = new OutputStreamWriter(new FileOutputStream(tmp), StandardCharsets.UTF_8);
			JsonWriter jw = new JsonWriter(w);
			jw.setIndent("  ");
			JSONConfig.GSON.toJson(getData(), data.getClass(), jw);
			jw.close();

			if (this.file.getFile().exists()) {
				UneiConfiguration.getInstance().getLogger()
						.finer("Replacing already present file " + getFileName() + ".");
				this.file.getFile().delete();
			}
			tmp.renameTo(this.file.getFile());
			tmp.delete();
			UneiConfiguration.getInstance().getLogger().fine("Successfully written.");
		} catch (IOException e) {
			UneiConfiguration.getInstance().getLogger()
					.warning("An error occured while saving JSON file " + getFileName() + ":");
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void reload() throws FileFormatException {
		if (!this.canAccess()) {
			return;
		}

		if (this.parent != null) {
			this.parent.reload();
			// this.synchronize();
			return;
		}

		if (this.file.getFile() == null) {
			this.data = new StringHashMap<>();
			return;
		}

		if (!this.file.getFile().exists()) {
			this.save();
			return;
		}

		try {
			UneiConfiguration.getInstance().getLogger().fine("Reading JSON from file " + getFileName() + "...");
			Reader r = new InputStreamReader(new FileInputStream(file.getFile()), StandardCharsets.UTF_8);
			Storage<?> tmpData;

			try {
				tmpData = JSONConfig.GSON.fromJson(r, StringHashMap.class);
			} catch (JsonSyntaxException jse) {
				throw new FileFormatException("JSON", file.getFile(), "", jse);
			}

			if (tmpData != null) {
				this.data = (Storage<Object>) tmpData;
			}
			r.close();
			this.runTreeUpdate();
			UneiConfiguration.getInstance().getLogger().fine("Successfully read.");
		} catch (IOException e) {
			UneiConfiguration.getInstance().getLogger()
					.warning("An error occured while loading JSON file " + getFileName() + ":");
			e.printStackTrace();
			return;
		}
	}

	@Override
	public Set<String> getKeys() {
		return this.data.getKeys();
	}

	@Override
	public boolean contains(String path) {
		PathComponent.PathComponentsList list = PathNavigator.parsePath(path, symType);
		Storage<Object> node = this.getParentMap(list);
		return node.has(list.last().getKey(node.getStorageType()));
	}

	@Override
	public Object get(String path) {
		PathComponent.PathComponentsList list = PathNavigator.parsePath(path, symType);
		Storage<Object> node = this.getParentMap(list);
		Key key = list.last().getKey(node.getStorageType());

		if (!node.has(key)) {
			return null;
		}
		return node.get(key);
	}

	@Override
	public Object tryGet(String path) throws NoFieldException {
		PathComponent.PathComponentsList list = PathNavigator.parsePath(path, symType);
		Storage<Object> node = this.getParentMap(list);
		Key key = list.last().getKey(node.getStorageType());

		if (!node.has(key)) {
			throw new NoFieldException(path, getFile(), "No value is associated to this key");
		}
		return node.get(key);
	}

	@Override
	public JSONConfig getSubSection(PathComponent.PathComponentsList path) {
		if (!this.canAccess()) {
			return null;
		}

		if (path == null || path.isEmpty()) {
			return this;
		}
		PathNavigator<JSONConfig> navigator = new PathNavigator<JSONConfig>(this);

		if (navigator.followPath(path)) {
			return navigator.getCurrentNode();
		}
		return null;
	}

	@Override
	public void set(String path, Object value) {
		if (!this.canAccess()) {
			return;
		}
		PathComponent.PathComponentsList list = PathNavigator.parsePath(path, symType);
		Storage<Object> node = this.getParentMap(list);

		if (value == null) {
			Configuration.removeRecursive(node, list.last().getKey(node.getStorageType()));
		} else {

			if (value instanceof Double) {

				if (((Double) value).isInfinite() || ((Double) value).isNaN()) {
					node.set(list.last().getKey(node.getStorageType()), value.toString());
				} else {
					node.set(list.last().getKey(node.getStorageType()), value);
				}
			} else if (value instanceof Float) {

				if (((Float) value).isInfinite() || ((Float) value).isNaN()) {
					node.set(list.last().getKey(node.getStorageType()), value.toString());
				} else {
					node.set(list.last().getKey(node.getStorageType()), value);
				}
			} else {
				node.set(list.last().getKey(node.getStorageType()), value);
			}
		}
	}

	@Override
	public void setSubSection(String path, IConfiguration value) {
		if (!this.canAccess()) {
			return;
		}

		if (value == null) {
			remove(path);
			return;
		}

		if (!(value instanceof JSONConfig)) {
			// TODO ConfigType conversion
			return;
		}
		PathComponent.PathComponentsList list = PathNavigator.parsePath(path, symType);
		JSONConfig node = this.getParentObj(list);
		Key key = list.last().getKey(node.getType());
		((JSONConfig) value).validate(node, key);
		node.data.set(key, ((JSONConfig) value).data);
	}

	@Override
	public void remove(String path) {
		set(path, null);
	}

	@Override
	public String toFormattedString() {
		StringWriter sw = new StringWriter();
		JsonWriter jw = new JsonWriter(sw);
		jw.setIndent("  ");
		JSONConfig.GSON.toJson(data, data.getClass(), jw);
		String res = null;

		try {
			res = sw.toString();
			jw.close();
			sw.close();
		} catch (IOException e) {
			//
		}
		return res;
	}

	@Override
	public String toMinimizedString() {
		return JSONConfig.GSON.toJson(data, data.getClass());
	}

	@Override
	public String saveToString() {
		return this.toFormattedString();
	}

	@Override
	public void loadFromString(String p_data) {
		if (!this.canAccess()) {
			return;
		}
		this.data.clear();
		Map<?, ?> tmpMap = JSONConfig.GSON.fromJson(p_data, Map.class);

		for (Entry<?, ?> e : tmpMap.entrySet()) {
			this.data.set(new Key(e.getKey()), e.getValue());
		}
	}

	@Override
	public String toString() {
		return "JSONConfig=" + this.data.toString();
	}
}
