package com.webs.itmexicali.colorized.gamestates;

import com.webs.itmexicali.colorized.util.Const;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;

public class TestState extends BaseState {
	
public TestState(statesIDs id) {
		super(id);
		rect = new Rect(0,0,100,100);
		pRed = new Paint();
		pRed.setColor(Color.RED);
	}

	final int MAX_INDEX = 20;
	boolean clicks[] = new boolean[MAX_INDEX];
	float clickX[]=new float[MAX_INDEX], clickY[] = new float[MAX_INDEX];
	
	Rect rect = null;
	Paint pRed = null;
	
	
	@Override
	public void draw(Canvas canvas, boolean isPortrait) {
		canvas.drawColor(Color.BLACK);
		
		for(int i =0;i<MAX_INDEX;i++){
			if(clicks[i]){
				canvas.save();
				canvas.translate(clickX[i], clickY[i]);
				canvas.drawRect(rect, pRed);
				canvas.restore();
			}
		}

	}
	
	@Override
	public boolean touch(MotionEvent event) {
		int action = event.getAction() & MotionEvent.ACTION_MASK;
		int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
		int pointerId = event.getPointerId(pointerIndex);
		int pointerCount = event.getPointerCount();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_POINTER_DOWN:
			Const.d(TestState.class.getSimpleName(),"touchID:"+pointerId+",touchIndx"+pointerIndex+
					"=("+event.getX(pointerIndex)+","+event.getY(pointerIndex)+")");
			clicks[pointerId] = true;
			clickX[pointerId] = event.getX(pointerIndex);
			clickY[pointerId] = event.getY(pointerIndex);
			break;

		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
		case MotionEvent.ACTION_CANCEL:
			Const.w(TestState.class.getSimpleName(),"relID:"+pointerId+",relIndx"+pointerIndex+
					"=("+event.getX(pointerIndex)+","+event.getY(pointerIndex)+")");
			clicks[pointerId] = false;
			clickX[pointerId] = clickY[pointerIndex] = -1f;
			break;

		case MotionEvent.ACTION_MOVE:
			for (int i = 0; i < pointerCount; i++) {
				pointerIndex = i;
				pointerId = event.getPointerId(pointerIndex);
				Const.i(TestState.class.getSimpleName(),"moveID:"+pointerId+",moveIndx"+pointerIndex+
						"=("+event.getX(pointerIndex)+","+event.getY(pointerIndex)+")");
	
				clickX[pointerId] = event.getX(pointerIndex);
				clickY[pointerId] = event.getY(pointerIndex);
			}
			break;
		default:
			return false;
		}
		return true;
	}

}
