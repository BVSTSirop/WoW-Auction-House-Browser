package ch.killenberger.wowauctionhousebrowser.service;

import android.content.Context;
import android.os.AsyncTask;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import ch.killenberger.wowauctionhousebrowser.R;
import ch.killenberger.wowauctionhousebrowser.client.HttpGetClient;
import ch.killenberger.wowauctionhousebrowser.enums.Region;
import ch.killenberger.wowauctionhousebrowser.model.ApplicationSettings;
import ch.killenberger.wowauctionhousebrowser.model.UserSettings;
import ch.killenberger.wowauctionhousebrowser.model.item.ItemClass;
import ch.killenberger.wowauctionhousebrowser.sqlite.DatabaseHelper;
import ch.killenberger.wowauctionhousebrowser.util.AlertUtil;

public class ItemClassUpdateService extends AsyncTask<String, Void, Boolean> {
    private final Context mContext;

    private Exception exception;

    public ItemClassUpdateService(final Context c) {
        this.mContext = c;
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        final ApplicationSettings appSettings   = ApplicationSettings.getInstance();
        final Locale          locale            = appSettings.getLocale();
        final Region          region            = UserSettings.getInstance().getRegion();
        final DatabaseHelper  db                = new DatabaseHelper(appSettings.getApplicationContext());

        final String          endpoint          = region.getHost() + "/data/wow/item-class/index?namespace="+ region.getStaticNamespace() + "&locale=" + locale.toString() + "&access_token=" + ApplicationSettings.getInstance().getAccessToken().getToken();

        try {
            final String    response = HttpGetClient.call(endpoint);
            final ItemClass ic       = parseResponse(response);

            if(ic.getId() == db.getHighestItemClassId()) {
                return false;
            }
        } catch (IOException e) {
            this.exception = e;
        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);

        if(exception != null) {
            AlertUtil.createAlertDialog(this.mContext, "Oops", this.mContext.getString(R.string.connection_failed_error_msg));
        }
    }

    private ItemClass parseResponse(String resp) throws JsonProcessingException {
        final ObjectNode node = new ObjectMapper().readValue(resp, ObjectNode.class);
        String results = node.get("item_classes").toString();

        ItemClass[] classes = new ObjectMapper().readValue(results,  ItemClass[].class);

        return classes[classes.length - 1];
    }
}
