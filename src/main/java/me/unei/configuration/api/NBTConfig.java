package me.unei.configuration.api;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;

import me.unei.configuration.SavedFile;
import me.unei.configuration.api.format.INBTCompound;
import me.unei.configuration.api.fs.PathComponent;
import me.unei.configuration.api.fs.PathComponent.PathComponentsList;
import me.unei.configuration.api.fs.PathNavigator;
import me.unei.configuration.formats.nbtproxy.NBTProxyCST;
import me.unei.configuration.formats.nbtproxy.NBTProxyCompound;
import me.unei.configuration.plugin.UneiConfiguration;

public final class NBTConfig implements INBTConfiguration {

    public static final String NBT_FILE_EXT = ".dat";
    public static final String NBT_TMP_EXT = ".tmp";

    private NBTProxyCompound rootCompound = null;

    private SavedFile configFile = null;

    private String fullPath = "";
    private String tagName = "";
    private NBTConfig parent = null;

    public NBTConfig(File folder, String fileName) {
        this.configFile = new SavedFile(folder, fileName, NBTConfig.NBT_FILE_EXT);
        this.rootCompound = new NBTProxyCompound();

        this.init();
    }

    private NBTConfig(NBTConfig p_parent, String p_tagName) {
        this.parent = p_parent;
        this.tagName = p_tagName;
        this.fullPath = NBTConfig.buildPath(p_parent.fullPath, p_tagName);

        this.configFile = this.parent.configFile;
    }

    private void init() {
        if (this.parent != null) {
            this.parent.init();
            return;
        }
        this.configFile.init();
        this.reload();
    }
    
    @SuppressWarnings("deprecation")
	NBTConfig getElementParent(String path)
    {
    	PathNavigator<NBTConfig> pn = new PathNavigator<NBTConfig>(this);
    	PathComponentsList parsedpath = PathNavigator.parsePath(path);
    	parsedpath.cleanPath();
    	parsedpath.removeLast();
    	if (pn.followPath(parsedpath))
    	{
    		return pn.getCurrentNode();
    	}
    	return this;
    }

    private static String buildPath(String path, String child) {
        if (path == null || path.isEmpty() || child == null) {
            return PathComponent.escapeComponent(child);
        }
        return path + PathNavigator.PATH_SEPARATOR + PathComponent.escapeComponent(child);
    }

    public static NBTConfig getForPath(File folder, String fileName, String path) {
        return NBTConfig.getForPath(new NBTConfig(folder, fileName), path);
    }

    public static NBTConfig getForPath(NBTConfig root, String path) {
        if (path == null || path.isEmpty()) {
            return root;
        }
        PathNavigator<NBTConfig> navigator = new PathNavigator<NBTConfig>(root);
        navigator.navigate(path);
        return navigator.getCurrentNode();
    }

    public SavedFile getFile() {
        return this.configFile;
    }

    public String getFileName() {
        if (this.configFile == null && this.parent != null) {
            return this.parent.getFileName();
        }
        return this.configFile.getFileName();
    }

    public String getName() {
        return this.tagName;
    }

    public String getCurrentPath() {
        return this.fullPath;
    }

    public void lock() {
        if (this.parent != null) {
            this.parent.lock();
        }
        this.configFile.lock();
    }

    public NBTConfig getRoot() {
        if (this.parent != null) {
            return this.parent.getRoot();
        }
        return this;
    }

    public NBTConfig getParent() {
        if (this.parent != null) {
            return this.parent;
        }
        return this;
    }

    public NBTConfig getChild(String name) {
        if (!this.configFile.canAccess()) {
            return null;
        }
        NBTConfig sub = new NBTConfig(this, name);
        return sub;
    }

    private NBTProxyCompound getTagCp() {
        NBTProxyCompound papa;
        if (this.parent != null) {
            papa = this.parent.getTagCp();
        } else {
            papa = rootCompound;
        }
        if (papa == null) {
            return null;
        }
        return papa.getCompound(this.tagName).clone();
    }

    public INBTCompound getTagCopy() {
        return this.getTagCp();
    }

    private void setTagCp(NBTProxyCompound compound) {
        if (!this.configFile.canAccess()) {
            return;
        }
        NBTProxyCompound papa;
        if (this.parent != null) {
            papa = this.parent.getTagCp();
        } else {
            papa = rootCompound;
        }
        if (papa != null) {
            papa.set(this.tagName, compound);
            if (this.parent != null) {
                this.parent.setTagCp(papa);
            } else {
                this.rootCompound = papa;
            }
        }
    }

    public void setTagCopy(INBTCompound compound) {
        this.setTagCp((NBTProxyCompound) compound);
    }

    public boolean canAccess() {
        if (this.parent != null) {
            return this.parent.canAccess();
        }
        return this.configFile.canAccess();
    }

