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

		intentFilter.addAction(WeightSensorService.EVENT_WEIGHT_SENSOR_CONNECTED);
		intentFilter.addAction(WeightSensorService.EVENT_WEIGHT_SENSOR_DISCONNECTED);
		intentFilter.addAction(WeightSensorService.EVENT_WEIGHT_SENSOR_WEIGHT_GONE);
		intentFilter.addAction(WeightSensorService.EVENT_WEIGHT_SENSOR_WEIGHT_PRESENT);

		return intentFilter;
	}
}
