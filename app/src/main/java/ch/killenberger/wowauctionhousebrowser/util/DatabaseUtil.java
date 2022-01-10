package ch.killenberger.wowauctionhousebrowser.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ch.killenberger.wowauctionhousebrowser.BuildConfig;

public class DatabaseUtil {
    public static String DB_PATH = "/data/data/" + BuildConfig.APPLICATION_ID + "/databases/";
    public static String DB_NAME = "wow-auction-house-browser.db";

    public static void setDefaultDataBase(Context context) {
        System.out.println("Copying default database");

        try {
            final String       outFileName = DB_PATH + DB_NAME;
            final InputStream  is          = context.getAssets().open(DB_NAME);
            final OutputStream os          = new FileOutputStream(outFileName);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }

            os.flush();
            os.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkDataBase(final Context context) {
        System.out.println("checkDataBase: Enter");
        SQLiteDatabase checkDB = null;

        try {
            checkDB = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);
            checkDB.close();
            System.out.println("checkDataBase: loaded");
        } catch (SQLiteException e) {
            System.out.println("checkDataBase: SQLiteException---" + e);
            e.printStackTrace();
            DatabaseUtil.setDefaultDataBase(context);
        } catch (Exception e) {
            System.out.println("checkDataBase: Exception " + e);
            e.printStackTrace();
            DatabaseUtil.setDefaultDataBase(context);
        }
        return checkDB != null;
    }
}
