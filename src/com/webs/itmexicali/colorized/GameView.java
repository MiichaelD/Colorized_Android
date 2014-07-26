package com.webs.itmexicali.colorized;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;


@SuppressLint("WrongCall")
public class GameView extends SurfaceView implements Callback, Runnable{
	
	//this clas TAG
	protected String TAG = Const.TAG + "-GameView";
	
	// mPortrait true to indicate that the width is smaller than the heigth
	public boolean mPortrait;
	
	public 	  boolean 	run, selected, surfaceCreated=false;
	protected SurfaceHolder sh;
	
	//Paints to be used to Draw text and shapes
	protected Paint 	mPaints[];
	protected Bitmap	mBitmaps[];
	protected Rect		mRects[];
	protected RectF		mRectFs[];
	private Thread		tDraw;
	
	
	protected int bgColor=Color.WHITE;
	
	protected float width, height, textSize, ratio;
	
	//canvas to be drawn on
	protected Canvas canvas;
	
	//Context of the instance that instantiated this class
	protected Context  mContext;

	
	/********************************************CONSTRUCTORS*****************************/
	public GameView(Context context) {
		super(context);
		mContext = context;
		initHolder(context);
	}
	
	public GameView(Context context, AttributeSet attrs, int defStyle){
        super( context , attrs , defStyle );
        mContext = context;
        initHolder(context);
    }

    public GameView ( Context context , AttributeSet attrs ){
        super( context , attrs );
        mContext = context;
        initHolder(context);
    }
    /**************************************************************************************/
	
    
    /** Init this SurfaceView's Holder    */
	public final void initHolder(Context context){
		sh = getHolder();
		sh.setFormat(PixelFormat.TRANSLUCENT);
		sh.addCallback(this);
	}

	/** Init the paints to be used on canvas */
	public void initPaints() {
		
		mPaints = new Paint[5];
		mPaints[0] = new Paint();
		mPaints[0].setColor(Color.WHITE);
		mPaints[0].setTextAlign(Align.LEFT);
		//mPaints[0].setTypeface(Typeface.createFromAsset(this.getContext().getAssets(), "fonts/KellySlab-Regular.ttf"));
		mPaints[0].setTextSize(textSize*3/2);
		mPaints[0].setAntiAlias(true);
		
		mPaints[1] = new Paint();
		mPaints[1].setARGB(200, 100, 180, 180);
		mPaints[1].setStyle(Paint.Style.FILL);
		mPaints[1].setAntiAlias(true);
		
		mPaints[2] = new Paint();
		mPaints[2].setColor(Color.BLACK);
		mPaints[2].setTextSize(textSize);
		mPaints[2].setAntiAlias(true);
		
		mPaints[3] = new Paint();
		mPaints[3].setColor(bgColor);
		mPaints[3].setStyle(Paint.Style.FILL);
		mPaints[3].setAntiAlias(true);
		
		mPaints[4] = new Paint();
		mPaints[4].setColor(Color.RED);
		mPaints[4].setStyle(Paint.Style.FILL);
		mPaints[4].setAntiAlias(true);
	}
	
	/** This is what is going to be shown on the canvas
	 * @see android.view.View#onDraw(android.graphics.Canvas)	 */
	/** This is what is going to be shown on the canvas
	 * @see android.view.View#onDraw(android.graphics.Canvas) */
	public void onDraw(Canvas canvas) {
		try {
			//Background
			canvas.drawColor(bgColor);
			
			
			
			if( mPortrait ) drawPortrait(canvas);
			else	drawLandscape(canvas);
			
			
			
			//Color Div
			canvas.drawRoundRect(mRectFs[0], 25.0f, 20.0f, mPaints[1]);
			
			//Picture  - 2.0f*w, 2.0f*w, 40.0f*w, 20.0f*h
			if ( mBitmaps[0] != null )
				canvas.drawBitmap(mBitmaps[0], 3.0f*width, 3.0f*width, null);
			
			
			
		} catch (Exception e) {
			Log.e(TAG, "onDraw(canvas)");
			e.printStackTrace();
		}
	}
	
	public void drawPortrait(Canvas canvas){

	}
	
	public void drawLandscape(Canvas canvas){
		
	}

	/** resize window parameters each time the screen changes from resolution */
	protected final void resize(int w, int h){
		width = w; 
		height = h;
		ratio = ((float) w) / h;
		mPortrait = ratio > 1.0f ? false : true;
		GameActivity.getIns().setAdsPosition(mPortrait);
		if(Const.D)
			Log.v(TAG,"ratio: "+ratio+". width = "+w+", height = "+h+" so, portrait = "+mPortrait);
		
		initPaints();
		reloadByResize();
	}
	/** This method will be called when the surface has been resized, so all
	 * screen width and height dependents must be reloaded - 
	 * NOTE: DO NOT INCLUDE initPaints()*/
	protected void reloadByResize() {
		mRectFs = new RectF[2];
		
		if( mPortrait)
			mRectFs[0] = new RectF(0, height/2-width/2, width, height/2+width/2);
		else
			mRectFs[0] = new RectF(0, 0, height, height);
		
		mRectFs[1] = new RectF(44.2f*width, 2.2f*width, 57.8f*width, 15.8f*width);
		
		mBitmaps = new Bitmap[1];
		//this task must be on a separated thread because of it's lag while fetching the img from server
				new Thread(new Runnable(){
					@Override
					public void run(){
						float val = mPortrait ? height/8 : width/8;
						mBitmaps[0] = BitmapLoader.resizeImage(GameActivity.getIns(),R.drawable.ic_launcher, val, val);
						refreshUI();
					}
				}).start();
	}
	
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		//Log.d(TAG,"SurfaceCreated "+window);
		canvas = null;
		surfaceCreated=true;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,	int height) {
		//Log.v(TAG, "SurfaceChanged "+window );
		resize(width, height);
		refreshUI();
		//startThread();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		//Log.v(TAG, "surfaceDestroyed "+window);
		surfaceCreated = false;
		stopThread();

	}
	
	
	
	@SuppressLint("ClickableViewAccessibility")
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction() & MotionEvent.ACTION_MASK;
		switch (action) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_POINTER_DOWN:
			refreshUI();
			break;
		default:
			break;
		}
		
		return true;
	}
	
	
	/** Start a new thread to keep the UI refreshing constantly
	 * restrict to create and start a thread JUST when:
	 * There is no other thread running  and
	 * The SurfaceView has been created */
	public final synchronized void startThread(){
		if(run == false){
			tDraw = new Thread(this);
			run = true;
			tDraw.start();
		}
	}
	
	/** Stop any thread in charge of refreshing the UI*/
	public final synchronized void stopThread(){
		if(run){
			run = false;
			boolean retry = true;
			while (retry) {
				try {
					if (tDraw != null)
						tDraw.join();
					retry = false;
				} catch (InterruptedException e) {
					Log.e(TAG, "stopThread: " + e.getMessage());
				}
			}
		}
	}

	/** this thread is responsible for updating the canvas
	 * @see java.lang.Runnable#run() */
	public final void run() {		
		while (run && surfaceCreated) {
			try {
				//sleep 20 millis to get around 50 FPS
				Thread.sleep(20);
			} catch (InterruptedException e) { }
			refreshUI();
		}
	}

	/** Refresh the User Interface to show the updates*/
	public final synchronized void refreshUI() {
		if(Const.D)
			Log.v(TAG,"Refreshing UI GameView");
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
			if(Const.D)
				Log.e(TAG,"Refreshing UI GameView CANCELLED because surface is not created");
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

}
