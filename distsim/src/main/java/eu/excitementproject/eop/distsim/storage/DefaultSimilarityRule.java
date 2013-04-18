/**
 * 
 */
package eu.excitementproject.eop.distsim.storage;


import eu.excitementproject.eop.distsim.items.AdditionalInfo;
import eu.excitementproject.eop.distsim.items.Element;
import eu.excitementproject.eop.distsim.scoring.SimilarityRule;

/**
 * @author Meni Adler
 * @since 09/09/2012
 *
 */
public class DefaultSimilarityRule extends DefaultSimilarityMeasure
		implements SimilarityRule {

	private static final long serialVersionUID = 1L;	

	public DefaultSimilarityRule(Element leftElement,Element rightElement, double score, AdditionalInfo additionalInfo) {
		super(score, additionalInfo);
		this.leftElement = leftElement;
		this.rightElement = rightElement;
	}
	
	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.distsim.scoring.SimilarityRule#getLeftElementID()
	 */
	@Override
	public Element getLeftElement() {
		return leftElement;
	}
	
	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.distsim.scoring.SimilarityRule#getRightElementID()
	 */
	@Override
	public Element getRightElement() {
		return rightElement;
	}

	protected final Element leftElement;
	protected final Element rightElement;

}
