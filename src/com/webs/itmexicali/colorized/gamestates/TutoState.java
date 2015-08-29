package com.webs.itmexicali.colorized.gamestates;

import com.webs.itmexicali.colorized.GameActivity;
import com.webs.itmexicali.colorized.GameView;
import com.webs.itmexicali.colorized.R;
import com.webs.itmexicali.colorized.util.Const;
import com.webs.itmexicali.colorized.util.ProgNPrefs;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.MotionEvent;

public class TutoState extends BaseState{
	
	public TutoState(statesIDs id){
		super(id);
	}

	enum innerStates{INIT, FIRST, SEC, THIRD, FOURTH, FIFTH, BACK, FINAL}
	
	private float translate_x;
	
	private innerStates mInnerState, mPreState;
	
	//A reference to GameState, so we can paint it before painting this UI
	private GameState pGame;
	
	private TextPaint selectionPaint, tutoTextPaint;
	
	//Rects to be used on canvas
	protected RectF			mRectFs[];
	
	@Override
	public void onPopped() {
		//Log.d(TutoState.class.getSimpleName(),"onPopped");	
		pGame = null;
	}	

	@Override
	public void onPushed() {
		//Log.d(TutoState.class.getSimpleName(),"onPushed");
		BaseState bs = StateMachine.getIns().getPrevioustState();
		if(! (bs instanceof GameState))
			StateMachine.getIns().popState();
		pGame = (GameState)bs;
		
		mInnerState = innerStates.INIT;
		mRectFs = new RectF[1];
		
		selectionPaint = new TextPaint(); //SELECTIONS
		selectionPaint.setColor(Color.WHITE);
		selectionPaint.setStyle(Paint.Style.STROKE);
		selectionPaint.setAntiAlias(true);
		
		tutoTextPaint = new TextPaint(); // TUTO TEXT
		tutoTextPaint.setColor(Color.WHITE);
		tutoTextPaint.setStyle(Paint.Style.FILL);
		tutoTextPaint.setAntiAlias(true);
	}
	
	public void resize(float width, float height){
		//Log.v("TutoState","canvas size: "+width+"x"+height);
		pGame.resize(width, height);

		float boardPixels = pGame.mRectFs[0].width()/Const.BOARD_SIZES[ProgNPrefs.getIns().getDifficulty()];
		mRectFs[0] = new RectF(pGame.mRectFs[0].left, pGame.mRectFs[0].top, 
				pGame.mRectFs[0].left + boardPixels, pGame.mRectFs[0].top + boardPixels);
		
		selectionPaint.setStrokeWidth(width/50);
		tutoTextPaint.setTextSize(GameView.mPortrait? width/14 : height/14);
	}

