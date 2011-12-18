package edu.ntu.mobile.smallelephant.ader;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;


public class MyCheckbox extends Button {
	
	private static final int[] STATE_ONLINE = {R.attr.state_online};
	
	private boolean mIsOnline = false;

	public MyCheckbox(Context context, AttributeSet attrs) {
	    super(context, attrs);
	}
	public void setOnline(boolean isOnline) {mIsOnline = isOnline;}
	@Override
	protected int[] onCreateDrawableState(int extraSpace) {
	    final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
	    if (mIsOnline) {
	        mergeDrawableStates(drawableState, STATE_ONLINE);
	    }
	    return drawableState;
	}
}
