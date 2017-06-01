package com.kjmcnult.uw.edu.shredio;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by sii92_000 on 6/1/2017.
 */

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}
