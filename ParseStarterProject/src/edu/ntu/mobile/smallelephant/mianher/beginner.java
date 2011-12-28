package edu.ntu.mobile.smallelephant.mianher;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONArray;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.Facebook.DialogListener;
import com.parse.GetCallback;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import edu.ntu.mobile.smallelephant.ader.ParseStarterProjectActivity;
import edu.ntu.mobile.smallelephant.ader.R;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class beginner extends Activity {
	ImageButton login;
	ImageButton instruction;
	ImageButton start;
	ImageButton logout;
	public static final String PREF = "SMALL_ELEPHANT_PREF";
	public static final String PREF_USER_ID = "PARSE_USER_id";


	@Override
	public void onStop() {
		Log.d("fbSession", "beginner_onStop");
		super.onStop();
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.beginner);
		Log.d("trace","beginner");
		Parse.initialize(this, "L6Qx3IQVB2zNv3bHrUzTwNbak0MF1xHQHqE2BVCc",
				"ksAA2JMvQVhQwnWLV8ZanZIChJlpsGIRUfKo3GIX");
		login = (ImageButton)findViewById(R.id.begin_login);
		logout = (ImageButton)findViewById(R.id.begin_logout);
		instruction = (ImageButton)findViewById(R.id.begin_instruction);
		start = (ImageButton)findViewById(R.id.begin_start);

		Log.e("yaya", "yayaya");

		if (ParseStarterProjectActivity.facebook.isSessionValid()) {
			Log.d("fbSession", "session valid 1");
			login.setVisibility(View.INVISIBLE);
			logout.setVisibility(View.VISIBLE);
			start.setVisibility(View.VISIBLE);
			//先load名單進來了!!!
		}
		else{
			start.setVisibility(View.INVISIBLE);
			login.setVisibility(View.VISIBLE);
			logout.setVisibility(View.INVISIBLE);
		}

		login.setOnClickListener(loginListener);
		start.setOnClickListener(new OnClickListener(){

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				pass_to_ader();
			}
		});
		logout.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				ParseStarterProjectActivity.fbAsyncRunner.logout(beginner.this,
						logoutListener);

			}
		});
		instruction.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent instru = new Intent(
								beginner.this,
								instruction.class);
						startActivity(instru);
			}
		});
	}

	private OnClickListener loginListener = new OnClickListener() {

		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (!ParseStarterProjectActivity.facebook.isSessionValid()) {
				Log.d("fbSession", "session invalid");
				ParseStarterProjectActivity.facebook.authorize(beginner.this,
						new String[] { "read_friendlists", "user_about_me",
								"user_photos", "friends_photos" },
						Facebook.FORCE_DIALOG_AUTH, new DialogListener() {

							public void onFacebookError(final FacebookError e) {
								// TODO Auto-generated method stub
								Log.d("fbSession",
										"facebook error: " + e.getMessage());
							}

							public void onError(final DialogError e) {
								// TODO Auto-generated method stub
								Log.d("fbSession",
										"dialog error: " + e.getMessage());
							}

							public void onComplete(Bundle values) {
								// TODO Auto-generated method stub
								login.setVisibility(View.INVISIBLE);
								logout.setVisibility(View.VISIBLE);
								start.setVisibility(View.VISIBLE);
						 							    
									pass_to_ader();
							}

							public void onCancel() {
								// TODO Auto-generated method stub
								Log.d("fbSession", "cancel ");

							}
						});
			} else {
				Log.d("fbSession", "session valid 2");

			}
		}
	};

	private RequestListener logoutListener = new RequestListener() {

		public void onMalformedURLException(MalformedURLException e,
				Object state) {
			// TODO Auto-generated method stub

		}

		public void onIOException(IOException e, Object state) {
			// TODO Auto-generated method stub

		}

		public void onFileNotFoundException(FileNotFoundException e,
				Object state) {
			// TODO Auto-generated method stub

		}

		public void onFacebookError(FacebookError e, Object state) {
			// TODO Auto-generated method stub

		}

		public void onComplete(String response, Object state) {
			// TODO Auto-generated method stub
			beginner.this.runOnUiThread(new Runnable() {
				public void run() {
					start.setVisibility(View.INVISIBLE);
					login.setVisibility(View.VISIBLE);
					logout.setVisibility(View.INVISIBLE);
				}
			});
		}
	};


	void pass_to_ader(){
		Intent intent = new Intent(
				beginner.this,
				ParseStarterProjectActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("accessToken", ParseStarterProjectActivity.facebook.getAccessToken());
		intent.putExtras(bundle);
		startActivity(intent);
	}

}