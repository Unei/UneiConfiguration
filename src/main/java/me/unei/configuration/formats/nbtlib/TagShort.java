package me.unei.configuration.formats.nbtlib;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class TagShort extends Tag {

    private short data;

    TagShort() {
    }

    public TagShort(short value) {
        this.data = value;
    }

    @Override
    void write(DataOutput output) throws IOException {
        output.writeShort(this.data);
    }

    @Override
    void read(DataInput output) throws IOException {
        this.data = output.readShort();
    }

    @Override
    public byte getTypeId() {
        return Tag.TAG_Short;
    }

    @Override
    public String toString() {
        return Short.toString(this.data) + "s";
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ this.data;
    }

    public short getValue() {
        return this.data;
    }

    @Override
    public boolean equals(Object other) {
        if (!super.equals(other)) {
            return false;
        }
        TagShort tb = (TagShort) other;
        return (tb.data == this.data);
    }

    @Override
    public TagShort clone() {
        return new TagShort(this.data);
    }
}