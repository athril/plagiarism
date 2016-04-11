package plag;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Word {
	private static final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	private static Map<String, Word> words = new HashMap<String, Word>();

	public static Word find(String string) {
		string = string.toLowerCase();
		if (!words.containsKey(string)) {
			words.put(string, new Word(string));
		}
		return (Word) words.get(string);
	}

	public static Word[] convert(Reader reader) throws IOException {
		ArrayList<Word> sequence = new ArrayList<Word>();
		StringBuffer buffer = new StringBuffer();
		while (true) {
			int ch = reader.read();
			if (ch == -1)
				break;
			if (alphabet.indexOf(ch) != -1) {
				buffer.append((char) ch);
				continue;
			}
			if (buffer.length() == 0)
				continue;
			sequence.add(Word.find(buffer.toString()));
			buffer.delete(0, buffer.length());
		}
		if (buffer.length() > 0) {
			sequence.add(Word.find(buffer.toString()));
		}
		return (Word[]) sequence.toArray(new Word[] {});
	}

	private String data;
	private int code;

	private Word(String string) {
		data = string;
		code = string.hashCode();
	}

	public boolean equals(Object other) {
		return (this == other);
	}

	public int hashCode() {
		return code;
	}

	public String toString() {
		return data;
	}
}
