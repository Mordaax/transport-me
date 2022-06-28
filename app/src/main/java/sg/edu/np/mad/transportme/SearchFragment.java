package sg.edu.np.mad.transportme;

import static sg.edu.np.mad.transportme.LoadingScreen.globalBusStops;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
        // On touch listener for icon at the side of the textview
        ArrayList<String> searchStrings = new ArrayList<String>();
        for (int i =0 ; i<globalBusStops.size();i++){
            searchStrings.add(globalBusStops.get(i).getDescription());
            searchStrings.add(globalBusStops.get(i).getBusStopCode());

        }
        AutoCompleteTextView searchAutoComplete = view.findViewById(R.id.autoCompleteTextView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, searchStrings);
        searchAutoComplete.setAdapter(adapter);

        searchAutoComplete.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (searchAutoComplete.getRight() - searchAutoComplete.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) { //If search icon is clicked, run code bellow
                        String searchText = String.valueOf(searchAutoComplete.getText());
                        if (searchText.equals("")){
                            Toast.makeText(getContext(),"No Bus Stops",Toast.LENGTH_LONG).show();
                            return true;
                        }
                        searchText = searchText.substring(0, 1).toUpperCase() + searchText.substring(1);

                        ArrayList<BusStop> searchBusStops = new ArrayList<>();
                        for (int i = 0; i<globalBusStops.size();i++) { //Compare search term with all description and Roadname in globalBusStop
                            if (globalBusStops.get(i).getDescription().contains(searchText) || globalBusStops.get(i).getRoadName().equalsIgnoreCase(searchText)){
                                searchBusStops.add(globalBusStops.get(i));
                            }
                        }


                        for (int i = 0; i<globalBusStops.size();i++){ // Find all bus stop with Similar bus stop code as search term
                            if (globalBusStops.get(i).getBusStopCode().equals(searchText)) {
                                searchBusStops.add(globalBusStops.get(i));
                            }
                        }

                        if(searchBusStops.size() > 90){ // Check if search term is too General like (Punggol), API call can only handle about 90 bus stops at a time
                            Toast.makeText(getContext(), "Please be more specific", Toast.LENGTH_SHORT).show();
                        }
                        else if (searchBusStops.size()>0){ //Call api to get bus stop timing for bus services
                            if (searchBusStops.size() > 20){
                                Toast.makeText(getContext(),"More than 20 bus stops found, Might take a while",Toast.LENGTH_LONG).show();
                            }
                            ApiBusStopService apiBusStopService = new ApiBusStopService(getActivity());
                            apiBusStopService.getBusService(searchBusStops,new ApiBusStopService.VolleyResponseListener2() {
                                @Override
                                public void onError(String message) {
                                    Log.d("Yes","api fail");
                                }
                                @Override
                                public void onResponse(ArrayList<BusStop> busStopsLoaded) {

                                    RecyclerView rv = view.findViewById(R.id.searchrecyclerView); //Load bus stops into RecyclerView in fragment
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

        // On click listner for keyboard search button
        searchAutoComplete.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String searchText = String.valueOf(searchAutoComplete.getText());
                    if (searchText.equals("")){
                        Toast.makeText(getContext(),"No Bus Stops",Toast.LENGTH_LONG).show();
                        return true;
                    }
                    searchText = searchText.substring(0, 1).toUpperCase() + searchText.substring(1);
                    ProgressDialog progressDialog = new ProgressDialog(getContext(),R.style.MyAlertDialogStyle);
                    progressDialog.show();
                    progressDialog.setContentView(R.layout.progress_dialog);
                    progressDialog.getWindow().setBackgroundDrawableResource(
                            android.R.color.transparent
                    );
                    ArrayList<BusStop> searchBusStops = new ArrayList<>();
                    for (int i = 0; i<globalBusStops.size();i++) {  //Compare search term with all description and Roadname in globalBusStop
                        if (globalBusStops.get(i).getDescription().contains(searchText) || globalBusStops.get(i).getRoadName().equalsIgnoreCase(searchText)){
                            searchBusStops.add(globalBusStops.get(i));
                        }
                    }


                    for (int i = 0; i<globalBusStops.size();i++){ // Find all bus stop with Similar bus stop code as search term
                        if (globalBusStops.get(i).getBusStopCode().equals(searchText)) {
                            searchBusStops.add(globalBusStops.get(i));
                        }
                    }

                    /*MainActivity main = (MainActivity) getActivity();*/
                    if(searchBusStops.size() > 90){ // Check if search term is too General like (Punggol), API call can only handle about 90 bus stops at a time
                        Toast.makeText(getContext(), "Please be more specific", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                    else if (searchBusStops.size()>0){
                        if (searchBusStops.size() > 20){
                            Toast.makeText(getContext(),"More than 20 bus stops found, Might take a while",Toast.LENGTH_LONG).show();
                        }
                        ApiBusStopService apiBusStopService = new ApiBusStopService(getActivity());
                        apiBusStopService.getBusService(searchBusStops,new ApiBusStopService.VolleyResponseListener2() {
                            @Override
                            public void onError(String message) {
                                Log.d("Yes","api fail");
                            }
                            @Override
                            public void onResponse(ArrayList<BusStop> busStopsLoaded) {

                                RecyclerView rv = view.findViewById(R.id.searchrecyclerView); //Load searched bus stops into recycler view
                                BusStopAdapter adapter = new BusStopAdapter(busStopsLoaded,getActivity());
                                LinearLayoutManager layout = new LinearLayoutManager(getActivity());
                                rv.setAdapter(adapter);
                                rv.setLayoutManager(layout);
                                progressDialog.dismiss();
                            }
                        });
                    }


                    else{
                        Toast.makeText(getContext(), "No Matching Bus Stops", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }

                    return true;
                }
                return false;
            }
        });


        return view;
    }
}