package me.unei.configuration.reflection;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.Bukkit;

public final class NMSReflection
{
	private static Class<?> nbtCompressedStreamTools;
	private static Class<?> nbtTagBase;
	private static Class<?> nbtTagCompound;
	private static Class<?> nbtTagList;
	private static Class<?> nbtTagString;
	
	private static final String VERSION;
	
	static
	{
		String[] array = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",");
		VERSION = (array.length == 4 ? array[3] : "");
		
		NMSReflection.nbtCompressedStreamTools = NMSReflection.getNMSClass("NBTCompressedStreamTools");
		NMSReflection.nbtTagBase = NMSReflection.getNMSClass("NBTBase");
		NMSReflection.nbtTagCompound = NMSReflection.getNMSClass("NBTTagCompound");
		NMSReflection.nbtTagList = NMSReflection.getNMSClass("NBTTagList");
		NMSReflection.nbtTagString = NMSReflection.getNMSClass("NBTTagString");
		
		NBTBaseReflection.setBaseClass(NMSReflection.nbtTagBase);
		NBTListReflection.setListClass(NMSReflection.nbtTagList);
	}
	
	public static String getVersion()
	{
		return NMSReflection.VERSION;
	}
	
	public static Class<?> getNMSClass(String name)
	{
		if(name == null || name.isEmpty() || NMSReflection.getVersion().isEmpty())
		{
			return null;
		}
		try
		{
			return Class.forName("net.minecraft.server." + NMSReflection.getVersion() + "." + name);
		}
		catch(ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public static Object getNewNBTCompound()
	{
		try
		{
			return NMSReflection.nbtTagCompound.getConstructor().newInstance();
		}
		catch (InvocationTargetException e)
		{
			e.printStackTrace();
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		catch (NoSuchMethodException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}