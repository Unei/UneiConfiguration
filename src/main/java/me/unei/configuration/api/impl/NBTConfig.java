package me.unei.configuration.api.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;

import me.unei.configuration.SavedFile;
import me.unei.configuration.api.IConfiguration;
import me.unei.configuration.api.IInternalStorageUse;
import me.unei.configuration.api.INBTConfiguration;
import me.unei.configuration.api.UntypedStorage;
import me.unei.configuration.api.Configurations.ConfigurationType;
import me.unei.configuration.api.exceptions.NoFieldException;
import me.unei.configuration.api.format.INBTTag;
import me.unei.configuration.api.fs.IPathNavigator.PathSymbolsType;
import me.unei.configuration.api.fs.PathComponent;
import me.unei.configuration.api.fs.PathNavigator;
import me.unei.configuration.formats.AtomicIndexList;
import me.unei.configuration.formats.Storage;
import me.unei.configuration.formats.StorageType;
import me.unei.configuration.formats.StringHashMap;
import me.unei.configuration.formats.Storage.Key;
import me.unei.configuration.formats.StorageConverter;
import me.unei.configuration.formats.nbtlib.NBTIO;
import me.unei.configuration.formats.nbtlib.Tag;
import me.unei.configuration.formats.nbtlib.TagCompound;
import me.unei.configuration.formats.nbtlib.TagList;
import me.unei.configuration.plugin.UneiConfiguration;

/**
 * @version 2.5.0
 * @since 0.0.1
 */
public final class NBTConfig extends UntypedStorage<NBTConfig> implements INBTConfiguration, IInternalStorageUse {

	public static final String NBT_FILE_EXT = ".dat";
	public static final String NBT_TMP_EXT = ".tmp";

	private Storage<Object> data = null;

	@Override
	public final Storage<Object> getStorageObject() {
		if (data == null) {
			data = new StringHashMap<Object>();
		}
		return data;
	}

	@Override
	public final void setStorageObject(Storage<Object> sto) {
		this.data = sto;
		propagate();
	}

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

