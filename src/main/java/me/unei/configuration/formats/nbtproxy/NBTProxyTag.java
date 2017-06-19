package me.unei.configuration.formats.nbtproxy;

import me.unei.configuration.formats.nbtlib.Tag;
import me.unei.configuration.reflection.NBTBaseReflection;
import me.unei.configuration.reflection.NMSReflection;

public abstract class NBTProxyTag
{
	public static final LibType Unei_Type_NMS = LibType.NMS;
	public static final LibType Unei_Type_UNEI = LibType.UNEI;
	
	protected Object nms_representation;
	protected Tag unei_representation;
	
	protected final LibType unei_type;
	
	protected NBTProxyTag(LibType type)
	{
		this.unei_type = type;
	}
	
	protected NBTProxyTag(Object rep)
	{
		if (rep == null)
		{
			throw new IllegalArgumentException("Tag cannot be null");
		}
		if (!NBTBaseReflection.isNBTTag(rep))
		{
			throw new IllegalArgumentException("Argument is not NBTTag");
		}
		this.nms_representation = rep;
		this.unei_type = NBTProxyTag.Unei_Type_NMS;
	}
	
	protected NBTProxyTag(Tag rep, int unused)
	{
		if (rep == null)
		{
			throw new IllegalArgumentException("Tag cannot be null");
		}
		this.unei_representation = rep;
		this.unei_type = NBTProxyTag.Unei_Type_UNEI;
	}
	
	protected static LibType getLibType()
	{
		if (NMSReflection.canUseNMS())
		{
			return LibType.NMS;
		}
		return LibType.UNEI;
	}
	
	public byte getTypeId()
	{
		switch (this.unei_type)
		{
			case NMS:
				return NBTBaseReflection.getTypeId(nms_representation);
			case UNEI:
				return this.unei_representation.getTypeId();
		}
		
		return -1;
	}
	
	public boolean isEmpty()
	{
		switch (this.unei_type)
		{
			case NMS:
				return NBTBaseReflection.isEmpty(nms_representation);
			case UNEI:
				return this.unei_representation.isEmpty();
		}
		
		return true;
	}
	
	@Override
	public abstract NBTProxyTag clone();
	
	@Override
	public String toString()
	{
		switch (this.unei_type)
		{
			case NMS:
				return nms_representation.toString();
			case UNEI:
				return unei_representation.toString();
		}
		
		return "";
	}
	
	@Override
	public int hashCode()
	{
		switch (this.unei_type)
		{
			case NMS:
				return nms_representation.hashCode();
			case UNEI:
				return unei_representation.hashCode();
		}
		
		return 0;
	}
	
	@Override
	public boolean equals(Object other)
	{
		switch (this.unei_type)
		{
			case NMS:
				return nms_representation.equals(other);
			case UNEI:
				return unei_representation.equals(other);
		}
		
		return false;
	}
	
	protected static enum LibType
	{
		NMS,
		UNEI;
	}
}