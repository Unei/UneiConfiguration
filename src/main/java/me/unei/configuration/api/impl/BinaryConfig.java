package me.unei.configuration.api.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Set;

import org.apache.commons.lang.NotImplementedException;

import me.unei.configuration.SavedFile;
import me.unei.configuration.api.IConfiguration;
import me.unei.configuration.api.UntypedStorage;
import me.unei.configuration.api.exceptions.FileFormatException;
import me.unei.configuration.api.fs.IPathNavigator.PathSymbolsType;
import me.unei.configuration.api.fs.PathComponent;
import me.unei.configuration.api.fs.PathNavigator;
import me.unei.configuration.formats.Storage;
import me.unei.configuration.formats.StorageType;
import me.unei.configuration.formats.Storage.Key;
import me.unei.configuration.formats.StorageConverter;
import me.unei.configuration.formats.StringHashMap;
import me.unei.configuration.plugin.UneiConfiguration;

public final class BinaryConfig extends UntypedStorage<BinaryConfig> implements IConfiguration {
	
	public static final String BINARY_FILE_EXT = ".bin";
	public static final String BINARY_TMP_EXT = ".tmp";
	
	private Storage<Object> data = null;
	
	final Storage<Object> getData() {
		if (data == null)
		{
			data = new StringHashMap<Object>();
		}
		return data;
	}
	
	public BinaryConfig(SavedFile file, PathSymbolsType symType) {
		super(file, symType);
		
		this.init();
	}
	
	public BinaryConfig(SavedFile file) {
		this(file, PathSymbolsType.BUKKIT);
	}
	
	public BinaryConfig(File folder, String fileName) {
		this(folder, fileName, PathSymbolsType.BUKKIT);
	}
	
	public BinaryConfig(File folder, String fileName, PathSymbolsType symType) {
		this(new SavedFile(folder, fileName, BinaryConfig.BINARY_FILE_EXT), symType);
	}
	
	public BinaryConfig(BinaryConfig parent, String tagName) {
		super(parent, tagName);
		
		this.updateNode();
	}

	public static BinaryConfig getForPath(File folder, String fileName, String path, PathSymbolsType symType) {
		return BinaryConfig.getForPath(new BinaryConfig(folder, fileName, symType), path);
	}

	public static BinaryConfig getForPath(File folder, String fileName, String path) {
		return BinaryConfig.getForPath(new BinaryConfig(folder, fileName), path);
	}

	public static BinaryConfig getForPath(BinaryConfig root, String path) {
		if (root == null) {
			return null;
		}
		return root.getSubSection(path);
	}
	
	@Override
	public StorageType getType() {
		return (this.data != null) ? this.data.getStorageType() : StorageType.UNDEFINED;
	}
	
	@Override
	protected void updateFromParent() {
		if (this.parent != null && this.parent.data != null) {
			if (this.parent.getData().getStorageType() != StorageType.UNDEFINED) {
				Object me = this.parent.data.get(Key.of(this.parent.getType(), nodeAtomicIndex, nodeName));
				Storage<Object> tmp = StorageConverter.allocateBest(me, null, null);
				if (tmp != null) {
					this.data = tmp;
				} else {
					this.data = new StringHashMap<Object>();
				}
				this.parent.data.set(Key.of(this.parent.getType(), nodeAtomicIndex, nodeName), this.data);
			}
		}
	}
	
	@Override
	public void setType(StorageType type) {
    	if (!this.canAccess()) {
    		return;
    	}
    	throw new NotImplementedException();
	}
	
	@Override
	public BinaryConfig getRoot() {
		return (BinaryConfig) super.getRoot();
	}
	
	public BinaryConfig getChild(String name) {
		if (!this.canAccess()) {
			return null;
		}
		if (name == null || name.isEmpty()) {
			return this;
		}
		return new BinaryConfig(this, name);
	}
	
	private BinaryConfig getParentObj(PathComponent.PathComponentsList path) {
		PathNavigator<BinaryConfig> pn = new PathNavigator<BinaryConfig>(this);
		PathComponent.PathComponentsList pathList = PathNavigator.cleanPath(path);
		pathList.removeLast();
		pn.followPath(pathList);
		return pn.getCurrentNode();
	}
	
