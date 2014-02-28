import java.awt.Point;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import connectK.BoardModel;

public class GameSearcher {
	HelperFunctions helper;
	long start;
	int deadline;
	Point bestMove; //current best move
	int bestValue = Integer.MIN_VALUE;
	int a; // alpha
	int b; // beta
	
	
	public GameSearcher(){
		helper = new HelperFunctions();
	}
	/*
	 * alphaBetaSearch() returns the point corresponding to the highest minmax algorithm value 
	 * found via a depth-limited search alpha-beta pruning and some given evaluation function.
	 */
	public Point alphaBetaSearch(BoardModel state, int deadline, HashSet<Point> moves, Map<Point, List<Chain>> myChains, Map<Point, List<Chain>> enemyChains, int maxDepth){
		int depth = 0;
		this.deadline = deadline;
		int best = Integer.MIN_VALUE;
		depth = 0;
		System.out.println("Depth: " + maxDepth);
		/*
		 * For each move we can make, see what the WORST-CASE scenario is.
		 * In the end, we define our BEST-CASE move as the one with the best WORST-CASE.
		 * The board is cloned; the move is made on the cloned board; THEN the cloned board is passed down.
		 */
		System.out.println("AI CHAINS:");
		for(List<Chain> chain:myChains.values()){
			for(Chain c:chain){
				System.out.println(c.toString());
			}
		}
		System.out.println("ENEMY CHAINS:");
		for(List<Chain> chain:enemyChains.values()){
			for(Chain c:chain){
				System.out.println(c.toString());
			}
		}
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
		return bestMove;
	}
	
	/*
	 * maxValue is used for moves made from our perspective.
	 * We have received a cloned board from some higher call with the enemy's move already made.
	 * First, we update relevant moves and chains; then, we do terminal tests and evaluate if
	 * the state is a leaf node (maximum depth or game-over).
	 */
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
		return value;
	}
	
	/*
	 * Essentially identical to maxValue, except inverse, as it is from the
	 * opponent's perspective.
	 */
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
		return value;
	}
	

	private int eval(BoardModel state, Map<Point, List<Chain>> myChains, Map<Point, List<Chain>> enemyChains) {
		int result = 0;
		for (int k = state.kLength; k > 0; k--) {
			for(List<Chain> l : myChains.values()) {
				for(Chain c : l) {
						result += c.length * 2^k^k;
				}
			}
			for(List<Chain> l : enemyChains.values()) {
				for(Chain c : l) {
					if(c.deadLeft && c.deadRight)
						result += c.length * 2^k^k;
					else if(c.deadLeft || c.deadRight)
						result += c.length * 2^k;
				}
			}
		}
		//System.out.println("Move: (" + state.lastMove.x + "," + state.lastMove.y + ")\tEval: " + result);
		return result;
	}
}
