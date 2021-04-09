package com.example.myjourney;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.myjourney.data.PlaceContract;

public class PlaceCursorAdapter extends CursorAdapter {
    TextView countryText;
    TextView cityText;
    RelativeLayout backgroundImage;
    ImageView editImage;
    TextView date;

    public PlaceCursorAdapter(Context context, Cursor c) {
        super(context, c,0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.country_list,parent,false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        countryText=view.findViewById(R.id.card_country);
        cityText=view.findViewById(R.id.card_city);
        backgroundImage=view.findViewById(R.id.relative_image);
        editImage=view.findViewById(R.id.edit);
        date=view.findViewById(R.id.date);

        int idColumn=cursor.getColumnIndex(PlaceContract.CountryEntry._ID);
        int countryName=cursor.getColumnIndex(PlaceContract.CountryEntry.COLUMN_COUNTRY_NAME);
        int cityName=cursor.getColumnIndex(PlaceContract.CountryEntry.COLUMN_CITY_NAME);
        int photo=cursor.getColumnIndex(PlaceContract.CountryEntry.COLUMN_PHOTO);
        int dateColumn=cursor.getColumnIndex(PlaceContract.CountryEntry.COLUMN_DATE);

        String currentCountry=cursor.getString(countryName);
        String currentCity=cursor.getString(cityName);
        String currentDate=cursor.getString(dateColumn);
        if(currentDate.equals("Choose a Date")){
            currentDate="In Future";
        }
        byte[] currentPhoto=cursor.getBlob(photo);
        if(currentPhoto.length > 0){
            Bitmap bitmap= BitmapFactory.decodeByteArray(currentPhoto,0,currentPhoto.length);
            BitmapDrawable backgorund=new BitmapDrawable(context.getResources(),bitmap);
            backgroundImage.setBackground(backgorund);
        }
        else{
            Drawable drawable=context.getResources().getDrawable(R.drawable.ic_launcher_background);
            backgroundImage.setBackground(drawable);
        }
        final long id=cursor.getInt(idColumn);


        countryText.setText(currentCountry);
        cityText.setText(currentCity);
        date.setText(currentDate);

        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,EditActivity.class);
                Uri currentPlaceUri= ContentUris.withAppendedId(PlaceContract.CountryEntry.CONTENT_URI,id);
                intent.setData(currentPlaceUri);
                context.startActivity(intent);
            }
        });

    }
}
