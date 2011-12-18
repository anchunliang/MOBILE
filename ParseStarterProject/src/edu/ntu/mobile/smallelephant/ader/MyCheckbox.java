package edu.ntu.mobile.smallelephant.ader;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class MyCheckbox extends Button {

	private static final int[] STATE_ONLINE = { R.attr.state_online };
	private static final int[] STATE_CHECKED = { R.attr.state_checked };
	private boolean mIsChecked = false;
	private boolean mIsOnline = false;

	public MyCheckbox(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	

	@Override
	protected int[] onCreateDrawableState(int extraSpace) {
		final int[] drawableState = super.onCreateDrawableState(extraSpace + 2);
		if (mIsOnline) {
			mergeDrawableStates(drawableState, STATE_ONLINE);
		}
		if (mIsChecked) {
			mergeDrawableStates(drawableState, STATE_CHECKED);
		}
		return drawableState;
	}

	public boolean isChecked() {
		return mIsChecked;
	}

	public void setChecked(Boolean checked) {
		mIsChecked = checked;
	}
	
	public void setOnline(boolean isOnline) {
		mIsOnline = isOnline;
	}
	
	public boolean isOnline(){
		return mIsOnline;
	}
	
	public void toggle(){
		mIsChecked = !mIsChecked;
	}
}
