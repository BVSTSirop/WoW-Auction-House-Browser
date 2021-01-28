package ch.killenberger.wowauctionhousebrowser;

import android.database.CursorIndexOutOfBoundsException;

import org.junit.Test;

import ch.killenberger.wowauctionhousebrowser.util.CurrencyUtil;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class CurrencyUtilTest {

    @Test
    public void copperToSilver_sub_one() {
        final int copper = 94;
        assertEquals(0, CurrencyUtil.copperToSilver(copper));
    }

    @Test
    public void copperToSilver_sub_10() {
        final int copper = 814;
        assertEquals(8, CurrencyUtil.copperToSilver(copper));
    }

    @Test
    public void copperToSilver_sub_100() {
        final int copper = 5837;
        assertEquals(58, CurrencyUtil.copperToSilver(copper));
    }

    @Test
    public void copperToSilver_above_100() {
        final int copper = 15837;
        assertEquals(158, CurrencyUtil.copperToSilver(copper));
    }

    @Test
    public void copperToGold_sub_one() {
        final int copper = 1045;
        assertEquals(0, CurrencyUtil.copperToGold(copper));
    }

    @Test
    public void copperToGold_sub_10() {
        final int copper = 85045;
        assertEquals(8, CurrencyUtil.copperToGold(copper));
    }

    @Test
    public void copperToGold_sub_100() {
        final int copper = 759289;
        assertEquals(75, CurrencyUtil.copperToGold(copper));
    }

    @Test
    public void formatCopperAmount() {
        final int copper = 759289;

        assertEquals("75g 92s 89c", CurrencyUtil.formatCopperAmount(copper));
    }

    @Test
    public void formatCopperAmount_zero() {
        final int copper = 0;

        assertEquals("0g 0s 0c", CurrencyUtil.formatCopperAmount(copper));
    }

    @Test
    public void formatCopperAmount_sub_1g() {
        final int copper = 9289;

        assertEquals("0g 92s 89c", CurrencyUtil.formatCopperAmount(copper));
    }

    @Test
    public void formatCopperAmount_above_1000g() {
        final int copper = 100759289;

        assertEquals("10075g 92s 89c", CurrencyUtil.formatCopperAmount(copper));
    }
}