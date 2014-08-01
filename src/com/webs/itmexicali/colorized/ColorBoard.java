package com.webs.itmexicali.colorized;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

/**
 * This class will hold the matrix of colors and handle
 * the users interactions. 
 * */
public class ColorBoard {

	/** the matrix holding the color blocks*/
	private int mColorBoard[][];
	
	/** number of blocks per side in the square matrix*/
	private int blocksPerSide;
	
	/** Actual movements counter*/
	private int moves;
	
	/** handle concurrent access to colorize method, not using blocking synchronized modifier*/
	private boolean isColorizing = false;
	
	
	
	/** Initialize a new random {@link ColorBoard} matrix*/
	ColorBoard(int blocks){
		startRandomColorBoard(blocks);
	}
	
	
	/** Load a given {@link ColorBoard} matrix
	 * @param blocks number of blocks per side of board
	 * @param moves number of user interactions in loaded board
	 * @param mat the board representation as an array of integers*/
	ColorBoard(int blocks, int moves, int[][] mat){
		if(mat == null){
			startRandomColorBoard(blocks);
			return;
		}
		this.moves = moves;
		blocksPerSide = blocks;
		mColorBoard = mat;
	}
	
	
	/** Start a new random matrix and set moves to 0
	 * @param blocks the new number of blocks per side of the matrix*/
	public void startRandomColorBoard(int blocks){
		moves = 0;
		blocksPerSide = blocks;
		mColorBoard = new int[blocks][blocks];
		for(int i=0; i<blocks; i++)
			for(int j=0; j<blocks; j++)
				mColorBoard[i][j] = (int)(Math.random()*6);
	}
	
	/** Start a new random matrix and reset moves counter*/
	public void startRandomColorBoard(){
		moves = 0;
		for(int i=0; i<blocksPerSide; i++)
			for(int j=0; j<blocksPerSide; j++)
				mColorBoard[i][j] = (int)(Math.random()*6);
	}
	
	
	
	/** change the color of the blocks neighbor the main block
	 * which are from the same color (than the main block as well) */
	public void colorize(int newColor){
		//check if the new color is different from last one
		// not the best approach, but will block a few threads
		if(isColorizing || newColor == mColorBoard[0][0])
			return;
		
		isColorizing = true;
		Log.v("ColorBoard","colorise: "+newColor);
		
		GameActivity.instance.playSound(GameActivity.SoundType.TOUCH);
		
		moves++;//count this move
		
		//check the Neighbor block
		checkNeighborBlocks(1, 0, newColor);
		checkNeighborBlocks(0, 1, newColor);
		
		mColorBoard[0][0] = newColor; //update the main block's color
		
		int i=0,j=0;
		for(;i<blocksPerSide;i++)
			for(;j<blocksPerSide;j++)
				if(newColor != mColorBoard[i][j])
					break;
		
		boolean finished = i == blocksPerSide-1 && j == blocksPerSide-1;
		GameView.getIns().onBoardOpFinish(finished);
		
		isColorizing = false;
		
	}
	
	/** compare neighbors colors with main block's color to update
	 * color recursively*/
	private void checkNeighborBlocks(int x, int y, int newColor){
		//check if we are within the matrix boundaries and also that
		// the boarding neighbor is from the same color as the main block
		if((x==0 && y==0) || x < 0  || x >= blocksPerSide || y < 0 ||
			y >= blocksPerSide ||mColorBoard[0][0] != mColorBoard[x][y])
			return;
		
		mColorBoard[x][y] = newColor;
		checkNeighborBlocks(x-1, y ,newColor);//check left neighbor
		checkNeighborBlocks(x+1, y, newColor);//check right neighbor
		checkNeighborBlocks(x, y-1, newColor);//check upper neighbor
		checkNeighborBlocks(x, y+1, newColor);//check below neighbor
	}	
	
	/** return the matrix of colors*/
	public int[][] getMatrix(){
		return mColorBoard;
	}
	
	/** The amount of moves this game has processed*/
	public int getMoves(){
		return moves;
	}
	
	/** draw the board within the rect given*/
	public void updateBoard(Canvas canvas, RectF rectf, Paint[] paints){
		float boardPixels = rectf.width();
		float blockPixels = boardPixels / blocksPerSide;
		float left = rectf.left;
		float top = rectf.top - blockPixels;
		
		for(int i=0; i<blocksPerSide; i++){
			top+=blockPixels;
			for(int j=0; j<blocksPerSide; j++)
				canvas.drawRect(left+blockPixels*j, top, left+blockPixels*(j+1), top+blockPixels, paints[mColorBoard[i][j]]);
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
		sb.append(blocksPerSide);
		sb.append(" ");
		sb.append(moves);
		for(int i=0;i<blocksPerSide;i++)
			for(int k=0;k<blocksPerSide;k++)
				sb.append(" ").append(mColorBoard[i][k]);
		return sb.toString();
	}
}
