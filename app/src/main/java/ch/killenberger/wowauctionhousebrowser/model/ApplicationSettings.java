package ch.killenberger.wowauctionhousebrowser.model;

import android.content.Context;

import java.util.Locale;

import ch.killenberger.wowauctionhousebrowser.model.oauth2.AccessToken;

public class ApplicationSettings {
    private static ApplicationSettings settings;

    private Locale      locale = Locale.getDefault();
    private AccessToken accessToken;
    private Context     applicationContext;

    private ApplicationSettings() { }

    public static ApplicationSettings getInstance() {
        if(settings == null) {
            settings = new ApplicationSettings();
        }

        return settings;
    }

    public void setAccessToken(final AccessToken accessToken) {
        this.accessToken = accessToken;
    }

    public AccessToken getAccessToken() {
        return this.accessToken;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public void setLocale(final Locale locale) {
        this.locale = locale;
    }

    public Context getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(Context applicationContext) {
        this.applicationContext = applicationContext;
    }
}
