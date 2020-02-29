package me.unei.configuration.reflection.nbtmirror;

import java.util.List;
import java.util.Map;

import me.unei.configuration.reflection.NBTArrayReflection;
import me.unei.configuration.reflection.NBTBaseReflection;

public final class MirrorTagByteArray extends MirrorTag {

	public MirrorTagByteArray(Object original) {
		super(original, NBTArrayReflection::isNBTByteArray);
	}

	public int size() {
		return NBTArrayReflection.getSize(mirroredTag);
	}

	public byte[] getByteArray() {
		return NBTArrayReflection.getByteArray(mirroredTag);
	}

	public byte[] getAsObject() {
		return this.getAsObject(DEFAULT_CREATOR);
	}

	@Override
	public <M extends Map<String, Object>, L extends List<Object>> byte[] getAsObject(ObjectCreator<M, L> creator) {
		return this.getByteArray();
	}

	@Override
	public MirrorTagByteArray clone() {
		return new MirrorTagByteArray(NBTBaseReflection.cloneNBT(mirroredTag));
	}

	@Override
	public me.unei.configuration.formats.nbtlib.TagByteArray localCopy() {
		return new me.unei.configuration.formats.nbtlib.TagByteArray(getByteArray());
	}
}
