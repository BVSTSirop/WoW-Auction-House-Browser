package ch.killenberger.wowauctionhousebrowser.service;

import android.os.AsyncTask;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Base64;

import ch.killenberger.wowauctionhousebrowser.model.oauth2.AccessToken;

public class OAuth2Service extends AsyncTask<String, Void, AccessToken> {

    @Override
    protected AccessToken doInBackground(String... urls) {
        final String endpoint = "https://eu.battle.net/oauth/token";
        final String user     = "3478c563948b43849e053b7d629ac2e9";
        final String secret   = "b03xb6VD9S53XhKIJVDiuWd0KG3eeS2Y";

        try {
            final URL               url        = new URL(endpoint);
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            final String            encoding   = createBase64Credentials(user, secret);

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",  "application/x-www-form-urlencoded");
            connection.setRequestProperty("Authorization", "Basic " + encoding);
            connection.setRequestProperty( "Accept", "*/*" );
            connection.setConnectTimeout(3600);
            connection.setReadTimeout(3600);
            connection.setDoOutput(true);

            connection.connect();

            connection.getOutputStream().write("grant_type=client_credentials".getBytes());
            connection.getOutputStream().flush();
            connection.getOutputStream().close();

            try (InputStream content = connection.getInputStream()) {
                BufferedReader in = new BufferedReader(new InputStreamReader(content));
                String line;

                StringBuffer response = new StringBuffer();
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }

                connection.disconnect();

                return parseResponse(response.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String createBase64Credentials(final String user, final String secret) {
        return Base64.getEncoder().encodeToString((user + ":" + secret).getBytes());
    }

    private AccessToken parseResponse(final String resp) throws JsonProcessingException {
        return new ObjectMapper().readValue(resp, AccessToken.class);
    }
}
