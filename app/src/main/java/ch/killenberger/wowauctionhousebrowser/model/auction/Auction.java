package ch.killenberger.wowauctionhousebrowser.model.auction;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import ch.killenberger.wowauctionhousebrowser.jackson.AuctionDeserializer;
import ch.killenberger.wowauctionhousebrowser.model.item.Item;
import ch.killenberger.wowauctionhousebrowser.util.CurrencyUtil;

@JsonDeserialize(using = AuctionDeserializer.class)
public class Auction implements Parcelable {

    @JsonProperty
    private int id;

    @JsonProperty
    private long price;

    @JsonProperty
    private int quantity;

    @JsonProperty
    private int itemId;

    public Auction() { }

    public Auction(final int id, final int itemId, final int quantity, final long price) {
        this.id       = id;
        this.quantity = quantity;
        this.itemId   = itemId;
        this.price    = price;
    }

    protected Auction(Parcel in) {
        id = in.readInt();
        price = in.readLong();
        quantity = in.readInt();
        itemId = in.readInt();
    }

    public static final Creator<Auction> CREATOR = new Creator<Auction>() {
        @Override
        public Auction createFromParcel(Parcel in) {
            return new Auction(in);
        }

        @Override
        public Auction[] newArray(int size) {
            return new Auction[size];
        }
    };

    public String getFormattedPrice() {
        return CurrencyUtil.formatCopperAmount(this.price);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    @Override
    public String toString() {
        return "Auction{" +
                "id=" + id +
                ", price=" + price +
                ", quantity=" + quantity +
                ", itemId=" + itemId +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeLong(price);
        dest.writeInt(quantity);
        dest.writeInt(itemId);
    }
}
