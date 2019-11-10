package me.unei.configuration.reflection.nbtmirror;

import java.util.List;
import java.util.Map;

import me.unei.configuration.api.format.TagType;
import me.unei.configuration.reflection.NBTBaseReflection;

final class MirrorTagEnd extends MirrorTag {

    public MirrorTagEnd(Object original) {
        super(original, MirrorTagEnd::isNBTTagEnd);
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
    public MirrorTagEnd clone() {
        return new MirrorTagEnd(NBTBaseReflection.cloneNBT(mirroredTag));
    }
    
    private static boolean isNBTTagEnd(Object tag) {
    	if (!NBTBaseReflection.isNBTTag(tag)) {
    		return false;
    	}
    	return NBTBaseReflection.getTypeId(tag) == NBTBaseReflection.TagEndId;
    }
    
    @Override
    public me.unei.configuration.formats.nbtlib.Tag localCopy() {
    	return me.unei.configuration.formats.nbtlib.Tag.newTag(TagType.TAG_End);
    }
}