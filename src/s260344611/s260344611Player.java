package s260344611;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import boardgame.Board;
import boardgame.Move;
import boardgame.Player;
import halma.*;
import s260344611.utils.BoardUtils;


public class s260344611Player extends Player {
    private boolean verbose = false;
    Random rand = new Random();
    
    /** Provide a default public constructor */
    public s260344611Player() { 
    	super("s260344611"); 
    }
    public s260344611Player(String s) { 
    	super(s); 
    }
    
    public Board createBoard() { return new CCBoard(); }

    /** Implement a very stupid way of picking moves */
    public Move chooseMove(Board board) 
    {
        // Cast the arguments to the objects we want to work with
    	boolean firstMove = true;
        CCBoard b = (CCBoard) board;
        ArrayList<CCMove> moves = b.getLegalMoves();
        
        int currMoveVal;
        int bestMoveVal = 0;
        ArrayList<CCMove> bestMoves = new ArrayList<CCMove>();    

        Point goal = BoardUtils.getGoal(board, playerID);
        System.out.println("-> Goal: " + goal);
        for(CCMove move : moves) {
        	
        	try {
				currMoveVal = computeMoveValue(board, move, goal, playerID);
				System.out.println(currMoveVal + ": " + move.toPrettyString());
				if (currMoveVal > bestMoveVal) {
					bestMoveVal = currMoveVal;
					bestMoves.clear();
					bestMoves.add(move);
				}
				else if (currMoveVal == bestMoveVal) {
					bestMoves.add(move);
				}
			} catch (NullPointerException e) {
				firstMove = false;
				//end turn
//				return new CCMove(playerID, null, null);
			}
        }
        
        if (!firstMove && bestMoveVal <= 0) {
        	// not positive moves left, end turn
        	return new CCMove(playerID, null, null);
        }
        
        try {
        	CCMove selectedMove = bestMoves.get(rand.nextInt(bestMoves.size()));
    		System.out.println("Best move (" + bestMoveVal + "): " + selectedMove.toPrettyString());
            return selectedMove;
        } catch (IllegalArgumentException e){
        	return new CCMove(playerID, null, null);
        }
    }
    
    public static int computeMoveValue(Board board, CCMove move, Point goal, int playerID) {
    	
    	if (CCBoard.bases[BoardUtils.getAlly(playerID)].contains(move.getFrom())) {
    		return 0; //already in base
    	}
    	
        int distBefore = BoardUtils.computeDistance(move.getFrom(), goal);
        //System.out.println("-> Dist before: " + distBefore);
        int distAfter = BoardUtils.computeDistance(move.getTo(), goal);    
        int distance = distBefore - distAfter;
    	
    	return (distance <= 0) ? (distance) : (distance * distBefore);
    }
    
}
