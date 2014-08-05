package com.webs.itmexicali.colorized.gamestates;

import com.webs.itmexicali.colorized.GameActivity;
import com.webs.itmexicali.colorized.GameView;
import com.webs.itmexicali.colorized.Preferences;
import com.webs.itmexicali.colorized.R;

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
import android.view.MotionEvent;

public class TutoState extends BaseState{
	
	public TutoState(){
		mID = statesIDs.TUTO;
	}

	enum innerStates{INIT, FIRST, SEC, THIRD, FOURTH, FIFTH, BACK, FINAL}
	
	private innerStates mInnerState, mPreState;
	
	//paints to be used on canvas
	protected TextPaint 	mPaints[];
	protected Rect			mRectFs[];
	
	@Override
	public void onPopped() {
		//Log.d(TutoState.class.getSimpleName(),"onPopped");		
	}
	
	
	@Override
	public void surfaceChanged(float width, float height) {
		//Log.d(TutoState.class.getSimpleName(),"surfaceChanged");
		resize(width, height);		
	}

	@Override
	public void onPushed() {
		//Log.d(TutoState.class.getSimpleName(),"onPushed");
		mInnerState = innerStates.INIT;	
		resize(GameView.width,GameView.height);
	}
	
	private void resize(float width, float height){
		//Log.v("TutoState","canvas size: "+width+"x"+height);
		
		mRectFs = new Rect[3];
		mRectFs[0] = new Rect(0, (int)(2*height/5+23*width/48), (int)(width), (int)(2*height/5+31*width/48));
		
		float boardPixels = GameView.getIns().mRectFs[0].width()/Preferences.getIns().getBoardSize();
		mRectFs[1] = new Rect((int)(width/16), (int)(5*height/16-5*width/16), 
				(int)(width/16 + boardPixels), (int)(5*height/16-5*width/16 + boardPixels));
		
		mRectFs[2] = new Rect((int)(3*GameView.width/16), 0,
				(int)(13*GameView.width/16),(int)(GameView.height/96+GameView.width/8));
		
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
		mPaints[1].setTextSize(GameView.mPortrait? width/14 : height/14);
		mPaints[1].setAntiAlias(true);
		
		mPaints[2] = new TextPaint();
		mPaints[2].setColor(Color.WHITE);
		mPaints[2].setStyle(Paint.Style.STROKE);
		mPaints[2].setStrokeWidth(width/50);
		mPaints[2].setTextSize(GameView.mPortrait? width/14 : height/14);
		mPaints[2].setAntiAlias(true);
		
		mPaints[3] = new TextPaint(0);
		mPaints[3].setColor(0xff101010);
		mPaints[3].setAlpha(170);
		mPaints[3].setStyle(Paint.Style.FILL);
		mPaints[3].setMaskFilter(new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL));
		
