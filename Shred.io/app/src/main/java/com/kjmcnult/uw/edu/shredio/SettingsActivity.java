package com.kjmcnult.uw.edu.shredio;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Seung on 6/1/2017.
 */

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
