package me.unei.configuration.api.format;

public interface INBTList extends INBTTag {

    public byte getTagType();

    public void add(INBTTag tag);
    public void set(int idx, INBTTag tag);
    public INBTTag get(int idx);
    public INBTTag remove(int idx);

    public int size();
}
