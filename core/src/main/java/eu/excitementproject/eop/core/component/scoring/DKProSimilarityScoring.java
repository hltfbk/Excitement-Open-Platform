package eu.excitementproject.eop.core.component.scoring;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.similarity.algorithms.api.JCasTextSimilarityMeasure;
import de.tudarmstadt.ukp.similarity.algorithms.api.SimilarityException;
import de.tudarmstadt.ukp.similarity.algorithms.api.TextSimilarityMeasure;
import eu.excitementproject.eop.common.component.scoring.ScoringComponent;
import eu.excitementproject.eop.common.component.scoring.ScoringComponentException;
import eu.excitementproject.eop.core.DKProSimilarityClassificationEDA;


/**
 * Scoring component based on DKPro Similarity. A {@link TestSimilarityMeasure} is
 * expected to be assigned in the initialization phase, e.g. by using the
 * {@link DKProSimilarityClassificationEDA}.
 */
public class DKProSimilarityScoring
	implements ScoringComponent
{
	public static final String TEXT_VIEW = "TextView";
	public static final String HYPOTHESIS_VIEW = "HypothesisView";
	
	public static final double ERROR_SCORE = -1.0;
	
	TextSimilarityMeasure measure;
	Class<Annotation> annotation;
	
	@Override
	public String getComponentName() {
		return this.getClass().getSimpleName() + "_" + measure.getName();
	}

	@Override
	public String getInstanceName() {
		return this.getClass().getSimpleName() + "_" + measure.getName();
	}

	/**
	 * Passes the contents of the current JCas on to the given text similarity measure
	 * for finally computing text similarity.
	 */
	@Override
	public Vector<Double> calculateScores(JCas jcas)
			throws ScoringComponentException
	{
		Vector<Double> scores = new Vector<Double>();
		
		Double sim = ERROR_SCORE;		
		try {
			JCas textView = jcas.getView(TEXT_VIEW);
			JCas hypothesisView = jcas.getView(HYPOTHESIS_VIEW);
			
			if (annotation.equals(DocumentAnnotation.class))
			{
				String text1 = textView.getDocumentText();
				String text2 = hypothesisView.getDocumentText();
				
				sim = measure.getSimilarity(text1, text2);
			}
			else if (annotation.equals(Token.class))
			{
				Collection<Token> tokens1 = JCasUtil.select(textView, Token.class);
				Collection<Token> tokens2 = JCasUtil.select(hypothesisView, Token.class);
				
				Collection<String> text1 = new ArrayList<String>();
				Collection<String> text2 = new ArrayList<String>();
				
				for (Token token : tokens1)
					text1.add(token.getCoveredText());
				
				for (Token token : tokens2)
					text2.add(token.getCoveredText());				
				
				sim = measure.getSimilarity(text1, text2);
			}
			else if (annotation.equals(Lemma.class))
			{
				Collection<Lemma> lemmas1 = JCasUtil.select(textView, Lemma.class);
				Collection<Lemma> lemmas2 = JCasUtil.select(hypothesisView, Lemma.class);
				
				Collection<String> text1 = new ArrayList<String>();
				Collection<String> text2 = new ArrayList<String>();
				
				for (Lemma lemma : lemmas1)
					text1.add(lemma.getValue());
				
				for (Lemma lemma : lemmas2)
					text2.add(lemma.getValue());				
				
				sim = measure.getSimilarity(text1, text2);
			}
			else if (annotation.equals(JCas.class))
			{
				JCasTextSimilarityMeasure jcasMeasure = (JCasTextSimilarityMeasure)measure;
				sim = jcasMeasure.getSimilarity(textView, hypothesisView);
			}
			
		}
		catch (CASException e) {
			throw new ScoringComponentException(e);
		}
		catch (SimilarityException e) {
			throw new ScoringComponentException(e);
		}
		
		scores.add(sim);
		
		return scores;
	}

	public TextSimilarityMeasure getMeasure() {
		return measure;
	}

	public void setMeasure(TextSimilarityMeasure measure) {
		this.measure = measure;
	}

	public Class<Annotation> getAnnotation() {
		return annotation;
	}

	public void setAnnotation(Class<Annotation> annotation) {
		this.annotation = annotation;
	}
}
