package me.unei.configuration.api.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;

import me.unei.configuration.SavedFile;
import me.unei.configuration.api.Configuration;
import me.unei.configuration.api.IConfiguration;
import me.unei.configuration.api.INBTConfiguration;
import me.unei.configuration.api.UntypedStorage;
import me.unei.configuration.api.format.INBTCompound;
import me.unei.configuration.api.fs.PathComponent;
import me.unei.configuration.api.fs.PathNavigator;
import me.unei.configuration.api.fs.PathNavigator.PathSymbolsType;
import me.unei.configuration.formats.Storage;
import me.unei.configuration.formats.StorageType;
import me.unei.configuration.formats.StringHashMap;
import me.unei.configuration.formats.Storage.Key;
import me.unei.configuration.formats.nbtlib.NBTIO;
import me.unei.configuration.formats.nbtlib.TagCompound;
import me.unei.configuration.plugin.UneiConfiguration;

/**
 * @version 2.5.0
 * @since 0.0.1
 */
public class NBTConfig2 extends UntypedStorage<NBTConfig2> implements INBTConfiguration {

    public static final String NBT_FILE_EXT = ".dat";
    public static final String NBT_TMP_EXT = ".tmp";
	
	protected NBTRootConfig root;
	
	protected NBTConfig2(SavedFile file, PathSymbolsType type) {
		super(file, type);
	}

    public NBTConfig2(NBTConfig2 p_parent, String p_tagName) {
        super(p_parent, p_tagName);
        
        if (p_parent == null) {
        	throw new IllegalArgumentException("parent must not be null");
        }
        
        this.root = p_parent.root;
    }

    public static NBTConfig2 getForPath(File folder, String fileName, String path, PathSymbolsType symType) {
        return NBTConfig2.getForPath(new NBTRootConfig(folder, fileName, symType), path);
    }

    public static NBTConfig2 getForPath(File folder, String fileName, String path) {
        return NBTConfig2.getForPath(new NBTRootConfig(folder, fileName), path);
    }

    public static NBTConfig2 getForPath(NBTConfig2 root, String path) {
        if (path == null || path.isEmpty()) {
            return root;
        }
        PathNavigator<NBTConfig2> navigator = new PathNavigator<NBTConfig2>(root);
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
    			}
    		}
    	}
    	return null;
    }
	
    @Override
    protected void propagate() {
        if (this.parent != null) {
            this.parent.propagate();
        }
    }

    @Override
    public NBTConfig2 getRoot() {
    	if (this.root != null) {
    		return this.root;
    	}
        return (this.root = (NBTRootConfig) super.getRoot());
    }

    public NBTConfig2 getChild(String name) {
        if (!this.canAccess()) {
            return null;
        }
        if (name == null || name.isEmpty()) {
            return this;
        }
        return new NBTConfig2(this, name);
    }
    
    protected TagCompound getTagCp() {
    	return this.root.getTagCp();
    }

    private Storage<Object> getParentMap(PathComponent.PathComponentsList path) {
        NBTConfig2 dir;
        PathNavigator<NBTConfig2> pn = new PathNavigator<NBTConfig2>(this);
        PathComponent.PathComponentsList pathList = PathNavigator.cleanPath(path);
        pathList.removeLast();
        if (!pn.followPath(pathList)) {
            return getNodeData();
        }
        dir = pn.getCurrentNode();
        return dir.getNodeData();
    }

    public INBTCompound getTagCopy() {
        return this.getTagCp();
    }
    
    protected void setTagCp(TagCompound compound) {
    	this.root.setTagCp(compound);
    }

    public void setTagCopy(INBTCompound compound) {
        this.setTagCp((TagCompound) compound);
    }

	public void reload() {
		this.root.reload();
    }

	public void save() {
		this.root.save();
    }

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

    public void setSubSection(String path, IConfiguration value) {
        if (!this.canAccess()) {
            return;
        }
        if (value == null) {
            remove(path);
            return;
        }
        if (!(value instanceof NBTConfig2)) {
            //TODO ConfigType conversion
            return;
        }
        PathComponent.PathComponentsList list = PathNavigator.parsePath(path, symType);
        Storage<Object> node = this.getParentMap(list);
        Key key = list.last().getKey(node.getStorageType());
        ((NBTConfig2) value).nodeAtomicIndex = key.getKeyAtomicInt();
        node.set(key, ((NBTConfig2) value).getNodeData());
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
    public NBTConfig2 getSubSection(PathComponent.PathComponentsList path) {
        if (!this.canAccess()) {
            return null;
        }
        PathNavigator<NBTConfig2> navi = new PathNavigator<NBTConfig2>(this);
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
    	if (value instanceof IConfiguration) {
    		setSubSection(path, (IConfiguration) value);
    		return;
    	}
        PathComponent.PathComponentsList list = PathNavigator.parsePath(path, symType);
        Storage<Object> node = this.getParentMap(list);
        node.set(list.last().getKey(node.getStorageType()), value);
    }

    @Override
    public String toString() {
        return "NBTConfig=" + this.getNodeData().toString();
    }
    
    protected static final class NBTRootConfig extends NBTConfig2
    {
    	private StringHashMap<Object> data = new StringHashMap<Object>();
    	
    	public NBTRootConfig(SavedFile file, PathSymbolsType symType)
    	{
    		super(file, symType);

    		this.root = this;
        	this.init();
    	}
        
        public NBTRootConfig(SavedFile file) {
        	this(file, PathSymbolsType.BUKKIT);
        }

    	public NBTRootConfig(File folder, String fileName, PathSymbolsType symType) {
            this(new SavedFile(folder, fileName, NBTConfig.NBT_FILE_EXT), symType);
        }

        public NBTRootConfig(File folder, String fileName) {
            this(folder, fileName, PathSymbolsType.BUKKIT);
        }
    	
    	@Override
    	public NBTRootConfig getRoot() {
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
    	protected TagCompound getTagCp() {
        	TagCompound result = new TagCompound();
        	result.loadMap(data);
        	return result;
        }
        
    	@Override
        protected void setTagCp(TagCompound compound) {
        	if (!this.canAccess() || compound == null) {
        		return;
        	}
        	this.data = compound.getAsObject(new StringHashMap<Object>());
        	this.propagate();
        }

    	@Override
    	public void reload() {
    		if (!this.canAccess()) {
    			return;
    		}
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
    			this.data = compound.getAsObject(new StringHashMap<Object>());
    		} else {
    			this.data = new StringHashMap<Object>();
    		}
    	}

    	@Override
    	public void save() {
            if (!this.canAccess()) {
                return;
            }
            File tmp = new File(this.file.getFolder(), this.file.getFullName() + NBTConfig2.NBT_TMP_EXT);
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
    }
}