package me.unei.configuration.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public final class NBTBaseReflection {

    public static final byte TagEndId = 0, TagByteId = 1, TagShortId = 2, TagIntId = 3, TagLongId = 4, TagFloatId = 5, TagDoubleId = 6, TagByteArrayId = 7, TagStringId = 8, TagListId = 9, TagCompoundId = 10, TagIntArrayId = 11, TagLongArray = 12, TagNumber = 99;

    private static String[] tagList = null;

    private static Class<?> nbtTagBase = null;

    public static void setBaseClass(Class<?> type) {
        if (type != null && NBTBaseReflection.nbtTagBase == null) {
            NBTBaseReflection.nbtTagBase = type;
            try {
                NBTBaseReflection.tagList = (String[]) NBTBaseReflection.nbtTagBase.getField("a").get(null);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isNBTTag(Object obj) {
        if (NBTBaseReflection.nbtTagBase == null || obj == null) {
            return false;
        }
        return NBTBaseReflection.nbtTagBase.isAssignableFrom(obj.getClass());
    }

    public static Object cloneNBT(Object obj) {
        if (!NBTBaseReflection.isNBTTag(obj)) {
            return null;
        }
        try {
            Method cloneM = obj.getClass().getMethod("clone");
            return obj.getClass().cast(cloneM.invoke(obj));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isEmpty(Object obj) {
        if (!NBTBaseReflection.isNBTTag(obj)) {
            return true;
        }
        try {
            Method cloneM = obj.getClass().getMethod("isEmpty");
            return (Boolean) cloneM.invoke(obj);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static byte getTypeId(Object obj) {
        if (!NBTBaseReflection.isNBTTag(obj)) {
            return -1;
        }
        try {
            Method cloneM = obj.getClass().getMethod("getTypeId");
            return (Byte) cloneM.invoke(obj);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static Class<?> getClassType() {
        return NBTBaseReflection.nbtTagBase;
    }

    public static String[] getTypeList() {
        if (NBTBaseReflection.tagList == null) {
            return null;
        }
        return Arrays.copyOf(NBTBaseReflection.tagList, NBTBaseReflection.tagList.length);
    }
}