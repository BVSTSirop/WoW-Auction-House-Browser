package ch.killenberger.wowauctionhousebrowser.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ch.killenberger.wowauctionhousebrowser.R;
import ch.killenberger.wowauctionhousebrowser.model.auction.Auction;
import ch.killenberger.wowauctionhousebrowser.model.auction.AuctionGroup;
import ch.killenberger.wowauctionhousebrowser.model.item.Item;
import ch.killenberger.wowauctionhousebrowser.util.CurrencyUtil;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class AuctionDetailsAdapter extends RecyclerView.Adapter<AuctionDetailsAdapter.ViewHolder> {
    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView id;
        public TextView  gold;
        public ImageView goldImg;
        public TextView  silver;
        public ImageView silverImg;
        public TextView  copper;
        public ImageView copperImg;
        public TextView level;
        public TextView quantity;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            super(itemView);

            id       = itemView.findViewById(R.id.auctionItemId);
            gold      = itemView.findViewById(R.id.auctionItemPriceGold);
            goldImg   = itemView.findViewById(R.id.auctionItemPriceGoldImg);
            silver    = itemView.findViewById(R.id.auctionItemPriceSilver);
            silverImg = itemView.findViewById(R.id.auctionItemPriceSilverImg);
            copper    = itemView.findViewById(R.id.auctionItemPriceCopper);
            copperImg = itemView.findViewById(R.id.auctionItemPriceCopperImg);
            level    = itemView.findViewById(R.id.auctionItemLevel);
            quantity = itemView.findViewById(R.id.auctionItemQuantity);
        }
    }

    // Store a member variable for the contacts
    private final AuctionGroup  group;
    private final Item item;
    private final List<Auction> mAuction;

    // Pass in the contact array into the constructor
    public AuctionDetailsAdapter(AuctionGroup group) {
        this.group    = group;
        this.item     = group.getItem();
        this.mAuction = group.getAuctions();
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public AuctionDetailsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View auctionView = inflater.inflate(R.layout.auction_detail_row_layout, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(auctionView);

        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(AuctionDetailsAdapter.ViewHolder holder, int position) {
        final Auction auction = mAuction.get(position);

        setPriceFields(auction.getPrice(), holder);

        TextView level = holder.level;
        level.setText(String.valueOf(this.item.getLevel()));

        TextView quantity = holder.quantity;
        quantity.setText(String.valueOf(auction.getQuantity()));
    }

    private void setPriceFields(final long price, AuctionDetailsAdapter.ViewHolder holder) {
        final long copperAmount = price % 100;
        final long silverAmount = CurrencyUtil.copperToSilver(price) % 100;
        final long goldAmount   = CurrencyUtil.copperToGold(price);

        if(goldAmount == 0) {
            holder.gold.setVisibility(View.GONE);
            holder.goldImg.setVisibility(View.GONE);
        } else {
            holder.gold.setText(String.valueOf(goldAmount));

            holder.gold.setVisibility(View.VISIBLE);
            holder.goldImg.setVisibility(View.VISIBLE);
        }

        if(silverAmount == 0) {
            holder.silver.setVisibility(View.GONE);
            holder.silverImg.setVisibility(View.GONE);
        } else {
            holder.silver.setText(String.valueOf(silverAmount));

            holder.silver.setVisibility(View.VISIBLE);
            holder.silverImg.setVisibility(View.VISIBLE);
        }

        if(goldAmount == 0 && silverAmount == 0) {
            holder.copper.setText(String.valueOf(price));
        } else {
            if(copperAmount == 0) {
                holder.copper.setVisibility(View.GONE);
                holder.copperImg.setVisibility(View.GONE);
            } else {
                holder.copper.setText(String.valueOf(copperAmount));

                holder.copper.setVisibility(View.VISIBLE);
                holder.copperImg.setVisibility(View.VISIBLE);
            }
        }
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mAuction.size();
    }
}