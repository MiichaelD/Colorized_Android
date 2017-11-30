package com.webs.itmexicali.colorized.gamestates;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;

import com.webs.itmexicali.colorized.util.Log;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * StateMachine handles the transition between states,
 * pushing new states to draw a custom window and popping
 * states to get to the previous state
 */
public class StateMachine {

  public static Context mContext;
  //Singleton instance
  private static StateMachine instance;
  //List of states
  private StateList pStateList = null;

  //prevent multiple instances of StateMachine
  private StateMachine(Context ctx) {
    mContext = ctx;
    pStateList = new StateList();
  }

  public static void setup(Context ctx) {
    if (instance == null) {
      instance = new StateMachine(ctx);
    } else {
      throw new IllegalStateException("You can only setup StateMachine ONCE!");
    }
  }

  public static boolean isSetUp() {
    return instance != null;
  }

  /**
   * Get the instance of {@link StateMachine}
   *
   * @return Singleton
   */
  public static StateMachine getIns() {
    if (instance == null)
      throw new IllegalStateException("You should setup StateMachine before using it");
    return instance;
  }

  /**
   * Get the first {@link BaseState} pushed to this {@link StateMachine}
   */
  public BaseState getFirstState() {
    return pStateList.isEmpty() ? null : pStateList.getFirst();
  }

  /**
   * Get the last {@link BaseState} pushed to this {@link StateMachine}
   */
  public BaseState getCurrentState() {
    return pStateList.isEmpty() ? null : pStateList.getLast();
  }

  /**
   * Get the previous to the last {@link BaseState} pushed to this {@link StateMachine}
   */
  public BaseState getPrevioustState() {
    return pStateList.size() < 2 ? null : pStateList.getBeforeLast();
  }

  /**
   * Push a new state to start interacting with it
   */
  public void pushState(BaseState.statesIDs id) {
    Log.i(StateMachine.class.getSimpleName(), "pushState by ID: " + id);

    if (checkStateIsInList(id))
      return;

    //create a new BaseState and push it to the list
    pushAtEnd(BaseState.stateFactory(id));
  }

  /**
   * Push a new state object to start interacting with it
   */
  public void pushState(BaseState bs) {
    Log.i(StateMachine.class.getSimpleName(), "pushState by Object Reference");

    if (checkStateIsInList(bs.getID()))
      return;

    pushAtEnd(bs);
  }

  /**
   * After validating that there is no state with same ID in the list, push it in the list
   */
  private void pushAtEnd(BaseState bs) {
    //let the previous state that it has lost focus
    if (pStateList.size() > 0)
      pStateList.getLast().onFocusLost();

    pStateList.addLast(bs);
    bs.onPushed();
    bs.onFocus();
  }

  /**
   * check if there the same state already on the lists
   *
   * @param id state id to be checked
   * @return true if the state is in the list
   */
  public boolean checkStateIsInList(BaseState.statesIDs id) {
    Iterator<BaseState> it = pStateList.iterator();
    BaseState bs = null;
    while (it.hasNext()) {
      bs = it.next();
      //Log.v(StateMachine.class.getSimpleName(),"States: "+bs.getID());
      if (bs.mID == id)
        return true;
    }
    return false;
  }

  /**
   * Remove current state if any
   */
  public boolean popState() {
    if (pStateList.isEmpty())
      return false;
    BaseState bs = pStateList.removeLast();
    bs.onFocusLost();
    bs.onPopped();

    if (pStateList.size() > 0)
      pStateList.getLast().onFocus();
    return true;
  }

  /**
   * Get back in the stack to given state if it exists
   */
  public void getBackToState(BaseState.statesIDs id) {
    if (!checkStateIsInList(id))
      throw new NoSuchElementException("Given id is not in the list");

    while (!pStateList.isEmpty()) {
      if (pStateList.getLast().mID == id)
        return;

      //remove last state, calling the callback methods
      popState();
    }

  }

  /**
   * Remove a state by it's ID if contained
   * NOTE: use carefully; Why would you want to remove this state?
   */
  public void removeState(BaseState.statesIDs id) {
    BaseState aux;
    //check if there the same state already on the lists
    Iterator<BaseState> it = pStateList.iterator();
    while (it.hasNext()) {
      aux = it.next();
      if (aux.mID == id) {
        //if it is the last state let it know it lost focus
        if (aux == pStateList.getLast()) {
          //remove last state, calling the callback methods
          popState();
        } else {
          //let it know we are taking it off from list
          aux.onPopped();
          it.remove();
        }
        return;
      }
    }
  }

  /**********************  CALLBACKS TO CURRENT STATE **********************/

  /**
   * Provide the current state with the canvas to draw the UI
   */
  public boolean draw(Canvas canvas, boolean isPortrait) {
    if (pStateList.isEmpty())
      return false;
    pStateList.getLast().draw(canvas, isPortrait);
    return true;
  }

  /**
   * Provide the current state with the MotionEvent to interact with the UI
   */
  public boolean touch(MotionEvent me) {
    if (pStateList.isEmpty())
      return false;

    return pStateList.getLast().touch(me);
  }

  /**
   * Provide the current state with the MotionEvent to interact with the UI
   */
  public void surfaceChanged(float width, float height) {
    if (pStateList.isEmpty())
      return;

    pStateList.getLast().surfaceChanged(width, height);
  }

  /**
   * Let the current state react on the back key
   */
  public boolean onBackPressed() {
    if (pStateList.isEmpty()) // if there are states continue
      return false;

    if (pStateList.getLast().onBackPressed())// let the current state react
      return true;

    return popState();// if no reaction, drop from list
  }

  @SuppressWarnings("serial")
  private class StateList extends LinkedList<BaseState> {

    /**
     * This method returns the state previous to the last one.
     * This helps for {@link TutoState}, because it needs to paint over
     * the layer of {@link GameState}
     *
     * @return The {@link StateBase} contained previous to the last one
     * NOTE: If there are not at least 2 items in the list, {@link NoSuchElementException}
     * will be thrown.
     */
    public BaseState getBeforeLast() {
      int size = size(), i = 0;
      if (size >= 2) {
        BaseState bs = null;
        Iterator<BaseState> it = iterator();
        for (; i < size - 1; i++) {
          bs = it.next();
        }
        return bs;
      } else
        throw new NoSuchElementException();
    }
  }
}
