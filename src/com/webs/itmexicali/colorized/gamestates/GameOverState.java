package com.webs.itmexicali.colorized.gamestates;

import com.webs.itmexicali.colorized.gamestates.GameState.GameFinishedListener;

import com.webs.itmexicali.colorized.GameActivity;
import com.webs.itmexicali.colorized.GameView;
import com.webs.itmexicali.colorized.R;
import com.webs.itmexicali.colorized.drawcomps.BitmapLoader;
import com.webs.itmexicali.colorized.drawcomps.DrawButton;
import com.webs.itmexicali.colorized.drawcomps.DrawButtonContainer;
import com.webs.itmexicali.colorized.drawcomps.DrawText;
import com.webs.itmexicali.colorized.util.Const;
import com.webs.itmexicali.colorized.util.ProgNPrefs;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.Layout.Alignment;
import android.view.MotionEvent;

public class GameOverState extends BaseState implements GameFinishedListener {

	//A reference to MainState, so we can paint it before painting this UI
	private MainState pState;
	private GameState pGame;
	
	private Bitmap mBitmaps[];
	
	private int bgColor = Color.DKGRAY;
	
	private DrawButtonContainer pButtons;
	
	private DrawText pShareLabel;
	
	//variables containing previous game results
	private boolean pWin;
	private int		pMovesCount;
	
	private String pTitle, pDescription, pShareText = null;
	
	private TextPaint bgColorText, whiteText;
	
	public GameOverState(statesIDs id) {
		super(id);
		
		pButtons = new DrawButtonContainer(4, true);
		mBitmaps = new Bitmap[4];
		bgColorText = new TextPaint();
		whiteText	= new TextPaint();
		
		//RESTART
		pButtons.setOnActionListener(0, DrawButtonContainer.RELEASE_EVENT, new DrawButton.ActionListener(){
			@Override public void onActionPerformed() {
				//play sound
				GameActivity.instance.playSound(GameActivity.SoundType.TOUCH);
				restartGamePlay();
				
			}
		});
		
		//QUIT
		pButtons.setOnActionListener(1, DrawButtonContainer.RELEASE_EVENT, new DrawButton.ActionListener(){
			@Override public void onActionPerformed() {
				//play sound
				GameActivity.instance.playSound(GameActivity.SoundType.TOUCH);
				
				quit();
			}
		});
		
		//SHARE G+
		pButtons.setOnActionListener(2, DrawButtonContainer.RELEASE_EVENT, new DrawButton.ActionListener(){
			@Override public void onActionPerformed() {
				//play sound
				GameActivity.instance.playSound(GameActivity.SoundType.TOUCH);
				
				GameActivity.instance.onGoogleShareRequested(pShareText);
			}
		});
		
		//SHARE FB
		pButtons.setOnActionListener(3, DrawButtonContainer.RELEASE_EVENT, new DrawButton.ActionListener(){
			@Override public void onActionPerformed() {
				//play sound
				GameActivity.instance.playSound(GameActivity.SoundType.TOUCH);
				
				GameActivity.instance.onFbShareRequested(pShareText,null);
			}
		});

		
		whiteText.setColor(Color.WHITE);
		whiteText.setStyle(Paint.Style.FILL);
		whiteText.setTextAlign(Align.LEFT);
		whiteText.setAntiAlias(true);
		
		bgColorText.setColor(bgColor);
		bgColorText.setStyle(Paint.Style.FILL);
		bgColorText.setTextAlign(Align.CENTER);
		bgColorText.setAntiAlias(true);		
	}
	
	private StaticLayout layout;
	
	private void quit(){
		GameActivity.instance.playMusic(true);
		StateMachine.getIns().getBackToState(statesIDs.MAIN);
	}
	
	private void restartGamePlay(){
		GameActivity.instance.playMusic(true);
		pGame.createNewBoard(Const.BOARD_SIZES[ProgNPrefs.getIns().getDifficulty()]);
		StateMachine.getIns().popState();		
	}
	
