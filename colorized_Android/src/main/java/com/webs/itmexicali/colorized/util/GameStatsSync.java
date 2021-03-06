package com.webs.itmexicali.colorized.util;

import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.achievement.Achievement;
import com.google.android.gms.games.achievement.AchievementBuffer;
import com.google.android.gms.games.achievement.Achievements.LoadAchievementsResult;
import com.google.android.gms.games.leaderboard.LeaderboardScore;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.games.leaderboard.Leaderboards;
import com.google.android.gms.games.leaderboard.Leaderboards.LoadPlayerScoreResult;
import com.webs.itmexicali.colorized.GameActivity;
import com.webs.itmexicali.colorized.R;
import com.webs.itmexicali.colorized.board.Constants;

import java.util.concurrent.TimeUnit;

public class GameStatsSync {

  static int sChanges = 0;

  /**
   * Update achievements and leaderboard after finishing a game
   */
  public static void updateAchievementsAndLeaderboards(GoogleApiClient gApiClient,
                                                       boolean win, int moves, int gameMode, int boardSize) {
    //NOTE AS VALIDATION, FOR EACH ACHIEVEMENT ADDED HERE, MAKE A SEARCH, YOU MUST
    //FIND THE ACHIEVEMENT INDEX AT LEAST 2 TIMES IN THIS FILE (updateAchievementsAndLEaderboards()
    // & syncAchievements() ) one EACH TIME THE GAME IS FINISHED AND ONE WHEN SYNC-ING

    //These achievements don't care of board sizes, gameModes or if the player won
    incrementAchievements(gApiClient, 1, new int[]{
        R.string.achievement_bored,
        R.string.achievement_really_bored,
        R.string.achievement_a_lot_of_free_time});

    revealFinishedCountAchievements(gApiClient);


    //if the game is not casual, finish 1000 games in any board size
    if (gameMode != Constants.CASUAL)
      incrementAchievement(gApiClient, 1, R.string.achievement_not_just_anybody);


    if (!win || gameMode == Constants.CASUAL)
      return;


    incrementAchievements(gApiClient, 1, new int[]{
        R.string.achievement_it_is_not_that_hard,//step mode win 5 games in any board size
        R.string.achievement_getting_used_to_it, //step mode win 100 games in any board size
        R.string.achievement_this_is_my_game});  //step mode win 1000 games in any board size


    switch (boardSize) {
      case Constants.SMALL:
        //Win small board step mode  < 20 movs
        if (moves < 20)
          unlockAchievement(gApiClient, R.string.achievement_premature);

        //step mode win 10,20,35,50,100 games in small board size
        incrementAchievements(gApiClient, 1, new int[]{
            R.string.achievement_small_amateur,
            R.string.achievement_small_pro,
            R.string.achievement_small_apprentice,
            R.string.achievement_small_teacher,
            R.string.achievement_small_champion});
        break;
      case Constants.MEDIUM:
        //Win medium board step mode  < 30 movs
        if (moves < 30)
          unlockAchievement(gApiClient, R.string.achievement_that_was_easy);

        //step mode win 10,20,35,50,100 games in medium board size
        incrementAchievements(gApiClient, 1, new int[]{
            R.string.achievement_amateur,
            R.string.achievement_apprentice,
            R.string.achievement_teacher,
            R.string.achievement_pro,
            R.string.achievement_champion});
        break;
      case Constants.LARGE:
        //Win large board step mode  < 39 movs
        if (moves < 39)
          unlockAchievement(gApiClient, R.string.achievement_big_flood);

        //step mode win 10,20,35,50,100 games in large board size
        incrementAchievements(gApiClient, 1, new int[]{
            R.string.achievement_big_amateur,
            R.string.achievement_big_apprentice,
            R.string.achievement_big_teacher,
            R.string.achievement_big_pro,
            R.string.achievement_big_champion});
        break;
      default:
        return;
    }


    checkStreakAchievements(gApiClient);
    revealWonCountPerBoard(gApiClient, Constants.TOTAL_SIZES);
    updateLeaderboards(gApiClient, boardSize, true);
  }


  /************************************************* ACHIEVEMENTS SYNC METHODS ****************************************************/

