package com.webs.itmexicali.colorized.board;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import com.webs.itmexicali.colorized.util.Const;


public class BoardSolver {

	static LinkedList<String> s_path;
	static int s_minMoves;
	
	public static int getOptimalPath(ColorBoard board){
		s_minMoves = Integer.MAX_VALUE;
		s_path = new LinkedList<String>();
		SolvingTree solver = new SolvingTree(board);
		
		long startT = System.currentTimeMillis();
		solver.solveRecursively();
		Const.v(BoardSolver.class.getSimpleName(),"Recursive time: "+(System.currentTimeMillis()-startT));
		solver.printTree();
		
		s_minMoves = Integer.MAX_VALUE;
		s_path = new LinkedList<String>();
		startT = System.currentTimeMillis();
		solver.solveIterativelyDepthFirst();
		Const.v(BoardSolver.class.getSimpleName(),"Iterative DepthFirst time: "+(System.currentTimeMillis()-startT));
		solver.printTree();
		
		s_minMoves = Integer.MAX_VALUE;
		s_path = new LinkedList<String>();
		startT = System.currentTimeMillis();
		solver.solveIterativelyBreadthFirst();
		Const.v(BoardSolver.class.getSimpleName(),"Iterative BreadthFirst time: "+(System.currentTimeMillis()-startT));
		solver.printTree();
		
		return s_minMoves;
	}
}


class SolvingTree{
	ColorBoard m_board = null;
	SolvingTree m_parent = null;
	LinkedList<SolvingTree> m_children = null;
	
	SolvingTree(ColorBoard board){
		m_board = board.clone();
	}
	
	void solveIteratively(){
//		Queue<SolvingTree> collection = new LinkedList<SolvingTree>(); //BREADTH-FIRST SEARCH
//		collection.offer(this);
		Stack<SolvingTree> collection = new Stack<SolvingTree>();// DEPTH-FIRST SEARCH
		collection.push(this);
		SolvingTree node = null;
		while(!collection.isEmpty()){
//			node = collection.poll();
			node = collection.pop();
			int currentColor = node.m_board.getCurrentColor(), i = currentColor;
			boolean finished = true;
			do{
				if(++i >= ColorBoard.NUMBER_OF_COLORS) i = 0;
				
				if(i == currentColor) break;
				
				if(node.m_board.isColorFinished(i))	continue;
				
				finished = false;
				SolvingTree aux = new SolvingTree(node.m_board);
				aux.m_board.colorize(i);
				if(!wasProductiveChange(aux.m_board,m_board) || aux.m_board.getMoves() >= BoardSolver.s_minMoves){
					continue;
				}
				aux.m_parent = node;
//				collection.offer(aux);
				collection.push(aux);
			}while(!node.m_board.allColorsFinished());
			
			if(finished && BoardSolver.s_minMoves >= node.m_board.getMoves()){
				savePath(node);
//				uncomment if we are doing a breadth-first search (queue) to stop
//				at the first ocurrence (it should be the best/shortest path)
//				collection.clear();
			}
		}
	}
	
	void solveIterativelyBreadthFirst(){
		Queue<SolvingTree> collection = new LinkedList<SolvingTree>(); //BREADTH-FIRST SEARCH
		collection.offer(this);
		SolvingTree node = null;
		while(!collection.isEmpty()){
			node = collection.poll();
			int currentColor = node.m_board.getCurrentColor(), i = currentColor;
			boolean finished = true;
			do{
				if(++i >= ColorBoard.NUMBER_OF_COLORS) i = 0;
				
				if(i == currentColor) break;
				
				if(node.m_board.isColorFinished(i))	continue;
				
				finished = false;
				SolvingTree aux = new SolvingTree(node.m_board);
				aux.m_board.colorize(i);
				if(!wasProductiveChange(aux.m_board,m_board) || aux.m_board.getMoves() >= BoardSolver.s_minMoves){
					continue;
				}
				aux.m_parent = node;
				collection.offer(aux);
			}while(!node.m_board.allColorsFinished());
			
			if(finished && BoardSolver.s_minMoves >= node.m_board.getMoves()){
				savePath(node);
				collection.clear();
			}
		}
	}
	
