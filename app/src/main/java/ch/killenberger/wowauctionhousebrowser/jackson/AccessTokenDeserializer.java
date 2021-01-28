package ch.killenberger.wowauctionhousebrowser.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.IntNode;

import java.io.IOException;
import java.time.Instant;

import ch.killenberger.wowauctionhousebrowser.model.oauth2.AccessToken;

public class AccessTokenDeserializer extends StdDeserializer<AccessToken> {
    public AccessTokenDeserializer() {
        this(null);
    }

    public AccessTokenDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public AccessToken deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = p.getCodec().readTree(p);

        final String  token  = node.get("access_token").asText();
        final String  type   = node.get("token_type").asText();
        final int     expiry_in_seconds = node.get("expires_in").asInt();
        final Instant expiry            = Instant.now().plusSeconds(expiry_in_seconds);

        return new AccessToken(token, type, expiry);
    }
}
