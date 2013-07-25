package eu.excitementproject.eop.transformations.operations.rules.lexical;
import static eu.excitementproject.eop.common.representation.partofspeech.SimplerPosTagConvertor.simplerPos;
import static eu.excitementproject.eop.transformations.utilities.TransformationsConfigurationParametersNames.DB_DRIVER;
import static eu.excitementproject.eop.transformations.utilities.TransformationsConfigurationParametersNames.DB_URL;
import static eu.excitementproject.eop.transformations.utilities.TransformationsConfigurationParametersNames.LIMIT_NUMBER_OF_RULES;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSetWrapper;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.transformations.operations.rules.ByLemmaPosLexicalRuleBaseWithCache;
import eu.excitementproject.eop.transformations.operations.rules.LexicalRule;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseException;

/**
 * TODO Hard Coded String 
 * @author Asher Stern
 * @since Mar 15, 2011
 *
 */
@Deprecated
public class LinDependencyFromDBLexicalRuleBase extends ByLemmaPosLexicalRuleBaseWithCache<LexicalRule>
{
	public static final String QUERY_V = "SELECT * FROM lin_dep_v WHERE word2 like ? ORDER BY sim DESC limit ?";
	public static final String QUERY_N = "SELECT * FROM lin_dep_n WHERE word2 like ? ORDER BY sim DESC limit ?";
	public static final String QUERY_A = "SELECT * FROM lin_dep_a WHERE word2 like ? ORDER BY sim DESC limit ?";
	
	
	public LinDependencyFromDBLexicalRuleBase(ConfigurationParams params) throws RuleBaseException
	{
		try
		{
			String driverClassName = params.get(DB_DRIVER);
			Class.forName(driverClassName);
			
			String url = params.get(DB_URL);
			
			Connection connection = DriverManager.getConnection(url);
			this.connection = connection;
			initMapPosToTable();
			this.limitNumberOfRules = params.getInt(LIMIT_NUMBER_OF_RULES);
			
			
		} catch (ConfigurationException e)
		{
			throw new RuleBaseException("Configuration file problem",e);
		} catch (ClassNotFoundException e)
		{
			throw new RuleBaseException("DB driver problem",e);
		} catch (SQLException e)
		{
			throw new RuleBaseException("DB problem",e);
		}
	}
	
	public LinDependencyFromDBLexicalRuleBase(Connection connection, int limit) throws RuleBaseException
	{
		this.connection = connection;
		
		try
		{
			initMapPosToTable();
			this.limitNumberOfRules = limit;
		} catch (SQLException e)
		{
			throw new RuleBaseException("SQL problem.",e);
		}
	}
	

	@Override
	protected ImmutableSet<LexicalRule> getRulesNotInCache(String lhsLemma, PartOfSpeech lhsPos) throws RuleBaseException
	{
//		synchronized(this.getClass())
//		{
//			return getRulesImpl(lhsLemma,lhsPos);
//		}
		return getRulesImpl(lhsLemma,lhsPos);
	}


	
	protected ImmutableSet<LexicalRule> getRulesImpl(String lhsLemma, PartOfSpeech lhsPos) throws RuleBaseException
	{
		try
		{
			Set<LexicalRule> setRules = new LinkedHashSet<LexicalRule>();
			
			if (MAP_POS_TO_TABLE.get(simplerPos(lhsPos.getCanonicalPosTag()))!=null)
			{
				PreparedStatement statement = connection.prepareStatement(MAP_POS_TO_TABLE.get(simplerPos(lhsPos.getCanonicalPosTag())));
				statement.setString(1,lhsLemma);
				statement.setInt(2, limitNumberOfRules);
				
//				linQueryTracker.start();
				ResultSet rs = statement.executeQuery();
				while (rs.next())
				{
					String rhsLemma = rs.getString("word1");
					double score = rs.getDouble("sim");
					setRules.add(new LexicalRule(lhsLemma, lhsPos, rhsLemma, lhsPos, score));
				}
//				linQueryTracker.end();
			}
			return new ImmutableSetWrapper<LexicalRule>(setRules);
		}
		catch(SQLException e)
		{
			throw new RuleBaseException("SQL problem",e);
		}
	}
	

	
	private Connection connection;
	
	private Map<SimplerCanonicalPosTag,String> MAP_POS_TO_TABLE;
	
	private void initMapPosToTable() throws SQLException
	{
		MAP_POS_TO_TABLE = new LinkedHashMap<SimplerCanonicalPosTag, String>();
		
		MAP_POS_TO_TABLE.put(SimplerCanonicalPosTag.ADJECTIVE, QUERY_A);
		MAP_POS_TO_TABLE.put(SimplerCanonicalPosTag.ADVERB, QUERY_A);
		MAP_POS_TO_TABLE.put(SimplerCanonicalPosTag.VERB, QUERY_V);
		MAP_POS_TO_TABLE.put(SimplerCanonicalPosTag.NOUN, QUERY_N);
	}
	
	private int limitNumberOfRules = 0;

	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(LinDependencyFromDBLexicalRuleBase.class);

//	public static final TimeElapsedTracker linQueryTracker = new TimeElapsedTracker();
}
