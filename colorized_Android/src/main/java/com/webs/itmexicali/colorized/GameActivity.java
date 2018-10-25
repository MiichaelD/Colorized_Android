package com.webs.itmexicali.colorized;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer.Result;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;
import com.google.example.games.basegameutils.BaseGameActivity;
import com.webs.itmexicali.colorized.GameView.SurfaceListener;
import com.webs.itmexicali.colorized.ads.Advertising;
import com.webs.itmexicali.colorized.board.Constants;
import com.webs.itmexicali.colorized.gamestates.BaseState;
import com.webs.itmexicali.colorized.gamestates.GameState;
import com.webs.itmexicali.colorized.gamestates.GameState.GameFinishedListener;
import com.webs.itmexicali.colorized.gamestates.StateMachine;
import com.webs.itmexicali.colorized.util.Const;
import com.webs.itmexicali.colorized.util.GameStatsSync;
import com.webs.itmexicali.colorized.util.Log;
import com.webs.itmexicali.colorized.util.Notifier;
import com.webs.itmexicali.colorized.util.Platform;
import com.webs.itmexicali.colorized.util.ProgNPrefs;
import com.webs.itmexicali.colorized.util.PushNotificationHelper;
import com.webs.itmexicali.colorized.util.Screen;
import com.webs.itmexicali.colorized.util.Tracking;

public class GameActivity extends BaseGameActivity implements GameFinishedListener, SurfaceListener {

  /** This activity instance, to access its members from other classes. */
  public static GameActivity instance; //TODO: make it private and refactor where needed.

  /** Request codes we use when invoking an external activity. */
  private static final int RC_UNUSED = 5001, RC_SHARE = 742;//, RC_RESOLVE = 50007;

  private static final String APP_PLAY_STORE_URL =
      "https://play.google.com/store/apps/details?id=com.webs.itmexicali.colorized";

  private CallbackManager callbackManager;
  private ShareDialog shareDialog;

  private Advertising pAds = null; // Ads handler.
  private boolean firstGameFinished = true; // First finished game since the app being launched
  /** Media player to play sounds of user interactions & background music. */
  private MediaPlayer soundPlayer = null, musicPlayer = null;
  private String mPlayerName = null;
  private boolean p_subscribeForPush = false;

  public enum SoundType {
    NONE, TOUCH, WIN, LOSE
  }

  public static GameActivity getActivity() {
    return instance;
  }

  @SuppressLint({"InlinedApi", "NewApi"})
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.v(GameActivity.class.getSimpleName(), "onCreate()");
    enableDebugLog(Const.D);
    instance = this;

    Screen.setFullScreen(this); // FullScreen with no Action and Navigation Bars
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //Keep screen on

    Platform.init(this);// Keep current version and locale on tracking info
    Tracking.shared().updateVersion(Platform.getVersionName(), Platform.getVersionCode());
    Tracking.shared().updateLocale(Platform.getLocale());

    // INCREMENT THE APP OPENED COUNTER if onCreate has no savedState ONLY
    // AFTER FINISHING THE FIRST GAME since app launch
    if (savedInstanceState == null) {
      firstGameFinished = true;
    }

    if (!StateMachine.isSetUp()) {
      StateMachine.setup(this); //Init StateMachine
    }
    setContentView(R.layout.game_screen); // Set the game screen
    // Set surface listener to reposition Sign-In Button
    ((GameView) findViewById(R.id.gameView)).setSurfaceListener(this);

    callbackManager = CallbackManager.Factory.create();
    shareDialog = new ShareDialog(this);
    shareDialog.registerCallback(callbackManager, facebookCallback);

