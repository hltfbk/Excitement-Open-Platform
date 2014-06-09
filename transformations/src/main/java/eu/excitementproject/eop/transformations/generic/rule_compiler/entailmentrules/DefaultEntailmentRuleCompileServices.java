package eu.excitementproject.eop.transformations.generic.rule_compiler.entailmentrules;

import java.util.LinkedHashSet;
import java.util.Set;

import eu.excitementproject.eop.common.component.syntacticknowledge.SyntacticRule;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultEdgeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultNodeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultSyntacticInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DependencyRelation;
import eu.excitementproject.eop.common.representation.parse.representation.basic.EdgeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.representation.basic.NodeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.StanfordDependencyRelation.StanfordDepedencyRelationType;
import eu.excitementproject.eop.common.representation.parse.representation.basic.SyntacticInfo;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicConstructionNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.partofspeech.BySimplerCanonicalPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.transformations.generic.rule_compiler.CompilationException;
import eu.excitementproject.eop.transformations.generic.rule_compiler.charger.CgxMultipleChoiceExpander;
import eu.excitementproject.eop.transformations.generic.rule_compiler.charger.CgxReadingUtils;
import eu.excitementproject.eop.transformations.generic.rule_compiler.charger.RuleBuildingUtils;
/**
 * Main implementation for {@link EntailmentRuleCompileServices}.<br>
 * Singleton
 * @author Amnon Lotan
 * @since 02/06/2011
 * 
 */
public class DefaultEntailmentRuleCompileServices implements EntailmentRuleCompileServices<Info, BasicNode, BasicConstructionNode> 
{
	private static final String PERIOD = ".";
	private static DefaultEntailmentRuleCompileServices instance ;
	/**
	 * A runinng counter used for the ID of each created node.
	 */
	private static Integer nodeID = 0;

	private final BasicNode PERIOD_NODE ;
	
	/**
	 * Enum of the allowed alignment arrow types
	 * @author Amnon Lotan
	 *
	 * @since Jul 7, 2012
	 */
	private enum AlignmentType {
		COPY, COPY_REL, COPY_TAG, COPY_LEMMA, DEL_LY_FROM_LEMMA
		;
		private final static Set<String> ALIGNMENT_TYPES_STRING = new LinkedHashSet<String>();
		static	{ for (AlignmentType alignmentType : AlignmentType.values()) ALIGNMENT_TYPES_STRING.add(alignmentType.toString());	}
	}

	/**
	 * All the string parameter names that are permitted to appear inside a node in a CGX rule file
	 */
	private enum ParamNames {
		LEMMA("lemma"), REL("rel"), POS_TAG("tag"), 
		;
		
		private String strRepresentation;
		private ParamNames(String strRepresentation)	{		this.strRepresentation = strRepresentation;		}
		public String toString()	{ return strRepresentation;	}
		private final static Set<String> PARAM_NAMES_SET = new LinkedHashSet<String>();
		static	{ for (ParamNames paramName : ParamNames.values()) PARAM_NAMES_SET.add(paramName.toString());	}
	}
	
	/**
	 * Ctor
	 * @throws EntailmentCompilationException 
	 */
	private DefaultEntailmentRuleCompileServices() throws EntailmentCompilationException {
		try {
			PERIOD_NODE = new BasicNode(new DefaultInfo("", new DefaultNodeInfo(PERIOD, PERIOD, -1, null, 
					new DefaultSyntacticInfo(new BySimplerCanonicalPartOfSpeech(SimplerCanonicalPosTag.PUNCTUATION))), 
					new DefaultEdgeInfo(new DependencyRelation(StanfordDepedencyRelationType.punct.name(), null))));
		} catch (UnsupportedPosTagStringException e) {
			throw new EntailmentCompilationException("Internal bug! could not instantiate a new UnspecifiedPartOfSpeech(SimplerCanonicalPosTag.PUNCTUATION)", e);
		}
	}
	
	///////////////////////////////////////////////////////// PUBLIC IMPLEMENTATIONS //////////////////////////////////////////////////////////////////////////////////

