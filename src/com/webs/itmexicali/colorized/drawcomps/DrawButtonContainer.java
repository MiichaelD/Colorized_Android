package com.webs.itmexicali.colorized.drawcomps;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.view.MotionEvent;

/**This button container is to make easier the MotionEvent handling over DrawButtons
 * @author michael.duarte */
public class DrawButtonContainer {

	private final byte MAX_TOUCH_POINTERS = (byte)20;
	
	/** Use this constants to set onActionListeners for DrawButtons*/ 
	public static final byte PRESS_EVENT = 0, RELEASE_EVENT = 1, MOVE_EVENT = 2;
	
	//Container's buttons
	private DrawButton buttons[];
	
	//Number of buttons this container will handle
	private int num_buttons;
	
	//This array will keep the relation between pointerID and a buttonID
	private byte point_button_rel[];
	
	/**Create a new DrawButtonContainer
	 * @param num_buttons amount of buttons to be handled
	 * @param initButtons whether let the constructor to initialize the
	 * buttons with its coordinates equal to 0*/
	public DrawButtonContainer(int num_buttons, boolean initButtons){
		this.num_buttons = num_buttons;
		buttons = new DrawButton[num_buttons];
		point_button_rel = new byte[MAX_TOUCH_POINTERS];
		if(initButtons)
			for(int i = 0; i < num_buttons; i++)
				buttons[i] = new DrawButton();
	}
	
	/** Initialize the {@link DrawButton} specified by it's index
	 * the last 4 parameters are the specific coordinates for specific button.
	 * @param text String given to the button
	 * @param index the button's index, if its a greater than this containers capacity
	 * or less than zero, nothing will be initialized */
	public void initDrawButton(String text, int index, float left, float top, float right, float bottom){
		if(index < num_buttons && index >= 0)
			buttons[index] = new DrawButton(text, left, top, right, bottom);
		else 
			throw new NullPointerException("There is no draw button with index:"+index+" to init");
	}
	
	
	
	/** Initialize the {@link DrawButton} specified by it's index
	 * the last 4 parameters are the specific coordinates for specific button.
	 * @param index the button's index, if its a greater than this containers capacity
	 * or less than zero, nothing will be initialized */
	public void initDrawButton(int index, float left, float top, float right, float bottom){
		if(index < num_buttons && index >= 0)
			buttons[index] = new DrawButton(left, top, right, bottom);
		else 
			throw new NullPointerException("There is no draw button with index:"+index+" to init");
	}
	
	/** Change the given {@link DrawButton}'s position
	 * the first parameter is the index of the {@link DrawButton}
	 * the last 4 parameters are the specific coordinates for specific button.
	 * @param index the button's index
	 * @param l left bound of button
	 * @param t top bound of button
	 * @param r right bound of button
	 * @param b bottom bound of button*/
	public void repositionDButton(int index, float l, float t, float r, float b){
		if(index < num_buttons && index >= 0)
			buttons[index].set(l, t, r, b);
		else 
			throw new NullPointerException("There is no draw button with index:"+index+" to reposition");
	}
	
	/**Get the DrawButton Specified by this index
	 * @param index the button's index, if its a greater than this containers capacity
	 * or less than zero, null will be returned
	 * @return a reference to the specified index */
	public DrawButton getDButton(int index){
		if(index < num_buttons && index >= 0)
			return buttons[index];
		else 
			throw new NullPointerException("There is no draw button with index:"+index+" to return");
	}
	
	/**Set a text to DrawButton Specified by this index
	 * @param index the button's index, if its a greater than this containers capacity
	 * or less than zero, NullPointerException will be thrown.
	 * @param text text to be assign to button with given index */
	public void setText(int index, String text){
		if(index < num_buttons && index >= 0)
			buttons[index].setText(text);
		else 
			throw new NullPointerException("There is no draw button with index:"+index+" to return");
	}
	
	/** Bound an {@link DrawButton.ActionListener} to a {@link DrawButton}
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
	public int getPressedButtonsCounter(){
		int i,j;
		for(i = 0, j = 0; i < num_buttons; i++)
			if(buttons[i].isPressed())
				j++;
		return j;
	}
	
	/** Paint all {@link DrawButton} on given canvas
	 * @param c Canvas to draw on
	 * @param r the roundness of the button
	 * @param relCol Paint containing the color of the button on release mode
	 * @param presCol Paint containing the color of the button on pressed mode*/
	public void drawButtons(Canvas c, float r, Paint relCol, Paint presCol){
		drawButtons(0, num_buttons, c, r, relCol, presCol);
	}
	
	/** Paint selected {@link DrawButton} on given canvas
	 * @param st starting index
	 * @param end ending index, not inclusive
	 * @param c Canvas to draw on
	 * @param r the roundness of the button
	 * @param relCol Paint containing the color of the button on release mode
	 * @param presCol Paint containing the color of the button on pressed mode*/
	public void drawButtons(int st, int end, Canvas c, float r, Paint relCol, Paint presCol){
		for(int i =0 ; i< num_buttons; i++){
			buttons[i].draw(c, r, relCol, presCol);
		}
	}
	
	/** Paint all {@link DrawButton} with their texts on given canvas also with the text centered in the button
	 * If the text is too wide, this method will scale it (only the X length) 
	 * @param c Canvas to draw on
	 * @param r the roundness of the button
	 * @param rc Paint containing the color of the button on release mode
	 * @param pc Paint containing the color of the button on pressed mode
	 * @param trc Paint containing the size and color to draw on the text on release mode
	 * @param tpc Paint containing the size and color to draw on the text on pressed mode*/
	public void drawButtonsAndText(Canvas c, float r, Paint rc, Paint pc, TextPaint trc, TextPaint tpc ){
		this.drawButtonsAndText(0, num_buttons, c, r, rc, pc, trc, tpc);
	}
	
	/** Paint selected {@link DrawButton}s with their texts on given canvas also with the text centered in the button
	 * If the text is too wide, this method will scale it (only the X length) 
	 * @param st starting index
	 * @param end ending index, not inclusive
	 * @param c Canvas to draw on
	 * @param r the roundness of the button
	 * @param rc Paint containing the color of the button on release mode
	 * @param pc Paint containing the color of the button on pressed mode
	 * @param trc Paint containing the size and color to draw on the text on release mode
	 * @param tpc Paint containing the size and color to draw on the text on pressed mode*/
	public void drawButtonsAndText(int st, int end, Canvas c, float r, Paint rc, Paint pc, TextPaint trc, TextPaint tpc ){
		for(int i =st ; i< end; i++){
			buttons[i].draw(c, r, rc, pc, trc, tpc);
		}
	}
	
	/** Paint selected {@link DrawButton} with their texts on given canvas also with the text centered in the button
	 * If the text is too wide, this method will scale it (only the X length) 
	 * @param st selected button's index
	 * @param c Canvas to draw on
	 * @param r the roundness of the button
	 * @param rc Paint containing the color of the button on release mode
	 * @param pc Paint containing the color of the button on pressed mode
	 * @param trc Paint containing the size and color to draw on the text on release mode
	 * @param tpc Paint containing the size and color to draw on the text on pressed mode*/
	public void drawButtonsAndText(int st, Canvas c, float r, Paint rc, Paint pc, TextPaint trc, TextPaint tpc ){
		buttons[st].draw(c, r, rc, pc, trc, tpc);
	}
	
	
	
	
}
