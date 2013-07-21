package eu.excitementproject.eop.transformations.operations.rules.distsim;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.common.codeannotations.LanguageDependent;
import eu.excitementproject.eop.common.codeannotations.ParserSpecific;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultEdgeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultNodeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultSyntacticInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DependencyRelation;
import eu.excitementproject.eop.common.representation.parse.representation.basic.EdgeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.representation.basic.NodeInfo;
import eu.excitementproject.eop.common.representation.parse.tree.TreeIterator;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.partofspeech.MiniparPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.transformations.utilities.ParserSpecificConfigurations;
import eu.excitementproject.eop.transformations.utilities.ParserSpecificConfigurations.PARSER;


/**
 * TODO (Comment by Asher Stern:)
 * This class's code is quite bad. Should be reprogrammed.
 * 
 * @author Asher Stern
 *
 */
@LanguageDependent("English")
@ParserSpecific({"minipar","easyfirst"})
public class TemplateToTree
{
	public static final String DEFAULT_RELATION = "mod";
	public static final String DEFAULT_RELATION_TO_PREP = "pcomp-n";
	public static final String DEFAULT_ID = "rule";
	public static final String SPLIT_RIGHT = ">";
	public static final String SPLIT_LEFT = "<";
	public static final String SPLIT_NODE_COMPONENTS = ":";
	public static final String PREP_POS = "Prep";
	public static final Set<String> PREP_LIST;


	public static Map<String, String> POS_MAP;
	static
	{
		POS_MAP = new LinkedHashMap<String, String>();
		POS_MAP.put("p","Prep");
		PREP_LIST = new LinkedHashSet<String>();
		PREP_LIST.add("about");
		PREP_LIST.add("above");
		PREP_LIST.add("across");
		PREP_LIST.add("after");
		PREP_LIST.add("along");
		PREP_LIST.add("around");
		PREP_LIST.add("aside");
		PREP_LIST.add("at");
		PREP_LIST.add("before");
		PREP_LIST.add("behind");
		PREP_LIST.add("below");
		PREP_LIST.add("between");
		PREP_LIST.add("beyond");
		PREP_LIST.add("by");
		PREP_LIST.add("down");
		PREP_LIST.add("during");
		PREP_LIST.add("for");
		PREP_LIST.add("from");
		PREP_LIST.add("in");
		PREP_LIST.add("inside");
		PREP_LIST.add("into");
		PREP_LIST.add("near");
		PREP_LIST.add("next");
		PREP_LIST.add("of");
		PREP_LIST.add("off");
		PREP_LIST.add("on");
		PREP_LIST.add("onto");
		PREP_LIST.add("out");
		PREP_LIST.add("over");
		PREP_LIST.add("through");
		PREP_LIST.add("to");
		PREP_LIST.add("toward");
		PREP_LIST.add("under");
		PREP_LIST.add("up");
		PREP_LIST.add("upon");
		PREP_LIST.add("with");
		PREP_LIST.add("within");
		PREP_LIST.add("without");
	}


	public TemplateToTree(String template)
	{
		super();
		this.template = template;
	}

	public void createTree() throws TemplateToTreeException
	{
		try
		{
			privateCreateTree();
			setLeaves();
		}
		catch (UnsupportedPosTagStringException e)
		{
			throw new TemplateToTreeException("pos-tag problem. Currently only Minipar is supported.",e);
		}
		catch (RuntimeException e)
		{
			throw new TemplateToTreeException("bad Template: "+template+". See nested exception",e);

		}
	}

	public BasicNode getTree() throws TemplateToTreeException
	{
		if (null==this.tree) throw new TemplateToTreeException("null tree");
		return tree;
	}

	public String getTemplate()
	{
		return template;
	}

	public BasicNode getLeftVariableNode() throws TemplateToTreeException
	{
		if (null==this.tree) throw new TemplateToTreeException("null tree");
		return leftVariableNode;
	}

	public BasicNode getRightVariableNode() throws TemplateToTreeException
	{
		if (null==this.tree) throw new TemplateToTreeException("null tree");
		return rightVariableNode;
	}
	
	///////////////////////////// PRIVATE ///////////////////////////////////

