package me.unei.configuration.api.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.DumperOptions.LineBreak;
import org.yaml.snakeyaml.DumperOptions.ScalarStyle;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import me.unei.configuration.SavedFile;
import me.unei.configuration.api.IConfiguration;
import me.unei.configuration.api.IYAMLConfiguration;
import me.unei.configuration.api.UntypedStorage;
import me.unei.configuration.api.exceptions.FileFormatException;
import me.unei.configuration.api.fs.PathComponent;
import me.unei.configuration.api.fs.PathNavigator;
import me.unei.configuration.api.fs.PathNavigator.PathSymbolsType;
import me.unei.configuration.formats.AtomicIndexList;
import me.unei.configuration.formats.Storage;
import me.unei.configuration.formats.StorageType;
import me.unei.configuration.formats.StringHashMap;
import me.unei.configuration.formats.Storage.Key;
import me.unei.configuration.plugin.UneiConfiguration;

public final class YAMLConfig extends UntypedStorage<YAMLConfig> implements IYAMLConfiguration {

    public static final String YAML_FILE_EXT = ".yml";
    public static final String YAML_TMP_EXT = ".tmp";
    private static final Yaml BEAUTIFIED_YAML;
    private static final Yaml MINIFIED_YAML;
    
    static
    {
    	DumperOptions dumperOpts = new DumperOptions();
    	dumperOpts.setDefaultFlowStyle(FlowStyle.BLOCK);
    	dumperOpts.setDefaultScalarStyle(ScalarStyle.PLAIN);
    	dumperOpts.setLineBreak(LineBreak.UNIX);
    	dumperOpts.setPrettyFlow(true);
    	dumperOpts.setIndent(2);
    	BEAUTIFIED_YAML = new Yaml(dumperOpts);
    	
    	DumperOptions miniDumperOpts = new DumperOptions();
    	miniDumperOpts.setDefaultFlowStyle(FlowStyle.FLOW);
    	miniDumperOpts.setDefaultScalarStyle(ScalarStyle.PLAIN);
    	miniDumperOpts.setLineBreak(LineBreak.UNIX);
    	miniDumperOpts.setPrettyFlow(false);
    	miniDumperOpts.setIndent(1);
    	MINIFIED_YAML = new Yaml(dumperOpts);
    }

    @Deprecated
    private Map<String, Object> data = new HashMap<String, Object>();
    
    private Storage<Object> nodeData;

    public YAMLConfig(SavedFile file) {
    	this(file, PathSymbolsType.BUKKIT);
    }
    
    public YAMLConfig(SavedFile file, PathSymbolsType symType) {
    	super(file, symType);
    	
    	this.init();
    }
    
    public YAMLConfig(File folder, String fileName) {
        this(folder, fileName, PathSymbolsType.BUKKIT);
    }

    public YAMLConfig(File folder, String fileName, PathSymbolsType symType) {
        this(new SavedFile(folder, fileName, YAMLConfig.YAML_FILE_EXT), symType);
    }

    public YAMLConfig(String data, PathSymbolsType symType) {
        super(new SavedFile(), symType);

        this.init();
        this.loadFromString(data);
    }

    public YAMLConfig(String data) {
    	this(data, PathSymbolsType.BUKKIT);
    }

    private YAMLConfig(YAMLConfig p_parent, String p_nodeName) {
        super(p_parent, p_nodeName);

        this.updateNode();
    }

    public static YAMLConfig getForPath(File folder, String fileName, String path, PathSymbolsType symType) {
        return YAMLConfig.getForPath(new YAMLConfig(folder, fileName, symType), path);
    }

    public static YAMLConfig getForPath(File folder, String fileName, String path) {
        return YAMLConfig.getForPath(new YAMLConfig(folder, fileName), path);
    }

    public static YAMLConfig getForPath(YAMLConfig root, String path) {
        if (root == null) {
            return null;
        }
        return root.getSubSection(path);
    }

    @Override
    public YAMLConfig getRoot() {
        return (YAMLConfig) super.getRoot();
    }

    @Override
	public YAMLConfig getChild(String name) {
        if (!this.canAccess()) {
            return null;
        }
        if (name == null || name.isEmpty()) {
            return this;
        }
        return new YAMLConfig(this, name);
    }

