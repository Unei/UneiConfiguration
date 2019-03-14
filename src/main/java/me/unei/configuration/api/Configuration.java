package me.unei.configuration.api;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.lang.model.element.NestingKind;

import me.unei.configuration.SavedFile;
import me.unei.configuration.api.exceptions.FileFormatException;
import me.unei.configuration.api.exceptions.InvalidNodeException;
import me.unei.configuration.api.fs.PathComponent;
import me.unei.configuration.api.fs.PathNavigator;
import me.unei.configuration.api.fs.PathNavigator.PathSymbolsType;
import me.unei.configuration.formats.Storage.Key;
import me.unei.configuration.formats.StorageType;

public abstract class Configuration<T extends Configuration<T>> implements IConfiguration
{
	protected T parent;
	
	protected final List<WeakReference<T>> childrens;
	
	protected SavedFile file;
	private boolean valid = true;
	
	protected PathSymbolsType symType;
	protected PathComponent.PathComponentsList fullPath;
	
	protected String nodeName;
	protected AtomicInteger nodeAtomicIndex;
	@Deprecated
	protected int nodeIndex;
	
	protected Configuration(SavedFile p_file, PathSymbolsType p_symType)
	{
		if (p_file == null)
		{
			throw new IllegalArgumentException("SavedFile should not be null");
		}
		this.parent = null;
		this.childrens = new ArrayList<WeakReference<T>>();
		this.file = p_file;
		this.symType = p_symType;
		this.fullPath = new PathComponent.PathComponentsList(p_symType);
		this.fullPath.appendRoot();
		this.nodeName = "";
		this.nodeIndex = -1;
		this.nodeAtomicIndex = null;
	}
	
	protected Configuration(T p_parent, String childName)
	{
		if (p_parent == null)
		{
			throw new IllegalArgumentException("Configuration parent should not be null");
		}
		this.parent = p_parent;
		this.childrens = new ArrayList<WeakReference<T>>();
		this.file = p_parent.file;
		this.symType = p_parent.symType;
		this.fullPath = Configuration.buildPath(p_parent.fullPath, childName);
		this.nodeName = (childName != null ? childName : "");
		try
		{
			this.nodeIndex = Integer.valueOf(childName);
			this.nodeAtomicIndex = new AtomicInteger(this.nodeIndex);
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
		this.childrens = new ArrayList<WeakReference<T>>();
		this.file = p_parent.file;
		this.symType = p_parent.symType;
		this.fullPath = Configuration.buildPath(p_parent.fullPath, index);
		this.nodeName = Integer.toString(index);
		this.nodeIndex = index;
		this.nodeAtomicIndex = new AtomicInteger(index);
	}
	
	protected final void init()
	{
		throwInvalid();
		this.file.init();
		try
		{
			this.reload();
			this.updateNode();
		}
		catch (FileFormatException e)
		{
			e.printStackTrace();
		}
	}
	
	protected final void invalidate()
	{
		this.valid = false;
		this.parent = null;
		this.childrens.forEach(wptr -> {
			if (wptr.get() != null) {
				wptr.get().invalidate();
				wptr.clear();
			}
		});
		this.childrens.clear();
		this.fullPath = null;
		this.nodeName = null;
		this.nodeAtomicIndex = null;
		this.symType = null;
		this.file = null;
	}
	
	protected final void validate(T newParent, int index)
	{
		validate(newParent, new AtomicInteger(index));
	}
	
	protected final void validate(T newParent, AtomicInteger index)
	{
		if (newParent == null)
		{
			throw new IllegalArgumentException("Configuration parent should not be null");
		}
		this.valid = true;
		this.parent = newParent;
		this.fullPath = Configuration.buildPath(newParent.fullPath, index.get());
		this.file = newParent.file;
		this.nodeAtomicIndex = index;
		this.nodeName = Integer.toString(index.get());
		this.symType = newParent.symType;
	}
	
	protected final void validate(T newParent, String childName)
	{
		if (newParent == null)
		{
			throw new IllegalArgumentException("Configuration parent should not be null");
		}
		this.valid = true;
		this.parent = newParent;
		this.fullPath = Configuration.buildPath(newParent.fullPath, childName);
		this.file = newParent.file;
		this.nodeName = (childName != null ? childName : "");
		try
		{
			int idx = Integer.valueOf(childName);
			this.nodeAtomicIndex = new AtomicInteger(idx);
		}
		catch (NumberFormatException ignored)
		{}
		this.symType = newParent.symType;
	}
	
	protected final void validate(T newParent, Key childKey)
	{
		switch (childKey.getType())
		{
		case LIST:
			validate(newParent, childKey.getKeyAtomicInt());
			break;
			
		case MAP:
			validate(newParent, childKey.getKeyString());
			break;

		default:
			break;
		}
	}
	
	public final boolean isValid()
	{
		return this.valid;
	}
	
	public final void throwInvalid() throws InvalidNodeException
	{
		if (!this.valid)
		{
			throw new InvalidNodeException();
		}
	}
	
	// v2 IMPL
	
	@SuppressWarnings("unchecked")
	protected final void updateNode()
	{
		throwInvalid();
		if (this.parent != null)
		{
			updateFromParent();
			this.parent.childrens.add(new WeakReference<T>((T) this));
		}
		if (!this.childrens.isEmpty())
		{
			List<WeakReference<T>> shallowCopy = new ArrayList<WeakReference<T>>(this.childrens);
			this.childrens.clear();
			Iterator<WeakReference<T>> it = shallowCopy.iterator();
			while (it.hasNext())
			{
				WeakReference<T> elem = it.next();
				if (elem.isEnqueued() || elem.get() == null)
				{
					it.remove();
				}
				else
				{
					elem.get().updateNode();
				}
			}
		}
	}
	
	protected abstract void updateFromParent();
	//
	
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
	
	public String getName()
	{
		return this.nodeName;
	}
	
	public int getIndex()
	{
		return (this.nodeAtomicIndex != null) ? this.nodeAtomicIndex.get() : -1;
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
		if (!this.isValid())
		{
			return false;
		}
		if (this.parent != null)
		{
			return this.parent.canAccess();
		}
		return this.file.canAccess();
	}
	
	public final void lock()
	{
		if (this.isValid())
		{
			if (this.parent != null)
			{
				this.parent.lock();
			}
			else
			{
				this.file.lock();
			}
		}
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