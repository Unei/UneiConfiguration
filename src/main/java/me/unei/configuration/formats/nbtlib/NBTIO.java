package me.unei.configuration.formats.nbtlib;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import me.unei.configuration.api.format.TagType;

public final class NBTIO {

	public static TagCompound readCompressed(InputStream input) throws IOException {
		DataInputStream datin = new DataInputStream(new BufferedInputStream(new GZIPInputStream(input)));

		TagCompound main;

		try {
			main = NBTIO.read(datin);
		} finally {
			datin.close();
		}

		return main;
	}

	public static void writeCompressed(TagCompound compound, OutputStream stream) throws IOException {
		DataOutputStream datout = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(stream)));

		try {
			NBTIO.write(compound, datout);
		} finally {
			datout.close();
		}
	}

	public static TagCompound read(DataInput input) throws IOException {
		Tag tag = NBTIO.readNBT(input);

		if (tag instanceof TagCompound) {
			return (TagCompound) tag;
		}
		throw new IOException("Root tag must be a named compound tag");
	}

	public static void write(TagCompound compound, DataOutput output) throws IOException {
		NBTIO.writeTag(compound, output);
	}

	private static void writeTag(Tag tag, DataOutput output) throws IOException {
		output.writeByte(tag.getTypeId());

		if (tag.getType() != TagType.TAG_End) {
			output.writeUTF("");
			tag.write(output);
		}
	}

	private static Tag readNBT(DataInput input) throws IOException {
		byte type = input.readByte();

		if (type == TagType.TAG_End.getId()) {
			return new TagEnd();
		} else {
			input.readUTF();
			Tag base = Tag.newTag(type);

			try {
				base.read(input);
				return base;
			} catch (IOException exception) {
				throw new RuntimeException("Unable to load NBT data [UNNAMED TAG] (" + Byte.toString(type) + ")",
						exception);
			}
		}
	}
}
