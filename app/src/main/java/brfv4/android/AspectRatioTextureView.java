package brfv4.android;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;

public class AspectRatioTextureView extends TextureView {

	private int _ratioWidth		= 0;
	private int _ratioHeight	= 0;

	public AspectRatioTextureView(Context context) {
		this(context, null);
	}

	public AspectRatioTextureView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public AspectRatioTextureView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setAspectRatio(int width, int height) {
		if (width < 0 || height < 0) {
			throw new IllegalArgumentException("Size cannot be negative.");
		}
		_ratioWidth = width;
		_ratioHeight = height;
		requestLayout();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);

		if (0 == _ratioWidth || 0 == _ratioHeight) {

			setMeasuredDimension(width, height);

		} else {

			if (width < (float)height * (float)_ratioWidth / (float)_ratioHeight) {

                setMeasuredDimension(width, (int)((float)width * (float)_ratioHeight / (float)_ratioWidth));

			} else {

				setMeasuredDimension((int)((float)height * (float)_ratioWidth / (float)_ratioHeight), height);
			}
		}
	}
}
