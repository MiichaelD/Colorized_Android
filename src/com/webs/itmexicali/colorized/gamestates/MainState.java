package com.webs.itmexicali.colorized.gamestates;

import com.webs.itmexicali.colorized.GameActivity;
import com.webs.itmexicali.colorized.GameView;
import com.webs.itmexicali.colorized.Prefs;
import com.webs.itmexicali.colorized.drawcomps.DrawButton;
import com.webs.itmexicali.colorized.drawcomps.DrawButtonContainer;
import com.webs.itmexicali.colorized.R;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.text.TextPaint;
import android.view.MotionEvent;

public class MainState extends BaseState {
	
	//paints to be used in the canvas
	public TextPaint 				mPaints[];
	private DrawButtonContainer 	dbc;
	public float 					roundness;
	
	private String mAppName = null;
	
	MainState(statesIDs id){
		super(id);
		
		mPaints = new TextPaint[12];
		dbc	= new DrawButtonContainer(6, true);
		
		//play button
		dbc.setOnActionListener(0, DrawButtonContainer.RELEASE_EVENT, new DrawButton.ActionListener(){
			@Override public void onActionPerformed() {
				//play sound
				GameActivity.instance.playSound(GameActivity.SoundType.TOUCH);
				
				new Thread(new Runnable(){public void run(){
					pushGame(false);
				}}).start();
		}});
		
		//Tutorial button
		dbc.setOnActionListener(1, DrawButtonContainer.RELEASE_EVENT, new DrawButton.ActionListener(){
			@Override public void onActionPerformed() {
				//play sound
				GameActivity.instance.playSound(GameActivity.SoundType.TOUCH);
				
				new Thread(new Runnable(){public void run(){
					pushGame(true);
				}}).start();
		}});
		
		//Options button
		dbc.setOnActionListener(2, DrawButtonContainer.RELEASE_EVENT, new DrawButton.ActionListener(){
			@Override public void onActionPerformed() {
				//play sound
				GameActivity.instance.playSound(GameActivity.SoundType.TOUCH);
				
				new Thread(new Runnable(){public void run(){
					StateMachine.getIns().pushState(BaseState.statesIDs.OPTION);
				}}).start();}
		});
		
		//About	
		dbc.setOnActionListener(3, DrawButtonContainer.RELEASE_EVENT, new DrawButton.ActionListener(){
			@Override public void onActionPerformed() {
				//play sound
				GameActivity.instance.playSound(GameActivity.SoundType.TOUCH);
				
				new Thread(new Runnable(){public void run(){
					StateMachine.getIns().pushState(BaseState.statesIDs.ABOUT);
				}}).start();}
		});
		
		//LEADERboards
		dbc.setOnActionListener(4, DrawButtonContainer.RELEASE_EVENT, new DrawButton.ActionListener(){
			@Override public void onActionPerformed() {
				//play sound
				GameActivity.instance.playSound(GameActivity.SoundType.TOUCH);
				
				new Thread(new Runnable(){public void run(){
					StateMachine.getIns().pushState(BaseState.statesIDs.LEADER);
				}}).start();}
		});
		
		//Achievements
		dbc.setOnActionListener(5, DrawButtonContainer.RELEASE_EVENT, new DrawButton.ActionListener(){
			@Override public void onActionPerformed() {
				//play sound
				GameActivity.instance.playSound(GameActivity.SoundType.TOUCH);
				
				new Thread(new Runnable(){public void run(){
					//TODO Achievements
					StateMachine.getIns().pushState(BaseState.statesIDs.LEADER);
				}}).start();}
		});
		
		/*
		//MUSIC button
		dbc.setOnActionListener(6, DrawButtonContainer.RELEASE_EVENT, new DrawButton.ActionListener(){
			@Override public void onActionPerformed() {
				Preferences.getIns().toggleMusic();
		}});
		
		//SFX button
		dbc.setOnActionListener(7, DrawButtonContainer.RELEASE_EVENT, new DrawButton.ActionListener(){
			@Override public void onActionPerformed() {
				Preferences.getIns().toggleSFX();
		}});
		*/
	}
	
