package com.kjmcnult.uw.edu.shredio;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by sii92_000 on 5/24/2017.
 */

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";
    private FirebaseAuth mAuth;

    private EditText emailEdit;
    private EditText passwordEdit;
    private EditText password2Edit;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();

        emailEdit = (EditText) findViewById(R.id.signup_id);
        passwordEdit = (EditText) findViewById(R.id.signup_password);
        password2Edit = (EditText) findViewById(R.id.signup_password2);

        // setting up the onclick event for the signup button
        AppCompatButton signUpButton = (AppCompatButton) findViewById(R.id.signup_signup_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp(v);
            }
        });

        emailEdit.addTextChangedListener(new CustomTextWatcher(this, emailEdit,  R.id.signup_id_layout));
        passwordEdit.addTextChangedListener(new CustomTextWatcher(this, passwordEdit, R.id.signup_password_layout));

        // setting up second password field to check if it's same as first password field
        password2Edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                TextInputLayout inputLayout = (TextInputLayout) findViewById(R.id.signup_password_layout2);
                if (s.length() > 0 && passwordEdit.getText().toString().equals(password2Edit.getText().toString())) {
                    inputLayout.setError(null);
                } else {
                    inputLayout.setError("Must be equal to above password");
                }
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
        // first make sure all the fields are occupied
        if (emailEdit.getText() != null && passwordEdit.getText() != null && password2Edit.getText() != null) {
            TextInputLayout inputLayout = (TextInputLayout) findViewById(R.id.signup_password_layout2);

            // check 2nd password field is same as 1st
            if (inputLayout.getError() == null) {
                String email = emailEdit.getText().toString().trim();
                String password = passwordEdit.getText().toString().trim();

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign up success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Authentication.updateUI(SignUpActivity.this, user);
                                } else {
                                    // If sign up fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(SignUpActivity.this, "SignUp failed.",
                                            Toast.LENGTH_SHORT).show();
                                    Authentication.updateUI(SignUpActivity.this , null);
                                }
                            }
                        });
            } else {
                Log.v(TAG, "Incorrect second password");
                Toast.makeText(SignUpActivity.this, "Second password field must be same as first", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.v(TAG, "Empty field");
            Toast.makeText(SignUpActivity.this, "All fields must be filled", Toast.LENGTH_SHORT).show();
        }
    }
}
