package me.unei.configuration.api;

import me.unei.configuration.SavedFile;

import java.util.List;

public interface IConfiguration {

    public SavedFile getFile();
    public String getFileName();
    public String getName();
    public String getCurrentPath();

    public boolean canAccess();
    public void lock();

    public IConfiguration getRoot();
    public IConfiguration getParent();

    public void save();
    public void reload();

    public boolean contains(String key);

    public String getString(String key);
    public double getDouble(String key);
    public boolean getBoolean(String key);
    public byte getByte(String key);
    public float getFloat(String key);
    public int getInteger(String key);
    public long getLong(String key);
    public List<Byte> getByteList(String key);
    public List<Integer> getIntegerList(String key);

    public IConfiguration getSubSection(String path);

    public void setString(String key, String value);
    public void setDouble(String key, double value);
    public void setBoolean(String key, boolean value);
    public void setByte(String key, byte value);
    public void setFloat(String key, float value);
    public void setInteger(String key, int value);
    public void setLong(String key, long value);
    public void setByteList(String key, List<Byte> value);
    public void setIntegerList(String key, List<Integer> value);

    public void setSubSection(String path, IConfiguration value);

    public void remove(String key);
}