package ch.killenberger.wowauctionhousebrowser.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.zip.GZIPInputStream;

import ch.killenberger.wowauctionhousebrowser.enums.Region;
import ch.killenberger.wowauctionhousebrowser.model.ApplicationSettings;

public class HttpGetClient {

    public static String call(final String endpoint) throws IOException {
        final URL url       = new URL(endpoint);
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type",  "application/json");
        connection.setRequestProperty("Accept",        "application/json" );

        connection.connect();

        try (InputStream content = connection.getInputStream()) {
            final BufferedReader in = new BufferedReader(new InputStreamReader(content));
            final StringBuilder result = new StringBuilder();

            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }

            connection.disconnect();

            return result.toString();
        }
    }

    public static String gzipCall(final String endpoint) throws IOException {
        final URL url       = new URL(endpoint);
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type",    "application/json");
        connection.setRequestProperty("Accept-Encoding", "gzip" );

        connection.connect();

        try (InputStream content = new GZIPInputStream(connection.getInputStream());) {
            final BufferedReader in = new BufferedReader(new InputStreamReader(content));
            final StringBuilder result = new StringBuilder();

            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }

            connection.disconnect();

            return result.toString();
        }
    }
}
