package com.webs.itmexicali.colorized.gamestates;

import java.util.LinkedList;
import java.util.Scanner;

import com.webs.itmexicali.colorized.Const;
import com.webs.itmexicali.colorized.GameActivity;
import com.webs.itmexicali.colorized.GameView;
import com.webs.itmexicali.colorized.Preferences;
import com.webs.itmexicali.colorized.R;
import com.webs.itmexicali.colorized.drawcomps.BitmapLoader;
import com.webs.itmexicali.colorized.drawcomps.DrawButton;
import com.webs.itmexicali.colorized.drawcomps.DrawButtonContainer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.text.TextPaint;
import android.util.Log;
import android.view.MotionEvent;

public class GameState extends BaseState {
	
	//UI components to be used to Draw text and shapes
	public TextPaint	mPaints[];
	public Bitmap		mBitmaps[];
	public RectF		mRectFs[];
	DrawButtonContainer dbc;
	
	public float boardWidth, remainHeight, roundness;
	
	private String formated_moves_str = null, formated_moves_str_casual = null, moves_txt;
	private int mov_lim = 0;

	//font text size modifiers, this helps to change the Xfactor to texts
	float ts0 = 1.0f;

	//the matrix of color
	public ColorBoard mColorBoard = null;
	
	
	GameState(statesIDs id){
		super(id);
			
		mRectFs = new RectF[3];
		mPaints = ((MainState)StateMachine.getIns().getFirstState()).mPaints;
		mBitmaps = new Bitmap[3];
		
		
		dbc = new DrawButtonContainer(8,true);
		
		for(int i =0;i<8;i++)
			registerButtonToColorize(i);
		
		dbc.setOnActionListener(6, DrawButtonContainer.RELEASE_EVENT, new DrawButton.ActionListener(){
			@Override public void onActionPerformed() {
				new Thread(new Runnable(){public void run(){
					GameActivity.instance.playSound(GameActivity.SoundType.TOUCH);
					//OpenSettings;
					StateMachine.getIns().pushState(BaseState.statesIDs.TUTO);
				}}).start();}
		});
		
		dbc.setOnActionListener(7, DrawButtonContainer.RELEASE_EVENT, new DrawButton.ActionListener(){
			@Override public void onActionPerformed() {
				GameActivity.instance.runOnUiThread(new Runnable(){public void run(){
					GameActivity.instance.playSound(GameActivity.SoundType.TOUCH);
					showRestartDialog();
				}});
		}});
		
		formated_moves_str = GameActivity.instance.getString(R.string.moves_txt);
		formated_moves_str_casual = formated_moves_str.substring(0,formated_moves_str.length()-5);
		
	}
	
	/** Register each button to colorize certain color*/
	private void registerButtonToColorize(final int i){
		dbc.setOnActionListener(i, DrawButtonContainer.RELEASE_EVENT, new DrawButton.ActionListener(){
			@Override public void onActionPerformed() {
				new Thread(new Runnable(){public void run(){
					mColorBoard.colorize(i);
				}}).start();}
		});
	}
	
	@Override
	public void onPushed() {
		//check if the tutorial has been completed
		if(Preferences.getIns().isTutorialCompleted()){
			//if there is a gamestate saved, load it again
			if(Preferences.getIns().isGameSaved()){
				//parse the gamestate
				if(parseBoardFromString(Preferences.getIns().getSavedGame()))
					showSavedGameDialog();
			}
		}
		else{
			createNewBoard(Preferences.getIns().getBoardSize());
			StateMachine.getIns().pushState(BaseState.statesIDs.TUTO);			
		}
		
		if(mColorBoard == null){
			createNewBoard(Preferences.getIns().getBoardSize());
			mov_lim = Const.mov_limit[Preferences.getIns().getDifficulty()];
		}

	}
	
	@Override
	public void resize(float width, float height) {
		StateMachine.getIns().getFirstState().resize(width, height);
		reloadByResize();		
		roundness = height/48;
	}

