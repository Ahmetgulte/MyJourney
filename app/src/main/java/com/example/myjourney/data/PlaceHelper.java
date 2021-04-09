package com.example.myjourney.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.myjourney.data.PlaceContract;
import com.example.myjourney.data.PlaceContract.CountryEntry;
import androidx.annotation.Nullable;

public class PlaceHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG=PlaceHelper.class.getSimpleName();
    private static final String DATABASE_NAME="countries.db";
    private static final int DATABASE_VERSION=1;
    public PlaceHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_LITE_CREATE="CREATE TABLE "+CountryEntry.TABLE_NAME+" ("
                +CountryEntry._ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
                +CountryEntry.COLUMN_PHOTO +" BLOB,"
                +CountryEntry.COLUMN_COUNTRY_NAME+" TEXT NOT NULL,"
                +CountryEntry.COLUMN_CITY_NAME+" TEXT NOT NULL,"
                +CountryEntry.COLUMN_DATE+" TEXT,"
                +CountryEntry.COLUMN_SITUATION +" INTEGER);";
        db.execSQL(SQL_LITE_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

