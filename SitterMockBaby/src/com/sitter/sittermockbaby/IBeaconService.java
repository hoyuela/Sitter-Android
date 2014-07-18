package com.sitter.sittermockbaby;

import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Toast;

import com.radiusnetworks.ibeacon.IBeacon;
import com.radiusnetworks.ibeacon.IBeaconConsumer;
import com.radiusnetworks.ibeacon.IBeaconManager;
import com.radiusnetworks.ibeacon.RangeNotifier;
import com.radiusnetworks.ibeacon.Region;

public class IBeaconService extends Service implements IBeaconConsumer {

	private static final String ESTIMOTE_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
	private static final String UNIQUE_ID = "myUniqueId";
	private static final String BLUETOOTH_UUID = "2b229de0-0e7c-11e4-9191-0800200c9a66";
	private static final String BLUETOOTH_DEVICE_MAC_ADDRESS = "BC:F5:AC:46:F2:B9";
	
	public static final String EVENT_LOG = "EVENT_LOG";
	public static final String MESSAGE = "MESSAGE";
	
	private IBeaconManager beaconMgr;
	private Region region = new Region(UNIQUE_ID, ESTIMOTE_UUID, 99, 13928);;
	
	private BluetoothSocket socket;
	private BluetoothDevice device;
	
	private boolean ranging = false;

	public class LocalBinder extends Binder {
		public IBeaconService getService() {
			return IBeaconService.this;
		}
	}
	private final IBinder binder = new LocalBinder();
	
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public void onIBeaconServiceConnect() {
		sendMessageIntent("beacon init");
		setupRangeNotifier();
		startRanging();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		beaconMgr = IBeaconManager.getInstanceForApplication(getApplicationContext());
		
		beaconMgr.bind(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		beaconMgr.unBind(this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
		
		// If we get killed, after returning from here, restart
		return START_STICKY;
	}
	
	private void setupRangeNotifier() {
		ranging = true;
		
		beaconMgr.setRangeNotifier(new RangeNotifier() {

			@Override
			public void didRangeBeaconsInRegion(Collection<IBeacon> beaconsInRange, Region region) {
				if (beaconsInRange.size() < 1)
					return;
				IBeacon beacon = beaconsInRange.iterator().next();
				if (beacon.getAccuracy() < 3.0 && ranging) {
					sendMessageIntent("Found beacon");
					ranging = false;
					
					notifyBluetoothServerSockets();
					
					try {
						beaconMgr.stopRangingBeaconsInRegion(region);
					} catch (RemoteException e) {
						sendMessageIntent("Error stop ranging");
					}
				}
			}
		});
	}
	
	private void startRanging() {
		try {
			beaconMgr.startRangingBeaconsInRegion(region);
		} catch (RemoteException e) {
			sendMessageIntent("Error start ranging");
		}
	}
	
	private void notifyBluetoothServerSockets() {
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		adapter.cancelDiscovery();
		
		device = adapter.getRemoteDevice(BLUETOOTH_DEVICE_MAC_ADDRESS);
		
		while (true) {
			try {
				socket = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString(BLUETOOTH_UUID));
				socket.connect();
			} catch (IOException e) {
				sendMessageIntent("Cannot connect to bluetooth");
				continue;
			}
			sendMessageIntent("Bluetooth connection successful");
			break;
		}
		
	}
	
	private void sendMessageIntent(String message) {
		Intent broadcastIntent = new Intent(EVENT_LOG);
		broadcastIntent.putExtra(MESSAGE, message);
		sendBroadcast(broadcastIntent);
	}
}
