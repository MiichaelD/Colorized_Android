package com.webs.itmexicali.colorized.board;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.text.TextPaint;

import com.webs.itmexicali.colorized.util.Log;
import com.webs.itmexicali.colorized.util.ProgNPrefs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.Scanner;

import ProtectedInt.ProtectedInt;

/**
 * This class will hold the matrix of colors and handle
 * the users interactions.
 */
public class ColorBoard {

  public static final int NUMBER_OF_COLORS = 6;
  private static final int ALL_COLORS_FINISHED = 63; //(2^6 -1 )
  private final int FINISHED_COLORS_FLAGS[] = {1, 2, 4, 8, 16, 32};
  /** */
  private GameBoardListener m_listener;
  /**
   * the matrix holding the color blocks
   */
  private int m_colorMatrix[][];
  /**
   * number of blocks per side in the square matrix
   */
  private int m_blocksPerSide;
  /**
   * Actual and Total movements counters
   */
  private ProtectedInt m_movesCount = new ProtectedInt(), m_movesLimit = new ProtectedInt();
  /**
   * handle concurrent access to colorize method, not using blocking synchronized modifier
   */
  private boolean m_isColorizing = false;
  /**
   * Structure to hold blocks to be checked
   */
  private LinkedList<Integer[]> m_blocksToCheck;
  /**
   * variable containing finished colors, each bit is either 1 for finished
   * or 0 for unfinished
   */
  private int finishedColors = 0;
  /**
   * Counter of each color
   */
  private int m_colorCounter[];

  /**
   * Initialize a new random {@link ColorBoard} matrix
   */
  public ColorBoard(int blocks) {
    randomize(blocks);
  }

  /**
   * Load a given {@link ColorBoard} matrix
   *
   * @param blocks number of blocks per side of board
   * @param moves  number of user interactions in loaded board
   * @param mat    a reference to the board representation as an array of integers
   */
  public ColorBoard(int blocks, int cur_moves, int moves_lim, int[][] mat) {
    if (mat == null) {
      randomize(blocks);
      return;
    }
    m_movesCount.set(cur_moves);
    m_movesLimit.set(moves_lim);
    m_blocksPerSide = blocks;
    m_colorMatrix = mat;
    restartFinishedColors();
    setDefaultMoveLimit();
  }

  /**
   * Given a string containing a saved ColorBoard state,
   * parse it to create a new game state identical
   */
  public static ColorBoard newBoardFromString(String state) {
    if (state == null)
      return null;

    //try reading it as a Json if it fails, continue with legacy parsing
    try {
      JSONObject object = new JSONObject(state);
      return newBoardFromJson(object);
    } catch (Exception e) {
      Log.v(ColorBoard.class.getSimpleName(), "Parsing board as Json failed. continuing to parse it as a regular string");
    }

    Scanner scanner = null;
    ColorBoard colorBoard = null;
    try {
      scanner = new Scanner(state);
      int mov_lim = scanner.nextInt();
      int bps = scanner.nextInt();
      int moves = scanner.nextInt();
      int[][] board = new int[bps][bps];
      for (int i = 0; i < bps; i++)
        for (int j = 0; j < bps; j++)
          board[i][j] = scanner.nextInt();
      colorBoard = new ColorBoard(bps, moves, mov_lim, board);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (scanner != null)
        scanner.close();
    }
    return colorBoard;
  }

