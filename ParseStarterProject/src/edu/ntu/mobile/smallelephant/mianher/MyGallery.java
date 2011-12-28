package edu.ntu.mobile.smallelephant.mianher;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONObject;

import com.parse.ParsePush;
import com.parse.PushService;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import edu.ntu.mobile.smallelephant.ader.CONSTANT;
import edu.ntu.mobile.smallelephant.ader.R;

public class MyGallery extends Activity {
	/** Called when the activity is first created. */
	//public static Facebook facebook = new Facebook("255313284527691");
	//public static AsyncFacebookRunner fbAsyncRunner = new AsyncFacebookRunner(facebook);
	CoverFlow coverFlow;
	CoverFlow scoverFlow;
	private ArrayList<String> PhotoURLS = new ArrayList<String>();
	ImageAdapter coverImageAdapter;
	sImageAdapter scoverImageAdapter; 
	private ProgressDialog progressDialog;
	private String friendId = null;
	private String friendName = null;
	private String myId = null;
	private String myName = null;
	private ArrayList<String> friendPhotosToShare = null;
	int selections;
	int friend_selections;
	ArrayList<Drawable> drawablesFromUrl;
	

	@Override
	public void onResume(){
		super.onResume();
		IntentFilter filter = new IntentFilter( CONSTANT.ACTION_CHOOSING);
		registerReceiver(photoReceiver, filter);
		IntentFilter filter2 = new IntentFilter( CONSTANT.ACTION_SHARING);
		registerReceiver(receiver, filter2);
	}
	
