package com.webs.itmexicali.colorized.gamestates;

import com.webs.itmexicali.colorized.GameView;

import android.graphics.Canvas;
import android.view.MotionEvent;

public abstract class BaseState {
	
	/** The Identifiers for each state extending this class*/
	public static enum statesIDs{MENU, TUTO, GAME}
	
	/** ID for this state*/
	protected statesIDs mID;
	
	/** Get this state identifier
	 * @returns mID */
	public final statesIDs getID() {
		return mID;
	}
		
	/** callback to let the state that it has been pushed on top*/
	public void onPushed(){
		resize(GameView.width,GameView.height);
	}
	
	/** callback to let the state that it has been popped out from top*/
	public abstract void onPopped();
	
	/** draw the UI on the main canvas
	 * @param canvas to draw on
	 * @isPortrait true if the device is in portrait orientation
	 * false if it's on landscape*/
	public abstract void draw(Canvas canvas, boolean isPortrait);
	
	/** handle the user's touch inputs
	 * @param me the MotionEvent to be handled*/
	public abstract boolean touch(MotionEvent me);
	
	/** callback to let the state that it has been resized
	 * @param width the canvas width
	 * @param height the canvas height*/
	public void surfaceChanged(float width, float height){
		resize(width,height);
	}
	
	/** In this method all the size dependant variables should be resized*/
	public abstract void resize(float width, float height);
	
	/** callback to let the state handle the back key*/
	public abstract boolean onBackPressed();
	
	/** Create a new State by it's ID and return it
	 * @param id the type of BaseState to create
	 * @return BaseState of type ID reference*/
	public static BaseState createStateByID(statesIDs id){
		switch(id){
		case MENU:
			break;
		case TUTO:
			return new TutoState();
		case GAME:
			return new GameState();
		}
		return null;
	}
}
