package ch.killenberger.wowauctionhousebrowser.service;

import android.os.AsyncTask;

import ch.killenberger.wowauctionhousebrowser.model.ApplicationSettings;
import ch.killenberger.wowauctionhousebrowser.sqlite.DatabaseHelper;

public class ItemMediaUpdateService extends AsyncTask<String, Integer, Boolean> {

    private static final int[] NO_MEDIA_ITEMS = new int[]{58170, 111076, 114960, 115603, 115854, 117583, 117784, 118226, 119809, 119811, 119812, 119816, 119818, 119820, 122334, 130318, 137675, 159894, 159895, 161973, 161974, 174800};

    @Override
    protected Boolean doInBackground(String... strings) {
        final DatabaseHelper db             = new DatabaseHelper(ApplicationSettings.getInstance().getApplicationContext());
        final int            nrMissingMedia = db.getMissingItemMediaIds().size();
        db.close();

        return nrMissingMedia - NO_MEDIA_ITEMS.length != 0;
    }
}