	private void privateCreateTree() throws TemplateToTreeException, UnsupportedPosTagStringException
	{
		String[] leftPart = template.split(SPLIT_LEFT);
		String lastLeftPart = leftPart[leftPart.length-1];
		String[] rightPart = lastLeftPart.split(SPLIT_RIGHT);

		if ( (leftPart.length>1) && (rightPart.length>1) )
		{
			tree = createSubTreeOfRight(emptyEdgeInfo(),rightPart,0);
			int leftPartIndex = leftPart.length-1-1;
			EdgeInfo leftChildEdgeInfo = null;
			if (isNode(leftPart[leftPartIndex]))
			{
				leftChildEdgeInfo = new DefaultEdgeInfo(new DependencyRelation(DEFAULT_RELATION, null));
			}
			else
			{
				leftChildEdgeInfo = fromStringEdgeInfo(leftPart[leftPartIndex]);
				--leftPartIndex;
			}
			if (!isNode(leftPart[leftPartIndex]))
			{
				NodeInfo leftChildNodeInfo = fromStringVariable(leftPart[leftPartIndex],true);
				tree.addChild(new BasicNode(new DefaultInfo(DEFAULT_ID, leftChildNodeInfo, leftChildEdgeInfo)));
			}
			else
			{
				tree.addChild(createSubTreeOfLeft(leftChildEdgeInfo,leftPart,leftPartIndex));
			}
		}
		else if (leftPart.length>1)
		{
			int index = leftPart.length-1;
			if (isNode(leftPart[index]))
			{
				tree = createSubTreeOfLeft(emptyEdgeInfo(), leftPart, index);
			}
			else
			{
				NodeInfo treeNodeInfo = fromStringVariable(leftPart[index],false);
				tree = new BasicNode(new DefaultInfo(DEFAULT_ID, treeNodeInfo, emptyEdgeInfo()));
				index--;
				EdgeInfo childEdgeInfo = null;
				if (isNode(leftPart[index]))
				{
					childEdgeInfo = new DefaultEdgeInfo(new DependencyRelation(DEFAULT_RELATION, null));
				}
				else
				{
					childEdgeInfo = fromStringEdgeInfo(leftPart[index]);
					--index;
				}
				if (!isNode(leftPart[index]))
				{
					NodeInfo leafNodeInfo = fromStringVariable(leftPart[index],true);
					tree.addChild(new BasicNode(new DefaultInfo(DEFAULT_ID, leafNodeInfo, childEdgeInfo)));
				}
				else
				{
					tree.addChild(createSubTreeOfLeft(childEdgeInfo, leftPart, index));
				}
			}
		}
		else if (rightPart.length>1)
		{
			int index=0;
			if (isNode(rightPart[index]))
			{
				tree = createSubTreeOfRight(emptyEdgeInfo(), rightPart, index);
			}
			else
			{
				NodeInfo treeNodeInfo = fromStringVariable(rightPart[index],true);
				tree = new BasicNode(new DefaultInfo(DEFAULT_ID, treeNodeInfo, emptyEdgeInfo()));
				++index;
				EdgeInfo childEdgeInfo = null;
				if (isNode(rightPart[index]))
				{
					childEdgeInfo = new DefaultEdgeInfo(new DependencyRelation(DEFAULT_RELATION, null));
				}
				else
				{
					childEdgeInfo = fromStringEdgeInfo(rightPart[index]);
					++index;
				}
				if (!isNode(rightPart[index]))
				{
					NodeInfo childNodeInfo = fromStringVariable(rightPart[index],false);
					tree.addChild(new BasicNode(new DefaultInfo(DEFAULT_ID, childNodeInfo, childEdgeInfo)));
				}
				else
				{
					tree.addChild(createSubTreeOfRight(childEdgeInfo, rightPart, index));
				}
			}

		}
		else
			throw new TemplateToTreeException("Empty template: "+template);
	}



