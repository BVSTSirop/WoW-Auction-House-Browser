package ch.killenberger.wowauctionhousebrowser.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.Image;
import android.os.AsyncTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ch.killenberger.wowauctionhousebrowser.R;
import ch.killenberger.wowauctionhousebrowser.enums.Region;
import ch.killenberger.wowauctionhousebrowser.model.ApplicationSettings;
import ch.killenberger.wowauctionhousebrowser.model.UserSettings;
import ch.killenberger.wowauctionhousebrowser.runnable.ImageDownloadThread;
import ch.killenberger.wowauctionhousebrowser.sqlite.DatabaseHelper;
import ch.killenberger.wowauctionhousebrowser.util.AlertUtil;

public class ItemMediaService extends AsyncTask<String, Integer, Void> {
    private final static int MIN_IMAGES_PER_THREAD = 1000;

    private final ApplicationSettings appSettings = ApplicationSettings.getInstance();
    private final Locale              locale      = appSettings.getLocale();
    private final Region              region      = UserSettings.getInstance().getRegion();
    private final Context             mContext;

    private ProgressDialog dialog;
    private Exception      exception;

    public ItemMediaService(final Context c) {
        this.mContext = c;
    }

    @Override
    protected void onPreExecute() {
        this.dialog = ProgressDialog.show(this.mContext,"Downloading", "Fetching item media assets... This might take a couple of minutes depending on how many assets need to be fetched.");
    }

    @Override
    protected Void doInBackground(String... strings) {
        final DatabaseHelper db = new DatabaseHelper(ApplicationSettings.getInstance().getApplicationContext());
        final List<Integer>  ids        = db.getMissingItemMediaIds();

        List<ImageDownloadThread> generatedThreads = startDownloadThreads(MIN_IMAGES_PER_THREAD, ids);

        while(hasAliveThreads(generatedThreads)) {
            // still downloading
        }

        db.close();

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (dialog.isShowing()) {
            dialog.dismiss();
        }

        if(exception != null) {
            AlertUtil.createAlertDialog(this.mContext, "Oops", this.mContext.getString(R.string.connection_failed_error_msg));
        }
    }

    public boolean hasAliveThreads(final List<ImageDownloadThread> threads) {
        for(Thread t : threads) {
            if(t.isAlive()) {
                return true;
            }
        }

        return false;
    }

    private List<ImageDownloadThread> startDownloadThreads(final int minImagesPerThread, final List<Integer> ids) {
        final List<ImageDownloadThread> threads = new ArrayList<>();

        int threadsNeeded = ids.size() / minImagesPerThread;
        if(ids.size() % minImagesPerThread > 0) {
            threadsNeeded++;
        }

        System.out.println("Starting " + threadsNeeded + " threads to download " + ids.size() + " images");

        for(int i = 0; i < threadsNeeded; i++) {
            int startIndex = i * minImagesPerThread;

            int endIndex;
            if(startIndex + minImagesPerThread < ids.size()) {
                endIndex = startIndex + minImagesPerThread;
            } else {
                endIndex = ids.size() - 1;
            }

            ImageDownloadThread t = new ImageDownloadThread(this.region, this.locale, ids.subList(startIndex, endIndex));
            t.start();

            threads.add(t);
        }

        return threads;
    }
}
