package me.unei.configuration.reflection.nbtmirror;

import java.util.List;
import java.util.Map;

import me.unei.configuration.reflection.NBTBaseReflection;
import me.unei.configuration.reflection.NBTNumberReflection;

public final class MirrorTagDouble extends MirrorTag {

	public MirrorTagDouble(Object originalTag) {
		super(originalTag, NBTNumberReflection::isNBTDouble);
	}

	public double getValue() {
		return NBTNumberReflection.getDouble(mirroredTag);
	}

	@Override
	public Double getAsObject() {
		return this.getAsObject(DEFAULT_CREATOR);
	}

	@Override
	public <M extends Map<String, Object>, L extends List<Object>> Double getAsObject(ObjectCreator<M, L> creator) {
		return Double.valueOf(this.getValue());
	}

	@Override
	public MirrorTagDouble clone() {
		return new MirrorTagDouble(NBTBaseReflection.cloneNBT(mirroredTag));
	}

	@Override
	public me.unei.configuration.formats.nbtlib.TagDouble localCopy() {
		return new me.unei.configuration.formats.nbtlib.TagDouble(getValue());
	}
}
