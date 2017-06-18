package brfv4;

import android.graphics.Bitmap;

import java.util.Vector;

import brfv4.geom.Point;
import brfv4.geom.Rectangle;

class BRFv4Context {

	// load our native library
	static {
		System.loadLibrary("BRFv4");
	}

	static native void		init(int srcWidth, int srcHeight, int imageRoiX, int imageRoiY, int imageRoiWidth, int imageRoiHeight, String appId);
	static native void		update(Bitmap imageData);
	static native void		reset();

	static native String	get_mode();
	static native void		set_mode(String mode);

	//
	// Face Detection
	//

	static native void		set_faceDetectionParams(int minFaceSize, int maxFaceSize, int stepSize, int minMergeNeighbors);
	static native void		set_faceDetectionRoi(int fdRoiX, int fdRoiY, int fdRoiWidth, int fdRoiHeight);
	static native int		get_allDetectedFaces_length();
	static native int		get_allDetectedFaces(float[] arr);
	static native int		get_mergedDetectedFaces_length();
	static native int		get_mergedDetectedFaces(float[] arr);

	//
	// Face Tracking
	//

	static native void		set_numFacesToTrack(int numFaces);
	static native void		set_faceTrackingStartParams(float startMinFaceWidth, float startMaxFaceWidth, float startRotationX, float startRotationY, float startRotationZ);
	static native void		set_faceTrackingResetParams(float resetMinFaceWidth, float resetMaxFaceWidth, float resetRotationX, float resetRotationY, float resetRotationZ);

	static native int		get_faces_length();

	static native String	get_face_lastState(int fi);
	static native String	get_face_state(int fi);
	static native String	get_face_nextState(int fi);

	static native int		get_face_vertices_length(int fi);
	static native int		get_face_vertices(int fi, float[] arr);
	static native int		get_face_triangles_length(int fi);
	static native int		get_face_triangles(int fi, int[] arr);
	static native int		get_face_points_length(int fi);
	static native int		get_face_points(int fi, float[] arr);

	static native int		get_face_bounds(int fi, float[] arr);
	static native int		get_face_refRect(int fi, float[] arr);

	static native int		get_face_candideVertices_length(int fi);
	static native int		get_face_candideVertices(int fi, float[] arr);
	static native int		get_face_candideTriangles_length(int fi);
	static native int		get_face_candideTriangles(int fi, int[] arr);

	static native float		get_face_scale(int fi);
	static native float		get_face_translationX(int fi);
	static native float		get_face_translationY(int fi);
	static native float		get_face_rotationX(int fi);
	static native float		get_face_rotationY(int fi);
	static native float		get_face_rotationZ(int fi);

	//
	// OpticalFlow
	//

	static native void		set_opticalFlowParams(int patchSize, int numLevels, int numIterations, float error);
	static native void		add_opticalFlowPoints(float[] arr, int length);

	static native int		get_opticalFlowPoints_length();
	static native int		get_opticalFlowPoints(float[] arr);
	static native int		get_opticalFlowPointStates_length();
	static native int		get_opticalFlowPointStates(int[] arr);

	static native int		get_opticalFlowCheckPointsValidBeforeTracking();
	static native void		set_opticalFlowCheckPointsValidBeforeTracking(int value);

	static void updateWithBitmap(Bitmap imageData) {
		update(imageData);
	}

	static void getOpticalFlowPoints(Vector<Point> pointsToTrack) {

		int i = pointsToTrack.size();
		int l = get_opticalFlowPoints_length();

		for(; i < l; i++) { pointsToTrack.add(new Point()); }
		for(; i > l; i--) { pointsToTrack.remove(i - 1); }

		float[] arr = new float[l * 2];

		get_opticalFlowPoints(arr);

		int arrI = 0;

		for(i = 0; i < l; i++) {

			Point p = pointsToTrack.get(i);

			p.x = arr[arrI++];
			p.y = arr[arrI++];
		}
	}

	static void getOpticalFlowPointStates(Vector<Boolean> pointStates) {

		int i = pointStates.size();
		int l = get_opticalFlowPointStates_length();

		for(; i < l; i++) { pointStates.add(Boolean.FALSE); }
		for(; i > l; i--) { pointStates.remove(i - 1); }

		int[] arr = new int[l];

		get_opticalFlowPointStates(arr);

		int arrI = 0;

		for(i = 0; i < l; i++) {
			Boolean s = arr[arrI++] == 1 ? Boolean.TRUE : Boolean.FALSE;
			pointStates.set(i, s);
		}
	}

