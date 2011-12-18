package edu.ntu.mobile.smallelephant.ader;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewCache {
	 
    private View baseView;
    private TextView textView;
    private ImageView imageView;
    private MyCheckbox checkbox;
 
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
    
    public MyCheckbox getCheckbox() {
    	if( checkbox == null) {
    		checkbox = (MyCheckbox) baseView.findViewById(R.id.MyAdapter_CheckBox_checkBox);
    	}
    	return checkbox;
    }
    
//    public void setChecked(boolean isChecked){
//    	if( checkbox != null)
//    		checkbox.setChecked(isChecked);
//    }
//    
//    public boolean isChecked(){
//    	if( checkbox != null)
//    		return checkbox.isChecked();
//    	else return false;
//    }
//    
//    public void toggle(){
//    	if( checkbox != null)
//    		checkbox.toggle();
//    }
//    public boolean isOnline(){
//    	if( checkbox != null)
//    		return checkbox.isOnline();
//    	else return false;
//    }
}
