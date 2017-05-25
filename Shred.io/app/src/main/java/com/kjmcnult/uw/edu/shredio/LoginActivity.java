package com.kjmcnult.uw.edu.shredio;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by sii92_000 on 5/23/2017.
 */

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;

    private EditText emailEdit;
    private EditText passwordEdit;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        emailEdit = (EditText) findViewById(R.id.login_id);
        passwordEdit = (EditText) findViewById(R.id.login_password);

        emailEdit.addTextChangedListener(new CustomTextWatcher(this, emailEdit,  R.id.login_id_layout));
        passwordEdit.addTextChangedListener(new CustomTextWatcher(this, passwordEdit, R.id.login_password_layout));

        Button loginButton = (Button) findViewById(R.id.login_login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(v);
            }
        });

        AppCompatButton signUpButton = (AppCompatButton) findViewById(R.id.login_signup_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp(v);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Authentication.updateUI(this, currentUser);
    }

    public void signUp(View v) {
        startActivity(new Intent(this, SignUpActivity.class));
    }

    public void login(View v) {

        String email = emailEdit.getText().toString().trim();
        String password = passwordEdit.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Authentication.updateUI(LoginActivity.this, user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            Authentication.updateUI(LoginActivity.this, null);
                        }
                    }
                });
    }
}

/*
Error description received from server: {
                                                        "error": {
                                                         "errors": [
                                                          {
                                                           "domain": "global",
                                                           "reason": "invalid",
                                                           "message": "EMAIL_NOT_FOUND"
                                                          }
                                                         ],
                                                         "code": 400,
                                                         "message": "EMAIL_NOT_FOUND"
                                                        }
                                                       }
 */
