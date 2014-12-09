package com.webs.itmexicali.colorized.util;

import java.util.LinkedList;

import com.webs.itmexicali.colorized.board.ColorBoard;

public class BoardSolver {

	static StringBuilder s_path;
	static int s_minMoves;
	
	public static String getOptimalPath(ColorBoard board){
		s_minMoves = Integer.MAX_VALUE;
		s_path = null;
		SolvingTree solver = new SolvingTree(board);
		solver.printTree();
		return s_path.toString();
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
		if(!wasProductiveChange(m_board,board)){
			parent.m_children.removeLast();
			return;
		}
		m_parent = parent;
		m_children = new LinkedList<SolvingTree>(); 
		
		boolean finished = true;
		for(int i = newColor+1 ;i != newColor && BoardSolver.s_minMoves == Integer.MAX_VALUE; i++){
			if( i >= ColorBoard.NUMBER_OF_COLORS)
				i = 0;
			
			if(m_board.isColorFinished(i) || i == newColor)
				continue;
			finished = false;
			m_children.add(new SolvingTree(this, m_board,i));
		}
		if(finished && BoardSolver.s_minMoves > m_board.getMoves()){
			BoardSolver.s_minMoves = m_board.getMoves();
			BoardSolver.s_path = new StringBuilder();
			SolvingTree node = this;
			System.out.print("SOLUTION in "+node.m_board.getMoves()+ " moves:");
			do{
				if(node.m_parent == null)
					break;
				BoardSolver.s_path.append(node.m_board.getCurrentColor());
				BoardSolver.s_path.append(' ');
				node = node.m_parent;
			}while(true);
			
			System.out.println(BoardSolver.s_path.toString());
		}
	}
	
	
	boolean wasProductiveChange(ColorBoard newBoard, ColorBoard prevBoard){
		int prevColor = prevBoard.getCurrentColor();
		ColorBoard aux = newBoard.clone();
		aux.colorize(prevColor);
		return aux.colorRepetitions(prevColor) > prevBoard.colorRepetitions(prevColor);
	}
	
	public void printTree(){
		
	}
}
