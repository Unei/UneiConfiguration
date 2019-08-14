package me.unei.configuration.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class NMSReflection {

    private static Class<?> nbtCompressedStreamTools = null;
    private static Class<?> nbtTagBase = null;
    private static Class<?> nbtTagCompound = null;
    private static Class<?> nbtTagList = null;
    private static Class<?> nbtTagString = null;

    private static final String VERSION;

    static {
        if (NMSReflection.canUseNMS()) {
            String[] array = /*Bukkit.getServer().getClass().getPackage().getName()*/"a.a".replace(".", ",").split(",");
            VERSION = (array.length == 4? array[3] : "");

            NMSReflection.nbtCompressedStreamTools = NMSReflection.getNMSClass("NBTCompressedStreamTools", true);
            NMSReflection.nbtTagBase = NMSReflection.getNMSClass("NBTBase", true);
            NMSReflection.nbtTagCompound = NMSReflection.getNMSClass("NBTTagCompound", true);
            NMSReflection.nbtTagList = NMSReflection.getNMSClass("NBTTagList", true);
            NMSReflection.nbtTagString = NMSReflection.getNMSClass("NBTTagString", true);

            NBTBaseReflection.setBaseClass(NMSReflection.nbtTagBase);
            NBTListReflection.setListClass(NMSReflection.nbtTagList);
            NBTStringReflection.setStringClass(NMSReflection.nbtTagString);
            NBTCompoundReflection.setCompoundClass(NMSReflection.nbtTagCompound);
            NBTCompressedStreamToolsReflection.setCSTClass(NMSReflection.nbtCompressedStreamTools);
            
            /* Retrieve numbers */
            NBTNumberReflection.setIntClass(NMSReflection.getNMSClass("NBTTagInt", true));
            NBTNumberReflection.setLongClass(NMSReflection.getNMSClass("NBTTagLong", true));
            NBTNumberReflection.setByteClass(NMSReflection.getNMSClass("NBTTagByte", true));
            NBTNumberReflection.setShortClass(NMSReflection.getNMSClass("NBTTagShort", true));
            NBTNumberReflection.setFloatClass(NMSReflection.getNMSClass("NBTTagFloat", true));
            NBTNumberReflection.setDoubleClass(NMSReflection.getNMSClass("NBTTagDouble", true));
            
            /* Retrieve numbers Arrays */
            NBTArrayReflection.setIntArrayClass(NMSReflection.getNMSClass("NBTTagIntArray", true));
            NBTArrayReflection.setByteArrayClass(NMSReflection.getNMSClass("NBTTagByteArray", true));
            NBTArrayReflection.setLongArrayClass(NMSReflection.getNMSClass("NBTTagLongArray", false));
        } else {
            VERSION = "";
        }
    }

    public static String getVersion() {
        return NMSReflection.VERSION;
    }

    public static void doNothing() {
    }

    public static boolean canUseNMS() {
        /*try {
            Bukkit.getVersion();
            return true;
        } catch (Throwable t) {*/
            return false;
        //}
    }

    public static Class<?> getNMSClass(String name, boolean logError) {
        if (name == null || name.isEmpty()) {
        	return null;
        }
        if (NMSReflection.getVersion().isEmpty()) {
            try {
                return Class.forName("net.minecraft.server." + name);
            } catch (ClassNotFoundException e) {
            	if (logError)
            		e.printStackTrace();
            }
        }
        try {
            return Class.forName("net.minecraft.server." + NMSReflection.getVersion() + "." + name);
        } catch (ClassNotFoundException e) {
        	if (logError)
        		e.printStackTrace();
        }
        return null;
    }
    
    public static Method getPossibleMethods(Class<?> source, String names[], Class<?>...argsType) throws NoSuchMethodException {
    	for (String name : names) {
    		try {
    			return source.getMethod(name, argsType);
    		} catch (NoSuchMethodException e) {
    			continue;
    		}
    	}
    	throw new NoSuchMethodException();
    }
    
    public static Object getMethodOrField(Class<?> src, String methodName, String fieldName) throws NoSuchFieldException {
    	try
    	{
    		return src.getMethod(methodName);
    	}
    	catch (NoSuchMethodException e)
    	{
    		Field f = src.getDeclaredField(fieldName);
    		f.setAccessible(true);
    		return f;
    	}
    }

    public static Class<?> getOBCClass(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        if (NMSReflection.getVersion().isEmpty()) {
            try {
                return Class.forName("org.bukkit.craftbukkit." + name);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        try {
            return Class.forName("org.bukkit.craftbukkit." + NMSReflection.getVersion() + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}