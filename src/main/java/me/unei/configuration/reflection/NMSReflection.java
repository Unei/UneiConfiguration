package me.unei.configuration.reflection;

import org.bukkit.Bukkit;

public final class NMSReflection
{
	private static Class<?> nbtCompressedStreamTools = null;
	private static Class<?> nbtTagBase = null;
	private static Class<?> nbtTagCompound = null;
	private static Class<?> nbtTagList = null;
	private static Class<?> nbtTagString = null;
	
	private static final String VERSION;
	
	static
	{
		if (NMSReflection.canUseNMS())
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
		else
		{
			VERSION = "";
		}
	}
	
	public static String getVersion()
	{
		return NMSReflection.VERSION;
	}
	
	public static void doNothing()
	{}
	
	public static boolean canUseNMS()
	{
		try
		{
			Class.forName("org.bukkit.Bukkit");
			Package.getPackage("net.minecraft.server");
			return true;
		}
		catch (ClassNotFoundException e)
		{
			return false;
		}
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