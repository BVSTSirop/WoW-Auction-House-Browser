package ch.killenberger.wowauctionhousebrowser.service;

import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import ch.killenberger.wowauctionhousebrowser.client.HttpGetClient;
import ch.killenberger.wowauctionhousebrowser.enums.Region;
import ch.killenberger.wowauctionhousebrowser.model.ApplicationSettings;
import ch.killenberger.wowauctionhousebrowser.model.Realm;
import ch.killenberger.wowauctionhousebrowser.model.UserSettings;
import ch.killenberger.wowauctionhousebrowser.model.oauth2.AccessToken;

public class RealmService extends AsyncTask<String, Void, List<Realm>> {

    @Override
    protected List<Realm> doInBackground(String... strings) {
        final Locale locale   = ApplicationSettings.getInstance().getLocale();
        final Region region   = UserSettings.getInstance().getRegion();
        final String endpoint = region.getHost() + "/data/wow/realm/index?namespace="+ region.getDynamicNamespace() + "&locale=" + locale.toString() + "&access_token=" + ApplicationSettings.getInstance().getAccessToken().getToken();

        final String result = HttpGetClient.call(endpoint);

        try {
            return parseResponse(result);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    private List<Realm> parseResponse(String resp) throws JsonProcessingException {
        final ObjectNode node = new ObjectMapper().readValue(resp, ObjectNode.class);
        String realms = node.get("realms").toString();

        return Arrays.asList(new ObjectMapper().readValue(realms,  Realm[].class));
    }
}
