package eu.excitementproject.eop.distsim.parsing;

/**
 * This class holds the specific separators for the UkWac index
 * @author hila
 * @date 05/04/2012
 */
public class UkwacIndexSeperators {
	
	private final String POS_SEP;
	private final String TOKEN_SEP;
	private final String VERB_SEP;
	private int len;
	private int lemma_index;
	private final String documentStart = "<text";
	private final String documentEnd = "</text";
	private final String sentenceStart = "<s>";
	private final String sentenceEnd = "</s>";

	
	public UkwacIndexSeperators(boolean isCorpusIndex){
		lemma_index = 1;
		if (isCorpusIndex){
			POS_SEP = "#_@";//hack for index
			TOKEN_SEP = "%%%";//hack for index
			VERB_SEP = "__";//hack for index
			len= 5;
		
		}
		else{
			POS_SEP = "\t";
			TOKEN_SEP = "\n";
			VERB_SEP = " ";
			len = 6;
		}
	}
	
	public String getDocumentStart() {
		return documentStart;
	}



	public String getDocumentEnd() {
		return documentEnd;
	}



	public String getSentenceStart() {
		return sentenceStart;
	}



	public String getSentenceEnd() {
		return sentenceEnd;
	}
	
	public String getPOSSeperator(){
		return POS_SEP;
	}
	
	public String getTokenSeperator(){
		return TOKEN_SEP;
	}
	
	public String getVerbSeperator(){
		return VERB_SEP;
	}
	
	public int getLength(){
		return len;
	}
	
	public int getLemmaIndex(){
		return lemma_index;
	}
}
