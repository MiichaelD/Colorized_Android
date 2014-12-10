package com.webs.itmexicali.colorized.board;

import java.util.LinkedList;


public class BoardSolver {

	static LinkedList<StringBuilder> s_path;
	static int s_minMoves;
	
	public static int getOptimalPath(ColorBoard board){
		s_minMoves = Integer.MAX_VALUE;
		s_path = new LinkedList<StringBuilder>();
		SolvingTree solver = new SolvingTree(board);
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
		m_children = new LinkedList<SolvingTree>();

		int currentColor = m_board.getCurrentColor();
		for(int i = 0 ;i < ColorBoard.NUMBER_OF_COLORS ; i++){
			if(m_board.isColorFinished(i) || i == currentColor)
				continue;
			m_children.add(new SolvingTree(this, m_board,i));
		}
	}
	
	SolvingTree(SolvingTree parent, ColorBoard board, int newColor){
		m_board = board.clone();
		m_board.colorize(newColor);
		if(!wasProductiveChange(m_board,board) || m_board.getMoves() >= BoardSolver.s_minMoves){
			parent.m_children.remove(this);
			return;
		}
		m_parent = parent;
		m_children = new LinkedList<SolvingTree>(); 
		
		boolean finished = true;
		int i = newColor;
		do{
			if(++i >= ColorBoard.NUMBER_OF_COLORS) i = 0;
			
			if(i == newColor) break;
			
			if(m_board.isColorFinished(i))	continue;
			
			finished = false;
			m_children.add(new SolvingTree(this, m_board,i));
		}while(true);
		
		if(finished && BoardSolver.s_minMoves >= m_board.getMoves()){
			if(BoardSolver.s_minMoves > m_board.getMoves()){
				BoardSolver.s_path.clear();
			}
			BoardSolver.s_minMoves = m_board.getMoves();
			StringBuilder path = new StringBuilder();
			SolvingTree node = this;
			System.out.print("SOLUTION in "+node.m_board.getMoves()+ " moves:");
			do{
				if(node.m_parent == null)
					break;
				path.append(node.m_board.getCurrentColor());
				path.append(' ');
				node = node.m_parent;
			}while(true);
			
			BoardSolver.s_path.add(path);
			System.out.println(path.toString());
		}
		parent.m_children.remove(this);
	}
	
	
	boolean wasProductiveChange(ColorBoard newBoard, ColorBoard prevBoard){
		int prevColor = prevBoard.getCurrentColor();
		ColorBoard aux = newBoard.clone();
		aux.colorize(prevColor);
		return aux.colorRepetitions(prevColor) > prevBoard.colorRepetitions(prevColor);
	}
	
	public void printTree(){
		LinkedList<StringBuilder> paths = BoardSolver.s_path;
		
		System.out.println("COMPLETE PATHS: "+paths.size()+" - min:"+BoardSolver.s_minMoves);
		for(StringBuilder path : paths){
			System.out.println(path);
		}
	}
}
