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
import ch.killenberger.wowauctionhousebrowser.model.item.ItemSubClass;
import ch.killenberger.wowauctionhousebrowser.sqlite.DatabaseHelper;

public class ItemSubClassService extends AsyncTask<String, Void, List<ItemSubClass>> {

    @Override
    protected List<ItemSubClass> doInBackground(String... strings) {
        final ApplicationSettings appSettings   = ApplicationSettings.getInstance();
        final UserSettings        userSettings  = UserSettings.getInstance();
        final Locale              locale        = appSettings.getLocale();
        final Region              region        = userSettings.getRegion();
        final DatabaseHelper      db            = new DatabaseHelper(appSettings.getApplicationContext());

        final List<ItemClass>     parentClasses = db.getItemClasses();
        final List<ItemSubClass>  result        = new ArrayList<>();

        int parentId;
        for(ItemClass parent : parentClasses) {
            parentId = parent.getId();

            final String endpoint = region.getHost() + "/data/wow/item-class/" + parentId + "?namespace="+ region.getStaticNamespace() + "&locale=" + locale.toString() + "&access_token=" + ApplicationSettings.getInstance().getAccessToken().getToken();

            try {
                final List<ItemSubClass> subClasses = parseResponse(HttpGetClient.call(endpoint));

                result.addAll(subClasses);

                db.createItemSubClasses(subClasses);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        db.close();

        return result;
    }

    private List<ItemSubClass> parseResponse(final String resp) throws JsonProcessingException {
        final ObjectNode node           = new ObjectMapper().readValue(resp, ObjectNode.class);
        final int        parentClassId  = node.get("class_id").asInt();
        final String     results        = node.get("item_subclasses").toString();

        final List<ItemSubClass> subClasses = Arrays.asList(new ObjectMapper().readValue(results,  ItemSubClass[].class));

        for(ItemSubClass sub : subClasses) {
            sub.setParentClassId(parentClassId);
        }

        return subClasses;
    }
}
