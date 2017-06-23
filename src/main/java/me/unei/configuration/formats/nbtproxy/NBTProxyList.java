package me.unei.configuration.formats.nbtproxy;

import me.unei.configuration.api.format.INBTList;
import me.unei.configuration.api.format.INBTTag;
import me.unei.configuration.formats.nbtlib.TagList;
import me.unei.configuration.reflection.NBTBaseReflection;
import me.unei.configuration.reflection.NBTListReflection;

class NBTProxyList extends NBTProxyTag implements INBTList {

    private Object nms_representation;
    private TagList unei_representation;

    NBTProxyList(Object nms, int unused) {
        super(NBTProxyTag.Unei_Type_NMS);
        this.nms_representation = nms;
    }

    NBTProxyList(TagList unei) {
        super(NBTProxyTag.Unei_Type_UNEI);
        this.unei_representation = unei;
    }

    public NBTProxyList() {
        super(NBTProxyTag.getLibType());
        switch(this.unei_type) {
            case NMS:
                this.nms_representation = NBTListReflection.newInstance();
                break;
            case UNEI:
                this.unei_representation = new TagList();
                break;
        }
    }

    @Override
    protected Object getNMSObject() {
        return this.nms_representation;
    }

    @Override
    protected TagList getUNEIObject() {
        return this.unei_representation;
    }

    public void add(INBTTag tag) {
        switch(this.unei_type) {
            case NMS:
                NBTListReflection.add(nms_representation, ((NBTProxyTag) tag).getNMSObject());
                break;
            case UNEI:
                unei_representation.add(((NBTProxyTag) tag).getUNEIObject());
                break;
        }
    }

    public void set(int idx, INBTTag tag) {
        switch(this.unei_type) {
            case NMS:
                NBTListReflection.set(nms_representation, idx, ((NBTProxyTag) tag).getNMSObject());
                break;
            case UNEI:
                unei_representation.set(idx, ((NBTProxyTag) tag).getUNEIObject());
                break;
        }
    }

    public byte getTypeInList() {
        switch(this.unei_type) {
            case NMS:
                return ((byte) (NBTListReflection.getTypeInList(nms_representation) & 0xff));
            case UNEI:
                return ((byte) (unei_representation.getTagType() & 0xff));
        }
        return 0;
    }

    public NBTProxyTag remove(int idx) {
        byte type = this.getTypeInList();
        Object res = null;
        switch(this.unei_type) {
            case NMS:
                res = NBTListReflection.remove(nms_representation, idx);
                break;
            case UNEI:
                res = unei_representation.remove(idx);
                break;
        }
        return NBTProxyTag.createTag(type, res, this.unei_type);
    }

    public NBTProxyTag get(int idx) {
        byte type = this.getTypeInList();
        Object res = null;
        switch(this.unei_type) {
            case NMS:
                res = NBTListReflection.getAsNBTBase(nms_representation, idx);
                break;
            case UNEI:
                res = unei_representation.get(idx);
                break;
        }
        return NBTProxyTag.createTag(type, res, this.unei_type);
    }

    public int size() {
        switch(this.unei_type) {
            case NMS:
                return NBTListReflection.size(nms_representation);
            case UNEI:
                return unei_representation.size();
        }
        return 0;
    }

    @Override
    public NBTProxyList clone() {
        switch(this.unei_type) {
            case NMS:
                Object cp1 = NBTBaseReflection.cloneNBT(nms_representation);
                return new NBTProxyList(cp1, 0);
            case UNEI:
                return new NBTProxyList(unei_representation.clone());
        }
        return new NBTProxyList();
    }
}