	static void getAllDetectedFaces(Vector<Rectangle> allDetectedFaces) {

		int i = allDetectedFaces.size();
		int l = get_allDetectedFaces_length();

		for(; i < l; i++) { allDetectedFaces.add(new Rectangle()); }
		for(; i > l; i--) { allDetectedFaces.remove(i - 1); }

		float[] arr = new float[l * 4];

		get_allDetectedFaces(arr);

		int arrI = 0;

		for(i = 0; i < l; i++) {

			Rectangle r = allDetectedFaces.get(i);

			r.x = arr[arrI++];
			r.y = arr[arrI++];
			r.width = arr[arrI++];
			r.height = arr[arrI++];
		}
	}

	static void getMergedDetectedFaces(Vector<Rectangle> mergedDetectedFaces) {

		int i = mergedDetectedFaces.size();
		int l = get_mergedDetectedFaces_length();

		for(; i < l; i++) { mergedDetectedFaces.add(new Rectangle()); }
		for(; i > l; i--) { mergedDetectedFaces.remove(i - 1); }

		float[] arr = new float[l * 4];

		get_mergedDetectedFaces(arr);

		int arrI = 0;

		for(i = 0; i < l; i++) {

			Rectangle r = mergedDetectedFaces.get(i);

			r.x = arr[arrI++];
			r.y = arr[arrI++];
			r.width = arr[arrI++];
			r.height = arr[arrI++];
		}
	}

	static void getFaces(Vector<BRFFace> faces) {

		int fi = faces.size();
		int fl = get_faces_length();

		for(; fi < fl; fi++) { faces.add(new BRFFace()); }
		for(; fi > fl; fi--) { faces.remove(fi - 1); }

		for(fi = 0; fi < fl; fi++) {

			BRFFace face = faces.get(fi);

			face.lastState	= get_face_lastState(fi);
			face.state		= get_face_state(fi);
			face.nextState  = get_face_nextState(fi);

			int l = get_face_vertices_length(fi);

			if(l > 0) {

				if(face.vertices == null) {
					face.vertices = new float[l];
				}

				// vertices length is larger than 4, so use it for the rectangles as well.
				get_face_bounds(fi, face.vertices);

				int arrI = 0;

				face.bounds.x	   = (double)face.vertices[arrI++];
				face.bounds.y	   = (double)face.vertices[arrI++];
				face.bounds.width   = (double)face.vertices[arrI++];
				face.bounds.height  = (double)face.vertices[arrI];

				get_face_refRect(fi, face.vertices);

				arrI = 0;

				face.refRect.x	  = (double)face.vertices[arrI++];
				face.refRect.y	  = (double)face.vertices[arrI++];
				face.refRect.width  = (double)face.vertices[arrI++];
				face.refRect.height = (double)face.vertices[arrI];

				get_face_vertices(fi, face.vertices);

				int i = face.points.size();

				l /= 2;

				for(; i < l; i++) { face.points.add(new Point()); }
				for(; i > l; i--) { face.points.remove(i - 1); }

				for(i = 0, arrI = 0; i < l; i++) {

					Point p = face.points.get(i);
					p.x	 = face.vertices[arrI++];
					p.y	 = face.vertices[arrI++];
				}
			}

			if(face.triangles == null) {

				l = get_face_triangles_length(fi);

				if(l > 0) {

					if(face.triangles == null) {
						face.triangles = new int[l];
					}

					get_face_triangles(fi, face.triangles);
				}
			}

			l = get_face_candideVertices_length(fi);

			if(l > 0) {

				if(face.candideVertices == null) {
					face.candideVertices = new float[l];
				}

				get_face_candideVertices(fi, face.candideVertices);
			}

			if(face.candideTriangles == null) {

				l = get_face_candideTriangles_length(fi);

				if(l > 0) {

					if(face.candideTriangles == null) {
						face.candideTriangles = new int[l];
					}

					get_face_candideTriangles(fi, face.candideTriangles);
				}
			}

			face.scale		  = get_face_scale(fi);
			face.translationX   = get_face_translationX(fi);
			face.translationY   = get_face_translationY(fi);
			face.rotationX	  = get_face_rotationX(fi);
			face.rotationY	  = get_face_rotationY(fi);
			face.rotationZ	  = get_face_rotationZ(fi);
		}
	}

	static void addOpticalFlowPoints(Vector<Point> pointArray) {

		int i = 0;
		int l = pointArray.size();

		float[] arr = new float[l * 2];

		int arrI = 0;

		for(; i < l; i++) {

			Point p = pointArray.get(i);

			arr[arrI++] = (float)p.x;
			arr[arrI++] = (float)p.y;
		}

		add_opticalFlowPoints(arr, l * 2);
	}
}

class BRFv4ContextManager {

	private Vector<Rectangle> _allDetectedFaces;
	private Vector<Rectangle> _mergedDetectedFaces;
	private Vector<BRFFace> _faces;

	private Vector<Point> _pointsToTrack;
	private Vector<Boolean> _pointStates;

