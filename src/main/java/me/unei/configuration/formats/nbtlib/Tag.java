package me.unei.configuration.formats.nbtlib;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public abstract class Tag implements Cloneable {

    public static final byte TAG_End = 0;
    public static final byte TAG_Byte = 1;
    public static final byte TAG_Short = 2;
    public static final byte TAG_Int = 3;
    public static final byte TAG_Long = 4;
    public static final byte TAG_Float = 5;
    public static final byte TAG_Double = 6;
    public static final byte TAG_Byte_Array = 7;
    public static final byte TAG_String = 8;
    public static final byte TAG_List = 9;
    public static final byte TAG_Compound = 10;
    public static final byte TAG_Int_Array = 11;
    public static final byte TAG_Long_Array = 12;
    public static final byte Number_TAG = 99;

    abstract void write(DataOutput output) throws IOException;

    abstract void read(DataInput input) throws IOException;

    @Override
    public abstract String toString();

    public abstract byte getTypeId();

    protected Tag() {
    }

    public static Tag newTag(byte type) {
        switch(type) {
            case TAG_End:
                return new TagEnd();
            case TAG_Byte:
                return new TagByte();
            case TAG_Short:
                return new TagShort();
            case TAG_Int:
                return new TagInt();
            case TAG_Long:
                return new TagLong();
            case TAG_Float:
                return new TagFloat();
            case TAG_Double:
                return new TagDouble();
            case TAG_Byte_Array:
                return new TagByteArray();
            case TAG_Int_Array:
                return new TagIntArray();
            case TAG_String:
                return new TagString();
            case TAG_List:
                return new TagList();
            case TAG_Compound:
                return new TagCompound();
            case TAG_Long_Array:
                return new TagLongArray();

            default:
                return null;
        }
    }

    public static String getTagName(byte type) {
        switch(type) {
            case TAG_End:
                return "TAG_End";
            case TAG_Byte:
                return "TAG_Byte";
            case TAG_Short:
                return "TAG_Short";
            case TAG_Int:
                return "TAG_Int";
            case TAG_Long:
                return "TAG_Long";
            case TAG_Float:
                return "TAG_Float";
            case TAG_Double:
                return "TAG_Double";
            case TAG_Byte_Array:
                return "TAG_Byte_Array";
            case TAG_Int_Array:
                return "TAG_Int_Array";
            case TAG_String:
                return "TAG_String";
            case TAG_List:
                return "TAG_List";
            case TAG_Compound:
                return "TAG_Compound";
            case TAG_Long_Array:
                return "TAG_Long_Array";

            case Number_TAG:
                return "Any Numeric Tag";

            default:
                return "UNKNOWN";
        }
    }

    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof Tag && this.getTypeId() == ((Tag) other).getTypeId());
    }

    @Override
    public int hashCode() {
        return this.getTypeId();
    }

    public String getString() {
        return this.toString();
    }

    @Override
    public abstract Tag clone();
}