package ch.killenberger.wowauctionhousebrowser.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;


public class AlertUtil {

    private AlertUtil() { }

    public static void createAlertDialog(final Context c, final String title, final String text) {
        new MaterialAlertDialogBuilder(c)
                .setTitle(title)
                .setMessage(text)
                .setPositiveButton("Okay", null)
                .show();
    }

    public static void createAlertDialog(final Context c, final String title, final String text, DialogInterface.OnClickListener listener) {
        new MaterialAlertDialogBuilder(c)
                .setTitle(title)
                .setMessage(text)
                .setPositiveButton("Okay", listener)
                .show();
    }
}
