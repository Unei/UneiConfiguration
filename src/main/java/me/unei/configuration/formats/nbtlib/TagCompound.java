package me.unei.configuration.formats.nbtlib;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import me.unei.configuration.ArrayTools;
import me.unei.configuration.SerializerHelper;
import me.unei.configuration.api.exceptions.UnexpectedClassException;
import me.unei.configuration.api.format.INBTCompound;
import me.unei.configuration.api.format.INBTList;
import me.unei.configuration.api.format.INBTTag;
import me.unei.configuration.api.format.TagType;
import me.unei.configuration.reflection.NBTCompoundReflection;

public final class TagCompound extends Tag implements INBTCompound {

    private Map<String, Tag> tags = new HashMap<String, Tag>();
    private static final Pattern name_conventions = Pattern.compile("[A-Za-z0-9._+-]+");

    public TagCompound() {}

    @Override
    void write(DataOutput output) throws IOException {
        Iterator<String> it = this.tags.keySet().iterator();

        while (it.hasNext()) {
            String key = it.next();
            Tag tag = this.tags.get(key);

            TagCompound.writeEntry(key, tag, output);
        }

        output.writeByte(TagType.TAG_End.getId());
    }

    @Override
    void read(DataInput input) throws IOException {
        this.tags.clear();

        byte type;

        while ((type = input.readByte()) != TagType.TAG_End.getId()) {
            String key = input.readUTF();
            Tag tag = Tag.newTag(type);
            tag.read(input);

            this.tags.put(key, tag);
        }
    }
    
	public void loadMap(Map<?, ?> datas) throws UnexpectedClassException {
    	this.tags.clear();
    	for (Entry<?, ?> entry : datas.entrySet()) {
    		String key = entry.getKey().toString();
    		Object value = entry.getValue();
    		if (value == null) {
    			continue;
    		}
    		if (value instanceof CharSequence) {
    			this.setString(key, value.toString());
    		} else if (value instanceof Tag) {
    			this.set(key, (Tag)value);
    		} else if (value instanceof Map) {
    			TagCompound subTag = new TagCompound();
    			subTag.loadMap((Map<?, ?>)value);
    			this.set(key, subTag);
    		} else if (value instanceof Iterable) {
    			Class<?> subType = ArrayTools.getIterableParam((Iterable<?>) value);
    			if (subType == null) {
    				TagList subTag = new TagList();
    				subTag.loadList((Iterable<?>)value);
    				this.set(key, subTag);
    			} else if (subType.equals(byte.class) || subType.equals(Byte.class)) {
    				this.setByteArray(key, ArrayTools.toBytes(value));
    			} else if (subType.equals(int.class) || subType.equals(Integer.class)) {
    				this.setIntArray(key, ArrayTools.toInts(value));
    			} else if (subType.equals(long.class) || subType.equals(Long.class)) {
    				this.setLongArray(key, ArrayTools.toLongs(value));
    			} else {
    				TagList subTag = new TagList();
    				subTag.loadList((Iterable<?>)value);
    				this.set(key, subTag);
    			}
    		} else if (value instanceof Integer) {
    			this.setInt(key, (Integer)value);
    		} else if (value instanceof Byte) {
    			this.setByte(key, (Byte)value);
    		} else if (value instanceof Double) {
    			this.setDouble(key, (Double)value);
    		} else if (value instanceof Short) {
    			this.setShort(key, (Short)value);
    		} else if (value instanceof Long) {
    			this.setLong(key, (Long)value);
    		} else if (value instanceof Float) {
    			this.setFloat(key, (Float)value);
    		} else if (value instanceof Boolean) {
    			this.setBoolean(key, (Boolean)value);
    		} else if (value instanceof UUID) {
    			this.setUUID(key, (UUID)value);
    		} else if (value instanceof int[]) {
    			this.setIntArray(key, (int[])value);
    		} else if (value instanceof Integer[]) {
    			this.setIntArray(key, ArrayTools.toPrimitive((Integer[])value));
    		} else if (value instanceof byte[]) {
    			this.setByteArray(key, (byte[])value);
    		} else if (value instanceof Byte[]) {
    			this.setByteArray(key, ArrayTools.toPrimitive((Byte[])value));
    		} else if (value instanceof long[]) {
    			this.setLongArray(key, (long[])value);
    		} else if (value instanceof Long[]) {
    			this.setLongArray(key, ArrayTools.toPrimitive((Long[])value));
    		} else if (value instanceof Serializable) {
    			this.setByteArray(key + "Object", SerializerHelper.serialize(value));
    		} else {
    			throw new UnexpectedClassException(value.getClass());
    		}
    	}
    }
    
