package mt;

public class TestWordNet {

	public static void main(String args[]) {
		SimilaritySynonymWn mtesyt  = new SimilaritySynonymWn();
		String[] featureNames = {"LEMMA" };
		Word proposedWord=new Word("1");
		proposedWord.setFeature("LEMMA", "cat");
		Word referenceWord = new Word("2");
		referenceWord.setFeature("LEMMA", "guy");
		System.err.println(mtesyt.similarity(featureNames, proposedWord, referenceWord));
	}
}
