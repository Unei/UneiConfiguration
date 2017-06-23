package me.unei.configuration.api.fs;

public interface UneiFsFile {

    public abstract UneiFsFile getParent();

    public abstract UneiFsFile getRoot();

    public abstract UneiFsFile getChild(String name);
}