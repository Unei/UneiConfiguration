package me.unei.configuration.formats.nbtlib;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;

import me.unei.configuration.api.format.INBTList;
import me.unei.configuration.api.format.INBTTag;
import me.unei.configuration.reflection.NBTListReflection;

public final class TagList extends Tag implements INBTList{

    private List<Tag> list = new ArrayList<Tag>();
    private byte type = 0;

    public TagList() {
    }

    @Override
    void write(DataOutput output) throws IOException {
        if (this.list.isEmpty()) {
            this.type = 0;
        } else {
            this.type = this.list.get(0).getTypeId();
        }

        output.writeByte(this.type);
        output.writeInt(this.list.size());
        for (int i = 0; i < this.list.size(); i++) {
            this.list.get(i).write(output);
        }
    }

    @Override
    void read(DataInput input) throws IOException {
        this.type = input.readByte();
        int size = input.readInt();

        if (this.type == 0 && size > 0) {
            throw new RuntimeException("Missing type on ListTag");
        }
        this.list = new ArrayList<Tag>(size);
        for (int i = 0; i < size; i++) {
            Tag tag = Tag.newTag(this.type);
            tag.read(input);
            this.list.add(tag);
        }
    }
    
    public void loadList(Iterable<?> datas)
    {
    	Iterator<?> it = datas.iterator();
    	while (it.hasNext())
    	{
    		Object value = it.next();
    		if (value == null) {
    			continue;
    		}
    		if (value instanceof CharSequence) {
    			this.add(new TagString(value.toString()));
    		} else if (value instanceof Tag) {
    			this.add((Tag)value);
    		} else if (value instanceof Iterable) {
    			TagList subTag = new TagList();
    			subTag.loadList((Iterable<?>)value);
    			this.add(subTag);
    		} else if (value instanceof Map) {
    			TagCompound subTag = new TagCompound();
    			subTag.loadMap((Map<?, ?>)value);
    			this.add(subTag);
    		} else if (value instanceof Void) {
    			this.add(new TagEnd());
    		} else if (value instanceof Integer) {
    			this.add(new TagInt((Integer)value));
    		} else if (value instanceof Byte) {
    			this.add(new TagByte((Byte)value));
    		} else if (value instanceof Double) {
    			this.add(new TagDouble((Double)value));
    		} else if (value instanceof Long) {
    			this.add(new TagLong((Long)value));
    		} else if (value instanceof Float) {
    			this.add(new TagFloat((Float)value));
    		} else if (value instanceof Short) {
    			this.add(new TagShort((Short)value));
    		} else if (value instanceof int[]) {
    			this.add(new TagIntArray((int[])value));
    		} else if (value instanceof Integer[]) {
    			this.add(new TagIntArray(ArrayUtils.toPrimitive((Integer[])value)));
    		} else if (value instanceof byte[]) {
    			this.add(new TagByteArray((byte[])value));
    		} else if (value instanceof Byte[]) {
    			this.add(new TagByteArray(ArrayUtils.toPrimitive((Byte[])value)));
    		} else if (value instanceof long[]) {
    			this.add(new TagLongArray((long[])value));
    		} else if (value instanceof Long[]) {
    			this.add(new TagLongArray(ArrayUtils.toPrimitive((Long[])value)));
    		}
    	}
    }
    
    public List<Object> getAsObject()
    {
    	List<Object> result = new ArrayList<Object>();
    	Iterator<Tag> it = this.list.iterator();
    	
    	while (it.hasNext())
    	{
    		Tag t = it.next();
    		result.add(t.getAsObject());
    	}
    	return result;
    }
    
    public Object getAsNMS() {
    	Object nmsList = NBTListReflection.newInstance();
    	if (nmsList != null) {
    		for (Tag tag : this.list) {
    			NBTListReflection.add(nmsList, tag.getAsNMS());
    		}
    	}
    	return nmsList;
    }
    
    public void getFromNMS(Object nmsList) {
    	if (NBTListReflection.isNBTList(nmsList)) {
    		this.list.clear();
    		int typeID = NBTListReflection.getTypeInList(nmsList);
    		for (int i = 0; i < NBTListReflection.size(nmsList); i++) {
    			Tag current = Tag.newTag((byte)typeID);
    			current.getFromNMS(NBTListReflection.getAsNBTBase(nmsList, i));
    			this.list.add(current);
    		}
    	}
    }

    @Override
    public byte getTypeId() {
        return Tag.TAG_List;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("[");

        for (int i = 0; i < this.list.size(); ++i) {
            if (i != 0) {
                builder.append(",");
            }

            builder.append(i).append(":").append(this.list.get(i));
        }

        return builder.append("]").toString();
    }
    
    public void add(INBTTag elem) {
    	this.add((Tag)elem);
    }

    public void add(Tag elem) {
        if (elem.getTypeId() != Tag.TAG_End) {
            if (this.type == 0) {
                this.type = elem.getTypeId();
            } else if (this.type != elem.getTypeId()) {
                return;
            }

            this.list.add(elem);
        }
    }
    
    public void set(int idx, INBTTag elem) {
    	this.set(idx, (Tag)elem);
    }

    public void set(int idx, Tag elem) {
        if (elem.getTypeId() != Tag.TAG_End) {
            if (idx >= 0 && idx < this.list.size()) {
                if (this.type == 0) {
                    this.type = elem.getTypeId();
                } else if (this.type != elem.getTypeId()) {
                    return;
                }

                this.list.set(idx, elem);
            }
        }
    }

    public Tag remove(int index) {
        return this.list.remove(index);
    }

    @Override
    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    public Tag get(int index) {
        return this.list.get(index);
    }

    public int size() {
        return this.list.size();
    }

    @Override
    public TagList clone() {
        TagList copy = new TagList();
        copy.type = this.type;
        Iterator<Tag> it = this.list.iterator();

        while (it.hasNext()) {
            Tag base = it.next();
            Tag cloned = base.clone();
            copy.list.add(cloned);
        }
        return copy;
    }

    @Override
    public boolean equals(Object other) {
        if (super.equals(other)) {
            TagList taglist = (TagList) other;

            if (this.type == taglist.type) {
                return this.list.equals(taglist.list);
            }
        }

        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ this.list.hashCode();
    }

    public byte getTagType() {
        return this.type;
    }
}