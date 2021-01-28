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
    private int    itemClassId;

    public Item() { }

    public Item(final int id, final String name, final int level, final int itemClassId) {
        this.id        = id;
        this.name      = name;
        this.level     = level;
        this.itemClassId = itemClassId;
    }

    protected Item(Parcel in) {
        id = in.readInt();
        name = in.readString();
        level = in.readInt();
        itemClassId = in.readInt();
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

    public int getItemClassId() {
        return itemClassId;
    }

    public void setItemClassId(int itemClassId) {
        this.itemClassId = itemClassId;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", level=" + level +
                ", itemClassId=" + itemClassId +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(level);
        dest.writeInt(itemClassId);
    }
}
