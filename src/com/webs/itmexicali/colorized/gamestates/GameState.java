package com.webs.itmexicali.colorized.gamestates;

import org.json.JSONObject;

import com.webs.itmexicali.colorized.GameActivity;
import com.webs.itmexicali.colorized.GameView;
import com.webs.itmexicali.colorized.R;
import com.webs.itmexicali.colorized.board.BoardSolver;
import com.webs.itmexicali.colorized.board.ColorBoard;
import com.webs.itmexicali.colorized.board.GameBoardListener;
import com.webs.itmexicali.colorized.drawcomps.BitmapLoader;
import com.webs.itmexicali.colorized.drawcomps.DrawButton;
import com.webs.itmexicali.colorized.drawcomps.DrawButtonContainer;
import com.webs.itmexicali.colorized.util.Const;
import com.webs.itmexicali.colorized.util.ProgNPrefs;
import com.webs.itmexicali.colorized.util.Tracking;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Align;
import android.text.TextPaint;
import android.util.Log;
import android.view.MotionEvent;

public class GameState extends BaseState implements GameBoardListener{
    
	/** Interface to communicate game results to the listeners*/
	public interface GameFinishedListener{
		/** Pass all the game match parameters to be processed
		 * @param win if the player win this match
		 * @param moves the number of moves needed
		 * @param GameMode game mode used in the game
		 * @param boardSize the board size of game played {small,medium,large}*/
		public void onGameOver(boolean win, int moves, int gameMode, int boardSize);
	}
	
	//UI components to be used to Draw text and shapes
	public TextPaint	mPaints[];
	public Bitmap		mBitmaps[];
	public RectF		mRectFs[];
	DrawButtonContainer dbc;	
	
	public TextPaint darkBlurPaint, movesTextPaint;
	
	private boolean ignoreSavedGame = false, askToPlaySavedGame = true;
	
	public float boardWidth, remainHeight, roundness;
	
	private String formated_moves_str = null, formated_moves_str_casual = null, moves_txt;

	//font text size modifiers, this helps to change the Xfactor to texts
	float ts0 = 1.0f;

	//the matrix of color
	public ColorBoard mColorBoard = null;
	
	GameState(statesIDs id){
		super(id);
		//Const.v("GameState","Constructor");
		
		movesTextPaint = new TextPaint(); // WHITE for TEXT
		movesTextPaint.setColor(Color.WHITE);
		movesTextPaint.setStyle(Paint.Style.FILL);
		movesTextPaint.setTextAlign(Align.CENTER);
		movesTextPaint.setAntiAlias(true);
		
		darkBlurPaint = new TextPaint(0);//BACKGROUND
		darkBlurPaint.setColor(0xff101010);
		darkBlurPaint.setAlpha(170);
		darkBlurPaint.setStyle(Paint.Style.FILL);
		darkBlurPaint.setMaskFilter(new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL));
		
		mPaints = new TextPaint[7];
		
		mPaints[0] = new TextPaint();//RED 
		mPaints[0].setColor(Color.RED);
		mPaints[0].setStyle(Paint.Style.FILL);
		mPaints[0].setAntiAlias(true);
		
		mPaints[1] = new TextPaint();//BLUE 
		mPaints[1].setColor(Color.rgb(0, 162, 232));
		mPaints[1].setStyle(Paint.Style.FILL);
		//mPaints[1].setTextSize(GameView.mPortrait? width/14 : height/14);
		mPaints[1].setAntiAlias(true);		
		
		mPaints[2] = new TextPaint();//YELLOW 
		mPaints[2].setColor(Color.YELLOW);
		mPaints[2].setStyle(Paint.Style.FILL);
		mPaints[2].setAntiAlias(true);
		
		mPaints[3] = new TextPaint();//PURPLE
		mPaints[3].setColor(Color.rgb(163, 73, 164));
		mPaints[3].setStyle(Paint.Style.FILL);
		mPaints[3].setAntiAlias(true);
		
