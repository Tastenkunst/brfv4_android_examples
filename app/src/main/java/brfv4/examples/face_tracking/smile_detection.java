package brfv4.examples.face_tracking;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.util.Vector;

import brfv4.BRFFace;
import brfv4.BRFManager;
import brfv4.android.DrawingUtils;
import brfv4.examples.BRFBasicJavaExample;
import brfv4.geom.Point;
import brfv4.geom.Rectangle;
import brfv4.utils.BRFv4PointUtils;

public class smile_detection extends BRFBasicJavaExample {

	public Point p0 = new Point();
	public Point p1 = new Point();

	public smile_detection(Context context) {
		super(context);
	}

	@Override
	public void initCurrentExample(BRFManager brfManager, Rectangle resolution) {

		Log.d("BRFv4", "BRFv4 - intermediate - face tracking - simple smile detection.\n" +
				"Detects how much someone is smiling.");

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

				// Smile Detection

				setPoint(face.vertices, 48, p0); // mouth corner left
				setPoint(face.vertices, 54, p1); // mouth corner right

				double mouthWidth = calcDistance(p0, p1);

				setPoint(face.vertices, 39, p1); // left eye inner corner
				setPoint(face.vertices, 42, p0); // right eye outer corner

				double eyeDist = calcDistance(p0, p1);
				double smileFactor = mouthWidth / eyeDist;

				smileFactor -= 1.40; // 1.40 - neutral, 1.70 smiling

				if(smileFactor > 0.25) smileFactor = 0.25;
				if(smileFactor < 0.00) smileFactor = 0.00;

				smileFactor *= 4.0;

				if(smileFactor < 0.0) { smileFactor = 0.0; }
				if(smileFactor > 1.0) { smileFactor = 1.0; }

				// Let the color show you how much you are smiling.

				int color =
						(((int)( 0xff * (1.0 - smileFactor)) & 0xff) << 16) +
						((((int)(0xff * smileFactor) & 0xff) << 8));

				// Face Tracking results: 68 facial feature points.

				draw.drawTriangles(	face.vertices, face.triangles, false, 1.0, color, 0.4);
				draw.drawVertices(	face.vertices, 2.0, false, color, 0.4);

				Log.d("BRFv4", "smile factor: " + (smileFactor * 100) + "%");
			}
		}
	}

	private void setPoint(float[] v, int i, Point p) {
		BRFv4PointUtils.setPoint(v, i, p);
	}

	private double calcDistance(Point p0, Point p1) {
		return BRFv4PointUtils.calcDistance(p0, p1);
	}
}