	/**
	 * Get the singleton instance
	 * @return
	 * @throws EntailmentCompilationException
	 */
	public static DefaultEntailmentRuleCompileServices getInstance() throws EntailmentCompilationException 
	{
		if (instance == null)
			instance = new DefaultEntailmentRuleCompileServices();
		return instance;
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.compile_rules.RuleCompileServices#newNode(ac.biu.nlp.nlp.instruments.parse.representation.english.Info)
	 */
	public BasicNode newNode(Info info) {
		return new BasicNode(info);
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.rule_compiler.RuleCompileServices#doExtractionRuleRhsLastFixes(ac.biu.nlp.nlp.instruments.parse.tree.AbstractConstructionNode)
	 */
	public void doRuleLastFixes(SyntacticRule<Info, BasicNode> rule) throws EntailmentCompilationException {
		// add a period to the lhs root
		if (rule.isExtraction())
			rule.getRightHandSide().addChild(PERIOD_NODE);
	}
	
	///////////////////////////////////// PROTECTED OVERRIDE //////////////////////////////////////////////////////////////////////

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.generic.rule_compiler.RuleCompileServices#label2Node(java.lang.String)
	 */
	@Override
	public BasicConstructionNode label2Node(String label) throws EntailmentCompilationException 
	{
		if (label.contains(CgxMultipleChoiceExpander.OR_CHAR_REAL))
			throw new EntailmentCompilationException("Found a '" + CgxMultipleChoiceExpander.OR_CHAR_REAL + "' in the label which was not properly compiled. There must be a problem with the label, or in parsing a multiple choice value in: " + label);
		String lemma = null;
		DependencyRelation relation = null;
		SyntacticInfo syntacticInfo = null;
		try {
			CgxReadingUtils.sanityCheckLabel(label, ParamNames.PARAM_NAMES_SET);
			
			// a null param value means we leave its field blank
			
			lemma = CgxReadingUtils.readStringParam(label, ParamNames.LEMMA.toString());
			
			// read what parameters you can, assign null to those you can't
			String relationStr = CgxReadingUtils.readStringParam(label, ParamNames.REL.toString());
			relation = relationStr != null	? CgxReadingUtils.newDependencyRelation(relationStr) : null;
			
			// carefully construct the POS
			String partOfSpeech = CgxReadingUtils.readStringParam(label, ParamNames.POS_TAG.toString());
			syntacticInfo = CgxReadingUtils.stringToSyntacticInfo(partOfSpeech);
		} catch (CompilationException e) {
			throw new EntailmentCompilationException("see nested", e);
		}

		// a variable is a label with no lemma
		boolean isVariable = lemma == null;
		
		NodeInfo nodeInfo = isVariable ?		
				DefaultNodeInfo.newVariableDefaultNodeInfo(nodeID, syntacticInfo)	// lemmas of variables are ignored
			:
				new DefaultNodeInfo(lemma, lemma, -1, null, syntacticInfo);
						
		BasicConstructionNode node = new BasicConstructionNode( new DefaultInfo(nodeID.toString(),	nodeInfo, new DefaultEdgeInfo(relation)));				
		nodeID++;
		return node;
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.generic.rule_compiler.RuleCompileServices#getFullMappingTypeString()
	 */
	@Override
	public String getFullAlignmentTypeString() {
		return AlignmentType.COPY.name();
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.generic.rule_compiler.entailment.EntailmentRuleCompileServices#supplementRightInfoWithLeftInfo(ac.biu.nlp.nlp.instruments.parse.representation.basic.Info, ac.biu.nlp.nlp.instruments.parse.representation.basic.Info)
	 */
	@Override
	public Info supplementRightInfoWithLeftInfo(Info leftInfo, Info rightInfo) throws EntailmentCompilationException {
		if (leftInfo == null)
			throw new EntailmentCompilationException("got null left info");
		if (rightInfo == null)
			throw new EntailmentCompilationException("got null right info");
		
		Info alphaAnnotatedInfo = rightInfo;
		Info betaAnnotatedInfo = leftInfo;

		// choose args: alphaObj != null ? alphaObj : betaObj
		String lemma = (String) RuleBuildingUtils.chooseAlphaBeta(alphaAnnotatedInfo.getNodeInfo().getWordLemma(), betaAnnotatedInfo.getNodeInfo().getWordLemma()); 
		EdgeInfo newEdgeAnnotatedInfo 	= new DefaultEdgeInfo(	(DependencyRelation)	
				RuleBuildingUtils.chooseAlphaBeta(alphaAnnotatedInfo.getEdgeInfo().getDependencyRelation(), betaAnnotatedInfo.getEdgeInfo().getDependencyRelation())); 
		SyntacticInfo syntacticInfo = (SyntacticInfo) 
				RuleBuildingUtils.chooseAlphaBeta(alphaAnnotatedInfo.getNodeInfo().getSyntacticInfo(), betaAnnotatedInfo.getNodeInfo().getSyntacticInfo()); 
					
		// decide if to construct a NodeInfo or a VariableNodeAnnotatedInfo by the type of the betaNode?
		NodeInfo newAnnotatedNodeInfo;
		if (betaAnnotatedInfo.getNodeInfo().isVariable())
			// copied variable nodes get the word and lemma of their lhs originals
			newAnnotatedNodeInfo = DefaultNodeInfo.newVariableDefaultNodeInfo (betaAnnotatedInfo.getNodeInfo().getVariableId(), null, -1, null, syntacticInfo);
		else
		{
			String word = (String) RuleBuildingUtils.chooseAlphaBeta(alphaAnnotatedInfo.getNodeInfo().getWord(), betaAnnotatedInfo.getNodeInfo().getWord()); 
			newAnnotatedNodeInfo = new DefaultNodeInfo(word, lemma, -1, null, syntacticInfo);
		}
					
		Info newAnnotatedInfo =  new DefaultInfo(betaAnnotatedInfo.getId(), newAnnotatedNodeInfo, newEdgeAnnotatedInfo);			
		return newAnnotatedInfo;
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.generic.rule_compiler.entailment.EntailmentRuleCompileServices#copyLeftParamToRight(ac.biu.nlp.nlp.instruments.parse.representation.basic.Info, ac.biu.nlp.nlp.instruments.parse.representation.basic.Info, java.lang.String)
	 */
	@Override
	public Info copyLeftParamToRight(Info leftInfo, Info rightInfo,	String alignmentTypeStr) throws EntailmentCompilationException 
	{
		if (leftInfo == null)
			throw new EntailmentCompilationException("got null left info");
		if (rightInfo == null)
			throw new EntailmentCompilationException("got null right info");
		
		// get all the values from the right info
		String lemma = InfoGetFields.getLemma(rightInfo);
		SyntacticInfo syntacticInfo = new DefaultSyntacticInfo( InfoGetFields.getPartOfSpeechObject(rightInfo));
		DependencyRelation relation = rightInfo.getEdgeInfo().getDependencyRelation();
		String word = InfoGetFields.getWord(rightInfo);
		
		AlignmentType alignmentType;
		try {
			alignmentType = AlignmentType.valueOf(alignmentTypeStr.toUpperCase());
		} catch (Exception e) {
			throw new EntailmentCompilationException(alignmentTypeStr +" is not one of the valid alignment types: " + AlignmentType.ALIGNMENT_TYPES_STRING);
		}
		// override the specified parameter with the value from the left info
		switch (alignmentType)
		{
			case COPY_LEMMA:
				if (leftInfo.getNodeInfo().isVariable())
					throw new EntailmentCompilationException("A \""+ AlignmentType.COPY_LEMMA.name() + "\" cannot come out of a variable node");
				lemma = leftInfo.getNodeInfo().getWordLemma();	
				word = leftInfo.getNodeInfo().getWord();	// if the lemma is copied, the word is copied with it
				break;
			case COPY_TAG:
				syntacticInfo =  leftInfo.getNodeInfo().getSyntacticInfo();
				break;
			case COPY_REL:
				relation = leftInfo.getEdgeInfo().getDependencyRelation();
				break;
			case DEL_LY_FROM_LEMMA:
				throw new EntailmentCompilationException(AlignmentType.DEL_LY_FROM_LEMMA.name() + " type is still unsupported");
				// TODO implement at RHS instantiation
//				lemma = leftInfo.getNodeInfo().getWordLemma().replaceFirst("ly$", "");	
//				word = leftInfo.getNodeInfo().getWord().replaceFirst("ly$", "");	// if the lemma is copied, the word is copied with it
//				break;
			case COPY:
				throw new EntailmentCompilationException("Internal bug: got the full/regular alignment type \""+AlignmentType.COPY.name() +"\" where only partial alignments should be");
			default: 
				throw new EntailmentCompilationException(alignmentType + " is not expected as an AlignmentType here");
		}
		
		Info newInfo = new DefaultInfo(null, new DefaultNodeInfo(word, lemma, -1, null, syntacticInfo), new DefaultEdgeInfo(relation));
		return newInfo;
	}
}