	private Storage<Object> getParentMap(PathComponent.PathComponentsList path) {
		PathNavigator<BinaryConfig> pn = new PathNavigator<BinaryConfig>(this);
		PathComponent.PathComponentsList pathList = PathNavigator.cleanPath(path);
		pathList.removeLast();
		pn.followPath(pathList);
		return pn.getCurrentNode().data;
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
		File tmp = new File(this.file.getFolder(), this.file.getFullName() + BinaryConfig.BINARY_TMP_EXT);
		UneiConfiguration.getInstance().getLogger().fine("Writing Binary to file " + getFileName() + "...");
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(tmp));
			oos.writeInt(0xdeadbeef);
			oos.writeObject(getData());
			oos.writeInt(0xfeebdaed);
			oos.flush();
			oos.close();
			if (this.file.getFile().exists()) {
				UneiConfiguration.getInstance().getLogger().finer("Replacing already present file " + getFileName() + ".");
				this.file.getFile().delete();
			}
			tmp.renameTo(this.file.getFile());
			tmp.delete();
			UneiConfiguration.getInstance().getLogger().fine("Successfully written.");
		} catch (IOException e) {
			UneiConfiguration.getInstance().getLogger().warning("An error occured while saving Binary file " + getFileName() + ":");
			e.printStackTrace();
		}
	}
	
	public void reload() throws FileFormatException
	{
		if (!this.canAccess()) {
			return;
		}
		if (this.parent != null) {
			this.parent.reload();
			return;
		}
		if (this.file.getFile() == null) {
			return;
		}
		if (!this.file.getFile().exists()) {
			this.save();
			return;
		}
		UneiConfiguration.getInstance().getLogger().fine("Reading Binary file " + getFileName() + "...");
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(new FileInputStream(this.file.getFile()));
			if (ois.readInt() != 0xdeadbeef) {
				UneiConfiguration.getInstance().getLogger().warning("The binary file " + getFileName() + " is not a deadbeef :");
				throw new FileFormatException("Raw binary", this.file.getFile(), "some dead beef could not be found... sadness");
			}
			Object result = ois.readObject();
			this.data = StorageConverter.allocateBest(result, null, () -> new StringHashMap<>());
			if (ois.readInt() != 0xfeebdaed) {
				UneiConfiguration.getInstance().getLogger().warning("The binary file " + getFileName() + " is not a deadbeef :");
				throw new FileFormatException("Raw binary", this.file.getFile(), "some dead beef could not be found... sadness");
			}
			UneiConfiguration.getInstance().getLogger().fine("Successfully written.");
		} catch (ClassNotFoundException e) {
			UneiConfiguration.getInstance().getLogger().warning("The object contained in the binary file " + getFileName() + " is not a Map :");
			e.printStackTrace();
		} catch (IOException e) {
			UneiConfiguration.getInstance().getLogger().warning("An error occured while reading Binary file " + getFileName() + ":");
			e.printStackTrace();
		} finally {
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e) {}
			}
		}
	}
	
	public Set<String> getKeys() {
		return this.getData().getKeys();
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
	
	@Override
	public BinaryConfig getSubSection(PathComponent.PathComponentsList path) {
		if (!this.canAccess()) {
			return null;
		}
		if (path == null || path.isEmpty()) {
			return this;
		}
		PathNavigator<BinaryConfig> navigator = new PathNavigator<BinaryConfig>(this);
		if (navigator.followPath(path)) {
			return navigator.getCurrentNode();
		}
		return null;
	}
	
	public void set(String path, Object value) {
		if (!this.canAccess()) {
			return;
		}
		PathComponent.PathComponentsList list = PathNavigator.parsePath(path, symType);
		Storage<Object> node = this.getParentMap(list);
		if (value == null) {
			node.remove(list.last().getKey(node.getStorageType()));
		} else {
			node.set(list.last().getKey(node.getStorageType()), value);
		}
	}

	public void setSubSection(String path, IConfiguration value) {
		if (!this.canAccess()) {
			return;
		}
		if (value == null) {
			this.remove(path);
			return;
		}
		if (!(value instanceof BinaryConfig)) {
			//TODO ConfigType conversion
			return;
		}
		PathComponent.PathComponentsList list = PathNavigator.parsePath(path, symType);
		BinaryConfig node = this.getParentObj(list);
		BinaryConfig bc = (BinaryConfig) value;
		Key key = list.last().getKey(node.data.getStorageType());
		bc.validate(node, key);
		node.data.set(key, bc.data);
	}
	
	public void remove(String path) {
		set(path, null);
	}
	
	@Override
	public String toString() {
		return "BinaryConfig=" + this.data.toString();
	}
}