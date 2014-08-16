package com.webs.itmexicali.colorized.drawcomps;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import android.text.TextPaint;
import android.view.MotionEvent;

public class DrawButton  extends RectF{

	private boolean isPressed;
	private int		touchID;
	private float	mTextXfact = 1.0f; 
	private String	mText = null;

	private ActionListener onPress, onRelease;
	
	
	/** Create a new {@link DrawButton} with the specified coordinates and text.
	 * Note: no range checking is performed, so the caller must
	 * ensure that left <= right and top <= bottom.
	 * @param text The text given to this button;
	 * @param left  The X coordinate of the left side of the DrawButton 
	 * @param top  The Y coordinate of the top of the DrawButton
	 * @param right  The X coordinate of the right side of the DrawButton 
	 * @param bottom  The Y coordinate of the bottom of the DrawButton	*/
	public DrawButton(String text, float left, float top, float right, float bottom){
		super(left,top,right,bottom);
		mText = text;
	}
	
	/** Create a new {@link DrawButton} with the specified coordinates.
	 * Note: no range checking is performed, so the caller must
	 * ensure that left <= right and top <= bottom.
	 * @param left  The X coordinate of the left side of the DrawButton 
	 * @param top  The Y coordinate of the top of the DrawButton
	 * @param right  The X coordinate of the right side of the DrawButton 
	 * @param bottom  The Y coordinate of the bottom of the DrawButton	*/
	public DrawButton(float left, float top, float right, float bottom){
		super(left,top,right,bottom);
	}
	
	/** Create a new {@link DrawButton} with its coordinates equal to 0
	 * @param left  The X coordinate of the left side of the DrawButton 
	 * @param top  The Y coordinate of the top of the DrawButton
	 * @param right  The X coordinate of the right side of the DrawButton 
	 * @param bottom  The Y coordinate of the bottom of the DrawButton	*/
	public DrawButton(){
		super();
	}
	
	
	/** Draw {@link DrawButton} on given canvas
	 * @param c Canvas to draw on
	 * @param round the roundness of the button
	 * @param butCol Paint containing the color of the button*/
	public void draw(Canvas c, float round, Paint butCol ){
		c.drawRoundRect(this, round, round, butCol);
	}
	
	/** Draw {@link DrawButton} on given canvas
	 * @param c Canvas to draw on
	 * @param round the roundness of the button
	 * @param relCol Paint containing the color of the button on release mode
	 * @param presCol Paint containing the color of the button on pressed mode*/
	public void draw(Canvas c, float round, Paint relCol, Paint presCol ){
		c.drawRoundRect(this, round, round, isPressed?presCol:relCol);
	}
	
	/** Draw {@link DrawButton} on given canvas also with the text centered in the button
	 * If the text is too wide, this method will scale it (only the X length) 
	 * @param c Canvas to draw on
	 * @param round the roundness of the button
	 * @param relCol Paint containing the color of the button on release mode
	 * @param presCol Paint containing the color of the button on pressed mode
	 * @param textCol Paint containing the size and color to draw on the text*/
	public void draw(Canvas c, float round, Paint relCol, Paint presCol, TextPaint textCol){
		c.drawRoundRect(this, round, round, isPressed?presCol:relCol);
		
		Paint.Align ta = textCol.getTextAlign();
		textCol.setTextAlign(Align.CENTER);
		textCol.setTextScaleX(mTextXfact);
		while((textCol.measureText(mText))+10 >= width()){
			mTextXfact-=0.05f;
			textCol.setTextScaleX(mTextXfact);
		}
		c.drawText(mText, centerX(), centerY()+textCol.getTextSize()/3, textCol);
		textCol.setTextScaleX(1.0f);
		textCol.setTextAlign(ta);
	}
	
	/** Draw {@link DrawButton} on given canvas also with the text centered in the button
	 * If the text is too wide, this method will scale it (only the X length) 
	 * @param c Canvas to draw on
	 * @param round the roundness of the button
	 * @param relCol Paint containing the color of the button on release mode
	 * @param presCol Paint containing the color of the button on pressed mode
	 * @param textColRel Paint containing the size and color to draw on the text on release mode
	 * @param textColRel Paint containing the size and color to draw on the text on pressed mode*/
	public void draw(Canvas c, float round, Paint relCol, Paint presCol, TextPaint textColRel, TextPaint textColPres){
		draw(c,round,relCol,presCol,isPressed?textColPres:textColRel);
	}
	
	
	public boolean isPressed(){
		return isPressed;
	}
	
	public void setPressed(boolean pressed){
		 isPressed = pressed;
	}
	
	public boolean contains(float x, float y){
		isPressed = super.contains(x,y);
		return isPressed;
	}
	
	/** Check if the current button is being pressed by the Motion event
	 * @param event MotionEvent that triggered this update
	 * @param pointerIndex pointer Index from this MotionEvent
	 * @param touch current touchID
	 * @return the true if it's being pressed else false */
	public boolean updatePress(MotionEvent event, int pointerIndex, int touch){
		if (!isPressed && contains(event.getX(pointerIndex), event.getY(pointerIndex))){
			touchID = touch;//event.getPointerId(pointerIndex);
			if(onPress != null)
				onPress.onActionPerformed();
		}
		return isPressed;
	}
	
	/** Check if the current button has been released by the Motion event
	 * @param event MotionEvent that triggered this update
	 * @param pointerIndex pointer Index from this MotionEvent
	 * @return the true if it's being pressed else false */
	public boolean updateRelease(MotionEvent event, int pointerIndex){
		if (isPressed && contains(event.getX(pointerIndex), event.getY(pointerIndex))){
			isPressed = false;
			if( touchID != -1 ){//was pressed and linked to a touch ID since updatePress (no exiting the button)
				touchID = -1;
				if(onRelease != null)
					onRelease.onActionPerformed();
			}
		}
		return isPressed;
	}
	
	/** this is to update the button (to know if it is still being pressed)
	 * @param event MotionEvent that triggered this update
	 * @param pointerIndex pointer Index from this MotionEvent
	 * @return the true if it's being pressed else false */
	public boolean updateMove(MotionEvent event, int pointerIndex){
		if(isPressed ){
			if (!contains(event.getX(pointerIndex), event.getY(pointerIndex)))
				touchID = -1;
		}else {
			contains(event.getX(pointerIndex), event.getY(pointerIndex));
		}			
		return isPressed;
	}
	
	public void setOnPressListener(ActionListener al){
		onPress = al;
	}
	
	public void setOnReleaseListener(ActionListener al){
		onRelease = al;
	}
	
	
	public void setText(String txt){
		mText = txt;
	}
	
	/** This interface was designed to be bounded to {@link DrawButton}
	 * so they can do a given action.*/ 
	public static interface ActionListener{
		public void onActionPerformed();
	}
	
}