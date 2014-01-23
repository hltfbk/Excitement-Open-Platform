package eu.excitementproject.eop.lap.biu.uima.ae.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;
import eu.excitementproject.eop.common.representation.parse.representation.basic.NodeInfo;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeUtils;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicConstructionNode;
import eu.excitementproject.eop.common.representation.partofspeech.PennPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.lap.biu.en.parser.BasicPipelinedParser;
import eu.excitementproject.eop.lap.biu.en.parser.ParserRunException;
import eu.excitementproject.eop.lap.biu.postagger.PosTaggedToken;
import eu.excitementproject.eop.lap.biu.uima.AbstractNodeCASUtils;
import eu.excitementproject.eop.lap.biu.uima.AbstractNodeCasException;
import eu.excitementproject.eop.lap.biu.uima.ae.SingletonSynchronizedAnnotator;


/**
 * A UIMA Analysis Engine that parses the document in the CAS for a dependency parse. <BR>
 * This is only a wrapper for an existing non-UIMA <code>eu.excitementproject.eop.lap.biu.postagger.PosTagger</code>
 * interface.
 * 
 * @author Ofer Bronstein
 * @since Jan 2013
 *
 */
public abstract class StanfordDependenciesParserAE<T extends BasicPipelinedParser> extends SingletonSynchronizedAnnotator<T> {

	private static final String DEPPACKAGE = Dependency.class.getPackage().getName()+".";