	//font text size modifiers, this helps to change the Xfactor to texts
	float ts0 = 1.0f, ts1=1.0f, ts2=1.0f, ts3=1.0f, ts4=1.0f, ts5=1.0f, ts6=1.0f, ts7=1.0f;
	
	@Override
	public void resize(float width, float height) {
		roundness = GameView.height/48;
			
		mPaints[0] = new TextPaint();//RED 
		mPaints[0].setColor(Color.RED);
		mPaints[0].setStyle(Paint.Style.FILL);
		mPaints[0].setAntiAlias(true);
		
		mPaints[1] = new TextPaint();//BLUE 
		mPaints[1].setColor(Color.rgb(0, 162, 232));
		mPaints[1].setStyle(Paint.Style.FILL);
		mPaints[1].setTextSize(GameView.mPortrait? width/14 : height/14);
		//mPaints[1].setTextSize(GameView.mPortrait? width/9 : height/9);
		mPaints[1].setTextAlign(Align.CENTER);
		mPaints[1].setAntiAlias(true);		
		
		mPaints[2] = new TextPaint();//YELLOW 
		mPaints[2].setColor(Color.YELLOW);
		mPaints[2].setStyle(Paint.Style.FILL);
		mPaints[2].setAntiAlias(true);
		
		mPaints[3] = new TextPaint();//PURPLE
		mPaints[3].setColor(Color.rgb(163, 73, 164));
		mPaints[3].setStyle(Paint.Style.FILL);
		mPaints[3].setAntiAlias(true);
		
		mPaints[4] = new TextPaint();//GRAY
		mPaints[4].setColor(Color.LTGRAY);
		mPaints[4].setStyle(Paint.Style.FILL);
		mPaints[4].setAntiAlias(true);
		
		mPaints[5] = new TextPaint();//GREEN
		mPaints[5].setColor(Color.rgb(21, 183, 46));
		//mPaints[5].setColor(Color.rgb(0, 159, 60));
		mPaints[5].setStyle(Paint.Style.FILL);
		mPaints[5].setAntiAlias(true);
		
		mPaints[6] = new TextPaint(); //SWEEP GRADIENT
		Shader sh = null;
		sh = new SweepGradient(5, 5, new int[] {Color.BLUE,	Color.RED, Color.GREEN}, null);
		//mPaints[6].setShader(new LinearGradient( 0, 0, 0, 2, Color.CYAN, Color.GREEN, Shader.TileMode.CLAMP));
		//mPaints[6].setShader(new LinearGradient(0, 1, 1, 0, Color.BLUE, Color.RED, Shader.TileMode.CLAMP));
		mPaints[6].setShader(sh);
		mPaints[6].setStyle(Paint.Style.FILL);
		mPaints[6].setAntiAlias(true);
		
		mPaints[7] = new TextPaint(); // DKGRAY for pushed buttons
		mPaints[7].setColor(Color.DKGRAY);
		mPaints[7].setStyle(Paint.Style.FILL);
		mPaints[7].setAntiAlias(true);
		mPaints[7].setAlpha(120);
		
		mPaints[8] = new TextPaint(); // WHITE for TEXT
		mPaints[8].setColor(Color.WHITE);
		mPaints[8].setStyle(Paint.Style.FILL);
		mPaints[8].setTextSize(GameView.mPortrait? width/13 : height/13);
		mPaints[8].setTextAlign(Align.CENTER);
		mPaints[8].setAntiAlias(true);
		
		mPaints[9] = new TextPaint(); //TEXTS on Tuto and About
		mPaints[9].setColor(Color.CYAN);
		mPaints[9].setStyle(Paint.Style.FILL);
		mPaints[9].setTextSize(GameView.mPortrait? width/14 : height/14);
		mPaints[9].setAntiAlias(true);
		
		mPaints[10] = new TextPaint(); //SELECTIONS
		mPaints[10].setColor(Color.WHITE);
		mPaints[10].setStyle(Paint.Style.STROKE);
		mPaints[10].setStrokeWidth(width/50);
		mPaints[10].setAntiAlias(true);
		
		mPaints[11] = new TextPaint(0);//BACKGROUND
		mPaints[11].setColor(0xff101010);
		mPaints[11].setAlpha(170);
		mPaints[11].setStyle(Paint.Style.FILL);
		mPaints[11].setMaskFilter(new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL));
		
