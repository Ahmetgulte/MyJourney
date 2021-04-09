package com.example.myjourney;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.myjourney.data.PlaceContract;
import com.example.myjourney.data.PlaceContract.CountryEntry;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private boolean mPlaceHasChanged =false;
    private ImageView imageView;
    private EditText countryName;
    private EditText cityName;
    private Spinner situation;
    private int mSituation=CountryEntry.SITUATION_NOTVISITED;
    private DatePickerDialog datePickerDialog;
    private Button dateButton;
    private Bitmap selectedImage;
    private Uri currentPlaceUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        imageView=findViewById(R.id.image);
        countryName=findViewById(R.id.country_name);
        cityName=findViewById(R.id.city_name);
        situation=findViewById(R.id.situation_spinner);
        imageView.setOnTouchListener(mTouchListener);
        countryName.setOnTouchListener(mTouchListener);
        cityName.setOnTouchListener(mTouchListener);
        situation.setOnTouchListener(mTouchListener);
        setupSpinner();
        initDatePicker();
        dateButton = findViewById(R.id.datePickerButton);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(EditActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(EditActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                else{
                    Intent intentToGallery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intentToGallery,2);

                }
            }
        });

        Intent intent=getIntent();
        currentPlaceUri=intent.getData();
        if(currentPlaceUri == null){
            setTitle("Add a Place");
        }
        else {
            setTitle("Edit the Place");
            LoaderManager.getInstance(this).initLoader(0, null, this);
        }

    }

    public void savePlace(){
        ContentValues contentValues=new ContentValues();
        String country=countryName.getText().toString().trim();
        String city=cityName.getText().toString().trim();
        String date=dateButton.getText().toString();

        if(selectedImage ==null){
            contentValues.put(CountryEntry.COLUMN_PHOTO,new byte[] {});
        }
        else{
            Bitmap smallImage=makeSmallerImage(selectedImage,300);

            ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
            smallImage.compress(Bitmap.CompressFormat.PNG,50,outputStream);
            byte[] byteArray=outputStream.toByteArray();
            contentValues.put(CountryEntry.COLUMN_PHOTO,byteArray);
        }


        contentValues.put(CountryEntry.COLUMN_COUNTRY_NAME,country);
        contentValues.put(CountryEntry.COLUMN_CITY_NAME,city);
        contentValues.put(CountryEntry.COLUMN_DATE,date);
        contentValues.put(CountryEntry.COLUMN_SITUATION,mSituation);

        if(currentPlaceUri == null){
            Uri newUri = getApplication().getContentResolver().insert(CountryEntry.CONTENT_URI,contentValues);

            if(newUri == null){
                Toast.makeText(this,"Failed",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this,"Succesfully done!",Toast.LENGTH_SHORT).show();
            }
        }
        else{
            int rowAffected = getApplication().getContentResolver().update(currentPlaceUri,contentValues,null,null);
            if (rowAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, "failed",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, "Succesfully done",
                        Toast.LENGTH_SHORT).show();
            }
        }





    }
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mPlaceHasChanged = true;
            return false;
        }
    };
    public Bitmap makeSmallerImage(Bitmap image,int maxSize){
        int width= image.getWidth();
        int height=image.getHeight();

        float bitmapRatio =(float) width /(float) height ;
        if(bitmapRatio > 1) {
            width=maxSize;
            height=(int) (width/bitmapRatio);
        }
        else{
            height=maxSize;
            width=(int) (height/bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image,width,height,true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 1){
            if(grantResults.length > 0 && grantResults[0] ==PackageManager.PERMISSION_GRANTED){
                Intent intentToGallery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentToGallery,2);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 2 && resultCode== RESULT_OK && data !=null){
            Uri imageData=data.getData();
            try {
                if(Build.VERSION.SDK_INT >= 28){
                    ImageDecoder.Source source=ImageDecoder.createSource(this.getContentResolver(),imageData);
                    selectedImage =ImageDecoder.decodeBitmap(source);
                }
                else{
                    selectedImage=MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageData);
                }
                imageView.setImageBitmap(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.editor_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Do nothing for now
                savePlace();
                Intent intent=new Intent(EditActivity.this,MainActivity.class);
                startActivity(intent);
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                if(currentPlaceUri != null){
                    AlertDialog.Builder alert=new AlertDialog.Builder(this);
                    alert.setTitle("Warning");
                    alert.setMessage("Are you sure?");
                    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getContentResolver().delete(currentPlaceUri, null, null);
                            finish();
                        }
                    });
                    alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alert.create().show();
                }else{
                    // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                    // Create a click listener to handle the user confirming that
                    // changes should be discarded.
                    DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // User clicked "Discard" button, navigate to parent activity.
                            Intent intent2=new Intent(EditActivity.this,MainActivity.class);
                            startActivity(intent2);
                        }
                    };

                    // Show a dialog that notifies the user they have unsaved changes
                    showUnsavedChangesDialog(discardButtonClickListener);
                }


                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setMessage("Unsaved");
        builder.setPositiveButton("Discard", discardButtonClickListener);
        builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void initDatePicker()
    {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                month = month + 1;
                String date = makeDateString(day, month, year);
                dateButton.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        //datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

    }
    private String makeDateString(int day, int month, int year)
    {
        return year+"/"+month+"/"+day;
    }

    public void openDatePicker(View view)
    {
        datePickerDialog.show();
    }


    public void setupSpinner(){

        ArrayAdapter situationSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);
        situationSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        situation.setAdapter(situationSpinnerAdapter);
        situation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals("Visited")) {
                        mSituation = CountryEntry.SITUATION_VISITED;
                    } else {
                        mSituation = CountryEntry.SITUATION_NOTVISITED;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSituation = CountryEntry.SITUATION_NOTVISITED;
            }
        });

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection ={
                CountryEntry.COLUMN_COUNTRY_NAME,
                CountryEntry.COLUMN_CITY_NAME,
                CountryEntry.COLUMN_DATE,
                CountryEntry.COLUMN_PHOTO,
                CountryEntry.COLUMN_SITUATION
        };
        return new CursorLoader(this,currentPlaceUri,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        System.out.println(data.moveToFirst());
        if(data.moveToFirst()) {
            int countryNameC = data.getColumnIndex(PlaceContract.CountryEntry.COLUMN_COUNTRY_NAME);
            int cityNameC = data.getColumnIndex(PlaceContract.CountryEntry.COLUMN_CITY_NAME);
            int photoC = data.getColumnIndex(PlaceContract.CountryEntry.COLUMN_PHOTO);
            int dateC = data.getColumnIndex(CountryEntry.COLUMN_DATE);
            int situationC = data.getColumnIndex(CountryEntry.COLUMN_SITUATION);

            String currentCountry = data.getString(countryNameC);
            String currentCity = data.getString(cityNameC);
            byte[] currentPhoto = data.getBlob(photoC);
            String currentDate = data.getString(dateC);
            int currentSituation = data.getInt(situationC);

            Bitmap bitmap = BitmapFactory.decodeByteArray(currentPhoto, 0, currentPhoto.length);

            countryName.setText(currentCountry);
            cityName.setText(currentCity);
            imageView.setImageBitmap(bitmap);
            selectedImage=bitmap;
            dateButton.setText(currentDate);
            switch (currentSituation){
                case CountryEntry.SITUATION_VISITED:
                    situation.setSelection(0);
                    break;
                case CountryEntry.SITUATION_NOTVISITED:
                    situation.setSelection(1);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

}