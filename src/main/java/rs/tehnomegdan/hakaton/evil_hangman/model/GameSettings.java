package rs.tehnomegdan.hakaton.evil_hangman.model;

import android.content.Context;
import android.content.SharedPreferences;

public class GameSettings {

	private final String PREFS_NAME = "EvilHangmanPrefs";
	private final String WORD_LENGTH = "wordLength";
	private final String MAX_ATTEMPTS = "maxAttempts";

	private SharedPreferences settings;

	private int defWordLength = 5;
	private int defMaxAttempts = 30;

	public GameSettings(Context context) {
		settings = context.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);
	}

	public int findWordLength() {
		return settings.getInt(WORD_LENGTH, defWordLength);
	}

	public int findMaxAttempts() {
		return settings.getInt(MAX_ATTEMPTS, defMaxAttempts);
	}

	public void updateWordLength(int newVal) {
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(WORD_LENGTH, newVal);
		editor.commit();
	}

	public void updateMaxAttempts(int newVal) {
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(MAX_ATTEMPTS, newVal);
		editor.commit();
	}

	public int getDefWordLength() {
		return defWordLength;
	}

	public int getDefMaxAttempts() {
		return defMaxAttempts;
	}

}
