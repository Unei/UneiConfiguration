package me.unei.configuration.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

public class NBTCompoundReflection {

    private static Class<?> nbtTagCompound = null;

    public static void setCompoundClass(Class<?> type) {
        if (type != null && NBTCompoundReflection.nbtTagCompound == null) {
            NBTCompoundReflection.nbtTagCompound = type;
        }
    }

    public static boolean isNBTCompound(Object obj) {
        if (!NBTBaseReflection.isNBTTag(obj)) {
            return false;
        }
        if (NBTCompoundReflection.nbtTagCompound == null) {
            return false;
        }
        return NBTCompoundReflection.nbtTagCompound.isAssignableFrom(obj.getClass());
    }

    public static Object newInstance() {
        try {
            return NBTCompoundReflection.nbtTagCompound.getConstructor().newInstance();
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

    @SuppressWarnings("unchecked")
    public static Set<String> keySet(Object obj) {
        if (!NBTCompoundReflection.isNBTCompound(obj)) {
            return null;
        }
        try {
            Method c = NBTCompoundReflection.nbtTagCompound.getMethod("c");
            return (Set<String>) c.invoke(obj);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            if (e.getCause() != null && (e.getCause() instanceof RuntimeException))
                throw (RuntimeException) e.getCause();
            else e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int size(Object obj) {
        if (NBTCompoundReflection.isNBTCompound(obj)) {
            try {
                Method add = NBTCompoundReflection.nbtTagCompound.getMethod("d");
                return (Integer) add.invoke(obj);
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
        return 0;
    }

    public static void set(Object obj, String key, Object nbtbase) {
        if (NBTCompoundReflection.isNBTCompound(obj) && NBTBaseReflection.isNBTTag(nbtbase)) {
            try {
                Method set = NBTCompoundReflection.nbtTagCompound.getMethod("set", String.class, NBTBaseReflection.getClassType());
                set.invoke(obj, key, nbtbase);
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
    }

    public static void setByte(Object obj, String key, byte b0) {
        if (NBTCompoundReflection.isNBTCompound(obj)) {
            try {
                Method set = NBTCompoundReflection.nbtTagCompound.getMethod("setByte", String.class, byte.class);
                set.invoke(obj, key, b0);
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
    }

    public static void setShort(Object obj, String key, short short0) {
        if (NBTCompoundReflection.isNBTCompound(obj)) {
            try {
                Method set = NBTCompoundReflection.nbtTagCompound.getMethod("setShort", String.class, short.class);
                set.invoke(obj, key, short0);
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
    }

    public static void setInt(Object obj, String key, int b0) {
        if (NBTCompoundReflection.isNBTCompound(obj)) {
            try {
                Method set = NBTCompoundReflection.nbtTagCompound.getMethod("setInt", String.class, int.class);
                set.invoke(obj, key, b0);
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
    }

    public static void setLong(Object obj, String key, long b0) {
        if (NBTCompoundReflection.isNBTCompound(obj)) {
            try {
                Method set = NBTCompoundReflection.nbtTagCompound.getMethod("setLong", String.class, long.class);
                set.invoke(obj, key, b0);
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
    }

    public static void setUUID(Object obj, String key, UUID uuid) {
        if (NBTCompoundReflection.isNBTCompound(obj)) {
            try {
                Method set = NBTCompoundReflection.nbtTagCompound.getMethod("a", String.class, UUID.class);
                set.invoke(obj, key, uuid);
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
    }

    public static UUID getUUID(Object obj, String key) {
        if (NBTCompoundReflection.isNBTCompound(obj)) {
            try {
                Method set = NBTCompoundReflection.nbtTagCompound.getMethod("a", String.class);
                return (UUID) set.invoke(obj, key);
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

    public static boolean isUUID(Object obj, String key) {
        if (NBTCompoundReflection.isNBTCompound(obj)) {
            try {
                Method set = NBTCompoundReflection.nbtTagCompound.getMethod("b", String.class);
                return (Boolean) set.invoke(obj, key);
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
        return false;
    }

    public static void setFloat(Object obj, String key, float b0) {
        if (NBTCompoundReflection.isNBTCompound(obj)) {
            try {
                Method set = NBTCompoundReflection.nbtTagCompound.getMethod("setFloat", String.class, float.class);
                set.invoke(obj, key, b0);
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
    }

    public static void setDouble(Object obj, String key, double b0) {
        if (NBTCompoundReflection.isNBTCompound(obj)) {
            try {
                Method set = NBTCompoundReflection.nbtTagCompound.getMethod("setDouble", String.class, double.class);
                set.invoke(obj, key, b0);
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
    }

    public static void setString(Object obj, String key, String b0) {
        if (NBTCompoundReflection.isNBTCompound(obj)) {
            try {
                Method set = NBTCompoundReflection.nbtTagCompound.getMethod("setString", String.class, String.class);
                set.invoke(obj, key, b0);
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
    }

    public static void setByteArray(Object obj, String key, byte[] b0) {
        if (NBTCompoundReflection.isNBTCompound(obj)) {
            try {
                Method set = NBTCompoundReflection.nbtTagCompound.getMethod("setByteArray", String.class, byte[].class);
                set.invoke(obj, key, b0);
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
    }

    public static void setIntArray(Object obj, String key, int[] b0) {
        if (NBTCompoundReflection.isNBTCompound(obj)) {
            try {
                Method set = NBTCompoundReflection.nbtTagCompound.getMethod("setIntArray", String.class, int[].class);
                set.invoke(obj, key, b0);
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
    }

    public static void setLongArray(Object obj, String key, long[] b0) {
        if (NBTCompoundReflection.isNBTCompound(obj)) {
            try {
                Method set = NBTCompoundReflection.nbtTagCompound.getMethod("setLongArray", String.class, long[].class);
                set.invoke(obj, key, b0);
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
    }

    public static void setBoolean(Object obj, String key, boolean b0) {
        if (NBTCompoundReflection.isNBTCompound(obj)) {
            try {
                Method set = NBTCompoundReflection.nbtTagCompound.getMethod("setBoolean", String.class, boolean.class);
                set.invoke(obj, key, b0);
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
    }

    public static Object get(Object obj, String key) {
        if (NBTCompoundReflection.isNBTCompound(obj)) {
            try {
                Method set = NBTCompoundReflection.nbtTagCompound.getMethod("get", String.class);
                return set.invoke(obj, key);
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

    public static byte getTypeOf(Object obj, String key) {
        if (NBTCompoundReflection.isNBTCompound(obj)) {
            try {
                Method set = NBTCompoundReflection.nbtTagCompound.getMethod("d", String.class);
                return (Byte) set.invoke(obj, key);
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
        return -1;
    }

    public static boolean hasKey(Object obj, String key) {
        if (NBTCompoundReflection.isNBTCompound(obj)) {
            try {
                Method set = NBTCompoundReflection.nbtTagCompound.getMethod("hasKey", String.class);
                return (Boolean) set.invoke(obj, key);
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
        return false;
    }

    public static boolean hasKeyOfType(Object obj, String key, int type) {
        if (NBTCompoundReflection.isNBTCompound(obj)) {
            try {
                Method set = NBTCompoundReflection.nbtTagCompound.getMethod("hasKeyOfType", String.class, int.class);
                return (Boolean) set.invoke(obj, key, type);
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
        return false;
    }

    public static byte getByte(Object obj, String key) {
        if (NBTCompoundReflection.isNBTCompound(obj)) {
            try {
                Method set = NBTCompoundReflection.nbtTagCompound.getMethod("getByte", String.class);
                return (Byte) set.invoke(obj, key);
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
        return (byte) 0;
    }

    public static short getShort(Object obj, String key) {
        if (NBTCompoundReflection.isNBTCompound(obj)) {
            try {
                Method set = NBTCompoundReflection.nbtTagCompound.getMethod("getShort", String.class);
                return (Short) set.invoke(obj, key);
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
        return (short) 0;
    }

    public static int getInt(Object obj, String key) {
        if (NBTCompoundReflection.isNBTCompound(obj)) {
            try {
                Method set = NBTCompoundReflection.nbtTagCompound.getMethod("getInt", String.class);
                return (Integer) set.invoke(obj, key);
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
        return 0;
    }

    public static long getLong(Object obj, String key) {
        if (NBTCompoundReflection.isNBTCompound(obj)) {
            try {
                Method set = NBTCompoundReflection.nbtTagCompound.getMethod("getLong", String.class);
                return (Long) set.invoke(obj, key);
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
        return 0L;
    }

    public static float getFloat(Object obj, String key) {
        if (NBTCompoundReflection.isNBTCompound(obj)) {
            try {
                Method set = NBTCompoundReflection.nbtTagCompound.getMethod("getFloat", String.class);
                return (Float) set.invoke(obj, key);
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
        return 0.0F;
    }

    public static double getDouble(Object obj, String key) {
        if (NBTCompoundReflection.isNBTCompound(obj)) {
            try {
                Method set = NBTCompoundReflection.nbtTagCompound.getMethod("getDouble", String.class);
                return (Double) set.invoke(obj, key);
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
        return 0.0D;
    }

    public static String getString(Object obj, String key) {
        if (NBTCompoundReflection.isNBTCompound(obj)) {
            try {
                Method set = NBTCompoundReflection.nbtTagCompound.getMethod("getString", String.class);
                return (String) set.invoke(obj, key);
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
        return "";
    }

    public static byte[] getByteArray(Object obj, String key) {
        if (NBTCompoundReflection.isNBTCompound(obj)) {
            try {
                Method set = NBTCompoundReflection.nbtTagCompound.getMethod("getByteArray", String.class);
                return (byte[]) set.invoke(obj, key);
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
        return new byte[0];
    }

    public static int[] getIntArray(Object obj, String key) {
        if (NBTCompoundReflection.isNBTCompound(obj)) {
            try {
                Method set = NBTCompoundReflection.nbtTagCompound.getMethod("getIntArray", String.class);
                return (int[]) set.invoke(obj, key);
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
        return new int[0];
    }

    public static long[] getLongArray(Object obj, String key) {
        if (NBTCompoundReflection.isNBTCompound(obj)) {
            try {
                Method set = NBTCompoundReflection.nbtTagCompound.getMethod("getLongArray", String.class);
                return (long[]) set.invoke(obj, key);
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
        return new long[0];
    }

    public static Object getCompound(Object obj, String key) {
        if (NBTCompoundReflection.isNBTCompound(obj)) {
            try {
                Method set = NBTCompoundReflection.nbtTagCompound.getMethod("getCompound", String.class);
                return set.invoke(obj, key);
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
        return NBTCompoundReflection.newInstance();
    }

    public static Object getList(Object obj, String key, int type) {
        if (NBTCompoundReflection.isNBTCompound(obj)) {
            try {
                Method set = NBTCompoundReflection.nbtTagCompound.getMethod("getList", String.class, int.class);
                return set.invoke(obj, key, type);
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
        return NBTListReflection.newInstance();
    }

    public static boolean getBoolean(Object obj, String key) {
        if (NBTCompoundReflection.isNBTCompound(obj)) {
            try {
                Method set = NBTCompoundReflection.nbtTagCompound.getMethod("getBoolean", String.class);
                return (Boolean) set.invoke(obj, key);
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
        return false;
    }

    public static void remove(Object obj, String key) {
        if (NBTCompoundReflection.isNBTCompound(obj)) {
            try {
                Method set = NBTCompoundReflection.nbtTagCompound.getMethod("remove", String.class);
                set.invoke(obj, key);
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
    }

    public static Class<?> getClassType() {
        return NBTCompoundReflection.nbtTagCompound;
    }
}