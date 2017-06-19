package me.unei.configuration.formats.nbtlib;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class TagDouble extends Tag
{
	private double data;
	
	TagDouble()
	{}
	
	public TagDouble(double value)
	{
		this.data = value;
	}
	
	@Override
	void write(DataOutput output) throws IOException
	{
		output.writeDouble(this.data);
	}
	
	@Override
	void read(DataInput output) throws IOException
	{
		this.data = output.readDouble();
	}
	
	@Override
	public byte getTypeId()
	{
		return Tag.TAG_Double;
	}
	
	@Override
	public String toString()
	{
		return Double.toString(this.data) + "d";
	}
	
	@Override
	public int hashCode()
	{
		long i = Double.doubleToLongBits(this.data);
		return super.hashCode() ^ (int)(i ^ i >>> 32);
	}
	
	public double getValue()
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
		TagDouble tb = (TagDouble)other;
		return (tb.data == this.data);
	}
	
	@Override
	public TagDouble clone()
	{
		return new TagDouble(this.data);
	}
}