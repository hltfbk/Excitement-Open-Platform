package eu.excitementproject.eop.lap.dkpro;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.maltparser.MaltParser;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import de.tudarmstadt.ukp.dkpro.core.treetagger.TreeTaggerPosLemmaTT4J;
import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.lappoc.LAP_ImplBaseAE;

/**
 * 
 * German tokenizer + TreeTagger + MaltParser (wrapped in DKPro component).
 * This class is provides all LAPAccess methods simply by overriding
 * addAnnotationTo() of LAP_ImplBase.
 * 
 * @author Rui
 * 
 */

public class MaltParserDE extends LAP_ImplBaseAE {

	/**
	 * the parameter for the classifier (i.e., SVM).
	 */
	private String aVariant;

	/**
	 * the default constructor.
	 * 
	 * @throws LAPException
	 */
	public MaltParserDE() throws LAPException {
		super();
		languageIdentifier = "DE";
		this.aVariant = "linear";
	}

	/**
	 * constructor with parameter for the classifier.
	 * 
	 * @param aVariant
	 *            the parameter for the classifier
	 * @throws LAPException
	 */
	public MaltParserDE(String aVariant) throws LAPException {
		super();
		languageIdentifier = "DE"; // set languageIdentifer
		this.aVariant = aVariant;
	}

	/**
	 * constructor with parameter for the classifier and view names.
	 * 
	 * @param aVariant
	 * @param views
	 * @throws LAPException
	 */
	public MaltParserDE(String aVariant, String[] views) throws LAPException {
		super(views);
		languageIdentifier = "DE";
		this.aVariant = aVariant;
	}

	@Override
	public final AnalysisEngineDescription[] listAEDescriptors()
			throws LAPException {
		// This example uses DKPro BreakIterSegmenter, TreeTagger, and
		// MaltParser
		// simply return them in an array, with order. (sentence segmentation
		// first, then tagging, then parsing)
		final int NUM_OF_ARR = 3;
		AnalysisEngineDescription[] descArr = new AnalysisEngineDescription[NUM_OF_ARR];
		try {
			descArr[0] = createPrimitiveDescription(BreakIteratorSegmenter.class);
			descArr[1] = createPrimitiveDescription(TreeTaggerPosLemmaTT4J.class);
			descArr[2] = createPrimitiveDescription(MaltParser.class,
					MaltParser.PARAM_VARIANT, aVariant,
					MaltParser.PARAM_PRINT_TAGSET, true);
		} catch (ResourceInitializationException e) {
			throw new LAPException("Unable to create AE descriptions", e);
		}

		return descArr;
	}

}
