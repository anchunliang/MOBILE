package edu.ntu.mobile.smallelephant.ader;

import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class InvitationListener extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle extras = intent.getExtras();
		if (extras != null) {
			JSONObject data = null;
			String action = null;
			String title = null;
			String alert = null;
			try {
				data = new JSONObject(extras.getString("com.parse.Data"));
				action = data.getString("action");
				title = data.getString("title");
				alert = data.getString("alert");
			} catch (Exception e) {
				// TODO: handle exception
				e.getStackTrace();
			}
			Log.d(CONSTANT.DEBUG_BROADCAST, "Invitation >> action: "+ action+"\ttitle: "+title +"\tbundle:"+data );
		}
	}
}