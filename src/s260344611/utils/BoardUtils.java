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
    	
		if (b.getTurnsPlayed() > 100) {
	    	for (Point p : baseList) {
	    		if (b.getPieceAt(p) == null) {
//	    			System.out.println("\n------> Goal: " + p);
	    			return p;
	    		}
	    	} 
		}
    	return baseList.get(0); //default case, just return the corner point
    }    
    
    public static int computeDistance(Point a, Point b) {
    	return Math.abs(b.x-a.x) + Math.abs(b.y-a.y);    	
    }
    
    public static int computeDistanceToCenter(Point a, int playerID) {
    	if (playerID == 0 || playerID == 3) 
    		return Math.abs( (a.y-a.x+1)/2 );
    	else
    		return 8 - Math.abs( (a.y-a.x+1)/2 );
    }
    
    public static int isOnEdge(Point a) {
    	if ((a.x==0 || a.x==15) && (a.y==0 || a.y==15)) {
    		//point is a corner of the board
    		return 2;
    	}
    	else if (a.x==0 || a.x==15 || a.y==0 || a.y==15) {
    		//point is on the edge of the board
    		return 1;
    	}
    	else {
    		//point is not on an edge
    		return 0;
    	}
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
    
}
