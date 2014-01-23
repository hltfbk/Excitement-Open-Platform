package eu.excitementproject.eop.distsim.builders.reader;

import java.io.File;
import java.util.Iterator;

import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.xmldom.PennXmlTreePosFactory;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.xmldom.TreeAndSentence;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.xmldom.TreeXmlException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.xmldom.XmlToListTrees;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.xmldom.XmlTreePartOfSpeechFactory;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.distsim.util.Configuration;
import eu.excitementproject.eop.distsim.util.CreationException;
import eu.excitementproject.eop.distsim.util.Factory;
import eu.excitementproject.eop.distsim.util.Pair;

/**
 * An implementation of the {@link SentenceReader} interface, for a given stream of Conll representations of parsed sentences.
 * where the extracted sentences are converted to  {@link BasicNode}, 
 * 
 * @author Meni Adler
 * @since 25/04/2013
 *
 */

public class XMLNodeSentenceReader extends FileBasedSentenceReader<BasicNode> {

	public XMLNodeSentenceReader() {
		this.ignoreSavedCanonicalPosTag = true;
		this.posFactory = new PennXmlTreePosFactory();
		this.trees = null;
		this.position = 0;
	}

	public XMLNodeSentenceReader(boolean ignoreSavedCanonicalPosTag, XmlTreePartOfSpeechFactory posFactory) {
		this.ignoreSavedCanonicalPosTag = ignoreSavedCanonicalPosTag;
		this.posFactory = posFactory;
		this.trees = null;
		this.position = 0;
	}

	public XMLNodeSentenceReader(ConfigurationParams params) throws  CreationException {
		try {
			this.ignoreSavedCanonicalPosTag = params.getBoolean(Configuration.IGNORE_SAVED_CANONICAL_POS_TAG);
		} catch (ConfigurationException e) {
			this.ignoreSavedCanonicalPosTag = true;
		}
		try {
			this.posFactory = (XmlTreePartOfSpeechFactory)Factory.create(params.get(Configuration.PART_OF_SPEECH_FACTORY_CLASS),"");
		} catch (ConfigurationException e) {
			this.posFactory = new PennXmlTreePosFactory();
		}
		this.trees = null;
		this.position = 0;
	}
	
	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.distsim.builders.cooccurrence.SentenceReader#setSource(java.lang.Object)
	 */
	@Override
	public synchronized  void setSource(File source) throws SentenceReaderException {
		XmlToListTrees xml2trees = new XmlToListTrees(source.getPath(),ignoreSavedCanonicalPosTag,posFactory);
		try {
			xml2trees.createListTrees();
			trees = xml2trees.getListTrees().iterator();
			this.position = source.getTotalSpace();
		} catch (TreeXmlException e) {
			throw new SentenceReaderException(e);
		}
	}

	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.distsim.builders.cooccurrence.SentenceReader#closeSource()
	 */
	@Override
	public void closeSource() throws SentenceReaderException {
	}

	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.distsim.builders.cooccurrence.SentenceReader#nextSentence()
	 */
	@Override
	public synchronized Pair<BasicNode,Long> nextSentence() throws SentenceReaderException {
		if (trees == null || !trees.hasNext())
			return null;
		else
			return new Pair<BasicNode,Long>(trees.next().getTree(),1L);
	}
	
	
	protected boolean ignoreSavedCanonicalPosTag;
	protected XmlTreePartOfSpeechFactory posFactory;
	protected Iterator<TreeAndSentence> trees;

}

