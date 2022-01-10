package ch.killenberger.wowauctionhousebrowser.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import ch.killenberger.wowauctionhousebrowser.enums.Region;
import ch.killenberger.wowauctionhousebrowser.jackson.RealmDeserializer;

@JsonDeserialize(using = RealmDeserializer.class)
public class Realm {
    private Region region;

    @JsonProperty
    private int    id;

    @JsonProperty
    private String slug;

    @JsonProperty
    private String name;

    public void Realm() {
        // TODO: Realm constructor
    }

    public Realm(final int id, final String slug, final String name) {
        this.id   = id;
        this.slug = slug;
        this.name = name;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