    initAds();
    Notifier.getInstance(this).clearAll();
  }

  private final FacebookCallback<Result> facebookCallback = new FacebookCallback<Result>() {
    @Override
    public void onSuccess(Result result) {
      GameStatsSync.revealAchievement(getApiClient(), R.string.achievement_sharing_is_good);
      Log.d(GameActivity.class.getSimpleName(), "onFbShare postID=" + result.getPostId());
    }

    @Override
    public void onCancel() {
      Log.d(GameActivity.class.getSimpleName(), "onFbShare didCancel= true");
    }

    @Override
    public void onError(FacebookException error) {
      Log.e(GameActivity.class.getSimpleName(),
          String.format("onFbShare Error: %s", error.toString()));
      GameStatsSync.revealAchievement(getApiClient(), R.string.achievement_sharing_is_good);
    }
  };

  public void onSurfaceChanged(float width, float height) {
    //buttonSI.setPadding((int)(8*width/16), (int)(38*height/48), 0, 0);
    if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean(Notifier.DIRECT_TO_GAME)) {
      Log.i("GameActivity", Notifier.DIRECT_TO_GAME + " from getIntent().getExtras()");
      GameState gameState = (GameState) BaseState.stateFactory(BaseState.statesIDs.GAME);
      gameState.setAskToPlaySavedGame(false);
      StateMachine.getIns().pushState(gameState);
      getIntent().getExtras().remove(Notifier.DIRECT_TO_GAME);
    }
  }

  /**
   * Load ads if they are enabled, depending on the ads service using load
   * the corresponding one AirPush or AdMob
   */
  private void initAds() {

		/* Now the ads object is just created once, not destroying it and loading it if
     * it already exists, to change dad, remove the pAds == null validation from next
		 * if clause*/
    if (Advertising.SHOW_ADS && pAds == null) {
      LinearLayout layout = (LinearLayout) findViewById(R.id.LayMain);

			/* If we want to create a new banner, destroy and remove current if it exists*/
      if (pAds != null && pAds.getBanner() != null) {
        layout.removeView(pAds.getBanner());
        pAds.destroyBanner();
      }

      LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
      params.gravity = android.view.Gravity.CENTER_HORIZONTAL;

      pAds = Advertising.instantiate(this);
      pAds.getBanner().setLayoutParams(params);

			/* When the game_Screen.xml root tag used to be RelativeLayout, the ad was just
	         OVER the GameView, now the ad shares the screen size with GameView
	        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
	        		RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
	        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
	    	params.addRule(RelativeLayout.CENTER_HORIZONTAL);
	    	layout.addView(adView, params);	    	*/

      // Add the AdView to the view hierarchy. The view will have no size
      // until the ad is loaded.
      layout.addView(pAds.getBanner());
    }
  }

  public View getBannerView() {
    return pAds.getBanner();
  }

  /**
   * Loads Interstitial ads, some ads-services like airPush needs to call SmartWallAds before
   * showing them.
   */
  public void loadInterstitial() {
    Log.d(this.getLocalClassName(), "Loading interstitial");
    if (pAds != null)
      pAds.loadInterstitial();
  }

  /** Invokes displayInterstitial() when you are ready to display an interstitial. */
  public void displayInterstitial() {
    Log.d(this.getLocalClassName(), "Displaying interstitial");
    if (pAds != null)
      pAds.showInterstitial();
  }

  @Override
  public void onStart() {
    super.onStart();
    Log.v(GameActivity.class.getSimpleName(), "onStart()");

    startMusicPlayer();
  }

  @Override
  public void onResume() {
    super.onResume();
    Log.v(GameActivity.class.getSimpleName(), "onResume()");
    Tracking.shared().onResume(this);
    updateCurrentState(); // In case user sign-out from GPS Achievements/Leaderboards UI
    Screen.setFullScreen(this); // Keep sticky full immersive screen in case we lost it
    playMusic(true);

    if (pAds != null) {
      pAds.onResume();
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    Log.v(GameActivity.class.getSimpleName(), "onPause()");

    // Uploads the file containing the logged events. The onPause method is
    // guaranteed to be called in the life cycle of an Android App, so we
    // are guaranteed the events log file are uploaded
    Tracking.shared().onPause();
    playMusic(false);

    if (pAds != null) {
      pAds.onPause();
    }
  }

  @Override
  public void onStop() {
    Log.v(GameActivity.class.getSimpleName(), "onStop()");
    stopSound();
    stopMusicPlayer();
    super.onStop();
  }

  @Override
  public void onDestroy() {
    Log.v(GameActivity.class.getSimpleName(), "onDestroy()");
    super.onDestroy();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    Log.v(GameActivity.class.getSimpleName(), "onSaveInstanceState()");
  }

  private void scheduleReminderNotificationForSavedGame() {
    if (ProgNPrefs.getIns().isGameSaved()) {
      Notifier notifier = Notifier.getInstance(GameActivity.instance);
      notifier.clearAll();
      String title = getResources().getString(R.string.notif_title_need_you);
      String msg = getResources().getString(R.string.notif_msg_need_you);
      int oneDayInMinutes = (60 * 24 - 15);
      notifier.schedule(title, msg, SplashScreen.class, oneDayInMinutes, true);
    }
  }

  /** Displays an exit confirmation dialog to prevent accidentally quitting the game.  */
  public void showExitDialog() {
    //Use the Builder class for convenient dialog construction
    new AlertDialog.Builder(GameActivity.this)
        .setMessage(R.string.exit_confirmation)
        .setPositiveButton(R.string.ok, (unusedDialog, unusedId) -> { // Exit the game
          scheduleReminderNotificationForSavedGame();
          GameActivity.instance = null;
          GameActivity.this.finish();
        })
        .setNegativeButton(R.string.cancel, (unusedDialog, unusedId) -> { // dismiss menu
          Screen.setFullScreen(GameActivity.this);
          playMusic(true);
        })
        .setOnCancelListener(unusedDialog -> {
          Screen.setFullScreen(GameActivity.this);
          playMusic(true);
        })
        .create()
        .show();
  }

  @Override
  public void onBackPressed() {
    if (StateMachine.getIns().onBackPressed()) {
      return;
    }
    showExitDialog();
    super.onBackPressed();
  }

  /**
   * Plays sound when user interacts with the UI.
   *
   * @param soundType to select type of sound to play
   */
  public void playSound(SoundType soundType) {
    if (!ProgNPrefs.getIns().playSFX()) {
      return;
    }
    stopSound();

    switch (soundType) {
      case TOUCH:
        soundPlayer = MediaPlayer.create(this, R.raw.confirm);
        break;
      case WIN:
        soundPlayer = MediaPlayer.create(this, R.raw.win);
        break;
      case LOSE:
        soundPlayer = MediaPlayer.create(this, R.raw.lose);
        break;
      case NONE:
      default:
        break;
    }
    soundPlayer.start();
  }

  /**
   * If there is a sound playing, stop and release it so we can restart it later
   */
  public void stopSound() {
    try {
      if (soundPlayer != null) {
        soundPlayer.stop();
        soundPlayer.release();
        soundPlayer = null;
      }
    } catch (IllegalStateException ise) { /* Do Nothing*/ }
  }

  /**
   * Play/Stop Background music
   *
   * @param play true to play music, false to pause it
   */
  public void playMusic(boolean play) {
    if (musicPlayer == null)
      startMusicPlayer();

    //if we have an instance of the player and the user wants to play sounds
    if (play && ProgNPrefs.getIns().playMusic()) {
      if (!musicPlayer.isPlaying())
        musicPlayer.start();
    } else if (musicPlayer.isPlaying()) {
      musicPlayer.pause();
    }
  }

  private void startMusicPlayer() {
    //start Player with a resource
    if (musicPlayer == null) {
      musicPlayer = MediaPlayer.create(this, R.raw.music);
      musicPlayer.setLooping(true);
    }
  }

  private void stopMusicPlayer() {
    try {
      if (musicPlayer != null) {
        musicPlayer.stop();
        musicPlayer.release();
        musicPlayer = null;
      }
    } catch (IllegalStateException ise) { /* Do Nothing*/ }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    super.onActivityResult(requestCode, resultCode, intent);
    callbackManager.onActivityResult(requestCode, resultCode, intent);

    if (requestCode == RC_SHARE) {
      if (resultCode == RESULT_OK) {
        GameStatsSync.unlockAchievement(getApiClient(), R.string.achievement_sharing_is_good);
        Log.d(GameActivity.class.getSimpleName(), "onGoogleShare Successful");
      } else
        GameStatsSync.revealAchievement(getApiClient(), R.string.achievement_sharing_is_good);
    }
  }

  @Override
  public void onSignInFailed() {
    // Show sign-in button on main menu
    updateCurrentState();
    mPlayerName = null;
  }
	
	/*------------------------ LEADERBOARDS and ACHIEVEMENTS METHODS -------------------------------*/
  @Override
  public void onSignInSucceeded() {
    Player player = Games.Players.getCurrentPlayer(getApiClient());
    // Show sign-out button on main menu
    // Show "you are signed in" message on win screen, with no sign in button.
    // Set the greeting appropriately on main menu
    // Sync data between local  and google games services saves
    GameStatsSync.syncLeaderboardsScores(getApiClient(), true);
    GameStatsSync.syncAchievements(getApiClient());

    // We also want to keep track of how many times the user has signed in
    if (player != null) {
      //subscribe for push notifications only when player has signed in
      //using his google account.
      if (!p_subscribeForPush) {
        PushNotificationHelper push = new PushNotificationHelper();
        push.subscribeToPush(player.getPlayerId());
        p_subscribeForPush = true;
      }

      Log.v(GameActivity.class.getSimpleName(), "onSignInSucceeded tracking player properties");
      //track the player info
      Tracking.shared().onPlayerIdUpdated(player.getPlayerId());
      Tracking.shared().setPlayerProperty("$name", player.getDisplayName());
      Tracking.shared().setPlayerProperty("google_title", player.getTitle());
      Tracking.shared().setPlayerProperty("google_level", Integer.toString(player.getLevelInfo().getCurrentLevel().getLevelNumber()));
      if (player.hasHiResImage())
        Tracking.shared().setPlayerProperty("image", player.getHiResImageUrl());
      if (player.hasIconImage())
        Tracking.shared().setPlayerProperty("icon", player.getIconImageUrl());
      Tracking.shared().track("onSignIn", null);
    }
    updateCurrentState(); // Update state to display the player's name

    if (mActivityToShow == ACHIEVEMENTS_AFTER_SIGNIN) {
      onShowAchievementsRequested();
    } else if (mActivityToShow == LEADERBOARDS_AFTER_SIGNIN) {
      onShowLeaderboardsRequested();
    }
  }

  /** If signed in, Returns the player name if signed in, null otherwise. */
  public String getPlayerName() {
    if (mPlayerName != null) {
      return mPlayerName;
    }

    final int maxNumOfNames = 3;
    if (isSignedIn()) {
      Player player = Games.Players.getCurrentPlayer(getApiClient());
      if (player != null) {
        mPlayerName = player.getDisplayName();
        String space = " ";
        String[] names = mPlayerName.split(space);
        if (names.length > maxNumOfNames) {
          StringBuilder sb = new StringBuilder(names[0]);
          for (int i = 1; i < maxNumOfNames; ++i) {
            sb.append(space).append(names[i]);
          }
          mPlayerName = sb.toString();
        }
      }
      Log.d(GameActivity.class.getSimpleName(), "User: " + mPlayerName);
    }
    return mPlayerName;
  }

  /** Tries to launch the Google+ share dialog. */
  public boolean onGoogleShareRequested(String text) {
    Log.d(GameActivity.class.getSimpleName(), "Launch the Google+ share.");
    if (isSignedIn()) {
      Intent shareIntent = new com.google.android.gms.plus.PlusShare.Builder(this)
          .setType("text/plain")
          .setText(text)
          .setContentUrl(Uri.parse(APP_PLAY_STORE_URL))
          .getIntent();

      // This flag ensures that when we reopen the app, it doesn't show the sharing intent screen
      shareIntent.addFlags(
          VERSION.SDK_INT < VERSION_CODES.LOLLIPOP
              ? Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET
              : Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
      startActivityForResult(shareIntent, RC_SHARE);
      Tracking.shared().onShare("Google");
      return true;
    } else {
      showAlert(getString(R.string.share_not_available));
    }
    return false;
  }

  /** Tries to launch the Facebook share dialog. */
  public boolean onFbShareRequested(String text, String pictureURL) {
    if (pictureURL == null) {
      pictureURL = "https://image.ibb.co/mYA2Rn/10603423_531397536990342_6991488415590407112_n.png";
    }

    if (ShareDialog.canShow(ShareLinkContent.class)) {
      ShareLinkContent linkContent = new ShareLinkContent.Builder()
          .setContentUrl(Uri.parse(getString(R.string.app_url)))
          .setQuote(text)
          .setShareHashtag(new ShareHashtag.Builder()
              .setHashtag(getString(R.string.app_hashtag))
              .build())
          .setImageUrl(Uri.parse(pictureURL))
          .build();
      shareDialog.show(linkContent);
    }
    Tracking.shared().onShare("Facebook");
    return true;
  }

  /**
   * Tries to show Achievements activity, if not signed in, show message.
   *
   * @return true if Achievements were shown
   */
  public boolean onShowAchievementsRequested() {
    if (isSignedIn()) {
      Tracking.shared().track("Achievements", null);
      startActivityForResult(Games.Achievements.getAchievementsIntent(getApiClient()), RC_UNUSED);
      return true;
    } else {
      if (isGAPPSavailable()) {
        // showAlert(getString(R.string.achievements_not_available));
        signInAndShow(ACHIEVEMENTS_AFTER_SIGNIN);
      } else {
        StateMachine.getIns().pushState(BaseState.statesIDs.STATS);
      }
    }
    return false;
  }

  /**
   * Tries to show Leaderboards activity, if not signed in, show message
   *
   * @return true if Leaderboards were shown
   */
  public boolean onShowLeaderboardsRequested() {
    if (isSignedIn()) {
      Tracking.shared().track("Leaderboards", null);
      startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(getApiClient()), RC_UNUSED);
      return true;
    } else {
      if (isGAPPSavailable()) {
        // showAlert(getString(R.string.leaderboards_not_available));
        signInAndShow(LEADERBOARDS_AFTER_SIGNIN);
      } else {
        StateMachine.getIns().pushState(BaseState.statesIDs.STATS);
      }
    }
    return false;
  }

  /** Unlocks achievement on tutorial finishing. */
  public void onTutorialFinished() {
    //Achievement Finish the tutorial
    GameStatsSync.unlockAchievement(getApiClient(), R.string.achievement_now_i_get_it);
  }

  /**
   * Saves the finished game info the info locally and tries to push it to Google Games Services.
   */
  public void onGameOver(boolean win, int moves, int gameMode, int boardSize) {
    if (firstGameFinished) {
      firstGameFinished = false;
      ProgNPrefs.getIns().incrementAppOpened();// LOCALLY
    }

    ProgNPrefs.getIns().updateGameFinished(boardSize, gameMode, win);

    // Show interstitial every 2 games.
    if ((ProgNPrefs.getIns().getGamesFinished(Constants.TOTAL_SIZES) % Advertising.GAMEOVERS_TO_INTERSTITIAL) == 0)
      GameActivity.instance.displayInterstitial();
    else
      GameActivity.instance.loadInterstitial();

    // Update achievements and leaderboards.
    GameStatsSync.updateAchievementsAndLeaderboards(getApiClient(), win, moves, gameMode, boardSize);
  }

  /** Starts sign-in flow by user interaction. */
  public void onSignInButtonClicked() {
    if (isSignedIn() || getApiClient().isConnecting() || getApiClient().isConnected())
      return;
    Log.d(GameActivity.class.getSimpleName(), "onSignInButtonClicked()");
    mPlayerName = null;
    beginUserInitiatedSignIn();
  }

  /** Starts sign-out flow by user interaction. */
  public void onSignOutButtonClicked() {
    signOut();
    mPlayerName = null;
  }

  /** Updates currently active state in the statestack. */
  private void updateCurrentState() {
    if (StateMachine.getIns().getCurrentState() != null)
      StateMachine.getIns().getCurrentState().onFocus();
  }
}
