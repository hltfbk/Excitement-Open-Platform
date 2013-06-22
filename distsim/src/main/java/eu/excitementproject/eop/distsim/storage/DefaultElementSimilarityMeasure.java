/**
 * 
 */
package eu.excitementproject.eop.distsim.storage;



import eu.excitementproject.eop.distsim.items.AdditionalInfo;
import eu.excitementproject.eop.distsim.items.Element;
import eu.excitementproject.eop.distsim.scoring.ElementSimilarityMeasure;

/**
 * @author Meni Adler
 * @since 09/09/2012
 *
 */
public class DefaultElementSimilarityMeasure extends DefaultSimilarityMeasure
		implements ElementSimilarityMeasure {

	private static final long serialVersionUID = 1L;	

	public DefaultElementSimilarityMeasure(Element element, double score, AdditionalInfo additionalInfo) {
		super(score, additionalInfo);
		this.element = element;
	}
	
	/* (non-Javadoc)
	 * @see org.excitement.distsim.scoring.ElementSimilarityMeasure#getElement()
	 */
	@Override
	public Element getElement() {
		return element;
	}
	
	protected final Element element;

}
