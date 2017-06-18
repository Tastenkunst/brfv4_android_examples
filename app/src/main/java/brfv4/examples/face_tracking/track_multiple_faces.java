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

public class track_multiple_faces extends BRFBasicJavaExample {

	public int numFacesToTrack = 2;		// Set the number of faces to detect and track.

	public track_multiple_faces(Context context) {
		super(context);
	}

	@Override
	public void initCurrentExample(BRFManager brfManager, Rectangle resolution) {

		Log.d("BRFv4", "BRFv4 - basic - face tracking - track multiple faces\n" +
			"Detect and track " + numFacesToTrack + " faces and draw their 68 facial landmarks.");

		// By default everything necessary for a single face tracking app
		// is set up for you in brfManager.init.

		brfManager.init(resolution, resolution, _appId);

		// But here we tell BRFv4 to track multiple faces. In this case two.

		// While the first face is getting tracked the face detection
		// is performed in parallel and is looking for a second face.

		brfManager.setNumFacesToTrack(numFacesToTrack);

		// Relax starting conditions to eventually find more faces.

		double maxFaceSize = resolution.height;

		if(resolution.width < resolution.height) {
			maxFaceSize = resolution.width;
		}

		brfManager.setFaceDetectionParams(		(int)(maxFaceSize * 0.30), (int)(maxFaceSize * 1.00), 24, 5);
		brfManager.setFaceTrackingStartParams(	(int)(maxFaceSize * 0.30), (int)(maxFaceSize * 1.00), 32, 35, 32);
		brfManager.setFaceTrackingResetParams(	(int)(maxFaceSize * 0.25), (int)(maxFaceSize * 1.00), 40, 55, 32);
	}

	@Override
	public void updateCurrentExample(BRFManager brfManager, Bitmap imageData, DrawingUtils draw) {

		brfManager.update(imageData);

		// Drawing the results:

		draw.clear();

		// Get all faces. We get numFacesToTrack faces in that array.

		draw.drawRects(brfManager.getAllDetectedFaces(),	false, 1.0, 0x00a1ff, 0.5);
		draw.drawRects(brfManager.getMergedDetectedFaces(), false, 2.0, 0xffd200, 1.0);

		// Get all faces. The default setup only tracks one face.

		Vector<BRFFace> faces = brfManager.getFaces();

		for(int i = 0; i < faces.size(); i++) {

			BRFFace face = faces.get(i);

			// Every face has it's own states.
			// While the first face might already be tracking,
			// the second face might just try to detect a face.

			if(face.state.equals(brfv4.BRFState.FACE_DETECTION)) {

				// Face detection results: a rough rectangle used to start the face tracking.

				draw.drawRects(brfManager.getMergedDetectedFaces(), false, 2.0, 0xffd200, 1.0);

			} else if(	face.state.equals(brfv4.BRFState.FACE_TRACKING_START) ||
						face.state.equals(brfv4.BRFState.FACE_TRACKING)) {

				// Face tracking results: 68 facial feature points.

				draw.drawTriangles(	face.vertices, face.triangles, false, 1.0, 0x00a0ff, 0.4);
				draw.drawVertices(	face.vertices, 2.0, false, 0x00a0ff, 0.4);
			}
		}
	}
}
