package com.webs.itmexicali.colorized.gamestates;

import com.webs.itmexicali.colorized.Const;
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
		
		options = new DrawButtonContainer(7,true);
		
		for(int i =0;i<3;i++){
			registerButtons2Dificulties(i);
		}
		
		options.setOnActionListener(3, DrawButtonContainer.RELEASE_EVENT, new DrawButton.ActionListener(){
			@Override public void onActionPerformed() {
				Preferences.getIns().setGameMode(Const.STEP);
		}});
		
		options.setOnActionListener(4, DrawButtonContainer.RELEASE_EVENT, new DrawButton.ActionListener(){
			@Override public void onActionPerformed() {
				//TODO Casual Mode
				Preferences.getIns().setGameMode(Const.CASUAL);
		}});

		
		options.setOnActionListener(5, DrawButtonContainer.RELEASE_EVENT, new DrawButton.ActionListener(){
			@Override public void onActionPerformed() {
				Preferences.getIns().toggleMusic();
		}});
		
		options.setOnActionListener(6, DrawButtonContainer.RELEASE_EVENT, new DrawButton.ActionListener(){
			@Override public void onActionPerformed() {
				Preferences.getIns().toggleSFX();
		}});

		smallText = new TextPaint();
		smallText.setColor(Color.WHITE);
		
	}
	
	/** link each button to set a difficulty in a game*/
	private void registerButtons2Dificulties(final int i){
		options.setOnActionListener(i, DrawButtonContainer.RELEASE_EVENT, new DrawButton.ActionListener(){
			@Override public void onActionPerformed() {
				//play sound
				GameActivity.instance.playSound(GameActivity.SoundType.TOUCH);
				
				new Thread(new Runnable(){public void run(){
					Preferences.getIns().setDifficulty(i);
				}}).start();}
		});
	}

	@Override
	public void draw(Canvas canvas, boolean isPortrait) {
		ms.draw(canvas, isPortrait);
		canvas.drawRoundRect(base, ms.roundness, ms.roundness, ms.mPaints[11]);
		
		int dif = Preferences.getIns().getDifficulty();
		int mod = Preferences.getIns().getGameMode();
		ms.mPaints[1].setTextSize(GameView.mPortrait?GameView.width/18:GameView.height/18);
		
		// Difficulty subtitle
		canvas.drawText(GameActivity.instance.getString(R.string.options_difficulty),
				GameView.width/2, base.top + 1.5f*ms.mPaints[9].getTextSize(),ms.mPaints[9]);
		
		// Mode subtitle
		canvas.drawText(GameActivity.instance.getString(R.string.options_game_mode),
				GameView.width/2, base.centerY() + 1*ms.mPaints[9].getTextSize(),ms.mPaints[9]);
		
		
		//Draw Board Size Buttons
		options.drawButtonsAndText(0, options.getButtonsCount(), canvas, ms.roundness, ms.mPaints[8],
				ms.mPaints[7], ms.mPaints[1], smallText);
		
		//paint different button on difficulty selected
		options.drawButtonsAndText(dif, canvas, ms.roundness, ms.mPaints[1],
				ms.mPaints[7], smallText, smallText);
		
		//paint different button on game mode selected
		options.drawButtonsAndText(mod+3, canvas, ms.roundness, ms.mPaints[1],
				ms.mPaints[7], smallText, smallText);
		
		
		
		if(Preferences.getIns().playMusic()) //paint music button different when on
			options.drawButtonsAndText(5, canvas, ms.roundness, ms.mPaints[1],
					ms.mPaints[7], smallText, smallText);

		if(Preferences.getIns().playSFX())//paint sounds button different when on
			options.drawButtonsAndText(6, canvas, ms.roundness, ms.mPaints[1],
					ms.mPaints[7], smallText, smallText);
	}
	
	
	@Override
	public void resize(float width, float height) {
		ms.resize(width, height);		
		ms.mPaints[11].setAlpha(235);
		
		base = new RectF(width/16,height/8,15*width/16,7*height/8);
		
		dy = height/48;
		options.repositionDButton(0, width/4, 11.0f*height/48,3*width/4, 14.0f*height/48); // play
		options.repositionDButton(1, width/4, 15.0f*height/48,3*width/4, 18.0f*height/48);// tuto
		options.repositionDButton(2, width/4, 19.0f*height/48,3*width/4, 22.0f*height/48); // leaderboard
		
		options.repositionDButton(3, width/4, 27.5f*height/48,3*width/4, 30.5f*height/48);// Step mode
		options.repositionDButton(4, width/4, 31.5f*height/48,3*width/4, 34.5f*height/48); //Casual mode
		
		options.repositionDButton(5, 3*width/16, base.bottom-5*dy,7.5f*width/16, base.bottom-2*dy); // music
		options.repositionDButton(6, 8.5f*width/16, base.bottom-5*dy,13*width/16, base.bottom-2*dy); //sounds
		
		ms.mPaints[9].setTextAlign(Align.CENTER);
		smallText.setTextSize(GameView.mPortrait? width/18 : height/18);
		ms.mPaints[1].setTextSize(GameView.mPortrait?width/18:height/18);
		smallText.setTextAlign(Align.CENTER);
		dy = smallText.getTextSize()/3;
		
	}
	
	
	@Override
	public void onPushed(){
		
		options.setText(0, GameActivity.instance.getString(R.string.options_easy));
		options.setText(1, GameActivity.instance.getString(R.string.options_med));
		options.setText(2, GameActivity.instance.getString(R.string.options_hard));

		options.setText(3, GameActivity.instance.getString(R.string.options_step_mode));
		options.setText(4, GameActivity.instance.getString(R.string.options_casual_mode));
		
		options.setText(5, GameActivity.instance.getString(R.string.music_button));
		options.setText(6, GameActivity.instance.getString(R.string.sfx_button));
	}

	@Override
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
