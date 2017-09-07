package eu.excitementproject.eop.biutee.rteflow.systems.excitement;

import java.util.List;
import java.util.Map;

import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.fit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import eu.excitement.type.entailment.Hypothesis;
import eu.excitement.type.entailment.Pair;
import eu.excitement.type.entailment.Text;
import eu.excitementproject.eop.biutee.rteflow.systems.rtepairs.PairData;
import eu.excitementproject.eop.common.DecisionLabel;
import eu.excitementproject.eop.common.EDAException;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformation;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.common.utilities.datasets.rtepairs.RTEClassificationType;
import eu.excitementproject.eop.common.utilities.datasets.rtepairs.TextHypothesisPair;
import eu.excitementproject.eop.lap.biu.uima.CasTreeConverter;
import eu.excitementproject.eop.lap.biu.uima.CasTreeConverterException;
import eu.excitementproject.eop.lap.implbase.LAP_ImplBase;

/**
 * Converts a JCas created by an EOP LAP, to BIU's PairData.
 * 
 * @author Ofer Bronstein
 * @since March 2013
 */
public class CasPairDataConverter {

	/**
	 * Converts a JCas created by an EOP LAP, to BIU's PairData.
	 * @param jcas
	 * @return
	 * @throws CasTreeConverterException
	 * @throws UnsupportedPosTagStringException
	 * @throws CASException
	 * @throws EDAException 
	 */
	public static PairData convertCasToPairData(JCas jcas) throws CasTreeConverterException, UnsupportedPosTagStringException, CASException, EDAException {
		Pair pairAnno = JCasUtil.selectSingle(jcas, Pair.class);
		Text textAnno = pairAnno.getText();
		Hypothesis hypothesisAnno = pairAnno.getHypothesis();
		JCas textView = jcas.getView(LAP_ImplBase.TEXTVIEW);
		JCas hypothesisView = jcas.getView(LAP_ImplBase.HYPOTHESISVIEW);
		
		Integer id = null;
		String stringID = pairAnno.getPairID();
		try {
			id = Integer.valueOf(stringID);
		}
		catch (NumberFormatException e) {
			// Ignore
		}
		
		RTEClassificationType gold = null;
		String goldString = pairAnno.getGoldAnswer();
		if (goldString != null) {
			DecisionLabel goldDecision = DecisionLabel.getLabelFor(goldString);
			gold = DecisionTypeMap.toRTEClassificationType(goldDecision);
		}
		
		CasTreeConverter converter = new CasTreeConverter();
		List<BasicNode> textTrees = converter.convertCasToTrees(textView);
		Map<BasicNode, String> mapTreesToSentences = converter.getTreesToSentences();
		
		Sentence hypothesisSentence = JCasUtil.selectSingle(hypothesisView, Sentence.class);
		BasicNode hypothesisTree = converter.convertSingleSentenceToTree(hypothesisView, hypothesisSentence);
		
		// Currently not supporting coreference information - using empty map
		TreeCoreferenceInformation<BasicNode> coreferenceInformation = new TreeCoreferenceInformation<BasicNode>();
		
		TextHypothesisPair pair = new TextHypothesisPair(textAnno.getCoveredText(), hypothesisAnno.getCoveredText(), id, gold, null);
		return new PairData(pair, textTrees, hypothesisTree, mapTreesToSentences, coreferenceInformation);
		
	}

	/**
	 * Returns the pair ID from a JCas created by an EOP LAP.
	 * @param jcas
	 * @return
	 */
	public static String getPairIdFromCas(JCas jcas) {
		Pair pairAnno = JCasUtil.selectSingle(jcas, Pair.class);
		return pairAnno.getPairID();
	}
}