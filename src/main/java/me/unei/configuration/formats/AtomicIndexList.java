package me.unei.configuration.formats;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;

public class AtomicIndexList<E> extends AbstractList<E> implements List<E>, Serializable, Cloneable, Storage<E>
{
	private static final long serialVersionUID = 3789885954514462522L;
	
	private final ArrayList<AtomicWrapper<E>> content;
	
	public AtomicIndexList() {
		this.content = new ArrayList<>();
	}

	@Override
	public StorageType getStorageType() {
		return StorageType.LIST;
	}
	
	@Override
	public int size() {
		return content.size();
	}

	@Override
	public E get(Key key) {
		if (key != null && key.getType() == this.getStorageType()) {
			if (key.getKeyInt() < 0 || key.getKeyInt() >= content.size()) {
				return null;
			}
			return content.get(key.getKeyInt()).element;
		}
		return null;
	}

	@Override
	public void set(Key key, E value) {
		if (key != null && key.getType() == this.getStorageType() && key.getKeyInt() >= 0) {
			AtomicWrapper<E> old;
			if (key.getKeyInt() < this.size()) {
				old = content.set(key.getKeyInt(), new AtomicWrapper<E>(key.getKeyAtomicInt(), value));
				if (old != null) {
					old.index.set(-1);
				}
			} else {
				content.add(key.getKeyInt(), new AtomicWrapper<E>(key.getKeyAtomicInt(), value));
			}
		}
	}
	
	@Override
	public E set(int index, E element) {
		AtomicWrapper<E> old = content.set(index, new AtomicWrapper<E>(index, element));
		return (old != null) ? old.element : null;
	}
	
	@Override
	public void add(int index, E element) {
		content.add(index, new AtomicWrapper<E>(index, element));
	}

	@Override
	public void remove(Key key) {
		if (key != null && key.getType() == this.getStorageType()) {
			if (key.getKeyInt() < 0 || key.getKeyInt() >= content.size()) {
				return;
			}
			AtomicWrapper<E> old = content.remove(key.getKeyInt());
			old.index.set(-1);
			for (int i = key.getKeyInt(); i < content.size(); ++i) {
				content.get(i).index.set(i);
			}
		}
	}
	
	@Override
	public E remove(int index) {
		if (index < 0 || index >= content.size()) {
			return null;
		}
		AtomicWrapper<E> old = content.remove(index);
		for (int i = index; i < content.size(); ++i) {
			content.get(i).index.set(i);
		}
		return (old != null) ? old.invalidate() : null;
	}
	
	@Override
	public void clear() {
		for (AtomicWrapper<E> elem : this.content) {
			elem.index.set(-1);
		}
		content.clear();
	}

	@Override
	public boolean has(Key key) {
		if (key != null && key.getType() == this.getStorageType()) {
			return (key.getKeyInt() >= 0 && key.getKeyInt() < content.size());
		}
		return false;
	}
	
	@Override
	public boolean hasValue(E value) {
		return content.contains(value);
	}

	@Override
	public Set<String> getKeys() {
		Set<String> r = new HashSet<String>(content.size());
		for (int i = 0; i < content.size(); ++i) {
			r.add(Integer.toString(i));
		}
		return r;
	}

	@Override
	public E get(int index) {
		if (index < 0 || index >= content.size()) {
			return null;
		}
		return content.get(index).element;
	}
	
	@Override
	public Iterator<Entry<Key, E>> entryIterator() {
		return new EntryIterator<E>(super.listIterator());
	}
	
	private static class EntryIterator<V> implements Iterator<Map.Entry<Key, V>>
	{
		private final ListIterator<V> originalIt;
		
		public EntryIterator(ListIterator<V> orig) {
			this.originalIt = orig;
		}
		
		@Override
		public boolean hasNext() {
			return originalIt.hasNext();
		}

		@Override
		public Entry<Key, V> next() {
			return new KeyEntry<>(originalIt.nextIndex(), originalIt.next(), originalIt::add);
		}
		
		@Override
		public void remove() {
			originalIt.remove();
		}
		
		@Override
		public void forEachRemaining(Consumer<? super Entry<Key, V>> action) {
			originalIt.forEachRemaining((entry) -> action.accept(new KeyEntry<V>(originalIt.previousIndex(), entry, originalIt::set)));
		}
		
		private static class KeyEntry<V> implements Map.Entry<Key, V>
		{
			private final Key key;
			private V value;
			private final Consumer<V> setter;
			
			public KeyEntry(int key, V value, Consumer<V> setter)
			{
				this.key = new Key(key);
				this.value = value;
				this.setter = setter;
			}
			
			@Override
			public Key getKey() {
				return key;
			}

			@Override
			public V getValue() {
				return value;
			}

			@Override
			public V setValue(V value) {
				this.setter.accept(value);
				V old = this.value;
				this.value = value;
				return old;
			}
		}
	}
}