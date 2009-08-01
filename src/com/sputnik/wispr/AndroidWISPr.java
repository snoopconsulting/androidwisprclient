package com.sputnik.wispr;

import com.joan.wispr.R;
import com.joan.wispr.R.layout;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class AndroidWISPr extends PreferenceActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.preferences);
	}
}