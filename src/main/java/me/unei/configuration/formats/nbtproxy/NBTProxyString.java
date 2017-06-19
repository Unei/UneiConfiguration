package me.unei.configuration.formats.nbtproxy;

import me.unei.configuration.formats.nbtlib.TagString;
import me.unei.configuration.reflection.NBTBaseReflection;
import me.unei.configuration.reflection.NBTStringReflection;

public class NBTProxyString extends NBTProxyTag
{
	NBTProxyString(TagString copy, int unused)
	{
		super(copy, unused);
	}
	
	NBTProxyString(Object copy)
	{
		super(copy);
	}
	
	public NBTProxyString()
	{
		super(NBTProxyTag.getLibType().equals(LibType.NMS));
		if (NBTProxyTag.getLibType().equals(LibType.NMS))
		{
			this.nms_representation = NBTStringReflection.newInstance();
		}
		else
		{
			this.unei_representation = new TagString();
		}
	}
	
	public NBTProxyString(String orig)
	{
		super(NBTProxyTag.getLibType().equals(LibType.NMS));
		if (NBTProxyTag.getLibType().equals(LibType.NMS))
		{
			this.nms_representation = NBTStringReflection.newInstance(orig);
		}
		else
		{
			this.unei_representation = new TagString(orig);
		}
	}
	
	@Override
	public NBTProxyString clone()
	{
		switch (this.unei_type)
		{
			case NMS:
				return new NBTProxyString(NBTBaseReflection.cloneNBT(nms_representation));
			case UNEI:
				return new NBTProxyString(((TagString)unei_representation).clone(), 0);
		}
		return new NBTProxyString(getString());
	}
	
	public String getString()
	{
		switch (this.unei_type)
		{
			case NMS:
				return NBTStringReflection.getString(nms_representation);
			case UNEI:
				return unei_representation.getString();
		}
		return "";
	}
}