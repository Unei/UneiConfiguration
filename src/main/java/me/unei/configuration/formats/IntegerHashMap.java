package me.unei.configuration.formats;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class IntegerHashMap<V> extends HashMap<Integer, V> implements Storage<V>
{
	private static final long serialVersionUID = 7697713067094562335L;
	
	@Override
	public StorageType getStorageType() {
		return StorageType.LIST;
	}
	
	@Override
	public V get(Key key) {
		if (key != null && key.getType() == this.getStorageType()) {
			return super.get(key.getKeyInt());
		}
		return null;
	}
	
	@Override
	public void set(Key key, V value) {
		if (key != null && key.getType() == this.getStorageType()) {
			super.put(key.getKeyInt(), value);
		}
	}
	
	@Override
	public void remove(Key key) {
		if (key != null && key.getType() == this.getStorageType()) {
			super.remove(key.getKeyInt());
		}
	}
	
	@Override
	public Iterator<V> iterator() {
		return super.values().iterator();
	}
	
	@Override
	public Set<String> getKeys() {
		Set<String> r = new HashSet<String>(this.size());
		for (Integer key : super.keySet()) {
			if (key != null && key.intValue() >= 0) {
				r.add(key.toString());
			}
		}
		return r;
	}
	
	@Override
	public boolean has(Key key) {
		if (key != null && key.getType() == this.getStorageType()) {
			return super.containsKey(key.getKeyInt());
		}
		return false;
	}
}