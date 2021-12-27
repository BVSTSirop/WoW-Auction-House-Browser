package ch.killenberger.wowauctionhousebrowser.enums;

import android.graphics.Color;

public enum ItemQuality {
    POOR("Poor", "#ff9d9d9d"),
    COMMON("Common", "#ffffffff"),
    UNCOMMON("Uncommon", "#ff1eff00"),
    RARE("Rare", "#ff0070dd"),
    EPIC("Epic", "#ffa335ee"),
    LEGENDARY("Legendary", "#ffff8000"),
    ARTIFACT("Artifact", "#ffe6cc80"),
    HEIRLOOM("Heirloom", "#ffe6cc80"),
    WOW_TOKEN("WoW Token", "#ff00ccff");

    private String name;
    private int    color;
    ItemQuality(final String name, final String colorCode) {
        this.name      = name;
        this.color = Color.parseColor(colorCode);
    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }
}
