import java.awt.Point;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import connectK.BoardModel;

public class GameSearcher {
	HelperFunctions helper;
	long start;
	int deadline;
	Point bestMove;
	int bestValue = Integer.MIN_VALUE;
	int a;
	int b;
	
	
	public GameSearcher(){
		helper = new HelperFunctions();
	}
	
	// alphaBetaSearch() returns the point corresponding to the highest minmax algorithm value found
	// via alpha-beta pruning and some given evaluation function.
	public Point alphaBetaSearch(BoardModel state, int deadline, HashSet<Point> moves, Map<Point, List<Chain>> myChains, Map<Point, List<Chain>> enemyChains, int maxDepth){
		int depth = 0;
		this.deadline = deadline;
		// Timer needs to be implemented better, preferably within maxValue and minValue
		// Also this doesn't make use of the deadline variable. Bad practice
		// Also needs global access to timer and deadline from main file
		int best = Integer.MIN_VALUE;
		depth = 0;
		System.out.println("Depth: " + maxDepth);
		for(Point move:moves){
			a = Integer.MIN_VALUE;
			b = Integer.MAX_VALUE;
			BoardModel c = state.clone();
			c.placePiece(move,  TeamMaybeAI.player);	
			int thisMoveValue = minValue(state, moves, myChains, enemyChains, depth, maxDepth);
			if(TeamMaybeAI.timesUp(deadline)){
				return null;
			}
			if(best < thisMoveValue){
				best = thisMoveValue;
				bestMove = move;
				System.out.println("NEW KING OF MOVES: " + best + "; which is " + bestMove);
			}
		}
		/*
		while(!timesUp()){
			depth = 0;
			maxDepth++;
			System.out.println("Depth: " + maxDepth);
			best = maxValue(state, Integer.MIN_VALUE, Integer.MAX_VALUE, moves, myChains, enemyChains, depth, maxDepth);
			System.out.println("Best: " + best);
		}
		for(Point move:moves){
			BoardModel c = state.clone();
			c.placePiece(move, TeamMaybeAI.player);
			int val = eval(c, myChains, enemyChains);
			//System.out.println("Move: (" + move.x + "," + move.y + ")");
			if(val > bestValue) {
				bestValue = val;
				bestMove = new Point(move);
			}
			if(state.pieces[move.x][move.y] == 0 && val == best) {
				return move;
			}
		}
		*/
		return bestMove;
	}
	
	// Part of alpha-beta pruning algorithm
	private int maxValue(BoardModel state, HashSet<Point> moves, Map<Point, List<Chain>> myChains, Map<Point, List<Chain>> enemyChains, int depth, int maxDepth){
		HashSet<Point> currentRelevantMoves = helper.relevantMoves(state, moves);
		Map<Point, List<Chain>> currentMyChains = helper.addMyChains(state, myChains);
		Map<Point, List<Chain>> currentEnemyChains = helper.addEnemyChains(state, enemyChains);
		if(state.winner() != -1 || depth >= maxDepth || TeamMaybeAI.timesUp(deadline)){
			return eval(state, myChains, enemyChains);
		}
		int value = Integer.MIN_VALUE;
		
		for(Point move:moves){
			BoardModel c = state.clone();
			c = c.placePiece(move, TeamMaybeAI.player);
			value = Math.max(value, minValue(c, currentRelevantMoves, currentMyChains, currentEnemyChains, depth+1, maxDepth));
			if(value >= b){
				return value;
			}
			a = Math.max(a, value);
		}
		//System.out.println("Max Value: " + value);
		return value;
	}
	
	// Part of alpha-beta pruning algorithm
	private int minValue(BoardModel state, HashSet<Point> moves, Map<Point, List<Chain>> myChains, Map<Point, List<Chain>> enemyChains, int depth, int maxDepth) {
		HashSet<Point> currentRelevantMoves = helper.relevantMoves(state, moves);
		Map<Point, List<Chain>> currentMyChains = helper.addMyChains(state, myChains);
		Map<Point, List<Chain>> currentEnemyChains = helper.addEnemyChains(state, enemyChains);
		if(state.winner() != -1 || depth >= maxDepth || TeamMaybeAI.timesUp(deadline)){
			return eval(state, myChains, enemyChains);
		}
		int value = Integer.MAX_VALUE;

		for(Point move:moves){
			BoardModel c = state.clone();
			c = c.placePiece(move, TeamMaybeAI.enemy);
			value = Math.min(value, maxValue(c, currentRelevantMoves, currentMyChains, currentEnemyChains, depth+1, maxDepth));
			if(value <= a){
				return value;
			}
			b = Math.min(b, value);
		}
		//System.out.println("Min Value: " + value);
		return value;
	}
	

	private int eval(BoardModel state, Map<Point, List<Chain>> myChains, Map<Point, List<Chain>> enemyChains) {
		int result = 0;
		for (int k = state.kLength; k > 0; k--) {
			for(List<Chain> l : myChains.values()) {
				for(Chain c : l) {
						result += c.length * 100^k^k;
				}
			}
			for(List<Chain> l : enemyChains.values()) {
				for(Chain c : l) {
					if(c.deadLeft && c.deadRight)
						result += c.length * 100^k^k;
					else if(c.deadLeft || c.deadRight)
						result += c.length * 100^k;
				}
			}
		}
		//System.out.println("Move: (" + state.lastMove.x + "," + state.lastMove.y + ")\tEval: " + result);
		return result;
	}
}
