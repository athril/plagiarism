package plag;

import java.io.File;

public class Plagiarism {

	public static void main(String[] args) throws Exception {
		Detector detector = new Detector();

		if (args.length != 4 || !args[0].equals("-s") || !args[2].equals("-d")) {
			System.out.println("SYNTAX: EntryPoint -s <suspect_file> -d <base_dir>");
			System.exit(1);
		}

		File suspect = new File(args[1]);
		File dir = new File(args[3]);
		if (!dir.isDirectory() || !suspect.isFile()) {
			System.out.println("SYNTAX: EntryPoint -s <suspect_file> -d <directory>");
			System.out.println("ERROR: Unspecified file");
			System.exit(1);
		}

		System.out.println("Analyzing suspected file " + suspect.toString() + " with files in base directory "
				+ dir.toString() + ".");

		String[] files = dir.list();
		for (int i = 0; i < files.length; i++) {
			detector.addFile(dir.getAbsolutePath() + "/" + files[i]);
		}
		Match[] matches = detector.analyze(suspect.toString(), 4);

		System.out.println("Detected.....");
		if (matches.length == 0)
			System.out.println("none.");
		for (int i = 0; i < matches.length; i++)
			System.out.println(matches[i].toString());
	}
}