  /**
   * Push local data to google achievements counter, to increment or unlock depending
   * on locally saved progress. each time we sign in correctly
   */
  public static void syncAchievements(GoogleApiClient gApiClient) {
    //NOTE AS VALIDATION, FOR EACH ACHIEVEMENT ADDED HERE, MAKE A SEARCH, YOU MUST
    //FIND THE ACHIEVEMENT INDEX AT LEAST 2 TIMES IN THIS FILE (updateAchievementsAndLEaderboards()
    // & syncAchievements() ) one EACH TIME THE GAME IS FINISHED AND ONE WHEN SYNC-ING

    //These achievements don't care of board sizes, gameModes or if the player won
    if (ProgNPrefs.getIns().getGamesFinished(Constants.TOTAL_SIZES) > 0)
      setAchievementSteps(gApiClient,
          ProgNPrefs.getIns().getGamesFinished(Constants.TOTAL_SIZES),
          new int[]{
              R.string.achievement_bored,
              R.string.achievement_really_bored,
              R.string.achievement_a_lot_of_free_time});


    //if the game is not casual, finish 1000 games in any board size (sum of step mode finished counters)
    int sum = ProgNPrefs.getIns().getGamesFinished(Constants.SMALL) +
        ProgNPrefs.getIns().getGamesFinished(Constants.MEDIUM) +
        ProgNPrefs.getIns().getGamesFinished(Constants.LARGE);
    if (sum > 0)
      setAchievementSteps(gApiClient, sum, R.string.achievement_not_just_anybody);


    if (ProgNPrefs.getIns().getGamesWon(Constants.TOTAL_SIZES) > 0)
      setAchievementSteps(gApiClient,
          ProgNPrefs.getIns().getGamesWon(Constants.TOTAL_SIZES),
          new int[]{
              R.string.achievement_it_is_not_that_hard,//step mode win 5 games in any board size
              R.string.achievement_getting_used_to_it, //step mode win 100 games in any board size
              R.string.achievement_this_is_my_game});  //step mode win 1000 games in any board size

		
		/*
		 * 	TODO
		   //STILL TO IMPLEMENT,  so for now they only get unlocked if 
		   	done while connected.. which for me it is OK :)
		   	achievement_now_i_get_it		- Finish the tutorial
			achievement_premature			- Win small board step mode  < 20 movs
			achievement_that_was_easy		- Win medium board step mode  < 30 movs
			achievement_big_flood			- Win large board step mode  < 39 movs
		 *
		 */


    //step mode win 10,50,100 games in small board size
    if (ProgNPrefs.getIns().getGamesWon(Constants.SMALL) > 0)
      setAchievementSteps(gApiClient,
          ProgNPrefs.getIns().getGamesWon(Constants.SMALL),
          new int[]{
              R.string.achievement_small_amateur,  //step mode win 10 games in small board size
              R.string.achievement_small_apprentice,//step mode win 20 games in small board size
              R.string.achievement_small_teacher,  //step mode win 35 games in small board size
              R.string.achievement_small_pro,    //step mode win 50 games in small board size
              R.string.achievement_small_champion});//step mode win 100 games in small board size


    //step mode win 10,50,100 games in medium board size
    if (ProgNPrefs.getIns().getGamesWon(Constants.MEDIUM) > 0)
      setAchievementSteps(gApiClient,
          ProgNPrefs.getIns().getGamesWon(Constants.MEDIUM),
          new int[]{
              R.string.achievement_amateur,  //step mode win 10 games in medium board size
              R.string.achievement_apprentice,//step mode win 20 games in medium board size
              R.string.achievement_teacher,  //step mode win 35 games in medium board size
              R.string.achievement_pro,    //step mode win 50 games in medium board size
              R.string.achievement_champion});//step mode win 100 games in medium board size


    //step mode win 10,50,100 games in large board size
    if (ProgNPrefs.getIns().getGamesWon(Constants.LARGE) > 0)
      setAchievementSteps(gApiClient,
          ProgNPrefs.getIns().getGamesWon(Constants.LARGE),
          new int[]{
              R.string.achievement_big_amateur,  //step mode win 10 games in large board size
              R.string.achievement_big_apprentice,//step mode win 20 games in large board size
              R.string.achievement_big_teacher,  //step mode win 35 games in large board size
              R.string.achievement_big_pro,    //step mode win 50 games in large board size
              R.string.achievement_big_champion});//step mode win 100 games in large board size


    //if the app has been launched more than 50 times
    if (ProgNPrefs.getIns().getAppOpenedCount() > 0)
      setAchievementSteps(gApiClient, ProgNPrefs.getIns().getAppOpenedCount(),
          R.string.achievement_loyal_to_colors);

    checkStreakAchievements(gApiClient);
    revealFinishedCountAchievements(gApiClient);
    revealWonCountPerBoard(gApiClient, Constants.TOTAL_SIZES);


    compareAchievementsInfo(gApiClient);
  }

