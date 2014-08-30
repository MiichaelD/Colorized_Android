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
	int savedAlpha, textIndex;
	float dx,dy, scrollY, startY, limY, totalDY;
	StaticLayout mLayout;
	
	TextPaint smallText;
	
	//Titles
	String tDeveloper, tVersion, tSounds, tDesigners;
		
	//Values
	String vDeveloper, vVersion, vSounds[], vDesigners[];
	
	protected AboutState(statesIDs id){
		super(id);
		ms = ((MainState)StateMachine.getIns().getFirstState());

		base = new RectF();
		
		tDeveloper	= GameActivity.instance.getString(R.string.about_developer);
		tVersion	= GameActivity.instance.getString(R.string.about_version);
		tSounds		= GameActivity.instance.getString(R.string.about_sound);
		tDesigners	= GameActivity.instance.getString(R.string.about_sound);
		
		vDeveloper = "Michael Duarte";
		/*
		vDesigners	= new String[4];
		vDesigners[0] = vDeveloper;
		vDesigners[1] = "Bianca Moya";
		vDesigners[2] = "Ivan Hybrid";
		vDesigners[3] = "Carlos Duarte";
		*/
		
		vSounds 	= new String[6];
		vSounds[0]	= GameActivity.instance.getString(R.string.about_sound_values_1);
		vSounds[1]	= GameActivity.instance.getString(R.string.about_sound_values_2);
		vSounds[2]	= GameActivity.instance.getString(R.string.about_sound_values_3);
		vSounds[3]	= GameActivity.instance.getString(R.string.about_sound_values_4);
		vSounds[4]	= GameActivity.instance.getString(R.string.about_sound_values_5);
		vSounds[5]	= GameActivity.instance.getString(R.string.about_sound_values_6);
		
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
		
		scrollY=0;
		startY=0;
		
		totalDY = 16.5f; // total of lines to display
		limY = 0;//3; // limY = The number of text lines hidden by scrolling -1
	}
	
	private float getDYandInctInd(int increments){
		textIndex += increments;
		return dy*increments;
	}

	@Override
	public void draw(Canvas canvas, boolean isPortrait) {
		ms.draw(canvas, isPortrait);
		
		textIndex = 0;
		
		ms.mPaints[8].setTextSize(4*ms.mPaints[8].getTextSize()/5);
		ms.mPaints[9].setTextSize(4*ms.mPaints[9].getTextSize()/5);
		
		/*
		if(Const.D){
			mLayout = getLayout("scrollY = "+scrollY+"\nstartY="+startY, smallText);
			mLayout.draw(canvas);
		}
		*/		
		
		canvas.drawRoundRect(base, ms.roundness, ms.roundness, ms.mPaints[11]);
		canvas.save(); // Before Scroll Y
		canvas.translate(0,scrollY);
		canvas.save(); // Before Text Alignment
		
		// Developer
		canvas.translate(base.left, getDYandInctInd(1)+base.top);
		mLayout = getLayout(tDeveloper, ms.mPaints[9]);
		if( scrollY  > -dy*textIndex && scrollY < -dy*(textIndex-totalDY))
			mLayout.draw(canvas);
		
		canvas.translate(0,getDYandInctInd(1));
		ms.mPaints[8].setTextAlign(Align.LEFT);
		mLayout = getLayout(vDeveloper, ms.mPaints[8]);
		if( scrollY  > -dy*textIndex && scrollY < -dy*(textIndex-totalDY))
			mLayout.draw(canvas);

		canvas.translate(0, getDYandInctInd(2));

		//Graphic designers
		if(vDesigners != null){
			mLayout = getLayout("Graphic Designers.", ms.mPaints[9]);
			if( scrollY  > -dy*textIndex && scrollY < -dy*(textIndex-totalDY))
				mLayout.draw(canvas);
			
			for(String desig:vDesigners){
				canvas.translate(0,getDYandInctInd(1));
				mLayout = getLayout(desig, ms.mPaints[8]);
				if( scrollY  > -dy*textIndex && scrollY < -dy*(textIndex-totalDY))
					mLayout.draw(canvas);
			}
			canvas.translate(0, getDYandInctInd(2));
		}
		
		//music
		if(vSounds != null){
			mLayout = getLayout(tSounds, ms.mPaints[9]);
			if( scrollY  > -dy*textIndex && scrollY < -dy*(textIndex-totalDY))
				mLayout.draw(canvas);
			
			for(String sndTxt:vSounds){
				canvas.translate(0, getDYandInctInd(1));
				mLayout = getLayout(sndTxt, smallText);
				if( scrollY  > -dy*textIndex && scrollY < -dy*(textIndex-totalDY))
					mLayout.draw(canvas);
			}
			canvas.translate(0, getDYandInctInd(2));
		}
				
		if(tVersion != null){		//version
			mLayout = getLayout(tVersion, ms.mPaints[9]);
			if( scrollY  > -dy*textIndex && scrollY < -dy*(textIndex-totalDY))
				mLayout.draw(canvas);
			
			canvas.translate(0,getDYandInctInd(1));
			mLayout = getLayout(vVersion, ms.mPaints[8]);
			if( scrollY  > -dy*textIndex && scrollY < -dy*(textIndex-totalDY))
				mLayout.draw(canvas);

			canvas.translate(0, getDYandInctInd(2));
		}
		canvas.restore(); // Vertical Text Alignments
		
		
		canvas.restore();// Scroll Y
		
		ms.mPaints[8].setTextAlign(Align.CENTER);
		ms.mPaints[8].setTextSize(5*ms.mPaints[8].getTextSize()/4);
		ms.mPaints[9].setTextSize(5*ms.mPaints[9].getTextSize()/4);
		
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
		base.set(width/16,height/8,15*width/16,7*height/8);
		
		ms.mPaints[11].setAlpha(235);
		smallText.setTextSize(GameView.mPortrait? GameView.width/22 : GameView.height/22);
		
		dx = base.left;
		dy = height/24;//ms.mPaints[8].getTextSize() ;
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
			
			startY = event.getY(pointerIndex);
			break;

		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
		case MotionEvent.ACTION_CANCEL:	
			
			break;

		case MotionEvent.ACTION_MOVE:
			scrollY = scrollY - (startY - event.getY(pointerIndex));
			startY = event.getY(pointerIndex);
			if(scrollY < -limY*dy )
				scrollY = -limY*dy;
			else if(scrollY > 0)
				scrollY = 0;
			
			break;
		default:
			break;
		}
		
		return true;
	}
	
}
