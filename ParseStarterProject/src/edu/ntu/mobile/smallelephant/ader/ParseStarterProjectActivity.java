package edu.ntu.mobile.smallelephant.ader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
	private Boolean[] friendsOnline;
//	private SimpleAdapter adapter;
	ArrayList<ImageAndText> list = new ArrayList<ImageAndText>();
	private String[] friendsName;
	
	@Override
	public void onStop( ) {
		super.onStop();
		ParseQuery query = new ParseQuery("User");
		query.whereEqualTo("idNumber", myId);
		query.findInBackground(new FindCallback() {
			public void done(List<ParseObject> friendList, ParseException e) {
				if (e == null) {
					// The count request succeeded. Log the
					// count
					if( friendList.size() == 0){
						ParseObject myPost = new ParseObject(
								"User");
						myPost.put("idNumber", myId);
						myPost.put("name", myName);
						myPost.put("online", false);
						myPost.saveInBackground();
					}
					else{
						ParseObject myPost = friendList.get(0);
						myPost.put("online", false);
						myPost.saveInBackground();
					}
					
				} else {
					// The request failed
				}
			}
		});
		
	}
	
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

	private void setVisibilities() {
		btnClear.setVisibility(View.INVISIBLE);
		btnInvite.setVisibility(View.INVISIBLE);
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

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!facebook.isSessionValid()) {
					Log.d("fbSession", "session invalid");
					facebook.authorize(ParseStarterProjectActivity.this,
							new String[] { "read_friendlists" },
							Facebook.FORCE_DIALOG_AUTH, new DialogListener() {

								public void onFacebookError(
										final FacebookError e) {
									// TODO Auto-generated method stub
									Log.d("fbSession","facebook error: " + e.getMessage());
								}

								public void onError(final DialogError e) {
									// TODO Auto-generated method stub
									Log.d("fbSession","dialog error: " + e.getMessage());
								}

								public void onComplete(Bundle values) {
									// TODO Auto-generated method stub

									ParseStarterProjectActivity.this
											.runOnUiThread(new Runnable() {
												public void run() {
													changeToFriendSelectPage();
												}
											});

								}

								public void onCancel() {
									// TODO Auto-generated method stub
									Log.d("fbSession","cancel ");

								}
							});
				} else {
					Log.d("fbSession", "session valid");
					mainTitle.setText("session Valid!");
					changeToFriendSelectPage();
				}
			}
		});
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
				ParseStarterProjectActivity.this.runOnUiThread(new Runnable() {

					public void run() {
						// TODO Auto-generated method stub
						ParseQuery query = new ParseQuery("User");
						query.whereEqualTo("idNumber", queryId);
						query.findInBackground(new FindCallback() {
							public void done(List<ParseObject> friendList, ParseException e) {
								if (e == null) {
									// The count request succeeded. Log the
									// count
									if( friendList.size() == 0){
										ParseObject myPost = new ParseObject(
												"User");
										myPost.put("idNumber", queryId);
										myPost.put("name", queryName);
										myPost.put("online", true);
										myPost.saveInBackground();
									}
									else{
										ParseObject myPost = friendList.get(0);
										myPost.put("online", true);
										myPost.saveInBackground();
									}
									
								} else {
									// The request failed
								}
							}
						});

					}
				});

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	};
	private RequestListener friendsRequestListener = new RequestListener() {

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
			JSONObject friend;
			JSONArray friendlist;
			try {
				friend = new JSONObject(response);
				friendlist = friend.getJSONArray("data");
				FBfriendsId = new String[friendlist.length()];
				for (int i = 0; i < friendlist.length(); i++) {
					FBfriendsId[i] = friendlist.getJSONObject(i)
							.getString("id");
				}
				final String[] queryFriendsId = FBfriendsId;
				ParseStarterProjectActivity.this.runOnUiThread(new Runnable() {

					public void run() {
						// TODO Auto-generated method stub
						setUserList(queryFriendsId);
					}
				});
			} catch (JSONException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			loginout.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					// TODO Auto-generated method stub
					fbAsyncRunner.logout(ParseStarterProjectActivity.this,
							logoutListener);
				}
			});
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

	private void changeToFriendSelectPage() {
		Toast.makeText(getApplicationContext(), "complete!", Toast.LENGTH_SHORT);
		mainTitle.setText("朋友名單");
		// //Parse.initialize(ParseStarterProjectActivity.this,
		// "L6Qx3IQVB2zNv3bHrUzTwNbak0MF1xHQHqE2BVCc",
		// "ksAA2JMvQVhQwnWLV8ZanZIChJlpsGIRUfKo3GIX");
		listViewFriends.setVisibility(View.VISIBLE);
		btnClear.setVisibility(View.VISIBLE);
		btnInvite.setVisibility(View.VISIBLE);
		fbAsyncRunner.request("me", myProfileListener);
		fbAsyncRunner.request("me/friends", friendsRequestListener);
		loginout.setText("logout");
		btnClear.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(),
						"Clear button clicked!", Toast.LENGTH_SHORT);
				clearSelections();
			}
		});
		btnInvite.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated
				// method
				// stub
				try {
					/*
					 * long[] invited = listViewFriends.getCheckItemIds();
					 * Intent intent = new Intent(
					 * ParseStarterProjectActivity.this, ChoosingPhoto.class);
					 * Bundle bundle = new Bundle(); bundle.putString(
					 * "accessToken", facebook.getAccessToken());
					 * bundle.putString( "myId", myId); bundle.putString(
					 * "myName", myName);
					 * bundle.putString("numSelectedFriends",""+
					 * invited.length); for (int i = 0; i < invited.length; i++)
					 * { bundle.putString( "friend" + i, FBfriendsId[(int)
					 * invited[i]]); }
					 */
					Intent intent = new Intent(
							ParseStarterProjectActivity.this, MyGallery.class);
					Bundle bundle = new Bundle();

					intent.putExtras(bundle);
					startActivity(intent);
				} catch (Exception e) {
					// TODO: handle
					// exception
					Log.d("debug", e.getMessage());
				}

			}
		});
	}

	private void setUserList(final String[] queryFriendsId) {
		ParseQuery query = new ParseQuery("User");
		query.whereContainedIn("idNumber", Arrays.asList(queryFriendsId));
		query.findInBackground(new FindCallback() {
			public void done(List<ParseObject> friendList, ParseException e) {
				if (e == null) {
					friendsName = new String[friendList.size()];
					friendsId = new String[friendList.size()];
					friendsOnline = new Boolean[friendList.size()];
					Log.d("friends", "Retrieved " + friendList.size()
							+ " scores");
					int i = 0;
					for (ParseObject friend : friendList) {
						friendsName[i] = friend.getString("name");
						friendsId[i] = friend.getString("idNumber");
						friendsOnline[i] = friend.getBoolean("online");
						Log.d("friends profile", "Id " + friendsId[i] + "name\t" + friendsName[i]);
						i++;
					}
					
					for ( i = 0; i < friendsId.length; i++) {
						
						String img_url = "http://graph.facebook.com/"+friendsId[i]+"/picture?type=small";
						ImageAndText item = new ImageAndText(img_url,friendsName[i], friendsOnline[i]);
						list.add(item);
					}
					final ImageAndTextListAdapter adapter = 
								new ImageAndTextListAdapter(ParseStarterProjectActivity.this, list, listViewFriends);
					if (friendsName.length > 0) {
						ParseStarterProjectActivity.this
								.runOnUiThread(new Runnable() {
									public void run() {
										listViewFriends.setAdapter(adapter);
//										adapter = new SimpleAdapter( 
//												 ParseStarterProjectActivity.this, 
//												 list,
//												 R.layout.adapter,
//												 new String[] { "name","photo" },
//												 new int[] { R.id.MyAdapter_TextView_title,R.id.MyAdapter_ImageView_icon } );
//										listViewFriends.setAdapter(adapter);
//										for (int i = 0; i < friendsId.length; i++) {
//											HashMap<String, Object> item = new HashMap<String, Object>();
//											URL img_value;
//											Bitmap mIcon1;
//											try {
//												img_value = new URL("http://graph.facebook.com/"+friendsId[i]+"/picture?type=small");
//												Log.d("debug_url",img_value.toString());
//												mIcon1 = BitmapFactory.decodeStream(img_value.openConnection().getInputStream());
//												item.put("photo", mIcon1);
//											} catch (MalformedURLException e1) {
//												// TODO Auto-generated catch block
//												e1.printStackTrace();
//											} catch (IOException e) {
//												// TODO Auto-generated catch block
//												e.printStackTrace();
//											}
//											item.put("name", friendsName[i]);
//											list.add(item);
//											adapter.notifyDataSetChanged();
//										}
										
//										listViewFriends
//												.setAdapter(new ArrayAdapter<String>(
//														ParseStarterProjectActivity.this,
//														android.R.layout.simple_list_item_multiple_choice,
//														friendsFinal));
									}
								});
					}

				} else {
					Log.d("score", "Error: " + e.getMessage());
				}
			}
		});

	}

}