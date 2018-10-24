package com.webs.itmexicali.colorized.gamestates;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.MotionEvent;

import com.plattysoft.leonids.ParticleSystem;
import com.plattysoft.leonids.modifiers.ScaleModifier;
import com.webs.itmexicali.colorized.GameActivity;
import com.webs.itmexicali.colorized.GameView;
import com.webs.itmexicali.colorized.R;
import com.webs.itmexicali.colorized.board.Constants;
import com.webs.itmexicali.colorized.drawcomps.DrawButton;
import com.webs.itmexicali.colorized.drawcomps.DrawButtonContainer;
import com.webs.itmexicali.colorized.drawcomps.DrawText;
import com.webs.itmexicali.colorized.gamestates.GameState.GameFinishedListener;
import com.webs.itmexicali.colorized.util.BitmapLoader;

//Particle Effect from:					https://github.com/plattysoft/Leonids

public class GameOverState extends BaseState implements GameFinishedListener {

  private GameState pGame;

  private Bitmap mBitmaps[];

  private TextPaint mPaints[];

  private int bgColor = Color.DKGRAY;

  private DrawButtonContainer pButtons;

  private DrawText pShareLabel;

  private float textScale = 1.0f;

  private ParticleSystem effect = null;

  //variables containing previous game results
  private boolean pWin;
  private int pMovesCount;

  private String pTitle, pDescription, pShareText = null;

  private TextPaint bgColorText, whiteText;
  private StaticLayout layout;

  public GameOverState(statesIDs id) {
    super(id);

    pButtons = new DrawButtonContainer(4, true);
    mBitmaps = new Bitmap[4];
    bgColorText = new TextPaint();
    whiteText = new TextPaint();

    //RESTART
    pButtons.setOnActionListener(0, DrawButtonContainer.RELEASE_EVENT, new DrawButton.ActionListener() {
      @Override
      public void onActionPerformed() {
        //play sound
        GameActivity.instance.playSound(GameActivity.SoundType.TOUCH);
        restartGamePlay();

      }
    });

    //QUIT
    pButtons.setOnActionListener(1, DrawButtonContainer.RELEASE_EVENT, () -> {
      GameActivity.instance.playSound(GameActivity.SoundType.TOUCH);  //play sound
      quit();
    });

    //SHARE G+
    pButtons.setOnActionListener(2, DrawButtonContainer.RELEASE_EVENT, () -> {
      GameActivity.instance.playSound(GameActivity.SoundType.TOUCH);  //play sound
      GameActivity.instance.onGoogleShareRequested(pShareText);
    });

    //SHARE FB
    // pButtons.setOnActionListener(3, DrawButtonContainer.RELEASE_EVENT, () -> {
    //   GameActivity.instance.playSound(GameActivity.SoundType.TOUCH);  //play sound
    //   GameActivity.instance.onFbShareRequested(pShareText, null);
    // });

    mPaints = new TextPaint[3];

    whiteText.setColor(Color.WHITE);
    whiteText.setStyle(Paint.Style.FILL);
    whiteText.setTextAlign(Align.LEFT);
    whiteText.setAntiAlias(true);

    bgColorText.setColor(bgColor);
    bgColorText.setStyle(Paint.Style.FILL);
    bgColorText.setTextAlign(Align.CENTER);
    bgColorText.setAntiAlias(true);

    mPaints[0] = new TextPaint(); // DKGRAY for pushed buttons
    mPaints[0].setColor(Color.DKGRAY);
    mPaints[0].setStyle(Paint.Style.FILL);
    mPaints[0].setAntiAlias(true);
    mPaints[0].setAlpha(120);

    mPaints[1] = new TextPaint(); // WHITE for TEXT
    mPaints[1].setColor(Color.WHITE);
    mPaints[1].setStyle(Paint.Style.FILL);
    mPaints[1].setTextAlign(Align.CENTER);
    mPaints[1].setAntiAlias(true);

    mPaints[2] = new TextPaint(); // WHITE for TITLE
    mPaints[2].setColor(Color.WHITE);
    mPaints[2].setFakeBoldText(true);
    mPaints[2].setStyle(Paint.Style.FILL);
    mPaints[2].setTextAlign(Align.CENTER);
    mPaints[2].setAntiAlias(true);
  }

  private void quit() {
    GameActivity.instance.playMusic(true);
    StateMachine.getIns().getBackToState(statesIDs.MAIN);
  }

  private void restartGamePlay() {
    GameActivity.instance.playMusic(true);
    pGame.restartBoard(true);
    StateMachine.getIns().popState();
  }