    @Override
	public Map<String, Object> getAsObject() {
    	return getAsObject(DEFAULT_CREATOR);
    }
    
    public <M extends Map<String, Object>, L extends List<Object>> M getAsObject(ObjectCreator<M, L> creator) {
    	if (creator == null) {
    		return null;
    	}
    	M result = creator.newMap();
    	for (Entry<String, Tag> entry : this.tags.entrySet()) {
    		String key = entry.getKey();
    		if (key.endsWith("Most") || key.endsWith("Least")) {
    			if (key.endsWith("Most")) {
    				key = key.substring(0, key.length() - "Most".length());
    			} else if (key.endsWith("Least")) {
    				key = key.substring(0, key.length() - "Least".length());
    			}
    			if (this.isUUID(key)) {
    				if (!result.containsKey(key)) {
    					result.put(key, this.getUUID(key));
    				}
    			} else {
    				result.put(entry.getKey(), entry.getValue().getAsObject(creator));
    			}
    		} else if (key.endsWith("Object") && this.hasKeyOfType(key, TagType.TAG_Byte_Array)) {
    			key = key.substring(0, key.length() - "Object".length());
    			Object r = SerializerHelper.deserialize(this.getByteArray(entry.getKey()));
    			if (r != null) {
    				result.put(key, r);
    			} else {
    				result.put(entry.getKey(), entry.getValue());
    			}
    		} else {
    			result.put(entry.getKey(), entry.getValue().getAsObject(creator));
    		}
    	}
    	return result;
    }
    
    @Override
	public Object getAsNMS() {
    	Object NMSCompound = NBTCompoundReflection.newInstance();
    	if (NMSCompound == null) {
    		return null;
    	}
    	for (Entry<String, Tag> entry : this.tags.entrySet()) {
    		NBTCompoundReflection.set(NMSCompound, entry.getKey(), entry.getValue().getAsNMS());
    	}
    	return NMSCompound;
    }
    
    @Override
	public void getFromNMS(Object nmsCompound) {
    	if (!NBTCompoundReflection.isNBTCompound(nmsCompound)) {
    		return;
    	}
    	
    	this.tags.clear();
    	Set<String> keys = NBTCompoundReflection.keySet(nmsCompound);
    	for (String key : keys) {
    		byte typeID = NBTCompoundReflection.getTypeOf(nmsCompound, key);
    		Tag current = Tag.newTag(typeID);
    		current.getFromNMS(NBTCompoundReflection.get(nmsCompound, key));
    		this.set(key, current);
    	}
    }

    public Set<String> keySet() {
        return this.tags.keySet();
    }

    public int size() {
        return this.tags.size();
    }

    @Override
    public byte getTypeId() {
        return getType().getId();
    }
    
    @Override
    public TagType getType() {
    	return TagType.TAG_Compound;
    }

    public void set(String key, INBTTag tag) {
        this.set(key, (Tag)tag);
    }
    
    public void set(String key, Tag tag) {
        this.tags.put(key, tag);
    }

    public void setByte(String key, byte value) {
        this.tags.put(key, new TagByte(value));
    }

    public void setShort(String key, short value) {
        this.tags.put(key, new TagShort(value));
    }

    public void setInt(String key, int value) {
        this.tags.put(key, new TagInt(value));
    }

    public void setLong(String key, long value) {
        this.tags.put(key, new TagLong(value));
    }
    
    public void setUUID(String key, UUID uuid) {
    	this.setLong(key + "Most", uuid.getMostSignificantBits());
    	this.setLong(key + "Least", uuid.getLeastSignificantBits());
    }
    
    public UUID getUUID(String key) {
    	return new UUID(this.getLong(key + "Most"), this.getLong(key + "Least"));
    }
    
    public boolean isUUID(String key) {
    	return (this.hasKeyOfType(key + "Most", TagType.Number_TAG) && this.hasKeyOfType(key + "Least", TagType.Number_TAG));
    }

