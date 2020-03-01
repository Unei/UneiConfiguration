package me.unei.configuration.api;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import me.unei.configuration.SavedFile;
import me.unei.configuration.api.exceptions.NoFieldException;
import me.unei.configuration.api.fs.IPathNavigator.PathSymbolsType;

public abstract class UntypedStorage<T extends UntypedStorage<T>> extends Configuration<T> {

	protected UntypedStorage(SavedFile file, PathSymbolsType symType) {
		super(file, symType);
	}

	protected UntypedStorage(T parent, String childName) {
		super(parent, childName);
	}

	protected UntypedStorage(T parent, int idx) {
		super(parent, idx);
	}

	@Override
	public Object get(String path, Object def) {
		Object res = get(path);
		return (res != null) ? res : def;
	}

	public String getString(String path) {
		Object get = get(path);

		if (get instanceof String) {
			return (String) get;
		}
		return (get != null ? get.toString() : null);
	}

	@Override
	public String tryGetString(String key) throws NoFieldException {
		Object obj = tryGet(key);

		if (!(obj instanceof CharSequence)) {
			throw new NoFieldException(key, getFile(), "The value for this key is not a string");
		}
		return obj.toString();
	}

	public String getString(String path, String def) {
		String val = getString(path);
		return (val != null) ? val : def;
	}

	public Double getDouble(String path) {
		try {
			return tryGetDouble(path);
		} catch (NoFieldException e) {
			return null;
		}
	}

	public double tryGetDouble(String path) throws NoFieldException {
		Object val = get(path);

		if (val == null) {
			throw new NoFieldException(path, getFile(), "No value for this key");
		}

		if (val instanceof Number) {
			return ((Number) val).doubleValue();
		} else {

			try {
				return Double.parseDouble(val.toString());
			} catch (NumberFormatException nfe) {
				throw new NoFieldException(path, getFile(), "The value for this key could not be parsed as double");
			}
		}
	}

	public double getDouble(String path, double def) {
		Double val = getDouble(path);
		return (val != null) ? val.doubleValue() : def;
	}

	public Boolean getBoolean(String path) {
		try {
			return tryGetBoolean(path);
		} catch (NoFieldException nfe) {
			return null;
		}
	}

	public boolean tryGetBoolean(String path) throws NoFieldException {
		Object val = get(path);

		if (val == null) {
			throw new NoFieldException(path, getFile(), "No value for this key");
		}

		if (val instanceof Boolean) {
			return ((Boolean) val).booleanValue();
		} else if (val instanceof Number) {
			return ((Number) val).byteValue() == 0 ? false : true;
		} else if (Boolean.parseBoolean(val.toString())) {
			return true;
		}
		throw new NoFieldException(path, getFile(), "The value could not be parsed as boolean");
	}

	public boolean getBoolean(String path, boolean def) {
		Boolean val = getBoolean(path);
		return (val != null) ? val.booleanValue() : def;
	}

	public Byte getByte(String path) {
		try {
			return tryGetByte(path);
		} catch (NoFieldException nfe) {
			return null;
		}
	}

	public byte tryGetByte(String path) throws NoFieldException {
		Object val = get(path);

		if (val == null) {
			throw new NoFieldException(path, getFile(), "No value for this key");
		}

		if (val instanceof Number) {
			return ((Number) val).byteValue();
		} else {

			try {
				return Byte.parseByte(val.toString());
			} catch (NumberFormatException nfe) {
				throw new NoFieldException(path, getFile(), "The value for this key could not be parsed as byte");
			}
		}
	}

	public byte getByte(String path, byte def) {
		Byte val = getByte(path);
		return (val != null) ? val.byteValue() : def;
	}

	public Short getShort(String path) {
		try {
			return tryGetShort(path);
		} catch (NoFieldException nfe) {
			return null;
		}
	}

	public short tryGetShort(String path) throws NoFieldException {
		Object val = get(path);

		if (val == null) {
			throw new NoFieldException(path, getFile(), "No value for this key");
		}

		if (val instanceof Number) {
			return ((Number) val).shortValue();
		} else {

			try {
				return Short.parseShort(val.toString());
			} catch (NumberFormatException nfe) {
				throw new NoFieldException(path, getFile(), "The value for this key could not be parsed as short");
			}
		}
	}

	public short getShort(String path, short def) {
		Short val = getShort(path);
		return (val != null) ? val.shortValue() : def;
	}

	public Float getFloat(String path) {
		try {
			return tryGetFloat(path);
		} catch (NoFieldException nfe) {
			return null;
		}
	}

