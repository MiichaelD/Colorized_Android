package com.webs.itmexicali.colorized.util;

import java.util.HashMap;

import com.webs.itmexicali.colorized.GameActivity;
import com.webs.itmexicali.colorized.R;
import com.webs.itmexicali.colorized.security.MCrypt;

import ProtectedInt.ProtectedInt;
import android.content.Context;
import android.content.SharedPreferences;

public class ProgNPrefs {

	//Application Context
	Context mContext;
	
	//SharedPreferences to read and edit
	SharedPreferences sp;
	SharedPreferences.Editor spEdit;
	
	//singleton instance
	private static ProgNPrefs instance;
	
	// create a new instance of Preferences
	public static ProgNPrefs init(Context context){
		instance = new ProgNPrefs(context);
		return instance;
	}
	
	//get preferences singleton
	public static ProgNPrefs getIns(){
		if (instance == null)
			throw new IllegalStateException("You must initialize ProgNPrefs before using it");
		return instance;
	}
		
	//get preferences singleton
	public static ProgNPrefs getIns(Context context){
		if(instance == null)
			init(context);
		return instance;
	}
	
	boolean isTuto, playSFX, playMusic, showNotifs;
	ProtectedInt difficulty, gMode, gFinished[], gWon[], pOpenedTimes, pCurrentStreak, pBestStreak;
	
	/** IDS used for save number of times player finish a game in different board sizes*/
	private int[] finished_ids = {R.string.key_times_finished_0,R.string.key_times_finished_1,
			  R.string.key_times_finished_2, R.string.key_times_finished};
	
	/** IDS used for save number of times player wins a game in different board sizes*/
	private int[] won_ids = {R.string.key_times_won_0,R.string.key_times_won_1,
			  		 R.string.key_times_won_2, R.string.key_times_won};
	
	private ProgNPrefs(Context context){
		mContext = 	context;
		sp = 		context.getSharedPreferences(Const.TAG, 0);
		spEdit = 	sp.edit();
		
		isTuto = 	getBool(R.string.key_tutorial_played, false);
		playMusic = getBool(R.string.key_music,true);
		showNotifs =getBool(R.string.key_notifications, true);
		playSFX =	getBool(R.string.key_sfx,true);
		HashMap<String, Object> props = new HashMap<String, Object>();
		props.put("Notifications", showNotifs);
		props.put("Music", playMusic);
		props.put("Sfx", playSFX);
		Tracking.shared().registerSuperProperties(props);
		
		gMode =		getInt(R.string.key_game_mode, Const.STEP);
		difficulty= getInt(R.string.key_difficulty,0);
		
		
		pOpenedTimes	= getEncryptedInt(R.string.key_times_app_opened, -1, 0);
		pCurrentStreak	= getEncryptedInt(R.string.key_current_win_streak, -1, 0);
		pBestStreak		= getEncryptedInt(R.string.key_best_win_streak, -1, 0);
		
		
		gFinished = new ProtectedInt[Const.TOTAL_SIZES+1];
		gWon =		new ProtectedInt[Const.TOTAL_SIZES+1];
		
		gFinished[Const.TOTAL_SIZES] =	new ProtectedInt();
		gWon[Const.TOTAL_SIZES] =		new ProtectedInt();
		
		int gFinTotal = gFinished[Const.TOTAL_SIZES].get();
		int gWinTotal = gWon[Const.TOTAL_SIZES].get();
		
		for(int i =0;i<Const.TOTAL_SIZES;i++){
			gFinished[i] =	getEncryptedInt(finished_ids[i], i, 0);
			gWon[i] =		getEncryptedInt(won_ids[i], i, 0);
			
			//last index of array is just a sum from previous array's values
			gFinished[Const.TOTAL_SIZES].add(gFinished[i].get());
			gWon[Const.TOTAL_SIZES].add(gWon[i].get());
		}
		
		
		gFinTotal = gFinished[Const.TOTAL_SIZES].get();
		gWinTotal = gWon[Const.TOTAL_SIZES].get();
		
		//check if we have a stored value greater than the derived one:
		ProtectedInt temp = getEncryptedInt(finished_ids[Const.TOTAL_SIZES], -1, 0);
		if(gFinTotal < temp.get())
			gFinished[Const.TOTAL_SIZES] = temp;
		
		temp = getEncryptedInt(won_ids[Const.TOTAL_SIZES], -1, 0);
		if(gWinTotal < temp.get())
			gWon[Const.TOTAL_SIZES] = temp;
		
		
		deleteUnnecessaryData();
	}
	
