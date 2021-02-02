package ch.killenberger.wowauctionhousebrowser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import ch.killenberger.wowauctionhousebrowser.model.auction.AuctionGroup;
import ch.killenberger.wowauctionhousebrowser.ui.AuctionDetailsAdapter;

public class AuctionDetailsActivity extends AppCompatActivity {
    private RecyclerView auctionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auction_details_activty);

        AuctionGroup group = getIntent().getParcelableExtra("AUCTION_GROUP");

        this.auctionView = findViewById(R.id.auctionDetailsRecyclerView);
        this.auctionView.setAdapter(new AuctionDetailsAdapter(group));
        this.auctionView.setLayoutManager(new LinearLayoutManager(this));
    }
}