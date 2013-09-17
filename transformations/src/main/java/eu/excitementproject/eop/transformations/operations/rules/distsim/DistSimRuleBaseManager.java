package eu.excitementproject.eop.transformations.operations.rules.distsim;
import static eu.excitementproject.eop.transformations.utilities.TransformationsConfigurationParametersNames.DB_DRIVER;
import static eu.excitementproject.eop.transformations.utilities.TransformationsConfigurationParametersNames.DB_URL;
import static eu.excitementproject.eop.transformations.utilities.TransformationsConfigurationParametersNames.LIMIT_NUMBER_OF_RULES;
import static eu.excitementproject.eop.transformations.utilities.TransformationsConfigurationParametersNames.RULES_TABLE_NAME;
import static eu.excitementproject.eop.transformations.utilities.TransformationsConfigurationParametersNames.TEMPLATES_TABLE_NAME;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.core.component.syntacticknowledge.utilities.PARSER;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseException;
import eu.excitementproject.eop.transformations.utilities.Constants;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;


/**
 * 
 * @deprecated No longer used.
 * 
 * @author Asher Stern
 * @since Mar 9, 2011
 *
 */
@Deprecated
public class DistSimRuleBaseManager
{
	public DistSimRuleBaseManager(String name, ConfigurationParams params, PARSER parser)
	{
		super();
		this.name = name;
		this.params = params;
		this.parser = parser;
	}

	
	
	public void init() throws ConfigurationException, ClassNotFoundException, SQLException, RuleBaseException
	{
		String driverName = params.get(DB_DRIVER);
		Class.forName(driverName);
		
		String dbUrl = params.get(DB_URL);
		
		connection = DriverManager.getConnection(dbUrl);
		
		String templates = params.get(TEMPLATES_TABLE_NAME);
		String rules = params.get(RULES_TABLE_NAME);
		int limit = params.getInt(LIMIT_NUMBER_OF_RULES);

		// Hard coded number. Nevermind, this class is anyway deprecated.
		int lhsCacheSize = 2*Constants.DEFAULT_DIRT_LIKE_RESOURCES_CACHE_SIZE;
		int rulesCacheSize = Constants.DEFAULT_DIRT_LIKE_RESOURCES_CACHE_SIZE;
//		int lhsCacheSize = Constants.CACHES_SIZE.get(name+Constants.CACHE_DISTSIM_NAME_LHS_POSTFIX);
//		int rulesCacheSize = Constants.CACHES_SIZE.get(name+Constants.CACHE_DISTSIM_NAME_RULES_POSTFIX);
		DistSimParameters distSimParameters = new DistSimParameters(templates, rules, limit, lhsCacheSize, rulesCacheSize);
		
		this.ruleBase = new DistSimRuleBase(connection, distSimParameters, name, parser);
		
		
		
	}
	
	public DistSimRuleBase getRuleBase() throws TeEngineMlException
	{
		if (this.ruleBase==null)
			throw new TeEngineMlException("Not initialized");
		
		return this.ruleBase;
	}
	
	public void cleanUp() throws SQLException
	{
		if (connection!=null)
			connection.close();
		
	}

	protected final PARSER parser;
	protected String name;
	protected ConfigurationParams params;
	
	protected DistSimRuleBase ruleBase = null;
	protected Connection connection=null;
}
