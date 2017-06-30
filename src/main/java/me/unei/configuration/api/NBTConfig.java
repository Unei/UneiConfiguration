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
import me.unei.configuration.api.fs.PathNavigator.PathSymbolsType;
import me.unei.configuration.formats.nbtproxy.NBTProxyCST;
import me.unei.configuration.formats.nbtproxy.NBTProxyCompound;
import me.unei.configuration.plugin.UneiConfiguration;

public final class NBTConfig extends Configuration<NBTConfig> implements INBTConfiguration {

    public static final String NBT_FILE_EXT = ".dat";
    public static final String NBT_TMP_EXT = ".tmp";

    private NBTProxyCompound rootCompound = null;

    public NBTConfig(File folder, String fileName) {
    	super(new SavedFile(folder, fileName, NBTConfig.NBT_FILE_EXT), PathSymbolsType.BUKKIT);

    	this.rootCompound = new NBTProxyCompound();

        this.init();
    }

    private NBTConfig(NBTConfig p_parent, String p_tagName) {
    	super(p_parent, p_tagName);
    }
    
    @Override
	protected void synchronize() {}
    
    @Override
	protected void propagate() {}

    @Deprecated
	NBTConfig getElementParent(String path)
    {
    	PathNavigator<NBTConfig> pn = new PathNavigator<NBTConfig>(this);
    	PathComponentsList parsedpath = PathNavigator.parsePath(path, this.symType);
    	parsedpath.cleanPath();
    	parsedpath.removeLast();
    	if (pn.followPath(parsedpath))
    	{
    		return pn.getCurrentNode();
    	}
    	return this;
    }

    public static NBTConfig getForPath(File folder, String fileName, String path) {
        return NBTConfig.getForPath(new NBTConfig(folder, fileName), path);
    }

    public static NBTConfig getForPath(NBTConfig root, String path) {
        if (path == null || path.isEmpty()) {
            return root;
        }
        PathNavigator<NBTConfig> navigator = new PathNavigator<NBTConfig>(root);
        navigator.navigate(path, root.symType);
        return navigator.getCurrentNode();
    }

    @Override
	public NBTConfig getRoot()
    {
    	return (NBTConfig) super.getRoot();
    }

    public NBTConfig getChild(String name) {
        if (!this.canAccess()) {
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
            return rootCompound;
        }
        if (papa == null) {
            return null;
        }
        return papa.getCompound(this.nodeName).clone();
    }
    
    private NBTProxyCompound getTagParentAt(PathComponent.PathComponentsList path)
    {
    	NBTConfig dir;
    	PathNavigator<NBTConfig> pn = new PathNavigator<NBTConfig>(this);
    	PathComponent.PathComponentsList pathList = PathNavigator.cleanPath(path);
    	pathList.removeLast();
    	if (!pn.followPath(pathList))
    	{
    		return this.getTagCp();
    	}
    	dir = pn.getCurrentNode();
    	return dir.getTagCp();
    }

    public INBTCompound getTagCopy() {
        return this.getTagCp();
    }

    private void setTagCp(NBTProxyCompound compound) {
        if (!this.canAccess()) {
            return;
        }
        NBTProxyCompound papa;
        if (this.parent != null) {
            papa = this.parent.getTagCp();
        } else {
            this.rootCompound = compound;
            return;
        }
        if (papa != null) {
            papa.set(this.nodeName, compound);
            this.parent.setTagCp(papa);
        }
        
    }
    
    private void setTagParentAt(PathComponent.PathComponentsList path, NBTProxyCompound compound)
    {
    	NBTConfig dir;
    	PathNavigator<NBTConfig> pn = new PathNavigator<NBTConfig>(this);
    	PathComponent.PathComponentsList pathList = PathNavigator.cleanPath(path);
    	pathList.removeLast();
    	if (!pn.followPath(pathList))
    	{
    		this.setTagCp(compound);
    	}
    	dir = pn.getCurrentNode();
    	if (dir != null)
    	{
    		dir.setTagCp(compound);
    	}
    }