	private BasicNode createSubTreeOfRight(EdgeInfo relationToParent, String[] rightPart, int index) throws UnsupportedPosTagStringException
	{
		BasicNode ret = createNode(relationToParent, rightPart[index]);
		++index;
		if (index<rightPart.length)
		{
			if ((rightPart.length-1)==index)
			{
				EdgeInfo edgeInfoForChild = new DefaultEdgeInfo(new DependencyRelation(DEFAULT_RELATION_TO_PREP, null));
				if (!isNode(rightPart[index]))
				{
					NodeInfo nodeInfoForChild = fromStringVariable(rightPart[index],false);
					ret.addChild(new BasicNode(new DefaultInfo(DEFAULT_ID, nodeInfoForChild, edgeInfoForChild)));
				}
				else
				{
					ret.addChild(createSubTreeOfRight(edgeInfoForChild, rightPart, index));
				}
			}
			else
			{
				EdgeInfo edgeInfoForChild = null;
				if (isNode(rightPart[index]))
				{
					String childRelation = null;
					if (InfoGetFields.getPartOfSpeech(ret.getInfo()).equals(PREP_POS))
						childRelation = DEFAULT_RELATION_TO_PREP;
					else
						childRelation = DEFAULT_RELATION;
					edgeInfoForChild = new DefaultEdgeInfo(new DependencyRelation(childRelation, null));
				}
				else
				{
					edgeInfoForChild = fromStringEdgeInfo(rightPart[index]);
					++index;
				}
				if (!isNode(rightPart[index]))
				{
					NodeInfo childNodeInfo = fromStringVariable(rightPart[index],false);
					ret.addChild(new BasicNode(new DefaultInfo(DEFAULT_ID, childNodeInfo, edgeInfoForChild)));
				}
				else
				{
					BasicNode child = createSubTreeOfRight(edgeInfoForChild,rightPart,index);
					ret.addChild(child);
				}
			}
		}
		return ret;
	}

	private BasicNode createSubTreeOfLeft(EdgeInfo relation, String[] leftPart, int index) throws UnsupportedPosTagStringException
	{		
		BasicNode ret = createNode(relation, leftPart[index]);
		--index;
		if (0==index)
		{
			EdgeInfo childEdgeInfo = new DefaultEdgeInfo(new DependencyRelation(DEFAULT_RELATION_TO_PREP, null));
			if (!isNode(leftPart[index]))
			{
				NodeInfo childNodeInfo = fromStringVariable(leftPart[index],true);
				ret.addChild(new BasicNode(new DefaultInfo(DEFAULT_ID, childNodeInfo, childEdgeInfo)));
			}
			else
			{
				ret.addChild(createSubTreeOfLeft(childEdgeInfo, leftPart, index));
			}
		}
		else
		{
			EdgeInfo childEdgeInfo = null;
			if (isNode(leftPart[index]))
			{
				String childRelation = null;
				if (InfoGetFields.getPartOfSpeech(ret.getInfo()).equals(PREP_POS))
					childRelation = DEFAULT_RELATION_TO_PREP;
				else
					childRelation = DEFAULT_RELATION;

				childEdgeInfo = new DefaultEdgeInfo(new DependencyRelation(childRelation, null));
			}
			else
			{
				childEdgeInfo = fromStringEdgeInfo(leftPart[index]);
				--index;
			}
			if (!isNode(leftPart[index]))
			{
				NodeInfo childNodeInfo = fromStringVariable(leftPart[index],true);
				ret.addChild(new BasicNode(new DefaultInfo(DEFAULT_ID, childNodeInfo, childEdgeInfo)));
			}
			else
			{
				ret.addChild(createSubTreeOfLeft(childEdgeInfo,leftPart,index));
			}

		}
		return ret;
	}


	private void setLeaves()
	{
		for (BasicNode node : TreeIterator.iterableTree(tree))
		{
			if (InfoGetFields.isVariable(node.getInfo()))
			{
				Integer varId = node.getInfo().getNodeInfo().getVariableId();
				if (this.mapVariableIdToLeftRight.get(varId).booleanValue())
				{
					this.leftVariableNode = node;
				}
				else
				{
					this.rightVariableNode = node;
				}
			}
		}
	}


	private boolean isNode(String str)
	{
		return (str.split(SPLIT_NODE_COMPONENTS).length>1);
	}

