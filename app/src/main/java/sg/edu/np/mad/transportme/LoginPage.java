package sg.edu.np.mad.transportme;

import static sg.edu.np.mad.transportme.LoadingScreen.globalBusStops;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class LoginPage extends AppCompatActivity implements View.OnClickListener{
    public static String globalName = "";
    public static String globalEmail = "";
    public static Boolean SignedIn = false;
    public static ArrayList<BusStop> globalFavouriteBusStop = new ArrayList<>();
    public static Double globalCloseness;
    //private TextView register;
    private EditText editTextEmail, editTextPassword;
    private Button signIn, register;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        /*Remove this lol */
        //startActivity(new Intent(LoginPage.this, MainActivity.class));
        signIn = findViewById(R.id.loginbutton);
        signIn.setOnClickListener(this);

        register = findViewById(R.id.gotoregisterpage);
        register.setOnClickListener(this);

        editTextEmail = findViewById(R.id.emailAddress);
        editTextPassword = findViewById(R.id.password);

        progressBar = findViewById(R.id.progressBar);
        /* here*/
        SharedPreferences prefs =  getSharedPreferences("LoginData", MODE_PRIVATE);
        globalName = prefs.getString("name", "");
        globalEmail = prefs.getString("email", "");
        SignedIn = prefs.getString("login", "").equals("True");
        globalCloseness = Double.valueOf(prefs.getString("closeness","0.5"));
        if(SignedIn){ // If User is signedIn, phone connects to Firebase and gets favourite bus stops

            FirebaseDatabase db = FirebaseDatabase.getInstance("https://transportme-c607f-default-rtdb.asia-southeast1.firebasedatabase.app/");
            DatabaseReference myRef = db.getReference("User");
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(SignedIn){
                        globalFavouriteBusStop.clear();
                        /*DatabaseReference favouriteref = myRef.child(globalName).child("Favourited");*/
                        for (DataSnapshot favBS : snapshot.child(globalName).child("Favourited").getChildren()) {
                            String busStopCode = favBS.getKey();
                            for (int i = 0 ; i< globalBusStops.size(); i++){
                                if (busStopCode.equals(globalBusStops.get(i).getBusStopCode())){
                                    globalFavouriteBusStop.add(globalBusStops.get(i)); //Update globalFavoruiteBusStop with favourited bus Stops
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            Intent intent = new Intent(LoginPage.this, MainActivity.class);
            startActivity(intent);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.loginbutton: //If login button is clicked
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();

                if(email.isEmpty()){
                    editTextEmail.setError("Email is required");
                    editTextEmail.requestFocus();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){ //Check for email pattern
                    editTextEmail.setError("Please enter a valid email");
                    editTextEmail.requestFocus();
                    return;
                }

                if(password.isEmpty()){ //Check if password is empty
                    editTextPassword.setError("Password is empty");
                    editTextPassword.requestFocus();
                    return;
                }
                if(password.length() < 6){ //Check if password meets the requirement
                    editTextPassword.setError("Min password length is 6 characters");
                    editTextPassword.requestFocus();
                    return;
                }

                editTextEmail.setEnabled(false);
                editTextPassword.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE); //Ensure that user cannot edit the input while it is loading
                FirebaseDatabase db = FirebaseDatabase.getInstance("https://transportme-c607f-default-rtdb.asia-southeast1.firebasedatabase.app/"); //Initialise Database
                DatabaseReference myRef = db.getReference("User"); //Database Reference User
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot user : snapshot.getChildren()) {
                            if (user.child("email").getValue().toString().equals(email)) {
                                if (BCrypt.verifyer().verify(password.toCharArray(), user.child("password").getValue().toString()).verified){ //Making sure the password of the email account is correct
                                    if(!SignedIn)
                                    {

                                        Toast.makeText(LoginPage.this, "Login Successful!", Toast.LENGTH_LONG).show();
                                        globalName = user.child("name").getValue().toString();
                                        globalEmail = user.child("email").getValue().toString();
                                        SignedIn = true;

                                        /* here*/
                                        SharedPreferences.Editor editor = getSharedPreferences("LoginData", MODE_PRIVATE).edit();
                                        editor.putString("name", globalName);
                                        editor.putString("email", globalEmail);
                                        editor.putString("login","True" );
                                        editor.apply();
                                        Intent intent = new Intent(LoginPage.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                }
                            }

                        }

                        if(SignedIn){ //On every favourite change reloads the globalFavouriteBusStop variable
                            globalFavouriteBusStop.clear();
                            /*DatabaseReference favouriteref = myRef.child(globalName).child("Favourited");*/
                            for (DataSnapshot favBS : snapshot.child(globalName).child("Favourited").getChildren()) {
                                String busStopCode = favBS.getKey();
                                for (int i = 0 ; i< globalBusStops.size(); i++){
                                    if (busStopCode.equals(globalBusStops.get(i).getBusStopCode())){
                                        globalFavouriteBusStop.add(globalBusStops.get(i));
                                    }
                                }
                            }
                        }

                        if (!SignedIn){ //If the password is entered incorrectly or if there is no such email existing in the database
                            Toast.makeText(LoginPage.this, "Invalid Credentials, please try again", Toast.LENGTH_LONG).show();
                            editTextEmail.setEnabled(true);
                            editTextPassword.setEnabled(true);
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;

            case R.id.gotoregisterpage: //when register is pressed
                startActivity(new Intent(this, RegistrationPage.class));
                break;
        }

    }
    //Firebase authentication code, Not Used
}