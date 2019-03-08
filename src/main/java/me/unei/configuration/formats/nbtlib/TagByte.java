package me.unei.configuration.formats.nbtlib;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import me.unei.configuration.reflection.NBTNumberReflection;

public final class TagByte extends Tag {

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
    public Byte getAsObject() {
    	return this.getAsObject(DEFAULT_CREATOR);
    }
    
    @Override
    public <M extends Map<String, Object>, L extends List<Object>> Byte getAsObject(ObjectCreator<M, L> creator) {
    	return Byte.valueOf(this.getValue());
    }
    
    Object getAsNMS() {
    	return NBTNumberReflection.newByte(this.getValue());
    }
    
    void getFromNMS(Object nmsByte) {
    	if (NBTNumberReflection.isNBTByte(nmsByte)) {
    		this.data = NBTNumberReflection.getByte(nmsByte);
    	}
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