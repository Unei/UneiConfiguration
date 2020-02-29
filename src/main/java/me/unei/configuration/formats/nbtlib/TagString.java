package me.unei.configuration.formats.nbtlib;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import me.unei.configuration.api.format.INBTString;
import me.unei.configuration.api.format.TagType;
import me.unei.configuration.reflection.NBTStringReflection;

@SuppressWarnings("deprecation")
public final class TagString extends Tag implements INBTString {

	private String data;

	public TagString() {
		this.data = new String("");
	}

	public TagString(String s) {
		if (s == null) {
			throw new NullPointerException("Null string not allowed");
		}
		this.data = s;
	}

	@Override
	void write(DataOutput output) throws IOException {
		output.writeUTF(this.data);
	}

	@Override
	void read(DataInput input) throws IOException {
		this.data = input.readUTF();
	}

	Object getAsNMS() {
		return NBTStringReflection.newInstance(this.data);
	}

	void getFromNMS(Object strNMS) {
		if (NBTStringReflection.isNBTString(strNMS)) {
			this.data = NBTStringReflection.getString(strNMS);
		}
	}

	@Override
	public byte getTypeId() {
		return getType().getId();
	}

	@Override
	public TagType getType() {
		return TagType.TAG_String;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		char delimiter = '\0';

		for (int i = 0; i < this.data.length(); ++i) {
			char c = this.data.charAt(i);

			if (c == '\\') {
				sb.append('\\');
			} else if (c == '\'' || c == '"') {

				if (delimiter == '\0') {
					delimiter = (c == '\'') ? '"' : '\'';
				} else if (delimiter == c) {
					sb.append('\\');
				}
			}

			sb.append(c);
		}

		if (delimiter == '\0') {
			delimiter = '"';
		}

		sb.append(delimiter);
		sb.insert(0, delimiter);

		return sb.toString();
	}

	@Override
	public TagString clone() {
		return new TagString(this.data);
	}

	@Override
	public boolean isEmpty() {
		return this.data.isEmpty();
	}

	@Override
	public boolean equals(Object other) {
		if (!super.equals(other)) {
			return false;
		}
		return (other.toString().equals(this.toString()));
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ this.data.hashCode();
	}

	public static String toStr(String content) {
		StringBuilder sb = new StringBuilder("\"");

		for (int i = 0; i < content.length(); ++i) {
			char c = content.charAt(i);

			if (c == '\\' || c == '"')
				sb.append('\\');
			sb.append(c);
		}
		return sb.append("\"").toString();
	}

	@Override
	public String getString() {
		return this.data;
	}

	@Override
	public String getAsObject() {
		return this.getAsObject(DEFAULT_CREATOR);
	}

	@Override
	public <M extends Map<String, Object>, L extends List<Object>> String getAsObject(ObjectCreator<M, L> creator) {
		return this.getString();
	}
}
