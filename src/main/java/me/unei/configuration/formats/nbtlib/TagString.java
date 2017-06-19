package me.unei.configuration.formats.nbtlib;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class TagString extends Tag
{
	private String data;
	
	public TagString()
	{
		this.data = new String("");
	}
	
	public TagString(String s)
	{
		if (s == null)
		{
			throw new IllegalArgumentException("Null string not allowed");
		}
		this.data = s;
	}
	
	@Override
	void write(DataOutput output) throws IOException
	{
		output.writeUTF(this.data);
	}
	
	@Override
	void read(DataInput input) throws IOException
	{
		this.data = input.readUTF();
	}
	
	@Override
	public byte getTypeId()
	{
		return Tag.TAG_String;
	}
	
	@Override
	public String toString()
	{
		return "\"" + this.data.replaceAll("\"", "\\\"") + "\"";
	}
	
	@Override
	public TagString clone()
	{
		return new TagString(this.data);
	}
	
	@Override
	public boolean isEmpty()
	{
		return this.data.isEmpty();
	}
	
	@Override
	public boolean equals(Object other)
	{
		if (!super.equals(other))
		{
			return false;
		}
		return (other.toString().equals(this.toString()));
	}
	
	@Override
	public int hashCode()
	{
		return super.hashCode() ^ this.data.hashCode();
	}
	
	@Override
	public String getString()
	{
		return this.data;
	}
}