package ch.killenberger.wowauctionhousebrowser.service;

import android.content.Context;
import android.os.AsyncTask;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import ch.killenberger.wowauctionhousebrowser.R;
import ch.killenberger.wowauctionhousebrowser.client.HttpGetClient;
import ch.killenberger.wowauctionhousebrowser.enums.Region;
import ch.killenberger.wowauctionhousebrowser.model.ApplicationSettings;
import ch.killenberger.wowauctionhousebrowser.model.Realm;
import ch.killenberger.wowauctionhousebrowser.model.UserSettings;
import ch.killenberger.wowauctionhousebrowser.util.AlertUtil;

public class ConnectedRealmService extends AsyncTask<String, Void, Integer> {
    private final UserSettings        userSettings = UserSettings.getInstance();
    private final ApplicationSettings appSettings  = ApplicationSettings.getInstance();

    private Context mContext;
    private Exception exception; // Used for exception handling

    public ConnectedRealmService(final Context c) {
        this.mContext = c;
    }

    @Override
    protected Integer doInBackground(String... strings) {
        final Locale locale = appSettings.getLocale();
        final Region region = userSettings.getRegion();
        final Realm  realm  = userSettings.getRealm();
        final String endpoint    = region.getHost() + "/data/wow/search/connected-realm?namespace=" + region.getDynamicNamespace() + "&realms.name." + locale.toString() + "=" + realm.getName() + "&access_token=" + ApplicationSettings.getInstance().getAccessToken().getToken();

        final String response;
        try {
            response = HttpGetClient.call(endpoint);

            return parseResponse(response);
        } catch (IOException e) {
            this.exception = e;
        }

        return -1;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);

        if(exception != null) {
            AlertUtil.createAlertDialog(mContext, "Oops", this.mContext.getString(R.string.connection_failed_error_msg));
        }
    }

    private int parseResponse(String resp) throws JsonProcessingException {
        JsonNode mapper = new ObjectMapper().readTree(resp);

        return mapper.get("results").get(0).get("data").get("id").asInt();
    }
}
