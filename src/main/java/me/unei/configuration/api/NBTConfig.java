package me.unei.configuration.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import me.unei.configuration.SavedFile;
import me.unei.configuration.reflection.NBTBaseReflection;
import me.unei.configuration.reflection.NBTCompoundReflection;
import me.unei.configuration.reflection.NBTCompressedStreamToolsReflection;

public final class NBTConfig implements INBTConfiguration
{
	public static final String NBT_FILE_EXT = new String(".dat");
	public static final String NBT_TMP_EXT = new String(NBTConfig.NBT_FILE_EXT + ".tmp");
	
	private Object rootCompound = null;

	private SavedFile configFile = null;
	
	private String fullPath = "";
	private String tagName = "";
	private NBTConfig parent = null;
	
	public NBTConfig(File folder, String fileName)
	{
		this.configFile = new SavedFile(folder, fileName, NBTConfig.NBT_FILE_EXT);
		this.rootCompound = NBTCompoundReflection.newInstance();
	}
	
	NBTConfig(File folder, String fileName, String p_tagName)
	{
		this(new NBTConfig(folder, fileName), p_tagName);
	}
	
	NBTConfig(NBTConfig p_parent, String p_tagName)
	{
		this.parent = p_parent;
		this.tagName = p_tagName;
		this.fullPath = NBTConfig.buildPath(p_parent.fullPath, p_tagName);
	}
	
	public String getFileName()
	{
		if (this.configFile == null && this.parent != null)
		{
			return this.parent.getFileName();
		}
		return this.configFile.getFileName();
	}
	
	public String getName()
	{
		return this.tagName;
	}
	
	public String getCurrentPath()
	{
		return this.fullPath;
	}
	
	public void lock()
	{
		if (this.parent != null)
		{
			this.parent.lock();
		}
		this.configFile.lock();
	}
	
	public NBTConfig getRoot()
	{
		if (this.parent != null)
		{
			return this.parent.getRoot();
		}
		return this;
	}
	
	public NBTConfig getParent()
	{
		return this.parent;
	}
	
	public Object getTagCopy()
	{
		Object papa;
		if (this.parent != null)
		{
			papa = this.parent.getTagCopy();
		}
		else
		{
			papa = rootCompound;
		}
		if (papa == null)
		{
			return null;
		}
		return NBTBaseReflection.cloneNBT(NBTCompoundReflection.getCompound(papa, this.tagName));
	}
	
	public void setTagCopy(Object compound)
	{
		if (!this.configFile.canAccess())
		{
			return;
		}
		if (!NBTCompoundReflection.isNBTCompound(compound))
		{
			return;
		}
		Object papa;
		if (this.parent != null)
		{
			papa = this.parent.getTagCopy();
		}
		else
		{
			papa = rootCompound;
		}
		if (papa != null)
		{
			NBTCompoundReflection.set(papa, this.tagName, compound);
			if (this.parent != null)
			{
				this.parent.setTagCopy(papa);
			}
			else
			{
				this.rootCompound = papa;
			}
		}
	}
	
	public boolean canAccess()
	{
		if (this.parent != null)
		{
			return this.parent.canAccess();
		}
		return this.configFile.canAccess();
	}
	
	public void init()
	{
		if (this.parent != null)
		{
			this.parent.init();
		}
		else
		{
			this.configFile.init();
		}
	}
	
	public void load()
	{
		if (!this.canAccess())
		{
			return;
		}
		if (this.parent != null)
		{
			this.parent.load();
		}
		else
		{
			if (!this.configFile.getFile().exists())
			{
				this.save();
				return;
			}
			Object tmpCompound = null;
			try
			{
				tmpCompound = NBTCompressedStreamToolsReflection.read(new FileInputStream(this.configFile.getFile()));
			}
			catch (IOException e)
			{
				e.printStackTrace();
				return;
			}
			if (tmpCompound != null)
			{
				this.rootCompound = NBTBaseReflection.cloneNBT(tmpCompound);
			}
		}
	}
	
