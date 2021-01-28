package ch.killenberger.wowauctionhousebrowser.model.item;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import ch.killenberger.wowauctionhousebrowser.jackson.ItemClassDeserializer;

@JsonDeserialize(using = ItemClassDeserializer.class)
public class ItemClass {

    @JsonProperty
    private int    id;

    @JsonProperty
    private String name;

    public ItemClass() { }

    public ItemClass(final int id, final String name) {
        this.id   = id;
        this.name = name;
    }

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

    @Override
    public String toString() {
        return "ItemClass{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
