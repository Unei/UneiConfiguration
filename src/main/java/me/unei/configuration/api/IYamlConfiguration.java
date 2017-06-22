package me.unei.configuration.api;

import java.util.Set;

public interface IYamlConfiguration extends IConfiguration {

    public Set<String> getKeys();

    public Object get(String key);

    public void set(String key, Object value);

    public String toYAMLString();

}