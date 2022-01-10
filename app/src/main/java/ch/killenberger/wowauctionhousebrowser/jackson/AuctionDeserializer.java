package ch.killenberger.wowauctionhousebrowser.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

import ch.killenberger.wowauctionhousebrowser.model.auction.Auction;

public class AuctionDeserializer extends StdDeserializer<Auction> {
    public AuctionDeserializer() {
        this(null);
    }

    public AuctionDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Auction deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        final JsonNode    node = p.getCodec().readTree(p);

        final int id       = node.get("id").asInt();
        final int quantity = node.get("quantity").asInt();

        long buyout;
        if(node.has("buyout")) {
            buyout = node.get("buyout").asLong();
        } else {
            buyout = -1;
        }

        long unitPrice;
        if(node.has("unit_price")) {
            unitPrice = node.get("unit_price").asLong();
        } else {
            unitPrice = -1;
        }

        final long price;
        if(buyout == -1) {
            price = unitPrice;
        } else {
            price = buyout;
        }

        final int  itemId = node.get("item").get("id").asInt();

        return new Auction(id, itemId, quantity, price);
    }
}
