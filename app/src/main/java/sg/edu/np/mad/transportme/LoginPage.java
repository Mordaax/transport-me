package sg.edu.np.mad.transportme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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

import at.favre.lib.crypto.bcrypt.BCrypt;

public class LoginPage extends AppCompatActivity implements View.OnClickListener{

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

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.loginbutton:
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();

                if(email.isEmpty()){
                    editTextEmail.setError("Email is required");
                    editTextEmail.requestFocus();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    editTextEmail.setError("Please enter a valid email");
                    editTextEmail.requestFocus();
                    return;
                }

                if(password.isEmpty()){
                    editTextPassword.setError("Password is empty");
                    editTextPassword.requestFocus();
                    return;
                }
                if(password.length() < 6){
                    editTextPassword.setError("Min password length is 6 characters");
                    editTextPassword.requestFocus();
                    return;
                }

                editTextEmail.setEnabled(false);
                editTextPassword.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                FirebaseDatabase db = FirebaseDatabase.getInstance("https://transportme-c607f-default-rtdb.asia-southeast1.firebasedatabase.app/");
                DatabaseReference myRef = db.getReference("User");
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean verified = true;
                        for(DataSnapshot user : snapshot.getChildren()) {
                            if (user.child("email").getValue().toString().equals(email)) {
                                if (BCrypt.verifyer().verify(password.toCharArray(), user.child("password").getValue().toString()).verified){
                                    startActivity(new Intent(LoginPage.this, MainActivity.class));
                                    Toast.makeText(LoginPage.this, "Login Successful!", Toast.LENGTH_LONG).show();
                                    verified = false;
                                }
                            }
                        }
                        if (verified){
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
            case R.id.gotoregisterpage:
                startActivity(new Intent(this, RegistrationPage.class));
                break;
        }
    }

    //private void userLogin() {

        /**mAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                startActivity(new Intent(LoginPage.this, MainActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(LoginPage.this, "Error"+ authResult.getException().getMessage(), Toast.LENGTH_LONG).show();
                Toast.makeText(LoginPage.this, "Invalid Credentials, please try again", Toast.LENGTH_LONG).show();
                editTextEmail.setEnabled(true);
                editTextPassword.setEnabled(true);
                progressBar.setVisibility(View.INVISIBLE);
            }
        });**/

        /**mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    //redirect to user profile (get main page from github)
                    startActivity(new Intent(LoginPage.this, MainActivity.class));

                }
                else{
                    Toast.makeText(LoginPage.this, "Invalid Credentials, please try again", Toast.LENGTH_LONG).show();
                    editTextEmail.setEnabled(true);
                    editTextPassword.setEnabled(true);
                    progressBar.setVisibility(View.INVISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }**/
/**
    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
    }**/
    //}
}