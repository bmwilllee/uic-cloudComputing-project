package mainp;

public class Test {
	
	public static void main(String[] args) {
		PorterStemmer stemmer = new PorterStemmer();
		System.out.println(stemmer.stem("lovely"));
	}
}
