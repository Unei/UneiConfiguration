package me.unei.configuration.reflection;

import java.util.Arrays;

import me.unei.configuration.reflection.versioning.NMSNBTVersioning;

public final class NBTBaseReflection {

    public static final byte TagEndId = 0, TagByteId = 1, TagShortId = 2, TagIntId = 3, TagLongId = 4, TagFloatId = 5, TagDoubleId = 6, TagByteArrayId = 7, TagStringId = 8, TagListId = 9, TagCompoundId = 10, TagIntArrayId = 11, TagLongArray = 12, TagNumber = 99;

    private static String[] tagList = null;

    private static Class<?> nbtTagBase = null;

    static void setBaseClass(Class<?> type) {
        if (type != null && NBTBaseReflection.nbtTagBase == null) {
            NBTBaseReflection.nbtTagBase = type;
            NBTBaseReflection.tagList = (String[]) NMSNBTVersioning.F_NBTBASE_TYPENAMES.callOrGet(getClassType(), null);
        }
    }

    /**
     * <p>Checks whenever the given object is an instance of NMS {@code NBTBase}</p>
     * 
     * @param obj The object to check.
     * @return Returns {@code true} if the object is a NMS NBT tag.
     */
    public static boolean isNBTTag(Object obj) {
        if (NBTBaseReflection.nbtTagBase == null || obj == null) {
            return false;
        }
        return NBTBaseReflection.nbtTagBase.isAssignableFrom(obj.getClass());
    }

    /**
     * <p>Make a call to {@code NBTBase::clone}</p>
     * 
     * @param obj The tag to clone (any subclasses of NMS {@code NBTBase}).
     * @return Returns a clone of the tag (created by calling {@code NBTBase::clone}).
     */
    public static Object cloneNBT(Object obj) {
        if (!NBTBaseReflection.isNBTTag(obj)) {
            return null;
        }
        return NMSNBTVersioning.M_NBTBASE_CLONE.callOrGet(getClassType(), obj);
    }

    /**
     * <p>Make a call to {@code NBTBase::isEmpty}</p>
     * <p>Implementation notice: This method does not exist any more in NBTBase since MC 1.13</p>
     * 
     * @param obj The tag to clone (any subclasses of NMS {@code NBTBase}).
     * @return Returns if the tag is empty or not (see {@code NBTBase::isEmpty}).
     */
    public static boolean isEmpty(Object obj) {
        if (!NBTBaseReflection.isNBTTag(obj)) {
            return true;
        }
    	Boolean val = Boolean.class.cast(NMSNBTVersioning.M_NBTBASE_ISEMPTY.callOrGet(getClassType(), obj));
    	return (val == null) ? false : val.booleanValue();
    }

    /**
     * <p>Gets the string representation of the tag value.</p>
     * <p>Implementation changes: method renamed from '{@code c_()}' to '{@code asString()}' in MC 1.13</p>
     * 
     * @param obj The tag to get the string from (any subclasses of NMS {@code NBTBase}).
     * @return Returns the string representation of the tag (see {@code NBTBase::c_} or {@code NBTBase::asString}).
     */
    public static String getString(Object obj) {
        if (!NBTBaseReflection.isNBTTag(obj)) {
            return null;
        }
    	return String.class.cast(NMSNBTVersioning.M_NBTBASE_ASSTRING.callOrGet(getClassType(), obj));
    }

    /**
     * <p>Create a new (empty) tag, as implemented in {@code NBTBase::createTag}</p>
     * <p>Implementation changes: {@code protected} access modified has been removed in MC 1.13</p>
     * 
     * @param type The type of the tag to create.
     * @return Returns the newly created tag (or null if type byte is invalid)
     */
    public static Object createTag(byte type) {
    	return NMSNBTVersioning.M_NBTBASE_CREATETAG.callOrGet(getClassType(), null, type);
    }

    /**
     * <p>Gets a tag type string representation (differs from tag type names as in {@link #getTypeList()}).</p>
     * <p>Implementation changes: THis method has changed a lot, it may exists or not</p>
     * 
     * @param type The type of the tag.
     * @return Returns the string representation of the tag type (like 'TAG_Short').
     */
    public static String getTagName(int type) {
    	return String.class.cast(NMSNBTVersioning.M_NBTBASE_GETNAMEBYID.callOrGet(getClassType(), null, type));
    }

    /**
     * <p>Gets the type of the given tag.</p> 
     * 
     * @param obj The tag to get the type from (any subclasses of NMS {@code NBTBase}).
     * @return Returns the tag type as byte.
     */
    public static byte getTypeId(Object obj) {
        if (!NBTBaseReflection.isNBTTag(obj)) {
            return -1;
        }
        Object r = NMSNBTVersioning.M_NBTBASE_GETTYPEID.callOrGet(getClassType(), obj);
        return (r == null) ? ((byte) -1) : Byte.class.cast(r).byteValue();
    }

    public static Class<?> getClassType() {
        return NBTBaseReflection.nbtTagBase;
    }


    /**
     * <p>Gets the list of tag type names.</p>
     * <p>Implementation notice:
     * 	<ul>
     * 		<li>Not available in MC &lt;= 1.4.X</li>
     * 		<li>{@code static} modifier removed in MC 1.13</li>
     * 	</ul>
     * </p>
     * 
     * @return Returns the type name list if available.
     * @deprecated The list of tag names availability cannot be assured.
     */
    @Deprecated
    public static String[] getTypeList() {
        if (NBTBaseReflection.tagList == null) {
            return null;
        }
        return Arrays.copyOf(NBTBaseReflection.tagList, NBTBaseReflection.tagList.length);
    }
}