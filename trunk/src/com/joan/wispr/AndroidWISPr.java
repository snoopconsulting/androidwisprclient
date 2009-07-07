package com.joan.wispr;

import com.joan.pruebas.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class AndroidWISPr extends PreferenceActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.preferences);
	}
}