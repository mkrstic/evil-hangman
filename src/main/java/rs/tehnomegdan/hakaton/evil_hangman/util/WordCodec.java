package rs.tehnomegdan.hakaton.evil_hangman.util;

import java.util.HashSet;

import android.util.Pair;

public class WordCodec {
	
	private String alphabet = "_АБВГДЂЕЖЗИЈКЛЉМНЊОПРСТЋУФХЦЧЏШ";
	private final int LETTER_SIZE = 5;
	private final long MASK = 0x1F; // binary 11111
	
	public long compressLetter(final char val) {
		return alphabet.indexOf(val);
	}

	public long compressLetter(final String val) {
		return alphabet.indexOf(val);
	}

	public char decompressLetter(final long val) {
		return alphabet.charAt((int)(val & MASK));
	}

	public long compressWord(final String word) {
		long compressed = 0;
		char[] arr = word.toCharArray();
		int j = 0;
		for (int i = arr.length-1; i >= 0; i--) {
			compressed |= (compressLetter(arr[i]) << LETTER_SIZE*j);
			j++;
		}
		return compressed;
	}
	public String decompressWord(final long wordCode, final int len) {
		StringBuilder decompressed = new StringBuilder("");
		for (int i = 0; i < len; i++) {
			long nextLetterCode = (wordCode & (MASK << LETTER_SIZE*i)) >> LETTER_SIZE*i;
			decompressed.append(decompressLetter(nextLetterCode));
		}
		return decompressed.reverse().toString();
	}
	

	public long findEqClass(long left, long right, int len) {
		long eqClass = 0;
		for (int i = 0; i < len; i++) {
			long leftLetter = (left & (MASK << LETTER_SIZE*i)) >> LETTER_SIZE*i;
			long rightLetter = (right & (MASK << LETTER_SIZE*i)) >> LETTER_SIZE*i;
			eqClass |= (leftLetter == rightLetter) ? (leftLetter << LETTER_SIZE*i) : 0;
		}
		return eqClass;
	}
	
	public long _findEqClass(long left, long right, int len) {
		HashSet<Pair<Integer, Long>> h1 = new HashSet<Pair<Integer, Long>>();
		HashSet<Pair<Integer, Long>> h2 = new HashSet<Pair<Integer, Long>>();
		for (int i = 0; i < len; i++) {
			long letter = (left & (MASK << LETTER_SIZE*i)) >> LETTER_SIZE*i;
			h1.add(new Pair<Integer, Long>(i, letter));
		}
		for (int i = 0; i < len; i++) {
			long letter = (right & (MASK << LETTER_SIZE*i)) >> LETTER_SIZE*i;
			h2.add(new Pair<Integer, Long>(i, letter));
		}
		h1.retainAll(h2);
		long intersection = createWordMask("_", len); 
		if (!h1.isEmpty()) {
			for (Pair<Integer, Long>  p: h1) {
				intersection |= p.second << LETTER_SIZE*p.first;
			}
		}
		return intersection;
	}
	public long createWordMask(final String letter, final int size) {
		long letterCode = compressLetter(letter);
		long wordMask = 0;
		for (int i = 0; i < size; i++) {
			wordMask |= (letterCode << LETTER_SIZE*i);
		}
		return wordMask;
	}
}