    public void setTagCopy(INBTCompound compound) {
        this.setTagCp((NBTProxyCompound) compound);
    }

    public void reload() {
        if (!this.canAccess()) {
            return;
        }
        if (this.parent != null) {
            this.parent.reload();
        } else {
            if (!this.file.getFile().exists()) {
                this.save();
                return;
            }
            NBTProxyCompound tmpCompound = null;
            try {
                UneiConfiguration.getInstance().getLogger().fine("Reading NBT Compound from file " + getFileName() + "...");
                tmpCompound = NBTProxyCST.readCompressed(new FileInputStream(this.file.getFile()));
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
        File tmp = new File(this.file.getFolder(), this.file.getFileName() + NBTConfig.NBT_TMP_EXT);
        try {
            NBTProxyCST.writeCompressed(rootCompound.clone(), new FileOutputStream(tmp));
            if (this.file.getFile().exists()) {
                this.file.getFile().delete();
            }
            tmp.renameTo(this.file.getFile());
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
        PathComponent.PathComponentsList list = PathNavigator.parsePath(key, symType);
        NBTProxyCompound tag = this.getTagParentAt(list);
        return tag.hasKey(list.lastChild());
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
        PathComponent.PathComponentsList list = PathNavigator.parsePath(key, symType);
        NBTProxyCompound tag = this.getTagParentAt(list);
        return tag.getString(list.lastChild());
    }

    public void setString(String key, String value) {
        PathComponent.PathComponentsList list = PathNavigator.parsePath(key, symType);
        NBTProxyCompound tag = this.getTagParentAt(list);
        tag.setString(list.lastChild(), value);
        this.setTagParentAt(list, tag);
    }

    public void setSubSection(String path, IConfiguration value) {
        if (!this.canAccess()) {
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
        PathComponent.PathComponentsList list = PathNavigator.parsePath(key, symType);
        NBTProxyCompound tag = this.getTagParentAt(list);
        tag.remove(list.lastChild());
        this.setTagParentAt(list, tag);
    }

    @Override
	public NBTConfig getSubSection(PathComponent.PathComponentsList path)
    {
    	if (!this.canAccess()) {
    		return null;
    	}
    	PathNavigator<NBTConfig> navi = new PathNavigator<NBTConfig>(this);
    	navi.followPath(path);
    	return navi.getCurrentNode();
    }

    public double getDouble(String key) {
        PathComponent.PathComponentsList list = PathNavigator.parsePath(key, symType);
        NBTProxyCompound tag = this.getTagParentAt(list);
        return tag.getDouble(list.lastChild());
    }

    public boolean getBoolean(String key) {
        PathComponent.PathComponentsList list = PathNavigator.parsePath(key, symType);
        NBTProxyCompound tag = this.getTagParentAt(list);
        return tag.getBoolean(list.lastChild());
    }

    public byte getByte(String key) {
        PathComponent.PathComponentsList list = PathNavigator.parsePath(key, symType);
        NBTProxyCompound tag = this.getTagParentAt(list);
        return tag.getByte(list.lastChild());
    }

    public float getFloat(String key) {
        PathComponent.PathComponentsList list = PathNavigator.parsePath(key, symType);
        NBTProxyCompound tag = this.getTagParentAt(list);
        return tag.getFloat(list.lastChild());
    }

    public int getInteger(String key) {
        PathComponent.PathComponentsList list = PathNavigator.parsePath(key, symType);
        NBTProxyCompound tag = this.getTagParentAt(list);
        return tag.getInt(list.lastChild());
    }

    public long getLong(String key) {
        PathComponent.PathComponentsList list = PathNavigator.parsePath(key, symType);
        NBTProxyCompound tag = this.getTagParentAt(list);
        return tag.getLong(list.lastChild());
    }

    public List<Byte> getByteList(String key) {
        PathComponent.PathComponentsList list = PathNavigator.parsePath(key, symType);
        NBTProxyCompound tag = this.getTagParentAt(list);
        return Arrays.asList(ArrayUtils.toObject(tag.getByteArray(list.lastChild())));
    }

    public List<Integer> getIntegerList(String key) {
        PathComponent.PathComponentsList list = PathNavigator.parsePath(key, symType);
        NBTProxyCompound tag = this.getTagParentAt(list);
        return Arrays.asList(ArrayUtils.toObject(tag.getIntArray(list.lastChild())));
    }

    public List<Long> getLongList(String key) {
        PathComponent.PathComponentsList list = PathNavigator.parsePath(key, symType);
        NBTProxyCompound tag = this.getTagParentAt(list);
        return Arrays.asList(ArrayUtils.toObject(tag.getLongArray(list.lastChild())));
    }

    public void set(String key, Object value) {
        if (value == null) {
            remove(key);
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
        PathComponent.PathComponentsList list = PathNavigator.parsePath(key, symType);
        NBTProxyCompound tag = this.getTagParentAt(list);
        tag.setDouble(list.lastChild(), value);
        this.setTagParentAt(list, tag);
    }

    public void setBoolean(String key, boolean value) {
        PathComponent.PathComponentsList list = PathNavigator.parsePath(key, symType);
        NBTProxyCompound tag = this.getTagParentAt(list);
        tag.setBoolean(list.lastChild(), value);
        this.setTagParentAt(list, tag);
    }

    public void setByte(String key, byte value) {
        PathComponent.PathComponentsList list = PathNavigator.parsePath(key, symType);
        NBTProxyCompound tag = this.getTagParentAt(list);
        tag.setByte(list.lastChild(), value);
        this.setTagParentAt(list, tag);
    }

    public void setFloat(String key, float value) {
        PathComponent.PathComponentsList list = PathNavigator.parsePath(key, symType);
        NBTProxyCompound tag = this.getTagParentAt(list);
        tag.setFloat(list.lastChild(), value);
        this.setTagParentAt(list, tag);
    }

    public void setInteger(String key, int value) {
        PathComponent.PathComponentsList list = PathNavigator.parsePath(key, symType);
        NBTProxyCompound tag = this.getTagParentAt(list);
        tag.setInt(list.lastChild(), value);
        this.setTagParentAt(list, tag);
    }

    public void setLong(String key, long value) {
        PathComponent.PathComponentsList list = PathNavigator.parsePath(key, symType);
        NBTProxyCompound tag = this.getTagParentAt(list);
        tag.setLong(list.lastChild(), value);
        this.setTagParentAt(list, tag);
    }

    public void setByteList(String key, List<Byte> value) {
        PathComponent.PathComponentsList list = PathNavigator.parsePath(key, symType);
        NBTProxyCompound tag = this.getTagParentAt(list);
        tag.setByteArray(list.lastChild(), ArrayUtils.toPrimitive(value.toArray(new Byte[value.size()]), (byte) 0));
        this.setTagParentAt(list, tag);
    }

    public void setIntegerList(String key, List<Integer> value) {
        PathComponent.PathComponentsList list = PathNavigator.parsePath(key, symType);
        NBTProxyCompound tag = this.getTagParentAt(list);
        tag.setIntArray(list.lastChild(), ArrayUtils.toPrimitive(value.toArray(new Integer[value.size()]), 0));
        this.setTagParentAt(list, tag);
    }

    public void setLongList(String key, List<Long> value) {
        PathComponent.PathComponentsList list = PathNavigator.parsePath(key, symType);
        NBTProxyCompound tag = this.getTagParentAt(list);
        tag.setLongArray(list.lastChild(), ArrayUtils.toPrimitive(value.toArray(new Long[value.size()]), 0));
        this.setTagParentAt(list, tag);
    }

    @Override
    public String toString() {
        return "NBTConfig=" + this.getTagCopy().toString();
    }
}