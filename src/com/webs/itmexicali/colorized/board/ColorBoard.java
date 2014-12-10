package com.webs.itmexicali.colorized.board;

import java.util.LinkedList;

import ProtectedInt.ProtectedInt;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.text.TextPaint;
/**
 * This class will hold the matrix of colors and handle
 * the users interactions. */
public class ColorBoard{
	
	/** */
	private GameBoardListener m_listener;

	/** the matrix holding the color blocks*/
	private int m_colorMatrix[][];
	
	/** number of blocks per side in the square matrix*/
	private int m_blocksPerSide;
	
	/** Actual movements counter*/
	private ProtectedInt m_moves = new ProtectedInt(0);
	
	/** handle concurrent access to colorize method, not using blocking synchronized modifier*/
	private boolean m_isColorizing = false;
	
	/** Structure to hold blocks to be checked*/
	private LinkedList<Integer[]> toCheck;
	
	/** variable containing finished colors, each bit is either 1 for finished
	 * or 0 for unfinished*/
	private int finishedColors = 0;
	
	/** Counter of each color */
	private int m_colorCounter[];
	
	public static final int NUMBER_OF_COLORS = 6;
	private final int FINISHED_COLORS_FLAGS[] = {1, 2, 4, 8, 16, 32};
	private static final int ALL_COLORS_FINISHED = 63; //(2^6 -1 )
	
	/** Initialize a new random {@link ColorBoard} matrix*/
	public ColorBoard(int blocks){
		startRandomColorBoard(blocks);
	}
	
	/** Load a given {@link ColorBoard} matrix
	 * @param blocks number of blocks per side of board
	 * @param moves number of user interactions in loaded board
	 * @param mat the board representation as an array of integers*/
	public ColorBoard(int blocks, int moves, int[][] mat){
		if(mat == null){
			startRandomColorBoard(blocks);
			return;
		}
		m_moves.set(moves);
		m_blocksPerSide = blocks;
//		mat.clone();
		m_colorMatrix = mat;
		restartFinishedColors();
	}
	
	
	/** Start a new random matrix and set moves to 0
	 * @param blocks the new number of blocks per side of the matrix*/
	public void startRandomColorBoard(int blocks){
		m_blocksPerSide = blocks;
		m_colorMatrix = new int[blocks][blocks];
		startRandomColorBoard();
	}
	
	/** Start a new random matrix and reset moves counter*/
	public void startRandomColorBoard(){
		this.m_moves.set(0);
		for(int i=0; i<m_blocksPerSide; i++)
			for(int j=0; j<m_blocksPerSide; j++)
				m_colorMatrix[i][j] = (int)(Math.random()*NUMBER_OF_COLORS);

		restartFinishedColors();
	}
	
	
	public void setGameBoardListener(GameBoardListener listener){
		m_listener = listener;
	}
	
	/** Restart the variable containing the finished colors*/
	private void restartFinishedColors(){
		finishedColors = 0 ;
		checkBoardStatus();
	}
	
	public ColorBoard clone(){
		ColorBoard toReturn =  new ColorBoard(m_blocksPerSide, m_moves.get(), cloneMatrix());
		return toReturn;
	}
	
	public int colorRepetitions(int color){
		return m_colorCounter[color];
	}
	
	/** Store the color finished*/
	private void addFinishedColor(int color){
		finishedColors |= FINISHED_COLORS_FLAGS[color];
	}
	
	private void removeFinishedColor(int color){
		if ( isColorFinished(color) )
			finishedColors ^= FINISHED_COLORS_FLAGS[color];
	}
	
	public boolean allColorsFinished(){
		return finishedColors == ALL_COLORS_FINISHED;
	}
	
	/** Check if a color is finished
	 * @param color color to check if is finished*/
	public boolean isColorFinished(int color){
		return FINISHED_COLORS_FLAGS[color] == (finishedColors & FINISHED_COLORS_FLAGS[color]);
	}
	
	/** Get the color of the main tile (upper-left corner)*/
	public int getCurrentColor(){
		return m_colorMatrix[0][0];
	}	
	
	public int getBlocksPerSide(){
		return m_blocksPerSide;
	}
	
	
	/** change the color of the blocks neighbor the main block
	 * which are from the same color (than the main block as well) */
	public void colorize(int newColor){
		//check if the new color is different from last one
		// not the best approach, but will block a few threads
		if(m_isColorizing || newColor == m_colorMatrix[0][0])
			return;
		
		m_isColorizing = true;
//		Log.v("ColorBoard","colorize: "+newColor);
		
		this.m_moves.increment();//count this move
		
		//check the Neighbor block
		//checkNeighborBlocks(1, 0, newColor);
		//checkNeighborBlocks(0, 1, newColor);
		toCheck = new LinkedList<Integer[]>();
		toCheck.add(new Integer[]{0,0});
		checkNeighborBlocks(newColor);
		
		m_colorMatrix[0][0] = newColor; //update the main block's color
		checkBoardStatus();
		if(m_listener != null)
			m_listener.onBoardFloodingFinished(allColorsFinished());
		
		m_isColorizing = false;		
	}
	
