import connectK.CKPlayer;
import connectK.BoardModel;
import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;

public class DummyAI extends CKPlayer {
	private BoardModel board;
	private HashSet<Point> relaventMoves = new HashSet<Point>();
	private HashMap<Point, Chain>[] chains = new HashMap[2]; 
	
	public DummyAI(byte player, BoardModel state) {
		super(player, state);
		teamName = "DummyAI";
	}

	@Override
	public Point getMove(BoardModel state) {
		return getMove(state, 9999);
	}

	@Override
	public Point getMove(BoardModel state, int deadline) {
		long start = System.currentTimeMillis();
		Point move;
		board = state;
		addRelaventMoves();
		if(state.lastMove == null)
			move = firstMove(state);
		else
			move = ids(state, 0);
		return makeMove(move);
	}
	
	private Point firstMove(BoardModel state) {
		return new Point((state.getWidth() - 1) / 2,(state.getHeight() - 1) / 2);
	}
	
	private Point ids(BoardModel state, int depth) {
		Point move = dumbMove(state);
		
		return move;
	}
	
	private boolean checkTime(long start, int deadline) {
		long end = System.currentTimeMillis();
		return (end - start) < (deadline * 0.95 * 1000);
	}
	
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
	
	private Point dumbMove(BoardModel state) {
		for(int i = 0; i < state.width; i++)
			for(int j = 0; j < state.height; j++)
				if(state.pieces[i][j] == 0)
					return new Point(i,j);
		return null;
	}
	
	private void addChains() {
		
	}
	
	private Point makeMove(Point move){
		addChains();
		removeRelaventMoves(move);
		printRelaventMoves();
		return move;
	}
	
	private void removeRelaventMoves(Point move) {
		if(relaventMoves.contains(board.lastMove))
			relaventMoves.remove(board.lastMove);
		if(relaventMoves.contains(move))
			relaventMoves.remove(move);
	}
	
	private void printRelaventMoves() {
		System.out.println();
		for(Point p : relaventMoves)
			System.out.println("All relavent moves: " + p.x + ", " + p.y);
	}
	
	private class Chain {
		private Point start;
		private Point end;
		private Point left;
		private Point right;
	}
}
