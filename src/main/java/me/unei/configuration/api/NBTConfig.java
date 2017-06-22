package me.unei.configuration.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import me.unei.configuration.SavedFile;
import me.unei.configuration.formats.nbtproxy.NBTProxyCST;
import me.unei.configuration.formats.nbtproxy.NBTProxyCompound;
import me.unei.configuration.plugin.UneiConfiguration;

public final class NBTConfig implements INBTConfiguration
{
	public static final String NBT_FILE_EXT = ".dat";
	public static final String NBT_TMP_EXT = NBTConfig.NBT_FILE_EXT + ".tmp";
	
	private NBTProxyCompound rootCompound = null;

    private SavedFile configFile = null;

    private String fullPath = "";
    private String tagName = "";
    private NBTConfig parent = null;
    
    public NBTConfig(File folder, String fileName) {
        this.configFile = new SavedFile(folder, fileName, NBTConfig.NBT_FILE_EXT);
        this.rootCompound = new NBTProxyCompound();
    }

    NBTConfig(File folder, String fileName, String p_tagName) {
        this(new NBTConfig(folder, fileName), p_tagName);
    }

    NBTConfig(NBTConfig p_parent, String p_tagName) {
        this.parent = p_parent;
        this.tagName = p_tagName;
        this.fullPath = NBTConfig.buildPath(p_parent.fullPath, p_tagName);
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
        return this.parent;
    }

    public NBTProxyCompound getTagCopy() {
        NBTProxyCompound papa;
        if (this.parent != null) {
            papa = this.parent.getTagCopy();
        } else {
            papa = rootCompound;
        }
        if (papa == null) {
            return null;
        }
        return papa.getCompound(this.tagName).clone();
    }

    public void setTagCopy(NBTProxyCompound compound) {
        if (!this.configFile.canAccess()) {
            return;
        }
        NBTProxyCompound papa;
        if (this.parent != null) {
            papa = this.parent.getTagCopy();
        } else {
            papa = rootCompound;
        }
        if (papa != null) {
            papa.set(this.tagName, compound);
            if (this.parent != null) {
                this.parent.setTagCopy(papa);
            } else {
                this.rootCompound = papa;
            }
        }
    }

    public boolean canAccess() {
        if (this.parent != null) {
            return this.parent.canAccess();
        }
        return this.configFile.canAccess();
    }

    public void init() {
        if (this.parent != null) {
            this.parent.init();
        } else {
            this.configFile.init();
        }
    }

