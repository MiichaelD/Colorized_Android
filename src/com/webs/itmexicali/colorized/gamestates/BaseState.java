package com.webs.itmexicali.colorized.gamestates;

import com.webs.itmexicali.colorized.GameView;

import android.graphics.Canvas;
import android.view.MotionEvent;

public abstract class BaseState {
	
	/** The Identifiers for each state extending this class*/
	public static enum statesIDs{MAIN, TUTO, GAME, STATS, ABOUT, OPTION, OVER}
	
	
	public BaseState(statesIDs id){mID = id; }
	
	/** ID for this state*/
	protected statesIDs mID;
	
	/** Get this state identifier
	 * @returns mID */
	public final statesIDs getID() {
		return mID;
	}
		
	/** callback to let the state that it has been pushed on top
	 * of list*/
	public void onPushed(){}
	
	/** callback to let the state that it has been popped out from
	 * top of the list*/
	public void onPopped(){}
	
	/** callback to let the state know that it's been brought to the
	 * surface to start drawing. this method calls resize with canvas'
	 * size as param*/
	public void onFocus(){
		resize(GameView.width,GameView.height);
	}
	
	/** callback to let the state know that a new state is above current state,
	 * so it has lost it's focus*/
	public void onFocusLost(){}
	
	/** draw the UI on the main canvas
	 * @param canvas to draw on
	 * @isPortrait true if the device is in portrait orientation
	 * false if it's on landscape*/
	public abstract void draw(Canvas canvas, boolean isPortrait);
	
	/** handle the user's touch inputs
	 * @param me the MotionEvent to be handled*/
	public boolean touch(MotionEvent me){
		return false;
	}
	
	/** callback to let the state that it has been resized.
	 * This method calls resize method if not overriden
	 * @param width the canvas width
	 * @param height the canvas height*/
	public void surfaceChanged(float width, float height){
		resize(width,height);
	}
	
	/** In this method all the size dependent variables (if any) should be resized*/
	public void resize(float width, float height){}
	
	/** callback to let the state handle the back key*/
	public boolean onBackPressed(){
		return false;
	}
	
	/** Create a new State by it's ID and return it
	 * @param id the type of BaseState to create
	 * @return BaseState of type ID reference*/
	public static BaseState createStateByID(statesIDs id){
		switch(id){
		case ABOUT:
			return new AboutState(statesIDs.ABOUT);
		case GAME:
			return new GameState(statesIDs.GAME);
		case MAIN:
			return new MainState(statesIDs.MAIN);
		case OPTION:
			return new OptionState(statesIDs.OPTION);
		case OVER:
			return new GameOverState(statesIDs.OVER);
		case STATS:
			return new StatisticState(statesIDs.STATS);
		case TUTO:
			return new TutoState(statesIDs.TUTO);
		}
		return null;
	}
}
