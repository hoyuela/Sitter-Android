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

import com.sitter.widgets.ChildProfileView;
import com.sitter.widgets.ChildProfileView.Position;
import com.solstice.sitter.notifications.NotificationManager;
import com.solstice.sitter.notifications.NotificationType;
import com.solstice.sitterble.WeightSensorService;


public class MainActivity extends Activity {
	private ChildProfileView childOne;
	private ChildProfileView childTwo;
	private ChildProfileView childThree;

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
			
			if( action.equalsIgnoreCase(WeightSensorService.EVENT_WEIGHT_SENSOR_BABY_FORGOTTEN) ) {
				NotificationManager.notify((MainActivity)context, NotificationType.AUTOMOBILE_NOTIFICATION);
			} else if( action.equalsIgnoreCase(WeightSensorService.EVENT_WEIGHT_SENSOR_BABY_OVERHEATING) ) {
				NotificationManager.notify((MainActivity)context, NotificationType.TEMPERATURE_NOTIFICATION);
			}
		}
	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getActionBar().hide();

		childOne = (ChildProfileView) findViewById(R.id.child_01);
		updateChildOne(childOne);

		childTwo = (ChildProfileView) findViewById(R.id.child_02);
		updateChildTwo(childTwo);

		childThree = (ChildProfileView) findViewById(R.id.child_03);
		updateChildThree(childThree);
	}

	private void updateChildOne(ChildProfileView iv) {
		iv.setBorderColor(getResources().getColor(R.color.red));
		iv.setBorderWidth(16);
		iv.setChildNamePosition(Position.UPPER_RIGHT);
		iv.setChildName("Steve");
	}

	private void updateChildTwo(ChildProfileView iv) {
		iv.setBorderColor(getResources().getColor(R.color.green));
		iv.setBorderWidth(20);
		iv.setChildNamePosition(Position.UPPER_LEFT);
		iv.setBubbleScaleSize(1.25f);
		iv.setChildName("Sam");
	}

	private void updateChildThree(ChildProfileView iv) {
		 iv.setBorderColor(getResources().getColor(R.color.purple));
		 iv.setBorderWidth(18);
		 iv.setChildNamePosition(Position.LOWER_RIGHT);
		 iv.setBubbleScaleSize(1.5f);
		 iv.setChildName("Derrick");
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
	public void onResume() {
		super.onResume();
		startService(new Intent(MainActivity.this, WeightSensorService.class));
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
