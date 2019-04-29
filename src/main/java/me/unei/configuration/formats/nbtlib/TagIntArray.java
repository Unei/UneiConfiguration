package me.unei.configuration.formats.nbtlib;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import me.unei.configuration.api.format.TagType;
import me.unei.configuration.reflection.NBTArrayReflection;

public final class TagIntArray extends Tag {

    private int[] data;

    TagIntArray() {
    }

    public TagIntArray(int[] value) {
        this.data = value;
    }

    @Override
    void write(DataOutput output) throws IOException {
        output.writeInt(this.data.length);
        for (int i = 0; i < this.data.length; i++) {
            output.writeInt(this.data[i]);
        }
    }

    @Override
    void read(DataInput input) throws IOException {
        int len = input.readInt();
        this.data = new int[len];
        for (int i = 0; i < this.data.length; i++) {
            this.data[i] = input.readInt();
        }
    }
    
    Object getAsNMS() {
    	return NBTArrayReflection.newIntArray(this.getIntArray());
    }
    
    void getFromNMS(Object nmsIntArray) {
    	if (NBTArrayReflection.isNBTIntArray(nmsIntArray)) {
    		this.data = NBTArrayReflection.getIntArray(nmsIntArray);
    	}
    }

    @Override
    public byte getTypeId() {
        return getType().getId();
    }

    @Override
    public TagType getType() {
        return TagType.TAG_Int_Array;
    }

    @Override
    public String toString() {
        String result = "[";
        int[] aint = this.data;
        int lgh = aint.length;

        for (int j = 0; j < lgh; ++j) {
            int k = aint[j];

            result += k + ",";
        }

        return result + "]";
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ Arrays.hashCode(this.data);
    }

    public int size() {
        return this.data.length;
    }

    public int[] getIntArray() {
        return this.data;
    }
    
    public int[] getAsObject() {
    	return this.getAsObject(DEFAULT_CREATOR);
    }
    
    @Override
    public <M extends Map<String, Object>, L extends List<Object>> int[] getAsObject(ObjectCreator<M, L> creator) {
    	return this.getIntArray();
    }

    @Override
    public boolean equals(Object other) {
        if (!super.equals(other)) {
            return false;
        }
        TagIntArray tb = (TagIntArray) other;
        return Arrays.equals(this.data, tb.data);
    }

    @Override
    public TagIntArray clone() {
        int[] copy = new int[this.data.length];

        System.arraycopy(this.data, 0, copy, 0, this.data.length);
        return new TagIntArray(copy);
    }
}