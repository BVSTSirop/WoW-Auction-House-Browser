package ch.killenberger.wowauctionhousebrowser.enums;

public enum Region {
    US("us", "api.blizzard.com"),
    EU("eu", "api.blizzard.com"),
    KR("kr", "api.blizzard.com"),
    TW("tw", "api.blizzard.com"),
    CN("gateway", "battlenet.com.cn");

    private String identifier;
    private String host;

    private Region(final String identifier, final String host) {
        this.identifier = identifier;
        this.host       = "https://" + this.identifier + "." + host;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public String getHost() {
        return this.host;
    }

    public String getDynamicNamespace() {
        return "dynamic-" + this.getIdentifier();
    }

    public String getStaticNamespace() {
        return "static-" + this.getIdentifier();
    }
}
