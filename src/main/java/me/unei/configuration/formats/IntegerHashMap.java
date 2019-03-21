package me.unei.configuration.formats;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class IntegerHashMap<V> extends HashMap<Integer, V> implements Storage<V>
{
	private static final long serialVersionUID = 7697713067094562335L;
	
	@Override
	public StorageType getStorageType() {
		return StorageType.DISCONTINUED_LIST;
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
	
	@Override
	public boolean hasValue(V value) {
		return super.containsValue(value);
	}
	
	@Override
	public Iterator<Entry<Key, V>> entryIterator() {
		return new EntryIterator<V>(super.entrySet().iterator());
	}
	
	private static class EntryIterator<V> implements Iterator<Map.Entry<Key, V>>
	{
		private final Iterator<Map.Entry<Integer, V>> originalIt;
		
		public EntryIterator(Iterator<Map.Entry<Integer, V>> orig) {
			this.originalIt = orig;
		}
		
		@Override
		public boolean hasNext() {
			return originalIt.hasNext();
		}

		@Override
		public Entry<Key, V> next() {
			return new KeyEntry<>(originalIt.next());
		}
		
		@Override
		public void remove() {
			originalIt.remove();
		}
		
		@Override
		public void forEachRemaining(Consumer<? super Entry<Key, V>> action) {
			originalIt.forEachRemaining((entry) -> action.accept(new KeyEntry<V>(entry)));
		}
		
		private static class KeyEntry<V> implements Map.Entry<Key, V>
		{
			private final Map.Entry<Integer, V> originalEntry;
			private final Key key;
			
			public KeyEntry(Map.Entry<Integer, V> orig)
			{
				this.originalEntry = orig;
				this.key = new Key(orig.getKey());
			}
			
			@Override
			public Key getKey() {
				return key;
			}

			@Override
			public V getValue() {
				return originalEntry.getValue();
			}

			@Override
			public V setValue(V value) {
				return originalEntry.setValue(value);
			}
		}
	}
}