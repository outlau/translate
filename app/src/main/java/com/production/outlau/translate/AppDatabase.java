package com.production.outlau.translate;

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Hashtable;

public class AppDatabase extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "wordDB";

    //  Table names
    private static final String TABLE_WORDS = "wordsTable";

    // Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_ENG = "en";
    private static final String KEY_SWE = "sv";

    public AppDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_WORDS_TABLE = "CREATE TABLE " + TABLE_WORDS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_ENG + " TEXT,"
                + KEY_SWE + " TEXT)";

        db.execSQL(CREATE_WORDS_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORDS);
        onCreate(db);
    }

    /*
     * INSERT QUERIES
     */

    public void insertValueToTable(String engValue, String sweValue) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String engVal = engValue;
        String sweVal = sweValue;
        if(engVal.contains("'")){
            engVal = engVal.replace("'","\'");
        }
        if(sweVal.contains("'")){
            sweVal = sweVal.replace("'","\'");
        }
        values.put(KEY_ENG, engVal);
        values.put(KEY_SWE, sweVal);
        db.insert(TABLE_WORDS, null, values);
        db.close();
    }

    /*
     * SELECT QUERIES
     */

    public Hashtable<String, String> getWords(String defaultLang) {
        SQLiteDatabase db = this.getReadableDatabase();

        Hashtable<String, String> returnList = new Hashtable();

        String firstCol = KEY_ENG;
        String secondCol = KEY_SWE;

        if(defaultLang == "sv") {
            firstCol = KEY_SWE;
            secondCol = KEY_ENG;
        }

        Cursor c = db.rawQuery("SELECT " + firstCol + "," + secondCol + " FROM " + TABLE_WORDS, null);

        if(c.moveToFirst()){
            do{
                returnList.put(c.getString(0),c.getString(1));
            }while(c.moveToNext());
        }
        c.close();
        db.close();
        return returnList;
    }

    /*
     * DELETE QUERIES
     */

    public void deleteValue(String value, String defaultLang){
        SQLiteDatabase db = this.getReadableDatabase();
        String col = KEY_ENG;
        if(defaultLang == "sv"){
            col = KEY_SWE;
        }
        String val = value;
        if(val.contains("'")){
            val = val.replace("'","''");
        }
        db.execSQL("delete from "+TABLE_WORDS+" where "+col+"='"+val+"'");
        db.close();
    }

    public void deleteAllInTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WORDS, null, null);
        db.close();
    }
}

