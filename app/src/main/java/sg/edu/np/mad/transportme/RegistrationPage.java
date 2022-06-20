package sg.edu.np.mad.transportme;

import static sg.edu.np.mad.transportme.LoadingScreen.globalBusStops;
import static sg.edu.np.mad.transportme.LoginPage.SignedIn;
import static sg.edu.np.mad.transportme.LoginPage.globalEmail;
import static sg.edu.np.mad.transportme.LoginPage.globalFavouriteBusStop;
import static sg.edu.np.mad.transportme.LoginPage.globalName;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class RegistrationPage extends AppCompatActivity {

    private Button registerUser, switchtoLogin;
    private EditText editTextName, editTextEmail, editTextPassword;
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

        progressBar = findViewById(R.id.progressBar);

        switchtoLogin = findViewById(R.id.gotologinpage);
        registerUser = findViewById(R.id.registerbutton);

        DatabaseUser dbUser = new DatabaseUser();


        /**if (mAuth.getCurrentUser() != null){
            startActivity(new Intent(RegistrationPage.this, MainActivity.class));
            finish();
        }**/

        Intent regIntent = new Intent(RegistrationPage.this, LoginPage.class);
        registerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //registerUser();
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                String name = editTextName.getText().toString().trim();

                if (name.isEmpty()) {
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

                editTextEmail.setEnabled(false);
                editTextName.setEnabled(false);
                editTextPassword.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);

                String hashedpw = BCrypt.withDefaults().hashToString(12, password.toCharArray());
                User u = new User(name, email, hashedpw, null);

// ...
                mDatabase = FirebaseDatabase.getInstance().getReference();

                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!SignedIn){
                            if(String.valueOf(snapshot.child("name").getValue()).equals(name)){
                                startActivity(new Intent(RegistrationPage.this, RegistrationPage.class));
                                Toast.makeText(RegistrationPage.this, "Please Enter another Username", Toast.LENGTH_LONG).show();
                                finish();
                            }
                            else if(String.valueOf(snapshot.child("email").getValue()).equals(email)){
                                Toast.makeText(RegistrationPage.this, "Email already registered", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(RegistrationPage.this, RegistrationPage.class));
                                finish();
                            }
                            else{
                                dbUser.add(u).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                                        editor.apply();
                                        SignedIn = true;
                                        startActivity(new Intent(RegistrationPage.this, MainActivity.class));
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        editTextEmail.setEnabled(true);
                                        editTextName.setEnabled(true);
                                        editTextPassword.setEnabled(true);
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(RegistrationPage.this, "Registration failed! Try again!", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                        else{
                            globalFavouriteBusStop.clear();
                            /*DatabaseReference favouriteref = myRef.child(globalName).child("Favourited");*/
                            for ( DataSnapshot favBS : snapshot.child("Favourited").getChildren()) {
                                String busStopCode = favBS.getKey();
                                for (int i = 0 ; i< globalBusStops.size(); i++){
                                    if (busStopCode.equals(globalBusStops.get(i).BusStopCode)){
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

                /*mDatabase.child("User").child(name).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        }
                        else {
                            if(String.valueOf(task.getResult().child("name").getValue()).equals(name)){
                                startActivity(new Intent(RegistrationPage.this, RegistrationPage.class));
                                Toast.makeText(RegistrationPage.this, "Please Enter another Username", Toast.LENGTH_LONG).show();
                                finish();
                            }
                            else if(String.valueOf(task.getResult().child("email").getValue()).equals(email)){
                                Toast.makeText(RegistrationPage.this, "Email already registered", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(RegistrationPage.this, RegistrationPage.class));
                                finish();
                            }
                            else{
                                dbUser.add(u).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                                        editor.apply();
                                        SignedIn = false;
                                        startActivity(new Intent(RegistrationPage.this, MainActivity.class));
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        editTextEmail.setEnabled(true);
                                        editTextName.setEnabled(true);
                                        editTextPassword.setEnabled(true);
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(RegistrationPage.this, "Registration failed! Try again!", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    }
                });*/

            }
        });
        Intent myIntent = new Intent(this, LoginPage.class);
        switchtoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(myIntent);
            }
        });


    }

    /**private void registerUser() {


        mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Log.d("Key", "Success");

                startActivity(new Intent(RegistrationPage.this, MainActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Key", "Oh no");
                editTextEmail.setEnabled(true);
                editTextName.setEnabled(true);
                editTextPassword.setEnabled(true);
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(RegistrationPage.this, "Registration failed! Try again!", Toast.LENGTH_LONG).show();
            }
        });
       mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegistrationPage.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //storing user information (need to add bus stops list)
                            User user = new User(name, email, null);


                            FirebaseDatabase database = FirebaseDatabase.getInstance("https://transportme-c607f-default-rtdb.asia-southeast1.firebasedatabase.app/");
                            DatabaseReference ref = database.getReference("users");
                            ref.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegistrationPage.this, "User has been registered Successfully", Toast.LENGTH_LONG).show();
                                        register = true;
                                        //redirect to login layout
                                        //Intent intent = new Intent(RegistrationPage.this, MainActivity.class);

                                    } else {
                                        Toast.makeText(RegistrationPage.this, "Registration failed! Try again!", Toast.LENGTH_LONG).show();
                                        register = false;
                                        editTextEmail.setEnabled(true);
                                        editTextName.setEnabled(true);
                                        editTextPassword.setEnabled(true);
                                    }
                                    progressBar.setVisibility(View.INVISIBLE);

                                }
                            });
                        } else {
                            Toast.makeText(RegistrationPage.this, "Registration failed! Try again!", Toast.LENGTH_LONG).show();
                            register = false;
                            editTextEmail.setEnabled(true);
                            editTextName.setEnabled(true);
                            editTextPassword.setEnabled(true);
                        }
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                        ;

                    });
                }**/
    }