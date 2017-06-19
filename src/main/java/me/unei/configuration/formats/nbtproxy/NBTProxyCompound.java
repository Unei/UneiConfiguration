package me.unei.configuration.formats.nbtproxy;

import java.util.Set;

import me.unei.configuration.formats.nbtlib.TagCompound;
import me.unei.configuration.reflection.NBTBaseReflection;
import me.unei.configuration.reflection.NBTCompoundReflection;

public class NBTProxyCompound extends NBTProxyTag
{
	NBTProxyCompound(Object orig)
	{
		super(orig);
	}
	
	NBTProxyCompound(TagCompound orig)
	{
		super(orig, 0);
	}
	
	public NBTProxyCompound()
	{
		super(NBTCompoundReflection.newInstance());
	}
	
	public Set<String> keySet()
	{
		return NBTCompoundReflection.keySet(nms_representation);
	}
	
	public int size()
	{
		return NBTCompoundReflection.size(nms_representation);
	}
	
	public void set(String key, NBTProxyTag elem)
	{
		NBTCompoundReflection.set(nms_representation, key, elem.nms_representation);
	}
	
	public void setString(String key, String value)
	{
		NBTCompoundReflection.setString(nms_representation, key, value);
	}
	
	public NBTProxyTag get(String key)
	{
		Object rep = NBTCompoundReflection.get(nms_representation, key);
		byte type = NBTBaseReflection.getTypeId(rep);
		return NBTProxyTag.newTag(type, rep);
	}
	
	public byte getTypeOf(String key)
	{
		return NBTCompoundReflection.getTypeOf(nms_representation, key);
	}
	
	public boolean hasKey(String key)
	{
		return NBTCompoundReflection.hasKey(nms_representation, key);
	}
	
	public boolean hasKeyOfType(String key, int type)
	{
		return NBTCompoundReflection.hasKeyOfType(nms_representation, key, type);
	}
	
	public String getString(String key)
	{
		return NBTCompoundReflection.getString(nms_representation, key);
	}
	
	public NBTProxyCompound getCompound(String key)
	{
		Object got = NBTCompoundReflection.getCompound(nms_representation, key);
		return new NBTProxyCompound(got);
	}
	
	public void remove(String key)
	{
		NBTCompoundReflection.remove(nms_representation, key);
	}
}