package me.unei.configuration.formats;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class StringHashMap<V> extends HashMap<String, V> implements Storage<V>
{
	private static final long serialVersionUID = 7697713067094562335L;
	

    public StringHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public StringHashMap(int initialCapacity) {
        super(initialCapacity);
    }

    public StringHashMap() {
        super();
    }

    public StringHashMap(Map<String, ? extends V> m) {
    	super(m);
    }

    @SuppressWarnings({ "unchecked", "unused" })
	public StringHashMap(Map<?, ?> m, int ignored) {
    	super((Map<String, V>) m);
    }
	
	@Override
	public StorageType getStorageType() {
		return StorageType.MAP;
	}
	
	@Override
	public V get(Key key) {
		if (key != null && key.getType() == this.getStorageType()) {
			return super.get(key.getKeyString());
		}
		return null;
	}
	
	@Override
	public void set(Key key, V value) {
		if (key != null && key.getType() == this.getStorageType()) {
			super.put(key.getKeyString(), value);
		}
	}
	
	@Override
	public void remove(Key key) {
		if (key != null && key.getType() == this.getStorageType()) {
			super.remove(key.getKeyString());
		}
	}
	
	@Override
	public Iterator<V> iterator() {
		return super.values().iterator();
	}
	
	@Override
	public Set<String> getKeys() {
		return keySet();
	}
	
	@Override
	public boolean has(Key key) {
		if (key != null && key.getType() == this.getStorageType()) {
			return super.containsKey(key.getKeyString());
		}
		return false;
	}
}