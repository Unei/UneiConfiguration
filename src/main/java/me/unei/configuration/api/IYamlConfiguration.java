package me.unei.configuration.api;

public interface IYamlConfiguration extends IConfiguration {

    /**
     * Returns this configuration's YAML representation as text.
     *
     * @return this configuration's YAML representation
     */
    public String saveToString();

    /**
     * Loads the provided YAML data into this YamlConfig.
     *
     * @param data The YAML data as text
     */
    public void loadFromString(String data);

}