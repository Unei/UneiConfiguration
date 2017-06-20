package me.unei.configuration.formats.nbtlib;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

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
        if (tag.getTypeId() != Tag.TAG_End) {
            output.writeUTF("");
            tag.write(output);
        }
    }

    private static Tag readNBT(DataInput input) throws IOException {
        byte type = input.readByte();

        if (type == Tag.TAG_End) {
            return new TagEnd();
        } else {
            input.readUTF();
            Tag base = Tag.newTag(type);

            try {
                base.read(input);
                return base;
            } catch (IOException exception) {
                throw new RuntimeException("Unable to load NBT data [UNNAMED TAG] (" + Byte.valueOf(type) + ")", exception);
            }
        }
    }
}