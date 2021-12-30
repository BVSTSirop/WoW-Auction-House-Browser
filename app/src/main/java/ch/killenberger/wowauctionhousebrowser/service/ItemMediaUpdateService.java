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

    private static final int[] NO_MEDIA_ITEMS = new int[]{58170, 111076, 114960, 115603, 115854, 117583, 117784, 118226, 119809, 119811, 119812, 119816, 119818, 119820, 122334, 130318, 137675, 159894, 159895, 161973, 161974, 174800};

    @Override
    protected Boolean doInBackground(String... strings) {
        final DatabaseHelper db             = new DatabaseHelper(ApplicationSettings.getInstance().getApplicationContext());
        final int            nrMissingMedia = db.getMissingItemMediaIds().size();
        db.close();

        if(nrMissingMedia - NO_MEDIA_ITEMS.length == 0) {
            return false;
        }

        return true;
    }
}