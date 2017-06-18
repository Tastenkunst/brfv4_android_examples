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

public class color_libs extends BRFBasicJavaExample {


	public color_libs(Context context) {
		super(context);
	}

	@Override
	public void initCurrentExample(BRFManager brfManager, Rectangle resolution) {

		Log.d("BRFv4", "BRFv4 - intermediate - face tracking - color libs.\n" +
				"Draws triangles with a certain fill color.");

		brfManager.init(resolution, resolution, _appId);
	}

	@Override
	public void updateCurrentExample(BRFManager brfManager, Bitmap imageData, DrawingUtils draw) {

		brfManager.update(imageData);

		draw.clear();

		// Face detection results: a rough rectangle used to start the face tracking.

		draw.drawRects(brfManager.getAllDetectedFaces(),	false, 1.0, 0x00a1ff, 0.5);
		draw.drawRects(brfManager.getMergedDetectedFaces(),	false, 2.0, 0xffd200, 1.0);

		Vector<BRFFace> faces = brfManager.getFaces();

		for(int i = 0; i < faces.size(); i++) {

			BRFFace face = faces.get(i);

			if(		face.state.equals(brfv4.BRFState.FACE_TRACKING_START) ||
					face.state.equals(brfv4.BRFState.FACE_TRACKING)) {

				// Face tracking results: 68 facial feature points.

				draw.drawTriangles(	face.vertices, face.triangles, false, 1.0, 0x00a0ff, 0.4);
				draw.drawVertices(	face.vertices, 2.0, false, 0x00a0ff, 0.4);

				// Now just draw all the triangles of the mouth in a certain color.

				draw.fillTriangles(	face.vertices, libTriangles, false, 0xff7900, 0.8);
			}
		}
	}

	private int[] libTriangles	= {
			48, 49, 60,
			48, 59, 60,
			49, 50, 61,
			49, 60, 61,
			50, 51, 62,
			50, 61, 62,
			51, 52, 62,
			52, 53, 63,
			52, 62, 63,
			53, 54, 64,
			53, 63, 64,
			54, 55, 64,
			55, 56, 65,
			55, 64, 65,
			56, 57, 66,
			56, 65, 66,
			57, 58, 66,
			58, 59, 67,
			58, 66, 67,
			59, 60, 67
			//,					// mouth whole
			// 60, 61, 67,
			// 61, 62, 66,
			// 61, 66, 67,
			// 62, 63, 66,
			// 63, 64, 65,
			// 63, 65, 66
	};
}
