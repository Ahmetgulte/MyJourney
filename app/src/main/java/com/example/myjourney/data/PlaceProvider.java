package com.example.myjourney.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myjourney.data.PlaceContract.CountryEntry;
public class PlaceProvider extends ContentProvider {
    private static final String LOG_TAG = PlaceProvider.class.getSimpleName();

    private static final int PLACE = 100;
    private static final int PLACE_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(PlaceContract.CONTENT_AUTHORITY, PlaceContract.PATH_PLACE, PLACE);
        sUriMatcher.addURI(PlaceContract.CONTENT_AUTHORITY, PlaceContract.PATH_PLACE + "/#", PLACE_ID);
    }

    private PlaceHelper databaseHelper;
    Cursor cursor;


    @Override
    public boolean onCreate() {
        databaseHelper = new PlaceHelper(this.getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);
        switch (match) {
            case PLACE:
                cursor = db.query(CountryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PLACE_ID:
                selection = CountryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(CountryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI" + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int match = sUriMatcher.match(uri);

        switch (match) {
            case PLACE:
                return insertPlace(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported");
        }
    }

    private Uri insertPlace(Uri uri, ContentValues contentValues) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        String countryInserted=contentValues.getAsString(CountryEntry.COLUMN_COUNTRY_NAME);
        String cityInserted=contentValues.getAsString(CountryEntry.COLUMN_CITY_NAME);
        if(countryInserted ==null){
            throw new IllegalArgumentException("You should enter a country name");
        }
        if(cityInserted == null){
            throw new IllegalArgumentException("You should enter a city");
        }
        long id = db.insert(CountryEntry.TABLE_NAME, null, contentValues);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for" + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        int rowsDeleted;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PLACE:
                rowsDeleted = db.delete(CountryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PLACE_ID:
                selection = CountryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(CountryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported");
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }


    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PLACE:
                return updatePlace(uri, values, selection, selectionArgs);
            case PLACE_ID:
                selection = CountryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatePlace(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported");
        }

    }


    public int updatePlace(@NonNull Uri uri, @Nullable ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        String countryInserted=values.getAsString(CountryEntry.COLUMN_COUNTRY_NAME);
        String cityInserted=values.getAsString(CountryEntry.COLUMN_CITY_NAME);
        if(countryInserted ==null){
            throw new IllegalArgumentException("You should enter a country name");
        }
        if(cityInserted == null){
            throw new IllegalArgumentException("You should enter a city");
        }
        if(values.size() ==0){
            return 0;
        }
        int updatedRows = db.update(CountryEntry.TABLE_NAME, values, selection, selectionArgs);
        if (updatedRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return updatedRows;
    }
}
