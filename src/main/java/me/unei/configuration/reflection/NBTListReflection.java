package me.unei.configuration.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NBTListReflection {

    private static Class<?> nbtTagList = null;

    public static void setListClass(Class<?> type) {
        if (type != null && NBTListReflection.nbtTagList == null) {
            NBTListReflection.nbtTagList = type;
        }
    }

    public static boolean isNBTList(Object obj) {
        if (!NBTBaseReflection.isNBTTag(obj)) {
            return false;
        }
        if (NBTListReflection.nbtTagList == null) {
            return false;
        }
        return NBTListReflection.nbtTagList.isAssignableFrom(obj.getClass());
    }

    public static Object newInstance() {
        if (NBTListReflection.nbtTagList == null) {
            return null;
        }
        try {
            return NBTListReflection.nbtTagList.getConstructor().newInstance();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            if (e.getCause() != null && (e.getCause() instanceof RuntimeException))
                throw (RuntimeException) e.getCause();
            else e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void add(Object list, Object nbtBase)//TODO some methods to add directly strings/numbers...
    {
        if (NBTBaseReflection.isNBTTag(nbtBase) && NBTListReflection.isNBTList(list)) {
            try {
                Method add = NBTListReflection.nbtTagList.getMethod("add", NBTBaseReflection.getClassType());
                add.invoke(list, nbtBase);
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

    public static void set(Object list, int idx, Object nbtBase) {
        if (NBTBaseReflection.isNBTTag(nbtBase) && NBTListReflection.isNBTList(list)) {
            try {
                Method add = NBTListReflection.nbtTagList.getMethod("a", int.class, NBTBaseReflection.getClassType());
                add.invoke(list, idx, nbtBase);
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

    public static Object remove(Object list, int idx) {
        if (NBTListReflection.isNBTList(list)) {
            try {
                Method add = NBTListReflection.nbtTagList.getMethod("remove", int.class);
                return add.invoke(list, idx);
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

    public static boolean isEmpty(Object list) {
        return NBTBaseReflection.isEmpty(list);
    }

    public static Object getAsCompound(Object list, int idx) {
        if (NBTListReflection.isNBTList(list)) {
            try {
                Method add = NBTListReflection.nbtTagList.getMethod("get", int.class);
                return add.invoke(list, idx);
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

    public static int getAsInt(Object list, int idx) {
        if (NBTListReflection.isNBTList(list)) {
            try {
                Method add = NBTListReflection.nbtTagList.getMethod("c", int.class);
                return (Integer) add.invoke(list, idx);
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

    public static int[] getAsIntArray(Object list, int idx) {
        if (NBTListReflection.isNBTList(list)) {
            try {
                Method add = NBTListReflection.nbtTagList.getMethod("d", int.class);
                return (int[]) add.invoke(list, idx);
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

    public static double getAsDouble(Object list, int idx) {
        if (NBTListReflection.isNBTList(list)) {
            try {
                Method add = NBTListReflection.nbtTagList.getMethod("f", int.class);
                return (Double) add.invoke(list, idx);
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

    public static float getAsFloat(Object list, int idx) {
        if (NBTListReflection.isNBTList(list)) {
            try {
                Method add = NBTListReflection.nbtTagList.getMethod("g", int.class);
                return (Float) add.invoke(list, idx);
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

    public static String getAsString(Object list, int idx) {
        if (NBTListReflection.isNBTList(list)) {
            try {
                Method add = NBTListReflection.nbtTagList.getMethod("getString", int.class);
                return (String) add.invoke(list, idx);
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

    public static Object getAsNBTBase(Object list, int idx) {
        if (NBTListReflection.isNBTList(list)) {
            try {
                Method add = NBTListReflection.nbtTagList.getMethod("i", int.class);
                return add.invoke(list, idx);
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

    public static int size(Object list) {
        if (NBTListReflection.isNBTList(list)) {
            try {
                Method add = NBTListReflection.nbtTagList.getMethod("size");
                return (Integer) add.invoke(list);
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

    public static int getTypeInList(Object list) {
        if (NBTListReflection.isNBTList(list)) {
            try {
                Method add = NBTListReflection.nbtTagList.getMethod("g");
                return (Integer) add.invoke(list);
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
}