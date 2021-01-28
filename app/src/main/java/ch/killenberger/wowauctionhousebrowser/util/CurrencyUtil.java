package ch.killenberger.wowauctionhousebrowser.util;

public class CurrencyUtil {

    private CurrencyUtil() { }

    public static long copperToSilver(final long copper) {
        return copper / 100;
    }

    public static long copperToGold(final long copper) {
        return copper / 100 / 100;
    }

    public static String formatCopperAmount(final long amount) {
        final long copper = amount %  100;
        final long silver = copperToSilver(amount) % 100;
        final long gold   = copperToGold(amount);

        return gold + "g " + silver + "s " + copper + "c";
    }
}
