package edu.ntu.mobile.smallelephant.ader;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewCache {
	 
    private View baseView;
    private TextView textView;
    private ImageView imageView;
 
    public ViewCache(View baseView) {
        this.baseView = baseView;
    }
 
    public TextView getTextView() {
        if (textView == null) {
            textView = (TextView) baseView.findViewById(R.id.MyAdapter_TextView_title);
        }
        return textView;
    }
 
    public ImageView getImageView() {
        if (imageView == null) {
        	imageView = (ImageView) baseView.findViewById(R.id.MyAdapter_ImageView_icon);
        }
        return imageView;
    }
}
