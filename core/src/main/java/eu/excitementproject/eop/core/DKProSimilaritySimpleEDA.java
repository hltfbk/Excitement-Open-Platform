package eu.excitementproject.eop.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil;

import eu.excitement.type.entailment.Pair;
import eu.excitementproject.eop.common.DecisionLabel;
import eu.excitementproject.eop.common.EDAException;
import eu.excitementproject.eop.common.TEDecision;
import eu.excitementproject.eop.common.component.scoring.ScoringComponent;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.configuration.NameValueTable;
import eu.excitementproject.eop.common.exception.ComponentException;
import eu.excitementproject.eop.common.exception.ConfigurationException;


/**
 * Simple DKPro Similarity based EDA. Supports multiple text similarity measures in parallel,
 * where the maximum score is used in combination with a given threshold to determin whether
 * an entailment relationship holds or not. 
 */
public class DKProSimilaritySimpleEDA
	extends DKProSimilarityEDA_ImplBase<TEDecision>
{
	private double threshold;
	
	@Override
	public void initialize(CommonConfig config)
		throws ConfigurationException, EDAException, ComponentException
	{
		super.initialize(config);
		initializeThreshold(config);
	}
	
	/**
	 * Reads the threshold from the configuration. We use a threshold to decide
	 * whether it's an entailment (above the threshold) or not (below). This is
	 * intended for illustration purposes only, as the similarity scores are
	 * not normally distributed.
	 */
	private void initializeThreshold(CommonConfig config)
			throws ConfigurationException, ComponentException
	{
		NameValueTable EDA = null;
		try {
			EDA = config.getSection(this.getClass().getName());
		} catch (ConfigurationException e) {
			throw new ConfigurationException(e.getMessage()
					+ " No EDA section.");
		}
		
		threshold = EDA.getDouble("threshold");
	}
	
	@Override
	public ClassificationTEDecision process(JCas jcas)
		throws EDAException, ComponentException
	{
		Pair pair = JCasUtil.selectSingle(jcas, Pair.class);
		
		// Compute similarity scores with all components
		List<Double> scores = new ArrayList<Double>();
		
		for (ScoringComponent component : getComponents())
		{
			Vector<Double> subscores = component.calculateScores(jcas);
			
			scores.addAll(subscores);
		}

		// If multiple components have been used, we use the highest score
		// to determine the Entailment/NonEntailment relationship.
		// This is intended for illustration purposes only, as the similarity
		// scores are not normally distributed.	
		double maxScore = Collections.max(scores); 
		
		DecisionLabel label;
		if (maxScore >= threshold)
			label = DecisionLabel.Entailment;
		else
			label = DecisionLabel.NonEntailment;
		
		return new ClassificationTEDecision(label,
				scores.get(0),
				pair.getPairID());
	}

	@Override
	public void shutdown()
	{
		// nothing to do
	}

	@Override
	public void startTraining(CommonConfig c)
		throws ConfigurationException, EDAException, ComponentException
	{
		// We don't train for this simple EDA.
		
	}
}
