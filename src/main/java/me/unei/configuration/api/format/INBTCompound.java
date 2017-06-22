package me.unei.configuration.api.format;

import java.util.Set;


public interface INBTCompound extends INBTTag{
	public Set<String> keySet();
	
	public int size();
	
	public void set(String key, INBTTag elem);
	
	public void setByte(String key, byte value);
	public void setShort(String key, short value);
	public void setInt(String key, int value);
	public void setLong(String key, long value);
	public void setFloat(String key, float value);
	public void setDouble(String key, double value);
	public void setString(String key, String value);
	public void setByteArray(String key, byte[] value);
	public void setIntArray(String key, int[] value);
	public void setLongArray(String key, long[] value);
	public void setBoolean(String key, boolean value);
	
	public INBTTag get(String key);
	
	public byte getTypeOf(String key);
	public boolean hasKey(String key);
	
	public byte getByte(String key);
	public short getShort(String key);
	public int getInt(String key);
	public long getLong(String key);
	public float getFloat(String key);
	public double getDouble(String key);
	public String getString(String key);
	public byte[] getByteArray(String key);
	public int[] getIntArray(String key);
	public long[] getLongArray(String key);
	public INBTCompound getCompound(String key);
	public boolean getBoolean(String key);
	
	public INBTList getList(String key, byte type);
	
	public void remove(String key);
}
