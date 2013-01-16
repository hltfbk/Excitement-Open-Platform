package eu.excitementproject.eop.transformations.generic.rule_compiler.annotationrules;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultEdgeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultNodeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DependencyRelation;
import eu.excitementproject.eop.common.representation.parse.representation.basic.NodeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.SyntacticInfo;
import eu.excitementproject.eop.common.utilities.Utils;
import eu.excitementproject.eop.transformations.generic.rule_compiler.CompilationException;
import eu.excitementproject.eop.transformations.generic.rule_compiler.charger.CgxMultipleChoiceExpander;
import eu.excitementproject.eop.transformations.generic.rule_compiler.charger.CgxReadingUtils;
import eu.excitementproject.eop.transformations.generic.truthteller.application.AnnotationRuleApplierFactory;
import eu.excitementproject.eop.transformations.generic.truthteller.application.ct.ClauseTruthAnnotationRuleApplier;
import eu.excitementproject.eop.transformations.generic.truthteller.representation.BasicRuleAnnotations;
import eu.excitementproject.eop.transformations.generic.truthteller.representation.CtRuleAnnotationValue;
import eu.excitementproject.eop.transformations.generic.truthteller.representation.NuRuleAnnotationValue;
import eu.excitementproject.eop.transformations.generic.truthteller.representation.PtRuleAnnotationValue;
import eu.excitementproject.eop.transformations.representation.AdditionalInformationServices;
import eu.excitementproject.eop.transformations.representation.AdditionalNodeInformation;
import eu.excitementproject.eop.transformations.representation.ExtendedConstructionNode;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedInfoGetFields;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.representation.AdditionalNodeInformation.Monotonicity;
import eu.excitementproject.eop.transformations.representation.annotations.ClauseTruth;
import eu.excitementproject.eop.transformations.representation.annotations.NegationAndUncertainty;
import eu.excitementproject.eop.transformations.representation.annotations.PredTruth;
import eu.excitementproject.eop.transformations.representation.annotations.PredicateSignature;

/**
 * Default implementation of {@link AnnotationRuleCompileServices}, for interpreting the String values in rule CGX/XML files meant to be annotation rules, 
 * using {@link ExtendedInfo}, and {@link ExtendedNode}.
 * 
 * @author Amnon Lotan
 * @since 02/06/2011
 * 
 */
public class DefaultAnnotationRuleCompileServices implements AnnotationRuleCompileServices<ExtendedInfo, ExtendedNode, ExtendedConstructionNode, BasicRuleAnnotations>	 
{
	/////////////////////////////////////////////////////////// ENUMS ////////////////////////////////////////////////////////////
	
	/**
	 * These are the supported types of rule-compilation-mapping, where certain parameters in a rule's LHS are mapped (and maybe altered) to
	 * a certain LHS node of the same rule
	 * 
	 * @author Amnon Lotan
	 * @since 19/06/2011
	 */
	public enum MappingType {
		COPY, 
		LOOKUP_PREDTYPE_OF_LEMMA, 
		COPY_CT_AND_PT, 
		COPY_MONOTONICITY,
		COPY_NU,
		COPY_CT,
		COPY_PT,	// insofar, this is never actually used in the (not special) CGX rules
		;
		
		public static final String PRINTED_VALUES = Utils.arrayToCollection(MappingType.values(), new HashSet<MappingType>()).toString();
	}
	
	/**
	 * All the string parameter names that are permitted to appear inside a node in a CGX rule file
	 */
	private enum ParamNames {
		LEMMA("lemma"), REL("rel"), POS_TAG("tag"),  
		/**
		 * Predicate implication signature (factive, implicative, regular...) 
		 */
		SIGNATURE("sig"),
		/**
		 * Negation and Uncertainty
		 */
		NU("nu"),
		/**
		 * Clause Truth 
		 */
		CT("ct"), 
		/**
		 * Predicate Truth. insofar, it is not altered by any rule, but only by {@link ClauseTruthAnnotationRuleApplier} 
		 */
		PT("pt"),	
		MONOTONICITY("monotonicity"),
		;
		
		private String strRepresentation;				// this is the string used in the CGX rule files. no spaces!
		private ParamNames(String strRepresentation)	{		this.strRepresentation = strRepresentation;		}
		public String toString()	{ return strRepresentation;	}
		private final static Set<String> PARAM_NAMES_SET = new HashSet<String>();
		static	{ for (ParamNames paramName : ParamNames.values()) PARAM_NAMES_SET.add(paramName.toString());	}
		private final static Set<String> ANNOTATION_PARAM_NAMES_SET = Utils.arrayToCollection(new String[]
				{SIGNATURE.toString(), NU.toString(), CT.toString(), PT.toString(), MONOTONICITY.toString() }, new HashSet<String>());
	}

