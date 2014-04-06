package s260344611;

import java.awt.Point;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;

import boardgame.Board;
import boardgame.Move;
import boardgame.Player;
import halma.*;
import s260344611.utils.BoardUtils;
import s260344611.utils.GenericTreeNode;
import sun.reflect.generics.tree.Tree;


public class s260344611Player extends Player {
	
	// weighting constants for evaluation function
	private static final int CONST_A = 8; 	//difference between start and end locations
	private static final int CONST_B = 1;	//distance from goal at start of move
	private static final int CONST_C = 4;	//distance from center of board (after move)
	private static final int CONST_D = 10;	//distance from edge (before move)

	
    private boolean verbose = false;
    private ArrayList<CCMove> moveSequence = new ArrayList<CCMove>();
    private boolean inSeq = false;
    
    private static int bestMoveVal = 0;
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
    public Move chooseMove(Board board) {
    	bestMoveVal = 0;
    	CCMove selectedMove;      	
        CCBoard b = (CCBoard) board;
        
    	if (inSeq) {
    		selectedMove = moveSequence.get(0);
    		moveSequence.remove(0);
    		if (moveSequence.isEmpty()) {
    			inSeq = false;
    		}
    		if (b.isLegal(selectedMove)) {
    			return selectedMove;
    		}
    	}  
        
        Point goal = BoardUtils.getGoal(board, playerID);
        System.out.println("\n-> Goal: " + goal);
        
    	ArrayList<Point> visited = new ArrayList<Point>();
        GenericTreeNode<CCMove> moveTree = new GenericTreeNode<CCMove>(new CCMove(playerID, null, null), null, 0);    
        ArrayList< GenericTreeNode<CCMove> > bestMoves = new ArrayList< GenericTreeNode<CCMove> >();
        
        ArrayList<Point> pieces = b.getPieces(playerID);
        for (Point p : pieces) {
        	visited.clear();
        	getAllMovesForPiece(moveTree, visited, 
        						bestMoves, b, 
        						p, goal, playerID);
        }
        
        System.out.println("\n----------\nBest moves (value:" + bestMoveVal + ")\n----------");
        for (GenericTreeNode<CCMove> p : bestMoves) {
        	System.out.println(p.getData().toPrettyString());
        }
        System.out.println("----------");
        
        if (bestMoves.isEmpty()) {
        	// every legal move is negative in value: just pick a random one to get "unstuck"        	
        	ArrayList<CCMove> legalMoves =  b.getLegalMoves();
        	return legalMoves.get(rand.nextInt(legalMoves.size()));
        }
        
        GenericTreeNode<CCMove> selectedNode = bestMoves.get(rand.nextInt(bestMoves.size()));
        moveSequence = getMoveSequence(selectedNode, playerID);
        for (CCMove move : moveSequence) {
        	System.out.println("-> Selected Move: " + move.toPrettyString());
        }
        System.out.println("----------\n");
        
        selectedMove = moveSequence.get(0);
        moveSequence.remove(0);
        if (moveSequence.size() > 0) 
        	inSeq = true;
        
        return selectedMove;      
    }
    
    public static void getAllMovesForPiece(GenericTreeNode<CCMove> moveTreeNode, ArrayList<Point> visited, 
    									   ArrayList< GenericTreeNode<CCMove> > bestMoves, Board board, 
    									   Point p, Point goal, int playerID) {
    	CCBoard b = (CCBoard) board;    	
    	ArrayList<CCMove> moves = b.getLegalMoveForPiece(p, playerID); 
    	visited.add(p);
    	
    	Point dest;
    	int val;
    	
    	for (CCMove move : moves) {

        	int currVal = moveTreeNode.getValue();
    		dest = move.getTo();
    		
    		if (!visited.contains(dest)) {
    			if (dest != null) {
    				val = computeMoveValue(b, move, goal, playerID);
    			}
    			else {
    				val = 0;    
    			}	
	    		
    			System.out.print("(" + currVal + ")");
	    		currVal += val;
    			System.out.println(" + " + val + " = " + currVal + ": " + move.toPrettyString());
				GenericTreeNode<CCMove> newMove = new GenericTreeNode<CCMove>(move, moveTreeNode, currVal);
				
	    		if (val < 0) {
	    			//ignore moves with negative value to save time
	    			continue;
	    		}
	    		else if (currVal > bestMoveVal) {
	    			bestMoveVal = currVal;
	    			bestMoves.clear();
	    			bestMoves.add(newMove);
	    		}
	    		else if (currVal == bestMoveVal) {
	    			bestMoves.add(newMove);
	    		}	    		
	    		
	    		//explore this move
				CCBoard newBoard = (CCBoard) b.clone();
				newBoard.move(move);
				moveTreeNode.addChild(newMove);
				getAllMovesForPiece(newMove, visited, 
									bestMoves, newBoard, 
									dest, goal, playerID);
				
    		} 		
    	}
    }    	

    
    public static int computeMoveValue(Board board, CCMove move, Point goal, int playerID) {
    	
    	if (CCBoard.bases[BoardUtils.getAlly(playerID)].contains(move.getFrom())) {
    		return 0; //already in goal area
    	}
    	
        int distBefore = BoardUtils.computeDistance(move.getFrom(), goal);
        int distAfter = BoardUtils.computeDistance(move.getTo(), goal);
        int distCenter = BoardUtils.computeDistanceToCenter(move.getTo(), playerID);   
        int edge = BoardUtils.isOnEdge(move.getFrom());
        int distance = distBefore - distAfter;
//    	System.out.print("Dist: " + distance + " // ");
    	
    	return (distance <= 0) ? (-1) : (CONST_A*distance) +
    									(CONST_B*distBefore) +
    									(CONST_C*distCenter) +
    									(CONST_D*edge);
    }
    
    public static ArrayList<CCMove> getMoveSequence(GenericTreeNode<CCMove> moveTreeNode, int playerID) {
    	
    	CCMove move;
    	ArrayList<CCMove> moveSeq = new ArrayList<CCMove>();
    	moveSeq.add(moveTreeNode.getData());
    	//moveSeq.add(new CCMove(playerID, null, null));
    	
    	GenericTreeNode<CCMove> parent = moveTreeNode.getParent();
    	while (parent != null) {
    		moveTreeNode = parent;
    		move = moveTreeNode.getData();
    		if (move.getTo() != null)
    			moveSeq.add(0, move); //insert the parent node
    		else
    			moveSeq.add(move); 	//put the "end turn" node at the end
    		parent = moveTreeNode.getParent();
    	}
    	return moveSeq;
    }
    
}
