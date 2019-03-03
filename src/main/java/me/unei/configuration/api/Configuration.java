package me.unei.configuration.api;

import me.unei.configuration.SavedFile;
import me.unei.configuration.api.exceptions.FileFormatException;
import me.unei.configuration.api.fs.PathComponent;
import me.unei.configuration.api.fs.PathNavigator;
import me.unei.configuration.api.fs.PathNavigator.PathSymbolsType;
import me.unei.configuration.formats.StorageType;


public abstract class Configuration<T extends Configuration<T>> implements IConfiguration
{
	protected T parent;
	
	protected SavedFile file;
	
	protected PathSymbolsType symType;
	protected PathComponent.PathComponentsList fullPath;
	
	protected String nodeName;
	protected int nodeIndex;
	
	protected Configuration(SavedFile p_file, PathSymbolsType p_symType)
	{
		if (p_file == null)
		{
			throw new IllegalArgumentException("SavedFile should not be null");
		}
		this.file = p_file;
		this.symType = p_symType;
		this.fullPath = new PathComponent.PathComponentsList(p_symType);
		this.fullPath.appendRoot();
		this.nodeName = "";
		this.nodeIndex = -1;
	}
	
	protected Configuration(T p_parent, String childName)
	{
		if (p_parent == null)
		{
			throw new IllegalArgumentException("Configuration parent should not be null");
		}
		this.parent = p_parent;
		this.file = p_parent.file;
		this.symType = p_parent.symType;
		this.fullPath = Configuration.buildPath(p_parent.fullPath, childName);
		this.nodeName = (childName != null ? childName : "");
		try
		{
			this.nodeIndex = Integer.valueOf(childName);
		}
		catch (NumberFormatException ignored)
		{
			this.nodeIndex = -1;
		}
	}
	
	protected Configuration(T p_parent, int index)
	{
		if (p_parent == null)
		{
			throw new IllegalArgumentException("Configuration parent should not be null");
		}
		this.parent = p_parent;
		this.file = p_parent.file;
		this.symType = p_parent.symType;
		this.fullPath = Configuration.buildPath(p_parent.fullPath, index);
		this.nodeName = Integer.toString(index);
		this.nodeIndex = index;
	}
	
	protected final void init()
	{
		this.file.init();
		try
		{
			this.reload();
		}
		catch (FileFormatException e)
		{
			e.printStackTrace();
		}
	}
	
	protected abstract void propagate();
	
	public final SavedFile getFile()
	{
		return this.file;
	}
	
	public final String getFileName()
	{
		if (this.parent != null)
		{
			return this.parent.getFileName();
		}
		return this.file.getFileName();
	}
	
	PathSymbolsType getPathSymbolsType()
	{
		return this.symType;
	}
	
	public StorageType getType()
	{
		return StorageType.UNDEFINED;
	}
	
	@Override
	public void setType(StorageType type) { }
	
	public String getName()
	{
		return this.nodeName;
	}
	
	public int getIndex()
	{
		return this.nodeIndex;
	}
	
	public final String getCurrentPath()
	{
		return this.fullPath.toString();
	}
	
	public final PathComponent.PathComponentsList getRealListPath()
	{
		return fullPath.clone();
	}
	
	
	public final boolean canAccess()
	{
		if (this.parent != null)
		{
			return this.parent.canAccess();
		}
		return this.file.canAccess();
	}
	
	public final void lock()
	{
		if (this.parent != null)
		{
			this.parent.lock();
			return;
		}
		this.file.lock();
	}
	
	
	public Configuration<T> getRoot()
	{
		if (this.parent != null)
		{
			return this.parent.getRoot();
		}
		return this;
	}
	
	public T getParent()
	{
		return this.parent;
	}
	
	
	public T getSubSection(String path)
	{
		return this.getSubSection(PathNavigator.parsePath(path, symType));
	}
	
	public abstract T getSubSection(PathComponent.PathComponentsList path);
	
	
	protected static PathComponent.PathComponentsList buildPath(PathComponent.PathComponentsList parent, String child)
	{
		PathComponent.PathComponentsList copy;
		if (parent == null || parent.isEmpty())
		{
			copy = new PathComponent.PathComponentsList(parent.getSymbolsType());
			copy.appendRoot();
		}
		else
		{
			copy = parent.clone();
		}
		if (child == null || child.isEmpty())
		{
			return copy;
		}
		copy.appendChild(child);
		return copy;
	}

	protected static PathComponent.PathComponentsList buildPath(PathComponent.PathComponentsList parent, int index)
	{
		PathComponent.PathComponentsList copy;
		if (parent == null || parent.isEmpty())
		{
			copy = new PathComponent.PathComponentsList(parent.getSymbolsType());
			copy.appendRoot();
		}
		else
		{
			copy = parent.clone();
		}
		if (index < 0)
		{
			return copy;
		}
		copy.appendIndex(index);
		return copy;
	}
}