	void solveIterativelyDepthFirst(){
		Stack<SolvingTree> collection = new Stack<SolvingTree>();// DEPTH-FIRST SEARCH
		collection.push(this);
		SolvingTree node = null;
		while(!collection.isEmpty()){
			node = collection.pop();
			int currentColor = node.m_board.getCurrentColor(), i = currentColor;
			boolean finished = true;
			do{
				if(++i >= ColorBoard.NUMBER_OF_COLORS) i = 0;
				
				if(i == currentColor) break;
				
				if(node.m_board.isColorFinished(i))	continue;
				
				finished = false;
				SolvingTree aux = new SolvingTree(node.m_board);
				aux.m_board.colorize(i);
				if(!wasProductiveChange(aux.m_board,m_board) || aux.m_board.getMoves() >= BoardSolver.s_minMoves){
					continue;
				}
				aux.m_parent = node;
				collection.push(aux);
			}while(!node.m_board.allColorsFinished());
			
			if(finished && BoardSolver.s_minMoves >= node.m_board.getMoves()){
				savePath(node);
			}
		}
	}
	
	
	void solveRecursively(){
		m_children = new LinkedList<SolvingTree>();
		int currentColor = m_board.getCurrentColor();
		
		for(int i = 0 ;i < ColorBoard.NUMBER_OF_COLORS ; i++){
			if(m_board.isColorFinished(i) || i == currentColor)
				continue;
			
			addTreeNode(this, m_board, i);
		}
	}
	
	void addTreeNode(SolvingTree parent, ColorBoard board, int newColor){
		SolvingTree aux = new SolvingTree(board);
		aux.m_board.colorize(newColor);
		if(!wasProductiveChange(aux.m_board,board) || aux.m_board.getMoves() >= BoardSolver.s_minMoves){
			return;
		}
		parent.m_children.add(aux);
		aux.m_parent = parent;
		aux.m_children = new LinkedList<SolvingTree>();  
		
		//solve
		boolean finished = true;
		int i = newColor;
		do{
			if(++i >= ColorBoard.NUMBER_OF_COLORS) i = 0;
			
			if(i == newColor) break;
			
			if(aux.m_board.isColorFinished(i))	continue;
			
			finished = false;
			aux.addTreeNode(aux, aux.m_board, i);
		}while(true);

		if(finished && BoardSolver.s_minMoves >= aux.m_board.getMoves()){
			savePath(aux);
		}
		parent.m_children.remove(aux);
	}	
	
	private void savePath(SolvingTree node){
		int i, moves = node.m_board.getMoves();
		
		if(BoardSolver.s_minMoves > moves){
			BoardSolver.s_path.clear();
			BoardSolver.s_minMoves = moves;
		}
		
		StringBuilder path = new StringBuilder();
		int solution[] = new int[moves];
		for(i = 0 ; i < moves; i++, node=node.m_parent){
			solution[i] = node.m_board.getCurrentColor();
		}
		for(i = moves-1 ; i >= 0; i--){
			path.append(Const.COLOR_NAMES[solution[i]]);
			path.append(' ');
		}
		BoardSolver.s_path.add(path.toString());
	}
	
	boolean wasProductiveChange(ColorBoard newBoard, ColorBoard prevBoard){
		int prevColor = prevBoard.getCurrentColor();
		ColorBoard aux = newBoard.clone();
		aux.colorize(prevColor);
		return aux.colorRepetitions(prevColor) > prevBoard.colorRepetitions(prevColor);
	}
	
	public void printTree(){
		LinkedList<String> paths = BoardSolver.s_path;
		
		Const.i(BoardSolver.class.getSimpleName(),"COMPLETE PATHS: "+paths.size()+" - min:"+BoardSolver.s_minMoves);
		for(String path : paths){
			Const.i(BoardSolver.class.getSimpleName(),"\t"+path);
		}
	}
}