	public SharedPreferences getSharedPrefs(){
		return sp;
	}
	
	public SharedPreferences.Editor getSharedPrefsEditor(){
		return spEdit;
	}
	
	/** called once every game launch to delete unnecessary data*/
	private void deleteUnnecessaryData(){
	    spEdit.remove("key_board_saved");
	    spEdit.remove("key_times_finished");
	    spEdit.remove("key_times_won");
		spEdit.commit();
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
	 * @param boardSize index of boardsize if this encrypted int is boardsize dependent
	 * if it is not dependent of boardsize, it should be negative.
	 * @param Default default value in case there is nothing stored with given key*/
	private ProtectedInt getEncryptedInt(int id, int boardSize, int Default){
		return new ProtectedInt(getEncryptedStringAsInt(id,mContext.getString(id),boardSize,Default));
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
		Const.i(ProgNPrefs.class.getSimpleName(),"set music: "+play);
		playMusic = play;
		spEdit.putBoolean(mContext.getString(R.string.key_music), play);
		spEdit.commit();
		GameActivity.instance.playMusic(play);
		
		HashMap<String, Object> props = new HashMap<String, Object>();
		props.put("Music", play);
		Tracking.shared().registerSuperProperties(props);
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
		Const.i(ProgNPrefs.class.getSimpleName(),"set SFX: "+play);
		playSFX = play;
		spEdit.putBoolean(mContext.getString(R.string.key_sfx), play);
		spEdit.commit();
		
		HashMap<String, Object> props = new HashMap<String, Object>();
		props.put("Sfx", play);
		Tracking.shared().registerSuperProperties(props);
	}

	/** Check if notifications are enabled*/
	public boolean showNotifications(){
		return showNotifs;
	}
	
	/** Change the current value of playSFX to the opposite*/
	public void toggleNotifications(){
		setShowNotifications(!showNotifs);
		GameActivity.instance.playSound(GameActivity.SoundType.TOUCH);
	}
	
	/** set notifications preference to enable/disable
	 * @param play whether the music will be played or not*/
	public void setShowNotifications(boolean show){
		Const.i(ProgNPrefs.class.getSimpleName(),"Notifications enabled: "+show);
		showNotifs = show;
		spEdit.putBoolean(mContext.getString(R.string.key_notifications), showNotifs);
		spEdit.commit();
		
		HashMap<String, Object> props = new HashMap<String, Object>();
		props.put("Notifications", show);
		Tracking.shared().registerSuperProperties(props);
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
		try{
			gameState = new String(MCrypt.getIns().decrypt(Const.HexStringToByte(gameState)));
			System.out.println("gameState decrypted: "+gameState);
			if(gameState.startsWith(MCrypt.AES_PREF)){
				gameState = gameState.substring(4);
				System.out.println("gameState: "+gameState);
			}
		}catch(NullPointerException npe){
			gameState = null;
			saveGame(false,null);
			npe.printStackTrace();
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
	 * @param boardSizeInd index of boardsize if this encrypted int is boardsize dependent
	 * if it is not dependent of boardsize, it should be negative.
	 * @param Default default value if there is no value paired to given key*/
	private int getEncryptedStringAsInt(int id, String aux, int boardSizeInd, int Default){
		if(aux == null){
			aux = mContext.getString(id);
		}
		int toReturn = Default;
		try{
			//try to get it as a encrypted string
			aux = sp.getString(aux, null);
			
			if(aux == null) // if the value doesn't exist, return default
				return toReturn;
			
			/*using different secret keys for different strings elevates the difficulty hacking the
			 * game, if every string were using the same key, copying one string having the value from 
			 * games_finished for example, and copying it to games_won string, could have changed the value
			 * and put both with the same value. Now doing that, will just corrupt the replaced value and
			 * reset it to 0 */
			switch(id){
			case R.string.key_current_win_streak:
				MCrypt.getIns().resetSecretKeyIndex();
				break;

			case R.string.key_times_finished:
			case R.string.key_times_finished_0:
			case R.string.key_times_finished_1:
			case R.string.key_times_finished_2:
				MCrypt.getIns().setSecretKeyIndex(MCrypt.FIN_GAM_IND);
				break;
								
			case R.string.key_times_won:
			case R.string.key_times_won_0:
			case R.string.key_times_won_1:
			case R.string.key_times_won_2:
				MCrypt.getIns().setSecretKeyIndex(MCrypt.WON_GAM_IND);
				break;
				
			case R.string.key_times_app_opened:
				MCrypt.getIns().setSecretKeyIndex(MCrypt.APP_OPEN_IND);
				break;

			case R.string.key_best_win_streak:
				MCrypt.getIns().setSecretKeyIndex(MCrypt.BEST_STREAK_IND);
				break;
			default:
				return -1; //No valid encryption for given ID
			}
			
			//decrypt the string and build a new one from the gotten bytes
			aux = new String (MCrypt.getIns().decrypt(Const.HexStringToByte(aux)));
			
			if(aux.startsWith(MCrypt.AES_PREF)){
				aux = aux.substring(MCrypt.AES_PREF.length());
				
				//if this value is not board size dependent parse the number
				if(boardSizeInd < 0){
					toReturn = Integer.parseInt(aux);
				}
				//validate that the board size index is identical to the number of the
				//first character of the string
				else if(boardSizeInd == Integer.parseInt(aux.substring(0,1))){
					aux = aux.substring(2);//boardIndex + space = 2
					toReturn = Integer.parseInt(aux);
				}
				//if it doesn't match it means this value was hardcoded externally
				//probably copied from another boardsize-indexed value.
				// so I wont parse and the value will be 0, and corrected in the next
				//google games service synchronization
			}
			//if it doesn't start with Algorithm prefix, it is not
			//a VALID VALUE and should be treated as corrupted and I return 0
		
			
		}catch(ClassCastException cce){
			//if it is storead as int, <u>get it as int</u> NOT ANYMORE
			// toReturn = sp.getInt(aux, Default);
		}catch(NullPointerException npe){
			//given if used different SecretKey for saving and for reading
			npe.printStackTrace();
		}catch(NumberFormatException nfe){
			//thrown if we used the correct secretkey, but the value stored is not int
			nfe.printStackTrace();
		}finally{
			//restore to default secret key
			MCrypt.getIns().resetSecretKeyIndex();
		}

		return toReturn;
	}
	
	/** Increments by one the number of games finished and won
	 * @param win true if this game was won or false if it was lost*/
	public void updateGameFinished(int boardSize, int game_mode, Boolean win){		 
		//increment the finished games value for given board size & Save
		gFinished[Const.TOTAL_SIZES].increment();
		saveFinishedGame(Const.TOTAL_SIZES);
		
		//Only if the game played was of mode Step Challenge, save data to its board size
		if(game_mode == Const.STEP){
			updateWinStreak(win);
			
			//increment the finished games value for given board size index
			gFinished[boardSize].increment();
			saveFinishedGame(boardSize);			
			
			
			if(win){//if won, update wins count
				//increment the finished games value for all board sizes
				gWon[Const.TOTAL_SIZES].increment();
				saveWonGame(Const.TOTAL_SIZES);
				
				//increment the won games value for given board size index
				gWon[boardSize].increment();
				saveWonGame(boardSize);
			}
		}
	}
	
	public int getAppOpenedCount(){
		return pOpenedTimes.get();
	}
	
	public int getBestStreak(){
		return pBestStreak.get();
	}
	
	public int getGamesFinished(int boardSize){
		return gFinished[boardSize].get();
	}
	
	public int getGamesWon(int boardSize){
		return gWon[boardSize].get();
	}
	
	/** Set a new value to the given board size finished games counter.
	 * @param boardSize the boardSize index of the finished game counter
	 * @param nVal the new value to set to the counter
	 * @param saveOnFile true to save the updated value on File, false doesn't update */
	public void setGamesFinishedValue(int boardSize, int nVal, boolean saveOnFile){
		gFinished[boardSize].set(nVal);
		if(saveOnFile)
			saveFinishedGame(boardSize);
	}
	
	/** Set a new value to the given board size won games counter.
	 * @param boardSize the boardSize index of the won game counter
	 * @param nVal the new value to set to the counter
	 * @param saveOnFile true to save the updated value on File, false doesn't update */
	public void setGamesWonValue(int boardSize, int nVal, boolean saveOnFile){
		gWon[boardSize].set(nVal);
		if(saveOnFile)
			saveWonGame(boardSize);
	}
	
	/** Update the app opened counter*/
	public void incrementAppOpened(){
		pOpenedTimes.increment();
		genericEncryptedSave(MCrypt.APP_OPEN_IND,
				R.string.key_times_app_opened,
				MCrypt.AES_PREF+pOpenedTimes.get());
	}
	
	/** Update the app opened counter
	 * @param nVal new value
	 * @param saveToFile if you want to save to file*/
	public void setAppOpened(int nVal, boolean saveToFile){
		pOpenedTimes.set(nVal);
		if(saveToFile)
			genericEncryptedSave(MCrypt.APP_OPEN_IND,
				R.string.key_times_app_opened,
				MCrypt.AES_PREF+pOpenedTimes.get());
	}
	
	/** Update the win streak counter
	 * @param continueGood if true, increment the streak, else reset it*/
	public void updateWinStreak(boolean continueGood){
		if(continueGood){
			pCurrentStreak.increment();
			if(pCurrentStreak.get()>pBestStreak.get()){
				pBestStreak.set(pCurrentStreak);
				genericEncryptedSave(MCrypt.BEST_STREAK_IND,
						R.string.key_best_win_streak,
						MCrypt.AES_PREF+pBestStreak.get());
			}
		}
		else{
			// if lost reset streak
			pCurrentStreak.set(0);
		}
		genericEncryptedSave(MCrypt.MAIN_IND,
				R.string.key_current_win_streak,
				MCrypt.AES_PREF+pCurrentStreak.get());
	}
	
	/** Save the updated counter of FINISHED GAMES of the given boardsize
	 * as a encrypted value in file
	 * @param boardSize the size of the board to update {SMALL,MEDIUM,LARGE}*/
	public void saveFinishedGame(int boardSize){
		// Set key for Finished Games
		MCrypt.getIns().setSecretKeyIndex(MCrypt.FIN_GAM_IND);
		String key = mContext.getString(finished_ids[boardSize]);
		String encrypted = null;
		
		switch(boardSize){
		case Const.TOTAL_SIZES:
			//encrypt as AES_PREF+value
			encrypted = Const.byteArrayToHexString(MCrypt.getIns().encrypt(
					MCrypt.AES_PREF+gFinished[Const.TOTAL_SIZES].get()));
			break;
		default:
			//encrypt as AES_PREF+[boardSize]+<space>+value
			encrypted = Const.byteArrayToHexString(MCrypt.getIns().encrypt(
					MCrypt.AES_PREF+boardSize+" "+gFinished[boardSize].get()));
			break;
		}
		
		//store the value
		spEdit.putString(key, encrypted);
		spEdit.commit();
		MCrypt.getIns().resetSecretKeyIndex();
	}
	
	/** Save the updated counter of WON GAMES of the given boardsize
	 * as a encrypted value in file
	 * @param boardSize the size of the board to update {SMALL,MEDIUM,LARGE}*/
	public void saveWonGame(int boardSize){
		// Set key for Won Games
		MCrypt.getIns().setSecretKeyIndex(MCrypt.WON_GAM_IND);
		String key = mContext.getString(won_ids[boardSize]);
		String encrypted = null;
		
		switch(boardSize){
		case Const.TOTAL_SIZES:
			//encrypt as AES_PREF+value
			encrypted = Const.byteArrayToHexString(MCrypt.getIns().encrypt(
					MCrypt.AES_PREF+gWon[Const.TOTAL_SIZES].get()));
			break;
		default:
			//encrypt as AES_PREF+[boardSize]+<space>+value
			encrypted = Const.byteArrayToHexString(MCrypt.getIns().encrypt(
					MCrypt.AES_PREF+boardSize+" "+gWon[boardSize].get()));
			break;
		}
		
		//store the value
		spEdit.putString(key, encrypted);
		spEdit.commit();
		MCrypt.getIns().resetSecretKeyIndex();
	}
	
	/** Generic encrypt and store a string value 
	 * @param enc_key_index the index of the encryption to use
	 * @param val_key_id the ID of the string in resources containing the key
	 * for the shared preferences to write put the encrypted value
	 * @param toEncrypt the string to encrypt*/
	public void genericEncryptedSave(int enc_key_index, int val_key_id, String toEncrypt){
		// Set key for Won Games
		MCrypt.getIns().setSecretKeyIndex(enc_key_index);
		String key = mContext.getString(val_key_id);
		String encrypted = Const.byteArrayToHexString(MCrypt.getIns().encrypt(toEncrypt));
		
		//store the value
		spEdit.putString(key, encrypted);
		spEdit.commit();
		MCrypt.getIns().resetSecretKeyIndex();
	}
}