	/** This method will be called when the surface has been resized, so all
	 * screen width and height dependents must be reloaded - 
	 * NOTE: DO NOT INCLUDE initPaints()*/
	protected void reloadByResize() {
		float width = GameView.width/16,  height= GameView.height/16;
		boardWidth = 14*width;
		remainHeight = (GameView.height-boardWidth)/8;
		
		mRectFs[0] = new RectF(8*width-boardWidth/2, 8*height-boardWidth/2,
				8*width+boardWidth/2, 8*height+boardWidth/2);
		
		
		
		mRectFs[1] = new RectF(0, 8*height+boardWidth/2+   remainHeight,
				  GameView.width, 8*height+boardWidth/2+ 3*remainHeight);
		
		//color controls
		width = boardWidth/29.5f;  height= GameView.height/5;
		float y = mRectFs[1].top+(2*remainHeight - 4.5f*width)/2;
		float finY = y +4.5f*width;
		float val =GameView.width/16;
		//5		1			4
		dbc.repositionDButton(0, val, y, val+4.5f*width, finY);
		dbc.repositionDButton(1, val+5*width, y, val+9.5f*width, finY);
		dbc.repositionDButton(2, val+10*width, y, val+14.5f*width, finY);
		dbc.repositionDButton(3, val+15*width, y, val+19.5f*width, finY);
		dbc.repositionDButton(4, val+20*width, y, val+24.5f*width, finY);
		dbc.repositionDButton(5, val+25*width, y, val+29.5f*width, finY);
		
		//settings buttons
		val = GameView.mPortrait ? GameView.width/9 : GameView.height/9;
		mBitmaps[0] = BitmapLoader.resizeImage(GameActivity.instance,R.drawable.ic_settings, val, val);
		mBitmaps[1] = BitmapLoader.resizeImage(GameActivity.instance,R.drawable.ic_restart, val, val);
		mBitmaps[2] = Bitmap.createBitmap(60, 60, Config.ARGB_8888);

		
		width = GameView.width/16;  height= GameView.height/96;
		dbc.repositionDButton(6, width, remainHeight, width+val,  remainHeight+val);
		dbc.repositionDButton(7, 15*width-mBitmaps[1].getWidth(),  remainHeight, 
				15*width-mBitmaps[1].getWidth()+val,  remainHeight+val);
		
		mRectFs[2] = new RectF(width+val+5, remainHeight,	15*width-val-5, 3*remainHeight);
		
	}
	
	@Override
	public void onPopped() {
		saveProgress();
	}

	@Override
	public void draw(Canvas canvas, boolean isPortrait) {
		//Background
		canvas.drawColor(Color.DKGRAY);//(mPaints[(int)(Math.random()*8)].getColor());
		
		//Color Div
		float roundness = GameView.height/48;
		canvas.drawRect(mRectFs[0].left-10, mRectFs[0].top-10, mRectFs[0].right+10, mRectFs[0].bottom+10, mPaints[6]);
		canvas.drawRoundRect(mRectFs[1], roundness, roundness, mPaints[6]);
		
		//canvas.save(); dg = (dg+1)%360;
		//canvas.rotate(dg, mRectFs[0].left+mRectFs[0].width()/2, mRectFs[0].top+mRectFs[0].height()/2);
		drawBoard(canvas);
		//canvas.restore();
		
		drawText(canvas);
		
		if ( mBitmaps != null ){
			canvas.drawBitmap(mBitmaps[0], GameView.width/16, remainHeight, null);
			canvas.drawBitmap(mBitmaps[1], 15*GameView.width/16-mBitmaps[1].getWidth(), remainHeight, null);
			
		}
		
		drawButtons(canvas);

	}
	
