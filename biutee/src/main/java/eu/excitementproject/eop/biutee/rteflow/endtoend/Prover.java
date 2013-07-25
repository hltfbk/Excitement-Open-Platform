package eu.excitementproject.eop.biutee.rteflow.endtoend;

import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.biutee.script.OperationsScript;
import eu.excitementproject.eop.biutee.utilities.BiuteeException;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.lap.biu.lemmatizer.Lemmatizer;

/**
 * 
 * @author Asher Stern
 * @since Jul 14, 2013
 *
 * @param <I>
 * @param <P>
 */
public abstract class Prover<I extends Instance, P extends Proof>
{
	public abstract P prove(I instance, OperationsScript<Info, BasicNode> script, LinearClassifier classifierForSearch) throws BiuteeException;
	protected abstract Lemmatizer getLemmatizer() throws BiuteeException;
}
