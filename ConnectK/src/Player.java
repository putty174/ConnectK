import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import connectK.BoardModel;

public class Player {
	private int playerNumber;
	
	private BoardModel board;
	
	private Map<Point, List<Chain>> chains = new HashMap<Point, List<Chain>>(); //An array of maps for this player's chains
	
	public Player(int i) {
		playerNumber = i;
	}
	
	public void addRelaventMoves(HashSet<Point> moves) {
		int x;
		int y;
		
		for(int i = -1; i < 2; i++)
			for(int j = -1; j < 2; j++)
				if(!(i == 0 && j == 0)) {
					x = board.lastMove.x + i;
					y = board.lastMove.y + j;
					if (isEmpty(x,y))
						moves.add(new Point(x,y));
				}
	}
	
	private boolean isValid(int x, int y) {
		return (x > -1 && x < board.width) && (y > -1 && y < board.height);
	}
	
	private boolean isEmpty(int x, int y) {
		return (isValid(x,y) && board.pieces[x][y] == 0);
	}
}