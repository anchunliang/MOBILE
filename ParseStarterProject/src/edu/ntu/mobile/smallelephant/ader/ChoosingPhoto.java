package edu.ntu.mobile.smallelephant.ader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.parse.Parse;

public class ChoosingPhoto extends Activity {
	public static Facebook facebook = new Facebook("255313284527691");
	public static AsyncFacebookRunner fbAsyncRunner = new AsyncFacebookRunner(
			facebook);
	//相簿的權限 everyone, custom, private...
	private List<String> ALBUMPRIVACY = Arrays.asList("everyone");
	//albums的id
	private ArrayList<String> albumIds;
	//albums的cover photo的url
	private ArrayList<String> albumCoverUrls;
	//所有的相片的url: 存著pair ( albumId, album中的所有相片的url)
	private TreeMap<String,ArrayList<String>> photoUrls;
	String accessToken;
	String myId;
	String myName;
	String friendIds[];
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Parse.initialize(this, "L6Qx3IQVB2zNv3bHrUzTwNbak0MF1xHQHqE2BVCc",
				"ksAA2JMvQVhQwnWLV8ZanZIChJlpsGIRUfKo3GIX");
		setContentView(R.layout.gallery);
		getIntentData();
		facebook.setAccessToken(accessToken);
		Log.d("facebookURL","send album request");
		fbAsyncRunner.request(myId+"/albums", albumsRequestListener);
	}

	private void getIntentData() {
		Bundle bundle = this.getIntent().getExtras();
		accessToken = bundle.getString("accessToken");
		myId = bundle.getString("myId");
		Log.d("facebookURL","myId is: "+myId);
		myName = bundle.getString("myName");
		Integer count = Integer.valueOf(bundle.getString("numSelectedFriends"));
		friendIds = new String[count];
		for (int i = 0; i < count; i++) {
			friendIds[i] = bundle.getString("friend" + i);
		}
	};

	RequestListener albumsRequestListener = new RequestListener() {

		public void onMalformedURLException(MalformedURLException e,
				Object state) {
			// TODO Auto-generated method stub
			e.getStackTrace();
		}

		public void onIOException(IOException e, Object state) {
			// TODO Auto-generated method stub
			e.getStackTrace();
		}

		public void onFileNotFoundException(FileNotFoundException e,
				Object state) {
			// TODO Auto-generated method stub
			e.getStackTrace();
		}

		public void onFacebookError(FacebookError e, Object state) {
			// TODO Auto-generated method stub
			e.getStackTrace();
		}

		public void onComplete(String response, Object state) {
			// TODO Auto-generated method stub
			JSONObject result;
			JSONArray albumList;
			try {
				// 拿到所有album 的 id
				result = new JSONObject(response);
				albumList = result.getJSONArray("data");
				albumIds = new ArrayList<String>();
				albumCoverUrls = new ArrayList<String>();
				photoUrls = new TreeMap< String, ArrayList<String>>();
				for (int i = 0; i < albumList.length(); i++) {
					Log.d("facebookURL","album "+ i );
					if (ALBUMPRIVACY.contains(albumList.getJSONObject(i)
							.getString("privacy"))) {
						Log.d("facebookURL"," 				everyone");
						String albumId = albumList.getJSONObject(i).getString(
								"id");
						albumIds.add(albumId);
						albumCoverUrls.add("http://graph.facebook.com/"
								+ albumId + "/picture?type=small&accessToken="
								+ accessToken);
						fbAsyncRunner.request(albumId + "/photos", albumPhotoRequestListener, albumId);
					}
				}
				// album的封面
			} catch (JSONException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	};
	RequestListener albumPhotoRequestListener = new RequestListener() {

		public void onMalformedURLException(MalformedURLException e,
				Object state) {
			// TODO Auto-generated method stub
			e.getStackTrace();
		}

		public void onIOException(IOException e, Object state) {
			// TODO Auto-generated method stub
			e.getStackTrace();
		}

		public void onFileNotFoundException(FileNotFoundException e,
				Object state) {
			// TODO Auto-generated method stub
			e.getStackTrace();
		}

		public void onFacebookError(FacebookError e, Object state) {
			// TODO Auto-generated method stub
			e.getStackTrace();
		}

		public void onComplete(String response, Object state) {
			// TODO Auto-generated method stub
			JSONObject result;
			JSONArray photoList;
			try {
				// 拿到所有album 的 id
				Log.d("facebookURL","album "+ (String)state);
				result = new JSONObject(response);
				photoList = result.getJSONArray("data");
				ArrayList<String> albumPhotoUrls = new ArrayList<String>(); 
				for (int i = 0; i < photoList.length(); i++) {
					Log.d("facebookURL","album "+ (String)state+"  photo: "+ i);
					String photoId = photoList.getJSONObject(i).getString("id");
					albumPhotoUrls.add("http://graph.facebook.com/" + photoId
							+ "/picture?type=thumbnail&accessToken=" + accessToken);
				}
				if( state.getClass().equals(String.class)){
					photoUrls.put( (String)state, albumPhotoUrls);
				}
				else Log.d("facebookURL","state not correct");
				// album的封面
			} catch (JSONException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	};
}
