package edu.ntu.mobile.smallelephant.mianher;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;


import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.parse.Parse;

import edu.ntu.mobile.smallelephant.ader.R;
import edu.ntu.mobile.smallelephant.ader.R.id;
import edu.ntu.mobile.smallelephant.ader.R.layout;

public class ChoosingPhoto extends Activity {
	public static Facebook facebook = new Facebook("255313284527691");
	public static AsyncFacebookRunner fbAsyncRunner = new AsyncFacebookRunner(
			facebook);
	//�貊倏����everyone, custom, private...
	private List<String> ALBUMPRIVACY = Arrays.asList("everyone");
	//albums�d
	private ArrayList<String> albumIds;
	private ArrayList<String> albumNames;
	//albums�over photo�rl
	private ArrayList<String> albumCoverUrls;
	//������url: 摮�pair ( albumId, album銝剔�����貊��rl)
	private TreeMap<String,ArrayList<String>> photoUrls;
	String accessToken;
	String myId;
	String myName;
	String friendId;
//	String friendIds[];
	private int count;
	private Bitmap[] thumbnails;
	private boolean[] thumbnailsselection;
	private String[] arrPath;
	private ImageAdapter imageAdapter;
	GridView imagegrid;
	//private ArrayList<String> PhotoURLS = new ArrayList<String>();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.album_main);
		
		imagegrid = (GridView) findViewById(R.id.PhoneImageGrid);
		imageAdapter = new ImageAdapter();
		imagegrid.setAdapter(imageAdapter);
    	