  /**
   * Compare achievements stored in google game services to unlock them within
   * the app and store them locally
   */
  public static void compareAchievementsInfo(GoogleApiClient gApiClient) {
    //NOTE AS VALIDATION, FOR EACH ACHIEVEMENT ADDED HERE, MAKE A SEARCH, YOU MUST
    //FIND THE ACHIEVEMENT INDEX AT LEAST 2 TIMES IN THIS FILE (updateAchievementsAndLEaderboards()
    // & syncAchievements() ) one EACH TIME THE GAME IS FINISHED AND ONE WHEN SYNC-ING

    final String loyal_to_colors = GameActivity.instance.getString(R.string.achievement_loyal_to_colors),
        not_just_anybody = GameActivity.instance.getString(R.string.achievement_not_just_anybody);

    boolean fullLoad = false;  // set to 'true' to reload all achievements (ignoring cache)

    PendingResult<LoadAchievementsResult> p = Games.Achievements.load(gApiClient, fullLoad);
    p.setResultCallback(new ResultCallback<LoadAchievementsResult>() {
      public void onResult(LoadAchievementsResult r) {
        int status = r.getStatus().getStatusCode();
        if (status != GamesStatusCodes.STATUS_OK) {
          r.release();// Error Occured
          return;
        }

        // cache the loaded achievements
        AchievementBuffer buf = r.getAchievements();
        int bufSize = buf.getCount();
        for (int i = 0; i < bufSize; i++) {
          Achievement ach = buf.get(i);

          // here you now have access to the achievement's data
          String id = ach.getAchievementId();
          String name = ach.getName() + "-(" + id + ")\t";  // the achievement ID string
          boolean unlocked = ach.getState() == Achievement.STATE_UNLOCKED;  // is unlocked
          boolean incremental = ach.getType() == Achievement.TYPE_INCREMENTAL;  // is incremental

          int steps = 0;
          if (incremental)
            steps = ach.getCurrentSteps();  // current incremental steps

          //get the app counter (TO KEEP COUNTING THEM)
          if (id.equals(loyal_to_colors) && steps > ProgNPrefs.getIns().getAppOpenedCount())
            ProgNPrefs.getIns().setAppOpened(steps, true);

            //get the total of games finished counter (TO KEEP COUNTING THEM)
          else if (id.equals(not_just_anybody) && steps > ProgNPrefs.getIns().getGamesFinished(Constants.TOTAL_SIZES))
            ProgNPrefs.getIns().setGamesFinishedValue(Constants.TOTAL_SIZES, steps, true);

          Log.i(GameStatsSync.class.getSimpleName(), "Ach:" + name + ",unlocked(" + unlocked + (incremental ? "), steps: " + steps + "/" + ach.getTotalSteps() : ")"));
          //Log.d(GameStatsSync.class.getSimpleName(),"Ach:"+name+",img("+ach.getRevealedImageUrl()+") - "+ach.getUnlockedImageUrl());
          //Log.d(GameStatsSync.class.getSimpleName(),"Ach:"+name+",img("+ach.getRevealedImageUri()+") - "+ach.getUnlockedImageUri());
          //System.out.println("EMPTYLINE");

        }
        buf.close();
        r.release();
      }
    });
  }


  /************************************************* ACHIEVEMENTS UTIL METHODS ****************************************************/


  /**
   * Set number of steps to the achievements given by the ids
   *
   * @params incAmount the amount to increment
   * @params achievementsIDs the ids of acheivements to increment
   */
  private static void setAchievementSteps(GoogleApiClient apiClient, int incAmount, int[] achievementsIDs) {
    for (int i : achievementsIDs)
      setAchievementSteps(apiClient, incAmount, i);
  }

