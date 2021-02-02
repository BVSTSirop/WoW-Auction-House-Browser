package ch.killenberger.wowauctionhousebrowser.service;

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
import ch.killenberger.wowauctionhousebrowser.model.item.ItemClass;
import ch.killenberger.wowauctionhousebrowser.sqlite.DatabaseHelper;

public class ItemClassService extends AsyncTask<String, Void, List<ItemClass>> {

    @Override
    protected List<ItemClass> doInBackground(String... strings) {
        final Locale          locale   = ApplicationSettings.getInstance().getLocale();
        final Region          region   = UserSettings.getInstance().getRegion();
        final String          endpoint = region.getHost() + "/data/wow/item-class/index?namespace="+ region.getStaticNamespace() + "&locale=" + locale.toString() + "&access_token=" + ApplicationSettings.getInstance().getAccessToken().getToken();
        final List<ItemClass> result   = new ArrayList<>();

        final String response = HttpGetClient.call(endpoint);

        try {
            result.addAll(parseResponse(response));

            DatabaseHelper db = new DatabaseHelper(ApplicationSettings.getInstance().getApplicationContext());
            db.createItemClasses(result);
            db.close();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return result;
    }

    private List<ItemClass> parseResponse(String resp) throws JsonProcessingException {
        final ObjectNode node = new ObjectMapper().readValue(resp, ObjectNode.class);
        String results = node.get("item_classes").toString();

        return Arrays.asList(new ObjectMapper().readValue(results,  ItemClass[].class));
    }
}
