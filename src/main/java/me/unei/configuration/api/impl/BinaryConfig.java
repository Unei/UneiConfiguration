package me.unei.configuration.api.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import me.unei.configuration.SavedFile;
import me.unei.configuration.api.IConfiguration;
import me.unei.configuration.api.UntypedStorage;
import me.unei.configuration.api.exceptions.FileFormatException;
import me.unei.configuration.api.fs.PathComponent;
import me.unei.configuration.api.fs.PathNavigator;
import me.unei.configuration.api.fs.PathNavigator.PathSymbolsType;
import me.unei.configuration.formats.IntegerKeyMap;
import me.unei.configuration.plugin.UneiConfiguration;

public final class BinaryConfig extends UntypedStorage<BinaryConfig> implements IConfiguration {
	
	public static final String BINARY_FILE_EXT = ".bin";
	public static final String BINARY_TMP_EXT = ".tmp";
	
	private SectionType type;
	
	private Map<String, Object> data = new HashMap<String, Object>();
	
	private IntegerKeyMap<Object> dataAsList = new IntegerKeyMap<Object>();
	
	@Deprecated
	final Map<String, Object> getData() {
		return data;
	}
	
	@Deprecated
	final Map<Integer, Object> getDataList() {
		return dataAsList;
	}
	
	private void reset()
	{
		this.data = null;
		this.dataAsList = null;
	}
	
	@SuppressWarnings("unchecked")
	private void setup(SectionType type, Map<?, Object> instance)
	{
		this.reset();
		this.type = type;
		switch (type)
		{
		case MAP:
			this.data = (HashMap<String, Object>) instance;
			break;
			
		case LIST:
			this.dataAsList = (IntegerKeyMap<Object>) instance;
			break;
		}
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
		
		this.updateFromParent();
		this.propagate();
	}
	
	public BinaryConfig(BinaryConfig parent, int idx) {
		super(parent, idx);
		
		this.updateFromParent();
		this.propagate();
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
	
	private Map<?, Object> getMyData() {
		switch (this.type) {
		case MAP:
			return this.data;
			
		case LIST:
			return this.dataAsList;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private void updateFromParent() {
		if (this.parent != null) {
			switch (this.parent.type)
			{
			case MAP:
				if (this.parent.data != null)
				{
					Object me = this.parent.data.get(nodeName);
					if (me != null && (me instanceof Map)) {
						this.setup((me instanceof IntegerKeyMap) ? SectionType.LIST : SectionType.MAP, (Map<?, Object>) me);
					}
				}
				break;

			case LIST:
				if (this.parent.dataAsList != null)
				{
					Object me = this.parent.dataAsList.get(nodeIndex);
					if (me != null && (me instanceof Map)) {
						this.setup((me instanceof IntegerKeyMap) ? SectionType.LIST : SectionType.MAP, (Map<?, Object>) me);
					}
				}
				break;
			}
		}
	}
	
	@Override
	protected void propagate() {
		if (this.parent != null) {
			switch (this.parent.type)
			{
			case MAP:
			this.parent.data.put(this.nodeName, this.getMyData());
				break;
				
			case LIST:
				this.parent.dataAsList.put(this.nodeIndex, this.getMyData());
				break;
			}
			this.parent.propagate();
		}
	}
	
	@Override
	public SectionType getType() {
		return this.type;
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
	
	public BinaryConfig getAt(int idx) {
		if (!this.canAccess()) {
			return null;
		}
		if (idx < 0) {
			return this;
		}
		return new BinaryConfig(this, idx);
	}
	
	private Map<String, Object> getParentMap(PathComponent.PathComponentsList path) {
		PathNavigator<BinaryConfig> pn = new PathNavigator<BinaryConfig>(this);
		PathComponent.PathComponentsList pathList = PathNavigator.cleanPath(path);
		pathList.removeLast();
		pn.followPath(pathList);
		BinaryConfig current = pn.getCurrentNode();
		if (current.data == null) {
			current.data = new HashMap<String, Object>();
		}
		return current.data;
	}
	
	private IntegerKeyMap<Object> getParentList(PathComponent.PathComponentsList path) {
		PathNavigator<BinaryConfig> pn = new PathNavigator<BinaryConfig>(this);
		PathComponent.PathComponentsList pathList = PathNavigator.cleanPath(path);
		pathList.removeLast();
		pn.followPath(pathList);
		BinaryConfig current = pn.getCurrentNode();
		if (current.dataAsList == null) {
			current.dataAsList = new IntegerKeyMap<Object>();
		}
		return current.dataAsList;
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
			oos.writeObject(this.getMyData());
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
	
	@SuppressWarnings("unchecked")
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
			if (result != null && (result instanceof Map)) {
				Map<?, ?> tmpData = (Map<?, ?>) result;
				if (tmpData instanceof IntegerKeyMap) {
					this.setup(SectionType.LIST, (Map<?, Object>) tmpData);
				} else {
					this.setup(SectionType.MAP, (Map<?, Object>) tmpData);
				}
			}
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
		return this.data.keySet();
	}
	
	public boolean contains(String path) {
		PathComponent.PathComponentsList list = PathNavigator.parsePath(path, symType);
		switch (this.type) {
		case MAP:
			Map<String, Object> node = this.getParentMap(list);
			return node.containsKey(list.lastChild());
			
		case LIST:
			IntegerKeyMap<Object> nodeList = this.getParentList(list);
			return nodeList.containsKey(list.lastIndex());
		}
		return false;
	}

	public Object get(String path) {
		PathComponent.PathComponentsList list = PathNavigator.parsePath(path, symType);
		switch (this.type) {
		case MAP:
			Map<String, Object> node = this.getParentMap(list);
			return node.get(list.lastChild());
			
		case LIST:
			IntegerKeyMap<Object> nodeList = this.getParentList(list);
			return nodeList.get(list.lastIndex());
		}
		return null;
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
		switch (this.type) {
		case MAP:
			Map<String, Object> node = this.getParentMap(list);
			if (value == null) {
				node.remove(list.lastChild());
			} else {
				node.put(list.lastChild(), value);
			}
			break;
			
		case LIST:
			IntegerKeyMap<Object> nodeList = this.getParentList(list);
			if (value == null) {
				nodeList.remove(list.lastIndex());
			} else {
				nodeList.put(list.lastIndex(), value);
			}
			break;
		}
	}
	
	public void setSubSection(String path, IConfiguration value) {
		if (!(value instanceof BinaryConfig)) {
			//TODO ConfigType conversion
			return;
		}
		set(path, ((BinaryConfig) value).getMyData());
	}
	
	public void remove(String path) {
		set(path, null);
	}
	
	@Override
	public String toString() {
		switch (this.type)
		{
		case MAP:
			return "BinaryConfig{}=" + this.data.toString();
			
		case LIST:
			return "BinaryConfig[]=" + this.dataAsList.toString();
		}
		return "BinaryConfig=ERROR";
	}
}