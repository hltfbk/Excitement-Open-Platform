package eu.excitementproject.eop.core.component.alignment.nemex;

import org.apache.uima.jcas.JCas;

public class QueryOffset {
	
	JCas hypoView;
	int startOffset; //inclusive
	int endOffset; //exclusive
	
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
