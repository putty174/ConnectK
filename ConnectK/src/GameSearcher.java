import java.awt.Point;
import java.util.ArrayList;
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
	int maxDepth;
	
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
		this.maxDepth = maxDepth;
		System.out.println("maxDepth: " + maxDepth);
		/*
		 * For each move we can make, see what the WORST-CASE scenario is.
		 * In the end, we define our BEST-CASE move as the one with the best WORST-CASE.
		 * The board is cloned; the move is made on the cloned board; THEN the cloned board is passed down.
		 */
		/*
		// DEBUG
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
		*/
		// END DEBUG
		for(Point move:moves){

			a = Integer.MIN_VALUE;
			b = Integer.MAX_VALUE;
			BoardModel c = state.placePiece(move,  TeamMaybeAI.player);	
			int thisMoveValue = minValue(c, moves, myChains, enemyChains, depth);
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
	private int maxValue(BoardModel state, HashSet<Point> moves, Map<Point, List<Chain>> myChains, Map<Point, List<Chain>> enemyChains, int depth){
		HashSet<Point> currentRelevantMoves = helper.relevantMoves(state, moves);
		Map<Point, List<Chain>> currentEnemyChains = helper.addEnemyChains(state, enemyChains);
		if(state.winner() != -1 || depth >= maxDepth || TeamMaybeAI.timesUp(deadline)){
			return eval(state);
		}
		int value = Integer.MIN_VALUE;
		
		for(Point move:moves){
			BoardModel c = state.placePiece(move, TeamMaybeAI.player);
			value = Math.max(value, minValue(c, currentRelevantMoves, myChains, currentEnemyChains, depth + 1));
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
	private int minValue(BoardModel state, HashSet<Point> moves, Map<Point, List<Chain>> myChains, Map<Point, List<Chain>> enemyChains, int depth) {
		HashSet<Point> currentRelevantMoves = helper.relevantMoves(state, moves);
		Map<Point, List<Chain>> currentMyChains = helper.addMyChains(state, myChains);
		if(state.winner() != -1 || depth >= maxDepth || TeamMaybeAI.timesUp(deadline)){
			return eval(state);
		}
		int value = Integer.MAX_VALUE;

		for(Point move:moves){
			BoardModel c = state.placePiece(move, TeamMaybeAI.enemy);
			value = Math.min(value, maxValue(c, currentRelevantMoves, currentMyChains, enemyChains, depth + 1));
			if(value <= a){
				return value;
			}
			b = Math.min(b, value);
		}
		return value;
	}
	
/*
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
	*/
	private int eval(BoardModel state){
		int result = 0;
		ArrayList<HashSet<Point>> ourChains = new ArrayList<HashSet<Point>>();
		ArrayList<HashSet<Point>> enemyChains = new ArrayList<HashSet<Point>>();
		for(int i = 0; i < state.getWidth(); i++){
			for(int j = 0; j < state.getHeight(); j++){
				if(state.getSpace(i,j) == TeamMaybeAI.player){
					ArrayList<HashSet<Point>> chains = generateChains(i, j, state, TeamMaybeAI.player);
					for(HashSet<Point> chain:chains){
						ourChains.add(chain);
					}
				}
				else if(state.getSpace(i,j) == TeamMaybeAI.enemy){
					ArrayList<HashSet<Point>> chains = generateChains(i, j, state, TeamMaybeAI.enemy);
					for(HashSet<Point> chain:chains){
						enemyChains.add(chain);
					}
				}
			}
		}
		for(HashSet<Point> chain:ourChains){
			if(chain.size() > state.getkLength() - 3){
				result += chain.size()^2;
			}
		}
		for(HashSet<Point> chain:enemyChains){
			if(chain.size() > state.getkLength() - 3){
				result -= chain.size()^2;
			}		}
		return result;
	}
	
	private ArrayList<HashSet<Point>> generateChains(int i, int j, BoardModel state, byte player){
		int x = i;
		int y = j;
		HashSet<Point> chain73 = new HashSet<Point>();
		HashSet<Point> chain46 = new HashSet<Point>();
		HashSet<Point> chain19 = new HashSet<Point>();
		HashSet<Point> chain28 = new HashSet<Point>();
		chain73.add(new Point(x,y));
		chain46.add(new Point(x,y));
		chain19.add(new Point(x,y));
		chain28.add(new Point(x,y));
		
		byte thisPlayer = player;
		// Direction 7 and 3 chain
		while(x > 0 && y < state.height - 1 && state.getSpace(x-1, y+1) == thisPlayer){
			chain73.add(new Point(x-1, y+1));
			x--;
			y++;
		}
		x = i;
		y = j;
		while(x < state.width - 1 && y > 0 && state.getSpace(x+1, y-1) == thisPlayer){
			chain73.add(new Point(x+1, y-1));
			x++;
			y--;
		}
		
		// Direction 4 and 6 chain
		x = i;
		y = j;
		while(x > 0 && state.getSpace(x-1, y) == thisPlayer){
			chain46.add(new Point(x-1, y));
			x--;
		}
		x = i;
		while(x < state.width - 1 && state.getSpace(x+1, y) == thisPlayer){
			chain46.add(new Point(x+1, y));
			x++;
		}
		
		
		// Direction 1 and 9 chain
		x = i;
		y = j;
		while(x > 0 && y > 0 && state.getSpace(x-1, y-1) == thisPlayer){
			chain19.add(new Point(x-1, y-1));
			x--;
			y--;
		}
		x = i;
		y = j;
		while(x < state.width - 1 && y < state.height - 1 && state.getSpace(x+1, y+1) == thisPlayer){
			chain19.add(new Point(x+1, y+1));
			x++;
			y++;
		}
		/*
		System.out.println("Chains found in this evaluation:");
		System.out.println(chain73.toString());
		System.out.println(chain46.toString());

		System.out.println(chain19.toString());

		System.out.println(chain28.toString());
*/
		// Direction 2 and 8 chain
		x = i;
		y = j;
		while(y > 0 && state.getSpace(x, y-1) == thisPlayer){
			chain28.add(new Point(x, y-1));
			y--;
		}
		y = j;
		while(y < state.height - 1 && state.getSpace(x, y+1) == thisPlayer){
			chain28.add(new Point(x, y+1));
			y++;
		}
		
		ArrayList<HashSet<Point>> chains = new ArrayList<HashSet<Point>>();
		chains.add(chain73);
		chains.add(chain46);
		chains.add(chain19);
		chains.add(chain28);
		return chains;
	}
}