  /**
   * Set number of steps to the achievement given by its id
   *
   * @params incAmount the amount to increment
   * @params achievementsID the id of acheivement to increment
   */
  private static void setAchievementSteps(GoogleApiClient apiClient, int incAmount, int achievementID) {
    if (GameActivity.instance.isSignedIn())
      Games.Achievements.setSteps(apiClient, GameActivity.instance.getString(achievementID), incAmount);
  }


  /**
   * Increment achievements by ids
   *
   * @params incAmount the amount to increment
   * @params achievementsIDs the ids of acheivements to increment
   */
  private static void incrementAchievements(GoogleApiClient apiClient, int incAmount, int[] achievementsIDs) {
    for (int i : achievementsIDs)
      incrementAchievement(apiClient, incAmount, i);
  }

  /**
   * Increment an achievement given by its id
   *
   * @params incAmount the amount to increment
   * @params achievementsID the id of acheivement to increment
   */
  public static void incrementAchievement(GoogleApiClient apiClient, int incAmount, int achievementID) {
    if (GameActivity.instance.isSignedIn())
      Games.Achievements.increment(apiClient, GameActivity.instance.getString(achievementID), incAmount);
  }


  /**
   * Unlock an achievement by its ID
   *
   * @params achievementsId the id of acheivements to unlock
   */
  public static void unlockAchievement(GoogleApiClient apiClient, int achievementId) {
    String fallbackText = GameActivity.instance.getString(achievementId);
    if (GameActivity.instance.isSignedIn()) {
      Games.Achievements.unlock(apiClient, fallbackText);
    } else {
    	   /* Display an achievement toast, only if not signed in. If signed in,
    	    * the standard google games toast will appear, so we don't show our own.*/
      Toast.makeText(GameActivity.instance, GameActivity.instance.getString(R.string.achievement_unlocked), Toast.LENGTH_LONG).show();
    }
  }


  /**
   * Reveal an achievement by its ID
   *
   * @params achievementsId the id of acheivements to unlock
   */
  public static void revealAchievement(GoogleApiClient apiClient, int achievementId) {
    String fallbackText = GameActivity.instance.getString(achievementId);
    if (GameActivity.instance.isSignedIn()) {
      Games.Achievements.reveal(apiClient, fallbackText);
    }
  }


  /*********************************************************** GAME ACHIEVEMENTS ***************************************************/


  /**
   * Check if streak achievements (Win in step mode continuosly without loosing)
   * and unlock them if they are accomplished
   */
  public static void checkStreakAchievements(GoogleApiClient gApiClient) {
    //if best winning streak in step mode is over 3
    if (ProgNPrefs.getIns().getBestStreak() >= 3)
      unlockAchievement(gApiClient, R.string.achievement_feeling_lucky);

    //if best winning streak in step mode is over 5
    if (ProgNPrefs.getIns().getBestStreak() >= 5) {
      unlockAchievement(gApiClient, R.string.achievement_where_is_the_challenge);
      revealAchievement(gApiClient, R.string.achievement_777);
    }

    //if best winning streak in step mode is over 7
    if (ProgNPrefs.getIns().getBestStreak() >= 7) {
      unlockAchievement(gApiClient, R.string.achievement_777);
      revealAchievement(gApiClient, R.string.achievement_9_win_streak);
    }

    //if best winning streak in step mode is over 9
    if (ProgNPrefs.getIns().getBestStreak() >= 9)
      unlockAchievement(gApiClient, R.string.achievement_9_win_streak);
  }


  /**
   * Check if the finished counting achievements (Finish 32, 96, 192 games)
   * and reveal them if the previous one has already been unlocked
   */
  public static void revealFinishedCountAchievements(GoogleApiClient gApiClient) {
    int finished = ProgNPrefs.getIns().getGamesFinished(Constants.TOTAL_SIZES);
    if (finished > 95)
      revealAchievement(gApiClient, R.string.achievement_a_lot_of_free_time);
    else if (finished > 31)
      revealAchievement(gApiClient, R.string.achievement_really_bored);

  }


