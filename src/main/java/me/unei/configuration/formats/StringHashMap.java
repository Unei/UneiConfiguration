package me.unei.configuration.formats;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class StringHashMap<V> extends HashMap<String, V> implements Storage<V> {
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

	@SuppressWarnings({
		"unchecked"
	})
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

	@Override
	public boolean hasValue(V value) {
		return super.containsValue(value);
	}

	@Override
	public Iterator<Entry<Key, V>> entryIterator() {
		return new EntryIterator<V>(super.entrySet().iterator());
	}

	private Iterable<Entry<Key, V>> entryIterable;

	@Override
	public Iterable<Entry<Key, V>> entryIterable() {
		if (entryIterable == null) {
			entryIterable = new EntryIterable();
		}
		return entryIterable;
	}

	private class EntryIterable implements Iterable<Map.Entry<Key, V>> {
		@Override
		public Iterator<Entry<Key, V>> iterator() {
			return entryIterator();
		}
	}

	private static class EntryIterator<V> implements Iterator<Map.Entry<Key, V>> {
		private final Iterator<Map.Entry<String, V>> originalIt;

		public EntryIterator(Iterator<Map.Entry<String, V>> orig) {
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

		private static class KeyEntry<V> implements Map.Entry<Key, V> {
			private final Map.Entry<String, V> originalEntry;
			private final Key key;

			public KeyEntry(Map.Entry<String, V> orig) {
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
