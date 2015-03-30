package com.webs.itmexicali.colorized.gamestates;

import net.opentracker.android.OTLogService;

import com.webs.itmexicali.colorized.GameActivity;
import com.webs.itmexicali.colorized.GameView;
import com.webs.itmexicali.colorized.R;
import com.webs.itmexicali.colorized.board.GameBoardListener;
import com.webs.itmexicali.colorized.drawcomps.DrawButton;
import com.webs.itmexicali.colorized.drawcomps.DrawButtonContainer;
import com.webs.itmexicali.colorized.util.Const;
import com.webs.itmexicali.colorized.util.ProgNPrefs;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.MotionEvent;

public class OptionState extends BaseState {

	RectF base;
	MainState pMain;
	BaseState pPrevState;
	float dy;
	StaticLayout mLayout;
	DrawButtonContainer options;
	
	TextPaint smallText;
	
	int 	prevAlpha;
	//float 	animY; //ANIMATION
		
	protected OptionState(statesIDs id){
		super(id);
		
		options = new DrawButtonContainer(7,true);
		
		for(int i =0;i<3;i++){
			registerButtons2Dificulties(i);
		}
		
		options.setOnActionListener(3, DrawButtonContainer.RELEASE_EVENT, new DrawButton.ActionListener(){
			@Override public void onActionPerformed() {
				//play sound
				GameActivity.instance.playSound(GameActivity.SoundType.TOUCH);
				
				ProgNPrefs.getIns().setGameMode(Const.STEP);
				tryUpdateChangesNow(false);
		}});
		
		options.setOnActionListener(4, DrawButtonContainer.RELEASE_EVENT, new DrawButton.ActionListener(){
			@Override public void onActionPerformed() {
				//play sound
				GameActivity.instance.playSound(GameActivity.SoundType.TOUCH);
				
				ProgNPrefs.getIns().setGameMode(Const.CASUAL);
				tryUpdateChangesNow(false);
		}});

		
		options.setOnActionListener(5, DrawButtonContainer.RELEASE_EVENT, new DrawButton.ActionListener(){
			@Override public void onActionPerformed() {
				ProgNPrefs.getIns().toggleMusic();
		}});
		
		options.setOnActionListener(6, DrawButtonContainer.RELEASE_EVENT, new DrawButton.ActionListener(){
			@Override public void onActionPerformed() {
				ProgNPrefs.getIns().toggleSFX();
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
				
				ProgNPrefs.getIns().setDifficulty(i);
				
				tryUpdateChangesNow(false);
			}});
	}
	
	/** Check if we are over GameState, if so, try to make changes visible right away and
	 * closes the OptionState to be focused on GameState
	 * @param force if true it will not care if player is currently on a started board,
	 * if it is false, it will ask for user confirmation. */
	private void tryUpdateChangesNow(boolean force){
		if(pPrevState instanceof GameBoardListener){
			((GameBoardListener)pPrevState).restartBoard(force);
			StateMachine.getIns().popState();
		}
	}

