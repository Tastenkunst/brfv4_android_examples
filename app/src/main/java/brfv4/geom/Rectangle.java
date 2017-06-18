package brfv4.geom;

public class Rectangle {

	public double x;
	public double y;
	public double width;
	public double height;

	public Rectangle() {
		setTo(0.0, 0.0, 0.0, 0.0);
	}

	public Rectangle(double x, double y, double width, double height) {
		setTo(x, y, width, height);
	}

	public void setTo(double _x, double _y, double _width, double _height) {
		x		= _x;
		y		= _y;
		width	= _width;
		height	= _height;
	}

	/**
	 * Floors x and y to multiple of the raster parameter.
	 * Floors width and height to multiple of the raster parameter and adds value of raster to them.
	 *
	 * @param raster - a choosen raster, e.g. 4.0 or 0.1.
	 */
	public void rasterBy(double raster) {

		if(raster < 0) raster = -raster;

		double rf = 1;

		while(raster < 1) {
			raster *= 10;
			rf *= 10;
		}

		long rRaster = (long)raster;

		long rx = (long)Math.floor(x * rf);
		long ry = (long)Math.floor(y * rf);
		long rw = (long)Math.floor(width * rf);
		long rh = (long)Math.floor(height * rf);

		x		= rx - (rx % rRaster);
		y		= ry - (ry % rRaster);
		width	= rw - (rw % rRaster) + rRaster;
		height 	= rh - (rh % rRaster) + rRaster;

		x		/= rf;
		y		/= rf;
		width	/= rf;
		height	/= rf;
	}

	public boolean contains(double _x, double _y) {
		return (_x >= x && _x <= x + width && _y >= y && _y <= y + height);
	}
}
