package eu.excitementproject.eop.lexicalminer.dataAccessLayer;


import java.sql.DriverManager;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.lexicalminer.LexiclRulesRetrieval.RuleData;


/*
 * This class purpose is to execute all the DB-involved operations for the retrieval operations.
 * All operations are state less for concurrent support
 * 
 */
public class RetrievalTool {

	private static int counter=0;
	private static Connection connection=null;

	
	// this query gets all the rules when the left side is an input
		private static String leftQuery =
					" SELECT * FROM "
					+ " (select rules.*, ranks.rank classifierRank from (" 
					+"	select rules.*, pat.POSPattern , pat.wordsPattern , pat.relationsPattern , pat.POSrelationsPattern, pat.fullPattern from ("
					+" 		SELECT ? classifierId, a.id ruleID, tl.value leftTerm, tr.value rightTerm , rr.reSource ruleResource, "
					+" 			rt.defultRank, rt.ruleName ruleType ,a.ruleMetadata , a.ruleSourceId "
					+" 		FROM rules a, terms tl, terms tr,  ruleresources rr, ruletypes rt "
					+" 		where tl.id = a.leftTermId and tr.id = a.rightTermId "
					+" 		and rr.id = a.ruleResource and rt.id = a.ruleType "
					+" 		and tl.value = ? "
					+"	) rules LEFT JOIN rulepatterns pat "
					+"	ON pat.ruleId = rules.ruleID "
					+") rules LEFT JOIN "
					+"	rulesranks ranks "
					+" ON ranks.ruleId = rules.ruleId and ranks.classifierId = rules.classifierId"
					+" ) rules "
					+" order by rules.classifierRank desc, rules.defultRank desc ";

		// this query gets all the rules when the right side is an input
		private static String rightQuery =
				" SELECT * FROM "
				+ " (select rules.*, ranks.rank classifierRank from (" 
				+"	select rules.*, pat.POSPattern , pat.wordsPattern , pat.relationsPattern , pat.POSrelationsPattern, pat.fullPattern from ("
				+" 		SELECT ? classifierId, a.id ruleID, tl.value leftTerm, tr.value rightTerm , rr.reSource ruleResource, "
				+" 			rt.defultRank, rt.ruleName ruleType ,a.ruleMetadata , a.ruleSourceId "
				+" 		FROM rules a, terms tl, terms tr,  ruleresources rr, ruletypes rt "
				+" 		where tl.id = a.leftTermId and tr.id = a.rightTermId "
				+" 		and rr.id = a.ruleResource and rt.id = a.ruleType "
				+" 		and tr.value = ? "
				+"	) rules LEFT JOIN rulepatterns pat "
				+"	ON pat.ruleId = rules.ruleID "
				+") rules LEFT JOIN "
				+"	rulesranks ranks "
				+" ON ranks.ruleId = rules.ruleId and ranks.classifierId = rules.classifierId"
				+" ) rules "
				+" order by rules.classifierRank desc, rules.defultRank desc ";
		
		// this query gets all the rules when the both sides are inputs - use this to get the rank and the other metadata
		private static String bothSidesQuery =
				" SELECT * FROM "
				+ " (select rules.*, ranks.rank classifierRank from (" 
				+"	select rules.*, pat.POSPattern , pat.wordsPattern , pat.relationsPattern , pat.POSrelationsPattern, pat.fullPattern from ("
				+" 		SELECT ? classifierId, a.id ruleID, tl.value leftTerm, tr.value rightTerm, rr.reSource ruleResource, "
				+" 			rt.defultRank, rt.ruleName ruleType ,a.ruleMetadata , a.ruleSourceId "
				+" 		FROM rules a, terms tl, terms tr,  ruleresources rr, ruletypes rt "
				+" 		where tl.id = a.leftTermId and tr.id = a.rightTermId "
				+" 		and rr.id = a.ruleResource and rt.id = a.ruleType "
				+" 		and tl.value = ? "
				+" 		and tr.value = ? "			
				+"	) rules LEFT JOIN rulepatterns pat "
				+"	ON pat.ruleId = rules.ruleID "
				+") rules LEFT JOIN "
				+"	rulesranks ranks "
				+" ON ranks.ruleId = rules.ruleId and ranks.classifierId = rules.classidierId"
				+" ) rules "
				+" order by rules.classifierRank desc, rules.defultRank desc ";	
	

	private String m_driver;
	private String m_url;
	private String m_username;
	private String m_password; 
	protected org.apache.log4j.Logger m_logger;
	
	public RetrievalTool(ConfigurationParams params) throws ConfigurationException
	{
		try {
			m_driver = params.get("driver");
			m_url = params.get("url");
			m_username = params.get("username");
			m_password = params.get("password");
			m_logger = org.apache.log4j.Logger.getLogger(RetrievalTool.class.getName());
			
		} catch (ConfigurationException e) {
			throw new ConfigurationException("Error getting parameters for Database connection. make sure you have values for driver,url,username and password params in Database module on the configuration file you supplied", e);
		}
	}			
	
