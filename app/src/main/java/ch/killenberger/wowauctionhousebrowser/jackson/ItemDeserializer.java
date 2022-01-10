package ch.killenberger.wowauctionhousebrowser.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.Locale;

import ch.killenberger.wowauctionhousebrowser.enums.ItemQuality;
import ch.killenberger.wowauctionhousebrowser.model.ApplicationSettings;
import ch.killenberger.wowauctionhousebrowser.model.item.Item;

public class ItemDeserializer extends StdDeserializer<Item> {
    public ItemDeserializer() {
        this(null);
    }

    public ItemDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Item deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        final JsonNode node = (JsonNode) p.getCodec().readTree(p).get("data");
        final Locale   lang = ApplicationSettings.getInstance().getLocale();

        final int         id           = node.get("id").asInt();
        final int         level        = node.get("level").asInt();
        final String      name         = node.get("name").get(lang.toString()).asText().replace("\"", "").replace("\'", "");
        final int         classId      = node.get("item_class").get("id").asInt();
        final int         subClassId   = node.get("item_subclass").get("id").asInt();
        final ItemQuality quality      = ItemQuality.valueOf(node.get("quality").get("name").get("en_US").asText().replace("\"", "").replace("\'", "").replace(" ", "_").toUpperCase());

        return new Item(id, name, level, classId, subClassId, quality);
    }
}
