package me.unei.configuration.api.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
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
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;

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

public class YAMLConfig2 extends UntypedStorage<YAMLConfig2> implements IYAMLConfiguration {

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
	
	protected YAMLRootConfig root;

    protected YAMLConfig2(SavedFile file, PathSymbolsType symType) {
    	super(file, symType);
    	
    	this.init();
    }
    
    public YAMLConfig2(YAMLConfig2 p_parent, String p_nodeName) {
        super(p_parent, p_nodeName);
        
        if (p_parent == null) {
        	throw new IllegalArgumentException("parent must not be null");
        }
        
        this.root = p_parent.root;
    }

    public YAMLConfig2(YAMLConfig2 p_parent, int p_index) {
        super(p_parent, p_index);
        
        if (p_parent == null) {
        	throw new IllegalArgumentException("parent must not be null");
        }
        
        this.root = p_parent.root;
    }

    public static YAMLConfig2 getForPath(File folder, String fileName, String path, PathSymbolsType symType) {
        return YAMLConfig2.getForPath(new YAMLRootConfig(folder, fileName, symType), path);
    }

    public static YAMLConfig2 getForPath(File folder, String fileName, String path) {
        return YAMLConfig2.getForPath(new YAMLRootConfig(folder, fileName), path);
    }

    public static YAMLConfig2 getForPath(SavedFile file, String path, PathSymbolsType symType) {
        return YAMLConfig2.getForPath(new YAMLRootConfig(file, symType), path);
    }

    public static YAMLConfig2 getForPath(SavedFile file, String path) {
        return YAMLConfig2.getForPath(new YAMLRootConfig(file), path);
    }

    public static YAMLConfig2 getForPath(YAMLConfig2 root, String path) {
        if (path == null || path.isEmpty()) {
            return root;
        }
        PathNavigator<YAMLConfig2> navigator = new PathNavigator<YAMLConfig2>(root);
        navigator.navigate(path, root.symType);
        return navigator.getCurrentNode();
    }
    
    @SuppressWarnings("unchecked")
	protected Storage<Object> getNodeData() {
    	if (this.parent != null) {
    		Storage<Object> parentSto = this.parent.getNodeData();
    		if (parentSto != null) {
    			Object o = parentSto.get(Key.of(parentSto.getStorageType(), nodeAtomicIndex, nodeName));
    			if (o == null) {
    				Storage<Object> tmp = new StringHashMap<Object>();
    				parentSto.set(Key.of(parentSto.getStorageType(), nodeAtomicIndex, nodeName), tmp);
    				return tmp;
    			} else if (o instanceof Storage) {
    				return (Storage<Object>) o;
    			} else if (o instanceof Map) {
    				return new StringHashMap<Object>((Map<?, ?>) o, 0);
    			}
    		}
    	}
    	return null;
    }
    
	protected void setNodeData(Storage<Object> newObject) {
    	if (this.parent != null) {
    		Storage<Object> parentSto = this.parent.getNodeData();
    		if (parentSto != null) {
				parentSto.set(Key.of(parentSto.getStorageType(), nodeAtomicIndex, nodeName), newObject);
    		}
    	}
    }
    
    public void changeType(StorageType newType)
    {
    	Storage<Object> cnt;
    	if (newType != null
    			&& newType != StorageType.UNDEFINED
    			&& ((cnt = getNodeData()) == null
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
    			setNodeData(newObject);
    		}
    	}
    }

    @Override
    public YAMLConfig2 getRoot() {
    	if (this.root != null) {
    		return this.root;
    	}
        return (this.root = (YAMLRootConfig) super.getRoot());
    }

    @Override
	public YAMLConfig2 getChild(String name) {
        if (!this.canAccess()) {
            return null;
        }
        if (name == null || name.isEmpty()) {
            return this;
        }
        return new YAMLConfig2(this, name);
    }

    public YAMLConfig2 getAt(int index) {
        if (!this.canAccess()) {
            return null;
        }
        if (index >= 0) {
            return this;
        }
        return new YAMLConfig2(this, index);
    }

    private Storage<Object> getParentMap(PathComponent.PathComponentsList path) {
    	YAMLConfig2 dir;
        PathNavigator<YAMLConfig2> pn = new PathNavigator<YAMLConfig2>(this);
        PathComponent.PathComponentsList pathList = PathNavigator.cleanPath(path);
        pathList.removeLast();
        if (!pn.followPath(pathList)) {
            return getNodeData();
        }
        dir = pn.getCurrentNode();
        return dir.getNodeData();
    }

    @Override
	public void reload() throws FileFormatException {
		this.root.reload();
    }

    @Override
	public void save() {
		this.root.save();
    }
    
    @Override
    protected void propagate() {
        if (this.parent != null) {
            this.parent.propagate();
        }
    }

    @Override
    public Set<String> getKeys() {
    	return this.getNodeData().getKeys();
    }

    public boolean contains(String path) {
        PathComponent.PathComponentsList list = PathNavigator.parsePath(path, symType);
        Storage<Object> node = this.getParentMap(list);
        return node.has(list.last().getKey(node.getStorageType()));
    }

    public Object get(String path) {
        PathComponent.PathComponentsList list = PathNavigator.parsePath(path, symType);
        Storage<Object> node = this.getParentMap(list);
        return node.get(list.last().getKey(node.getStorageType()));
    }

