package ch.killenberger.wowauctionhousebrowser.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import ch.killenberger.wowauctionhousebrowser.client.HttpGetClient;
import ch.killenberger.wowauctionhousebrowser.enums.Region;
import ch.killenberger.wowauctionhousebrowser.model.ApplicationSettings;
import ch.killenberger.wowauctionhousebrowser.model.UserSettings;
import ch.killenberger.wowauctionhousebrowser.model.item.Item;
import ch.killenberger.wowauctionhousebrowser.sqlite.DatabaseHelper;

public class ItemUpdateService extends AsyncTask<Integer, Integer, Boolean> {
    private final ApplicationSettings appSettings = ApplicationSettings.getInstance();
    private final Locale              locale      = appSettings.getLocale();
    private final Region              region      = UserSettings.getInstance().getRegion();

    private Context context;

    @Override
    protected Boolean doInBackground(Integer... strings) {
        final DatabaseHelper db     = new DatabaseHelper(appSettings.getApplicationContext());

        final String endpoint = this.region.getHost() + "/data/wow/search/item?namespace=" + this.region.getStaticNamespace() + "&_pageSize=1&_page=1&orderby=id:desc&locale=" + this.locale.toString() + "&access_token=" + ApplicationSettings.getInstance().getAccessToken().getToken();

        try {
            Item item = parseResponse(HttpGetClient.call(endpoint));

            if(item.getId() == db.getHighestItemId()) {
                return false;
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return true;
    }

    private Item parseResponse(String resp) throws JsonProcessingException {
        final ObjectNode node = new ObjectMapper().readValue(resp, ObjectNode.class);
        final String     results = node.get("results").toString();

        return new ObjectMapper().readValue(results,  Item[].class)[0];
    }
}
