package edu.ntu.mobile.smallelephant.ader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import edu.ntu.mobile.smallelephant.ader.AsyncImageLoader.ImageCallback;

public class ImageAndTextListAdapter extends ArrayAdapter<ImageAndText> {

	private ListView listView;
	private AsyncImageLoader asyncImageLoader;
	public Map<Integer, Boolean> isSelected;
	public Map<Integer, Boolean> isOnline;

	public ImageAndTextListAdapter(Activity activity,
			List<ImageAndText> imageAndTexts, ListView listView) {
		super(activity, 0, imageAndTexts);
		this.listView = listView;
		asyncImageLoader = new AsyncImageLoader();
		isOnline = new HashMap<Integer, Boolean>();
		isSelected = new HashMap<Integer, Boolean>();
		for (int i = 0; i < imageAndTexts.size(); i++) {
			if (imageAndTexts.get(i).isOnline()) {
				isOnline.put(i, true);
			} else
				isOnline.put(i, false);
			isSelected.put(i, false);
		}
		Log.d("CheckBox",
				"constructor :: isSelected size = " + isSelected.size()
						+ " isOnline size = " + isOnline.size());
	}

	@Override
	public boolean isEnabled(int position) {
		return isOnline.get(position) == null ? false : isOnline.get(position);
	}

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
		ImageAndText imageAndText = getItem(position);

		rowView.setClickable(true);
		rowView.setFocusable(true);
		//rowView.setBackgroundResource(android.R.drawable.menuitem_background);
		rowView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ViewCache vc = (ViewCache)v.getTag();
				if( vc.getCheckbox().isEnabled())
					vc.getCheckbox().toggle();
				Log.d("CheckBox", "item checked!  online? "+vc.getCheckbox().isEnabled()+"  checked? "+vc.getCheckbox().isChecked());
			}

		});
		// Load the image and set it on the ImageView

		Log.d("ImageAndText", "Load the image and set it on the ImageView "
				+ imageAndText.getImageUrl());
		String imageUrl = imageAndText.getImageUrl();
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
		textView.setText(imageAndText.getText());

		// // Set the state on the CheckBox
		viewCache.getCheckbox();
		if (isSelected.get(position) != null)
			viewCache.getCheckbox().setChecked(isSelected.get(position));
		if (isOnline.get(position) != null){
			Log.d("online", position + "   is " + (isOnline.get(position)?"online":"offline"));
			viewCache.getCheckbox().setEnabled(isOnline.get(position));
			if( ! isOnline.get(position))
				viewCache.getTextView().setTextColor(Color.GRAY);
			else viewCache.getTextView().setTextColor(0xffCC6600);
		}
		// CheckBox.setOnline(true);
		Log.d("CheckBox", "get View!  online? "+viewCache.getCheckbox().isEnabled()+"  checked? "+viewCache.getCheckbox().isChecked());
		viewCache.getCheckbox().refreshDrawableState();
		return rowView;
	}
}