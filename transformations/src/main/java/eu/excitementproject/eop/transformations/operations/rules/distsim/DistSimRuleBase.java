package eu.excitementproject.eop.transformations.operations.rules.distsim;
import static eu.excitementproject.eop.common.representation.partofspeech.SimplerPosTagConvertor.simplerPos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.common.component.syntacticknowledge.RuleWithConfidenceAndDescription;
import eu.excitementproject.eop.common.component.syntacticknowledge.SyntacticRule;
import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.datastructures.SimpleBidirectionalMap;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSetWrapper;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;
import eu.excitementproject.eop.common.utilities.Cache;
import eu.excitementproject.eop.common.utilities.CacheFactory;
import eu.excitementproject.eop.core.component.syntacticknowledge.utilities.PARSER;
import eu.excitementproject.eop.core.component.syntacticknowledge.utilities.TemplateToTree;
import eu.excitementproject.eop.core.component.syntacticknowledge.utilities.TemplateToTreeException;
import eu.excitementproject.eop.transformations.datastructures.LemmaAndPos;
import eu.excitementproject.eop.transformations.operations.rules.DynamicRuleBase;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseException;

/**
 * Represents a distributional similarity rule base that is stored in a DIRT-style data-base.
 * As for usage, see {@link DynamicRuleBase}.
 * <P>
 * Hopefully, this is parser independent (originally designed for Minipar, but
 * hard-coded strings were removed).
 * 
 * @deprecated No longer used.
 * 
 * @see DynamicRuleBase
 *  
 * @author Asher Stern
 * @since February, 2011
 *
 */
