package ch.killenberger.wowauctionhousebrowser.runnable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import ch.killenberger.wowauctionhousebrowser.client.HttpGetClient;
import ch.killenberger.wowauctionhousebrowser.enums.Region;
import ch.killenberger.wowauctionhousebrowser.model.ApplicationSettings;
import ch.killenberger.wowauctionhousebrowser.sqlite.DatabaseHelper;

public class ImageDownloadThread extends Thread {
    private static final String VALUE_START = "\"value\":\"";
    private static final String VALUE_END   = "\",";

    private final List<Integer> ids;
    private final Region        region;
    private final Locale        locale;

    public ImageDownloadThread(final Region region, final Locale locale, final List<Integer> imageIds) {
        this.region = region;
        this.locale = locale;
        this.ids    = imageIds;
    }

    @Override
    public void run() {
        final DatabaseHelper db = new DatabaseHelper(ApplicationSettings.getInstance().getApplicationContext());

        for(int id : ids) {
            String response = null;
            try {
                response = HttpGetClient.call(assembleURl(id));
            } catch (IOException e) {
                e.printStackTrace();

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
