package ch.killenberger.wowauctionhousebrowser.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.Locale;

import ch.killenberger.wowauctionhousebrowser.model.ApplicationSettings;
import ch.killenberger.wowauctionhousebrowser.model.item.Item;
import ch.killenberger.wowauctionhousebrowser.model.item.ItemClass;
import ch.killenberger.wowauctionhousebrowser.sqlite.DatabaseHelper;

public class ItemDeserializer extends StdDeserializer<Item> {
    public ItemDeserializer() {
        this(null);
    }

    public ItemDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Item deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        final JsonNode node = (JsonNode) p.getCodec().readTree(p).get("data");
        final Locale   lang = ApplicationSettings.getInstance().getLocale();

        try {
            final int    id         = node.get("id").asInt();
            final int    level      = node.get("level").asInt();
            final String name       = node.get("name").get(lang.toString()).asText().replaceAll("\"", "").replaceAll("\'", "");
            final int    classId    = node.get("item_class").get("id").asInt();
            final int    subClassId = node.get("item_subclass").get("id").asInt();

            return new Item(id, name, level, classId, subClassId);
        } catch (Exception e) {
            e.printStackTrace();

            System.out.println(node.toPrettyString());
        }

        return null;
    }
}
