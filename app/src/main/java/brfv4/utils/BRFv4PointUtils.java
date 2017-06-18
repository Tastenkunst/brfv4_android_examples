package brfv4.utils;

import brfv4.geom.Point;

public class BRFv4PointUtils {

	public static double PI = 3.14159265358979323846;

	public static double abs(double a) {
		return (a < 0.0) ? -a : a;
	}

	public static double max(double a, double b, double c) {
		double _max = a;
		if(b > _max) _max = b;
		if(c > _max) _max = c;
		return _max;
	}

	public static void setPoint(float[] v, int i, Point p) {
		p.x = v[i * 2]; p.y = v[i * 2 + 1];
	}

	public static void applyMovementVector(Point p, Point p0, Point pmv, double f) {
		p.x = p0.x + pmv.x * f;
		p.y = p0.y + pmv.y * f;
	}

	public static void interpolatePoint(Point p, Point p0, Point p1, double f) {
		p.x = p0.x + f * (p1.x - p0.x);
		p.y = p0.y + f * (p1.y - p0.y);
	}

	public static void calcMovementVector(Point p, Point p0, Point p1, double f) {
		p.x = f * (p1.x - p0.x);
		p.y = f * (p1.y - p0.y);
	}

	public static void calcMovementVectorOrthogonalCW(Point p, Point p0, Point p1, double f) {
		calcMovementVector(p, p0, p1, f);
		double x = p.x;
		double y = p.y;
		p.x = -y;
		p.y = x;
	}

	public static void calcMovementVectorOrthogonalCCW(Point p, Point p0, Point p1, double f) {
		calcMovementVector(p, p0, p1, f);
		double x = p.x;
		double y = p.y;
		p.x = y;
		p.y = -x;
	}

	public static void calcIntersectionPoint(Point p, Point pk0, Point pk1, Point pg0, Point pg1) {

		//y1 = m1 * x1  + t1 ... y2 = m2 * x2 + t1
		//m1 * x  + t1 = m2 * x + t2
		//m1 * x - m2 * x = (t2 - t1)
		//x * (m1 - m2) = (t2 - t1)

		double dx1 = (pk1.x - pk0.x); if(dx1 == 0) dx1 = 0.01;
		double dy1 = (pk1.y - pk0.y); if(dy1 == 0) dy1 = 0.01;

		double dx2 = (pg1.x - pg0.x); if(dx2 == 0) dx2 = 0.01;
		double dy2 = (pg1.y - pg0.y); if(dy2 == 0) dy2 = 0.01;

		double m1 = dy1 / dx1;
		double t1 = pk1.y - m1 * pk1.x;

		double m2 = dy2 / dx2;
		double t2 = pg1.y - m2 * pg1.x;

		double m1m2 = (m1 - m2); if(m1m2 == 0) m1m2 = 0.01;
		double t2t1 = (t2 - t1); if(t2t1 == 0) t2t1 = 0.01;
		double px = t2t1 / m1m2;
		double py = m1 * px + t1;

		p.x = px;
		p.y = py;
	}

	public static double calcDistance(Point p0, Point p1) {
		return Math.sqrt(
				(p1.x - p0.x) * (p1.x - p0.x) +
				(p1.y - p0.y) * (p1.y - p0.y));
	}

	public static double calcAngle(Point p0, Point p1) {
		return Math.atan2((p1.y - p0.y), (p1.x - p0.x));
	}

	public static double toDegree(double x) {
		return x * 180.0 / BRFv4PointUtils.PI;
	}

	public static double toRadian(double x) {
		return x * BRFv4PointUtils.PI / 180.0;
	}
}
