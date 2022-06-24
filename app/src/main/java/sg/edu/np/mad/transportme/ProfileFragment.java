package sg.edu.np.mad.transportme;

import static android.content.Context.MODE_PRIVATE;
import static sg.edu.np.mad.transportme.LoginPage.globalEmail;
import static sg.edu.np.mad.transportme.LoginPage.globalFavouriteBusStop;
import static sg.edu.np.mad.transportme.LoginPage.globalName;
import static sg.edu.np.mad.transportme.LoginPage.globalCloseness;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.content.SharedPreferences;
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
        closenessTextView.setText("Bus Stop Radius ("+ String.valueOf((int) (globalCloseness*1000)) +" Meters)");
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

        username.setText(globalName);
        email.setText(globalEmail);
        password.setText("CHANGE PASSWORD");
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
        Button editProfile = rootView.findViewById(R.id.editProfile);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String uEmail = email.getEditableText().toString();
                String uPassw = password.getEditableText().toString();
                if (!Patterns.EMAIL_ADDRESS.matcher(uEmail).matches()){
                    email.setError("Invalid Email Address");
                    email.requestFocus();
                    return;
                }


                AlertDialog.Builder confirmDataChange = new AlertDialog.Builder(getActivity());
                confirmDataChange.setTitle("Confirm edit profile?");
                confirmDataChange.setMessage("Your data will be changed");
                confirmDataChange.setCancelable(true);
                confirmDataChange.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
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

                                if (!uEmail.equals(globalEmail)){
                                    //emailChanged = true;
                                    reference.child(globalName).child("email").setValue(uEmail); //sso thing makes email address put the old one
                                    SharedPreferences.Editor editor = getActivity().getSharedPreferences("LoginData", MODE_PRIVATE).edit();
                                    globalEmail = uEmail;
                                    editor.putString("email", uEmail);
                                    editor.commit();

                                    Toast.makeText(getContext(), "Email Changed", Toast.LENGTH_SHORT).show();
                                }
                                if (!BCrypt.verifyer().verify(uPassw.toCharArray(), snapshot.child(globalName).child("password").getValue().toString()).verified){
                                    //passwordChanged = true;
                                    Log.i("puii", String.valueOf(password.getText()));
                                    if (String.valueOf(password.getText()).equals("CHANGE PASSWORD")){
                                        return;
                                    }
                                    else{
                                        reference.child(globalName).child("password").setValue(BCrypt.withDefaults().hashToString(12, uPassw.toCharArray()));
                                        Toast.makeText(getContext(), "Password Changed", Toast.LENGTH_SHORT).show();
                                        Log.d("pui",password.getEditableText().toString());
                                    }
                                    /*if (!password.getEditableText().equals("CHANGE PASSWORD")){

                                    }
*/
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