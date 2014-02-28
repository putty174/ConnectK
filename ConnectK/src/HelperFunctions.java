import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import connectK.BoardModel;

public class HelperFunctions {
	public HashSet<Point> relevantMoves(BoardModel state, HashSet<Point> current) {
		HashSet<Point> list = new HashSet<Point>(current);
		int x;
		int y;
		
		for(int i = -1; i < 2; i++)
			for(int j = -1; j < 2; j++)
				if(!(i == 0 && j == 0)) {
					x = state.lastMove.x + i;
					y = state.lastMove.y + j;
					if ((x > -1 && x < state.width) && (y > -1 && y < state.height) && state.pieces[x][y] == 0)
						list.add(new Point(x,y));
				}
		return list;
	}
	
	public Map<Point, List<Chain>> addEnemyChains(BoardModel state, Map<Point,List<Chain>> chains) {
		Map<Point, List<Chain>> newChains = new HashMap<Point, List<Chain>>(chains);
		newEnemyChains(state, newChains);
		
		for(Point p : newChains.keySet())	{
			for(Chain c : newChains.get(p)) {
				while(!c.deadLeft && isValid(state, c.left.x, c.left.y)) {
					if(state.pieces[c.left.x][c.left.y] == TeamMaybeAI.enemy)
						continueEnemyLeft(state, c);
					else if(state.pieces[c.left.x][c.left.y] == TeamMaybeAI.player)
						c.deadLeft = true;
				}
				while(!c.deadRight && isValid(state, c.right.x, c.right.y)) {
					if(state.pieces[c.right.x][c.right.y] == TeamMaybeAI.enemy)
						continueEnemyRight(state, c);
					else if(state.pieces[c.right.x][c.right.y] == TeamMaybeAI.player)
						c.deadRight = true;
				}
			}
		}
		return newChains;
	}
	
	public Map<Point, List<Chain>> addMyChains(BoardModel state, Map<Point,List<Chain>> chains) {
		Map<Point, List<Chain>> newChains = new HashMap<Point, List<Chain>>(chains);
		newMyChains(state, newChains);
		
		for(Point p : newChains.keySet()){
			for(Chain c : newChains.get(p)) {
				while(!c.deadLeft && isValid(state, c.left.x, c.left.y)) {
					if(state.pieces[c.left.x][c.left.y] == TeamMaybeAI.player)
						continueMyLeft(state, c);
					else if(state.pieces[c.left.x][c.left.y] == TeamMaybeAI.enemy)
						c.deadLeft = true;
				}
				while(!c.deadRight && isValid(state, c.right.x, c.right.y)) {
					if(state.pieces[c.right.x][c.right.y] == TeamMaybeAI.player)
						continueMyRight(state, c);
					else if(state.pieces[c.right.x][c.right.y] == TeamMaybeAI.enemy)
						c.deadRight = true;
				}
			}
		}
		return newChains;
	}
	
	private void newEnemyChains(BoardModel board, Map<Point,List<Chain>> enemyChains) {
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
			if(isValidHolder(board, x1, y1)) {
				pl = new Point(x1, y1);
				if(isValid(board, x1,y1) && board.pieces[x1][y1] == 0)
					deadLeft = false;
			}
			if(isValidHolder(board, x2, y2)) {
				pr = new Point(x2, y2);
				if(isValid(board, x2,y2) && board.pieces[x2][y2] == 0)
					deadRight = false;
			}
			if(newEnemyChain(board, x1,y1) && newEnemyChain(board, x2,y2))
				chains.add(new Chain(1,pl,pr,deadLeft,deadRight));
		}
		
		deadLeft = true;
		deadRight = true;
		x1 = board.lastMove.x;
		x2 = board.lastMove.x;
		y1 = board.lastMove.y - 1;
		y2 = board.lastMove.y + 1;
		if(isValidHolder(board, x1, y1)) {
			pl = new Point(x1, y1);
			if(isValid(board, x1,y1) && board.pieces[x1][y1] == 0)
				deadLeft = false;
		}
		if(isValidHolder(board, x2, y2)) {
			pr = new Point(x2, y2);
			if(isValid(board, x2,y2) && board.pieces[x2][y2] == 0)
				deadRight = false;
		}
		if(newEnemyChain(board, x1,y1) && newEnemyChain(board, x2,y2))
			chains.add(new Chain(1,pl,pr,deadLeft, deadRight));
		