		dbc.repositionDButton(0, width/4, 14*height/48,3*width/4, 22*height/48); // play
		dbc.repositionDButton(1, width/4, 23*height/48,3*width/4, 27*height/48);// tuto
		dbc.repositionDButton(2, width/4, 28*height/48,3*width/4, 32*height/48);//options
		dbc.repositionDButton(3, width/4, 33*height/48,3*width/4, 37*height/48); // about
		dbc.repositionDButton(4, 1.5f*width/16, 42*height/48, 7.5f*width/16, 45*height/48); // Leaderboard
		dbc.repositionDButton(5, 8.5f*width/16, 42*height/48, 14.5f*width/16, 45*height/48); //Achievements
		//dbc.repositionDButton(6, width/8, 38*height/48,3*width/8, 41*height/48); //music
		//dbc.repositionDButton(7, width/8, 42*height/48, 7*width/16, 45*height/48); // sounds
		
		mAppName = GameActivity.instance.getString(R.string.app_name);
		dbc.setText(0, GameActivity.instance.getString(R.string.play_button));
		dbc.setText(1, GameActivity.instance.getString(R.string.tutorial_button));
		dbc.setText(2, GameActivity.instance.getString(R.string.options_button));
		dbc.setText(3, GameActivity.instance.getString(R.string.about_button));
		dbc.setText(4, GameActivity.instance.getString(R.string.leader_button));
		dbc.setText(5, GameActivity.instance.getString(R.string.achiev_button));		
	}

	@Override
	public void draw(Canvas canvas, boolean isPortrait) {
		canvas.drawColor(Color.rgb(0, 162, 232));
		
		drawTitle(canvas, mAppName);//draw Title
		
		//draw play button BIGGER
		mPaints[1].setTextSize(GameView.mPortrait? GameView.width/9 : GameView.height/9);
		dbc.drawButtonsAndText(0, canvas, roundness, mPaints[8], mPaints[7], mPaints[1], mPaints[1]);
		mPaints[1].setTextSize(GameView.mPortrait? GameView.width/14 : GameView.height/14);
		
		//draw the rest of the buttons
		dbc.drawButtonsAndText(1,dbc.getButtonsCount(), canvas, roundness, mPaints[8], mPaints[7], mPaints[1], mPaints[1]);
		
	}
	
	public void drawTitle(Canvas canvas, String text){
		float x = GameView.width,	y = GameView.height;
		
		mPaints[8].setTextSize(GameView.mPortrait? x/7 : y/7);
		mPaints[8].setFakeBoldText(true);
		mPaints[8].setTextScaleX(ts0);
		while((mPaints[8].measureText(text))+10 >= GameView.width){
			ts0-=0.05f;
			mPaints[8].setTextScaleX(ts0);
		}
		canvas.drawText(text,x/2, 8*y/48, mPaints[8]);
		mPaints[8].setTextScaleX(1.0f);
		mPaints[8].setFakeBoldText(false);
		mPaints[8].setTextSize(GameView.mPortrait? x/13 : y/13);
		
		//canvas.drawText("DOWN",GameView.width/2,GameView.height,mPaints[8]);
	}

	@Override
	public boolean touch(MotionEvent event) {
		int action = event.getAction() & MotionEvent.ACTION_MASK;
		int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
		int pointerId = event.getPointerId(pointerIndex);
		switch (action) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_POINTER_DOWN:
			dbc.onPressUpdate(event, pointerIndex, pointerId);
			break;

		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
		case MotionEvent.ACTION_CANCEL:	
			dbc.onReleaseUpdate(event, pointerIndex, pointerId);
			break;

		case MotionEvent.ACTION_MOVE:
			dbc.onMoveUpdate(event, pointerIndex);
			break;
		default:
			return false;
		}
		return true;
	}

	@Override
	public boolean onBackPressed() {
		GameActivity.instance.showExitDialog();
		return true;
	}


	/** Push the action phase state 
	 * @param tuto true to enable tutorial, regardless if it was
	 * already completed once*/
	private void pushGame(boolean tuto){
		if(tuto)
			Prefs.getIns().setTutorialCompleted(false);
		StateMachine.getIns().pushState(BaseState.statesIDs.GAME);
	}
}