	private StaticLayout getLayout(){
		//if(layout == null) // FIX - sometimes it doesn't update correctly
		{
			layout = new StaticLayout(pDescription, whiteText,
					(int)(GameView.width*7/8), Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
		}
		return layout;
	}

	@Override
	public void draw(Canvas canvas, boolean isPortrait) {
		canvas.drawColor(bgColor);

		canvas.save();
		canvas.translate(0, 3*GameView.height/48);
		pState.drawTitle(canvas, pTitle);
		canvas.restore();
		
		canvas.save();		
		canvas.translate(GameView.width/16, 16f*GameView.height/48);
		getLayout().draw(canvas);
		canvas.restore();
		
		if(pShareLabel != null){
			pShareLabel.draw(canvas, pState.roundness, whiteText);
		}
		
		if(mBitmaps[0] != null &&  mBitmaps[1] != null){
			canvas.drawBitmap(pButtons.getDButton(2).isPressed()?
				mBitmaps[1]:mBitmaps[0],
				pButtons.getDButton(2).left,
				pButtons.getDButton(2).top, null);
		}
		
		if(mBitmaps[2] != null &&  mBitmaps[3] != null){
			canvas.drawBitmap(pButtons.getDButton(3).isPressed()?
				mBitmaps[3]:mBitmaps[2],
				pButtons.getDButton(3).left,
				pButtons.getDButton(3).top, null);
		}
		
		pButtons.drawButtonsAndText(0, 2, canvas, pState.roundness,
				pState.mPaints[8], pState.mPaints[7], bgColorText, whiteText);
	}
	
	@Override
	public void onPopped() {
		//Const.d(TutoState.class.getSimpleName(),"onPopped");	
		pState = null;
	}	

	@Override
	public void onPushed() {
		//Const.d(TutoState.class.getSimpleName(),"onPushed");
		GameActivity.instance.playMusic(false);
		
		BaseState bs = StateMachine.getIns().getPrevioustState();
		if(! (bs instanceof GameState))
			StateMachine.getIns().popState();
		pGame = (GameState)bs;

		pState = (MainState) StateMachine.getIns().getFirstState();
		
		
		pTitle = StateMachine.mContext.getString(
				pWin?R.string.game_over_win_title:R.string.game_over_lose_title);
		
		pDescription = pWin? 
				String.format(StateMachine.mContext.getString(R.string.game_over_win_desc),pMovesCount) :
				StateMachine.mContext.getString(R.string.game_over_lose_desc);
	}
	
	public void resize(float width, float height){
		//Const.v("GameOverState","canvas size: "+width+"x"+height);
		pState.resize(width, height);

		pButtons.repositionDButton(0, 8.5f*width/16, 34*height/48, 14.5f*width/16, 39*height/48); //ok
		pButtons.repositionDButton(1, 1.5f*width/16, 34*height/48, 7.5f*width/16, 39*height/48); // no
		

		pButtons.setText(0, GameActivity.instance.getString(R.string.ok));
		pButtons.setText(1, GameActivity.instance.getString(R.string.no));
		
		if(pShareText != null){
			
			pShareLabel = new DrawText(
					GameActivity.instance.getString(R.string.game_over_share_btn),
					1.5f*width/16, 40*height/48, 7.5f*width/16, 45*height/48);
			
			
			mBitmaps[0] = BitmapLoader.resizeImage(GameActivity.instance,R.drawable.btn_g_normal,
					false, 5*height/48, 5*height/48);
			
			mBitmaps[1] = BitmapLoader.resizeImage(GameActivity.instance,R.drawable.btn_g_pressed,
					false, 5*height/48, 5*height/48);
			
			mBitmaps[2] = BitmapLoader.resizeImage(GameActivity.instance,R.drawable.btn_fb_normal,
					false, 5*height/48, 5*height/48);
			
			mBitmaps[3] = BitmapLoader.resizeImage(GameActivity.instance,R.drawable.btn_fb_pressed,
					false, 5*height/48, 5*height/48);
			
			
			
			pButtons.repositionDButton(2, 8.5f*width/16, 40*height/48,
					8.5f*width/16 + 5*height/48, 45*height/48); // G+ share
			//pButtons.setText(2, GameActivity.instance.getString(R.string.game_over_share_btn));
			
			pButtons.repositionDButton(3,  14.5f*width/16 - 5*height/48, 40*height/48,
					14.5f*width/16, 45*height/48);// Fb share
		}
		
		bgColorText.setTextSize(GameView.mPortrait? width/14 : height/14);
		whiteText.setTextSize(GameView.mPortrait? width/12 : height/12);
		layout = null;
	}

	
	@Override
	public boolean touch(MotionEvent event) {
		int action = event.getAction() & MotionEvent.ACTION_MASK;
		int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
		int pointerId = event.getPointerId(pointerIndex);
		switch (action) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_POINTER_DOWN:
			pButtons.onPressUpdate(event, pointerIndex, pointerId);
			break;

		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
		case MotionEvent.ACTION_CANCEL:	
			pButtons.onReleaseUpdate(event, pointerIndex, pointerId);
			break;

		case MotionEvent.ACTION_MOVE:
			pButtons.onMoveUpdate(event, pointerIndex);
			break;
		default:
			return false;
		}
		return true;
	}
	
	public boolean onBackPressed(){
		GameActivity.instance.stopSound();
		quit();
		return true;
	}

	@Override
	public void onGameOver(boolean win, int moves, int gameMode, int boardSize) {
		pWin = win;
		pMovesCount = moves;
		bgColor = win? Color.rgb(21, 183, 46):Color.RED;
		bgColorText.setColor(bgColor);
		
		if(win && gameMode == Const.STEP && GameActivity.instance.isGAPPSavailable())
			pShareText = String.format(StateMachine.mContext.getString(R.string.game_over_share_txt),
					StateMachine.mContext.getString(Const.BOARD_NAMES_IDS[boardSize]),moves);
		
		((GameFinishedListener)GameActivity.instance).onGameOver(win,moves,gameMode,boardSize);
	}
}