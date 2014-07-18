package com.solstice.sitterble;

import android.app.Notification;
import android.app.NotificationManager;
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
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class WeightSensorService extends Service {
	private static final String TAG = WeightSensorService.class.getCanonicalName();

	public static String EVENT_WEIGHT_SENSOR_BABY_FORGOTTEN = "EVENT_WEIGHT_SENSOR_BABY_FORGOTTEN";
	public static String EVENT_WEIGHT_SENSOR_BABY_OVERHEATING = "EVENT_WEIGHT_SENSOR_BABY_OVERHEATING";

	private int DISCONNECT_TIMEOUT = 10000;
	private boolean weightPresent = false;
	private Handler handler = new Handler();
	private Runnable disconnectTimout = new Runnable() {
		
		@Override
		public void run() {
			Log.i(TAG, "Disconnect timeout");
			if(weightPresent) {
				Intent intent = new Intent(EVENT_WEIGHT_SENSOR_BABY_FORGOTTEN);
				sendBroadcast(intent);
			}
		}
	};

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


			if (BLEService.ACTION_GATT_CONNECTED.equals(action)) {
				Toast.makeText(getApplicationContext(), "Gatt Connected", Toast.LENGTH_SHORT).show();
				handler.removeCallbacks(disconnectTimout);
			} else if (BLEService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
				Toast.makeText(getApplicationContext(), "Gatt services discovered", Toast.LENGTH_SHORT).show();
				getGattService(bluetoothLeService.getSupportedGattService());
			} else if (BLEService.ACTION_GATT_DISCONNECTED.equals(action)) {
				Toast.makeText(getApplicationContext(), "Gatt Disconnected", Toast.LENGTH_SHORT).show();
				handler.postDelayed(disconnectTimout, DISCONNECT_TIMEOUT);
			} else if (BLEService.ACTION_DATA_AVAILABLE.equals(action)) {
				Toast.makeText(getApplicationContext(), "Gatt received data", Toast.LENGTH_SHORT).show();
				Log.i(TAG, "Got data in activity");

				String data = new String(intent.getByteArrayExtra(BLEService.EXTRA_DATA));
				Log.i(TAG, "Data is " + data);

				String[] parts = data.split(":");
				if(parts.length > 1) {
					if("ZForce".equals(parts[0])) {
						String forceString = parts[1];
						try {
							int force = Integer.parseInt(forceString);
							weightPresent = force > 50;
							Log.i(TAG, "Weight present: " + weightPresent);
						} catch (NumberFormatException e) {
							Log.e(TAG, "Couldn't parse force value: " + forceString, e);
						}
					} else if("ZTemp".equals(parts[0])) {
						String tempString = parts[1];
						try {
							int temp = Integer.parseInt(tempString);
							Log.i(TAG, "Temp: " + temp);
							if (temp > 28) {
								Intent broadcastIntent = new Intent(EVENT_WEIGHT_SENSOR_BABY_OVERHEATING);
								sendBroadcast(broadcastIntent);
							}

						} catch (NumberFormatException e) {
							Log.e(TAG, "Couldn't parse temp value: " + tempString, e);
						}
					}
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
			bluetoothLeService.readCharacteristic(characteristicRx);
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
		if (!bindService(new Intent(this, BLEService.class), bleServiceConnection, BIND_AUTO_CREATE))
			Log.w(TAG, "Error starting BLEService from Weight sensor service");
		registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter());

		Notification n = new Notification.Builder(this)
				.setContentTitle("Weight sensor service")
				.setContentText("Running")
				.setSmallIcon(R.drawable.ic_launcher)
				.setAutoCancel(true).build();

		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.notify(0, n);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(gattUpdateReceiver);
	}

}
