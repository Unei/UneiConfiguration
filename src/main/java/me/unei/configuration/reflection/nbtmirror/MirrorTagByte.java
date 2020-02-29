package me.unei.configuration.reflection.nbtmirror;

import java.util.List;
import java.util.Map;

import me.unei.configuration.reflection.NBTBaseReflection;
import me.unei.configuration.reflection.NBTNumberReflection;

public final class MirrorTagByte extends MirrorTagNumber {

	public MirrorTagByte(Object original) {
		super(original, NBTNumberReflection::isNBTByte);
	}

	public byte getValue() {
		return asByte();
	}

	@Override
	public byte asByte() {
		return NBTNumberReflection.getByte(mirroredTag);
	}

	@Override
	public Byte asJLNumber() {
		return Byte.valueOf(asByte());
	}

	@Override
	public Byte getAsObject() {
		return this.getAsObject(DEFAULT_CREATOR);
	}

	@Override
	public <M extends Map<String, Object>, L extends List<Object>> Byte getAsObject(ObjectCreator<M, L> creator) {
		return Byte.valueOf(this.getValue());
	}

	@Override
	public MirrorTagByte clone() {
		return new MirrorTagByte(NBTBaseReflection.cloneNBT(mirroredTag));
	}

	@Override
	public me.unei.configuration.formats.nbtlib.TagByte localCopy() {
		return new me.unei.configuration.formats.nbtlib.TagByte(getValue());
	}
}
