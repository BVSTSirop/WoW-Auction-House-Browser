package ch.killenberger.wowauctionhousebrowser.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

import ch.killenberger.wowauctionhousebrowser.model.Realm;

public class RealmDeserializer extends StdDeserializer<Realm> {
    public RealmDeserializer() {
        this(null);
    }

    public RealmDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Realm deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        final int id      = node.get("id").asInt();
        final String name = node.get("name").asText();
        final String slug = node.get("slug").asText();

        return new Realm(id, slug, name);
    }
}
