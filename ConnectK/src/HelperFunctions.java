import java.awt.Point;
import java.util.HashSet;

import connectK.BoardModel;

public class HelperFunctions {
	public HashSet<Point> relevantMoves(BoardModel state, HashSet<Point> current) {
		HashSet<Point> list = new HashSet<Point>(current);
		int x;
		int y;
		
		for(int i = -1; i < 2; i++)
			for(int j = -1; j < 2; j++)
				if(!(i == 0 && j == 0)) {
					x = state.lastMove.x + i;
					y = state.lastMove.y + j;
					if ((x > -1 && x < state.width) && (y > -1 && y < state.height) && state.pieces[x][y] == 0)
						list.add(new Point(x,y));
				}
		return list;
	}
}