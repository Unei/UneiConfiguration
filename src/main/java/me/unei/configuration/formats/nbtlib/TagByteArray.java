package me.unei.configuration.formats.nbtlib;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import me.unei.configuration.api.format.TagType;
import me.unei.configuration.reflection.NBTArrayReflection;

public final class TagByteArray extends Tag {

	private byte[] data;

	TagByteArray() {
	}

	public TagByteArray(byte[] value) {
		this.data = value;
	}

	@Override
	void write(DataOutput output) throws IOException {
		output.writeInt(this.data.length);
		output.write(this.data);
	}

	@Override
	void read(DataInput input) throws IOException {
		int len = input.readInt();
		this.data = new byte[len];
		input.readFully(this.data);
	}

	Object getAsNMS() {
		return NBTArrayReflection.newByteArray(this.getByteArray());
	}

	void getFromNMS(Object nmsByteArray) {
		if (NBTArrayReflection.isNBTByteArray(nmsByteArray)) {
			this.data = NBTArrayReflection.getByteArray(nmsByteArray);
		}
	}

	@Override
	public byte getTypeId() {
		return getType().getId();
	}

	@Override
	public TagType getType() {
		return TagType.TAG_Byte_Array;
	}

	@Override
	public String toString() {
		return "[" + Integer.toString(this.data.length) + " bytes]";
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ Arrays.hashCode(this.data);
	}

	public int size() {
		return this.data.length;
	}

	public byte[] getByteArray() {
		return this.data;
	}

	public byte[] getAsObject() {
		return this.getAsObject(DEFAULT_CREATOR);
	}

	@Override
	public <M extends Map<String, Object>, L extends List<Object>> byte[] getAsObject(ObjectCreator<M, L> creator) {
		return this.getByteArray();
	}

	@Override
	public boolean equals(Object other) {
		if (!super.equals(other)) {
			return false;
		}
		TagByteArray tb = (TagByteArray) other;
		return Arrays.equals(this.data, tb.data);
	}

	@Override
	public TagByteArray clone() {
		byte[] copy = new byte[this.data.length];

		System.arraycopy(this.data, 0, copy, 0, this.data.length);
		return new TagByteArray(copy);
	}
}
