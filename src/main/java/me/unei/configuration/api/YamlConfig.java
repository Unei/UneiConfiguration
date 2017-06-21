package me.unei.configuration.api;

import java.util.HashMap;

import org.yaml.snakeyaml.Yaml;

/**
 * Development in progress
 */
@SuppressWarnings("unused")
@Deprecated
abstract class YamlConfig implements IYamlConfiguration
{
	public static final String YAML_FILE_EXT = new String(".yml");
	
	private HashMap<String, Object> datas = new HashMap<String, Object>();
	private Yaml yaml;
	
	public YamlConfig()
	{
	}
}