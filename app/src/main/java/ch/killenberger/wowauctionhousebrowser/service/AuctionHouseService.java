package ch.killenberger.wowauctionhousebrowser.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ch.killenberger.wowauctionhousebrowser.R;
import ch.killenberger.wowauctionhousebrowser.client.HttpGetClient;
import ch.killenberger.wowauctionhousebrowser.enums.Region;
import ch.killenberger.wowauctionhousebrowser.model.ApplicationSettings;
import ch.killenberger.wowauctionhousebrowser.model.auction.Auction;
import ch.killenberger.wowauctionhousebrowser.model.UserSettings;
import ch.killenberger.wowauctionhousebrowser.model.auction.AuctionGroup;
import ch.killenberger.wowauctionhousebrowser.model.item.Item;
import ch.killenberger.wowauctionhousebrowser.sqlite.DatabaseHelper;
import ch.killenberger.wowauctionhousebrowser.ui.AuctionsAdapter;
import ch.killenberger.wowauctionhousebrowser.util.AlertUtil;

public class AuctionHouseService extends AsyncTask<String, Void, List<AuctionGroup>> {
    final UserSettings        userSettings = UserSettings.getInstance();
    final ApplicationSettings appSettings  = ApplicationSettings.getInstance();

    private Exception exception; // Used for exception handling

    private Context        mContext;
    private ProgressDialog dialog;
    private RecyclerView   list;

    public AuctionHouseService(Context context, RecyclerView list){
        super();

        this.mContext = context;
        this.list     = list;
    }

    @Override
    protected void onPreExecute() {
        this.dialog = ProgressDialog.show(mContext,"Auctions", "Loading auctions...");
    }

    @Override
    protected List<AuctionGroup> doInBackground(String... strings) {
        final Locale locale           = appSettings.getLocale();
        final Region region           = userSettings.getRegion();
        final int    connectedRealmId = userSettings.getConnectedRealmId();
        final String endpoint         = region.getHost() + "/data/wow/connected-realm/" + connectedRealmId + "/auctions?namespace="+ region.getDynamicNamespace() + "&locale=" + locale.toString() + "&access_token=" + ApplicationSettings.getInstance().getAccessToken().getToken();


        try {
            final String        response = HttpGetClient.gzipCall(endpoint);
            final List<Auction> auctions = parseResponse(response);

            return groupAuctions(auctions);
        } catch (IOException e) {
            this.exception = e;
        }

        return new ArrayList<>();
    }

    private List<AuctionGroup> groupAuctions(List<Auction> auctions) {
        final Map<Integer, AuctionGroup> groups = new HashMap<>();
        final DatabaseHelper             db     = new DatabaseHelper(appSettings.getApplicationContext());

        for(Auction auction : auctions) {
            final int  itemId = auction.getItemId();
            final long price  = auction.getPrice();

            if(!groups.containsKey(itemId)) {
                final Item item = db.getItemById(itemId);

                groups.put(itemId, new AuctionGroup(item));
            }

            final AuctionGroup group = groups.get(itemId);

            if(group.hasAucitonWithPrice(price)) {
                Auction a = group.getAuctionByPrice(price);
                a.setQuantity(a.getQuantity() + auction.getQuantity());
            } else {
                group.add(auction);
            }
        }

        db.close();

        final List<AuctionGroup> result = new ArrayList(groups.values());
        Collections.sort(result);

        return result;
    }

    @Override
    protected void onPostExecute(List<AuctionGroup> auctions) {
        super.onPostExecute(auctions);

        if (dialog.isShowing()) {
            dialog.dismiss();
        }

        if(exception != null) {
            AlertUtil.createAlertDialog(mContext, "Oops", this.mContext.getString(R.string.connection_failed_error_msg));
        }

        list.setAdapter(new AuctionsAdapter(auctions));
        list.setLayoutManager(new LinearLayoutManager(mContext));
    }

    private List<Auction> parseResponse(String resp) throws JsonProcessingException {
        final ObjectNode node = new ObjectMapper().readValue(resp, ObjectNode.class);
        final String auctions = node.get("auctions").toString();

        return Arrays.asList(new ObjectMapper().readValue(auctions,  Auction[].class));
    }
}