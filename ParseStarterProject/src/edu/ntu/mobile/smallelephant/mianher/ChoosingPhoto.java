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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.parse.Parse;

import edu.ntu.mobile.smallelephant.ader.CONSTANT;
import edu.ntu.mobile.smallelephant.ader.ParseStarterProjectActivity;
import edu.ntu.mobile.smallelephant.ader.R;
import edu.ntu.mobile.smallelephant.ader.ViewCache;
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
	private TreeMap<String,ArrayList<PhotoUnit>> photos;
	//private TreeMap<String,ArrayList<Boolean>> photoselection;
	String accessToken;
	String myId;
	String myName;
	String friendId;
	String nowalbumid;
//	String friendIds[];
	private int count;
	private Bitmap[] thumbnails;
	//private boolean[] photoselection;
	private String[] arrPath;
	private AlbumAdapter albumAdapter;
	private PhotoAdapter photoAdapter;
	GridView albumgrid;
	GridView photogrid;
	ImageView mimage;
	private ProgressDialog progressDialog;
	int flag=0;
	int selections=0; 
	//private ArrayList<String> PhotoURLS = new ArrayList<String>();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.album_main);
		mimage=(ImageView) findViewById(R.id.arrow);
		albumgrid = (GridView) findViewById(R.id.AlbumGrid);
		photogrid = (GridView) findViewById(R.id.PhotoGrid);
		mimage.setOnClickListener(new OnClickListener() {
			/*public void onItemClick(AdapterView<?> parent, View view,
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
			}*/

			public void onClick(View arg0) {
				Intent intent = new Intent(ChoosingPhoto.this,MyGallery.class);
				Bundle bundle = new Bundle();
				
				int count=0;
				if(selections==0)
					Toast.makeText(ChoosingPhoto.this,"Please select some photos.",Toast.LENGTH_SHORT).show();
				else{
					for(String albumid: albumIds){
						ArrayList<PhotoUnit> photounits =photos.get(albumid);
						for(PhotoUnit p:photounits){
							if(p.photoselection){
								bundle.putString("photo"+count,p.photourllarge);
								count++;
							}						
						}
					}
					if(count==selections){
						bundle.putString("selections", ""+selections);
						intent.putExtras(bundle);
						startActivity(intent);
						
					}
					else{
						Log.d("trace","selection count not match!");
						
					}
					
				}
					
				
				
			}
		});
		photogrid.setVisibility(View.GONE);
		albumAdapter = new AlbumAdapter();
		albumgrid.setAdapter(albumAdapter);
    	
		Parse.initialize(this, "L6Qx3IQVB2zNv3bHrUzTwNbak0MF1xHQHqE2BVCc",
				"ksAA2JMvQVhQwnWLV8ZanZIChJlpsGIRUfKo3GIX");
		getIntentData();
		facebook.setAccessToken(accessToken);
		Log.d("facebookURL","send album request");
		progressDialog = ProgressDialog.show(ChoosingPhoto.this, "讀取相簿列表中", "請稍候...", true, false); 
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		
      
     new Thread()
     { 
       public void run()
       { 
         try
         { 
        	 sleep(2000);
         }
         catch (Exception e)
         {
           e.printStackTrace();
         }
         finally
         {
        	 progressDialog.dismiss();   
         }
       }
     }.start(); 
     fbAsyncRunner.request(myId+"/albums", albumsRequestListener);
		
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 && flag==1) {
			// do something on back.
			flag=0;
			ChoosingPhoto.this.runOnUiThread(new Runnable() {
			    public void run() {
			    	albumgrid.setVisibility(View.VISIBLE);
					photogrid.setVisibility(View.GONE);
					
					
			    }
			});
		return true;
		}

		return super.onKeyDown(keyCode, event);
	}
	public class AlbumAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		ArrayList<Drawable> drawablesFromUrl = new ArrayList<Drawable>();
		Context mContext;
		FileInputStream fis;
		public AlbumAdapter() {
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
				holder.checkbox.setVisibility(View.INVISIBLE);
				holder.mtextview = (TextView) convertView.findViewById(R.id.textView1);
				convertView.setTag(holder);
			}
			else {
				holder = (ViewHolder) convertView.getTag();
			}
			//holder.checkbox.setId(position);
			holder.imageview.setId(position);
			holder.mtextview.setId(position);
			/*holder.checkbox.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					// TODO Auto-generated method stub
					CheckBox cb = (CheckBox) v;
					int id = cb.getId();
					if (photoselection[id]){
						cb.setChecked(false);
						photoselection[id] = false;
						//Toast.makeText(MyCustomActivity.this, "onClick", Toast.LENGTH_SHORT).show();
					} else {
						cb.setChecked(true);
						photoselection[id] = true;
						//Toast.makeText(MyCustomActivity.this, "onClick", Toast.LENGTH_SHORT).show();
					}
				}
			});*/
			holder.imageview.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					photoAdapter = new PhotoAdapter();
					photogrid.setAdapter(photoAdapter);
					final int id=((ImageView)v).getId();
					nowalbumid=albumIds.get(id);
					flag=1;//select photo
					ChoosingPhoto.this.runOnUiThread(new Runnable() {
					    public void run() {
					    	albumgrid.setVisibility(View.GONE);
							photogrid.setVisibility(View.VISIBLE);
							
							ArrayList<PhotoUnit> photo=photos.get(albumIds.get(id));
					    	for (PhotoUnit photounit : photo) {
					    		photoAdapter.addItem(LoadImageFromURL(photounit.photourlsmall));
					    		Log.d("trace","photoadapter after addItem, url="+photounit.photourlsmall);
								
					    		
					    	}
					    	photoAdapter.notifyDataSetChanged();
					    }
					});
					Log.d("trace","image onclick");
				}
			});
			//holder.imageview.setImageBitmap(thumbnails[position]);
			holder.imageview.setImageDrawable(drawablesFromUrl.get(position));
			holder.mtextview.setText(albumNames.get(position));
			//holder.imageview.setImageResource(R.drawable.ic_launcher);
			//holder.imageview.setLayoutParams(new CoverFlow.LayoutParams(100, 100));

			holder.imageview.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			//holder.checkbox.setChecked(photoselection[position]);
			holder.id = position;
			//BitmapDrawable drawable = (BitmapDrawable) holder.imageview.getDrawable();
			//drawable.setAntiAlias(true);
			return convertView;
		}
	}
	public class PhotoAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		ArrayList<Drawable> drawablesFromUrl = new ArrayList<Drawable>();
		Context mContext;
		FileInputStream fis;
		public PhotoAdapter() {
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
			holder.mtextview.setVisibility(View.INVISIBLE);
			if (getselectionbyalbumandposition(nowalbumid,position)){
				holder.checkbox.setChecked(true);
				//Toast.makeText(MyCustomActivity.this, "onClick", Toast.LENGTH_SHORT).show();
			} 
			else {
				holder.checkbox.setChecked(false);
				//Toast.makeText(MyCustomActivity.this, "onClick", Toast.LENGTH_SHORT).show();
			}
			
			
			holder.checkbox.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					// TODO Auto-generated method stub
					CheckBox cb = (CheckBox) v;
					int id = cb.getId();
					if (getselectionbyalbumandposition(nowalbumid,id)){
						cb.setChecked(false);
						setselectionbyalbumandposition(nowalbumid,id,false);
						selections--;
						//Toast.makeText(MyCustomActivity.this, "onClick", Toast.LENGTH_SHORT).show();
					} else {
						cb.setChecked(true);
						setselectionbyalbumandposition(nowalbumid,id,true);
						selections++;
						//Toast.makeText(MyCustomActivity.this, "onClick", Toast.LENGTH_SHORT).show();
					}
				}
			});
			/*holder.imageview.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					// TODO Auto-generated method stub
					/*int id = v.getId();
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.parse("file://" + arrPath[id]), "image/*");
					startActivity(intent);
					
					Log.d("trace","image onclick");
				}
			});*/
			//holder.imageview.setImageBitmap(thumbnails[position]);
			holder.imageview.setImageDrawable(drawablesFromUrl.get(position));
			//holder.mtextview.setText(albumNames.get(position));
			//holder.imageview.setImageResource(R.drawable.ic_launcher);
			//holder.imageview.setLayoutParams(new CoverFlow.LayoutParams(100, 100));

			holder.imageview.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			//holder.checkbox.setChecked(photoselection[position]);
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
	
	class PhotoUnit{
		String photourlsmall;
		String photourllarge;
		boolean photoselection;
	}
	
	private Drawable LoadImageFromURL(String url) {
		/*try {
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
			Log.d("trace","MalformedURLException");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.d("trace","IOException");
		}
		return null;
		*/
		try{
			InputStream is = (InputStream) new URL(url).getContent();
			Drawable d = Drawable.createFromStream(is, "src name");
			return d;
		}
		catch (Exception e) {
			Log.d("trace","Exception");
			e.printStackTrace();
			return null;
		}
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
				photos = new TreeMap< String, ArrayList<PhotoUnit>>();
				Log.d("trace","albumList.length="+albumList.length());
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
				
				
				//imagecursor.close();
				ChoosingPhoto.this.runOnUiThread(new Runnable() {
				    public void run() {
				    	
				    	for (String url : albumCoverUrls) {
				    		albumAdapter.addItem(LoadImageFromURL(url));
				    		Log.d("trace","after addItem, url="+url);
							Log.d("trace","after addItem");
				    		
				    		
				    	}
				    	albumAdapter.notifyDataSetChanged();
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
				ArrayList<PhotoUnit> albumPhotos = new ArrayList<PhotoUnit>(); 
				
				for (int i = 0; i < photoList.length(); i++) {
					String photoId = photoList.getJSONObject(i).getString("id");
					PhotoUnit p=new PhotoUnit();
					p.photoselection=false;
					p.photourlsmall="https://graph.facebook.com/" + photoId+ "/picture?type=thumbnail&access_token=" + accessToken;
					p.photourllarge="https://graph.facebook.com/" + photoId+ "/picture?type=normal&access_token=" + accessToken;
					albumPhotos.add(p);
					Log.d("facebookURL","album "+ (String)state+"  photo: "+ i +"  https://graph.facebook.com/" + photoId
							+ "/picture?type=thumbnail&access_token=" + accessToken);
				}
				if( state.getClass().equals(String.class)){
					Log.d("tracephoto",(String)state);
					
					photos.put( (String)state, albumPhotos);
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
	
	boolean getselectionbyalbumandposition(String albumid,int index){
		ArrayList<PhotoUnit> photounit=photos.get(albumid);
		PhotoUnit p =photounit.get(index);
		if(p.photoselection==true)
			return true;
		else
			return false;
	}
	void setselectionbyalbumandposition(String albumid,int index,boolean select){
		ArrayList<PhotoUnit> photounit=photos.get(albumid);
		PhotoUnit p =photounit.get(index);
		p.photoselection=select;
		photounit.set(index, p);
		
	}
	
	
}
