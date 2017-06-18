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
import brfv4.utils.BRFv4ExtendedFace;

public class extended_face_shape extends BRFBasicJavaExample {

	public BRFv4ExtendedFace _extendedShape = new BRFv4ExtendedFace();

	public extended_face_shape(Context context) {
		super(context);
	}

	@Override
	public void initCurrentExample(BRFManager brfManager, Rectangle resolution) {

		Log.d("BRFv4", "BRFv4 - basic - face tracking - extended face shape\n" +
			"There are 6 more landmarks for the forehead calculated from the 68 landmarks.");

		brfManager.init(resolution, resolution, _appId);
	}

	@Override
	public void updateCurrentExample(BRFManager brfManager, Bitmap imageData, DrawingUtils draw) {

		brfManager.update(imageData);

		draw.clear();

		// Face detection results: a rough rectangle used to start the face tracking.

		draw.drawRects(brfManager.getAllDetectedFaces(),	false, 1.0, 0x00a1ff, 0.5);
		draw.drawRects(brfManager.getMergedDetectedFaces(),	false, 2.0, 0xffd200, 1.0);

		// Get all faces. The default setup only tracks one face.

		Vector<BRFFace> faces = brfManager.getFaces();

		for(int i = 0; i < faces.size(); i++) {

			BRFFace face = faces.get(i);

			if(		face.state.equals(brfv4.BRFState.FACE_TRACKING_START) ||
					face.state.equals(brfv4.BRFState.FACE_TRACKING)) {

				// The extended face shape is calculated from the usual 68 facial features.
				// The additional landmarks are just estimated, they are not actually tracked.

				_extendedShape.update(face);

				// Then we draw all 74 landmarks of the _extendedShape.

				draw.drawTriangles(	_extendedShape.vertices, _extendedShape.triangles,
						false, 1.0, 0x00a0ff, 0.4);
				draw.drawVertices(	_extendedShape.vertices, 2.0, false, 0x00a0ff, 0.4);
			}
		}
	}
}
