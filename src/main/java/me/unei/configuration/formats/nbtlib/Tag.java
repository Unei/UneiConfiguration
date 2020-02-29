package me.unei.configuration.formats.nbtlib;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.unei.configuration.api.format.INBTTag;
import me.unei.configuration.api.format.TagType;

public abstract class Tag implements INBTTag, Cloneable {

	@Deprecated
	public static final byte TAG_End = 0;
	@Deprecated
	public static final byte TAG_Byte = 1;
	@Deprecated
	public static final byte TAG_Short = 2;
	@Deprecated
	public static final byte TAG_Int = 3;
	@Deprecated
	public static final byte TAG_Long = 4;
	@Deprecated
	public static final byte TAG_Float = 5;
	@Deprecated
	public static final byte TAG_Double = 6;
	@Deprecated
	public static final byte TAG_Byte_Array = 7;
	@Deprecated
	public static final byte TAG_String = 8;
	@Deprecated
	public static final byte TAG_List = 9;
	@Deprecated
	public static final byte TAG_Compound = 10;
	@Deprecated
	public static final byte TAG_Int_Array = 11;
	@Deprecated
	public static final byte TAG_Long_Array = 12;
	@Deprecated
	public static final byte Number_TAG = 99;

	abstract void write(DataOutput output) throws IOException;

	abstract void read(DataInput input) throws IOException;

	@Override
	public abstract String toString();

	public abstract byte getTypeId();

	protected Tag() {
	}

	Object getAsObject() {
		return getAsObject(DEFAULT_CREATOR);
	}

	abstract <M extends Map<String, Object>, L extends List<Object>> Object getAsObject(ObjectCreator<M, L> creator);

	abstract Object getAsNMS();

	abstract void getFromNMS(Object nmsObject);

	public me.unei.configuration.reflection.nbtmirror.MirrorTag getAsMirrorTag() {
		return me.unei.configuration.reflection.nbtmirror.MirrorTag.wrap(getAsNMS());
	}

	static Tag newTag(byte type) {
		switch (type) {
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

	public static Tag newTag(TagType type) {
		switch (type) {
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

	public static String getTagName(TagType type) {
		if (type != null) {
			return type.getTagName();
		}
		return "UNKNOWN";
	}

	@Deprecated
	public static String getTagName(byte type) {
		switch (type) {
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

	public static interface ObjectCreator<M extends Map<String, Object>, L extends List<Object>> {
		public M newMap();

		public L newList();
	}

	public static class DefaultObjectCreator implements ObjectCreator<HashMap<String, Object>, ArrayList<Object>> {
		@Override
		public HashMap<String, Object> newMap() {
			return new HashMap<>();
		}

		@Override
		public ArrayList<Object> newList() {
			return new ArrayList<>();
		}
	}

	protected static final DefaultObjectCreator DEFAULT_CREATOR = new DefaultObjectCreator();
}
