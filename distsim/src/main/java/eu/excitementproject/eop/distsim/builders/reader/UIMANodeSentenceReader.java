/**
 * 
 */
package eu.excitementproject.eop.distsim.builders.reader;

import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;

import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.lap.biu.uima.CasTreeConverterException;

/**
 * @author Meni Adler
 * @since 29 Aug 2013
 *
 */
public class UIMANodeSentenceReader extends UIMASentenceReader<BasicNode> {

	public UIMANodeSentenceReader() {
		super();
	}
	
	public UIMANodeSentenceReader(ConfigurationParams params) {
		super(params);
	}
	
	public UIMANodeSentenceReader(String aeTemplateFile) {
		super(aeTemplateFile);
	}
	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.distsim.builders.cooccurrence.UIMASentenceReader#extractData()
	 */
	@Override
	protected void extractData() throws CasTreeConverterException {
		data = converter.getSentenceToRootMap().values().iterator();			
	}

}
