package edu.ntu.mobile.smallelephant.ader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import edu.ntu.mobile.smallelephant.mianher.ChoosingPhoto;
import edu.ntu.mobile.smallelephant.ader.CONSTANT;

public class ParseStarterProjectActivity extends Activity {
	/** Called when the activity is first created. */
	public static Facebook facebook = new Facebook("255313284527691");
	public static AsyncFacebookRunner fbAsyncRunner = new AsyncFacebookRunner(
			facebook);
	private Button loginout;
	private Button btnRefresh;
	private Button btnInvite;
	private TextView mainTitle;
	private ListView listViewFriends;
	private String myId;
	private String myName;
	private String[] FBfriendsId;
	private String[] friendsId;
	private String[] friendsIp;
	private Boolean[] friendsOnline;
	// private SimpleAdapter adapter;
	ArrayList<ImageAndText> list = new ArrayList<ImageAndText>();
	private String[] friendsName;
	public static final String PREF = "SMALL_ELEPHANT_PREF";
	public static final String PREF_USER_ID = "PARSE_USER_id";
	private String parse_user_id = null;
	ImageAndTextListAdapter adapter = null;

	@Override
	public void onStop() {
		Log.d(CONSTANT.DEBUG_TAG, "onStop");
		super.onStop();
		logoutParse();
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
		if (facebook.isSessionValid()) {
			Log.d(CONSTANT.DEBUG_FACEBOOK, "oncreate: session valid");
			mainTitle.setText("session Valid!");
			changeToFriendSelectPage();
		}

	}

	private void setVisibilities() {
		btnRefresh.setVisibility(View.INVISIBLE);
		btnInvite.setVisibility(View.INVISIBLE);
	}

