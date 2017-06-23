package me.unei.configuration.formats.nbtproxy;

import java.util.Collections;
import java.util.Set;

import me.unei.configuration.api.format.INBTCompound;
import me.unei.configuration.api.format.INBTTag;
import me.unei.configuration.formats.nbtlib.TagCompound;
import me.unei.configuration.formats.nbtlib.TagList;
import me.unei.configuration.reflection.NBTBaseReflection;
import me.unei.configuration.reflection.NBTCompoundReflection;

public class NBTProxyCompound extends NBTProxyTag implements INBTCompound {

    private Object nms_representation;
    private TagCompound unei_representation;

    NBTProxyCompound(Object orig, int unused) {
        super(NBTProxyTag.Unei_Type_NMS);
        this.nms_representation = orig;
    }

    NBTProxyCompound(TagCompound orig) {
        super(NBTProxyTag.Unei_Type_UNEI);
        this.unei_representation = orig;
    }

    public NBTProxyCompound() {
        super(NBTProxyTag.getLibType());
        if (NBTProxyTag.getLibType().equals(LibType.NMS)) {
            this.nms_representation = NBTCompoundReflection.newInstance();
        } else {
            this.unei_representation = new TagCompound();
        }
    }

    @Override
    protected Object getNMSObject() {
        return this.nms_representation;
    }

    @Override
    protected TagCompound getUNEIObject() {
        return this.unei_representation;
    }

    public Set<String> keySet() {
        switch(this.unei_type) {
            case NMS:
                return NBTCompoundReflection.keySet(nms_representation);
            case UNEI:
                return unei_representation.keySet();
        }
        return Collections.<String>emptySet();
    }

    public int size() {
        switch(this.unei_type) {
            case NMS:
                return NBTCompoundReflection.size(nms_representation);
            case UNEI:
                return unei_representation.size();
        }
        return 0;
    }

    public void set(String key, INBTTag elem) {
        switch(this.unei_type) {
            case NMS:
                NBTCompoundReflection.set(nms_representation, key, ((NBTProxyTag) elem).getNMSObject());
                break;
            case UNEI:
                unei_representation.set(key, ((NBTProxyTag) elem).getUNEIObject());
                break;
        }
    }

    public void setByte(String key, byte value) {
        switch(this.unei_type) {
            case NMS:
                NBTCompoundReflection.setByte(nms_representation, key, value);
                break;
            case UNEI:
                unei_representation.setByte(key, value);
                break;
        }
    }

    public void setShort(String key, short value) {
        switch(this.unei_type) {
            case NMS:
                NBTCompoundReflection.setShort(nms_representation, key, value);
                break;
            case UNEI:
                unei_representation.setShort(key, value);
                break;
        }
    }

    public void setInt(String key, int value) {
        switch(this.unei_type) {
            case NMS:
                NBTCompoundReflection.setInt(nms_representation, key, value);
                break;
            case UNEI:
                unei_representation.setInt(key, value);
                break;
        }
    }

    public void setLong(String key, long value) {
        switch(this.unei_type) {
            case NMS:
                NBTCompoundReflection.setLong(nms_representation, key, value);
                break;
            case UNEI:
                unei_representation.setLong(key, value);
                break;
        }
    }

    public void setFloat(String key, float value) {
        switch(this.unei_type) {
            case NMS:
                NBTCompoundReflection.setFloat(nms_representation, key, value);
                break;
            case UNEI:
                unei_representation.setFloat(key, value);
                break;
        }
    }

    public void setDouble(String key, double value) {
        switch(this.unei_type) {
            case NMS:
                NBTCompoundReflection.setDouble(nms_representation, key, value);
                break;
            case UNEI:
                unei_representation.setDouble(key, value);
                break;
        }
    }

    public void setString(String key, String value) {
        switch(this.unei_type) {
            case NMS:
                NBTCompoundReflection.setString(nms_representation, key, value);
                break;
            case UNEI:
                unei_representation.setString(key, value);
                break;
        }
    }

    public void setByteArray(String key, byte[] value) {
        switch(this.unei_type) {
            case NMS:
                NBTCompoundReflection.setByteArray(nms_representation, key, value);
                break;
            case UNEI:
                unei_representation.setByteArray(key, value);
                break;
        }
    }

    public void setIntArray(String key, int[] value) {
        switch(this.unei_type) {
            case NMS:
                NBTCompoundReflection.setIntArray(nms_representation, key, value);
                break;
            case UNEI:
                unei_representation.setIntArray(key, value);
                break;
        }
    }

    public void setLongArray(String key, long[] value) {
        switch(this.unei_type) {
            case NMS:
                NBTCompoundReflection.setLongArray(nms_representation, key, value);
                break;
            case UNEI:
                unei_representation.setLongArray(key, value);
                break;
        }
    }

    public void setBoolean(String key, boolean value) {
        switch(this.unei_type) {
            case NMS:
                NBTCompoundReflection.setBoolean(nms_representation, key, value);
                break;
            case UNEI:
                unei_representation.setBoolean(key, value);
                break;
        }
    }

    public NBTProxyTag get(String key) {
        Object rep;
        byte type = this.getTypeOf(key);
        switch(this.unei_type) {
            case NMS:
                rep = NBTCompoundReflection.get(nms_representation, key);
                break;
            case UNEI:
                rep = unei_representation.get(key);
                break;

            default:
                rep = null;
        }
        return NBTProxyTag.createTag(type, rep, this.unei_type);
    }

