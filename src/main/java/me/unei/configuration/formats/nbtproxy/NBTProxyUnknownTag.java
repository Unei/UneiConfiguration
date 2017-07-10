package me.unei.configuration.formats.nbtproxy;

import me.unei.configuration.formats.nbtlib.Tag;
import me.unei.configuration.reflection.NBTBaseReflection;

@Deprecated
class NBTProxyUnknownTag extends NBTProxyTag {

    private Object nms_representation;
    private Tag unei_representation;

    NBTProxyUnknownTag(Object nms, int unused) {
        super(NBTProxyTag.Unei_Type_NMS);
        this.nms_representation = nms;
    }

    NBTProxyUnknownTag(Tag unei) {
        super(NBTProxyTag.Unei_Type_UNEI);
        this.unei_representation = unei;
    }

    @Override
    protected Object getNMSObject() {
        return this.nms_representation;
    }

    @Override
    protected Tag getUNEIObject() {
        return this.unei_representation;
    }

    @Override
    public NBTProxyUnknownTag clone() {
        switch(this.unei_type) {
            case NMS:
                Object cloned1 = NBTBaseReflection.cloneNBT(nms_representation);
                return new NBTProxyUnknownTag(cloned1, 0);
            case UNEI:
                Tag cloned2 = unei_representation.clone();
                return new NBTProxyUnknownTag(cloned2);
        }
        return null;
    }
}