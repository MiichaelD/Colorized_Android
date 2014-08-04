package com.webs.itmexicali.colorized;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

	//Application Context
	Context mContext;
	
	//SharedPreferences to read and edit
	SharedPreferences sp;
	SharedPreferences.Editor spEdit;
	
	//singleton instance
	private static Preferences instance;
	
	// create a new instance of Preferences
	public static Preferences initPreferences(Context context){
		instance = new Preferences(context);
		return instance;
	}
	
	//get preferences singleton
	public static Preferences getIns(){
		return instance;
	}
	
	Preferences(Context context){
		mContext = context;
		sp = context.getSharedPreferences(Const.TAG, 0);
		spEdit = sp.edit();
	}
	
	/** Check if the tutorial has been completed or cancelled by the user*/
	boolean isTutorialCompleted(){
		return sp.getBoolean(mContext.getString(R.string.key_tutorial_played), false);
	}
	
	/** Check if music will be played*/
	boolean playMusic(){
		return sp.getBoolean(mContext.getString(R.string.key_music),true);
	}
	
	/** Check if sound effects will be played*/
	boolean playSFX(){
		return sp.getBoolean(mContext.getString(R.string.key_sound),true);
	}
	
	/** return current board size or default*/
	int getBoardSize(){
		return sp.getInt(mContext.getString(R.string.key_board_size), 12);
	}

	/** Check if there is a game saved*/
	boolean isGameSaved(){
		return sp.getBoolean(mContext.getString(R.string.key_is_game_saved), false);
	}
	
	/** get the saved game string*/
	String getSavedGame(){
		return sp.getString(mContext.getString(R.string.key_board_saved), null);
	}
	
	/** Save game progress or delete saved game
	 * @param save true to save the game, false to delete it
	 * @param gameState the string containing the game state, can be null
	 * when save is set to false*/
	void saveGame(boolean save, String gameState){
		if(save){
			spEdit.putBoolean(mContext.getString(R.string.key_is_game_saved),true);
		    spEdit.putString(mContext.getString(R.string.key_board_saved),gameState);
		}
		else{
			spEdit.remove(mContext.getString(R.string.key_is_game_saved));
		    spEdit.remove(mContext.getString(R.string.key_board_saved));
		}
	    spEdit.commit();
	}
	
	/** Increments by one the number of games finished and won
	 * @param win true if this game was won or false if it was lost
	 * @return toReturn int array containing number of games finished
	 * at index 0 and IF AND ONLY IF parameter win is true
	 * at index 1 the number of games won will be returned */
	int[] updateGameFinished(Boolean win){
		int[] toReturn = new int[2]; 
		String str = mContext.getString(R.string.key_times_finished); // store the key string
		toReturn[0] = sp.getInt(str, 0)+1;
		spEdit.putInt(str, toReturn[0]);
		
		if(win){//if won, update wins count
			str = mContext.getString(R.string.key_times_won);// store the key string
			toReturn[1] = sp.getInt(str, 0)+1;
			spEdit.putInt(str, toReturn[1]);
		}
		spEdit.commit();
		return toReturn;
	}
	
	
}