	private NodeInfo fromStringNonVariable(String str) throws UnsupportedPosTagStringException
	{
		String[] components = str.split(SPLIT_NODE_COMPONENTS);
		String pos = posOf(components[0]); 
		String lemma = components[1];
		return new DefaultNodeInfo(lemma, lemma, 0, null, new DefaultSyntacticInfo(new MiniparPartOfSpeech(pos)));
	}

	private EdgeInfo fromStringEdgeInfo(String str)
	{
		return new DefaultEdgeInfo(new DependencyRelation(str, null));
	}

	private NodeInfo fromStringVariable(String str,boolean left) throws UnsupportedPosTagStringException
	{
		mapVariableIdToLeftRight.put(nextVariableId, left);
		NodeInfo ret = DefaultNodeInfo.newVariableDefaultNodeInfo(nextVariableId, new DefaultSyntacticInfo(new MiniparPartOfSpeech(str.toUpperCase())));
		++nextVariableId;
		return ret;
	}

	private EdgeInfo emptyEdgeInfo()
	{
		return new DefaultEdgeInfo(new DependencyRelation("", null));
	}

	private String posOf(String templatePos)
	{
		if (POS_MAP.containsKey(templatePos))
			return POS_MAP.get(templatePos);
		else
			return templatePos.toUpperCase();
	}

	private BasicNode createNode(EdgeInfo relationToParent, String info) throws UnsupportedPosTagStringException {

		String[] components = info.split(SPLIT_NODE_COMPONENTS);
		String pos = posOf(components[0]); 
		String lemma = components[1];
		String[] lemmaComponents = lemma.split("\\s+");
		BasicNode ret=null;
		if (ParserSpecificConfigurations.getParserMode()==PARSER.MINIPAR) {
			NodeInfo nodeInfo = fromStringNonVariable(info);
			ret = new BasicNode(new DefaultInfo(DEFAULT_ID, nodeInfo, relationToParent));
		}
		else { //MINIPAR TO EASYFIRST CONVERSION
			if(lemmaComponents.length==2 && pos.equalsIgnoreCase("v") && PREP_LIST.contains(lemmaComponents[1])) { //particles - "take over", "pick up", etc.

				NodeInfo verbNodeInfo = new DefaultNodeInfo(lemmaComponents[0], lemmaComponents[0], 0, null, new DefaultSyntacticInfo(new MiniparPartOfSpeech(pos)));
				ret = new BasicNode(new DefaultInfo(DEFAULT_ID, verbNodeInfo, relationToParent));

				NodeInfo rpNodeInfo = new DefaultNodeInfo(lemmaComponents[1], lemmaComponents[1], 0, null, new DefaultSyntacticInfo(new MiniparPartOfSpeech("prep")));
				BasicNode child = new BasicNode(new DefaultInfo(DEFAULT_ID, rpNodeInfo, new DefaultEdgeInfo(new DependencyRelation("prt", null)))); 
				ret.addChild(child);
			}
			else if(lemmaComponents.length==2 && pos.equalsIgnoreCase("n")) { //noun compounds - "oil field"
				NodeInfo parentNodeInfo = new DefaultNodeInfo(lemmaComponents[1], lemmaComponents[1], 0, null, new DefaultSyntacticInfo(new MiniparPartOfSpeech(pos)));
				ret = new BasicNode(new DefaultInfo(DEFAULT_ID, parentNodeInfo, relationToParent));

				NodeInfo childNodeInfo = new DefaultNodeInfo(lemmaComponents[0], lemmaComponents[0], 0, null, new DefaultSyntacticInfo(new MiniparPartOfSpeech(pos)));
				BasicNode child = new BasicNode(new DefaultInfo(DEFAULT_ID, childNodeInfo, new DefaultEdgeInfo(new DependencyRelation("nn", null)))); 
				ret.addChild(child);
			}
			else {
				NodeInfo nodeInfo = fromStringNonVariable(info);
				ret = new BasicNode(new DefaultInfo(DEFAULT_ID, nodeInfo, relationToParent));
			}
		}
		return ret;
	}

	private String template;
	private BasicNode tree = null;
	private BasicNode leftVariableNode = null;
	private BasicNode rightVariableNode = null;
	private Map<Integer, Boolean> mapVariableIdToLeftRight = new LinkedHashMap<Integer, Boolean>();
	private int nextVariableId = 0;



}
