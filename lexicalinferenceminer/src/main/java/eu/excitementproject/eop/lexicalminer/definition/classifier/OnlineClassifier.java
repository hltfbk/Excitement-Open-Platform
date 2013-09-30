package eu.excitementproject.eop.lexicalminer.definition.classifier;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.lexicalminer.dataAccessLayer.RetrievalTool;

public abstract class OnlineClassifier extends Classifier {

	protected OnlineClassifier(RetrievalTool retrivalTool, double NPBonus){
		super(retrivalTool, NPBonus);
	}	

	@Override
	public final void setAllRank() throws LexicalResourceException {
		throw new LexicalResourceException("The classifier is an online function, so \"setAllRank\" canot be called");
	}
}
