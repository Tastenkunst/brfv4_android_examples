package brfv4.android;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import java.util.Vector;

import brfv4.geom.Point;
import brfv4.geom.Rectangle;

public class DrawingUtils {

	protected Paint params;
	protected Canvas graphics;

	public DrawingUtils() {
		params = new Paint();
		graphics = null;
	}

	public Canvas getCanvas() {
		return graphics;
	}

	public void setCanvas(Canvas canvas) {
		graphics = canvas;
	}

	public void updateLayout(int width, int height) {}

	public void clear() {}

	public int getColor(int color, double alpha) {
		return ((((int)(alpha * 255)) & 0xff) << 24) + color;
	}

	public void drawVertices(float[] vertices, double radius, boolean clear,
			int fillColor, double fillAlpha) {

		params.reset();
		params.setStrokeWidth((float)radius);
		params.setColor(getColor(fillColor, fillAlpha));
		params.setStyle(Paint.Style.FILL);

		graphics.drawPoints(vertices, params);
	}

	public void drawTriangles(float[] vertices, int[] triangles, boolean clear,
			double lineThickness, int lineColor, double lineAlpha) {

		params.reset();
		params.setStrokeWidth((float)lineThickness);
		params.setColor(getColor(lineColor, lineAlpha));
		params.setStyle(Paint.Style.STROKE);
		params.setAntiAlias(true);

		Canvas g = graphics;

		int i = 0;
		int l = triangles.length;

		while(i < l) {
			int ti0 = triangles[i];
			int ti1 = triangles[i + 1];
			int ti2 = triangles[i + 2];

			float x0 = vertices[ti0 * 2];
			float y0 = vertices[ti0 * 2 + 1];
			float x1 = vertices[ti1 * 2];
			float y1 = vertices[ti1 * 2 + 1];
			float x2 = vertices[ti2 * 2];
			float y2 = vertices[ti2 * 2 + 1];

			g.drawLine(x0, y0, x1, y1, params);
			g.drawLine(x1, y1, x2, y2, params);
			g.drawLine(x2, y2, x0, y0, params);

			i+=3;
		}
	}

	public void fillTriangles(float[] vertices, int[] triangles, boolean clear,
			int fillColor, double fillAlpha) {

		params.reset();
		params.setColor(getColor(fillColor, fillAlpha));
		params.setStyle(Paint.Style.FILL);
		params.setAntiAlias(true);

		Canvas g = graphics;

		int i = 0;
		int l = triangles.length;

		while(i < l) {
			int ti0 = triangles[i];
			int ti1 = triangles[i + 1];
			int ti2 = triangles[i + 2];

			float x0 = vertices[ti0 * 2];
			float y0 = vertices[ti0 * 2 + 1];
			float x1 = vertices[ti1 * 2];
			float y1 = vertices[ti1 * 2 + 1];
			float x2 = vertices[ti2 * 2];
			float y2 = vertices[ti2 * 2 + 1];


			Path path = new Path();
			path.moveTo(x0, y0);
			path.lineTo(x1, y1);
			path.lineTo(x2, y2);
			path.lineTo(x0, y0);
			path.close();

			g.drawPath(path, params);

			i+=3;
		}
	}

	public void drawTexture(float[] vertices, int[] triangles, float[] uvData, Bitmap texture) {
		//TODO: implement
	}

	public void drawRect(Rectangle rect, boolean clear,
			double lineThickness, int lineColor, double lineAlpha) {

		params.reset();
		params.setStrokeWidth((float)lineThickness);
		params.setColor(getColor(lineColor, lineAlpha));
		params.setStyle(Paint.Style.STROKE);

		graphics.drawRect((float)rect.x, (float)rect.y, (float)(rect.x + rect.width), (float)(rect.y + rect.height), params);
	}

	public void drawRects(Vector<Rectangle> rects, boolean clear,
			double lineThickness, int lineColor, double lineAlpha) {

		params.reset();
		params.setStrokeWidth((float)lineThickness);
		params.setColor(getColor(lineColor, lineAlpha));
		params.setStyle(Paint.Style.STROKE);

		int i = 0;
		int l = rects.size();
		Rectangle rect = null;

		for(; i < l; i++) {
			rect = rects.get(i);
			graphics.drawRect((float)rect.x, (float)rect.y, (float)(rect.x + rect.width), (float)(rect.y + rect.height), params);
		}
	}

	public void drawPoint(Point point, double radius, boolean clear,
			int lineColor, double lineAlpha) {

		params.reset();
		params.setStrokeWidth((float)radius);
		params.setColor(getColor(lineColor, lineAlpha));
		params.setStyle(Paint.Style.FILL);

		graphics.drawCircle((float)point.x, (float)point.y, (float)radius, params);
	}

	public void drawPoints(Vector<Point> points, double radius, boolean clear,
			int lineColor, double lineAlpha) {

		params.reset();
		params.setStrokeWidth((float)radius);
		params.setColor(getColor(lineColor, lineAlpha));
		params.setStyle(Paint.Style.FILL);

		int i = 0;
		int l = points.size();
		Point point = null;

		for(; i < l; i++) {
			point = points.get(i);
			graphics.drawCircle((float)point.x, (float)point.y, (float)radius, params);
		}
	}
}
