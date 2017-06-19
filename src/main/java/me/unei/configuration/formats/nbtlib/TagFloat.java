package me.unei.configuration.formats.nbtlib;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class TagFloat extends Tag
{
	private float data;
	
	TagFloat()
	{}
	
	public TagFloat(float value)
	{
		this.data = value;
	}
	
	@Override
	void write(DataOutput output) throws IOException
	{
		output.writeFloat(this.data);
	}
	
	@Override
	void read(DataInput output) throws IOException
	{
		this.data = output.readFloat();
	}
	
	@Override
	public byte getTypeId()
	{
		return Tag.TAG_Float;
	}
	
	@Override
	public String toString()
	{
		return Float.toString(this.data) + "f";
	}
	
	@Override
	public int hashCode()
	{
		return super.hashCode() ^ Float.floatToIntBits(this.data);
	}
	
	public float getValue()
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
		TagFloat tb = (TagFloat)other;
		return (tb.data == this.data);
	}
	
	@Override
	public TagFloat clone()
	{
		return new TagFloat(this.data);
	}
}