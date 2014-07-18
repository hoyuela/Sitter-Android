package com.solstice.sitter;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;

import com.solstice.sitterble.BluetoothProximityService;

public class ProximityTestActivity extends Activity {

	private BluetoothProximityService proxService;
	
	private final ServiceConnection proxServiceConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			proxService = ((BluetoothProximityService.LocalBinder) service).getService();
			Log.i("sitter", "service connected");
		}
	};
	
	private final BroadcastReceiver proximityReciever = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.i("sitter", "Recieved intent: " + action);
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		bindService(new Intent(getApplicationContext(), BluetoothProximityService.class), proxServiceConnection, BIND_AUTO_CREATE);
		registerReceiver(proximityReciever, createIntentFilter());
		
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		
		((TextView) findViewById(R.id.label)).setText(adapter.getAddress());
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		unbindService(proxServiceConnection);
		unregisterReceiver(proximityReciever);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	private IntentFilter createIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();

		intentFilter.addAction(BluetoothProximityService.EVENT_PROXIMITY_ALERT);

		return intentFilter;
	}

}
