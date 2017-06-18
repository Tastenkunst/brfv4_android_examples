package brfv4;

import android.graphics.Bitmap;

import java.util.Vector;

import brfv4.geom.Point;
import brfv4.geom.Rectangle;

public class BRFManager extends BRFv4ContextManager {

	public void init(Rectangle src, Rectangle imageRoi, String appId) {
		super.init(src, imageRoi, appId);
	}
	public void update(Bitmap imageData) {
		super.update(imageData);
	}

	public void reset() {
		super.reset();
	}
	public String getMode() {
		return super.getMode();
	}
	public void setMode(String mode) {
		super.setMode(mode);
	}

	//
	// Face Detection
	//

	public void setFaceDetectionParams(int minFaceSize, int maxFaceSize, int stepSize, int minMergeNeighbors) {
		super.setFaceDetectionParams(minFaceSize, maxFaceSize, stepSize, minMergeNeighbors);
	}
	public void setFaceDetectionRoi(Rectangle roi) {
		super.setFaceDetectionRoi(roi);
	}
	public Vector<Rectangle> getAllDetectedFaces() {
		return super.getAllDetectedFaces();
	}
	public Vector<Rectangle> getMergedDetectedFaces() {
		return super.getMergedDetectedFaces();
	}

	//
	// Face Tracking
	//

	public void setNumFacesToTrack(int numFaces) {
		super.setNumFacesToTrack(numFaces);
	}
	public void setFaceTrackingStartParams(double startMinFaceWidth, double startMaxFaceWidth, double startRotationX, double startRotationY, double startRotationZ) {
		super.setFaceTrackingStartParams(startMinFaceWidth, startMaxFaceWidth, startRotationX, startRotationY, startRotationZ);
	}
	public void setFaceTrackingResetParams(double resetMinFaceWidth, double resetMaxFaceWidth, double resetRotationX, double resetRotationY, double resetRotationZ) {
		super.setFaceTrackingResetParams(resetMinFaceWidth, resetMaxFaceWidth, resetRotationX, resetRotationY, resetRotationZ);
	}
	public Vector<BRFFace> getFaces() {
		return super.getFaces();
	}

	//
	// Optical Flow
	//

	public void setOpticalFlowParams(int patchSize, int numLevels, int numIterations, double error) {
		super.setOpticalFlowParams(patchSize, numLevels, numIterations, error);
	}
	public void addOpticalFlowPoints(Vector<Point> pointArray) {
		super.addOpticalFlowPoints(pointArray);
	}
	public Vector<Point> getOpticalFlowPoints() {
		return super.getOpticalFlowPoints();
	}
	public Vector<Boolean> getOpticalFlowPointStates() {
		return super.getOpticalFlowPointStates();
	}
	public boolean getOpticalFlowCheckPointsValidBeforeTracking() {
		return super.getOpticalFlowCheckPointsValidBeforeTracking();
	}
	public void setOpticalFlowCheckPointsValidBeforeTracking(boolean value) {
		super.setOpticalFlowCheckPointsValidBeforeTracking(value);
	}
}
