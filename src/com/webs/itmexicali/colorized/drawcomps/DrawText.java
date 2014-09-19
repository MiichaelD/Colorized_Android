package com.webs.itmexicali.colorized.drawcomps;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.RectF;

public class DrawText  extends RectF{

	private float	mTextXfact = 1.0f; 
	private String	mText = null;
	
	
	/** Create a new {@link DrawText} with the specified coordinates and text.
	 * Note: no range checking is performed, so the caller must
	 * ensure that left <= right and top <= bottom.
	 * @param text The text given to this button;
	 * @param left  The X coordinate of the left side of the DrawText 
	 * @param top  The Y coordinate of the top of the DrawText
	 * @param right  The X coordinate of the right side of the DrawText 
	 * @param bottom  The Y coordinate of the bottom of the DrawText	*/
	public DrawText(String text, float left, float top, float right, float bottom){
		super(left,top,right,bottom);
		mText = text;
	}
	
	/** Create a new {@link DrawText} with the specified coordinates.
	 * Note: no range checking is performed, so the caller must
	 * ensure that left <= right and top <= bottom.
	 * @param left  The X coordinate of the left side of the DrawText 
	 * @param top  The Y coordinate of the top of the DrawText
	 * @param right  The X coordinate of the right side of the DrawText 
	 * @param bottom  The Y coordinate of the bottom of the DrawText	*/
	public DrawText(float left, float top, float right, float bottom){
		super(left,top,right,bottom);
	}
	
	/** Create a new {@link DrawText} with its coordinates equal to 0
	 * @param left  The X coordinate of the left side of the DrawText 
	 * @param top  The Y coordinate of the top of the DrawText
	 * @param right  The X coordinate of the right side of the DrawText 
	 * @param bottom  The Y coordinate of the bottom of the DrawText	*/
	public DrawText(){
		super();
	}
	
	
	/** Draw {@link DrawText} on given canvas also with the text centered in the button
	 * If the text is too wide, this method will scale it (only the X length) 
	 * @param c Canvas to draw on
	 * @param round the roundness of the button
	 * @param txtCol Paint containing the color of the text*/
	public void draw(Canvas c, float round, Paint textCol){
		
		Paint.Align ta = textCol.getTextAlign();
		if(mText!= null){
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
	}
	
	
	
	public void setText(String txt){
		mText = txt;
	}
	
	public String getText(){
		return mText;
	}
	
	/** This interface was designed to be bounded to {@link DrawText}
	 * so they can do a given action.*/ 
	public static interface ActionListener{
		public void onActionPerformed();
	}
	
}