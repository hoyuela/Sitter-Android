package com.solstice.sitterble;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.radiusnetworks.ibeacon.IBeaconConsumer;

public class IBeaconService extends Service implements IBeaconConsumer {

	@Override
	public void onIBeaconServiceConnect() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		// don't allow binding
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

}
