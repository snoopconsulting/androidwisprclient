package com.joan.pruebas;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class PruebaAndroid extends Activity {
	private static String TAG = PruebaAndroid.class.getName();

	private SharedPreferences mPreferences = null;;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		EditText userText = (EditText) findViewById(R.id.user);
		EditText passText = (EditText) findViewById(R.id.password);
		mPreferences = getSharedPreferences("PruebaJoan", Context.MODE_PRIVATE);
		userText.setText(mPreferences.getString("username", ""));
		passText.setText(mPreferences.getString("password", ""));

		Button confirmButton = (Button) findViewById(R.id.confirm);

		confirmButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				EditText userText = (EditText) findViewById(R.id.user);
				EditText passText = (EditText) findViewById(R.id.password);
				String username = userText.getText().toString();
				String password = passText.getText().toString();

				Editor editor = mPreferences.edit();
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

				String result = wl.login(username, password);
				Log.d(TAG, "login result:" + result);
				if (result.equals(WISPrConstants.WISPR_RESPONSE_CODE_LOGIN_SUCCEEDED)) {
					Log.d(TAG, "Congratulations!!!!");
				}
			}
		});
	}
}