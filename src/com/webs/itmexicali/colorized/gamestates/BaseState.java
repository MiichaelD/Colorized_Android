package com.webs.itmexicali.colorized.gamestates;

import android.graphics.Canvas;

public abstract class BaseState {
	
	public abstract void draw(Canvas canvas);

	static enum innerStates{INIT, SEC, THIRD, FOURTH, FIFTH, FINAL}
}
