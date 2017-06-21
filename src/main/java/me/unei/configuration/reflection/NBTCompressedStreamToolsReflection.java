package me.unei.configuration.reflection;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NBTCompressedStreamToolsReflection {

    private static Class<?> nbtcst = null;

    public static void setCSTClass(Class<?> type) {
        if (type != null && NBTCompressedStreamToolsReflection.nbtcst == null) {
            NBTCompressedStreamToolsReflection.nbtcst = type;
        }
    }

    public static boolean isNBTCST(Object obj) {
        if (NBTCompressedStreamToolsReflection.nbtcst == null) {
            return false;
        }
        return NBTCompressedStreamToolsReflection.nbtcst.isAssignableFrom(obj.getClass());
    }

    public static void write(Object nbtCompound, OutputStream os) {
        if (NBTCompoundReflection.isNBTCompound(nbtCompound)) {
            try {
                Method set = NBTCompressedStreamToolsReflection.nbtcst.getMethod("a", NBTCompoundReflection.getClassType(), OutputStream.class);
                set.invoke(null, nbtCompound, os);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static Object read(InputStream is) {
        try {
            Method set = NBTCompressedStreamToolsReflection.nbtcst.getMethod("a", InputStream.class);
            return set.invoke(null, is);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}