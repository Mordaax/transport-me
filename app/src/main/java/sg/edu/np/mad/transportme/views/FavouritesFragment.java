package sg.edu.np.mad.transportme.views;

import static sg.edu.np.mad.transportme.user.LoginPage.globalFavouriteBusStop;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import sg.edu.np.mad.transportme.BusStop;
import sg.edu.np.mad.transportme.BusStopAdapter;
import sg.edu.np.mad.transportme.R;
import sg.edu.np.mad.transportme.api.ApiBusStopService;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavouritesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavouritesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FavouritesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FavouritesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FavouritesFragment newInstance(String param1, String param2) {
        FavouritesFragment fragment = new FavouritesFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_favourites, container, false);
        if(globalFavouriteBusStop.size() > 0)       //Does not run this API function if there is not bus stops in the favourited list
        {
            ApiBusStopService apiBusStopService = new ApiBusStopService(getContext());
            apiBusStopService.getBusService(globalFavouriteBusStop,new ApiBusStopService.VolleyResponseListener2() {    //Gets the bus services for the bus stop
                @Override
                public void onError(String message) {
                    Toast.makeText(getContext(),"Cannot Get Bus Stops",Toast.LENGTH_LONG).show();
                }
                @Override
                public void onResponse(ArrayList<BusStop> busStopsLoaded) {
                    RecyclerView rv = rootView.findViewById(R.id.favouritesrecyclerView);               //Declare recyclerview
                    BusStopAdapter adapter = new BusStopAdapter(globalFavouriteBusStop,getContext());   //Create the RecyclerView for BusStop
                    LinearLayoutManager layout = new LinearLayoutManager(getContext());                 //LayoutManager tells RecyclerView how to draw the list
                    rv.setAdapter(adapter);
                    rv.setLayoutManager(layout);        //Pass in layout and adapter
                }
            });
        }

        return rootView;
    }
}