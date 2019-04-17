package me.unei.configuration.formats;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

public class StorageConverter
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
	
	private StorageConverter()
	{}
}
