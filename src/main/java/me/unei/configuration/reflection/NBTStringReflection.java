package me.unei.configuration.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NBTStringReflection {

    private static Class<?> nbtTagString = null;

    public static void setStringClass(Class<?> type) {
        if (type != null && NBTStringReflection.nbtTagString == null) {
            NBTStringReflection.nbtTagString = type;
        }
    }

    public static boolean isNBTString(Object obj) {
        if (!NBTBaseReflection.isNBTTag(obj)) {
            return false;
        }
        if (NBTStringReflection.nbtTagString == null) {
            return false;
        }
        return NBTStringReflection.nbtTagString.isAssignableFrom(obj.getClass());
    }

    public static Object newInstance() {
    	if (NBTStringReflection.nbtTagString == null) {
    		return null;
    	}
        try {
            return NBTStringReflection.nbtTagString.getConstructor().newInstance();
        } catch (InvocationTargetException e) {
            if (e.getCause() != null && (e.getCause() instanceof RuntimeException))
                throw (RuntimeException) e.getCause();
            else e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object newInstance(String orig) {
    	if (NBTStringReflection.nbtTagString == null) {
    		return null;
    	}
        try {
            return NBTStringReflection.nbtTagString.getConstructor(String.class).newInstance(orig);
        } catch (InvocationTargetException e) {
            if (e.getCause() != null && (e.getCause() instanceof RuntimeException))
                throw (RuntimeException) e.getCause();
            else e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getString(Object obj) {
        if (NBTStringReflection.isNBTString(obj)) {
            try {
                Method getStr = obj.getClass().getMethod("c_");
                return (String) getStr.invoke(obj);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                if (e.getCause() != null && (e.getCause() instanceof RuntimeException))
                    throw (RuntimeException) e.getCause();
                else e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}