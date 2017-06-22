package me.unei.configuration.formats.nbtlib;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

class TagEnd extends Tag {

    public TagEnd() {
        super();
    }

    @Override
    void read(DataInput input) throws IOException {
    }

    @Override
    void write(DataOutput output) throws IOException {
    }

    @Override
    public byte getTypeId() {
        return Tag.TAG_End;
    }

    @Override
    public String toString() {
        return "END";
    }

    @Override
    public TagEnd clone() {
        return new TagEnd();
    }
}