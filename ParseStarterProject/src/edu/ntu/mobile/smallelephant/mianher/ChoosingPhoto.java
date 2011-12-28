package edu.ntu.mobile.smallelephant.mianher;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.MalformedURLException;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.support.v4.view.MenuItem.OnMenuItemClickListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.parse.Parse;

import edu.ntu.mobile.smallelephant.ader.CONSTANT;
import edu.ntu.mobile.smallelephant.ader.R;

public class ChoosingPhoto extends FragmentActivity {
	// the menu button's Id
	private final int RESET = 2000;
	private final int SELECTALL = 2001;

	public static Facebook facebook = new Facebook("255313284527691");
	public static AsyncFacebookRunner fbAsyncRunner = new AsyncFacebookRunner(
			facebook);
	// ��??��?����everyone, custom, private...
	private List<String> ALBUMPRIVACY = Arrays.asList("everyone");
	// albums�d
	private ArrayList<String> albumIds;
	private ArrayList<String> albumNames;
	private Map<String, PhotoAdapter> photoadaptermap;
	// albums�over photo��?rl
	private ArrayList<String> albumCoverUrls;
	// ����?��?��url: ?��pair ( albumId, album?��?������?��?�rl)
	private TreeMap<String, ArrayList<PhotoUnit>> photos;
	// private TreeMap<String,ArrayList<Boolean>> photoselection;
	String accessToken;
	String myId;
	String myName;
	String friendId;
	String friendIp;
	String friendName;
	String nowalbumid;
	// String friendIds[];
	private int count;
	private Bitmap[] thumbnails;
	// private boolean[] photoselection;
	private String[] arrPath;
	private AlbumAdapter albumAdapter;
	private PhotoAdapter photoAdapter;
	GridView albumgrid;
	GridView photogrid;
	ImageView mimage;
	private ProgressDialog AlbumprogressDialog;
	private ProgressDialog PhotoprogressDialog;
	int flag=0;
	int selections=0; 
	Intent intent1;
	Bundle bundle1;
	//private ArrayList<String> PhotoURLS = new ArrayList<String>();
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		menu.findItem(RESET).setVisible(flag != 0);
		if (flag == 1) {
			if (isNoSelectionInAlbum(nowalbumid)) {
				menu.findItem(RESET).setIcon(R.drawable.ic_menu_mark);
			} else {
				menu.findItem(RESET).setIcon(R.drawable.ic_menu_clear_playlist);
			}
		} else {
			menu.findItem(RESET).setIcon(R.drawable.ic_menu_mark);
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// menu.add(0, SELECTALL, 0,"All")
		// // .setIcon(R.drawable.ic_menu_revert)
		// .setOnMenuItemClickListener(new OnMenuItemClickListener() {
		//
		// @Override
		// public boolean onMenuItemClick(MenuItem item) {
		// // TODO Auto-generated method stub
		// if( flag ==1){
		// selectAllSelectionByAlbumId(nowalbumid);
		// Toast.makeText(getApplicationContext(),
		// "album "+ nowalbumid + " all selected!", Toast.LENGTH_SHORT).show();
		// }
		// return false;
		// }
		// })
		// .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS|
		// MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		//

		menu.add(0, RESET, 0, "Reset").setIcon(R.drawable.ic_menu_mark)
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {
						// TODO Auto-generated method stub
						if (flag == 1) {
							if (isNoSelectionInAlbum(nowalbumid)) {
								selectAllSelectionByAlbumId(nowalbumid);
								Toast.makeText(
										getApplicationContext(),
										"相簿 \"" + albumNames.get(albumIds.indexOf(nowalbumid))
												+ " \"已全部選取",
										Toast.LENGTH_SHORT).show();
							} else {
								resetSelectionByAlbumId(nowalbumid);
								Toast.makeText(getApplicationContext(),
										"相簿 \"" +  albumNames.get(albumIds.indexOf(nowalbumid)) + " \"已全部取消",
										Toast.LENGTH_SHORT).show();
							}

						}
						return false;
					}
				}).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		menu.add("Logout").setIcon(R.drawable.ic_menu_set_as)
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {
						// TODO Auto-generated method stub
						// item.setActionView(R.layout.indeterminate_progress_action);
						Toast.makeText(getApplicationContext(),
								"logout button clicked!", Toast.LENGTH_SHORT)
								.show();
						setResult(CONSTANT.RESULT_LOGOUT);
						finish();
						return false;
					}
				}).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		//
		menu.add("Start").setIcon(R.drawable.ic_menu_forward)
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(ChoosingPhoto.this,
								MyGallery.class);
						Bundle bundle = new Bundle();

						int count = 0;
						if (selections == 0)
							Toast.makeText(ChoosingPhoto.this,
									"Please select some photos.",
									Toast.LENGTH_SHORT).show();
						else {
							for (String albumid : albumIds) {
								ArrayList<PhotoUnit> photounits = photos
										.get(albumid);
								for (PhotoUnit p : photounits) {
									if (p.photoselection) {
										bundle.putString("photo" + count,
												p.photourllarge);
										count++;
									}
								}
							}
							if (count == selections) {
								bundle.putString("selections", "" + selections);
								intent.putExtras(bundle);
								startActivity(intent);

							} else {
								Log.d("trace", "selection count not match!");

							}

						}
						return false;
					}
				}).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		// menu.add("Refresh")
		// .setIcon(R.drawable.ic_refresh)
		// .setOnMenuItemClickListener(new OnMenuItemClickListener() {
		//
		// @Override
		// public boolean onMenuItemClick(MenuItem item) {
		// // TODO Auto-generated method stub
		// Toast.makeText(getApplicationContext(),
		// "Refresh button clicked!", Toast.LENGTH_SHORT).show();
		// refreshFriendStatus();
		// return false;
		// }
		// })
		// .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		//
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.album_main);
		AlbumprogressDialog = ProgressDialog.show(ChoosingPhoto.this,
				"讀取相簿列表中", "請稍候...", true, false);
		AlbumprogressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		intent1 = new Intent(ChoosingPhoto.this,MyGallery.class);
		bundle1 = new Bundle();
		photoadaptermap = new HashMap<String,PhotoAdapter>();
		getSupportActionBar().setTitle("相簿列表");
		
		albumgrid = (GridView) findViewById(R.id.AlbumGrid);
		photogrid = (GridView) findViewById(R.id.PhotoGrid);
		
		photogrid.setVisibility(View.GONE);
		albumAdapter = new AlbumAdapter();
		albumgrid.setAdapter(albumAdapter);

		Parse.initialize(this, "L6Qx3IQVB2zNv3bHrUzTwNbak0MF1xHQHqE2BVCc",
				"ksAA2JMvQVhQwnWLV8ZanZIChJlpsGIRUfKo3GIX");
		getIntentData();
		facebook.setAccessToken(accessToken);
		Log.d("facebookURL", "send album request");

		Thread mThread = new Thread(new Runnable() {

			public void run() {

				fbAsyncRunner.request(myId + "/albums", albumsRequestListener);

			}
		});
		mThread.start();

	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				albumAdapter.notifyDataSetChanged();
				AlbumprogressDialog.dismiss();
				break;
			case 1:
				Log.d("time", "handler begin Tid=" + getTaskId());
				albumgrid.setVisibility(View.GONE);
				getSupportActionBar().setTitle("勾選分享相片");
				photogrid.setVisibility(View.VISIBLE);
            	photogrid.setAdapter(photoAdapter);
            	PhotoprogressDialog.dismiss();
            	Log.d("time","handler finish Tid="+getTaskId());
            	break; 
            case 2:
            	bundle1.putString("selections", ""+selections);
				intent1.putExtras(bundle1);
				startActivity(intent1);
                break;   
            case 3:
            	albumAdapter.notifyDataSetChanged();
				break;
            case 4:
            	photoAdapter.notifyDataSetChanged();
				break;
            }  
        }  
    };  
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0
				&& flag == 1) {
			// do something on back.
			flag = 0;
			ChoosingPhoto.this.runOnUiThread(new Runnable() {
				public void run() {
					albumgrid.setVisibility(View.VISIBLE);
					getSupportActionBar().setTitle("相簿列表");
					photogrid.setVisibility(View.GONE);
					invalidateOptionsMenu();

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
		public void setItem(int index,Drawable item) {
			drawablesFromUrl.set(index,item);
		}
		public void addItem(Drawable item) {
			drawablesFromUrl.add(item);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.album_item, null);
				holder.imageview = (ImageView) convertView
						.findViewById(R.id.thumbImage);
				holder.checkbox = (CheckBox) convertView
						.findViewById(R.id.itemCheckBox);
				holder.checkbox.setVisibility(View.INVISIBLE);
				holder.mtextview = (TextView) convertView
						.findViewById(R.id.textView1);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			// holder.checkbox.setId(position);
			holder.imageview.setId(position);
			holder.mtextview.setId(position);
			/*
			 * holder.checkbox.setOnClickListener(new OnClickListener() {
			 * 
			 * public void onClick(View v) { // TODO Auto-generated method stub
			 * CheckBox cb = (CheckBox) v; int id = cb.getId(); if
			 * (photoselection[id]){ cb.setChecked(false); photoselection[id] =
			 * false; //Toast.makeText(MyCustomActivity.this, "onClick",
			 * Toast.LENGTH_SHORT).show(); } else { cb.setChecked(true);
			 * photoselection[id] = true;
			 * //Toast.makeText(MyCustomActivity.this, "onClick",
			 * Toast.LENGTH_SHORT).show(); } } });
			 */
			holder.imageview.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					// TODO Auto-generated method stub
					flag = 1;// select photo
					final int id = ((ImageView) v).getId();
					nowalbumid = albumIds.get(id);
					invalidateOptionsMenu();
					if (photoadaptermap.containsKey(albumIds.get(id))) {
						nowalbumid = albumIds.get(id);
						photoAdapter = photoadaptermap.get(albumIds.get(id));
						albumgrid.setVisibility(View.GONE);
						getSupportActionBar().setTitle("勾選分享相片");
						photogrid.setVisibility(View.VISIBLE);
						photogrid.setAdapter(photoAdapter);
					} else {
						photoAdapter = new PhotoAdapter();
						PhotoprogressDialog = ProgressDialog.show(
								ChoosingPhoto.this, "讀取相片中", "請稍候...", true,
								false);
						PhotoprogressDialog
								.setProgressStyle(ProgressDialog.STYLE_SPINNER);
						// invalidateOptionsMenu();

						Thread mThread = new Thread(new Runnable() {

							public void run() {

								Log.d("time", "onclick begin Tid=");

								nowalbumid = albumIds.get(id);
								/*
								 * ChoosingPhoto.this.runOnUiThread(new
								 * Runnable() { public void run() {
								 */

								ArrayList<PhotoUnit> photo = photos
										.get(albumIds.get(id));
								for(int i=0;i<photo.size();i++){
									photoAdapter.addItem(getResources().getDrawable(R.drawable.question));	
								}
								Message msg = new Message();
								msg.what = 1;
								mHandler.sendMessage(msg);
								int i=0;
								for (PhotoUnit photounit : photo) {
									
									photoAdapter
											.setItem(i,LoadImageFromURL(photounit.photourlsmall));
									i++;
									Log.d("trace",
											"photoadapter after addItem, url="
													+ photounit.photourlsmall);
									msg = new Message();
									msg.what = 4;
									mHandler.sendMessage(msg);

								}
								// photoAdapter.notifyDataSetChanged();
								Log.d("time", "onclick finish Tid="
										+ getTaskId());
								photoadaptermap.put(nowalbumid, photoAdapter);
								/*msg = new Message();
								msg.what = 1;
								mHandler.sendMessage(msg);*/
								// }
								// });
								Log.d("trace", "image onclick");
							}
						});
						mThread.start();
						// photoAdapter = new PhotoAdapter();
					}

				}
			});
			// holder.imageview.setImageBitmap(thumbnails[position]);
			holder.imageview.setImageDrawable(drawablesFromUrl.get(position));
			try{
				String s=albumNames.get(position);
				holder.mtextview.setText(s);
			}
			catch(Exception e){
				holder.mtextview.setText("");
				e.printStackTrace();
			}
			// holder.imageview.setImageResource(R.drawable.ic_launcher);
			// holder.imageview.setLayoutParams(new CoverFlow.LayoutParams(100,
			// 100));

			holder.imageview.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			// holder.checkbox.setChecked(photoselection[position]);
			holder.id = position;
			// BitmapDrawable drawable = (BitmapDrawable)
			// holder.imageview.getDrawable();
			// drawable.setAntiAlias(true);
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
		public void setItem(int index,Drawable item) {
			drawablesFromUrl.set(index,item);
		}
		public void addItem(Drawable item) {
			drawablesFromUrl.add(item);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.album_item, null);
				holder.imageview = (ImageView) convertView
						.findViewById(R.id.thumbImage);
				holder.checkbox = (CheckBox) convertView
						.findViewById(R.id.itemCheckBox);
				holder.mtextview = (TextView) convertView
						.findViewById(R.id.textView1);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.checkbox.setId(position);
			holder.imageview.setId(position);
			holder.mtextview.setId(position);
			holder.mtextview.setVisibility(View.GONE);
			if (getselectionbyalbumandposition(nowalbumid,position)){
				holder.checkbox.setChecked(true);
				// Toast.makeText(MyCustomActivity.this, "onClick",
				// Toast.LENGTH_SHORT).show();
			} else {
				holder.checkbox.setChecked(false);
				// Toast.makeText(MyCustomActivity.this, "onClick",
				// Toast.LENGTH_SHORT).show();
			}

			holder.checkbox.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					// TODO Auto-generated method stub
					CheckBox cb = (CheckBox) v;
					int id = cb.getId();
					if (getselectionbyalbumandposition(nowalbumid, id)) {
						cb.setChecked(false);
						setselectionbyalbumandposition(nowalbumid, id, false);
						selections--;
						// Toast.makeText(MyCustomActivity.this, "onClick",
						// Toast.LENGTH_SHORT).show();
					} else {
						cb.setChecked(true);
						setselectionbyalbumandposition(nowalbumid, id, true);
						selections++;
						// Toast.makeText(MyCustomActivity.this, "onClick",
						// Toast.LENGTH_SHORT).show();
					}
				}
			});
			/*
			 * holder.imageview.setOnClickListener(new OnClickListener() {
			 * 
			 * public void onClick(View v) { // TODO Auto-generated method stub
			 * /*int id = v.getId(); Intent intent = new Intent();
			 * intent.setAction(Intent.ACTION_VIEW);
			 * intent.setDataAndType(Uri.parse("file://" + arrPath[id]),
			 * "image/*"); startActivity(intent);
			 * 
			 * Log.d("trace","image onclick"); } });
			 */
			// holder.imageview.setImageBitmap(thumbnails[position]);
			holder.imageview.setImageDrawable(drawablesFromUrl.get(position));
			// holder.mtextview.setText(albumNames.get(position));
			// holder.imageview.setImageResource(R.drawable.ic_launcher);
			// holder.imageview.setLayoutParams(new CoverFlow.LayoutParams(100,
			// 100));

			holder.imageview.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			// holder.checkbox.setChecked(photoselection[position]);
			holder.id = position;
			// BitmapDrawable drawable = (BitmapDrawable)
			// holder.imageview.getDrawable();
			// drawable.setAntiAlias(true);
			return convertView;
		}
	}

	class ViewHolder {
		ImageView imageview;
		CheckBox checkbox;
		TextView mtextview;
		int id;
	}

	class PhotoUnit {
		String photourlsmall;
		String photourllarge;
		boolean photoselection;
	}

	private Drawable LoadImageFromURL(String url) {
		/*
		 * try { URL URL = new URL(url); URLConnection conn =
		 * URL.openConnection();
		 * 
		 * HttpURLConnection httpConn = (HttpURLConnection) conn;
		 * httpConn.setRequestMethod("GET"); httpConn.connect();
		 * 
		 * if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
		 * InputStream inputStream = httpConn.getInputStream();
		 * 
		 * Bitmap b = BitmapFactory.decodeStream(inputStream);
		 * inputStream.close(); Drawable d = new BitmapDrawable(b);
		 * Log.d("trace","Load image OK"); return d; //
		 * mImage.setImageBitmap(bitmap); } } catch (MalformedURLException e1) {
		 * // TODO Auto-generated catch block
		 * Log.d("trace","MalformedURLException"); } catch (IOException e) { //
		 * TODO Auto-generated catch block Log.d("trace","IOException"); }
		 * return null;
		 */
		try {
			InputStream is = (InputStream) new URL(url).getContent();
			Drawable d = Drawable.createFromStream(is, "src name");
			return d;
		} catch (Exception e) {
			Log.d("trace", "Exception");
			e.printStackTrace();
			return null;
		}
	}

	private void getIntentData() {
		Bundle bundle = this.getIntent().getExtras();
		accessToken = bundle.getString("accessToken");
		myId = bundle.getString("myId");
		Log.d("facebookURL", "myId is: " + myId);
		myName = bundle.getString("myName");
		friendId = bundle.getString("friendId");
		friendName = bundle.getString("friendName");
		friendIp = bundle.getString("friendIp");
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
				// �踹?����album ��id
				result = new JSONObject(response);
				albumList = result.getJSONArray("data");
				albumIds = new ArrayList<String>();
				albumNames = new ArrayList<String>();
				albumCoverUrls = new ArrayList<String>();
				photos = new TreeMap<String, ArrayList<PhotoUnit>>();
				Log.d("trace", "albumList.length=" + albumList.length());
				Message msg;
				for (int i = 0; i < albumList.length(); i++) {
					if (ALBUMPRIVACY.contains(albumList.getJSONObject(i).getString("privacy"))) {
						albumAdapter.addItem(getResources().getDrawable(R.drawable.question));	
						
					}
				}
				msg = new Message();
				msg.what = 0;
				mHandler.sendMessage(msg);
				int j=0;
				for (int i = 0; i < albumList.length(); i++) {
					Log.d("facebookURL", "album " + i);
					
					if (ALBUMPRIVACY.contains(albumList.getJSONObject(i)
							.getString("privacy"))) {
						
						String albumId = albumList.getJSONObject(i).getString(
								"id");
						String albumName = albumList.getJSONObject(i)
								.getString("name");
						albumIds.add(albumId);
						albumNames.add(albumName);
						
						albumCoverUrls.add("https://graph.facebook.com/"
								+ albumId + "/picture?type=small&access_token="
								+ accessToken);
						albumAdapter.setItem(j,LoadImageFromURL("https://graph.facebook.com/"	+ albumId + "/picture?type=small&access_token="	+ accessToken));
						j++;
						msg = new Message();
						msg.what = 3;
						mHandler.sendMessage(msg);
						
						
						Log.d("trace_album", "https://graph.facebook.com/"
								+ albumId + "/picture?type=small&access_token="
								+ accessToken);
						fbAsyncRunner.request(albumId + "/photos",
								albumPhotoRequestListener, albumId);
					}
				}

				// imagecursor.close();
				/*
				 * ChoosingPhoto.this.runOnUiThread(new Runnable() { public void
				 * run() {
				 */

				/*for (String url : albumCoverUrls) {
					albumAdapter.addItem(LoadImageFromURL(url));
					Log.d("trace", "after addItem, url=" + url);
					Log.d("trace", "after addItem");

				}*/
				// albumAdapter.notifyDataSetChanged();
				// }
				// });
				/*Message msg = new Message();
				msg.what = 0;
				mHandler.sendMessage(msg);*/
				// album��?���?} catch (JSONException e) {
				// TODO: handle exception
				// e.printStackTrace();
			} catch (Exception e) {
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
				// �踹?����album ��id
				Log.d("facebookURL", "album " + (String) state);
				result = new JSONObject(response);
				photoList = result.getJSONArray("data");
				ArrayList<PhotoUnit> albumPhotos = new ArrayList<PhotoUnit>();

				for (int i = 0; i < photoList.length(); i++) {
					String photoId = photoList.getJSONObject(i).getString("id");
					JSONArray imageDatas = photoList.getJSONObject(i)
							.getJSONArray("images");
					PhotoUnit p = new PhotoUnit();
					p.photoselection = false;
					p.photourlsmall = "https://graph.facebook.com/" + photoId
							+ "/picture?type=thumbnail&access_token="
							+ accessToken;
					// p.photourllarge="https://graph.facebook.com/" + photoId+
					// "/picture?type=normal&access_token=" + accessToken;
					String imageUrl = imageUrlNormal(imageDatas);
					if (imageUrl != null) {
						p.photourllarge = imageUrl;
						Log.d(CONSTANT.DEBUG_FACEBOOK, "imageUrl : " + imageUrl);
					} else {
						p.photourllarge = "https://graph.facebook.com/"
								+ photoId
								+ "/picture?type=normal&access_token="
								+ accessToken;
						Log.d(CONSTANT.DEBUG_FACEBOOK, "imageUrl : null");
					}
					albumPhotos.add(p);
					Log.d("facebookURL", "album " + (String) state
							+ "  photo: " + i + "  https://graph.facebook.com/"
							+ photoId + "/picture?type=thumbnail&access_token="
							+ accessToken);
				}
				if (state.getClass().equals(String.class)) {
					Log.d("tracephoto", (String) state);

					photos.put((String) state, albumPhotos);
				} else
					Log.d("facebookURL", "state not correct");
				// album��?���?} catch (JSONException e) {
				// TODO: handle exception
				// e.printStackTrace();
			} catch (Exception e) {
				// TODO: handle
				// exception
				Log.d("debug", e.getMessage());
			}
		}
	};

	private String imageUrlNormal(JSONArray images) {
		try {
			for (int i = 0; i < images.length(); i++) {
				JSONObject image = images.getJSONObject(i);
				if (image.getInt("width") <= 180) {
					return image.getString("source");
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.d("debug", e.getMessage());
		}
		return null;
	}

	boolean getselectionbyalbumandposition(String albumid, int index) {
		ArrayList<PhotoUnit> photounit = photos.get(albumid);
		PhotoUnit p = photounit.get(index);
		if (p.photoselection == true)
			return true;
		else
			return false;
	}

	private boolean isNoSelectionInAlbum(String albumId) {
		if (albumId != null) {
			if (photos != null && !photos.isEmpty()
					&& photos.containsKey(albumId)) {
				ArrayList<PhotoUnit> album = photos.get(albumId);
				if (album != null) {
					for (PhotoUnit p : album) {
						if (p.photoselection == true) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	void setselectionbyalbumandposition(String albumid, int index,
			boolean select) {
		ArrayList<PhotoUnit> photounit = photos.get(albumid);
		PhotoUnit p = photounit.get(index);
		p.photoselection = select;
		photounit.set(index, p);
		invalidateOptionsMenu();

	}

	private void resetSelectionByAlbumId(String albumId) {
		setAllSelectionByAlbumId(albumId, false);
	}

	private void selectAllSelectionByAlbumId(String albumId) {
		setAllSelectionByAlbumId(albumId, true);
	}

	private void setAllSelectionByAlbumId(String albumId, boolean select) {
		ArrayList<PhotoUnit> album = photos.get(albumId);
		for (PhotoUnit p : album) {
			if (p.photoselection == !select) {
				p.photoselection = select;
				selections = selections + (select ? 1 : -1);
			}
		}
		invalidateOptionsMenu();
		photogrid.invalidateViews();
	}
	
//	private void onInvitationAlert(final String friendId)
//    {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage( friendName+" wants to share photo with you!").setCancelable(
//                false).set
//                
////                .setsetPositiveButton("Share",
////                new DialogInterface.OnClickListener() {
////                    public void onClick(DialogInterface dialog, int id) {
////                        dialog.cancel();
////                    }
////                }).setNegativeButton("Cancel",
////                new DialogInterface.OnClickListener() {
////                    public void onClick(DialogInterface dialog, int id) {
////                        dialog.cancel();
////                    }
////                });
//        AlertDialog alert = builder.create();
//        alert.show();
//    }
	
	public BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle extras = intent.getExtras();
			if (extras != null) {
				JSONObject data = null;
				String action = null;
				String title = null;
				String message = null;
				try {
					data = new JSONObject(extras.getString("com.parse.Data"));
					action = data.getString("action");
					title = data.getString("title");
					message = data.getString("message");
				} catch (Exception e) {
					// TODO: handle exception
					e.getStackTrace();
				}
				if( title.equals("cancel")){
					
				}
			}
		}
	};

}
