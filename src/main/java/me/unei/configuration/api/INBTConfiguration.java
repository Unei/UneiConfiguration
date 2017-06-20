package me.unei.configuration.api;

import me.unei.configuration.formats.nbtproxy.NBTProxyCompound;

public interface INBTConfiguration extends IConfiguration
{
	public NBTProxyCompound getTagCopy();
	
	public void setTagCopy(NBTProxyCompound tag);
}