  /**
   * Given a string containing a saved ColorBoard state,
   * parse it to create a new game state identical
   */
  public static ColorBoard newBoardFromJson(JSONObject object) {
    if (object == null)
      return null;

    try {
      int bps = object.getInt("blocks_per_side");
      int moves = object.getInt("moves_count");
      int mov_lim = object.getInt("moves_limit");
      String matrix = object.getString("board_matrix");
      int[][] board = new int[bps][bps];
      for (int i = 0; i < bps; i++)
        for (int j = 0; j < bps; j++)
          //note that this only works if the max number of colors is 10 or less
          board[i][j] = matrix.charAt(i * bps + j) - '0';

      ColorBoard colorBoard = new ColorBoard(bps, moves, mov_lim, board);
      return colorBoard;
    } catch (JSONException e) {
      Log.e(ColorBoard.class.getSimpleName(), "Error parsing game from Json");
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Start a new random matrix and set moves to 0
   *
   * @param blocks the new number of blocks per side of the matrix
   */
  public void randomize(int blocks) {
    m_blocksPerSide = blocks;
    m_colorMatrix = new int[blocks][blocks];
    randomize();
  }

  /**
   * Start a new random matrix and reset moves counter
   */
  public void randomize() {
    this.m_movesCount.set(0);
    for (int i = 0; i < m_blocksPerSide; i++)
      for (int j = 0; j < m_blocksPerSide; j++)
        m_colorMatrix[i][j] = (int) (Math.random() * NUMBER_OF_COLORS);

    restartFinishedColors();
    setDefaultMoveLimit();
  }

  /**
   * check if we have 1 or more moves remaining
   */
  public boolean hasMovesRemaining() {
    int total = getMovesLimit();
    return (getMovesCount() < total) || (total < 0);
  }

  /**
   * set a listener to deliver board notifications
   */
  public void setGameBoardListener(GameBoardListener listener) {
    m_listener = listener;
  }

  /**
   * Restart the variable containing the finished colors
   */
  private void restartFinishedColors() {
    finishedColors = 0;
    updateBoardStatus();
  }

  /**
   * Create an identical new board
   */
  public ColorBoard clone() {
    ColorBoard toReturn = new ColorBoard(m_blocksPerSide, m_movesCount.get(), m_movesLimit.get(), cloneMatrix());
    return toReturn;
  }

  public int colorRepetitions(int color) {
    return m_colorCounter[color];
  }

  /**
   * Store the color finished
   */
  private void addFinishedColor(int color) {
    finishedColors |= FINISHED_COLORS_FLAGS[color];
  }

  private void removeFinishedColor(int color) {
    if (isColorFinished(color))
      finishedColors ^= FINISHED_COLORS_FLAGS[color];
  }

  public boolean allColorsFinished() {
    return finishedColors == ALL_COLORS_FINISHED;
  }

  /**
   * Check if the board is completed in one color or we ran out of moves
   *
   * @return true if game is over, false if not
   */
  public boolean isCompleted() {
    return allColorsFinished() || !hasMovesRemaining();
  }

  /**
   * Check if a color is finished
   *
   * @param color color to check if is finished
   */
  public boolean isColorFinished(int color) {
    return FINISHED_COLORS_FLAGS[color] == (finishedColors & FINISHED_COLORS_FLAGS[color]);
  }

  /**
   * Get the color of the main tile (upper-left corner)
   */
  public int getCurrentColor() {
    return m_colorMatrix[0][0];
  }

  /**
   * checks if the color from the parameter is the same as the current color
   *
   * @param color to compare with current color of the board
   */
  public boolean isCurrentColor(int color) {
    return color == getCurrentColor();
  }

  /**
   * Get number of blocks per side
   */
  public int getBlocksPerSide() {
    return m_blocksPerSide;
  }

  // ------------------------ BOARD CONTROLS ------------------------------/

  /**
   * Get the size of the board.
   *
   * @returns 0 for Small, 1 for Medium and 2 for Large sizes
   */
  public int getSize() {
    //transform it from blocks/side to boardsize Constantsant
    int boardSize = m_blocksPerSide == Constants.BOARD_SIZES[Constants.SMALL] ? Constants.SMALL :
        m_blocksPerSide == Constants.BOARD_SIZES[Constants.MEDIUM] ? Constants.MEDIUM : Constants.LARGE;
    return boardSize;
  }

  /**
   * Get the current game type, casual means there is no limit on the number of moves
   * and step is when you should finish the board before running out of moves.
   */
  public int getGameMode() {
    int total = getMovesLimit();
    //if the limit is negative or is the max value, we are playing casual
    return (total < 0 || total == Integer.MAX_VALUE) ? Constants.CASUAL : Constants.STEP;
  }

  /**
   * change the color of the blocks neighbor the main block
   * which are from the same color (than the main block as well)
   */
  public void colorize(int newColor) {
    //check if the new color is different from last one
    // not the best approach, but will block a few threads
    if (m_isColorizing || newColor == getCurrentColor())
      return;

    m_isColorizing = true;
    this.m_movesCount.increment();//count this move

    //check the Neighbor block
    //checkNeighborBlocks(1, 0, newColor);
    //checkNeighborBlocks(0, 1, newColor);
    m_blocksToCheck = new LinkedList<Integer[]>();
    m_blocksToCheck.add(new Integer[]{0, 0});
    checkNeighborBlocks(newColor);

    m_colorMatrix[0][0] = newColor; //update the main block's color
    updateBoardStatus();
    if (m_listener != null)
      m_listener.onBoardFloodingFinished(allColorsFinished());

    m_isColorizing = false;
  }

  /**
   * Update board status.
   *
   * @return true if the board is filled with 1 color
   */
  public boolean updateBoardStatus() {
    int i, j;
    m_colorCounter = new int[NUMBER_OF_COLORS];
    for (i = 0; i < m_blocksPerSide; i++)
      for (j = 0; j < m_blocksPerSide; j++)
        m_colorCounter[m_colorMatrix[i][j]]++;

    for (i = 0; i < NUMBER_OF_COLORS; i++) {
//			Log.i("ColorBoard", "Color "+ i+" count: "+m_colorCounter[i]);
      if (m_colorCounter[i] == 0)
        addFinishedColor(i);
      else
        removeFinishedColor(i);
    }
    addFinishedColor(m_colorMatrix[0][0]);
    return allColorsFinished();
  }

  /**
   * compare neighbors colors with main block's color to update
   * color recursively
   */
  @SuppressWarnings("unused")
  private void checkNeighborBlocks(int x, int y, int newColor) {
    //check if we are within the matrix boundaries and also that
    // the boarding neighbor is from the same color as the main block
    if ((x == 0 && y == 0) || x < 0 || x >= m_blocksPerSide || y < 0 ||
        y >= m_blocksPerSide || m_colorMatrix[0][0] != m_colorMatrix[x][y])
      return;

    m_colorMatrix[x][y] = newColor;
    checkNeighborBlocks(x - 1, y, newColor);//check left neighbor
    checkNeighborBlocks(x + 1, y, newColor);//check right neighbor
    checkNeighborBlocks(x, y - 1, newColor);//check upper neighbor
    checkNeighborBlocks(x, y + 1, newColor);//check below neighbor
  }

  /**
   * compare neighbors colors with main block's color to update
   * color iteratively
   */
  private void checkNeighborBlocks(int newColor) {
    Integer in[];
    int oldColor = m_colorMatrix[0][0];
    while (!m_blocksToCheck.isEmpty()) {
      in = m_blocksToCheck.removeLast();
      m_colorMatrix[in[0]][in[1]] = newColor;
      if (in[0] > 0 && m_colorMatrix[in[0] - 1][in[1]] == oldColor && !(in[0] - 1 == 0 && in[1] == 0)) {
        m_colorMatrix[in[0] - 1][in[1]] = newColor;
        m_blocksToCheck.add(new Integer[]{in[0] - 1, in[1]});
      }

      if (in[1] > 0 && m_colorMatrix[in[0]][in[1] - 1] == oldColor && !(in[0] == 0 && in[1] - 1 == 0)) {
        m_colorMatrix[in[0]][in[1] - 1] = newColor;
        m_blocksToCheck.add(new Integer[]{in[0], in[1] - 1});
      }

      if (in[0] < m_blocksPerSide - 1 && m_colorMatrix[in[0] + 1][in[1]] == oldColor) {
        m_colorMatrix[in[0] + 1][in[1]] = newColor;
        m_blocksToCheck.add(new Integer[]{in[0] + 1, in[1]});
      }

      if (in[1] < m_blocksPerSide - 1 && m_colorMatrix[in[0]][in[1] + 1] == oldColor) {
        m_colorMatrix[in[0]][in[1] + 1] = newColor;
        m_blocksToCheck.add(new Integer[]{in[0], in[1] + 1});
      }

    }

  }

  /**
   * return the matrix of colors
   */
  public int[][] getMatrix() {
    return m_colorMatrix;
  }

  public int[][] cloneMatrix() {
    int[][] toReturn = new int[m_blocksPerSide][m_blocksPerSide];
    for (int i = 0; i < m_blocksPerSide; i++)
      for (int j = 0; j < m_blocksPerSide; j++)
        toReturn[i][j] = m_colorMatrix[i][j];
    return toReturn;
  }

  /**
   * The amount of moves this game has processed
   */
  public int getMovesCount() {
    return this.m_movesCount.get();
  }

  /**
   * If the player has made more than 1 move, then its considered as he started playing
   */
  public boolean isStarted() {
    return m_movesCount.get() > 0;
  }

  /**
   * @returns the limit amount of moves this game can accept
   */
  public int getMovesLimit() {
    return this.m_movesLimit.get();
  }

  /**
   * @param moves the limit amount of moves this game can accept
   */
  public int setTotalMoves(int newTotal) {
    return this.m_movesLimit.set(newTotal);
  }

  /**
   * Set the move limit corresponding to the board size or set it to negative if the game
   * mode is CASUAL_MODE
   */
  public void setDefaultMoveLimit() {
    if (ProgNPrefs.getIns().getGameMode() == Constants.CASUAL)
      m_movesLimit.set(-1);
    else
      m_movesLimit.set(Constants.MOV_LIMS[ProgNPrefs.getIns().getDifficulty()]);
  }

  /**
   * draw the board within the rect given
   */
  public void updateBoard(Canvas canvas, RectF rectf, TextPaint[] paints) {
    float boardPixels = rectf.width();
    float blockPixels = boardPixels / m_blocksPerSide;
    float left = rectf.left;
    float top = rectf.top - blockPixels;

    for (int i = 0; i < m_blocksPerSide; i++) {
      top += blockPixels;
      for (int j = 0; j < m_blocksPerSide; j++)
        canvas.drawRect(left + blockPixels * j, top, left + blockPixels * (j + 1) + (j == m_blocksPerSide - 1 ? 0 : 5),
            top + blockPixels + (i == m_blocksPerSide - 1 ? 0 : 5), paints[m_colorMatrix[i][j]]);

    }

  }

  /**
   * Get the representation of current {@link ColorBoard} state
   *
   * @return Json formatted String formated
   */
  @Override
  public String toString() {
    return getCurrentState().toString();
//		return getCurrentStateAsLegacyString();

  }

  /**
   * Representation of current {@link ColorBoard} state
   *
   * @return string formated as follows: "<b>i j []</b>",
   * where <b>i</b> is the number of blocks per side
   * <b>j</b> is the number of moves at current time and
   * <b>[]</b> is the representation of the board as a succession of numbers
   */
  @SuppressWarnings("unused")
  @Deprecated
  private String getCurrentStateAsLegacyString() {
    StringBuilder sb = new StringBuilder();
    sb.append(m_movesLimit.get()).append(' ');
    sb.append(m_blocksPerSide).append(' ');
    sb.append(m_movesCount.get());
    for (int i = 0; i < m_blocksPerSide; i++)
      for (int k = 0; k < m_blocksPerSide; k++)
        sb.append(' ').append(m_colorMatrix[i][k]);
    return sb.toString();
  }

  private String getBoardRepresentation() {
    StringBuilder sb = new StringBuilder(m_blocksPerSide * m_blocksPerSide);
    for (int i = 0; i < m_blocksPerSide; i++)
      for (int k = 0; k < m_blocksPerSide; k++)
        sb.append(m_colorMatrix[i][k]);
    return sb.toString();
  }

  public JSONObject getCurrentState() {
    JSONObject curState = new JSONObject();
    try {
      curState.put("blocks_per_side", m_blocksPerSide);
      curState.put("moves_count", m_movesCount.get());
      curState.put("moves_limit", m_movesLimit.get());
      curState.put("board_matrix", getBoardRepresentation());

      //optionals
//			curState.put("game_mode", getGameMode());
    } catch (JSONException e) {/* there should be no exception*/}
    return curState;
  }

}
