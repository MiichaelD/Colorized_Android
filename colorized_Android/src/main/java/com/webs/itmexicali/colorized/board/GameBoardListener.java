package com.webs.itmexicali.colorized.board;

/**
 * Interface to request for a change in the GameBoard
 */
public interface GameBoardListener {
  /**
   * Ask for a new board
   *
   * @param forced if true, it will not matter if the player has a current game state
   *               if false, it will ask if want to restart
   */
  public void restartBoard(boolean forced);


  /**
   * Let the listener know that the operation on the board has been procesed
   * and if the board is completed
   */
  public void onBoardFloodingFinished(boolean won);
}
