package edu.ntu.mobile.smallelephant.ader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
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
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class MyGallery extends Activity {
	/** Called when the activity is first created. */
	//public static Facebook facebook = new Facebook("255313284527691");
	//public static AsyncFacebookRunner fbAsyncRunner = new AsyncFacebookRunner(facebook);
	static CoverFlow coverFlow;
	static CoverFlow scoverFlow;
	private ArrayList<String> PhotoURLS = new ArrayList<String>();
	static ImageAdapter coverImageAdapter;
	static sImageAdapter scoverImageAdapter; 

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallery);
		coverImageAdapter = new ImageAdapter(this);
		scoverImageAdapter= new sImageAdapter(this);
		coverFlow = (CoverFlow) findViewById(R.id.Gallery);
		scoverFlow = (CoverFlow) findViewById(R.id.sGallery);
		Log.d("trace", "findviewbyid");
		scoverFlow.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				//coverFlow.setSelection(position, true);
				//coverFlow.onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
			}
		});
		// coverFlow.setAdapter(new ImageAdapter(this));
		// scoverFlow.setAdapter(new ImageAdapter(this));
		// coverImageAdapter.createReflectedImages();
		coverFlow.setAdapter(coverImageAdapter);
		scoverFlow.setAdapter(scoverImageAdapter);
		coverFlow.setSpacing(-25);
		scoverFlow.setSpacing(-25);
		coverFlow.setSelection(0, true);
		scoverFlow.setSelection(0, true);
		coverFlow.setAnimationDuration(1000);
		scoverFlow.setAnimationDuration(1000);
		coverFlow.setOnItemSelectedListener(new OnItemSelectedListener() 
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long i){
            	//int diff=scoverFlow.getSelectedItemPosition()-position;
            	if(scoverFlow.getSelectedItemPosition()>position){
            		int diff=scoverFlow.getSelectedItemPosition()-position;
            		for(int j=0;j<diff;j++){
            			scoverFlow.onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, null);
            			/*Handler handler = new Handler(); 
            		    handler.postDelayed(new Runnable() { 
            		         public void run() { 
            		              //my_button.setBackgroundResource(R.drawable.defaultcard); 
            		         } 
            		    }, 1000); */
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
            	//scoverFlow.setSelection(position, true);
            }

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}

           
        });

		PhotoURLS.add("http://graph.facebook.com/100000582813465/picture?type=large");
		PhotoURLS.add("http://graph.facebook.com/1397199871/picture?type=large");
		PhotoURLS.add("http://graph.facebook.com/1614072820/picture?type=large");
		PhotoURLS.add("http://graph.facebook.com/1816303569/picture?type=large");
		PhotoURLS.add("http://graph.facebook.com/100000049127720/picture?type=large");
		PhotoURLS.add("http://graph.facebook.com/100001416500297/picture?type=large");
		/*
		 * MyGallery.this.runOnUiThread(new Runnable() { public void run() { for
		 * (String url : PhotoURLS) {
		 * coverImageAdapter.addItem(LoadImageFromURL(url));
		 * scoverImageAdapter.addItem(LoadImageFromURL(url)); }
		 * coverImageAdapter.notifyDataSetChanged();
		 * scoverImageAdapter.notifyDataSetChanged(); } });
		 */
		for (String url : PhotoURLS) {
			coverImageAdapter.addItem(LoadImageFromURL(url));
			scoverImageAdapter.addItem(LoadImageFromURL(url));
		}
		coverImageAdapter.notifyDataSetChanged();
		scoverImageAdapter.notifyDataSetChanged();
	}

	public class ImageAdapter extends BaseAdapter {
		int mGalleryItemBackground;
		Context mContext;
		ArrayList<Drawable> drawablesFromUrl = new ArrayList<Drawable>();
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
			BitmapDrawable drawable = (BitmapDrawable) i.getDrawable();
			drawable.setAntiAlias(true);
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
			BitmapDrawable drawable = (BitmapDrawable) i.getDrawable();
			drawable.setAntiAlias(true);
			return i;

			// return mImages[position];
		}

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
}