package me.unei.configuration.api;

import me.unei.configuration.SavedFile;

public abstract class UntypedFlatStorage<T extends UntypedFlatStorage<T>> extends FlatConfiguration<T> {

    protected UntypedFlatStorage(SavedFile file) {
        super(file);
    }

    public double getDouble(String path) {
    	try {
    		return (Double.valueOf(getString(path)).doubleValue());
    	} catch (NumberFormatException nfe) {
    		return 0.0D;
    	}
    }

    public boolean getBoolean(String path) {
    	try {
    		return (Boolean.valueOf(getString(path)).booleanValue());
    	} catch (NumberFormatException nfe) {
    		return false;
    	}
    }

    public byte getByte(String path) {
    	try {
    		return (Byte.valueOf(getString(path)).byteValue());
    	} catch (NumberFormatException nfe) {
    		return (byte) 0;
    	}
    }

    public float getFloat(String path) {
    	try {
    		return (Float.valueOf(getString(path)).floatValue());
    	} catch (NumberFormatException nfe) {
    		return 0.0F;
    	}
    }

    public int getInteger(String path) {
    	try {
    		return (Integer.valueOf(getString(path)).intValue());
    	} catch (NumberFormatException nfe) {
    		return 0;
    	}
    }

    public long getLong(String path) {
    	try {
    		return (Long.valueOf(getString(path)).longValue());
    	} catch (NumberFormatException nfe) {
    		return 0L;
    	}
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