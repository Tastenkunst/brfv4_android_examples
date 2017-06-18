package brfv4.examples.face_tracking;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.util.Vector;

import brfv4.BRFFace;
import brfv4.BRFManager;
import brfv4.android.DrawingUtils;
import brfv4.examples.BRFBasicJavaExample;
import brfv4.geom.Rectangle;
import brfv4.utils.BRFv4PointUtils;

public class restrict_to_center extends BRFBasicJavaExample {

	private Rectangle _faceDetectionRoi = new Rectangle();

	public restrict_to_center(Context context) {
		super(context);
	}

	@Override
	public void initCurrentExample(BRFManager brfManager, Rectangle resolution) {

		Log.d("BRFv4", "BRFv4 - basic - face tracking - restrict to frontal and center\n" +
			"Only track a face if it is in a certain distance to the camera and is frontal.");

		brfManager.init(resolution, resolution, _appId);

		// Sometimes you want to restrict the position and pose of a face.

		// In this setup we will restrict pick up of the face to the center of the image
		// and we will let BRFv4 reset if the user turns his head too much.

		// We limit the face detection region of interest to be in the central
		// part of the overall analysed image (green rectangle).

		_faceDetectionRoi.setTo(
			resolution.width * 0.10, resolution.height * 0.20,
			resolution.width * 0.80, resolution.height * 0.60
		);
		brfManager.setFaceDetectionRoi(_faceDetectionRoi);

		// We can have either a landscape area (desktop), then choose height or
		// we can have a portrait area (mobile), then choose width as max face size.

		double maxFaceSize = _faceDetectionRoi.height;

		if(_faceDetectionRoi.width < _faceDetectionRoi.height) {
			maxFaceSize = _faceDetectionRoi.width;
		}

		// Use the usual detection distances to be able to tell the user what to do.

		brfManager.setFaceDetectionParams((int)(maxFaceSize * 0.30), (int)(maxFaceSize * 1.00), 12, 8);

		// Set up the pickup parameters for the face tracking:
		// startMinFaceSize, startMaxFaceSize, startRotationX/Y/Z

		// Faces will only get picked up, if they look straight into the camera
		// and have a certain size (distance to camera).

		brfManager.setFaceTrackingStartParams((int)(maxFaceSize * 0.50), (int)(maxFaceSize * 0.70), 15, 15, 15);

		// Set up the reset conditions for the face tracking:
		// resetMinFaceSize, resetMaxFaceSize, resetRotationX/Y/Z

		// Face tracking will reset to face detection, if the face turns too much or leaves
		// the desired distance to the camera.

		brfManager.setFaceTrackingResetParams((int)(maxFaceSize * 0.45), (int)(maxFaceSize * 0.75), 25, 25, 25);
	}

	@Override
	public void updateCurrentExample(BRFManager brfManager, Bitmap imageData, DrawingUtils draw) {

		brfManager.update(imageData);

		draw.clear();

		draw.drawRect(_faceDetectionRoi,					false, 2.0, 0x8aff00, 0.5);
		draw.drawRects(brfManager.getAllDetectedFaces(),	false, 1.0, 0x00a1ff, 0.5);

		Vector<Rectangle> mergedFaces = brfManager.getMergedDetectedFaces();

		draw.drawRects(mergedFaces,							false, 2.0, 0xffd200, 1.0);

		Vector<BRFFace> faces = brfManager.getFaces();
		boolean oneFaceTracked = false;

		for(int i = 0; i < faces.size(); i++) {

			BRFFace face = faces.get(i);

			if(face.state.equals(brfv4.BRFState.FACE_TRACKING)) {

				// Read the rotation of the face and draw it
				// green if the face is frontal or
				// red if the user turns the head too much.

				double maxRot = BRFv4PointUtils.toDegree(
					BRFv4PointUtils.max(
						Math.abs(face.rotationX),
						Math.abs(face.rotationY),
						Math.abs(face.rotationZ)
					)
				);

				double percent = maxRot / 20.0;

				if(percent < 0.0) { percent = 0.0; }
				if(percent > 1.0) { percent = 1.0; }

				int color =
					(((int)( 0xff * percent) & 0xff) << 16) +
					((((int)(0xff * (1.0 - percent)) & 0xff) << 8));

				draw.drawTriangles(	face.vertices, face.triangles, false, 1.0, color, 0.4);
				draw.drawVertices(	face.vertices, 2.0, false, color, 0.4);

				oneFaceTracked = true;
			}
		}

		// Check, if the face is too close or too far way and tell the user what to do.

		if(!oneFaceTracked && mergedFaces.size() > 0) {

			Rectangle mergedFace = mergedFaces.get(0);

			if(mergedFace.width < _faceDetectionRoi.width * 0.50) { // startMinFaceSize

				Log.d("BRFv4", "Come closer.");

			} else if(mergedFace.width > _faceDetectionRoi.width * 0.70) { // startMaxFaceSize

				Log.d("BRFv4", "Move further away.");
			}
		}
	}
}
