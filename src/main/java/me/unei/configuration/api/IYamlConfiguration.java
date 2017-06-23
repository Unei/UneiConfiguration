package me.unei.configuration.api;

public interface IYamlConfiguration extends IConfiguration {

    public String saveToString();
    public void loadFromString(String data);
}