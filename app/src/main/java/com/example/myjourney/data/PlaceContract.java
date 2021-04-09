package com.example.myjourney.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class PlaceContract {
    public static  final String CONTENT_AUTHORITY="com.example.places";
    public static final Uri BASE_CONTENT_URI=Uri.parse("content://"+CONTENT_AUTHORITY);
    public static final String PATH_PLACE="places";
    private PlaceContract(){

    }
    public static final class CountryEntry implements BaseColumns {
        public  static final Uri CONTENT_URI=Uri.withAppendedPath(BASE_CONTENT_URI,PATH_PLACE);

        public static final String TABLE_NAME="places";
        public static final String _ID=BaseColumns._ID;
        public static final String COLUMN_PHOTO="photoId";
        public static final String COLUMN_COUNTRY_NAME="country";
        public static final String COLUMN_CITY_NAME="city";
        public static final String COLUMN_DATE="date";
        public static final String COLUMN_SITUATION="situation";

        public final static int SITUATION_VISITED=0;
        public final static int SITUATION_NOTVISITED=1;

    }
}

