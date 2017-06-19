package me.unei.configuration.api;

import java.util.Set;

public interface IYamlConfiguration extends IConfiguration
{
	public Set<String> getKeys();
	
	public String saveToString();
	
	public void loadFromString(String yamldata);
}