    public void load() {
        if (!this.canAccess()) {
            return;
        }
        if (this.parent != null) {
            this.parent.load();
        } else {
            if (!this.configFile.getFile().exists()) {
                this.save();
                return;
            }
            NBTProxyCompound tmpCompound = null;
            try {
            	UneiConfiguration.getInstance().getLogger().fine("Reading NBT Compound from file " + this.configFile.getFileName() + "...");
                tmpCompound = NBTProxyCST.readCompressed(new FileInputStream(this.configFile.getFile()));
                UneiConfiguration.getInstance().getLogger().fine("OK : " + (tmpCompound == null ? "(null)" : tmpCompound.toString()));
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

    public void reset() {
        //
    }

    public boolean contains(String key) {
        NBTProxyCompound tag = this.getTagCopy();
        return tag.hasKey(key);
    }

    public String getString(String key) {
        NBTProxyCompound tag = this.getTagCopy();
        return tag.getString(key);
    }

    public void setString(String key, String value) {
        NBTProxyCompound tag = this.getTagCopy();
        tag.setString(key, value);
        this.setTagCopy(tag);
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
        NBTProxyCompound nbt = this.getTagCopy();
        nbt.set(path, cfg.getTagCopy());
        this.setTagCopy(nbt);
    }

    public void remove(String key) {
        NBTProxyCompound tag = this.getTagCopy();
        tag.remove(key);
        this.setTagCopy(tag);
    }

    public NBTConfig getSubSection(String path) {
        if (!this.configFile.canAccess()) {
            return null;
        }
        NBTConfig sub = new NBTConfig(this, path);
        return sub;
    }

    private static String buildPath(String parent, String child) {
        if (parent == null || parent.isEmpty() || child == null) {
            return child;
        }
        return new String(parent + "." + child);
    }

    private static String[] splitPath(String path) {
        return path.split(".");
    }

    public static NBTConfig getForPath(File folder, String fileName, String path) {
        return NBTConfig.getForPath(new NBTConfig(folder, fileName), path);
    }

    public static NBTConfig getForPath(NBTConfig root, String path) {
        if (path == null || path.isEmpty()) {
            return root;
        }
        if (!path.contains(".")) {
            return root.getSubSection(path);
        }
        NBTConfig last = root;
        for (String part : NBTConfig.splitPath(path)) {
            last = last.getSubSection(part);
        }
        return last;
    }

    public double getDouble(String key) {
        NBTProxyCompound tag = this.getTagCopy();
        return tag.getDouble(key);
    }

    public boolean getBoolean(String key) {
        NBTProxyCompound tag = this.getTagCopy();
        return tag.getBoolean(key);
    }

    public byte getByte(String key) {
        NBTProxyCompound tag = this.getTagCopy();
        return tag.getByte(key);
    }

    public float getFloat(String key) {
        NBTProxyCompound tag = this.getTagCopy();
        return tag.getFloat(key);
    }

    public int getInteger(String key) {
        NBTProxyCompound tag = this.getTagCopy();
        return tag.getInt(key);
    }

    public long getLong(String key) {
        NBTProxyCompound tag = this.getTagCopy();
        return tag.getLong(key);
    }

    public List<Byte> getByteList(String key) {
        NBTProxyCompound tag = this.getTagCopy();
        return Arrays.asList(ArrayUtils.toObject(tag.getByteArray(key)));
    }

    public List<Integer> getIntegerList(String key) {
        NBTProxyCompound tag = this.getTagCopy();
        return Arrays.asList(ArrayUtils.toObject(tag.getIntArray(key)));
    }

    public void setDouble(String key, double value) {
        NBTProxyCompound tag = this.getTagCopy();
        tag.setDouble(key, value);
        this.setTagCopy(tag);
    }

    public void setBoolean(String key, boolean value) {
        NBTProxyCompound tag = this.getTagCopy();
        tag.setBoolean(key, value);
        this.setTagCopy(tag);
    }

    public void setByte(String key, byte value) {
        NBTProxyCompound tag = this.getTagCopy();
        tag.setByte(key, value);
        this.setTagCopy(tag);
    }

    public void setFloat(String key, float value) {
        NBTProxyCompound tag = this.getTagCopy();
        tag.setFloat(key, value);
        this.setTagCopy(tag);
    }

    public void setInteger(String key, int value) {
        NBTProxyCompound tag = this.getTagCopy();
        tag.setInt(key, value);
        this.setTagCopy(tag);
    }

    public void setLong(String key, long value) {
        NBTProxyCompound tag = this.getTagCopy();
        tag.setLong(key, value);
        this.setTagCopy(tag);
    }

    public void setByteList(String key, List<Byte> value) {
        NBTProxyCompound tag = this.getTagCopy();
        tag.setByteArray(key, ArrayUtils.toPrimitive(value.toArray(new Byte[value.size()]), (byte) 0));
        this.setTagCopy(tag);

    }

    public void setIntegerList(String key, List<Integer> value) {
        NBTProxyCompound tag = this.getTagCopy();
        tag.setIntArray(key, ArrayUtils.toPrimitive(value.toArray(new Integer[value.size()]), 0));
        this.setTagCopy(tag);
    }
    
    public String toString()
    {
    	return "NBTConfig : " + this.getTagCopy().toString();
    }
}