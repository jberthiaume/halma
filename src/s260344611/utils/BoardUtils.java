package s260344611.utils;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Random;

import boardgame.Board;
import boardgame.Move;
import boardgame.Player;
import halma.*;

public class BoardUtils {
    
    public static Point getGoal(Board board, int playerID) {
    	CCBoard b = (CCBoard) board;    	
    	int goalBaseID = getAlly(playerID);    	
    	
    	HashSet<Point> base = CCBoard.bases[goalBaseID];
    	ArrayList<Point> baseList = new ArrayList<Point>(base); //convert hashset to ArrayList for sorting
		Collections.sort(baseList, new PointCompare(goalBaseID));
    	
		if (b.getTurnsPlayed() < 100) {
	    	for (Point p : baseList) {
	    		if (b.getPieceAt(p) == null) {
	//    			System.out.println("-> Goal: " + p);
	    			return p;
	    		}
	    	} 
		}
    	return baseList.get(0); //default case, just return the corner point
    }    
    
    /**
     *  Comparator adapted from user Paul Croarkin @ StackOverflow.com
     */
    public static class PointCompare implements Comparator<Point> {
    	private int player;
    	
    	public PointCompare() {
    		this.player = 0;
    	}
    	public PointCompare(int playerID) {
    		this.player = playerID;
    	}
    	
        public int compare(Point a, Point b) {
        	if (player == 1 || player == 3) {
        		// left-side players: sort with max x first
        		if (a.x < b.x) {
                    return 1;
                }
                else if (a.x > b.x) {
                    return -1;
                }
                else {
                	if (player == 1) {
                		return (a.y < b.y) ? -1 : ((a.y == b.y) ? 0 : 1);
                	}
                	else {
                		return (a.y < b.y) ? 1 : ((a.y == b.y) ? 0 : -1);
                	}
                }
        	}
        	else {
        		// right-side players: sort with max x first
        		if (a.x < b.x) {
                    return -1;
                }
                else if (a.x > b.x) {
                    return 1;
                }
                else {
                	if (player == 2) {
                		return (a.y < b.y) ? 1 : ((a.y == b.y) ? 0 : -1);
                	}
                	else {
                		return (a.y < b.y) ? -1 : ((a.y == b.y) ? 0 : 1);
                	}
                }
        	}
            
        }
    }
    
    public static int computeDistance(Point a, Point b) {
    	return Math.max( Math.abs(b.x-a.x), Math.abs(b.y-a.y) );    	
    }
    
    public static int getAlly(int playerID) {
    	switch(playerID) {
    	case 0:
    		return 3;
    	case 1:
    		return 2;
    	case 2:
    		return 1;
    	case 3:
    		return 0;
    	default:
    		return -1;
    	}
    }
    
}
