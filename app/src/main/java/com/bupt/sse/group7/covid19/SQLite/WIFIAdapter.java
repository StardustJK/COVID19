package com.bupt.sse.group7.covid19.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.bupt.sse.group7.covid19.model.BroadcastKey;
import com.bupt.sse.group7.covid19.model.WIFIConnection;

import java.util.Date;


public class WIFIAdapter {

    private static final String DB_NAME = "WIFIConnection.db";
    private static final String DB_TABLE_WIFIConnection = "connectioninfo";
    private static final String DB_TABLE_BroadcastKey = "broadcastkey";
    private static final int DB_VERSION = 1;

    public static final String KEY_ID = "_id";
    public static final String KEY_DATE = "datetime";
    public static final String KEY_DURATION ="duration";
    public static final String KEY_MAC ="mac";
    public static final String KEY_LEVEL ="level";
    public static final String KEY_ISSent = "isSent";
    public static final String KEY_NAME = "name";
    public static final String KEY_LOW_LEVEL ="lowLevel";
    public static final String KEY_HIGH_LEVEL ="highLevel";


    private SQLiteDatabase db;
    private final Context context;
    private DBOpenHelper dbOpenHelper;

    public WIFIAdapter(Context _context) {
        context = _context;
    }

    /** Close the database */
    public void close() {
        if (db != null){
            db.close();
            db = null;
        }
    }

    /** Open the database */
    public void open() throws SQLiteException {
        dbOpenHelper = new DBOpenHelper(context, DB_NAME, null, DB_VERSION);
        try {
            db = dbOpenHelper.getWritableDatabase();
        }
        catch (SQLiteException ex) {
            db = dbOpenHelper.getReadableDatabase();
        }
    }


    public long insertWIFIConnection(WIFIConnection connection) {
        ContentValues newValues = new ContentValues();


        newValues.put(KEY_DATE, WIFIConnection.DateToString(connection.datetime));
        newValues.put(KEY_ISSent, connection.isSent);
        newValues.put(KEY_MAC,connection.MAC_address);
        newValues.put(KEY_NAME,connection.name);
        newValues.put(KEY_DURATION,connection.duration);
        newValues.put(KEY_LEVEL,connection.level);
        return db.insert(DB_TABLE_WIFIConnection, null, newValues);
    }

    public WIFIConnection[] queryAllWIFIConnection() {
        Cursor results =  db.query(DB_TABLE_WIFIConnection, new String[] { KEY_ID, KEY_DATE, KEY_DURATION,KEY_MAC,KEY_NAME,KEY_LEVEL,KEY_ISSent},
                null, null, null, null, null);
        return ConvertToWIFIConnection(results);
    }

    public WIFIConnection[] queryUnsentWIFIConnection() {
        Cursor results =  db.query(DB_TABLE_WIFIConnection, new String[] { KEY_ID, KEY_DATE, KEY_DURATION,KEY_MAC,KEY_NAME,KEY_LEVEL,KEY_ISSent},
                KEY_ISSent + "= 0" , null, null, null, null);
        return ConvertToWIFIConnection(results);
    }

    public WIFIConnection[] queryWIFIConnectionByID(long id) {
        Cursor results =  db.query(DB_TABLE_WIFIConnection, new String[] {KEY_ID, KEY_DATE, KEY_DURATION,KEY_MAC,KEY_NAME,KEY_LEVEL,KEY_ISSent},
                KEY_ID + "=" + id, null, null, null, null);
        return ConvertToWIFIConnection(results);
    }

    public WIFIConnection[] queryWIFIConnectionByDate(Date date1, Date date2) {
        Cursor results =  db.query(DB_TABLE_WIFIConnection, new String[] { KEY_ID, KEY_DATE, KEY_DURATION,KEY_MAC,KEY_NAME,KEY_LEVEL,KEY_ISSent},
                "date("+KEY_DATE+") >= "+" date(?) and date("+KEY_DATE+") <= "+
                        " date(?) ORDER BY "+KEY_DATE +" DESC"
                , new String[] {WIFIConnection.DateToString(date1),WIFIConnection.DateToString(date2)}, null, null, null);
        return ConvertToWIFIConnection(results);
    }

    public BroadcastKey[] queryBroadcastKeyByDate(Date date1, Date date2) {
        Cursor results =  db.query(DB_TABLE_BroadcastKey, new String[] { KEY_ID, KEY_DATE, KEY_DURATION,KEY_MAC,KEY_NAME,KEY_LOW_LEVEL,KEY_HIGH_LEVEL},
                "date("+KEY_DATE+") >= "+" date(?) and date("+KEY_DATE+") <= "+
                        " date(?) ORDER BY "+KEY_DATE +" DESC"
                , new String[] {WIFIConnection.DateToString(date1),WIFIConnection.DateToString(date2)}, null, null, null);
        return ConvertToBroadcastKey(results);
    }

    public WIFIConnection[] queryWIFIConnectionByDate2(Date date1, Date date2) {
        Cursor results =  db.query(DB_TABLE_WIFIConnection, new String[] { KEY_ID, KEY_DATE, KEY_DURATION,KEY_MAC,KEY_NAME,KEY_LEVEL,KEY_ISSent},
                "datetime("+KEY_DATE+") >= "+" datetime(?) and datetime("+KEY_DATE+") <= "+
                        " datetime(?) ORDER BY "+KEY_DATE +" DESC"
                , new String[] {WIFIConnection.DateToString(date1),WIFIConnection.DateToString(date2)}, null, null, null);
        return ConvertToWIFIConnection(results);
    }


