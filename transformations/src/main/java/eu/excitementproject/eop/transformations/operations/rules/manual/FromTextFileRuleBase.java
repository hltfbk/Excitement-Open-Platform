package eu.excitementproject.eop.transformations.operations.rules.manual;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.common.component.syntacticknowledge.RuleWithConfidenceAndDescription;
import eu.excitementproject.eop.common.component.syntacticknowledge.SyntacticRule;
import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.datastructures.SimpleBidirectionalMap;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSetWrapper;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicTreeFormalString;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicTreeFormalString.EnglishTreeFormalStringException;
import eu.excitementproject.eop.transformations.operations.rules.BagOfRulesRuleBase;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseException;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.TreeUtilities;

/**
 * This rule base contains rules that are loaded from a text file that
 * stores those rules as string.
 * <BR>
 * The rule representation in that text file (the text-file structure) is
 * currently not documented.
 * <P>
 * That rule base is used to manually define ad-hoc rules when it is observed
 * that such rules are required but could not be found in any other rule base.
 * <P>
 * The rule base has also a dynamic-mode. In that mode in each call to
 * {@link #getRules()} - the file contents are read again. That mode is good
 * for an interactive GUI, in which the user wants to add rules to the file
 * and see how if they are matched to the text and if they lead to a better proof.
 * However, that mode is very very inefficient in regular run of the system.
 * 
 * @author Asher Stern
 * @since Jun 27, 2011
 *
 */
public class FromTextFileRuleBase implements BagOfRulesRuleBase<Info, BasicNode>
{
	public FromTextFileRuleBase(File rulesFile, boolean dynamicMode)
	{
		super();
		this.rulesFile = rulesFile;
		this.dynamicMode = dynamicMode;
	}



	public ImmutableSet<RuleWithConfidenceAndDescription<Info, BasicNode>> getRules()
			throws RuleBaseException
	{
		if ( (null==this.immutableRulesSet) || (this.dynamicMode) )
		{
			createSetOfRules();
			immutableRulesSet = new ImmutableSetWrapper<RuleWithConfidenceAndDescription<Info,BasicNode>>(this.rulesSet);
			logger.info("manual rules loaded: "+rulesSet.size());
		}
		
		return immutableRulesSet;
	}
	
	private static class RuleStringArrayAndDescription
	{
		public RuleStringArrayAndDescription(String[] stringArray, String description)
		{
			this.stringArray = stringArray;
			this.description = description;
		}
		public String[] getStringArray()
		{
			return stringArray;
		}
		public String getDescription()
		{
			return description;
		}


		private final String[] stringArray;
		private final String description;
	}
	
	
	
	private String[] cleanComments(String[] stringArray)
	{
		Vector<String> retVector = new Vector<String>();
		for (String line : stringArray)
		{
			if (!line.startsWith("#"))
			{
				line = line.trim();
				if (line.length()>0)
					retVector.add(line);
			}
				
		}
		return retVector.toArray(new String[0]);
	}
	
	private List<RuleStringArrayAndDescription> getRulesDeclarations(String[] stringArray)
	{
		stringArray = cleanComments(stringArray);
		List<RuleStringArrayAndDescription> ret = new LinkedList<RuleStringArrayAndDescription>();

		String ruleDescription = "no description available";
		int index=0;
		while (index<stringArray.length)
		{
			String nextRuleDescription=null;
			Vector<String> vectorForSingleRule = new Vector<String>();
			while ( (index<stringArray.length) && (!stringArray[index].startsWith(";")) )
			{
				vectorForSingleRule.add(stringArray[index]);
				++index;
			}
			while ( (index<stringArray.length) && (stringArray[index].startsWith(";")) )
			{
				nextRuleDescription = stringArray[index];
				++index;
			}
			if (vectorForSingleRule.size()>0)
			{
				String[] arrayForSingleRule = vectorForSingleRule.toArray(new String[0]);
				ret.add(new RuleStringArrayAndDescription(arrayForSingleRule,ruleDescription));
			}
			ruleDescription = nextRuleDescription;
		}
		return ret;
	}

