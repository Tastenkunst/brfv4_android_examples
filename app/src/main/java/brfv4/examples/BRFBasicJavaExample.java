package brfv4.examples;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;

import brfv4.BRFManager;
import brfv4.android.DrawingUtils;
import brfv4.geom.Rectangle;

public class BRFBasicJavaExample extends View {

	public String		_appId;

	public Bitmap		_bmd;
	public DrawingUtils	_drawing;

	public Rectangle	_brfImageRoi;
	public Rectangle	_brfFaceDectionRoi;
	public BRFManager	_brfManager;

	public boolean		_initialized;

	public BRFBasicJavaExample(Context context) {
		this(context, null);
	}

	public BRFBasicJavaExample(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public BRFBasicJavaExample(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		_appId = "com.tastenkunst.brfv4.java.examples";

		_bmd				= null;
		_drawing			= new DrawingUtils();

		_brfImageRoi		= new Rectangle();
		_brfFaceDectionRoi	= new Rectangle();
		_brfManager			= new BRFManager();

		_initialized		= false;

		setWillNotDraw(false);
	}

	public void init(int width, int height, Bitmap bmd) {

		_bmd = bmd;
		_drawing.updateLayout(width, height);

		_brfImageRoi.setTo(0, 0, width, height);
		_brfFaceDectionRoi.setTo(0, 0, width, height);

		initCurrentExample(_brfManager, _brfImageRoi);

		_initialized = true;
	}

	public void initCurrentExample(BRFManager brfManager, Rectangle resolution) {}

	public void updateCurrentExample(BRFManager brfManager, Bitmap imageData, DrawingUtils draw) {}

	@Override
	protected void onDraw(Canvas canvas) {
		_drawing.setCanvas(canvas);
		updateCurrentExample(_brfManager, _bmd, _drawing);
		invalidate();
	}
}
