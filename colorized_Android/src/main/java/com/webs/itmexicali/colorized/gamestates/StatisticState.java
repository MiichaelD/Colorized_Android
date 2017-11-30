package com.webs.itmexicali.colorized.gamestates;

import android.graphics.Canvas;

import com.webs.itmexicali.colorized.GameActivity;
import com.webs.itmexicali.colorized.R;
import com.webs.itmexicali.colorized.board.Constants;
import com.webs.itmexicali.colorized.drawcomps.DrawButtonContainer;
import com.webs.itmexicali.colorized.util.ProgNPrefs;

public class StatisticState extends BaseState {

  DrawButtonContainer container;

  private MainState ms;
  private String played, won, percentage;
  private String small, medium, large, total;

  StatisticState(statesIDs id) {
    super(id);
    container = new DrawButtonContainer(4 * 4, false);
  }

  public void onPopped() {
    ms = null;
  }


  public void onPushed() {
    ms = ((MainState) StateMachine.getIns().getFirstState());

    for (int i = 0; i < (Constants.TOTAL_SIZES + 1); i++) {
      int games = ProgNPrefs.getIns().getGamesWon(i);
      won = GameActivity.instance.getString(R.string.totalGamesWon);
      won = String.format(won, games);

      float percent = games;

      games = ProgNPrefs.getIns().getGamesFinished(i);
      played = GameActivity.instance.getString(R.string.totalGamesPlayed);
      played = String.format(played, games);

      if (games == 0)
        games = 1;
      percent /= games;

      percentage = GameActivity.instance.getString(R.string.percentGamesWon);
      percentage = String.format(percentage, percent * 100f);

      container.initDrawButton(4 * i, 0, 0, 0, 0);
      container.initDrawButton(4 * i + 1, played, 0, 0, 0, 0);
      container.initDrawButton(4 * i + 2, won, 0, 0, 0, 0);
      container.initDrawButton(4 * i + 3, percentage, 0, 0, 0, 0);
    }

    small = GameActivity.instance.getString(R.string.options_easy);
    medium = GameActivity.instance.getString(R.string.options_med);
    large = GameActivity.instance.getString(R.string.options_hard);
    total = GameActivity.instance.getString(R.string.options_size_all);

    won = played = percentage = null;
  }

  @Override
  public void resize(float width, float height) {
    ms.mPaints[8].setTextScaleX(0.68f);

    float w = width / 16, h = height / 16, transY = 0;
    container.repositionDButton(Constants.SMALL, w, h * 3 + transY, 7.5f * w, 7.5f * h + transY);
    container.repositionDButton(Constants.SMALL + 1, w, h * 3 + transY, 7.5f * w, 4.5f * h + transY);
    container.repositionDButton(Constants.SMALL + 2, w, h * 4.5f + transY, 7.5f * w, 6 * h + transY);
    container.repositionDButton(Constants.SMALL + 3, w, h * 6 + transY, 7.5f * w, 7.5f * h + transY);


    container.repositionDButton(Constants.MEDIUM * 4, 8.5f * w, h * 3 + transY, 15 * w, 7.5f * h + transY);
    container.repositionDButton(Constants.MEDIUM * 4 + 1, 8.5f * w, h * 3 + transY, 15 * w, 4.5f * h + transY);
    container.repositionDButton(Constants.MEDIUM * 4 + 2, 8.5f * w, h * 4.5f + transY, 15 * w, 6 * h + transY);
    container.repositionDButton(Constants.MEDIUM * 4 + 3, 8.5f * w, h * 6 + transY, 15 * w, 7.5f * h + transY);


    transY = h;
    container.repositionDButton(Constants.LARGE * 4, w, h * 8.5f + transY, 7.5f * w, 13 * h + transY);
    container.repositionDButton(Constants.LARGE * 4 + 1, w, h * 8.5f + transY, 7.5f * w, 10 * h + transY);
    container.repositionDButton(Constants.LARGE * 4 + 2, w, h * 10 + transY, 7.5f * w, 11.5f * h + transY);
    container.repositionDButton(Constants.LARGE * 4 + 3, w, h * 11.5f + transY, 7.5f * w, 13 * h + transY);


    container.repositionDButton(Constants.TOTAL_SIZES * 4, 8.5f * w, h * 8.5f + transY, 15 * w, 13 * h + transY);
    container.repositionDButton(Constants.TOTAL_SIZES * 4 + 1, 8.5f * w, h * 8.5f + transY, 15 * w, 10 * h + transY);
    container.repositionDButton(Constants.TOTAL_SIZES * 4 + 2, 8.5f * w, h * 10 + transY, 15 * w, 11.5f * h + transY);
    container.repositionDButton(Constants.TOTAL_SIZES * 4 + 3, 8.5f * w, h * 11.5f + transY, 15 * w, 13 * h + transY);


  }


  @Override
  public void draw(Canvas canvas, boolean isPortrait) {
    //canvas.drawColor(Color.DKGRAY);
    /*
	  	float x = GameView.width/2, y = GameView.height, percent;
		canvas.drawText(played,x, 3*y/8,ms.mPaints[8]);
		canvas.drawText(won,x, y/2,ms.mPaints[8]);
		ms.mPaints[8].setTextScaleX(0.65f);
		canvas.drawText(percentage,x, 5*y/8,ms.mPaints[8]);
		ms.mPaints[8].setTextScaleX(1.0f);
		*/
    container.drawButtonsAndText(canvas, MainState.roundness, ms.mPaints[8], null, ms.mPaints[1], null);


    canvas.drawText(small, container.getDButton(0).centerX(), container.getDButton(0).top - 5, ms.mPaints[8]);
    canvas.drawText(medium, container.getDButton(4).centerX(), container.getDButton(4).top - 5, ms.mPaints[8]);
    canvas.drawText(large, container.getDButton(8).centerX(), container.getDButton(8).top - 5, ms.mPaints[8]);
    canvas.drawText(total, container.getDButton(12).centerX(), container.getDButton(12).top - 5, ms.mPaints[8]);
  }
}
