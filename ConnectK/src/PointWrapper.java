import java.awt.Point;


public class PointWrapper implements Comparable<PointWrapper>{
	public int score;
	public Point move;
	public PointWrapper(int score, Point move){
		this.score = score;
		this.move = move;
	}

	@Override
	public int compareTo(PointWrapper p) {
		return score - p.score;
	}
}