	////////////////////////////////////////////////// PRIVATE CONSTANTS ///////////////////////////////////////////////////////////////
	
	private final HashMap<String, PredicateSignature> lemmaToPredTypeMap;
	/**
	 * all listed predicates
	 */
	private final Set<String> predicateList;
	private final Set<PhrasalVerbTemplate> phraseVerbTemplates;
	/**
	 * just a running index of the nodes created
	 */
	private static Integer nodeID = 0;

	////////////////////////////////////////////////// PUBLIC ///////////////////////////////////////////////////////////////
	
	/**
	 * Ctor - <br>
	 * constructs a private lemma-->{@link PredicateSignature} map out of the predicate lists file<br>
	 * A map of phrasal verb {@link PredicateSignature}s<br>
	 * an {@link AnnotationRuleApplierFactory}<br>
	 *  
	 * @param predListPropsName name of the predicate lists file
	 * @param recursiveCTLabel 
	 * @param complementRelations 
	 * @param infinitiveComplementRelations 
	 * @param prepRelations 
	 * @throws AnnotationCompilationException 
	 */
	public DefaultAnnotationRuleCompileServices(String predListPropsName, 
			String phrasalVerbFamiliesFile, String phrasalNounFamiliesFile, String phrasalImplicativeTemplatesFile) throws  AnnotationCompilationException
	{
		// iterate over the props file's keys (predicate types)and map their values (predicates) back to them
		try {
			lemmaToPredTypeMap = AnnotationRuleLexiconUtils.loadPredicateTypePropertiesFile(predListPropsName);
			
			predicateList = lemmaToPredTypeMap.keySet();
			
			phraseVerbTemplates = AnnotationRuleLexiconUtils.loadPhrasalVerbTemplates(
					phrasalVerbFamiliesFile, phrasalNounFamiliesFile, phrasalImplicativeTemplatesFile);
			
		} catch (Exception e) {
			throw new AnnotationCompilationException("Error initializing. See nested", e);
		}
	}

