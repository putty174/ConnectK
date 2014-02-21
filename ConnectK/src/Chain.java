import java.awt.Point;

public class Chain {
		public int length; //List of tiles that make up a chain
		public Point left;	 //Spot needed to extend chain at Start
		public Point right; //Spot needed to extend chain at End
		public Chain(int len, Point l, Point r) {
			length = len;
			left = l;
			right = r;
		}
	}