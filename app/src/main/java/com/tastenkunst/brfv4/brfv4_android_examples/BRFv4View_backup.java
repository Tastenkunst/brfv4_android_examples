package com.tastenkunst.brfv4.brfv4_android_examples;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.TextureView;
import android.widget.FrameLayout;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import brfv4.BRFFace;
import brfv4.BRFManager;
import brfv4.BRFMode;
import brfv4.geom.Rectangle;

public class BRFv4View_backup extends FrameLayout implements TextureView.SurfaceTextureListener {


	protected TextureView mTextureView;
	protected Camera mCamera;
	protected int mCameraId;
	protected int mCameraOrientation;
	protected SurfaceTexture mSurfaceTexture;
	protected BRFManager mBrfManager  = new BRFManager();
	protected Paint mPaint = new Paint();
	protected Matrix mMatrix = new Matrix();
	protected float[] mMeshVertices;
	protected int[] mMeshTriangles;
	protected Bitmap mFaceDetectionBitmap;
	protected HandlerThread mHandlerThread;
	protected Handler mHandler;

	protected static final int PREVIEW_WIDTH = 480;
	protected static final int PREVIEW_HEIGHT = 640;
	protected static final int MSG_NEW_BITMAP = 1;


	public BRFv4View_backup(Context context) {
		super(context);
	}
	public BRFv4View_backup(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		// Create a TextureView child that has the same size as each preview frame
		mTextureView = new TextureView(getContext());
		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(PREVIEW_WIDTH, PREVIEW_HEIGHT);
		layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
		mTextureView.setLayoutParams(layoutParams);
		mTextureView.setSurfaceTextureListener(this);
		addView(mTextureView);

		// We're going to scale the TextureView to fill our area, so turn off clipping
		setClipChildren(false);

		// Find the front-facing camera
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
			Camera.getCameraInfo(i, cameraInfo);
			if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				mCameraId = i;
				mCameraOrientation = cameraInfo.orientation;
				mCamera = Camera.open(mCameraId);
				break;
			}
		}

		// Start the background face detection thread
		mHandlerThread = new HandlerThread("FaceDetectionThread");
		mHandlerThread.start();
		mHandler = new Handler(mHandlerThread.getLooper()) {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == MSG_NEW_BITMAP) {
					processFaceDetectionBitmap();
				}
				super.handleMessage(msg);
			}
		};

	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();

		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}

		mHandlerThread.quit();
		mHandlerThread = null;
		mHandler = null;
		mFaceDetectionBitmap = null;
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);

		// Draw the tron mesh. Has to be sync'd with background thread which produces the mesh data
		mPaint.setStrokeWidth(4.0f);
		mPaint.setColor(0xffffffff);
//		synchronized (BRFv4View.this) {
			if (mMeshTriangles != null) {
				for (int p = 0; p < mMeshTriangles.length; p += 3) {
					int a = mMeshTriangles[p];
					int b = mMeshTriangles[p + 1];
					int c = mMeshTriangles[p + 2];
					float x0 = mMeshVertices[a * 2];
					float y0 = mMeshVertices[a * 2 + 1];
					float x1 = mMeshVertices[b * 2];
					float y1 = mMeshVertices[b * 2 + 1];
					float x2 = mMeshVertices[c * 2];
					float y2 = mMeshVertices[c * 2 + 1];
					canvas.drawLine(x0, y0, x1, y1, mPaint);
					canvas.drawLine(x1, y1, x2, y2, mPaint);
					canvas.drawLine(x2, y2, x0, y0, mPaint);
				}
			}
