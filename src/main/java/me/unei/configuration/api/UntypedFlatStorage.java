package me.unei.configuration.api;

import me.unei.configuration.SavedFile;
import me.unei.configuration.api.exceptions.NoFieldException;

public abstract class UntypedFlatStorage<T extends UntypedFlatStorage<T>> extends FlatConfiguration<T> {

	protected UntypedFlatStorage(SavedFile file) {
		super(file);
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
		String val = getString(path);

		if (val == null) {
			throw new NoFieldException(path, getFile(), "No value for this key");
		}

		try {
			return Double.parseDouble(val);
		} catch (NumberFormatException nfe) {
			throw new NoFieldException(path, getFile(), "The value for this key could not be parsed as double");
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
		String val = getString(path);

		if (val == null) {
			throw new NoFieldException(path, getFile(), "No value for this key");
		}

		try {
			return Boolean.parseBoolean(val);
		} catch (NumberFormatException nfe) {
			throw new NoFieldException(path, getFile(), "The value for this key could not be parsed as boolean");
		}
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
		String val = getString(path);

		if (val == null) {
			throw new NoFieldException(path, getFile(), "No value for this key");
		}

		try {
			return Byte.parseByte(val);
		} catch (NumberFormatException nfe) {
			throw new NoFieldException(path, getFile(), "The value for this key could not be parsed as byte");
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
		String val = getString(path);

		if (val == null) {
			throw new NoFieldException(path, getFile(), "No value for this key");
		}

		try {
			return Short.parseShort(val);
		} catch (NumberFormatException nfe) {
			throw new NoFieldException(path, getFile(), "The value for this key could not be parsed as short");
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
		String val = getString(path);

		if (val == null) {
			throw new NoFieldException(path, getFile(), "No value for this key");
		}

		try {
			return Float.parseFloat(val);
		} catch (NumberFormatException nfe) {
			throw new NoFieldException(path, getFile(), "The value for this key could not be parsed as float");
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
		String val = getString(path);

		if (val == null) {
			throw new NoFieldException(path, getFile(), "No value for this key");
		}

		try {
			return Integer.parseInt(val);
		} catch (NumberFormatException nfe) {
			throw new NoFieldException(path, getFile(), "The value for this key could not be parsed as integer");
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
		String val = getString(path);

		if (val == null) {
			throw new NoFieldException(path, getFile(), "No value for this key");
		}

		try {
			return Long.parseLong(val);
		} catch (NumberFormatException nfe) {
			throw new NoFieldException(path, getFile(), "The value for this key could not be parsed as long");
		}
	}

	public long getLong(String path, long def) {
		Long val = getLong(path);
		return (val != null) ? val.longValue() : def;
	}

	public void setDouble(String path, double value) {
		setString(path, Double.toString(value));
	}

	public void setBoolean(String path, boolean value) {
		setString(path, Boolean.toString(value));
	}

	public void setByte(String path, byte value) {
		setString(path, Byte.toString(value));
	}

	public void setShort(String path, short value) {
		setString(path, Short.toString(value));
	}

	public void setFloat(String path, float value) {
		setString(path, Float.toString(value));
	}

	public void setInteger(String path, int value) {
		setString(path, Integer.toString(value));
	}

	public void setLong(String path, long value) {
		setString(path, Long.toString(value));
	}
}
