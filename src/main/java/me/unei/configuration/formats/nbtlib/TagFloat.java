package me.unei.configuration.formats.nbtlib;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import me.unei.configuration.api.format.TagType;
import me.unei.configuration.reflection.NBTNumberReflection;

public final class TagFloat extends Tag {

	private float data;

	TagFloat() {
	}

	public TagFloat(float value) {
		this.data = value;
	}

	@Override
	void write(DataOutput output) throws IOException {
		output.writeFloat(this.data);
	}

	@Override
	void read(DataInput output) throws IOException {
		this.data = output.readFloat();
	}

	Object getAsNMS() {
		return NBTNumberReflection.newFloat(this.getValue());
	}

	void getFromNMS(Object nmsFloat) {
		if (NBTNumberReflection.isNBTFloat(nmsFloat)) {
			this.data = NBTNumberReflection.getFloat(nmsFloat);
		}
	}

	@Override
	public byte getTypeId() {
		return getType().getId();
	}

	@Override
	public TagType getType() {
		return TagType.TAG_Float;
	}

	@Override
	public String toString() {
		return Float.toString(this.data) + "f";
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ Float.floatToIntBits(this.data);
	}

	public float getValue() {
		return this.data;
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
	public boolean equals(Object other) {
		if (!super.equals(other)) {
			return false;
		}
		TagFloat tb = (TagFloat) other;
		return (tb.data == this.data);
	}

	@Override
	public TagFloat clone() {
		return new TagFloat(this.data);
	}
}
