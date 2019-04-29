package me.unei.configuration.formats.nbtlib;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import me.unei.configuration.api.format.TagType;

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
        return getType().getId();
    }

    @Override
    public TagType getType() {
        return TagType.TAG_End;
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
    	return this.getAsObject(DEFAULT_CREATOR);
    }
    
    @Override
    public <M extends Map<String, Object>, L extends List<Object>> Void getAsObject(ObjectCreator<M, L> creator) {
    	return null;
    }

    @Override
    public TagEnd clone() {
        return new TagEnd();
    }
}