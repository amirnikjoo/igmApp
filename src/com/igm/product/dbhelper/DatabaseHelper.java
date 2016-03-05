package com.igm.product.dbhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.igm.product.entity.Part;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * User: Amir Nikjoo,  02/27/2016,  01:27 PM
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 4;

    // Database Name
    private static final String DATABASE_NAME = "db_product";

    // Table Names
    private static final String TABLE_LAST_EDIT = "last_edit";
    private static final String TABLE_PARTS = "parts";

    // last_edit Table - column
    private static final String KEY_LAST_EDIT_DATE_ID = "last_edit_date_id";
    private static final String COL_LAST_EDIT = "last_edit";

    // part Table - column
    private static final String KEY_PART_ID = "part_id";
    private static final String COL_DESCRIPTION = "description";
    private static final String COL_CAR_TYPE = "car_type";
    private static final String COL_STATUS = "status";

    // tables create statement
    private static final String CREATE_TABLE_LAST_EDIT = "CREATE TABLE "
            + TABLE_LAST_EDIT + "(" + KEY_LAST_EDIT_DATE_ID + " INTEGER PRIMARY KEY," + COL_LAST_EDIT
            + " LONG )";

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
        String str = "INSERT INTO " + TABLE_LAST_EDIT + " (" + KEY_LAST_EDIT_DATE_ID + "," + COL_LAST_EDIT + ") VALUES (1,201602010000); ";
        db.execSQL(str);

        db.execSQL(CREATE_TABLE_PARTS);