		mPaints[4] = new TextPaint();
		Shader sh = null;
		sh = new SweepGradient(5, 5, new int[] {Color.BLUE,	Color.RED}, null);
		mPaints[4].setShader(sh);
		mPaints[4].setStyle(Paint.Style.FILL);
		mPaints[4].setAntiAlias(true);
	}

	@Override
	public void draw(Canvas canvas, boolean isPotrait) {
		//canvas.drawRect(new Rect(0,0,(int)GameView.width,(int)GameView.height),mPaints[3]);
		canvas.drawColor(mPaints[3].getColor());
		
		StaticLayout mTextLayout;
		canvas.save();
		switch(mInnerState){
		case INIT:// touch to continue
			
			canvas.drawText(GameActivity.instance.getString(R.string.tutorial),
					GameView.width/2, GameView.height/3,	mPaints[0]);
			
			mTextLayout = new StaticLayout(
					GameActivity.instance.getString(R.string.tuto0), mPaints[1],
					canvas.getWidth(), Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);

				canvas.translate(0, GameView.height/2-mPaints[1].getTextSize());
				mTextLayout.draw(canvas);
			break;
			
		case FIRST://fill the board
			
			mTextLayout = new StaticLayout(
				GameActivity.instance.getString(R.string.tuto1), mPaints[1],
				canvas.getWidth(), Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);

			canvas.drawRoundRect(GameView.getIns().mRectFs[0],0,0,mPaints[2]);
			GameView.getIns().mColorBoard.updateBoard(canvas, GameView.getIns().mRectFs[0], GameView.getIns().mPaints);
			canvas.translate(0, 5*GameView.height/16+5*GameView.width/8);
			mTextLayout.draw(canvas);
			
			break;
		case SEC: // start with first tile
			mTextLayout = new StaticLayout(
				GameActivity.instance.getString(R.string.tuto2), mPaints[1],
				canvas.getWidth(), Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);

			canvas.drawRect(mRectFs[1],mPaints[2]);
			canvas.drawRect(mRectFs[1],GameView.getIns().mPaints[
			                                 GameView.getIns().mColorBoard.getMatrix()[0][0]
			                                		 ]);
			canvas.translate(0, GameView.height/2 - 3*mPaints[1].getTextSize());
			mTextLayout.draw(canvas);
			break;
			
		case THIRD:	// color picker
			mTextLayout = new StaticLayout(
					GameActivity.instance.getString(R.string.tuto3), mPaints[1],
					canvas.getWidth(), Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);

			canvas.drawRoundRect(GameView.getIns().mRectFs[1], 50.0f, 40.0f, GameView.getIns().mPaints[6]);
			canvas.drawRect(mRectFs[0],mPaints[2]);
			GameView.getIns().drawButtons(canvas);
			canvas.translate(0, GameView.height/2-3*mPaints[1].getTextSize());
				mTextLayout.draw(canvas);
			break;
			
		case FOURTH:	//keep changing
			mTextLayout = new StaticLayout(
					GameActivity.instance.getString(R.string.tuto4), mPaints[1],
					canvas.getWidth(), Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
			
			canvas.drawRoundRect(GameView.getIns().mRectFs[0],0,0,mPaints[2]);
			int boardSize = Preferences.getIns().getBoardSize() -1;
			canvas.drawRect(GameView.getIns().mRectFs[0],
					GameView.getIns().mPaints[GameView.getIns().
					                          mColorBoard.getMatrix()[boardSize][boardSize]]);
			canvas.translate(0, 5*GameView.height/16+5*GameView.width/8);
			mTextLayout.draw(canvas);
			break;
			
		case FIFTH: // run out of moves
			mTextLayout = new StaticLayout(
					GameActivity.instance.getString(R.string.tuto5), mPaints[1],
					canvas.getWidth(), Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);

			//TODO change hardcoded value
			//draw moves counter
			canvas.drawText("Moves: "+GameView.getIns().getMoves()+"/21",
					GameView.width/2, GameView.height/16,GameView.getIns(). mPaints[8]);
			
			canvas.drawRect(mRectFs[2], mPaints[2]); // highlight moves counter
			
			canvas.translate(0, GameView.height/2 - 5*mPaints[1].getTextSize());
			mTextLayout.draw(canvas);
			break;
		
		case FINAL: //final text, have fun
			mTextLayout = new StaticLayout(
					GameActivity.instance.getString(R.string.tuto6), mPaints[1],
					canvas.getWidth(), Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);

			canvas.translate(0, GameView.height/2 - 4*mPaints[1].getTextSize());
			mTextLayout.draw(canvas);
			break;
			
			
		case BACK:// want to exit tutorial?
			mTextLayout = new StaticLayout(
					GameActivity.instance.getString(R.string.tuto_exit), mPaints[1],
					canvas.getWidth(), Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);

			canvas.translate(0, GameView.height/2 - 3*mPaints[1].getTextSize());
			mTextLayout.draw(canvas);
			break;
		}
		canvas.restore();
	}
	

	private void saveTutorialFinish(){
		//Log.i(TutoState.class.getSimpleName(),"tutorial FInished");
		Preferences.getIns().setTutorialCompleted(true);
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
			//Log.d(TutoState.class.getSimpleName(),"mState: "+mInnerState);
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