//		}
	}


	//
	// TextureView.SurfaceTextureListener
	//
	@Override
	public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {

		// Tell camera what size we want preview frames to be
		// TODO: this should be dynamically chosen
		Camera.Parameters parameters = mCamera.getParameters();
		parameters.setPreviewSize(PREVIEW_HEIGHT, PREVIEW_WIDTH);
		// Select the lowest framerate
		List<int[]> previewFpsRanges = parameters.getSupportedPreviewFpsRange();
		parameters.setPreviewFpsRange(previewFpsRanges.get(0)[0],previewFpsRanges.get(0)[1]);
		mCamera.setParameters(parameters);

		// Make the textureView aspect-fill us
		int viewWidth = getWidth();
		int viewHeight = getHeight();
		RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
		RectF surfaceRect = new RectF(0, 0, width, height);
		mMatrix.setRectToRect(surfaceRect, viewRect, Matrix.ScaleToFit.CENTER);
		mTextureView.setTransform(mMatrix);

		mCamera.setDisplayOrientation(360 - mCameraOrientation);
		mSurfaceTexture = surfaceTexture;
		try {
			mCamera.setPreviewTexture(mSurfaceTexture);
		} catch (IOException e) {
			e.printStackTrace();
		}

		mCamera.setPreviewCallbackWithBuffer(null);
		mCamera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
			@Override
			public void onPreviewFrame(byte[] data, Camera camera) {
				Camera.Parameters parameters = camera.getParameters();
				Camera.Size previewSize = parameters.getPreviewSize();
				int format = parameters.getPreviewFormat();

				// TODO: BRFManager should be able to handle raw byte buffers
//				synchronized (BRFv4View.this) {
					if (mFaceDetectionBitmap == null) {
						mFaceDetectionBitmap = mTextureView.getBitmap();
						mHandler.obtainMessage(MSG_NEW_BITMAP).sendToTarget();
					}
//				}
				invalidate();

				camera.addCallbackBuffer(data);
			}
		});
		parameters = mCamera.getParameters();
		Camera.Size previewSize = parameters.getPreviewSize();
		int imageFormat = parameters.getPreviewFormat();
		int bufferSize = previewSize.width * previewSize.height * ImageFormat.getBitsPerPixel(imageFormat) / 8;
		for (int i=0 ; i<2 ; i++) {
			byte[] bb = new byte[bufferSize];
			mCamera.addCallbackBuffer(bb);
		}

		mCamera.startPreview();

		mBrfManager.init(
				new Rectangle(0, 0, PREVIEW_WIDTH, PREVIEW_HEIGHT),
				new Rectangle(0, 0, PREVIEW_WIDTH, PREVIEW_HEIGHT),
				"com.tastenkunst.brfv4.jni.android"
		);
		// Set up the face detection.
		double border = 40.0;
		mBrfManager.setFaceDetectionRoi(new Rectangle(border, border, PREVIEW_WIDTH - border*2, PREVIEW_HEIGHT - border*2));
		//mBrfManager.setFaceDetectionParams(80, 500);
		//mBrfManager.candideEnabled(true);
		//mBrfManager.candideActionUnitsEnabled(true);
		mBrfManager.setMode(BRFMode.FACE_TRACKING);

	}

	@Override
	public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
	}

	@Override
	public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
		return true;
	}

	@Override
	public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {}


	/**
	 * Face detection thread
	 */
	protected void processFaceDetectionBitmap() {

		// Get the bitmap ref into a local variable
		Bitmap bitmap = mFaceDetectionBitmap;
//		synchronized (BRFv4View.this) {
//			if (mFaceDetectionBitmap != null) {
//				bitmap = mFaceDetectionBitmap;
//				mFaceDetectionBitmap = null;
//			}
//		}
		if (bitmap == null) {
			return;
		}

		// Let the BRF face detector do its thing
		mBrfManager.update(bitmap); // note we do not block main thread

		// Find any faces?
		float[] newMeshVertices = null;
		int[] newMeshTriangles = null;

		Vector<BRFFace> faces = mBrfManager.getFaces();

		if (faces.size() == 1) {

			BRFFace face = faces.get(0);

			if(	face.state.equals(brfv4.BRFState.FACE_TRACKING_START) ||
					face.state.equals(brfv4.BRFState.FACE_TRACKING)) {

				mMatrix.mapPoints(face.vertices);

				newMeshVertices = face.vertices;
				newMeshTriangles = face.triangles;
			}
		}

		// Signal UI to redraw
//		synchronized (BRFv4View.this) {
			mMeshVertices = newMeshVertices;
			mMeshTriangles = newMeshTriangles;
//		}
		postInvalidate();
	}

}
