import java.awt.geom.Point2D;


@SuppressWarnings("serial")
public class ComparablePoint2D extends Point2D.Float implements Comparable<Point2D> {

	public ComparablePoint2D() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ComparablePoint2D(float x, float y) {
		super(x, y);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int compareTo(Point2D other) {
		if(this.y<other.getY()||(this.y==other.getY()&&this.x<other.getX())){
			return -1;
		}else if(this.y==other.getY()&&this.x==other.getX()){
			return 0;
		}else{
			return 1;
		}
	}


}
