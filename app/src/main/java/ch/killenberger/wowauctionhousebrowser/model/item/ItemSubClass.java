package ch.killenberger.wowauctionhousebrowser.model.item;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import ch.killenberger.wowauctionhousebrowser.jackson.ItemSubClassDeserializer;

@JsonDeserialize(using = ItemSubClassDeserializer.class)
public class ItemSubClass extends ItemClass {
    private int    parentClassId;

    public ItemSubClass() { }

    public ItemSubClass(final int parentId, final int id, final String name) {
        super(id, name);
        this.parentClassId = parentId;
    }

    public int getParentClassId() {
        return parentClassId;
    }

    public void setParentClassId(int parentClassId) {
        this.parentClassId = parentClassId;
    }

    @Override
    public String toString() {
        return getName();
    }
}
