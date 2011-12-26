package edu.ntu.mobile.smallelephant.ader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.support.v4.view.Window;
import android.support.v4.view.MenuItem.OnMenuItemClickListener;
import android.telephony.TelephonyManager;
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
import edu.ntu.mobile.smallelephant.mianher.MyGallery;

public class ParseStarterProjectActivity extends FragmentActivity {
	/** Called when the activity is first created. */
	public static Facebook facebook = new Facebook("255313284527691");
	public static AsyncFacebookRunner fbAsyncRunner = new AsyncFacebookRunner(
			facebook);
	// private Button loginout;
	// private Button btnRefresh;
	// private Button btnInvite;
	// private TextView mainTitle;
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
	private static final int CHOOSING_PHOTO = 0;
	private String parse_user_id = null;
	ImageAndTextListAdapter adapter = null;
	private static int serverport = 5055;
	private static ServerSocket serverSocket;
	private static Socket remoteSocket;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add("Logout")
				.setIcon(R.drawable.ic_menu_set_as)
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {
						// TODO Auto-generated method stub
						// item.setActionView(R.layout.indeterminate_progress_action);
						Toast.makeText(getApplicationContext(),
								"logout button clicked!", Toast.LENGTH_SHORT)
								.show();
						logoutReset();
						return false;
					}
				})
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_ALWAYS
								);

		menu.add("Refresh").setIcon(R.drawable.ic_refresh)
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {
						// TODO Auto-generated method stub
						Toast.makeText(getApplicationContext(),
								"Refresh button clicked!", Toast.LENGTH_SHORT)
								.show();
						refreshFriendStatus();
						return false;
					}
				}).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onStop() {
		Log.d(CONSTANT.DEBUG_TAG, "onStop");
		logoutParse();
		super.onStop();
	}

	@Override
	public void onDestroy() {
		Log.d(CONSTANT.DEBUG_TAG, "onDestroy");
		logoutParse();
		super.onDestroy();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		// getContactsName();
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.login);
		threadforaccept();
		setProgressBarIndeterminateVisibility(Boolean.FALSE);
		findViews();
		// Add your initialization code here
		// Parse.initialize(this, "your application id goes here",
		// "your client key goes here");
		Parse.initialize(this, "L6Qx3IQVB2zNv3bHrUzTwNbak0MF1xHQHqE2BVCc",
				"ksAA2JMvQVhQwnWLV8ZanZIChJlpsGIRUfKo3GIX");
		if (facebook.isSessionValid()) {
			getSupportActionBar().setTitle("朋友名單");
			Log.d(CONSTANT.DEBUG_FACEBOOK, "oncreate: session valid");
			changeToFriendSelectPage();
		}
	}

	private void threadforaccept() {
		try{
			serverSocket = new ServerSocket(serverport);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		Thread t = new Thread(new Runnable() {
			public void run() {
				//while(true){
					int count=0;
					try {
						Log.d("network",""+InetAddress.getLocalHost().getHostAddress());
						
						Log.d("network","wait for accept");
						remoteSocket = serverSocket.accept();
						Log.d("network","acceptted");
						BufferedReader br = new BufferedReader(new InputStreamReader(remoteSocket.getInputStream()));
						Intent intent = new Intent(ParseStarterProjectActivity.this,MyGallery.class);
						Bundle bundle = new Bundle();
						while (remoteSocket.isConnected()) {
							String msg= br.readLine();
							if (msg=="end")
								break;
							bundle.putString("photo"+count,msg);
							Log.d("network",msg);
							count++;
							 
							
							
						}
						bundle.putString("selections", ""+count);
						intent.putExtras(bundle);
						startActivity(intent);
					}	
					catch (IOException e) {
						e.printStackTrace();
					}
				//}
			}
		});
		
		t.start();
		
	}

	// private void setVisibilities() {
	// btnRefresh.setVisibility(View.INVISIBLE);
	// btnInvite.setVisibility(View.INVISIBLE);
	// }

	private void findViews() {
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
					try {
						serverSocket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					startActivityForResult(intent, CHOOSING_PHOTO);
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
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CHOOSING_PHOTO) {
			if (resultCode == CONSTANT.RESULT_LOGOUT) {
				// A contact was picked. Here we will just display it
				// to the user.
				logoutReset();
			}
		}
	}

	// private void setListener() {
	// loginout.setOnClickListener(loginListener);
	// }

	private OnClickListener loginListener = new OnClickListener() {

		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (!facebook.isSessionValid()) {
				Log.d(CONSTANT.DEBUG_FACEBOOK,
						"login onclick : session invalid");
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
								Log.d(CONSTANT.ERROR_FACEBOOK, "dialog error: "
										+ e.getMessage());
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
						final String myIpAddress = getCurrentIP();
						TelephonyManager tMgr = (TelephonyManager) ParseStarterProjectActivity.this
								.getSystemService(Context.TELEPHONY_SERVICE);
						String mPhoneNumber = tMgr.getLine1Number();
						if (mPhoneNumber == null) {
							Log.d(CONSTANT.DEBUG_TAG, "my PhoneNumber is null");
							mPhoneNumber = "";
						} else
							Log.d(CONSTANT.DEBUG_TAG, "my PhoneNumber is "
									+ mPhoneNumber);
						final String finalmPhoneNumber = mPhoneNumber;
						if (myIpAddress == null) {
							Log.d(CONSTANT.DEBUG_TAG, "Ip address is null");
						} else {
							if (!"".equals(parse_user_id)) {
								Log.d(CONSTANT.DEBUG_SHAREPREF, "get "
										+ parse_user_id);
								query.getInBackground(parse_user_id,
										new GetCallback() {

											@Override
											public void done(
													ParseObject myPost,
													ParseException e) {
												// TODO Auto-generated method
												// stub
												if (e == null) {
													Log.d(CONSTANT.DEBUG_SHAREPREF,
															" parse_user_id  :  "
																	+ parse_user_id
																	+ " ipaddress: "
																	+ myIpAddress);
													myPost.put("ip",
															myIpAddress);
													myPost.put("online", true);
													myPost.put("phoneNumber",
															finalmPhoneNumber);
													myPost.saveInBackground();
												} else {
													Log.e("ERROR",
															e.getMessage());
												}
											}
										});
							} else {
								Log.d(CONSTANT.DEBUG_SHAREPREF,
										"parse_user_id not found");
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
												myPost.put("facebookId",
														queryId);
												myPost.put("name", queryName);
												myPost.put("online", true);
												myPost.put("phoneNumber",
														finalmPhoneNumber);
												myPost.put("ip", myIpAddress);
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
												myPost.put("phoneNumber",
														finalmPhoneNumber);
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
											Log.d(CONSTANT.DEBUG_SHAREPREF,
													"find In background failed : "
															+ e.getMessage());
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
			// loginout.setOnClickListener(new OnClickListener() {
			//
			// public void onClick(View v) {
			// // TODO Auto-generated method stub
			// fbAsyncRunner.logout(ParseStarterProjectActivity.this,
			// logoutListener);
			//
			// }
			// });
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

	private void logoutReset() {
		logoutParse();
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
		finish();
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
		// //Parse.initialize(ParseStarterProjectActivity.this,
		// "L6Qx3IQVB2zNv3bHrUzTwNbak0MF1xHQHqE2BVCc",
		// "ksAA2JMvQVhQwnWLV8ZanZIChJlpsGIRUfKo3GIX");
		listViewFriends.setVisibility(View.VISIBLE);
		// btnInvite.setVisibility(View.VISIBLE);
		fbAsyncRunner.request("me", myProfileListener);
		fbAsyncRunner.request("me/friends", friendsRequestListener);

		// for test
		// btnInvite.setOnClickListener(new OnClickListener() {
		//
		// public void onClick(View v) {
		// // TODO Auto-generated
		// // method
		// // stub
		// try {
		// /*
		// * long[] invited = listViewFriends.getCheckItemIds();
		// * Intent intent = new Intent(
		// * ParseStarterProjectActivity.this, ChoosingPhoto.class);
		// * Bundle bundle = new Bundle(); bundle.putString(
		// * "accessToken", facebook.getAccessToken());
		// * bundle.putString( "myId", myId); bundle.putString(
		// * "myName", myName);
		// * bundle.putString("numSelectedFriends",""+
		// * invited.length); for (int i = 0; i < invited.length; i++)
		// * { bundle.putString( "friend" + i, FBfriendsId[(int)
		// * invited[i]]); }
		// */
		//
		// Intent intent = new Intent(
		// ParseStarterProjectActivity.this,
		// ChoosingPhoto.class);
		// long[] invited = listViewFriends.getCheckItemIds();
		// Bundle bundle = new Bundle();
		// bundle.putString("accessToken", facebook.getAccessToken());
		// bundle.putString("myId", myId);
		// Log.d(CONSTANT.DEBUG_FACEBOOK, "myId was: " + myId);
		// bundle.putString("myName", myName);
		// bundle.putString("friendId", myId);
		// bundle.putString("numSelectedFriends", "" + invited.length);
		// for (int i = 0; i < invited.length; i++) {
		// bundle.putString("friend" + i,
		// friendsId[(int) invited[i]]);
		// }
		// intent.putExtras(bundle);
		// startActivity(intent);
		// } catch (Exception e) {
		// // TODO: handle
		// // exception
		// Log.d(CONSTANT.DEBUG_TAG, e.getMessage());
		// }
		//
		// }
		// });
	}

	private void setUserList(final String[] queryFriendsId) {
		ParseStarterProjectActivity.this.runOnUiThread(new Runnable() {

			public void run() {
				// TODO Auto-generated method stub
				setProgressBarIndeterminateVisibility(Boolean.TRUE);
				setProgress(0);
				ParseQuery query = new ParseQuery("User");
				query.whereContainedIn("facebookId",
						Arrays.asList(queryFriendsId));
				query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
				query.findInBackground(new FindCallback() {
					public void done(List<ParseObject> friendList,
							ParseException e) {
						setProgressBarIndeterminateVisibility(Boolean.FALSE);
						if (e == null) {
							Log.d(CONSTANT.DEBUG_PARSE, "parse find:");
							friendsName = new String[friendList.size()];
							friendsId = new String[friendList.size()];
							friendsIp = new String[friendList.size()];
							friendsOnline = new Boolean[friendList.size()];
							Log.d(CONSTANT.DEBUG_PARSE, "Retrieved "
									+ friendList.size() + " scores");
							int i = 0;
							for (ParseObject friend : friendList) {
								friendsName[i] = friend.getString("name");
								friendsId[i] = friend.getString("facebookId");
								friendsOnline[i] = friend.getBoolean("online");
								friendsIp[i] = friend.getString("ip");
								Log.d(CONSTANT.DEBUG_PARSE, "Id "
										+ friendsId[i] + "name\t"
										+ friendsName[i]);
								i++;
							}
							adapter.clear();
							for (i = 0; i < friendsId.length; i++) {

								String img_url = "http://graph.facebook.com/"
										+ friendsId[i] + "/picture?type=small";
								ImageAndText item = new ImageAndText(img_url,
										friendsName[i], friendsOnline[i],
										friendsId[i]);
								if (friendsOnline[i]) {
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
							Log.e(CONSTANT.ERROR_TAG,
									"Error: " + e.getMessage());
						}
					}
				});
			}
		});
	}

	private void logoutParse() {
		if (parse_user_id != null) {
			Log.d(CONSTANT.DEBUG_SHAREPREF, "logoutParse:  parse_user_id = "
					+ parse_user_id);
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
						if (!inetAddress.getHostAddress().toString()
								.equals("10.0.2.15")) {
							return inetAddress.getHostAddress().toString();
						} else {
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

	public String getCurrentIP() {
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet("http://wiki.iti-lab.org/ip.php");
			// HttpGet httpget = new
			// HttpGet("http://whatismyip.everdot.org/ip");
			// HttpGet httpget = new HttpGet("http://whatismyip.com.au/");
			// HttpGet httpget = new HttpGet("http://www.whatismyip.org/");
			HttpResponse response;

			response = httpclient.execute(httpget);

			// Log.i("externalip",response.getStatusLine().toString());

			HttpEntity entity = response.getEntity();
			if (entity != null) {
				long len = entity.getContentLength();
				if (len != -1 && len < 1024) {
					String str = EntityUtils.toString(entity);
					// Log.i("externalip",str);
					return str;
				} else {
					Log.d(CONSTANT.ERROR_TAG,
							"get ipaddress: Response too long or error.");
					return null;
					// debug
					// ip.setText("Response too long or error: "+EntityUtils.toString(entity));
					// Log.i("externalip",EntityUtils.toString(entity));
				}
			} else {
				Log.d(CONSTANT.ERROR_TAG, "get ipaddress: (Null) "
						+ response.getStatusLine().toString());
				return null;
			}

		} catch (Exception e) {
			Log.d(CONSTANT.ERROR_TAG,
					"get ipaddress: (Exception) " + e.getMessage());
			return null;
		}

	}

	public void getContactsName() {
		// 取得內容解析器
		ContentResolver contentResolver = this.getContentResolver();
		// 設定你要從電話簿取出的欄位
		// String[] projection = new String[] { Contacts.People.NAME,
		// Contacts.People.NUMBER };
		Uri uri = Uri.parse("content://contacts/myContactCard");
		// 取得所有聯絡人
		Cursor cursor = contentResolver.query(uri, null, null, null, null);
		// String[] contactsName = new String[cursor.getCount()];
		Log.d(CONSTANT.DEBUG_TAG, "htc count: " + cursor.getCount());
		for (int i = 0; i < cursor.getCount(); i++) {
			// 移到指定位置
			cursor.moveToPosition(i);
			// 取得第一個欄位
			// contactsName[i] = cursor.getString(0);
			Log.d(CONSTANT.DEBUG_TAG, "htc content: " + cursor.getString(0));
		}
		// return contactsName;
	}
}