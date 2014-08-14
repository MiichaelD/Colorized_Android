package com.webs.itmexicali.colorized.gamestates;

import com.webs.itmexicali.colorized.GameActivity;
import com.webs.itmexicali.colorized.GameView;
import com.webs.itmexicali.colorized.Preferences;
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
	
	//button strings
	private String[] mButStr = null;
	private String mAppName = null;
	
	MainState(statesIDs id){
		super(id);
		
		mPaints = new TextPaint[12];
		dbc	= new DrawButtonContainer(7, true);
		mButStr = new String[7];
		
		//play button
		dbc.setOnActionListener(0, DrawButtonContainer.RELEASE_EVENT, new DrawButton.ActionListener(){
			@Override public void onActionPerformed() {
				new Thread(new Runnable(){public void run(){
					pushGame(false);
			}}).start();
		}});
		
		//Tutorial button
		dbc.setOnActionListener(1, DrawButtonContainer.RELEASE_EVENT, new DrawButton.ActionListener(){
			@Override public void onActionPerformed() {
				new Thread(new Runnable(){public void run(){
					pushGame(true);
				}}).start();
		}});
		
		//LEADERboards
		dbc.setOnActionListener(2, DrawButtonContainer.RELEASE_EVENT, new DrawButton.ActionListener(){
			@Override public void onActionPerformed() {
				new Thread(new Runnable(){public void run(){
					StateMachine.getIns().pushState(BaseState.statesIDs.LEADER);
				}}).start();}
		});
		
		//HOLDER for menu bitmap
		dbc.setOnActionListener(3, DrawButtonContainer.RELEASE_EVENT, new DrawButton.ActionListener(){
			@Override public void onActionPerformed() {
				new Thread(new Runnable(){public void run(){
					//TODO Settings
				}}).start();}
		});
		
		//music button
		dbc.setOnActionListener(4, DrawButtonContainer.RELEASE_EVENT, new DrawButton.ActionListener(){
			@Override public void onActionPerformed() {
				new Thread(new Runnable(){public void run(){
					Preferences.getIns().toggleMusic();
				}}).start();}
		});
		
		//sfx button
		dbc.setOnActionListener(5, DrawButtonContainer.RELEASE_EVENT, new DrawButton.ActionListener(){
			@Override public void onActionPerformed() {
				new Thread(new Runnable(){public void run(){
					Preferences.getIns().toggleSFX();
				}}).start();}
		});
		
		//About	
		dbc.setOnActionListener(6, DrawButtonContainer.RELEASE_EVENT, new DrawButton.ActionListener(){
			@Override public void onActionPerformed() {
				new Thread(new Runnable(){public void run(){
					StateMachine.getIns().pushState(BaseState.statesIDs.ABOUT);
				}}).start();}
		});
		
		//share button
		dbc.setOnActionListener(7, DrawButtonContainer.RELEASE_EVENT, new DrawButton.ActionListener(){
			@Override public void onActionPerformed() {
				new Thread(new Runnable(){public void run(){
					//TODO sharebutton
				}}).start();}
		});
		
		
	}
	
	//font text size modifiers, this helps to change the Xfactor to texts
	float ts0 = 1.0f, ts1=1.0f, ts2=1.0f, ts3=1.0f, ts4=1.0f, ts5=1.0f, ts6=1.0f;
	
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
		
		
		dbc.repositionDButton(0, width/4, 14*height/48,3*width/4, 22*height/48);
		dbc.repositionDButton(1, width/4, 23*height/48,3*width/4, 27*height/48);
		dbc.repositionDButton(2, width/4, 28*height/48,3*width/4, 32*height/48);
		//dbc.repositionDButton(3, 5*width/8, 400,7*width/8, 490);
		dbc.repositionDButton(4, width/8, 35*height/48,3*width/8, 38*height/48);
		dbc.repositionDButton(5, width/8, 39*height/48,3*width/8, 42*height/48);
		dbc.repositionDButton(6, 5*width/8, 39*height/48,7*width/8, 42*height/48);
		
		
		mAppName = GameActivity.instance.getString(R.string.app_name);
		mButStr[0]=GameActivity.instance.getString(R.string.play_button);
		mButStr[1]=GameActivity.instance.getString(R.string.tutorial_button);
		mButStr[2]=GameActivity.instance.getString(R.string.leader_button);
		mButStr[4]=GameActivity.instance.getString(R.string.music_button);
		mButStr[5]=GameActivity.instance.getString(R.string.sfx_button);
		mButStr[6]=GameActivity.instance.getString(R.string.about_button);
	}

	@Override
	public void draw(Canvas canvas, boolean isPortrait) {
		canvas.drawColor(Color.rgb(0, 162, 232));
		drawButtons(canvas);
		drawText(canvas);
	}
	
	/** Draw UI units capable to react to touch events*/
	public void drawButtons(Canvas canvas){
		for(int i =0 ; i < dbc.getButtonsCount();i++){
			canvas.drawRoundRect(dbc.getDButton(i), roundness, roundness,  mPaints[8]);
			if( dbc.getDButton(i).isPressed() )
				canvas.drawRoundRect(dbc.getDButton(i), roundness, roundness,  mPaints[7]);
			switch(i){
			case 4:
				if(!Preferences.getIns().playMusic())
					canvas.drawRoundRect(dbc.getDButton(i), roundness, roundness,  mPaints[7]);
				break;
			case 5:
				if(!Preferences.getIns().playSFX())
					canvas.drawRoundRect(dbc.getDButton(i), roundness, roundness,  mPaints[7]);
				break;
			default:
				break;
			}
		}
	}
	
	private void drawText(Canvas canvas){
		int paCo = 1;
		float x = GameView.width,	y = GameView.height;
		
		mPaints[8].setTextSize(GameView.mPortrait? x/7 : y/7);
		mPaints[8].setFakeBoldText(true);
		mPaints[8].setTextScaleX(ts0);
		while((mPaints[8].measureText(mAppName))+10 >= GameView.width){
			ts0-=0.03f;
			mPaints[8].setTextScaleX(ts0);
		}
		canvas.drawText(mAppName,x/2, 8*y/48, mPaints[8]);
		mPaints[8].setTextScaleX(1.0f);
		mPaints[8].setFakeBoldText(false);
		mPaints[8].setTextSize(GameView.mPortrait? x/13 : y/13);
		
		x = GameView.width/2;	y = 19*GameView.height/48;
		mPaints[paCo].setTextScaleX(ts1);
		mPaints[paCo].setTextSize(GameView.mPortrait? GameView.width/9 : GameView.height/9);
		while((mPaints[paCo].measureText(mButStr[0]))+10 >= dbc.getDButton(0).width()){
			ts1-=0.03f;
			mPaints[paCo].setTextScaleX(ts1);
		}
		canvas.drawText(mButStr[0], x, y, mPaints[paCo]);
		mPaints[paCo].setTextSize(GameView.mPortrait? GameView.width/14 : GameView.height/14);
		
		mPaints[paCo].setTextScaleX(ts2);
		while((mPaints[paCo].measureText(mButStr[1]))+10 >= dbc.getDButton(1).width()){
			ts2-=0.03f;
			mPaints[paCo].setTextScaleX(ts2);
		}
		canvas.drawText(mButStr[1], x, 26*GameView.height/48, mPaints[paCo]);
		
		mPaints[paCo].setTextScaleX(ts3);
		while((mPaints[paCo].measureText(mButStr[2]))+10 >= dbc.getDButton(2).width()){
			ts3-=0.03f;
			mPaints[paCo].setTextScaleX(ts3);
		}
		canvas.drawText(mButStr[2], x, 31*GameView.height/48, mPaints[paCo]);
		
		x = GameView.width/4;	y = 41*GameView.height/48;
		mPaints[paCo].setTextScaleX(ts4);
		while((mPaints[paCo].measureText(mButStr[4]))+10 >= dbc.getDButton(4).width()){
			ts4-=0.03f;
			mPaints[paCo].setTextScaleX(ts4);
		}
		canvas.drawText(mButStr[4], x, 37*GameView.height/48, mPaints[paCo]);
		
		mPaints[paCo].setTextScaleX(ts5);
		while((mPaints[paCo].measureText(mButStr[5]))+10 >= dbc.getDButton(5).width()){
			ts5-=0.03f;
			mPaints[paCo].setTextScaleX(ts5);
		}
		canvas.drawText(mButStr[5], x, y, mPaints[paCo]);
		mPaints[paCo].setTextScaleX(ts6);
		while((mPaints[paCo].measureText(mButStr[6]))+10 >= dbc.getDButton(6).width()){
			ts6-=0.03f;
			mPaints[paCo].setTextScaleX(ts6);
		}
		canvas.drawText(mButStr[6], 3*x, y, mPaints[paCo]);
		
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
			Preferences.getIns().setTutorialCompleted(false);
		StateMachine.getIns().pushState(BaseState.statesIDs.GAME);
	}
}