    @Override
    public YAMLConfig2 getSubSection(PathComponent.PathComponentsList path) {
        if (!this.canAccess()) {
            return null;
        }
        if (path == null || path.isEmpty()) {
            return this;
        }
        PathNavigator<YAMLConfig2> navigator = new PathNavigator<YAMLConfig2>(this);
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
    	if (value == null) {
    		this.remove(path);
    		return;
    	}
    	if (value instanceof IConfiguration) {
    		setSubSection(path, (IConfiguration) value);
    		return;
    	}
        PathComponent.PathComponentsList list = PathNavigator.parsePath(path, symType);
        Storage<Object> node = this.getParentMap(list);
        node.set(list.last().getKey(node.getStorageType()), value);
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
        if (!(value instanceof YAMLConfig2)) {
            //TODO ConfigType conversion
            return;
        }
        PathComponent.PathComponentsList list = PathNavigator.parsePath(path, symType);
        Storage<Object> node = this.getParentMap(list);
        Key key = list.last().getKey(node.getStorageType());
        ((YAMLConfig2) value).nodeAtomicIndex = key.getKeyAtomicInt();
        node.set(key, ((YAMLConfig2) value).getNodeData());
    }

	public void remove(String key) {
        if (!this.canAccess()) {
            return;
        }
        PathComponent.PathComponentsList list = PathNavigator.parsePath(key, symType);
        Storage<Object> node = this.getParentMap(list);
        node.remove(list.last().getKey(node.getStorageType()));
    }
    
    @Override
	public String toFormattedString() {
    	Storage<Object> data = getNodeData();
    	if (data.getStorageType() == StorageType.MAP) {
    		return YAMLConfig2.BEAUTIFIED_YAML.dumpAsMap(data);
    	}
    	return YAMLConfig2.BEAUTIFIED_YAML.dump(data);
    }
    
    @Override
	public String toMinimizedString() {
    	Storage<Object> data = getNodeData();
    	if (data.getStorageType() == StorageType.MAP) {
    		return YAMLConfig2.MINIFIED_YAML.dumpAsMap(data);
    	}
    	return YAMLConfig2.MINIFIED_YAML.dump(data);
    }

    @Override
	public String saveToString() {
        return this.toFormattedString();
    }

    @Override
	public void loadFromString(String p_data) {
    	Storage<Object> data = this.getNodeData();
    	data.clear();
    	Node rootNode = YAMLConfig2.MINIFIED_YAML.compose(new StringReader(p_data));
    	if (rootNode.getTag().equals(Tag.SEQ)) {
    		List<?> tmpMap = YAMLConfig2.MINIFIED_YAML.loadAs(p_data, List.class);
            for (Object e : tmpMap) {
            	data.set(new Key(data.size()), e);
            }
    	} else if (rootNode.getTag().equals(Tag.MAP)) {
            Map<?, ?> tmpMap = YAMLConfig2.MINIFIED_YAML.loadAs(p_data, Map.class);
            for (Entry<?, ?> e : tmpMap.entrySet()) {
            	data.set(new Key(e.getKey()), e.getValue());
            }
    	}
    }

    @Override
    public String toString() {
        return "YAMLConfig=" + this.getNodeData().toString();
    }
    
    protected static final class YAMLRootConfig extends YAMLConfig2
    {
    	private StringHashMap<Object> data = new StringHashMap<Object>();
    	
    	public YAMLRootConfig(SavedFile file, PathSymbolsType symType)
    	{
    		super(file, symType);

    		this.root = this;
        	this.init();
    	}
        
        public YAMLRootConfig(SavedFile file) {
        	this(file, PathSymbolsType.BUKKIT);
        }

    	public YAMLRootConfig(File folder, String fileName, PathSymbolsType symType) {
            this(new SavedFile(folder, fileName, NBTConfig2.NBT_FILE_EXT), symType);
        }

        public YAMLRootConfig(File folder, String fileName) {
            this(folder, fileName, PathSymbolsType.BUKKIT);
        }
    	
    	@Override
    	public YAMLRootConfig getRoot() {
    		return this;
    	}
    	
    	@Override
    	public StorageType getType() {
    		return this.data.getStorageType();
    	}
    	
    	@Override
    	public void setType(StorageType type) {
    		if (type != this.getType()) {
    			throw new UnsupportedOperationException("Cannot change the type of a NBT root Tag to anything but " + this.getType().name());
    		}
    	}
    	
    	@Override
    	protected StringHashMap<Object> getNodeData() {
    		return this.data;
    	}

    	@Override
    	public void reload() throws FileFormatException {
    		if (!this.canAccess()) {
    			return;
    		}
            if (!this.file.getFile().exists()) {
                this.save();
                return;
            }
            this.data.clear();
            try {
                UneiConfiguration.getInstance().getLogger().fine("Reading YAML from file " + getFileName() + "...");
                Reader r = new InputStreamReader(new FileInputStream(file.getFile()), StandardCharsets.UTF_8);
                Map<?, ?> tmpData;
                try {
                	tmpData = YAMLConfig2.BEAUTIFIED_YAML.loadAs(r, Map.class);
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

    	@Override
    	public void save() {
            if (!this.canAccess()) {
                return;
            }
            if (this.file.getFile() == null) {
                return;
            }
            File tmp = new File(this.file.getFolder(), this.file.getFullName() + YAMLConfig2.YAML_TMP_EXT);
            UneiConfiguration.getInstance().getLogger().fine("Writing YAML to file " + getFileName() + "...");
            try {
                Writer w = new OutputStreamWriter(new FileOutputStream(tmp), StandardCharsets.UTF_8);
                YAMLConfig2.BEAUTIFIED_YAML.dump(data, w);
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
    }
}
