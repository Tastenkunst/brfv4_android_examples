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

public class candide_overlay extends BRFBasicJavaExample {

	public candide_overlay(Context context) {
		super(context);
	}

	@Override
	public void initCurrentExample(BRFManager brfManager, Rectangle resolution) {

		Log.d("BRFv4", "BRFv4 - basic - face tracking - candide shape overlay\n" +
			"The Candide 3 model is calculated from the 68 landmarks.");

		// By default everything necessary for a single face tracking app
		// is set up for you in brfManager.init. There is actually no
		// need to configure much more for a jump start.

		brfManager.init(resolution, resolution, _appId);
	}

	@Override
	public void updateCurrentExample(BRFManager brfManager, Bitmap imageData, DrawingUtils draw) {

		// In a webcam example imageData is the mirrored webcam video feed.
		// In an image example imageData is the (not mirrored) image content.

		brfManager.update(imageData);

		// Drawing the results:

		draw.clear();

		// Face detection results: a rough rectangle used to start the face tracking.

		draw.drawRects(brfManager.getAllDetectedFaces(),	false, 1.0, 0x00a1ff, 0.5);
		draw.drawRects(brfManager.getMergedDetectedFaces(), false, 2.0, 0xffd200, 1.0);

		// Get all faces. The default setup only tracks one face.

		Vector<BRFFace> faces = brfManager.getFaces();

		for(int i = 0; i < faces.size(); i++) {

			BRFFace face = faces.get(i);

			if(	face.state.equals(brfv4.BRFState.FACE_TRACKING)) {

				// Instead of drawing the 68 landmarks this time we draw the Candide3 model shape (yellow).

				draw.drawTriangles(	face.candideVertices, face.candideTriangles, false, 1.0, 0xffd200, 0.4);
				draw.drawVertices(	face.candideVertices, 2.0, false, 0xffd200, 0.4);

				// And for a reference also draw the 68 landmarks (blue).

				draw.drawVertices(	face.vertices, 2.0, false, 0x00a1ff, 0.4);
			}
		}
	}
}
