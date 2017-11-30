package com.webs.itmexicali.colorized.board;

import com.webs.itmexicali.colorized.R;

public class Constants {

  // Game board constants
  /**
   * Game modes
   */
  public final static int STEP = 0, CASUAL = 1, TOTAL_MODES = 2;

  /**
   * Board Sizes
   */
  public final static int SMALL = 0, MEDIUM = 1, LARGE = 2, TOTAL_SIZES = 3;

  /**
   * Board constants
   */
  public final static int MOV_LIMS[] = {21, 34, 44}, BOARD_SIZES[] = {12, 18, 24};

  public final static int BOARD_NAMES_IDS[] = {R.string.options_easy, R.string.options_med, R.string.options_hard};

  public final static String COLOR_NAMES[] = {"RED", "BLUE", "YELLOW", "PURPLE", "GRAY", "GREEN"};

  public final static String GAME_MODES[] = {"STEP", "CASUAL"};
}
