package eu.excitementproject.eop.transformations.operations.rules.lexical;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResource;
import eu.excitementproject.eop.common.datastructures.DummySet;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSetWrapper;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.transformations.codeannotations.Workaround;
import eu.excitementproject.eop.transformations.operations.rules.ByLemmaPosLexicalRuleBaseWithCache;
import eu.excitementproject.eop.transformations.operations.rules.LexicalRule;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseException;
import eu.excitementproject.eop.transformations.utilities.TransformationsConfigurationParametersNames;

/**
 * Wraps "Lin similarity" knowledge resource that has been calculated over the
 * Reuters corpus.
 * Though there is a wrapper in the new lexical resources (a {@link LexicalResource}),
 * I had to wrap it and read it directly from the data-base, since it has some
 * errors in the scores, which cause the lexical-resource of infrastructure to
 * throw exceptions.
 * <P>
 * Note that here all the rules get the same constant score, regardless the score
 * in the data-base, due to the above-mentioned problem.
 * <BR>
 * It is a workaround.
 * 
 * 
 * @author Asher Stern
 * @since Apr 22, 2012
 *
 */
@Workaround
public class LinReutersFromDBLexicalResource extends ByLemmaPosLexicalRuleBaseWithCache<LexicalRule>
{
	public static final String TABLE_NAME = "lin_rules_lemmas";
	public static final String SCORE_COLUMN = "score";
	public static final String LEFT_COLUMN = "left_element";
	public static final String RIGHT_COLUMN = "right_element";
	public static final boolean USE_CONSTANT_SCORE = true;
	
	public static LinReutersFromDBLexicalResource fromParams(ConfigurationParams params) throws SQLException, ConfigurationException
	{
		String connectionString = params.getString(TransformationsConfigurationParametersNames.CONNECTION_STRING_LIN_REUTERS_PARAMETER_NAME);
		Connection connection = DriverManager.getConnection(connectionString);
		return new LinReutersFromDBLexicalResource(
				connection,
				params.getInt(TransformationsConfigurationParametersNames.LIMIT_LIN_REUTERS_PARAMETER_NAME)
				);
	}
	
	public LinReutersFromDBLexicalResource(Connection connection, int limit) throws SQLException
	{
		super();
		this.connection = connection;
		this.limit = limit;
		
		statement = connection.prepareStatement("SELECT * FROM "+TABLE_NAME+" WHERE "+LEFT_COLUMN+" LIKE ? LIMIT "+String.valueOf(limit));
	}
	
	
	@Override
	protected ImmutableSet<LexicalRule> getRulesNotInCache(String lhsLemma, PartOfSpeech lhsPos) throws RuleBaseException
	{
		lhsLemma = lhsLemma.trim();
		if (lhsLemma.length()==0)
			return new ImmutableSetWrapper<LexicalRule>(new DummySet<LexicalRule>());

		try
		{
			statement.setString(1, lhsLemma);
			ResultSet resultSet = statement.executeQuery();
			Set<LexicalRule> setRules = new LinkedHashSet<LexicalRule>();
			while (resultSet.next())
			{
				String right = resultSet.getString(RIGHT_COLUMN);
				double score = E_MINUS_1;
				if (!USE_CONSTANT_SCORE)
				{
					score = resultSet.getDouble(SCORE_COLUMN);
				}
				setRules.add(new LexicalRule(lhsLemma, lhsPos, right, lhsPos, score));
			}
			return new ImmutableSetWrapper<LexicalRule>(setRules);
		}
		catch(SQLException e)
		{
			throw new RuleBaseException("Failed due to SQL exception.",e);
		}
		
	}
	

	@SuppressWarnings("unused")
	private Connection connection;
	@SuppressWarnings("unused")
	private int limit;
	
	private PreparedStatement statement = null;
	
	private static final double E_MINUS_1 = Math.exp(-1.0);
}
