package eu.excitementproject.eop.transformations.operations.rules.lexical;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSetWrapper;
import eu.excitementproject.eop.common.representation.parse.representation.basic.NamedEntity;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.transformations.operations.finders.Substitution2DLexicalRuleByLemmaPosNerFinder;
import eu.excitementproject.eop.transformations.operations.rules.ByLemmaPosLexicalRuleBaseWithCache;
import eu.excitementproject.eop.transformations.operations.rules.LexicalRule;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseException;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseWithNamedEntities;
import eu.excitementproject.eop.transformations.utilities.TransformationsConfigurationParametersNames;
//import eu.excitementproject.eop.transformations.rteflow.macro.DefaultOperationScript;
//import eu.excitementproject.eop.transformations.rteflow.micro.perform.LexicalRuleByLemmaPos2DPerformFactory;

/**
 * Geographical lexical rule base. Retrieves the rules directly
 * from the data-base.
 * <P>
 * Currently the flow is as follows:<BR>
 * This knowledge resource is constructed in the class {@link DefaultOperationScript},
 * as a regular lexical knowledge resource. There are two diferrences between this
 * class to other lexical knowledge resources:
 * <OL>
 * <LI>It is not created using {@link LexicalResourceWrapper}</LI>
 * <LI>It implements {@link RuleBaseWithNamedEntities}</LI>
 * </OL>
 * The latter property is crucial. When the engine has to apply rules of this resource,
 * it uses {@link LexicalRuleByLemmaPos2DPerformFactory}. In this class, in the method
 * {@link LexicalRuleByLemmaPos2DPerformFactory#getFinder(ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap, ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap, eu.excitementproject.eop.transformations.operations.rules.ByLemmaPosLexicalRuleBase, String)},
 * it uses RTTI and identifies that the rule base implements {@link RuleBaseWithNamedEntities}.<BR>
 * Then, it creates a finder: {@link Substitution2DLexicalRuleByLemmaPosNerFinder} which
 * applies the rules only on parse-tree-nodes that are annotated with a named-entity that
 * is in the set of named entities returned by {@link #getNamedEntitiesOfRuleBase()}.
 * <P>
 * Note that there is no mechanism to handle named entities for the multi-word
 * mechanism of lexical resources. It is a limitation. Thus, in the configuration
 * file the GEO lexical resource should <B>NOT</B> be in the list of multi-word
 * lexical resources.
 * 
 * 
 * @author Asher Stern
 * @since Apr 5, 2012
 *
 */
public class GeoFromDBLexicalRuleBase extends ByLemmaPosLexicalRuleBaseWithCache<LexicalRule> implements RuleBaseWithNamedEntities
{
	public static GeoFromDBLexicalRuleBase fromConfigurationParams(ConfigurationParams params) throws ConfigurationException, RuleBaseException
	{
		try
		{
			Class<?> cls = Class.forName("com.mysql.jdbc.Driver");
			if (logger.isDebugEnabled())
			{
				logger.debug("GeoFromDBLexicalRuleBase: com.mysql.jdbc.Driver: driver loaded from: "+
						cls.getClassLoader().getResource(cls.getName().replace(".", "/")+".class").getPath()
						);
			}
			

			String connectionString = params.get(TransformationsConfigurationParametersNames.CONNECTION_STRING_GEO_PARAMETER_NAME);
			String tableName = params.get(TransformationsConfigurationParametersNames.TABLE_NAME_PARAMETER_NAME);

			Connection connection = DriverManager.getConnection(connectionString);

			Set<NamedEntity> ne = new LinkedHashSet<NamedEntity>();
			ne.add(NamedEntity.LOCATION);
			return new GeoFromDBLexicalRuleBase(connection,tableName,ne);
		}
		catch (SQLException e)
		{
			throw new RuleBaseException("Could not construct GeoFromDBLexicalRuleBase",e);
		}
		catch (ClassNotFoundException e)
		{
			throw new RuleBaseException("Could not construct GeoFromDBLexicalRuleBase",e);
		}
	}

	public GeoFromDBLexicalRuleBase(Connection connection, String tableName,
			Set<NamedEntity> namedEntities) throws RuleBaseException
	{
		super();
		this.connection = connection;
		this.tableName = tableName;
		this.namedEntities = namedEntities;
		init();
	}

	public void init() throws RuleBaseException
	{
		try
		{
			statement = connection.prepareStatement("SELECT * FROM "+tableName+" WHERE entailing LIKE ?");
		}
		catch(SQLException e)
		{
			throw new RuleBaseException("SQL problem",e);
		}
	}

	@Override
	public Set<NamedEntity> getNamedEntitiesOfRuleBase()
			throws RuleBaseException
	{
		return namedEntities;
	}

	
	@Override
	protected ImmutableSet<LexicalRule> getRulesNotInCache(String lhsLemma, PartOfSpeech lhsPos) throws RuleBaseException
	{
		Set<LexicalRule> ret = new LinkedHashSet<LexicalRule>();
		try
		{
			statement.setString(1, lhsLemma);
			ResultSet resultSet = statement.executeQuery();
			while(resultSet.next())
			{
				String entailing = resultSet.getString("entailing");
				String entailed = resultSet.getString("entailed");
				ret.add(new LexicalRule(entailing, lhsPos, entailed, lhsPos, CONFIDENCE));
			}
			return new ImmutableSetWrapper<LexicalRule>(ret);
		}
		catch(SQLException e)
		{
			throw new RuleBaseException("SQL problem",e);
		}
	}
	
	public void cleanUp()
	{
		try
		{
			connection.close();
		} catch (SQLException e)
		{
			// I am "stopping" this exception, since it causes no damage.
			logger.error("Cannot close connection of GEO rule base. Program will continue.",e);

		}
	}


	protected Connection connection;
	protected String tableName;
	protected Set<NamedEntity> namedEntities;
	
	
	protected PreparedStatement statement;
	
	private static final double CONFIDENCE = Math.exp(-1.0);
	
	private static final Logger logger = Logger.getLogger(GeoFromDBLexicalRuleBase.class);
}