  private StaticLayout getLayout() {
    //if(layout == null) // FIX - sometimes it doesn't update correctly
    {
      layout = new StaticLayout(pDescription, whiteText,
          (int) (GameView.width * 7 / 8), Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
    }
    return layout;
  }

  @Override
  public void draw(Canvas canvas, boolean isPortrait) {
    //canvas.drawColor(bgColor);

    canvas.save();
    canvas.translate(0, 3 * GameView.height / 48);
    drawTitle(canvas, pTitle);
    canvas.restore();

    canvas.save();
    canvas.translate(GameView.width / 16, 16f * GameView.height / 48);
    getLayout().draw(canvas);
    canvas.restore();

    if (pShareLabel != null) {
      pShareLabel.draw(canvas, MainState.roundness, whiteText);
    }

    // G+ share
    if (mBitmaps[0] != null && mBitmaps[1] != null) {
      canvas.drawBitmap(pButtons.getDButton(2).isPressed() ?
              mBitmaps[1] : mBitmaps[0],
          pButtons.getDButton(2).left,
          pButtons.getDButton(2).top, null);
    }

    // FB share
    // if (mBitmaps[2] != null && mBitmaps[3] != null) {
    //   canvas.drawBitmap(pButtons.getDButton(3).isPressed() ?
    //           mBitmaps[3] : mBitmaps[2],
    //       pButtons.getDButton(3).left,
    //       pButtons.getDButton(3).top, null);
    // }

    pButtons.drawButtonsAndText(0, 2, canvas, MainState.roundness,
        mPaints[1], mPaints[0], bgColorText, whiteText);
  }

  private void drawTitle(Canvas canvas, String text) {
    float x = GameView.width, y = GameView.height;

    while ((mPaints[2].measureText(text)) + 10 >= GameView.width) {
      textScale -= 0.05f;
      mPaints[2].setTextScaleX(textScale);
    }
    canvas.drawText(text, x / 2, 8 * y / 48, mPaints[2]);
  }


  @Override
  public void onPushed() {
    //Log.d(TutoState.class.getSimpleName(),"onPushed");
    GameActivity.instance.playMusic(false);

    BaseState bs = StateMachine.getIns().getPrevioustState();
    if (!(bs instanceof GameState))
      StateMachine.getIns().popState();
    pGame = (GameState) bs;

    pTitle = StateMachine.mContext.getString(
        pWin ? R.string.game_over_win_title : R.string.game_over_lose_title);

    pDescription = pWin ?
        String.format(StateMachine.mContext.getString(R.string.game_over_win_desc), pMovesCount) :
        StateMachine.mContext.getString(R.string.game_over_lose_desc);

    if (pWin) {

      effect = new ParticleSystem(GameActivity.instance, 20, R.drawable.star, 3000)
          .setSpeedByComponentsRange(-0.25f, 0.25f, -0.7f, -0.2f)
          .setAcceleration(0.000005f, 90)
          .setInitialRotationRange(0, 360)
          .setRotationSpeed(220)
          .setFadeOut(2000)
          .addModifier(new ScaleModifier(0f, 1f, 0, 1500));
      //.oneShot(GameView.getIns(), 12);
      effect.oneShot(GameActivity.instance.getBannerView(), 30);

    }
  }

  public void resize(float width, float height) {
    //Log.v("GameOverState","canvas size: "+width+"x"+height);
    pButtons.repositionDButton(0, 8.5f * width / 16, 31 * height / 48, 14.5f * width / 16, 36 * height / 48); //ok
    pButtons.repositionDButton(1, 1.5f * width / 16, 31 * height / 48, 7.5f * width / 16, 36 * height / 48); // no


    pButtons.setText(0, GameActivity.instance.getString(R.string.ok));
    pButtons.setText(1, GameActivity.instance.getString(R.string.no));

    if (pShareText != null) {

      pShareLabel = new DrawText(
          GameActivity.instance.getString(R.string.game_over_share_btn),
          1.5f * width / 16, 40 * height / 48, 7.5f * width / 16, 45 * height / 48);


      mBitmaps[0] = BitmapLoader.resizeImage(GameActivity.instance, R.drawable.btn_g_normal,
          false, 5 * height / 48, 5 * height / 48);

      mBitmaps[1] = BitmapLoader.resizeImage(GameActivity.instance, R.drawable.btn_g_pressed,
          false, 5 * height / 48, 5 * height / 48);

      mBitmaps[2] = BitmapLoader.resizeImage(GameActivity.instance, R.drawable.btn_fb_normal,
          false, 5 * height / 48, 5 * height / 48);

      mBitmaps[3] = BitmapLoader.resizeImage(GameActivity.instance, R.drawable.btn_fb_pressed,
          false, 5 * height / 48, 5 * height / 48);


      pButtons.repositionDButton(2, 8.5f * width / 16, 40 * height / 48,
          8.5f * width / 16 + 5 * height / 48, 45 * height / 48); // G+ share
      //pButtons.setText(2, GameActivity.instance.getString(R.string.game_over_share_btn));

      pButtons.repositionDButton(3, 14.5f * width / 16 - 5 * height / 48, 40 * height / 48,
          14.5f * width / 16, 45 * height / 48);// Fb share
    }

    bgColorText.setTextSize(GameView.mPortrait ? width / 16 : height / 16);
    whiteText.setTextSize(GameView.mPortrait ? width / 15 : height / 16);

    mPaints[1].setTextSize(GameView.mPortrait ? width / 13 : height / 13);
    textScale = 1.0f;
    mPaints[2].setTextScaleX(textScale);
    mPaints[2].setTextSize(GameView.mPortrait ? width / 7 : height / 7);

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

  public boolean onBackPressed() {
    GameActivity.instance.stopSound();
    quit();
    return true;
  }

  @Override
  public void onGameOver(boolean win, int moves, int gameMode, int boardSize) {
    pWin = win;
    pMovesCount = moves;
    //bgColor = win? Color.rgb(21, 183, 46):Color.RED;
    bgColor = Color.DKGRAY;
    bgColorText.setColor(Color.rgb(0, 162, 232));

    if (win && gameMode == Constants.STEP && GameActivity.instance.isGAPPSavailable())
      pShareText = String.format(StateMachine.mContext.getString(R.string.game_over_share_txt),
          StateMachine.mContext.getString(Constants.BOARD_NAMES_IDS[boardSize]), moves);

    ((GameFinishedListener) GameActivity.instance).onGameOver(win, moves, gameMode, boardSize);
  }
}
