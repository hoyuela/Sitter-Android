package com.solstice.sitterble;

import java.io.IOException;
import java.util.UUID;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class BluetoothProximityService extends Service {

	private static final String BLUETOOTH_UUID = "2b229de0-0e7c-11e4-9191-0800200c9a66";
	private static final String NAME = "TroyAndAbedInTheMorning";
	public static final String EVENT_PROXIMITY_ALERT = "EVENT_PROXIMITY_ALERT";
	
	private BluetoothServerSocket serverSocket;
	
	public class LocalBinder extends Binder {
		public BluetoothProximityService getService() {
			return BluetoothProximityService.this;
		}
	}
	private final IBinder binder = new LocalBinder();
	
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		try {
			serverSocket = BluetoothAdapter.getDefaultAdapter().listenUsingInsecureRfcommWithServiceRecord(NAME, UUID.fromString(BLUETOOTH_UUID));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		new Thread() {
			public void run() {
				listenForBluetoothConnection();
			}
		}.start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onUnbind(Intent intent) {
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return super.onUnbind(intent);
	}
	
	private void listenForBluetoothConnection() {
		BluetoothSocket socket = null;
		
		while (true) {
			try {
				socket = serverSocket.accept(100);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if (socket != null) {
				Intent broadcastIntent = new Intent(EVENT_PROXIMITY_ALERT);
				sendBroadcast(broadcastIntent);
				break;
			}
		}
	}
}
