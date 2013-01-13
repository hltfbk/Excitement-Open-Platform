package ac.biu.nlp.nlp.engineml.utilities.parsetreeutils;

import java.util.Map;

import eu.excitementproject.eop.common.representation.partofspeech.CanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;

import ac.biu.nlp.nlp.engineml.alignment.AlignmentCalculator;
import ac.biu.nlp.nlp.engineml.alignment.AlignmentCriteria;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.engineml.rteflow.systems.Constants;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.InfoGetFields;

/**
 * Contains criteria about equalities of two nodes or two edges.
 * The methods here take as arguments two nodes or two edges, and decide whether those
 * two nodes / edges are equal or not (i.e. whether they store the same information).
 * <P>
 * See also {@link AlignmentCriteria} which uses this class (depends on {@link Constants} flags).
 * <P>
 * <B>
 * NOTE: !!!!!!!!!!!<BR>
 * The equality between two nodes that takes into account also the lowest ancestor that is yet
 * a content word - is implemented in {@link AdvancedEqualities}.
 * See also {@link Constants#USE_ADVANCED_EQUALITIES}
 * </B>
 *
 * @see AdvancedEqualities
 * @see AlignmentCriteria
 * @see AlignmentCalculator
 * 
 * @author Asher Stern
 * 
 *
 */
public class Equalities
{
	public static boolean areEqualNodes(Info info1, Info info2)
	{
		if ((info1==null) && (info2==null))return true;
		else if ((info1==null) || (info2==null))return false;
		
		String lemma1 = InfoGetFields.getLemma(info1);
		String lemma2 = InfoGetFields.getLemma(info2);
		
		CanonicalPosTag canonicalPos1 = InfoGetFields.getPartOfSpeechObject(info1).getCanonicalPosTag();
		CanonicalPosTag canonicalPos2 = InfoGetFields.getPartOfSpeechObject(info2).getCanonicalPosTag();
		if ( (lemma1.equalsIgnoreCase(lemma2)) && (canonicalPos1.equals(canonicalPos2)))
			return true;
		else
			return false;
	}
	
	public static boolean areEqualRelations(ExtendedNode node1, ExtendedNode node2, Map<ExtendedNode, ExtendedNode> parentMap1,Map<ExtendedNode, ExtendedNode> parentMap2)
	{
		ExtendedNode parent1 = parentMap1.get(node1);
		ExtendedNode parent2 = parentMap2.get(node2);
		if ((parent1==null)&&(parent2==null))
		{
			if (areEqualNodes(node1.getInfo(), node2.getInfo()))return true;
			else return false;
		}
		if ((parent1==null)||(parent2==null)) return false;

		if (areEqualNodes(node1.getInfo(), node2.getInfo())&&areEqualNodes(parent1.getInfo(), parent2.getInfo()))
		{
			Info info1 = node1.getInfo();
			Info info2 = node2.getInfo();
			if ((info1==null) && (info2==null))return true;
			else if ((info1==null) || (info2==null))return false;

			String relation1 = InfoGetFields.getRelation(info1);
			String relation2 = InfoGetFields.getRelation(info2);
			if (relation1.equalsIgnoreCase(relation2))
				return true;
			else
				return false;
		}
		else
			return false;
	}
	
	public static final boolean lemmasEqual(String textLemma, String hypothesisLemma)
	{
		return textLemma.equalsIgnoreCase(hypothesisLemma);
	}
	
	public static final boolean posEqual(PartOfSpeech textPos, PartOfSpeech hypothesisPos)
	{
		if (textPos==hypothesisPos)return true;
		CanonicalPosTag canonicalText = (null==textPos)?CanonicalPosTag.OTHER:textPos.getCanonicalPosTag();
		CanonicalPosTag canonicalhypothesis = (null==hypothesisPos)?CanonicalPosTag.OTHER:hypothesisPos.getCanonicalPosTag();
		
		if (canonicalText.equals(canonicalhypothesis))
			return true;
		else
			return false;
	}

	
	

}
