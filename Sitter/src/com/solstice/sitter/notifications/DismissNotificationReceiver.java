package com.solstice.sitter.notifications;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DismissNotificationReceiver extends BroadcastReceiver {
	public static final String CLEAR_INTENT = "com.sitter.intent.action.clear";
	 
    @Override
    public void onReceive(Context context, Intent intent) {
    	NotificationManager.stop();
    }
    
    static public PendingIntent getDeleteIntent(Context context)
    {
        Intent intent = new Intent(context, DismissNotificationReceiver.class);
        intent.setAction("notification_cancelled");
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}
