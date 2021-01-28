package ch.killenberger.wowauctionhousebrowser.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ch.killenberger.wowauctionhousebrowser.model.auction.Auction;
import ch.killenberger.wowauctionhousebrowser.model.item.Item;
import ch.killenberger.wowauctionhousebrowser.model.item.ItemClass;

public class DatabaseHelper extends SQLiteOpenHelper {
    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 3;

    // Database Name
    private static final String DATABASE_NAME = "WOWAUCTIONBROWSER";

    // Table Names
    private static final String TABLE_ITEM = "ITEM";
    private static final String TABLE_ITEM_CLASS = "ITEM_CLASS";
    private static final String TABLE_ITEM_SUB_CLASS = "ITEM_SUB_CLASS";

    // Common column names
    private static final String KEY_ID       = "ID";
    private static final String COL_NAME     = "NAME";
    private static final String COL_CLASS_ID = "CLASS_ID";

    // ITEM Table - column names
    private static final String COL_ITEM_LEVEL = "LEVEL";

    // ITEM_SUB_CLASS Table - column names
    private static final String COL_ITEM_SUB_CLASS_ID = "SUB_CLASS_ID";

    // Table Create Statements
    private static final String CREATE_TABLE_ITEM = "CREATE TABLE " +
            TABLE_ITEM + "(" +
                KEY_ID         + " INTEGER PRIMARY KEY," +
                COL_NAME       + " TEXT," +
                COL_ITEM_LEVEL + " INTEGER," +
                COL_CLASS_ID   + " INTEGER, FOREIGN KEY (" + COL_CLASS_ID + ") REFERENCES " + TABLE_ITEM_CLASS + " (ID))";

    // Tag table create statement
    private static final String CREATE_TABLE_ITEM_CLASS = "CREATE TABLE " +
            TABLE_ITEM_CLASS + "(" +
                KEY_ID   + " INTEGER PRIMARY KEY," +
                COL_NAME + " TEXT)";

    // Tag table create statement
    private static final String CREATE_TABLE_ITEM_SUB_CLASS = "CREATE TABLE " +
            TABLE_ITEM_SUB_CLASS + "(" +
                KEY_ID                + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_ITEM_SUB_CLASS_ID + " INTEGER," +
                COL_NAME              + " TEXT," +
                COL_CLASS_ID          + " INTEGER, FOREIGN KEY (" + COL_CLASS_ID + ") REFERENCES " + TABLE_ITEM_SUB_CLASS + " (ID))";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL(CREATE_TABLE_ITEM_CLASS);
        db.execSQL(CREATE_TABLE_ITEM_SUB_CLASS);
        db.execSQL(CREATE_TABLE_ITEM);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        resetDatabase();
    }

    public long createItem(Item i) {
        if(i == null) {
            return -1;
        }

        SQLiteDatabase db = this.getWritableDatabase();

        System.out.println("Creating item: " + i);

        ContentValues values = new ContentValues();
        values.put(KEY_ID,         i.getId());
        values.put(COL_NAME,       i.getName());
        values.put(COL_ITEM_LEVEL, i.getLevel());
        values.put(COL_CLASS_ID,   i.getItemClassId());

        final long result = db.insert(TABLE_ITEM, null, values);

        return result;
    }

    public void createItems(Collection<Item> items) {
        final SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            ContentValues values = new ContentValues();
            for (Item i : items) {
                if (i != null) {
                    values.put(KEY_ID,         i.getId());
                    values.put(COL_NAME,       i.getName());
                    values.put(COL_ITEM_LEVEL, i.getLevel());
                    values.put(COL_CLASS_ID,   i.getItemClassId());

                    db.insert(TABLE_ITEM, null, values);

                    values.clear();
                }
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public List<Item> getAllItems() {
        final SQLiteDatabase db = this.getReadableDatabase();
        final List<Item> items       = new ArrayList<>();
        final String     selectQuery = "SELECT  * FROM " + TABLE_ITEM;

        final Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Item i = new Item();
                i.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                i.setName((c.getString(c.getColumnIndex(COL_NAME))));
                i.setLevel(c.getInt(c.getColumnIndex(COL_ITEM_LEVEL)));

                items.add(i);
            } while (c.moveToNext());
        }

        return items;
    }

    public Item getItemById(final int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_ITEM + " WHERE " + KEY_ID + " = " + id;

        Cursor c = db.rawQuery(selectQuery, null);

        if(c != null && c.moveToFirst() ) {
            final Item i = new Item();

            i.setId(c.getInt(c.getColumnIndex(KEY_ID)));
            i.setName(c.getString(c.getColumnIndex(COL_NAME)));
            i.setLevel(c.getInt(c.getColumnIndex(COL_ITEM_LEVEL)));
            i.setItemClassId(c.getInt(c.getColumnIndex(COL_CLASS_ID)));

            return i;
        }

        return new Item();
    }

    public long createItemClass(ItemClass ic) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID,                 ic.getId());
        values.put(COL_NAME,               ic.getName());

        final long result = db.insert(TABLE_ITEM_CLASS, null, values);

        return result;
    }

    public void createItemClasses(Collection<ItemClass> classes) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            ContentValues values = new ContentValues();
            for (ItemClass ic : classes) {
                values.put(KEY_ID, ic.getId());
                values.put(COL_NAME, ic.getName());

                db.insert(TABLE_ITEM_CLASS, null, values);

                values.clear();
            }

            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
        }
    }

    public List<ItemClass> getAllItemClasses() {
        final SQLiteDatabase db = this.getReadableDatabase();
        final List<ItemClass> classes     = new ArrayList<>();
        final String          selectQuery = "SELECT  * FROM " + TABLE_ITEM_CLASS;

        final Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                ItemClass ic = new ItemClass();
                ic.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                ic.setName((c.getString(c.getColumnIndex(COL_NAME))));

                classes.add(ic);
            } while (c.moveToNext());
        }

        return classes;
    }

    public ItemClass getItemClassById(final int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_ITEM_CLASS + " WHERE " + KEY_ID + " = " + id;

        Cursor c = db.rawQuery(selectQuery, null);
        if(c != null && c.moveToFirst() ) {
            ItemClass ic = new ItemClass();
            ic.setId(c.getInt((c.getColumnIndex(KEY_ID))));
            ic.setName((c.getString(c.getColumnIndex(COL_NAME))));

            return ic;
        }

        return new ItemClass();
    }

    public long createItemSubClass(int parentClassId, ItemClass ic) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID,       ic.getId());
        values.put(COL_NAME,     ic.getName());
        values.put(COL_CLASS_ID, parentClassId);

        final long result = db.insert(TABLE_ITEM_SUB_CLASS, null, values);

        return result;
    }

    public void createItemSubClasses(final int parentClassId, final List<ItemClass> subClasses) {
        final SQLiteDatabase db = this.getWritableDatabase();

        db.beginTransaction();

        try {
            ContentValues values = new ContentValues();
            for(ItemClass subClass : subClasses) {
                values.put(COL_ITEM_SUB_CLASS_ID, subClass.getId());
                values.put(COL_NAME,              subClass.getName());
                values.put(COL_CLASS_ID,          parentClassId);

                db.insert(TABLE_ITEM_SUB_CLASS, null, values);

                values.clear();
            }

            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
        }
    }

    public void close() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    private void dropTables() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEM_SUB_CLASS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEM_CLASS);
    }

    public void resetDatabase() {
        dropTables();

        onCreate(this.getWritableDatabase());
    }
}