package me.unei.configuration.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Charsets;

import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;

import me.unei.configuration.SavedFile;
import me.unei.configuration.api.fs.PathComponent;
import me.unei.configuration.api.fs.PathComponent.PathComponentsList;
import me.unei.configuration.api.fs.PathNavigator;
import me.unei.configuration.api.fs.PathNavigator.PathSymbolsType;
import me.unei.configuration.plugin.UneiConfiguration;

public class YAMLConfig extends UntypedStorage<YAMLConfig> implements IYAMLConfiguration {

    public static final String YAML_FILE_EXT = ".yml";
    public static final String YAML_TMP_EXT = ".tmp";
    private static final Yaml YAML = new Yaml();

    private Map<String, Object> data = new HashMap<String, Object>();

    public YAMLConfig(File folder, String fileName) {
    	this(folder, fileName, PathSymbolsType.BUKKIT);
    }
    
    public YAMLConfig(File folder, String fileName, PathSymbolsType symType) {
        super(new SavedFile(folder, fileName, YAMLConfig.YAML_FILE_EXT), symType);

        this.init();
    }

    public YAMLConfig(String data) {
    	super(new SavedFile(), PathSymbolsType.BUKKIT);

        this.init();
        this.loadFromString(data);
    }

    private YAMLConfig(YAMLConfig p_parent, String p_nodeName) {
    	super(p_parent, p_nodeName);

        this.synchronize();
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


    public YAMLConfig getChild(String name) {
        if (!this.canAccess()) {
            return null;
        }
        if (name == null || name.isEmpty()) {
            return this;
        }
        return new YAMLConfig(this, name);
    }
    
    private Map<String, Object> getParentMap(PathComponent.PathComponentsList path)
    {
    	YAMLConfig dir;
    	PathNavigator<YAMLConfig> pn = new PathNavigator<YAMLConfig>(this);
    	PathComponent.PathComponentsList pathList = PathNavigator.cleanPath(path);
    	pathList.removeLast();
    	if (!pn.followPath(pathList))
    	{
    		return new HashMap<String, Object>(data);
    	}
    	dir = pn.getCurrentNode();
		return new HashMap<String, Object>(dir.data);
    }
    
    private void setParentMap(PathComponent.PathComponentsList path, Map<String, Object> map)
    {
    	YAMLConfig dir;
    	PathNavigator<YAMLConfig> pn = new PathNavigator<YAMLConfig>(this);
    	PathComponent.PathComponentsList pathList = PathNavigator.cleanPath(path);
    	pathList.removeLast();
    	if (!pn.followPath(pathList))
    	{
    		this.data = map;
    		this.propagate();
    		return;
    	}
    	dir = pn.getCurrentNode();
    	if (dir != null)
    	{
    		dir.data = map;
    		dir.propagate();
    	}
    }
    
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
        File tmp = new File(this.file.getFolder(), this.file.getFileName() + YAMLConfig.YAML_TMP_EXT);
        UneiConfiguration.getInstance().getLogger().fine("Writing YAML to file " + getFileName() + "...");
        String tmpData = this.saveToString();
        try {
            Writer w = new OutputStreamWriter(new FileOutputStream(tmp), Charsets.UTF_8);
            w.write(tmpData);
            w.flush();
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

    public void reload() {
        if (!this.canAccess()) {
            return;
        }
        if (this.parent != null) {
            this.parent.reload();
            this.synchronize();
            return;
        }
        if (!this.file.getFile().exists()) {
            this.save();
            return;
        }
        this.data.clear();
        try {
            UneiConfiguration.getInstance().getLogger().fine("Reading YAML from file " + getFileName() + "...");
            Reader r = new InputStreamReader(new FileInputStream(file.getFile()), Charsets.UTF_8);
            Map<?, ?> tmpData = YAMLConfig.YAML.loadAs(r, Map.class);
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
	@SuppressWarnings("unchecked")
    protected void synchronize() {
        YAMLConfig currentNode = this.getRoot();
        Map<String, Object> currentData = currentNode.data;

        PathComponentsList path = this.fullPath.clone();
        path = PathNavigator.cleanPath(path);
        for (PathComponent component : path) {
            switch(component.getType()) {
                case ROOT:
                    currentNode = currentNode.getRoot();
                    currentData = currentNode.data;
                    break;

                case PARENT:
                    currentNode = currentNode.getParent();
                    currentData = currentNode.data;
                    break;

                case CHILD:
                    currentNode = null;
                    Object childData = currentData.get(component.getValue());
                    if (childData != null && childData instanceof Map) {
                        currentData = (Map<String, Object>) childData;
                    } else {
                        return;
                    }
                    break;
            }
        }
        this.data = currentData;
    }

    @Override
	protected void propagate() {
        if (this.parent != null) {
            this.parent.data.put(this.nodeName, this.data);
            this.parent.propagate();
        }
    }

    public Set<String> getKeys() {
        return this.data.keySet();
    }

    public boolean contains(String path) {
    	PathComponent.PathComponentsList list = PathNavigator.parsePath(path, symType);
    	Map<String, Object> node = this.getParentMap(list);
    	return node.containsKey(list.lastChild());
    }

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

    public void set(String path, Object value) {
    	PathComponent.PathComponentsList list = PathNavigator.parsePath(path, symType);
    	Map<String, Object> node = this.getParentMap(list);
    	if (value == null) {
    		node.remove(list.lastChild());
    	} else {
    		node.put(list.lastChild(), value);
    	}
    	this.setParentMap(list, node);
    }

    public void setSubSection(String path, IConfiguration value) {
        if (!(value instanceof YAMLConfig)) {
            //TODO ConfigType conversion
            return;
        }
        set(path, ((YAMLConfig) value).data);
    }

    public void remove(String path) {
        set(path, null);
    }

    public String saveToString() {
        return YAMLConfig.YAML.dumpAs(this.data, null, FlowStyle.BLOCK);
    }

    public void loadFromString(String p_data) {
        this.data.clear();
        Map<?, ?> tmpMap = YAMLConfig.YAML.loadAs(p_data, Map.class);
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