	public float tryGetFloat(String path) throws NoFieldException {
		Object val = get(path);

		if (val == null) {
			throw new NoFieldException(path, getFile(), "No value for this key");
		}

		if (val instanceof Number) {
			return ((Number) val).floatValue();
		} else {

			try {
				return Float.parseFloat(val.toString());
			} catch (NumberFormatException nfe) {
				throw new NoFieldException(path, getFile(), "The value for this key could not be parsed as float");
			}
		}
	}

	public float getFloat(String path, float def) {
		Float val = getFloat(path);
		return (val != null) ? val.floatValue() : def;
	}

	public Integer getInteger(String path) {
		try {
			return tryGetInteger(path);
		} catch (NoFieldException nfe) {
			return null;
		}
	}

	public int tryGetInteger(String path) throws NoFieldException {
		Object val = get(path);

		if (val == null) {
			throw new NoFieldException(path, getFile(), "No value for this key");
		}

		if (val instanceof Number) {
			return ((Number) val).intValue();
		} else {

			try {
				return Integer.parseInt(val.toString());
			} catch (NumberFormatException nfe) {
				throw new NoFieldException(path, getFile(), "The value for this key could not be parsed as integer");
			}
		}
	}

	public int getInteger(String path, int def) {
		Integer val = getInteger(path);
		return (val != null) ? val.intValue() : def;
	}

	public Long getLong(String path) {
		try {
			return tryGetLong(path);
		} catch (NoFieldException nfe) {
			return null;
		}
	}

	public long tryGetLong(String path) throws NoFieldException {
		Object val = get(path);

		if (val == null) {
			throw new NoFieldException(path, getFile(), "No value for this key");
		}

		if (val instanceof Number) {
			return ((Number) val).longValue();
		} else {

			try {
				return Long.parseLong(val.toString());
			} catch (NumberFormatException nfe) {
				throw new NoFieldException(path, getFile(), "The value for this key could not be parsed as long");
			}
		}
	}

	public long getLong(String path, long def) {
		Long val = getLong(path);
		return (val != null) ? val.longValue() : def;
	}

	public List<Byte> getByteList(String path) {
		try {
			List<Byte> list = new ArrayList<Byte>();
			Object obj = get(path);

			if (obj instanceof Iterable<?>) {

				for (Object value : (Iterable<?>) obj) {
					list.add(((Number) value).byteValue());
				}
			} else if (obj.getClass().isArray()) {

				for (int i = 0; i < Array.getLength(obj); ++i) {
					list.add(((Number) Array.get(obj, i)).byteValue());
				}
			} else {
				return null;
			}
			return list;
		} catch (Exception e) {
			return null;
		}
	}

	public List<Integer> getIntegerList(String path) {
		try {
			List<Integer> list = new ArrayList<Integer>();
			Object obj = get(path);

			if (obj instanceof Iterable<?>) {

				for (Object value : (Iterable<?>) obj) {
					list.add(((Number) value).intValue());
				}
			} else if (obj.getClass().isArray()) {

				for (int i = 0; i < Array.getLength(obj); ++i) {
					list.add(((Number) Array.get(obj, i)).intValue());
				}
			} else {
				return null;
			}
			return list;
		} catch (Exception e) {
			return null;
		}
	}

	public List<Long> getLongList(String path) {
		try {
			List<Long> list = new ArrayList<Long>();
			Object obj = get(path);

			if (obj instanceof Iterable<?>) {

				for (Object value : (Iterable<?>) obj) {
					list.add(((Number) value).longValue());
				}
			} else if (obj.getClass().isArray()) {

				for (int i = 0; i < Array.getLength(obj); ++i) {
					list.add(((Number) Array.get(obj, i)).longValue());
				}
			} else {
				return null;
			}
			return list;
		} catch (Exception e) {
			return null;
		}
	}

	public void setString(String path, String value) {
		set(path, value);
	}

	public void setDouble(String path, double value) {
		set(path, value);
	}

	public void setBoolean(String path, boolean value) {
		set(path, value);
	}

	public void setByte(String path, byte value) {
		set(path, value);
	}

	public void setShort(String path, short value) {
		set(path, value);
	}

	public void setFloat(String path, float value) {
		set(path, value);
	}

	public void setInteger(String path, int value) {
		set(path, value);
	}

	public void setLong(String path, long value) {
		set(path, value);
	}

	public void setByteList(String path, List<Byte> value) {
		set(path, value);
	}

	public void setIntegerList(String path, List<Integer> value) {
		set(path, value);
	}

	public void setLongList(String path, List<Long> value) {
		set(path, value);
	}
}
