package eu.excitementproject.eop.core.component.alignment.vectorlink;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.chunk.Chunk;
import eu.excitement.type.alignment.Link;
import eu.excitement.type.alignment.Link.Direction;
import eu.excitement.type.alignment.Target;
import eu.excitementproject.eop.common.component.alignment.AlignmentComponent;
import eu.excitementproject.eop.common.component.alignment.AlignmentComponentException;
import eu.excitementproject.eop.common.component.alignment.PairAnnotatorComponentException;
import eu.excitementproject.eop.common.configuration.CommonConfig;
import eu.excitementproject.eop.common.configuration.NameValueTable;
import eu.excitementproject.eop.common.exception.ConfigurationException;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;

/**
 * Aligner using Word2Vec through DeepLearning4j
 * 
 * @author Madhumita
 * @since July 2015
 */
public class VectorAligner implements AlignmentComponent {

	/**
	 * Loads the model file for vector alignment. Sets similarity threshold for
	 * alignment. Reads annotations to be aligned.
	 * 
	 * @param config
	 *            Configuration file
	 * @param sectionName
	 *            Name of section containing required configuration.
	 * @throws ConfigurationException
	 * @throws IOException
	 */
	public VectorAligner(CommonConfig config, String sectionName,
			String annotName) throws ConfigurationException, IOException {

		// load the vector model
		initializeModel(config, sectionName);

		// initialize similarity threshold
		intializeThreshold(config, sectionName);

		// initialize annotation name
		this.annotName = annotName;
	}

	/**
	 * Initialize vector similarity threshold
	 * 
	 * @param config
	 *            Configuration file
	 * @param sectionName
	 *            Section name containing configuration
	 * @throws ConfigurationException
	 */
	private void intializeThreshold(CommonConfig config, String sectionName)
			throws ConfigurationException {

		NameValueTable comp = config.getSection(sectionName);

		if (null == comp) {
			throw new ConfigurationException(
					"Could not read the required section from configuration file.");
		}

		String th = comp.getString("threshold");
		if (null == th) {
			logger.info("Threshold not specified. Using 0.75 by default");
			threshold = 0.75;
		} else {
			threshold = Double.parseDouble(th);
		}

	}

	/**
	 * Initialize vector model file
	 * 
	 * @param config
	 *            Configuration file
	 * @param sectionName
	 *            Section containing required configuration
	 * @throws ConfigurationException
	 * @throws IOException
	 */
	private void initializeModel(CommonConfig config, String sectionName)
			throws ConfigurationException, IOException {
		NameValueTable comp = config.getSection(sectionName);

		if (null == comp) {
			throw new ConfigurationException(
					"Could not read the required section from configuration file.");
		}

		String modelType = comp.getString("modelType");
		String vecModel = comp.getString("vecModel");

		if (null == vecModel) {
			logger.warning("Please specify the vector model file path.");
		}

		if (modelType.equalsIgnoreCase("google")) {

			File modelFile = new File(vecModel);
			vec = WordVectorSerializer.loadGoogleModel(modelFile, true);
			if (null == vec) {
				throw new IOException("Could not load Google model file.");
			}

		}

		else {
			logger.warning("Please specify the correct model type to load");
		}

	}

