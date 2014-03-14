import connectK.CKPlayer;
import connectK.BoardModel;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TeamMaybeAI extends CKPlayer {
	public static  byte player;
	public static  byte enemy;
	private BoardModel board; //The BoardModel state so we don't have to pass this everywhere
	private TreeMap<Integer, BoardModel> relaventMoves = new TreeMap<Integer, BoardModel>(); //A list of empty spots 8 way adjacent to already placed pieces

	
	private Map<Point, List<Chain>> myChains = new HashMap<Point, List<Chain>>(); //An array of maps for our chains
	private Map<Point, List<Chain>> enemyChains = new HashMap<Point, List<Chain>>(); //An array of maps for enemy chains

	private HashSet<Point> futureRelaventMoves = new HashSet<Point>();

	private Point move; //The move we want to make
	
	private static long start; //A timer to track when our turn started
	
	private GameSearcher GS = new GameSearcher();
	
	//Constructor
	public TeamMaybeAI(byte p, BoardModel state) {
		super(p, state);
		teamName = "Team Maybe";
		player = p;
		if(player == 1)
			enemy = 2;
		else
			enemy = 1;
	}
	
	//"main" decision function that doesn't have a deadline
	@Override
	public Point getMove(BoardModel state) {
		return getMove(state, 9999);
	}
	
	//"main decision function that does have a deadline
	/*
	 * Begins by taking timer, then using iterative deepening search
	 * until deadline becomes true. If the deadline is reached while searching,
	 * the any results found in the current IDS iteration are discarded in favour of
	 * the results from the previous iteration.
	 */
	@Override
	public Point getMove(BoardModel state, int deadline) {
		start = System.currentTimeMillis();
		board = state;
		HelperFunctions helper = new HelperFunctions();
		if(state.lastMove == null)
			move = firstMove(state);
		else {
			readBoard();
			int maxDepth = 0;
			Point tempMove;
			while(!timesUp(deadline)){
				maxDepth++;
				tempMove = GS.alphaBetaSearch(board, relaventMoves, deadline, maxDepth, helper);
				if(tempMove != null){
					move = tempMove;
				}
				else{
					//System.out.println("DEADLINE");
				}
			}
		}
		return makeMove();
	}
	
	//If we're first, automatically make this move closest to the centre of the board.
	private Point firstMove(BoardModel state) {
		return new Point((state.getWidth() - 1) / 2,(state.getHeight() - 1) / 2);
	}
	
	
	private HashSet<Point> readBoard() {
		addEnemyRelaventMoves();
		addEnemyChains();
		return futureRelaventMoves;
	}
	
	//Build the enemy list of moves that are 8 way adjacent to tiles already filled
	private void addEnemyRelaventMoves() {
		int x;
		int y;
		
		for(int i = -1; i < 2; i++)
			for(int j = -1; j < 2; j++)
				if(!(i == 0 && j == 0)) {
					x = board.lastMove.x + i;
					y = board.lastMove.y + j;
					if (isEmpty(x,y))
						relaventMoves.put(0,board.clone().placePiece(new Point(x, y),enemy));
				}
	}
	
	private void addMyRelaventMoves() {
		int x;
		int y;
		
		for(int i = -1; i < 2; i++)
			for(int j = -1; j < 2; j++)
				if(!(i == 0 && j == 0)) {
					x = move.x + i;
					y = move.y + j;
					if (isEmpty(x,y))
						relaventMoves.put(0,board.clone().placePiece(new Point(x, y),player));
				}
	}
	
	//Removes any already used moves by both players
		private void removeIrrelaventMoves() {
			if(relaventMoves.containsValue(board.lastMove))
				relaventMoves.remove(board.lastMove);
			if(relaventMoves.containsValue(move))
				relaventMoves.remove(move);
		}
	
	private void addEnemyChains() {
		newEnemyChains();
		
		for(Point p : enemyChains.keySet())	{
			for(Chain c : enemyChains.get(p)) {
				while(!c.deadLeft && isValid(c.left.x, c.left.y) && board.pieces[c.left.x][c.left.y] == enemy)
					continueEnemyLeft(c);
				while(!c.deadRight && isValid(c.right.x, c.right.y) && board.pieces[c.right.x][c.right.y] == enemy)
					continueEnemyRight(c);
			}
		}
	}
	
	private void addMyChains() {
		newMyChains();
		
		for(Point p : myChains.keySet())	{
			for(Chain c : myChains.get(p)) {
				while(!c.deadLeft && isValid(c.left.x, c.left.y) && board.pieces[c.left.x][c.left.y] == player)
					continueMyLeft(c);
				while(!c.deadRight && isValid(c.right.x, c.right.y) && board.pieces[c.right.x][c.right.y] == player)
					continueMyRight(c);
			}
		}
	}

	private void continueEnemyLeft(Chain c) {
		if(!c.deadLeft) {
			int dx = c.right.x - c.left.x;
			int dy = c.right.y - c.left.y;
			
			if(dx < 0)
				c.left.x++;
			else if(dx > 0)
				c.left.x--;
			if(dy < 0)
				c.left.y++;
			else if(dy > 0)
				c.left.y--;
			
			if(isValidEnemy(c.left.x, c.left.y))
				c.length++;
			else {
				c.deadLeft = true;
			}
		}
	}
	
	private void continueMyLeft(Chain c) {
		if(!c.deadLeft) {
			int dx = c.right.x - c.left.x;
			int dy = c.right.y - c.left.y;
			
			if(dx < 0)
				c.left.x++;
			else if(dx > 0)
				c.left.x--;
			if(dy < 0)
				c.left.y++;
			else if(dy > 0)
				c.left.y--;
			
			if(isValidMe(c.left.x, c.left.y))
				c.length++;
			else {
				c.deadLeft = true;
			}
		}
	}
	
	private void continueEnemyRight(Chain c) {
		if(!c.deadRight) {
			int dx = c.right.x - c.left.x;
			int dy = c.right.y - c.left.y;
			
			if(dx < 0)
				c.right.x--;
			else if(dx > 0)
				c.right.x++;
			if(dy < 0)
				c.right.y--;
			else if(dy > 0)
				c.right.y++;
			
			if(isValidEnemy(c.right.x, c.right.y))
				c.length++;
			else {
				c.deadRight = true;
			}
		}
	}
	
	private void continueMyRight(Chain c) {
		if(!c.deadRight) {
			int dx = c.right.x - c.left.x;
			int dy = c.right.y - c.left.y;
			
			if(dx < 0)
				c.right.x--;
			else if(dx > 0)
				c.right.x++;
			if(dy < 0)
				c.right.y--;
			else if(dy > 0)
				c.right.y++;
			
			if(isValidMe(c.right.x, c.right.y))
				c.length++;
			else {
				c.deadRight = true;
			}
		}
	}

	private void newEnemyChains() {
		int x1, y1, x2, y2;
		boolean deadLeft, deadRight;
		Point pl = null, pr = null;
		List<Chain> chains = new ArrayList<Chain>();
		
		for(int j = -1; j < 2; j++) {
			deadLeft = true;
			deadRight = true;
			x1 = board.lastMove.x - 1;
			x2 = board.lastMove.x + 1;
			y1 = board.lastMove.y + j;
			y2 = board.lastMove.y - j;
			if(isValidHolder(x1, y1)) {
				pl = new Point(x1, y1);
				if(isValid(x1,y1) && board.pieces[x1][y1] == 0)
					deadLeft = false;
			}
			if(isValidHolder(x2, y2)) {
				pr = new Point(x2, y2);
				if(isValid(x2,y2) && board.pieces[x2][y2] == 0)
					deadRight = false;
			}
			if(newEnemyChain(x1,y1) && newEnemyChain(x2,y2))
				chains.add(new Chain(1,pl,pr,deadLeft,deadRight));
		}
		
		deadLeft = true;
		deadRight = true;
		x1 = board.lastMove.x;
		x2 = board.lastMove.x;
		y1 = board.lastMove.y - 1;
		y2 = board.lastMove.y + 1;
		if(isValidHolder(x1, y1)) {
			pl = new Point(x1, y1);
			if(isValid(x1,y1) && board.pieces[x1][y1] == 0)
				deadLeft = false;
		}
		if(isValidHolder(x2, y2)) {
			pr = new Point(x2, y2);
			if(isValid(x2,y2) && board.pieces[x2][y2] == 0)
				deadRight = false;
		}
		if(newEnemyChain(x1,y1) && newEnemyChain(x2,y2))
			chains.add(new Chain(1,pl,pr,deadLeft, deadRight));
		
		enemyChains.put(board.lastMove, chains);
	}
	
	//Adds chains by both players to memory
		private void newMyChains() {
			int x1, y1, x2, y2;
			boolean deadLeft = true, deadRight = true;
			Point pl = null, pr = null;
			List<Chain> chains = new ArrayList<Chain>();
			
			for(int j = -1; j < 2; j++) {
				deadLeft = true;
				deadRight = true;
				x1 = move.x - 1;
				x2 = move.x + 1;
				y1 = move.y + j;
				y2 = move.y - j;
				if(isValidHolder(x1, y1)) {
					pl = new Point(x1, y1);
					if(isValid(x1,y1) && board.pieces[x1][y1] == 0)
						deadLeft = false;
				}
				if(isValidHolder(x2, y2)) {
					pr = new Point(x2, y2);
					if(isValid(x2,y2) && board.pieces[x2][y2] == 0)
						deadRight = false;
				}
				if(newMyChain(x1,y1) && newMyChain(x2,y2))
					chains.add(new Chain(1,pl,pr,deadLeft,deadRight));
			}
			
			deadLeft = true;
			deadRight = true;
			x1 = move.x;
			x2 = move.x;
			y1 = move.y - 1;
			y2 = move.y + 1;
			if(isValidHolder(x1, y1)) {
				pl = new Point(x1, y1);
				if(isValid(x1,y1) && board.pieces[x1][y1] == 0)
					deadLeft = false;
			}
			if(isValid(x1,y1) && isValidHolder(x2, y2)) {
				pr = new Point(x2, y2);
				if(isValid(x2,y2) && board.pieces[x2][y2] == 0)
					deadRight = false;
			}
			if(newMyChain(x1,y1) && newMyChain(x2,y2))
				chains.add(new Chain(1,pl,pr,deadLeft,deadRight));
			
			myChains.put(board.lastMove, chains);
		}
	
	private boolean newEnemyChain(int x, int y) {
		if(isValid(x,y))
			return !(board.pieces[x][y] == enemy);
		else
			return true;
	}
	
	private boolean newMyChain(int x, int y) {
		if(isValid(x,y))
			return !(board.pieces[x][y] == player);
		else
			return true;
	}
	
	//Cleans up memory before giving up turn
	private Point makeMove(){
		addMyRelaventMoves();
		addMyChains();
		removeIrrelaventMoves();
		return move;
	}
	
	private boolean isValid(int x, int y) {
		return (x > -1 && x < board.width) && (y > -1 && y < board.height);
	}
	
	private boolean isEmpty(int x, int y) {
		return (isValid(x,y) && board.pieces[x][y] == 0);
	}
	
	private boolean isValidHolder(int x, int y) {
		return ((x > -2 && x < board.width + 1) && (y > -2 && y < board.height + 1));
	}
	
	private boolean isValidEnemy(int x, int y) {
		return (isValid(x,y) && (board.pieces[x][y] == 0 || board.pieces[x][y] == enemy));
	}
	
	private boolean isValidMe(int x, int y) {
		return (isValid(x,y) && (board.pieces[x][y] == 0 || board.pieces[x][y] == player));
	}
	
	//Prints all relevant moves
	public static boolean timesUp(int deadline) {
		return (deadline * 0.8 < System.currentTimeMillis() - start);
	}
}
