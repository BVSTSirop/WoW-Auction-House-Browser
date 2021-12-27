package ch.killenberger.wowauctionhousebrowser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ch.killenberger.wowauctionhousebrowser.enums.ItemQuality;
import ch.killenberger.wowauctionhousebrowser.model.auction.AuctionGroup;
import ch.killenberger.wowauctionhousebrowser.model.item.Item;
import ch.killenberger.wowauctionhousebrowser.sqlite.DatabaseHelper;
import ch.killenberger.wowauctionhousebrowser.ui.AuctionDetailsAdapter;
import de.hdodenhof.circleimageview.CircleImageView;

public class AuctionDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auction_details_activty);

        final AuctionGroup    group            = getIntent().getParcelableExtra("AUCTION_GROUP");
        final Item            item             = group.getItem();
        final ItemQuality     quality          = item.getQuality();
        final DatabaseHelper  db               = new DatabaseHelper(this);
        final RecyclerView    auctionView      = findViewById(R.id.auctionDetailsRecyclerView);
        final CircleImageView itemTooltipImage = findViewById(R.id.itemTooltipImage);
        final TextView        itemTooltipName  = findViewById(R.id.itemTooltipName);

        Bitmap bmp = db.getImageByItemId(item.getId());
        db.close();

        itemTooltipImage.setImageBitmap(bmp);
        itemTooltipImage.setBorderColor(item.getQuality().getColor());

        itemTooltipName.setText(item.getName());
        itemTooltipName.setTextColor(quality.getColor());

        auctionView.setAdapter(new AuctionDetailsAdapter(group));
        auctionView.setLayoutManager(new LinearLayoutManager(this));
    }
}