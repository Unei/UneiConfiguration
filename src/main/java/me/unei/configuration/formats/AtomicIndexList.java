package me.unei.configuration.formats;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
			return content.get(key.getKeyInt()).element;
		}
		return null;
	}

	@Override
	public void set(Key key, E value) {
		if (key != null && key.getType() == this.getStorageType()) {
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
			AtomicWrapper<E> old = content.remove(key.getKeyInt());
			old.index.set(-1);
			for (int i = key.getKeyInt(); i < content.size(); ++i) {
				content.get(i).index.set(i);
			}
		}
	}
	
	@Override
	public E remove(int index) {
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
	public Set<String> getKeys() {
		Set<String> r = new HashSet<String>(content.size());
		for (int i = 0; i < content.size(); ++i) {
			r.add(Integer.toString(i));
		}
		return r;
	}

	@Override
	public E get(int index) {
		return content.get(index).element;
	}
}