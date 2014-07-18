package com.sitter.sittermockbaby;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
	
	private IBeaconService beaconService;
	
	private final ServiceConnection proxServiceConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			beaconService = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			beaconService = ((IBeaconService.LocalBinder) service).getService();
		}
	};
	
	private final BroadcastReceiver proximityReciever = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String message = intent.getExtras().getString(IBeaconService.MESSAGE);
			Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button b = (Button) findViewById(R.id.startRangingButton);
		b.setOnClickListener(this);
		b = (Button) findViewById(R.id.stopRangingButton);
		b.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.startRangingButton) {
			bindService(new Intent(getApplicationContext(), IBeaconService.class), proxServiceConnection, BIND_AUTO_CREATE);
			registerReceiver(proximityReciever, makeIBeaconIntentFilter());
		} else {
			unbindService(proxServiceConnection);
			unregisterReceiver(proximityReciever);
		}
	}
	
	private IntentFilter makeIBeaconIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();

		intentFilter.addAction(IBeaconService.EVENT_LOG);

		return intentFilter;
	}
}
