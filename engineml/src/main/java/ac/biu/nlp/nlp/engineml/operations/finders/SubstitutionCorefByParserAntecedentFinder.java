package ac.biu.nlp.nlp.engineml.operations.finders;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import ac.biu.nlp.nlp.engineml.operations.OperationException;
import ac.biu.nlp.nlp.engineml.operations.specifications.SubstitutionSubtreeSpecification;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.engineml.utilities.preprocess.ParserSpecificConfigurations;
import eu.excitementproject.eop.common.codeannotations.ParserSpecific;
import eu.excitementproject.eop.common.datastructures.SimpleValueSetMap;
import eu.excitementproject.eop.common.datastructures.ValueSetMap;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.representation.basic.StanfordDependencyRelation.StanfordDepedencyRelationType;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNodeUtils;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;


/**
 * This class finds specifications of replacing one subtree by another one, based
 * on the "antecedent" ExtendedInformation given by the parser. 
 * 
 * @author Asher Stern
 * @since February, 2011
 *
 */
public class SubstitutionCorefByParserAntecedentFinder implements Finder<SubstitutionSubtreeSpecification>
{
	public static final String REF_RELATION = StanfordDepedencyRelationType.ref.name();
	
	public SubstitutionCorefByParserAntecedentFinder(TreeAndParentMap<ExtendedInfo,ExtendedNode> textTree)
	{
		super();
		this.textTree = textTree;
	}



	@Override
	public void find() throws OperationException
	{
		specs = new LinkedHashSet<SubstitutionSubtreeSpecification>();
		
		nodesAndTheirAntecedent = new LinkedHashMap<ExtendedNode, ExtendedNode>();
		mapNodeToThoseWhoHaveItAsAntecedent = new SimpleValueSetMap<ExtendedNode, ExtendedNode>();
		Set<ExtendedNode> nodes = AbstractNodeUtils.treeToSet(textTree.getTree());
		for (ExtendedNode node : nodes)
		{
			if (node.getAntecedent()!=null)
			{
				ExtendedNode antecedent = AbstractNodeUtils.getDeepAntecedentOf(node);
				nodesAndTheirAntecedent.put(node,antecedent);
				mapNodeToThoseWhoHaveItAsAntecedent.put(antecedent, node);
			}
		}
		
		for (ExtendedNode nodeThatHasAntecedent : nodesAndTheirAntecedent.keySet())
		{
			specs.add(new SubstitutionSubtreeSpecification(nodeThatHasAntecedent, nodesAndTheirAntecedent.get(nodeThatHasAntecedent)));
		}
		
		if (ParserSpecificConfigurations.getParserMode().equals(ParserSpecificConfigurations.PARSER.EASYFIRST))
		{
			addCopyForRefRelation();
		}
	}
	
	
	
	public Set<SubstitutionSubtreeSpecification> getSpecs() throws OperationException
	{
		if (null==specs) throw new OperationException("find() was not called.");
		return specs;
	}
	
	@ParserSpecific("easyfirst")
	private void addCopyForRefRelation()
	{
		for (ExtendedNode node : textTree.getParentMap().keySet())
		{
			if ( (node.getAntecedent()!=null) && (InfoGetFields.getRelation(node.getInfo()).equals(REF_RELATION)) )
			{
				HashSet<ExtendedNode> alreadyHandeledNodes = new HashSet<ExtendedNode>();
				alreadyHandeledNodes.add(node);
				ExtendedNode parent = textTree.getParentMap().get(node);
				ExtendedNode antecedent = node.getAntecedent();
				while (antecedent!=null)
				{
					if (!alreadyHandeledNodes.contains(antecedent))
					{
						specs.add(new SubstitutionSubtreeSpecification(null,antecedent, parent,findSubtreesToOmit(antecedent,parent,node)));
					}
					alreadyHandeledNodes.add(antecedent);
					if (null==antecedent.getAntecedent())
					{
						if (mapNodeToThoseWhoHaveItAsAntecedent.get(antecedent)!=null)
						{
							for (ExtendedNode otherWhoHasItAsAntecedent : mapNodeToThoseWhoHaveItAsAntecedent.get(antecedent))
							{
								if (!alreadyHandeledNodes.contains(otherWhoHasItAsAntecedent))
								{
									specs.add(new SubstitutionSubtreeSpecification(null, otherWhoHasItAsAntecedent, parent ,findSubtreesToOmit(otherWhoHasItAsAntecedent,parent,node)));
								}
								
								alreadyHandeledNodes.add(otherWhoHasItAsAntecedent);
							}
						}
					}
					antecedent = antecedent.getAntecedent();
				}
			}
		}
	}
	
	/**
	 * If I have a sentence like "The boy, who managed to escape, arrived".
	 * In EasyFirst it looks like that: "manage" is child of "boy" with  rcmod relation.
	 * "manage" has "who" as subject.
	 * When I copy "boy" instead of "who" as the subject of "manage", I don't want to
	 * copy "manage" subtree as well.
	 * This is what this method does:
	 * <BR>
	 * It goes from "who" - which is the subtreeToRemove - upwards until it finds
	 * "boy", and the child of "boy" on the path from "who" to "boy" - which is "manage" -
	 * is returned as subtreeToOmit.
	 * 
	 * @param subtreeToRemove
	 * @param subtreeToAdd
	 * @return
	 */
	private Set<ExtendedNode> findSubtreesToOmit(ExtendedNode subtreeToRemove, ExtendedNode subtreeToAdd, ExtendedNode anywayOmitThis)
	{
		ExtendedNode subtreeToOmit = null;
		Map<ExtendedNode, ExtendedNode> parentMap = textTree.getParentMap();
		ExtendedNode current = subtreeToRemove;
		while ( (parentMap.get(current)!=null) && (subtreeToAdd!=parentMap.get(current)) )
		{
			current = parentMap.get(current);
		}
		
		if (subtreeToAdd==parentMap.get(current))
		{
			subtreeToOmit = current;
		}
		
		HashSet<ExtendedNode> subtressToOmit = new HashSet<ExtendedNode>();
		if (anywayOmitThis!=null) subtressToOmit.add(anywayOmitThis);
		if (subtreeToOmit!=null) subtressToOmit.add(subtreeToOmit);
		return subtressToOmit;
	}



	private TreeAndParentMap<ExtendedInfo,ExtendedNode> textTree;
	
	private Map<ExtendedNode, ExtendedNode> nodesAndTheirAntecedent;
	private ValueSetMap<ExtendedNode, ExtendedNode> mapNodeToThoseWhoHaveItAsAntecedent;
	
	private Set<SubstitutionSubtreeSpecification> specs = null;
	
	
}
