package me.unei.configuration.formats.nbtlib;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TagList extends Tag {

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

    public int getTagType() {
        return this.type;
    }
}