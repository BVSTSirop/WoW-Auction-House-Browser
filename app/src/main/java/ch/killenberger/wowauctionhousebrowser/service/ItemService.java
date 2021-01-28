package ch.killenberger.wowauctionhousebrowser.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ch.killenberger.wowauctionhousebrowser.client.HttpGetClient;
import ch.killenberger.wowauctionhousebrowser.enums.Region;
import ch.killenberger.wowauctionhousebrowser.model.ApplicationSettings;
import ch.killenberger.wowauctionhousebrowser.model.auction.AuctionGroup;
import ch.killenberger.wowauctionhousebrowser.model.item.Item;
import ch.killenberger.wowauctionhousebrowser.model.UserSettings;
import ch.killenberger.wowauctionhousebrowser.sqlite.DatabaseHelper;
import ch.killenberger.wowauctionhousebrowser.ui.AuctionsAdapter;

public class ItemService extends AsyncTask<String, Integer, List<Item>> {
    private final ApplicationSettings appSettings = ApplicationSettings.getInstance();
    private final List<Item>          items       = new ArrayList<>();
    private final Locale              locale      = appSettings.getLocale();
    private final Region              region      = UserSettings.getInstance().getRegion();
    private ProgressDialog      dialog;

    private int currentId = 1;
    private static int MAX_PROGRESS = 184000;
    private Context context;

    public ItemService(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        this.dialog = ProgressDialog.show(context,"Downloading", "Fetching item assets...");
    }

    @Override
    protected List<Item> doInBackground(String... strings) {
        final List<Item>     result = new ArrayList<>();

        List<Item> response;
        while((response = parseResponse(HttpGetClient.call(assembleURl(currentId)))).size() > 0) {
            result.addAll(response);

            currentId = response.get(response.size() - 1).getId() + 1;
        }

        final DatabaseHelper db = new DatabaseHelper(appSettings.getApplicationContext());
        db.createItems(result);
        db.close();

        return result;
    }

    @Override
    protected void onPostExecute(List<Item> items) {
        super.onPostExecute(items);

        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private List<Item> parseResponse(String resp) {
        final ObjectNode node;
        try {
            node = new ObjectMapper().readValue(resp, ObjectNode.class);
            final String     results = node.get("results").toString();

            return Arrays.asList(new ObjectMapper().readValue(results,  Item[].class));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    private String assembleURl(final int id) {
       return this.region.getHost() + "/data/wow/search/item?namespace=" + this.region.getStaticNamespace() + "&_pageSize=1000&orderby=id:asc&id=[" + currentId + ",]&locale=" + this.locale.toString() + "&access_token=" + ApplicationSettings.getInstance().getAccessToken().getToken();
    }
}
