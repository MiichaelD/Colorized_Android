package com.webs.itmexicali.colorized.gamestates;

import java.util.HashMap;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.webs.itmexicali.colorized.GameActivity;
import com.webs.itmexicali.colorized.GameView;
import com.webs.itmexicali.colorized.drawcomps.DrawButton;
import com.webs.itmexicali.colorized.drawcomps.DrawButtonContainer;
import com.webs.itmexicali.colorized.util.BitmapLoader;
import com.webs.itmexicali.colorized.util.ProgNPrefs;
import com.webs.itmexicali.colorized.util.Tracking;
import com.webs.itmexicali.colorized.R;

import android.graphics.Bitmap;
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
	public static float 			roundness;
	
	private Bitmap pBitmap[] = null;
	
	private String mAppName = null, playerName = null;
	
	MainState(statesIDs id){
		super(id);
		
		mPaints = new TextPaint[12];
		dbc	= new DrawButtonContainer(8, true);
		pBitmap = new Bitmap[4];
		
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
				HashMap<String, Object> map = new HashMap<String,Object>();
				map.put("Intentionally", true);
				Tracking.shared().track("Tutorial", map);
				
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
				Tracking.shared().track("About", null);
				//play sound
				GameActivity.instance.playSound(GameActivity.SoundType.TOUCH);
				
				new Thread(new Runnable(){public void run(){
					StateMachine.getIns().pushState(BaseState.statesIDs.ABOUT);
					//StateMachine.getIns().pushState(BaseState.statesIDs.TEST);
				}}).start();}
		});
		
		//LEADERboards
		dbc.setOnActionListener(4, DrawButtonContainer.RELEASE_EVENT, new DrawButton.ActionListener(){
			@Override public void onActionPerformed() {
				//play sound
				GameActivity.instance.playSound(GameActivity.SoundType.TOUCH);
				
				GameActivity.instance.onShowLeaderboardsRequested();
			}});
		
		//Achievements
		dbc.setOnActionListener(5, DrawButtonContainer.RELEASE_EVENT, new DrawButton.ActionListener(){
			@Override public void onActionPerformed() {
				//play sound
				GameActivity.instance.playSound(GameActivity.SoundType.TOUCH);
				
				GameActivity.instance.onShowAchievementsRequested();
		}});
		
		//Sign in
		dbc.setOnActionListener(6, DrawButtonContainer.RELEASE_EVENT, new DrawButton.ActionListener(){
			@Override public void onActionPerformed() {
				Tracking.shared().track("Sign-in", null);
				dbc.setEnabled(6,false);
				//play sound
				GameActivity.instance.playSound(GameActivity.SoundType.TOUCH);
				
				GameActivity.instance.onSignInButtonClicked();
		}});
		
		//Google Games
		dbc.setOnActionListener(7, DrawButtonContainer.RELEASE_EVENT, new DrawButton.ActionListener(){
			@Override public void onActionPerformed() {
				dbc.setEnabled(6,false);
				//play sound
				GameActivity.instance.playSound(GameActivity.SoundType.TOUCH);
				
				
		}});
	}
	
	//font text size modifiers, this helps to change the Xfactor to texts
	float ts0 = 1.0f, ts1=1.0f;
	
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
		
		//If we have google play services 
		if(GooglePlayServicesUtil.isGooglePlayServicesAvailable(GameActivity.instance.getApplicationContext())
    			== ConnectionResult.SUCCESS
    			){
			dbc.repositionDButton(4, 1.5f*width/16, 40*height/48, 7.5f*width/16, 44*height/48); // Leaderboard
			dbc.repositionDButton(5, 8.5f*width/16, 40*height/48, 14.5f*width/16, 44*height/48); //Achievements

			dbc.setText(4, GameActivity.instance.getString(R.string.leader_button));
			dbc.setText(5, GameActivity.instance.getString(R.string.achiev_button));
			
			playerName = GameActivity.instance.getPlayerName();
			if(playerName == null){
				pBitmap[0] = BitmapLoader.resizeImage(StateMachine.mContext, R.drawable.white_signin_medium_base_44dp,
						false, 2*width/5, height/12);
				pBitmap[1] = BitmapLoader.resizeImage(StateMachine.mContext, R.drawable.white_signin_medium_press_44dp,
						false, 2*width/5, height/12);
				dbc.setEnabled(6,true); 
			}
			else{
				playerName = String.format(GameActivity.instance.getString(R.string.greeting_player),playerName);
				dbc.setEnabled(6,false);
			}			
			//position sign in button
			dbc.repositionDButton(6, GameView.width/2 - pBitmap[0].getWidth()/2, 9.25f*GameView.height/48, //sign in
					GameView.width/2 + pBitmap[0].getWidth()/2, 13.25f*height/48);

			/*
			// google play games icon
			pBitmap[2] = BitmapLoader.resizeImage(StateMachine.mContext, R.drawable.play_games_ic,
					height/12, height/12);
			pBitmap[3] = BitmapLoader.resizeImage(StateMachine.mContext, R.drawable.play_games_pressed_ic,
					height/12, height/12);
			dbc.repositionDButton(7, 1.5f*width/16, 40*height/48, 1.5f*width/16+height/12, 40*height/48+height/12); // Play games Icon
			*/
		}else{
			dbc.repositionDButton(4, width/4, 39*height/48, 3*width/4, 43*height/48); // statistics
			dbc.setText(4, GameActivity.instance.getString(R.string.stats_button)); //statistics
		}
		
		mAppName = GameActivity.instance.getString(R.string.app_name);
		dbc.setText(0, GameActivity.instance.getString(R.string.play_button));
		dbc.setText(1, GameActivity.instance.getString(R.string.tutorial_button));
		dbc.setText(2, GameActivity.instance.getString(R.string.options_button));
		dbc.setText(3, GameActivity.instance.getString(R.string.about_button));
		
	}

	@Override
	public void draw(Canvas canvas, boolean isPortrait) {
		//canvas.drawColor(Color.rgb(0, 162, 232));
		//canvas.drawColor(Color.DKGRAY);
		
		drawTitle(canvas, mAppName);//draw Title
		
		if(playerName != null){
			mPaints[8].setTextScaleX(ts1);
			while((mPaints[8].measureText(playerName))+10 >= GameView.width){
				ts1-=0.05f;
				mPaints[8].setTextScaleX(ts1);
			}
			canvas.drawText(playerName, GameView.width/2, 12*GameView.height/48, mPaints[8]);
			mPaints[8].setTextScaleX(1.0f);
		}else if (pBitmap[0] != null && pBitmap[1] != null){
			canvas.drawBitmap(dbc.getDButton(6).isPressed()?pBitmap[1]:pBitmap[0],
					GameView.width/2 - pBitmap[0].getWidth()/2, 9.25f*GameView.height/48, null);
		}
		
		if(pBitmap[2]!=null){
			canvas.drawBitmap(dbc.getDButton(7).isPressed()?pBitmap[3]:pBitmap[2],1.5f*GameView.width/16, 40*GameView.height/48, null);
		}
		
		//draw play button BIGGER
		mPaints[1].setTextSize(GameView.mPortrait? GameView.width/9 : GameView.height/9);
		dbc.drawButtonAndText(0, canvas, roundness, mPaints[8], mPaints[7], mPaints[1], mPaints[1]);
		mPaints[1].setTextSize(GameView.mPortrait? GameView.width/14 : GameView.height/14);
		
		//draw the rest of the buttons
		dbc.drawButtonsAndText(1,dbc.getButtonsCount()-2, canvas, roundness, mPaints[8], mPaints[7], mPaints[1], mPaints[1]);
		
	}
	
	public void drawTitle(Canvas canvas, String text){
		float x = GameView.width,	y = GameView.height;
		
		mPaints[8].setTextSize(GameView.mPortrait? x/7 : y/7);
		mPaints[8].setTextScaleX(ts0);
		mPaints[8].setFakeBoldText(true);
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
			ProgNPrefs.getIns().setTutorialCompleted(false);
		StateMachine.getIns().pushState(BaseState.statesIDs.GAME);
	}
}