	private static final Set<String> DEEP_DEPENDENCY_RELATIONS = new LinkedHashSet<String>(Arrays.asList(new String[] {
			"xsubj",
			"ref"
	}));
	
	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		try {
			
			for (Sentence sentenceAnno : JCasUtil.select(jcas, Sentence.class)) {
				List<PosTaggedToken> taggedTokens = new ArrayList<PosTaggedToken>();

				// Make sure this list is an ArrayList, so that we can access elements by index freely (random access)
				ArrayList<Token> tokenAnnotations = new ArrayList<Token>(JCasUtil.selectCovered(jcas, Token.class, sentenceAnno));
			
				// Build PosTaggedToken list of the sentence
				for (Token tokenAnno : tokenAnnotations) {
					String tokenText = tokenAnno.getCoveredText();
					PennPartOfSpeech partOfSpeech = new PennPartOfSpeech(tokenAnno.getPos().getPosValue());
					PosTaggedToken taggedToken = new PosTaggedToken(tokenText, partOfSpeech);
					taggedTokens.add(taggedToken);
				}
				
				BasicConstructionNode root;
				ArrayList<BasicConstructionNode> orderedNodes;
				
				synchronized (innerTool) {
					innerTool.setSentence(taggedTokens);
					innerTool.parse();
					root = innerTool.getMutableParseTree();
					orderedNodes = innerTool.getNodesOrderedByWords();
				}
				
				if (tokenAnnotations.size() != orderedNodes.size()) {
					throw new ParserRunException("Got parse for " + orderedNodes.size() +
							" tokens, should have gotten according to the total number of tokens in the sentence: " + tokenAnnotations.size());
				}
				
				// Get extra nodes
				Set<BasicConstructionNode> extraNodes = AbstractNodeUtils.treeToLinkedHashSet(root);
				extraNodes.removeAll(orderedNodes);
				
				// Get parent map
				Map<BasicConstructionNode, BasicConstructionNode> parentMap = AbstractNodeUtils.parentMap(root);
				
				// Handle all concrete (non-extra) nodes
				for (BasicConstructionNode node : orderedNodes) {
					NodeInfo nodeInfo = node.getInfo().getNodeInfo();
					Token tokenAnno = AbstractNodeCASUtils.nodeToToken(tokenAnnotations, node);
					
					// handle Lemma
					Lemma lemma = new Lemma(jcas, tokenAnno.getBegin(), tokenAnno.getEnd());
					lemma.setValue(nodeInfo.getWordLemma());
					lemma.addToIndexes();
					tokenAnno.setLemma(lemma);
					
					// process dependency of concrete node
					if (node != root) {
						BasicConstructionNode parentNode = parentMap.get(node);
						String relationName = node.getInfo().getEdgeInfo().getDependencyRelation().getStringRepresentation();
						
						//TODO this is a hack due to issue: https://github.com/hltfbk/Excitement-Open-Platform/issues/220
						// When the problem is solved, remove the try-catch, and just leave the plain call to processDependency()
						try {
							processDependency(jcas, tokenAnno, parentNode, relationName, tokenAnnotations, sentenceAnno);
						}
						catch (ParserRunException e) {
							if (e.getMessage().contains("is not defined in type system")) {
								System.err.println("\n\tDouble-root problem in sentence: " + taggedTokens);
								continue;
							}
							else {
								throw e;
							}
						}
					}
				}
				
				// Handle all extra nodes
				for (BasicConstructionNode node : extraNodes) {
					if (node.getAntecedent() == null) {
						throw new ParserRunException("Got node that should have an antecedent, but doesn't have one: " + node);
					}
					
					// the child in the dependency is the topmost antecedent
					BasicConstructionNode dependencyChildNode = AbstractNodeUtils.getDeepAntecedentOf(node);
					Token dependencyChildToken = AbstractNodeCASUtils.nodeToToken(tokenAnnotations, dependencyChildNode);
					
					// the parent in the dependency is the node's parent
					BasicConstructionNode dependencyParentNode = parentMap.get(node);
					
					String relationName = node.getInfo().getEdgeInfo().getDependencyRelation().getStringRepresentation();
					processDependency(jcas, dependencyChildToken, dependencyParentNode, relationName, tokenAnnotations, sentenceAnno);
					
				}
			}
		} catch (ParserRunException e) {
			throw new AnalysisEngineProcessException(AnalysisEngineProcessException.ANNOTATOR_EXCEPTION, null, e);
		} catch (AbstractNodeCasException e) {
			throw new AnalysisEngineProcessException(AnalysisEngineProcessException.ANNOTATOR_EXCEPTION, null, e);
		} catch (UnsupportedPosTagStringException e) {
			throw new AnalysisEngineProcessException(AnalysisEngineProcessException.ANNOTATOR_EXCEPTION, null, e);
		}
	}
	
	public static Set<String> getDeepDependencyRelations() {
		return DEEP_DEPENDENCY_RELATIONS;
	}
	
	private void processDependency(JCas jcas, Token childToken, BasicConstructionNode parentNode, String relationName, ArrayList<Token> tokenAnnotations, Sentence sentenceAnno) throws AbstractNodeCasException, ParserRunException {			
		// Taken mostly from de.tudarmstadt.ukp.dkpro.core.stanfordnlp.util.StanfordAnnotator.createDependencyAnnotation()
		
		// Known issue in DKPro
		if (relationName.equalsIgnoreCase("AUX")) {
			relationName = "AUX0";
		}

		String dependencyTypeName = DEPPACKAGE + relationName.toUpperCase();
		Type type = jcas.getTypeSystem().getType(dependencyTypeName);
        if (type == null) {
			throw new ParserRunException("Type [" + dependencyTypeName + "] mapped to tag ["
					+ relationName + "] is not defined in type system");
        }
        
        Token parentTokenAnno = AbstractNodeCASUtils.nodeToToken(tokenAnnotations, parentNode);
        		
		AnnotationFS anno = jcas.getCas().createAnnotation(type, sentenceAnno.getBegin(), sentenceAnno.getEnd());
		anno.setStringValue(type.getFeatureByBaseName("DependencyType"), relationName);
		anno.setFeatureValue(type.getFeatureByBaseName("Governor"), parentTokenAnno);
		anno.setFeatureValue(type.getFeatureByBaseName("Dependent"), childToken);
		jcas.addFsToIndexes(anno);
	}
}
