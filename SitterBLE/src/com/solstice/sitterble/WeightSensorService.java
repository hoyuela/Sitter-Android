package com.solstice.sitterble;

import java.io.UnsupportedEncodingException;

import android.app.Service;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class WeightSensorService extends Service {
	private static final String TAG = WeightSensorService.class.getCanonicalName();

	public static String EVENT_WEIGHT_SENSOR_CONNECTED = "EVENT_WEIGHT_SENSOR_CONNECTED";
	public static String EVENT_WEIGHT_SENSOR_DISCONNECTED = "EVENT_WEIGHT_SENSOR_DISCONNECTED";
	public static String EVENT_WEIGHT_SENSOR_WEIGHT_PRESENT = "EVENT_WEIGHT_SENSOR_WEIGHT_PRESENT";
	public static String EVENT_WEIGHT_SENSOR_WEIGHT_GONE = "EVENT_WEIGHT_SENSOR_WEIGHT_GONE";
	public static String EVENT_WEIGHT_SENSOR_TEMPERATURE = "EVENT_WEIGHT_SENSOR_TEMPERATURE";

	public class LocalBinder extends Binder {
		public WeightSensorService getService() {
			return WeightSensorService.this;
		}
	}
	private final IBinder binder = new LocalBinder();
	private BLEService bluetoothLeService;

	private final ServiceConnection bleServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName componentName, IBinder service) {
			bluetoothLeService = ((BLEService.LocalBinder) service).getService();
			Log.i(TAG, "Weight sensor service connected to BLEService");
			if (!bluetoothLeService.initialize()) {
				Log.e(TAG, "Unable to initialize BLESergice");
				stopSelf();
			} else {
				Log.i(TAG, "BLEService initialized, connecting to device");
				bluetoothLeService.connect("D1:E9:89:94:04:FB");
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			Log.i(TAG, "BLEService disconnected from Weight Sensor service: " + componentName.flattenToShortString());
			bluetoothLeService = null;
		}
	};

	private final BroadcastReceiver gattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();

			Log.i(TAG, "Weight sensor service received BLEService broadcast with action " + action);

			Intent broadcastIntent;

			if (BLEService.ACTION_GATT_CONNECTED.equals(action)) {
				Toast.makeText(getApplicationContext(), "Gatt Connected", Toast.LENGTH_SHORT).show();
				broadcastIntent = new Intent(EVENT_WEIGHT_SENSOR_CONNECTED);
				sendBroadcast(broadcastIntent);
			} else if (BLEService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
				Toast.makeText(getApplicationContext(), "Gatt services discovered", Toast.LENGTH_SHORT).show();
				getGattService(bluetoothLeService.getSupportedGattService());
			} else if (BLEService.ACTION_GATT_DISCONNECTED.equals(action)) {
				Toast.makeText(getApplicationContext(), "Gatt Disconnected", Toast.LENGTH_SHORT).show();
				broadcastIntent = new Intent(EVENT_WEIGHT_SENSOR_DISCONNECTED);
				sendBroadcast(broadcastIntent);
			} else if (BLEService.ACTION_DATA_AVAILABLE.equals(action)) {
				Toast.makeText(getApplicationContext(), "Gatt received data", Toast.LENGTH_SHORT).show();
				Log.i(TAG, "Got data in activity");

				try {
					String data = new String(intent.getByteArrayExtra(BLEService.EXTRA_DATA), "UTF-8");
					Log.i(TAG, "Data is " + data);

					String event;
					boolean weightPresent = true;
					if (weightPresent) {
						event = EVENT_WEIGHT_SENSOR_WEIGHT_PRESENT;
					} else {
						event = EVENT_WEIGHT_SENSOR_WEIGHT_GONE;
					}

					broadcastIntent = new Intent(event);
					broadcastIntent.putExtra(EVENT_WEIGHT_SENSOR_TEMPERATURE, 70);
					sendBroadcast(broadcastIntent);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
	};

	private void getGattService(BluetoothGattService gattService) {
		if (gattService == null)
			return;

		final BluetoothGattCharacteristic characteristicRx = gattService.getCharacteristic(BLEService.UUID_BLE_SHIELD_RX);
		if (characteristicRx != null) {
			Log.i(TAG, "Weight sensor service subscribing to characteristic notifications");
			bluetoothLeService.setCharacteristicNotification(characteristicRx, true);
		}
	}

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();

		intentFilter.addAction(BLEService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BLEService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(BLEService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BLEService.ACTION_DATA_AVAILABLE);

		return intentFilter;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return binder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "Weight sensor service created");
		Toast.makeText(getApplicationContext(), "Weight Sensor Service Created", Toast.LENGTH_SHORT).show();
		bindService(new Intent(this, BLEService.class), bleServiceConnection, BIND_AUTO_CREATE);
		registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter());
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(gattUpdateReceiver);
	}

}
