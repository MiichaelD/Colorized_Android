package com.webs.itmexicali.colorized;

import com.webs.itmexicali.colorized.security.MCrypt;
import com.webs.itmexicali.colorized.util.Const;

import ProtectedInt.ProtectedInt;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class Prefs {

	//Application Context
	Context mContext;
	
	//SharedPreferences to read and edit
	SharedPreferences sp;
	SharedPreferences.Editor spEdit;
	
	//singleton instance
	private static Prefs instance;
	
	// create a new instance of Preferences
	public static Prefs initPreferences(Context context){
		instance = new Prefs(context);
		return instance;
	}
	
	//get preferences singleton
	public static Prefs getIns(){
		return instance;
	}
	
	boolean isTuto, playSFX, playMusic;
	ProtectedInt difficulty, gFinished, gWon, gMode;
	
	private Prefs(Context context){
		mContext = 	context;
		sp = 		context.getSharedPreferences(Const.TAG, 0);
		spEdit = 	sp.edit();
		
		isTuto = 	getBool(R.string.key_tutorial_played, false);
		playMusic = getBool(R.string.key_music,true);
		playSFX =	getBool(R.string.key_sfx,true);
		
		
		gFinished = getEncryptedInt(R.string.key_times_finished, 0);
		gWon =		getEncryptedInt(R.string.key_times_won, 0);
		gMode =		getInt(R.string.key_game_mode, Const.STEP);
		difficulty= getInt(R.string.key_difficulty,0);
	}
	
	/** Simpler method to get saved boolean
	 * @param id resource id containing the key string
	 * @param Default default value in case there is nothing stored with given key*/
	private boolean getBool(int id, boolean Default){
		return sp.getBoolean(mContext.getString(id), Default);
	}
	
	/** Simpler method to get saved int
	 * @param id resource id containing the key string
	 * @param Default default value in case there is nothing stored with given key*/
	private ProtectedInt getInt(int id, int Default){
		return new ProtectedInt(sp.getInt(mContext.getString(id),Default));
	}
	
	/** Simpler method to get saved Encrypted ints as ProtectedIntegers
	 * @param id resource id containing the key string
	 * @param Default default value in case there is nothing stored with given key*/
	private ProtectedInt getEncryptedInt(int id, int Default){
		return new ProtectedInt(getEncryptedStringAsInt(mContext.getString(id),Default));
	}
	
	public int getGameMode(){
		return gMode.get();
	}
	
	
	public void setGameMode(int gm){
		if(gMode.get() != gm){
			gMode.set(gm);
			spEdit.putInt(mContext.getString(R.string.key_game_mode), gm);
			spEdit.commit();
		}
	}
	
	public int getDifficulty(){
		return difficulty.get();
	}
	
	public void setDifficulty(int diff){
		if(difficulty.get() != diff){
			difficulty.set(diff);
			spEdit.putInt(mContext.getString(R.string.key_difficulty), diff);
			spEdit.commit();
		}
	}
	
	/** Check if the tutorial has been completed or cancelled by the user*/
	public boolean isTutorialCompleted(){
		return isTuto;
	}
	
	/** Save/remove the tutorial screen as completed
	 * @param completed whether the tutorial has been completed*/
	public void setTutorialCompleted(boolean completed){
		isTuto = completed;
		spEdit.putBoolean(mContext.getString(R.string.key_tutorial_played), completed);
		spEdit.commit();
	}
	
	/** Check if music will be played*/
	public boolean playMusic(){
		return playMusic;
	}
	
	/** Change the current value of playMusic to the opposite*/
	public void toggleMusic(){
		setPlayMusic(!playMusic);
	}
	
	/** set play music preference
	 * @param play whether the music will be played or not*/
	public void setPlayMusic(boolean play){
		Log.i(Prefs.class.getSimpleName(),"set music: "+play);
		playMusic = play;
		spEdit.putBoolean(mContext.getString(R.string.key_music), play);
		spEdit.commit();
		GameActivity.instance.playMusic(play);
	}
	
	/** Check if sound effects will be played*/
	public boolean playSFX(){
		return playSFX;
	}
	
	/** Change the current value of playSFX to the opposite*/
	public void toggleSFX(){
		setPlaySFX(!playSFX);
		GameActivity.instance.playSound(GameActivity.SoundType.TOUCH);
	}
	
	/** set play music preference
	 * @param play whether the music will be played or not*/
	public void setPlaySFX(boolean play){
		Log.i(Prefs.class.getSimpleName(),"set SFX: "+play);
		playSFX = play;
		spEdit.putBoolean(mContext.getString(R.string.key_sfx), play);
		spEdit.commit();
	}

	/** Check if there is a game saved*/
	public boolean isGameSaved(){
		return sp.getBoolean(mContext.getString(R.string.key_is_game_saved), false);
	}
	
	/** get the saved game string*/
	public String getSavedGame(){
		String gameState = sp.getString(mContext.getString(R.string.key_board_saved), null);
		
		//if the gameState contains spaces, it is not encrypted
		System.out.println("gameState to check: "+gameState);
		if(!gameState.contains(" ")){
			gameState = new String(MCrypt.getIns().decrypt(Const.HexStringToByte(gameState)));
			System.out.println("gameState decrypted: "+gameState);
			if(gameState.startsWith(Const.AES_ALG)){
				gameState = gameState.substring(4);
				System.out.println("gameState: "+gameState);
			}
		}
		
		return gameState;
	}
	
	/** Save game progress or delete saved game
	 * @param save true to save the game, false to delete it
	 * @param gameState the string containing the game state, can be null
	 * when save is set to false*/
	public void saveGame(boolean save, String gameState){
		if(save){
			//Encrypt the gameState
			gameState = Const.byteArrayToHexString(MCrypt.getIns().encrypt(Const.AES_ALG+gameState));
			spEdit.putBoolean(mContext.getString(R.string.key_is_game_saved),true);
		    spEdit.putString(mContext.getString(R.string.key_board_saved),gameState);
		}
		else{
			spEdit.remove(mContext.getString(R.string.key_is_game_saved));
		    spEdit.remove(mContext.getString(R.string.key_board_saved));
		}
	    spEdit.commit();
	}
	
	/** Get the encrypted String as Int, if the value is stored as int, return it,
	 * if it is saved as String, retrieve it, decrypt it and return it
	 * @param aux a string containing the key name to fetch from the SharedPreferences
	 * @param Default default value if there is no value paired to given key*/
	private int getEncryptedStringAsInt(String aux, int Default){
		int toReturn = Default;
		try{
			//try to get it as a encrypted string
			aux = sp.getString(aux, null);
			if(aux != null){			
				//decrypt the string and build a new one from the gotten bytes
				aux = new String (MCrypt.getIns().decrypt(aux));
				
				if(aux.startsWith(Const.AES_ALG)){
					aux = aux.substring(4);
					toReturn = Integer.parseInt(aux);
				}
			}
			
		}catch(ClassCastException cce){
			//if it is storead as int, get it as int
			toReturn = sp.getInt(aux, Default);
		}catch(NullPointerException npe){
			
		}
		return toReturn;
	}
	
	/** Increments by one the number of games finished and won
	 * @param win true if this game was won or false if it was lost
	 * @return toReturn int array containing number of games finished
	 * at index 0 and IF AND ONLY IF parameter win is true
	 * at index 1 the number of games won will be returned */
	public int[] updateGameFinished(int boardSize, int game_mode, Boolean win){
		int[] toReturn = new int[2]; 
		
		String key = mContext.getString(R.string.key_times_finished); // store the key string
		toReturn[0] = getEncryptedStringAsInt(key,0);
		
		toReturn[0]++;//increment the finished games counter
		spEdit.remove(key);//TODO remove this on next version (7+) or when everyone is updated over 6
		spEdit.putString(key, Const.byteArrayToHexString(MCrypt.getIns().encrypt(Integer.toString(toReturn[0]))));
		gFinished.set(toReturn[0]);
		
		if(win){//if won, update wins count
			
			key = mContext.getString(R.string.key_times_won);// store the key string
			toReturn[1] =  getEncryptedStringAsInt(key,0);
			
			toReturn[1]++;//increment the won games counter
			spEdit.remove(key);//TODO remove this on next version (7+) or when everyone is updated over 6s
			spEdit.putString(key, Const.byteArrayToHexString(MCrypt.getIns().encrypt(Integer.toString(toReturn[1]))));
			gWon.set(toReturn[1]);
		}
		spEdit.commit();
		return toReturn;
	}
	
	public int getGamesFinished(){
		return gFinished.get();
	}
	
	public int getGamesWon(){
		return gWon.get();
	}
}
