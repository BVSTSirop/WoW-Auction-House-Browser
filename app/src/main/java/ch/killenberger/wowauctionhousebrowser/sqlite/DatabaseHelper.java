package ch.killenberger.wowauctionhousebrowser.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ch.killenberger.wowauctionhousebrowser.model.item.Item;
import ch.killenberger.wowauctionhousebrowser.model.item.ItemClass;
import ch.killenberger.wowauctionhousebrowser.model.item.ItemSubClass;

public class DatabaseHelper extends SQLiteOpenHelper {
    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 3;

    // Database Name
    private static final String DATABASE_NAME = "WOWAUCTIONBROWSER";

    // Table Names
    private static final String TABLE_ITEM           = "ITEM";
    private static final String TABLE_ITEM_CLASS     = "ITEM_CLASS";
    private static final String TABLE_ITEM_SUB_CLASS = "ITEM_SUB_CLASS";

    // Common column names
    private static final String COL_ID = "ID";
    private static final String COL_NAME     = "NAME";
    private static final String COL_CLASS_ID = "CLASS_ID";

    // ITEM Table - column names
    private static final String COL_ITEM_LEVEL = "LEVEL";

    // ITEM_SUB_CLASS Table - column names
    private static final String COL_SUB_CLASS_ID = "SUB_CLASS_ID";

    // Table Create Statements
    private static final String CREATE_TABLE_ITEM = "CREATE TABLE " +
            TABLE_ITEM + "(" +
            COL_ID + " INTEGER PRIMARY KEY," +
                COL_NAME         + " TEXT," +
                COL_ITEM_LEVEL   + " INTEGER," +
                COL_CLASS_ID     + " INTEGER," +
                COL_SUB_CLASS_ID + " INTEGER," +
                " FOREIGN KEY (" + COL_CLASS_ID + ") REFERENCES " + TABLE_ITEM_CLASS + " (ID)," +
                " FOREIGN KEY (" + COL_SUB_CLASS_ID + ") REFERENCES " + TABLE_ITEM_SUB_CLASS + " (ID))";

    // Tag table create statement
    private static final String CREATE_TABLE_ITEM_CLASS = "CREATE TABLE " +
            TABLE_ITEM_CLASS + "(" +
            COL_ID + " INTEGER PRIMARY KEY," +
                COL_NAME + " TEXT)";

    // Tag table create statement
    private static final String CREATE_TABLE_ITEM_SUB_CLASS = "CREATE TABLE " +
            TABLE_ITEM_SUB_CLASS + "(" +
            COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_SUB_CLASS_ID + " INTEGER," +
                COL_NAME         + " TEXT," +
                COL_CLASS_ID     + " INTEGER, FOREIGN KEY (" + COL_CLASS_ID + ") REFERENCES " + TABLE_ITEM_SUB_CLASS + " (ID))";

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
        values.put(COL_ID,           i.getId());
        values.put(COL_NAME,         i.getName());
        values.put(COL_ITEM_LEVEL,   i.getLevel());
        values.put(COL_CLASS_ID,     i.getClassId());
        values.put(COL_SUB_CLASS_ID, i.getSubClassId());

