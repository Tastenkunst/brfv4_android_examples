package brfv4.examples.face_detection;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.util.Vector;

import brfv4.BRFManager;
import brfv4.android.DrawingUtils;
import brfv4.examples.BRFBasicJavaExample;
import brfv4.geom.Rectangle;

public class detect_larger_faces extends BRFBasicJavaExample {

	private Rectangle _faceDetectionRoi = new Rectangle();

	public detect_larger_faces(Context context) {
		super(context);
	}

	@Override
	public void initCurrentExample(BRFManager brfManager, Rectangle resolution) {

		Log.d("BRFv4", "BRFv4 - basic - face detection - detect large faces\n" +
				"Come closer to the webcam to see detection results.");

		brfManager.init(resolution, resolution, _appId);

		// We explicitly set the mode to run in: BRFMode.FACE_DETECTION.

		brfManager.setMode(brfv4.BRFMode.FACE_DETECTION);

		// Then we set the face detection region of interest to be
		// most/all of the overall analysed image (green rectangle, 100%).

		_faceDetectionRoi.setTo(
			resolution.width * 0.00, resolution.height * 0.00,
			resolution.width * 1.00, resolution.height * 1.00
		);
		brfManager.setFaceDetectionRoi(_faceDetectionRoi);

		// We can have either a landscape area (desktop), then choose height or
		// we can have a portrait area (mobile), then choose width as max face size.

		double maxFaceSize = _faceDetectionRoi.height;

		if(_faceDetectionRoi.width < _faceDetectionRoi.height) {
			maxFaceSize = _faceDetectionRoi.width;
		}

		// Merged faces (yellow) will only show up if they are at least 60% of maxFaceSize.
		// So: come really close the your webcam to see a detection result.

		// Default would be 30% of maxFaceSize as minimal value and 90% of maxFaceSize
		// as maximal value, but here we set larger desired sizes.

		brfManager.setFaceDetectionParams((int)(maxFaceSize * 0.60), (int)(maxFaceSize * 1.00), 12, 8);
	}

	@Override
	public void updateCurrentExample(BRFManager brfManager, Bitmap imageData, DrawingUtils draw) {

		brfManager.update(imageData);

		// Drawing the results:

		draw.clear();

		// Show the region of interest (green).

		draw.drawRect(_faceDetectionRoi,					false, 4.0, 0x8aff00, 0.5);

		// Then draw all detected faces (blue).

		draw.drawRects(brfManager.getAllDetectedFaces(),	false, 1.0, 0x00a1ff, 0.5);

		// In the end add the merged detected faces that have at least 12 detected faces
		// in a certain area (yellow).

		draw.drawRects(brfManager.getMergedDetectedFaces(),	false, 2.0, 0xffd200, 1.0);

		// Now print the face sizes:

		printSize(brfManager.getMergedDetectedFaces(), true);
	}

	public void printSize(Vector<Rectangle> rects, boolean printAlwaysMinMax) {

		double maxWidth = 0;
		double minWidth = 9999;

		for(int i = 0, l = rects.size(); i < l; i++) {

			if(rects.get(i).width < minWidth) {
				minWidth = rects.get(i).width;
			}

			if(rects.get(i).width > maxWidth) {
				maxWidth = rects.get(i).width;
			}
		}

		if(maxWidth > 0) {

			String str = "";

			// One face or same size: name it size, otherwise name it min/max.

			if(minWidth == maxWidth && !printAlwaysMinMax) {
				str = "size: " + maxWidth;
			} else {
				str = "min: " + minWidth + " max: " + maxWidth;
			}

			Log.d("BRFv4", str);
		}
	}
}