    private Storage<Object> getParentMap(PathComponent.PathComponentsList path)
    {
    	YAMLConfig dir;
    	PathNavigator<YAMLConfig> pn = new PathNavigator<YAMLConfig>(this);
    	PathComponent.PathComponentsList pathList = PathNavigator.cleanPath(path);
    	pathList.removeLast();
    	if (!pn.followPath(pathList))
    	{
    		return nodeData;
    	}
    	dir = pn.getCurrentNode();
		return dir.nodeData;
    }
    
    private YAMLConfig getForPath(PathComponent.PathComponentsList path)
    {
    	YAMLConfig dir;
    	PathNavigator<YAMLConfig> pn = new PathNavigator<YAMLConfig>(this);
    	PathComponent.PathComponentsList pathList = PathNavigator.cleanPath(path);
    	pathList.removeLast();
    	if (!pn.followPath(pathList))
    	{
    		return this;
    	}
    	dir = pn.getCurrentNode();
		return dir;
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
        File tmp = new File(this.file.getFolder(), this.file.getFullName() + YAMLConfig.YAML_TMP_EXT);
        UneiConfiguration.getInstance().getLogger().fine("Writing YAML to file " + getFileName() + "...");
        try {
            Writer w = new OutputStreamWriter(new FileOutputStream(tmp), StandardCharsets.UTF_8);
            YAMLConfig.BEAUTIFIED_YAML.dump(data, w);
            w.close();
            if (this.file.getFile().exists()) {
                UneiConfiguration.getInstance().getLogger().finer("Replacing already present file " + getFileName() + ".");
                this.file.getFile().delete();
            }
            tmp.renameTo(this.file.getFile());
            tmp.delete();
            UneiConfiguration.getInstance().getLogger().fine("Successfully written.");
        } catch (IOException e) {
            UneiConfiguration.getInstance().getLogger().warning("An error occured while saving YAML file " + getFileName() + ":");
            e.printStackTrace();
        }
    }

    @Override
	public void reload() throws FileFormatException {
        if (!this.canAccess()) {
            return;
        }
        if (this.parent != null) {
            this.parent.reload();
            //this.synchronize();
            return;
        }
        if (!this.file.getFile().exists()) {
            this.save();
            return;
        }
        this.nodeData.clear();
        try {
            UneiConfiguration.getInstance().getLogger().fine("Reading YAML from file " + getFileName() + "...");
            Reader r = new InputStreamReader(new FileInputStream(file.getFile()), StandardCharsets.UTF_8);
            Map<?, ?> tmpData;
            try {
            	tmpData = YAMLConfig.BEAUTIFIED_YAML.loadAs(r, Map.class);
            } catch (YAMLException ye) {
            	throw new FileFormatException("YAML", this.file.getFile(), "", ye);
            }
            
            if (tmpData != null && !tmpData.isEmpty()) {
                for (Entry<?, ?> entry : tmpData.entrySet()) {
                    String key = entry.getKey() != null? entry.getKey().toString() : null;
                    this.data.put(key, entry.getValue());
                }
            }
            r.close();
            UneiConfiguration.getInstance().getLogger().fine("Successfully read.");
        } catch (IOException e) {
            UneiConfiguration.getInstance().getLogger().warning("An error occured while loading YAML file " + getFileName() + ":");
            e.printStackTrace();
            return;
        }
    }
    
	@SuppressWarnings("unchecked")
	protected void updateFromParent() {
		if (this.parent != null) {
			Storage<Object> sto = this.parent.nodeData;
			if (sto != null && sto.getStorageType() != StorageType.UNDEFINED) {
				Object me = sto.get(Key.of(sto.getStorageType(), nodeAtomicIndex, nodeName));
				if (me != null && me instanceof Storage) {
					this.nodeData = (Storage<Object>) me;
				} else if (me != null && me instanceof Map) {
					StringHashMap<Object> r = new StringHashMap<Object>();
					r.putAll((Map<String, Object>) me);
					this.nodeData = r;
				} else if (me != null && me instanceof List) {
					AtomicIndexList<Object> r = new AtomicIndexList<Object>();
					r.addAll((List<Object>) me);
					this.nodeData = r;
				} else {
					this.nodeData = new StringHashMap<Object>();
					sto.set(Key.of(sto.getStorageType(), nodeAtomicIndex, nodeName), this.nodeData);
				}
			}
		}
	}
    
