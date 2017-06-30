package me.unei.configuration.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;

import me.unei.configuration.SavedFile;
import me.unei.configuration.api.fs.PathComponent;
import me.unei.configuration.api.fs.PathComponent.PathComponentsList;
import me.unei.configuration.api.fs.PathNavigator;
import me.unei.configuration.api.fs.PathNavigator.PathSymbolsType;
import me.unei.configuration.plugin.UneiConfiguration;

public class YamlConfig extends GettersInOneConfig<YamlConfig> implements IYamlConfiguration {

    public static final String YAML_FILE_EXT = ".yml";
    public static final String YAML_TMP_EXT = ".tmp";
    private static final Yaml YAML = new Yaml();

    private Map<String, Object> data = new HashMap<String, Object>();

    public YamlConfig(File folder, String fileName) {
        super(new SavedFile(folder, fileName, YamlConfig.YAML_FILE_EXT), PathSymbolsType.BUKKIT);

        this.init();
    }

    public YamlConfig(String data) {
    	super(new SavedFile(), PathSymbolsType.BUKKIT);

        this.init();
        this.loadFromString(data);
    }

    private YamlConfig(YamlConfig p_parent, String p_nodeName) {
    	super(p_parent, p_nodeName);

        this.synchronize();
    }

    public static YamlConfig getForPath(File folder, String fileName, String path) {
        return YamlConfig.getForPath(new YamlConfig(folder, fileName), path);
    }

    public static YamlConfig getForPath(YamlConfig root, String path) {
        if (root == null) {
            return null;
        }
        return root.getSubSection(path);
    }

    @Override
	public YamlConfig getRoot() {
        return (YamlConfig) super.getRoot();
    }


    public YamlConfig getChild(String name) {
        if (!this.canAccess()) {
            return null;
        }
        if (name == null || name.isEmpty()) {
            return this;
        }
        return new YamlConfig(this, name);
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
        File tmp = new File(this.file.getFolder(), this.file.getFileName() + YamlConfig.YAML_TMP_EXT);
        String tmpData = this.saveToString();
        try {
            OutputStream out = new FileOutputStream(tmp);
            out.write(tmpData.getBytes());
            out.close();
            if (this.file.getFile().exists()) {
                this.file.getFile().delete();
            }
            tmp.renameTo(this.file.getFile());
            tmp.delete();
        } catch (IOException e) {
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
            InputStream in = new FileInputStream(this.file.getFile());
            Map<?, ?> tmpData = YamlConfig.YAML.loadAs(in, Map.class);
            if (tmpData != null && !tmpData.isEmpty()) {
                for (Entry<?, ?> entry : tmpData.entrySet()) {
                    String key = entry.getKey() != null? entry.getKey().toString() : null;
                    this.data.put(key, entry.getValue());
                }
            }
            in.close();
            UneiConfiguration.getInstance().getLogger().fine("Successfully read.");
            UneiConfiguration.getInstance().getLogger().finest(tmpData == null? "(null)" : tmpData.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    @SuppressWarnings("unchecked")
    protected void synchronize() {
        YamlConfig currentNode = this.getRoot();
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
        if (path == null || path.isEmpty()) {
            if (this.parent != null) {
                return this.parent.data.containsKey(this.nodeName);
            } else {
                return true;
            }
        }
        PathNavigator<YamlConfig> navigator = new PathNavigator<YamlConfig>(this);
        if (navigator.navigate(path, this.symType)) {
            YamlConfig node = navigator.getCurrentNode();
            return node.contains("");
        }
        return false;
    }

    public Object get(String path) {
        if (path == null || path.isEmpty()) {
            if (this.parent != null) {
                return this.parent.data.get(this.nodeName);
            } else {
                return this.data;
            }
        }
        PathNavigator<YamlConfig> navigator = new PathNavigator<YamlConfig>(this);
        if (navigator.navigate(path, this.symType)) {
            return navigator.getCurrentNode().get("");
        }
        return null;
    }

    @Override
	public YamlConfig getSubSection(PathComponent.PathComponentsList path) {
    	if (!this.canAccess()) {
    		return null;
    	}
        if (path == null || path.isEmpty()) {
            return this;
        }
        PathNavigator<YamlConfig> navigator = new PathNavigator<YamlConfig>(this);
        if (navigator.followPath(path)) {
            return navigator.getCurrentNode();
        }
        return null;
    }

    public void set(String path, Object value) {
        if (path == null || path.isEmpty()) {
            if (this.parent != null) {
                if (value == null) {
                    this.parent.data.remove(this.nodeName);
                } else {
                    this.parent.data.put(this.nodeName, value);
                }
                this.parent.propagate();
            }
            return;
        }
        PathNavigator<YamlConfig> navigator = new PathNavigator<YamlConfig>(this);
        if (navigator.navigate(path, this.symType)) {
            navigator.getCurrentNode().set("", value);
        }
    }

    public void setSubSection(String path, IConfiguration value) {
        if (!(value instanceof YamlConfig)) {
            //TODO ConfigType conversion
            return;
        }
        set(path, ((YamlConfig) value).data);
    }

    public void remove(String path) {
        set(path, null);
    }

    public String saveToString() {
        return YamlConfig.YAML.dumpAs(this.data, null, FlowStyle.BLOCK);
    }

    public void loadFromString(String p_data) {
        this.data.clear();
        Map<?, ?> tmpMap = YamlConfig.YAML.loadAs(p_data, Map.class);
        for (Entry<?, ?> e : tmpMap.entrySet()) {
            if (e.getKey() instanceof String) {
                this.data.put((String) e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public String toString() {
        return "YamlConfig=" + this.data.toString();
    }
}
