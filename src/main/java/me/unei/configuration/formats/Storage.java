package me.unei.configuration.formats;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public interface Storage<V> extends Iterable<V>
{
	public StorageType getStorageType();
	
	public int size();
	
	public boolean isEmpty();
	
	public void clear();
	
	public V get(Key key);
	
	public void set(Key key, V value);
	
	public void remove(Key key);
	
	public boolean has(Key key);
	
	public boolean hasValue(V value);
	
	public Set<String> getKeys();
	
	public Iterator<Map.Entry<Key, V>> entryIterator();
	
	public default Iterable<Map.Entry<Key, V>> entryIterable()
	{
		return new Iterable<Map.Entry<Key,V>>() {
			@Override
			public Iterator<Entry<Key, V>> iterator() {
				return entryIterator();
			}
		};
	}
	
	public static final class Converter
	{
		@SuppressWarnings("unchecked")
		public static <T> Storage<T> allocateBest(Object origin, Class<T> type, Supplier<Storage<T>> defaultVal)
		{
			if (origin == null)
			{
				return (defaultVal != null) ? defaultVal.get() : null;
			}
			if (origin instanceof Storage)
			{
				Storage<?> sto = (Storage<?>) origin;
				return ((Storage<T>) sto);
			}
			else if (origin instanceof Iterable)
			{
				Iterable<?> list = (Iterable<?>) origin;
				AtomicIndexList<T> result = new AtomicIndexList<T>();
				for (Object o : list)
				{
					if (type == null || type.isInstance(o))
					{
						result.add((T) o);
					}
				}
				return result;
			}
			else if (origin instanceof Map)
			{
				Map<?, ?> map = (Map<?, ?>) origin;
				if (map.isEmpty())
				{
					return new StringHashMap<T>();
				}
				Storage<T> result = null;
				boolean isStr = true;
				boolean isNbr = true;
				for (Object key : map.keySet())
				{
					if (!(key instanceof CharSequence))
					{
						isStr = false;
					}
					if (!(key instanceof Number))
					{
						isNbr = false;
					}
				}
				if (isStr)
				{
					result = new StringHashMap<T>();
					for (Entry<?, ?> entry : map.entrySet())
					{
						if (type == null || type.isInstance(entry.getValue()))
						{
							((Map<String, T>) result).put(entry.getKey().toString(), (T) entry.getValue());
						}
					}
				}
				else if (isNbr)
				{
					result = new IntegerHashMap<T>();
					for (Entry<?, ?> entry : map.entrySet())
					{
						if (type == null || type.isInstance(entry.getValue()))
						{
							((Map<Integer, T>) result).put(((Number) entry.getKey()).intValue(), (T) entry.getValue());
						}
					}
				}
				return result;
			}
			return (defaultVal != null) ? defaultVal.get() : null;
		} 
		
		private Converter()
		{}
	}
	
	public static class Key
	{
		private final AtomicInteger keyAtomicInt;
		private final String keyString;
		private final StorageType type;
		
		public Key(int key)
		{
			this.keyAtomicInt = new AtomicInteger(key);
			this.keyString = null;
			this.type = StorageType.LIST;
		}
		
		public Key(String key)
		{
			this.keyAtomicInt = null;
			this.keyString = key;
			this.type = StorageType.MAP;
		}
		
		public Key(Object key)
		{
			if (key instanceof CharSequence)
			{
				this.keyAtomicInt = null;
				this.keyString = key.toString();
				this.type = StorageType.MAP;
			}
			else if (key instanceof AtomicInteger)
			{
				this.keyAtomicInt = (AtomicInteger) key;
				this.keyString = null;
				this.type = StorageType.LIST;
			}
			else if (key instanceof Number)
			{
				this.keyAtomicInt = new AtomicInteger(((Number) key).intValue());
				this.keyString = null;
				this.type = StorageType.LIST;
			}
			else
			{
				throw new IllegalArgumentException("key must be either an integer or a string. Not a type of: " + key.getClass().getName());
			}
		}
		
		public static Key of(StorageType type, int idx, String name)
		{
			switch (type) {
			case MAP:
				return new Key(name);
				
			case LIST:
				return new Key(idx);
				
			default:
				return null;
			}
		}
		
		public static Key of(StorageType type, AtomicInteger idx, String name)
		{
			switch (type) {
			case MAP:
				return new Key(name);
				
			case LIST:
				return new Key(idx);
				
			default:
				return null;
			}
		}
		
		public int getKeyInt()
		{
			return (this.keyAtomicInt != null) ? this.keyAtomicInt.get() : -1;
		}
		
		public AtomicInteger getKeyAtomicInt()
		{
			return this.keyAtomicInt;
		}
		
		public String getKeyString()
		{
			return this.keyString;
		}
		
		@Override
		public String toString() {
			return "Key=" + ((StorageType.LIST == this.type) ? this.keyAtomicInt.toString() : this.keyString) + ";";
		}
		
		public StorageType getType()
		{
			return this.type;
		}
	}
}