package edu.ntu.mobile.smallelephant.ader;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import edu.ntu.mobile.smallelephant.ader.AsyncImageLoader.ImageCallback;
import edu.ntu.mobile.smallelephant.mianher.ChoosingPhoto;

public class ImageAndTextListAdapter extends ArrayAdapter<ImageAndText> {

	private ListView listView;
	private AsyncImageLoader asyncImageLoader;

	// public Map<Integer, Boolean> isSelected;
	// public Map<Integer, Boolean> isOnline;

	public ImageAndTextListAdapter(Activity activity,
			List<ImageAndText> imageAndTexts, ListView listView) {
		super(activity, 0, imageAndTexts);
		this.listView = listView;
		asyncImageLoader = new AsyncImageLoader();
		// isOnline = new HashMap<Integer, Boolean>();
		// isSelected = new HashMap<Integer, Boolean>();
		// for (int i = 0; i < imageAndTexts.size(); i++) {
		// if (imageAndTexts.get(i).isOnline()) {
		// isOnline.put(imageAndTexts.get(i).hashCode(), true);
		// } else
		// isOnline.put(imageAndTexts.get(i).hashCode(), false);
		// isSelected.put(imageAndTexts.get(i).hashCode(), false);
		// }
		// Log.d("CheckBox",
		// "constructor :: isSelected size = " + isSelected.size()
		// + " isOnline size = " + isOnline.size());
	}

	@Override
	public void add(ImageAndText item) {
		super.add(item);
		int i = this.getCount();
		Log.d("adapter", "in add , count = " + i);
		// if (item.isOnline()) {
		// isOnline.put(i, true);
		// } else{
		// isOnline.put(i, false);
		// }
		// isSelected.put(i, false);
	}

	// @Override
	// public boolean isEnabled(int position) {
	// return isOnline.get(position) == null ? false : isOnline.get(position);
	// }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Activity activity = (Activity) getContext();

		Log.d("ImageAndText", "getView");
		// Inflate the views from XML
		View rowView = convertView;
		ViewCache viewCache;
		if (rowView == null) {
			LayoutInflater inflater = activity.getLayoutInflater();
			rowView = inflater.inflate(R.layout.adapter, null);
			viewCache = new ViewCache(rowView);
			rowView.setTag(viewCache);
		} else {
			viewCache = (ViewCache) rowView.getTag();
		}
		ImageAndText item = getItem(position);
		// Load the image and set it on the ImageView
		Log.d("ImageAndText", "Load the image and set it on the ImageView "
				+ item.getImageUrl());
		String imageUrl = item.getImageUrl();
		ImageView imageView = viewCache.getImageView();
		imageView.setTag(imageUrl);
		Drawable cachedImage = asyncImageLoader.loadDrawable(imageUrl,
				new ImageCallback() {
					public void imageLoaded(Drawable imageDrawable,
							String imageUrl) {
						Log.d("ImageAndText", "drawable loaded   " + imageUrl);
						ImageView imageViewByTag = (ImageView) listView
								.findViewWithTag(imageUrl);
						if (imageViewByTag != null) {
							imageViewByTag.setImageDrawable(imageDrawable);
							Log.d("ImageAndText",
									"drawable set on imageViewByTag   "
											+ imageUrl);
						}
					}
				});
		imageView.setImageDrawable(cachedImage);
		Log.d("ImageAndText", "drawable set on imageView   " + imageUrl);
		// Set the text on the TextView
		TextView textView = viewCache.getTextView();
		textView.setText(item.getText());
		viewCache.id = item.id;
		viewCache.ip = item.ip;
		Log.d("online", position + "   is "
				+ (item.isOnline() ? "online" : "offline"));
		viewCache.getButton().setChecked(item.isOnline());
		if (!item.isOnline())
			viewCache.getTextView().setTextColor(Color.GRAY);
		else
			viewCache.getTextView().setTextColor(0xffCC6600);
		viewCache.getButton().refreshDrawableState();
		return rowView;
	}
}