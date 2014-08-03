package com.webs.itmexicali.colorized.gamestates;

import com.webs.itmexicali.colorized.Const;
import com.webs.itmexicali.colorized.GameActivity;
import com.webs.itmexicali.colorized.GameView;
import com.webs.itmexicali.colorized.R;

import android.content.SharedPreferences;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.MotionEvent;

public class TutoState extends BaseState{
	
	public TutoState(){
		mID = statesIDs.TUTO;
	}

	enum innerStates{INIT, FIRST, SEC, THIRD, FOURTH, FIFTH, BACK, FINAL}
	
	private innerStates mInnerState, mPreState;
	
	//paints to be used on canvas
	protected TextPaint 	mPaints[];
	
	@Override
	public void onPopped() {
		Log.d(TutoState.class.getSimpleName(),"onPopped");		
	}
	
	
	@Override
	public void surfaceChanged(float width, float height) {
		Log.d(TutoState.class.getSimpleName(),"surfaceChanged");
		resizePaints(width, height);
		
	}

	@Override
	public void onPushed() {
		Log.d(TutoState.class.getSimpleName(),"onPushed");
		mInnerState = innerStates.INIT;	
		resizePaints(GameView.width,GameView.height);
	}
	
	private void resizePaints(float width, float height){
		Log.v("TutoState","canvas size: "+width+"x"+height);
		mPaints = new TextPaint[5];
		
		mPaints[0] = new TextPaint();
		mPaints[0].setColor(Color.WHITE);
		mPaints[0].setStyle(Paint.Style.FILL);
		mPaints[0].setTextSize(GameView.mPortrait? width/11 : height/11);
		mPaints[0].setTextAlign(Align.CENTER);
		mPaints[0].setAntiAlias(true);
		
		mPaints[1] = new TextPaint();
		mPaints[1].setColor(Color.CYAN);
		mPaints[1].setStyle(Paint.Style.FILL);
		mPaints[1].setTextSize(GameView.mPortrait? width/11 : height/11);
		mPaints[1].setAntiAlias(true);
		
		mPaints[2] = new TextPaint();
		mPaints[2].setColor(Color.RED);
		mPaints[2].setStyle(Paint.Style.STROKE);
		mPaints[2].setTextSize(GameView.mPortrait? width/11 : height/11);
		mPaints[2].setAntiAlias(true);
		
		mPaints[3] = new TextPaint(0);
		mPaints[3].setColor(0xff101010);
		mPaints[3].setAlpha(170);
		mPaints[3].setStyle(Paint.Style.FILL);
		mPaints[3].setMaskFilter(new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL));
		
		mPaints[4] = new TextPaint();
		Shader sh = null;
		sh = new SweepGradient(5, 5, new int[] {Color.BLUE,	Color.RED, Color.GREEN}, null);
		mPaints[4].setShader(sh);
		mPaints[4].setStyle(Paint.Style.FILL);
		mPaints[4].setAntiAlias(true);
	}

	@Override
	public void draw(Canvas canvas, boolean isPotrait) {
		canvas.drawRect(new Rect(0,0,(int)GameView.width,(int)GameView.height),mPaints[3]);
		canvas.drawRect(new Rect(canvas.getWidth()/4,0,3*canvas.getWidth()/4,(int)GameView.height/14),mPaints[4]);
		
		canvas.drawText(GameActivity.instance.getString(R.string.tutorial),
				GameView.width/2, GameView.height/20,	mPaints[0]);
		
		
		StaticLayout mTextLayout;
		canvas.save();
		switch(mInnerState){
		case INIT:
			mTextLayout = new StaticLayout(
					GameActivity.instance.getString(R.string.tuto0), mPaints[1],
					canvas.getWidth(), Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);

				canvas.translate(0, GameView.height/2);
				mTextLayout.draw(canvas);
			break;
		case FIRST:
			
			mTextLayout = new StaticLayout(
				GameActivity.instance.getString(R.string.tuto1), mPaints[1],
				canvas.getWidth(), Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);

			canvas.translate(0, GameView.height/2);
			mTextLayout.draw(canvas);
			
			break;
		case SEC:
			mTextLayout = new StaticLayout(
				GameActivity.instance.getString(R.string.tuto2), mPaints[1],
				canvas.getWidth(), Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);

			canvas.translate(0, GameView.height/2);
			mTextLayout.draw(canvas);
			break;
		case THIRD:	
			mTextLayout = new StaticLayout(
					GameActivity.instance.getString(R.string.tuto3), mPaints[1],
					canvas.getWidth(), Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);

				canvas.translate(0, GameView.height/2);
				mTextLayout.draw(canvas);
			break;
		case FOURTH:	
			mTextLayout = new StaticLayout(
					GameActivity.instance.getString(R.string.tuto4), mPaints[1],
					canvas.getWidth(), Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);

				canvas.translate(0, GameView.height/2);
				mTextLayout.draw(canvas);
			break;
		case FIFTH:
			mTextLayout = new StaticLayout(
					GameActivity.instance.getString(R.string.tuto5), mPaints[1],
					canvas.getWidth(), Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);

				canvas.translate(0, GameView.height/2);
				mTextLayout.draw(canvas);
			break;
		case BACK:
			mTextLayout = new StaticLayout(
					GameActivity.instance.getString(R.string.tuto_exit), mPaints[1],
					canvas.getWidth(), Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);

				canvas.translate(0, GameView.height/2);
				mTextLayout.draw(canvas);
			break;
		case FINAL:
			mTextLayout = new StaticLayout(
					GameActivity.instance.getString(R.string.tuto6), mPaints[1],
					canvas.getWidth(), Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);

				canvas.translate(0, GameView.height/2);
				mTextLayout.draw(canvas);
			break;
		}
		canvas.restore();
	}
	

	private void saveTutorialFinish(){
		Log.i(TutoState.class.getSimpleName(),"tutorial FInished");
		GameActivity a = GameActivity.instance;
		SharedPreferences sh = a.getSharedPreferences(Const.TAG,0);
		SharedPreferences.Editor editor = sh.edit();
		editor.putBoolean(a.getString(R.string.key_tutorial_played), true).commit();
	}

	@Override
	public void touch(MotionEvent me) {
		int action = me.getAction() & MotionEvent.ACTION_MASK;
		if(action == MotionEvent.ACTION_UP ||
			action == MotionEvent.ACTION_POINTER_UP ||
			action == MotionEvent.ACTION_CANCEL){
			switch(mInnerState){
			case INIT: mInnerState = innerStates.FIRST; break;
			case FIRST: mInnerState = innerStates.SEC; break;
			case SEC: mInnerState = innerStates.THIRD; break;
			case THIRD: mInnerState = innerStates.FOURTH; break;
			case FOURTH: mInnerState = innerStates.FIFTH; break;
			case FIFTH: mInnerState = innerStates.FINAL; break;
			case BACK: mInnerState = mPreState; break;
			case FINAL: saveTutorialFinish(); 
					StateMachine.getIns().popState(); break;
			}
			Log.d(TutoState.class.getSimpleName(),"mState: "+mInnerState);
		}
		
	}
	
	public boolean onBackPressed(){
		if(mInnerState != innerStates.BACK){
			mPreState = mInnerState;
			mInnerState = innerStates.BACK;
			return true;
		}			
		return false;
	}
	
}
