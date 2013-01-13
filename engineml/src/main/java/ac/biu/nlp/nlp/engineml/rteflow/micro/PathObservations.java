package ac.biu.nlp.nlp.engineml.rteflow.micro;
import static ac.biu.nlp.nlp.engineml.utilities.preprocess.ParserSpecificConfigurations.getParserMode;

import java.util.HashSet;
import java.util.Set;

import ac.biu.nlp.nlp.engineml.operations.specifications.MoveNodeSpecification;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.engineml.rteflow.macro.FeatureUpdate;
import ac.biu.nlp.nlp.engineml.utilities.InfoObservations;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;
import ac.biu.nlp.nlp.engineml.utilities.parsetreeutils.PathInTree;
import ac.biu.nlp.nlp.engineml.utilities.preprocess.ParserSpecificConfigurations.PARSER;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.InfoGetFields;
import ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.codeannotations.ParserSpecific;

/**
 * This class contains several static methods that give information about a path
 * in a tree. That information is used to decide about a "move" operation
 * to which feature it should be mapped (i.e. the {@link FeatureUpdate} class
 * uses this class to decide how to change the feature vector for a given
 * "move" operation).
 * 
 * Currently programmed for Minipar and EasyFirst. Note that the default is
 * EasyFirst. If you want to change to work on minipar, the field {@link #MODE_PARSER}
 * should be changed via the static method {@link #changeParser(PARSER)}.
 * 
 * <B> !!!!! TODO !!!!! </B> (comment by Asher Stern)
 * TODO: This class contains hard-coded strings!
 * <BR>
 * TODO: This class is inadequate from SW point of view.
 * <BR>
 * 
 * 
 * 
 * @author Asher Stern
 * @since Jan 13, 2011
 *
 */
@ParserSpecific({"minipar","easyfirst"})
public class PathObservations
{
	public static final Set<String> EASY_FIRST_LOGICAL_SUBJECT_RELATIONS;
	public static final Set<String> EASY_FIRST_LOGICAL_OBJECT_RELATIONS;
	static
	{
		EASY_FIRST_LOGICAL_SUBJECT_RELATIONS = new HashSet<String>();
		EASY_FIRST_LOGICAL_SUBJECT_RELATIONS.add("csubj");
		EASY_FIRST_LOGICAL_SUBJECT_RELATIONS.add("nsubj");
		EASY_FIRST_LOGICAL_SUBJECT_RELATIONS.add("xsubj");
		EASY_FIRST_LOGICAL_OBJECT_RELATIONS = new HashSet<String>();
		EASY_FIRST_LOGICAL_OBJECT_RELATIONS.add("csubjpass");
		EASY_FIRST_LOGICAL_OBJECT_RELATIONS.add("nsubjpass");
		EASY_FIRST_LOGICAL_OBJECT_RELATIONS.add("dobj");
		EASY_FIRST_LOGICAL_OBJECT_RELATIONS.add("iobj");
	}

	
	
	public static boolean pathCrossContentVerb(PathInTree path)
	{
		boolean ret = false;
//		if (
//			(path.getLeastCommonAncestor()!=path.getFrom())
//			&&
//			(path.getLeastCommonAncestor()!=path.getTo())
//			)
//		{
//			if (InfoObservations.infoIsContentVerb(path.getLeastCommonAncestor().getInfo()))
//				ret = true;
//		}
		if (!ret)
		{
			for (ExtendedNode node : path.getUpNodes())
			{
				if (InfoObservations.infoIsContentVerb(node.getInfo()))
				{
					ret = true;
					break;
				}
			}
		}
		if (!ret)
		{
			for (ExtendedNode node : path.getDownNodes())
			{
				if (InfoObservations.infoIsContentVerb(node.getInfo()))
				{
					ret = true;
					break;
				}
			}
		}
		if (!ret)
		{
			if (path.getTo()!=path.getLeastCommonAncestor())
			{
				if (InfoObservations.infoIsContentVerb(path.getTo().getInfo()))
					ret = true;
			}
		}
		
		return ret;
	}
	
	
	/**
	 * Returns <tt>true</tt> if the root is a verb, and the path is up and down, and
	 * the relation to the subtree that the node was in originally is strongly different than
	 * the relation to the subtree that the node is going to be part of now.
	 * <BR>
	 * "Strongly different" means subject -> object and vice versa. "s" to subject or
	 * object is also considered as strong.  
	 * @param path
	 * @return
	 * @throws TeEngineMlException 
	 */
	public static boolean pathStrongChangeRelationToRootVerb(PathInTree path) throws TeEngineMlException
	{
		boolean ret = false;
		if (
			(path.getFrom()==path.getLeastCommonAncestor())
			||
			(path.getTo()==path.getLeastCommonAncestor())
			)
		{
			ret = false; // only if path is up and down
		}
		else
		{
			if (InfoObservations.infoIsContentVerb(path.getLeastCommonAncestor().getInfo()))
			{
				ExtendedNode rootChildUp = null;
				if (path.getUpNodes().size()>0)
				{
					rootChildUp = path.getUpNodes().get(path.getUpNodes().size()-1);
				}
				else
				{
					rootChildUp = path.getFrom();
				}
				
				ExtendedNode rootChildDown = null;
				if (path.getDownNodes().size()>0)
				{
					rootChildDown = path.getDownNodes().get(0);
				}
				else
				{
					rootChildDown = path.getTo();
				}
				
				ret = relationStrongChange(InfoGetFields.getRelation(rootChildUp.getInfo()),InfoGetFields.getRelation(rootChildDown.getInfo()));
			}
		}
		return ret;
	}
	
	
	public static boolean pathOnlyChangeRelation(PathInTree path, TreeAndParentMap<ExtendedInfo,ExtendedNode> tree)
	{
		if (path.getTo()==tree.getParentMap().get(path.getFrom()))
			return true;
		else
			return false;
	}
	
	
	
