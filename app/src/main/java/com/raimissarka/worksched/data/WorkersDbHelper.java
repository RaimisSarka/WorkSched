package com.raimissarka.worksched.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.raimissarka.worksched.data.WorkersContract.*;
/**
 * Created by Raimis on 1/9/2018.
 */

public class WorkersDbHelper extends SQLiteOpenHelper {

    // The database name
    private static final String DATABASE_NAME = "workers.db";

    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 2;



    public WorkersDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create a table to hold workers data
        final String SQL_CREATE_WORKERS_TABLE = "CREATE TABLE " + WorkersEntry.TABLE_NAME + " (" +
                WorkersEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                WorkersEntry.COLUMN_WORKER_NAME + " TEXT NOT NULL, " +
                WorkersEntry.COLUMN_WORKER_POSITION + " TEXT NOT NULL, " +
                WorkersEntry.COLUMN_SHIFT_DEPENDENCY + " INTEGER NOT NULL, " +
                WorkersEntry.COLUMN_EMPLOYMENT_DEPENDENCY + " INTEGER NOT NULL, " +
                WorkersEntry.COLUMN_PHONE_NUMBER + " TEXT" +
                "); ";

        db.execSQL(SQL_CREATE_WORKERS_TABLE);
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + WorkersEntry.TABLE_NAME);
        onCreate(db);
    }


}
