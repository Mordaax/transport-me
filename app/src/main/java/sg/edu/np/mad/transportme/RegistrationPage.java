package sg.edu.np.mad.transportme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationPage extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private TextView registerUser;
    private EditText editTextName, editTextEmail, editTextPassword;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_page);

        mAuth = FirebaseAuth.getInstance();

        editTextName = findViewById(R.id.reg_FullName);
        editTextEmail = findViewById(R.id.reg_Email);
        editTextPassword = findViewById(R.id.reg_Password);

        progressBar = findViewById(R.id.progressBar);

        registerUser = findViewById(R.id.registerbutton);
        registerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });


    }

    private void registerUser() {
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

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegistrationPage.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //storing user information (need to add bus stops list)
                            User user = new User(name, email, null);


                            FirebaseDatabase database = FirebaseDatabase.getInstance("https://authuser-17b26-default-rtdb.asia-southeast1.firebasedatabase.app/");
                            DatabaseReference ref = database.getReference("users");
                            ref.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegistrationPage.this, "User has been registered Successfully", Toast.LENGTH_LONG).show();

                                        //redirect to login layout
                                        //Intent intent = new Intent(RegistrationPage.this, MainActivity.class);

                                    } else {
                                        Toast.makeText(RegistrationPage.this, "Registration failed! Try again!", Toast.LENGTH_LONG).show();
                                    }
                                    progressBar.setVisibility(View.GONE);

                                }
                            });
                        } else {
                            Toast.makeText(RegistrationPage.this, "Registration failed! Try again!", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                        ;

                    }
                });
    }
}