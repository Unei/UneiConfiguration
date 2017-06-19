package me.unei.configuration.api;

import me.unei.configuration.formats.nbtlib.TagCompound;

public interface INBTConfiguration extends IConfiguration
{
	public TagCompound getTagCopy();
	
	public void setTagCopy(TagCompound tag);
}