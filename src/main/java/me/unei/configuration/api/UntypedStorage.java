package me.unei.configuration.api;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import me.unei.configuration.SavedFile;
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

    public String getString(String path) {
    	Object get = get(path);
    	if (get instanceof String) {
    		return (String) get;
    	}
    	return (get != null ? get.toString() : null);
        /*try {
            return (String) get(path);
        } catch (Exception e) {
            return "";
        }*/
    }

    public double getDouble(String path) {
        try {
            return ((Number) get(path)).doubleValue();
        } catch (Exception e) {
            try {
            	return (Double.valueOf(getString(path)).doubleValue());
            } catch (NumberFormatException nfe) {
            	return 0.0D;
            }
        }
    }

    public boolean getBoolean(String path) {
        try {
            return ((Boolean) get(path)).booleanValue();
        } catch (Exception e) {
            try {
            	return (Boolean.valueOf(getString(path)).booleanValue());
            } catch (NumberFormatException nfe) {
            	return false;
            }
        }
    }

    public byte getByte(String path) {
        try {
            return ((Number) get(path)).byteValue();
        } catch (Exception e) {
            try {
            	return (Byte.valueOf(getString(path)).byteValue());
            } catch (NumberFormatException nfe) {
            	return (byte) 0;
            }
        }
    }

    public short getShort(String path) {
        try {
            return ((Number) get(path)).shortValue();
        } catch (Exception e) {
            try {
            	return (Short.valueOf(getString(path)).shortValue());
            } catch (NumberFormatException nfe) {
            	return (short) 0;
            }
        }
    }

    public float getFloat(String path) {
        try {
            return ((Number) get(path)).floatValue();
        } catch (Exception e) {
            try {
            	return (Float.valueOf(getString(path)).floatValue());
            } catch (NumberFormatException nfe) {
            	return 0.0F;
            }
        }
    }

    public int getInteger(String path) {
        try {
            return ((Number) get(path)).intValue();
        } catch (Exception e) {
            try {
            	return (Integer.valueOf(getString(path)).intValue());
            } catch (NumberFormatException nfe) {
            	return 0;
            }
        }
    }

    public long getLong(String path) {
        try {
            return ((Number) get(path)).longValue();
        } catch (Exception e) {
            try {
            	return (Long.valueOf(getString(path)).longValue());
            } catch (NumberFormatException nfe) {
            	return 0L;
            }
        }
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