	/**
	 * return an array of all listed predicates
	 * @return an array of all listed predicates
	 */
	@Override
	public Set<String> getPredicateList()
	{
		return predicateList;
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.compile_rules.RuleCompileServices#newNode(ac.biu.nlp.nlp.instruments.parse.representation.english.Info)
	 */
	public ExtendedNode newNode(ExtendedInfo info) {
		return new ExtendedNode(info);
	}
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.compile_rules.RuleCompileServices#performPartialMappings(ac.biu.nlp.nlp.compile_rules.IterableDirectedPairMap)
	 */
	public void performPartialMappings(	List<PartialAlignment<ExtendedConstructionNode, BasicRuleAnnotations>> partialAlignments)	throws AnnotationCompilationException 
	{
		for (PartialAlignment<ExtendedConstructionNode, BasicRuleAnnotations> partialAlignment : partialAlignments)
		{		
			ExtendedConstructionNode lhs = partialAlignment.getNode();
			MappingType mappingType;
			try {
				mappingType = MappingType.valueOf(partialAlignment.getType().toUpperCase());
			} catch (Exception e) {
				throw new AnnotationCompilationException("There is an illegal \"" + partialAlignment.getType() +"\" partial alignment arrow in the rule, coming out of: "+lhs + ". \nThese are the allowed types: " + MappingType.PRINTED_VALUES, e);
			}
			
			// make a dummy node to represent the one attribute from lhs that needs to be mapped
			ClauseTruth ct;
			PredTruth pt;
			BasicRuleAnnotations annotations = partialAlignment.getAnnotations();
			try {
				switch (mappingType)
				{
					case COPY_CT:
						ct = ExtendedInfoGetFields.getClauseTruthObj(lhs.getInfo());
						if (ct != null)
							annotations.setCt(new CtRuleAnnotationValue(ct));
						break;
					case COPY_PT:
						pt = ExtendedInfoGetFields.getPredTruthObj(lhs.getInfo());
						if (pt != null)
							annotations.setPt(new PtRuleAnnotationValue(pt));
						break;
					case COPY_CT_AND_PT:
						ct = ExtendedInfoGetFields.getClauseTruthObj(lhs.getInfo());
						if (ct != null)
							annotations.setCt(new CtRuleAnnotationValue(ct));
						pt = ExtendedInfoGetFields.getPredTruthObj(lhs.getInfo());
						if (pt != null)
							annotations.setPt(new PtRuleAnnotationValue(pt));
						break;
					case COPY_MONOTONICITY:
						Monotonicity monotonicity = lhs.getInfo().getAdditionalNodeInformation().getMonotonicity();
						if (monotonicity != null)
							annotations.setMonotonicity(monotonicity);
						break;
					case COPY_NU:
						NegationAndUncertainty nuObj = ExtendedInfoGetFields.getNegationAndUncertaintyObj(lhs.getInfo());
						if (nuObj != null)
							annotations.setNu(new NuRuleAnnotationValue( nuObj ));
						break;
					case LOOKUP_PREDTYPE_OF_LEMMA:
						// copy the predType of the lhs's lemma to the rhs
						PredicateSignature  predType = lemmaToPredTypeMap.get(lhs.getInfo().getNodeInfo().getWordLemma());
						annotations.setSig(predType);
						break;
					case COPY:
						throw new AnnotationCompilationException("Internal error: " + MappingType.COPY + " is the reglar/full alignment type, and should not be found here");
					default: 
						throw new AnnotationCompilationException(mappingType + " is not one of these defined partial MappingTypes: " + MappingType.PRINTED_VALUES);
				}
			} catch (eu.excitementproject.eop.transformations.generic.truthteller.representation.AnnotationValueException e) {
				throw new AnnotationCompilationException("One of the annotation values is illegal in the node: "+ lhs, e);
			}
		}	
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.generic.rule_compiler.RuleCompileServices#label2Node(java.lang.String)
	 */
	public ExtendedConstructionNode label2Node(String label)  throws AnnotationCompilationException
	{
		if (label.contains(CgxMultipleChoiceExpander.OR_CHAR_REAL))
			throw new AnnotationCompilationException("This label contains an '" + CgxMultipleChoiceExpander.OR_CHAR_REAL + "'. There must be a problem in parsing this varied parameter: " + label);
		// a null param value means we leave its field blank
		String lemma = null;
		DependencyRelation dr;
		SyntacticInfo syntacticInfo = null;
		PredicateSignature predType = null;
		ClauseTruth clauseTruth = null;
		Monotonicity monotonicity = null;
		NegationAndUncertainty negation = null;
		PredTruth predTruth = null;
		try {
			CgxReadingUtils.sanityCheckLabel(label, ParamNames.PARAM_NAMES_SET);
			
			lemma = CgxReadingUtils.readStringParam(label, ParamNames.LEMMA.toString());
			
			// read what parameters you can, assign null to those you can't
			String relation = CgxReadingUtils.readStringParam(label, ParamNames.REL.toString());
			dr = relation != null	? CgxReadingUtils.newDependencyRelation(relation) : null;
			
			// carefully construct the POS
			String partOfSpeech = CgxReadingUtils.readStringParam(label, ParamNames.POS_TAG.toString());
			syntacticInfo = CgxReadingUtils.stringToSyntacticInfo(partOfSpeech);

			// read annotations, defaulting to null. If the label doesn't specify a specific annotation, leave its variable null
			String predTypeStr = 		CgxReadingUtils.readStringParam(label, ParamNames.SIGNATURE.toString());
			predType = predTypeStr != null ? PredicateSignature.valueOf(predTypeStr) : null;
			String clauseTruthStr = 	CgxReadingUtils.readStringParam(label, ParamNames.CT.toString());
			try {clauseTruth = clauseTruthStr != null ? ClauseTruth.valueOf(clauseTruthStr) : null;	}
			catch (Exception e) { throw new AnnotationCompilationException(clauseTruthStr + " is not a valid CT value", e);	}
			String monotonicityStr = 	CgxReadingUtils.readStringParam(label, ParamNames.MONOTONICITY.toString());
			monotonicity = monotonicityStr != null ? Monotonicity.valueOf(monotonicityStr) : null;
			String negationStr = 	CgxReadingUtils.readStringParam(label, ParamNames.NU.toString());
			try {	negation = negationStr != null ? NegationAndUncertainty.valueOf(negationStr) : null;	}
			catch (Exception e) { throw new AnnotationCompilationException(negationStr + " is not a valid NU value", e);	}
			String predTruthStr = 	CgxReadingUtils.readStringParam(label, ParamNames.PT.toString());
			try {predTruth = predTruthStr != null ? PredTruth.valueOf(predTruthStr) : null;	}
			catch (Exception e) { throw new AnnotationCompilationException(predTruthStr + " is not a valid PT value", e);	}
		} catch (CompilationException e) {
			throw new AnnotationCompilationException("see nested", e);
		}
		
		// a variable is a label with no "lemma" param
		boolean isVar = lemma == null;
		
		NodeInfo nodeAnnotatedInfo = isVar ?		// this is a variable
				DefaultNodeInfo.newVariableDefaultNodeInfo(nodeID, null, -1, null,syntacticInfo)
				// lemmas of variables are ignored
			:
				new DefaultNodeInfo(lemma, lemma, -1, null, syntacticInfo);				// construct an AdditionalNodeInformation
		
		AdditionalNodeInformation additionalNodeInfo = 	AdditionalInformationServices.generateFromAnnotations(predType, negation, predTruth, clauseTruth, monotonicity);
				
		ExtendedConstructionNode node = new ExtendedConstructionNode( new ExtendedInfo(
					nodeID.toString(),
					nodeAnnotatedInfo,
					new DefaultEdgeInfo(dr), additionalNodeInfo));				
		
		nodeID ++;
		return node;
	}
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.generic.rule_compiler.annotated.AnnotationRuleCompileServices#labelToAnnotations(java.lang.String)
	 */
	@Override
	public BasicRuleAnnotations labelToAnnotations(String label) throws AnnotationCompilationException {
		if (label.contains(CgxMultipleChoiceExpander.OR_CHAR_REAL))
			throw new AnnotationCompilationException("This label contains an '" + CgxMultipleChoiceExpander.OR_CHAR_REAL + "'. There must be a problem in parsing this varied parameter: " + label);
		try {
			CgxReadingUtils.sanityCheckLabel(label, ParamNames.ANNOTATION_PARAM_NAMES_SET);
			
			// read annotations, defaulting to null. If the label doesn't specify a specific annotation, leave its variable null
			String predTypeStr = 		CgxReadingUtils.readStringParam(label, ParamNames.SIGNATURE.toString());
			PredicateSignature predType = 		predTypeStr != null ? PredicateSignature.valueOf(predTypeStr) : null;
			
			String monotonicityStr = 	CgxReadingUtils.readStringParam(label, ParamNames.MONOTONICITY.toString());
			Monotonicity monotonicity = monotonicityStr != null ? Monotonicity.valueOf(monotonicityStr) : null;
			
			CtRuleAnnotationValue ct = null;
			NuRuleAnnotationValue nu = null;
			PtRuleAnnotationValue pt = null;
			try {
				String clauseTruthStr = 	CgxReadingUtils.readStringParam(label, ParamNames.CT.toString());
				if (clauseTruthStr != null)
					ct = new CtRuleAnnotationValue(clauseTruthStr);
				
				String negationStr = 	CgxReadingUtils.readStringParam(label, ParamNames.NU.toString());
				if (negationStr != null)
					nu = new NuRuleAnnotationValue(negationStr);
				
				String predTruthStr = 	CgxReadingUtils.readStringParam(label, ParamNames.PT.toString());
				if (predTruthStr != null)
					pt = new PtRuleAnnotationValue(predTruthStr);
			} catch (eu.excitementproject.eop.transformations.generic.truthteller.representation.AnnotationValueException e) {
				throw new AnnotationCompilationException("Illegal annotation value found in this label: " + label, e);
			}
			
			BasicRuleAnnotations annotations = new BasicRuleAnnotations(predType, nu, ct, pt, monotonicity);
			return annotations;
		} catch (CompilationException e) {
			throw new AnnotationCompilationException("see nested", e);
		}
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.rule_compiler.RuleCompileServices#getFullMappingType()
	 */
	public String getFullAlignmentTypeString() {
		return MappingType.COPY.name();
	}
	
	
	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.generic.rule_compiler.RuleCompileServices#doSpecialRuleTextExpantion(java.util.Collection)
	 */
	@Override
	public Set<String> doSpecialRuleTextExpantion(Collection<String> ruleTexts) throws AnnotationCompilationException {
		return AnnotationRuleLexiconUtils.expandPhrasalVerbSignatureAnnotationRule(ruleTexts, phraseVerbTemplates);
	}
	
//	/* (non-Javadoc)
//	 * @see ac.biu.nlp.nlp.generic.rule_compiler.annotated.AnnotationRuleCompileServices#getSpecialRule(ac.biu.nlp.nlp.engineml.generic.annotation.representation.RuleType, ac.biu.nlp.nlp.engineml.generic.annotation.representation.AnnotationRule)
//	 */
//	@Override
//	public AnnotationRule<ExtendedNode, BasicRuleAnnotations> getSpecialRule(RuleType ruleType, AnnotationRule<ExtendedNode, BasicRuleAnnotations> rule)
//			throws AnnotationCompilationException 
//	{
//		try {
//			return AnnotationRuleApplierFactory.getSpecialSubstitutionRule(ruleType, rule);
//		} catch (AnnotatorException e) {
//			throw new AnnotationCompilationException("see nested", e);
//		}
//	}
}