	@Override
	public void onPause(){
		unregisterReceiver(photoReceiver);
		unregisterReceiver(receiver);
		super.onPause();
	}
	
	
	@Override
	public void onDestroy(){
		ParsePush push = new ParsePush();
		push.setChannel(CONSTANT.PARSE_CHANNEL_TAG + friendId);
		JSONObject data = new JSONObject();
		try {
			data.put("action", CONSTANT.ACTION_SHARING);
			data.put("title1", "cancel");
			data.put("message", myId);
		} catch (Exception e) {
			// TODO: handle exception
			e.getStackTrace();
		}
		push.setData(data);
		push.sendInBackground();
		super.onDestroy();
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallery);
		drawablesFromUrl = new ArrayList<Drawable>();	
		coverImageAdapter = new ImageAdapter(this);
		scoverImageAdapter= new sImageAdapter(this);
		coverFlow = (CoverFlow) findViewById(R.id.Gallery);
		scoverFlow = (CoverFlow) findViewById(R.id.sGallery);
		progressDialog = ProgressDialog.show(MyGallery.this, "正在生成Gallery中", "請稍候...", true, false); 
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		coverFlow.setAdapter(coverImageAdapter);
		scoverFlow.setAdapter(scoverImageAdapter);
		Log.d(CONSTANT.DEBUG_BROADCAST, "gallery Listener >> subscripttions: " +  PushService.getSubscriptions(MyGallery.this).toString());
       // mThread.start();  
        Thread mThread = new Thread(new Runnable() {  
            
            public void run() {  
            	getIntentData();
        		
        		Log.d("trace", "findviewbyid");
        		scoverFlow.setOnItemClickListener(new OnItemClickListener() {
        			public void onItemClick(AdapterView<?> parent, View v,
        					int position, long id) {
        				coverFlow.setSelection(position, true);
        				
        				ParsePush push = new ParsePush();
            			push.setChannel(CONSTANT.PARSE_CHANNEL_TAG + friendId);
            			JSONObject data = new JSONObject();
            			try {
            				data.put("action", CONSTANT.ACTION_SHARING);
            				data.put("title1", "coversetposition");
            				data.put("message", position);
            			} catch (Exception e) {
            				// TODO: handle exception
            				e.getStackTrace();
            			}
            			push.setData(data);
            			push.sendInBackground();
        				//coverFlow.onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
        			}
        		});
        		// coverFlow.setAdapter(new ImageAdapter(this));
        		// scoverFlow.setAdapter(new ImageAdapter(this));
        		// coverImageAdapter.createReflectedImages();
        		coverFlow.setOnItemSelectedListener(new OnItemSelectedListener() 
                {
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long i){
                    	if(scoverFlow.getSelectedItemPosition()>position){
                    		int diff=scoverFlow.getSelectedItemPosition()-position;
                    		for(int j=0;j<diff;j++){
                    			scoverFlow.onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, null);
                      		}
                    		
                    	}
                    	else if(scoverFlow.getSelectedItemPosition()<position){
                    		int diff=position-scoverFlow.getSelectedItemPosition();
                    		for(int j=0;j<diff;j++){
                    			scoverFlow.onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
                    			
                    			/*Handler handler = new Handler(); 
                    		    handler.postDelayed(new Runnable() { 
                    		         public void run() { 
                    		              //my_button.setBackgroundResource(R.drawable.defaultcard); 
                    		         } 
                    		    }, 1000); */
                    		}
                    		
                    	}
                    	ParsePush push = new ParsePush();
            			push.setChannel(CONSTANT.PARSE_CHANNEL_TAG + friendId);
            			JSONObject data = new JSONObject();
            			try {
            				data.put("action", CONSTANT.ACTION_SHARING);
            				data.put("title1", "coverflip");
            				data.put("message", position);
            			} catch (Exception e) {
            				// TODO: handle exception
            				e.getStackTrace();
            			}
            			push.setData(data);
            			push.sendInBackground();
                    	
                    	//scoverFlow.setSelection(position, true);
                    }

        			public void onNothingSelected(AdapterView<?> arg0) {
        				// TODO Auto-generated method stub
        				
        			}

                   
                });
                    
        		/*PhotoURLS.add("http://graph.facebook.com/100000582813465/picture?type=large");
        		PhotoURLS.add("http://graph.facebook.com/1397199871/picture?type=large");
        		PhotoURLS.add("http://graph.facebook.com/1614072820/picture?type=large");
        		PhotoURLS.add("http://graph.facebook.com/1816303569/picture?type=large");
        		PhotoURLS.add("http://graph.facebook.com/100000049127720/picture?type=large");
        		PhotoURLS.add("http://graph.facebook.com/100001416500297/picture?type=large");
        		*/
        		/*
        		 * MyGallery.this.runOnUiThread(new Runnable() { public void run() { for
        		 * (String url : PhotoURLS) {
        		 * coverImageAdapter.addItem(LoadImageFromURL(url));
        		 * scoverImageAdapter.addItem(LoadImageFromURL(url)); }
        		 * coverImageAdapter.notifyDataSetChanged();
        		 * scoverImageAdapter.notifyDataSetChanged(); } });
        		 */
        		coverFlow.setSpacing(-25);
        		scoverFlow.setSpacing(-25);
        		coverFlow.setSelection(0, true);
        		scoverFlow.setSelection(0, true);
        		coverFlow.setAnimationDuration(1000);
        		scoverFlow.setAnimationDuration(1000);
                int i=0;
        		for (String url : PhotoURLS) {
        			
        			Drawable d=LoadImageFromURL(url);
        			coverImageAdapter.addItem(d);
        			
        			if(i%10==9){
        				Message msg = new Message(); 
        				msg.what = 1;  
        				mHandler.sendMessage(msg);
        			}
                    if(i==4){
                    	Message msg = new Message(); 
        				msg.what = 1;  
        				mHandler.sendMessage(msg);
                    	msg = new Message(); 
                		msg.what = 0;  
                        mHandler.sendMessage(msg);
                    }
                    i++;
        		}
        		Message msg = new Message(); 
				msg.what = 1;  
				mHandler.sendMessage(msg);
				msg = new Message(); 
        		msg.what = 0;  
                mHandler.sendMessage(msg);
        		
           }  
             
        });  
        mThread.start();  
        
		
        
		
		    

		
	}
	private Handler mHandler = new Handler(){  
        public void handleMessage(Message msg){  
            switch (msg.what) {  
            case 0:  
            	progressDialog.dismiss();   
                break;    
            case 1:
            	coverImageAdapter.notifyDataSetChanged();
    			scoverImageAdapter.notifyDataSetChanged();
            	break;
            }  
        }
    };
	public class ImageAdapter extends BaseAdapter {
		int mGalleryItemBackground;
		Context mContext;
		FileInputStream fis;

		public ImageAdapter(Context c) {
			
			mContext = c;

		}

		public boolean createReflectedImages() {
			// The gap we want between the reflection and the original image
			final int reflectionGap = 4;

			for (Drawable d : drawablesFromUrl) {
				Bitmap originalImage = ((BitmapDrawable) d).getBitmap();
				int width = originalImage.getWidth();
				int height = originalImage.getHeight();

				// This will not scale but will flip on the Y axis
				Matrix matrix = new Matrix();
				matrix.preScale(1, -1);

				// Create a Bitmap with the flip matrix applied to it.
				// We only want the bottom half of the image
				Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0,
						height / 2, width, height / 2, matrix, false);

				// Create a new bitmap with same width but taller to fit
				// reflection
				Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
						(height + height / 2), Config.ARGB_8888);

				// Create a new Canvas with the bitmap that's big enough for
				// the image plus gap plus reflection
				Canvas canvas = new Canvas(bitmapWithReflection);
				// Draw in the original image
				canvas.drawBitmap(originalImage, 0, 0, null);
				// Draw in the gap
				Paint deafaultPaint = new Paint();
				canvas.drawRect(0, height, width, height + reflectionGap,
						deafaultPaint);
				// Draw in the reflection
				canvas.drawBitmap(reflectionImage, 0, height + reflectionGap,
						null);

				// Create a shader that is a linear gradient that covers the
				// reflection
				Paint paint = new Paint();
				LinearGradient shader = new LinearGradient(0,
						originalImage.getHeight(), 0,
						bitmapWithReflection.getHeight() + reflectionGap,
						0x70ffffff, 0x00ffffff, TileMode.CLAMP);
				// Set the paint to use this shader (linear gradient)
				paint.setShader(shader);
				// Set the Transfer mode to be porter duff and destination in
				paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
				// Draw a rectangle using the paint with our linear gradient
				canvas.drawRect(0, height, width,
						bitmapWithReflection.getHeight() + reflectionGap, paint);

				ImageView imageView = new ImageView(mContext);
				imageView.setImageBitmap(bitmapWithReflection);
				imageView.setLayoutParams(new CoverFlow.LayoutParams(120, 180));
				imageView.setScaleType(ScaleType.MATRIX);
				// mImages[index++] = imageView;

			}
			return true;
		}

		public void addItem(Drawable item) {
			drawablesFromUrl.add(item);
		}

		public int getCount() {
			return drawablesFromUrl.size();
		}

		public Drawable getItem(int position) {
			return drawablesFromUrl.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			// Use this code if you want to load from resources
			ImageView i = new ImageView(mContext);
			i.setImageDrawable(drawablesFromUrl.get(position));
			i.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT));

			// i.setLayoutParams(new CoverFlow.LayoutParams(130, 130));
			i.setScaleType(ImageView.ScaleType.FIT_CENTER/* CENTER_INSIDE */);

			// Make sure we set anti-aliasing otherwise we get jaggies
			/*BitmapDrawable drawable = (BitmapDrawable) i.getDrawable();
			drawable.setAntiAlias(true);*/
			return i;

			// return mImages[position];
		}

		/**
		 * Returns the size (0.0f to 1.0f) of the views depending on the
		 * 'offset' to the center.
		 */
		public float getScale(boolean focused, int offset) {
			/* Formula: 1 / (2 ^ offset) */
			return Math.max(0, 1.0f / (float) Math.pow(2, Math.abs(offset)));
		}
		
	}

	public class sImageAdapter extends ImageAdapter {

		public sImageAdapter(Context c) {
			super(c);
			// TODO Auto-generated constructor stub
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {

			// Use this code if you want to load from resources
			ImageView i = new ImageView(mContext);
			i.setImageDrawable(drawablesFromUrl.get(position));
			// i.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
			// LayoutParams.FILL_PARENT));
			i.setLayoutParams(new CoverFlow.LayoutParams(70, 70));

			i.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			// i.setLayoutParams(new CoverFlow.LayoutParams(130, 130));
			// i.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

			// Make sure we set anti-aliasing otherwise we get jaggies
			//BitmapDrawable drawable = (BitmapDrawable) i.getDrawable();
			//drawable.setAntiAlias(true);
			return i;

			// return mImages[position];
		}

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
			//d.setBounds(0, 0, 800, 480);
			//System.gc();
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
		friendId = bundle.getString("friendId");
		friendName = bundle.getString("friendName");
		myId = bundle.getString("myId");
		myName = bundle.getString("myName");
		selections = Integer.parseInt(bundle.getString("selections"));
		friend_selections = Integer.parseInt(bundle.getString("friend_selections"));
		for(int i=0;i<friend_selections;i++){
			String url=bundle.getString("friend_photo"+i);
			PhotoURLS.add(url);		
		}
		for(int i=0;i<selections;i++){
			String url=bundle.getString("photo"+i);
			PhotoURLS.add(url);		
		}
//		
//		for(int i=0;i<friend_selections;i++){
//			String url=bundle.getString("friend_photo"+i);
//			PhotoURLS.add(url);		
//		}
	};
	/*public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// do something on back.
			coverImageAdapter=null;
			scoverImageAdapter=null;
			System.gc();
			return super.onKeyDown(keyCode, event);
		}
			

		return super.onKeyDown(keyCode, event);
	}*/
	public BroadcastReceiver photoReceiver = new BroadcastReceiver() {

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
					title = data.getString("title1");
					message = data.getString("message");
				} catch (Exception e) {
					// TODO: handle exception
					e.getStackTrace();
				}
				Log.d(CONSTANT.DEBUG_BROADCAST, "gallery Listener >>" +  data.toString());
				if( title.equals("cancel")){
					Toast.makeText(MyGallery.this, friendName+ "abort!",
							Toast.LENGTH_SHORT).show();
					onFriendAbortAlert();
					finish();
				}
				if( title.equals("finish")){
					try {
						Integer count = data.getInt("count");
//						friendPhotosToShare = new ArrayList<String>();
						if( count != null && count >0){
							for( int i = 0; i < count; i++){
								Drawable d=LoadImageFromURL(data.getString("photo"+i));
			        			coverImageAdapter.addItem(d);
			        			Message msg = new Message(); 
			    				msg.what = 1;  
			    				mHandler.sendMessage(msg);
									//PhotoURLS.add(data.getString("photo"+i));		
//								friendPhotosToShare.add(i, data.getString("photo"+i));
							}
						}
						Toast.makeText(MyGallery.this, friendName+ " 選好了照片",
								Toast.LENGTH_SHORT).show();
					} catch (Exception e) {
						// TODO: handle exception
						e.getStackTrace();
					}
					
				}
			}
		}
	};
	
	public BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle extras = intent.getExtras();
			if (extras != null) {
				JSONObject data = null;
				String action = null;
				String title = null;
				try {
					data = new JSONObject(extras.getString("com.parse.Data"));
					action = data.getString("action");
					title = data.getString("title1");
				} catch (Exception e) {
					// TODO: handle exception
					e.getStackTrace();
				}

				Log.d(CONSTANT.DEBUG_BROADCAST, "gallery Listener >>" +  data.toString());
				if( title.equals("cancel")){
					onFriendAbortAlert();
					Toast.makeText(MyGallery.this, friendName+ "abort",
							Toast.LENGTH_SHORT).show();
				}
				else if ( title.equals("scover")){
					String message = null;
					try{
						message = data.getString("message");
					}catch( Exception e){
						e.getStackTrace();
					}
					
				}
				else if ( title.equals("coversetposition")){
				
					int position = coverFlow.getSelectedItemPosition();
					try{
						position = data.getInt("message");
					}catch( Exception e){
						e.getStackTrace();
					}
					coverFlow.setSelection(position);
				}
				else if ( title.equals("coverflip")){
					int position = coverFlow.getSelectedItemPosition();
					try{
						position = data.getInt("message");
					}catch( Exception e){
						e.getStackTrace();
					}
					
					if(coverFlow.getSelectedItemPosition()>position){
                		int diff=coverFlow.getSelectedItemPosition()-position;
                		for(int j=0;j<diff;j++){
                			coverFlow.onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, null);
                		}
                	}
					else if(coverFlow.getSelectedItemPosition()<position){
                		int diff=position-coverFlow.getSelectedItemPosition();
                		for(int j=0;j<diff;j++){
                			coverFlow.onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
                		}
                	}
					
					if(scoverFlow.getSelectedItemPosition()>position){
                		int diff=scoverFlow.getSelectedItemPosition()-position;
                		for(int j=0;j<diff;j++){
                			scoverFlow.onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, null);
                		}
                	}
					else if(scoverFlow.getSelectedItemPosition()<position){
                		int diff=position-scoverFlow.getSelectedItemPosition();
                		for(int j=0;j<diff;j++){
                			scoverFlow.onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
                		}
                	}
					
					
				}
			}
		}
	};
	private void onFriendAbortAlert()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage( friendName+" aborted!").setCancelable(
                false).setNeutralButton("Ok",
                		new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								setResult(RESULT_OK);
								finish();
								dialog.cancel();
							}
						});
                
//                .setsetPositiveButton("Share",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                }).setNegativeButton("Cancel",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                });
        AlertDialog alert = builder.create();
        alert.show();
    }


}
