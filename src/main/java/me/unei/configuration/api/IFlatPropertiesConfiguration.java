package me.unei.configuration.api;

public interface IFlatPropertiesConfiguration extends IFlatConfiguration {
	
	public String get(String key, String defaultValue);
}