	public RetrievalTool(String  driver, String  url, String  username,
			String  password) {
		this.m_driver = driver;
		this.m_url = url;
		this.m_username = username;
		this.m_password = password;
	}

	public List<RuleData> getRulesForRight(String lemma, int classifierID) throws LexicalResourceException, UnsupportedPosTagStringException, SQLException
	{
		return getRulesForQuery(lemma, rightQuery,  classifierID);
	}
	
	public List<RuleData> getRulesForLeft(String lemma, int classifierID) throws LexicalResourceException, UnsupportedPosTagStringException, SQLException
	{	
		return getRulesForQuery(lemma, leftQuery,  classifierID);
	}
	
	public List<RuleData> getRulesForBothSides(String leftLemma, String rightLemma,  int classifierID) throws LexicalResourceException, UnsupportedPosTagStringException, SQLException
	{	
		return getRulesForQuery(leftLemma, rightLemma, bothSidesQuery,  classifierID);
	}	

	/**
	 * gets the ID of the classifier from DB
	 * @param calssifierName
	 * @param insertNewClassifier	- if true: make sure it's the first insert of that name to the DB (if not-throws SQLException)
	 * @return ID of the classifier
	 * @throws SQLException
	 */
	public int getClassifierId(String calssifierName, boolean insertNewClassifier) throws SQLException
	{
		Connection conn = getMySqlConnection();
		
		int id = (-2);
		
		String query;
		if (insertNewClassifier)	//make sure it's the first, if not -throw exception
		{
			query = "select insertNewClassifier(?) id from dual";
		}
		else
		{
			query = "select getClassifierId(?) id from dual";		
		}
		PreparedStatement cs = (PreparedStatement) conn.prepareStatement(query);
		cs.setString(1, calssifierName);
		ResultSet rs = cs.executeQuery();

		 while (rs.next())
	      {
			 id=  rs.getInt("id");
	      }

		if (id == (-2))
		{
			 throw new  SQLException("Can't get Id for classifier name: " + calssifierName);
		}
		else if (id == (-1))
		{
			 throw new  SQLException("Classifier Name allreay exists: " + calssifierName);	
		}
		return id;
	}
	
	private List<RuleData> getRulesForQuery(
			String lemma, String query, int classifierID) throws SQLException,
			LexicalResourceException, UnsupportedPosTagStringException {
		return getRulesForQuery(lemma, null, query, classifierID);
	}
	
	private List<RuleData> getRulesForQuery(
			String first_lemma, String second_lemma, String query, int classifierID) throws SQLException,
			LexicalResourceException, UnsupportedPosTagStringException {
		List<RuleData> res = new ArrayList<RuleData>();
		
		Connection conn = getMySqlConnection();
		
		PreparedStatement cs = (PreparedStatement) conn.prepareStatement(query);
		
		cs.setInt(1, classifierID);
		cs.setString(2, first_lemma);
		if (second_lemma != null)
		{
			cs.setString(3, second_lemma);
		}
		
		ResultSet rs = cs.executeQuery();

		 while (rs.next())
	      {
			 
			 int ruleId = rs.getInt("ruleID");
				String leftTerm = rs.getString("leftTerm");
				String rightTerm = rs.getString("rightTerm");
				String ruleResource = rs.getString("ruleResource");
				double defultRank = rs.getDouble("defultRank");
				String ruleType = rs.getString("ruleType");
				String ruleMetadata = rs.getString("ruleMetadata");
				int ruleSourceId = rs.getInt("ruleSourceId");
				String POSPattern  = rs.getString("POSPattern");
				String wordsPattern  = rs.getString("wordsPattern");
				String relationsPattern  = rs.getString("relationsPattern");
				String POSrelationsPattern  = rs.getString("POSrelationsPattern");				
				String fullPattern  = rs.getString("fullPattern");
				Double classifierRank = rs.getDouble("classifierRank");				
				
				res.add(new RuleData(leftTerm, rightTerm, POSPattern, wordsPattern, relationsPattern, POSrelationsPattern, fullPattern, defultRank, 
										classifierRank, ruleId, ruleResource, ruleType, ruleMetadata, ruleSourceId));
	      }
		 
		 closeConnection();
		return res;
	}

	public static void forceCloseConnection() throws SQLException
	{
		counter =1;
		closeConnection();
	}
	
	public static void closeConnection() throws SQLException
	{
		if (counter > 1)
		{
			counter--;
		}
		else
		{
			if (connection != null)
				connection.close();
			counter = 0;
		}
	}
	
	  public Connection getMySqlConnection() throws SQLException {
		    try {
				Class.forName(m_driver);
			} catch (ClassNotFoundException e) {
				m_logger.fatal(String.format("ClassNotFoundException exception, did not fount class for: %s. ",m_driver),e);
			}
		    if (counter == 0 )
		    {
			    connection = (Connection) DriverManager.getConnection(m_url, m_username, m_password);		   
			    
		    }
		    counter++;
		    
		    return connection;
		  }
	
}
