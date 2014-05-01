/**
 * 
 */
package eu.excitementproject.eop.transformations.generic.truthteller.application.ct;
import java.util.Iterator;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.StanfordDependencyRelation.StanfordDepedencyRelationType;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.partofspeech.PennPartOfSpeech.PennPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerPosTagConvertor;
import eu.excitementproject.eop.lap.biu.en.parser.minipar.AbstractMiniparParser;
import eu.excitementproject.eop.transformations.generic.truthteller.AnnotatorException;
import eu.excitementproject.eop.transformations.generic.truthteller.TruthTellerConstants;
import eu.excitementproject.eop.transformations.representation.ExtendedConstructionNode;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedInfoGetFields;

/**
 * @author Amnon Lotan
 *
 * @since Jul 10, 2012
 */
public class SpecialRuleApplicationUtils {

	private static final String EXPLATIVE = "there";


	/**
	 *  skip the artificial "ROOT" node
	 * @param tree
	 * @return
	 * @throws AnnotatorException 
	 */
	public static <N extends AbstractNode<? extends Info, N>> N skipRoot(N tree) throws AnnotatorException {
		if (tree == null)
			throw new AnnotatorException("got null tree");
		N nodeToAnnotate;
		if (tree.getInfo().getId().equals(AbstractMiniparParser.ROOT_NODE_ID))
			// skip the artificial "ROOT" node
			if (tree.hasChildren())
				nodeToAnnotate = tree.getChildren().get(0);
			else
				throw new AnnotatorException("got a sentence that is an empty root: " + tree);
		else
			nodeToAnnotate = tree;
		return nodeToAnnotate;
	}

	
	/**
	 * Get the node's argument node (the most obvious one).<br>
	 * With prepositions, there is a need to smooth cases of complex prepositional argument relations, where one or more prepositional
	 * nodes separate the parent from its actual argument node.
	 * I.e. prep+pobj and prep+pcomp are a special case of a 2-chain of relations that must be treated as a unit. E.g. "think of eating".
	 * @param node
	 * @return
	 * @throws AnnotatorException 
	 */
	public static ExtendedConstructionNode skipPrepositionalNodes(ExtendedConstructionNode node) throws AnnotatorException {
		StanfordDepedencyRelationType relation = getRelation(node);
		node = skipPrepositionNodes(node, relation);
		return node;
	}
	
	/**
	 * get the relation from this node to its parent
	 * @param node
	 * @return
	 * @throws AnnotatorException 
	 */
	public static StanfordDepedencyRelationType getRelation(ExtendedConstructionNode node) throws AnnotatorException {
		StanfordDepedencyRelationType relation;
		try {
			relation = StanfordDepedencyRelationType.valueOf(ExtendedInfoGetFields.getRelation(node.getInfo()));
		} catch (Exception e) {
			throw new AnnotatorException("Bug. This node has a dependancy relation that isn't a StanfordDepedencyRelationType: " + node);
		}
		if (relation == null)
			throw new AnnotatorException("Bug. This node has no dependancy relation: " + node);
		
		return relation;
	}
	
	
	/**
	 * Activated by flag {@link TruthTellerConstants#AMMEND_COMPLEMENT_RELATION_IN_EXPLATIVE_PHRASES}.<br>
	 * if this node is the nsubj of a "there is" construction (with a "there/EX/expl" node), replace the relation with dobj 
	 * @param node
	 * @param parent 
	 * @param relation
	 * @return
	 */
	static StanfordDepedencyRelationType ammendRelationInExplativeVPs(ExtendedConstructionNode node, ExtendedConstructionNode parent,
			StanfordDepedencyRelationType relation) 
	{
		if (TruthTellerConstants.AMMEND_COMPLEMENT_RELATION_IN_EXPLATIVE_PHRASES)
		{
			if (StanfordDepedencyRelationType.nsubj.equals(relation))
			{
				if (parent != null && parent.hasChildren())
				{
					boolean isExplative = false;
					for (Iterator<ExtendedConstructionNode> iter = parent.getChildren().iterator(); iter.hasNext() && !isExplative ; )
					{
						ExtendedInfo siblingInfo = iter.next().getInfo();
						if (EXPLATIVE.equalsIgnoreCase(ExtendedInfoGetFields.getLemma(siblingInfo))  && 
							ExtendedInfoGetFields.getPartOfSpeechObject(siblingInfo).getStringRepresentation().equals(PennPosTag.EX.name())	&&
							ExtendedInfoGetFields.getRelation(siblingInfo).equals(StanfordDepedencyRelationType.expl.name()))
							isExplative = true;
					}
					if (isExplative)
						relation = StanfordDepedencyRelationType.dobj;
				}
			}
			
		}
		return relation;
	}

	///////////////////////////////////////////////// PRIVATE	////////////////////////////////////////////////////////////

	/**
	 * prep+pobj, prep+pcomp and others, are a special case of a 2-chain of relations that must be treated atomically. 
	 * Notice its possible to find a path of 2 or more 'prep's. In such cases this method recourses into the path until reaching, validating, and returning, its end.
	 * E.g. "To walk along with somebody".
	 * @param node
	 * @param relation 
	 * @return
	 * @throws AnnotatorException 
	 */
	private static ExtendedConstructionNode skipPrepositionNodes(ExtendedConstructionNode node, StanfordDepedencyRelationType relation) throws AnnotatorException {
		ExtendedConstructionNode ret;
		if (	!relation.equals(StanfordDepedencyRelationType.prep) || 
				!SimplerPosTagConvertor.simplerPos(ExtendedInfoGetFields.getCanonicalPartOfSpeech(node.getInfo())).equals(SimplerCanonicalPosTag.PREPOSITION) || 
				!node.hasChildren())
			// in case this node is not a 'prep', nor a PREPOSITION, or has no children (a parser error, but a benign one), it doesn't get any special prep-skipping 
			// treatment and is returned as is.
			ret = node;
		else				// skip the prep node(s)
		{
			// select the child that has a relation appropriate for 'prep' (preps may have several children), OR, at least, a child that is a predicate
			ret = null;
			for ( Iterator<ExtendedConstructionNode> iter = node.getChildren().iterator(); ret == null && iter.hasNext(); ) 
			{
				ExtendedConstructionNode child = iter.next();
				StanfordDepedencyRelationType relationToChild = getRelation(child);
				if (ComplementRelations.PREP_RELATIONS.contains(relationToChild) ||	ExtendedInfoGetFields.getPredicateSignatureObj(child.getInfo()) != null)
					ret = child;
				else if (relationToChild.equals(StanfordDepedencyRelationType.prep))
					// this is part of a path of 'prep's, so recourse into it 
					ret = skipPrepositionNodes(child, relationToChild);	
			}
			if (ret == null)	// in case node has no predicative children, just return node and continue with the default algorithm
				ret = node;
		}
		return ret;
	}
}
