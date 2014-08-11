package com.webs.itmexicali.colorized.gamestates;

import com.webs.itmexicali.colorized.GameActivity;
import com.webs.itmexicali.colorized.GameView;
import com.webs.itmexicali.colorized.Preferences;
import com.webs.itmexicali.colorized.R;

import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;

public class StatisticState extends BaseState {

	MainState ms = ((MainState)StateMachine.getIns().getFirstState());
	private String played, won, percentage;
	float x = GameView.width/2, y = GameView.height, percent;
	
	StatisticState(){
		mID = statesIDs.LEADER;
		
		int games = Preferences.getIns().getGamesWon();
		won  = GameActivity.instance.getString(R.string.totalGamesWon);
		won = String.format(won,games);
		
		float percent = games;
		
		games = Preferences.getIns().getGamesFinished();
		played = GameActivity.instance.getString(R.string.totalGamesPlayed);
		played = String.format(played,games);
		
		percent/=games;
		
		percentage = GameActivity.instance.getString(R.string.percentGamesWon);
		percentage = String.format(percentage, percent*100f);
	}
	
	@Override
	public void draw(Canvas canvas, boolean isPortrait) {
		canvas.drawColor(Color.DKGRAY);
		canvas.drawText(played,x, 3*y/8,ms.mPaints[8]);
		canvas.drawText(won,x, y/2,ms.mPaints[8]);
		ms.mPaints[8].setTextScaleX(0.65f);
		canvas.drawText(percentage,x, 5*y/8,ms.mPaints[8]);
		ms.mPaints[8].setTextScaleX(1.0f);
	}

	@Override
	public boolean touch(MotionEvent me) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void resize(float width, float height) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onBackPressed() {
		// TODO Auto-generated method stub
		return false;
	}

}
