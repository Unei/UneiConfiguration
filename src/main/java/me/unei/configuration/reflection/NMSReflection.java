package me.unei.configuration.reflection;

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
		NBTStringReflection.setStringClass(NMSReflection.nbtTagString);
		NBTCompoundReflection.setCompoundClass(NMSReflection.nbtTagCompound);
		NBTCompressedStreamToolsReflection.setCSTClass(NMSReflection.nbtCompressedStreamTools);
	}
	
	public static String getVersion()
	{
		return NMSReflection.VERSION;
	}
	
	public static void doNothing()
	{}
	
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
	
	public static Class<?> getOBCClass(String name)
	{
		if(name == null || name.isEmpty() || NMSReflection.getVersion().isEmpty())
		{
			return null;
		}
		try
		{
			return Class.forName("org.bukkit.craftbukkit." + NMSReflection.getVersion() + "." + name);
		}
		catch(ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}