	public void save()
	{
		if (!this.canAccess())
		{
			return;
		}
		if (this.parent != null)
		{
			this.parent.save();
			return;
		}
		File tmp = new File(this.configFile.getFolder(), this.configFile.getFileName() + NBTConfig.NBT_TMP_EXT);
		try
		{
			NBTCompressedStreamToolsReflection.write(NBTBaseReflection.cloneNBT(rootCompound), new FileOutputStream(tmp));
			if (this.configFile.getFile().exists())
			{
				this.configFile.getFile().delete();
			}
			tmp.renameTo(this.configFile.getFile());
			tmp.delete();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void reset()
	{
		//
	}
	
	public boolean contains(String key)
	{
		Object tag = this.getTagCopy();
		return NBTCompoundReflection.hasKey(tag, key);
	}
	
	public String getString(String key)
	{
		Object tag = this.getTagCopy();
		return NBTCompoundReflection.getString(tag, key);
	}
	
	public void setString(String key, String value)
	{
		Object tag = this.getTagCopy();
		NBTCompoundReflection.setString(tag, key, value);
		this.setTagCopy(tag);
	}
	
	public void setSubSection(String path, IConfiguration value)
	{
		if (!this.configFile.canAccess())
		{
			return;
		}
		if (!(value instanceof NBTConfig))
		{
			//TODO ConfigType conversion
			return;
		}
		NBTConfig cfg = (NBTConfig)value;
		Object nbt = this.getTagCopy();
		NBTCompoundReflection.set(nbt, path, cfg.getTagCopy());
		this.setTagCopy(nbt);
	}
	
	public void remove(String key)
	{
		Object tag = this.getTagCopy();
		NBTCompoundReflection.remove(tag, key);
		this.setTagCopy(tag);
	}
	
	public NBTConfig getSubSection(String path)
	{
		if (!this.configFile.canAccess())
		{
			return null;
		}
		NBTConfig sub = new NBTConfig(this, path);
		return sub;
	}

	private static String buildPath(String parent, String child)
	{
		if (parent == null || parent.isEmpty() || child == null)
		{
			return child;
		}
		return new String(parent + "." + child);
	}
	
	private static String[] splitPath(String path)
	{
		return path.split(".");
	}
	
	public static NBTConfig getForPath(File folder, String fileName, String path)
	{
		return NBTConfig.getForPath(new NBTConfig(folder, fileName), path);
	}
	
	public static NBTConfig getForPath(NBTConfig root, String path)
	{
		if (path == null || path.isEmpty())
		{
			return root;
		}
		if (!path.contains("."))
		{
			return root.getSubSection(path);
		}
		NBTConfig last = root;
		for (String part : NBTConfig.splitPath(path))
		{
			last = last.getSubSection(part);
		}
		return last;
	}

	public double getDouble(String key)
	{
		Object tag = this.getTagCopy();
		return NBTCompoundReflection.getDouble(tag, key);
	}

	public boolean getBoolean(String key)
	{
		Object tag = this.getTagCopy();
		return NBTCompoundReflection.getBoolean(tag, key);
	}

	public byte getByte(String key)
	{
		Object tag = this.getTagCopy();
		return NBTCompoundReflection.getByte(tag, key);
	}

	public float getFloat(String key)
	{
		Object tag = this.getTagCopy();
		return NBTCompoundReflection.getFloat(tag, key);
	}

	public int getInteger(String key)
	{
		Object tag = this.getTagCopy();
		return NBTCompoundReflection.getInt(tag, key);
	}

	public long getLong(String key)
	{
		Object tag = this.getTagCopy();
		return NBTCompoundReflection.getLong(tag, key);
	}

	public List<Byte> getByteList(String key)
	{
		Object tag = this.getTagCopy();
		return Arrays.asList(ArrayUtils.toObject(NBTCompoundReflection.getByteArray(tag, key)));
	}

	public List<Integer> getIntegerList(String key)
	{
		Object tag = this.getTagCopy();
		return Arrays.asList(ArrayUtils.toObject(NBTCompoundReflection.getIntArray(tag, key)));
	}

	public void setDouble(String key, double value)
	{
		Object tag = this.getTagCopy();
		NBTCompoundReflection.setDouble(tag, key, value);
		this.setTagCopy(tag);
	}

	public void setBoolean(String key, boolean value)
	{
		Object tag = this.getTagCopy();
		NBTCompoundReflection.setBoolean(tag, key, value);
		this.setTagCopy(tag);
	}

	public void setByte(String key, byte value)
	{
		Object tag = this.getTagCopy();
		NBTCompoundReflection.setByte(tag, key, value);
		this.setTagCopy(tag);
	}

	public void setFloat(String key, float value)
	{
		Object tag = this.getTagCopy();
		NBTCompoundReflection.setFloat(tag, key, value);
		this.setTagCopy(tag);
	}

	public void setInteger(String key, int value)
	{
		Object tag = this.getTagCopy();
		NBTCompoundReflection.setInt(tag, key, value);
		this.setTagCopy(tag);
	}

	public void setLong(String key, long value)
	{
		Object tag = this.getTagCopy();
		NBTCompoundReflection.setLong(tag, key, value);
		this.setTagCopy(tag);
	}

	public void setByteList(String key, List<Byte> value)
	{
		Object tag = this.getTagCopy();
		NBTCompoundReflection.setByteArray(tag, key, ArrayUtils.toPrimitive(value.toArray(new Byte[value.size()])));
		this.setTagCopy(tag);
		
	}

	public void setIntegerList(String key, List<Integer> value)
	{
		Object tag = this.getTagCopy();
		NBTCompoundReflection.setIntArray(tag, key, ArrayUtils.toPrimitive(value.toArray(new Integer[value.size()])));
		this.setTagCopy(tag);
	}
}