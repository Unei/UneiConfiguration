package me.unei.configuration.reflection.nbtmirror;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import me.unei.configuration.ArrayTools;
import me.unei.configuration.SerializerHelper;
import me.unei.configuration.api.exceptions.NotImplementedException;
import me.unei.configuration.api.exceptions.UnexpectedClassException;
import me.unei.configuration.api.format.INBTCompound;
import me.unei.configuration.api.format.INBTList;
import me.unei.configuration.api.format.INBTTag;
import me.unei.configuration.api.format.TagType;
import me.unei.configuration.formats.nbtlib.TagCompound;
import me.unei.configuration.formats.nbtlib.TagList;
import me.unei.configuration.reflection.NBTBaseReflection;
import me.unei.configuration.reflection.NBTCompoundReflection;

public final class MirrorTagCompound extends MirrorTag implements INBTCompound {

	public MirrorTagCompound(Object original) {
		super(original, NBTCompoundReflection::isNBTCompound);
	}

	public void loadMap(Map<?, ?> datas) throws UnexpectedClassException {
		this.clear();

		for (Entry<?, ?> entry : datas.entrySet()) {
			String key = entry.getKey().toString();
			Object value = entry.getValue();

			if (value == null) {
				continue;
			}

			if (value instanceof CharSequence) {
				this.setString(key, value.toString());
			} else if (value instanceof MirrorTag) {
				this.set(key, (MirrorTag) value);
			} else if (value instanceof me.unei.configuration.formats.nbtlib.Tag) {
				this.set(key, ((me.unei.configuration.formats.nbtlib.Tag) value).getAsMirrorTag());
			} else if (value instanceof Map) {
				TagCompound subTag = new TagCompound();
				subTag.loadMap((Map<?, ?>) value);
				this.set(key, subTag);
			} else if (value instanceof Iterable) {
				Class<?> subType = ArrayTools.getIterableParam((Iterable<?>) value);

				if (subType == null) {
					TagList subTag = new TagList();
					subTag.loadList((Iterable<?>) value);
					this.set(key, subTag);
				} else if (subType.equals(byte.class) || subType.equals(Byte.class)) {
					this.setByteArray(key, ArrayTools.toBytes(value));
				} else if (subType.equals(int.class) || subType.equals(Integer.class)) {
					this.setIntArray(key, ArrayTools.toInts(value));
				} else if (subType.equals(long.class) || subType.equals(Long.class)) {
					this.setLongArray(key, ArrayTools.toLongs(value));
				} else {
					TagList subTag = new TagList();
					subTag.loadList((Iterable<?>) value);
					this.set(key, subTag);
				}
			} else if (value instanceof Integer) {
				this.setInt(key, (Integer) value);
			} else if (value instanceof Byte) {
				this.setByte(key, (Byte) value);
			} else if (value instanceof Double) {
				this.setDouble(key, (Double) value);
			} else if (value instanceof Short) {
				this.setShort(key, (Short) value);
			} else if (value instanceof Long) {
				this.setLong(key, (Long) value);
			} else if (value instanceof Float) {
				this.setFloat(key, (Float) value);
			} else if (value instanceof Boolean) {
				this.setBoolean(key, (Boolean) value);
			} else if (value instanceof UUID) {
				this.setUUID(key, (UUID) value);
			} else if (value instanceof int[]) {
				this.setIntArray(key, (int[]) value);
			} else if (value instanceof Integer[]) {
				this.setIntArray(key, ArrayTools.toPrimitive((Integer[]) value));
			} else if (value instanceof byte[]) {
				this.setByteArray(key, (byte[]) value);
			} else if (value instanceof Byte[]) {
				this.setByteArray(key, ArrayTools.toPrimitive((Byte[]) value));
			} else if (value instanceof long[]) {
				this.setLongArray(key, (long[]) value);
			} else if (value instanceof Long[]) {
				this.setLongArray(key, ArrayTools.toPrimitive((Long[]) value));
			} else if (value instanceof Serializable) {
				this.setByteArray(key + "Object", SerializerHelper.serialize(value));
			} else {
				throw new UnexpectedClassException(value.getClass());
			}
		}
	}

	@Override
	public Map<String, Object> getAsObject() {
		return getAsObject(DEFAULT_CREATOR);
	}

	public <M extends Map<String, Object>, L extends List<Object>> M getAsObject(ObjectCreator<M, L> creator) {
		if (creator == null) {
			return null;
		}
		M result = creator.newMap();

		for (String key : keySet()) {
			MirrorTag t = get(key);

			if (key.endsWith("Most") || key.endsWith("Least")) {

				if (key.endsWith("Most")) {
					key = key.substring(0, key.length() - "Most".length());
				} else if (key.endsWith("Least")) {
					key = key.substring(0, key.length() - "Least".length());
				}

				if (this.isUUID(key)) {

					if (!result.containsKey(key)) {
						result.put(key, this.getUUID(key));
					}
				} else {
					result.put(key, t.getAsObject(creator));
				}
			} else if (key.endsWith("Object") && this.hasKeyOfType(key, TagType.TAG_Byte_Array)) {
				key = key.substring(0, key.length() - "Object".length());
				Object r = SerializerHelper.deserialize(this.getByteArray(key));

				if (r != null) {
					result.put(key, r);
				} else {
					result.put(key, t);
				}
			} else {
				result.put(key, t.getAsObject(creator));
			}
		}
		return result;
	}

