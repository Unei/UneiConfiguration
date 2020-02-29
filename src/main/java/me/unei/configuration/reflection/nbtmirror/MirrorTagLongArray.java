package me.unei.configuration.reflection.nbtmirror;

import java.util.List;
import java.util.Map;

import me.unei.configuration.reflection.NBTArrayReflection;
import me.unei.configuration.reflection.NBTBaseReflection;

public final class MirrorTagLongArray extends MirrorTag {

	public MirrorTagLongArray(Object original) {
		super(original, NBTArrayReflection::isNBTLongArray);
	}

	public int size() {
		return NBTArrayReflection.getSize(mirroredTag);
	}

	public long[] getLongArray() {
		return NBTArrayReflection.getLongArray(mirroredTag);
	}

	public long[] getAsObject() {
		return this.getAsObject(DEFAULT_CREATOR);
	}

	@Override
	public <M extends Map<String, Object>, L extends List<Object>> long[] getAsObject(ObjectCreator<M, L> creator) {
		return this.getLongArray();
	}

	@Override
	public MirrorTagLongArray clone() {
		return new MirrorTagLongArray(NBTBaseReflection.cloneNBT(mirroredTag));
	}

	@Override
	public me.unei.configuration.formats.nbtlib.TagLongArray localCopy() {
		return new me.unei.configuration.formats.nbtlib.TagLongArray(getLongArray());
	}
}
