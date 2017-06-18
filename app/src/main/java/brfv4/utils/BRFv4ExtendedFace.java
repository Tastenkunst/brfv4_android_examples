package brfv4.utils;

import java.util.Vector;

import brfv4.BRFFace;
import brfv4.geom.Point;
import brfv4.geom.Rectangle;

public class BRFv4ExtendedFace {

	public float[]			vertices;
	public int[]			triangles;
	public Vector<Point>	points;
	public Rectangle		bounds;

	public Point			_tmpPoint0;
	public Point			_tmpPoint1;
	public Point			_tmpPoint2;
	public Point			_tmpPoint3;
	public Point			_tmpPoint4;
	public Point			_tmpPoint5;

	public BRFv4ExtendedFace() {

		vertices	= null;
		triangles	= null;
		points		= new Vector<Point>();
		bounds		= new Rectangle();

		_tmpPoint0	= new Point();
		_tmpPoint1	= new Point();
		_tmpPoint2	= new Point();
		_tmpPoint3	= new Point();
		_tmpPoint4	= new Point();
		_tmpPoint5	= new Point();
	}

	public void update(BRFFace face) {

		int i, l;

		for(i = points.size(), l = face.points.size() + 6; i < l; ++i) {
			points.add(new Point(0.0, 0.0));
		}

		generateExtendedVertices(face);
		generateExtendedTriangles(face);
		updateBounds();
		updatePoints();
	}

	public void generateExtendedVertices(BRFFace face) {

		float[] v = face.vertices;
		int i = 0;
		int l = v.length;

		if(vertices == null) {
			vertices = new float[l + 12];
		}

		for(; i < l; i++) {
			vertices[i] = v[i];
		}

		addUpperForeheadPoints(vertices, i);
	}

	public void generateExtendedTriangles(BRFFace face) {

		if(triangles == null) {

			int i = 0;
			int k = 0;
			int l = face.triangles.length;

			int[] tmp = {
					0, 17, 68,
					17, 18, 68,
					18, 19, 69,
					18, 68, 69,
					19, 20, 69,
					20, 23, 71,
					20, 69, 70,
					20, 70, 71,
					23, 24, 72,
					23, 71, 72,
					24, 25, 72,
					25, 26, 73,
					25, 72, 73,
					16, 26, 73
			};

			triangles = new int[l + tmp.length];

			for(; i < l; i++) {
				triangles[i] = face.triangles[i];
			}

			for(k = i, i = 0, l = tmp.length; i < l; i++, k++) {
				triangles[k] = (tmp[i]);
			}
		}
	}

	public void updateBounds() {

		double minX = 0;
		double minY = 0;
		double maxX = 99999;
		double maxY = 99999;

		int i, l;
		double value;

		for(i = 0, l = vertices.length; i < l; i++) {
			value = vertices[i];

			if((i % 2) == 0) {
				if(value < minX) minX = value;
				if(value > maxX) maxX = value;
			} else {
				if(value < minY) minY = value;
				if(value > maxY) maxY = value;
			}
		}

		bounds.x = minX;
		bounds.y = minY;
		bounds.width = maxX - minX;
		bounds.height = maxY - minY;
	}

	public void updatePoints() {

		int i, k, l;
		double x, y;

		for(i = 0, k = 0, l = points.size(); i < l; ++i) {

			x = vertices[k]; k++;
			y = vertices[k]; k++;

			points.get(i).x = x;
			points.get(i).y = y;
		}
	}

	public void addUpperForeheadPoints(float[] v, int i) {

		Point p0 = _tmpPoint0;
		Point p1 = _tmpPoint1;
		Point p2 = _tmpPoint2;
		Point p3 = _tmpPoint3;
		Point p4 = _tmpPoint4;
		Point p5 = _tmpPoint5;

		// base distance

		setPoint(v, 33, p0); // nose top
		setPoint(v, 27, p1); // nose base
		double baseDist = calcDistance(p0, p1) * 1.5;

		// eyes as base line for orthogonal vector

		setPoint(v, 39, p0); // left eye inner corner
		setPoint(v, 42, p1); // right eye inner corner

		double distEyes = calcDistance(p0, p1);

		calcMovementVectorOrthogonalCCW(p4, p0, p1, baseDist / distEyes);

		// orthogonal line for intersection point calculation

		setPoint(v, 27, p2); // nose top
		applyMovementVector(p3, p2, p4, 10.95);
		applyMovementVector(p2, p2, p4, -10.95);

		calcIntersectionPoint(p5, p2, p3, p0, p1);

		// simple head rotation

		double f = 0.5-calcDistance(p0, p5) / distEyes;

		// outer left forehead point

		setPoint(v, 0, p5); // top left outline point
		double dist = calcDistance(p0, p5) * 0.75;

		interpolatePoint(		p2, p0, p1, (dist / -distEyes));
		applyMovementVector(	p3, p2, p4, 0.75);
		addToExtendedVertices(	p3, i); i+=2;

		// upper four forehead points

		interpolatePoint(		p2, p0, p1, f - 0.65);
		applyMovementVector(	p3, p2, p4, 1.02);
		addToExtendedVertices(	p3, i); i+=2;

		interpolatePoint(		p2, p0, p1, f + 0.0);
		applyMovementVector(	p3, p2, p4, 1.10);
		addToExtendedVertices(	p3, i); i+=2;

		interpolatePoint(		p2, p0, p1, f + 1.0);
		applyMovementVector(	p3, p2, p4, 1.10);
		addToExtendedVertices(	p3, i); i+=2;

		interpolatePoint(		p2, p0, p1, f + 1.65);
		applyMovementVector(	p3, p2, p4, 1.02);
		addToExtendedVertices(	p3, i); i+=2;

		// outer right forehead point

		setPoint(v, 16, p5); // top right outline point
		dist = calcDistance(p1, p5) * 0.75;

		interpolatePoint(		p2, p1, p0, (dist / -distEyes));
		applyMovementVector(	p3, p2, p4, 0.75);
		addToExtendedVertices(	p3, i);
	}

	private void addToExtendedVertices(Point p, int i) {
		vertices[i] = (float)p.x;
		vertices[i + 1] = (float)p.y;
	};

	private void setPoint(float[] v, int i, Point p) {
		BRFv4PointUtils.setPoint(v, i, p);
	}

	private double calcDistance(Point p0, Point p1) {
		return BRFv4PointUtils.calcDistance(p0, p1);
	}

	private void applyMovementVector(Point p, Point p0, Point pmv, double f) {
		BRFv4PointUtils.applyMovementVector(p, p0, pmv, f);
	}

	private void interpolatePoint(Point p, Point p0, Point p1, double f) {
		BRFv4PointUtils.interpolatePoint(p, p0, p1, f);
	}

	private void calcMovementVector(Point p, Point p0, Point p1, double f) {
		BRFv4PointUtils.calcMovementVector(p, p0, p1, f);
	}

	private void calcMovementVectorOrthogonalCW(Point p, Point p0, Point p1, double f) {
		BRFv4PointUtils.calcMovementVectorOrthogonalCW(p, p0, p1, f);
	}

	private void calcMovementVectorOrthogonalCCW(Point p, Point p0, Point p1, double f) {
		BRFv4PointUtils.calcMovementVectorOrthogonalCCW(p, p0, p1, f);
	}

	private void calcIntersectionPoint(Point p, Point pk0, Point pk1, Point pg0, Point pg1) {
		BRFv4PointUtils.calcIntersectionPoint(p, pk0, pk1, pg0, pg1);
	}
}
