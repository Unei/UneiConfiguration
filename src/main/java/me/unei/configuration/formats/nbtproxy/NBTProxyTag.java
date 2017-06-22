package me.unei.configuration.formats.nbtproxy;

import me.unei.configuration.api.format.INBTTag;
import me.unei.configuration.formats.nbtlib.Tag;
import me.unei.configuration.formats.nbtlib.TagCompound;
import me.unei.configuration.formats.nbtlib.TagList;
import me.unei.configuration.formats.nbtlib.TagString;
import me.unei.configuration.reflection.NBTBaseReflection;
import me.unei.configuration.reflection.NMSReflection;

abstract class NBTProxyTag implements INBTTag {

    public static final LibType Unei_Type_NMS = LibType.NMS;
    public static final LibType Unei_Type_UNEI = LibType.UNEI;

    protected final LibType unei_type;

    protected NBTProxyTag(LibType type) {
        this.unei_type = type;
    }

    protected static LibType getLibType() {
        if (NMSReflection.canUseNMS()/* && !UneiConfiguration.getInstance().shouldForceUneiLib()*/) {
            return LibType.NMS;
        }
        return LibType.UNEI;
    }

    protected static NBTProxyTag createTag(byte type, Object obj, LibType uneitype) {
        switch(uneitype) {
            case NMS: {
                switch(type) {
                    case NBTBaseReflection.TagStringId:
                        return new NBTProxyString(obj, 0);
                    case NBTBaseReflection.TagCompoundId:
                        return new NBTProxyCompound(obj, 0);
                    case NBTBaseReflection.TagListId:
                        return new NBTProxyList(obj, 0);

                    default:
                        return new NBTProxyUnknownTag(obj, 0);
                }
            }
            case UNEI: {
                switch(type) {
                    case Tag.TAG_String:
                        return new NBTProxyString(((TagString) obj));
                    case Tag.TAG_Compound:
                        return new NBTProxyCompound((TagCompound) obj);
                    case Tag.TAG_List:
                        return new NBTProxyList((TagList) obj);

                    default:
                        return new NBTProxyUnknownTag((Tag) obj);
                }
            }
        }
        return null;
    }

    protected abstract Object getNMSObject();
    protected abstract Tag getUNEIObject();
    
    public LibType getUneiType()
    {
    	return this.unei_type;
    }

    public byte getTypeId() {
        switch(this.unei_type) {
            case NMS:
                return NBTBaseReflection.getTypeId(this.getNMSObject());
            case UNEI:
                return this.getUNEIObject().getTypeId();
        }

        return -1;
    }

    public boolean isEmpty() {
        switch(this.unei_type) {
            case NMS:
                return NBTBaseReflection.isEmpty(this.getNMSObject());
            case UNEI:
                return this.getUNEIObject().isEmpty();
        }

        return true;
    }

    @Override
    public abstract NBTProxyTag clone();

    @Override
    public String toString() {
        switch(this.unei_type) {
            case NMS:
                return getNMSObject().toString();
            case UNEI:
                return getUNEIObject().toString();
        }

        return "";
    }

    @Override
    public int hashCode() {
        switch(this.unei_type) {
            case NMS:
                return getNMSObject().hashCode();
            case UNEI:
                return getUNEIObject().hashCode();
        }

        return 0;
    }

    @Override
    public boolean equals(Object other) {
        switch(this.unei_type) {
            case NMS:
                return getNMSObject().equals(other);
            case UNEI:
                return getUNEIObject().equals(other);
        }

        return false;
    }

    protected static enum LibType {
        NMS, UNEI;
    }
}