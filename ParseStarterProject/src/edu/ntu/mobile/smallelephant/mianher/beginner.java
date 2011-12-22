package edu.ntu.mobile.smallelephant.mianher;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
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
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

public class beginner extends Activity {
	ImageButton login;
	ImageButton instruction;
	ImageButton start;
	ImageButton logout;
	private String myId;
	private String myName;
	public static final String PREF = "SMALL_ELEPHANT_PREF";
	public static final String PREF_USER_ID = "PARSE_USER_id";
	private String parse_user_id = null;

	
	@Override
	public void onStop() {
		Log.d("fbSession", "onStop");
		super.onStop();
		logoutParse();
	}

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.beginner);
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
								ParseStarterProjectActivity.fbAsyncRunner.request("me", myProfileListener);
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
			logoutParse();
			beginner.this.runOnUiThread(new Runnable() {
				public void run() {
					start.setVisibility(View.INVISIBLE);
					login.setVisibility(View.VISIBLE);
					logout.setVisibility(View.INVISIBLE);
				}
			});
		}
	};
	
	private void logoutParse() {
		if (parse_user_id != null) {
			Log.d("shrPref", "logoutParse:  parse_user_id = " + parse_user_id);
			ParseQuery query = new ParseQuery("User");
			query.getInBackground(parse_user_id, new GetCallback() {
				public void done(ParseObject object, ParseException e) {
					if (e == null) {
						// The count request succeeded. Log the
						// count
						object.put("online", false);
						object.saveInBackground();

					} else {
						// The request failed
						Log.d("Parse", e.getMessage());
					}
				}
			});
			parse_user_id = null;
		}
	}
	
	void pass_to_ader(){
		Intent intent = new Intent(
				beginner.this,
				ParseStarterProjectActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("accessToken", ParseStarterProjectActivity.facebook.getAccessToken());
	//	bundle.putString("myId", myId);
	//	Log.d("facebookURL", "myId was: " + myId);
	//	bundle.putString("myName", myName);
	//	bundle.putString("friendId", vc.id);
		intent.putExtras(bundle);
		startActivity(intent);
	
	}

	private RequestListener myProfileListener = new RequestListener() {

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
			JSONObject myProfile;
			try {
				myProfile = new JSONObject(response);
				myId = myProfile.getString("id");
				myName = myProfile.getString("name");

				final String queryId = myId;
				final String queryName = myName;

				beginner.this.runOnUiThread(new Runnable() {

					public void run() {
						// TODO Auto-generated method stub
						ParseQuery query = new ParseQuery("User");

						SharedPreferences settings = getSharedPreferences(PREF,
								0);
						parse_user_id = settings.getString(myId, "");
						if (!"".equals(parse_user_id)) {
							Log.d("shrPref", "get " + parse_user_id);
							query.getInBackground(parse_user_id,
									new GetCallback() {

										@Override
										public void done(ParseObject object,
												ParseException e) {
											// TODO Auto-generated method stub
											if (e != null) {
												object.put("online", true);
												object.saveInBackground();
											}
										}
									});
						} else {
							Log.d("shrPref", "parse_user_id not found");
							query.whereEqualTo("idNumber", queryId);
							// query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
							query.findInBackground(new FindCallback() {
								public void done(List<ParseObject> friendList,
										ParseException e) {
									if (e == null) {
										// The count request succeeded. Log the
										// count
										if (friendList.size() == 0) {
											ParseObject myPost = new ParseObject(
													"User");
											myPost.put("idNumber", queryId);
											myPost.put("name", queryName);
											myPost.put("online", true);
											myPost.saveInBackground();
											parse_user_id = myPost
													.getObjectId();
											Log.d("shrPref",
													"parse_user_id stored "
															+ parse_user_id);
											SharedPreferences settings = getSharedPreferences(
													PREF, 0);
											settings.edit()
													.putString(queryId,
															parse_user_id)
													.commit();
										} else {
											ParseObject myPost = friendList
													.get(0);
											myPost.put("online", true);
											myPost.saveInBackground();
											parse_user_id = myPost
													.getObjectId();
											Log.d("shrPref",
													"parse_user_id stored "
															+ parse_user_id);
											SharedPreferences settings = getSharedPreferences(
													PREF, 0);
											settings.edit()
													.putString(queryId,
															parse_user_id)
													.commit();
										}
									} else {
										parse_user_id = null;
										// The request failed
									}
								}
							});
						}
					}
				});
				pass_to_ader();

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	};
}
