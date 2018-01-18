package com.raimissarka.worksched.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.raimissarka.worksched.data.EmploymentsContract.*;

/**
 * Created by Raimis on 1/16/2018.
 */

public class EmploymentsDbHelper extends SQLiteOpenHelper {


    // The database name
    private static final String DATABASE_NAME = "employments.db";

    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 1;



    public EmploymentsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a table to hold employments data
        final String SQL_CREATE_EMPLOYMENT_TABLE = "CREATE TABLE " + EmploymentsEntry.TABLE_NAME + " (" +
                EmploymentsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                EmploymentsEntry.COLUMN_EMPLOYMENT_NAME + " TEXT NOT NULL " +
                "); ";

        db.execSQL(SQL_CREATE_EMPLOYMENT_TABLE);
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + EmploymentsEntry.TABLE_NAME);
        onCreate(db);
    }
}
