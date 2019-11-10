package me.unei.configuration.reflection.nbtmirror;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import me.unei.configuration.ArrayTools;
import me.unei.configuration.api.format.INBTList;
import me.unei.configuration.api.format.INBTTag;
import me.unei.configuration.api.format.TagType;
import me.unei.configuration.reflection.NBTArrayReflection;
import me.unei.configuration.reflection.NBTBaseReflection;
import me.unei.configuration.reflection.NBTCompoundReflection;
import me.unei.configuration.reflection.NBTListReflection;
import me.unei.configuration.reflection.NBTNumberReflection;
import me.unei.configuration.reflection.NBTStringReflection;

public final class MirrorTagList extends MirrorTag implements INBTList {

    public MirrorTagList(Object original) {
    	super(original, NBTListReflection::isNBTList);
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
    			this.add(new MirrorTagString(NBTStringReflection.newInstance(value.toString())) );
    		} else if (value instanceof MirrorTag) {
    			this.add((MirrorTag) value);
    		} else if (value instanceof me.unei.configuration.formats.nbtlib.Tag) {
    			this.add(((me.unei.configuration.formats.nbtlib.Tag) value).getAsMirrorTag());
    		} else if (value instanceof Map) {
    			MirrorTagCompound subTag = new MirrorTagCompound(NBTCompoundReflection.newInstance());
    			subTag.loadMap((Map<?, ?>)value);
    			this.add(subTag);
    		} else if (value instanceof Iterable) {
    			Class<?> subType = ArrayTools.getIterableParam((Iterable<?>) value);
    			if (subType.equals(byte.class) || subType.equals(Byte.class)) {
    				this.addByteArray(ArrayTools.toBytes(value));
    			} else if (subType.equals(int.class) || subType.equals(Integer.class)) {
    				this.addIntArray(ArrayTools.toInts(value));
    			} else if (subType.equals(long.class) || subType.equals(Long.class)) {
    				this.addLongArray(ArrayTools.toLongs(value));
    			} else {
    				MirrorTagList subTag = new MirrorTagList(NBTListReflection.newInstance());
    				subTag.loadList((Iterable<?>)value);
    				this.add(subTag);
    			}
    		} else if (value instanceof Void) {
    			this.add(createTag(TagType.TAG_End.getId()));
    		} else if (value instanceof Integer) {
    			this.addInt((Integer)value);
    		} else if (value instanceof Byte) {
    			this.addByte((Byte)value);
    		} else if (value instanceof Double) {
    			this.addDouble((Double)value);
    		} else if (value instanceof Long) {
    			this.addLong((Long)value);
    		} else if (value instanceof Float) {
    			this.addFloat((Float)value);
    		} else if (value instanceof Short) {
    			this.addShort((Short)value);
    		} else if (value instanceof int[]) {
    			this.addIntArray((int[])value);
    		} else if (value instanceof Integer[]) {
    			this.addIntArray(ArrayTools.toPrimitive((Integer[])value));
    		} else if (value instanceof byte[]) {
    			this.addByteArray((byte[])value);
    		} else if (value instanceof Byte[]) {
    			this.addByteArray(ArrayTools.toPrimitive((Byte[])value));
    		} else if (value instanceof long[]) {
    			this.addLongArray((long[])value);
    		} else if (value instanceof Long[]) {
    			this.addLongArray(ArrayTools.toPrimitive((Long[])value));
    		}
    	}
    }

    @Override
    public List<Object> getAsObject() {
    	return getAsObject(DEFAULT_CREATOR);
    }

    @Override
    public <M extends Map<String, Object>, L extends List<Object>> L getAsObject(ObjectCreator<M, L> creator)
    {
    	L result = creator.newList();
    	for (int i = 0; i < size(); ++i) {
    		MirrorTag t = get(i);
    		result.add(t.getAsObject(creator));
    	}
    	return result;
    }
    
    @Override
    public me.unei.configuration.formats.nbtlib.TagList localCopy() {
    	me.unei.configuration.formats.nbtlib.TagList list = new me.unei.configuration.formats.nbtlib.TagList();
    	list.getFromNMS(mirroredTag);
    	return list;
    }
    
    @Override
    public void add(INBTTag elem) {
    	if (elem instanceof MirrorTag) {
    		this.add((MirrorTag) elem);
    	} else if (elem instanceof me.unei.configuration.formats.nbtlib.Tag) {
			this.add(((me.unei.configuration.formats.nbtlib.Tag) elem).getAsMirrorTag());
		}
    }
    
    @Override
    public void addByte(byte elem) {
    	this.add(new MirrorTagByte(NBTNumberReflection.newByte(elem)));
    }
    
    @Override
    public void addShort(short elem) {
    	this.add(new MirrorTagShort(NBTNumberReflection.newShort(elem)));
    }
    
    @Override
    public void addInt(int elem) {
    	this.add(new MirrorTagInt(NBTNumberReflection.newInt(elem)));
    }
    
    @Override
    public void addLong(long elem) {
    	this.add(new MirrorTagLong(NBTNumberReflection.newLong(elem)));
    }
    
    @Override
    public void addFloat(float elem) {
    	this.add(new MirrorTagFloat(NBTNumberReflection.newFloat(elem)));
    }
    
    @Override
    public void addDouble(double elem) {
    	this.add(new MirrorTagDouble(NBTNumberReflection.newDouble(elem)));
    }
    
    @Override
    public void addString(String elem) {
    	this.add(new MirrorTagString(NBTStringReflection.newInstance(elem)));
    }
    
    @Override
    public void addByteArray(byte[] elem) {
    	this.add(new MirrorTagByteArray(NBTArrayReflection.newByteArray(elem)));
    }
    
    @Override
    public void addIntArray(int[] elem) {
    	this.add(new MirrorTagIntArray(NBTArrayReflection.newIntArray(elem)));
    }
    
    @Override
    public void addLongArray(long[] elem) {
    	this.add(new MirrorTagLongArray(NBTArrayReflection.newLongArray(elem)));
    }
    
    @Override
    public void addBoolean(boolean elem) {
    	this.addByte(elem ? ((byte) 1) : ((byte) 0));
    }

    public void add(MirrorTag elem) {
    	NBTListReflection.add(mirroredTag, elem.getNMS());
    }

    @Override
    public void set(int idx, INBTTag elem) {
    	if (elem instanceof MirrorTag) {
    		this.set(idx, (MirrorTag) elem);
    	} else if (elem instanceof me.unei.configuration.formats.nbtlib.Tag) {
			this.set(idx, ((me.unei.configuration.formats.nbtlib.Tag) elem).getAsMirrorTag());
		}
    }

    public void set(int idx, MirrorTag elem) {
        NBTListReflection.set(mirroredTag, idx, elem.getNMS());
    }

    @Override
    public MirrorTag remove(int index) {
        return wrap(NBTListReflection.remove(mirroredTag, index));
    }

    @Override
    public MirrorTag get(int index) {
        return wrap(NBTListReflection.getAsNBTBase(mirroredTag, index));
    }

    @Override
    public byte getTypeOf(int index) {
        return getTagTypeId();
    }

    public TagType getTypeOfTag(int index) {
        return getTagType();
    }

    @Override
    public byte getByte(int index) {
    	return NBTNumberReflection.getByte(NBTListReflection.getAsNBTBase(mirroredTag, index));
    }

    @Override
    public short getShort(int index) {
    	return NBTListReflection.getAsShort(mirroredTag, index);
    }

    @Override
    public int getInt(int index) {
    	return NBTListReflection.getAsInt(mirroredTag, index);
    }

    @Override
    public long getLong(int index) {
    	return NBTNumberReflection.getLong(NBTListReflection.getAsNBTBase(mirroredTag, index));
    }

    @Override
    public float getFloat(int index) {
    	return NBTListReflection.getAsFloat(mirroredTag, index);
    }

    @Override
    public double getDouble(int index) {
    	return NBTListReflection.getAsDouble(mirroredTag, index);
    }

    @Override
    public String getString(int index) {
    	return NBTListReflection.getAsString(mirroredTag, index);
    }

    @Override
    public byte[] getByteArray(int index) {
    	return NBTArrayReflection.getByteArray(NBTListReflection.getAsNBTBase(mirroredTag, index));
    }

    @Override
    public int[] getIntArray(int index) {
    	return NBTListReflection.getAsIntArray(mirroredTag, index);
    }

    @Override
    public long[] getLongArray(int index) {
    	return NBTArrayReflection.getLongArray(NBTListReflection.getAsNBTBase(mirroredTag, index));
    }

    @Override
    public MirrorTagCompound getCompound(int index) {
    	return new MirrorTagCompound(NBTListReflection.getAsCompound(mirroredTag, index));
    }

    @Override
    public MirrorTagList getList(int index, byte type) {
    	return new MirrorTagList(NBTListReflection.getAsList(mirroredTag, index));
    }
    
    @Override
    public INBTList getList(int index, TagType type) {
    	return getList(index, type.getId());
    }

    @Override
    public boolean getBoolean(int index) {
        return this.getByte(index) != 0;
    }

    @Override
    public int size() {
        return NBTListReflection.size(mirroredTag);
    }
    
    public void clear() {
    	NBTListReflection.clear(mirroredTag);
    }

    @Override
    public MirrorTagList clone() {
        return new MirrorTagList(NBTBaseReflection.cloneNBT(mirroredTag));
    }

    @Override
    public byte getTagTypeId() {
    	return (byte) NBTListReflection.getTypeInList(mirroredTag);
    }

    @Override
    public TagType getTagType() {
        return TagType.getByTypeId(getTagTypeId());
    }
}