@Deprecated
public class DistSimRuleBase extends DynamicRuleBase<Info, BasicNode>
{
	////////////////////////////// PUBLIC /////////////////////////////////////
	
	
	public DistSimRuleBase(Connection connection, DistSimParameters distSimParameters, String ruleBaseName, PARSER parser) throws RuleBaseException
	{
		super();
		this.parser = parser;
		this.connection = connection;
		this.distSimParameters = distSimParameters;
		this.ruleBaseName = ruleBaseName;
		
		try
		{
			createPreparedStatements();
			initCaches();
		}
		catch(SQLException e)
		{
			throw new RuleBaseException("sql problem",e);
		}
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.operations.rules.DynamicRuleBase#getLeftHandSidesByLemmaAndPos(ac.biu.nlp.nlp.engineml.datastructures.LemmaAndPos)
	 */
	@Override
	public ImmutableSet<BasicNode> getLeftHandSidesByLemmaAndPos(LemmaAndPos lemmaAndPos) throws RuleBaseException
	{
		try
		{
			return getLeftHandSidesByLemmaAndPosImpl(lemmaAndPos);
		}
		catch (SQLException e)
		{
			throw new RuleBaseException("database problem.",e);
		}
		catch (TemplateToTreeException e)
		{
			throw new RuleBaseException("template to tree exception.",e);
		}
	}

	/* (non-Javadoc)
	 * @see ac.biu.nlp.nlp.engineml.operations.rules.DynamicRuleBase#getRulesByLeftHandSide(ac.biu.nlp.nlp.instruments.parse.tree.AbstractNode)
	 */
	@Override
	public ImmutableSet<RuleWithConfidenceAndDescription<Info, BasicNode>> getRulesByLeftHandSide(BasicNode leftHandSide) throws RuleBaseException
	{
		try
		{
			return getRulesByLeftHandSideImpl(leftHandSide);
		}
		catch (SQLException e)
		{
			throw new RuleBaseException("database problem.",e);
		}
		catch (TemplateToTreeException e)
		{
			throw new RuleBaseException("template to tree exception.",e);
		}
		
	}
	
	
	///////////////////// PROTECTED AND PRIVATE //////////////////////////////
	
	protected void createPreparedStatements() throws SQLException
	{
		String getLhsQuery = "SELECT * FROM "+distSimParameters.getTemplatesTableName()+" WHERE "+distSimParameters.getTemplatesTableName()+".description like ?";
		getLhsStmt = connection.prepareStatement(getLhsQuery);
		
		String queryGetEntailedTemplate = "SELECT "+distSimParameters.getTemplatesTableName()+".id, "+distSimParameters.getTemplatesTableName()+".description, "+distSimParameters.getRulesTableName()+".score "+
		"FROM "+distSimParameters.getRulesTableName()+", "+distSimParameters.getTemplatesTableName()+" "+
		"WHERE "+distSimParameters.getRulesTableName()+".right_element_id=? "+
		"AND "+distSimParameters.getRulesTableName()+".left_element_id="+distSimParameters.getTemplatesTableName()+".id "+
		"ORDER BY score DESC "+
		"LIMIT "+String.valueOf(distSimParameters.getLimitNumberOfRules());
		getEntailedTemplates = connection.prepareStatement(queryGetEntailedTemplate);
	}
	
	
	
	protected ImmutableSet<BasicNode> getLeftHandSidesByLemmaAndPosImpl(LemmaAndPos lemmaAndPos) throws SQLException, TemplateToTreeException, RuleBaseException
	{
		if (firstCallToGetByLemma)
		{
			// if this is the first time this method is called (for this instance),
			// or it this is the first time this method is called after earlier calls to
			// getRulesByLeftHandSide()
			mapTreeToIdInDataBase = new LinkedHashMap<BasicNode, Integer>();
			mapTreeToTemplateToTree = new LinkedHashMap<BasicNode, TemplateToTree>();
			firstCallToGetByLemma=false;
		}
		ImmutableSet<BasicNode> ret = null;
		// mapping from left-hand-side to the template from which it was created.
		Map<BasicNode, TemplateToTree> thisLemmaMapTreeToTemplateToTree = null;
		// mapping from left-hand-side to the "id" of its template in the data base.
		Map<BasicNode,Integer> thisLemmaMapTreeToIdInDataBase = null;

		boolean returnedFromCache = false;
		
		// The caches are shared among all threads. They are shared among
		// all instances of DistSimRuleBase that were constructed with the same
		// "ruleBaseName". Therefore I have to synchronize the access using a static
		// object (I could also use synchronized(DistSimRuleBase.class) )
		synchronized(lhsCacheSynchronizer)
		{
			if ( (cacheLHS.containsKey(lemmaAndPos)) && (cacheLHSMap.containsKey(lemmaAndPos)) && (cacheLHSMapId.containsKey(lemmaAndPos)) )
			{
				thisLemmaMapTreeToTemplateToTree = cacheLHSMap.get(lemmaAndPos);
				thisLemmaMapTreeToIdInDataBase = cacheLHSMapId.get(lemmaAndPos);
				ret = cacheLHS.get(lemmaAndPos);
				returnedFromCache = true;
			}
		}
		if (!returnedFromCache)
		{
			thisLemmaMapTreeToTemplateToTree = new LinkedHashMap<BasicNode, TemplateToTree>();
			thisLemmaMapTreeToIdInDataBase = new LinkedHashMap<BasicNode, Integer>();
			String lemmaAndPosAsString = lemmaAndPosToString(lemmaAndPos);
			if (lemmaAndPosAsString!=null)
			{
				Set<BasicNode> retSet = new LinkedHashSet<BasicNode>();
				getLhsStmt.setString(1, "%"+lemmaAndPosAsString+"%"); // The query will look for "...WHERE description like '%v:go:v%'", for example
				ResultSet resultSet = getLhsStmt.executeQuery();
				while (resultSet.next())
				{
					String templateString = resultSet.getString("description");
					Integer id = resultSet.getInt("id");
					TemplateToTree converter = new TemplateToTree(templateString,parser);
					converter.createTree();
					BasicNode retTree = converter.getTree();
					thisLemmaMapTreeToTemplateToTree.put(retTree, converter);
					thisLemmaMapTreeToIdInDataBase.put(retTree, id);
					retSet.add(retTree);
				}
				
				ret = new ImmutableSetWrapper<BasicNode>(retSet);
			}
			if (null==ret)ret = emptyLHS;
			
			synchronized(lhsCacheSynchronizer)
			{
				cacheLHS.put(lemmaAndPos, ret);
				cacheLHSMap.put(lemmaAndPos,thisLemmaMapTreeToTemplateToTree);
				cacheLHSMapId.put(lemmaAndPos, thisLemmaMapTreeToIdInDataBase);
			}
		}
		
		// No synchronization is needed here. Those maps are not static, they are
		// member fields of the current instance (the current instance of DistSimRuleBase)
		mapTreeToIdInDataBase.putAll(thisLemmaMapTreeToIdInDataBase);
		mapTreeToTemplateToTree.putAll(thisLemmaMapTreeToTemplateToTree);
		
		if (null==ret)ret = emptyLHS;
		return ret;
	}
	
	
	protected ImmutableSet<RuleWithConfidenceAndDescription<Info, BasicNode>> getRulesByLeftHandSideImpl(BasicNode leftHandSide) throws SQLException, TemplateToTreeException, RuleBaseException
	{
		// Next time getLeftHandSidesByLemmaAndPos() will be called, it will create new maps for
		// the left-hand-sides.
		firstCallToGetByLemma = true;
		
		ImmutableSet<RuleWithConfidenceAndDescription<Info, BasicNode>> ret = null;
		boolean returnedFromCache = false;
		
		// The cache is shared among all instances of DistSimRuleBase that have the
		// same ruleBaseName.
		synchronized(cacheRules)
		{
			if (cacheRules.containsKey(leftHandSide))
			{
				ret = cacheRules.get(leftHandSide);
				returnedFromCache = true;
			}
		}
		if (!returnedFromCache)
		{
			if (mapTreeToTemplateToTree.containsKey(leftHandSide) && mapTreeToIdInDataBase.containsKey(leftHandSide))
			{
				Set<RuleWithConfidenceAndDescription<Info, BasicNode>> rulesSet = new LinkedHashSet<RuleWithConfidenceAndDescription<Info,BasicNode>>();
				TemplateToTree converter = mapTreeToTemplateToTree.get(leftHandSide); // I will need it merely for the description of the rule.
				Integer id = mapTreeToIdInDataBase.get(leftHandSide);
				getEntailedTemplates.setInt(1, id.intValue());
				ResultSet resultSet = getEntailedTemplates.executeQuery();
				while (resultSet.next())
				{
					String entailedTemplateString = resultSet.getString("description");
					if (null==entailedTemplateString)throw new RuleBaseException("null description");
					double score = resultSet.getDouble("score");
					
					TemplateToTree entailedConverter = new TemplateToTree(entailedTemplateString,parser);
					entailedConverter.createTree();
					RuleWithConfidenceAndDescription<Info, BasicNode> rule = ruleFromTemplates(converter,entailedConverter,score);
					rulesSet.add(rule);

				}
	
				ret = new ImmutableSetWrapper<RuleWithConfidenceAndDescription<Info,BasicNode>>(rulesSet);
				cacheRules.put(leftHandSide, ret);
			}
			else
			{
				throw new RuleBaseException("The given left hand side was not retrieved by the last call to getLeftHandSidesByLemmaAndPos()");
			}
		}


		return ret;
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
		
		return new RuleWithConfidenceAndDescription<Info, BasicNode>(rule,score,description);
	}
	
	
	/**
	 * Converts to DIRT database style. For example for "go" with part-of-speech "V" it returns
	 * "v:go:v" 
	 * @param lemmaAndPos
	 * @return
	 * @throws RuleBaseException 
	 */
	protected String lemmaAndPosToString(LemmaAndPos lemmaAndPos) throws RuleBaseException
	{
		String ret = null;
		
		//if ( !(simplerPos(lemmaAndPos.getPartOfSpeech().getCanonicalPosTag()).equals(SimplerCanonicalPosTag.VERB)) && !(simplerPos(lemmaAndPos.getPartOfSpeech().getCanonicalPosTag()).equals(SimplerCanonicalPosTag.NOUN)) )
		if ( !(simplerPos(lemmaAndPos.getPartOfSpeech().getCanonicalPosTag()).equals(SimplerCanonicalPosTag.VERB)) )		
		{
		}
		else
		{
			String pos;
			if (simplerPos(lemmaAndPos.getPartOfSpeech().getCanonicalPosTag()).equals(SimplerCanonicalPosTag.VERB))
				pos = "v";
			else if (simplerPos(lemmaAndPos.getPartOfSpeech().getCanonicalPosTag()).equals(SimplerCanonicalPosTag.NOUN))
				pos = "n";
			else throw new RuleBaseException("Internal bug");
			
			ret = pos+":"+lemmaAndPos.getLemma()+":"+pos;
		}
		return ret;
	}
	
	
	
	/**
	 * Initialize the caches (either creates new caches, or "loads" the relevant cache from the
	 * static maps of caches).
	 * <BR>
	 * The caches are shared among all DistSimRuleBase with the same "ruleBaseName".
	 * The class DistSimRuleBase contains some static maps from rule-base-names to caches. Once a
	 * cache for a specific "ruleBaseName" was created, it is there until the end of the program.
	 * This function, gets the relevant cache from the map, and assigns the relevant member-variable
	 * of the actual instance ("this") to be a reference to that cache.
	 * If the cache was not yet created, it creates it, put it into the map, and assigns the
	 * member-variable to be a reference to that now-created cache.
	 */
	protected void initCaches()
	{
		synchronized(DistSimRuleBase.class)
		{
			if (!staticCacheLHS.containsKey(ruleBaseName))
			{
				staticCacheLHS.put(ruleBaseName, new CacheFactory<LemmaAndPos, ImmutableSet<BasicNode>>().getThreadSafeCache(distSimParameters.getCacheLhsSize()));
			}
			if (!staticCacheLHSMap.containsKey(ruleBaseName))
			{
				staticCacheLHSMap.put(ruleBaseName, new CacheFactory<LemmaAndPos,Map<BasicNode, TemplateToTree>>().getThreadSafeCache(distSimParameters.getCacheLhsSize()));
			}
			if (!staticCacheLHSMapId.containsKey(ruleBaseName))
			{
				staticCacheLHSMapId.put(ruleBaseName, new CacheFactory<LemmaAndPos,Map<BasicNode,Integer>>().getThreadSafeCache(distSimParameters.getCacheLhsSize()));
			}
			if (!staticCacheRules.containsKey(ruleBaseName))
			{
				staticCacheRules.put(ruleBaseName, new CacheFactory<BasicNode, ImmutableSet<RuleWithConfidenceAndDescription<Info, BasicNode>>>().getThreadSafeCache(distSimParameters.getCacheRulesSize()));
			}
			
			cacheLHS = staticCacheLHS.get(ruleBaseName);
			cacheLHSMap = staticCacheLHSMap.get(ruleBaseName);
			cacheLHSMapId = staticCacheLHSMapId.get(ruleBaseName);
			cacheRules = staticCacheRules.get(ruleBaseName);
		}
		
	}
	
	
	
	protected final PARSER parser;
	protected Connection connection;
	protected DistSimParameters distSimParameters;
	protected String ruleBaseName;
	
	protected PreparedStatement getLhsStmt;
	protected PreparedStatement getEntailedTemplates;
	
	
	
	
	protected Map<BasicNode, TemplateToTree> mapTreeToTemplateToTree;
	protected Map<BasicNode,Integer> mapTreeToIdInDataBase;
	protected boolean firstCallToGetByLemma = true;
	
	protected Cache<LemmaAndPos, ImmutableSet<BasicNode>> cacheLHS = null;
	protected Cache<LemmaAndPos,Map<BasicNode, TemplateToTree>> cacheLHSMap = null;
	protected Cache<LemmaAndPos,Map<BasicNode,Integer>> cacheLHSMapId = null;
	protected static Object lhsCacheSynchronizer = new Object();
	protected Cache<BasicNode, ImmutableSet<RuleWithConfidenceAndDescription<Info, BasicNode>>> cacheRules = null;

	
	protected static Map<String, Cache<LemmaAndPos, ImmutableSet<BasicNode>>>  staticCacheLHS = new LinkedHashMap<String, Cache<LemmaAndPos,ImmutableSet<BasicNode>>>();
	protected static Map<String, Cache<LemmaAndPos,Map<BasicNode, TemplateToTree>>> staticCacheLHSMap = new LinkedHashMap<String, Cache<LemmaAndPos,Map<BasicNode,TemplateToTree>>>();
	protected static Map<String, Cache<LemmaAndPos,Map<BasicNode,Integer>>> staticCacheLHSMapId = new LinkedHashMap<String, Cache<LemmaAndPos,Map<BasicNode,Integer>>>();
	protected static Map<String, Cache<BasicNode, ImmutableSet<RuleWithConfidenceAndDescription<Info, BasicNode>>>> staticCacheRules = new LinkedHashMap<String, Cache<BasicNode,ImmutableSet<RuleWithConfidenceAndDescription<Info,BasicNode>>>>();
	
	protected static final ImmutableSet<BasicNode> emptyLHS = new ImmutableSetWrapper<BasicNode>(new LinkedHashSet<BasicNode>());
}
