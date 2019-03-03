package me.unei.configuration.api.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.unei.configuration.SavedFile;
import me.unei.configuration.api.UntypedStorage;
import me.unei.configuration.api.fs.PathComponent;
import me.unei.configuration.api.fs.PathNavigator;
import me.unei.configuration.api.fs.PathComponent.PathComponentType;
import me.unei.configuration.api.fs.PathNavigator.PathSymbolsType;
import me.unei.configuration.formats.InfiniteSizeList;
import me.unei.configuration.formats.Storage;
import me.unei.configuration.formats.Storage.Key;
import me.unei.configuration.formats.StorageType;
import me.unei.configuration.formats.StringHashMap;

abstract class MappedConfiguration<A extends MappedConfiguration<A>> extends UntypedStorage<A>
{
	protected Storage<Object> data;
	
	public MappedConfiguration(SavedFile file, PathSymbolsType symType) {
		super(file, symType);
		
		this.data = new StringHashMap<Object>();
	}
	
	public MappedConfiguration(A parent, String tagName) {
		super(parent, tagName);
		
		this.updateFromParent();
		this.propagate();
	}
	
	public MappedConfiguration(A parent, int idx) {
		super(parent, idx);
		
		this.updateFromParent();
		this.propagate();
	}
	
	@SuppressWarnings({ "unchecked"})
	private void updateFromParent() {
		if (this.parent != null) {
			Storage<Object> sto = this.parent.data;
			if (sto != null && sto.getStorageType() != StorageType.UNDEFINED) {
				Object me = sto.get(Key.of(sto.getStorageType(), nodeIndex, nodeName));
				if (me != null && me instanceof Storage) {
					this.data = (Storage<Object>) me;
				} else if (me != null && me instanceof Map) {
					StringHashMap<Object> r = new StringHashMap<Object>();
					r.putAll((Map<String, Object>) me);
					this.data = r;
				} else if (me != null && me instanceof List) {
					InfiniteSizeList<Object> r = new InfiniteSizeList<Object>();
					r.addAll((List<Object>) me);
					this.data = r;
				} else {
					this.data = new StringHashMap<Object>();
				}
			}
		}
	}

	@Override
	protected void propagate() {
		if (this.parent != null) {
			Storage<Object> sto = this.parent.data;
			if (sto != null && sto.getStorageType() != StorageType.UNDEFINED) {
				sto.set(Key.of(sto.getStorageType(), nodeIndex, nodeName), this.data);
			}
			this.parent.propagate();
		}
	}
	
	public void setType(StorageType type) {
		if (!this.canAccess()) {
			return;
		}
		if (type != null && (this.data == null || this.data.getStorageType() != type)) {
			switch (type) {
			case MAP:
				this.data = new StringHashMap<Object>();
				break;
				
			case LIST:
				this.data = new InfiniteSizeList<Object>();
				break;
				
			default:
				throw new IllegalArgumentException("Cannot set the type to '" + type.name() + "'");
			}
		}
	}
	
	@Override
	public Set<String> getKeys() {
		return (this.data != null) ? this.data.keySet() : Collections.emptySet();
	}
	
	public StorageType getStorageType() {
		return ((this.data != null) ? this.data.getStorageType() : StorageType.UNDEFINED);
	}
	
	public static Key getFor(StorageType indice, PathComponent comp) {
		if (comp.getType() == PathComponentType.INDEX) {
			if (indice == StorageType.MAP) {
				return new Key(comp.getValue());
			}
			return new Key(comp.getIndex());
		}
		if (comp.getType() == PathComponentType.CHILD) {
			if (indice == StorageType.LIST) {
				return new Key(comp.getIndex());
			}
			return new Key(comp.getValue());
		}
		return null;
	}
	
	protected abstract A getThis();
	
	private Storage<Object> getParentData(PathComponent.PathComponentsList path) {
		PathNavigator<A> pn = new PathNavigator<A>(getThis());
		PathComponent.PathComponentsList pathList = PathNavigator.cleanPath(path);
		pathList.removeLast();
		pn.followPath(pathList);
		return pn.getCurrentNode().data;
	}
	
	public boolean contains(String path) {
		PathComponent.PathComponentsList list = PathNavigator.parsePath(path, symType);
		Storage<Object> holder = this.getParentData(list);
		return holder.has(getFor(holder.getStorageType(), list.last()));
	}

	public Object get(String path) {
		PathComponent.PathComponentsList list = PathNavigator.parsePath(path, symType);
		Storage<Object> holder = this.getParentData(list);
		return holder.get(getFor(holder.getStorageType(), list.last()));
	}
	
	public void set(String path, Object value) {
		if (!this.canAccess()) {
			return;
		}
		PathComponent.PathComponentsList list = PathNavigator.parsePath(path, symType);
		Storage<Object> holder = this.getParentData(list);
		if (value == null) {
			holder.remove(getFor(holder.getStorageType(), list.last()));
		} else {
			holder.set(getFor(holder.getStorageType(), list.last()), value);
		}
	}
	
	public void remove(String path) {
		set(path, null);
	}
}