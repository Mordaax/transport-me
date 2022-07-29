package sg.edu.np.mad.transportme.user;

import static android.content.Context.MODE_PRIVATE;
import static sg.edu.np.mad.transportme.user.LoginPage.globalEmail;
import static sg.edu.np.mad.transportme.user.LoginPage.globalFavouriteBusStop;
import static sg.edu.np.mad.transportme.user.LoginPage.globalName;
import static sg.edu.np.mad.transportme.user.LoginPage.globalCloseness;
import static sg.edu.np.mad.transportme.user.LoginPage.globalReminder;
import static sg.edu.np.mad.transportme.user.LoginPage.globalReminderBusService;
import static sg.edu.np.mad.transportme.user.LoginPage.grbsChange;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import at.favre.lib.crypto.bcrypt.BCrypt;
import sg.edu.np.mad.transportme.R;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    FirebaseDatabase db = FirebaseDatabase.getInstance("https://transportme-c607f-default-rtdb.asia-southeast1.firebasedatabase.app/");
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
        DatabaseReference reference;
        reference = db.getReference("User");
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        TextInputEditText username = rootView.findViewById(R.id.profileuserName);
        TextInputEditText email = rootView.findViewById(R.id.profileuserEmail);
        TextInputEditText password = rootView.findViewById(R.id.profileuserPassword);
        SeekBar closenessSeekBar = rootView.findViewById(R.id.seekBar);
        TextView closenessTextView = rootView.findViewById(R.id.closeness);
        //allowing user to change the distance radius of bus stops around the user
        String radiustext;
        try{
            radiustext = "Bus Stop Radius ("+ String.valueOf((int) (globalCloseness*1000)) +" Meters)";
        } catch (Exception e) {
            radiustext = "Bus Stop Radius (300 Meters)";
            e.printStackTrace();
        }
        globalCloseness = Double.valueOf(300);
        closenessTextView.setText(radiustext);
        closenessSeekBar.setProgress((int) (globalCloseness*10));
        closenessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                closenessTextView.setText("Bus Stop Radius ("+ String.valueOf(i*100) +" Meters)");
                Double doublei = Double.valueOf(i);
                globalCloseness = doublei/10;
                SharedPreferences.Editor editor = getActivity().getSharedPreferences("LoginData", MODE_PRIVATE).edit();
                editor.putString("closeness", String.valueOf(globalCloseness));
                editor.commit();

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //filling up profile details
        username.setText(globalName);
        email.setText(globalEmail);
        password.setText("CHANGE PASSWORD");
        //makes it such that when user clicks on edit textbox, textbox is not automatically filled with "CHANGE PASSWORD"
        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    password.setText("");
                }
                else /*if(b == false && password.getEditableText().equals(""))*/{
                    if(!(password.getEditableText().length() > 0)){
                        password.setText("CHANGE PASSWORD");
                    }
                }
            }
        });
        Intent intent = new Intent(getActivity(), LoginPage.class);
        Button signoutButton = rootView.findViewById(R.id.signoutbutton);

        signoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                //signing user out and removing it from sharedpreferences
                SharedPreferences.Editor editor = getActivity().getSharedPreferences("LoginData", MODE_PRIVATE).edit();
                editor.putString("name", "");
                editor.putString("email", "");
                editor.putString("login","False" );
                editor.apply();
                globalName = "";
                globalEmail = "";
                globalReminder = null;
                globalReminderBusService = "";
                grbsChange = new MutableLiveData<>();
                LoginPage.SignedIn = false;
                globalFavouriteBusStop.clear();
                getActivity().finish();
                startActivity(intent); //user gets directed to login page after signing out
            }
        });
        Button editProfile = rootView.findViewById(R.id.editProfile);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String uEmail = email.getEditableText().toString();
                String uPassw = password.getEditableText().toString();
                if (!Patterns.EMAIL_ADDRESS.matcher(uEmail).matches()){ //checking for valid email address input
                    email.setError("Invalid Email Address");
                    email.requestFocus();
                    return;
                }
                if (uEmail.isEmpty()) {
                    email.setError("Email is required");
                    email.requestFocus();
                    return;
                }
                if (uPassw.length() < 6) {
                    password.setError("Password should be at least 6 characters");
                    password.requestFocus();
                    return;
                }
                if (uEmail == globalEmail) {
                    Toast.makeText(getContext(), "Nothing Changed", Toast.LENGTH_SHORT).show();
                    return;
                }

                AlertDialog.Builder confirmDataChange = new AlertDialog.Builder(getActivity());
                confirmDataChange.setTitle("Confirm edit profile?");
                confirmDataChange.setMessage("Your data will be changed");
                confirmDataChange.setCancelable(true);
                confirmDataChange.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //reset information filled in edit textbox if user cancels profile edit
                        email.setText(globalEmail);
                        password.setText("");
                        return;
                    }
                });
                confirmDataChange.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                //checks to see if user changed their email
                                if (!uEmail.equals(globalEmail)){
                                    reference.child(globalName).child("email").setValue(uEmail);
                                    //saving changes in sharedpreferences so that changes are reflected when the user stays logged in
                                    SharedPreferences.Editor editor = getActivity().getSharedPreferences("LoginData", MODE_PRIVATE).edit();
                                    globalEmail = uEmail;
                                    editor.putString("email", uEmail);
                                    editor.commit();

                                    Toast.makeText(getContext(), "Email Changed", Toast.LENGTH_SHORT).show();
                                }
                                //checks to see if user changed their hashed password
                                if (!BCrypt.verifyer().verify(uPassw.toCharArray(), snapshot.child(globalName).child("password").getValue().toString()).verified){
                                    if (String.valueOf(password.getText()).equals("CHANGE PASSWORD")){ //prevents user from accidentally making placeholder their password
                                        Toast.makeText(getContext(), "Password Not Changed", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    else{
                                        reference.child(globalName).child("password").setValue(BCrypt.withDefaults().hashToString(12, uPassw.toCharArray())); //changes password tied to user in database
                                        Toast.makeText(getContext(), "Password Changed", Toast.LENGTH_SHORT).show(); //lets user know change has been made
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });
                AlertDialog alert = confirmDataChange.create();
                alert.show();

            }
        });
        return rootView;

    }
}