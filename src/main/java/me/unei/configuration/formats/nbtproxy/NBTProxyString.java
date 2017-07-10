package me.unei.configuration.formats.nbtproxy;

import me.unei.configuration.api.format.INBTString;
import me.unei.configuration.formats.nbtlib.TagString;
import me.unei.configuration.reflection.NBTBaseReflection;
import me.unei.configuration.reflection.NBTStringReflection;

@Deprecated
class NBTProxyString extends NBTProxyTag implements INBTString {

    private Object nms_representation;
    private TagString unei_representation;

    NBTProxyString(TagString copy) {
        super(NBTProxyTag.Unei_Type_UNEI);
        this.unei_representation = copy;
    }

    NBTProxyString(Object copy, int unused) {
        super(NBTProxyTag.Unei_Type_NMS);
        this.nms_representation = copy;
    }

    public NBTProxyString() {
        super(NBTProxyTag.getLibType());
        if (NBTProxyTag.getLibType().equals(LibType.NMS)) {
            this.nms_representation = NBTStringReflection.newInstance();
        } else {
            this.unei_representation = new TagString();
        }
    }

    public NBTProxyString(String orig) {
        super(NBTProxyTag.getLibType());
        if (NBTProxyTag.getLibType().equals(LibType.NMS)) {
            this.nms_representation = NBTStringReflection.newInstance(orig);
        } else {
            this.unei_representation = new TagString(orig);
        }
    }

    @Override
    protected Object getNMSObject() {
        return this.nms_representation;
    }

    @Override
    protected TagString getUNEIObject() {
        return this.unei_representation;
    }

    @Override
    public NBTProxyString clone() {
        switch(this.unei_type) {
            case NMS:
                return new NBTProxyString(NBTBaseReflection.cloneNBT(nms_representation), 0);
            case UNEI:
                return new NBTProxyString(unei_representation.clone());
        }
        return new NBTProxyString(getString());
    }

    public String getString() {
        switch(this.unei_type) {
            case NMS:
                return NBTStringReflection.getString(nms_representation);
            case UNEI:
                return unei_representation.getString();
        }
        return "";
    }
}