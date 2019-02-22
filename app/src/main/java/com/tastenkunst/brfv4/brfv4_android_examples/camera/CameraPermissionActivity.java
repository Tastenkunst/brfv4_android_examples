package com.tastenkunst.brfv4.brfv4_android_examples.camera;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.tastenkunst.brfv4.brfv4_android_examples.R;

public class CameraPermissionActivity extends AppCompatActivity {

	private static final String TAG						 = "CameraPermissionActivity";
	private static final int	REQUEST_CAMERA_PERMISSION   = 1;
	private static final String FRAGMENT_DIALOG			 = "dialog";

	protected void onCameraAccess() {}
	protected void onCameraAwaitingPermission() {}

	@Override
	protected void onResume() {
		super.onResume();

		if(checkCameraPermission()) {

			onCameraAccess();

		} else {

			onCameraAwaitingPermission();
		}
	}

	protected boolean checkCameraPermission() {

		boolean granted = (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);

		if(granted) {

			return true;

		} else {

			if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {

				buildPermissionDialog();

			} else {

				ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
			}
		}

		return false;
	}

	// To be able to ask for camera permission

	protected void buildPermissionDialog() {

		ConfirmationDialogFragment
				.newInstance(R.string.camera_permission_confirmation,
						new String[]{Manifest.permission.CAMERA},
						REQUEST_CAMERA_PERMISSION,
						R.string.camera_permission_not_granted)
				.show(getSupportFragmentManager(), FRAGMENT_DIALOG);
	}

	public static class ConfirmationDialogFragment extends DialogFragment {

		private static final String ARG_MESSAGE = "message";
		private static final String ARG_PERMISSIONS = "permissions";
		private static final String ARG_REQUEST_CODE = "request_code";
		private static final String ARG_NOT_GRANTED_MESSAGE = "not_granted_message";

		public static ConfirmationDialogFragment newInstance(@StringRes int message,
															 String[] permissions, int requestCode, @StringRes int notGrantedMessage) {
			ConfirmationDialogFragment fragment = new ConfirmationDialogFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_MESSAGE, message);
			args.putStringArray(ARG_PERMISSIONS, permissions);
			args.putInt(ARG_REQUEST_CODE, requestCode);
			args.putInt(ARG_NOT_GRANTED_MESSAGE, notGrantedMessage);
			fragment.setArguments(args);
			return fragment;
		}

		@NonNull
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final Bundle args = getArguments();
			return new AlertDialog.Builder(getActivity())
					.setMessage(args.getInt(ARG_MESSAGE))
					.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									String[] permissions = args.getStringArray(ARG_PERMISSIONS);
									if (permissions == null) {
										throw new IllegalArgumentException();
									}
									ActivityCompat.requestPermissions(getActivity(),
											permissions, args.getInt(ARG_REQUEST_CODE));
								}
							})
					.setNegativeButton(android.R.string.cancel,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									Toast.makeText(getActivity(),
											args.getInt(ARG_NOT_GRANTED_MESSAGE),
											Toast.LENGTH_SHORT).show();
								}
							})
					.create();
		}
	}
}