	BRFv4ContextManager() {

		_allDetectedFaces		= new Vector<Rectangle>();
		_mergedDetectedFaces	= new Vector<Rectangle>();
		_faces					= new Vector<BRFFace>();

		_pointsToTrack			= new Vector<Point>();
		_pointStates			= new Vector<Boolean>();
	}

	private void fetchResults() {

		BRFv4Context.getOpticalFlowPoints(_pointsToTrack);
		BRFv4Context.getOpticalFlowPointStates(_pointStates);

		BRFv4Context.getAllDetectedFaces(_allDetectedFaces);
		BRFv4Context.getMergedDetectedFaces(_mergedDetectedFaces);

		BRFv4Context.getFaces(_faces);
	}

	public void init(Rectangle src, Rectangle imageRoi, String appId) {

		if (src == null) {
			throw new IllegalArgumentException("BRFManager init is missing the first parameter \"src : BitmapData\". " +
					"Refer to the SDK examples for the correct usage.");
		}
		if (imageRoi == null) {
			throw new IllegalArgumentException("BRFManager constructor is missing the second parameter \"imageRoi : Rectangle\". " +
					"Refer to the SDK examples for the correct usage.");
		}
		if (appId == null || appId.length() < 8) {
			throw new IllegalArgumentException("BRFManager constructor is missing the third parameter \"appId : String (length >= 8)\". " +
					"Refer to the SDK examples for the correct usage.");
		}

		BRFv4Context.init((int)src.width, (int)src.height, (int)imageRoi.x, (int)imageRoi.y, (int)imageRoi.width, (int)imageRoi.height, appId);
		fetchResults();
	}
	public void update(Bitmap imageData) {

		if(imageData == null) { throw new IllegalArgumentException("BRFManager.update: BitmapData must NOT be null."); }

		BRFv4Context.updateWithBitmap(imageData);
		fetchResults();

	}
	public void reset() {
		BRFv4Context.reset();
		fetchResults();
	}
	public String getMode() {
		return BRFv4Context.get_mode();
	}
	public void setMode(String mode) {
		BRFv4Context.set_mode(mode);
	}

	//
	// Face Detection
	//

	public void setFaceDetectionParams(int minFaceSize, int maxFaceSize, int stepSize, int minMergeNeighbors) {
		BRFv4Context.set_faceDetectionParams(minFaceSize, maxFaceSize, stepSize, minMergeNeighbors);
	}
	public void setFaceDetectionRoi(Rectangle roi) {
		BRFv4Context.set_faceDetectionRoi((int)roi.x, (int)roi.y, (int)roi.width, (int)roi.height);
	}
	public Vector<Rectangle> getAllDetectedFaces() {
		return _allDetectedFaces;
	}
	public Vector<Rectangle> getMergedDetectedFaces() {
		return _mergedDetectedFaces;
	}

	//
	// Face Tracking
	//

	public void setNumFacesToTrack(int numFaces) {
		BRFv4Context.set_numFacesToTrack(numFaces);
	}
	public void setFaceTrackingStartParams(double startMinFaceWidth, double startMaxFaceWidth, double startRotationX, double startRotationY, double startRotationZ) {
		BRFv4Context.set_faceTrackingStartParams((float)startMinFaceWidth, (float)startMaxFaceWidth, (float)startRotationX, (float)startRotationY, (float)startRotationZ);
	}
	public void setFaceTrackingResetParams(double resetMinFaceWidth, double resetMaxFaceWidth, double resetRotationX, double resetRotationY, double resetRotationZ) {
		BRFv4Context.set_faceTrackingResetParams((float)resetMinFaceWidth, (float)resetMaxFaceWidth, (float)resetRotationX, (float)resetRotationY, (float)resetRotationZ);
	}
	public Vector<BRFFace> getFaces() {
		return _faces;
	}

	//
	// Optical Flow
	//

	public void setOpticalFlowParams(int patchSize, int numLevels, int numIterations, double error) {
		BRFv4Context.set_opticalFlowParams(patchSize, numLevels, numIterations, (float)error);
	}
	public void addOpticalFlowPoints(Vector<Point> pointArray) {
		BRFv4Context.addOpticalFlowPoints(pointArray);
	}
	public Vector<Point> getOpticalFlowPoints() {
		return _pointsToTrack;
	}
	public Vector<Boolean> getOpticalFlowPointStates() {
		return _pointStates;
	}
	public boolean getOpticalFlowCheckPointsValidBeforeTracking() {
		return BRFv4Context.get_opticalFlowCheckPointsValidBeforeTracking() == 1 ? true : false;
	}
	public void setOpticalFlowCheckPointsValidBeforeTracking(boolean value) {
		BRFv4Context.set_opticalFlowCheckPointsValidBeforeTracking(value ? 1 : 0);
	}
}
