package sg.edu.np.mad.transportme;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.text.method.Touch;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ablanco.zoomy.Zoomy;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MrtMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MrtMapFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MrtMapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MrtMapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MrtMapFragment newInstance(String param1, String param2) {
        MrtMapFragment fragment = new MrtMapFragment();
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
        View view = inflater.inflate(R.layout.fragment_mrt_map, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.mrtMapImage);


        Zoomy.Builder builder = new Zoomy.Builder(getActivity())
                .target(imageView)
                .animateZooming(false)
                .enableImmersiveMode(false);

        builder.register();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mrt_map, container, false);
    }
}