	@Override
	public void annotate(JCas aJCas) throws PairAnnotatorComponentException {

		logger.info("annotate() called with a JCas with the following T and H;  ");

		if (null == aJCas)
			throw new AlignmentComponentException(
					"annotate() got a null JCas object.");

		JCas tView;
		JCas hView;
		try {
			tView = aJCas.getView(LAP_ImplBase.TEXTVIEW);
			hView = aJCas.getView(LAP_ImplBase.HYPOTHESISVIEW);
		} catch (CASException e) {
			throw new AlignmentComponentException(
					"Failed to access the Two views (TEXTVIEW, HYPOTHESISVIEW)",
					e);
		}

		logger.info("TEXT: " + tView.getDocumentText());
		logger.info("HYPO: " + hView.getDocumentText());

		AnnotationIndex<Annotation> tAnnots = null, hAnnots = null;

		if (annotName.equalsIgnoreCase("tokenWord")) {
			tAnnots = tView.getAnnotationIndex(Token.type);
			hAnnots = hView.getAnnotationIndex(Token.type);
		} else if (annotName.equalsIgnoreCase("chunk")) {
			tAnnots = tView.getAnnotationIndex(Chunk.type);
			hAnnots = hView.getAnnotationIndex(Chunk.type);
		} else {
			throw new AlignmentComponentException(
					"Failed to align data: Incorrect annotation name specified");
		}

		if (null == tAnnots) {
			throw new AlignmentComponentException(
					"Could not read text annotations");
		}

		if (null == hAnnots) {
			throw new AlignmentComponentException(
					"Could not read hypothesis annotations");
		}

		for (FSIterator<Annotation> tIter = tAnnots.iterator(); tIter.hasNext();) {
			Annotation curTAnnot = tIter.next();
			String str1 = curTAnnot.getCoveredText();

			for (FSIterator<Annotation> hIter = hAnnots.iterator(); hIter
					.hasNext();) {

				Annotation curHAnnot = hIter.next();
				String str2 = curHAnnot.getCoveredText();

				double sim = vec.similarity(str1, str2);
				logger.info("Similarity between, " + str1 + " and " + str2
						+ " is: " + sim);

				int compare = Double.compare(sim, threshold);
				if (compare == 0 || compare > 0) {
					// if similarity >= threshold, add alignment link
					logger.info("Adding alignment link between, " + str1
							+ " and " + str2);
					addAlignmentLink(tView, hView, curTAnnot, curHAnnot, sim);
				}

			}
		}
	}

	/**
	 * Add alignment link between given text and hypothesis annotation
	 * 
	 * @param tView
	 *            text view.
	 * @param hView
	 *            hypothesis view.
	 * @param tAnnot
	 *            annotation in text which needs to be linked.
	 * @param hAnnot
	 *            annotation in hypothesis which needs to be linked.
	 * @param sim
	 *            word2vec similarity between strings for tAnnot and hAnnot
	 */
	private void addAlignmentLink(JCas tView, JCas hView, Annotation tAnnot,
			Annotation hAnnot, double sim) {
		logger.info("Adding alignment link");

		// Prepare the text Target instance
		Target tTarget = prepareTarget(tView, tAnnot);

		// Prepare the hypothesis Target instance
		Target hTarget = prepareTarget(hView, hAnnot);

		// Mark an alignment.Link and add it to the hypothesis view
		Link link = new Link(hView);

		// Set link targets
		link.setTSideTarget(tTarget);
		link.setHSideTarget(hTarget);

		// Set the link direction
		link.setDirection(Direction.Bidirection);

		// Mark begin and end according to the hypothesis target
		link.setBegin(hTarget.getBegin());
		link.setEnd(hTarget.getEnd());

		// Set strength for link
		link.setStrength(sim);

		// Add the link information
		link.setAlignerID("VectorAligner");
		link.setAlignerVersion("1.0");
		link.setLinkInfo("word2vec");

		// Add to index
		link.addToIndexes();

		logger.info("Added alignment link");

	}

	/**
	 * Prepare target instance for given view using given annotations
	 * 
	 * @param view
	 *            View to prepare target instance on
	 * @param annot
	 *            Annotation to add to given target instance
	 * @return prepared target instance
	 */
	private Target prepareTarget(JCas view, Annotation annot) {

		//prepare a Target instance.
		Target target = new Target(view);
		
		//prepare a FSArray instance, put the target annotations in it.   
		FSArray tAnnots = new FSArray(view, 1);
		tAnnots.set(0, annot);
		
		//the FSArray is prepared. Put it on field "targetAnnotations" 
		target.setTargetAnnotations(tAnnots);
		
		//set begin - end value of the Target annotation (just like any annotation)
		//setting of begin and end of Target is a convention. 
		// - begin as the earliest "begin" (among Target-ed annotations) 
		// - end as the latest "end" (among Target-ed annotations) 
		target.setBegin(annot.getBegin());
		target.setEnd(annot.getEnd());
		
		//add it to the index (just like any annotation) 
		target.addToIndexes();

		return target;

	}

	@Override
	public String getComponentName() {
		return ("VectorAligner");
	}

	@Override
	public String getInstanceName() {
		return null;
	}

	/**
	 * Word2Vec model
	 */
	Word2Vec vec;

	/**
	 * Annotation on which alignment needs to be performed.
	 */
	String annotName;

	/**
	 * Word2Vec similarity threshold for alignment.
	 */
	double threshold;

	/**
	 * the logger
	 */
	public final static Logger logger = Logger.getLogger(VectorAligner.class
			.getName());

}