	/** check if the board is filled by 1 color
	 * @return true if completed, false if not*/
	public void checkBoardStatus(){
		int i,j;
		m_colorCounter = new int[NUMBER_OF_COLORS];
		for(i=0;i<m_blocksPerSide;i++)
			for(j=0;j<m_blocksPerSide;j++)
				m_colorCounter[m_colorMatrix[i][j]]++;
		
		for(i = 0; i < NUMBER_OF_COLORS ; i++){
//			Const.i("ColorBoard", "Color "+ i+" count: "+m_colorCounter[i]);
			if(m_colorCounter[i] == 0)
				addFinishedColor(i);
			else
				removeFinishedColor(i);
		}
		addFinishedColor(m_colorMatrix[0][0]);
		
	} 
	
	/** compare neighbors colors with main block's color to update
	 * color recursively*/
	@SuppressWarnings("unused")
	private void checkNeighborBlocks(int x, int y, int newColor){
		//check if we are within the matrix boundaries and also that
		// the boarding neighbor is from the same color as the main block
		if((x==0 && y==0) || x < 0  || x >= m_blocksPerSide || y < 0 ||
			y >= m_blocksPerSide ||m_colorMatrix[0][0] != m_colorMatrix[x][y])
			return;
		
		m_colorMatrix[x][y] = newColor;
		checkNeighborBlocks(x-1, y ,newColor);//check left neighbor
		checkNeighborBlocks(x+1, y, newColor);//check right neighbor
		checkNeighborBlocks(x, y-1, newColor);//check upper neighbor
		checkNeighborBlocks(x, y+1, newColor);//check below neighbor
	}	
	
	/** compare neighbors colors with main block's color to update
	 * color iteratively*/
	private void checkNeighborBlocks(int newColor){
		Integer in[];
		int oldColor = m_colorMatrix[0][0];
		while(!toCheck.isEmpty()){
			in = toCheck.removeLast();
			m_colorMatrix[in[0]][in[1]] = newColor;
			if(in[0]>0 && m_colorMatrix[in[0]-1][in[1]] == oldColor	&& !(in[0]-1==0 && in[1]==0)){
				m_colorMatrix[in[0]-1][in[1]] = newColor;
				toCheck.add(new Integer[]{in[0]-1,in[1]});
			}
			
			if(in[1]>0 && m_colorMatrix[in[0]][in[1]-1] == oldColor	&& !(in[0]==0 && in[1]-1==0)){
				m_colorMatrix[in[0]][in[1]-1] = newColor;
				toCheck.add(new Integer[]{in[0],in[1]-1});
			}
			
			if(in[0]<m_blocksPerSide-1 &&  m_colorMatrix[in[0]+1][in[1]] == oldColor){
				m_colorMatrix[in[0]+1][in[1]] = newColor;
				toCheck.add(new Integer[]{in[0]+1,in[1]});
			}
			
			if(in[1]<m_blocksPerSide-1 &&  m_colorMatrix[in[0]][in[1]+1] == oldColor){
				m_colorMatrix[in[0]][in[1]+1] = newColor;
				toCheck.add(new Integer[]{in[0],in[1]+1});
			}
			
		}
		
	}	
	
	/** return the matrix of colors*/
	public int[][] getMatrix(){
		return m_colorMatrix;
	}
	
	
	public int[][] cloneMatrix(){
		int[][] toReturn = new int[m_blocksPerSide][m_blocksPerSide];
		for(int i = 0; i < m_blocksPerSide; i++)
			for(int j = 0; j < m_blocksPerSide; j++)
				toReturn[i][j] = m_colorMatrix[i][j];
		return toReturn;
	}
	
	/** The amount of moves this game has processed*/
	public int getMoves(){
		return this.m_moves.get();
	}
	
	/** draw the board within the rect given*/
	public void updateBoard(Canvas canvas, RectF rectf, TextPaint[] paints){
		float boardPixels = rectf.width();
		float blockPixels = boardPixels / m_blocksPerSide;
		float left = rectf.left;
		float top = rectf.top - blockPixels;
		
		for(int i=0; i<m_blocksPerSide; i++){
			top+=blockPixels;
			for(int j=0; j<m_blocksPerSide; j++)
				canvas.drawRect(left+blockPixels*j, top, left+blockPixels*(j+1) + (j == m_blocksPerSide-1 ? 0:5),
						top+blockPixels + (i == m_blocksPerSide-1? 0:5), paints[m_colorMatrix[i][j]]);
				
		}
		
	}
	
	/** Representation of current {@link ColorBoard} state
	 * @return string formated as follows: "<b>i j []</b>",
	 * where <b>i</b> is the number of blocks per side
	 * <b>j</b> is the number of moves at current time and
	 * <b>[]</b> is the representation of the board as a sucession of numbers*/
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(m_blocksPerSide);
		sb.append(" ");
		sb.append(m_moves.get());
		for(int i=0;i<m_blocksPerSide;i++)
			for(int k=0;k<m_blocksPerSide;k++)
				sb.append(" ").append(m_colorMatrix[i][k]);
		return sb.toString();
	}

}