	private void findViews() {
		loginout = (Button) findViewById(R.id.loginout);
		mainTitle = (TextView) findViewById(R.id.mainTitle);
		listViewFriends = (ListView) findViewById(R.id.listView1);
		listViewFriends.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listViewFriends.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.d(CONSTANT.DEBUG_TAG, "findViews: item clicked");
				ViewCache vc = (ViewCache) view.getTag();
				if (vc.getButton().isChecked()) {
					Intent intent = new Intent(
							ParseStarterProjectActivity.this,
							ChoosingPhoto.class);
					Bundle bundle = new Bundle();
					bundle.putString("accessToken", facebook.getAccessToken());
					bundle.putString("myId", myId);
					Log.d(CONSTANT.DEBUG_FACEBOOK, "myId was: " + myId);
					bundle.putString("myName", myName);
					bundle.putString("friendId", vc.id);
					bundle.putString("friendIp", vc.ip);
					intent.putExtras(bundle);
					startActivity(intent);
				} else {
					Toast.makeText(ParseStarterProjectActivity.this,
							"the person you selected is offline!",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		listViewFriends.setItemsCanFocus(false);
		adapter = new ImageAndTextListAdapter(ParseStarterProjectActivity.this,
				list, listViewFriends);
		listViewFriends.setAdapter(adapter);
		btnRefresh = (Button) findViewById(R.id.refresh);
		btnInvite = (Button) findViewById(R.id.invite);
	}

	private void setListener() {
		loginout.setOnClickListener(loginListener);
	}

	private OnClickListener loginListener = new OnClickListener() {

		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (!facebook.isSessionValid()) {
				Log.d(CONSTANT.DEBUG_FACEBOOK, "login onclick : session invalid");
				facebook.authorize(ParseStarterProjectActivity.this,
						new String[] { "read_friendlists", "user_about_me",
								"user_photos", "friends_photos" },
						Facebook.FORCE_DIALOG_AUTH, new DialogListener() {

							public void onFacebookError(final FacebookError e) {
								// TODO Auto-generated method stub
								Log.d(CONSTANT.ERROR_FACEBOOK,
										"facebook error: " + e.getMessage());
							}

							public void onError(final DialogError e) {
								// TODO Auto-generated method stub
								Log.d(CONSTANT.ERROR_FACEBOOK,
										"dialog error: " + e.getMessage());
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
								Log.d(CONSTANT.DEBUG_FACEBOOK, "cancel ");

							}
						});
			} else {
				Log.d(CONSTANT.DEBUG_FACEBOOK, "login onclick: session valid");
				mainTitle.setText("session Valid!");
				changeToFriendSelectPage();
			}
		}
	};
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

						SharedPreferences settings = getSharedPreferences(PREF,
								0);
						parse_user_id = settings.getString(myId, "");
						final String myIpAddress = getLocalIpAddress();
						if (myIpAddress == null) {
							Log.d(CONSTANT.DEBUG_TAG, "Ip address is null");
						} else {
							if (!"".equals(parse_user_id)) {
								Log.d(CONSTANT.DEBUG_SHAREPREF, "get " + parse_user_id);
								query.getInBackground(parse_user_id,
										new GetCallback() {

											@Override
											public void done(
													ParseObject object,
													ParseException e) {
												// TODO Auto-generated method
												// stub
												if (e == null) {
													Log.d(CONSTANT.DEBUG_SHAREPREF," parse_user_id  :  "+parse_user_id + " ipaddress: "+myIpAddress);
													object.put("ip",
															myIpAddress);
													object.put("online", true);
													object.saveInBackground();
												}
												else{
													Log.e("ERROR",e.getMessage());
												}
											}
										});
							} else {
								Log.d(CONSTANT.DEBUG_SHAREPREF, "parse_user_id not found");
								query.whereEqualTo("facebookId", queryId);
								query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
								query.findInBackground(new FindCallback() {
									public void done(
											List<ParseObject> friendList,
											ParseException e) {
										if (e == null) {
											// The count request succeeded. Log
											if (friendList.size() == 0) {
												ParseObject myPost = new ParseObject(
														"User");
												myPost.put("facebookId", queryId);
												myPost.put("name", queryName);
												myPost.put("online", true);
												myPost.put("ip",
														myIpAddress);
												myPost.saveInBackground();
												parse_user_id = myPost
														.getObjectId();
												Log.d(CONSTANT.DEBUG_SHAREPREF,
														"User first login: "
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
												Log.d(CONSTANT.DEBUG_SHAREPREF,
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
											Log.d(CONSTANT.DEBUG_SHAREPREF,"find In background failed : "+ e.getMessage());
											// The request failed
										}
									}
								});
							}
						}
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
				setUserList(queryFriendsId);

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
			logoutReset();
		}
	};
	private void logoutReset(){
		loginout.setOnClickListener(loginListener);
		logoutParse();
		ParseStarterProjectActivity.this.runOnUiThread(new Runnable() {
			public void run() {
				mainTitle.setText(R.string.hello);
				loginout.setText("login");
				listViewFriends.setVisibility(View.GONE);
				btnRefresh.setVisibility(View.INVISIBLE);
				btnInvite.setVisibility(View.INVISIBLE);
			}
		});
		myId = null;
		myName = null;
		FBfriendsId = null;
		friendsId = null;
		friendsIp = null;
		friendsOnline = null;
		list.clear();
		friendsName = null;
		parse_user_id = null;
		adapter.clear();
		adapter.notifyDataSetChanged();

	}
	private void refreshFriendStatus() {
		setUserList(FBfriendsId);
		// if (listViewFriends != null && listViewFriends.getAdapter() != null)
		// {
		// int count = listViewFriends.getAdapter().getCount();
		// for (int i = 0; i < count; i++) {
		// listViewFriends.setItemChecked(i, false);
		// }
		// }
	}

	private void changeToFriendSelectPage() {
		Toast.makeText(getApplicationContext(), "complete!", Toast.LENGTH_SHORT);
		mainTitle.setText("朋友名單");
		// //Parse.initialize(ParseStarterProjectActivity.this,
		// "L6Qx3IQVB2zNv3bHrUzTwNbak0MF1xHQHqE2BVCc",
		// "ksAA2JMvQVhQwnWLV8ZanZIChJlpsGIRUfKo3GIX");
		listViewFriends.setVisibility(View.VISIBLE);
		btnRefresh.setVisibility(View.VISIBLE);
//		btnInvite.setVisibility(View.VISIBLE);
		fbAsyncRunner.request("me", myProfileListener);
		fbAsyncRunner.request("me/friends", friendsRequestListener);
		loginout.setText("logout");
		btnRefresh.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(),
						"Refresh button clicked!", Toast.LENGTH_SHORT).show();
				refreshFriendStatus();
			}
		});

		// for test
