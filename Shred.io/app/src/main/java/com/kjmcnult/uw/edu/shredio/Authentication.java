package com.kjmcnult.uw.edu.shredio;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Seung on 5/24/2017.
 */

public class Authentication {
    public static void updateUI(AppCompatActivity activity, FirebaseUser currentUser) {
        if (currentUser != null) {
            activity.startActivity(new Intent(activity.getApplicationContext(), MapsActivity.class));
        }
    }
}


