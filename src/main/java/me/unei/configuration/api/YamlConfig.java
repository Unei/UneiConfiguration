package me.unei.configuration.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.yaml.snakeyaml.Yaml;

import me.unei.configuration.SavedFile;
import me.unei.configuration.api.fs.PathComponent;
import me.unei.configuration.api.fs.PathNavigator;
import me.unei.configuration.plugin.UneiConfiguration;

public class YamlConfig implements IYamlConfiguration {

    public static final String YAML_FILE_EXT = ".yml";
    public static final String YAML_TMP_EXT = ".tmp";
    private static final Yaml YAML = new Yaml();

    private Map<String, Object> data = null;

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

        this.init();
    }

    private void init() {
        if (this.parent != null) {
            this.parent.init();
        } else {
            this.configFile.init();
            this.reload();
        }
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
        if (path == null || path.isEmpty()) {
            return root;
        }
        PathNavigator navigator = new PathNavigator(root);
        navigator.navigate(path);
        return (YamlConfig) navigator.getCurrentNode();
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

    public IConfiguration getRoot() {
        if (this.parent != null) {
            return this.parent.getRoot();
        }
        return this;
    }

    public IConfiguration getParent() {
        if (this.parent != null) {
            return this.parent;
        }
        return this;
    }

    public YamlConfig getChild(String name) {
        if (!this.configFile.canAccess()) {
            return null;
        }

        YamlConfig sub = new YamlConfig(this, name);
        return sub;
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
        String tmpData = YamlConfig.YAML.dump(this.data);
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
        } else {
            if (!this.configFile.getFile().exists()) {
                this.data = new HashMap<String, Object>();
                this.save();
                return;
            }
            Map<?, ?> tmpData;
            try {
                UneiConfiguration.getInstance().getLogger().fine("Reading YAML from file " + getFileName() + "...");
                InputStream in = new FileInputStream(this.configFile.getFile());
                tmpData = YamlConfig.YAML.loadAs(in, Map.class);
                in.close();
                UneiConfiguration.getInstance().getLogger().fine("Successfully read.");
                UneiConfiguration.getInstance().getLogger().finest(tmpData == null? "(null)" : tmpData.toString());
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            if (this.data == null) {
                this.data = new HashMap<String, Object>();
            }
            this.data.clear();
            if (tmpData != null && !tmpData.isEmpty()) {
                for (Entry<?, ?> e : tmpData.entrySet()) {
                    this.data.put(e.getKey().toString(), e.getValue());
                }
            }
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

    public String getString(String key) {
        try {
            return (String) this.data.get(key);
        } catch (Exception e) {
            return null;
        }
    }

    public double getDouble(String key) {
        try {
            return ((Number) this.data.get(key)).doubleValue();
        } catch (Exception e) {
            return 0.0D;
        }
    }

    public boolean getBoolean(String key) {
        try {
            return ((Boolean) this.data.get(key)).booleanValue();
        } catch (Exception e) {
            return false;
        }
    }

    public byte getByte(String key) {
        try {
            return ((Number) this.data.get(key)).byteValue();
        } catch (Exception e) {
            return (byte) 0;
        }
    }

    public float getFloat(String key) {
        try {
            return ((Number) this.data.get(key)).floatValue();
        } catch (Exception e) {
            return 0.0F;
        }
    }

    public int getInteger(String key) {
        try {
            return ((Number) this.data.get(key)).intValue();
        } catch (Exception e) {
            return 0;
        }
    }

    public long getLong(String key) {
        try {
            return ((Number) this.data.get(key)).longValue();
        } catch (Exception e) {
            return 0L;
        }
    }

    public List<Byte> getByteList(String key) {
        try {
            List<Byte> bList = new ArrayList<Byte>();
            List<?> oList = (List<?>) this.data.get(key);
            for (Object value : oList) bList.add(((Number) value).byteValue());
            return bList;
        } catch (Exception e) {
            return null;
        }
    }

    public List<Integer> getIntegerList(String key) {
        try {
            List<Integer> iList = new ArrayList<Integer>();
            List<?> oList = (List<?>) this.data.get(key);
            for (Object value : oList) iList.add(((Number) value).intValue());
            return iList;
        } catch (Exception e) {
            return null;
        }
    }

    public YamlConfig getSubSection(String path) {
        if (!this.configFile.canAccess()) {
            return null;
        }

        YamlConfig sub = new YamlConfig(this, path);
        return sub;
    }

    public void set(String key, Object value) {
        if (value == null) {
            this.data.remove(key);
        } else {
            this.data.put(key, value);
        }
    }

    public void setString(String key, String value) {
        set(key, value);
    }

    public void setDouble(String key, double value) {
        set(key, value);
    }

    public void setBoolean(String key, boolean value) {
        set(key, value);
    }

    public void setByte(String key, byte value) {
        set(key, value);
    }

    public void setFloat(String key, float value) {
        set(key, value);
    }

    public void setInteger(String key, int value) {
        set(key, value);
    }

    public void setLong(String key, long value) {
        set(key, value);
    }

    public void setByteList(String key, List<Byte> value) {
        set(key, value);
    }

    public void setIntegerList(String key, List<Integer> value) {
        set(key, value);
    }

    public void setSubSection(String path, IConfiguration value) {
        if (!this.configFile.canAccess()) {
            return;
        }
        if (!(value instanceof YamlConfig)) {
            //TODO ConfigType conversion
            return;
        }
        YamlConfig cfg = (YamlConfig) value;
        this.data.put(path, cfg.data);
    }

    public void remove(String key) {
        this.data.remove(key);
    }

    public String saveToString() {
        return YamlConfig.YAML.dump(this.data);
    }

    public void loadFromString(String p_data) {
        if (!this.canAccess()) {
            return;
        }
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
