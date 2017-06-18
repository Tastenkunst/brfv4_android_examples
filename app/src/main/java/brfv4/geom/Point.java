package brfv4.geom;

public class Point {

	public double x;
	public double y;

	public Point() {
		setTo(0.0, 0.0);
	}

	public Point(double x, double y) {
		setTo(x, y);
	}

	public void setTo(double _x, double _y) {
		x = _x;
		y = _y;
	}
}
