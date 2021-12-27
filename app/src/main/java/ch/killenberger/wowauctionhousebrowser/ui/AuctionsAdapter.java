package ch.killenberger.wowauctionhousebrowser.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import ch.killenberger.wowauctionhousebrowser.AuctionDetailsActivity;
import ch.killenberger.wowauctionhousebrowser.R;
import ch.killenberger.wowauctionhousebrowser.model.ApplicationSettings;
import ch.killenberger.wowauctionhousebrowser.model.auction.AuctionGroup;
import ch.killenberger.wowauctionhousebrowser.model.item.Item;
import ch.killenberger.wowauctionhousebrowser.sqlite.DatabaseHelper;
import ch.killenberger.wowauctionhousebrowser.util.CurrencyUtil;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class AuctionsAdapter extends RecyclerView.Adapter<AuctionsAdapter.ViewHolder> {
    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public ImageView image;
        public TextView  id;
        public TextView  gold;
        public ImageView goldImg;
        public TextView  silver;
        public ImageView silverImg;
        public TextView  copper;
        public ImageView copperImg;
        public TextView  name;
        public TextView  level;
        public TextView  quantity;


        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            image     = itemView.findViewById(R.id.auctionImage);
            id        = itemView.findViewById(R.id.auctionItemId);
            gold      = itemView.findViewById(R.id.auctionItemPriceGold);
            goldImg   = itemView.findViewById(R.id.auctionItemPriceGoldImg);
            silver    = itemView.findViewById(R.id.auctionItemPriceSilver);
            silverImg = itemView.findViewById(R.id.auctionItemPriceSilverImg);
            copper    = itemView.findViewById(R.id.auctionItemPriceCopper);
            copperImg = itemView.findViewById(R.id.auctionItemPriceCopperImg);
            name      = itemView.findViewById(R.id.auctionItemName);
            level     = itemView.findViewById(R.id.auctionItemLevel);
            quantity  = itemView.findViewById(R.id.auctionItemQuantity);
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
        View auctionView = inflater.inflate(R.layout.auction_groups_row_layout, parent, false);

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
        final AuctionGroup auction  = mAuction.get(position);
        final Item         item     = auction.getItem();

        DatabaseHelper db  = new DatabaseHelper(ApplicationSettings.getInstance().getApplicationContext());
        final Bitmap   bmp = db.getImageByItemId(auction.getItem().getId());
        db.close();

        setPriceFields(auction.getLowestPrice(), holder);

        ImageView img = holder.image;
        img.setImageBitmap(bmp);

        TextView id = holder.id;
        id.setText(String.valueOf(item.getId()));

        TextView name = holder.name;
        name.setText(item.getName());
        name.setTextColor(item.getQuality().getColor());

        TextView level = holder.level;
        level.setText(String.valueOf(item.getLevel()));

        TextView quantity = holder.quantity;
        quantity.setText(String.valueOf(auction.getTotalQuantity()));
    }

    private void setPriceFields(final long price, AuctionsAdapter.ViewHolder holder) {
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