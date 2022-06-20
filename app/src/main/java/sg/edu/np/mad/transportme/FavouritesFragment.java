package sg.edu.np.mad.transportme;

import static sg.edu.np.mad.transportme.LoginPage.globalFavouriteBusStop;
import static sg.edu.np.mad.transportme.LoginPage.globalName;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

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
        Log.d("Yes", globalFavouriteBusStop.toString());
        View rootView = inflater.inflate(R.layout.fragment_favourites, container, false);
        if(globalFavouriteBusStop.size() > 0)
        {
            ApiBusStopService apiBusStopService = new ApiBusStopService(getContext());
            apiBusStopService.getBusService(globalFavouriteBusStop,new ApiBusStopService.VolleyResponseListener2() {
                @Override
                public void onError(String message) {
                    Toast.makeText(getContext(),"Cannot Get Bus Stops",Toast.LENGTH_LONG).show();
                }
                @Override
                public void onResponse(ArrayList<BusStop> busStopsLoaded) {


                    RecyclerView rv = rootView.findViewById(R.id.favouritesrecyclerView);
                    BusStopAdapter adapter = new BusStopAdapter(globalFavouriteBusStop,getContext());
                    LinearLayoutManager layout = new LinearLayoutManager(getContext());
                    rv.setAdapter(adapter);
                    rv.setLayoutManager(layout);
                }
            });
        }

        return rootView;
    }
}