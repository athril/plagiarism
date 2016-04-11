package plag;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Detector {
	private Set<File> originals;

	public Detector() {
		originals = new HashSet<File>();
	}

	public void addFile(String filename) throws IOException {
		File file = new File(filename);
		if (!file.canRead())
			throw new IOException("unreadable file");
		originals.add(file);
	}

	public void addDirectory(String filename) {
		//TODO
	}

	private static Word[] subarray(Word[] a, int b, int e) {
		Word[] r = new Word[e - b + 1];
		for (int i = b; i <= e; i++)
			r[i - b] = a[i];
		return r;
	}

	public Match[] analyze(String filename, int threshold) throws IOException {
		ArrayList<Match> results = new ArrayList<Match>();
		File suspect = new File(filename);
		if (!suspect.canRead())
			throw new IOException("unreadable file");
		Tree tree = new Tree(Word.convert(new FileReader(suspect)));
		Iterator<File> iter = originals.iterator();

		//Debug:
		//tree.print();

		while (iter.hasNext()) {
			File original = (File) iter.next();
			Word[] text = Word.convert(new FileReader(original));
			Tree.Search search = tree.newSearch();
			int match = 0, oldmatch = 0;
			for (int i = 0; i < text.length; i++) {
				oldmatch = match;
				match = search.analyze(text[i]);
				if (match < oldmatch && oldmatch >= threshold)
					results.add(new Match(original, subarray(text, i - oldmatch, i - 1)));
			}
			if (match >= threshold)
				results.add(new Match(original, subarray(text, text.length - match, text.length - 1)));
		}
		return (Match[]) results.toArray(new Match[] {});
	}
}
