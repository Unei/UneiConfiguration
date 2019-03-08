package me.unei.configuration.formats.nbtlib;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import me.unei.configuration.reflection.NBTNumberReflection;

public final class TagInt extends Tag {

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
    
    Object getAsNMS() {
    	return NBTNumberReflection.newInt(this.getValue());
    }
    
    void getFromNMS(Object nmsInteger) {
    	if (NBTNumberReflection.isNBTInteger(nmsInteger)) {
    		this.data = NBTNumberReflection.getInt(nmsInteger);
    	}
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
    public Integer getAsObject() {
    	return this.getAsObject(DEFAULT_CREATOR);
    }
    
    @Override
    public <M extends Map<String, Object>, L extends List<Object>> Integer getAsObject(ObjectCreator<M, L> creator) {
    	return Integer.valueOf(this.getValue());
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