package com.sitter.sittermockbaby;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
	
	private Handler uiHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button b = (Button) findViewById(R.id.startRangingButton);
		b.setOnClickListener(this);
		b = (Button) findViewById(R.id.stopRangingButton);
		b.setOnClickListener(this);
		
		uiHandler = new Handler(getMainLooper()) {

			@Override
			public void handleMessage(Message msg) {
				Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_LONG).show();
			}
			
		};
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.startRangingButton) {
			startService(new Intent(this, IBeaconService.class));
		} else {
			stopService(new Intent(this, IBeaconService.class));
		}
	}
}
