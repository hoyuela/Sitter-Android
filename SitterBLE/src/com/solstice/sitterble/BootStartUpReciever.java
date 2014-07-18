package com.solstice.sitterble;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootStartUpReciever extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// Start Service On Boot Start Up
		Intent service = new Intent(context, WeightSensorService.class);
		context.startService(service);

		// // Start App On Boot Start Up
		// Intent App = new Intent(context, MainActivity.class);
		// App.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// context.startActivity(App);

    }
}