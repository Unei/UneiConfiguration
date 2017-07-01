package me.unei.configuration.api;

import java.util.ArrayList;
import java.util.List;

import me.unei.configuration.SavedFile;
import me.unei.configuration.api.fs.PathNavigator.PathSymbolsType;

public abstract class UntypedStorage<T extends UntypedStorage<T>> extends Configuration<T> {
	protected UntypedStorage(SavedFile file, PathSymbolsType symType) {
		super(file, symType);
	}

	protected UntypedStorage(T parent, String childName) {
		super(parent, childName);
	}

	public String getString(String path) {
		try {
			return (String) get(path);
		} catch (Exception e) {
			return "";
		}
	}
	
	public double getDouble(String path) {
		try {
			return ((Number) get(path)).doubleValue();
		} catch (Exception e) {
			return 0.0D;
		}
	}

    public boolean getBoolean(String path) {
        try {
            return ((Boolean) get(path)).booleanValue();
        } catch (Exception e) {
            return false;
        }
    }

    public byte getByte(String path) {
        try {
            return ((Number) get(path)).byteValue();
        } catch (Exception e) {
            return (byte) 0;
        }
    }

    public float getFloat(String path) {
        try {
            return ((Number) get(path)).floatValue();
        } catch (Exception e) {
            return 0.0F;
        }
    }

    public int getInteger(String path) {
        try {
            return ((Number) get(path)).intValue();
        } catch (Exception e) {
            return 0;
        }
    }

    public long getLong(String path) {
        try {
            return ((Number) get(path)).longValue();
        } catch (Exception e) {
            return 0L;
        }
    }

    public List<Byte> getByteList(String path) {
        try {
            List<Byte> list = new ArrayList<Byte>();
            for (Object value : (List<?>) get(path)) {
                list.add(((Number) value).byteValue());
            }
            return list;
        } catch (Exception e) {
            return null;
        }
    }

    public List<Integer> getIntegerList(String path) {
        try {
            List<Integer> list = new ArrayList<Integer>();
            for (Object value : (List<?>) get(path)) {
                list.add(((Number) value).intValue());
            }
            return list;
        } catch (Exception e) {
            return null;
        }
    }

    public List<Long> getLongList(String path) {
        try {
            List<Long> list = new ArrayList<Long>();
            for (Object value : (List<?>) get(path)) {
                list.add(((Number) value).longValue());
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