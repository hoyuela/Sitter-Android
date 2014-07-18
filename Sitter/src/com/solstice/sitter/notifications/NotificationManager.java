package com.solstice.sitter.notifications;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.BigTextStyle;
import android.support.v4.app.NotificationManagerCompat;

import com.solstice.sitter.MainActivity;
import com.solstice.sitter.R;

public class NotificationManager {
    static boolean hasStopped = false;
    static MainActivity activity;
    static NotificationType type;
	static final String EXTRA_EVENT_ID = "WARNING";
	static final int EVENT_ID = 5;
	static final int NOTIFICATION_ID = 001;
	private static Handler customHandler = new Handler();
	
	static public void notify(final MainActivity activity, final NotificationType type) {
		NotificationManager.type = type;
		NotificationManager.activity = activity;
		
		// Build intent for notification content
		Intent viewIntent = new Intent(activity, MainActivity.class);
		viewIntent.putExtra(EXTRA_EVENT_ID, EVENT_ID);
		PendingIntent viewPendingIntent =
		        PendingIntent.getActivity(activity, 0, viewIntent, 0);
	
		String bigText = "";
		String child = "Joshua";
		String contentText = "Sitter";
		switch( type ) {
		case AUTOMOBILE_NOTIFICATION:
			bigText = "Don't Forget Me!";
			break;
		case HOME_NOTIFICATION:
			bigText = "Up to no good";
			break;
		}
		
		// Specify the 'big view' content to display the long
				// event description that may not fit the normal content text.
		BigTextStyle bigStyle = new NotificationCompat.BigTextStyle();
				bigStyle.bigText(bigText);

		NotificationCompat.Builder notificationBuilder =
		        new NotificationCompat.Builder(activity)
				.setLargeIcon(BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_launcher))
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle(child)
		        .setContentText(contentText)
		        .setPriority(NotificationCompat.PRIORITY_MAX)
		        .setVibrate(new long[] { 0, 2000, 500, 2000, 500, 2000 })
		        .setContentIntent(viewPendingIntent)
		        .setLights(Color.RED, 3000, 3000)
		        .setStyle(bigStyle);

		// Get an instance of the NotificationManager service
		NotificationManagerCompat notificationManager =
		        NotificationManagerCompat.from(activity);
		
	
		// Build the notification and issues it with notification manager.
		notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
		PlayDtfmTone(activity);
			
		if( !hasStopped ) {
			customHandler.postDelayed(updateTimerThread, 5000);
		} else {
			hasStopped = false;
		}
	}
	
	 private static Runnable updateTimerThread = new Runnable() {
	         public void run() {
	        	 if( !hasStopped ) {
	        		 NotificationManager.notify(activity, type);
	        	 } else {
	     			hasStopped = false;
	     		}
	         }
	 };
	
	private static void PlayDtfmTone(final MainActivity activity) {
		AudioManager audio = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
		int maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		//float percent = 0.7f;
		int seventyVolume = (int) (maxVolume);
		audio.setStreamVolume(AudioManager.STREAM_MUSIC, seventyVolume, 0);

		ToneGenerator toneGenerator = new ToneGenerator(
				AudioManager.STREAM_DTMF, ToneGenerator.MAX_VOLUME);
		// this will play tone for 2 seconds.
		toneGenerator.startTone(ToneGenerator.TONE_DTMF_1, 2000);
	}

	public static void stop() {
		hasStopped = true;

		// Get an instance of the NotificationManager service
		NotificationManagerCompat notificationManager =
		        NotificationManagerCompat.from(activity);
		
		notificationManager.cancel(NOTIFICATION_ID);
		
	}

	
}