    public void reload() {
        if (!this.canAccess()) {
            return;
        }
        if (this.parent != null) {
            this.parent.reload();
        } else {
            if (!this.configFile.getFile().exists()) {
                this.save();
                return;
            }
            NBTProxyCompound tmpCompound = null;
            try {
                UneiConfiguration.getInstance().getLogger().fine("Reading NBT Compound from file " + getFileName() + "...");
                tmpCompound = NBTProxyCST.readCompressed(new FileInputStream(this.configFile.getFile()));
                UneiConfiguration.getInstance().getLogger().fine("Successfully read.");
                UneiConfiguration.getInstance().getLogger().finest(tmpCompound == null? "(null)" : tmpCompound.toString());
                if (tmpCompound != null)
                    UneiConfiguration.getInstance().getLogger().fine("Type is " + tmpCompound.getUneiType());
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            if (tmpCompound != null) {
                this.rootCompound = tmpCompound.clone();
            } else {
                this.rootCompound = new NBTProxyCompound();
            }
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
        File tmp = new File(this.configFile.getFolder(), this.configFile.getFileName() + NBTConfig.NBT_TMP_EXT);
        try {
            NBTProxyCST.writeCompressed(rootCompound.clone(), new FileOutputStream(tmp));
            if (this.configFile.getFile().exists()) {
                this.configFile.getFile().delete();
            }
            tmp.renameTo(this.configFile.getFile());
            tmp.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Set<String> getKeys() {
        NBTProxyCompound tag = this.getTagCp();
        return tag.keySet();
    }

    public boolean contains(String key) {
        NBTProxyCompound tag = this.getTagCp();
        return tag.hasKey(key);
    }

    public Object get(String key) {
        String serialized = this.getString(key);
        InputStream is = new ByteArrayInputStream(serialized.getBytes());
        ObjectInputStream ois = null;
        Object result = null;
        try {
            ois = new ObjectInputStream(is);
            result = ois.readObject();
            ois.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        if (result != null) {
            return result;
        }
        return null;
    }

    public String getString(String key) {
        NBTProxyCompound tag = this.getTagCp();
        return tag.getString(key);
    }

    public void setString(String key, String value) {
        NBTProxyCompound tag = this.getTagCp();
        tag.setString(key, value);
        this.setTagCp(tag);
    }

    public void setSubSection(String path, IConfiguration value) {
        if (!this.configFile.canAccess()) {
            return;
        }
        if (!(value instanceof NBTConfig)) {
            //TODO ConfigType conversion
            return;
        }
        NBTConfig cfg = (NBTConfig) value;
        NBTProxyCompound nbt = this.getTagCp();
        nbt.set(path, cfg.getTagCp());
        this.setTagCp(nbt);
    }

    public void remove(String key) {
        NBTProxyCompound tag = this.getTagCp();
        tag.remove(key);
        this.setTagCp(tag);
    }

    public NBTConfig getSubSection(String path) {
        if (!this.configFile.canAccess()) {
            return null;
        }
        NBTConfig sub = new NBTConfig(this, path);
        return sub;
    }

    public double getDouble(String key) {
        NBTProxyCompound tag = this.getTagCp();
        return tag.getDouble(key);
    }

    public boolean getBoolean(String key) {
        NBTProxyCompound tag = this.getTagCp();
        return tag.getBoolean(key);
    }

    public byte getByte(String key) {
        NBTProxyCompound tag = this.getTagCp();
        return tag.getByte(key);
    }

    public float getFloat(String key) {
        NBTProxyCompound tag = this.getTagCp();
        return tag.getFloat(key);
    }

    public int getInteger(String key) {
        NBTProxyCompound tag = this.getTagCp();
        return tag.getInt(key);
    }

    public long getLong(String key) {
        NBTProxyCompound tag = this.getTagCp();
        return tag.getLong(key);
    }

    public List<Byte> getByteList(String key) {
        NBTProxyCompound tag = this.getTagCp();
        return Arrays.asList(ArrayUtils.toObject(tag.getByteArray(key)));
    }

    public List<Integer> getIntegerList(String key) {
        NBTProxyCompound tag = this.getTagCp();
        return Arrays.asList(ArrayUtils.toObject(tag.getIntArray(key)));
    }

    public void set(String key, Object value) {
        if (value == null) {
            return;
        }
        String serialized = null;
        ByteArrayOutputStream baos;
        ObjectOutputStream oos;
        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(value);
            oos.flush();
            serialized = new String(baos.toByteArray());
            oos.close();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (serialized != null) {
            this.setString(key, serialized);
        }
    }

    public void setDouble(String key, double value) {
        NBTProxyCompound tag = this.getTagCp();
        tag.setDouble(key, value);
        this.setTagCp(tag);
    }

    public void setBoolean(String key, boolean value) {
        NBTProxyCompound tag = this.getTagCp();
        tag.setBoolean(key, value);
        this.setTagCp(tag);
    }

    public void setByte(String key, byte value) {
        NBTProxyCompound tag = this.getTagCp();
        tag.setByte(key, value);
        this.setTagCp(tag);
    }

    public void setFloat(String key, float value) {
        NBTProxyCompound tag = this.getTagCp();
        tag.setFloat(key, value);
        this.setTagCp(tag);
    }

    public void setInteger(String key, int value) {
        NBTProxyCompound tag = this.getTagCp();
        tag.setInt(key, value);
        this.setTagCp(tag);
    }

    public void setLong(String key, long value) {
        NBTProxyCompound tag = this.getTagCp();
        tag.setLong(key, value);
        this.setTagCp(tag);
    }

    public void setByteList(String key, List<Byte> value) {
        NBTProxyCompound tag = this.getTagCp();
        tag.setByteArray(key, ArrayUtils.toPrimitive(value.toArray(new Byte[value.size()]), (byte) 0));
        this.setTagCp(tag);

    }

    public void setIntegerList(String key, List<Integer> value) {
        NBTProxyCompound tag = this.getTagCp();
        tag.setIntArray(key, ArrayUtils.toPrimitive(value.toArray(new Integer[value.size()]), 0));
        this.setTagCp(tag);
    }

    @Override
    public String toString() {
        return "NBTConfig=" + this.getTagCopy().toString();
    }
}