		enemyChains.put(board.lastMove, chains);
	}
	
	private  void newMyChains(BoardModel board, Map<Point,List<Chain>> myChains) {
		int x1, y1, x2, y2;
		boolean deadLeft = true, deadRight = true;
		Point pl = null, pr = null;
		List<Chain> chains = new ArrayList<Chain>();
		
		for(int j = -1; j < 2; j++) {
			deadLeft = true;
			deadRight = true;
			x1 = board.lastMove.x - 1;
			x2 = board.lastMove.x + 1;
			y1 = board.lastMove.y + j;
			y2 = board.lastMove.y - j;
			if(isValidHolder(board, x1, y1)) {
				pl = new Point(x1, y1);
				if(isValid(board, x1,y1) && board.pieces[x1][y1] == 0)
					deadLeft = false;
			}
			if(isValidHolder(board, x2, y2)) {
				pr = new Point(x2, y2);
				if(isValid(board, x2,y2) && board.pieces[x2][y2] == 0)
					deadRight = false;
			}
			if(newMyChain(board, x1,y1) && newMyChain(board, x2,y2))
				chains.add(new Chain(1,pl,pr,deadLeft,deadRight));
		}
		
		deadLeft = true;
		deadRight = true;
		x1 = board.lastMove.x;
		x2 = board.lastMove.x;
		y1 = board.lastMove.y - 1;
		y2 = board.lastMove.y + 1;
		if(isValidHolder(board, x1, y1)) {
			pl = new Point(x1, y1);
			if(isValid(board, x1,y1) && board.pieces[x1][y1] == 0)
				deadLeft = false;
		}
		if(isValid(board, x1,y1) && isValidHolder(board, x2, y2)) {
			pr = new Point(x2, y2);
			if(isValid(board, x2,y2) && board.pieces[x2][y2] == 0)
				deadRight = false;
		}
		if(newMyChain(board, x1,y1) && newMyChain(board, x2,y2))
			chains.add(new Chain(1,pl,pr,deadLeft,deadRight));
		
		myChains.put(board.lastMove, chains);
	}
	
	private void continueEnemyLeft(BoardModel board, Chain c) {
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
			
			if(isValidEnemy(board, c.left.x, c.left.y))
				c.length++;
			else {
				c.deadLeft = true;
			}
		}
	}
	
	private void continueMyLeft(BoardModel board, Chain c) {
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
			
			if(isValidMe(board, c.left.x, c.left.y))
				c.length++;
			else {
				c.deadLeft = true;
			}
		}
	}
	
	private void continueEnemyRight(BoardModel board, Chain c) {
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
			
			if(isValidEnemy(board, c.right.x, c.right.y))
				c.length++;
			else {
				c.deadRight = true;
			}
		}
	}
	
	private void continueMyRight(BoardModel board, Chain c) {
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
			
			if(isValidMe(board, c.right.x, c.right.y))
				c.length++;
			else {
				c.deadRight = true;
			}
		}
	}
	
	private boolean newEnemyChain(BoardModel board, int x, int y) {
		if(isValid(board, x,y))
			return !(board.pieces[x][y] == TeamMaybeAI.enemy);
		else
			return true;
	}
	
	private boolean newMyChain(BoardModel board, int x, int y) {
		if(isValid(board, x,y))
			return !(board.pieces[x][y] == TeamMaybeAI.player);
		else
			return true;
	}
	
	private boolean isValid(BoardModel board, int x, int y) {
		return (x > -1 && x < board.width) && (y > -1 && y < board.height);
	}
	
	private boolean isValidHolder(BoardModel board, int x, int y) {
		return ((x > -2 && x < board.width + 1) && (y > -2 && y < board.height + 1));
	}
	
	private boolean isValidEnemy(BoardModel board, int x, int y) {
		return (isValid(board, x,y) && (board.pieces[x][y] == 0 || board.pieces[x][y] == TeamMaybeAI.enemy));
	}
	
	private boolean isValidMe(BoardModel board, int x, int y) {
		return (isValid(board, x,y) && (board.pieces[x][y] == 0 || board.pieces[x][y] == TeamMaybeAI.player));
	}
}