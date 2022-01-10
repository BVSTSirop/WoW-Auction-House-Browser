package ch.killenberger.wowauctionhousebrowser.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import ch.killenberger.wowauctionhousebrowser.R;
import ch.killenberger.wowauctionhousebrowser.client.HttpGetClient;
import ch.killenberger.wowauctionhousebrowser.enums.Region;
import ch.killenberger.wowauctionhousebrowser.model.ApplicationSettings;
import ch.killenberger.wowauctionhousebrowser.model.UserSettings;
import ch.killenberger.wowauctionhousebrowser.sqlite.DatabaseHelper;
import ch.killenberger.wowauctionhousebrowser.util.AlertUtil;

public class ItemMediaService extends AsyncTask<String, Integer, Void> {
    private static final String VALUE_START = "\"value\":\"";
    private static final String VALUE_END   = "\",";

    private final Context        mContext;
    private       ProgressDialog dialog;

    private final ApplicationSettings appSettings = ApplicationSettings.getInstance();
    private final Locale              locale      = appSettings.getLocale();
    private final Region              region      = UserSettings.getInstance().getRegion();

    private Exception exception;

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

        for(int id : ids) {
            String response = "";
            try {
                response = HttpGetClient.call(assembleURl(id));
            } catch (IOException e) {
                this.exception = e;

                break;
            }

            try {
                String mediaLink = response.substring(response.indexOf(VALUE_START) + VALUE_START.length());
                mediaLink = mediaLink.substring(0, mediaLink.indexOf(VALUE_END));

                db.createItemMedia(id, downloadImage(mediaLink));
            } catch (StringIndexOutOfBoundsException e) {
                System.out.println("No media found for item with: " + id);
            }
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

    private String assembleURl(final int id) {
        return this.region.getHost() + "/data/wow/media/item/" + id + "?namespace=" + this.region.getStaticNamespace() + "&locale=" + this.locale.toString() + "&access_token=" + ApplicationSettings.getInstance().getAccessToken().getToken();
    }

    private byte[] downloadImage(final String link) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            URL url = new URL(link);

            try (InputStream is = url.openStream ()) {
                byte[] byteChunk = new byte[4096];
                int n;

                while ((n = is.read(byteChunk)) > 0) {
                    baos.write(byteChunk, 0, n);
                }

                return baos.toByteArray();
            } catch (IOException e) {
                System.err.printf ("Failed while reading bytes from %s: %s", url.toExternalForm(), e.getMessage());
                e.printStackTrace ();
            }
        } catch (IOException e) {
            System.err.printf ("Failed while reading bytes from %s: %s", link, e.getMessage());
            e.printStackTrace ();
            // Perform any other exception handling that's appropriate.
        }

        return new byte[]{};
    }
}