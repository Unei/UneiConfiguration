package me.unei.configuration.formats.nbtlib;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class TagLong extends Tag
{
	private long data;
	
	TagLong()
	{}
	
	public TagLong(long value)
	{
		this.data = value;
	}
	
	@Override
	void write(DataOutput output) throws IOException
	{
		output.writeLong(this.data);
	}
	
	@Override
	void read(DataInput output) throws IOException
	{
		this.data = output.readLong();
	}
	
	@Override
	public byte getTypeId()
	{
		return Tag.TAG_Long;
	}
	
	@Override
	public String toString()
	{
		return Long.toString(this.data) + "L";
	}
	
	@Override
	public int hashCode()
	{
		return super.hashCode() ^ (int)(this.data ^ this.data >>> 32);
	}
	
	public long getValue()
	{
		return this.data;
	}
	
	@Override
	public boolean equals(Object other)
	{
		if (!super.equals(other))
		{
			return false;
		}
		TagLong tb = (TagLong)other;
		return (tb.data == this.data);
	}
	
	@Override
	public TagLong clone()
	{
		return new TagLong(this.data);
	}
}