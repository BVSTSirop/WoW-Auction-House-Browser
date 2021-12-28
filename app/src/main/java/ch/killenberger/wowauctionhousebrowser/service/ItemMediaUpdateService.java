package ch.killenberger.wowauctionhousebrowser.service;

import android.content.Context;
import android.os.AsyncTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import ch.killenberger.wowauctionhousebrowser.client.HttpGetClient;
import ch.killenberger.wowauctionhousebrowser.enums.Region;
import ch.killenberger.wowauctionhousebrowser.model.ApplicationSettings;
import ch.killenberger.wowauctionhousebrowser.model.UserSettings;
import ch.killenberger.wowauctionhousebrowser.sqlite.DatabaseHelper;

public class ItemMediaUpdateService extends AsyncTask<String, Integer, Boolean> {

    @Override
    protected Boolean doInBackground(String... strings) {
        final DatabaseHelper db             = new DatabaseHelper(ApplicationSettings.getInstance().getApplicationContext());
        final int            nrMissingMedia = db.getMissingItemMediaIds().size();
        db.close();

        if(nrMissingMedia == 0) {
            return false;
        }

        return true;
    }
}