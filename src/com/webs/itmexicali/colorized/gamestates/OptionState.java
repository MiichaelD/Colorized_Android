package com.webs.itmexicali.colorized.gamestates;

import com.webs.itmexicali.colorized.GameActivity;
import com.webs.itmexicali.colorized.GameView;
import com.webs.itmexicali.colorized.Preferences;
import com.webs.itmexicali.colorized.R;
import com.webs.itmexicali.colorized.drawcomps.DrawButton;
import com.webs.itmexicali.colorized.drawcomps.DrawButtonContainer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.MotionEvent;

public class OptionState extends BaseState {

	RectF base;
	MainState ms;
	float dy;
	StaticLayout mLayout;
	DrawButtonContainer options;
	
	TextPaint smallText;
	
	protected OptionState(statesIDs id){
		super(id);
		ms = ((MainState)StateMachine.getIns().getFirstState());
		
		options = new DrawButtonContainer(5,true);
		
		for(int i =0;i<3;i++){
			registerButtons2Dificulties(i);
		}
		
		options.setOnActionListener(3, DrawButtonContainer.RELEASE_EVENT, new DrawButton.ActionListener(){
			@Override public void onActionPerformed() {
				new Thread(new Runnable(){public void run(){
					Preferences.getIns().toggleMusic();
				}}).start();}
		});
		
		options.setOnActionListener(4, DrawButtonContainer.RELEASE_EVENT, new DrawButton.ActionListener(){
			@Override public void onActionPerformed() {
				new Thread(new Runnable(){public void run(){
					Preferences.getIns().toggleSFX();
				}}).start();}
		});

		smallText = new TextPaint();
		smallText.setColor(Color.WHITE);
		
	}
	
	/** link each button to set a difficulty in a game*/
	private void registerButtons2Dificulties(final int i){
		options.setOnActionListener(i, DrawButtonContainer.RELEASE_EVENT, new DrawButton.ActionListener(){
			@Override public void onActionPerformed() {
				new Thread(new Runnable(){public void run(){
					Preferences.getIns().setDifficulty(i);
				}}).start();}
		});
	}

	@Override
	public void draw(Canvas canvas, boolean isPortrait) {
		ms.draw(canvas, isPortrait);
		canvas.drawRoundRect(base, ms.roundness, ms.roundness, ms.mPaints[11]);
		
		drawButtons(canvas);
		drawTexts(canvas);
	}
	
	/** Draw UI units capable to react to touch events*/
	private void drawButtons(Canvas canvas){
		for(int i =0 ; i < options.getButtonsCount();i++){
			canvas.drawRoundRect(options.getDButton(i), ms.roundness, ms.roundness, 
					 Preferences.getIns().getDifficulty() == i? ms.mPaints[1]: ms.mPaints[5]);
			
			if( options.getDButton(i).isPressed() )
				canvas.drawRoundRect(options.getDButton(i), ms.roundness, ms.roundness,  ms.mPaints[7]);
			
			switch(i){
			case 4:
				if(!Preferences.getIns().playMusic())
					canvas.drawRoundRect(options.getDButton(i), ms.roundness, ms.roundness,  ms.mPaints[7]);
				break;
			case 5:
				if(!Preferences.getIns().playSFX())
					canvas.drawRoundRect(options.getDButton(i), ms.roundness, ms.roundness,  ms.mPaints[7]);
				break;
			default:
				break;
			}
		}
	}
	
	private void drawTexts(Canvas canvas){
		DrawButton t;
		// Difficulty
		canvas.drawText(GameActivity.instance.getString(R.string.options_dificulty),
				GameView.width/2, base.top + 2*ms.mPaints[9].getTextSize(),ms.mPaints[9]);
		
		t = options.getDButton(0);
		canvas.drawText(GameActivity.instance.getString(R.string.options_easy),
				t.centerX(), t.centerY()+dy, smallText);
		
		t = options.getDButton(1);
		canvas.drawText(GameActivity.instance.getString(R.string.options_med),
				t.centerX(), t.centerY()+dy, smallText);
		
		t = options.getDButton(2);
		canvas.drawText(GameActivity.instance.getString(R.string.options_hard),
				t.centerX(), t.centerY()+dy, smallText);
				
	}
	
	
	@Override
	public void resize(float width, float height) {
		ms.resize(width, height);		
		ms.mPaints[11].setAlpha(235);
		ms.mPaints[9].setTextAlign(Align.CENTER);
		
		base = new RectF(width/16,height/8,15*width/16,7*height/8);
		
		smallText.setTextSize(GameView.mPortrait? GameView.width/18 : GameView.height/18);
		smallText.setTextAlign(Align.CENTER);
		dy = smallText.getTextSize()/3;
		
		options.repositionDButton(0, width/4, 14*height/48,3*width/4, 18*height/48); // play
		options.repositionDButton(1, width/4, 19*height/48,3*width/4, 23*height/48);// tuto
		options.repositionDButton(2, width/4, 24*height/48,3*width/4, 28*height/48); // leaderboard
		//options.repositionDButton(3, 5*width/8, 400,7*width/8, 490);				// achievements
	}
	
	public void onFocus(){
		super.onFocus();
	}

	public void onPopped(){
		ms = null;
	}
	
	@Override
	public boolean touch(MotionEvent event) {
		int action = event.getAction() & MotionEvent.ACTION_MASK;
		int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
		int pointerId = event.getPointerId(pointerIndex);
		switch (action) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_POINTER_DOWN:
				if(base.contains(event.getX(pointerIndex), event.getY(pointerIndex)))
					options.onPressUpdate(event, pointerIndex, pointerId);
				else
					StateMachine.getIns().popState();
				
				break;
	
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP:
			case MotionEvent.ACTION_CANCEL:	
				options.onReleaseUpdate(event, pointerIndex, pointerId);
				break;
				
	
			case MotionEvent.ACTION_MOVE:
				options.onMoveUpdate(event, pointerIndex);
				break;
			default:
				return false;
		}
		return true;
		
	}
	
}
