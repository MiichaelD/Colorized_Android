package com.webs.itmexicali.colorized.gamestates;

import com.webs.itmexicali.colorized.GameActivity;
import com.webs.itmexicali.colorized.GameView;
import com.webs.itmexicali.colorized.R;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import android.text.StaticLayout;
import android.text.Layout.Alignment;
import android.text.TextPaint;
import android.view.MotionEvent;

public class AboutState extends BaseState {

	RectF base;
	MainState ms;
	int savedAlpha;
	float dx,dy;
	StaticLayout mLayout;
	
	TextPaint smallText;
	
	//Titles
	String tDeveloper, tVersion, tSounds;
		
	//Values
	String vDeveloper, vVersion, vSounds;
	
	protected AboutState(statesIDs id){
		super(id);
		ms = ((MainState)StateMachine.getIns().getFirstState());
		
		tDeveloper	= GameActivity.instance.getString(R.string.about_developer);
		tVersion	= GameActivity.instance.getString(R.string.about_version);
		tSounds		= GameActivity.instance.getString(R.string.about_sound);
		
		vDeveloper = "Michael Duarte";
		vSounds		= GameActivity.instance.getString(R.string.about_sound_values);
		PackageInfo pi = null;
		try{
			pi =GameActivity.instance.getPackageManager().getPackageInfo(GameActivity.instance.getPackageName(), 0);
		}catch(NameNotFoundException e){
			vVersion = "0.0.0";
		}
		
		if(vVersion == null && pi != null){
			vVersion = pi.versionName;
		}	
		
		smallText = new TextPaint();
		smallText.setColor(Color.WHITE);
	}

	@Override
	public void draw(Canvas canvas, boolean isPortrait) {
		ms.draw(canvas, isPortrait);
		canvas.drawRoundRect(base, ms.roundness, ms.roundness, ms.mPaints[11]);
		
		canvas.save();
		// Developer
		canvas.translate(base.left, dy+base.top);
		mLayout = getLayout(tDeveloper, ms.mPaints[9]);
		mLayout.draw(canvas);
		
		canvas.translate(0,dy);
		ms.mPaints[8].setTextAlign(Align.LEFT);
		mLayout = getLayout(vDeveloper, ms.mPaints[8]);
		mLayout.draw(canvas);
		canvas.translate(0, dy);
		
		//music
		canvas.translate(0, dy);
		mLayout = getLayout(tSounds, ms.mPaints[9]);
		mLayout.draw(canvas);
		
		canvas.translate(0,dy);
		mLayout = getLayout(vSounds, smallText);
		
		mLayout.draw(canvas);
		
		//version
		canvas.translate(0, dy*6);
		mLayout = getLayout(tVersion, ms.mPaints[9]);
		mLayout.draw(canvas);
		
		canvas.translate(0,dy);
		mLayout = getLayout(vVersion, ms.mPaints[8]);
		mLayout.draw(canvas);
		canvas.restore();
		ms.mPaints[8].setTextAlign(Align.CENTER);
		
	}

	/** Get a layout to paint it on the canvas with given Text and TextPaint*/
	private StaticLayout getLayout(String text, TextPaint p){
		return new StaticLayout(
				text, p,(int)(7*GameView.width/8),
				Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
	}
	
	/** Get a layout to paint it on the canvas with given Text ID and TextPaint
	private StaticLayout getLayout(int text, TextPaint p){
		return getLayout(GameActivity.instance.getString(text),p);
	}
	*/
	
	@Override
	public void resize(float width, float height) {
		ms.resize(width, height);
		base = new RectF(width/16,height/8,15*width/16,7*height/8);
		dx = base.left;
		dy = ms.mPaints[8].getTextSize() ;
		ms.mPaints[11].setAlpha(235);
		smallText.setTextSize(GameView.mPortrait? GameView.width/22 : GameView.height/22);
	}
	
	public void onPopped(){
		ms = null;
	}
	
	@Override
	public boolean touch(MotionEvent event) {
		int action = event.getAction() & MotionEvent.ACTION_MASK;
		int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
		switch (action) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_POINTER_DOWN:
			if(!base.contains(event.getX(pointerIndex), event.getY(pointerIndex))){
				StateMachine.getIns().popState();
			}
			break;
		default:
			break;
		}
		return true;
	}
	
}
