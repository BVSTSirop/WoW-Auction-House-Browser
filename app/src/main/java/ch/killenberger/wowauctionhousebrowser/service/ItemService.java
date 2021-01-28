package ch.killenberger.wowauctionhousebrowser.service;

import android.os.AsyncTask;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import ch.killenberger.wowauctionhousebrowser.client.HttpGetClient;
import ch.killenberger.wowauctionhousebrowser.enums.Region;
import ch.killenberger.wowauctionhousebrowser.model.ApplicationSettings;
import ch.killenberger.wowauctionhousebrowser.model.item.Item;
import ch.killenberger.wowauctionhousebrowser.model.UserSettings;
import ch.killenberger.wowauctionhousebrowser.sqlite.DatabaseHelper;

public class ItemService extends AsyncTask<String, Void, List<Item>> {
    private final ApplicationSettings appSettings = ApplicationSettings.getInstance();
    private final List<Item>          items       = new ArrayList<>();
    private final Locale              locale      = appSettings.getLocale();
    private final Region              region      = UserSettings.getInstance().getRegion();

    private int currentId = 1;

    @Override
    protected List<Item> doInBackground(String... strings) {
        final List<Item>     result = new ArrayList<>();

        List<Item> response;
        while((response = parseResponse(HttpGetClient.gzipCall(assembleURl(currentId)))).size() > 0) {
            result.addAll(response);

            currentId = response.get(response.size() - 1).getId() + 1;
        }

        final DatabaseHelper db = new DatabaseHelper(appSettings.getApplicationContext());
        db.createItems(result);
        db.close();

        return result;
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
