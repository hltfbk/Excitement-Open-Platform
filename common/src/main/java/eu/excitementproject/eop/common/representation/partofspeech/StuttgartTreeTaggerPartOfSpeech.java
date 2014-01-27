package eu.excitementproject.eop.common.representation.partofspeech;

import java.util.HashMap;


public class StuttgartTreeTaggerPartOfSpeech extends PartOfSpeech{
	/**
	 * A Stuttgart University TreeTagger pos-tag (as using in UKWac corpus)
	 * The list is taken from: {@linkplain link http://www.ims.uni-stuttgart.de/projekte/corplex/TreeTagger/DecisionTreeTagger.html}
	 * <p>
	 * The tag's string must conform with the list. 
	 * 
	 * @author Eden Shalom Erez 
	 * @since 29 May 2012
	 * 
	 * adopted from PennPartOfSpeech Class
	 */
	
	private static final long serialVersionUID = 2116198022104636358L;
	
	
	private static final HashMap<String, CanonicalPosTag> TreeTaggerTag_TO_CANONICAL_MAP = new HashMap<String, CanonicalPosTag>();
	private static final HashMap<CanonicalPosTag, String[]> CANONICAL_TO_StringArrayOfPOS_MAP = new HashMap<CanonicalPosTag, String[]>();
	
	static
	{
		String []VERB = {"VV","VVD","VVG","VVN","VVP","VVZ"};
		String []NOUN  = {"NN","NNS","NNP","NNPS"};
		String []ADJECTIVE = {"JJ","JJR","JJS"};
		String []DETERMINER = {"DT","WDT","PDT"};
		String []PRONOUN = {"PRP","PRP$","WP","WP$"};
		String []ADVERB = {"RB","RBR","RBS","WRB"};
		String []PREPOSITION = {"CC","IN","RP","TO"};
		String []PUNCTUATION  = {"PUNC","RCB","LCB","LRB","RRB","LS"};
		String []OTHER  = {"SYM","SYM1","CD","EX","FW","MD","POS","UH"};
		
		CANONICAL_TO_StringArrayOfPOS_MAP.put(CanonicalPosTag.N, NOUN);
		CANONICAL_TO_StringArrayOfPOS_MAP.put(CanonicalPosTag.V, VERB);
		CANONICAL_TO_StringArrayOfPOS_MAP.put(CanonicalPosTag.ADJ , ADJECTIVE);
		CANONICAL_TO_StringArrayOfPOS_MAP.put(CanonicalPosTag.ART, DETERMINER);
		CANONICAL_TO_StringArrayOfPOS_MAP.put(CanonicalPosTag.PR, PRONOUN);
		CANONICAL_TO_StringArrayOfPOS_MAP.put(CanonicalPosTag.ADV, ADVERB);
		CANONICAL_TO_StringArrayOfPOS_MAP.put(CanonicalPosTag.PP, PREPOSITION);
		CANONICAL_TO_StringArrayOfPOS_MAP.put(CanonicalPosTag.PUNC, PUNCTUATION);
		CANONICAL_TO_StringArrayOfPOS_MAP.put(CanonicalPosTag.OTHER, OTHER);
		
		for (CanonicalPosTag strKey : CANONICAL_TO_StringArrayOfPOS_MAP.keySet()) {
			String []strArray = CANONICAL_TO_StringArrayOfPOS_MAP.get(strKey);
			for (String string : strArray) {
				TreeTaggerTag_TO_CANONICAL_MAP.put(string, strKey);
			}
		}
		
	}
	
	////////////////// PUBLIC CONSTRUCTORS AND METHODS /////////////////////////
	
	public StuttgartTreeTaggerPartOfSpeech(String posTagString) throws UnsupportedPosTagStringException
	{
		super(posTagString);
		setCanonicalPosTag();
	}
	

	public CanonicalPosTag GetCanonicalPosString(String strPOS)
	{
		if(TreeTaggerTag_TO_CANONICAL_MAP.containsKey(strPOS))
			return TreeTaggerTag_TO_CANONICAL_MAP.get(strPOS);
	
		return CanonicalPosTag.OTHER;
	}
	
	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech#setCanonicalPosTag()
	 */
	@Override
	protected void setCanonicalPosTag()
	{
		this.canonicalPosTag = CanonicalPosTag.OTHER;
		try{
			this.canonicalPosTag = GetCanonicalPosString(posTagString);
			}
		catch(
				RuntimeException e){this.canonicalPosTag = CanonicalPosTag.OTHER;
				}
	}

	
	///////////////////// PROTECTED ////////////////////////////
	
	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech#validatePosTagString(java.lang.String)
	 */
	@Override
	protected void validatePosTagString(String posTagString) throws UnsupportedPosTagStringException
	{
		if (this.posTagString!=null)
		{
			if (this.posTagString.length()>0)
			{
				if (!TreeTaggerTag_TO_CANONICAL_MAP.containsKey(GetCanonicalPosString(posTagString)))
					throw new UnsupportedPosTagStringException("The pos tag \""+posTagString+"\" is not in the set of canonical part of speeches");
			}
		}
	}

	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech#createNewPartOfSpeech(java.lang.String)
	 */
	@Override
	public PartOfSpeech createNewPartOfSpeech(String posTagString)
			throws UnsupportedPosTagStringException {
		return new StuttgartTreeTaggerPartOfSpeech(posTagString);
	}

}
