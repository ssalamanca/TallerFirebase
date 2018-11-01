package com.example.santi.tallerfirebase;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    EditText emailTxt ;
    EditText passwordTxt;
    Button login ;
    Button signup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        emailTxt = (EditText)findViewById(R.id.emailTxt);
        passwordTxt = (EditText)findViewById(R.id.passwordTxt);
        login = (Button)findViewById(R.id.login);
        signup = (Button)findViewById(R.id.signup);

        mAuth = FirebaseAuth.getInstance();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInUser();
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,CreateUserActivity.class);
                startActivity(intent);
            }
        });
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
// User is signed in
                    Toast.makeText(MainActivity.this, "Status -> Signed in ", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, MapsActivity.class));
                } else {
// User is signed out
                    Toast.makeText(MainActivity.this, "Status -> Signed out ", Toast.LENGTH_SHORT).show();
                }
            }
        };


    }
    protected void signInUser(){
        if(validateForm()){
            String email = emailTxt.getText().toString();
            String password = passwordTxt.getText().toString();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Toast.makeText(MainActivity.this, "signInWithEmail:onComplete:", Toast.LENGTH_SHORT).show();
                            if (!task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "signInWithEmail:failed", Toast.LENGTH_SHORT).show();
                                //emailTxt.setText("");
                                //passwordTxt.setText("");
                            }
                        }
                    });
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    private boolean validateForm() {
        boolean valid = true;
        String email = emailTxt.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailTxt.setError("Required.");
            valid = false;
        } else {
            emailTxt.setError(null);
        }
        String password = passwordTxt.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordTxt.setError("Required.");
            valid = false;
        } else {
            passwordTxt.setError(null);
        }
        return valid;
    }
}