		this.updateNode();
	}

	private NBTConfig(NBTConfig p_parent, int p_tagIndex) {
		super(p_parent, p_tagIndex);

		this.updateNode();
	}

	@Override
	public ConfigurationType getConfigurationType() {
		return ConfigurationType.NBT;
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
		NBTConfig child = super.findInChildrens(new Key(name));

		if (child != null) {
			child.parent = this;
			return child;
		}
		return new NBTConfig(this, name);
	}

	@Override
	public NBTConfig getAt(int index) {
		if (!this.canAccess()) {
			return null;
		}

		if (index < 0) {
			return this;
		}
		NBTConfig child = super.findInChildrens(new Key(index));

		if (child != null) {
			child.parent = this;
			return child;
		}
		return new NBTConfig(this, index);
	}

	@Override
	public StorageType getType() {
		return (this.data != null) ? this.data.getStorageType() : StorageType.UNDEFINED;
	}

	@Override
	protected void updateFromParent() {
		if (this.parent != null && this.parent.data != null) {

			if (this.parent.getStorageObject().getStorageType() != StorageType.UNDEFINED) {
				Object me = this.parent.data.get(Key.of(this.parent.getType(), nodeAtomicIndex, nodeName));
				Storage<Object> tmp = StorageConverter.allocateBest(me, null, null);

				if (tmp != null) {
					this.data = tmp;
				} else {
					this.data = new StringHashMap<Object>();
				}
				propagate();
			}
		} else if (this.parent == null) {
			this.data = new StringHashMap<Object>(); // Root must be a string-key map.
		}
	}

	private void propagate() {
		if (this.parent != null && this.parent.data != null) {

			if (this.parent.getStorageObject().getStorageType() != StorageType.UNDEFINED) {
				this.parent.data.set(Key.of(this.parent.getType(), nodeAtomicIndex, nodeName), this.data);
			}
		}
	}

	@Override
	public void setType(StorageType type) {
		if (this.parent == null && type != this.getType()) {
			throw new UnsupportedOperationException(
					"Cannot change the type of a NBT root Tag to anything but " + this.getType().name());
		}

		if (type == StorageType.DISCONTINUED_LIST) {
			throw new UnsupportedOperationException("Cannot set the type of any NBT Tag to " + type.name());
		}

		if (type == this.getType()) {
			return;
		}
		this.data.clear();
		this.data = null;

		switch (type) {
			case MAP:
				setStorageObject(new StringHashMap<Object>());
				break;

			case LIST:
				setStorageObject(new AtomicIndexList<Object>());
				break;

			default:
				setStorageObject(new StringHashMap<Object>());
				throw new IllegalArgumentException("Error while setting new NBT node type");
		}
	}

	private Tag getTagCp() {
		switch (this.getType()) {
			case MAP:
				TagCompound resultMap = new TagCompound();
				resultMap.loadMap((StringHashMap<Object>) data);
				return resultMap;

			case LIST:
				TagList resultList = new TagList();
				resultList.loadList(data);
				return resultList;

			default:
				return new TagCompound();
		}
	}

	private NBTConfig getParentObj(PathComponent.PathComponentsList path) {
		PathNavigator<NBTConfig> pn = new PathNavigator<NBTConfig>(this);
		PathComponent.PathComponentsList pathList = PathNavigator.cleanPath(path);
		pathList.removeLast();

		if (!pn.followPath(pathList)) {
			return this;
		}
		return pn.getCurrentNode();
	}

	private Storage<Object> getParentMap(PathComponent.PathComponentsList path) {
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

	public INBTTag getTagCopy() {
		return this.getTagCp();
	}

	private void setTagCp(Tag tag) {
		if (!this.canAccess() || tag == null) {
			return;
		}

		if (tag instanceof TagCompound) {
			TagCompound compound = (TagCompound) tag;
			this.data = compound.getAsObject(new Tag.ObjectCreator<StringHashMap<Object>, AtomicIndexList<Object>>() {
				@Override
				public StringHashMap<Object> newMap() {
					return new StringHashMap<Object>();
				}

				@Override
				public AtomicIndexList<Object> newList() {
					return new AtomicIndexList<Object>();
				}
			});
		} else if (tag instanceof TagList) {
			TagList list = (TagList) tag;
			this.data = list.getAsObject(new Tag.ObjectCreator<StringHashMap<Object>, AtomicIndexList<Object>>() {
				@Override
				public StringHashMap<Object> newMap() {
					return new StringHashMap<Object>();
				}

				@Override
				public AtomicIndexList<Object> newList() {
					return new AtomicIndexList<Object>();
				}
			});
		}
		propagate();
	}

	public void setTagCopy(INBTTag compound) {
		this.setTagCp((Tag) compound);
	}

	public void reload() {
		if (!this.canAccess()) {
			return;
		}

		if (this.parent != null) {
			this.parent.reload();
		} else {

			if (this.file.getFile() == null) {
				this.data = new StringHashMap<>();
				this.runTreeUpdate();
				return;
			}

			if (!this.file.getFile().exists()) {
				this.save();
				return;
			}

			if (this.getType() != StorageType.MAP) {
				this.data = new StringHashMap<Object>();
			}
			TagCompound compound = null;

			try {
				UneiConfiguration.getInstance().getLogger()
						.fine("Reading NBT Compound from file " + getFileName() + "...");
				compound = NBTIO.readCompressed(new FileInputStream(file.getFile()));
				UneiConfiguration.getInstance().getLogger().fine("Successfully read.");
			} catch (IOException e) {
				UneiConfiguration.getInstance().getLogger()
						.warning("An error occured while loading NBT file " + getFileName() + ":");
				e.printStackTrace();
				return;
			}

			if (compound != null) {
				this.setTagCp(compound);
			} else {
				this.data = new StringHashMap<Object>();
			}
			this.runTreeUpdate();
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
		compound.loadMap((StringHashMap<Object>) getStorageObject());

		try {
			UneiConfiguration.getInstance().getLogger().fine("Writing NBT Compound to file " + getFileName() + "...");
			NBTIO.writeCompressed(compound, new FileOutputStream(tmp));

			if (this.file.getFile().exists()) {
				UneiConfiguration.getInstance().getLogger()
						.finer("Replacing already present file " + getFileName() + ".");
				this.file.getFile().delete();
			}
			tmp.renameTo(this.file.getFile());
			tmp.delete();
			UneiConfiguration.getInstance().getLogger().fine("Successfully written.");
		} catch (IOException e) {
			UneiConfiguration.getInstance().getLogger()
					.warning("An error occured while saving NBT file " + getFileName() + ":");
			e.printStackTrace();
		}
	}

	public Set<String> getKeys() {
		return this.data.getKeys();
	}

	public boolean contains(String path) {
		PathComponent.PathComponentsList list = PathNavigator.parsePath(path, symType);
		Storage<Object> node = this.getParentMap(list);
		return node.has(list.last().getKey(node.getStorageType()));
	}

	@Override
	public Object get(String path) {
		PathComponent.PathComponentsList list = PathNavigator.parsePath(path, symType);
		Storage<Object> node = this.getParentMap(list);
		Key key = list.last().getKey(node.getStorageType());

		if (!node.has(key)) {
			return null;
		}
		return node.get(key);
	}

	@Override
	public Object tryGet(String path) throws NoFieldException {
		PathComponent.PathComponentsList list = PathNavigator.parsePath(path, symType);
		Storage<Object> node = this.getParentMap(list);
		Key key = list.last().getKey(node.getStorageType());

		if (!node.has(key)) {
			throw new NoFieldException(path, getFile(), "No value is associated to this key");
		}
		return node.get(key);
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
			// TODO ConfigType conversion
			return;
		}
		PathComponent.PathComponentsList list = PathNavigator.parsePath(path, symType);
		NBTConfig node = this.getParentObj(list);
		Key key = list.last().getKey(node.getType());
		((NBTConfig) value).validate(node, key);
		node.data.set(key, ((NBTConfig) value).data);
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
	public NBTConfig getSubSection(PathComponent.PathComponentsList path) {
		if (!this.canAccess()) {
			return null;
		}
		PathNavigator<NBTConfig> navi = new PathNavigator<NBTConfig>(this);
		navi.followPath(path);
		return navi.getCurrentNode();
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
		Storage<Object> node = this.getParentMap(list);
		node.set(list.last().getKey(node.getStorageType()), value);
	}

	@Override
	public String toString() {
		return "NBTConfig=" + this.data.toString();
	}
}
