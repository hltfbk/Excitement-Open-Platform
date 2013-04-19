package eu.excitementproject.eop.distsim.util;

import java.util.HashSet;

import eu.excitementproject.eop.common.representation.partofspeech.CanonicalPosTag;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;


/**
 * An implementation of the Filter interface, which is based on a fixed set of relevant CanonicalPosTags
 * 
 * @author Meni Adler
 * @since 05/12/2012
 *
 */
public class SetBasedPOSFilter extends SetBasedFilter<CanonicalPosTag> {

	public SetBasedPOSFilter(ConfigurationParams params) {
		try {
			this.relevantItems = new HashSet<CanonicalPosTag>();
			for (String relevantPOS : params.getStringArray(Configuration.RELEVANT_POS_LIST))
				this.relevantItems.add(CanonicalPosTag.valueOf(relevantPOS));
		} catch (ConfigurationException e) {
			this.relevantItems = null;
		}
	}
	
	public SetBasedPOSFilter(CanonicalPosTag... relevantPOSs) {		
		for (CanonicalPosTag relevantPOS : relevantPOSs)
			this.relevantItems.add(relevantPOS);
	}

}