        return db.insert(TABLE_ITEM, null, values);
    }

    public void createItems(Collection<Item> items) {
        final SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            ContentValues values = new ContentValues();
            for (Item i : items) {
                if (i != null) {
                    values.put(COL_ID,           i.getId());
                    values.put(COL_NAME,         i.getName());
                    values.put(COL_ITEM_LEVEL,   i.getLevel());
                    values.put(COL_CLASS_ID,     i.getClassId());
                    values.put(COL_SUB_CLASS_ID, i.getSubClassId());

                    db.insert(TABLE_ITEM, null, values);

                    values.clear();
                }
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public List<Item> getItems() {
        final SQLiteDatabase db = this.getReadableDatabase();
        final List<Item> items       = new ArrayList<>();
        final String     selectQuery = "SELECT  * FROM " + TABLE_ITEM;

        final Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                final String name       = c.getString(c.getColumnIndex(COL_NAME));
                final int    id         = c.getInt((c.getColumnIndex(COL_ID)));
                final int    level      = c.getInt(c.getColumnIndex(COL_ITEM_LEVEL));
                final int    classId    = c.getInt((c.getColumnIndex(COL_CLASS_ID)));
                final int    subClassId = c.getInt((c.getColumnIndex(COL_SUB_CLASS_ID)));

                Item i = new Item();
                i.setId(id);
                i.setName(name);
                i.setLevel(level);
                i.setClassId(classId);
                i.setSubClassId(subClassId);

                items.add(i);
            } while (c.moveToNext());
        }

        return items;
    }

    public Item getItemById(final int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_ITEM + " WHERE " + COL_ID + " = " + id;

        Cursor c = db.rawQuery(selectQuery, null);

        if(c != null && c.moveToFirst() ) {
            final String name       = c.getString(c.getColumnIndex(COL_NAME));
            final int    level      = c.getInt(c.getColumnIndex(COL_ITEM_LEVEL));
            final int    classId    = c.getInt((c.getColumnIndex(COL_CLASS_ID)));
            final int    subClassId = c.getInt((c.getColumnIndex(COL_SUB_CLASS_ID)));

            Item i = new Item();
            i.setId(id);
            i.setName(name);
            i.setLevel(level);
            i.setClassId(classId);
            i.setSubClassId(subClassId);

            return i;
        }

        return new Item();
    }

    public int getHighestItemId() {
        final SQLiteDatabase db = this.getReadableDatabase();
        final String query = "SELECT * FROM " + TABLE_ITEM + " ORDER BY " + COL_ID + " DESC LIMIT 0, 1";

        Cursor c = db.rawQuery(query, null);

        if (c != null && c.moveToFirst()) {
            return c.getInt(c.getColumnIndex(COL_ID));
        }

        return -1;
    }

    public int getHighestItemClassId() {
        final SQLiteDatabase db = this.getReadableDatabase();
        final String query = "SELECT * FROM " + TABLE_ITEM_CLASS + " ORDER BY " + COL_ID + " DESC LIMIT 0, 1";

        Cursor c = db.rawQuery(query, null);

        if (c != null && c.moveToFirst()) {
            return c.getInt(c.getColumnIndex(COL_ID));
        }

        return -1;
    }

    public int getHighestItemSubClassId(final int parentId) {
        final SQLiteDatabase db = this.getReadableDatabase();
        final String query = "SELECT * FROM " + TABLE_ITEM_SUB_CLASS + " WHERE " + COL_CLASS_ID + " = " + parentId + " ORDER BY " + COL_SUB_CLASS_ID + " DESC LIMIT 0, 1";

        Cursor c = db.rawQuery(query, null);

        if (c != null && c.moveToFirst()) {
            return c.getInt(c.getColumnIndex(COL_SUB_CLASS_ID));
        }

        return -1;
    }

    public long createItemClass(ItemClass ic) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_ID,                 ic.getId());
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
                values.put(COL_ID, ic.getId());
                values.put(COL_NAME, ic.getName());

                db.insert(TABLE_ITEM_CLASS, null, values);

                values.clear();
            }

            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
        }
    }

    public List<ItemClass> getItemClasses() {
        final SQLiteDatabase db = this.getReadableDatabase();
        final List<ItemClass> classes     = new ArrayList<>();
        final String          selectQuery = "SELECT  * FROM " + TABLE_ITEM_CLASS;

        final Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if(c != null && c.moveToFirst() ) {
            do {
                ItemClass ic = new ItemClass();
                ic.setId(c.getInt((c.getColumnIndex(COL_ID))));
                ic.setName((c.getString(c.getColumnIndex(COL_NAME))));

                classes.add(ic);
            } while (c.moveToNext());
        }

        return classes;
    }

    public ItemClass getItemClassById(final int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_ITEM_CLASS + " WHERE " + COL_ID + " = " + id;

        Cursor c = db.rawQuery(selectQuery, null);
        if(c != null && c.moveToFirst() ) {
            ItemClass ic = new ItemClass();
            ic.setId(c.getInt((c.getColumnIndex(COL_ID))));
            ic.setName((c.getString(c.getColumnIndex(COL_NAME))));

            return ic;
        }

        return new ItemClass();
    }

    public long createItemSubClass(final ItemSubClass isc) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_ID,       isc.getId());
        values.put(COL_NAME,     isc.getName());
        values.put(COL_CLASS_ID, isc.getParentClassId());

        final long result = db.insert(TABLE_ITEM_SUB_CLASS, null, values);

        return result;
    }

    public void createItemSubClasses(final List<ItemSubClass> subClasses) {
        final SQLiteDatabase db = this.getWritableDatabase();

        db.beginTransaction();

        try {
            ContentValues values = new ContentValues();
            for(ItemSubClass isc : subClasses) {
                values.put(COL_SUB_CLASS_ID, isc.getId());
                values.put(COL_NAME,         isc.getName());
                values.put(COL_CLASS_ID,     isc.getParentClassId());

                db.insert(TABLE_ITEM_SUB_CLASS, null, values);

                values.clear();
            }

            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
        }
    }

    public List<ItemSubClass> getSubClassesByParentId(final int id) {
        final SQLiteDatabase  db          = this.getReadableDatabase();
        final List<ItemSubClass> subClasses  = new ArrayList<>();
        final String          selectQuery = "SELECT  * FROM " + TABLE_ITEM_SUB_CLASS + " WHERE " + COL_CLASS_ID + " = " + id;

        Cursor c = db.rawQuery(selectQuery, null);
        if(c != null && c.moveToFirst() ) {
            do {
                ItemSubClass isc = new ItemSubClass();
                isc.setId(c.getInt((c.getColumnIndex(COL_SUB_CLASS_ID))));
                isc.setParentClassId(id);
                isc.setName((c.getString(c.getColumnIndex(COL_NAME))));

                subClasses.add(isc);
            } while (c.moveToNext());
        }

        return subClasses;
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