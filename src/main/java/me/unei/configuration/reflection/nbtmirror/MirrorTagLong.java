package me.unei.configuration.reflection.nbtmirror;

import java.util.List;
import java.util.Map;

import me.unei.configuration.reflection.NBTBaseReflection;
import me.unei.configuration.reflection.NBTNumberReflection;

public final class MirrorTagLong extends MirrorTag {

	public MirrorTagLong(Object original) {
		super(original, NBTNumberReflection::isNBTLong);
	}

	public long getValue() {
		return NBTNumberReflection.getLong(mirroredTag);
	}

	@Override
	public Long getAsObject() {
		return this.getAsObject(DEFAULT_CREATOR);
	}

	@Override
	public <M extends Map<String, Object>, L extends List<Object>> Long getAsObject(ObjectCreator<M, L> creator) {
		return Long.valueOf(this.getValue());
	}

	@Override
	public MirrorTagLong clone() {
		return new MirrorTagLong(NBTBaseReflection.cloneNBT(mirroredTag));
	}

	@Override
	public me.unei.configuration.formats.nbtlib.TagLong localCopy() {
		return new me.unei.configuration.formats.nbtlib.TagLong(getValue());
	}
}
