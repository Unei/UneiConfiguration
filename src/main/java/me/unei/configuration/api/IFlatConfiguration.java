package me.unei.configuration.api;

import java.util.Set;

import me.unei.configuration.SavedFile;
import me.unei.configuration.api.exceptions.FileFormatException;

public interface IFlatConfiguration {

    public SavedFile getFile();
    public String getFileName();
    public String getName();

    public boolean canAccess();
    public void lock();

    public void save();
    public void reload() throws FileFormatException;

    public Set<String> getKeys();

    public boolean contains(String key);

    public String getString(String key);
    public double getDouble(String key);
    public boolean getBoolean(String key);
    public byte getByte(String key);
    public float getFloat(String key);
    public int getInteger(String key);
    public long getLong(String key);

    public void setString(String key, String value);
    public void setDouble(String key, double value);
    public void setBoolean(String key, boolean value);
    public void setByte(String key, byte value);
    public void setFloat(String key, float value);
    public void setInteger(String key, int value);
    public void setLong(String key, long value);

    public void remove(String key);
}