    @Override
    public void setType(StorageType newType)
    {
    	if (!this.canAccess())
    	{
    		return;
    	}
    	Storage<Object> cnt;
    	if (newType != null
    			&& newType != StorageType.UNDEFINED
    			&& ((cnt = this.nodeData) == null
    				|| cnt.getStorageType() != newType))
    	{
    		if (cnt.isEmpty())
    		{
    			Storage<Object> newObject;
    			switch (newType)
    			{
    			case MAP:
    				newObject = new StringHashMap<Object>();
    				break;
    				
    			case LIST:
    				newObject = new AtomicIndexList<Object>();
    				break;
    				
    			default:
    				return;
    			}
    			this.childrens.forEach(weakPtr -> {
    				if (weakPtr.get() != null) {
    					weakPtr.get().invalidate();
    					weakPtr.clear();
    				}
    			});
    			this.childrens.clear();
    			this.nodeData = newObject;
    			if (this.parent != null)
    			{
    				Storage<Object> sto = this.parent.nodeData;
					sto.set(Key.of(sto.getStorageType(), nodeAtomicIndex, nodeName), this.nodeData);
    			}
    		}
    	}
    }

    @Override
	public Set<String> getKeys() {
        return this.nodeData.getKeys();
    }

    @Override
	public boolean contains(String path) {
        PathComponent.PathComponentsList list = PathNavigator.parsePath(path, symType);
        Map<String, Object> node = this.getParentMap(list);
        return node.containsKey(list.lastChild());
    }

    @Override
	public Object get(String path) {
        PathComponent.PathComponentsList list = PathNavigator.parsePath(path, symType);
        Map<String, Object> node = this.getParentMap(list);
        return node.get(list.lastChild());
    }

    @Override
    public YAMLConfig getSubSection(PathComponent.PathComponentsList path) {
        if (!this.canAccess()) {
            return null;
        }
        if (path == null || path.isEmpty()) {
            return this;
        }
        PathNavigator<YAMLConfig> navigator = new PathNavigator<YAMLConfig>(this);
        if (navigator.followPath(path)) {
            return navigator.getCurrentNode();
        }
        return null;
    }

    @Override
	public void set(String path, Object value) {
    	PathComponent.PathComponentsList list = PathNavigator.parsePath(path, symType);
    	YAMLConfig node = this.getForPath(list);
    	if (value == null) {
    		node.nodeData.remove(list.lastChild());
    	} else {
    		node.put(list.lastChild(), value);
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
        if (!(value instanceof YAMLConfig)) {
            //TODO ConfigType conversion
            return;
        }
        PathComponent.PathComponentsList list = PathNavigator.parsePath(path, symType);
        YAMLConfig node = this.getForPath(list);
        Key key = list.last().getKey(node.nodeData.getStorageType());
        ((YAMLConfig) value).validate(node, key);
        node.nodeData.set(key, ((YAMLConfig) value).nodeData);
    }

    @Override
	public void remove(String path) {
        set(path, null);
    }
    
    @Override
	public String toFormattedString() {
    	return YAMLConfig.BEAUTIFIED_YAML.dumpAsMap(data);
    }
    
    @Override
	public String toMinimizedString() {
    	return YAMLConfig.MINIFIED_YAML.dumpAsMap(data);
    }

    @Override
	public String saveToString() {
        return this.toFormattedString();
    }

    @Override
	public void loadFromString(String p_data) {
        this.data.clear();
        Map<?, ?> tmpMap = YAMLConfig.MINIFIED_YAML.loadAs(p_data, Map.class);
        for (Entry<?, ?> e : tmpMap.entrySet()) {
            if (e.getKey() instanceof String) {
                this.data.put((String) e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public String toString() {
        return "YAMLConfig=" + this.data.toString();
    }
}
