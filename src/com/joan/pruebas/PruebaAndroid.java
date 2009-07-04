package com.joan.pruebas;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class PruebaAndroid extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		EditText userText = (EditText) findViewById(R.id.user);
		EditText passText = (EditText) findViewById(R.id.password);
		SharedPreferences preferences = getSharedPreferences("PruebaJoan", Context.MODE_PRIVATE);
		userText.setText(preferences.getString("username", ""));
		passText.setText(preferences.getString("password", ""));

		Button confirmButton = (Button) findViewById(R.id.confirm);

		confirmButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				EditText userText = (EditText) findViewById(R.id.user);
				EditText passText = (EditText) findViewById(R.id.password);
				String username = userText.getText().toString();
				String password = passText.getText().toString();

				SharedPreferences preferences = getSharedPreferences("PruebaJoan", Context.MODE_PRIVATE);
				Editor editor = preferences.edit();
				editor.putString("username", username);
				editor.putString("password", password);
				editor.commit();
				setResult(RESULT_OK);
				finish();
			}
		});
		
		Button testButton = (Button) findViewById(R.id.test);
		testButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				WISPrLogger wl = new WISPrLogger();
				EditText userText = (EditText) findViewById(R.id.user);
				EditText passText = (EditText) findViewById(R.id.password);
				String username = userText.getText().toString();
				String password = passText.getText().toString();

				
				wl.login(username, password);
			}
		});
	}
}