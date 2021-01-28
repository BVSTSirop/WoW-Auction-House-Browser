package ch.killenberger.wowauctionhousebrowser;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import ch.killenberger.wowauctionhousebrowser.service.AuctionHouseService;
import ch.killenberger.wowauctionhousebrowser.ui.AuctionsAdapter;

public class AuctionsActivity extends AppCompatActivity {
    private RecyclerView  recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auctions);

        recyclerView = findViewById(R.id.acutionsRecyclerView);

        new AuctionHouseService(this, recyclerView).execute();
    }
}