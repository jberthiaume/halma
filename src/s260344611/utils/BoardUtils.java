package s260344611.utils;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;

import boardgame.Board;
import halma.*;

public class BoardUtils {	
	
	static boolean init = false;
	
	//hardcoded sequence of opening moves	
	private static final String[] START_SEQ = {
												"(2,0) (4,2)",
												"null null",
												
												"(0,1) (2,3)",
												"null null",
												
												"(4,2) (5,3)",
												
												"(0,0) (2,0)",
												"(2,0) (4,2)",
												"(4,2) (6,4)",
												"null null",
												
												"(2,1) (4,1)",
												"null null",
												
												"(4,1) (4,2)",
												
												"(0,3) (2,1)",
												"(2,1) (4,1)",
												"(4,1) (4,3)",
												"(4,3) (6,3)",
												"(6,3) (6,5)",
												"null null",
												
												"(6,5) (7,5)",
												
												"(1,2) (3,4)",
												"null null",
												
												"(3,4) (4,5)",
												
												"(1,0) (1,2)",
												"(1,2) (3,4)",
												"(3,4) (5,6)",
												"null null",
												
												"(0,2) (2,4)",
												"null null",
												
												"(2,4) (3,4)",
												
												"(1,1) (3,3)",
												"(3,3) (3,5)",
												"(3,5) (5,5)",
												"(5,5) (5,7)",
												"null null",
												
												"(5,7) (6,7)",
												
												"(3,0) (3,2)",
												"(3,2) (5,2)",
												"(5,2) (5,4)",
												"(5,4) (7,4)",
												"(7,4) (7,6)",
												"null null",
												
												"(7,6) (8,6)",
												
												"(2,2) (2,4)",
												"(2,4) (4,4)",
												"(4,4) (4,6)",
												"(4,6) (6,6)",
												"(6,6) (6,8)",
												"null null",
//												
//												"(1,3) (2,4)",
//												
//												"(6,8) (7,8)",
//												
//												"(2,4) (4,4)",
//												"(4,4) (4,6)",
//												"(4,6) (6,6)",
//												"(6,6) (6,8)",
//												"(6,8) (8,8)",
//												"null null",		
												};
	
	// Re-ordered base points
	final static Point[] basePoints={new Point(0,0), 
									 new Point(1,0), new Point(0,1), 
									 new Point(2,0), new Point(0,2),
									 new Point(1,1),
									 new Point(3,0), new Point(0,3),
									 new Point(2,1), new Point(1,2),
									 new Point(3,1), new Point(1,3),
									 new Point(2,2)
									 };
	private static ArrayList<ArrayList<Point>> bases = new ArrayList<ArrayList<Point>>();
	private static ArrayList<Point> reachedGoals = new ArrayList<Point>();

	// weighting constants for evaluation function
	private static final int CONST_A = 8; 	//difference between start and end locations
	private static final int CONST_B = 20;	//distance from goal at start of move
	private static final int CONST_C = 30;	//distance from center of board (after move)
	private static final int CONST_D = 10;	//distance from edge (before move)
	private static final int CONST_E = -30; //stranded pieces
	private static final int CONST_F = 100; //goal area
	
	@SuppressWarnings("unchecked")
	public static void initPlayerBases() {
		if (!init) {
	    	ArrayList<Point> baseList = new ArrayList<Point>();
	    	
	    	for(int i=0; i<4; i++) {
	    		
		    	for(Point p : basePoints) {
		    		baseList.add(convertToPlayer(p,3-i));
		    	}
		    	bases.add(i, (ArrayList<Point>)baseList.clone());
		    	baseList.clear();
	    	}
	    	init = true;
		}
    	return;
	}
    
