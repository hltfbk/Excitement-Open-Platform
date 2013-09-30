package eu.excitementproject.eop.distsim.builders.reader;

import java.io.File;
import java.io.IOException;

import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.distsim.parsing.UKWacReader;
import eu.excitementproject.eop.distsim.parsing.UkWacTreeTools;
import eu.excitementproject.eop.distsim.parsing.UkwacIndexSeperators;
import eu.excitementproject.eop.distsim.util.Configuration;
import eu.excitementproject.eop.distsim.util.CreationException;
import eu.excitementproject.eop.distsim.util.Pair;

/**
 * An implementation of the {@link SentenceReader} interface, for a given stream of Conll representations of parsed sentences.
 * where the extracted sentences are converted to  {@link BasicNode}, 
 * 
 * @author Meni Adler
 * @since 08/01/2013
 *
 */

public class UKwacNodeSentenceReader extends FileBasedSentenceReader<BasicNode> {

	protected  static final String DEFAULT_ENCODING = "UTF-8";
	
	public UKwacNodeSentenceReader(ConfigurationParams params) throws ConfigurationException, CreationException {
		ukwacTool = new UkWacTreeTools();
		seperator = new UkwacIndexSeperators(params.getBoolean(Configuration.IS_CORPUS_INDEX));
		try {
			encoding = params.get(Configuration.ENCODING);
		} catch (ConfigurationException e) {
			encoding = DEFAULT_ENCODING;
		}
		reader = null;
		position = 0;
	}

	public UKwacNodeSentenceReader(boolean bIsCorpusIndex) throws ConfigurationException, CreationException {
		ukwacTool = new UkWacTreeTools();
		seperator = new UkwacIndexSeperators(bIsCorpusIndex);
		reader = null;
		position = 0;
	}

	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.distsim.builders.cooccurrence.SentenceReader#setSource(java.lang.Object)
	 */
	@Override
	public synchronized void setSource(File source) throws SentenceReaderException {
		closeSource();		
		try {
			reader = new UKWacReader(source);
			position = 0;
		} catch (IOException e) {
			throw new SentenceReaderException(e);
		}	
	}

	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.distsim.builders.cooccurrence.SentenceReader#closeSource()
	 */
	@Override
	public synchronized void closeSource() throws SentenceReaderException {
		if (reader != null)
			reader.close();
	}

	
	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.distsim.builders.cooccurrence.SentenceReader#nextSentence()
	 */
	@Override
	public Pair<BasicNode,Long> nextSentence() throws SentenceReaderException {
		try {
			String sentence = null;
			synchronized(this) {
				sentence = reader.nextSentence();
				if (sentence == null) 
					return null;
				else 
					position += sentence.getBytes(encoding).length;
			}
			BasicNode node = UkWacTreeTools.buildTreeFromSentence(sentence, seperator);				
			return new Pair<BasicNode,Long>(node,1L);
		} catch (Exception e) {
			throw new SentenceReaderException(e);
		}
	}
	
	protected UKWacReader reader;
	protected UkwacIndexSeperators seperator;
	protected UkWacTreeTools ukwacTool;
	protected String encoding;
}