	/** Draw the text on the canvas*/
	public void drawText(Canvas canvas){
		if(mov_lim < 0){
			moves_txt = String.format(formated_moves_str_casual,mColorBoard.getMoves());
		}
		else{
			moves_txt = String.format(formated_moves_str, mColorBoard.getMoves(), mov_lim);
		}
		mPaints[8].setTextScaleX(ts0);
		while((mPaints[8].measureText(moves_txt)+10) >= mRectFs[2].width()){
			ts0-=0.05f;
			mPaints[8].setTextScaleX(ts0);
		}
		canvas.drawText(moves_txt,
				GameView.width/2, 2.15f*remainHeight, mPaints[8]);
		mPaints[8].setTextScaleX(1.0f);
	}
	
	/** Draw UI units capable to react to touch events*/
	public void drawButtons(Canvas canvas){
		for(int i =0 ; i < dbc.getButtonsCount();i++){
			if(i < 6)//after position 5, we are painting bitmaps instead of buttons
				canvas.drawRect(dbc.getDButton(i), mPaints[i]);
			if( dbc.getDButton(i).isPressed() )
				canvas.drawRect(dbc.getDButton(i), mPaints[7]);
		}
	}
	
	/** Draw board on surface's canvas*/
	public void drawBoard(Canvas canvas){
		mColorBoard.updateBoard(canvas, mRectFs[0], mPaints);
	}

	@Override
	public boolean touch(MotionEvent event) {
		int action = event.getAction() & MotionEvent.ACTION_MASK;
		int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
		int pointerId = event.getPointerId(pointerIndex);
		switch (action) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_POINTER_DOWN:
			dbc.onPressUpdate(event, pointerIndex, pointerId);
			break;

		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
		case MotionEvent.ACTION_CANCEL:	
			dbc.onReleaseUpdate(event, pointerIndex, pointerId);
			break;

		case MotionEvent.ACTION_MOVE:
			dbc.onMoveUpdate(event, pointerIndex);
			break;
		default:
			return false;
		}
		return true;
	}

	

	@Override
	public boolean onBackPressed() {
		return false;
	}
	
	/** If there is any progress in the game, save it in case the user
	 * wants to continue the next time he gets back to the game*/
	private void saveProgress(){
		if(getMoves() > 0 && !isGameOver()){
			// the game was started, lets save it 
			Preferences.getIns().saveGame(true,getBoardAsString());
		}
	}
	
	/*************************** POPUP DIALOGS ***********************************/
	/** Display dialog informing that there is a gamestate saved
	 * and ask if the user wants to play it or prefers a new match*/
	private void showSavedGameDialog(){
		//remove the saved game from the preferences
	    Preferences.getIns().saveGame(false, null);
	    GameActivity.instance.runOnUiThread(new Runnable(){
			public void run(){
				//Use the Builder class for convenient dialog construction
				new AlertDialog.Builder(GameActivity.instance)
				.setMessage(R.string.saved_game_confirmation)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) { 
		        	   Const.setFullScreen(GameActivity.instance);
		           }
				})
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   createNewBoard(Preferences.getIns().getBoardSize());
		        	   Const.setFullScreen(GameActivity.instance);
		           }
				})
				.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface arg0) {
			        	Const.setFullScreen(GameActivity.instance);
					}
				})
				.create()
				.show();
			}
		});
	}
	
	/** Display dialog informing that there is a gamestate saved
	 * and ask if the user wants to play it or prefers a new match*/
	public void showRestartDialog(){
		//Use the Builder class for convenient dialog construction
		GameActivity.instance.runOnUiThread(new Runnable(){
			public void run(){
				new AlertDialog.Builder(GameActivity.instance)
				.setMessage(R.string.restart_game_confirmation)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   createNewBoard(Preferences.getIns().getBoardSize());        	   
		        	   Const.setFullScreen(GameActivity.instance);   
		           }
				})
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {// dismiss menu
		        	   Const.setFullScreen(GameActivity.instance);
		           }
				})
				.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface arg0) {
			        	Const.setFullScreen(GameActivity.instance);
					}
				})
				.create()
				.show();
			}
		});
	}
	
	/** Display dialog informing that the game is over and restart again
	 * @param win if true, show congratulations text, else show condolences*/
	public void showGamOverDialog(final boolean win){
		Log.v(Const.TAG,"GameOver winning = "+win);
		GameActivity.instance.playMusic(false);
		
		//update games finished count
		int[] results = Preferences.getIns().updateGameFinished( win);
		
		if(results[0]%2 == 0)//each 2 games, show Interstitial
			GameActivity.instance.displayInterstitial();
		
		
		
		GameActivity.instance.runOnUiThread(new Runnable(){
			public void run(){
				String str =win? 
						String.format(GameActivity.instance.getString(R.string.game_over_win),getMoves()) :
							GameActivity.instance.getString(R.string.game_over_lose);
				//Use the Builder class for convenient dialog construction
				new AlertDialog.Builder(GameActivity.instance)
				.setMessage(str)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   createNewBoard(Preferences.getIns().getBoardSize());
		        	   Const.setFullScreen(GameActivity.instance);
		        	   GameActivity.instance.playMusic(true);
		           }
				})
				.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   Const.setFullScreen(GameActivity.instance);
		        	   createNewBoard(Preferences.getIns().getBoardSize());
		        	   StateMachine.getIns().popState();
		        	   GameActivity.instance.playMusic(true);
		           }
				})
				.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface arg0) {
						createNewBoard(Preferences.getIns().getBoardSize());
			        	Const.setFullScreen(GameActivity.instance);
			        	StateMachine.getIns().popState();
			        	GameActivity.instance.playMusic(true);
					}
				})
				.create()
				.show();
			}
		});
	}
	
	
	
