package edu.ntu.mobile.smallelephant.ader;

public final class CONSTANT {
	public static final String DEBUG_TAG = "debug";
	public static final String DEBUG_FACEBOOK = DEBUG_TAG + "_facebook";
	public static final String DEBUG_PARSE = DEBUG_TAG + "_parse";
	public static final String DEBUG_SHAREPREF = DEBUG_TAG + "_sharedPreference";
	public static final String ERROR_TAG = "error";
	public static final String ERROR_FACEBOOK = ERROR_TAG + "_facebook";
	public static final String DEBUG_BROADCAST = DEBUG_TAG + "_broadcast";
	
	//broadcast action
	public static final String SHARING_COMMUNICATION = "edu.ntu.mobile.smallelephant.ader.communication";
	public static final String ACTION_INVITE = "edu.ntu.mobile.smallelephant.ader.invitation";
	public static final String ACTION_CHOOSING = "edu.ntu.mobile.smallelephant.ader.choosingphoto";
	public static final String ACTION_SHARING = "edu.ntu.mobile.smallelephant.ader.sharingphoto";
	
	//broadcast channel
	public static final String PARSE_CHANNEL_TAG = "facebookId_";
	
	//state of the user
	public static final int STATE_FREE = 0;
	public static final int STATE_WAITING = 1;
	public static final int STATE_SHARING = 2;
	
	//intent result code
	public static final int RESULT_LOGOUT = 1000;
//	<receiver android:name="com.parse.ParseBroadcastReceiver" >
//    <intent-filter >
//        <action android:name="android.intent.action.BOOT_COMPLETED" />
//        <action android:name="android.intent.action.USER_PRESENT" />
//    </intent-filter>
//</receiver>
}
