package sg.edu.np.mad.transportme.user;

import static sg.edu.np.mad.transportme.views.LoadingScreen.globalBusStops;
import static sg.edu.np.mad.transportme.user.LoginPage.SignedIn;
import static sg.edu.np.mad.transportme.user.LoginPage.globalEmail;
import static sg.edu.np.mad.transportme.user.LoginPage.globalFavouriteBusStop;
import static sg.edu.np.mad.transportme.user.LoginPage.globalName;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import at.favre.lib.crypto.bcrypt.BCrypt;
import sg.edu.np.mad.transportme.BusStop;
import sg.edu.np.mad.transportme.R;
import sg.edu.np.mad.transportme.User;

public class RegistrationPage extends AppCompatActivity {

    private Button registerUser, switchtoLogin;
    private EditText editTextName, editTextEmail, editTextPassword, editTextConfirmPassword;
    private ProgressBar progressBar;
    private boolean register;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_page);

        editTextName = findViewById(R.id.reg_FullName);
        editTextEmail = findViewById(R.id.reg_Email);
        editTextPassword = findViewById(R.id.reg_Password);
        editTextConfirmPassword = findViewById(R.id.reg_Password2);
        progressBar = findViewById(R.id.progressBar);

        switchtoLogin = findViewById(R.id.gotologinpage);
        registerUser = findViewById(R.id.registerbutton);

        DatabaseUser dbUser = new DatabaseUser();

        registerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //registerUser();
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                String name = editTextName.getText().toString().trim();

                if (name.isEmpty()) { //handling errors for the inputs
                    editTextName.setError("Full name is required");
                    editTextName.requestFocus();
                    return;
                }

                if (email.isEmpty()) {
                    editTextEmail.setError("Email is required");
                    editTextEmail.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    editTextEmail.setError("Invalid email address");
                    editTextEmail.requestFocus();
                    return;
                }

                if (password.isEmpty()) {
                    editTextPassword.setError("Password is required");
                    editTextPassword.requestFocus();
                    return;
                }

                if (password.length() < 6) {
                    editTextPassword.setError("Password should be at least 6 characters");
                    editTextPassword.requestFocus();
                    return;
                }
                if (editTextConfirmPassword.getText().toString().length() <1){
                    editTextConfirmPassword.setError("Please Confirm Password");
                    editTextConfirmPassword.requestFocus();
                    return;
                }
                if (!password.equals(editTextConfirmPassword.getText().toString())){
                    editTextConfirmPassword.setError("Passwords are not the same");
                    editTextPassword.setError("Passwords are not the same");
                    editTextConfirmPassword.requestFocus();

                    return;
                }

                editTextEmail.setEnabled(false); //making sure user cannot edit the inputs when register is pressed and its loading
                editTextName.setEnabled(false);
                editTextPassword.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);

                String hashedpw = BCrypt.withDefaults().hashToString(12, password.toCharArray()); //hashing the password for security
                User u = new User(name, email, hashedpw, null); //creating the user class to add to the database

// ...
                mDatabase = FirebaseDatabase.getInstance().getReference(); //Getting reference to the database

                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!SignedIn){
                            if(String.valueOf(snapshot.child("name").getValue()).equals(name)){ //Making sure that the same name cannot be registered
                                startActivity(new Intent(RegistrationPage.this, RegistrationPage.class));
                                Toast.makeText(RegistrationPage.this, "Please Enter another Username", Toast.LENGTH_LONG).show();
                                finish();
                            }
                            else if(String.valueOf(snapshot.child("email").getValue()).equals(email)){ //Making sure that the same email cannot be registered
                                Toast.makeText(RegistrationPage.this, "Email already registered", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(RegistrationPage.this, RegistrationPage.class));
                                finish();
                            }
                            else{
                                dbUser.add(u).addOnSuccessListener(new OnSuccessListener<Void>() { //Add user to the database via the method in DatabaseUser
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(RegistrationPage.this, "Registration successful!", Toast.LENGTH_LONG).show();
                                        globalName = name;
                                        globalEmail = email;
                                        globalFavouriteBusStop = new ArrayList<BusStop>();
                                        SharedPreferences.Editor editor = getSharedPreferences("LoginData", MODE_PRIVATE).edit();
                                        editor.putString("name", globalName);
                                        editor.putString("email", globalEmail);
                                        editor.putString("login","True" );
                                        editor.apply(); //adding the names, email and login status to SharedPreference so if user exits the app and re enters, they will still be logged in
                                        SignedIn = true;
                                        startActivity(new Intent(RegistrationPage.this, LoginPage.class)); //Intent to MainActivity after registration
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        editTextEmail.setEnabled(true);
                                        editTextName.setEnabled(true);
                                        editTextPassword.setEnabled(true);
                                        progressBar.setVisibility(View.INVISIBLE); //If the registration fails, the progressbar must disappear so that users can edit inputs
                                        Toast.makeText(RegistrationPage.this, "Registration failed! Try again!", Toast.LENGTH_LONG).show(); //Showing that registration has failed
                                    }
                                });
                            }
                        }
                        if(SignedIn){
                            /*globalFavouriteBusStop.clear();*/ //globalFavouriteBusStop contains the favourited bus stops by the user loaded into Global variable
                            /*DatabaseReference favouriteref = myRef.child(globalName).child("Favourited");*/

                            for ( DataSnapshot favBS : snapshot.child("Favourited").getChildren()) { //On every favourite change reloads the globalFavouriteBusStop variable
                                String busStopCode = favBS.getKey();

                                for (int i = 0 ; i< globalBusStops.size(); i++){
                                    if (busStopCode.equals(globalBusStops.get(i).getBusStopCode())){
                                        globalFavouriteBusStop.add(globalBusStops.get(i));
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
        Intent myIntent = new Intent(this, LoginPage.class);
        switchtoLogin.setOnClickListener(new View.OnClickListener() { //User can go back to login page if they do not wish to register
            @Override
            public void onClick(View view) {
                startActivity(myIntent);
            }
        });

    }

}