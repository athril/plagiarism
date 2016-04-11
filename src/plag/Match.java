package plag;

import java.io.File;

public class Match {
	private Word[] text;
	private File file;

	public Match(File f, Word[] t) {
		file = f;
		text = t;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(file.getName());
		buf.append(":");
		for (int i = 0; i < text.length; i++) {
			buf.append(" ");
			buf.append(text[i].toString());
		}
		return buf.toString();
	}
}
