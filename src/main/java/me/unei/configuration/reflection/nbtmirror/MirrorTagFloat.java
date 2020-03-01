package me.unei.configuration.reflection.nbtmirror;

import java.util.List;
import java.util.Map;

import me.unei.configuration.reflection.NBTBaseReflection;
import me.unei.configuration.reflection.NBTNumberReflection;

public final class MirrorTagFloat extends MirrorTag {

	public MirrorTagFloat(Object original) {
		super(original, NBTNumberReflection::isNBTFloat);
	}

	public float getValue() {
		return NBTNumberReflection.getFloat(mirroredTag);
	}

	@Override
	public Float getAsObject() {
		return this.getAsObject(DEFAULT_CREATOR);
	}

	@Override
	public <M extends Map<String, Object>, L extends List<Object>> Float getAsObject(ObjectCreator<M, L> creator) {
		return Float.valueOf(this.getValue());
	}

	@Override
	public MirrorTagFloat clone() {
		return new MirrorTagFloat(NBTBaseReflection.cloneNBT(mirroredTag));
	}

	@Override
	public me.unei.configuration.formats.nbtlib.TagFloat localCopy() {
		return new me.unei.configuration.formats.nbtlib.TagFloat(getValue());
	}
}