  /**
   * Check if the Won counting achievements (Finish 10, 20, 35, 50, 100 games)
   * and reveal them if the previous one has already been unlocked
   *
   * @param boardSize check for the specific boardsize index
   */
  public static void revealWonCountPerBoard(GoogleApiClient gApiClient, int boardSize) {
    switch (boardSize) {
      case Constants.SMALL:
        if (ProgNPrefs.getIns().getGamesWon(Constants.SMALL) > 34)
          return;
        else if (ProgNPrefs.getIns().getGamesWon(Constants.SMALL) > 19)
          revealAchievement(gApiClient, R.string.achievement_small_teacher);
        else if (ProgNPrefs.getIns().getGamesWon(Constants.SMALL) > 9)
          revealAchievement(gApiClient, R.string.achievement_small_apprentice);
        break;
      case Constants.MEDIUM:
        if (ProgNPrefs.getIns().getGamesWon(Constants.MEDIUM) > 34)
          return;
        else if (ProgNPrefs.getIns().getGamesWon(Constants.MEDIUM) > 19)
          revealAchievement(gApiClient, R.string.achievement_teacher);
        else if (ProgNPrefs.getIns().getGamesWon(Constants.MEDIUM) > 9)
          revealAchievement(gApiClient, R.string.achievement_apprentice);
        break;
      case Constants.LARGE:
        if (ProgNPrefs.getIns().getGamesWon(Constants.LARGE) > 34)
          return;
        else if (ProgNPrefs.getIns().getGamesWon(Constants.LARGE) > 19)
          revealAchievement(gApiClient, R.string.achievement_big_teacher);
        else if (ProgNPrefs.getIns().getGamesWon(Constants.LARGE) > 9)
          revealAchievement(gApiClient, R.string.achievement_big_apprentice);
        break;
      case Constants.TOTAL_SIZES:
      default:
        revealWonCountPerBoard(gApiClient, Constants.SMALL);
        revealWonCountPerBoard(gApiClient, Constants.MEDIUM);
        revealWonCountPerBoard(gApiClient, Constants.LARGE);
        break;

    }

  }


  /************************************************* LEADERBOARDS SYNC METHODS ****************************************************/

  /**
   * Compare Leaderboards scores stored in google game services to sync them
   * within the app's ones and store the greater ones locally
   */
  public static void syncLeaderboardsScores(final GoogleApiClient gApiClient, boolean byCallback) {
    sChanges = 0;

    int[] ids = {R.string.leaderboard_small_board,
        R.string.leaderboard_medium_board,
        R.string.leaderboard_large_board,
        R.string.leaderboard_all_board_sizes};

    for (int i = 0; i < ids.length && GameActivity.instance != null; i++) {

      final String lboardID = GameActivity.instance.getString(ids[i]);
      final int lboardNumber = i;
      // load leaderboards
      final PendingResult<LoadPlayerScoreResult> p = Games.Leaderboards.loadCurrentPlayerLeaderboardScore(gApiClient,
          lboardID, LeaderboardVariant.TIME_SPAN_ALL_TIME, LeaderboardVariant.COLLECTION_PUBLIC);

      if (byCallback) {
        loadLeaderboardByCallback(gApiClient, p, lboardNumber, lboardID);
      } else {
        new Thread(new Runnable() {
          public void run() {
            loadLeaderboardByWaiting(gApiClient, p, lboardNumber, lboardID);
          }
        }).start();
      }

    }
  }

  /**
   * Set a callback to the PendingResult containing the loadPlayerScoreResult, once the PendingResult is ready
   * it will trigger the onResult method to do certain action with the leaderboardScore info
   *
   * @param p       PendingResult to set a callback
   * @param bNumber the leaderboard number in the loop
   * @param bID     the ID of the leaderboard
   */
  private static void loadLeaderboardByCallback(final GoogleApiClient gApiClient,
                                                PendingResult<LoadPlayerScoreResult> p, final int bNumber, final String bID) {
    final long tempTime = System.currentTimeMillis();
    p.setResultCallback(new ResultCallback<Leaderboards.LoadPlayerScoreResult>() {
      public void onResult(Leaderboards.LoadPlayerScoreResult r) {
        syncLeaderboardInfo(gApiClient, r, bNumber, bID, tempTime);
      }
    });
  }

