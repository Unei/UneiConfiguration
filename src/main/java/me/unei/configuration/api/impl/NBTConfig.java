package me.unei.configuration.api.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import me.unei.configuration.SavedFile;
import me.unei.configuration.api.IConfiguration;
import me.unei.configuration.api.INBTConfiguration;
import me.unei.configuration.api.UntypedStorage;
import me.unei.configuration.api.format.INBTCompound;
import me.unei.configuration.api.fs.PathComponent;
import me.unei.configuration.api.fs.PathNavigator;
import me.unei.configuration.api.fs.PathNavigator.PathSymbolsType;
import me.unei.configuration.formats.nbtlib.NBTIO;
import me.unei.configuration.formats.nbtlib.TagCompound;
import me.unei.configuration.plugin.UneiConfiguration;

/**
 * @version 2.5.0
 * @since 0.0.1
 */
public final class NBTConfig extends UntypedStorage<NBTConfig> implements INBTConfiguration {

    public static final String NBT_FILE_EXT = ".dat";
    public static final String NBT_TMP_EXT = ".tmp";

    private Map<String, Object> data = new HashMap<String, Object>();
    
    public NBTConfig(SavedFile file, PathSymbolsType symType) {
    	super(file, symType);
    	
    	this.init();
    }
    
    public NBTConfig(SavedFile file) {
    	this(file, PathSymbolsType.BUKKIT);
    }

	public NBTConfig(File folder, String fileName, PathSymbolsType symType) {
        this(new SavedFile(folder, fileName, NBTConfig.NBT_FILE_EXT), symType);
    }

    public NBTConfig(File folder, String fileName) {
        this(folder, fileName, PathSymbolsType.BUKKIT);
    }

    private NBTConfig(NBTConfig p_parent, String p_tagName) {
        super(p_parent, p_tagName);
        
        this.updateFromParent();
        this.propagate();
    }

    @Override
    protected void propagate() {
        if (this.parent != null) {
            this.parent.data.put(this.nodeName, this.data);
            this.parent.propagate();
        }
    }

    public static NBTConfig getForPath(File folder, String fileName, String path, PathSymbolsType symType) {
        return NBTConfig.getForPath(new NBTConfig(folder, fileName, symType), path);
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
    public NBTConfig getRoot() {
        return (NBTConfig) super.getRoot();
    }

    public NBTConfig getChild(String name) {
        if (!this.canAccess()) {
            return null;
        }
        if (name == null || name.isEmpty()) {
            return this;
        }
        return new NBTConfig(this, name);
    }
    
    @SuppressWarnings("unchecked")
	private void updateFromParent() {
		if (this.parent != null && this.parent.data != null) {
			Object me = this.parent.data.get(nodeName);
			if (me != null && (me instanceof Map)) {
				this.data = (Map<String, Object>) me;
			}
		}
	}

    private TagCompound getTagCp() {
    	TagCompound result = new TagCompound();
    	result.loadMap(data);
    	return result;
    }

    private Map<String, Object> getParentMap(PathComponent.PathComponentsList path) {
        NBTConfig dir;
        PathNavigator<NBTConfig> pn = new PathNavigator<NBTConfig>(this);
        PathComponent.PathComponentsList pathList = PathNavigator.cleanPath(path);
        pathList.removeLast();
        if (!pn.followPath(pathList)) {
            return data;
        }
        dir = pn.getCurrentNode();
        return dir.data;
    }

    public INBTCompound getTagCopy() {
        return this.getTagCp();
    }
    
    private void setTagCp(TagCompound compound) {
    	if (!this.canAccess() || compound == null) {
    		return;
    	}
    	this.data = compound.getAsObject();
    	this.propagate();
    }

    public void setTagCopy(INBTCompound compound) {
        this.setTagCp((TagCompound) compound);
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
            TagCompound compound = null;
            try {
                UneiConfiguration.getInstance().getLogger().fine("Reading NBT Compound from file " + getFileName() + "...");
                compound = NBTIO.readCompressed(new FileInputStream(file.getFile()));
                UneiConfiguration.getInstance().getLogger().fine("Successfully read.");
            } catch (IOException e) {
                UneiConfiguration.getInstance().getLogger().warning("An error occured while loading NBT file " + getFileName() + ":");
                e.printStackTrace();
                return;
            }
            if (compound != null) {
            	this.data = compound.getAsObject();
            } else {
            	this.data = new HashMap<String, Object>();
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
        File tmp = new File(this.file.getFolder(), this.file.getFullName() + NBTConfig.NBT_TMP_EXT);
        TagCompound compound = new TagCompound();
        compound.loadMap(data);
        try {
            UneiConfiguration.getInstance().getLogger().fine("Writing NBT Compound to file " + getFileName() + "...");
            NBTIO.writeCompressed(compound, new FileOutputStream(tmp));
            if (this.file.getFile().exists()) {
                UneiConfiguration.getInstance().getLogger().finer("Replacing already present file " + getFileName() + ".");
                this.file.getFile().delete();
            }
            tmp.renameTo(this.file.getFile());
            tmp.delete();
            UneiConfiguration.getInstance().getLogger().fine("Successfully written.");
        } catch (IOException e) {
            UneiConfiguration.getInstance().getLogger().warning("An error occured while saving NBT file " + getFileName() + ":");
            e.printStackTrace();
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

    public void setSubSection(String path, IConfiguration value) {
        if (!this.canAccess()) {
            return;
        }
        if (value == null) {
            remove(path);
            return;
        }
        if (!(value instanceof NBTConfig)) {
            //TODO ConfigType conversion
            return;
        }
        set(path, ((NBTConfig) value).data);
    }

	public void remove(String key) {
        if (!this.canAccess()) {
            return;
        }
        PathComponent.PathComponentsList list = PathNavigator.parsePath(key, symType);
        Map<String, Object> node = this.getParentMap(list);
        node.remove(list.lastChild());
    }

    @Override
    public NBTConfig getSubSection(PathComponent.PathComponentsList path) {
        if (!this.canAccess()) {
            return null;
        }
        PathNavigator<NBTConfig> navi = new PathNavigator<NBTConfig>(this);
        navi.followPath(path);
        return navi.getCurrentNode();
    }

    @Override
	public boolean getBoolean(String key) {
    	Object o = this.get(key);
    	if (o instanceof Boolean) {
    		return ((Boolean)o).booleanValue();
    	} else if (o instanceof Number) {
    		return (((Number)o).byteValue() == 0 ? false : true);
    	}
    	return false;
    }

	public void set(String path, Object value) {
    	if (!this.canAccess()) {
    		return;
    	}
    	if (value == null) {
    		this.remove(path);
    		return;
    	}
        PathComponent.PathComponentsList list = PathNavigator.parsePath(path, symType);
        Map<String, Object> node = this.getParentMap(list);
        node.put(list.lastChild(), value);
    }

    @Override
    public String toString() {
        return "NBTConfig=" + this.data.toString();
    }
}