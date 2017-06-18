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

public class blink_detection extends BRFBasicJavaExample {

	float[] _oldFaceShapeVertices = null;
	boolean _blinked = false;

	public blink_detection(Context context) {
		super(context);
	}

	@Override
	public void initCurrentExample(BRFManager brfManager, Rectangle resolution) {

		Log.d("BRFv4", "BRFv4 - advanced - face tracking - simple blink detection.\n" +
				"Detects a blink of the eyes.");

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

			if(		face.state.equals(brfv4.BRFState.FACE_TRACKING)) {

				// simple blink detection

				// A simple approach with quite a lot false positives. Fast movement can't be
				// handled properly. This code is quite good when it comes to
				// starring contest apps though.

				// It basically compares the old positions of the eye points to the current ones.
				// If rapid movement of the current points was detected it's considered a blink.

				float[] v = face.vertices;

				if(_oldFaceShapeVertices == null) storeFaceShapeVertices(v);

				int k, l;
				double yLE, yRE;

				// Left eye movement (y)

				for(k = 36, l = 41, yLE = 0; k <= l; k++) {
					yLE += v[k * 2 + 1] - _oldFaceShapeVertices[k * 2 + 1];
				}
				yLE /= 6;

				// Right eye movement (y)

				for(k = 42, l = 47, yRE = 0; k <= l; k++) {
					yRE += v[k * 2 + 1] - _oldFaceShapeVertices[k * 2 + 1];
				}

				yRE /= 6;

				double yN = 0;

				// Compare to overall movement (nose y)

				yN += v[27 * 2 + 1] - _oldFaceShapeVertices[27 * 2 + 1];
				yN += v[28 * 2 + 1] - _oldFaceShapeVertices[28 * 2 + 1];
				yN += v[29 * 2 + 1] - _oldFaceShapeVertices[29 * 2 + 1];
				yN += v[30 * 2 + 1] - _oldFaceShapeVertices[30 * 2 + 1];
				yN /= 4;

				double blinkRatio = Math.abs((yLE + yRE) / yN);

				if((blinkRatio > 12 && (yLE > 0.4 || yRE > 0.4))) {
					Log.d("BRFv4", "blink " + blinkRatio + " " + yLE + " " + yRE + " " + yN);
					blink();
				}

				// Let the color of the shape show whether you blinked.

				int color = 0x00a0ff;

				if(_blinked) {
					color = 0xffd200;
				}

				// Face Tracking results: 68 facial feature points.

				draw.drawTriangles(	face.vertices, face.triangles, false, 1.0, color, 0.4);
				draw.drawVertices(	face.vertices, 2.0, false, color, 0.4);

				Log.d("BRFv4", "blink: " + (_blinked ? "Yes" : "No"));

				storeFaceShapeVertices(v);
			}
		}
	}

	private void blink() {

		_blinked = true;

		new android.os.Handler().postDelayed(
			new Runnable() {
				public void run() {
					resetBlink();
				}
			}, 150
		);
	}

	private void resetBlink() {
		_blinked = false;
	}

	private void storeFaceShapeVertices(float[] vertices) {

		if(_oldFaceShapeVertices == null) {
			_oldFaceShapeVertices = new float[vertices.length];
		}

		for(int i = 0, l = vertices.length; i < l; i++) {
			_oldFaceShapeVertices[i] = vertices[i];
		}
	}
}
