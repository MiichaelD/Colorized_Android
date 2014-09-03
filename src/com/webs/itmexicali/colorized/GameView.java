package com.webs.itmexicali.colorized;

import com.webs.itmexicali.colorized.gamestates.BaseState;
import com.webs.itmexicali.colorized.gamestates.StateMachine;
import com.webs.itmexicali.colorized.util.Const;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;


@SuppressLint("WrongCall")
public class GameView extends SurfaceView implements Callback, Runnable{
	
	public interface surfaceListener{
		void onSurfaceChanged(float width, float height);
	}
	
	// mPortrait true to indicate that the width is smaller than the heigth
	static public boolean mPortrait;
	
	private boolean run = false, surfaceCreated=false;
	
	Thread tDraw = null;// painting thread
	
	protected SurfaceHolder sh;	
		
	public static float width = 1, height = 1, ratio;
	
	public static long drawSleep = 5;
	
	//the matrix of color
	//canvas to be drawn on
	protected Canvas canvas;
	
	//Context of the instance that instantiated this class
	protected Context  mContext;
	
	//this clas instance
	private static GameView instance = null;
	
	private surfaceListener pListener = null;
	
	public void setSurfaceListener(surfaceListener l){
		pListener = l;
	}
	
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
		
		sh = getHolder();
		sh.setFormat(PixelFormat.TRANSLUCENT);
		sh.addCallback(this);
	}
	
    
    /***************************SURFACE HOLDER CALLBACK METHODS*****************************/
    
    @Override
	public void surfaceCreated(SurfaceHolder holder) {
		Const.v(Const.TAG,"SurfaceCreated ");
		canvas = null;
		surfaceCreated=true;

		startThread();//Start painting thread
	}
    
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int wi,	int he) {
		Const.v(Const.TAG, "SurfaceChanged: "+wi+"x"+he+" Ratio = "+((double) wi) / he);
		GameView.width = wi; 
		GameView.height = he;
		ratio = ((float) width) / height;
		mPortrait = true;// = ratio > 1.0f ? false : true;
		
		StateMachine.getIns().surfaceChanged(width, height);
		
		if(StateMachine.getIns().getCurrentState() == null){
			StateMachine.getIns().pushState(BaseState.statesIDs.MAIN);
		}
		
		if(pListener != null)
			pListener.onSurfaceChanged(wi, he);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Const.v(Const.TAG, "surfaceDestroyed ");
		surfaceCreated = false;
		stopThread();
	}
	
	/******************************* DRAWING METHODS *********************************/
	
	/** This is what is going to be shown on the canvas
	 * @see android.view.View#onDraw(android.graphics.Canvas)	 */
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		try {
			//canvas.drawColor(Color.WHITE);
			StateMachine.getIns().draw(canvas, mPortrait);
			
		} catch (Exception e) {
			Const.e(Const.TAG, "onDraw(canvas)"+e.getMessage());
		}
		
	}
	
	/** Start a new thread to keep the UI refreshing constantly
	 * restrict to create and start a thread JUST when:
	 * There is no other thread running  and
	 * The SurfaceView has been created */
	public final void startThread(){
		Const.v(GameView.class.getName(), "Starting Thread" );
		if(run == false){
			tDraw = new Thread(this);
			tDraw.setPriority(Thread.MAX_PRIORITY);
			run = true;
			tDraw.start();
		}
	}
	
	/** Stop any thread in charge of refreshing the UI*/
	public final void stopThread(){
		Const.v(GameView.class.getName(), "Stoping Thread" );
		if(run){
			run = false;
			boolean retry = true;
			while (retry) {
				try {
					if (tDraw != null)
						tDraw.join();
					retry = false;
				} catch (InterruptedException e) {
					//Const.e(Const.TAG, "stopThread: " + e.getMessage());
				}
			}
		}
	}

	/** this thread is responsible for updating the canvas
	 * @see java.lang.Runnable#run() */
	public final void run() {		
		while (run && surfaceCreated) {
			try {
				//sleep {drawSleep} millis
				Thread.sleep(drawSleep);
				//Long t = System.currentTimeMillis();
				refreshUI();
				//Const.d("Draw","Time painting: "+(System.currentTimeMillis()-t));
			} catch (InterruptedException e) { }
		}
	}
	

	/** Refresh the User Interface to show the updates*/
	public final void refreshUI() {
		//Const.v(Const.TAG,"Refreshing UI GameView");
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
			Const.e(Const.TAG,"Refreshing UI GameView CANCELLED because surface is not created");
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
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent( MotionEvent event) {
		if(StateMachine.getIns().touch(event))
			return true;
		return false;
	}

}
