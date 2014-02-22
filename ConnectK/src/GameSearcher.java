import java.awt.Point;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import connectK.BoardModel;

public class GameSearcher {
	int depth;
	int maxDepth;
	HelperFunctions helper;
	
	public GameSearcher(){
		helper = new HelperFunctions();
	}
	
	// alphaBetaSearch() returns the point corresponding to the highest minmax algorithm value found
	// via alpha-beta pruning and some given evaluation function.
	public Point alphaBetaSearch(BoardModel state, HashSet<Point> moves, Map<Point, List<Chain>> myChains, Map<Point, List<Chain>> enemyChains){
		depth = 0;
		maxDepth = 0;
		long start = System.currentTimeMillis();
		// Timer needs to be implemented better, preferably within maxValue and minValue
		// Also this doesn't make use of the deadline variable. Bad practice
		// Also needs global access to timer and deadline from main file
		int best = Integer.MIN_VALUE;
		while(5 > System.currentTimeMillis() - start){
			depth = 0;
			maxDepth++;
			best = maxValue(state, Integer.MIN_VALUE, Integer.MAX_VALUE, moves, myChains, enemyChains);
		}
		for(Point move:moves){
			BoardModel c = state.clone();
			c.placePiece(move, TeamMaybeAI.player);
			int val = eval(c, myChains, enemyChains);
			System.out.println("Move: (" + move.x + "," + move.y + ")");
			if(state.pieces[move.x][move.y] == 0 && val == best) {
				return move;
			}
		}
		return null;
	}
	
	// Part of alpha-beta pruning algorithm
	private int maxValue(BoardModel state, int a, int b, HashSet<Point> moves, Map<Point, List<Chain>> myChains, Map<Point, List<Chain>> enemyChains){
		if(state.winner() != 0 || depth >= maxDepth){
			return eval(state, myChains, enemyChains);
		}
		int value = Integer.MIN_VALUE;
		helper.relevantMoves(state, moves);
		for(Point move:moves){
			BoardModel c = state.clone();
			c.placePiece(move, TeamMaybeAI.player);
			value = Math.max(value, minValue(c, a, b, moves, myChains, enemyChains));
			if(value >= b){
				return value;
			}
			a = Math.max(a, value);
		}
		return value;
	}
	
	// Part of alpha-beta pruning algorithm
	private int minValue(BoardModel state, int a, int b, HashSet<Point> moves, Map<Point, List<Chain>> myChains, Map<Point, List<Chain>> enemyChains) {
		if(state.winner() != 0 || depth >= maxDepth){
			return eval(state, myChains, enemyChains);
		}
		int value = Integer.MAX_VALUE;
		helper.relevantMoves(state, moves);
		for(Point move:moves){
			BoardModel c = state.clone();
			c.placePiece(move, TeamMaybeAI.enemy);
			value = Math.min(value, maxValue(c, a, b, moves, myChains, enemyChains));
			if(value <= a){
				return value;
			}
			b = Math.min(b, value);
		}
		return value;
	}
	
	// The evaluation function does things. We are probably going to end up with many of these!
	// Or maybe just a few, if we're lazy.
	
	/*
	 Evaluation Function 1:
	 ANY BOARD WITH THE PIECE IN THE MIDDLE IS THE GREATEST.
	 */
	private int eval(BoardModel state, Map<Point, List<Chain>> myChains, Map<Point, List<Chain>> enemyChains) {
		int result = 0;
		for (int k = state.kLength; k > 0; k--) {
			for(List<Chain> l : myChains.values()) {
				for(Chain c : l) {
					if(c.length == k) {
						Point left = c.left;
						Point right = c.right;
						if(c.left != null && !c.deadLeft && state.pieces[c.left.x][c.left.y] == 0 && result < (100^k+5)) {
							if(k == state.kLength)
								result = Integer.MAX_VALUE;
							else
								result += 100^k^k+5;
						}
						if(c.right != null && !c.deadRight && state.pieces[c.right.x][c.right.y] == 0 && result < (100^k+7)) {
							if(k == state.kLength)
								result = Integer.MAX_VALUE;
							else
								result += 100^k^k+7;
						}
					}
				}
			}
			for(List<Chain> l : enemyChains.values()) {
				for(Chain c : l) {
					if(c.length == k) {
						Point left = c.left;
						Point right = c.right;
						if(c.left != null && !c.deadLeft && state.pieces[c.left.x][c.left.y] == 0 && result < (100^k+1)) {
							if(k > state.kLength - 1)
								result = Integer.MIN_VALUE;
							else
								result -= (100^k^k + 1);
						}
						if(c.right != null && !c.deadRight && state.pieces[c.right.x][c.right.y] == 0 && result < (100^k+3)) {
							if(k > state.kLength - 1)
								result = Integer.MIN_VALUE;
							else
								result -= (100^k^k + 3);
						}
					}
				}
			}
		}
		System.out.println("Move: (" + state.lastMove.x + "," + state.lastMove.y + ")\tEval: " + result);
		return result;
	}
}