    public static Point getGoal(Board board, int playerID) {
    	CCBoard b = (CCBoard) board;  
    	ArrayList<Point> playerBase = bases.get(playerID);    	
    	
    	if (b.getTurnsPlayed() > 30) {
	    	for (int i=0; i<playerBase.size(); i++) {
	    		Point p = playerBase.get(i);
	    		if (b.getPieceAt(p) == null) {
	    			return p;
	    		}
	    	} 
		}
    	return playerBase.get(0); //default case, just return the corner point
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
    		return 0;
    	}
    	else if (a.x==0 || a.x==15 || a.y==0 || a.y==15) {
    		//point is on the edge of the board
    		return -1;
    	}
    	else {
    		//point is not on an edge
    		return 0;
    	}
    }
    
    public static int getForwardNeighbors(Board board, Point p, int distance, int playerID) {
    	CCBoard b = (CCBoard) board;
    	int count = 0;
    	
    	int xTarget = distance;
    	int yTarget = distance;

//		System.out.println("\nPOINT: " + (p.x) + "," + (p.y));
    	for(int i=-xTarget; i<=xTarget; i++) {
    		for(int j=-yTarget; j<=yTarget; j++) {
    			int newX = p.x+i;
    			int newY = p.y+j;
    			if (  newX<0 || newX>15 || newY<0 || newY>15 )
    				continue; //ignore the current point
    			if ( (i==j && (playerID==1 || playerID==2)) || (i==-j && (playerID==0 || playerID==3)) )
    				continue;
    			
    			Point newPoint = new Point(newX, newY);
    			if (b.getPieces(playerID).contains(newPoint) ) {
//    				System.out.println("NEIGHBOR AT POINT: " + (newPoint.x) + "," + (newPoint.y));
    				count++;
    			}
    			else {
//    				System.out.println("NOTHING AT POINT: " + (newPoint.x) + "," + (newPoint.y));
    			}
    		}    		
    	}    	
//    	System.out.println("TOTAL NEIGHBORS: " + count + " AT DISTANCE " + distance);
    	return count;
    }
    
    public static int isStranded(Board board, Point p, int playerID) {
    	int distance = 1;
    	while (distance < 4) {
    		if (getForwardNeighbors(board, p, distance, playerID) < 1)
    			distance++;
    		else
    			break;
    	}
    	
    	return distance-1; //return distance to nearest forward neighbor
    }
    
    public static int isInGoalArea(CCMove move, Point goal, int playerID) {
    	int goalArea = 0;
    	Point testPoint;
    	Point from = move.getFrom();
    	Point to = move.getTo();
    	if(bases.get(playerID).contains(from)) {
    		testPoint = convertToPlayerZero(from, playerID);
    		goalArea -=  (6 - (testPoint.x + testPoint.y));
    	} 
    	if (bases.get(playerID).contains(to)){
    		testPoint = convertToPlayerZero(to, playerID);
    		goalArea += (5 - (testPoint.x + testPoint.y));
    	}
    	if (goalArea == 0) {
    		return goalArea;
    	} else {
    		return goalArea;
    	}
    	
    }
    
    public static int computeMoveValue(Board board, CCMove move, Point goal, int playerID) {
    	
    	
        int distBefore = computeDistance(move.getFrom(), goal);
        int distAfter = computeDistance(move.getTo(), goal);
        int distCenter = computeDistanceToCenter(move.getTo(), playerID);   
        //int edge = isOnEdge(move.getTo());
    	int goalArea = isInGoalArea(move, goal, playerID);
        int distance = distBefore - distAfter;
       
        int strandedCount = 0;
        CCBoard b = (CCBoard) board;
        ArrayList<Point> pieces = b.getPieces(playerID);
        for(Point piece : pieces) {
        	strandedCount += isStranded(b, piece, playerID);
        }
       
        
//        System.out.print("A: " + (CONST_A*distance) + 
//        				" B:" + (CONST_B*distBefore) +
//        				" C:" + (CONST_C*distCenter) +
//        				" D:" + (CONST_D*edge) +
//        				" E:" + (CONST_E*strandedCount) +
//        				" F:" + (CONST_F*goalArea) +
//        				" // ");
    	return (distance <= 0) ? (-1) : (CONST_A*distance) +
    									(CONST_B*distBefore) +
    									(CONST_C*distCenter) +
    									//(CONST_D*edge) +
    									(CONST_E*strandedCount) +
    									(CONST_F*goalArea);
    }
    
    public static Point convertToPlayerZero(Point p, int playerID) {
    	switch(playerID) {
    	case 0:
    		return new Point(15-p.x, 15-p.y);
    	case 1:
    		return new Point(p.x, 15-p.y);
    	case 2:
    		return new Point(15-p.x, p.y);
    	case 3:
    		return p;
		default:
			return p;
    	}
    }
    
       
    public static Point convertToPlayer(Point p, int playerID) {
    	switch(playerID) {
    	case 0:
    		return p;
    	case 1:
    		return new Point(15-p.x, p.y);
    	case 2:
    		return new Point(p.x, 15-p.y);
    	case 3:
    		return new Point(15-p.x, 15-p.y);
		default:
			return p;
    	}
    }
    
    // converts a move from player 0 to the same point relative to another starting position
    public static CCMove convertToPlayer(CCMove move) {
    	int playerID = move.getPlayerID();
    	
    	if (move.getFrom() == null && move.getTo() == null) {
    		return new CCMove(playerID, null, null);
    	}
    	
    	return new CCMove(playerID, 
						  convertToPlayer(move.getFrom(), playerID),	
						  convertToPlayer(move.getTo(), playerID));   	
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
    
    public static String[] getStartSequence() {
    	return START_SEQ;
    }
    
}
