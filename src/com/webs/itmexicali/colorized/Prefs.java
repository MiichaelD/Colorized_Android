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
		return new ProtectedInt(getEncryptedStringAsInt(id,mContext.getString(id),Default));
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
			if(gameState.startsWith(MCrypt.AES_PREF)){
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
			gameState = Const.byteArrayToHexString(MCrypt.getIns().encrypt(MCrypt.AES_PREF+gameState));
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
	 * @param id is the Resource id to access the key
	 * @param aux a string containing the key name to fetch from the SharedPreferences,
	 * if it's null, this method will get the string from the context
	 * @param Default default value if there is no value paired to given key*/
	private int getEncryptedStringAsInt(int id, String aux, int Default){
		if(aux == null){
			aux = mContext.getString(id);
		}
		int toReturn = Default;
		try{
			//try to get it as a encrypted string
			aux = sp.getString(aux, null);
			if(aux != null){			
				
				/*using different secret keys for different strings elevates the difficulty hacking the
				 * game, if every string were using the same key, copying one string having the value from 
				 * games_finished for example, and copying it to games_won string, could have changed the value
				 * and put both with the same value. Now doing that, will just corrupt the replaced value and
				 * reset it to 0 */
				switch(id){
				case R.string.key_times_won:
					MCrypt.getIns().setSecretKeyIndex(MCrypt.WON_GAM_IND);
					break;
				case R.string.key_times_finished:
					MCrypt.getIns().setSecretKeyIndex(MCrypt.FIN_GAM_IND);
					break;
				
				}
				//decrypt the string and build a new one from the gotten bytes
				aux = new String (MCrypt.getIns().decrypt(Const.HexStringToByte(aux)));
				
				//restore to default secret key
				MCrypt.getIns().resetSecretKeyIndex();
				
				if(aux.startsWith(MCrypt.AES_PREF)){
					aux = aux.substring(4);
					toReturn = Integer.parseInt(aux);
				}
				//TODO if it doesn't start with Algorithm prefix, it is not
				//a VALID VALUE and should be treated as corrupted
			}
			
		}catch(ClassCastException cce){
			//if it is storead as int, get it as int
			toReturn = sp.getInt(aux, Default);
		}catch(NullPointerException npe){
			//given if used different SecretKey for saving and for reading
		}catch(NumberFormatException nfe){
			//thrown if we used the correct secretkey, but the value stored is not int
			nfe.printStackTrace();
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
		
		String key = mContext.getString(R.string.key_times_finished); // get the key string
		toReturn[0] = getEncryptedStringAsInt(R.string.key_times_finished,key,0);
		
		toReturn[0]++;//increment the finished games counter
		spEdit.remove(key);//TODO remove this on next version (7+) or when everyone is updated over 6
		MCrypt.getIns().setSecretKeyIndex(MCrypt.FIN_GAM_IND);
		String encrypted = Const.byteArrayToHexString(MCrypt.getIns().encrypt(MCrypt.AES_PREF+toReturn[0]));
		spEdit.putString(key, encrypted);
		gFinished.set(toReturn[0]);
		
		if(win){//if won, update wins count
			
			key = mContext.getString(R.string.key_times_won);// store the key string
			toReturn[1] =  getEncryptedStringAsInt(R.string.key_times_won,key,0);
			
			toReturn[1]++;//increment the won games counter
			spEdit.remove(key);//TODO remove this on next version (7+) or when everyone is updated over 6s
			MCrypt.getIns().setSecretKeyIndex(MCrypt.WON_GAM_IND);
			encrypted = Const.byteArrayToHexString(MCrypt.getIns().encrypt(MCrypt.AES_PREF+toReturn[1]));
			spEdit.putString(key, encrypted);
			gWon.set(toReturn[1]);
		}
		spEdit.commit();
		MCrypt.getIns().resetSecretKeyIndex();
		return toReturn;
	}
	
	public int getGamesFinished(){
		return gFinished.get();
	}
	
	public int getGamesWon(){
		return gWon.get();
	}
}
