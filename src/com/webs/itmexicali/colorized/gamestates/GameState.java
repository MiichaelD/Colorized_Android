package com.webs.itmexicali.colorized.gamestates;

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
	public TextPaint		mPaints[];
	public Bitmap		mBitmaps[];
	public RectF		mRectFs[];
	DrawButtonContainer dbc;


	//the matrix of color
	public ColorBoard mColorBoard = null;
	
	
	GameState(){
		mID = statesIDs.GAME;
			
		mRectFs = new RectF[2];
		mPaints = ((MainState)StateMachine.getIns().getFirstState()).mPaints;
		
		
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
				parseBoardFromString(Preferences.getIns().getSavedGame());
				showSavedGameDialog();
			}
		}
		else{
			createNewBoard(Preferences.getIns().getBoardSize());
			StateMachine.getIns().pushState(BaseState.statesIDs.TUTO);			
		}
		
		if(mColorBoard == null){
			createNewBoard(Preferences.getIns().getBoardSize());
		}

	}
	
	@Override
	public void resize(float width, float height) {
		StateMachine.getIns().getFirstState().resize(width, height);
		reloadByResize();		
	}

	/** This method will be called when the surface has been resized, so all
	 * screen width and height dependents must be reloaded - 
	 * NOTE: DO NOT INCLUDE initPaints()*/
	protected void reloadByResize() {
		float width = GameView.width/16,  height= GameView.height/16;
		mRectFs[0] = new RectF(width, 5*height-5*width, 15*width, 5*height+9*width);
		
		width = GameView.width/48;  height= GameView.height/5;
		mRectFs[1] = new RectF(0, 2*height+23*width, GameView.width, 2*height+31*width);

		mBitmaps = new Bitmap[3];
		float val = GameView.mPortrait ? GameView.width/9 : GameView.height/9;
		mBitmaps[0] = BitmapLoader.resizeImage(GameActivity.instance,R.drawable.ic_settings, val, val);
		mBitmaps[1] = BitmapLoader.resizeImage(GameActivity.instance,R.drawable.ic_restart, val, val);
		mBitmaps[2] = Bitmap.createBitmap(60, 60, Config.ARGB_8888);
		
		//color controls
		width = GameView.width/2;  height= GameView.height/5;
		dbc.repositionDButton(0, 3*width/14, 2*height+width, 6*width/14, 2*height+5*width/4);
		dbc.repositionDButton(1, 7*width/14, 2*height+width, 10*width/14, 2*height+5*width/4);
		dbc.repositionDButton(2, 11*width/14, 2*height+width, 14*width/14, 2*height+5*width/4);
		dbc.repositionDButton(3, 15*width/14, 2*height+width, 18*width/14, 2*height+5*width/4);
		dbc.repositionDButton(4, 19*width/14, 2*height+width, 22*width/14, 2*height+5*width/4);
		dbc.repositionDButton(5, 23*width/14, 2*height+width, 26*width/14, 2*height+5*width/4);
		
		//settings buttons
		width = GameView.width/16;  height= GameView.height/96;
		dbc.repositionDButton(6, width, height, width+val,  height+val);
		dbc.repositionDButton(7, 15*width-mBitmaps[1].getWidth(),  height, 
				15*width-mBitmaps[1].getWidth()+val,  height+val);
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
		canvas.drawRect(mRectFs[0].left-10, mRectFs[0].top-10, mRectFs[0].right+10, mRectFs[0].bottom+10, mPaints[6]);
		canvas.drawRoundRect(mRectFs[1], 50.0f, 40.0f, mPaints[6]);
		
		//canvas.save(); dg = (dg+1)%360;
		//canvas.rotate(dg, mRectFs[0].left+mRectFs[0].width()/2, mRectFs[0].top+mRectFs[0].height()/2);
		drawBoard(canvas);
		//canvas.restore();
		
		//TODO change hardcoded value
		canvas.drawText("Moves: "+mColorBoard.getMoves()+"/21", GameView.width/2, GameView.height/16, mPaints[8]);
		
		if ( mBitmaps != null ){
			canvas.drawBitmap(mBitmaps[0], GameView.width/16, GameView.height/96, null);
			canvas.drawBitmap(mBitmaps[1], 15*GameView.width/16-mBitmaps[1].getWidth(), GameView.height/96, null);
			
		}
		
		drawButtons(canvas);

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
		if(blocks < 0)//if blocks is negative, this will have the same blocks #
			mColorBoard.startRandomColorBoard();
		else
			mColorBoard = new ColorBoard(blocks);
		
		//refreshUI();
	}
	
	/** Given a string containing a saved ColorBoard state,
	 * parse it to create a new game state identical */
	public void parseBoardFromString(String state){
		if(state == null)
			return;
		Scanner scanner = null;
		try{
			scanner = new Scanner(state);
			int bps = scanner.nextInt();
			int moves = scanner.nextInt();
			int[][] board = new int[bps][bps];
			for(int i =0;i<bps;i++)
				for(int j=0;j<bps;j++)
					board[i][j] = scanner.nextInt();
			mColorBoard = new ColorBoard(bps, moves, board);
		}catch(Exception e){
		}
		finally{scanner.close();}
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
		return mColorBoard.toString();
		
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
		else if(getMoves() > 20){//TODO hardcoded value
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
			checkNeighborBlocks(1, 0, newColor);
			checkNeighborBlocks(0, 1, newColor);
			
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
