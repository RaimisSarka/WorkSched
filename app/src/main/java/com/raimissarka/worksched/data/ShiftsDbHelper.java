package com.raimissarka.worksched.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.raimissarka.worksched.data.ShiftsContract.*;

/**
 * Created by Raimis on 1/14/2018.
 */

public class ShiftsDbHelper extends SQLiteOpenHelper {

    // The database name
    private static final String DATABASE_NAME = "shifts.db";

    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 1;



    public ShiftsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a table to hold workers data
        final String SQL_CREATE_SHIFT_TABLE = "CREATE TABLE " + ShiftsEntry.TABLE_NAME + " (" +
                ShiftsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ShiftsEntry.COLUMN_SHIFT_NAME + " TEXT NOT NULL, " +
                ShiftsEntry.COLUMN_SHIFT_NUMBER + " INTEGER NOT NULL, " +
                ShiftsEntry.COLUMN_SHIFT_TYPE + " INTEGER NOT NULL, " +
                ShiftsEntry.COLUMN_SHIFT_START_DATE + " TEXT" +
                "); ";

        db.execSQL(SQL_CREATE_SHIFT_TABLE);
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ShiftsEntry.TABLE_NAME);
        onCreate(db);
    }
}
