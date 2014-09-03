package eu.excitementproject.eop.core.component.alignment.nemex;

import org.apache.uima.jcas.JCas;

/**
 * 
 * This class is used to store the information for any query. It stores the view
 * of the hypothesis which generates a query, along with start and end offsets
 * of the query.
 * 
 * @author Madhumita
 * @since June 2014
 * 
 */

public class QueryOffset {

	JCas hypoView;
	int startOffset; // inclusive
	int endOffset; // exclusive

	public QueryOffset(JCas hView, int start, int end) {
		hypoView = hView;
		startOffset = start;
		endOffset = end;
	}

	public JCas getHypothesisView() {
		return hypoView;
	}

	public int getStartOffset() {
		return startOffset;
	}

	public int getEndOffset() {
		return endOffset;
	}

}
