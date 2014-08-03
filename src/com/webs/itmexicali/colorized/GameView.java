package com.webs.itmexicali.colorized;

import java.util.Scanner;

import com.webs.itmexicali.colorized.drawcomps.DrawButtonContainer;
import com.webs.itmexicali.colorized.drawcomps.DrawButton;
import com.webs.itmexicali.colorized.gamestates.BaseState;
import com.webs.itmexicali.colorized.gamestates.StateMachine;
import com.webs.itmexicali.colorized.gamestates.TutoState;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;


@SuppressLint("WrongCall")
public class GameView extends SurfaceView implements Callback, Runnable{
	
	// mPortrait true to indicate that the width is smaller than the heigth
	static public boolean mPortrait;
	
	private 	  boolean 	run = false, selected, surfaceCreated=false;
	protected SurfaceHolder sh;
	
	//Paints to be used to Draw text and shapes
	protected Paint 	mPaints[];
	protected Bitmap	mBitmaps[];
	protected Rect		mRects[];
	protected RectF		mRectFs[];
	private Thread		tDraw;
	private long 		lastUpdate = 0;
	DrawButtonContainer dbc;
	
	
	protected int bgColor=Color.DKGRAY;
	
	public static float width, height, ratio;
	
	//the matrix of color
	private ColorBoard mColorBoard = null;
	
	//canvas to be drawn on
	protected Canvas canvas;
	
	//Context of the instance that instantiated this class
	protected Context  mContext;
	
	//this clas instance
	private static GameView instance = null;
	
	/********************************************CONSTRUCTORS*****************************/
	public GameView(Context context) {
		super(context);
		init();
	}
	
	public GameView(Context context, AttributeSet attrs, int defStyle){
        super( context , attrs , defStyle );
        init();
    }

    public GameView(Context context, AttributeSet attrs){
        super( context , attrs );
        init();
    }
    /**************************************************************************************/

    public static GameView getIns(){
    	return instance;
    }
    
    /** Init this SurfaceView's Holder    */
	public final void init(){
		instance = this;
		
		// load the shared preferences
		SharedPreferences sp = getContext().getSharedPreferences(Const.TAG, 0);
		//If tutorial hasn't been played, do not load saved game
		if(sp.getBoolean(getContext().getString(R.string.key_tutorial_played), false)){
			//if there is a gamestate saved, load it again
			if( sp.getBoolean(getContext().getString(R.string.key_is_game_saved), false)){
				//parse the gamestate
				parseBoardFromString(sp.getString(getContext().getString(R.string.key_board_saved),null));
			}
		}
		
		if(mColorBoard == null){
			createNewBoard(sp.getInt(getContext().getString(R.string.key_board_size), 12));
		}
		
		sh = getHolder();
		sh.setFormat(PixelFormat.TRANSLUCENT);
		sh.addCallback(this);
		
	}
	
    
    /***************************SURFACE HOLDER CALLBACK METHODS*****************************/
    
    @Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.v(Const.TAG,"SurfaceCreated ");
		canvas = null;
		surfaceCreated=true;

		mRectFs = new RectF[2];
		dbc = new DrawButtonContainer(8,true);
		
		//register actions to the buttons created:
		dbc.setOnActionListener(0, DrawButtonContainer.RELEASE_EVENT, new DrawButton.ActionListener(){
			@Override public void onActionPerformed() {
				new Thread(new Runnable(){public void run(){
					mColorBoard.colorize(0);
				}}).start();}
		});
		
		dbc.setOnActionListener(1, DrawButtonContainer.RELEASE_EVENT, new DrawButton.ActionListener(){
			@Override public void onActionPerformed() {
				new Thread(new Runnable(){public void run(){
					mColorBoard.colorize(1);
				}}).start();}
		});
		
		dbc.setOnActionListener(2, DrawButtonContainer.RELEASE_EVENT, new DrawButton.ActionListener(){
			@Override public void onActionPerformed() {
				new Thread(new Runnable(){public void run(){
					mColorBoard.colorize(2);
				}}).start();}
		});
		
		dbc.setOnActionListener(3, DrawButtonContainer.RELEASE_EVENT, new DrawButton.ActionListener(){
			@Override public void onActionPerformed() {
				new Thread(new Runnable(){public void run(){
					mColorBoard.colorize(3);
				}}).start();}
		});
		
		dbc.setOnActionListener(4, DrawButtonContainer.RELEASE_EVENT, new DrawButton.ActionListener(){
			@Override public void onActionPerformed() {
				new Thread(new Runnable(){public void run(){
					mColorBoard.colorize(4);
				}}).start();}
		});
		dbc.setOnActionListener(5, DrawButtonContainer.RELEASE_EVENT, new DrawButton.ActionListener(){
			@Override public void onActionPerformed() {
				new Thread(new Runnable(){public void run(){
					mColorBoard.colorize(5);
				}}).start();}
		});
		
