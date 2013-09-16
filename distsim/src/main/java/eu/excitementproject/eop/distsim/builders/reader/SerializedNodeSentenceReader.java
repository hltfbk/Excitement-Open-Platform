package eu.excitementproject.eop.distsim.builders.reader;

import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.distsim.util.Pair;
import eu.excitementproject.eop.distsim.util.Serialization;

/**
 * An implementation of the {@link SentenceReader} interface, where the sentence is represented by a {@link BasicNode}.
 * The given input stream is assumed to be composed of serializations of BasicNodes, one per each line
 * 
 * @author Meni Adler
 * @since 08/01/2013
 *
 */
public class SerializedNodeSentenceReader extends ReaderBasedSentenceReader<BasicNode>{

	public SerializedNodeSentenceReader() {
		super();
	}

	public SerializedNodeSentenceReader(ConfigurationParams params) {
		super(params);
	}

	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.distsim.builders.cooccurrence.SentenceReader#nextSentence()
	 */
	@Override
	public Pair<BasicNode,Long> nextSentence() throws SentenceReaderException {
		try {
			String line = null;
			synchronized(this) {
				line=reader.readLine();
				if (line == null) 
					return null;
				else 
					position += line.getBytes(charset).length;
			}
			return new Pair<BasicNode,Long>((BasicNode)Serialization.deserialize(line),1L);
		} catch (Exception e) {
			throw new SentenceReaderException(e);
		}
	}
	
	

}
