# Plagiarism
Java implementation of Ukkonen's suffix trees for plagiarsim detection.
The information about may be found at www.allisons.org/ll/AlgDS/Tree/Suffix/

##Running
Running the project requires setting the following flags:
-s (source suspected of being a plagiarism) -d (a directory with originals that are used for checking common sequences with suspect)

###Running with eclipse - set the following program arguments:
-s psalm89.txt -d Database

###Without Eclipse
cd src
javac plag/*
java plag/Plagiarism -s ../psalm89.txt -d ../Database

