package com.parse.starter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class ParseStarterProjectActivity extends Activity {
	/** Called when the activity is first created. */
	public static Facebook facebook = new Facebook("255313284527691");
	public static AsyncFacebookRunner fbAsyncRunner = new AsyncFacebookRunner(
			facebook);
	private Button loginout;
	private Button btnClear;
	private Button btnInvite;
	private TextView mainTitle;
	private ListView listViewFriends;
	private String myId;
	private String myName;
	private String[] FBfriendsId;
	private String[] friendsId;
	private String[] friendsName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		findViews();
		setVisibilities();
		setListener();
		// Add your initialization code here
		// Parse.initialize(this, "your application id goes here",
		// "your client key goes here");
		Parse.initialize(this, "L6Qx3IQVB2zNv3bHrUzTwNbak0MF1xHQHqE2BVCc",
				"ksAA2JMvQVhQwnWLV8ZanZIChJlpsGIRUfKo3GIX");
	}
	private void setVisibilities(){
		btnClear.setVisibility(View.GONE);
		btnInvite.setVisibility(View.GONE);
	}
	private void findViews() {
		loginout = (Button) findViewById(R.id.loginout);
		mainTitle = (TextView) findViewById(R.id.mainTitle);
		listViewFriends = (ListView) findViewById(R.id.listView1);
		listViewFriends.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		btnClear = (Button) findViewById(R.id.clear);
		btnInvite = (Button) findViewById(R.id.invite);
	}

	private void setListener() {
		loginout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				facebook.authorize(ParseStarterProjectActivity.this,
						new String[] { "read_friendlists" },
						new DialogListener() {

							@Override
							public void onFacebookError(FacebookError e) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onError(DialogError e) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onComplete(Bundle values) {
								// TODO Auto-generated method stub
								ParseStarterProjectActivity.this
										.runOnUiThread(new Runnable() {
											public void run() {
												mainTitle.setText("朋友清單");
//												//Parse.initialize(ParseStarterProjectActivity.this, "L6Qx3IQVB2zNv3bHrUzTwNbak0MF1xHQHqE2BVCc",
//														"ksAA2JMvQVhQwnWLV8ZanZIChJlpsGIRUfKo3GIX");
												listViewFriends.setVisibility(View.VISIBLE);
												btnClear.setVisibility(View.VISIBLE);
												btnInvite.setVisibility(View.VISIBLE);
											}
										});
								fbAsyncRunner.request("me", myProfileListener);
								fbAsyncRunner.request("me/friends",
										friendsRequestListener);
								loginout.setText("logout");
								btnClear.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										// TODO Auto-generated method stub
										Toast.makeText(getApplicationContext(),
												"Clear button clicked!", Toast.LENGTH_SHORT);
										clearSelections();
									}
								});
								btnInvite.setOnClickListener(new OnClickListener() {
									
									@Override
									public void onClick(View v) {
										// TODO Auto-generated method stub
										try {
											int count = listViewFriends.getAdapter().getCount();
											long[] invited = listViewFriends.getCheckedItemIds();
//											Intent intent = new Intent(ParseStarterProjectActivity.this,XXX.class);
											Bundle bundle = new Bundle();
											bundle.putString("accessToken", facebook.getAccessToken());
											bundle.putString("myId", myId);
											bundle.putString("myName", myName);
											bundle.putString("numSelectedFriends", ""+invited.length);
											for( int i = 0; i < invited.length; i++){
												bundle.putString( "friend"+i, FBfriendsId[ (int)invited[i]]);
											}
//											intent.putExtras(bundle);
//									        startActivityForResult(intent,1);
										} catch (Exception e) {
											// TODO: handle exception
											Log.d("debug", e.getMessage());
										}
										
									}
								});
							}

							@Override
							public void onCancel() {
								// TODO Auto-generated method stub

							}
						});
			}
		});
	}

	private RequestListener myProfileListener = new RequestListener() {

		@Override
		public void onMalformedURLException(MalformedURLException e,
				Object state) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onIOException(IOException e, Object state) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onFileNotFoundException(FileNotFoundException e,
				Object state) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onFacebookError(FacebookError e, Object state) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onComplete(String response, Object state) {
			// TODO Auto-generated method stub
			JSONObject myProfile;
			try {
				myProfile = new JSONObject(response);
				myId = myProfile.getString("id");
				myName = myProfile.getString("name");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	};
	private RequestListener friendsRequestListener = new RequestListener() {

		@Override
		public void onMalformedURLException(MalformedURLException e,
				Object state) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onIOException(IOException e, Object state) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onFileNotFoundException(FileNotFoundException e,
				Object state) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onFacebookError(FacebookError e, Object state) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onComplete(String response, Object state) {
			// TODO Auto-generated method stub
			JSONObject friend;
			JSONArray friendlist;
			try {
				friend = new JSONObject(response);
				friendlist = friend.getJSONArray("data");
				final String queryId = myId;
				final String queryName = myName;
				ParseStarterProjectActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						ParseQuery query = new ParseQuery("User");
						query.whereEqualTo("idNumber", queryId);
						query.countInBackground(new CountCallback() {
							public void done(int count, ParseException e) {
								if (e == null) {
									// The count request succeeded. Log the
									// count
									Log.d("score", "Sean has played " + count
											+ " games");
									if (count == 0) {
										ParseObject myPost = new ParseObject(
												"User");
										myPost.put("idNumber", queryId);
										myPost.put("name", queryName);
										myPost.saveInBackground();
									}
								} else {
									// The request failed
								}
							}
						});

					}
				});
				FBfriendsId = new String[friendlist.length()];
				for (int i = 0; i < friendlist.length(); i++) {
					FBfriendsId[i] = friendlist.getJSONObject(i)
							.getString("id");
				}
				final String[] queryFriendsId = FBfriendsId;
				ParseStarterProjectActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						ParseQuery query = new ParseQuery("User");
						query.whereContainedIn("idNumber",
								Arrays.asList(queryFriendsId));
						query.findInBackground(new FindCallback() {
							public void done(List<ParseObject> friendList,
									ParseException e) {
								if (e == null) {
									friendsName = new String[friendList.size()];
									friendsId = new String[friendList.size()];
									Log.d("friends",
											"Retrieved " + friendList.size()
													+ " scores");
									int i = 0;
									for (ParseObject friend : friendList) {
										friendsName[i] = friend
												.getString("name");
										friendsId[i] = friend.getString("id");
										i++;
									}
									final String[] friendsFinal = friendsName;
									if (friendsName.length > 0) {
										ParseStarterProjectActivity.this
												.runOnUiThread(new Runnable() {
													public void run() {
														listViewFriends
																.setAdapter(new ArrayAdapter<String>(
																		ParseStarterProjectActivity.this,
																		android.R.layout.simple_list_item_multiple_choice,
																		friendsFinal));
													}
												});
									}

								} else {
									Log.d("score", "Error: " + e.getMessage());
								}
							}
						});

					}
				});
			} catch (JSONException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			loginout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					fbAsyncRunner.logout(ParseStarterProjectActivity.this,
							logoutListener);
				}
			});
		}
	};
	private RequestListener logoutListener = new RequestListener() {

		@Override
		public void onMalformedURLException(MalformedURLException e,
				Object state) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onIOException(IOException e, Object state) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onFileNotFoundException(FileNotFoundException e,
				Object state) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onFacebookError(FacebookError e, Object state) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onComplete(String response, Object state) {
			// TODO Auto-generated method stub
			ParseStarterProjectActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					mainTitle.setText(R.string.hello);
					loginout.setText("login");
					listViewFriends.setVisibility(View.INVISIBLE);
					btnClear.setVisibility(View.GONE);
					btnInvite.setVisibility(View.GONE);
				}
			});
		}
	};

	private void clearSelections() {
		int count = this.listViewFriends.getAdapter().getCount();
		for (int i = 0; i < count; i++) {
			this.listViewFriends.setItemChecked(i, false);
		}

	}

}