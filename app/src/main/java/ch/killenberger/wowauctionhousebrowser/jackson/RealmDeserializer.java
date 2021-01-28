package ch.killenberger.wowauctionhousebrowser.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.Instant;

import ch.killenberger.wowauctionhousebrowser.enums.Region;
import ch.killenberger.wowauctionhousebrowser.model.ApplicationSettings;
import ch.killenberger.wowauctionhousebrowser.model.Realm;
import ch.killenberger.wowauctionhousebrowser.model.oauth2.AccessToken;

public class RealmDeserializer extends StdDeserializer<Realm> {
    public RealmDeserializer() {
        this(null);
    }

    public RealmDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Realm deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = p.getCodec().readTree(p);

        final int id      = node.get("id").asInt();
        final String name = node.get("name").asText();
        final String slug = node.get("slug").asText();

        return new Realm(id, slug, name);
    }
}
