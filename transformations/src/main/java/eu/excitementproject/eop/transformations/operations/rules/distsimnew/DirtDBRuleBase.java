package eu.excitementproject.eop.transformations.operations.rules.distsimnew;
import static eu.excitementproject.eop.transformations.utilities.TransformationsConfigurationParametersNames.DB_DRIVER;
import static eu.excitementproject.eop.transformations.utilities.TransformationsConfigurationParametersNames.DB_URL;
import static eu.excitementproject.eop.transformations.utilities.TransformationsConfigurationParametersNames.LIMIT_NUMBER_OF_RULES;
import static eu.excitementproject.eop.transformations.utilities.TransformationsConfigurationParametersNames.RULES_TABLE_NAME;
import static eu.excitementproject.eop.transformations.utilities.TransformationsConfigurationParametersNames.TEMPLATES_TABLE_NAME;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.common.codeannotations.NotThreadSafe;
import eu.excitementproject.eop.common.component.syntacticknowledge.RuleWithConfidenceAndDescription;
import eu.excitementproject.eop.common.component.syntacticknowledge.SyntacticRule;
import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.datastructures.DummySet;
import eu.excitementproject.eop.common.datastructures.SimpleBidirectionalMap;
import eu.excitementproject.eop.common.datastructures.ValueSetMap;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSetWrapper;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.utilities.Cache;
import eu.excitementproject.eop.common.utilities.CacheFactory;
import eu.excitementproject.eop.common.utilities.StringUtil;
import eu.excitementproject.eop.common.utilities.Utils;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.core.component.syntacticknowledge.utilities.PARSER;
import eu.excitementproject.eop.core.component.syntacticknowledge.utilities.TemplateToTree;
import eu.excitementproject.eop.core.component.syntacticknowledge.utilities.TemplateToTreeException;
import eu.excitementproject.eop.transformations.operations.rules.RuleBase;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseException;
import eu.excitementproject.eop.transformations.operations.rules.distsim.DistSimParameters;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.Constants;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;
import eu.excitementproject.eop.transformations.utilities.TransformationsConfigurationParametersNames;


/**
 * A rule base for lexical-syntactic rules that have the DIRT format, and
 * stored physically in a data-base.
 * <P>
 * <B>Not thread safe:</B> It is unsafe to concurrently call methods of an instance of this class from two different threads.
 * 
 * @author Asher Stern
 * @since Aug 2, 2011
 *
 */
@NotThreadSafe
public class DirtDBRuleBase implements RuleBase<Info,BasicNode>
{
	/**
	 * Creates the rule-base using the given {@link ConfigurationParams}.
	 * @param ruleBaseName
	 * @param params
	 * @return
	 * @throws RuleBaseException
	 */
	public static DirtDBRuleBase fromConfigurationParams(String ruleBaseName,ConfigurationParams params, final PARSER parser) throws RuleBaseException
	{
		try
		{
			// Load from ser file - means that the data-base is stored in a set file, so we do not query the data-base at all.
			// This option is NOT RECOMMENDED, and NOT IN USE!
			boolean loadFromSerFile = false;
			loadFromSerFile = params.containsKey(TransformationsConfigurationParametersNames.DIRT_LIKE_SER_FILE_PARAMETER_NAME);
			
			if (loadFromSerFile)
			{
				File serFile = params.getFile(TransformationsConfigurationParametersNames.DIRT_LIKE_SER_FILE_PARAMETER_NAME);
				return new DirtDBRuleBase(serFile,ruleBaseName,parser);
			}
			else
			{
				// Connect to the data-base
				Class.forName(params.get(DB_DRIVER));
				String dbUrl = params.get(DB_URL);
				Connection connection = DriverManager.getConnection(dbUrl);
				String templates = params.get(TEMPLATES_TABLE_NAME);
				String rules = params.get(RULES_TABLE_NAME);
				int limit = params.getInt(LIMIT_NUMBER_OF_RULES);
				DistSimParameters distSimParameters = new DistSimParameters(templates, rules, limit, Constants.DEFAULT_DIRT_LIKE_RESOURCES_CACHE_SIZE, Constants.DEFAULT_DIRT_LIKE_RESOURCES_CACHE_SIZE);

				return new DirtDBRuleBase(connection,ruleBaseName,distSimParameters,parser);
			}
		}
		catch (ClassNotFoundException e)
		{
			throw new RuleBaseException("Cannot construct rule base "+ruleBaseName+". See nested exception.",e);
		}
		catch (ConfigurationException e)
		{
			throw new RuleBaseException("Cannot construct rule base "+ruleBaseName+". See nested exception.",e);
		}
		catch (SQLException e)
		{
			throw new RuleBaseException("Cannot construct rule base "+ruleBaseName+". See nested exception.",e);
		}
		
		
	}
	
