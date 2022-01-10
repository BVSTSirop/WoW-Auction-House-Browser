package ch.killenberger.wowauctionhousebrowser.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

import ch.killenberger.wowauctionhousebrowser.model.item.ItemSubClass;

public class ItemSubClassDeserializer extends StdDeserializer<ItemSubClass> {
    public ItemSubClassDeserializer() {
        this(null);
    }

    public ItemSubClassDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public ItemSubClass deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        final JsonNode node = p.getCodec().readTree(p);

        final ItemSubClass sc = new ItemSubClass();
        sc.setId(node.get("id").asInt());
        sc.setName(node.get("name").asText());

        return sc;
    }
}
