package edu.ntu.mobile.smallelephant.ader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import edu.ntu.mobile.smallelephant.ader.AsyncImageLoader.ImageCallback;

public class ImageAndTextListAdapter extends ArrayAdapter<ImageAndText> {
	 
    private ListView listView;
    private AsyncImageLoader asyncImageLoader;
    public static Map<Integer, Boolean> isSelected;
    public static Map<Integer, Boolean> isOnline;
    
    public ImageAndTextListAdapter(Activity activity, List<ImageAndText> imageAndTexts, ListView listView) {
        super(activity, 0, imageAndTexts);
        this.listView = listView;
        asyncImageLoader = new AsyncImageLoader();
        isOnline = new HashMap<Integer, Boolean>();
        isSelected = new HashMap<Integer, Boolean>();
        for( int i = 0; i < imageAndTexts.size(); i++){
        	if( imageAndTexts.get(i).isOnline()){
        		isOnline.put(i,true);
        	}
        	else isOnline.put(i,false);
        	isSelected.put(i, false);
        }
    }
    @Override
    public boolean isEnabled(int position){
		return isOnline.get(position);
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
 
        // Load the image and set it on the ImageView

        Log.d("ImageAndText", "Load the image and set it on the ImageView "+ imageAndText.getImageUrl());
        String imageUrl = imageAndText.getImageUrl();
        ImageView imageView = viewCache.getImageView();
        imageView.setTag(imageUrl);
        Drawable cachedImage = asyncImageLoader.loadDrawable(imageUrl, new ImageCallback() {
            public void imageLoaded(Drawable imageDrawable, String imageUrl) {
            	Log.d("ImageAndText", "drawable loaded   "+ imageUrl);
                ImageView imageViewByTag = (ImageView) listView.findViewWithTag(imageUrl);
                if (imageViewByTag != null) {
                	imageViewByTag.setImageDrawable(imageDrawable);
                	Log.d("ImageAndText", "drawable set on imageViewByTag   "+ imageUrl);
                }
            }
        });
        imageView.setImageDrawable(cachedImage);
        Log.d("ImageAndText", "drawable set on imageView   "+ imageUrl);
        // Set the text on the TextView
        TextView textView = viewCache.getTextView();
        textView.setText(imageAndText.getText());
        
//        // Set the state on the CheckBox
        CheckBox checkbox = viewCache.getCheckbox();
        checkbox.setChecked(false);
//        checkbox.setOnline(true);
        
        return rowView;
    }
}