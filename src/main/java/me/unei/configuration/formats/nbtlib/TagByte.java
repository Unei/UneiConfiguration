package me.unei.configuration.formats.nbtlib;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class TagByte extends Tag {

    private byte data;

    TagByte() {
    }

    public TagByte(byte value) {
        this.data = value;
    }

    @Override
    void write(DataOutput output) throws IOException {
        output.writeByte(this.data);
    }

    @Override
    void read(DataInput output) throws IOException {
        this.data = output.readByte();
    }

    @Override
    public byte getTypeId() {
        return Tag.TAG_Byte;
    }

    @Override
    public String toString() {
        return Byte.toString(this.data) + "b";
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ this.data;
    }

    public byte getValue() {
        return this.data;
    }

    @Override
    public boolean equals(Object other) {
        if (!super.equals(other)) {
            return false;
        }
        TagByte tb = (TagByte) other;
        return (tb.data == this.data);
    }

    @Override
    public TagByte clone() {
        return new TagByte(this.data);
    }
}