package me.unei.configuration.formats.nbtlib;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class TagLongArray extends Tag
{
	private long[] data;
	
	TagLongArray()
	{}
	
	public TagLongArray(long[] value)
	{
		this.data = value;
	}
	
	@Override
	void write(DataOutput output) throws IOException
	{
		output.writeInt(this.data.length);
		for (int i = 0; i < this.data.length; i++)
		{
			output.writeLong(this.data[i]);
		}
	}
	
	@Override
	void read(DataInput input) throws IOException
	{
		int len = input.readInt();
		this.data = new long[len];
		for (int i = 0; i < this.data.length; i++)
		{
			this.data[i] = input.readLong();
		}
	}
	
	@Override
	public byte getTypeId()
	{
		return Tag.TAG_Long_Array;
	}
	
	@Override
	public String toString()
	{
		String result = "[";
		long[] aint = this.data;
		int lgh = aint.length;
		
		for(int j = 0; j < lgh; ++j)
		{
			long k = aint[j];
			
			result += k + ",";
		}
		
		return result + "]";
	}
	
	@Override
	public int hashCode()
	{
		return super.hashCode() ^ Arrays.hashCode(this.data);
	}
	
	public int size()
	{
		return this.data.length;
	}
	
	public long[] getLongArray()
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
		TagLongArray tb = (TagLongArray)other;
		return Arrays.equals(this.data, tb.data);
	}
	
	@Override
	public TagLongArray clone()
	{
		long[] copy = new long[this.data.length];
		
		System.arraycopy(this.data, 0, copy, 0, this.data.length);
		return new TagLongArray(copy);
	}
}