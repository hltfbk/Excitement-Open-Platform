/**
 * 
 */
package eu.excitementproject.eop.distsim.storage;


import eu.excitementproject.eop.distsim.items.AdditionalInfo;
import eu.excitementproject.eop.distsim.items.Element;
import eu.excitementproject.eop.distsim.scoring.ElementsSimilarityMeasure;
import eu.excitementproject.eop.distsim.scoring.SimilarityMeasure;

/**
 * A simple field-based implementation of the {@link SimilarityMeasure} interface
 * 
 * @author Meni Adler
 * @since 10/09/2012
 *
 */
public class DefaultElementsSimilarityMeasure extends DefaultSimilarityMeasure 
	implements ElementsSimilarityMeasure {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public DefaultElementsSimilarityMeasure(Element leftElement, Element rightElemen,
			double similarityMeasue, AdditionalInfo additionalInfo) {
		super(similarityMeasue, additionalInfo);
		this.leftElement = leftElement;
		this.rightElement = rightElemen;
	}


	/* (non-Javadoc)
	 * @see org.excitement.distsim.scoring.ElementsSimilarityMeasure#getLeftElement()
	 */
	@Override
	public Element getLeftElement() {
		return leftElement;
	}

	/* (non-Javadoc)
	 * @see org.excitement.distsim.scoring.ElementsSimilarityMeasure#getRightElement()
	 */
	@Override
	public Element getRightElement() {
		return rightElement;
	}
	
	protected final Element leftElement;
	protected final Element rightElement;

}