  /**
   * Wait for the PendingResult containing the loadPlayerScoreResult blocking current thread (KEEP IT IN MIND!!!),
   * once the PendingResult is ready it will return the leaderboardScore info to do certain action with it
   *
   * @param p       PendingResult to await for it to finish or timeout
   * @param bNumber the leaderboard number in the loop
   * @param bID     the ID of the leaderboard
   */
  private static void loadLeaderboardByWaiting(GoogleApiClient gApiClient,
                                               PendingResult<LoadPlayerScoreResult> p, int bNumber, String bID) {
    long tempTime = System.currentTimeMillis();
    long waitTime = 10;    // seconds to wait for achievements to load before timing out
    //if p.awaits() is not declared for PendingResult, check the IMPORTS TO BE CORRECT
    Leaderboards.LoadPlayerScoreResult r = (Leaderboards.LoadPlayerScoreResult) p.await(waitTime, TimeUnit.SECONDS);
    syncLeaderboardInfo(gApiClient, r, bNumber, bID, tempTime);
  }

  /**
   * Execute this method which processes the LeaderboardScore info once the result is returned
   *
   * @param r       LoadPlayerScoreResult containing all the info from the leaderboard
   * @param bNumber the leaderboard number in the loop
   * @param bID     the ID of the leaderboard
   * @param tTime   - TEMPORAL PARAMETER
   */
  private static void syncLeaderboardInfo(GoogleApiClient gApiClient, Leaderboards.LoadPlayerScoreResult r,
                                          int bNumber, String bID, long tTime) {

    if (r == null || r.getStatus().getStatusCode() != GamesStatusCodes.STATUS_OK) {
      return;// Error Occured
    }

    // cache the loaded achievements
    LeaderboardScore lScore = r.getScore();
    if (lScore == null) {
      incrementChangesAndNotifyOnce();
      updateLeaderboards(gApiClient, bNumber, false);
      return;
    }
    int score = (int) lScore.getRawScore();
    int local = ProgNPrefs.getIns().getGamesWon(bNumber);

    if (score < local) {
      incrementChangesAndNotifyOnce();
      updateLeaderboards(gApiClient, bNumber, false);

    } else if (local < score) {
      incrementChangesAndNotifyOnce();
      ProgNPrefs.getIns().setGamesWonValue(bNumber, score, true);
    }
    Log.d(GameStatsSync.class.getSimpleName(), "Leaderboard '" + bNumber + "' Score: Google-" + score + " == Local-" + local + " - changes: " + sChanges);
    //Log.d(GameStatsSync.class.getSimpleName(),"Tiempo en ejecucion "+bNumber+": "+(System.currentTimeMillis()-tTime));
  }


  /**
   * Update Google stored leaderboards with the user's local score.
   *
   * @param boardSize  to update in google game services.
   * @param totalSizes if true, also update total_sizes leaderboard
   */
  public static void updateLeaderboards(GoogleApiClient gApiClient, int boardSize, boolean totalSizes) {
    if (GameActivity.instance.isSignedIn()) {
      int id = 0;

      switch (boardSize) {
        case Constants.SMALL:
          id = R.string.leaderboard_small_board;
          break;
        case Constants.MEDIUM:
          id = R.string.leaderboard_medium_board;
          break;
        case Constants.LARGE:
          id = R.string.leaderboard_large_board;
          break;
        case Constants.TOTAL_SIZES:
          id = R.string.leaderboard_all_board_sizes;
          break;
        default:
          return;
      }

      //save on leaderboard game won counter for given boardSize
      Games.Leaderboards.submitScore(gApiClient,
          GameActivity.instance.getString(id), ProgNPrefs.getIns().getGamesWon(boardSize));

      if (totalSizes) {
        //save on leaderboard game won counter for all boardSizes
        Games.Leaderboards.submitScore(gApiClient,
            GameActivity.instance.getString(R.string.leaderboard_all_board_sizes),
            ProgNPrefs.getIns().getGamesWon(Constants.TOTAL_SIZES));
      }
    }
  }

  /**
   * Method to keep track of how many changes there have been in the last sync,
   * it also notifies the player that the progress is being uploaded
   */
  private static void incrementChangesAndNotifyOnce() {
    if (sChanges == -1) { // TODO
      GameActivity.instance.runOnUiThread(new Runnable() {
        public void run() {
          Toast.makeText(GameActivity.instance,
              GameActivity.instance.getString(R.string.your_progress_will_be_uploaded),
              Toast.LENGTH_LONG)
              .show();
        }
      });
    }
    sChanges++;
  }

}