    public byte getTypeOf(String key) {
        switch(this.unei_type) {
            case NMS:
                return NBTCompoundReflection.getTypeOf(nms_representation, key);
            case UNEI:
                return unei_representation.getTypeOf(key);
        }
        return -1;
    }

    public boolean hasKey(String key) {
        switch(this.unei_type) {
            case NMS:
                return NBTCompoundReflection.hasKey(nms_representation, key);
            case UNEI:
                return unei_representation.hasKey(key);
        }
        return false;
    }

    public boolean hasKeyOfType(String key, int type) {
        switch(this.unei_type) {
            case NMS:
                return NBTCompoundReflection.hasKeyOfType(nms_representation, key, type);
            case UNEI:
                return unei_representation.hasKeyOfType(key, (byte) (type & 0xff));
        }
        return false;
    }

    public byte getByte(String key) {
        switch(this.unei_type) {
            case NMS:
                return NBTCompoundReflection.getByte(nms_representation, key);
            case UNEI:
                return unei_representation.getByte(key);
        }
        return -1;
    }

    public short getShort(String key) {
        switch(this.unei_type) {
            case NMS:
                return NBTCompoundReflection.getShort(nms_representation, key);
            case UNEI:
                return unei_representation.getShort(key);
        }
        return -1;
    }

    public int getInt(String key) {
        switch(this.unei_type) {
            case NMS:
                return NBTCompoundReflection.getInt(nms_representation, key);
            case UNEI:
                return unei_representation.getInt(key);
        }
        return -1;
    }

    public long getLong(String key) {
        switch(this.unei_type) {
            case NMS:
                return NBTCompoundReflection.getLong(nms_representation, key);
            case UNEI:
                return unei_representation.getLong(key);
        }
        return -1;
    }

    public float getFloat(String key) {
        switch(this.unei_type) {
            case NMS:
                return NBTCompoundReflection.getFloat(nms_representation, key);
            case UNEI:
                return unei_representation.getFloat(key);
        }
        return -1;
    }

    public double getDouble(String key) {
        switch(this.unei_type) {
            case NMS:
                return NBTCompoundReflection.getDouble(nms_representation, key);
            case UNEI:
                return unei_representation.getDouble(key);
        }
        return -1;
    }

    public String getString(String key) {
        switch(this.unei_type) {
            case NMS:
                return NBTCompoundReflection.getString(nms_representation, key);
            case UNEI:
                return unei_representation.getString(key);
        }
        return "";
    }

    public byte[] getByteArray(String key) {
        switch(this.unei_type) {
            case NMS:
                return NBTCompoundReflection.getByteArray(nms_representation, key);
            case UNEI:
                return unei_representation.getByteArray(key);
        }
        return new byte[0];
    }

    public int[] getIntArray(String key) {
        switch(this.unei_type) {
            case NMS:
                return NBTCompoundReflection.getIntArray(nms_representation, key);
            case UNEI:
                return unei_representation.getIntArray(key);
        }
        return new int[0];
    }

    public long[] getLongArray(String key) {
        switch(this.unei_type) {
            case NMS:
                return NBTCompoundReflection.getLongArray(nms_representation, key);
            case UNEI:
                return unei_representation.getLongArray(key);
        }
        return new long[0];
    }

    public NBTProxyCompound getCompound(String key) {
        switch(this.unei_type) {
            case NMS:
                Object got = NBTCompoundReflection.getCompound(nms_representation, key);
                return new NBTProxyCompound(got, 0);
            case UNEI:
                TagCompound got2 = unei_representation.getCompound(key);
                return new NBTProxyCompound(got2);
        }
        return new NBTProxyCompound();
    }

    public NBTProxyList getList(String key, byte type) {
        switch(this.unei_type) {
            case NMS:
                Object got = NBTCompoundReflection.getList(nms_representation, key, type);
                return new NBTProxyList(got, 0);
            case UNEI:
                TagList got2 = unei_representation.getList(key, type);
                return new NBTProxyList(got2);
        }
        return new NBTProxyList();
    }

    public boolean getBoolean(String key) {
        switch(this.unei_type) {
            case NMS:
                return NBTCompoundReflection.getBoolean(nms_representation, key);
            case UNEI:
                return unei_representation.getBoolean(key);
        }
        return false;
    }

    public void remove(String key) {
        switch(this.unei_type) {
            case NMS:
                NBTCompoundReflection.remove(nms_representation, key);
                break;
            case UNEI:
                unei_representation.remove(key);
                break;
        }
    }

    @Deprecated
    public boolean mergeIfPossible(NBTProxyCompound other) {
        if (this.unei_type.equals(NBTProxyTag.Unei_Type_UNEI)) {
            unei_representation.merge(other.getUNEIObject());
            return true;
        }
        return false;
    }

    @Override
    public NBTProxyCompound clone() {
        switch(this.unei_type) {
            case NMS:
                Object copy = NBTBaseReflection.cloneNBT(nms_representation);
                return new NBTProxyCompound(copy, 0);
            case UNEI:
                TagCompound cloned = unei_representation.clone();
                return new NBTProxyCompound(cloned);
        }
        return new NBTProxyCompound();
    }
}