    private WIFIConnection[] ConvertToWIFIConnection(Cursor cursor){
        int resultCounts = cursor.getCount();
        if (resultCounts == 0 || !cursor.moveToFirst()){
            return null;
        }
        WIFIConnection[] connections = new WIFIConnection[resultCounts];
        for (int i = 0 ; i<resultCounts; i++){
            connections[i] = new WIFIConnection();
            connections[i].ID = cursor.getInt(0);
            connections[i].datetime = WIFIConnection.strToDate(cursor.getString(cursor.getColumnIndex(KEY_DATE)));
            connections[i].isSent = cursor.getInt(cursor.getColumnIndex(KEY_ISSent));
            connections[i].duration = cursor.getInt(cursor.getColumnIndex(KEY_DURATION));
            connections[i].MAC_address=cursor.getString(cursor.getColumnIndex(KEY_MAC));
            connections[i].name=cursor.getString(cursor.getColumnIndex(KEY_NAME));
            connections[i].level=cursor.getInt(cursor.getColumnIndex(KEY_LEVEL));
            cursor.moveToNext();
        }
        return connections;
    }

    public BroadcastKey[] queryAllBroadcastKey() {
        Cursor results =  db.query(DB_TABLE_BroadcastKey, new String[] { KEY_ID, KEY_DATE, KEY_DURATION,KEY_MAC,KEY_NAME,KEY_LOW_LEVEL,KEY_HIGH_LEVEL},
                null, null, null, null, null);
        return ConvertToBroadcastKey(results);
    }

    private BroadcastKey[] ConvertToBroadcastKey(Cursor cursor){
        int resultCounts = cursor.getCount();
        if (resultCounts == 0 || !cursor.moveToFirst()){
            return null;
        }
        BroadcastKey[] connections = new BroadcastKey[resultCounts];
        for (int i = 0 ; i<resultCounts; i++){
            connections[i] = new BroadcastKey();
            connections[i].ID = cursor.getInt(0);
            connections[i].datetime = WIFIConnection.strToDate(cursor.getString(cursor.getColumnIndex(KEY_DATE)));
           connections[i].duration = cursor.getInt(cursor.getColumnIndex(KEY_DURATION));
            connections[i].MAC_address=cursor.getString(cursor.getColumnIndex(KEY_MAC));
            connections[i].name=cursor.getString(cursor.getColumnIndex(KEY_NAME));
            connections[i].low_level=cursor.getInt(cursor.getColumnIndex(KEY_LOW_LEVEL));
            connections[i].high_level=cursor.getInt(cursor.getColumnIndex(KEY_HIGH_LEVEL));
            cursor.moveToNext();
        }
        return connections;
    }

    public long deleteAllWIFIConnection() {
        return db.delete(DB_TABLE_WIFIConnection, null, null);
    }


    public long deleteOneWIFIConnection(long id) {
        return db.delete(DB_TABLE_WIFIConnection,  KEY_ID + "=" + id, null);
    }

    public long deleteUselessWIFIConnection(Date date) {
        return db.delete(DB_TABLE_WIFIConnection,  " datetime("+KEY_DATE+") <= datetime(?)",
                new String[] {WIFIConnection.DateToString(date)});
    }

    public long updateWIFIConnection(long id , WIFIConnection connection){
        ContentValues updateValues = new ContentValues();
        updateValues.put(KEY_DATE, WIFIConnection.DateToString(connection.datetime));
        updateValues.put(KEY_ISSent, connection.isSent);
        updateValues.put(KEY_DURATION,connection.duration);
        updateValues.put(KEY_LEVEL,connection.level);

        return db.update(DB_TABLE_WIFIConnection, updateValues,  KEY_ID + "=" + id, null);
    }

    public long insertBroadcastKey(BroadcastKey broadcastKey) {
        ContentValues newValues = new ContentValues();

        newValues.put(KEY_DATE, WIFIConnection.DateToString(broadcastKey.datetime));
        newValues.put(KEY_MAC,broadcastKey.MAC_address);
        newValues.put(KEY_NAME,broadcastKey.name);
        newValues.put(KEY_DURATION,broadcastKey.duration);
        newValues.put(KEY_LOW_LEVEL,broadcastKey.low_level);
        newValues.put(KEY_HIGH_LEVEL,broadcastKey.high_level);
        return db.insert(DB_TABLE_BroadcastKey, null, newValues);
    }




    private static class DBOpenHelper extends SQLiteOpenHelper {

        public DBOpenHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        private static final String DB_CREATE_WIFIConnection = "create table " +
                DB_TABLE_WIFIConnection + " (" + KEY_ID + " integer primary key autoincrement, " +
                KEY_DATE + " text not null," + KEY_DURATION+" integer not null,"+
                KEY_NAME +" text not null,"+KEY_MAC +" text not null,"+ KEY_LEVEL+" integer not null,"+
                KEY_ISSent + " integer not null" +
                 ");";

        private static final String DB_CREATE_BroadcastKey = "create table " +
                DB_TABLE_BroadcastKey + " (" + KEY_ID + " integer primary key autoincrement, " +
                KEY_DATE + " text not null," + KEY_DURATION+" integer not null,"+
                KEY_NAME +" text not null,"+KEY_MAC +" text not null,"+
                KEY_LOW_LEVEL+" integer not null," +KEY_HIGH_LEVEL +" integer not null"+
                ");";



        @Override
        public void onOpen(SQLiteDatabase _db) {
            super.onOpen(_db);
            if(!_db.isReadOnly()) {
                _db.execSQL("PRAGMA foreign_keys = ON;");
            }
        }

        @Override
        public void onCreate(SQLiteDatabase _db)
        {
            _db.execSQL(DB_CREATE_WIFIConnection);
            _db.execSQL(DB_CREATE_BroadcastKey);

        }

        @Override
        public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion) {
            _db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_WIFIConnection);
            _db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_BroadcastKey);
            onCreate(_db);
        }
    }
}