		mPaints[4] = new TextPaint();//GRAY
		mPaints[4].setColor(Color.LTGRAY);
		mPaints[4].setStyle(Paint.Style.FILL);
		mPaints[4].setAntiAlias(true);
		
		mPaints[5] = new TextPaint();//GREEN
		mPaints[5].setColor(Color.rgb(21, 183, 46));
		mPaints[5].setStyle(Paint.Style.FILL);
		mPaints[5].setAntiAlias(true);
		
		mPaints[6] = new TextPaint(); // DKGRAY for pushed buttons
		mPaints[6].setColor(Color.DKGRAY);
		mPaints[6].setStyle(Paint.Style.FILL);
		mPaints[6].setAntiAlias(true);
		mPaints[6].setAlpha(175);
		
		
		mRectFs = new RectF[3];
		mBitmaps = new Bitmap[3];
		dbc = new DrawButtonContainer(8,true);
		
		for(int i =0;i<8;i++)
			registerButtonToColorize(i);
		
		dbc.setOnActionListener(6, DrawButtonContainer.RELEASE_EVENT, new DrawButton.ActionListener(){
			@Override public void onActionPerformed() {
				new Thread(new Runnable(){public void run(){
					GameActivity.instance.playSound(GameActivity.SoundType.TOUCH);
					//OpenSettings;
					StateMachine.getIns().pushState(BaseState.statesIDs.OPTION);
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
		
		//create board to prevent NullPointerException
		//if(mColorBoard == null)
		{
			//Const.v("GameState","created New Board");
			restartBoard(true);			
		}
		
	}
	
	/** Register each button to colorize certain color*/
	private void registerButtonToColorize(final int i){
		dbc.setOnActionListener(i, DrawButtonContainer.RELEASE_EVENT, new DrawButton.ActionListener(){
			@Override public void onActionPerformed() {
			//only if it's a different color and game has not finished
			if (!mColorBoard.isCurrentColor(i)  && mColorBoard.hasMovesRemaining() ){
				//play sound
				GameActivity.instance.playSound(GameActivity.SoundType.TOUCH);
				
				new Thread(new Runnable(){public void run(){
					mColorBoard.colorize(i);
				}}).start();}
			}
		});
	}
	
	
	@Override
	public void onFocus() {	
		super.onFocus();
		
		/* BUG FIX: this used to be in onPush() but in some devices (SCH-I535 - Android 4.3)
		 * it took several seconds (about 10s) just painting background, because the resize(f,f)
		 * method was not being called, sometimes causing the user to press back and cause a
		 * null pointer exception due there is no instance of mColorBoard yet to get the moves count*/
		checkTutoAndSavedGame();
	}
	
	/** Check that the tutorial has been completed, if not start it
	 * if the tutorial was completed, check if there is a game saved.*/
	private void checkTutoAndSavedGame(){
		//new Thread(new Runnable(){public void run(){
			//check if the tutorial has been completed
			if(ProgNPrefs.getIns().isTutorialCompleted()){
				//if there is a game state saved, load it again
				if(!ignoreSavedGame && ProgNPrefs.getIns().isGameSaved()){
					//parse the game state
					String boardSave = ProgNPrefs.getIns().getSavedGame();
					mColorBoard = ColorBoard.newBoardFromString(boardSave);
					if(mColorBoard != null){
						mColorBoard.setGameBoardListener(this);
						if(askToPlaySavedGame){
							showSavedGameDialog();
						}
					}
				}
			}
			else{
				createNewBoard(Const.BOARD_SIZES[ProgNPrefs.getIns().getDifficulty()]);
				StateMachine.getIns().pushState(BaseState.statesIDs.TUTO);			
			}
			
			if(mColorBoard == null){
				createNewBoard(Const.BOARD_SIZES[ProgNPrefs.getIns().getDifficulty()]);
				mColorBoard.setDefaultMoveLimit();
			}

		
		//}}).start();
	}
	
	void trackGameContinue(){
		java.util.HashMap<String, Object> map = new java.util.HashMap<String, Object>();
		map.put("BoardPlayed", ProgNPrefs.getIns().getLastBoardStarted());
		map.put("BoardCompleted", mColorBoard.toString());
		map.put("BlocksPerSide", mColorBoard.getBlocksPerSide());
		map.put("Difficulty", mColorBoard.getSize());
		map.put("Moves", mColorBoard.getMovesCount());
		map.put("MovesLimit", mColorBoard.getMovesLimit());
		map.put("GameMode", mColorBoard.getGameMode());
		Tracking.shared().track("GameContinue",map);
	}
	
	void trackGameStart(String currentBoard){
		//start counting time required to finish this board
		Tracking.shared().time("GameFinished");
		
		java.util.HashMap<String, Object> map = new java.util.HashMap<String, Object>();
		map.put("BoardPlayed", currentBoard);
		map.put("BlocksPerSide", mColorBoard.getBlocksPerSide());
		map.put("Difficulty", mColorBoard.getSize());
		map.put("Moves", mColorBoard.getMovesCount());
		map.put("MovesLimit", mColorBoard.getMovesLimit());
		map.put("GameMode", mColorBoard.getGameMode());
		Tracking.shared().track("NewGame", map);
	}
	
	void trackGameOver(boolean win){
		java.util.HashMap<String, Object> map = new java.util.HashMap<String, Object>();
		map.put("Result", win);
		map.put("Moves", mColorBoard.getMovesCount());
		map.put("BoardPlayed", ProgNPrefs.getIns().getLastBoardStarted());
		map.put("BoardCompleted", mColorBoard.toString());
		map.put("BlocksPerSide", mColorBoard.getBlocksPerSide());
		map.put("Difficulty", mColorBoard.getSize());
		map.put("MovesLimit", mColorBoard.getMovesLimit());
		map.put("GameMode", mColorBoard.getGameMode());
		Tracking.shared().track("GameFinished", map);
	}
	
	public void resize(float width, float height) {
		//Log.i("GameState","resize(f,f)");
		roundness = height/48;

		movesTextPaint.setTextSize(GameView.mPortrait? width/13 : height/13);
		mPaints[1].setTextSize(GameView.mPortrait? width/14 : height/14);
		movesTextPaint.setTextScaleX(1.0f);
		
		reloadByResize();		
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
		height= GameView.height/5;
		width = boardWidth/29.5f; // Color Control width 
		float y = mRectFs[1].top+(2*remainHeight - 4.5f*width)/2;
		float finY = y +4.5f*width;
		float val =GameView.width/16;
		
		if(mRectFs[1].height()<4.5f*width)
			mRectFs[1] = new RectF(0, y,GameView.width, finY);
		
		//5		1			4
		dbc.repositionDButton(0, val, y, val+4.5f*width, finY);
		dbc.repositionDButton(1, val+5*width, y, val+9.5f*width, finY);
		dbc.repositionDButton(2, val+10*width, y, val+14.5f*width, finY);
		dbc.repositionDButton(3, val+15*width, y, val+19.5f*width, finY);
		dbc.repositionDButton(4, val+20*width, y, val+24.5f*width, finY);
		dbc.repositionDButton(5, val+25*width, y, val+29.5f*width, finY);
		
		//settings buttons
		val = GameView.mPortrait ? GameView.width/9 : GameView.height/9;
		mBitmaps[0] = BitmapLoader.resizeImage(GameActivity.instance,R.drawable.ic_settings, true, val, val);
		mBitmaps[1] = BitmapLoader.resizeImage(GameActivity.instance,R.drawable.ic_restart, true, val, val);
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
		//canvas.drawColor(Color.DKGRAY);//(darkBlurPaint[(int)(Math.random()*8)].getColor());
		
		//Color Div
		float roundness = GameView.height/48;
		canvas.drawRect(mRectFs[0].left-20, mRectFs[0].top-20, mRectFs[0].right+20, mRectFs[0].bottom+20, darkBlurPaint);
		canvas.drawRoundRect(mRectFs[1], roundness, roundness, darkBlurPaint);
		
		//canvas.save(); dg = (dg+1)%360;
		//canvas.rotate(dg, mRectFs[0].left+mRectFs[0].width()/2, mRectFs[0].top+mRectFs[0].height()/2);
		drawBoard(canvas);
		//canvas.restore();
		
		drawText(canvas);
		
		if ( mBitmaps != null ){
			canvas.drawRect(dbc.getDButton(6), darkBlurPaint);
			canvas.drawRect(dbc.getDButton(7), darkBlurPaint);
			canvas.drawBitmap(mBitmaps[0], GameView.width/16, remainHeight, null);
			canvas.drawBitmap(mBitmaps[1], 15*GameView.width/16-mBitmaps[1].getWidth(), remainHeight, null);
		}
		
		drawButtons(canvas);

	}
	
	/** Draw the text on the canvas*/
	public void drawText(Canvas canvas){
		if(mColorBoard.getGameMode() == Const.CASUAL){
			moves_txt = String.format(formated_moves_str_casual,mColorBoard.getMovesCount());
		}
		else{
			moves_txt = String.format(formated_moves_str, mColorBoard.getMovesCount(), mColorBoard.getMovesLimit());
		}

		movesTextPaint.setTextScaleX(ts0);
		while((movesTextPaint.measureText(moves_txt)+10) >= mRectFs[2].width()){
			ts0-=0.05f;
			movesTextPaint.setTextScaleX(ts0);
		}
		canvas.drawText(moves_txt,
				GameView.width/2, 2.15f*remainHeight, movesTextPaint);
		movesTextPaint.setTextScaleX(1.0f);
	}
	
	/** Draw UI units capable to react to touch events*/
	public void drawButtons(Canvas canvas){
		
		
		
		for(int i =0 ; i < dbc.getButtonsCount();i++){
			if(i < ColorBoard.NUMBER_OF_COLORS){//after position 5, we are painting bitmaps instead of buttons
				dbc.setEnabled(i, !mColorBoard.isColorFinished(i));
				dbc.getDButton(i).draw(canvas, 0, mPaints[i], mPaints[6]);
			}
			if( dbc.getDButton(i).isPressed() )
				canvas.drawRect(dbc.getDButton(i), mPaints[6]);
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
		int pointerCount = event.getPointerCount();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_POINTER_DOWN:
			dbc.onPressUpdate(event, pointerIndex, pointerId);
			
			if(Const.CHEATS){
				//finish game if 4 fingers on board
				if(pointerCount == 4 && mRectFs[0] != null){				
					for(int i =0 ; i<pointerCount;i++){
						if(!mRectFs[0].contains((int)event.getX(i),(int)event.getY(i)))
							return true;
					}
					Const.d(GameState.class.getSimpleName(),"CHEATS OPENED");
					showGamOver(true);
					//
				} else if(pointerCount == 3 && mRectFs[0] != null){				
					for(int i =0 ; i<pointerCount;i++){
						if(!mRectFs[0].contains((int)event.getX(i),(int)event.getY(i)))
							return true;
					}
					Thread solver = new Thread(new Runnable(){
						public void run(){
							int mov_lim = BoardSolver.getOptimalPath(mColorBoard);
							mColorBoard.setTotalMoves(mov_lim);
					}});
					solver.setPriority(Thread.MAX_PRIORITY);
					solver.start();
					Const.d(GameState.class.getSimpleName(),"SOLVING BOARD");					
				}
			}
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
	
	/** Ignore saved game and load normal board */
	public void setIgnoreSavedGame(boolean ignore){
		ignoreSavedGame = ignore;
	}
	
	/** ask player if he wishes to play saved game */
	public void setAskToPlaySavedGame(boolean ask){
		askToPlaySavedGame = ask;
	}
	
	/** If there is any progress in the game, save it in case the user
	 * wants to continue the next time he gets back to the game*/
	private void saveProgress(){
		/* Save the board if:
		 * - we make more than 1 move &
		 * - the board is not completed & 
		 * - our moves is less than limit | the limit is negative (we have no limit)
		*/
		if(mColorBoard.isStarted() && !mColorBoard.isCompleted()){
			ProgNPrefs.getIns().saveCurrentGameState(true,mColorBoard.toString());
		}
	}
	
	/*************************** POPUP DIALOGS ***********************************/
	/** Display dialog informing that there is a gamestate saved
	 * and ask if the user wants to play it or prefers a new match*/
	private void showSavedGameDialog(){
		//if we are still on the list
		if(!StateMachine.getIns().checkStateIsInList(statesIDs.GAME))
			return;
		
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
		        	   restartBoard(true);
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
	
	/** Ask for confirmation if player wants to start a new board*/
	public void showRestartDialog(){		
		//Use the Builder class for convenient dialog construction
		GameActivity.instance.runOnUiThread(new Runnable(){
			public void run(){
				new AlertDialog.Builder(GameActivity.instance)
				.setMessage(R.string.restart_game_confirmation)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   restartBoard(true);        	   
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
	public void showGamOver(final boolean win){
		Log.d(Const.TAG,"GameOver winning = "+win);
		trackGameOver(win);
        
        //remove the previous saved game and last new board from the preferences, if any
        ProgNPrefs.getIns().saveCurrentGameState(false, null);
        ProgNPrefs.getIns().saveNewBoard(false, null);
		
		BaseState gameOver = BaseState.stateFactory(statesIDs.OVER);
		((GameFinishedListener)gameOver).onGameOver(win, mColorBoard.getMovesCount(), mColorBoard.getGameMode(), mColorBoard.getSize() );
		StateMachine.getIns().pushState(gameOver);
	}
	
	
	
	
	
/********************************* BOARD METHODS *********************************/
	
	@Override
	public void restartBoard(boolean forced) {
		if(forced){
			int blocksPerSide = Const.BOARD_SIZES[ProgNPrefs.getIns().getDifficulty()];
			createNewBoard(blocksPerSide);
			//Restart win streak counter to prevent cheating by restarting game before losing
			ProgNPrefs.getIns().updateWinStreak(false);
		}else{
			if(mColorBoard.isStarted() && !mColorBoard.isCompleted())
				showRestartDialog();
			else
				restartBoard(true);
		}
	}
	
	@Override
	public void onBoardFloodingFinished(final boolean won) {
		
		GameActivity.instance.runOnUiThread(new Runnable(){public void run(){
			if (won){
				GameActivity.instance.playSound(GameActivity.SoundType.WIN);
				showGamOver(true);
			} else if (mColorBoard.isStarted() && !mColorBoard.hasMovesRemaining()){
				GameActivity.instance.playSound(GameActivity.SoundType.LOSE);
				showGamOver(false);
			}
		}});
	}
	
	
	/** Create a new random board*/
	public void createNewBoard(int blocks){
		if(blocks < 0)//if blocks is negative, this will have the same blocks #
			mColorBoard.startRandomColorBoard();
		else
			mColorBoard = new ColorBoard(blocks);
		mColorBoard.setGameBoardListener(this);
		mColorBoard.setDefaultMoveLimit();
		
		//remove the previous saved game from the preferences, if any
		ProgNPrefs.getIns().saveCurrentGameState(false, null);
		String currentBoard = mColorBoard.toString();
		ProgNPrefs.getIns().saveNewBoard(true, currentBoard);
		
		trackGameStart(currentBoard);
	}
	
	
	
	/** Get the color of the tile in the top-left corner
	 * @return color as number*/
	public int getFirstTileColor(){
		return mColorBoard.getMatrix()[0][0];
	}
	
	/** Get the color of the tile in the bottom-right corner
	 * @return color as number*/
	public int getLastTileColor(){
		int lastIndex = mColorBoard.getBlocksPerSide()-1;
		return mColorBoard.getMatrix()[lastIndex][lastIndex];
	}
	
	public JSONObject getBoardAsJson(){
		return mColorBoard.getCurrentState();
	}	
}