		dbc.setOnActionListener(6, DrawButtonContainer.RELEASE_EVENT, new DrawButton.ActionListener(){
			@Override public void onActionPerformed() {
				new Thread(new Runnable(){public void run(){
					GameActivity.instance.playSound(GameActivity.SoundType.TOUCH);
					//OpenSettings;
					StateMachine.getIns().pushState(BaseState.statesIDs.TUTO);
				}}).start();}
		});
		
		dbc.setOnActionListener(7, DrawButtonContainer.RELEASE_EVENT, new DrawButton.ActionListener(){
			@Override public void onActionPerformed() {
				GameActivity.instance.runOnUiThread(new Runnable(){public void run(){
					GameActivity.instance.playSound(GameActivity.SoundType.TOUCH);
					GameActivity.instance.showRestartDialog();
				}});
			}});

		//Start painting thread
		startThread();
	}
    
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int wi,	int he) {
		Log.v(Const.TAG, "SurfaceChanged: "+wi+"x"+he+" Ratio = "+((double) width) / height);
		GameView.width = wi; 
		GameView.height = he;
		ratio = ((float) width) / height;
		mPortrait = ratio > 1.0f ? false : true;
		
		initPaints();
		reloadByResize();
		
		updateNow();//refreshUI();
		//startThread();
		StateMachine.getIns().surfaceChanged(width, height);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.v(Const.TAG, "surfaceDestroyed ");
		surfaceCreated = false;
		stopThread();
	}
	
	/********************************* UI RESIZE METHODS *********************************/

	/** Init the paints to be used on canvas */
	public void initPaints() {
		mPaints = new Paint[9];
		mPaints[0] = new Paint();
		mPaints[0].setColor(Color.RED);
		mPaints[0].setStyle(Paint.Style.FILL);
		mPaints[0].setAntiAlias(true);
		
		mPaints[1] = new Paint();
		mPaints[1].setColor(Color.rgb(0, 162, 232));
		mPaints[1].setStyle(Paint.Style.FILL);
		mPaints[1].setAntiAlias(true);		
		
		mPaints[2] = new Paint();
		mPaints[2].setColor(Color.YELLOW);
		mPaints[2].setStyle(Paint.Style.FILL);
		mPaints[2].setAntiAlias(true);
		
		mPaints[3] = new Paint();
		mPaints[3].setColor(Color.rgb(163, 73, 164));
		mPaints[3].setStyle(Paint.Style.FILL);
		mPaints[3].setAntiAlias(true);
		
		mPaints[4] = new Paint();
		mPaints[4].setColor(Color.LTGRAY);
		mPaints[4].setStyle(Paint.Style.FILL);
		mPaints[4].setAntiAlias(true);
		
		mPaints[5] = new Paint();
		mPaints[5].setColor(Color.rgb(21, 183, 46));
		//mPaints[5].setColor(Color.rgb(0, 159, 60));
		mPaints[5].setStyle(Paint.Style.FILL);
		mPaints[5].setAntiAlias(true);
		
		mPaints[6] = new Paint();
		Shader sh = null;
		sh = new SweepGradient(5, 5, new int[] {Color.BLUE,	Color.RED, Color.GREEN}, null);
		//mPaints[6].setShader(new LinearGradient( 0, 0, 0, 2, Color.CYAN, Color.GREEN, Shader.TileMode.CLAMP));
		//mPaints[6].setShader(new LinearGradient(0, 1, 1, 0, Color.BLUE, Color.RED, Shader.TileMode.CLAMP));
		mPaints[6].setShader(sh);
		mPaints[6].setStyle(Paint.Style.FILL);
		mPaints[6].setAntiAlias(true);
		
		mPaints[7] = new Paint();
		mPaints[7].setColor(Color.DKGRAY);
		mPaints[7].setStyle(Paint.Style.FILL);
		mPaints[7].setAntiAlias(true);
		mPaints[7].setAlpha(120);
		
		mPaints[8] = new Paint();
		mPaints[8].setColor(Color.WHITE);
		mPaints[8].setStyle(Paint.Style.FILL);
		mPaints[8].setTextSize(mPortrait? width/11 : height/11);
		mPaints[8].setTextAlign(Align.CENTER);
		mPaints[8].setAntiAlias(true);
	}
	
	/** This method will be called when the surface has been resized, so all
	 * screen width and height dependents must be reloaded - 
	 * NOTE: DO NOT INCLUDE initPaints()*/
	protected void reloadByResize() {		
		if( mPortrait){
			mRectFs[0] = new RectF(width/16, 2*height/5-5*width/16, 15*width/16, 2*height/5+9*width/16);
			mRectFs[1] = new RectF(0, height/2+23*width/48, width, height/2+31*width/48);

			mBitmaps = new Bitmap[3];
			float val = mPortrait ? width/8 : height/8;
			mBitmaps[0] = BitmapLoader.resizeImage(getContext(),R.drawable.ic_settings, val, val);
			mBitmaps[1] = BitmapLoader.resizeImage(getContext(),R.drawable.ic_restart, val, val);
			mBitmaps[2] = Bitmap.createBitmap(mBitmaps[0]);
			
			dbc.repositionDButton(0, 3*width/28, height/2+width/2, 6*width/28, height/2+5*width/8);
			dbc.repositionDButton(1, 7*width/28, height/2+width/2, 10*width/28, height/2+5*width/8);
			dbc.repositionDButton(2, 11*width/28, height/2+width/2, 14*width/28, height/2+5*width/8);
			dbc.repositionDButton(3, 15*width/28, height/2+width/2, 18*width/28, height/2+5*width/8);
			dbc.repositionDButton(4, 19*width/28, height/2+width/2, 22*width/28, height/2+5*width/8);
			dbc.repositionDButton(5, 23*width/28, height/2+width/2, 26*width/28, height/2+5*width/8);
			
			//settings buttons
			dbc.repositionDButton(6, width/16, 2*height/5-width/2, width/16+val, 2*height/5-width/2+val);
			dbc.repositionDButton(7, 15*width/16-mBitmaps[1].getWidth(), 2*height/5-width/2, 
					15*width/16-mBitmaps[1].getWidth()+val, 2*height/5-width/2+val);
			
			
			
		}
		else{
			mRectFs[0] = new RectF(0, 0, height, height);
		}
		
	}
	
	
	/******************************* DRAWING METHODS *********************************/
	
	/** This is what is going to be shown on the canvas
	 * @see android.view.View#onDraw(android.graphics.Canvas)	 */
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		try {
			//Background
			canvas.drawColor(bgColor);//(mPaints[(int)(Math.random()*8)].getColor());
			
			if( mPortrait ) drawPortrait(canvas);
			else	drawLandscape(canvas);
			
			StateMachine.getIns().draw(canvas, mPortrait);
			
			
		    canvas.setBitmap(mBitmaps[2]);
		    canvas.drawColor(Color.CYAN);
		    
		    canvas.drawBitmap(mBitmaps[2], new Rect(0,0,50,50),new Rect(250,250, 750, 750), null);

			
			
		} catch (Exception e) {
			//Log.e(Const.TAG, "onDraw(canvas)"+e.getLocalizedMessage());
		}
		
	}
	
	/** Draw the UI in Portrait mode */
	public void drawPortrait(Canvas canvas){
		//Color Div
		canvas.drawRect(mRectFs[0].left-10, mRectFs[0].top-10, mRectFs[0].right+10, mRectFs[0].bottom+10, mPaints[6]);
		canvas.drawRoundRect(mRectFs[1], 50.0f, 40.0f, mPaints[6]);
		
		mColorBoard.updateBoard(canvas, mRectFs[0], mPaints);
		
		canvas.drawText("Moves: "+mColorBoard.getMoves()+"/21", width/2, 2*height/5-13*width/32, mPaints[8]);
		
		if ( mBitmaps != null ){
			canvas.drawBitmap(mBitmaps[0], width/16, 2*height/5-width/2, null);
			canvas.drawBitmap(mBitmaps[1], 15*width/16-mBitmaps[1].getWidth(), 2*height/5-width/2, null);
		}
		
		for(int i =0 ; i < dbc.getButtonsCount();i++){
			if(i < 6)//after position 5, we are painting bitmaps instead of buttons
				canvas.drawRect(dbc.getDButton(i), mPaints[i]);
			if( dbc.getDButton(i).isPressed() )
				canvas.drawRect(dbc.getDButton(i), mPaints[7]);
		}
		
	}
	
	
	/** Draw the UI in Landscape mode */
	public void drawLandscape(Canvas canvas){
		
	}
	
	public final void updateNow(){
		/*
		if(run)
			lastUpdate = System.currentTimeMillis();
		else
			startThread();
			*/
	}
	
	/** Start a new thread to keep the UI refreshing constantly
	 * restrict to create and start a thread JUST when:
	 * There is no other thread running  and
	 * The SurfaceView has been created */
	public final void startThread(){
		Log.v(GameView.class.getName(), "Starting Thread" );
		if(run == false){
			tDraw = new Thread(this);
			run = true;
			tDraw.start();
		}
	}
	
	/** Stop any thread in charge of refreshing the UI*/
	public final void stopThread(){
		Log.v(GameView.class.getName(), "Stoping Thread" );
		if(run){
			run = false;
			boolean retry = true;
			while (retry) {
				try {
					if (tDraw != null)
						tDraw.join();
					retry = false;
				} catch (InterruptedException e) {
					//Log.e(Const.TAG, "stopThread: " + e.getMessage());
				}
			}
		}
	}

	/** this thread is responsible for updating the canvas
	 * @see java.lang.Runnable#run() */
	public final void run() {		
		lastUpdate = System.currentTimeMillis();
		while (run && surfaceCreated) {
			try {
				//sleep 20 millis to get around 50 FPS
				Thread.sleep(20);
				//if(System.currentTimeMillis() - lastUpdate < 2000)
				refreshUI();
			} catch (InterruptedException e) { }
		}
	}
	

	/** Refresh the User Interface to show the updates*/
	public final void refreshUI() {
		//Log.v(Const.TAG,"Refreshing UI GameView");
		canvas = null;
		if (surfaceCreated && sh != null){
			try {
				canvas = sh.lockCanvas(null);
				if(canvas != null)
					synchronized (sh) {
						onDraw(canvas);
					}
			} finally {
				if (canvas != null)
					sh.unlockCanvasAndPost(canvas);
			}	
		}
		else{
			Log.e(Const.TAG,"Refreshing UI GameView CANCELLED because surface is not created");
		}
		canvas = null;
	}
	
	/** Refresh the User Interface to show the updates in a new thread
	 * {@link refreshUI}*/
	public final void refreshUI_newThread() {
		new Thread(new Runnable(){
			public void run(){
				refreshUI();
			}
		}).start();
	}
	

	/******************************* *********** *********************************/
	
	@Override
	@SuppressLint("ClickableViewAccessibility")
	public boolean onTouchEvent( MotionEvent event) {
		if(StateMachine.getIns().touch(event))
			return true;
		//new Thread(new Runnable(){
			//public void run(){
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
				//refreshUI();
		//	}
		//}).start();
		return true;
	}
	
	
	/********************************* BOARD METHODS *********************************/
	
	/** Create a new random board*/
	public void createNewBoard(int blocks){
		if(blocks < 0)//if blocks is negative, this will have the same blocks #
			mColorBoard.startRandomColorBoard();
		else
			mColorBoard = new ColorBoard(blocks);
		
		updateNow();//refreshUI();
	}
	
	/** Given a string containing a saved ColorBoard state,
	 * parse it to create a new game state identical */
	public void parseBoardFromString(String state){
		if(state == null)
			return;
		Scanner scanner = null;
		try{
			scanner = new Scanner(state);
			int bps = scanner.nextInt();
			int moves = scanner.nextInt();
			int[][] board = new int[bps][bps];
			for(int i =0;i<bps;i++)
				for(int j=0;j<bps;j++)
					board[i][j] = scanner.nextInt();
			mColorBoard = new ColorBoard(bps, moves, board);
		}catch(Exception e){
		}
		finally{scanner.close();}
	}
	
	/** The string representation of current game state*/
	public String getBoardAsString(){
		return mColorBoard.toString();
		
	}
	
	/** The number of moves (user interactions) in current game*/
	public int getMoves(){
		return mColorBoard.getMoves();
	}
	
	/** Check if the board is completed in one color
	 * @return true if game is over, false if not */
	public boolean isGameOver(){
		return mColorBoard.isBoardCompleted();
	}
	
	/** Callback to let the game know that the user input has been processed*/
	public void onBoardOpFinish(boolean won){
		if (won){
			GameActivity.instance.runOnUiThread(new Runnable(){public void run(){
				GameActivity.instance.playSound(GameActivity.SoundType.WIN);
				GameActivity.instance.showGamOverDialog(true);
			}});
			
		}
		else if(getMoves() > 20){//TODO hardcoded value
			GameActivity.instance.runOnUiThread(new Runnable(){public void run(){
				GameActivity.instance.playSound(GameActivity.SoundType.LOSE);
				GameActivity.instance.showGamOverDialog(false);
			}});
		}
		updateNow();//refreshUI();
	}

	/********************************* STATE CALLING METHODS *********************************/

	/** call this method to show tutorial UI*/
	public void showTutorial(boolean show){
		StateMachine.getIns().pushState(BaseState.statesIDs.TUTO);
	}
}