	@Override
	public void draw(Canvas canvas, boolean isPortrait) {
		pGame.draw(canvas, isPortrait);
		
		canvas.drawColor(pGame.darkBlurPaint.getColor());
		
		StaticLayout mTextLayout;
		canvas.save();
		switch(mInnerState){
		case INIT:// touch to continue
			
			canvas.drawText(GameActivity.instance.getString(R.string.tutorial),
					GameView.width/2, GameView.height/3,	pGame.movesTextPaint);
			mTextLayout = getTutoLayout(GameActivity.instance.getString(R.string.tuto0));

			canvas.translate(translate_x, GameView.height/2-selectionPaint.getTextSize());
			mTextLayout.draw(canvas);
			break;
			
		case FIRST://fill the board
			mTextLayout = getTutoLayout(GameActivity.instance.getString(R.string.tuto1));

			canvas.drawRoundRect(pGame.mRectFs[0],0,0,selectionPaint);
			pGame.drawBoard(canvas);
			canvas.translate(translate_x, GameView.height/2 + pGame.boardWidth/2 + 5);
			mTextLayout.draw(canvas);
			
			break;
		case SEC: // start with first tile
			mTextLayout = getTutoLayout(GameActivity.instance.getString(R.string.tuto2));

			canvas.drawRect(mRectFs[0],selectionPaint);
			canvas.drawRect(mRectFs[0],pGame.mPaints[pGame.getFirstTileColor()]);
			canvas.translate(translate_x, GameView.height/2 - 4*selectionPaint.getTextSize());
			mTextLayout.draw(canvas);
			break;
			
		case THIRD:	// color picker
			mTextLayout = getTutoLayout(GameActivity.instance.getString(R.string.tuto3));

			canvas.drawRoundRect(pGame.mRectFs[1], 50.0f, 40.0f, selectionPaint);
			pGame.drawButtons(canvas);
			canvas.translate(translate_x, GameView.height/2 - 3*selectionPaint.getTextSize());
				mTextLayout.draw(canvas);
			break;
			
		case FOURTH:	//keep changing
			mTextLayout = getTutoLayout(GameActivity.instance.getString(R.string.tuto4));
			
			canvas.drawRoundRect(pGame.mRectFs[0],0,0, selectionPaint);
			canvas.drawRect(pGame.mRectFs[0],pGame.mPaints[pGame.getLastTileColor()]);
			canvas.translate(translate_x, GameView.height/2 + pGame.boardWidth/2 + 5);
			mTextLayout.draw(canvas);
			break;
			
		case FIFTH: // run out of moves
			mTextLayout = getTutoLayout(GameActivity.instance.getString(R.string.tuto5));

			//draw moves counter
			pGame.drawText(canvas);			
			canvas.drawRect(pGame.mRectFs[2], selectionPaint); // highlight moves counter
			
			canvas.translate(translate_x, GameView.height/2 - 5*selectionPaint.getTextSize());
			mTextLayout.draw(canvas);
			break;
		
		case FINAL: //final text, have fun
			mTextLayout = getTutoLayout(GameActivity.instance.getString(R.string.tuto6));
			canvas.translate(translate_x, GameView.height/2 - 4*selectionPaint.getTextSize());
			mTextLayout.draw(canvas);
			break;
			
			
		case BACK:// want to exit tutorial?
			mTextLayout = getTutoLayout(GameActivity.instance.getString(R.string.tuto_exit));
			canvas.translate(translate_x, GameView.height/2 - 3*selectionPaint.getTextSize());
			mTextLayout.draw(canvas);
			break;
		}
		canvas.restore();
	}
	
	private StaticLayout getTutoLayout(String text){
		translate_x = GameView.width/16;
		return new StaticLayout(
				text, tutoTextPaint,
				(int)translate_x*14, Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
	}
	

	private void saveTutorialFinish(){
		//Log.i(TutoState.class.getSimpleName(),"tutorial FInished");
		ProgNPrefs.getIns().setTutorialCompleted(true);
	}

	@Override
	public boolean touch(MotionEvent me) {
		int action = me.getAction() & MotionEvent.ACTION_MASK;
		if(action == MotionEvent.ACTION_UP ||
			action == MotionEvent.ACTION_POINTER_UP ||
			action == MotionEvent.ACTION_CANCEL){
			//play sound
			GameActivity.instance.playSound(GameActivity.SoundType.TOUCH);
			
			switch(mInnerState){
			case INIT: mInnerState = innerStates.FIRST; break;
			case FIRST: mInnerState = innerStates.SEC; break;
			case SEC: mInnerState = innerStates.THIRD; break;
			case THIRD: mInnerState = innerStates.FOURTH; break;
			case FOURTH: mInnerState = innerStates.FIFTH; break;
			case FIFTH: mInnerState = innerStates.FINAL; break;
			case BACK: mInnerState = mPreState; break;
			case FINAL: saveTutorialFinish();
					GameActivity.instance.onTutorialFinished();
					StateMachine.getIns().popState(); break;
			}
			//Log.d(TutoState.class.getSimpleName(),"mState: "+mInnerState);
		}
		return true;
	}
	
	public boolean onBackPressed(){
		if(mInnerState != innerStates.BACK){
			mPreState = mInnerState;
			mInnerState = innerStates.BACK;
			return true;
		}			
		saveTutorialFinish();
		return false;
	}
	
}
