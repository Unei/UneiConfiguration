package me.unei.configuration.reflection.nbtmirror;

import java.util.List;
import java.util.Map;

import me.unei.configuration.reflection.NBTBaseReflection;
import me.unei.configuration.reflection.NBTNumberReflection;

public final class MirrorTagShort extends MirrorTag {

    public MirrorTagShort(Object original) {
    	super(original, NBTNumberReflection::isNBTShort);
    }

    public short getValue() {
        return NBTNumberReflection.getShort(mirroredTag);
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
    public MirrorTagShort clone() {
        return new MirrorTagShort(NBTBaseReflection.cloneNBT(mirroredTag));
    }
    
    @Override
    public me.unei.configuration.formats.nbtlib.TagShort localCopy() {
    	return new me.unei.configuration.formats.nbtlib.TagShort(getValue());
    }
}