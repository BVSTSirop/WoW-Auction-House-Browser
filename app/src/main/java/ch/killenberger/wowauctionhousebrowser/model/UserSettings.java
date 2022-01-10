package ch.killenberger.wowauctionhousebrowser.model;

import ch.killenberger.wowauctionhousebrowser.enums.Region;

public class UserSettings {

    private static UserSettings settings;

    private Region             region;
    private Realm              realm;
    private int                connectedRealmId;

    private UserSettings() { }

    public static UserSettings getInstance() {
        if(settings == null) {
            settings = new UserSettings();
        }

        return settings;
    }

    public Region getRegion() {
        return this.region;
    }

    public void setRegion(final Region region) {
        this.region = region;
    }

    public Realm getRealm() {
        return this.realm;
    }

    public void setRealm(final Realm realm) {
        this.realm = realm;
    }

    public int getConnectedRealmId() {
        return connectedRealmId;
    }

    public void setConnectedRealmId(int connectedRealmId) {
        this.connectedRealmId = connectedRealmId;
    }
}
