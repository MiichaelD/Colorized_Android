package com.webs.itmexicali.colorized.gamestates;

import java.util.Iterator;
import java.util.LinkedList;

import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;

/** StateMachine handles the transition between states,
 * pushing new states to draw a custom window and popping
 * states to get to the previous state*/
public class StateMachine {

	//List of states
	private LinkedList<BaseState> stateList = null;
	
	//Singleton instance
	private static StateMachine instance;
	
	//prevent multiple instances of StateMachine
	private StateMachine(){
		stateList = new LinkedList<BaseState>();
	}
	
	/**Get the instance of {@Link StateMachine}
	 * @return Singleton */
	public static StateMachine getIns(){
		if(instance == null)
			instance = new StateMachine();
		return instance;
	}
	
	/** Push a new state to start interacting with it*/
	public void pushState(BaseState.statesIDs id){
		Log.i(StateMachine.class.getSimpleName(),"pushState by ID");
		
		//check if there the same state already on the lists
		Iterator<BaseState> it = stateList.iterator();
		while(it.hasNext()){
			if(it.next().mID == id)
				return;
		}
		//create a new BaseState
		BaseState bs = BaseState.createStateByID(id);
		
		//push it to the list
		stateList.addLast(bs);
		bs.onPushed();
	}
	
	/** Push a new state object to start interacting with it*/
	public void pushState(BaseState bs){
		Log.i(StateMachine.class.getSimpleName(),"pushState by Object Reference");
		
		//check if there the same state already on the lists
		Iterator<BaseState> it = stateList.iterator();
		while(it.hasNext()){
			if(it.next().mID == bs.getID())
				return;
		}
		stateList.addLast(bs);
		bs.onPushed();
	}
	
	/** Remove current state if any*/
	public boolean popState(){
		if(stateList.isEmpty())
			return false;
		stateList.removeLast().onPopped();
		return true;
	}
	
	/** Let the current state react on the back key*/
	public boolean onBackPressed(){
		if(stateList.isEmpty())
			return false;
		return stateList.getLast().onBackPressed();
	}
	
	/** Remove a state by it's ID if contained*/
	public void removeState(BaseState.statesIDs id){
		BaseState aux;
		//check if there the same state already on the lists
		Iterator<BaseState> it = stateList.iterator();
		while(it.hasNext()){
			aux = it.next();
			if(aux.mID == id){
				it.remove();
				return;
			}
		}
	}
	
	/**********************  CALLBACKS TO CURRENT STATE **********************/
	
	/** Provide the current state with the canvas to draw the UI*/
	public boolean draw(Canvas canvas, boolean isPortrait){
		if(stateList.isEmpty())
			return false;
		stateList.getLast().draw(canvas, isPortrait);
		return true;
	}
	
	/** Provide the current state with the MotionEvent to interact with the UI*/
	public boolean touch(MotionEvent me){
		if(stateList.isEmpty())
			return false;
		
		stateList.getLast().touch(me);
		return true;
	}
	
	/** Provide the current state with the MotionEvent to interact with the UI*/
	public boolean surfaceChanged(float width, float height){
		if(stateList.isEmpty())
			return false;
		
		stateList.getLast().surfaceChanged(width,height);
		return true;
	}
	
}
