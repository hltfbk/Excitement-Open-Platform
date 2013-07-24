package eu.excitementproject.eop.biutee.rteflow.endtoend;

import eu.excitementproject.eop.biutee.classifiers.LinearClassifier;
import eu.excitementproject.eop.biutee.script.OperationsScript;
import eu.excitementproject.eop.biutee.utilities.BiuteeException;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.lap.biu.lemmatizer.Lemmatizer;

/**
 * A prover is given a T-H pair (as an {@link Instance}), and finds
 * the "best" proof that proves H by T (i.e., converts T to H).
 * 
 * @author Asher Stern
 * @since Jul 14, 2013
 *
 * @param <I>
 * @param <P>
 */
public abstract class Prover<I extends Instance, P extends Proof>
{
	/**
	 * Given a T-H pair (as an {@link Instance}), find and return the proof that
	 * proves H by T.
	 * @param instance the T-H pair.
	 * @param script The scripts specifies which transformations can be used
	 * (and how they can be used) when transforming T into H.
	 * @param classifierForSearch A linear classifier, which assigns the
	 * confidence to a feature vector which represents full or partial proof.
	 * @return The "best" proof (the one with the highest confidence) for
	 * proving H by T.
	 * @throws BiuteeException
	 */
	public abstract P prove(I instance, OperationsScript<Info, BasicNode> script, LinearClassifier classifierForSearch) throws BiuteeException;
	protected abstract Lemmatizer getLemmatizer() throws BiuteeException;
}
