package me.unei.configuration.reflection.nbtmirror;

import java.util.List;
import java.util.Map;

import me.unei.configuration.reflection.NBTArrayReflection;
import me.unei.configuration.reflection.NBTBaseReflection;

public final class MirrorTagIntArray extends MirrorTag {

	public MirrorTagIntArray(Object original) {
		super(original, NBTArrayReflection::isNBTIntArray);
	}

	public int size() {
		return NBTArrayReflection.getSize(mirroredTag);
	}

	public int[] getIntArray() {
		return NBTArrayReflection.getIntArray(mirroredTag);
	}

	public int[] getAsObject() {
		return this.getAsObject(DEFAULT_CREATOR);
	}

	@Override
	public <M extends Map<String, Object>, L extends List<Object>> int[] getAsObject(ObjectCreator<M, L> creator) {
		return this.getIntArray();
	}

	@Override
	public MirrorTagIntArray clone() {
		return new MirrorTagIntArray(NBTBaseReflection.cloneNBT(mirroredTag));
	}

	@Override
	public me.unei.configuration.formats.nbtlib.TagIntArray localCopy() {
		return new me.unei.configuration.formats.nbtlib.TagIntArray(getIntArray());
	}
}
