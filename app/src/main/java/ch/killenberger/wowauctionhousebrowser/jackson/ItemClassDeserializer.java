package ch.killenberger.wowauctionhousebrowser.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

import ch.killenberger.wowauctionhousebrowser.model.item.ItemClass;

public class ItemClassDeserializer extends StdDeserializer<ItemClass> {
    public ItemClassDeserializer() {
        this(null);
    }

    public ItemClassDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public ItemClass deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        final JsonNode node = p.getCodec().readTree(p);

        final int       id        = node.get("id").asInt();
        final String    name      = node.get("name").asText();

        return new ItemClass(id, name);
    }
}
