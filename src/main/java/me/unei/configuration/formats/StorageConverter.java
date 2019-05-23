package me.unei.configuration.formats;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;
import java.util.function.Supplier;

public class StorageConverter
{
	public static <T> IntegerHashMap<T> listToDiscontinued(AtomicIndexList<T> source)
	{
		if (source == null) {
			return null;
		}
		if (source.isEmpty()) {
			return new IntegerHashMap<>();
		}
		IntegerHashMap<T> result = new IntegerHashMap<>();
		for (int i = 0; i < source.size(); ++i) {
			result.put(Integer.valueOf(i), source.get(i));
		}
		return result;
	}
	
	public static <T> StringHashMap<T> listToMap(AtomicIndexList<T> source)
	{
		if (source == null) {
			return null;
		}
		if (source.isEmpty()) {
			return new StringHashMap<>();
		}
		StringHashMap<T> result = new StringHashMap<>();
		for (int i = 0; i < source.size(); ++i) {
			result.put(Integer.toString(i), source.get(i));
		}
		return result;
	}
	
	public static <T> StringHashMap<T> discontinuedToMap(IntegerHashMap<T> source)
	{
		if (source == null) {
			return null;
		}
		if (source.isEmpty()) {
			return new StringHashMap<>();
		}
		StringHashMap<T> result = new StringHashMap<>();
		for (Entry<Integer, T> elem : source.entrySet()) {
			result.put(elem.getKey().toString(), elem.getValue());
		}
		return result;
	}
	
	public static <T> IntegerHashMap<T> mapToDiscontinued(StringHashMap<T> source, boolean ignoreErrors)
	{
		if (source == null) {
			return null;
		}
		if (source.isEmpty()) {
			return new IntegerHashMap<>();
		}
		IntegerHashMap<T> result = new IntegerHashMap<>();
		for (Entry<String, T> elem : source.entrySet()) {
			try {
				Integer intVal = Integer.valueOf(elem.getKey());
				if (intVal >= 0) {
					result.put(intVal, elem.getValue());
				} else if (!ignoreErrors) {
					return null;
				}
			} catch (NumberFormatException nfe) {
				if (!ignoreErrors) {
					return null;
				}
			}
		}
		return result;
	}
	
	public static <T> AtomicIndexList<T> mapToListRaw(StringHashMap<T> source)
	{
		if (source == null) {
			return null;
		}
		if (source.isEmpty()) {
			return new AtomicIndexList<>();
		}
		AtomicIndexList<T> result = new AtomicIndexList<>();
		for (Entry<String, T> elem : source.entrySet()) {
			result.add(elem.getValue());
		}
		return result;
	}
	
	public static <T> AtomicIndexList<T> discontinuedToListRaw(IntegerHashMap<T> source)
	{
		if (source == null) {
			return null;
		}
		if (source.isEmpty()) {
			return new AtomicIndexList<>();
		}
		AtomicIndexList<T> result = new AtomicIndexList<>();
		for (Entry<Integer, T> elem : source.entrySet()) {
			result.add(elem.getValue());
		}
		return result;
	}
	
	public static <T> AtomicIndexList<T> discontinuedToListFull(IntegerHashMap<T> source)
	{
		if (source == null) {
			return null;
		}
		if (source.isEmpty()) {
			return new AtomicIndexList<>();
		}
		AtomicIndexList<T> result = new AtomicIndexList<>();
		TreeSet<Integer> indexes = new TreeSet<>();
		for (Integer elem : source.keySet()) {
			indexes.add(elem);
		}
		Iterator<Integer> idxIt = indexes.iterator();
		Integer val = null;
		for (int i = 0; i < (indexes.last().intValue()); ++i) {
			if ((val == null || val.intValue() < i) && idxIt.hasNext()) {
				val = idxIt.next();
			}
			if (val != null && val.intValue() == i) {
				result.add(i, source.get(val));
			} else {
				result.add(i, null);
			}
		}
		return result;
	}
	
	@SuppressWarnings("incomplete-switch")
	public static <T> Storage<T> convert(Storage<T> source, StorageType destType, boolean ignoreErrors)
	{
		if (source == null || destType == null) {
			return null;
		}
		if (destType == source.getStorageType())
		{
			return source;
		}
		if (source.getStorageType() == StorageType.UNDEFINED || destType == StorageType.UNDEFINED)
		{
			return source;
		}
		
		switch (destType)
		{
		case DISCONTINUED_LIST:
			switch (source.getStorageType())
			{
			case LIST:
				return listToDiscontinued((AtomicIndexList<T>) source);
			case MAP:
				return mapToDiscontinued((StringHashMap<T>) source, ignoreErrors);
			}
		case MAP:
			switch (source.getStorageType())
			{
			case LIST:
				return listToMap((AtomicIndexList<T>) source);
			case DISCONTINUED_LIST:
				return discontinuedToMap((IntegerHashMap<T>) source);
			}
		case LIST:
			switch (source.getStorageType())
			{
			case MAP:
				return mapToListRaw((StringHashMap<T>) source);
			case DISCONTINUED_LIST:
				return discontinuedToListFull((IntegerHashMap<T>) source);
			}
		}
		return null;
	}
	
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
	
	public static <T> Storage<T> getForType(StorageType type)
	{
		switch (type)
		{
		case MAP:
			return new StringHashMap<T>();
			
		case LIST:
			return new AtomicIndexList<T>();
			
		case DISCONTINUED_LIST:
			return new IntegerHashMap<T>();

		default:
			return null;
		}
	}
	
	private StorageConverter()
	{}
}
