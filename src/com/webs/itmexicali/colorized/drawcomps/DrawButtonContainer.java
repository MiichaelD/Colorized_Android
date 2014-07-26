package com.webs.itmexicali.colorized.drawcomps;

import android.support.v4.view.ViewPager;
import android.view.MotionEvent;

/**This button container is to make easier the MotionEvent handling over DrawButtons
 * @author michael.duarte */
public class DrawButtonContainer {

	private final byte MAX_TOUCH_POINTERS = (byte)20;
	
	/** Use this constants to set onActionListeners for DrawButtons*/ 
	public static final byte PRESS_EVENT = 0, RELEASE_EVENT = 1, MOVE_EVENT = 2;
	
	//Container's buttons
	DrawButton buttons[];
	
	//Number of buttons this container will handle
	int num_buttons;
	
	//This array will keep the relation between pointerID and a buttonID
	byte point_button_rel[];
	
	/**Create a new DrawButtonContainer
	 * @param num_buttons amount of buttons to be handled	 */
	public DrawButtonContainer(int num_buttons){
		this.num_buttons = num_buttons;
		buttons = new DrawButton[num_buttons];
		point_button_rel = new byte[MAX_TOUCH_POINTERS];
	}
	
	/** Initialize the DrawButton specified by it's index
	 * the last 4 params are the specific coordinates for specific button.
	 * @param index the button's index, if its a greater than this containers capacity
	 * or less than zero, nothing will be initialized */
	public void initDrawButton(int index, float left, float top, float right, float bottom){
		if(index < num_buttons && index >= 0)
			buttons[index] = new DrawButton(left, top, right, bottom);
	}
	
	/**Get the DrawButton Specified by this index
	 * @param index the button's index, if its a greater than this containers capacity
	 * or less than zero, null will be returned
	 * @return a reference to the specified index */
	public DrawButton getDButton(int index){
		if(index < num_buttons && index >= 0)
			return buttons[index];
		return null;
	}	
	
	/** Bound an {@link DrawButton.ActionListener} listener to a {@link DrawButton}
	 * @param index button index
	 * @param event_type is the type of events registered in this class as constants 
	 * @param al the {@link DrawButton.ActionListener} that will be registered to given event and given button	 */
	public void setOnActionListener(int index, int event_type, DrawButton.ActionListener al){
		if(index < num_buttons && index >= 0){
			if (event_type == RELEASE_EVENT)
				buttons[index].setOnReleaseListener(al);
			else 
				buttons[index].setOnPressListener(al);
		}
	}
	
	/** Check {@link DrawButton}s has been Pressed by the Motion event
	 * @param event MotionEvent that triggered this update
	 * @param pointerIndex pointer Index from this MotionEvent */
	public void onPressUpdate(MotionEvent event, int pointerIndex, int touch){
		for(int i = 0 ; i < num_buttons; i++)
			if( !buttons[i].isPressed() )
				if(buttons[i].updatePress(event, pointerIndex, touch)){
					point_button_rel[touch] = (byte)i;
					break;
				}
	}
	
	/** Update the {@link DrawButton}'s state: {pressed:released}
	 * @param event MotionEvent that triggered this update
	 * @param pointerIndex pointer Index from this MotionEvent */
	public void onMoveUpdate(MotionEvent event, int pointerIndex){
		for(int i = 0 ; i < num_buttons; i++){
			buttons[i].updateMove(event, pointerIndex);
		}
	}
	
	/** Check if the {@link DrawButton}'s was released to update it
	 * @param event MotionEvent that triggered this update
	 * @param pointerIndex pointer Index from this MotionEvent */
	public void onReleaseUpdate(MotionEvent event, int pointerIndex, int touch){
		int i = point_button_rel[touch];
		if ( i != -1 && buttons[i].isPressed())
			buttons[i].updateRelease(event, pointerIndex);
		else{
			for(i = 0 ; i < num_buttons; i++){
				if( buttons[i].contains(event.getX(pointerIndex), event.getY(pointerIndex)))
					buttons[i].setPressed(false);
			}
		}
	}
	
	/** Get the number of buttons that this container has*/
	public int getButtonsCount(){
		return num_buttons;
	}
	
	/** Return the number of buttons being pressed*/
	public int getPressedButtons(){
		int i;
		for(i = 0 ; i < num_buttons; i++)
			if(buttons[i].isPressed())
				i++;
		return i;
	}
}