//        insertAllRecords(db);

        insertAllParts(db);

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
        long ret = -1L;
        try {
            SQLiteDatabase db = getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(KEY_PART_ID, p.getId());
            values.put(COL_DESCRIPTION, p.getDescription());
            values.put(COL_CAR_TYPE, p.getCarType());
            values.put(COL_STATUS, p.getStatus());
            values.put(COL_LAST_EDIT, getCustomDateTimeYYYYMMDDHHmm());
            ret = db.insert(TABLE_PARTS, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
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
        values.put(COL_LAST_EDIT, getCustomDateTimeYYYYMMDDHHmm());

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

    public List<Part> getPartsByCarType(int carType) {
        List<Part> parts = new ArrayList<Part>();
        String selectQuery = "SELECT " + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS +
                " FROM " + TABLE_PARTS
                + " WHERE " + COL_STATUS + " = 1" + " AND " + COL_CAR_TYPE + " = ? ";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, new String[]{carType + ""});

        if (c != null && c.moveToFirst()) {
            do {
                Part t = new Part();
                t.setId(c.getInt((c.getColumnIndex(KEY_PART_ID))));
                t.setDescription(c.getString(c.getColumnIndex(COL_DESCRIPTION)));
                t.setCarType(c.getInt(c.getColumnIndex(COL_CAR_TYPE)));
                t.setStatus(c.getInt(c.getColumnIndex(COL_STATUS)));

                parts.add(t);
            } while (c.moveToNext());
        }
        return parts;
    }

    public Part getPartById(int partId) {
        String selectQuery = "SELECT " + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS +
                " FROM " + TABLE_PARTS + " WHERE " + KEY_PART_ID + " = ? ";

        Part p = null;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c = db.rawQuery(selectQuery, new String[]{partId + ""});
            if (c != null && c.moveToFirst()) {
                c.moveToFirst();
                p = new Part();
                p.setId(partId);
                p.setDescription(c.getString(c.getColumnIndex(COL_DESCRIPTION)));
                p.setCarType(c.getInt(c.getColumnIndex(COL_CAR_TYPE)));
                p.setStatus(c.getInt(c.getColumnIndex(COL_STATUS)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // adding to tags list
        return p;
    }

    public long updateLastModified() {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        long currentTime = getCustomDateTimeYYYYMMDDHHmm();
        values.put(COL_LAST_EDIT, currentTime);
        db.update(TABLE_LAST_EDIT, values, "", null);
        return currentTime;
    }

    public long getLastEditDate() {
        String selectQuery = "SELECT " + COL_LAST_EDIT + " FROM " + TABLE_LAST_EDIT;

        SQLiteDatabase db = this.getReadableDatabase();
        Long d = 0L;

        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null) {
            c.moveToFirst();
            d = c.getLong(c.getColumnIndex(COL_LAST_EDIT));
        }
        return d;
    }

    public static long getCustomDateTimeYYYYMMDDHHmm() {
        Calendar cal = Calendar.getInstance();
        String date = "0";
        try {
            date = cal.get(Calendar.YEAR) +
                    padding("" + (cal.get(Calendar.MONTH) + 1), 2) +
                    padding("" + (cal.get(Calendar.DAY_OF_MONTH)), 2) +
                    padding("" + cal.get(Calendar.HOUR_OF_DAY), 2) +
                    padding("" + cal.get(Calendar.MINUTE), 2);
        } catch (Exception e) {
        }

        return Long.valueOf(date);
    }

    public static String padding(String str, int len) {
        if (str.length() >= len)
            return str;
        for (int i = str.length(); i < len; i++)
            str = '0' + str;
        return str;
    }

    public void insertAllParts(SQLiteDatabase db) {
        try {
            String str;
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "1" + ",'" + "آرم جلو و عقب سي يلو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "2" + ",'" + "آرم جلو و عقب ماتيز" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "3" + ",'" + "آهني کوچک توپي سر کمک ماتيز" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "4" + ",'" + "ابرويي زير چراغ جلو ماتيز چپ" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "5" + ",'" + "ابرويي زير چراغ جلو ماتيز راست" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "6" + ",'" + "اورينگ کشويي چرخ جلو دوو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "7" + ",'" + "اورينگ ليور دسته دنده دوو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "8" + ",'" + "اورينگ ترموستات دوو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "9" + ",'" + "اورينگ دلکو دوو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "10" + ",'" + "اورينگ دلکو ماتيز" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "11" + ",'" + "اورينگ واتر پمپ دوو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "12" + ",'" + "بست کمربندي اگزوز عقب دوو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "13" + ",'" + "بوش پلاستيکي پين کلاج دوو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "14" + ",'" + "بوش خرچنگي ليور دنده (تک اورينگ) دوو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "15" + ",'" + "بوش رابط خرچنگي ليور دنده (دو اورينگ) دوو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "16" + ",'" + "بوش کله قندي طبق ماتيز" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "17" + ",'" + "بوش لقي گلويي فرمان دوو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "18" + ",'" + "بوش مچي طبق دوو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "19" + ",'" + "بوش ميل فرمان دوو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "20" + ",'" + "بوش ميل موجگير دوو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "21" + ",'" + "بوش سيم دوقلو تعويض دنده ماتيز و MVM" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "22" + ",'" + "بوش و پين پدال کلاج دوو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "23" + ",'" + "پولک آب بزرگ ماتيز ( بغل سيلندر )" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "24" + ",'" + "پولک آب کوچک ماتيز ( سر سيلندر ) " + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "25" + ",'" + "پولک آب بزرگ دوو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "26" + ",'" + "پولک آب متوسط دوو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "27" + ",'" + "گل پاش عقب دوو سي يلو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "28" + ",'" + "پين خرچنگي ليور دنده دوو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "29" + ",'" + "تشتکي سيلندر چرخ عقب دوو اسپرو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "30" + ",'" + "تشتکي سيلندر چرخ عقب دوو سي يلو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "31" + ",'" + "دياق ابرويي سپر جلو سي يلو چپ " + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "32" + ",'" + "دياق ابرويي سپر جلو سي يلو راست" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "33" + ",'" + "دياق خاک اندازي سپر عقب سي يلو چپ " + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "34" + ",'" + "دياق خاک اندازي سپر عقب سي يلو راست" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "35" + ",'" + "دياق خاک اندازي سپر جلو سي يلو چپ " + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "36" + ",'" + "دياق خاک اندازي سپر جلو سي يلو راست" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "37" + ",'" + "دستگيره سقف سي يلو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "38" + ",'" + "دستگيره مچي در راست سي يلو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "39" + ",'" + "دسته موتور راست دوو سي يلو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "40" + ",'" + "دسته موتور عقب گيربکس دوو سي يلو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "41" + ",'" + "دوشاخه استارت دوو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "42" + ",'" + "خار تيغي دياق سپر دوو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "43" + ",'" + "خارملخي ميل کاپوت دوو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "44" + ",'" + "خار مربعي ته ميل کاپوت دوو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "45" + ",'" + "شيشه پرژکتور چپ سی يلو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "46" + ",'" + "شيشه پرژکتور راست سي يلو " + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "47" + ",'" + "شيلنگ واسطه سه راهی آب ماتيز" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "48" + ",'" + "شيلنگ بخاری ماتيز " + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "49" + ",'" + "شيلنگ بخار روغن سی يلو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "50" + ",'" + "شيلنگ بالاي رادياتور منجيددار سي يلو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "51" + ",'" + "شيلنگ زانويي کمپرس روغن سي يلو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "52" + ",'" + "طلق راهنماي جلو چپ ماتيز" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "53" + ",'" + "طلق راهنماي جلو راست ماتيز" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "54" + ",'" + "فنر اگزوز دوو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "55" + ",'" + "قاب دور پرژکتور سي يلو چپ" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "56" + ",'" + "قاب دور پرژکتور سي يلو راست" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "57" + ",'" + "قاب پلاک جلو سي يلو سفيد" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "58" + ",'" + "قاب پلاک عقب سي يلو سفيد" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "59" + ",'" + "قاب پلاک جلو سي يلو مشکی" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "60" + ",'" + "قاب پلاک عقب سي يلو مشکی" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "61" + ",'" + "قاب تسمه تايم کامل سي يلو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "62" + ",'" + "قاب دور دستگيره داخل سي يلو " + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "63" + ",'" + "کاپ کشويي چرخ جلو دوو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "64" + ",'" + "کورکن سيلندر ماتيز" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "65" + ",'" + "کورکن منيفولد ماتيز با واشر" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "66" + ",'" + "گردگير پلوس سمت چرخ دوو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "67" + ",'" + "گردگير پلوس سمت گيربکس دوو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "68" + ",'" + "گردگير پلوس سمت چرخ ماتيز" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "69" + ",'" + "گردگير پلوس سمت گيربکس دوو ماتيز" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "70" + ",'" + "گردگير سيلندر چرخ عقب دوو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "71" + ",'" + "صفحه مشکي پلاستيکي رو بوقي سي يلو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "72" + ",'" + "گردگير کشويي لبه دار چرخ جلو دوو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "73" + ",'" + "لاستيک داخل در مخزن کلاج بالا دوو " + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "74" + ",'" + "لاستيک شلنگ روي بوستر دوو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "75" + ",'" + "لاستيک گرد ته بوستر دوو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "76" + ",'" + "لاستيک پدال کلاج و ترمز دوو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "77" + ",'" + "لاستيک پدال گاز دوو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "78" + ",'" + "لاستيک توپي سر کمک ماتيز" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "79" + ",'" + "لاستيک چاکدار موجگير ماتيز" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "80" + ",'" + "واشر سه گوش گلويِي اگزوز ماتيز اورجينال" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "81" + ",'" + "لاستيک چاکدار موجگير اسپرو اصلي" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "82" + ",'" + "لاستيک دور ليواني دوو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "83" + ",'" + "لاستيک ضربگير بالا راديات سي يلو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "84" + ",'" + "لاستيک ضربگير بالا راديات اسپرو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "85" + ",'" + "لوازم پمپ ترمز سي يلو ( نيمه کامل با فنر )" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "86" + ",'" + "لوازم سيلندر چرخ عقب اسپرو کامل" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "87" + ",'" + "لوازم سيلندر چرخ عقب سي يلو و ماتيز کامل " + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "88" + ",'" + "لوازم کشوئي چرخ جلو دوو ( يکطرف )" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "89" + ",'" + "ليواني کمک کامل دوو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "90" + ",'" + "ميل موجگير کامل دوو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "91" + ",'" + "منجيد عقب اگزوز دوو " + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "92" + ",'" + "منجيد وسط اگزوز دوو " + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "93" + ",'" + "واشر اويل پمپ سي يلو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "94" + ",'" + "واشر اويل پمپ ماتيز" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "95" + ",'" + "واشر در سوپاپ سي يلو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "96" + ",'" + "واشر در سوپاپ اسپرو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "97" + ",'" + "واشر کارتل موتور سي يلو (چوب پنبه مشکي)" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "98" + ",'" + "واشر کارتل موتور سي يلو ( ويکتور )" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "99" + ",'" + "واشر کارتل گيربکس دوو ( ويکتور )" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "100" + ",'" + "واشر منيفولد دود سي يلو و ريسر " + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "101" + ",'" + "واشر منيفولد هواي سي يلو و ريسر 94" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "102" + ",'" + "واشر منيفولد هواي ريسر 92" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "103" + ",'" + "واشر منيفولد هواي اسپرو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "104" + ",'" + "واشر منيفولد دود اسپرو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "105" + ",'" + "واشر پايه دلکو ماتيز" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "106" + ",'" + "واشر منيفولد هوا اورينگي ماتيز" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "107" + ",'" + "واشر منيفولد دود ماتيز ( فلزي )" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "108" + ",'" + "واشر در سوپاپ ماتيز" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "109" + ",'" + "واشر گلويي اگزوز دوو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "110" + ",'" + "واشر لاستيکي در روغن موتور دوو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "111" + ",'" + "واشر لاستيکي در پمپ بنزين دوو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "112" + ",'" + "هوزينگ ترموستات سي يلو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "113" + ",'" + "خرطومي هواکش چهار راهي سي يلو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "114" + ",'" + "مهره کارتل دوو و هيونداي اصلي کره" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "115" + ",'" + "ضربگير دو سر پيچ راديات کولر سي يلو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "116" + ",'" + "بست بالاي راديات آب سيلو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "117" + ",'" + "گردگير دور توپي سوئيچ موتور" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "118" + ",'" + "خار آمپولي سپر دوو سي يلو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "119" + ",'" + "قاب تسمه تايم پاييني ماتيز " + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "120" + ",'" + "قاب دور ضبط دوو سی يلو" + "' ," + "1" + " ," + "1" + "); ";
            db.execSQL(str);


            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "200" + ",'" + "بوش جناقي پژو 405 " + "' ," + "2" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "201" + ",'" + "بوش لبه دار طبق پژو 405 " + "' ," + "2" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "202" + ",'" + "ميل موجگير پژو 405" + "' ," + "2" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "203" + ",'" + "چاکدار موجگير پژو 405" + "' ," + "2" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "204" + ",'" + "قرقری فرمان پژو 405" + "' ," + "2" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "205" + ",'" + "سيبک فرمان پژو 405 چپ و راست" + "' ," + "2" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "206" + ",'" + "سيبک زير کمک پژو 405" + "' ," + "2" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "207" + ",'" + "توپي سر کمک پژو 405 ساده " + "' ," + "2" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "208" + ",'" + "توپي سر کمک پژو 405 ( لبه دار)" + "' ," + "2" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "209" + ",'" + "توپي سر کمک پژو 405 (جديد دو لبه دار)" + "' ," + "2" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "210" + ",'" + "گردگير پلوس سمت چرخ پژو 1800" + "' ," + "2" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "211" + ",'" + "گردگير پلوس گيربکس پژو 1800 " + "' ," + "2" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "212" + ",'" + "گردگير جعبه فرمان (دو سرگشاد) راست" + "' ," + "2" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "213" + ",'" + "گردگير جعبه فرمان ( يکسر گشاد ) چپ" + "' ," + "2" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "214" + ",'" + "گردگير جعبه فرمان قديم ( جي ال ) " + "' ," + "2" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "215" + ",'" + "دسته موتور زير باتري پژو 405 " + "' ," + "2" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "216" + ",'" + "دسته موتورگرد پلاستيکی پژو 405" + "' ," + "2" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "217" + ",'" + "دسته موتورگرد فلزی پژو 405" + "' ," + "2" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "218" + ",'" + "دسته موتور دو سر پيچ پژو 405 روغن دار" + "' ," + "2" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "219" + ",'" + "واشر در سوپاپ پژو 405 و پرشيا" + "' ," + "2" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "220" + ",'" + "واشر بغل اگزوز پژو 405" + "' ," + "2" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "221" + ",'" + "پولي هرزگرد پژو 405" + "' ," + "2" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "222" + ",'" + "اورينگ ترموستات پژو 405 " + "' ," + "2" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "223" + ",'" + "اورينگ انژکتور پژو 405 و زانتيا" + "' ," + "2" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "224" + ",'" + "لوازم سيلندر چرخ عقب پژو 405 " + "' ," + "2" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "225" + ",'" + "لوازم سيلندر چرخ عقب سمند " + "' ," + "2" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "226" + ",'" + "منجيد اگزوز انتها يي پژو 1800 " + "' ," + "2" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "227" + ",'" + "منجيد اگزوز مياني پژو 1800 " + "' ," + "2" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "228" + ",'" + "بوش طبق پژو 206 ساده" + "' ," + "2" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "229" + ",'" + "ميل موجگير پژو 206 " + "' ," + "2" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "230" + ",'" + "سيبک فرمان  پژو 206 چپ و راست" + "' ," + "2" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "231" + ",'" + "قرقری فرمان پژو 206" + "' ," + "2" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "232" + ",'" + "دسته موتور دو سر پيچ  پژو 206 تيپ 5  و زانتيا  " + "' ," + "2" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "233" + ",'" + "دسته موتورگرد فلزی پژو 206" + "' ," + "2" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "234" + ",'" + "دسته موتورگرد پلاستيکی پژو 206" + "' ," + "2" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "235" + ",'" + "دسته موتور زير باتري پژو 206 " + "' ," + "2" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "236" + ",'" + "اورينگ پايه فيلتر روغن پژو 206  همه تيپ ها" + "' ," + "2" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "237" + ",'" + "اورينگ انژکتور 206 پرايد و پيکان و آردي" + "' ," + "2" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "238" + ",'" + "واشر گلويي اگزوز پژو 206" + "' ," + "2" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "239" + ",'" + "منجيد اگزوز پژو 206 " + "' ," + "2" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "240" + ",'" + "لوازم سيلندر چرخ عقب پژو 206" + "' ," + "2" + " ," + "1" + "); ";
            db.execSQL(str);


            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "300" + ",'" + "دسته موتور شماره  1  پرايد" + "' ," + "3" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "301" + ",'" + "دسته موتور شماره  2  پرايد" + "' ," + "3" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "302" + ",'" + "واشر در سوپاپ پرايد" + "' ," + "3" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "303" + ",'" + "گردگير پلوس سمت چرخ پرايد " + "' ," + "3" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "304" + ",'" + "گردگير پلوس سه گوش سمت گيربکس پرايد " + "' ," + "3" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "305" + ",'" + "گردگير جعبه فرمان پرايد  " + "' ," + "3" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "306" + ",'" + "گردگير بلند سيلندر چرخ جلو پرايد " + "' ," + "3" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "307" + ",'" + "گردگير کوتاه سيلندر چرخ جلو پرايد " + "' ," + "3" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "308" + ",'" + "منجيد اگزوز پرايد " + "' ," + "3" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "309" + ",'" + "لوازم سيلندر چرخ عقب پرايد " + "' ," + "3" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "310" + ",'" + "لاستيک زير منبع پمپ ترمز پرايد " + "' ," + "3" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "311" + ",'" + "لاستيک تعادل پرايد" + "' ," + "3" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "312" + ",'" + "لاستيک چاکدار پرايد" + "' ," + "3" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "313" + ",'" + "گردگير بالاي کمک فنر جلو پرايد  " + "' ," + "3" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "314" + ",'" + "گردگير بالاي کمک فنر عقب پرايد " + "' ," + "3" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "315" + ",'" + "لاستيک گرد بالاي فنرلول جلو پرايد(بدون لبه)" + "' ," + "3" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "316" + ",'" + "لاستيک گرد بالاي فنرلول عقب پرايد(لبه دار)" + "' ," + "3" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "317" + ",'" + "لاستيکهاي نر و ماده بالا کمک عقب" + "' ," + "3" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "318" + ",'" + "توپی سر کمک پرايد" + "' ," + "3" + " ," + "1" + "); ";
            db.execSQL(str);


            str = " SELECT * FROM " + TABLE_PARTS;
            Cursor c = db.rawQuery(str, null);
            Log.d(" test ", c.getCount() + "");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
