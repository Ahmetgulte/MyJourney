package com.example.myjourney;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.example.myjourney.data.PlaceContract;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class PlanningFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    ListView listView;
    PlaceCursorAdapter placeCursorAdapter;
    FloatingActionButton fb;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PlanningFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlannigFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlanningFragment newInstance(String param1, String param2) {
        PlanningFragment fragment = new PlanningFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_plannig, container, false);
        fb=view.findViewById(R.id.floating_button);

        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),EditActivity.class);
                startActivity(intent);
            }
        });
        listView=view.findViewById(R.id.list_view_planning);
        View emptyView=view.findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);
        placeCursorAdapter=new PlaceCursorAdapter(getActivity(),null);
        listView.setAdapter(placeCursorAdapter);
        LoaderManager.getInstance(this).initLoader(1, null, this);
        return view;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection ={
                PlaceContract.CountryEntry._ID,
                PlaceContract.CountryEntry.COLUMN_COUNTRY_NAME,
                PlaceContract.CountryEntry.COLUMN_CITY_NAME,
                PlaceContract.CountryEntry.COLUMN_DATE,
                PlaceContract.CountryEntry.COLUMN_PHOTO
        };
        return new CursorLoader(getActivity(),
                PlaceContract.CountryEntry.CONTENT_URI,
                projection,
                PlaceContract.CountryEntry.COLUMN_SITUATION+"=?",
                new String[]{String.valueOf(PlaceContract.CountryEntry.SITUATION_NOTVISITED)},
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        placeCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        placeCursorAdapter.swapCursor(null);
    }
}