		Parse.initialize(this, "L6Qx3IQVB2zNv3bHrUzTwNbak0MF1xHQHqE2BVCc",
				"ksAA2JMvQVhQwnWLV8ZanZIChJlpsGIRUfKo3GIX");
		getIntentData();
		facebook.setAccessToken(accessToken);
		Log.d("facebookURL","send album request");
		fbAsyncRunner.request(myId+"/albums", albumsRequestListener);
		
	}
	public class ImageAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		ArrayList<Drawable> drawablesFromUrl = new ArrayList<Drawable>();
		Context mContext;
		FileInputStream fis;
		public ImageAdapter() {
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public int getCount() {
			return drawablesFromUrl.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}
		public void addItem(Drawable item) {
			drawablesFromUrl.add(item);
		}
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(
						R.layout.album_item, null);
				holder.imageview = (ImageView) convertView.findViewById(R.id.thumbImage);
				holder.checkbox = (CheckBox) convertView.findViewById(R.id.itemCheckBox);
				holder.mtextview = (TextView) convertView.findViewById(R.id.textView1);
				convertView.setTag(holder);
			}
			else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.checkbox.setId(position);
			holder.imageview.setId(position);
			holder.mtextview.setId(position);
			holder.checkbox.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					// TODO Auto-generated method stub
					CheckBox cb = (CheckBox) v;
					int id = cb.getId();
					if (thumbnailsselection[id]){
						cb.setChecked(false);
						thumbnailsselection[id] = false;
						//Toast.makeText(MyCustomActivity.this, "onClick", Toast.LENGTH_SHORT).show();
					} else {
						cb.setChecked(true);
						thumbnailsselection[id] = true;
						//Toast.makeText(MyCustomActivity.this, "onClick", Toast.LENGTH_SHORT).show();
					}
				}
			});
			holder.imageview.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					// TODO Auto-generated method stub
					/*int id = v.getId();
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.parse("file://" + arrPath[id]), "image/*");
					startActivity(intent);
					*/
					Log.d("trace","image onclick");
				}
			});
			//holder.imageview.setImageBitmap(thumbnails[position]);
			holder.imageview.setImageDrawable(drawablesFromUrl.get(position));
			holder.mtextview.setText(albumNames.get(position));
			//holder.imageview.setImageResource(R.drawable.ic_launcher);
			//holder.imageview.setLayoutParams(new CoverFlow.LayoutParams(100, 100));

			holder.imageview.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			holder.checkbox.setChecked(thumbnailsselection[position]);
			holder.id = position;
			//BitmapDrawable drawable = (BitmapDrawable) holder.imageview.getDrawable();
			//drawable.setAntiAlias(true);
			return convertView;
		}
	}
	class ViewHolder {
		ImageView imageview;
		CheckBox checkbox;
		TextView mtextview;
		int id;
	}
	
	private Drawable LoadImageFromURL(String url) {
		try {
			URL URL = new URL(url);
			URLConnection conn = URL.openConnection();

			HttpURLConnection httpConn = (HttpURLConnection) conn;
			httpConn.setRequestMethod("GET");
			httpConn.connect();

			if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				InputStream inputStream = httpConn.getInputStream();

				Bitmap b = BitmapFactory.decodeStream(inputStream);
				inputStream.close();
				Drawable d = new BitmapDrawable(b);
				Log.d("trace","Load image OK");
				return d;
				// mImage.setImageBitmap(bitmap);
			}
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	private void getIntentData() {
		Bundle bundle = this.getIntent().getExtras();
		accessToken = bundle.getString("accessToken");
		myId = bundle.getString("myId");
		Log.d("facebookURL","myId is: "+myId);
		myName = bundle.getString("myName");
		friendId = bundle.getString("friendId");
//		Integer count = Integer.valueOf(bundle.getString("numSelectedFriends"));
//		friendIds = new String[count];
//		for (int i = 0; i < count; i++) {
//			friendIds[i] = bundle.getString("friend" + i);
//		}
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
				// �踹���album ��id
				result = new JSONObject(response);
				albumList = result.getJSONArray("data");
				albumIds = new ArrayList<String>();
				albumNames = new ArrayList<String>();
				albumCoverUrls = new ArrayList<String>();
				photoUrls = new TreeMap< String, ArrayList<String>>();
				for (int i = 0; i < albumList.length(); i++) {
					Log.d("facebookURL","album "+ i );
					if (ALBUMPRIVACY.contains(albumList.getJSONObject(i)
							.getString("privacy"))) {
						String albumId = albumList.getJSONObject(i).getString(
								"id");
						String albumName = albumList.getJSONObject(i).getString(
								"name");
						albumIds.add(albumId);
						albumNames.add(albumName);
						albumCoverUrls.add("https://graph.facebook.com/"
								+ albumId + "/picture?type=small&access_token="
								+ accessToken);
						Log.d("trace_album","https://graph.facebook.com/"
								+ albumId + "/picture?type=small&access_token="
								+ accessToken);
						fbAsyncRunner.request(albumId + "/photos", albumPhotoRequestListener, albumId);
					}
				}
				thumbnailsselection = new boolean[albumCoverUrls.size()];
				
				
				//imagecursor.close();
				ChoosingPhoto.this.runOnUiThread(new Runnable() {
				    public void run() {
				    	
				    	for (String url : albumCoverUrls) {
				    		imageAdapter.addItem(LoadImageFromURL(url));
				    		Log.d("trace","after addItem, url="+url);
							Log.d("trace","after addItem");
				    		
				    		
				    	}
				    	imageAdapter.notifyDataSetChanged();
				    }
				});
				// album����			} catch (JSONException e) {
				// TODO: handle exception
				//e.printStackTrace();
			}
			catch (Exception e) {
				// TODO: handle
				// exception
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
				// �踹���album ��id
				Log.d("facebookURL","album "+ (String)state);
				result = new JSONObject(response);
				photoList = result.getJSONArray("data");
				ArrayList<String> albumPhotoUrls = new ArrayList<String>(); 
				for (int i = 0; i < photoList.length(); i++) {
					String photoId = photoList.getJSONObject(i).getString("id");
					albumPhotoUrls.add("https://graph.facebook.com/" + photoId
							+ "/picture?type=thumbnail&access_token=" + accessToken);
					Log.d("facebookURL","album "+ (String)state+"  photo: "+ i +"  https://graph.facebook.com/" + photoId
							+ "/picture?type=thumbnail&access_token=" + accessToken);
				}
				if( state.getClass().equals(String.class)){
					photoUrls.put( (String)state, albumPhotoUrls);
				}
				else Log.d("facebookURL","state not correct");
				// album����			} catch (JSONException e) {
				// TODO: handle exception
				//e.printStackTrace();
			}
			catch (Exception e) {
				// TODO: handle
				// exception
				Log.d("debug", e.getMessage());
			}
		}
	};
}
