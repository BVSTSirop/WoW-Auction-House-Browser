package ch.killenberger.wowauctionhousebrowser.service;

import android.os.AsyncTask;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
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

public class ItemSubClassUpdateService extends AsyncTask<String, Void, Boolean> {

    @Override
    protected Boolean doInBackground(String... strings) {
        final ApplicationSettings appSettings   = ApplicationSettings.getInstance();
        final UserSettings        userSettings  = UserSettings.getInstance();
        final Locale              locale        = appSettings.getLocale();
        final Region              region        = userSettings.getRegion();
        final DatabaseHelper      db            = new DatabaseHelper(appSettings.getApplicationContext());
        final List<ItemClass> parentClasses     = db.getItemClasses();

        for(ItemClass parent : parentClasses) {
            final int parentId = parent.getId();
            final String endpoint = region.getHost() + "/data/wow/item-class/" + parentId + "?namespace=" + region.getStaticNamespace() + "&locale=" + locale.toString() + "&access_token=" + ApplicationSettings.getInstance().getAccessToken().getToken();

            try {
                ItemSubClass subClass = parseResponse(HttpGetClient.call(endpoint));

                if (subClass.getId() < db.getHighestItemSubClassId(subClass.getParentClassId())) {
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    private ItemSubClass parseResponse(final String resp) throws JsonProcessingException {
        final ObjectNode node           = new ObjectMapper().readValue(resp, ObjectNode.class);
        final int        parentClassId  = node.get("class_id").asInt();
        final String     results        = node.get("item_subclasses").toString();

        ItemSubClass[] subClasses = new ObjectMapper().readValue(results,  ItemSubClass[].class);
        ItemSubClass   subClass   = subClasses[subClasses.length - 1];
        subClass.setParentClassId(parentClassId);

        return subClass;
    }
}
