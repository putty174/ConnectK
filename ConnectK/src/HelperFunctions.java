import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import connectK.BoardModel;

public class HelperFunctions {
	private HashMap<Integer, ArrayList<Point>> boardMoves = new HashMap<Integer, ArrayList<Point>>();

	public ArrayList<Point> generateRelevantMoves(BoardModel state) {
		ArrayList<Point> rMoves = new ArrayList<Point>();
		if(!boardMoves.containsKey(state.hashCode())){
			if(state.gravityEnabled()){
				for(int i = 0; i < state.getWidth(); i++){
					for(int j = 0; j < state.getHeight(); j++){
						if(state.getSpace(i, j) == 0){
							rMoves.add(new Point(i,j));
							break;
						}
					}
				}
			}
			else{
				for(int i = 0; i < state.getWidth(); i++){
					for(int j = 0; j < state.getHeight(); j++){
						if(state.getSpace(i, j) == 0 &&(
								(i > 0 && state.getSpace(i - 1, j) != 0) ||	// spot in direction 4 is NOT empty
								(i > 0 && j > 0 && state.getSpace(i - 1, j - 1) != 0) || //spot in direction 1 is NOT empty
								(j > 0 && state.getSpace(i, j - 1) != 0) || // spot in direction 2 is NOT empty
								(i < state.getWidth() - 1 && j > 0 && state.getSpace(i + 1, j - 1) != 0) || // spot in direction 3 is NOT empty
								(i < state.getWidth() - 1 && state.getSpace(i + 1, j) != 0) || // spot in direction 6 is NOT empty
								(i < state.getWidth() - 1 && j < state.getHeight() - 1 && state.getSpace(i + 1, j + 1) != 0) || // spot in direction 9 is NOT empty
								(j < state.getHeight() - 1 && state.getSpace(i, j + 1) != 0) || // spot in direction 8 is NOT empty
								(i > 0 && j < state.getHeight() - 1 && state.getSpace(i - 1, j + 1) != 0) // spot in direction 7 is NOT empty
								)){
							rMoves.add(new Point(i,j));
						}
					}
				}
			}
			boardMoves.put(state.hashCode(), rMoves);
			System.out.println(boardMoves.get(state.hashCode()).toString() + " " + boardMoves.size());
			return rMoves;
		}
		else{
			return boardMoves.get(state.hashCode());
		}
	}
	
	public void updateMoveOrdering(ArrayList<PointWrapper> moves, BoardModel state){
		TreeSet<PointWrapper> sorter = new TreeSet<PointWrapper>();
		ArrayList<Point> sorted = new ArrayList<Point>();
		for(PointWrapper move:moves){
			sorter.add(move);
		}
		for(PointWrapper move:sorter){
			sorted.add(move.move);
		}
		for(Point move:sorted){
			boardMoves.get(state.hashCode()).remove(move);
			boardMoves.get(state.hashCode()).add(0, move);
		}
	}
}