//		btnInvite.setOnClickListener(new OnClickListener() {
//
//			public void onClick(View v) {
//				// TODO Auto-generated
//				// method
//				// stub
//				try {
//					/*
//					 * long[] invited = listViewFriends.getCheckItemIds();
//					 * Intent intent = new Intent(
//					 * ParseStarterProjectActivity.this, ChoosingPhoto.class);
//					 * Bundle bundle = new Bundle(); bundle.putString(
//					 * "accessToken", facebook.getAccessToken());
//					 * bundle.putString( "myId", myId); bundle.putString(
//					 * "myName", myName);
//					 * bundle.putString("numSelectedFriends",""+
//					 * invited.length); for (int i = 0; i < invited.length; i++)
//					 * { bundle.putString( "friend" + i, FBfriendsId[(int)
//					 * invited[i]]); }
//					 */
//
//					Intent intent = new Intent(
//							ParseStarterProjectActivity.this,
//							ChoosingPhoto.class);
//					long[] invited = listViewFriends.getCheckItemIds();
//					Bundle bundle = new Bundle();
//					bundle.putString("accessToken", facebook.getAccessToken());
//					bundle.putString("myId", myId);
//					Log.d(CONSTANT.DEBUG_FACEBOOK, "myId was: " + myId);
//					bundle.putString("myName", myName);
//					bundle.putString("friendId", myId);
//					bundle.putString("numSelectedFriends", "" + invited.length);
//					for (int i = 0; i < invited.length; i++) {
//						bundle.putString("friend" + i,
//								friendsId[(int) invited[i]]);
//					}
//					intent.putExtras(bundle);
//					startActivity(intent);
//				} catch (Exception e) {
//					// TODO: handle
//					// exception
//					Log.d(CONSTANT.DEBUG_TAG, e.getMessage());
//				}
//
//			}
//		});
	}

	private void setUserList(final String[] queryFriendsId) {
		ParseStarterProjectActivity.this.runOnUiThread(new Runnable() {

			public void run() {
				// TODO Auto-generated method stub

				ParseQuery query = new ParseQuery("User");
				query.whereContainedIn("facebookId",
						Arrays.asList(queryFriendsId));
				query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
				query.findInBackground(new FindCallback() {
					public void done(List<ParseObject> friendList,
							ParseException e) {
						if (e == null) {
							Log.d(CONSTANT.DEBUG_PARSE, "parse find:");
							friendsName = new String[friendList.size()];
							friendsId = new String[friendList.size()];
							friendsIp = new String[friendList.size()];
							friendsOnline = new Boolean[friendList.size()];
							Log.d(CONSTANT.DEBUG_PARSE, "Retrieved " + friendList.size()
									+ " scores");
							int i = 0;
							for (ParseObject friend : friendList) {
								friendsName[i] = friend.getString("name");
								friendsId[i] = friend.getString("facebookId");
								friendsOnline[i] = friend.getBoolean("online");
								friendsIp[i] = friend.getString("ip");
								Log.d(CONSTANT.DEBUG_PARSE, "Id " + friendsId[i]
										+ "name\t" + friendsName[i]);
								i++;
							}
							adapter.clear();
							for (i = 0; i < friendsId.length; i++) {

								String img_url = "http://graph.facebook.com/"
										+ friendsId[i] + "/picture?type=small";
								ImageAndText item = new ImageAndText(img_url,
										friendsName[i], friendsOnline[i],
										friendsId[i]);
								if( friendsOnline[i]){
									item.ip = friendsIp[i];
								}
								adapter.add(item);
							}
							// ComparatorImageAndText comparator = new
							// ComparatorImageAndText();
							// Collections.sort( list, comparator);
							//
							if (friendsName.length > 0) {
								ParseStarterProjectActivity.this
										.runOnUiThread(new Runnable() {
											public void run() {
												adapter.notifyDataSetChanged();
											}
										});
							}

						} else {
							Log.e(CONSTANT.ERROR_TAG, "Error: " + e.getMessage());
						}
					}
				});
			}
		});
	}

	private void logoutParse() {
		if (parse_user_id != null) {
			Log.d(CONSTANT.DEBUG_SHAREPREF, "logoutParse:  parse_user_id = " + parse_user_id);
			ParseQuery query = new ParseQuery("User");
			query.getInBackground(parse_user_id, new GetCallback() {
				public void done(ParseObject object, ParseException e) {
					if (e == null) {
						// The count request succeeded. Log the
						// count
						object.put("ip", "");
						object.put("online", false);
						object.saveInBackground();

					} else {
						// The request failed
						Log.e(CONSTANT.ERROR_TAG, e.getMessage());
					}
				}
			});
			parse_user_id = null;
		}
	}

	public String getLocalIpAddress() {
		String defaultReturn = null;
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						if (! inetAddress.getHostAddress().toString().equals("10.0.2.15")){
							return inetAddress.getHostAddress().toString();
						}
						else{
							defaultReturn = "10.0.2.15";
						}
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("socket Exception", ex.toString());
		}
		return defaultReturn;
	}
}