	@Override
	public Set<String> keySet() {
		return NBTCompoundReflection.keySet(mirroredTag);
	}

	@Override
	public me.unei.configuration.formats.nbtlib.TagCompound localCopy() {
		me.unei.configuration.formats.nbtlib.TagCompound cmp = new me.unei.configuration.formats.nbtlib.TagCompound();
		cmp.getFromNMS(mirroredTag);
		return cmp;
	}

	@Override
	public int size() {
		return NBTCompoundReflection.size(mirroredTag);
	}

	public void set(String key, INBTTag tag) {
		if (tag instanceof MirrorTag) {
			this.set(key, (MirrorTag) tag);
		} else if (tag instanceof me.unei.configuration.formats.nbtlib.Tag) {
			this.set(key, ((me.unei.configuration.formats.nbtlib.Tag) tag).getAsMirrorTag());
		}
	}

	public void set(String key, MirrorTag tag) {
		NBTCompoundReflection.set(mirroredTag, key, tag.getNMS());
	}

	public void setByte(String key, byte value) {
		NBTCompoundReflection.setByte(mirroredTag, key, value);
	}

	public void setShort(String key, short value) {
		NBTCompoundReflection.setShort(mirroredTag, key, value);
	}

	public void setInt(String key, int value) {
		NBTCompoundReflection.setInt(mirroredTag, key, value);
	}

	public void setLong(String key, long value) {
		NBTCompoundReflection.setLong(mirroredTag, key, value);
	}

	public void setUUID(String key, UUID uuid) {
		NBTCompoundReflection.setUUID(mirroredTag, key, uuid);
	}

	public UUID getUUID(String key) {
		return NBTCompoundReflection.getUUID(mirroredTag, key);
	}

	public boolean isUUID(String key) {
		return NBTCompoundReflection.isUUID(mirroredTag, key);
	}

	public void setFloat(String key, float value) {
		NBTCompoundReflection.setFloat(mirroredTag, key, value);
	}

	public void setDouble(String key, double value) {
		NBTCompoundReflection.setDouble(mirroredTag, key, value);
	}

	public void setString(String key, String value) {
		NBTCompoundReflection.setString(mirroredTag, key, value);
	}

	public void setByteArray(String key, byte[] values) {
		NBTCompoundReflection.setByteArray(mirroredTag, key, values);
	}

	public void setIntArray(String key, int[] values) {
		NBTCompoundReflection.setIntArray(mirroredTag, key, values);
	}

	public void setLongArray(String key, long[] values) {
		NBTCompoundReflection.setLongArray(mirroredTag, key, values);
	}

	public void setBoolean(String key, boolean value) {
		NBTCompoundReflection.setBoolean(mirroredTag, key, value);
	}

	public MirrorTag get(String key) {
		return wrap(NBTCompoundReflection.get(mirroredTag, key));
	}

	public byte getTypeOf(String key) {
		return NBTCompoundReflection.getTypeOf(mirroredTag, key);
	}

	@Override
	public TagType getTypeOfTag(String key) {
		return TagType.getByTypeId(getTypeId());
	}

	public boolean hasKey(String key) {
		return NBTCompoundReflection.hasKey(mirroredTag, key);
	}

	public boolean hasKeyOfType(String key, byte type) {
		return NBTCompoundReflection.hasKeyOfType(mirroredTag, key, type);
	}

	public boolean hasKeyOfType(String key, TagType type) {
		return hasKeyOfType(key, type.getId());
	}

	public byte getByte(String key) {
		return NBTCompoundReflection.getByte(mirroredTag, key);
	}

	public short getShort(String key) {
		return NBTCompoundReflection.getShort(mirroredTag, key);
	}

	public int getInt(String key) {
		return NBTCompoundReflection.getInt(mirroredTag, key);
	}

	public long getLong(String key) {
		return NBTCompoundReflection.getLong(mirroredTag, key);
	}

	public float getFloat(String key) {
		return NBTCompoundReflection.getFloat(mirroredTag, key);
	}

	public double getDouble(String key) {
		return NBTCompoundReflection.getDouble(mirroredTag, key);
	}

	public String getString(String key) {
		return NBTCompoundReflection.getString(mirroredTag, key);
	}

	public byte[] getByteArray(String key) {
		return NBTCompoundReflection.getByteArray(mirroredTag, key);
	}

	public int[] getIntArray(String key) {
		return NBTCompoundReflection.getIntArray(mirroredTag, key);
	}

	public long[] getLongArray(String key) {
		return NBTCompoundReflection.getLongArray(mirroredTag, key);
	}

	public MirrorTagCompound getCompound(String key) {
		return new MirrorTagCompound(NBTCompoundReflection.getCompound(mirroredTag, key));
	}

	public MirrorTagList getList(String key, byte type) {
		return new MirrorTagList(NBTCompoundReflection.getList(mirroredTag, key, type));
	}

	@Override
	public INBTList getList(String key, TagType type) {
		return getList(key, type.getId());
	}

	public boolean getBoolean(String key) {
		return NBTCompoundReflection.getBoolean(mirroredTag, key);
	}

	public void remove(String key) {
		NBTCompoundReflection.remove(mirroredTag, key);
	}

	public void clear() {
		throw new NotImplementedException("NMS NBTTagCompound class does not allow clear() method");
	}

	@Override
	public MirrorTagCompound clone() {
		return new MirrorTagCompound(NBTBaseReflection.cloneNBT(mirroredTag));
	}
}
