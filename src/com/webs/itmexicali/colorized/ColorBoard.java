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
	
	/** number of blocks per side in the square matrix
	 * and totalBlocks in matrix*/
	private int blocksPerSide, totalBlocks;
	
	/** Actual movements counter*/
	private int moves;
	
	/** Counter to keep track of updated blocks*/
	private int blocksUpdatedCount;
	
	/** Initialize a new random {@link ColorBoard} matrix*/
	ColorBoard(int blocks){
		moves = 0;
		startRandomColorBoard(blocks);
	}
	
	/** Load a given {@link ColorBoard} matrix*/
	ColorBoard(int blocks, int[][] mat){
		moves = 0;
		blocksPerSide = blocks;
		totalBlocks = blocks * blocks;
		if(mat == null)
			startRandomColorBoard(blocks);
		mColorBoard = mat;
	}
	
	/** Start a random matrix */
	public void startRandomColorBoard(int blocks){
		blocksPerSide = blocks;
		totalBlocks = blocks * blocks;
		mColorBoard = new int[blocks][blocks];
		for(int i=0; i<blocks; i++)
			for(int j=0; j<blocks; j++)
				mColorBoard[i][j] = (int)(Math.random()*6);
	}
	
	/** change the color of the blocks neighbor the main block
	 * which are from the same color (than the main block as well) */
	public void colorize(int newColor){
		Log.v("ColorBoard","colorise: "+newColor);
		//check if the new color is different from last one
		if(newColor == mColorBoard[0][0])
			return;
		
		moves++;//count this move
		
		//check the Neighbor block
		blocksUpdatedCount = 1; // counting the main block
		checkNeighborBlocks(1, 0, newColor);
		checkNeighborBlocks(0, 1, newColor);
		
		mColorBoard[0][0] = newColor; //update the main block's color
		
		boolean finished = blocksUpdatedCount == totalBlocks;
		GameView.getIns().boardOpFinish(finished);
		
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
		blocksUpdatedCount++;
		checkNeighborBlocks(x-1, y ,newColor);//check left neighbor
		checkNeighborBlocks(x+1, y, newColor);//check right neighbor
		checkNeighborBlocks(x, y-1, newColor);//check upper neighbor
		checkNeighborBlocks(x, y+1, newColor);//check below neighbor
	}	
	
	/** return the matrix of colors*/
	public int[][] getMatrix(){
		return mColorBoard;
	}
	
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
}
