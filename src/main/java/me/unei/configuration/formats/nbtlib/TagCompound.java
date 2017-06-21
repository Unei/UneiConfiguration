package me.unei.configuration.formats.nbtlib;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class TagCompound extends Tag {

    private Map<String, Tag> tags = new HashMap<String, Tag>();

    public TagCompound() {
    }

    @Override
    void write(DataOutput output) throws IOException {
        Iterator<String> it = this.tags.keySet().iterator();

        while (it.hasNext()) {
            String key = it.next();
            Tag tag = this.tags.get(key);

            TagCompound.writeEntry(key, tag, output);
        }

        output.writeByte(Tag.TAG_End);
    }

    @Override
    void read(DataInput input) throws IOException {
        this.tags.clear();

        byte type;

        while ((type = input.readByte()) != Tag.TAG_End) {
            String key = input.readUTF();
            Tag tag = Tag.newTag(type);
            tag.read(input);

            this.tags.put(key, tag);
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
        return Tag.TAG_Compound;
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
        Tag t = this.tags.get(key);
        return (t != null? t.getTypeId() : Tag.TAG_End);
    }

    public boolean hasKey(String key) {
        return this.tags.containsKey(key);
    }

    public boolean hasKeyOfType(String key, byte type) {
        byte other = this.getTypeOf(key);
        if (other == type) {
            return true;
        }
        if (type == Tag.Number_TAG) {
            if (other >= Tag.TAG_Byte && other <= Tag.TAG_Double) {
                return true;
            }
        }
        return false;
    }

    public byte getByte(String key) {
        try {
            return !this.hasKeyOfType(key, Tag.TAG_Byte)? 0 : ((TagByte) this.tags.get(key)).getValue();
        } catch (ClassCastException exception) {
            return 0;
        }
    }

    public short getShort(String key) {
        try {
            return !this.hasKeyOfType(key, Tag.TAG_Short)? 0 : ((TagShort) this.tags.get(key)).getValue();
        } catch (ClassCastException exception) {
            return 0;
        }
    }

    public int getInt(String key) {
        try {
            return !this.hasKeyOfType(key, Tag.TAG_Int)? 0 : ((TagInt) this.tags.get(key)).getValue();
        } catch (ClassCastException exception) {
            return 0;
        }
    }

    public long getLong(String key) {
        try {
            return !this.hasKeyOfType(key, Tag.TAG_Long)? 0L : ((TagLong) this.tags.get(key)).getValue();
        } catch (ClassCastException exception) {
            return 0L;
        }
    }

    public float getFloat(String key) {
        try {
            return !this.hasKeyOfType(key, Tag.TAG_Float)? 0.0F : ((TagFloat) this.tags.get(key)).getValue();
        } catch (ClassCastException exception) {
            return 0.0F;
        }
    }

    public double getDouble(String key) {
        try {
            return !this.hasKeyOfType(key, Tag.TAG_Double)? 0.0D : ((TagDouble) this.tags.get(key)).getValue();
        } catch (ClassCastException exception) {
            return 0.0D;
        }
    }

    public String getString(String key) {
        try {
            return !this.hasKeyOfType(key, Tag.TAG_String)? "" : this.tags.get(key).getString();
        } catch (ClassCastException exception) {
            return "";
        }
    }

    public byte[] getByteArray(String key) {
        try {
            return !this.hasKeyOfType(key, Tag.TAG_Byte_Array)? new byte[0] : ((TagByteArray) this.tags.get(key)).getByteArray();
        } catch (ClassCastException exception) {
            throw new RuntimeException("Type (" + Tag.getTagName(this.tags.get(key).getTypeId()) + ") diffenrent as expected (" + Tag.getTagName(Tag.TAG_Byte_Array) + ")", exception);
        }
    }

    public int[] getIntArray(String key) {
        try {
            return !this.hasKeyOfType(key, Tag.TAG_Int_Array)? new int[0] : ((TagIntArray) this.tags.get(key)).getIntArray();
        } catch (ClassCastException exception) {
            throw new RuntimeException("Type (" + Tag.getTagName(this.tags.get(key).getTypeId()) + ") diffenrent as expected (" + Tag.getTagName(Tag.TAG_Int_Array) + ")", exception);
        }
    }

    public long[] getLongArray(String key) {
        try {
            return !this.hasKeyOfType(key, Tag.TAG_Long_Array)? new long[0] : ((TagLongArray) this.tags.get(key)).getLongArray();
        } catch (ClassCastException exception) {
            throw new RuntimeException("Type (" + Tag.getTagName(this.tags.get(key).getTypeId()) + ") diffenrent as expected (" + Tag.getTagName(Tag.TAG_Long_Array) + ")", exception);
        }
    }

    public TagCompound getCompound(String key) {
        try {
            return !this.hasKeyOfType(key, Tag.TAG_Compound)? new TagCompound() : (TagCompound) this.tags.get(key);
        } catch (ClassCastException exception) {
            throw new RuntimeException("Type (" + Tag.getTagName(this.tags.get(key).getTypeId()) + ") diffenrent as expected (" + Tag.getTagName(Tag.TAG_Compound) + ")", exception);
        }
    }

    public TagList getList(String key, int type) {
        try {
            if (!this.hasKeyOfType(key, Tag.TAG_List)) {
                return new TagList();
            } else {
                TagList taglist = (TagList) this.tags.get(key);

                return taglist.size() > 0 && taglist.getTagType() != type? new TagList() : taglist;
            }
        } catch (ClassCastException exception) {
            throw new RuntimeException("Type (" + Tag.getTagName(this.tags.get(key).getTypeId()) + ") diffenrent as expected (" + Tag.getTagName(Tag.TAG_List) + ")", exception);
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
            builder.append(entry.getKey()).append(':').append(entry.getValue());
        }

        return builder.append('}').toString();
    }

    @Override
    public boolean isEmpty() {
        return this.tags.isEmpty();
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

            if (base.getTypeId() == 10) {
                if (this.hasKeyOfType(key, Tag.TAG_Compound)) {
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

    private static void writeEntry(String key, Tag base, DataOutput output) throws IOException {
        output.writeByte(base.getTypeId());
        if (base.getTypeId() != Tag.TAG_End) {
            output.writeUTF(key);
            base.write(output);
        }
    }
}