	@Override
	public void draw(Canvas canvas, boolean isPortrait) {
		pPrevState.draw(canvas, isPortrait); 
		pMain.mPaints[11].setAlpha(235);
		
		/* //ANIMATION
		canvas.save();
		canvas.translate(0, animY);
		*/
		canvas.drawRoundRect(base, MainState.roundness, MainState.roundness, pMain.mPaints[11]);
		
		int dif = ProgNPrefs.getIns().getDifficulty();
		int mod = ProgNPrefs.getIns().getGameMode();
		pMain.mPaints[1].setTextSize(GameView.mPortrait?GameView.width/18:GameView.height/18);
		
		// Difficulty subtitle
		canvas.drawText(GameActivity.instance.getString(R.string.options_difficulty),
				GameView.width/2, base.top + 1.5f*pMain.mPaints[9].getTextSize(),pMain.mPaints[9]);
		
		// Mode subtitle
		canvas.drawText(GameActivity.instance.getString(R.string.options_game_mode),
				GameView.width/2, base.centerY() + 1*pMain.mPaints[9].getTextSize(),pMain.mPaints[9]);
		
		
		//Draw Board Size Buttons
		options.drawButtonsAndText(0, options.getButtonsCount(), canvas, MainState.roundness, pMain.mPaints[8],
				pMain.mPaints[7], pMain.mPaints[1], smallText);
		
		//paint different button on difficulty selected
		options.drawButtonAndText(dif, canvas, MainState.roundness, pMain.mPaints[1],
				pMain.mPaints[7], smallText, smallText);
		
		//paint different button on game mode selected
		options.drawButtonAndText(mod+3, canvas, MainState.roundness, pMain.mPaints[1],
				pMain.mPaints[7], smallText, smallText);
		
		
		
		if(ProgNPrefs.getIns().playMusic()) //paint music button different when on
			options.drawButtonAndText(5, canvas, MainState.roundness, pMain.mPaints[1],
					pMain.mPaints[7], smallText, smallText);

		if(ProgNPrefs.getIns().playSFX())//paint sounds button different when on
			options.drawButtonAndText(6, canvas, MainState.roundness, pMain.mPaints[1],
					pMain.mPaints[7], smallText, smallText);
		
		//canvas.restore(); //ANIMATION
		pMain.mPaints[11].setAlpha(prevAlpha);
	}
	
	
	@Override
	public void resize(float width, float height) {
		pPrevState.resize(width, height);
		prevAlpha = pMain.mPaints[11].getAlpha();
		base = new RectF(width/16,height/8,15*width/16,7*height/8);
		
		dy = height/48;
		options.repositionDButton(0, width/4, 11.0f*height/48,3*width/4, 14.0f*height/48); // play
		options.repositionDButton(1, width/4, 15.0f*height/48,3*width/4, 18.0f*height/48);// tuto
		options.repositionDButton(2, width/4, 19.0f*height/48,3*width/4, 22.0f*height/48); // leaderboard
		
		options.repositionDButton(3, width/4, 27.5f*height/48,3*width/4, 30.5f*height/48);// Step mode
		options.repositionDButton(4, width/4, 31.5f*height/48,3*width/4, 34.5f*height/48); //Casual mode
		
		options.repositionDButton(5, 3*width/16, base.bottom-5*dy,7.5f*width/16, base.bottom-2*dy); // music
		options.repositionDButton(6, 8.5f*width/16, base.bottom-5*dy,13*width/16, base.bottom-2*dy); //sounds
		
		pMain.mPaints[9].setTextAlign(Align.CENTER);
		smallText.setTextSize(GameView.mPortrait? width/18 : height/18);
		pMain.mPaints[1].setTextSize(GameView.mPortrait?width/18:height/18);
		smallText.setTextAlign(Align.CENTER);
		dy = smallText.getTextSize()/3;
		
	}
	
	
	@Override
	public void onPushed(){

		pMain = ((MainState)StateMachine.getIns().getFirstState());
		pPrevState = StateMachine.getIns().getPrevioustState();
		
		options.setText(0, GameActivity.instance.getString(R.string.options_easy));
		options.setText(1, GameActivity.instance.getString(R.string.options_med));
		options.setText(2, GameActivity.instance.getString(R.string.options_hard));

		options.setText(3, GameActivity.instance.getString(R.string.options_step_mode));
		options.setText(4, GameActivity.instance.getString(R.string.options_casual_mode));
		
		options.setText(5, GameActivity.instance.getString(R.string.music_button));
		options.setText(6, GameActivity.instance.getString(R.string.sfx_button));
		
		/* //ANIMATION
		animY = -GameView.height;
		
		Thread t1 = new Thread(new Runnable(){public void run(){
			options.setEnabled(false);
			while(animY < 0 ){
				animY+= 0.5f;
				try {
					Thread.sleep(0, 250);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			animY = 0;
			options.setEnabled(true);
		}});
		t1.setPriority(Thread.MAX_PRIORITY);
		t1.start();
		*/
		

		OTLogService.sendEvent("User entered to Options");
	}

	@Override
	public void onPopped(){
		pPrevState = null;
		pMain = null;
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
