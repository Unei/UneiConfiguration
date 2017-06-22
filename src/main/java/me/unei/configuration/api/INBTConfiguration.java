package me.unei.configuration.api;

import me.unei.configuration.api.format.INBTCompound;

public interface INBTConfiguration extends IConfiguration {

    public INBTCompound getTagCopy();

    public void setTagCopy(INBTCompound tag);
}