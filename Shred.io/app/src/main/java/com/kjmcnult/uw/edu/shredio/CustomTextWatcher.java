package com.kjmcnult.uw.edu.shredio;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Created by Seung on 5/24/2017.
 */

public class CustomTextWatcher implements TextWatcher {
    private EditText currEditText;
    private AppCompatActivity activity;
    private int rID;

    public CustomTextWatcher(AppCompatActivity activity, EditText currEditText, int rID) {
        this.activity = activity;
        this.currEditText = currEditText;
        this.rID = rID;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) { }

    @Override
    public void afterTextChanged(Editable s) {
        TextInputLayout inputLayout = (TextInputLayout) activity.findViewById(rID);
        if (s.length() > 0) {
            inputLayout.setError(null);
        } else {
            inputLayout.setError("Must not be empty");
        }
    }
}