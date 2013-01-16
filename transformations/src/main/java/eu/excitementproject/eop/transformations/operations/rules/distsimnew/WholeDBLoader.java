package eu.excitementproject.eop.transformations.operations.rules.distsimnew;
import static eu.excitementproject.eop.transformations.utilities.TransformationsConfigurationParametersNames.DB_DRIVER;
import static eu.excitementproject.eop.transformations.utilities.TransformationsConfigurationParametersNames.DB_URL;
import static eu.excitementproject.eop.transformations.utilities.TransformationsConfigurationParametersNames.LIMIT_NUMBER_OF_RULES;
import static eu.excitementproject.eop.transformations.utilities.TransformationsConfigurationParametersNames.RULES_TABLE_NAME;
import static eu.excitementproject.eop.transformations.utilities.TransformationsConfigurationParametersNames.TEMPLATES_TABLE_NAME;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.common.datastructures.BidirectionalMap;
import eu.excitementproject.eop.common.datastructures.SimpleBidirectionalMap;
import eu.excitementproject.eop.common.datastructures.SimpleValueSetMap;
import eu.excitementproject.eop.common.datastructures.ValueSetMap;
import eu.excitementproject.eop.common.utilities.ExceptionUtil;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.common.utilities.log4j.BasicVerySimpleLoggerInitializer;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseException;
import eu.excitementproject.eop.transformations.operations.rules.distsim.DistSimParameters;
import eu.excitementproject.eop.transformations.utilities.Constants;

/**
 * Loads the whole data-base of a DIRT-like rules.
 * <P>
 * <B> !!! Note that it is no longer used !!! </B><BR>
 * It takes a very long time to load the whole DB.
 * I wrote it just to test whether it improves performance.
 * 
 * @author Asher Stern
 * @since Dec 6, 2011
 *
 */
public class WholeDBLoader
{
	public WholeDBLoader(Connection connection, DistSimParameters dbParameters)
	{
		super();
		this.connection = connection;
		this.dbParameters = dbParameters;
	}
	
	
	public void createMapTemplateToId() throws SQLException, RuleBaseException
	{
		mapTemplateToId = new SimpleBidirectionalMap<String, Integer>();
		String query = "SELECT id,description FROM "+dbParameters.getTemplatesTableName();
		Statement statement = connection.createStatement();
		try
		{
			ResultSet resultSet = statement.executeQuery(query);
			while(resultSet.next())
			{
				int id = resultSet.getInt("id");
				String description = resultSet.getString("description");
				if (description.isEmpty())
					;	// ignore rows with an empty pattern-description - they represent duplicates of another row that represents their pattern.
				else
				{
					if (mapTemplateToId.leftContains(description)) throw new RuleBaseException("Malformed rule base. Template "+description+" appears more than once.");
					mapTemplateToId.put(description, id);
				}
			}
		}
		finally
		{
			statement.close();
		}
	}
	
	public void createMapAllRules() throws SQLException
	{
		// get all ids
		logger.debug("Loading all IDs...");
		Set<Integer> setAllIds = new LinkedHashSet<Integer>();
		Statement stmtAllIds = connection.createStatement();
		try
		{
			ResultSet resultAllIds = stmtAllIds.executeQuery("SELECT left_element_id FROM "+dbParameters.getRulesTableName());
			while (resultAllIds.next())
			{
				setAllIds.add(resultAllIds.getInt("left_element_id"));
			}
			logger.debug("Loading all IDs done.");
		}
		finally
		{
			stmtAllIds.close();
		}
		
		mapAllRules = new SimpleValueSetMap<Integer, IdAndScore>();

		String query = "SELECT left_element_id,right_element_id,score FROM "+dbParameters.getRulesTableName()+" WHERE left_element_id = ? ORDER BY score DESC LIMIT "+String.valueOf(dbParameters.getLimitNumberOfRules());
		PreparedStatement preparedStatementAllRules = connection.prepareStatement(query);
		try
		{
			int countRules = 0;
			for (Integer id : setAllIds)
			{
				preparedStatementAllRules.setInt(1, id);
				ResultSet resultSetAllRulesForId = preparedStatementAllRules.executeQuery();
				while (resultSetAllRulesForId.next())
				{
					int right_id = resultSetAllRulesForId.getInt("right_element_id");
					double score = resultSetAllRulesForId.getDouble("score");
					IdAndScore idAndScore = new IdAndScore(right_id, score);
					mapAllRules.put(id,idAndScore);
					++countRules;
				}
				if (logger.isDebugEnabled()){logger.debug("Rules loaded... "+countRules);}
			}
		}
		finally
		{
			preparedStatementAllRules.close();
		}
	}
	
	public BidirectionalMap<String, Integer> getMapTemplateToId()
	{
		return mapTemplateToId;
	}
	public ValueSetMap<Integer, IdAndScore> getMapAllRules()
	{
		return mapAllRules;
	}
	
	/**
	 * Stores DB in one serialization file
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			if (args.length<3)throw new RuntimeException("args.length<3");
			new BasicVerySimpleLoggerInitializer().initLogger();
			//new LogInitializer(args[0]).init();
			ConfigurationFile confFile = new ConfigurationFile(args[0]);
			
			String module = args[1];
			String serFileName = args[2];
			ConfigurationParams params = confFile.getModuleConfiguration(module);
			
			Class.forName(params.get(DB_DRIVER));
			String dbUrl = params.get(DB_URL);
			Connection connection = DriverManager.getConnection(dbUrl);
			String templates = params.get(TEMPLATES_TABLE_NAME);
			String rules = params.get(RULES_TABLE_NAME);
			int limit = params.getInt(LIMIT_NUMBER_OF_RULES);
			DistSimParameters distSimParameters = new DistSimParameters(templates, rules, limit, Constants.DEFAULT_DIRT_LIKE_RESOURCES_CACHE_SIZE, Constants.DEFAULT_DIRT_LIKE_RESOURCES_CACHE_SIZE);

			WholeDBLoader loader = new WholeDBLoader(connection, distSimParameters);
			loader.createMapTemplateToId();
			loader.createMapAllRules();
			
			ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(new File(serFileName)));
			try
			{
				output.writeObject(loader.getMapTemplateToId());
				output.writeObject(loader.getMapAllRules());
			}
			finally
			{
				output.close();
			}
			
		}
		catch(Exception e)
		{
			ExceptionUtil.outputException(e, System.out);
			try{ExceptionUtil.logException(e, logger);}catch(Exception ex){}
		}
		
	}




	protected Connection connection=null;
	protected DistSimParameters dbParameters;
	
	protected BidirectionalMap<String, Integer> mapTemplateToId = null;
	protected ValueSetMap<Integer, IdAndScore> mapAllRules;
	
	private static final Logger logger = Logger.getLogger(WholeDBLoader.class);
}
