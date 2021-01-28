package ch.killenberger.wowauctionhousebrowser.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ch.killenberger.wowauctionhousebrowser.R;
import ch.killenberger.wowauctionhousebrowser.model.auction.Auction;
import ch.killenberger.wowauctionhousebrowser.model.auction.AuctionGroup;
import ch.killenberger.wowauctionhousebrowser.model.item.Item;

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
        public TextView price;
        public TextView name;
        public TextView level;
        public TextView quantity;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            super(itemView);

            id       = itemView.findViewById(R.id.auctionItemId);
            price    = itemView.findViewById(R.id.auctionItemPrice);
            name     = itemView.findViewById(R.id.auctionItemName);
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
        View auctionView = inflater.inflate(R.layout.auctions_layout, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(auctionView);

        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(AuctionDetailsAdapter.ViewHolder holder, int position) {
        final Auction auction = mAuction.get(position);

        TextView id = holder.id;
        id.setText(String.valueOf(this.item.getId()));

        TextView price = holder.price;
        price.setText(auction.getFormattedPrice());

        TextView name = holder.name;
        name.setText(this.item.getName());

        TextView level = holder.level;
        level.setText(String.valueOf(this.item.getLevel()));

        TextView quantity = holder.quantity;
        quantity.setText(String.valueOf(auction.getQuantity()));
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mAuction.size();
    }
}