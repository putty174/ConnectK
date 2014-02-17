import connectK.CKPlayer;
import connectK.BoardModel;
import java.awt.Point;

public class DummyAI extends CKPlayer {

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
		if(state.lastMove == null)
			return firstMove(state);
		else
		{
			return ids(0);
		}
	}
	
	private Point firstMove(BoardModel state) {
		return new Point((state.getWidth() - 1) / 2,(state.getHeight() - 1) / 2);
	}
	
	private Point ids(int depth) {
		Point move = null;
		
		
		return move;
	}
	
	private boolean checkTime(long start, int deadline) {
		long end = System.currentTimeMillis();
		return (end - start) < (deadline * 0.95 * 1000);
	}
}
