package me.unei.configuration.reflection.nbtmirror;

import java.util.List;
import java.util.Map;

import me.unei.configuration.reflection.NBTBaseReflection;
import me.unei.configuration.reflection.NBTNumberReflection;

public final class MirrorTagInt extends MirrorTag {

	public MirrorTagInt(Object original) {
		super(original, NBTNumberReflection::isNBTInteger);
	}

	public int getValue() {
		return NBTNumberReflection.getInt(mirroredTag);
	}

	@Override
	public Integer getAsObject() {
		return this.getAsObject(DEFAULT_CREATOR);
	}

	@Override
	public <M extends Map<String, Object>, L extends List<Object>> Integer getAsObject(ObjectCreator<M, L> creator) {
		return Integer.valueOf(this.getValue());
	}

	@Override
	public MirrorTagInt clone() {
		return new MirrorTagInt(NBTBaseReflection.cloneNBT(mirroredTag));
	}

	@Override
	public me.unei.configuration.formats.nbtlib.TagInt localCopy() {
		return new me.unei.configuration.formats.nbtlib.TagInt(getValue());
	}
}
