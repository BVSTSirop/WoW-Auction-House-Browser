package ch.killenberger.wowauctionhousebrowser.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ch.killenberger.wowauctionhousebrowser.client.HttpGetClient;
import ch.killenberger.wowauctionhousebrowser.enums.Region;
import ch.killenberger.wowauctionhousebrowser.model.ApplicationSettings;
import ch.killenberger.wowauctionhousebrowser.model.auction.Auction;
import ch.killenberger.wowauctionhousebrowser.model.UserSettings;
import ch.killenberger.wowauctionhousebrowser.model.auction.AuctionGroup;
import ch.killenberger.wowauctionhousebrowser.model.item.Item;
import ch.killenberger.wowauctionhousebrowser.sqlite.DatabaseHelper;
import ch.killenberger.wowauctionhousebrowser.ui.AuctionsAdapter;

public class AuctionHouseService extends AsyncTask<String, Void, List<AuctionGroup>> {
    final UserSettings        userSettings = UserSettings.getInstance();
    final ApplicationSettings appSettings  = ApplicationSettings.getInstance();

    private Context        mContext;
    private ProgressDialog dialog;
    private RecyclerView   list;

    public AuctionHouseService(Context context, RecyclerView list){
        super();

        this.mContext = context;
        this.dialog   = new ProgressDialog(mContext);
        this.list     = list;
    }

    @Override
    protected void onPreExecute() {
        this.dialog.setMessage("Loading auctions...");
        this.dialog.show();
    }

    @Override
    protected List<AuctionGroup> doInBackground(String... strings) {
        final Locale locale           = appSettings.getLocale();
        final Region region           = userSettings.getRegion();
        final int    connectedRealmId = userSettings.getConnectedRealmId();
        final String endpoint = region.getHost() + "/data/wow/connected-realm/" + connectedRealmId + "/auctions?namespace="+ region.getDynamicNamespace() + "&locale=" + locale.toString() + "&access_token=" + ApplicationSettings.getInstance().getAccessToken().getToken();

        final String response = HttpGetClient.gzipCall(endpoint);

        List<AuctionGroup> result = new ArrayList<>();

        try {
            List<Auction> auctions = parseResponse(response);

            result = groupAuctions(auctions);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return result;
    }

    private List<AuctionGroup> groupAuctions(List<Auction> auctions) {
        final Map<Integer, AuctionGroup> groups = new HashMap<>();
        final DatabaseHelper             db     = new DatabaseHelper(appSettings.getApplicationContext());

        for(Auction a : auctions) {
            final int itemId = a.getItemId();

            if(!groups.containsKey(itemId)) {
                final Item item = db.getItemById(itemId);

                groups.put(itemId, new AuctionGroup(item));
            }

            groups.get(itemId).add(a);
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

        list.setAdapter(new AuctionsAdapter(auctions));
        list.setLayoutManager(new LinearLayoutManager(mContext));
    }

    private List<Auction> parseResponse(String resp) throws JsonProcessingException {
        final ObjectNode node = new ObjectMapper().readValue(resp, ObjectNode.class);
        final String auctions = node.get("auctions").toString();

        return Arrays.asList(new ObjectMapper().readValue(auctions,  Auction[].class));
    }
}
