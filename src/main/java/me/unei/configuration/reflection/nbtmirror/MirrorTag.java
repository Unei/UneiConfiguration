package me.unei.configuration.reflection.nbtmirror;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import me.unei.configuration.api.format.INBTTag;
import me.unei.configuration.api.format.TagType;
import me.unei.configuration.reflection.NBTBaseReflection;

public abstract class MirrorTag implements INBTTag, Cloneable {

	protected static class RunResult<T> {
		public final boolean success;
		public final T result;

		public RunResult(boolean s, T r) {
			success = s;
			result = r;
		}
	}

	protected final Predicate<Object> checkTagType;
	protected Object mirroredTag;

	@Override
	public final byte getTypeId() {
		return NBTBaseReflection.getTypeId(mirroredTag);
	}

	@Override
	public final TagType getType() {
		return TagType.getByTypeId(getTypeId());
	}

	protected MirrorTag(Object tag, Predicate<Object> checker) {
		if (tag == null) {
			throw new IllegalArgumentException("Cannot mirror null value");
		}

		if (!checker.test(tag)) {
			throw new IllegalArgumentException("Trying to mirror wrong NBTTag type");
		}
		this.mirroredTag = tag;
		this.checkTagType = checker;
	}

	Object getAsObject() {
		return getAsObject(DEFAULT_CREATOR);
	}

	@Override
	public String getString() {
		return NBTBaseReflection.getString(mirroredTag);
	}

	abstract <M extends Map<String, Object>, L extends List<Object>> Object getAsObject(ObjectCreator<M, L> creator);

	static MirrorTag createTag(byte type) {
		return wrap(NBTBaseReflection.createTag(type));
	}

	public static MirrorTag wrap(Object tag) {
		if (!NBTBaseReflection.isNBTTag(tag)) {
			return null;
		}

		switch (NBTBaseReflection.getTypeId(tag)) {
			case 0:
				return new MirrorTagEnd(tag);

			case 1:
				return new MirrorTagByte(tag);

			case 2:
				return new MirrorTagShort(tag);

			case 3:
				return new MirrorTagInt(tag);

			case 4:
				return new MirrorTagLong(tag);

			case 5:
				return new MirrorTagFloat(tag);

			case 6:
				return new MirrorTagDouble(tag);

			case 7:
				return new MirrorTagByteArray(tag);

			case 8:
				return new MirrorTagString(tag);

			case 9:
				return new MirrorTagList(tag);

			case 10:
				return new MirrorTagCompound(tag);

			case 11:
				return new MirrorTagIntArray(tag);

			case 12:
				return new MirrorTagLongArray(tag);
		}
		return null;
	}

	public Object getNMS() {
		return mirroredTag;
	}

	public void setNMS(Object nmsObject) {
		if (nmsObject == null) {
			throw new IllegalArgumentException("Cannot mirror null value");
		}

		if (!this.checkTagType.test(nmsObject)) {
			throw new IllegalArgumentException("Cannot mirror anything but the right NMS NBT tag");
		}
		mirroredTag = nmsObject;
	}

	public static String getTagName(TagType type) {
		if (type != null) {
			return type.getTagName();
		}
		return "UNKNOWN";
	}

	public final boolean isEmpty() {
		return NBTBaseReflection.isEmpty(mirroredTag);
	}

	@Override
	public final boolean equals(Object other) {
		if (other instanceof MirrorTag) {
			return this.mirroredTag.equals(((MirrorTag) other).mirroredTag);
		}
		return false;
	}

	@Override
	public final int hashCode() {
		return this.mirroredTag.hashCode();
	}

	public final String toString() {
		return this.mirroredTag.toString();
	}

	public abstract me.unei.configuration.formats.nbtlib.Tag localCopy();

	@Override
	public abstract MirrorTag clone();

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
