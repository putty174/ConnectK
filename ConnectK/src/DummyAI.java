import connectK.CKPlayer;
import connectK.BoardModel;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class DummyAI extends CKPlayer {
	public static byte player;
	public static byte enemy;
	private BoardModel board; //The BoardModel state so we don't have to pass this everywhere
	private HashSet<Point> relaventMoves = new HashSet<Point>(); //A list of empty spots 8 way adjacent to already placed pieces 
	private Map<Point, List<Chain>> myChains = new HashMap<Point, List<Chain>>(); //An array of maps for our chains
	private Map<Point, List<Chain>> enemyChains = new HashMap<Point, List<Chain>>(); //An array of maps for enemy chains
	
	private BoardModel futureBoard;
	private HashSet<Point> futureRelaventMoves = new HashSet<Point>();
	private Map<Point, List<Chain>> futureMyChains = new HashMap<Point, List<Chain>>();
	private Map<Point, List<Chain>> futureEnemyChains = new HashMap<Point, List<Chain>>();
	private Point move; //The move we want to make
	
	private long start; //A timer to track when our turn started
	
	private GameSearcher GS = new GameSearcher();
	
	//Constructor
	public DummyAI(byte player, BoardModel state) {
		super(player, state);
		teamName = "DummyAI";
		this.player = player;
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
	@Override
	public Point getMove(BoardModel state, int deadline) {
		start = System.currentTimeMillis();
		board = state;
		if(state.lastMove == null)
			move = firstMove(state);
		else {
			readBoard();
			move = GS.alphaBetaSearch(board, relaventMoves);
		}
		return makeMove();
	}
	
	//If we're first, automatically make this move
	private Point firstMove(BoardModel state) {
		return new Point((state.getWidth() - 1) / 2,(state.getHeight() - 1) / 2);
	}
	
	//Otherwise, think about what move to make
	private Point ids(BoardModel state, int depth) {
		Point move = dumbMove(state);
		
		return move;
	}
	
	private HashSet<Point> readBoard() {
		addEnemyRelaventMoves();
		addEnemyChains();
		return futureRelaventMoves;
	}
	
	//Call whenever to check if we still have time
	private boolean checkTime(long start, int deadline) {
		long end = System.currentTimeMillis();
		return (end - start) < (deadline * 0.95 * 1000);
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
					if (isValid(x,y))
						relaventMoves.add(new Point(x,y));
				}
	}
	
	//Removes any already used moves by both players
		private void removeRelaventMoves() {
			if(relaventMoves.contains(board.lastMove))
				relaventMoves.remove(board.lastMove);
			if(relaventMoves.contains(move))
				relaventMoves.remove(move);
		}
	
	private void addEnemyChains() {
		newEnemyChains();
		
		for(Point p : enemyChains.keySet())	{
			for(Chain l : enemyChains.get(p)) {
				if(l.left != null) {
					while(board.pieces[l.left.x][l.left.y] == enemy)
						continueLeft(l);
				}
				if(l.right != null) {
					while(board.pieces[l.right.x][l.right.y] == enemy)
						continueRight(l);
				}
			}
		}
		
	}

	private void continueLeft(Chain l) {
		int dy = l.right.y - l.left.y;
		l.left.x--;
		if(dy < 0)
			l.left.y++;
		else if(dy > 0)
			l.left.y--;
		l.length++;
	}
	
	private void continueRight(Chain l) {
		int dy = l.right.y - l.left.y;
		l.left.x++;
		if(dy < 0)
			l.right.y--;
		else if(dy > 0)
			l.right.y++;
		l.length++;
	}

	private void newEnemyChains() {
		int x1, y1, x2, y2;
		Point pl = null;
		Point pr = null;
		List<Chain> chains = new ArrayList<Chain>();
		for(int i = -1; i < 2; i++)
			for(int j = -1; j < 2; j++)
				if(!(i == 0 && j == 0)) {
					x1 = board.lastMove.x + i;
					x2 = board.lastMove.x - i;
					y1 = board.lastMove.y + j;
					y2 = board.lastMove.y - j;
					if (isValid(x1,y1))
						pl = new Point(x1,y1);
					if (isValid(x2, y2))
						pr = new Point(x2,y2);
					chains.add(new Chain(1,pl, pr));
				}
		enemyChains.put(board.lastMove, chains);
	}
	
	//Adds chains by both players to memory
	private void addChains() {
		
	}
	
	//Cleans up memory before giving up turn
	private Point makeMove(){
		addChains();
		removeRelaventMoves();
		printEnemyChains();
		return move;
	}
	
	private boolean isValid(int x, int y) {
		return ((x > -1 && x < board.width) && (y > -1 && y < board.height) && board.pieces[x][y] == 0);
	}
	
	//Prints all relevant moves
	private void printRelaventMoves() {
		System.out.println();
		for(Point p : relaventMoves)
			System.out.println("All relavent moves: " + p.x + ", " + p.y);
	}
	
	private void printEnemyChains() {
		for(List<Chain> l : enemyChains.values())
			for(Chain c : l)
				System.out.println("Left Point: (" + c.left.x + "," + c.left.y + ") \t Right Point: (" + c.right.x + "," + c.right.y + ") \t Length: " + c.length);
	}
	
	//Until we have an actual AI to test, just choose the far left bottom spot
	private Point dumbMove(BoardModel state) {
		for(int i = 0; i < state.width; i++)
			for(int j = 0; j < state.height; j++)
				if(state.pieces[i][j] == 0)
					return new Point(i,j);
		return null;
	}
}
