package edu.ntu.mobile.smallelephant.ader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class SharingCommunication extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle extras = intent.getExtras();
		if (extras != null) {
			String action = extras.getString("action");
			String title = extras.getString("title");
			String alert = extras.getString("alert");
			Log.d(CONSTANT.DEBUG_BROADCAST, "Sharing >> action: "+ action+"\ttitle: "+title );
//			
//			if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
//				String phoneNumber = extras
//						.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
//				Log.w("DEBUG", phoneNumber);
//			}
		}
	}
}
