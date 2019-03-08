package me.unei.configuration.formats.nbtlib;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import me.unei.configuration.reflection.NBTNumberReflection;

public final class TagDouble extends Tag {

    private double data;

    TagDouble() {
    }

    public TagDouble(double value) {
        this.data = value;
    }

    @Override
    void write(DataOutput output) throws IOException {
        output.writeDouble(this.data);
    }

    @Override
    void read(DataInput output) throws IOException {
        this.data = output.readDouble();
    }
    
    Object getAsNMS() {
    	return NBTNumberReflection.newDouble(this.getValue());
    }
    
    void getFromNMS(Object nmsDouble) {
    	if (NBTNumberReflection.isNBTDouble(nmsDouble)) {
    		this.data = NBTNumberReflection.getDouble(nmsDouble);
    	}
    }

    @Override
    public byte getTypeId() {
        return Tag.TAG_Double;
    }

    @Override
    public String toString() {
        return Double.toString(this.data) + "d";
    }

    @Override
    public int hashCode() {
        long i = Double.doubleToLongBits(this.data);
        return super.hashCode() ^ (int) (i ^ i >>> 32);
    }

    public double getValue() {
        return this.data;
    }
    
    @Override
    public Double getAsObject() {
    	return this.getAsObject(DEFAULT_CREATOR);
    }
    
    @Override
    public <M extends Map<String, Object>, L extends List<Object>> Double getAsObject(ObjectCreator<M, L> creator) {
    	return Double.valueOf(this.getValue());
    }

    @Override
    public boolean equals(Object other) {
        if (!super.equals(other)) {
            return false;
        }
        TagDouble tb = (TagDouble) other;
        return (tb.data == this.data);
    }

    @Override
    public TagDouble clone() {
        return new TagDouble(this.data);
    }
}