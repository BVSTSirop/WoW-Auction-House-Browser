package ch.killenberger.wowauctionhousebrowser.service;

import android.os.AsyncTask;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import ch.killenberger.wowauctionhousebrowser.client.HttpGetClient;
import ch.killenberger.wowauctionhousebrowser.enums.Region;
import ch.killenberger.wowauctionhousebrowser.model.ApplicationSettings;
import ch.killenberger.wowauctionhousebrowser.model.Realm;
import ch.killenberger.wowauctionhousebrowser.model.UserSettings;

public class ConnectedRealmService extends AsyncTask<String, Void, Integer> {
    final UserSettings        userSettings = UserSettings.getInstance();
    final ApplicationSettings appSettings  = ApplicationSettings.getInstance();

    @Override
    protected Integer doInBackground(String... strings) {
        final Locale locale = appSettings.getLocale();
        final Region region = userSettings.getRegion();
        final Realm  realm  = userSettings.getRealm();
        final String endpoint    = region.getHost() + "/data/wow/search/connected-realm?namespace=" + region.getDynamicNamespace() + "&realms.name." + locale.toString() + "=" + realm.getName() + "&access_token=" + ApplicationSettings.getInstance().getAccessToken().getToken();

        final String response = HttpGetClient.call(endpoint);

        try {
            return parseResponse(response);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return -1;
    }

    private int parseResponse(String resp) throws JsonProcessingException {
        JsonNode mapper = new ObjectMapper().readTree(resp);

        return mapper.get("results").get(0).get("data").get("id").asInt();
    }
}
