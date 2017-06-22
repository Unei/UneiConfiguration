package me.unei.configuration.formats.nbtlib;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

class TagInt extends Tag {

    private int data;

    TagInt() {
    }

    public TagInt(int value) {
        this.data = value;
    }

    @Override
    void write(DataOutput output) throws IOException {
        output.writeInt(this.data);
    }

    @Override
    void read(DataInput output) throws IOException {
        this.data = output.readInt();
    }

    @Override
    public byte getTypeId() {
        return Tag.TAG_Int;
    }

    @Override
    public String toString() {
        return Integer.toString(this.data);
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ this.data;
    }

    public int getValue() {
        return this.data;
    }

    @Override
    public boolean equals(Object other) {
        if (!super.equals(other)) {
            return false;
        }
        TagInt tb = (TagInt) other;
        return (tb.data == this.data);
    }

    @Override
    public TagInt clone() {
        return new TagInt(this.data);
    }
}