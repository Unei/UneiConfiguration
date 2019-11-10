package me.unei.configuration.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import me.unei.configuration.formats.nbtlib.TagCompound;

public class CraftReflectionUtils {

    private static Class<?> craftItemStack = null;
    private static Class<?> nmsItemStack = null;
    
    private static void load() {
    	if (NMSReflection.canUseNMS()) {
    		craftItemStack = NMSReflection.getOBCClass("inventory.CraftItemStack");
    		nmsItemStack = NMSReflection.getNMSClass("ItemStack", false);
    	}
    }
    
    static {
    	load();
    }
    
    public static org.bukkit.inventory.ItemStack ensureCraftItem(org.bukkit.inventory.ItemStack is) {
    	if (is == null) return null;
    	if (craftItemStack == null) throw new IllegalStateException("Could not access base CraftItemStack, unable to convert");
    	if (craftItemStack.isInstance(is)) return is;
		try {
			return (org.bukkit.inventory.ItemStack) craftItemStack.getMethod("asCraftCopy", org.bukkit.inventory.ItemStack.class).invoke(null, is);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return null;
    }
    
    public static org.bukkit.inventory.ItemStack ensureNaturalBukkitItem(org.bukkit.inventory.ItemStack is) {
    	if (is == null) return null;
    	if (is.getClass() == org.bukkit.inventory.ItemStack.class) return is;
		try {
			Object nmsIS = getNMSItemStack(is);
			return (org.bukkit.inventory.ItemStack) craftItemStack.getMethod("asBukkitCopy", nmsItemStack).invoke(null, nmsIS);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return null;
    }
	
	public static Object getNMSItemStack(org.bukkit.inventory.ItemStack is) {
		if (is == null) return null;
		if (craftItemStack == null || !craftItemStack.isInstance(is)) {
			return null;
		}
		try {
			Field f = is.getClass().getDeclaredField("handle");
			boolean access = f.isAccessible();
			f.setAccessible(true);
			Object handle = f.get(is);
			f.setAccessible(access);
			if (handle != null && (nmsItemStack == null || !nmsItemStack.isInstance(handle))) {
				nmsItemStack = handle.getClass();
			}
			return handle;
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Object getItemStackNMSTag(org.bukkit.inventory.ItemStack is) {
		Object nmsIS = getNMSItemStack(is);
		if (nmsIS == null) return null;
		if (nmsItemStack == null || !nmsItemStack.isInstance(nmsIS)) {
			nmsItemStack = nmsIS.getClass();
		}
		try {
			Method f = nmsItemStack.getMethod("getTag");
			boolean access = f.isAccessible();
			f.setAccessible(true);
			Object tag = f.invoke(nmsIS);
			f.setAccessible(access);
			return tag;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static boolean setItemStackNMSTag(org.bukkit.inventory.ItemStack is, Object nmsTag) {
		if (nmsTag != null && !NBTCompoundReflection.isNBTCompound(nmsTag)) return false;
		Object nmsIS = getNMSItemStack(is);
		if (nmsIS == null) return false;
		if (nmsItemStack == null || !nmsItemStack.isInstance(nmsIS)) {
			nmsItemStack = nmsIS.getClass();
		}
		try {
			Method f = nmsItemStack.getMethod("setTag", NBTCompoundReflection.getClassType());
			boolean access = f.isAccessible();
			f.setAccessible(true);
			f.invoke(nmsIS, nmsTag);
			f.setAccessible(access);
			return true;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static TagCompound getItemStackTag(org.bukkit.inventory.ItemStack is) {
		Object nmsTag = getItemStackNMSTag(is);
		if (nmsTag == null) return null;
		TagCompound cmp = new TagCompound();
		cmp.getFromNMS(nmsTag);
		return cmp;
	}
	
	public static boolean setItemStackTag(org.bukkit.inventory.ItemStack is, TagCompound cmp) {
		return setItemStackNMSTag(is, cmp.getAsNMS());
	}
}
