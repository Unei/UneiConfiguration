package me.unei.configuration.reflection.nbtmirror;

import java.util.List;
import java.util.Map;

import me.unei.configuration.reflection.NBTBaseReflection;
import me.unei.configuration.reflection.NBTStringReflection;

public final class MirrorTagString extends MirrorTag {

    public MirrorTagString(Object original) {
    	super(original, NBTStringReflection::isNBTString);
    }

    @Override
    public String getString() {
        return NBTStringReflection.getString(mirroredTag);
    }
    
    @Override
    public String getAsObject() {
    	return this.getAsObject(DEFAULT_CREATOR);
    }
    
    @Override
    public <M extends Map<String, Object>, L extends List<Object>> String getAsObject(ObjectCreator<M, L> creator) {
    	return this.getString();
    }

	@Override
	public MirrorTagString clone() {
		return new MirrorTagString(NBTBaseReflection.cloneNBT(mirroredTag));
	}
    
    @Override
    public me.unei.configuration.formats.nbtlib.TagString localCopy() {
    	return new me.unei.configuration.formats.nbtlib.TagString(getString());
    }
}