package ch.killenberger.wowauctionhousebrowser.model.item;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import ch.killenberger.wowauctionhousebrowser.jackson.ItemDeserializer;

@JsonDeserialize(using = ItemDeserializer.class)
public class Item implements Parcelable {
    private int    id;
    private String name;
    private int    level;
    private int classId;
    private int    subClassId;


    public Item() { }

    public Item(final int id, final String name, final int level, final int classId, final int subClassId) {
        this.id         = id;
        this.name       = name;
        this.level      = level;
        this.classId    = classId;
        this.subClassId = subClassId;
    }

    protected Item(Parcel in) {
        this.id         = in.readInt();
        this.name       = in.readString();
        this.level      = in.readInt();
        this.classId    = in.readInt();
        this.subClassId = in.readInt();
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public int getSubClassId() {
        return subClassId;
    }

    public void setSubClassId(int subClassId) {
        this.subClassId = subClassId;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", level=" + level +
                ", classId=" + classId +
                ", subClassId=" + subClassId +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeInt(this.level);
        dest.writeInt(this.classId);
        dest.writeInt(this.subClassId);
    }
}
