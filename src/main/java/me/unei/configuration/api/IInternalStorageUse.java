package me.unei.configuration.api;

import me.unei.configuration.formats.Storage;

public interface IInternalStorageUse {
	public Storage<Object> getStorageObject();

	public void setStorageObject(Storage<Object> sto);
}
