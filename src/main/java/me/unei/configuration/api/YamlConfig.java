package me.unei.configuration.api;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.yaml.snakeyaml.Yaml;

public class YamlConfig implements IYamlConfiguration {

    public static final String YAML_FILE_EXT = new String(".yml");

    private HashMap<String, Object> datas = new HashMap<String, Object>();
    private Yaml yaml;

    public YamlConfig() {
    }

    public String getFileName() {
        return null;
    }

    public String getName() {
        return null;
    }

    public String getCurrentPath() {
        return null;
    }

    public boolean canAccess() {
        return false;
    }

    public void lock() {

    }

    public IConfiguration getRoot() {
        return null;
    }

    public IConfiguration getParent() {
        return null;
    }

    public void save() {

    }

    public void load() {

    }

    public void reset() {

    }

    public void init() {

    }

    public boolean contains(String key) {
        return false;
    }

    public String getString(String key) {
        return null;
    }

    public double getDouble(String key) {
        return 0;
    }

    public boolean getBoolean(String key) {
        return false;
    }

    public byte getByte(String key) {
        return 0;
    }

    public float getFloat(String key) {
        return 0;
    }

    public int getInteger(String key) {
        return 0;
    }

    public long getLong(String key) {
        return 0;
    }

    public List<Byte> getByteList(String key) {
        return null;
    }

    public List<Integer> getIntegerList(String key) {
        return null;
    }

    public IConfiguration getSubSection(String path) {
        return null;
    }

    public void setString(String key, String value) {

    }

    public void setDouble(String key, double value) {

    }

    public void setBoolean(String key, boolean value) {

    }

    public void setByte(String key, byte value) {

    }

    public void setFloat(String key, float value) {

    }

    public void setInteger(String key, int value) {

    }

    public void setLong(String key, long value) {

    }

    public void setByteList(String key, List<Byte> value) {

    }

    public void setIntegerList(String key, List<Integer> value) {

    }

    public void setSubSection(String path, IConfiguration value) {

    }

    public void remove(String key) {

    }

    public Set<String> getKeys() {
        return null;
    }

    public String saveToString() {
        return null;
    }

    public void loadFromString(String yamldata) {

    }
}