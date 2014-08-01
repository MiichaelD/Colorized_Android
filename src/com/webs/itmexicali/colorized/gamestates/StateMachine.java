package com.webs.itmexicali.colorized.gamestates;

import java.util.LinkedList;

import android.graphics.Canvas;

/** StateMachine handles the transition between states,
 * pushing new states to draw a custom window and popping
 * states to get to the previous state*/
public class StateMachine {

	//List of states
	private LinkedList<BaseState> stateList = null;
	
	//Singleton instance
	private static StateMachine instance;
	
	public StateMachine(){
		stateList = new LinkedList<BaseState>();
	}
	
	/**Get the instance of {@Link StateMachine}
	 * @return Singleton */
	public static StateMachine getIns(){
		if(instance == null)
			instance = new StateMachine();
		return instance;
	}
	
	/** Remove current state if any*/
	public void popState(){
		stateList.removeLast();
	}
	
	/** Push a new state to start interacting with it*/
	public void pushState(BaseState bs){
		stateList.addLast(bs);
	}
	
	/** Provide the current state with the canvas to draw the UI*/
	public boolean draw(Canvas canvas){
		if(stateList.isEmpty())
			return false;
		
		stateList.getLast().draw(canvas);
		return true;
	}
	
}
