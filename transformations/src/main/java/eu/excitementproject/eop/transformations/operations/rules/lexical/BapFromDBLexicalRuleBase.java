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
import java.util.LinkedHashSet;
import java.util.Set;

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
 * Lexical-resource of BAP - Directional Similarity.<BR>
 * @deprecated No longer used since the new lexical-resources have been available.
 * 
 * @author Asher Stern
 * @since Mar 22, 2011
 *
 */
@Deprecated
public class BapFromDBLexicalRuleBase extends ByLemmaPosLexicalRuleBaseWithCache<LexicalRule>
{
	public static BapFromDBLexicalRuleBase fromConfigurationParams(ConfigurationParams params) throws RuleBaseException
	{
		try
		{
			String driver = params.get(DB_DRIVER);
			String url = params.get(DB_URL);
			int limit = params.getInt(LIMIT_NUMBER_OF_RULES);
			
			Class.forName(driver);
			Connection connection = DriverManager.getConnection(url);
			
			return new BapFromDBLexicalRuleBase(connection, limit); 
		}
		catch (ConfigurationException e)
		{
			throw new RuleBaseException("failed to read parameters for BAP rule base",e);
		}
		catch (ClassNotFoundException e)
		{
			throw new RuleBaseException("failed to initialize SQL driver for BAP rule base",e);
		}
		catch (SQLException e)
		{
			throw new RuleBaseException("failed to connect to SQL for BAP rule base",e);
		}
	}
	
	public BapFromDBLexicalRuleBase(Connection connection, int limit)
	{
		super();
		this.connection = connection;
		this.limit = limit;
		this.limitString = String.valueOf(limit);
	}
	
	



	@Override
	protected ImmutableSet<LexicalRule> getRulesNotInCache(String lhsLemma, PartOfSpeech lhsPos)
			throws RuleBaseException
	{
		Set<LexicalRule> rules = new LinkedHashSet<LexicalRule>();
		String tableName = tableNameByPos(lhsPos);
		if (tableName!=null)
		{
			try
			{
				String query = "SELECT * FROM "+tableName+" WHERE lhs like ? ORDER BY score DESC LIMIT "+limitString;
				PreparedStatement statement = connection.prepareStatement(query);
				statement.setString(1, lhsLemma);
				ResultSet resultSet = statement.executeQuery();
				while(resultSet.next())
				{
					String rhsLemma = resultSet.getString("rhs");
					double score = resultSet.getDouble("score");
					
					rules.add(new LexicalRule(lhsLemma,lhsPos,rhsLemma,lhsPos,score));
				}
				

			}
			catch(SQLException e)
			{
				throw new RuleBaseException("SQL failure",e);
			}

		}
		
		return new ImmutableSetWrapper<LexicalRule>(rules);
	}

	
	private String tableNameByPos(PartOfSpeech pos)
	{
		String ret = null;
		if (simplerPos(pos.getCanonicalPosTag())==SimplerCanonicalPosTag.VERB)
			ret = "verbs_200";
		else if (simplerPos(pos.getCanonicalPosTag())==SimplerCanonicalPosTag.NOUN)
			ret = "nouns_200";
		
		return ret;
	}
	
	private Connection connection;
	@SuppressWarnings("unused")
	private int limit;
	private String limitString;
}
