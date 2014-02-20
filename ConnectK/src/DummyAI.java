import connectK.CKPlayer;
import connectK.BoardModel;
import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;

public class DummyAI extends CKPlayer {
	private BoardModel board; //The BoardModel state so we don't have to pass this everywhere
	private HashSet<Point> relaventMoves = new HashSet<Point>(); //A list of empty spots 8 way adjacent to already placed pieces 
	private HashMap<Point, Chain>[] chains = new HashMap[2]; //An array of maps for chains. 0 index is me, 1 index is opponent
	
	private long start; //A timer to track when our turn started
	
	//Constructor
	public DummyAI(byte player, BoardModel state) {
		super(player, state);
		teamName = "DummyAI";
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
		Point move;
		board = state;
		addRelaventMoves();
		if(state.lastMove == null)
			move = firstMove(state);
		else
			move = ids(state, 0);
		return makeMove(move);
	}
	
	//If we're first, automatically make this move
	private Point firstMove(BoardModel state) {
		return new Point((state.getWidth() - 1) / 2,(state.getHeight() - 1) / 2);
	}
	
	//Otherwise, think about what mvoe to make
	private Point ids(BoardModel state, int depth) {
		Point move = dumbMove(state);
		
		return move;
	}
	
	//Call whenever to check if we still have time
	private boolean checkTime(long start, int deadline) {
		long end = System.currentTimeMillis();
		return (end - start) < (deadline * 0.95 * 1000);
	}
	
	//Build the list of moves that are 8 way adjacent to tiles already filled
	private void addRelaventMoves() {
		int x;
		int y;
		
		for(int i = -1; i < 2; i++)
			for(int j = -1; j < 2; j++)
				if(i != 0 || j != 0) {
					x = board.lastMove.x + i;
					y = board.lastMove.y + j;
					if (x > -1 && x < board.width && y > -1 && y < board.height && board.pieces[x][y] == 0)
						relaventMoves.add(new Point(x,y));
				}
	}
	
	//Adds chains by both players to memory
	private void addChains() {
		
	}
	
	//Cleans up memory before giving up turn
	private Point makeMove(Point move){
		addChains();
		removeRelaventMoves(move);
		printRelaventMoves();
		return move;
	}
	
	//Removes any already used moves by both players
	private void removeRelaventMoves(Point move) {
		if(relaventMoves.contains(board.lastMove))
			relaventMoves.remove(board.lastMove);
		if(relaventMoves.contains(move))
			relaventMoves.remove(move);
	}
	
	//Prints all relevant moves
	private void printRelaventMoves() {
		System.out.println();
		for(Point p : relaventMoves)
			System.out.println("All relavent moves: " + p.x + ", " + p.y);
	}
	
	//Helper class to track chains
	private class Chain {
		private Point start; //Bottom left most spot where the chain starts
		private Point end; //Top right most sot where the chain ends
		private Point left; //Spot needed to extend chain at Start
		private Point right; //Spot needed to extend chain at End
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
