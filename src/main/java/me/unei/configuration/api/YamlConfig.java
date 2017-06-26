package me.unei.configuration.api;

import me.unei.configuration.SavedFile;
import me.unei.configuration.api.fs.PathComponent;
import me.unei.configuration.api.fs.PathComponent.PathComponentsList;
import me.unei.configuration.api.fs.PathNavigator;
import me.unei.configuration.plugin.UneiConfiguration;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class YamlConfig implements IYamlConfiguration {

    public static final String YAML_FILE_EXT = ".yml";
    public static final String YAML_TMP_EXT = ".tmp";
    private static final Yaml YAML = new Yaml();

    private Map<String, Object> data = new HashMap<String, Object>();

    private SavedFile configFile = null;

    private String fullPath = "";
    private String nodeName = "";
    private YamlConfig parent = null;

    public YamlConfig(File folder, String fileName) {
        this.configFile = new SavedFile(folder, fileName, YamlConfig.YAML_FILE_EXT);

        this.init();
    }

    public YamlConfig(String data) {
        this.configFile = new SavedFile();

        this.init();
        this.loadFromString(data);
    }

    private YamlConfig(YamlConfig p_parent, String p_nodeName) {
        this.parent = p_parent;
        this.nodeName = p_nodeName;
        this.fullPath = YamlConfig.buildPath(p_parent.fullPath, p_nodeName);

        this.configFile = this.parent.configFile;
        this.synchronize();
    }

    private void init() {
        if (this.parent != null) {
            this.parent.init();
            this.synchronize();
            return;
        }
        this.configFile.init();
        this.reload();
    }

    private static String buildPath(String path, String child) {
        if (path == null || path.isEmpty() || child == null) {
            return PathComponent.escapeComponent(child);
        }
        return path + PathNavigator.PATH_SEPARATOR + PathComponent.escapeComponent(child);
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

    public SavedFile getFile() {
        return this.configFile;
    }

    public String getFileName() {
        if (this.parent != null) {
            return this.parent.getFileName();
        }
        return this.configFile.getFileName();
    }

    public String getName() {
        return this.nodeName;
    }

    public String getCurrentPath() {
        return this.fullPath;
    }

    public boolean canAccess() {
        if (this.parent != null) {
            return this.parent.canAccess();
        }
        return this.configFile.canAccess();
    }

    public void lock() {
        if (this.parent != null) {
            this.parent.lock();
        } else {
            this.configFile.lock();
        }
    }

    public YamlConfig getRoot() {
        if (this.parent != null) {
            return this.parent.getRoot();
        }
        return this;
    }

    public YamlConfig getParent() {
        if (this.parent != null) {
            return this.parent;
        }
        return this;
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
        if (this.configFile.getFile() == null) {
            return;
        }
        File tmp = new File(this.configFile.getFolder(), this.configFile.getFileName() + YamlConfig.YAML_TMP_EXT);
        String tmpData = this.saveToString();
        try {
            OutputStream out = new FileOutputStream(tmp);
            out.write(tmpData.getBytes());
            out.close();
            if (this.configFile.getFile().exists()) {
                this.configFile.getFile().delete();
            }
            tmp.renameTo(this.configFile.getFile());
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
        if (!this.configFile.getFile().exists()) {
            this.save();
            return;
        }
        this.data.clear();
        try {
            UneiConfiguration.getInstance().getLogger().fine("Reading YAML from file " + getFileName() + "...");
            InputStream in = new FileInputStream(this.configFile.getFile());
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

        PathComponentsList path = PathNavigator.parsePath(this.fullPath);
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
        PathNavigator navigator = new PathNavigator(this);
        if (navigator.navigate(path)) {
            YamlConfig node = (YamlConfig) navigator.getCurrentNode();
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
        PathNavigator navigator = new PathNavigator(this);
        if (navigator.navigate(path)) {
            return ((YamlConfig) navigator.getCurrentNode()).get("");
        }
        return null;
    }

    public String getString(String path) {
        try {
            return (String) get(path);
        } catch (Exception e) {
            return null;
        }
    }

    public double getDouble(String path) {
        try {
            return ((Number) get(path)).doubleValue();
        } catch (Exception e) {
            return 0.0D;
        }
    }

    public boolean getBoolean(String path) {
        try {
            return ((Boolean) get(path)).booleanValue();
        } catch (Exception e) {
            return false;
        }
    }

    public byte getByte(String path) {
        try {
            return ((Number) get(path)).byteValue();
        } catch (Exception e) {
            return (byte) 0;
        }
    }

    public float getFloat(String path) {
        try {
            return ((Number) get(path)).floatValue();
        } catch (Exception e) {
            return 0.0F;
        }
    }

    public int getInteger(String path) {
        try {
            return ((Number) get(path)).intValue();
        } catch (Exception e) {
            return 0;
        }
    }

    public long getLong(String path) {
        try {
            return ((Number) get(path)).longValue();
        } catch (Exception e) {
            return 0L;
        }
    }

    public List<Byte> getByteList(String path) {
        try {
            List<Byte> list = new ArrayList<Byte>();
            for (Object value : (List<?>) get(path)) {
                list.add(((Number) value).byteValue());
            }
            return list;
        } catch (Exception e) {
            return null;
        }
    }

    public List<Integer> getIntegerList(String path) {
        try {
            List<Integer> list = new ArrayList<Integer>();
            for (Object value : (List<?>) get(path)) {
                list.add(((Number) value).intValue());
            }
            return list;
        } catch (Exception e) {
            return null;
        }
    }

    public YamlConfig getSubSection(String path) {
        if (path == null || path.isEmpty()) {
            return this;
        }
        PathNavigator navigator = new PathNavigator(this);
        if (navigator.navigate(path)) {
            return (YamlConfig) navigator.getCurrentNode();
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
        PathNavigator navigator = new PathNavigator(this);
        if (navigator.navigate(path)) {
            ((YamlConfig) navigator.getCurrentNode()).set("", value);
        }
    }

    public void setString(String path, String value) {
        set(path, value);
    }

    public void setDouble(String path, double value) {
        set(path, value);
    }

    public void setBoolean(String path, boolean value) {
        set(path, value);
    }

    public void setByte(String path, byte value) {
        set(path, value);
    }

    public void setFloat(String path, float value) {
        set(path, value);
    }

    public void setInteger(String path, int value) {
        set(path, value);
    }

    public void setLong(String path, long value) {
        set(path, value);
    }

    public void setByteList(String path, List<Byte> value) {
        set(path, value);
    }

    public void setIntegerList(String path, List<Integer> value) {
        set(path, value);
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