	public static boolean relationIsSubject(String relation) throws TeEngineMlException
	{
		
		if (getParserMode()==PARSER.EASYFIRST)
			return relationIsSubjectEasyFirst(relation);
		else if (getParserMode()==PARSER.MINIPAR)
			return relationIsSubjectMinipar(relation);
		else
			throw new TeEngineMlException("BUG");
		
	}
	
	public static boolean relationIsObject(String relation) throws TeEngineMlException
	{
		if (getParserMode()==PARSER.EASYFIRST)
			return relationIsObjectEasyFirst(relation);
		else if (getParserMode()==PARSER.MINIPAR)
			return relationIsObjectMinipar(relation);
		else
			throw new TeEngineMlException("BUG");
	}

	
	public static boolean moveChangeRelationStrong(MoveNodeSpecification spec, PathInTree path, TreeAndParentMap<ExtendedInfo,ExtendedNode> tree) throws TeEngineMlException
	{
		boolean ret = false;
		if (pathOnlyChangeRelation(path, tree))
		{
			String originalRelation = InfoGetFields.getRelation(spec.getTextNodeToMove().getInfo());
			String destinationRelation = InfoGetFields.getRelation(spec.getNewEdgeInfo());
			ret = relationStrongChange(originalRelation,destinationRelation);
		}
		return ret;
	}
	
	public static boolean relationStrongChange(String originalRelation, String destinationRelation) throws TeEngineMlException
	{
		boolean ret = false;
		if ((getParserMode()==PARSER.MINIPAR)&&(originalRelation.equalsIgnoreCase("s")))
		{
			if ( (relationIsSubject(destinationRelation)) || (relationIsObject(destinationRelation)) )
				ret = true;
		}
		else
		{
			if (
				( (relationIsSubject(originalRelation)) && (relationIsObject(destinationRelation)) )
				||
				( (relationIsObject(originalRelation)) && (relationIsSubject(destinationRelation)) )
				)
				ret = true;
		}
		
		return ret;
	}
	
	public static boolean introduceOnlySurfaceRelation(MoveNodeSpecification spec)
	{
		if (getParserMode()==PARSER.MINIPAR)
		{
			if (InfoGetFields.getRelation(spec.getNewEdgeInfo()).equalsIgnoreCase("s"))
				return true;
		}
		
		return false;
	}


	/////////////////////// PRIVATE /////////////////////////
	
	// TODO (comment by Asher Stern):
	// All of this paradigm, of hard coded strings and methods specific
	// for parsers is inadequate from SW point of view, and should be
	// replaced by a better mechanism.
	// See also comment at the beginning of this class.
	
	private static boolean relationIsSubjectEasyFirst(String relation)
	{
		return (EASY_FIRST_LOGICAL_SUBJECT_RELATIONS.contains(relation));
	}
	private static boolean relationIsSubjectMinipar(String relation)
	{
		if (relation.toLowerCase().contains("subj"))
			return true;
		else
			return false;
	}

	private static boolean relationIsObjectEasyFirst(String relation)
	{
		return (EASY_FIRST_LOGICAL_OBJECT_RELATIONS.contains(relation));
	}
	private static boolean relationIsObjectMinipar(String relation)
	{
		boolean ret = false;
		if (relation.toLowerCase().contains("obj"))
			ret = true;
		if (false==ret)
		{
			if (relation.equalsIgnoreCase("i"))
				ret = true;
		}
		return ret;
	}
}
