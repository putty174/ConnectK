import java.awt.Point;
import java.util.HashSet;

import connectK.BoardModel;

public class GameSearcher {
	int depth;
	int maxDepth;
	// alphaBetaSearch() returns the point corresponding to the highest minmax algorithm value found
	// via alpha-beta pruning and some given evaluation function.
	public Point alphaBetaSearch(BoardModel state, HashSet<Point> moves){
		depth = 0;
		maxDepth = 0;
		long start = System.currentTimeMillis();
		// Timer needs to be implemented better, preferably within maxValue and minValue
		// Also this doesn't make use of the deadline variable. Bad practice
		// Also needs global access to timer and deadline from main file
		int best = -99999;
		while(5 > System.currentTimeMillis() - start){
			depth = 0;
			maxDepth++;
			best = maxValue(state, -99999, 99999, moves);
		}
		for(Point move:moves){
			BoardModel c = state.clone();
			c.placePiece(move, DummyAI.player);
			if(eval(c) == best)
				return move;
		}
		return null;
	}
	
	// Part of alpha-beta pruning algorithm
	private int maxValue(BoardModel state, int a, int b, HashSet<Point> moves){
		if(state.winner() != 0 || depth >= maxDepth){
			return eval(state);
		}
		int value = -99999;
		updateMoves(moves, state);
		for(Point move:moves){
			BoardModel c = state.clone();
			c.placePiece(move, DummyAI.player);
			value = Math.max(value, minValue(c, a, b, moves));
			if(value >= b){
				return value;
			}
			a = Math.max(a, value);
		}
		return value;
	}
	
	// Part of alpha-beta pruning algorithm
	private int minValue(BoardModel state, int a, int b, HashSet<Point> moves) {
		if(state.winner() != 0 || depth >= maxDepth){
			return eval(state);
		}
		int value = 99999;
		updateMoves(moves, state);
		for(Point move:moves){
			BoardModel c = state.clone();
			c.placePiece(move, DummyAI.enemy);
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
	
	// The evaluation function does things. We are probably going to end up with many of these!
	// Or maybe just a few, if we're lazy.
	
	/*
	 Evaluation Function 1:
	 ANY BOARD WITH THE PIECE IN THE MIDDLE IS THE GREATEST.
	 */
	private int eval(BoardModel state) {
		if(state.getSpace((state.getWidth() - 1) / 2,(state.getHeight() - 1) / 2) == (DummyAI.player)){
			return 9;
		}
		else return -9;
	}
}