/********************************* BOARD METHODS *********************************/
	
	/** Create a new random board*/
	public void createNewBoard(int blocks){
		mov_lim = Const.mov_limit[Preferences.getIns().getDifficulty()];
		if(blocks < 0)//if blocks is negative, this will have the same blocks #
			mColorBoard.startRandomColorBoard();
		else
			mColorBoard = new ColorBoard(blocks);
		
		//refreshUI();
	}
	
	/** Given a string containing a saved ColorBoard state,
	 * parse it to create a new game state identical */
	public boolean  parseBoardFromString(String state){
		if(state == null)
			return false;
		Scanner scanner = null;
		try{
			scanner = new Scanner(state);
			mov_lim = scanner.nextInt();
			int bps = scanner.nextInt();
			int moves = scanner.nextInt();
			int[][] board = new int[bps][bps];
			for(int i =0;i<bps;i++)
				for(int j=0;j<bps;j++)
					board[i][j] = scanner.nextInt();
			mColorBoard = new ColorBoard(bps, moves, board);
		}catch(Exception e){
			if(scanner != null)
				scanner.close();
			return false;
		}
		finally{
			if(scanner != null)
				scanner.close();
		}
		return true;
	}
	
	/** Get the color of the tile in the top-left corner
	 * @return color as number*/
	public int getFirstTileColor(){
		return mColorBoard.getMatrix()[0][0];
	}
	
	/** Get the color of the tile in the bottom-right corner
	 * @return color as number*/
	public int getLastTileColor(){
		return mColorBoard.getMatrix()[mColorBoard.blocksPerSide-1][mColorBoard.blocksPerSide-1];
	}
	
	/** The string representation of current game state
	 * @return board representation*/
	public String getBoardAsString(){
		return mov_lim+" "+mColorBoard.toString();
		
	}
	
	/** The number of moves (user interactions) in current game
	 * @return number of moves*/
	public int getMoves(){
		return mColorBoard.getMoves();
	}
	
	/** Check if the board is completed in one color
	 * @return true if game is over, false if not */
	public boolean isGameOver(){
		return mColorBoard.isBoardCompleted();
	}
	
	/** Callback to let the game know that the user input has been processed*/
	public void onBoardOpFinish(boolean won){
		if (won){
			GameActivity.instance.runOnUiThread(new Runnable(){public void run(){
				GameActivity.instance.playSound(GameActivity.SoundType.WIN);
				showGamOverDialog(true);
			}});
			
		}
		else if(getMoves() >= mov_lim && mov_lim >= 0){
			GameActivity.instance.runOnUiThread(new Runnable(){public void run(){
				GameActivity.instance.playSound(GameActivity.SoundType.LOSE);
				showGamOverDialog(false);
			}});
		}
	}
	

	/**
	 * This class will hold the matrix of colors and handle
	 * the users interactions. */
	private class ColorBoard{

		/** the matrix holding the color blocks*/
		private int mColorBoard[][];
		
		/** number of blocks per side in the square matrix*/
		private int blocksPerSide;
		
		/** Actual movements counter*/
		private int moves;
		
		/** handle concurrent access to colorize method, not using blocking synchronized modifier*/
		private boolean isColorizing = false;
		
		/** Structure to hold blocks to be checked*/
		private LinkedList<Integer[]> toCheck;
		
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
			blocksPerSide = blocks;
			moves = 0;
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
			//checkNeighborBlocks(1, 0, newColor);
			//checkNeighborBlocks(0, 1, newColor);
			toCheck = new LinkedList<Integer[]>();
			toCheck.add(new Integer[]{0,0});
			checkNeighborBlocks(newColor);
			
			mColorBoard[0][0] = newColor; //update the main block's color
			onBoardOpFinish(isBoardCompleted());
			
			isColorizing = false;		
		}
		
		/** check if the board is filled by 1 color
		 * @return true if completed, false if not*/
		public boolean isBoardCompleted(){
			int i,j;
			for(i=0;i<blocksPerSide;i++)
				for(j=0;j<blocksPerSide;j++)
					if(mColorBoard[0][0] != mColorBoard[i][j])
						return false;
			return true;
		} 
		
		/** compare neighbors colors with main block's color to update
		 * color recursively*/
		@SuppressWarnings("unused")
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
		
		/** compare neighbors colors with main block's color to update
		 * color iteratively*/
		private void checkNeighborBlocks(int newColor){
			Integer in[];
			int oldColor = mColorBoard[0][0];
			while(!toCheck.isEmpty()){
				in = toCheck.removeLast();
				mColorBoard[in[0]][in[1]] = newColor;
				if(in[0]>0 && mColorBoard[in[0]-1][in[1]] == oldColor	&& !(in[0]-1==0 && in[1]==0)){
					mColorBoard[in[0]-1][in[1]] = newColor;
					toCheck.add(new Integer[]{in[0]-1,in[1]});
				}
				
				if(in[1]>0 && mColorBoard[in[0]][in[1]-1] == oldColor	&& !(in[0]==0 && in[1]-1==0)){
					mColorBoard[in[0]][in[1]-1] = newColor;
					toCheck.add(new Integer[]{in[0],in[1]-1});
				}
				
				if(in[0]<blocksPerSide-1 &&  mColorBoard[in[0]+1][in[1]] == oldColor){
					mColorBoard[in[0]+1][in[1]] = newColor;
					toCheck.add(new Integer[]{in[0]+1,in[1]});
				}
				
				if(in[1]<blocksPerSide-1 &&  mColorBoard[in[0]][in[1]+1] == oldColor){
					mColorBoard[in[0]][in[1]+1] = newColor;
					toCheck.add(new Integer[]{in[0],in[1]+1});
				}
				
			}
			
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
		public void updateBoard(Canvas canvas, RectF rectf, TextPaint[] paints){
			float boardPixels = rectf.width();
			float blockPixels = boardPixels / blocksPerSide;
			float left = rectf.left;
			float top = rectf.top - blockPixels;
			
			for(int i=0; i<blocksPerSide; i++){
				top+=blockPixels;
				for(int j=0; j<blocksPerSide; j++)
					canvas.drawRect(left+blockPixels*j, top, left+blockPixels*(j+1) + (j == blocksPerSide-1 ? 0:5),
							top+blockPixels + (i == blocksPerSide-1? 0:5), paints[mColorBoard[i][j]]);
					
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

}
