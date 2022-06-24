package sg.edu.np.mad.transportme;

import static sg.edu.np.mad.transportme.LoadingScreen.globalBusStops;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Geocoder geocoder2;
    public SearchFragment() {

        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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



        /**/

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        EditText searchBar = (EditText) view.findViewById(R.id.editTextSearch);
        /*Button GoToMap = view.findViewById(R.id.buttonFind);*/
       /* Double latitude;
        Double longitude;
        GoogleMap map = ((SupportMapFragment) getFragmentManager()
                .findFragmentById(R.id.map)).getMapAsync();*/

        searchBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (searchBar.getRight() - searchBar.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        String searchText = String.valueOf(searchBar.getText());
                        searchText = searchText.substring(0, 1).toUpperCase() + searchText.substring(1);

                        ArrayList<BusStop> searchBusStops = new ArrayList<>();
                        for (int i = 0; i<globalBusStops.size();i++) {
                            if (globalBusStops.get(i).getDescription().contains(searchText) || globalBusStops.get(i).getRoadName().equalsIgnoreCase(searchText)){
                                searchBusStops.add(globalBusStops.get(i));
                            }
                        }


                        for (int i = 0; i<globalBusStops.size();i++){
                            if (globalBusStops.get(i).getBusStopCode().equals(searchText)) {
                                searchBusStops.add(globalBusStops.get(i));
                            }
                        }

                        /*MainActivity main = (MainActivity) getActivity();*/
                        if(searchBusStops.size() > 90){
                            Toast.makeText(getContext(), "Please be more specific", Toast.LENGTH_SHORT).show();
                        }
                        else if (searchBusStops.size()>0){
                            ApiBusStopService apiBusStopService = new ApiBusStopService(getActivity());
                            apiBusStopService.getBusService(searchBusStops,new ApiBusStopService.VolleyResponseListener2() {
                                @Override
                                public void onError(String message) {
                                    Log.d("Yes","api fail");
                                }
                                @Override
                                public void onResponse(ArrayList<BusStop> busStopsLoaded) {

                                    RecyclerView rv = view.findViewById(R.id.searchrecyclerView);
                                    BusStopAdapter adapter = new BusStopAdapter(busStopsLoaded,getActivity());
                                    LinearLayoutManager layout = new LinearLayoutManager(getActivity());
                                    rv.setAdapter(adapter);
                                    rv.setLayoutManager(layout);
                                }
                            });
                        }

                        else{
                            Toast.makeText(getContext(), "No Matching Bus Stops", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                }
                return false;
            }
        });
        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String searchText = String.valueOf(searchBar.getText());
                    searchText = searchText.substring(0, 1).toUpperCase() + searchText.substring(1);

                    ArrayList<BusStop> searchBusStops = new ArrayList<>();
                    for (int i = 0; i<globalBusStops.size();i++) {
                        if (globalBusStops.get(i).getDescription().contains(searchText) || globalBusStops.get(i).getRoadName().equalsIgnoreCase(searchText)){
                            searchBusStops.add(globalBusStops.get(i));
                        }
                    }


                    for (int i = 0; i<globalBusStops.size();i++){
                        if (globalBusStops.get(i).getBusStopCode().equals(searchText)) {
                            searchBusStops.add(globalBusStops.get(i));
                        }
                    }

                    /*MainActivity main = (MainActivity) getActivity();*/
                    if(searchBusStops.size() > 90){
                        Toast.makeText(getContext(), "Please be more specific", Toast.LENGTH_SHORT).show();
                    }
                    else if (searchBusStops.size()>0){
                        ApiBusStopService apiBusStopService = new ApiBusStopService(getActivity());
                        apiBusStopService.getBusService(searchBusStops,new ApiBusStopService.VolleyResponseListener2() {
                            @Override
                            public void onError(String message) {
                                Log.d("Yes","api fail");
                            }
                            @Override
                            public void onResponse(ArrayList<BusStop> busStopsLoaded) {

                                RecyclerView rv = view.findViewById(R.id.searchrecyclerView);
                                BusStopAdapter adapter = new BusStopAdapter(busStopsLoaded,getActivity());
                                LinearLayoutManager layout = new LinearLayoutManager(getActivity());
                                rv.setAdapter(adapter);
                                rv.setLayoutManager(layout);
                            }
                        });
                    }

                    else{
                        Toast.makeText(getContext(), "No Matching Bus Stops", Toast.LENGTH_SHORT).show();
                    }


                    /*GoToMap.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            LinearLayout mapandrv = view.findViewById(R.id.MapAndRV);
                            FrameLayout fragmentlayout = view.findViewById(R.id.frame_layout);
                            *//*MainActivity main = *//*
                            *//*MainActivity main = (MainActivity) getActivity();*//*
                            *//*MainActivity main = (MainActivity) getParentFragment().getActivity();*//*

                            fragmentlayout.setVisibility(View.INVISIBLE);
                            mapandrv.setVisibility(View.VISIBLE);
                            ((MainActivity)inflater.getContext()).moveMapsCamera(closeBusStops.get(0).Latitude,closeBusStops.get(0).Longitude);

*//*
                            main.moveMapsCamera(closeBusStops.get(0).Latitude,closeBusStops.get(0).Longitude);
*//*


                        }
                    });*/
                    return true;
                }
                return false;
            }
        });


        return view;
    }
}