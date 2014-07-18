package com.solstice.sitter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.sitter.widgets.ActivityMenu;
import com.solstice.sitter.notifications.NotificationManager;
import com.solstice.sitter.notifications.NotificationType;
import com.solstice.sitterble.WeightSensorService;


public class MainActivity extends Activity {

	private static final String TAG = MainActivity.class.getCanonicalName();
	private WeightSensorService weightSensorService;
	private boolean flag = true;

	private final ServiceConnection weightSensorServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName componentName, IBinder service) {
			weightSensorService = ((WeightSensorService.LocalBinder) service).getService();
			Log.i(TAG, "onServiceConnected");
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			Log.i(TAG, "Service disconnected from activity: " + componentName.flattenToShortString());
			weightSensorService = null;
		}
	};

	private final BroadcastReceiver weightSensorReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			Log.i(TAG, "Activity received action from WS service: " + action);
		}
	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final MainActivity activity = this;
		Button button = (Button) this.findViewById(R.id.pressme);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				NotificationManager.notify(activity, NotificationType.AUTOMOBILE_NOTIFICATION);
			}
		});
		
		Button button2 = (Button)this.findViewById(R.id.stopme);
		button2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				NotificationManager.stop();
			}
		});
		
		Button button3 = (Button)this.findViewById(R.id.openmenu);
		button3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if( !ActivityMenu.isMenuOpen() ) {
					ActivityMenu activityMenu = new ActivityMenu(activity);
					activityMenu.show();
				}
			}
		});
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
	public void onResume() {
		super.onResume();
		bindService(new Intent(this, WeightSensorService.class), weightSensorServiceConnection, BIND_AUTO_CREATE);
		registerReceiver(weightSensorReceiver, makeWeightSensorIntentFilter());
	}

	@Override
	public void onPause() {
		super.onPause();
		unbindService(weightSensorServiceConnection);
		unregisterReceiver(weightSensorReceiver);
	}

	@Override
	protected void onStop() {
		super.onStop();
		flag = false;
	}

	private static IntentFilter makeWeightSensorIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();

		intentFilter.addAction(WeightSensorService.EVENT_WEIGHT_SENSOR_BABY_FORGOTTEN);
		intentFilter.addAction(WeightSensorService.EVENT_WEIGHT_SENSOR_BABY_OVERHEATING);

		return intentFilter;
	}
	

}
