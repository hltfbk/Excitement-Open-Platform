package eu.excitementproject.eop.lap.btbpipe;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.uima.jcas.JCas;

import eu.excitementproject.eop.lap.LAPException;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.*;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.*;

/**
 * Add the output of the BTB pipeline to the CAS
 * 
 * @author Iliana Simova (BulTreeBank team)
 * 
 */
public class LAP_BTBPipe extends LAP_ImplBase {

	Logger logger = Logger.getLogger(getClass().getName());

	private static final String CLARK_DATA_DIR = "/btbPipeData/clarkData";
	private static final String TAGSET_MAP = "/btbPipeData/btbTagset.map";
	private static final String DEFAULT_TAG = "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.O";
	private Properties tagSetMap;

	Preprocessor prep;

	/**
	 * Initialize the preprocessing module of the pipeline
	 * 
	 * @throws LAPException
	 */
	public LAP_BTBPipe() throws LAPException {
		super();
		this.languageIdentifier = "BG";

		String clarkDataAbsPath = this.getClass().getResource(CLARK_DATA_DIR)
				.getPath();

		// initialize the processor
		try {
			prep = new Preprocessor(clarkDataAbsPath);
		} catch (Exception e) {
			e.printStackTrace();
			throw new LAPException(
					"The preprocessing component in btbpipe could not be initialized");
		}

		// read the file containing the tagset mapping, needed to store the
		// result of POS tagging
		tagSetMap = new Properties();

		try {
			tagSetMap.load(new InputStreamReader(this.getClass()
					.getResourceAsStream(TAGSET_MAP)));
		} catch (IOException e) {
			e.printStackTrace();
			throw new LAPException(
					"Processing failed due to missing tagset mapping file for the BTB-tagset.");
		}

	}

	/**
	 * Add the produced annotations for this view
	 * 
	 * @param aJCas
	 * @param viewName
	 */
	@Override
	public void addAnnotationOn(JCas aJCas, String viewName)
			throws LAPException {
		try {
			String text = aJCas.getView(viewName).getDocumentText();

			List<BTBSentence> sentences = prep.process(text);

			for (BTBSentence sent : sentences) {
				// for each sentence
				Sentence sentAnnotation = new Sentence(aJCas);
				sentAnnotation.setBegin(sent.getBegin());
				sentAnnotation.setEnd(sent.getEnd());
				sentAnnotation.addToIndexes();

				for (BTBToken tok : sent.getTokens()) {
					// for each token

					Token tokAnnotation = new Token(aJCas.getView(viewName));
					tokAnnotation.setBegin(tok.getBegin());
					tokAnnotation.setEnd(tok.getEnd());

					// add lemma annotation
					Lemma lemAnnotation = new Lemma(aJCas.getView(viewName));
					lemAnnotation.setBegin(tok.getBegin());
					lemAnnotation.setEnd(tok.getEnd());
					lemAnnotation.setValue(tok.getLemma());
					lemAnnotation.addToIndexes();

					// add pos annotation
					Class<?> posAnnotClass = Class.forName(getTagType(tok.getBtbPOS()));
					Constructor<?> posAnnotClassCon = posAnnotClass.getConstructor(JCas.class);
					POS posAnnotation = (POS) posAnnotClassCon.newInstance(aJCas.getView(viewName));
					posAnnotation.setBegin(tok.getBegin());
					posAnnotation.setEnd(tok.getEnd());
					posAnnotation.setPosValue(tok.getBtbPOS());
					posAnnotation.addToIndexes();

					tokAnnotation.setLemma(lemAnnotation);
					tokAnnotation.setPos(posAnnotation);
					tokAnnotation.addToIndexes();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new LAPException("Error processing " + viewName);
		}

	}

	/**
	 * Return the POS type which corresponds to the given morphoTag
	 * 
	 * @param morphoTag
	 * @return
	 */
	private String getTagType(String morphoTag) {
		String mapKey = "";

		if (morphoTag.equalsIgnoreCase("punct")) {
			// punctuation tag
			mapKey = "Punct";
		} else if (morphoTag.equals("N")) {
			// noun tag defaults to common noun
			mapKey = "Nc";
		} else if (morphoTag.startsWith("N")) {
			// noun tag's second letter determines if it is personal or common
			mapKey = morphoTag.substring(0, 2);
		} else {
			mapKey = String.valueOf(morphoTag.charAt(0));
		}
		
		if (tagSetMap.containsKey(mapKey)) {
			return tagSetMap.getProperty(mapKey);
		} else {
			logger.warning("The short BTB-tag \""
					+ mapKey
					+ "\" was not found in the tagset map file. Assigning"
					+ " default tag value \"" + DEFAULT_TAG + "\"");
			return DEFAULT_TAG;
		}
	}
	
public static void main(String[] args) {
		// test
		try {
			LAP_BTBPipe l = new LAP_BTBPipe();

			String testFile = "src/test/resources/small_bg.xml";
			l.processRawInputFormat(new File(testFile), new File("./"));			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
