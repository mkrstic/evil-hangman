package rs.tehnomegdan.hakaton.evil_hangman.model;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import rs.tehnomegdan.hakaton.evil_hangman.util.GameOverException;
import rs.tehnomegdan.hakaton.evil_hangman.util.WordCodec;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

public class Game {
	private WordCodec codec;
	private int wordLen;
	private int remainingAttempts;
	private List<Long> words;
	private long curWordMask;
	private boolean gameOver;
	private Context context;

	public Game(Context context, int wordLen, int maxAttempts) {
		this.context = context;
		words = new ArrayList<Long>();
		this.wordLen = wordLen;
		this.remainingAttempts = maxAttempts;
		codec = new WordCodec();
		curWordMask = 0;
		try {
			encodeDatabase2();
		} catch (Exception ex) {
			Log.e("Game()", ex.getMessage());
		}

	}

	private void encodeDatabase2() throws Exception {
		AssetManager am = context.getAssets();
		 String filename = "dic" + wordLen + ".txt";
		//String filename = "dic10k.txt";
		BufferedReader in = new BufferedReader(new InputStreamReader(
				am.open(filename)));
		String nextWord;
		while ((nextWord = in.readLine()) != null) {
			if (nextWord.length() == wordLen) {
				words.add(codec.compressWord(nextWord));
			}
		}
		Log.i("encodeDatabase2()", "Ucitano " + words.size() + " reci");

	}

	private String findKeyWithMaxValues(HashMap<String, List<Long>> hashmap) {
		int max = 0;
		String key = "";
		for (Map.Entry<String, List<Long>> entry : hashmap.entrySet()) {
			int cur = entry.getValue().size();
			if (cur > max) {
				max = cur;
				key = entry.getKey();
			}
		}
		return key;
//		ValueComparator bvc =  new ValueComparator(hashmap);
//		TreeMap<String,List<Long>> sortedMap = new TreeMap<String,List<Long>>(bvc);
//		sortedMap.putAll(hashmap);
//		Object[] objects = sortedMap.entrySet().toArray();
//		if (objects.length > 2 && sortedMap.get(sortedMap.firstKey()).size() > 100) {
//			int max = objects.length > 5 ? 5 : objects.length;
//			int randInd = new Random().nextInt(max);
//			Map.Entry<String, List<Long>> entry = (Map.Entry<String, List<Long>>) objects[randInd];
//			return entry.getKey();
//		} else {
//			return sortedMap.firstKey();
//		}
	}

	public void play(String letter) throws GameOverException {
		if (remainingAttempts < 0 || gameOver) {
			throw new GameOverException();
		}
		HashMap<String, List<Long>> wordsmap = new HashMap<String, List<Long>>();
		long letterMask = codec.createWordMask(letter, wordLen);
		for (int i = words.size() - 1; i >= 0; i--) {
			long nextWord = Long.valueOf(words.get(i));
			String eqClassStr = codec.decompressWord(
					codec.findEqClass(nextWord, letterMask, wordLen), wordLen);
			List<Long> values = wordsmap.get(eqClassStr);
			if (values == null) {
				values = new ArrayList<Long>();
			}
			values.add(nextWord);
			wordsmap.put(eqClassStr, values);
			words.remove(i);
		}
		words.clear();
		String maxKeyMask = findKeyWithMaxValues(wordsmap);
		words.addAll(wordsmap.get(maxKeyMask));
		long newWordMask = curWordMask | codec.compressWord(maxKeyMask);
		if (newWordMask == curWordMask) {
			remainingAttempts--;
		}
		curWordMask = newWordMask;
		if (getCurrentWordMask().indexOf("_") == -1) {
			gameOver = true;
			Log.e("GAMEOVER", getCurrentWordMask());
		}
	}

	public boolean isGameOver() {
		return gameOver;
	}

	public void setGameIsOver(boolean gameIsOver) {
		this.gameOver = gameIsOver;
	}

	public String getCurrentWordMask() {
		return codec.decompressWord(curWordMask, wordLen);
	}

	public String revealWord() {
		int randIndex = new Random().nextInt(words.size());
		return codec.decompressWord(words.get(randIndex), wordLen);
	}

	public int getRemainingAttempts() {
		return remainingAttempts;
	}
	
	class ValueComparator implements Comparator<String> {

	    Map<String, List<Long>> base;
	    public ValueComparator(Map<String, List<Long>> base) {
	        this.base = base;
	    }
	    public int compare(String a, String b) {
	        if (base.get(a).size() <= base.get(b).size()) {
	            return -1;
	        } else {
	            return 1;
	        } 
	    }
	}

}
