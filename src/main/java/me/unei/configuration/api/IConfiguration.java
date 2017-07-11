package me.unei.configuration.api;

import me.unei.configuration.api.fs.NavigableFile;

import java.util.List;

public interface IConfiguration extends IFlatConfiguration, NavigableFile {

    public IConfiguration getRoot();
    public IConfiguration getParent();
    public IConfiguration getChild(String name);

    public boolean contains(String path);

    public Object get(String path);

    public String getString(String path);
    public double getDouble(String path);
    public boolean getBoolean(String path);
    public byte getByte(String path);
    public float getFloat(String path);
    public int getInteger(String path);
    public long getLong(String path);
    public List<Byte> getByteList(String path);
    public List<Integer> getIntegerList(String path);
    public List<Long> getLongList(String path);

    public IConfiguration getSubSection(String path);

    public void set(String path, Object value);

    public void setString(String path, String value);
    public void setDouble(String path, double value);
    public void setBoolean(String path, boolean value);
    public void setByte(String path, byte value);
    public void setFloat(String path, float value);
    public void setInteger(String path, int value);
    public void setLong(String path, long value);
    public void setByteList(String path, List<Byte> value);
    public void setIntegerList(String path, List<Integer> value);
    public void setLongList(String path, List<Long> value);

    public void setSubSection(String path, IConfiguration value);

    public void remove(String path);
}