	private String[] readFile(File file) throws IOException
	{
		String[] ret;
		Vector<String> retVector = new Vector<String>();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		try
		{
			String line = reader.readLine();
			while (line != null)
			{
				retVector.add(line);
				line = reader.readLine();
			}
		}
		finally
		{
			reader.close();
		}
		ret = retVector.toArray(new String[0]);
		return ret;
	}
	
	private void addRule(String[] rule, String description) throws RuleBaseException, EnglishTreeFormalStringException, TeEngineMlException
	{
		if (rule.length<(1+1))
			throw new RuleBaseException("Malformed rule: "+fromStringArray(rule));
		int index=0;
		
		
		String lhsString = rule[index];
		++index;
		String rhsString = rule[index];
		++index;
		
		BasicTreeFormalString etfs = new BasicTreeFormalString(lhsString,1);
		BasicNode lhs = etfs.getTree();
		etfs = new BasicTreeFormalString(rhsString,1);
		BasicNode rhs = etfs.getTree();
		Map<String,BasicNode> lhsIdMap = TreeUtilities.mapIdToNode(lhs);
		Map<String,BasicNode> rhsIdMap = TreeUtilities.mapIdToNode(rhs);
		
		Map<String,BasicNode> lhsVarIdMap = TreeUtilities.mapVarIdToNode(lhs);
		Map<String,BasicNode> rhsVarIdMap = TreeUtilities.mapVarIdToNode(rhs);
		
		
		
		BidirectionalMap<BasicNode, BasicNode> ruleMapping = new SimpleBidirectionalMap<BasicNode, BasicNode>();
		
		while (index<rule.length)
		{
			String mapLine = rule[index];
			String[] mapping = mapLine.split(":");
			if (mapping.length!=2)throw new RuleBaseException("Malformed:\n"+fromStringArray(rule));
			BasicNode leftNode = null;
			BasicNode rightNode = null;
			if (mapping[0].startsWith("*"))
			{
				leftNode = lhsVarIdMap.get(mapping[0].substring(1));
			}
			else
			{
				leftNode = lhsIdMap.get(mapping[0]);
			}
			if (mapping[1].startsWith("*"))
			{
				rightNode = rhsVarIdMap.get(mapping[1].substring(1));
			}
			else
			{
				rightNode = rhsIdMap.get(mapping[1]);
			}
			if ( (leftNode==null) || (rightNode==null) )throw new RuleBaseException("Malformed:\n"+fromStringArray(rule));
			ruleMapping.put(leftNode,rightNode);
			++index;
		}
		
		RuleWithConfidenceAndDescription<Info, BasicNode> rwcad =
			new RuleWithConfidenceAndDescription<Info, BasicNode>(new SyntacticRule<Info,BasicNode>(lhs,rhs,ruleMapping),EMINUS1,"manual: "+description);
		
		rulesSet.add(rwcad);
	}
	
	private void createSetOfRules() throws RuleBaseException
	{
		try
		{
			String[] allLines = readFile(rulesFile);
			List<RuleStringArrayAndDescription> rulesAsList = getRulesDeclarations(allLines);
			
			rulesSet = new LinkedHashSet<RuleWithConfidenceAndDescription<Info,BasicNode>>();
			for (RuleStringArrayAndDescription ruleStrings : rulesAsList)
			{
				addRule(ruleStrings.getStringArray(),ruleStrings.getDescription());
			}
		}
		catch(TeEngineMlException e)
		{
			throw new RuleBaseException("See nested",e);
		}
		catch (IOException e)
		{
			throw new RuleBaseException("See nested",e);
		}
		catch (EnglishTreeFormalStringException e)
		{
			throw new RuleBaseException("See nested",e);
		}
	}
	
	private static String fromStringArray(String[] stringArray)
	{
		StringBuffer sb = new StringBuffer();
		for (String str : stringArray)
		{
			sb.append(str);
			sb.append("\n");
		}
		return sb.toString();
	}
	
	
	private File rulesFile;
	
	private Set<RuleWithConfidenceAndDescription<Info, BasicNode>> rulesSet = null;
	private ImmutableSetWrapper<RuleWithConfidenceAndDescription<Info,BasicNode>> immutableRulesSet = null;
	boolean dynamicMode = true;
	
	private static final double EMINUS1 = Math.exp(-1);
	
	private static final Logger logger = Logger.getLogger(FromTextFileRuleBase.class);
}
