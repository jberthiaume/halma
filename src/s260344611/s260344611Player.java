package s260344611;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import boardgame.Board;
import boardgame.Move;
import boardgame.Player;
import halma.*;
import s260344611.utils.BoardUtils;
import s260344611.utils.GenericTreeNode;


public class s260344611Player extends Player {
	
    private ArrayList<CCMove> moveSequence = new ArrayList<CCMove>();
    private boolean inMoveSequence = true;
    private boolean initialized = false;
    
    private static int bestMoveVal = 0;
    Random rand = new Random();
    
    public s260344611Player() { 
    	super("s260344611"); 
    }
    public s260344611Player(String s) { 
    	super(s); 
    }
    
    public Board createBoard() { return new CCBoard(); }

    public Move chooseMove(Board board) {
    	if (!initialized) {
        	moveSequence = setStartingSequence(playerID);
        	BoardUtils.initPlayerBases();
        	initialized = true;
    	}    	
    	
    	try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	bestMoveVal = 0;
    	CCMove selectedMove;      	
        CCBoard b = (CCBoard) board;
       
		while(inMoveSequence) {
			//loop until a move is selected or the move queue is empty	    		
    		selectedMove = moveSequence.get(0);
    		moveSequence.remove(0);
    		if (moveSequence.isEmpty()) {
    			inMoveSequence = false;
    		}
    		System.out.println(selectedMove.toPrettyString());
    		if (b.isLegal(selectedMove)) {
    			return selectedMove;
    		}
		}
        
        Point goal = BoardUtils.getGoal(board, playerID);
//        System.out.println("\n-> Goal: " + goal);
        
    	ArrayList<Point> visited = new ArrayList<Point>();
        GenericTreeNode<CCMove> moveTree = new GenericTreeNode<CCMove>(new CCMove(playerID, null, null), null, 0);    
        ArrayList< GenericTreeNode<CCMove> > bestMoves = new ArrayList< GenericTreeNode<CCMove> >();
        
        ArrayList<Point> pieces = b.getPieces(playerID);
        for (Point p : pieces) {
        	visited.clear();
        	getAllMovesForPiece(b, moveTree, 
        						visited, bestMoves, 
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
        	inMoveSequence = true;
        
        return selectedMove;      
    }
    
    public static void getAllMovesForPiece(Board board, GenericTreeNode<CCMove> moveTreeNode, 
    									   ArrayList<Point> visited, ArrayList< GenericTreeNode<CCMove> > bestMoves, 
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
	    		//explore this move
				CCBoard newBoard = (CCBoard) b.clone();
				newBoard.move(move);
    			
    			if (dest != null) {
    				val = BoardUtils.computeMoveValue(newBoard, move, goal, playerID);
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
	    		
				moveTreeNode.addChild(newMove);
				
				//recursion step
				getAllMovesForPiece(newBoard, newMove, 
									visited, bestMoves, 
									dest, goal, playerID);				
    		} 		
    	}
    }    

    
    public static ArrayList<CCMove> setStartingSequence(int playerID) {
    	ArrayList<CCMove> m = new ArrayList<CCMove>();    
    	String[] sequence = BoardUtils.getStartSequence();
    	String moveString;
    	for (String s : sequence) {
    		moveString = playerID + " " + s;
    		m.add(BoardUtils.convertToPlayer( new CCMove(moveString) ));
    	}   	
    	return m;
    }
    
    public static ArrayList<CCMove> getMoveSequence(GenericTreeNode<CCMove> moveTreeNode, int playerID) {
    	
    	CCMove move;
    	ArrayList<CCMove> moveSeq = new ArrayList<CCMove>();
    	moveSeq.add(moveTreeNode.getData());
    	
    	GenericTreeNode<CCMove> parent = moveTreeNode.getParent();
    	while (parent != null) {
    		moveTreeNode = parent;
    		move = moveTreeNode.getData();
    		if (move.getTo() != null)
    			moveSeq.add(0, move); //insert the parent node
    		else
    			moveSeq.add(move); 	//always put the "end turn" node at the end
    		parent = moveTreeNode.getParent();
    	}
    	return moveSeq;
    }
    
}
