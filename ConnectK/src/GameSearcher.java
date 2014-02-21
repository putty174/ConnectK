import java.awt.Point;
import java.util.HashSet;

import connectK.BoardModel;

public class GameSearcher {
	byte player;
	byte enemy;
	int depth;
	int maxDepth;
	// alphaBetaSearch() returns the point corresponding to the highest minmax algorithm value found
	// via alpha-beta pruning and some given evaluation function.
	public Point alphaBetaSearch(BoardModel state, HashSet<Point> moves, byte player){
		this.player = player;
		depth = 0;
		maxDepth = 0;
		if(player == 1){
			enemy = 2;
		}
		else{
			enemy = 1;
		}
		long start = System.currentTimeMillis();
		// Timer needs to be implemented better, preferably within maxValue and minValue
		// Also this doesn't make use of the deadline variable. Bad practice
		int best = -99999;
		while(5 > System.currentTimeMillis() - start){
			depth = 0;
			maxDepth++;
			best = maxValue(state, -99999, 99999, moves);
		}
		for(Point move:moves){
			BoardModel c = state.clone();
			c.placePiece(move, player);
			if(eval(c) == best)
				return move;
		}
		return null;
	}

	
	private int maxValue(BoardModel state, int a, int b, HashSet<Point> moves){
		if(state.winner() != 0 || depth >= maxDepth){
			return eval(state);
		}
		int value = -99999;
		updateMoves(moves, state);
		for(Point move:moves){
			BoardModel c = state.clone();
			c.placePiece(move, player);
			value = Math.max(value, minValue(c, a, b, moves));
			if(value >= b){
				return value;
			}
			a = Math.max(a, value);
		}
		return value;
	}
	
	private int minValue(BoardModel state, int a, int b, HashSet<Point> moves) {
		if(state.winner() != 0 || depth >= maxDepth){
			return eval(state);
		}
		int value = 99999;
		updateMoves(moves, state);
		for(Point move:moves){
			BoardModel c = state.clone();
			c.placePiece(move, enemy);
			value = Math.min(value, maxValue(c, a, b, moves));
			if(value <= a){
				return value;
			}
			b = Math.min(b, value);
		}
		return value;
	}
	// updateMoves() updates the given hashset of moves based on the given board state
	// by looking at the changes incurred by the previous move.
	private void updateMoves(HashSet<Point> moves, BoardModel state){
		
	}
	// The evaluation function does things.
	private int eval(BoardModel state) {
		
	}
}
