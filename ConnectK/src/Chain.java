import java.awt.Point;

public class Chain {
		public int length; //List of tiles that make up a chain
		public Point left;	 //Spot needed to extend chain at Start
		public Point right; //Spot needed to extend chain at End
		public boolean deadLeft;
		public boolean deadRight;
		public Chain(int len, Point l, Point r, boolean dl, boolean dr) {
			length = len;
			left = l;
			right = r;
			deadLeft = dl;
			deadRight = dr;
		}
		
		public String toString() {
			return "Left: (" + left.x + ", " + left.y + "), Right: (" + right.x + ", " + right.y + "), Length : " + length + ", " + deadLeft +  ", " + deadRight;
		}
	}