	public DirtDBRuleBase(Connection connection, String ruleBaseName,
			DistSimParameters dbParameters, PARSER parser) throws RuleBaseException
	{
		super();
		this.parser = parser;
		
		if (!constantsOK()) throw new RuleBaseException("constants no OK");
		
		this.connection = connection;
		this.ruleBaseName = ruleBaseName;
		this.dbParameters = dbParameters;
		

		try
		{
			prepareStatements();
			if(Constants.DIRT_LIKE_LOAD_ALL_TEMPLATES_IN_ADVANCE)
			{
				logger.info("DirtDBRuleBase "+this.ruleBaseName+": Loading all templates in advance...");
				createMapTemplateToId(); // Creates a map from all templates to their IDs. Stored in this.mapTemplateToId
				logger.info("done. "+this.mapTemplateToId.leftSet().size()+" templates were loaded.");
			}
			if (Constants.DIRT_LIKE_LOAD_ALL_RULES_IN_ADVANCE) // Not used. Takes long run-time.
			{
				if(!Constants.DIRT_LIKE_LOAD_ALL_TEMPLATES_IN_ADVANCE) throw new RuleBaseException("mode DIRT_LIKE_LOAD_ALL_RULES_IN_ADVANCE requires also mode DIRT_LIKE_LOAD_ALL_TEMPLATES_IN_ADVANCE. Check the class Constants.");
				logger.info("DirtDBRuleBase: Loading all rules in advance...");
				createMapAllRules();
				logger.info("done.");
			}
			logger.info("Done: initialization of DirtDBRuleBase. Memory used: "+Utils.stringMemoryUsedInMB());
			
		}
		catch (SQLException e)
		{
			throw new RuleBaseException("Data Base problem for rule base \""+this.ruleBaseName+"\"",e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public DirtDBRuleBase(File serFileWholeDB, String ruleBaseName, PARSER parser) throws RuleBaseException
	{
		this.parser = parser;
		
		if (!constantsOK()) throw new RuleBaseException("constants no OK");

		try
		{
			logger.info("DirtDBRuleBase: Loading rule base from a serialization file...");
			this.ruleBaseName = ruleBaseName;
			
			ObjectInputStream input = new ObjectInputStream(new FileInputStream(serFileWholeDB));
			try
			{
				this.mapTemplateToId = (BidirectionalMap<String, Integer>) input.readObject();
				this.mapAllRules = (ValueSetMap<Integer, IdAndScore>) input.readObject();
				
				logger.info("DirtDBRuleBase: Loading rule base from a serialization file done.");
				logger.info("Done: initialization of DirtDBRuleBase. Memory used: "+Utils.stringMemoryUsedInMB());
			}
			finally
			{
				input.close();
			}
		} 
		catch (IOException e)
		{
			throw new RuleBaseException("Failed to load from ser file for rule base: "+ruleBaseName+".",e);
		}
		catch(ClassNotFoundException e)
		{
			throw new RuleBaseException("Failed to load from ser file for rule base: "+ruleBaseName+".",e);
		}
	}

	public void terminate()
	{
		try
		{
			if (logger.isDebugEnabled()){logger.debug("Terminating rule base: "+this.ruleBaseName);}
			this.mapTemplateToId=null;
			this.mapAllRules=null;
			if (this.connection!=null)
				this.connection.close();
		}
		catch (SQLException e)
		{
			logger.warn("Connection could not be closed for rule base: "+this.ruleBaseName);
		}
		
	}

	/**
	 * Given a tree, return the rules the match (subtrees of) this tree.
	 * 
	 * @param tree The tree
	 * @param hypothesisTemplates A set of all hypothesis templates (a template is the string representation of a path in parse tree, as in the DIRT data-base).
	 * Using this parameter depends on a boolean constant (in {@link Constants}).
	 * 
	 * @param hypothesisWords A set of hypothesis words. Usage depends on a boolean constant (in {@link Constants}).
	 * 
	 * @return All matching rules.
	 * 
	 * @throws RuleBaseException
	 */
	public ImmutableSet<RuleWithConfidenceAndDescription<Info,BasicNode>> getRulesForLeftByTree(
			ExtendedNode tree,
			ImmutableSet<String> hypothesisTemplates,
			Iterable<String> hypothesisWords
			) throws RuleBaseException
	{
		try
		{
			Set<RuleWithConfidenceAndDescription<Info,BasicNode>> setRules =
				new LinkedHashSet<RuleWithConfidenceAndDescription<Info,BasicNode>>();
			
			// Get all the templates in the given parse tree (which is the text parse tree, or a derivation of the text parse tree).
			// A Template is a string representation of a path in a parse-tree.
			TemplatesFromTree<ExtendedInfo, ExtendedNode> templateFromTree = new TemplatesFromTree<ExtendedInfo, ExtendedNode>(tree);
			templateFromTree.createTemplate();
			Set<String> templates = templateFromTree.getTemplates(); // Now we have the templates.
			
			if (logger.isDebugEnabled())
			{
				StringBuffer sb = new StringBuffer();
				sb.append("Templates generated:\n");
				for (String template : templates)
				{
					sb.append(template);
					sb.append("\n");
				}
				logger.debug(sb.toString());
			}
			
			// For each template in the given parse-tree (the text parse tree), find all matching rules.
			for (String template : templates)
			{
				setRules.addAll(
						getRulesForLeftByTemplate(template,hypothesisTemplates, hypothesisWords)
				);
			}
			
			if (logger.isDebugEnabled()){logger.debug("Total number of rules for the given tree: "+setRules.size());}

			return new ImmutableSetWrapper<RuleWithConfidenceAndDescription<Info,BasicNode>>(setRules);
		}
		catch(TeEngineMlException e)
		{
			throw new RuleBaseException("Could not fetch rules from resource "+this.ruleBaseName+". See nested exception.",e);
		}
		catch(SQLException e)
		{
			throw new RuleBaseException("Could not fetch rules from resource "+this.ruleBaseName+". See nested exception.",e);
		}
		catch(TemplateToTreeException e)
		{
			throw new RuleBaseException("Could not fetch rules from resource "+this.ruleBaseName+". See nested exception.",e);
		}
	}
	
	protected Set<RuleWithConfidenceAndDescription<Info,BasicNode>> getRulesForLeftByTemplate(String template, ImmutableSet<String> hypothesisTemplates, Iterable<String> hypothesisWords) throws SQLException, TemplateToTreeException, RuleBaseException
	{
		if (logger.isDebugEnabled()){logger.debug("Working on template: "+template);}
		Set<RuleWithConfidenceAndDescription<Info,BasicNode>> setRules = null;
		
		// Get the ID of the template, as it is in the template-table in the data-base (Null if does not exist in the data-base).
		Integer idOfTemplate = getIdForTemplate(template);
		if (logger.isDebugEnabled()){logger.debug("Id of template = "+idOfTemplate);}
		if (idOfTemplate!=null) // The template exists in the data-base.
		{
			// Get all the rules for which the given template is the "left-hand-side" of the rule (note that in the data-base
			// the "left-hand-side" is stored in the column "right". Confusing? I know. Not my idea...).
			setRules = getRulesOfId(template,idOfTemplate,hypothesisTemplates, hypothesisWords);
		}

		if (null==setRules)
			setRules = emptySetRules;

		if (logger.isDebugEnabled()){logger.debug("Total number of rules found for the template: "+setRules.size());}


		return setRules;
	}
	
	/**
	 * Given a template, returns the ID of that template, as it is in the template-table in the data-base.
	 * Returns <code>null</code> if the template does not exist in the data-base.
	 * @param template A template (a string representation of a path in the parse tree).
	 * @return The ID of the template, or <code>null</code> if it does not exist in the data-base.
	 * @throws SQLException
	 */
	protected Integer getIdForTemplate(String template) throws SQLException
	{
		Integer id = null;
		if(mapTemplateToId!=null)
		{
			if (this.mapTemplateToId.leftContains(template))
			{
				id = this.mapTemplateToId.leftGet(template);
			}
		}
		else
		{

			statementIdForTemplate.setString(1, template);
			ResultSet resultSet = statementIdForTemplate.executeQuery();
			int numberOfIterations = 0;
			while (resultSet.next())
			{
				if (0==numberOfIterations)
				{
					id = resultSet.getInt("id");
				}
				++numberOfIterations;
			}
			if (numberOfIterations>1)
			{
				logger.warn("More than one id for a single template in rule base: "+this.ruleBaseName+". Template was: "+template);
			}

		}
		return id;
	}
	
	/**
	 * Returns all rules for the given template (which is given as a template and an ID).
	 * @param template A given template.
	 * @param id The ID of the given template.
	 * @param hypothesisTemplates
	 * @param hypothesisWords
	 * @return The rules for this template.
	 * @throws SQLException
	 * @throws TemplateToTreeException
	 * @throws RuleBaseException
	 */
	protected Set<RuleWithConfidenceAndDescription<Info, BasicNode>> getRulesOfId(String template, int id, ImmutableSet<String> hypothesisTemplates, Iterable<String> hypothesisWords) throws SQLException, TemplateToTreeException, RuleBaseException
	{
		if (mapAllRules!=null) // If the whole data-base was loaded to memory. IT IS NO LONGER USED.
		{
			return getRulesOfIdUsingPreloadedRules(template,id,hypothesisTemplates, hypothesisWords);
		}
		else // The code that actually runs starts here:
		{
			Set<String> notYetHyothesisTemplates = null;
			if (Constants.DIRT_LIKE_QUERY_RULE_ALSO_BY_HYPOTHESIS_TEMPLATES)
			{
				notYetHyothesisTemplates = hypothesisTemplates.getMutableSetCopy();
			}
			TemplateToTree givenTemplateConverter = null;
			Set<RuleWithConfidenceAndDescription<Info, BasicNode>> setRules =
				new LinkedHashSet<RuleWithConfidenceAndDescription<Info,BasicNode>>();

			// The right hand side of the rule can be represented as the template of the right-hand-side, and the rule's score.
			Set<TemplateAndScore> rhsResults;  
			if (cache.containsKey(template)) // If the right-hand-side is already cached, use the cached right-hand-side.
			{
				rhsResults = cache.get(template);
				if (logger.isDebugEnabled()){logger.debug(""+rhsResults.size()+" templates loaded from cache for template: "+template);}
			}
			else
			{
				rhsResults = new LinkedHashSet<TemplateAndScore>();
				
				// Execute a query which returns all right-hand-sides of rules that have the given template as "left-hand-side".
				// Remember that in the data-base the "right-hand-side" is stored in "left" column, and "left-hand-side" is stored
				// in "right" column.
				statementRuleForId.setInt(1, id);
				ResultSet resultSet = statementRuleForId.executeQuery();
				while (resultSet.next())
				{
					// Get the template of the "right-hand-side"
					String description = resultSet.getString("description");
					// description = description.trim();
					
					// Get the score of the rule.
					double score = resultSet.getDouble("score");
					rhsResults.add(new TemplateAndScore(description, score));
				}
				cache.put(template, rhsResults);
				if (logger.isDebugEnabled()){logger.debug(""+rhsResults.size()+" templates loaded from data-base for template"+template);}
			}
			
			// For each "right-hand-side" and "score" - we can build a rule, since we know the left-hand-side - it is the given template.
			// So we have left-hand-side, we have right-hand-side, and we have a score.
			for (TemplateAndScore templateAndScore : rhsResults)
			{
				if (Constants.DIRT_LIKE_QUERY_RULE_ALSO_BY_HYPOTHESIS_TEMPLATES)
				{
					notYetHyothesisTemplates.remove(templateAndScore.getTemplate());
				}
				
				if (logger.isDebugEnabled()){logger.debug("Examining rule: "+template+" => "+templateAndScore.getTemplate());}
				
				// Here we filter the rules such that only rules with right-hand-side that matched the hypothesis-parse-tree
				// are used.
				boolean useThisTemplate = true;
				if (Constants.DIRT_LIKE_FILTER_BY_HYPOTHESIS_TEMPLATES)
				{
					if (!hypothesisTemplates.contains(templateAndScore.getTemplate()))
					{
						if (logger.isDebugEnabled()){logger.debug("Filtered: "+templateAndScore.getTemplate());}
						useThisTemplate = false;
					}
				}
				if (useThisTemplate)
				{
					// Now, we will convert the left-hand-side template into a sub-parse-tree,
					// and do the same for the right-hand-side template,
					// and create a rule.
					
					if (null==givenTemplateConverter) // Converting the left-hand-side can be done only once for all rules, since all of the rules have the same left-hand-side (which is the given template) 
					{
						givenTemplateConverter=new TemplateToTree(template,parser);
						givenTemplateConverter.createTree();
					}
					// convert the right-hand-side template into a sub-parse-tree 
					TemplateToTree entailedTemplateConverter = new TemplateToTree(templateAndScore.getTemplate(),parser);
					entailedTemplateConverter.createTree();
					
					// Based on the constant, we might want to filter the rules such that only rules which have root-of-right-hand-side
					// contained in the hypothesis will be used.
					boolean useThisTemplate2 = true; // default- no filter by words.
					if (Constants.DIRT_LIKE_FILTER_BY_HYPOTHESIS_WORDS)
					{
						String lemmaOfMainPredicateInRhsTemplate = getHighestLemmaOfTemplate(entailedTemplateConverter.getTree()); //was: = InfoGetFields.getLemma(entailedTemplateConverter.getTree().getInfo());
						if (StringUtil.setContainsIgnoreCase(hypothesisWords, lemmaOfMainPredicateInRhsTemplate))
						{
							useThisTemplate2 = true;
						}
						else
						{
							useThisTemplate2 = false;
						}
					}
					if (useThisTemplate2)
					{
						logger.debug("YES! Template is relevant for the hypothesis");
						setRules.add(
								ruleFromTemplates(givenTemplateConverter,entailedTemplateConverter,templateAndScore.getScore()));
					}
					else
					{
						logger.debug("NO! Template is irrelevant for the hypothesis");
					}
				}
			}
			
			// Get the top-K of the hypothesis-template.
			if (Constants.DIRT_LIKE_QUERY_RULE_ALSO_BY_HYPOTHESIS_TEMPLATES)
			{
				if (null==givenTemplateConverter)
				{
					givenTemplateConverter=new TemplateToTree(template,parser);
					givenTemplateConverter.createTree();
				}
				setRules.addAll(getRulesByHypothesisTemplates(template, id, givenTemplateConverter, notYetHyothesisTemplates));
			}

			return setRules;
		}
	}
	
	
	/**
	 * Used by the function {@link #getRulesOfId(String, int, ImmutableSet, Iterable)}, and only if
	 * {@link Constants#DIRT_LIKE_FILTER_BY_HYPOTHESIS_WORDS} is true.
	 * @param templateRoot
	 * @return
	 */
	private final static String getHighestLemmaOfTemplate(BasicNode templateRoot)
	{
		String ret = null;
		ret = InfoGetFields.getLemma(templateRoot.getInfo());
		if (notEmpty(ret))
		{
			return ret;
		}
		else
		{
			if (null==templateRoot.getChildren())
			{
				return null;
			}
			else if (templateRoot.getChildren().size()!=1)
			{
				return null; // two children - so the predicate should be the head!
			}
			else
			{
				return getHighestLemmaOfTemplate(templateRoot.getChildren().iterator().next());
			}
		}
		
	}
	
	private final static boolean notEmpty(String str)
	{
		if (null==str)return false;
		else if (str.trim().length()>0)
			return true;
		else
			return false;
	}
	
	// Not used
	protected Set<RuleWithConfidenceAndDescription<Info, BasicNode>> getRulesOfIdUsingPreloadedRules(String template, int id, ImmutableSet<String> hypothesisTemplates, Iterable<String> hypothesisWords) throws SQLException, TemplateToTreeException, RuleBaseException
	{
		
		
		TemplateToTree givenTemplateConverter = null;
		Set<RuleWithConfidenceAndDescription<Info, BasicNode>> setRules =
			new LinkedHashSet<RuleWithConfidenceAndDescription<Info,BasicNode>>();

		if (!mapAllRules.containsKey(id))
		{
			// do nothing
		}
		else
		{

			Set<Integer> hypothesisTemplatesIds = new LinkedHashSet<Integer>();
			for (String hypothesisTemplate : hypothesisTemplates)
			{
				if (mapTemplateToId.leftContains(hypothesisTemplate))
				{
					hypothesisTemplatesIds.add(mapTemplateToId.leftGet(hypothesisTemplate));
				}
			}

			Set<IdAndScore> rightHandSidesFilteredByHypothesis = new LinkedHashSet<IdAndScore>();
			if (Constants.DIRT_LIKE_FILTER_BY_HYPOTHESIS_TEMPLATES)
			{
				for (IdAndScore idAndScore : mapAllRules.get(id))
				{
					if (hypothesisTemplatesIds.contains(idAndScore.getId()))
					{
						rightHandSidesFilteredByHypothesis.add(idAndScore);
					}
				}
			}
			else
			{
				for (IdAndScore idAndScore : mapAllRules.get(id))
				{
					rightHandSidesFilteredByHypothesis.add(idAndScore);
				}
			}

			for (IdAndScore idAndScore : rightHandSidesFilteredByHypothesis)
			{
				if (!mapTemplateToId.rightContains(idAndScore.getId())) throw new RuleBaseException("BUG in rule base "+this.ruleBaseName);
				String rightHandSideTemplate = mapTemplateToId.rightGet(idAndScore.getId());
				if (null==givenTemplateConverter)
				{
					givenTemplateConverter=new TemplateToTree(template,parser);
					givenTemplateConverter.createTree();
				}
				if (logger.isDebugEnabled()){logger.debug("Found rule: "+template+" => "+rightHandSideTemplate);}
				TemplateToTree entailedTemplateConverter = new TemplateToTree(rightHandSideTemplate,parser);
				entailedTemplateConverter.createTree();

				boolean useThisTemplate2 = true;
				if (Constants.DIRT_LIKE_FILTER_BY_HYPOTHESIS_WORDS)
				{
					String lemmaOfMainPredicateInRhsTemplate = InfoGetFields.getLemma(entailedTemplateConverter.getTree().getInfo());
					if (StringUtil.setContainsIgnoreCase(hypothesisWords, lemmaOfMainPredicateInRhsTemplate))
					{
						useThisTemplate2 = true;
					}
					else
					{
						useThisTemplate2 = false;
					}
				}
				if (useThisTemplate2)
				{
					setRules.add(
							ruleFromTemplates(givenTemplateConverter,entailedTemplateConverter,idAndScore.getScore()));
				}
			}
		}
		
		return setRules;
	}
	
	
	/**
	 * Gets two {@link TemplateToTree} objects that represent the left-hand-side and the
	 * right-hand-side, and builds a rule.
	 * The {@link TemplateToTree} has information not only about the tree, but also about
	 * variables. The mapping is always left-leaf to left-leaf and right-leaf to right-leaf (that
	 * is how it works in DIRT).
	 * 
	 * @param entailing left hand side
	 * @param entailed right hand side
	 * @param score
	 * @return
	 * @throws TemplateToTreeException 
	 */
	protected RuleWithConfidenceAndDescription<Info, BasicNode> ruleFromTemplates(TemplateToTree entailing, TemplateToTree entailed, double score) throws TemplateToTreeException
	{
		BidirectionalMap<BasicNode, BasicNode> mapLhsRhs = new SimpleBidirectionalMap<BasicNode, BasicNode>();
		mapLhsRhs.put(entailing.getTree(), entailed.getTree());
		if ( (entailing.getLeftVariableNode()!=null) && (entailed.getLeftVariableNode()!=null) )
		{
			mapLhsRhs.put(entailing.getLeftVariableNode(),entailed.getLeftVariableNode());
		}
		if ( (entailing.getRightVariableNode()!=null) && (entailed.getRightVariableNode()!=null) )
		{
			mapLhsRhs.put(entailing.getRightVariableNode(),entailed.getRightVariableNode());
		}
		
		
		SyntacticRule<Info, BasicNode> rule = new SyntacticRule<Info, BasicNode>(entailing.getTree(), entailed.getTree(), mapLhsRhs);
		String description = entailing.getTemplate()+" -> "+entailed.getTemplate();
		
		double actualScore=0;
		if (Constants.DIRT_LIKE_USE_CONSTANT_SCORE_FOR_ALL_RULES)
		{
			actualScore = CONSTANT_SCORE;
		}
		else
		{
			actualScore = score;
		}
		return new RuleWithConfidenceAndDescription<Info, BasicNode>(rule,actualScore,description);
	}
	
	protected void prepareStatements() throws SQLException
	{
		// This query: Given a template - return its ID.
		String queryIdForTemplate = "SELECT id FROM "+dbParameters.getTemplatesTableName()+" WHERE description = ?";
		statementIdForTemplate = connection.prepareStatement(queryIdForTemplate);
		
		// This query: Given an ID - return all templates that are entailed by it (i.e. by the template whose ID is the given ID).
		// The assumption is that "right" in the data-base entails "left".
		// (Note: This query is complex, and might take long run-time.)
		String queryRuleForId = "SELECT "+dbParameters.getTemplatesTableName()+".description, "+dbParameters.getRulesTableName()+".score " +
				"FROM "+dbParameters.getTemplatesTableName()+", "+dbParameters.getRulesTableName()+" " +
						"WHERE "+dbParameters.getRulesTableName()+".right_element_id=? AND "+dbParameters.getRulesTableName()+".left_element_id="+dbParameters.getTemplatesTableName()+".id " +
								"ORDER BY "+dbParameters.getRulesTableName()+".score DESC LIMIT "+String.valueOf(dbParameters.getLimitNumberOfRules());
		
		statementRuleForId = connection.prepareStatement(queryRuleForId);
		
		if (Constants.DIRT_LIKE_QUERY_RULE_ALSO_BY_HYPOTHESIS_TEMPLATES)
		{
			String queryIdRightGivenIdLeft = "SELECT "+dbParameters.getRulesTableName()+".right_element_id, " + dbParameters.getRulesTableName()+".score " +
					"FROM "+dbParameters.getRulesTableName()+" " +
					"WHERE "+dbParameters.getRulesTableName()+".left_element_id=? " +
					"ORDER BY "+dbParameters.getRulesTableName()+".score DESC LIMIT "+String.valueOf(dbParameters.getLimitNumberOfRules());

			statementRightElementIdForGivenLeftElementId = connection.prepareStatement(queryIdRightGivenIdLeft);
		}
	}
	
	protected void createMapTemplateToId() throws SQLException, RuleBaseException
	{
		WholeDBLoader loader = new WholeDBLoader(this.connection, this.dbParameters);
		loader.createMapTemplateToId();
		this.mapTemplateToId = loader.getMapTemplateToId();
	}
	
	// Not used
	protected void createMapAllRules() throws SQLException
	{
		WholeDBLoader loader = new WholeDBLoader(this.connection, this.dbParameters);
		loader.createMapAllRules();
		this.mapAllRules = loader.getMapAllRules();
	}
	
	/**
	 * Given a set of hypothesis templates - get their top-K rules.<BR>
	 * The left-hand-side is known  - it is the text-template. The only question is whether it exists in the top-K rules of the
	 * hypothesis-template.
	 * <P>
	 * (Template is a path in a parse-tree. Templates are what stored in the data-base, in a compact string representation.
	 * Each rule in the data-base is represented by two templates and a score).
	 * 
	 * 
	 * @param textTemplate
	 * @param idOfTextTemplate
	 * @param textTemplateToTree
	 * @param notYetInRulesHypothesisTemplates
	 * @return
	 * @throws SQLException
	 * @throws TemplateToTreeException
	 */
	protected Set<RuleWithConfidenceAndDescription<Info, BasicNode>> getRulesByHypothesisTemplates(String textTemplate, int idOfTextTemplate, TemplateToTree textTemplateToTree, Set<String> notYetInRulesHypothesisTemplates) throws SQLException, TemplateToTreeException
	{
		Set<RuleWithConfidenceAndDescription<Info, BasicNode>> returnedRules = new LinkedHashSet<RuleWithConfidenceAndDescription<Info,BasicNode>>();
		for (String hypothesisTemplate : notYetInRulesHypothesisTemplates)
		{
			// Get the top-K of this template
			Set<IdAndScore> leftHandSideAndScore = queryAllRulesForGivenHypothesisTemplate(hypothesisTemplate);
			
			// If you get some top-K rules
			if (leftHandSideAndScore!=null){ if (leftHandSideAndScore.size()>0)
			{
				TemplateToTree hypothesisTemplateToTree  = null;
				for (IdAndScore idAndScore : leftHandSideAndScore)
				{
					if (idAndScore.getId()==idOfTextTemplate)
					{
						if (null==hypothesisTemplateToTree)
						{
							hypothesisTemplateToTree  = new TemplateToTree(hypothesisTemplate,parser);
							hypothesisTemplateToTree.createTree();
						}
						
						RuleWithConfidenceAndDescription<Info, BasicNode> rule =
								ruleFromTemplates(textTemplateToTree, hypothesisTemplateToTree, idAndScore.getScore());
						returnedRules.add(rule);
					}
				} // end for each IdAndScort
			}} // end if
		} // end for each hypothesis-template
		return returnedRules;
	}
	
	/**
	 * Given a template of the hypothesis - get its top-K rules.<BR>
	 * 
	 * @param hypothesisTemplate
	 * @return
	 * @throws SQLException
	 */
	protected Set<IdAndScore> queryAllRulesForGivenHypothesisTemplate(String hypothesisTemplate) throws SQLException
	{
		Set<IdAndScore> results = null;  
		if (cacheForHypothesisTemplates.containsKey(hypothesisTemplate))
		{
			results = cacheForHypothesisTemplates.get(hypothesisTemplate);
		}
		else
		{
			results = new LinkedHashSet<IdAndScore>();
			Integer id = getIdForTemplate(hypothesisTemplate);
			if (id != null)
			{
				statementRightElementIdForGivenLeftElementId.setInt(1, id);
				ResultSet resultSet = statementRightElementIdForGivenLeftElementId.executeQuery();
				while (resultSet.next())
				{
					int idRightColumn = resultSet.getInt("right_element_id");
					double score = resultSet.getDouble("score");
					results.add(new IdAndScore(idRightColumn, score));
				}
			}

			cacheForHypothesisTemplates.put(hypothesisTemplate, results);
		}
		return results;
	}

	private boolean constantsOK()
	{
		boolean ret = true;
		
		if (Constants.DIRT_LIKE_QUERY_RULE_ALSO_BY_HYPOTHESIS_TEMPLATES)
		{
			if (Constants.DIRT_LIKE_LOAD_ALL_RULES_IN_ADVANCE)
			{
				ret = false;
			}
		}
		return ret;
	}
	

	////////////////////// Nested class //////////////////////////
	
	/**
	 * Nested class, used to represent a rule's right-hand-side and the rule's score.
	 * @author Asher Stern
	 */
	private static final class TemplateAndScore
	{
		public TemplateAndScore(String template, double score)
		{
			super();
			this.template = template;
			this.score = score;
		}
		public String getTemplate(){return template;}
		public double getScore(){return score;}

		private final String template;
		private final double score;
	}

	



	

	/////////////////////// Fields //////////////////////////

	protected final PARSER parser;
	protected PreparedStatement statementIdForTemplate = null;
	protected PreparedStatement statementRuleForId = null;
	protected PreparedStatement statementRightElementIdForGivenLeftElementId = null;
	
	protected Connection connection=null;
	protected String ruleBaseName=null;
	protected DistSimParameters dbParameters=null;
	
	/**
	 * Map from a template to its ID.
	 */
	protected BidirectionalMap<String, Integer> mapTemplateToId = null;
	
	protected ValueSetMap<Integer, IdAndScore> mapAllRules = null;
	
	/**
	 * A cache that stores sets of right-hand-sides for given left-hand-sides
	 */
	private Cache<String, Set<TemplateAndScore>> cache =
		new CacheFactory<String, Set<TemplateAndScore>>().getCache(Constants.DEFAULT_DIRT_LIKE_RESOURCES_CACHE_SIZE);

	
	/**
	 * A cache that stores sets of left-hand-sides for given right-hand-sides
	 */
	private Cache<String, Set<IdAndScore>> cacheForHypothesisTemplates =
		new CacheFactory<String, Set<IdAndScore>>().getCache(Constants.DEFAULT_DIRT_LIKE_RESOURCES_CACHE_SIZE);

	
	private static final Set<RuleWithConfidenceAndDescription<Info,BasicNode>> emptySetRules = new DummySet<RuleWithConfidenceAndDescription<Info,BasicNode>>();

	private static final double CONSTANT_SCORE = Math.exp(-1.0);
	
	private static final Logger logger = Logger.getLogger(DirtDBRuleBase.class);
}
