package edu.ntu.mobile.smallelephant.ader;

import android.app.Activity;
import android.os.Bundle;

import com.parse.Parse;

public class ChoosingPhoto extends Activity {
	@Override
	public void onCreate( Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallery); 
		getIntentData();
		Parse.initialize(this, "L6Qx3IQVB2zNv3bHrUzTwNbak0MF1xHQHqE2BVCc",
				"ksAA2JMvQVhQwnWLV8ZanZIChJlpsGIRUfKo3GIX");
	}
	private void getIntentData(){
		Bundle bundle = this.getIntent().getExtras();
		bundle.getString("id");
		bundle.getString("name");
		bundle.getString("accessToken");
		bundle.getString("accessToken");
		bundle.getString("myId");
		bundle.getString("myName");
		Integer count = Integer.valueOf(bundle.getString("numSelectedFriends"));
		for (int i = 0; i < count; i++) {
			bundle.getString("friend" + i);
		}

	};
}
