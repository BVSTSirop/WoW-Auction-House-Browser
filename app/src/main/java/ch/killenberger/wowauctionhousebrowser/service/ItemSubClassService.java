package ch.killenberger.wowauctionhousebrowser.service;

import android.app.ProgressDialog;
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
import ch.killenberger.wowauctionhousebrowser.model.item.ItemSubClass;
import ch.killenberger.wowauctionhousebrowser.sqlite.DatabaseHelper;
import ch.killenberger.wowauctionhousebrowser.util.AlertUtil;

public class ItemSubClassService extends AsyncTask<String, Void, List<ItemSubClass>> {
    private final Context mContext;

    private ProgressDialog dialog;

    private Exception exception;

    public ItemSubClassService(Context context) {
        this.mContext = context;
    }

    @Override
    protected void onPreExecute() {
        this.dialog = ProgressDialog.show(mContext,"Downloading", "Downloading Item SubClasses...");
    }

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
            } catch (IOException e) {
                this.exception = e;

                break;
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

    @Override
    protected void onPostExecute(List<ItemSubClass> subClasses) {
        super.onPostExecute(subClasses);

        if (dialog.isShowing()) {
            dialog.dismiss();
        }

        if(exception != null) {
            AlertUtil.createAlertDialog(this.mContext, "Oops", this.mContext.getString(R.string.connection_failed_error_msg));
        }
    }
}
