package eu.excitementproject.eop.lexicalminer.Italian.language.textpro;

import java.util.ArrayList;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableList;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableListWrapper;
import eu.excitementproject.eop.common.representation.partofspeech.ByCanonicalPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.CanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.lap.biu.lemmatizer.Lemmatizer;
import eu.excitementproject.eop.lap.biu.lemmatizer.LemmatizerException;


/**
 * Created with IntelliJ IDEA.
 * User: aprosio
 * Date: 9/6/12
 * Time: 11:54 AM
 * To change this template use File | Settings | File Templates.
 */
public class TextProLemmatizer implements Lemmatizer {

    private String word;
    private String command;
    private PartOfSpeech partOfSpeech;
    private ImmutableList<String> lemmas;

    public TextProLemmatizer(ConfigurationParams params) throws LemmatizerException{
    	try {
			command = params.getString("TextPro-lemmatizer-command");
		} catch (ConfigurationException e) {
			throw new LemmatizerException("Nested exception while reading from the configuration file",e);
		}
    }
    
    public void init() throws LemmatizerException {
        word = "";
        partOfSpeech = null;
    }

    public void set(String word) throws LemmatizerException {
        this.word = word;
        try {
            this.partOfSpeech = new ByCanonicalPartOfSpeech(CanonicalPosTag.N.name());
        }
        catch(Exception e) {
            throw new LemmatizerException("POS exception", e);
        }
    }

    public void set(String word, PartOfSpeech partOfSpeech) throws LemmatizerException {
        this.word = word;
        this.partOfSpeech = partOfSpeech;
    }

    public void process() throws LemmatizerException {
	    String pos = null;
	    if (partOfSpeech != null) {
	        pos = partOfSpeech.toString();
        }
        ArrayList<String> tmpLemmas = GenerateLemmaFromWord.get(word, pos, command);
	    lemmas = new ImmutableListWrapper<String>(tmpLemmas);
    }

    public String getLemma() throws LemmatizerException {
        if (lemmas.size() < 1) {
            throw new LemmatizerException("No lemmas in array");
        }
        return lemmas.get(0);
    }

    public ImmutableList<String> getLemmas() throws LemmatizerException {
        return lemmas;
    }

    public void cleanUp() {
        try {
            init();
        } catch (LemmatizerException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