    public void setFloat(String key, float value) {
        this.tags.put(key, new TagFloat(value));
    }

    public void setDouble(String key, double value) {
        this.tags.put(key, new TagDouble(value));
    }

    public void setString(String key, String value) {
        this.tags.put(key, new TagString(value));
    }

    public void setByteArray(String key, byte[] values) {
        this.tags.put(key, new TagByteArray(values));
    }

    public void setIntArray(String key, int[] values) {
        this.tags.put(key, new TagIntArray(values));
    }

    public void setLongArray(String key, long[] values) {
        this.tags.put(key, new TagLongArray(values));
    }

    public void setBoolean(String key, boolean value) {
        this.setByte(key, (byte) (value? 1 : 0));
    }

    public Tag get(String key) {
        return this.tags.get(key);
    }

    public byte getTypeOf(String key) {
        return getTypeOfTag(key).getId();
    }
    
    @Override
    public TagType getTypeOfTag(String key) {
        Tag t = this.tags.get(key);
        return (t != null? t.getType() : TagType.TAG_End);
    }

    public boolean hasKey(String key) {
        return this.tags.containsKey(key);
    }

    public boolean hasKeyOfType(String key, byte type) {
        byte other = this.getTypeOf(key);
        if (other == type) {
            return true;
        }
        if (type == TagType.Number_TAG.getId()) {
            if (other >= TagType.TAG_Byte.getId() && other <= TagType.TAG_Double.getId()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasKeyOfType(String key, TagType type) {
    	TagType other = this.getTypeOfTag(key);
        if (other == type) {
            return true;
        }
        if (type == TagType.Number_TAG) {
            if (other.isNumberTag()) {
                return true;
            }
        }
        return false;
    }

    public byte getByte(String key) {
        try {
            return !this.hasKeyOfType(key, TagType.TAG_Byte)? 0 : ((TagByte) this.tags.get(key)).getValue();
        } catch (ClassCastException exception) {
            return 0;
        }
    }

    public short getShort(String key) {
        try {
            return !this.hasKeyOfType(key, TagType.TAG_Short)? 0 : ((TagShort) this.tags.get(key)).getValue();
        } catch (ClassCastException exception) {
            return 0;
        }
    }

    public int getInt(String key) {
        try {
            return !this.hasKeyOfType(key, TagType.TAG_Int)? 0 : ((TagInt) this.tags.get(key)).getValue();
        } catch (ClassCastException exception) {
            return 0;
        }
    }

    public long getLong(String key) {
        try {
            return !this.hasKeyOfType(key, TagType.TAG_Long)? 0L : ((TagLong) this.tags.get(key)).getValue();
        } catch (ClassCastException exception) {
            return 0L;
        }
    }

    public float getFloat(String key) {
        try {
            return !this.hasKeyOfType(key, TagType.TAG_Float)? 0.0F : ((TagFloat) this.tags.get(key)).getValue();
        } catch (ClassCastException exception) {
            return 0.0F;
        }
    }

    public double getDouble(String key) {
        try {
            return !this.hasKeyOfType(key, TagType.TAG_Double)? 0.0D : ((TagDouble) this.tags.get(key)).getValue();
        } catch (ClassCastException exception) {
            return 0.0D;
        }
    }

    public String getString(String key) {
        try {
            return !this.hasKeyOfType(key, TagType.TAG_String)? "" : this.tags.get(key).getString();
        } catch (ClassCastException exception) {
            return "";
        }
    }

    public byte[] getByteArray(String key) {
        try {
            return !this.hasKeyOfType(key, TagType.TAG_Byte_Array)? new byte[0] : ((TagByteArray) this.tags.get(key)).getByteArray();
        } catch (ClassCastException exception) {
            throw new RuntimeException("Type (" + Tag.getTagName(this.tags.get(key).getType()) + ") different than expected (" + Tag.getTagName(TagType.TAG_Byte_Array) + ")", exception);
        }
    }

    public int[] getIntArray(String key) {
        try {
            return !this.hasKeyOfType(key, TagType.TAG_Int_Array)? new int[0] : ((TagIntArray) this.tags.get(key)).getIntArray();
        } catch (ClassCastException exception) {
            throw new RuntimeException("Type (" + Tag.getTagName(this.tags.get(key).getType()) + ") different than expected (" + Tag.getTagName(TagType.TAG_Int_Array) + ")", exception);
        }
    }

    public long[] getLongArray(String key) {
        try {
            return !this.hasKeyOfType(key, TagType.TAG_Long_Array)? new long[0] : ((TagLongArray) this.tags.get(key)).getLongArray();
        } catch (ClassCastException exception) {
            throw new RuntimeException("Type (" + Tag.getTagName(this.tags.get(key).getType()) + ") different than expected (" + Tag.getTagName(TagType.TAG_Long_Array) + ")", exception);
        }
    }

    public TagCompound getCompound(String key) {
        try {
            return !this.hasKeyOfType(key, TagType.TAG_Compound)? new TagCompound() : (TagCompound) this.tags.get(key);
        } catch (ClassCastException exception) {
            throw new RuntimeException("Type (" + Tag.getTagName(this.tags.get(key).getType()) + ") different than expected (" + Tag.getTagName(TagType.TAG_Compound) + ")", exception);
        }
    }

    public TagList getList(String key, byte type) {
        try {
            if (!this.hasKeyOfType(key, TagType.TAG_List)) {
                return new TagList();
            } else {
                TagList taglist = (TagList) this.tags.get(key);

                return taglist.size() > 0 && taglist.getTagType().getId() != type? new TagList() : taglist;
            }
        } catch (ClassCastException exception) {
            throw new RuntimeException("Type (" + Tag.getTagName(this.tags.get(key).getType()) + ") diffenrent as expected (" + Tag.getTagName(TagType.TAG_List) + ")", exception);
        }
    }
    
    @Override
    public INBTList getList(String key, TagType type) {
        try {
            if (!this.hasKeyOfType(key, TagType.TAG_List)) {
                return new TagList();
            } else {
                TagList taglist = (TagList) this.tags.get(key);

                return taglist.size() > 0 && taglist.getTagType() != type? new TagList() : taglist;
            }
        } catch (ClassCastException exception) {
            throw new RuntimeException("Type (" + Tag.getTagName(this.tags.get(key).getType()) + ") diffenrent as expected (" + Tag.getTagName(TagType.TAG_List) + ")", exception);
        }
    }

    public boolean getBoolean(String key) {
        return this.getByte(key) != 0;
    }

    public void remove(String key) {
        this.tags.remove(key);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("{");

        Entry<String, Tag> entry;

        for (Iterator<Entry<String, Tag>> it = this.tags.entrySet().iterator(); it.hasNext(); ) {
            entry = it.next();
            if (builder.length() != 1) {
                builder.append(',');
            }
            builder.append(TagCompound.checkName(entry.getKey())).append(':').append(entry.getValue());
        }

        return builder.append('}').toString();
    }

    @Override
    public boolean isEmpty() {
        return this.tags.isEmpty();
    }
    
    public void clear() {
    	this.tags.clear();
    }

    @Override
    public TagCompound clone() {
        TagCompound comp = new TagCompound();
        Iterator<String> it = this.tags.keySet().iterator();

        while (it.hasNext()) {
            String key = it.next();
            comp.set(key, this.tags.get(key).clone());
        }

        return comp;
    }

    @Override
    public boolean equals(Object object) {
        if (super.equals(object)) {
            TagCompound comp = (TagCompound) object;

            return this.tags.entrySet().equals(comp.tags.entrySet());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ this.tags.hashCode();
    }

    public void merge(TagCompound compound) {
        Iterator<String> it = compound.tags.keySet().iterator();

        while (it.hasNext()) {
            String key = it.next();
            Tag base = compound.tags.get(key);

            if (base.getType() == TagType.TAG_Compound) {
                if (this.hasKeyOfType(key, TagType.TAG_Compound)) {
                    TagCompound tagcompound = (TagCompound) this.get(key);

                    tagcompound.merge((TagCompound) base);
                } else {
                    this.set(key, base.clone());
                }
            } else {
                this.set(key, base.clone());
            }
        }
    }

    private static String checkName(String name) {
        if (TagCompound.name_conventions.matcher(name).matches()) return name;
        return TagString.toStr(name);
    }

    private static void writeEntry(String key, Tag base, DataOutput output) throws IOException {
        output.writeByte(base.getTypeId());
        if (base.getType() != TagType.TAG_End) {
            output.writeUTF(key);
            base.write(output);
        }
    }
}