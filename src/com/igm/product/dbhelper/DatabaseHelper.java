package com.igm.product.dbhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.igm.product.entity.Part;

import java.util.*;

/**
 * User: Amir Nikjoo,  02/27/2016,  01:27 PM
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "db_product";

    // Table Names
    private static final String TABLE_LAST_EDIT = "last_edit";
    private static final String TABLE_PARTS = "parts";

    // last_edit Table - column
    private static final String KEY_LAST_EDIT_DATE_ID = "last_edit_date_id";

    // common - columns
    private static final String COL_LAST_EDIT = "last_edit";

    // part Table - column
    private static final String KEY_PART_ID = "part_id";
    private static final String COL_DESCRIPTION = "description";
    private static final String COL_CAR_TYPE = "car_type";
    private static final String COL_STATUS = "status";

    // tables create statement
    private static final String CREATE_TABLE_LAST_EDIT = "CREATE TABLE "
            + TABLE_LAST_EDIT + "(" + KEY_LAST_EDIT_DATE_ID + " INTEGER PRIMARY KEY," + COL_LAST_EDIT
            + " INTEGER" + ")";

    private static final String CREATE_TABLE_PARTS = "CREATE TABLE "
            + TABLE_PARTS + "(" + KEY_PART_ID + " INTEGER PRIMARY KEY," + COL_DESCRIPTION
            + " TEXT," + COL_CAR_TYPE + " INTEGER," + COL_STATUS + " INTEGER," + COL_LAST_EDIT + " INTEGER" + ")";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL(CREATE_TABLE_LAST_EDIT);
        db.execSQL(CREATE_TABLE_PARTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LAST_EDIT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PARTS);

        // create new tables
        onCreate(db);
    }

    public long insertPart(Part p) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PART_ID, p.getId());
        values.put(COL_DESCRIPTION, p.getDescription());
        values.put(COL_CAR_TYPE, p.getCarType());
        values.put(COL_STATUS, p.getStatus());
        values.put(COL_LAST_EDIT, Integer.valueOf(getyyyyMMdd()));

        return db.insert(TABLE_PARTS, null, values);
    }

    public long deletePart(int partId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PART_ID, partId);

        return db.update(TABLE_PARTS, values, KEY_PART_ID + " = ?", new String[]{String.valueOf(partId)});
    }

    public long updatePart(Part p) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PART_ID, p.getId());
        values.put(COL_DESCRIPTION, p.getDescription());
        values.put(COL_CAR_TYPE, p.getCarType());
        values.put(COL_STATUS, p.getStatus());

        return db.update(TABLE_PARTS, values, KEY_PART_ID + " = ?", new String[]{String.valueOf(p.getId())});
    }

    public List<Part> getAllParts() {
        List<Part> parts = new ArrayList<Part>();
        String selectQuery = "SELECT " + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS +
                " FROM " + TABLE_PARTS + " WHERE " + COL_STATUS + " = 1";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Part t = new Part();
                t.setId(c.getInt((c.getColumnIndex(KEY_PART_ID))));
                t.setDescription(c.getString(c.getColumnIndex(COL_DESCRIPTION)));
                t.setCarType(c.getInt(c.getColumnIndex(COL_CAR_TYPE)));
                t.setStatus(c.getInt(c.getColumnIndex(COL_STATUS)));

                // adding to tags list
                parts.add(t);
            } while (c.moveToNext());
        }
        return parts;
    }

    public List<Part> getAllPartsByCarType(int carType) {
        List<Part> parts = new ArrayList<Part>();
        String selectQuery = "SELECT " + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS +
                " FROM " + TABLE_PARTS + " WHERE " + COL_STATUS + " = 1" + " AND " + COL_CAR_TYPE + " = " + carType;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Part t = new Part();
                t.setId(c.getInt((c.getColumnIndex(KEY_PART_ID))));
                t.setDescription(c.getString(c.getColumnIndex(COL_DESCRIPTION)));
                t.setCarType(c.getInt(c.getColumnIndex(COL_CAR_TYPE)));
                t.setStatus(c.getInt(c.getColumnIndex(COL_STATUS)));

                // adding to tags list
                parts.add(t);
            } while (c.moveToNext());
        }
        return parts;
    }

    public Part getPartById(int partId) {
        String selectQuery = "SELECT " + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS +
                " FROM " + TABLE_PARTS + " WHERE " + KEY_PART_ID + " = " + partId;

        SQLiteDatabase db = this.getReadableDatabase();
        Part p = null;

        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null) {
            c.moveToFirst();
            p.setId(c.getInt((c.getColumnIndex(KEY_PART_ID))));
            p.setDescription(c.getString(c.getColumnIndex(COL_DESCRIPTION)));
            p.setCarType(c.getInt(c.getColumnIndex(COL_CAR_TYPE)));
            p.setStatus(c.getInt(c.getColumnIndex(COL_STATUS)));
        }
        // adding to tags list
        return p;
    }

    public long updateLastModified(Part p) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_LAST_EDIT,Integer.valueOf(getyyyyMMdd()));
        return db.update(TABLE_PARTS, values, "",null);
    }

    public long insertLastEdit() {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LAST_EDIT_DATE_ID, 1);
        values.put(COL_LAST_EDIT, Integer.valueOf(getyyyyMMdd()));
        return db.insert(TABLE_LAST_EDIT, null, values);
    }

    public Integer getLastEditDate() {
        String selectQuery = "SELECT " + COL_LAST_EDIT +
                " FROM " + TABLE_LAST_EDIT;

        SQLiteDatabase db = this.getReadableDatabase();
        Integer d = null;

        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null) {
            c.moveToFirst();
            d = c.getInt(c.getColumnIndex(COL_LAST_EDIT));
        }
        // adding to tags list
        return d;
    }

    public static String getyyyyMMdd() {
        Calendar calendar = new GregorianCalendar();
        try {
            return "" + calendar.get(Calendar.YEAR) +
                    padding(("" + calendar.get(Calendar.DAY_OF_MONTH) + 1), 2) +
                    padding(("" + calendar.get(Calendar.DAY_OF_MONTH)), 2);
        } catch (Exception e) {
            return "20150101";
        }
    }

    public static String padding(String str, int len) {
        if (str.length() >= len)
            return str;
        for (int i = str.length(); i < len; i++)
            str = '0' + str;
        return str;
    }

}
