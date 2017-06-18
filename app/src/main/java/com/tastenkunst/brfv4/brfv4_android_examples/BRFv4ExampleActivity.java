package com.tastenkunst.brfv4.brfv4_android_examples;

import android.os.Bundle;
import android.util.Log;

import com.tastenkunst.brfv4.brfv4_android_examples.camera.CameraPermissionActivity;

public class BRFv4ExampleActivity extends CameraPermissionActivity {

	private static final String TAG = "BRFv4ExampleActivity";

	private BRFv4Fragment _brfFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_brfv4_example);

		try { getSupportActionBar().hide(); } catch(NullPointerException e) {}
	}

	@Override
	protected void onCameraAccess() {

		Log.d(TAG, "onCameraAccess");

		if (_brfFragment == null) {
			_brfFragment = new BRFv4Fragment();

			getFragmentManager().beginTransaction()
					.add(R.id._root, _brfFragment)
					.commit();
		}
	}

	@Override
	protected void onCameraAwaitingPermission() {
		Log.d(TAG, "onCameraAwaitingPermission");
	}
}