package ch.killenberger.wowauctionhousebrowser.model.oauth2;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.time.Instant;

import ch.killenberger.wowauctionhousebrowser.jackson.AccessTokenDeserializer;

@JsonDeserialize(using = AccessTokenDeserializer.class)
public class AccessToken {
    private String  token;
    private String  type;
    private Instant expiry;

    public AccessToken() { }

    public AccessToken(final String token, final String type, final Instant expiry) {
        this.token  = token;
        this.type   = type;
        this.expiry = expiry;
    }

    public void update(final String token, final String type, final Instant expiry) {
        this.token  = token;
        this.type   = type;
        this.expiry = expiry;
    }

    public String getToken() {
        return token;
    }

    public String getType() {
        return type;
    }

    public Instant getExpiry() {
        return expiry;
    }

    @Override
    public String toString() {
        return "Token: "  + this.token +
               " Type: "   + this.type +
               " Expiry: " + this.expiry;
    }
}
