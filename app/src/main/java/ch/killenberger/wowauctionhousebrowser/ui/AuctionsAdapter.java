package ch.killenberger.wowauctionhousebrowser.ui;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import ch.killenberger.wowauctionhousebrowser.AuctionDetailsActivity;
import ch.killenberger.wowauctionhousebrowser.R;
import ch.killenberger.wowauctionhousebrowser.model.auction.Auction;
import ch.killenberger.wowauctionhousebrowser.model.auction.AuctionGroup;
import ch.killenberger.wowauctionhousebrowser.model.item.Item;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class AuctionsAdapter extends RecyclerView.Adapter<AuctionsAdapter.ViewHolder> {
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
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            id       = itemView.findViewById(R.id.auctionItemId);
            price    = itemView.findViewById(R.id.auctionItemPrice);
            name     = itemView.findViewById(R.id.auctionItemName);
            level    = itemView.findViewById(R.id.auctionItemLevel);
            quantity = itemView.findViewById(R.id.auctionItemQuantity);
        }
    }

    // Store a member variable for the contacts
    private final List<AuctionGroup> auctionGroups;
    private List<AuctionGroup> mAuction;

    // Pass in the contact array into the constructor
    public AuctionsAdapter(List<AuctionGroup> auctions) {
        auctionGroups = auctions;
        mAuction    = new ArrayList<>(auctionGroups);
    }

    // Usually involves inflating a layout from XML and returning the holder
    @NotNull
    @Override
    public AuctionsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View auctionView = inflater.inflate(R.layout.auctions_layout, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(auctionView);

        auctionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int itemId = Integer.parseInt((String) viewHolder.id.getText());

                AuctionGroup group = null;
                for(AuctionGroup g : mAuction) {
                    if(g.getItem().getId() == itemId) {
                        group = g;

                        break;
                    }
                }

                Intent intent = new Intent(context, AuctionDetailsActivity.class);
                intent.putExtra("AUCTION_GROUP", group);
                context.startActivity(intent);
            }
        });
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(AuctionsAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        AuctionGroup auction = mAuction.get(position);

        TextView id = holder.id;
        id.setText(String.valueOf(auction.getItem().getId()));

        TextView price = holder.price;
        price.setText(auction.getLowestPriceFormatted());

        TextView name = holder.name;
        name.setText(auction.getItem().getName());

        TextView level = holder.level;
        level.setText(String.valueOf(auction.getItem().getLevel()));

        TextView quantity = holder.quantity;
        quantity.setText(String.valueOf(auction.getTotalQuantity()));
    }

    public void filter(final String name, final int classId, final int subClassId) {
        if(classId == -1) {             // No class filter applied
            resetFilters();
        } else if (subClassId == -1) {  // No subclass filter applied
            filterByClass(classId);
        } else {
            filterBySubClass(classId, subClassId);
        }

        if(!name.isEmpty()) {
            filterByName(name);
        }
    }

    private void resetFilters() {
        mAuction = new ArrayList<>(auctionGroups);

        notifyDataSetChanged();
    }

    private void filterByClass(final int id) {
        mAuction.clear();

        for(AuctionGroup ag : this.auctionGroups) {
            if(ag.getItem().getClassId() == id) {
                mAuction.add(ag);
            }
        }

        notifyDataSetChanged();
    }

    private void filterBySubClass(final int parentClassId, final int subClassId) {
        this.mAuction.clear();

        for(AuctionGroup ag : this.auctionGroups) {
            final Item item = ag.getItem();

            if(item.getClassId() == parentClassId && item.getSubClassId() == subClassId) {
                mAuction.add(ag);
            }
        }

        notifyDataSetChanged();
    }

    private void filterByName(final String name) {
        final List<AuctionGroup> filtered = new ArrayList<>();

        for(AuctionGroup ag : this.mAuction) {
            final Item item = ag.getItem();

            if(item.getName() != null && item.getName().toLowerCase().contains(name.toLowerCase())) {
                filtered.add(ag);
            }
        }

        this.mAuction = filtered;

        notifyDataSetChanged();
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mAuction.size();
    }
}