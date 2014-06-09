package eu.excitementproject.eop.core.utilities.dictionary.wordnet;
import java.util.List;
import java.util.Set;

public class EmptySynset implements Synset {

	private static final long serialVersionUID = -4381182321711714249L;

	@Override
	public Set<String> getWords() throws WordNetException {
		throw new EmptySynsetException("EmptySynset should only be used " +
				"as a palceholder, it is not a concrete Synset");
	}

	@Override
	public List<WordAndUsage> getWordsAndUsages() throws WordNetException {
		throw new EmptySynsetException("EmptySynset should only be used " +
				"as a palceholder, it is not a concrete Synset");
	}

	@Override
	public String getGloss() throws WordNetException {
		throw new EmptySynsetException("EmptySynset should only be used " +
		"as a palceholder, it is not a concrete Synset");
	}

	@Override
	public WordNetPartOfSpeech getPartOfSpeech() throws WordNetException {
		throw new EmptySynsetException("EmptySynset should only be used " +
		"as a palceholder, it is not a concrete Synset");
	}

	@Override
	public Set<Synset> getNeighbors(WordNetRelation relationType)
			throws WordNetException {
		throw new EmptySynsetException("EmptySynset should only be used " +
		"as a palceholder, it is not a concrete Synset");
	}

	@Override
	public Set<Synset> getRelatedSynsets(WordNetRelation relation,
			int chainingLength) throws WordNetException {
		throw new EmptySynsetException("EmptySynset should only be used " +
		"as a palceholder, it is not a concrete Synset");
	}

	@Override
	public Set<Synset> getAttributes() throws WordNetException {
		throw new EmptySynsetException("EmptySynset should only be used " +
		"as a palceholder, it is not a concrete Synset");
	}

	@Override
	public Set<Synset> getCauses() throws WordNetException {
		throw new EmptySynsetException("EmptySynset should only be used " +
		"as a palceholder, it is not a concrete Synset");
	}

	@Override
	public Set<Synset> getHypernyms() throws WordNetException {
		throw new EmptySynsetException("EmptySynset should only be used " +
		"as a palceholder, it is not a concrete Synset");
	}

	@Override
	public Set<Synset> getHyponyms() throws WordNetException {
		throw new EmptySynsetException("EmptySynset should only be used " +
		"as a palceholder, it is not a concrete Synset");
	}

	@Override
	public Set<Synset> getEntailments() throws WordNetException {
		throw new EmptySynsetException("EmptySynset should only be used " +
		"as a palceholder, it is not a concrete Synset");
	}

	@Override
	public Set<Synset> getHolonyms() throws WordNetException {
		throw new EmptySynsetException("EmptySynset should only be used " +
		"as a palceholder, it is not a concrete Synset");
	}

	@Override
	public Set<Synset> getMemberHolonyms() throws WordNetException {
		throw new EmptySynsetException("EmptySynset should only be used " +
		"as a palceholder, it is not a concrete Synset");
	}

	@Override
	public Set<Synset> getMemberMeronyms() throws WordNetException {
		throw new EmptySynsetException("EmptySynset should only be used " +
		"as a palceholder, it is not a concrete Synset");
	}

	@Override
	public Set<Synset> getMeronyms() throws WordNetException {
		throw new EmptySynsetException("EmptySynset should only be used " +
		"as a palceholder, it is not a concrete Synset");
	}

	@Override
	public Set<Synset> getPartHolonyms() throws WordNetException {
		throw new EmptySynsetException("EmptySynset should only be used " +
		"as a palceholder, it is not a concrete Synset");
	}

	@Override
	public Set<Synset> getPartMeronyms() throws WordNetException {
		throw new EmptySynsetException("EmptySynset should only be used " +
		"as a palceholder, it is not a concrete Synset");
	}

	@Override
	public Set<Synset> getSubstanceHolonyms() throws WordNetException {
		throw new EmptySynsetException("EmptySynset should only be used " +
		"as a palceholder, it is not a concrete Synset");
	}

	@Override
	public Set<Synset> getSubstanceMeronyms() throws WordNetException {
		throw new EmptySynsetException("EmptySynset should only be used " +
		"as a palceholder, it is not a concrete Synset");
	}

	@Override
	public Set<Synset> getSynonyms() throws WordNetException {
		throw new EmptySynsetException("EmptySynset should only be used " +
		"as a palceholder, it is not a concrete Synset");
	}

	@Override
	public Set<Synset> getVerbGroup() throws WordNetException {
		throw new EmptySynsetException("EmptySynset should only be used " +
		"as a palceholder, it is not a concrete Synset");
	}

	@Override
	public long getUsageOf(String word) throws WordNetException {
		throw new EmptySynsetException("EmptySynset should only be used " +
		"as a palceholder, it is not a concrete Synset");
	}

	@Override
	public List<SensedWord> getAllSensedWords() throws WordNetException {
		throw new EmptySynsetException("EmptySynset should only be used " +
		"as a palceholder, it is not a concrete Synset");
	}

	@Override
	public long getOffset() throws WordNetException{
		throw new EmptySynsetException("EmptySynset should only be used " +
		"as a palceholder, it is not a concrete Synset");
	}

	@Override
	public LexicographerFileInformation getLexicographerFileInformation()
			throws WordNetException
	{
		throw new EmptySynsetException("EmptySynset should only be used " +
		"as a palceholder, it is not a concrete Synset");
	}

}
