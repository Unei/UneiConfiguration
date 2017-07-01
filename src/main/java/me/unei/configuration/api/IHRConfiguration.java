package me.unei.configuration.api;

public interface IHRConfiguration extends IConfiguration
{
	public String toFormattedString();
	
	public String toMinimizedString();
	
	public String saveToString();
	
	public void loadFromString(String data);
}