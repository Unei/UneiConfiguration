package me.unei.configuration.formats.nbtlib;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

final class TagEnd extends Tag {

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
    
    Object getAsNMS() {
    	return null;
    }
    
    void getFromNMS(Object nmsObject) {
    	// Nothing here
    }
    
    @Override
    public Void getAsObject() {
    	return null;
    }

    @Override
    public TagEnd clone() {
        return new TagEnd();
    }
}