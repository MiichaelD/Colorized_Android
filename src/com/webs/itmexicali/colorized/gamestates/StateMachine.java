package com.webs.itmexicali.colorized.gamestates;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;

/** StateMachine handles the transition between states,
 * pushing new states to draw a custom window and popping
 * states to get to the previous state*/
public class StateMachine {

	//List of states
	private StateList pStateList = null;
	
	//Singleton instance
	private static StateMachine instance;
	
	//prevent multiple instances of StateMachine
	private StateMachine(){
		pStateList = new StateList();
	}
	
	/**Get the instance of {@Link StateMachine}
	 * @return Singleton */
	public static StateMachine getIns(){
		if(instance == null)
			instance = new StateMachine();
		return instance;
	}
	
	/** Get the last {@link BaseState} pushed to this {@link StateMachine}*/
	public BaseState getCurrentState(){
		return pStateList.isEmpty() ? null : pStateList.getLast();
	}
	
	/** Get the previous to the last {@link BaseState} pushed to this {@link StateMachine}*/
	public BaseState getPrevioustState(){
		return pStateList.size() < 2 ? null : pStateList.getBeforeLast();
	}
	
	/** Push a new state to start interacting with it*/
	public void pushState(BaseState.statesIDs id){
		Log.i(StateMachine.class.getSimpleName(),"pushState by ID");
		
		if(checkStateIsInList(id))
			return;
		
		//create a new BaseState
		BaseState bs = BaseState.createStateByID(id);
		
		//push it to the list
		pStateList.addLast(bs);
		bs.onPushed();
	}
	
	/** Push a new state object to start interacting with it*/
	public void pushState(BaseState bs){
		Log.i(StateMachine.class.getSimpleName(),"pushState by Object Reference");
		
		if(checkStateIsInList(bs.getID()))
			return;
		
		pStateList.addLast(bs);
		bs.onPushed();
	}
	
	/** check if there the same state already on the lists */
	public boolean checkStateIsInList(BaseState.statesIDs id){
		Iterator<BaseState> it = pStateList.iterator();
		while(it.hasNext()){
			if(it.next().mID == id)
				return true;
		}
		return false;
	}
	
	/** Remove current state if any*/
	public boolean popState(){
		if(pStateList.isEmpty())
			return false;
		pStateList.removeLast().onPopped();
		return true;
	}
	
	
	/** Remove a state by it's ID if contained*/
	public void removeState(BaseState.statesIDs id){
		BaseState aux;
		//check if there the same state already on the lists
		Iterator<BaseState> it = pStateList.iterator();
		while(it.hasNext()){
			aux = it.next();
			if(aux.mID == id){
				aux.onPopped();
				it.remove();
				return;
			}
		}
	}
	
	/**********************  CALLBACKS TO CURRENT STATE **********************/
	
	/** Provide the current state with the canvas to draw the UI*/
	public boolean draw(Canvas canvas, boolean isPortrait){
		if(pStateList.isEmpty())
			return false;
		pStateList.getLast().draw(canvas, isPortrait);
		return true;
	}
	
	/** Provide the current state with the MotionEvent to interact with the UI*/
	public boolean touch(MotionEvent me){
		if(pStateList.isEmpty())
			return false;
		
		return pStateList.getLast().touch(me);
	}
	
	/** Provide the current state with the MotionEvent to interact with the UI*/
	public void surfaceChanged(float width, float height){
		if(pStateList.isEmpty())
			return;
		
		pStateList.getLast().surfaceChanged(width,height);
	}
	
	/** Let the current state react on the back key*/
	public boolean onBackPressed(){
		if(pStateList.isEmpty())
			return false;
		return pStateList.getLast().onBackPressed();
	}
	
	@SuppressWarnings("serial")
	private class StateList extends LinkedList<BaseState>{
		
		/** This method returns the state previous to the last one.
		 * This helps for {@link TutoState}, because it needs to paint over
		 * the layer of {@link GameState}
		 * @return The {@link StateBase} contained previous to the last one 
		 * NOTE: If there are not at least 2 items in the list, {@link NoSuchElementException}
		 * will be thrown.*/
		public BaseState getBeforeLast(){
			int size = size(),	i = 0;
			if(size >= 2){
				BaseState bs = null;
				Iterator<BaseState> it = iterator();
				for(;i<size-1; i++){
					bs = it.next();
				}
				return bs;
			}
			else
				throw new NoSuchElementException();
		}
	}
}
