package me.unei.configuration.api;

import java.io.*;
import java.util.*;

import me.unei.configuration.SavedFile;
import me.unei.configuration.plugin.UneiConfiguration;
import org.yaml.snakeyaml.Yaml;

public class YamlConfig implements IYamlConfiguration {

    public static final String YAML_FILE_EXT = ".yml";
    public static final String YAML_TMP_EXT = ".tmp";
    private static final Yaml YAML = new Yaml();

    private Map<String, Object> data = null;

    private SavedFile configFile = null;
    private String datum = null;

    private String fullPath = "";
    private String nodeName = "";
    private YamlConfig parent = null;

    public YamlConfig(File folder, String fileName) {
        this.configFile = new SavedFile(folder, fileName, YamlConfig.YAML_FILE_EXT);

        this.init();
    }

    public YamlConfig(String datum) {
        this.datum = datum;

        this.init();
    }

    YamlConfig(File folder, String fileName, String p_tagName) {
        this(new YamlConfig(folder, fileName), p_tagName);
    }

    YamlConfig(YamlConfig p_parent, String p_nodeName) {
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

    private static String buildPath(String parent, String child) {
        if (parent == null || parent.isEmpty() || child == null) {
            return child;
        }
        return parent + "." + child;
    }

    private static String[] splitPath(String path) {
        return path.split("\\.");
    }

    public static YamlConfig getForPath(File folder, String fileName, String path) {
        return YamlConfig.getForPath(new YamlConfig(folder, fileName), path);
    }

    public static YamlConfig getForPath(YamlConfig root, String path) {
        if (path == null || path.isEmpty()) {
            return root;
        }
        if (!path.contains(".")) {
            return root.getSubSection(path);
        }
        YamlConfig last = root;
        for (String part : YamlConfig.splitPath(path)) {
            last = last.getSubSection(part);
        }
        return last;
    }

    public SavedFile getFile() {
        return this.configFile;
    }

    public String getFileName() {
        if (this.configFile == null && this.parent != null) {
            return this.parent.getFileName();
        }
        return this.configFile != null? this.configFile.getFile().getName() : null;
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
        } else if (this.configFile != null) {
            return this.configFile.canAccess();
        }
        return true;
    }

    public void lock() {
        if (this.parent != null) {
            this.parent.lock();
        } else if (this.configFile != null) {
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
        return this.parent;
    }

    public void save() {
        if (!this.canAccess()) {
            return;
        }
        if (this.parent != null) {
            this.parent.save();
            return;
        }
        File tmp = new File(this.configFile.getFolder(), this.configFile.getFileName() + YamlConfig.YAML_TMP_EXT);
        String tmpData = YAML.dump(this.data);
        try {
            OutputStream out = new FileOutputStream(tmp);
            out.write(tmpData.getBytes());
            out.close();
            if (this.configFile.getFile().exists()) {
                this.configFile.getFile().delete();
            }
            tmp.renameTo(this.configFile.getFile());
            this.datum = tmpData;
            tmp.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public void reload() {
        if (!this.canAccess()) {
            return;
        }
        if (this.parent != null) {
            this.parent.reload();
        } else if (this.configFile != null) {
            if (!this.configFile.getFile().exists()) {
                this.data = new HashMap<String, Object>();
                this.save();
                return;
            }
            Object tmpData;
            try {
                UneiConfiguration.getInstance().getLogger().fine("Reading YAML from file " + getFileName() + "...");
                InputStream in = new FileInputStream(this.configFile.getFile());
                tmpData = YAML.load(in);
                in.close();
                UneiConfiguration.getInstance().getLogger().fine("OK : " + (tmpData == null? "(null)" : tmpData.toString()));
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            if (tmpData != null && tmpData instanceof Map) {
                this.data = (Map<String, Object>) tmpData;
                this.datum = YAML.dump(this.data);
            } else {
                this.data = new HashMap<String, Object>();
            }
        } else if (this.datum != null) {
            Object tmpData;

            UneiConfiguration.getInstance().getLogger().fine("Reading YAML from provided string...");
            tmpData = YAML.load(this.datum);
            UneiConfiguration.getInstance().getLogger().fine("OK : " + (tmpData == null? "(null)" : tmpData.toString()));

            if (tmpData != null && tmpData instanceof Map) {
                this.data = (Map<String, Object>) tmpData;
                this.datum = YAML.dump(this.data);
            } else {
                this.data = new HashMap<String, Object>();
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

    @SuppressWarnings("unchecked")
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

    @SuppressWarnings("unchecked")
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

    public String toYAMLString() {
        return YAML.dump(this.data);
    }

    public String toString() {
        return "YamlConfig : " + this.data.toString();
    }
}
