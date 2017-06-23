package me.unei.configuration.api.fs;

import java.util.ArrayList;

public final class DestinationDescription
{
	private final DestinationType type;
	private final String value;
	
	public DestinationDescription(DestinationType type, String value)
	{
		this.type = type;
		this.value = value;
	}
	
	public DestinationType getType()
	{
		return this.type;
	}
	
	public String getValue()
	{
		return this.value;
	}
	
	public static class DestinationList extends ArrayList<DestinationDescription>
	{
		private static final long serialVersionUID = 7055238860386957873L;
		
		public DestinationList()
		{
			super();
		}
		
		public boolean add(DestinationType type, String value)
		{
			return this.add(new DestinationDescription(type, value));
		}
	}
	
	public static enum DestinationType
	{
		Root,
		Parent,
		Child;
	}
}