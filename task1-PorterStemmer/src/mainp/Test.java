package mainp;

import java.io.IOException;

public class Test {
	
	public static void main(String[] args) throws IOException {
		PorterStemmer stemmer = new PorterStemmer();
		System.out.println(stemmer.stem("lovely\n\n\n"));
		
		String[] inputFile = new String[1];
		inputFile[0] = "input/NovelTexts.txt";
		PorterStemmer.porterMain(inputFile);
	}
}
