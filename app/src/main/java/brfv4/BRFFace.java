package brfv4;

import java.util.Vector;

import brfv4.geom.Point;
import brfv4.geom.Rectangle;

public class BRFFace {

	public String lastState;
	public String state;
	public String nextState;

	public float[] vertices;
	public int[] triangles;
	public Vector<Point> points;
	public Rectangle bounds;
	public Rectangle refRect;

	public float[] candideVertices;
	public int[] candideTriangles;

	public float scale;
	public float translationX;
	public float translationY;
	public float rotationX;
	public float rotationY;
	public float rotationZ;

	public BRFFace() {

		lastState			= BRFState.RESET;
		state				= BRFState.RESET;
		nextState			= BRFState.RESET;

		vertices			= null;
		triangles			= null;
		points				= new Vector<Point>();
		bounds				= new Rectangle();
		refRect				= new Rectangle();

		candideVertices		= null;
		candideTriangles	= null;

		scale				= 1.0f;
		translationX		= 0.0f;
		translationY		= 0.0f;
		rotationX			= 0.0f;
		rotationY			= 0.0f;
		rotationZ			= 0.0f;
	}
}
