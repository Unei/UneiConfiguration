package me.unei.configuration.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class NBTNumberReflection {
	
    private static Class<?> nbtTagInt = null;
    private static Class<?> nbtTagByte = null;
    private static Class<?> nbtTagLong = null;
    private static Class<?> nbtTagFloat = null;
    private static Class<?> nbtTagShort = null;
    private static Class<?> nbtTagDouble = null;

    static void setIntClass(Class<?> type) {
        if (type != null && NBTNumberReflection.nbtTagInt == null) {
        	NBTNumberReflection.nbtTagInt = type;
        }
    }
    
    static void setByteClass(Class<?> type) {
        if (type != null && NBTNumberReflection.nbtTagByte == null) {
        	NBTNumberReflection.nbtTagByte = type;
        }
    }
    
    static void setLongClass(Class<?> type) {
        if (type != null && NBTNumberReflection.nbtTagLong == null) {
        	NBTNumberReflection.nbtTagLong = type;
        }
    }
    
    static void setFloatClass(Class<?> type) {
        if (type != null && NBTNumberReflection.nbtTagFloat == null) {
        	NBTNumberReflection.nbtTagFloat = type;
        }
    }
    
    static void setShortClass(Class<?> type) {
        if (type != null && NBTNumberReflection.nbtTagShort == null) {
        	NBTNumberReflection.nbtTagShort = type;
        }
    }
    
    static void setDoubleClass(Class<?> type) {
        if (type != null && NBTNumberReflection.nbtTagDouble == null) {
        	NBTNumberReflection.nbtTagDouble = type;
        }
    }
    
    public static boolean isNBTInteger(Object obj) {
        if (!NBTBaseReflection.isNBTTag(obj)) {
            return false;
        }
        if (NBTNumberReflection.nbtTagInt != null && NBTNumberReflection.nbtTagInt.isAssignableFrom(obj.getClass())) {
        	return true;
        }
        return false;
    }
    
    public static boolean isNBTByte(Object obj) {
        if (!NBTBaseReflection.isNBTTag(obj)) {
            return false;
        }
        if (NBTNumberReflection.nbtTagByte != null && NBTNumberReflection.nbtTagByte.isAssignableFrom(obj.getClass())) {
        	return true;
        }
        return false;
    }
    
    public static boolean isNBTLong(Object obj) {
        if (!NBTBaseReflection.isNBTTag(obj)) {
            return false;
        }
        if (NBTNumberReflection.nbtTagLong != null && NBTNumberReflection.nbtTagLong.isAssignableFrom(obj.getClass())) {
        	return true;
        }
        return false;
    }
    
    public static boolean isNBTFloat(Object obj) {
        if (!NBTBaseReflection.isNBTTag(obj)) {
            return false;
        }
        if (NBTNumberReflection.nbtTagFloat != null && NBTNumberReflection.nbtTagFloat.isAssignableFrom(obj.getClass())) {
        	return true;
        }
        return false;
    }
    
    public static boolean isNBTShort(Object obj) {
        if (!NBTBaseReflection.isNBTTag(obj)) {
            return false;
        }
        if (NBTNumberReflection.nbtTagShort != null && NBTNumberReflection.nbtTagShort.isAssignableFrom(obj.getClass())) {
        	return true;
        }
        return false;
    }
    
    public static boolean isNBTDouble(Object obj) {
        if (!NBTBaseReflection.isNBTTag(obj)) {
            return false;
        }
        if (NBTNumberReflection.nbtTagDouble != null && NBTNumberReflection.nbtTagDouble.isAssignableFrom(obj.getClass())) {
        	return true;
        }
        return false;
    }

    public static boolean isNBTNumber(Object obj) {
        if (!NBTBaseReflection.isNBTTag(obj)) {
            return false;
        }
        if (NBTNumberReflection.nbtTagInt != null && NBTNumberReflection.nbtTagInt.isAssignableFrom(obj.getClass())) {
        	return true;
        }
        if (NBTNumberReflection.nbtTagByte != null && NBTNumberReflection.nbtTagByte.isAssignableFrom(obj.getClass())) {
        	return true;
        }
        if (NBTNumberReflection.nbtTagLong != null && NBTNumberReflection.nbtTagLong.isAssignableFrom(obj.getClass())) {
        	return true;
        }
        if (NBTNumberReflection.nbtTagFloat != null && NBTNumberReflection.nbtTagFloat.isAssignableFrom(obj.getClass())) {
        	return true;
        }
        if (NBTNumberReflection.nbtTagShort != null && NBTNumberReflection.nbtTagShort.isAssignableFrom(obj.getClass())) {
        	return true;
        }
        if (NBTNumberReflection.nbtTagDouble != null && NBTNumberReflection.nbtTagDouble.isAssignableFrom(obj.getClass())) {
        	return true;
        }
        return false;
    }
    
    public static Object newInt(int value) {
    	if (NBTNumberReflection.nbtTagInt == null) {
    		return null;
    	}
    	try {
    		Constructor<?> construct = NBTNumberReflection.nbtTagInt.getConstructor(int.class);
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
    
    public static Object newByte(byte value) {
    	if (NBTNumberReflection.nbtTagByte == null) {
    		return null;
    	}
    	try {
    		Constructor<?> construct = NBTNumberReflection.nbtTagByte.getConstructor(byte.class);
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
    
    public static Object newLong(long value) {
    	if (NBTNumberReflection.nbtTagLong == null) {
    		return null;
    	}
    	try {
    		Constructor<?> construct = NBTNumberReflection.nbtTagLong.getConstructor(long.class);
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
    
    public static Object newFloat(float value) {
    	if (NBTNumberReflection.nbtTagFloat == null) {
    		return null;
    	}
    	try {
    		Constructor<?> construct = NBTNumberReflection.nbtTagFloat.getConstructor(float.class);
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
    
    public static Object newShort(short value) {
    	if (NBTNumberReflection.nbtTagShort == null) {
    		return null;
    	}
    	try {
    		Constructor<?> construct = NBTNumberReflection.nbtTagShort.getConstructor(short.class);
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
    
    public static Object newDouble(double value) {
    	if (NBTNumberReflection.nbtTagDouble == null) {
    		return null;
    	}
    	try {
    		Constructor<?> construct = NBTNumberReflection.nbtTagDouble.getConstructor(double.class);
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
    
    public static int getInt(Object nmsDouble) {
    	if (NBTNumberReflection.isNBTInteger(nmsDouble)) {
    		try {
    			Method get = NBTNumberReflection.nbtTagInt.getMethod("e");
    			return (Integer)get.invoke(nmsDouble);
    		} catch (NoSuchMethodException e) {
    			try {
    				Field f = NBTNumberReflection.nbtTagInt.getDeclaredField("data");
    				return f.getInt(nmsDouble);
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
    	return 0;
    }
    
    public static byte getByte(Object nmsDouble) {
    	if (NBTNumberReflection.isNBTByte(nmsDouble)) {
    		try {
    			Method get = NBTNumberReflection.nbtTagByte.getMethod("g");
    			return (Byte)get.invoke(nmsDouble);
    		} catch (NoSuchMethodException e) {
    			try {
    				Field f = NBTNumberReflection.nbtTagByte.getDeclaredField("data");
    				return f.getByte(nmsDouble);
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
    	return (byte) 0;
    }
    
    public static long getLong(Object nmsDouble) {
    	if (NBTNumberReflection.isNBTLong(nmsDouble)) {
    		try {
    			Method get = NBTNumberReflection.nbtTagLong.getMethod("d");
    			return (Long)get.invoke(nmsDouble);
    		} catch (NoSuchMethodException e) {
    			try {
    				Field f = NBTNumberReflection.nbtTagLong.getDeclaredField("data");
    				return f.getLong(nmsDouble);
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
    	return 0L;
    }
    
    public static float getFloat(Object nmsDouble) {
    	if (NBTNumberReflection.isNBTFloat(nmsDouble)) {
    		try {
    			Method get = NBTNumberReflection.nbtTagFloat.getMethod("i");
    			return (Float)get.invoke(nmsDouble);
    		} catch (NoSuchMethodException e) {
    			try {
    				Field f = NBTNumberReflection.nbtTagFloat.getDeclaredField("data");
    				return f.getFloat(nmsDouble);
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
    	return 0.0F;
    }
    
    public static short getShort(Object nmsDouble) {
    	if (NBTNumberReflection.isNBTShort(nmsDouble)) {
    		try {
    			Method get = NBTNumberReflection.nbtTagShort.getMethod("f");
    			return (Short)get.invoke(nmsDouble);
    		} catch (NoSuchMethodException e) {
    			try {
    				Field f = NBTNumberReflection.nbtTagShort.getDeclaredField("data");
    				return f.getShort(nmsDouble);
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
    	return (short) 0;
    }
    
    public static double getDouble(Object nmsDouble) {
    	if (NBTNumberReflection.isNBTDouble(nmsDouble)) {
    		try {
    			Method get = NBTNumberReflection.nbtTagDouble.getMethod("asDouble");
    			return (Double)get.invoke(nmsDouble);
    		} catch (NoSuchMethodException e) {
    			try {
    				Field f = NBTNumberReflection.nbtTagDouble.getDeclaredField("data");
    				return f.getDouble(nmsDouble);
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
    	return 0.0D;
    }
}