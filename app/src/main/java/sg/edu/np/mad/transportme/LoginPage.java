package sg.edu.np.mad.transportme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginPage extends AppCompatActivity implements View.OnClickListener{

    //private TextView register;
    private EditText editTextEmail, editTextPassword;
    private Button signIn, register;

    private FirebaseAuth mAuth;
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

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.loginbutton:
                userLogin();
                break;
            case R.id.gotoregisterpage:
                startActivity(new Intent(this, RegistrationPage.class));
                break;
        }
    }

    private void userLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

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

        mAuth.signInWithEmailAndPassword(editTextEmail.getText().toString(),editTextPassword.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                startActivity(new Intent(LoginPage.this, MainActivity.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginPage.this, "Invalid Credentials, please try again", Toast.LENGTH_LONG).show();
                editTextEmail.setEnabled(true);
                editTextPassword.setEnabled(true);
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

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
            }
        });**/
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
    }
}
}