package me.unei.configuration.formats.nbtlib;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import me.unei.configuration.api.format.TagType;
import me.unei.configuration.reflection.NBTNumberReflection;

public final class TagShort extends Tag {

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
    
    Object getAsNMS() {
    	return NBTNumberReflection.newShort(this.getValue());
    }
    
    void getFromNMS(Object nmsShort) {
    	if (NBTNumberReflection.isNBTShort(nmsShort)) {
    		this.data = NBTNumberReflection.getShort(nmsShort);
    	}
    }

    @Override
    public byte getTypeId() {
        return getType().getId();
    }

    @Override
    public TagType getType() {
        return TagType.TAG_Short;
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
    public Short getAsObject() {
    	return this.getAsObject(DEFAULT_CREATOR);
    }
    
    @Override
    public <M extends Map<String, Object>, L extends List<Object>> Short getAsObject(ObjectCreator<M, L> creator) {
    	return Short.valueOf(this.getValue());
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