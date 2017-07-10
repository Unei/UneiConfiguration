package me.unei.configuration.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class NBTArrayReflection {
	
    private static Class<?> nbtTagIntArray = null;
    private static Class<?> nbtTagByteArray = null;
    private static Class<?> nbtTagLongArray = null;

    static void setIntArrayClass(Class<?> type) {
        if (type != null && NBTArrayReflection.nbtTagIntArray == null) {
        	NBTArrayReflection.nbtTagIntArray = type;
        }
    }
    
    static void setByteArrayClass(Class<?> type) {
        if (type != null && NBTArrayReflection.nbtTagByteArray == null) {
        	NBTArrayReflection.nbtTagByteArray = type;
        }
    }
    
    static void setLongArrayClass(Class<?> type) {
        if (type != null && NBTArrayReflection.nbtTagLongArray == null) {
        	NBTArrayReflection.nbtTagLongArray = type;
        }
    }
    
    public static boolean isNBTIntArray(Object obj) {
        if (!NBTBaseReflection.isNBTTag(obj)) {
            return false;
        }
        if (NBTArrayReflection.nbtTagIntArray != null && NBTArrayReflection.nbtTagIntArray.isAssignableFrom(obj.getClass())) {
        	return true;
        }
        return false;
    }
    
    public static boolean isNBTByteArray(Object obj) {
        if (!NBTBaseReflection.isNBTTag(obj)) {
            return false;
        }
        if (NBTArrayReflection.nbtTagByteArray != null && NBTArrayReflection.nbtTagByteArray.isAssignableFrom(obj.getClass())) {
        	return true;
        }
        return false;
    }
    
    public static boolean isNBTLongArray(Object obj) {
        if (!NBTBaseReflection.isNBTTag(obj)) {
            return false;
        }
        if (NBTArrayReflection.nbtTagLongArray != null && NBTArrayReflection.nbtTagLongArray.isAssignableFrom(obj.getClass())) {
        	return true;
        }
        return false;
    }
    
    public static Object newIntArray(int[] value) {
    	if (NBTArrayReflection.nbtTagIntArray == null) {
    		return null;
    	}
    	try {
    		Constructor<?> construct = NBTArrayReflection.nbtTagIntArray.getConstructor(int[].class);
    		return construct.newInstance(value);
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
    
    public static Object newByteArray(byte[] value) {
    	if (NBTArrayReflection.nbtTagByteArray == null) {
    		return null;
    	}
    	try {
    		Constructor<?> construct = NBTArrayReflection.nbtTagByteArray.getConstructor(byte[].class);
    		return construct.newInstance(value);
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
    
    public static Object newLongArray(long[] value) {
    	if (NBTArrayReflection.nbtTagLongArray == null) {
    		return null;
    	}
    	try {
    		Constructor<?> construct = NBTArrayReflection.nbtTagLongArray.getConstructor(long[].class);
    		return construct.newInstance(value);
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
    
    public static int[] getIntArray(Object nmsDouble) {
    	if (NBTArrayReflection.isNBTIntArray(nmsDouble)) {
    		try {
    			Method get = NBTArrayReflection.nbtTagIntArray.getMethod("e");
    			return (int[])get.invoke(nmsDouble);
    		} catch (NoSuchMethodException e) {
    			try {
    				Field f = NBTArrayReflection.nbtTagIntArray.getDeclaredField("data");
    				return (int[])f.get(nmsDouble);
    			} catch (NoSuchFieldException fe) {
    				fe.printStackTrace();
    			} catch (IllegalAccessException fe) {
        			fe.printStackTrace();
        		}
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
    
    public static byte[] getByteArray(Object nmsDouble) {
    	if (NBTArrayReflection.isNBTByteArray(nmsDouble)) {
    		try {
    			Method get = NBTArrayReflection.nbtTagByteArray.getMethod("g");
    			return (byte[])get.invoke(nmsDouble);
    		} catch (NoSuchMethodException e) {
    			try {
    				Field f = NBTArrayReflection.nbtTagByteArray.getDeclaredField("data");
    				return (byte[])f.get(nmsDouble);
    			} catch (NoSuchFieldException fe) {
    				fe.printStackTrace();
    			} catch (IllegalAccessException fe) {
        			fe.printStackTrace();
        		}
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
    
    public static long[] getLongArray(Object nmsDouble) {
    	if (NBTArrayReflection.isNBTLongArray(nmsDouble)) {
    		try {
    			Method get = NBTArrayReflection.nbtTagLongArray.getMethod("d");
    			return (long[])get.invoke(nmsDouble);
    		} catch (NoSuchMethodException e) {
    			try {
    				Field f = NBTArrayReflection.nbtTagLongArray.getDeclaredField("data");
    				return (long[])f.get(nmsDouble);
    			} catch (NoSuchFieldException fe) {
    				fe.printStackTrace();
    			} catch (IllegalAccessException fe) {
        			fe.printStackTrace();
        		}
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
}