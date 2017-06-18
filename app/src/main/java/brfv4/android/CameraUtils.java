package brfv4.android;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;

import java.io.IOException;
import java.util.List;

public class CameraUtils {

	public Camera camera;

	public boolean init() {

		// Find the front-facing camera
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();

		for (int i = 0; i < Camera.getNumberOfCameras(); i++) {

			Camera.getCameraInfo(i, cameraInfo);

			if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {

				camera = Camera.open(i);

				Camera.Parameters parameters = camera.getParameters();
				List<int[]> previewFpsRanges = parameters.getSupportedPreviewFpsRange();
				parameters.setPreviewFpsRange(previewFpsRanges.get(0)[0],previewFpsRanges.get(0)[1]);

				camera.setParameters(parameters);
				camera.setDisplayOrientation(360 - cameraInfo.orientation);

				return true;
			}
		}

		return false;
	}

	public void startStream(SurfaceTexture surfaceTexture) {

		try {

			camera.setPreviewTexture(surfaceTexture);
			camera.startPreview();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void stopStream() {

		if (camera != null) {
			camera.stopPreview();
			camera.release();
			camera = null;
		}
	}
}
