package me.unei.configuration.api.fs;

public interface NavigableFile {

    public NavigableFile getParent();

    public NavigableFile getRoot();

    public NavigableFile getChild(String name);
}