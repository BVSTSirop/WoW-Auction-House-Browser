package ch.killenberger.wowauctionhousebrowser.model.auction;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ch.killenberger.wowauctionhousebrowser.model.item.Item;
import ch.killenberger.wowauctionhousebrowser.util.CurrencyUtil;

public class AuctionGroup implements Comparable<AuctionGroup>, Parcelable {
    private final Item item;

    private List<Auction> auctions;

    public AuctionGroup(final Item item) {
        this(item, new ArrayList<>());
    }

    public AuctionGroup(final Item item, final List<Auction> auctions) {
        this.item     = item;
        this.auctions = auctions;
    }

    protected AuctionGroup(Parcel in) {
        item     = in.readParcelable(Item.class.getClassLoader());
        auctions = in.createTypedArrayList(Auction.CREATOR);
    }

    public long getLowestPrice() {
        long lowest = Long.MAX_VALUE;

        for(Auction a : this.auctions) {
            final long price = a.getPrice();

            if(price < lowest) {
                lowest = price;
            }
        }

        return lowest;
    }

    public int getTotalQuantity() {
        int quantity = 0;

        for(Auction a : auctions) {
            quantity += a.getQuantity();
        }

        return quantity;
    }

    public Item getItem() {
        return item;
    }

    public void add(final Auction a) {
        auctions.add(a);
    }

    public void addAll(final Collection<Auction> auctions) {
        auctions.addAll(auctions);
    }

    @Override
    public int compareTo(AuctionGroup a) {
        if(this.item == null || this.item.getName() == null) {
            return 1;
        }

        if(a.getItem() == null || a.getItem().getName() == null) {
            return -1;
        }

        return this.item.getName().compareTo(a.getItem().getName());
    }

    public List<Auction> getAuctions() {
        final List<Auction> cloned = new ArrayList<>(this.auctions);

        Collections.sort(cloned, new Comparator<Auction>() {
            @Override
            public int compare(Auction a1, Auction a2) {
                return ((Long)a1.getPrice()).compareTo(a2.getPrice());
            }
        });

        return cloned;
    }

    public static final Creator<AuctionGroup> CREATOR = new Creator<AuctionGroup>() {
        @Override
        public AuctionGroup createFromParcel(Parcel in) {
            return new AuctionGroup(in);
        }

        @Override
        public AuctionGroup[] newArray(int size) {
            return new AuctionGroup[size];
        }
    };

    public String getLowestPriceFormatted() {
        final long price = getLowestPrice();

        return CurrencyUtil.formatCopperAmount(price);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(item, flags);
        dest.writeTypedList(auctions);
    }
}
