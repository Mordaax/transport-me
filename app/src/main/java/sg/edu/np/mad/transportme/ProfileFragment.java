package sg.edu.np.mad.transportme;

import static android.content.Context.MODE_PRIVATE;
import static sg.edu.np.mad.transportme.LoginPage.globalEmail;
import static sg.edu.np.mad.transportme.LoginPage.globalFavouriteBusStop;
import static sg.edu.np.mad.transportme.LoginPage.globalName;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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

        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        TextInputEditText username = rootView.findViewById(R.id.profileuserName);
        TextInputEditText email = rootView.findViewById(R.id.profileuserEmail);

        username.setText(globalName);
        email.setText(globalEmail);
        Intent intent = new Intent(getActivity(), LoginPage.class);
        Button signoutButton = rootView.findViewById(R.id.signoutbutton);
        signoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SharedPreferences.Editor editor = getActivity().getSharedPreferences("LoginData", MODE_PRIVATE).edit();
                editor.putString("name", "");
                editor.putString("email", "");
                editor.putString("login","False" );
                editor.apply();
                globalName = "";
                globalEmail = "";

                LoginPage.SignedIn = false;
                globalFavouriteBusStop.clear();
                startActivity(intent);
            }
        });
        return rootView;

    }
}