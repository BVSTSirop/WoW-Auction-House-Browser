package ch.killenberger.wowauctionhousebrowser.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ch.killenberger.wowauctionhousebrowser.BuildConfig;
import ch.killenberger.wowauctionhousebrowser.enums.ItemQuality;
import ch.killenberger.wowauctionhousebrowser.model.item.Item;
import ch.killenberger.wowauctionhousebrowser.model.item.ItemClass;
import ch.killenberger.wowauctionhousebrowser.model.item.ItemSubClass;
import ch.killenberger.wowauctionhousebrowser.util.FileUtil;

public class DatabaseHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 3;

    // Database Name
    private static final String DB_DIR_PATH = "/data/data/" + BuildConfig.APPLICATION_ID + "/databases/";
    private static final String DB_NAME     = "wow-auction-house-browser.db";
    private static       String DB_PATH     = DB_DIR_PATH + DB_NAME;
    private static       String OLD_DB_PATH = DB_DIR_PATH + "old_" + DB_NAME;



    // Table Names
    private static final String TABLE_ITEM           = "ITEM";
    private static final String TABLE_ITEM_CLASS     = "ITEM_CLASS";
    private static final String TABLE_ITEM_SUB_CLASS = "ITEM_SUB_CLASS";
    private static final String TABLE_ITEM_MEDIA     = "ITEM_MEDIA";

    // Common column names
    private static final String COL_ID = "ID";
    private static final String COL_NAME     = "NAME";
    private static final String COL_CLASS_ID = "CLASS_ID";
    private static final String COL_ITEM_ID  = "ITEM_ID";
    private static final String COL_QUALITY  = "QUALITY";
    private static final String COL_IMAGE    = "IMAGE";

    // ITEM Table - column names
    private static final String COL_ITEM_LEVEL = "LEVEL";

    // ITEM_SUB_CLASS Table - column names
    private static final String COL_SUB_CLASS_ID = "SUB_CLASS_ID";

    // Table Create Statements
    private static final String CREATE_TABLE_ITEM = "CREATE TABLE IF NOT EXISTS " +
            TABLE_ITEM + "(" +
                COL_ID           + " INTEGER PRIMARY KEY," +
                COL_NAME         + " TEXT," +
                COL_ITEM_LEVEL   + " INTEGER," +
                COL_CLASS_ID     + " INTEGER," +
                COL_SUB_CLASS_ID + " INTEGER," +
                COL_QUALITY      + " TEXT," +
                " FOREIGN KEY (" + COL_CLASS_ID + ") REFERENCES " + TABLE_ITEM_CLASS  + " (" + COL_ID +")," +
                " FOREIGN KEY (" + COL_SUB_CLASS_ID + ") REFERENCES " + TABLE_ITEM_SUB_CLASS + " (" + COL_ID +"))";

    // Tag table create statement
    private static final String CREATE_TABLE_ITEM_CLASS = "CREATE TABLE IF NOT EXISTS " +
            TABLE_ITEM_CLASS + "(" +
            COL_ID + " INTEGER PRIMARY KEY," +
                COL_NAME + " TEXT)";

    // Tag table create statement
    private static final String CREATE_TABLE_ITEM_SUB_CLASS = "CREATE TABLE IF NOT EXISTS " +
            TABLE_ITEM_SUB_CLASS + "(" +
                COL_ID           + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_SUB_CLASS_ID + " INTEGER," +
                COL_NAME         + " TEXT," +
                COL_CLASS_ID     + " INTEGER, " +
                "FOREIGN KEY (" + COL_CLASS_ID + ") REFERENCES " + TABLE_ITEM_SUB_CLASS + " (" + COL_ID +"))";

    private static final String CREATE_TABLE_ITEM_MEDIA = "CREATE TABLE IF NOT EXISTS " +
        TABLE_ITEM_MEDIA + "(" +
            COL_ITEM_ID + " INTEGER PRIMARY KEY, " +
            COL_IMAGE   + " BLOB, " +
            "FOREIGN KEY (" + COL_ITEM_ID + ") REFERENCES " + TABLE_ITEM + " (" + COL_ID +"))";


    private final Context myContext;

    private boolean createDatabase = false;
    private boolean upgradeDatabase = false;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);

        this.myContext = context;
        DB_PATH = myContext.getDatabasePath(DB_NAME).getAbsolutePath();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.createDatabase = true;

        // creating required tables
        /*db.execSQL(CREATE_TABLE_ITEM_CLASS);
        db.execSQL(CREATE_TABLE_ITEM_SUB_CLASS);
        db.execSQL(CREATE_TABLE_ITEM);
        db.execSQL(CREATE_TABLE_ITEM_MEDIA);*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        this.upgradeDatabase = true;
        // TODO: Upgrade database script
    }

    /**
     * Upgrade the database in internal storage if it exists but is not current.
     * Create a new empty database in internal storage if it does not exist.
     */
    public void initializeDataBase() {
        /*
         * Creates or updates the database in internal storage if it is needed
         * before opening the database. In all cases opening the database copies
         * the database in internal storage to the cache.
         */
        getWritableDatabase();

        if (createDatabase) {
            System.out.println("Create database");
            /*
             * If the database is created by the copy method, then the creation
             * code needs to go here. This method consists of copying the new
             * database from assets into internal storage and then caching it.
             */
            try {
                /*
                 * Write over the empty data that was created in internal
                 * storage with the one in assets and then cache it.
                 */
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        } else if (upgradeDatabase) {
            System.out.println("Upgrade database");
            /*
             * If the database is upgraded by the copy and reload method, then
             * the upgrade code needs to go here. This method consists of
             * renaming the old database in internal storage, create an empty
             * new database in internal storage, copying the database from
             * assets to the new database in internal storage, caching the new
             * database from internal storage, loading the data from the old
             * database into the new database in the cache and then deleting the
             * old database from internal storage.
             */
            try {
                FileUtil.copyFile(DB_PATH, OLD_DB_PATH);
                copyDataBase();
                try (SQLiteDatabase oldDatabase = SQLiteDatabase.openDatabase(OLD_DB_PATH, null, SQLiteDatabase.OPEN_READWRITE)) {
                    // TODO: Handle SQLiteException
                }
                try (SQLiteDatabase newDatabase = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READWRITE)) {
                    // TODO: Handle SQLiteException
                }
                /*
                 * Add code to load data into the new database from the old
                 * database and then delete the old database from internal
                 * storage after all data has been transferred.
                 */
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    /**
     * Copies your database from your local assets-folder to the just created
     * empty database in the system folder, from where it can be accessed and
     * handled. This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException {
        /*
         * Close SQLiteOpenHelper so it will commit the created empty database
         * to internal storage.
         */
        close();

        /*
         * Open the database in the assets folder as the input stream.
         */
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        /*
         * Open the empty db in interal storage as the output stream.
         */
        OutputStream myOutput = new FileOutputStream(DB_PATH);

        /*
         * Copy over the empty db in internal storage with the database in the
         * assets folder.
         */
        FileUtil.copyFile(myInput, myOutput);

        /*
         * Access the copied database so SQLiteHelper will cache it and mark it
         * as created.
         */
        getWritableDatabase().close();
    }

    public long createItem(Item i) {
        if(i == null) {
            return -1;
        }

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_ID,           i.getId());
        values.put(COL_NAME,         i.getName());
        values.put(COL_ITEM_LEVEL,   i.getLevel());
        values.put(COL_CLASS_ID,     i.getClassId());
        values.put(COL_SUB_CLASS_ID, i.getSubClassId());
        values.put(COL_QUALITY,      i.getQuality().name());

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
                    values.put(COL_QUALITY,      i.getQuality().name());

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
        if (c != null && c.moveToFirst()) {
            do {
                final String      name       = c.getString(c.getColumnIndex(COL_NAME));
                final int         id         = c.getInt((c.getColumnIndex(COL_ID)));
                final int         level      = c.getInt(c.getColumnIndex(COL_ITEM_LEVEL));
                final int         classId    = c.getInt((c.getColumnIndex(COL_CLASS_ID)));
                final int         subClassId = c.getInt((c.getColumnIndex(COL_SUB_CLASS_ID)));
                final ItemQuality quality    = ItemQuality.valueOf(c.getString(c.getColumnIndex(COL_QUALITY)));

                items.add(new Item(id, name, level, classId, subClassId, quality));
            } while (c.moveToNext());

            c.close();
        }

        return items;
    }

    public Item getItemById(final int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_ITEM + " WHERE " + COL_ID + " = " + id;

        final Cursor c = db.rawQuery(selectQuery, null);

        if(c != null && c.moveToFirst() ) {
            final String      name       = c.getString(c.getColumnIndex(COL_NAME));
            final int         level      = c.getInt(c.getColumnIndex(COL_ITEM_LEVEL));
            final int         classId    = c.getInt((c.getColumnIndex(COL_CLASS_ID)));
            final int         subClassId = c.getInt((c.getColumnIndex(COL_SUB_CLASS_ID)));
            final ItemQuality quality    = ItemQuality.valueOf(c.getString(c.getColumnIndex(COL_QUALITY)));

            c.close();

            return new Item(id, name, level, classId, subClassId, quality);
        }

        return new Item();
    }

    public int getHighestItemId() {
        final SQLiteDatabase db = this.getReadableDatabase();
        final String query = "SELECT * FROM " + TABLE_ITEM + " ORDER BY " + COL_ID + " DESC LIMIT 0, 1";

        final Cursor c = db.rawQuery(query, null);

        if (c != null && c.moveToFirst()) {
            final int id = c.getInt(c.getColumnIndex(COL_ID));

            c.close();

            return id;
        }

        return -1;
    }

    public int getHighestItemClassId() {
        final SQLiteDatabase db    = this.getReadableDatabase();
        final String         query = "SELECT * FROM " + TABLE_ITEM_CLASS + " ORDER BY " + COL_ID + " DESC LIMIT 0, 1";

        final Cursor c = db.rawQuery(query, null);

        if (c != null && c.moveToFirst()) {
            final int id = c.getInt(c.getColumnIndex(COL_ID));

            c.close();

            return id;
        }

        return -1;
    }

    public int getHighestItemMediaItemId() {
        final SQLiteDatabase db    = this.getReadableDatabase();
        final String         query = "SELECT * FROM " + TABLE_ITEM_MEDIA + " ORDER BY " + COL_ITEM_ID + " DESC LIMIT 0, 1";

        final Cursor c = db.rawQuery(query, null);

        if (c != null && c.moveToFirst()) {
            final int id = c.getInt(c.getColumnIndex(COL_ITEM_ID));

            c.close();

            return id;
        }

        return -1;
    }

    public List<Integer> getItemIds() {
        final SQLiteDatabase db     = this.getReadableDatabase();
        final String         query  = "SELECT " + COL_ID + " FROM " + TABLE_ITEM + " ORDER BY " + COL_ID + " ASC";
        final List<Integer>  result = new ArrayList<>();

        final Cursor c = db.rawQuery(query, null);

        if (c != null && c.moveToFirst()) {
            do {
                result.add(c.getInt(c.getColumnIndex(COL_ID)));
            } while (c.moveToNext());

            c.close();
        }

        return result;
    }

    public int getHighestItemSubClassId(final int parentId) {
        final SQLiteDatabase db = this.getReadableDatabase();
        final String query = "SELECT * FROM " + TABLE_ITEM_SUB_CLASS + " WHERE " + COL_CLASS_ID + " = " + parentId + " ORDER BY " + COL_SUB_CLASS_ID + " DESC LIMIT 0, 1";

        final Cursor c = db.rawQuery(query, null);

        if (c != null && c.moveToFirst()) {
            final int id = c.getInt(c.getColumnIndex(COL_SUB_CLASS_ID));

            c.close();

            return id;
        }

        return -1;
    }

    public List<Integer> getMissingItemMediaIds() {
        final SQLiteDatabase db     = this.getReadableDatabase();
        final String         query  = "SELECT i." + COL_ID + " FROM " + TABLE_ITEM + " i LEFT JOIN " + TABLE_ITEM_MEDIA + " im ON i." + COL_ID + " = im." + COL_ITEM_ID + " WHERE im." + COL_ITEM_ID + " IS NULL ORDER BY i." + COL_ID + " ASC";
        final List<Integer>  result = new ArrayList<>();

        final Cursor c = db.rawQuery(query, null);

        if (c != null && c.moveToFirst()) {
            do {
                result.add(c.getInt(c.getColumnIndex(COL_ID)));
            } while (c.moveToNext());

            c.close();
        }

        return result;
    }

    public long createItemClass(ItemClass ic) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_ID,                 ic.getId());
        values.put(COL_NAME,               ic.getName());

        return db.insert(TABLE_ITEM_CLASS, null, values);
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

            c.close();
        }

        return classes;
    }

    public ItemClass getItemClassById(final int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_ITEM_CLASS + " WHERE " + COL_ID + " = " + id;

        final Cursor c = db.rawQuery(selectQuery, null);
        if(c != null && c.moveToFirst() ) {
            ItemClass ic = new ItemClass();
            ic.setId(c.getInt((c.getColumnIndex(COL_ID))));
            ic.setName((c.getString(c.getColumnIndex(COL_NAME))));

            c.close();

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

        return db.insert(TABLE_ITEM_SUB_CLASS, null, values);
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
        final SQLiteDatabase     db          = this.getReadableDatabase();
        final List<ItemSubClass> subClasses  = new ArrayList<>();
        final String             selectQuery = "SELECT  * FROM " + TABLE_ITEM_SUB_CLASS + " WHERE " + COL_CLASS_ID + " = " + id;

        final Cursor c = db.rawQuery(selectQuery, null);
        if(c != null && c.moveToFirst() ) {
            do {
                ItemSubClass isc = new ItemSubClass();
                isc.setId(c.getInt((c.getColumnIndex(COL_SUB_CLASS_ID))));
                isc.setParentClassId(id);
                isc.setName((c.getString(c.getColumnIndex(COL_NAME))));

                subClasses.add(isc);
            } while (c.moveToNext());

            c.close();
        }

        return subClasses;
    }

    public long createItemMedia(final int itemId, final byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_ITEM_ID, itemId);
        values.put(COL_IMAGE,   image);

        return db.insert(TABLE_ITEM_MEDIA, null, values);
    }

    public Bitmap getImageByItemId(final int itemId) {
        final SQLiteDatabase db    = this.getReadableDatabase();
        final String         query = "SELECT " + COL_IMAGE + " FROM " + TABLE_ITEM_MEDIA + " WHERE " + COL_ITEM_ID + " = " + itemId;

        final Cursor c = db.rawQuery(query, null);

        if(c != null && c.moveToFirst() ) {
            byte[] image = c.getBlob(0);

            c.close();

            return BitmapFactory.decodeByteArray(image, 0, image.length);
        }

        return null;
    }

    public void close() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    private void dropTables() {
        SQLiteDatabase db = this.getWritableDatabase();

        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEM_MEDIA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEM_SUB_CLASS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEM_CLASS);
    }

    public void resetDatabase() {
        dropTables();

        onCreate(this.getWritableDatabase());
    }
}