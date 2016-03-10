package com.igm.product.dbhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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

            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	1	" + ",'" + "	آرم جلو و عقب سی یلو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	2	" + ",'" + "	آرم جلو و عقب ماتیز	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	3	" + ",'" + "	آهنی کوچک توپی سر کمک ماتیز	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	4	" + ",'" + "	ابرویی زیر چراغ جلو ماتیز چپ	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	5	" + ",'" + "	ابرویی زیر چراغ جلو ماتیز راست	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	6	" + ",'" + "	اورینگ کشویی چرخ جلو دوو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	7	" + ",'" + "	اورینگ لیور دسته دنده دوو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	8	" + ",'" + "	اورینگ ترموستات دوو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	9	" + ",'" + "	اورینگ دلکو دوو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	10	" + ",'" + "	اورینگ دلکو ماتیز	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	11	" + ",'" + "	اورینگ واتر پمپ دوو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	12	" + ",'" + "	بست کمربندی اگزوز عقب دوو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	13	" + ",'" + "	بوش پلاستیکی پین کلاج دوو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	14	" + ",'" + "	بوش خرچنگی لیور دنده (تک اورینگ) دوو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	15	" + ",'" + "	بوش رابط خرچنگی لیور دنده (دو اورینگ) دوو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	16	" + ",'" + "	بوش کله قندی طبق ماتیز	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	17	" + ",'" + "	بوش لقی گلویی فرمان دوو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	18	" + ",'" + "	بوش مچی طبق دوو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	19	" + ",'" + "	بوش میل فرمان دوو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	20	" + ",'" + "	بوش میل موجگیر دوو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	21	" + ",'" + "	بوش سیم دوقلو تعویض دنده ماتیز و MVM	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	22	" + ",'" + "	بوش و پین پدال کلاج دوو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	23	" + ",'" + "	پولک آب بزرگ ماتیز ( بغل سیلندر )	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	24	" + ",'" + "	پولک آب کوچک ماتیز ( سر سیلندر ) 	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	25	" + ",'" + "	پولک آب بزرگ دوو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	26	" + ",'" + "	پولک آب متوسط دوو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	27	" + ",'" + "	گل پاش عقب دوو سی یلو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	28	" + ",'" + "	پین خرچنگی لیور دنده دوو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	29	" + ",'" + "	تشتکی سیلندر چرخ عقب دوو اسپرو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	30	" + ",'" + "	تشتکی سیلندر چرخ عقب دوو سی یلو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	31	" + ",'" + "	دیاق ابرویی سپر جلو سی یلو چپ 	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	32	" + ",'" + "	دیاق ابرویی سپر جلو سی یلو راست	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	33	" + ",'" + "	دیاق خاک اندازی سپر عقب سی یلو چپ 	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	34	" + ",'" + "	دیاق خاک اندازی سپر عقب سی یلو راست	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	35	" + ",'" + "	دیاق خاک اندازی سپر جلو سی یلو چپ 	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	36	" + ",'" + "	دیاق خاک اندازی سپر جلو سی یلو راست	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	37	" + ",'" + "	دستگیره سقف سی یلو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	38	" + ",'" + "	دستگیره مچی در راست سی یلو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	39	" + ",'" + "	دسته موتور راست دوو سی یلو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	40	" + ",'" + "	دسته موتور عقب گیربکس دوو سی یلو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	41	" + ",'" + "	دوشاخه استارت دوو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	42	" + ",'" + "	خار تیغی دیاق سپر دوو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	43	" + ",'" + "	خارملخی میل کاپوت دوو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	44	" + ",'" + "	خار مربعی ته میل کاپوت دوو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	45	" + ",'" + "	شیشه پرژکتور چپ سی یلو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	46	" + ",'" + "	شیشه پرژکتور راست سی یلو 	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	47	" + ",'" + "	شیلنگ واسطه سه راهی آب ماتیز	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	48	" + ",'" + "	شیلنگ بخاری ماتیز 	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	49	" + ",'" + "	شیلنگ بخار روغن سی یلو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	50	" + ",'" + "	شیلنگ بالای رادیاتور منجیددار سی یلو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	51	" + ",'" + "	شیلنگ زانویی کمپرس روغن سی یلو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	52	" + ",'" + "	طلق راهنمای جلو چپ ماتیز	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	53	" + ",'" + "	طلق راهنمای جلو راست ماتیز	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	54	" + ",'" + "	فنر اگزوز دوو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	55	" + ",'" + "	قاب دور پرژکتور سی یلو چپ	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	56	" + ",'" + "	قاب دور پرژکتور سی یلو راست	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	57	" + ",'" + "	قاب پلاک جلو سی یلو سفید	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	58	" + ",'" + "	قاب پلاک عقب سی یلو سفید	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	59	" + ",'" + "	قاب پلاک جلو سی یلو مشکی	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	60	" + ",'" + "	قاب پلاک عقب سی یلو مشکی	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	61	" + ",'" + "	قاب تسمه تایم کامل سی یلو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	62	" + ",'" + "	قاب دور دستگیره داخل سی یلو 	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	63	" + ",'" + "	کاپ کشویی چرخ جلو دوو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	64	" + ",'" + "	کورکن سیلندر ماتیز	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	65	" + ",'" + "	کورکن منیفولد ماتیز با واشر	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	66	" + ",'" + "	گردگیر پلوس سمت چرخ دوو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	67	" + ",'" + "	گردگیر پلوس سمت گیربکس دوو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	68	" + ",'" + "	گردگیر پلوس سمت چرخ ماتیز	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	69	" + ",'" + "	گردگیر پلوس سمت گیربکس دوو ماتیز	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	70	" + ",'" + "	گردگیر سیلندر چرخ عقب دوو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	71	" + ",'" + "	صفحه مشکی پلاستیکی رو بوقی سی یلو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	72	" + ",'" + "	گردگیر کشویی لبه دار چرخ جلو دوو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	73	" + ",'" + "	لاستیک داخل در مخزن کلاج بالا دوو 	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	74	" + ",'" + "	لاستیک شلنگ روی بوستر دوو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	75	" + ",'" + "	لاستیک گرد ته بوستر دوو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	76	" + ",'" + "	لاستیک پدال کلاج و ترمز دوو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	77	" + ",'" + "	لاستیک پدال گاز دوو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	78	" + ",'" + "	لاستیک توپی سر کمک ماتیز	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	79	" + ",'" + "	لاستیک چاکدار موجگیر ماتیز	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	80	" + ",'" + "	واشر سه گوش گلویِی اگزوز ماتیز اورجینال	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	81	" + ",'" + "	لاستیک چاکدار موجگیر اسپرو اصلی	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	82	" + ",'" + "	لاستیک دور لیوانی دوو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	83	" + ",'" + "	لاستیک ضربگیر بالا رادیات سی یلو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	84	" + ",'" + "	لاستیک ضربگیر بالا رادیات اسپرو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	85	" + ",'" + "	لوازم پمپ ترمز سی یلو ( نیمه کامل با فنر )	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	86	" + ",'" + "	لوازم سیلندر چرخ عقب اسپرو کامل	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	87	" + ",'" + "	لوازم سیلندر چرخ عقب سی یلو و ماتیز کامل 	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	88	" + ",'" + "	لوازم کشوئی چرخ جلو دوو ( یکطرف )	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	89	" + ",'" + "	لیوانی کمک کامل دوو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	90	" + ",'" + "	میل موجگیر کامل دوو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	91	" + ",'" + "	منجید عقب اگزوز دوو 	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	92	" + ",'" + "	منجید وسط اگزوز دوو 	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	93	" + ",'" + "	واشر اویل پمپ سی یلو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	94	" + ",'" + "	واشر اویل پمپ ماتیز	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	95	" + ",'" + "	واشر در سوپاپ سی یلو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	96	" + ",'" + "	واشر در سوپاپ اسپرو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	97	" + ",'" + "	واشر کارتل موتور سی یلو (چوب پنبه مشکی)	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	98	" + ",'" + "	واشر کارتل موتور سی یلو ( ویکتور )	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	99	" + ",'" + "	واشر کارتل گیربکس دوو ( ویکتور )	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	100	" + ",'" + "	واشر منیفولد دود سی یلو و ریسر 	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	101	" + ",'" + "	واشر منیفولد هوای سی یلو و ریسر 94	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	102	" + ",'" + "	واشر منیفولد هوای ریسر 92	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	103	" + ",'" + "	واشر منیفولد هوای اسپرو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	104	" + ",'" + "	واشر منیفولد دود اسپرو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	105	" + ",'" + "	واشر پایه دلکو ماتیز	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	106	" + ",'" + "	واشر منیفولد هوا اورینگی ماتیز	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	107	" + ",'" + "	واشر منیفولد دود ماتیز ( فلزی )	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	108	" + ",'" + "	واشر در سوپاپ ماتیز	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	109	" + ",'" + "	واشر گلویی اگزوز دوو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	110	" + ",'" + "	واشر لاستیکی در روغن موتور دوو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	111	" + ",'" + "	واشر لاستیکی در پمپ بنزین دوو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	112	" + ",'" + "	هوزینگ ترموستات سی یلو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	113	" + ",'" + "	خرطومی هواکش چهار راهی سی یلو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	114	" + ",'" + "	مهره کارتل دوو و هیوندای اصلی کره	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	115	" + ",'" + "	ضربگیر دو سر پیچ رادیات کولر سی یلو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	116	" + ",'" + "	بست بالای رادیات آب سیلو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	117	" + ",'" + "	گردگیر دور توپی سوئیچ موتور	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	118	" + ",'" + "	خار آمپولی سپر دوو سی یلو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	119	" + ",'" + "	قاب تسمه تایم پایینی ماتیز 	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	120	" + ",'" + "	قاب دور ضبط دوو سی یلو	" + "' ," + "	1	" + " ," + "1" + "); ";
            db.execSQL(str);


            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	200	" + ",'" + "	بوش جناقی پژو 405 	" + "' ," + "	2	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	201	" + ",'" + "	بوش لبه دار طبق پژو 405 	" + "' ," + "	2	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	202	" + ",'" + "	میل موجگیر پژو 405	" + "' ," + "	2	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	203	" + ",'" + "	چاکدار موجگیر پژو 405	" + "' ," + "	2	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	204	" + ",'" + "	قرقری فرمان پژو 405	" + "' ," + "	2	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	205	" + ",'" + "	سیبک فرمان پژو 405 چپ و راست	" + "' ," + "	2	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	206	" + ",'" + "	سیبک زیر کمک پژو 405	" + "' ," + "	2	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	207	" + ",'" + "	توپی سر کمک پژو 405 ساده 	" + "' ," + "	2	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	208	" + ",'" + "	توپی سر کمک پژو 405 ( لبه دار)	" + "' ," + "	2	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	209	" + ",'" + "	توپی سر کمک پژو 405 (جدید دو لبه دار)	" + "' ," + "	2	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	210	" + ",'" + "	گردگیر پلوس سمت چرخ پژو 1800	" + "' ," + "	2	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	211	" + ",'" + "	گردگیر پلوس گیربکس پژو 1800 	" + "' ," + "	2	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	212	" + ",'" + "	گردگیر جعبه فرمان (دو سرگشاد) راست	" + "' ," + "	2	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	213	" + ",'" + "	گردگیر جعبه فرمان ( یکسر گشاد ) چپ	" + "' ," + "	2	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	214	" + ",'" + "	گردگیر جعبه فرمان قدیم ( جی ال ) 	" + "' ," + "	2	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	215	" + ",'" + "	دسته موتور زیر باتری پژو 405 	" + "' ," + "	2	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	216	" + ",'" + "	دسته موتورگرد پلاستیکی پژو 405	" + "' ," + "	2	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	217	" + ",'" + "	دسته موتورگرد فلزی پژو 405	" + "' ," + "	2	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	218	" + ",'" + "	دسته موتور دو سر پیچ پژو 405 روغن دار	" + "' ," + "	2	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	219	" + ",'" + "	واشر در سوپاپ پژو 405 و پرشیا	" + "' ," + "	2	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	220	" + ",'" + "	واشر بغل اگزوز پژو 405	" + "' ," + "	2	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	221	" + ",'" + "	پولی هرزگرد پژو 405	" + "' ," + "	2	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	222	" + ",'" + "	اورینگ ترموستات پژو 405 	" + "' ," + "	2	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	223	" + ",'" + "	اورینگ انژکتور پژو 405 و زانتیا	" + "' ," + "	2	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	224	" + ",'" + "	لوازم سیلندر چرخ عقب پژو 405 	" + "' ," + "	2	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	225	" + ",'" + "	لوازم سیلندر چرخ عقب سمند 	" + "' ," + "	2	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	226	" + ",'" + "	منجید اگزوز انتها یی پژو 1800 	" + "' ," + "	2	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	227	" + ",'" + "	منجید اگزوز میانی پژو 1800 	" + "' ," + "	2	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	228	" + ",'" + "	بوش طبق پژو 206 ساده	" + "' ," + "	2	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	229	" + ",'" + "	میل موجگیر پژو 206 	" + "' ," + "	2	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	230	" + ",'" + "	سیبک فرمان پژو 206 چپ و راست	" + "' ," + "	2	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	231	" + ",'" + "	قرقری فرمان پژو 206	" + "' ," + "	2	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	232	" + ",'" + "	دسته موتور دو سر پیچ پژو 206 تیپ 5 و زانتیا 	" + "' ," + "	2	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	233	" + ",'" + "	دسته موتورگرد فلزی پژو 206	" + "' ," + "	2	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	234	" + ",'" + "	دسته موتورگرد پلاستیکی پژو 206	" + "' ," + "	2	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	235	" + ",'" + "	دسته موتور زیر باتری پژو 206 	" + "' ," + "	2	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	236	" + ",'" + "	اورینگ پایه فیلتر روغن پژو 206 همه تیپ ها	" + "' ," + "	2	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	237	" + ",'" + "	اورینگ انژکتور 206 پراید و پیکان و آردی	" + "' ," + "	2	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	238	" + ",'" + "	واشر گلویی اگزوز پژو 206	" + "' ," + "	2	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	239	" + ",'" + "	منجید اگزوز پژو 206 	" + "' ," + "	2	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	240	" + ",'" + "	لوازم سیلندر چرخ عقب پژو 206	" + "' ," + "	2	" + " ," + "1" + "); ";
            db.execSQL(str);

            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	300	" + ",'" + "	دسته موتور شماره 1 پراید	" + "' ," + "	3	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	301	" + ",'" + "	دسته موتور شماره 2 پراید	" + "' ," + "	3	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	302	" + ",'" + "	واشر در سوپاپ پراید	" + "' ," + "	3	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	303	" + ",'" + "	گردگیر پلوس سمت چرخ پراید 	" + "' ," + "	3	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	304	" + ",'" + "	گردگیر پلوس سه گوش سمت گیربکس پراید 	" + "' ," + "	3	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	305	" + ",'" + "	گردگیر جعبه فرمان پراید 	" + "' ," + "	3	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	306	" + ",'" + "	گردگیر بلند سیلندر چرخ جلو پراید 	" + "' ," + "	3	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	307	" + ",'" + "	گردگیر کوتاه سیلندر چرخ جلو پراید 	" + "' ," + "	3	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	308	" + ",'" + "	منجید اگزوز پراید 	" + "' ," + "	3	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	309	" + ",'" + "	لوازم سیلندر چرخ عقب پراید 	" + "' ," + "	3	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	310	" + ",'" + "	لاستیک زیر منبع پمپ ترمز پراید 	" + "' ," + "	3	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	311	" + ",'" + "	لاستیک تعادل پراید	" + "' ," + "	3	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	312	" + ",'" + "	لاستیک چاکدار پراید	" + "' ," + "	3	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	313	" + ",'" + "	گردگیر بالای کمک فنر جلو پراید 	" + "' ," + "	3	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	314	" + ",'" + "	گردگیر بالای کمک فنر عقب پراید 	" + "' ," + "	3	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	315	" + ",'" + "	لاستیک گرد بالای فنرلول جلو پراید(بدون لبه)	" + "' ," + "	3	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	316	" + ",'" + "	لاستیک گرد بالای فنرلول عقب پراید(لبه دار)	" + "' ," + "	3	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	317	" + ",'" + "	لاستیکهای نر و ماده بالا کمک عقب	" + "' ," + "	3	" + " ," + "1" + "); ";
            db.execSQL(str);
            str = "INSERT INTO " + TABLE_PARTS + " (" + KEY_PART_ID + "," + COL_DESCRIPTION + "," + COL_CAR_TYPE + "," + COL_STATUS + ") VALUES (" + "	318	" + ",'" + "	توپی سر کمک پراید	" + "' ," + "	3	" + " ," + "1" + "); ";
            db.execSQL(str);


//            str = " SELECT * FROM " + TABLE_PARTS;
//            Cursor c = db.rawQuery(str, null);
//            Log.d(" test ", c.getCount() + "");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
