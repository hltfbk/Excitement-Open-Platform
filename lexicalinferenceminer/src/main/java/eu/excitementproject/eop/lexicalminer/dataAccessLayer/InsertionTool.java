package eu.excitementproject.eop.lexicalminer.dataAccessLayer;

import java.sql.DriverManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

import com.mysql.jdbc.CallableStatement;
import com.mysql.jdbc.PreparedStatement;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.component.lexicalknowledge.RuleInfo;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.lexicalminer.definition.Common.BaseRuleInfo;
import eu.excitementproject.eop.lexicalminer.definition.Common.PatternRuleInfo.PatternRuleInfo;


/*
 * This class purpose is to execute all the DB-involved operations of the rules creation process.
 * All operations are state less for concurrent support
 * 
 */
public class InsertionTool {

	private  java.sql.Connection connection=null;
	private int waitingForExecuteBatchCount =0;
	private int m_insertionBatchSize;
	private CallableStatement m_cs;
	private java.sql.Connection m_connection;

	
    String m_driver;
    String m_url;
    String m_username;
    String m_password;

	public InsertionTool(ConfigurationParams params) throws ConfigurationException
	{
		try {
			m_driver = params.get("driver");
			m_url = params.get("url");
			m_username = params.get("username");
			m_password = params.get("password");
			m_insertionBatchSize=params.getInt("insertion batch size");
		} catch (ConfigurationException e) {
			throw new ConfigurationException("Error getting parameters for Database connection. make sure you have values for driver,url,username and password params in Database module on the configuration file you supplied", e);
		}
	}
	
	public void AddRule(LexicalRule<RuleInfo> lexicalRule) throws SQLException
	{
		if (lexicalRule.getInfo() instanceof PatternRuleInfo)
				AddPatternRule(lexicalRule);
		else
			AddSimpleRule(lexicalRule);
	}

	private void AddPatternRule(LexicalRule<RuleInfo> lexicalRule) throws SQLException
	{

		PatternRuleInfo patternRuleInfo = (PatternRuleInfo)lexicalRule.getInfo();
		
		
		AddRule(lexicalRule.getLLemma(),	lexicalRule.getRLemma(),
				lexicalRule.getResourceName(),
				patternRuleInfo.getPosPattern(),patternRuleInfo.getWordsPattern(),
				patternRuleInfo.getRelationPattern(),
				patternRuleInfo.getPosRelationPattern(),
				patternRuleInfo.getFullPattern(),
				lexicalRule.getRelation(),
				patternRuleInfo.getMetadata(),
				patternRuleInfo.GetSourceId(),
				lexicalRule);
	}
		

	private void AddSimpleRule(LexicalRule<RuleInfo> lexicalRule) throws SQLException
	{
		BaseRuleInfo baseRuleInfo = (BaseRuleInfo)lexicalRule.getInfo();
		
		
		AddRule(lexicalRule.getLLemma(),
				lexicalRule.getRLemma(),
				lexicalRule.getResourceName(),
				null,null,
				null,null, null,
				lexicalRule.getRelation(),
				baseRuleInfo.getMetadata(),
				baseRuleInfo.GetSourceId(),
				lexicalRule);
	}
	
	private void AddRule(String left_Term, 
			String right_Term,
			String rule_Resource, String rule_POS_Pattern,
			String rule_Words_Pattern,String rule_Relations_Pattern, String rule_POS_Relations_Pattern, 
			String rule_Full_Pattern,
			String rule_Type,String rule_Metadata,int rule_Source_Id,
			LexicalRule<RuleInfo> serialization_blob) throws SQLException
	{
		try
		{
			InesertPatternRule
			(left_Term,
					right_Term,
					rule_Resource, rule_POS_Pattern,
					rule_Words_Pattern,rule_Relations_Pattern, 
					rule_POS_Relations_Pattern,
					rule_Full_Pattern,
					rule_Type,rule_Metadata,rule_Source_Id,
					serialization_blob);
			}
		catch (Exception e)
		{
			try // in case we have an open connection - close it first
			{
				connection.close();
			}
			catch (Exception ex) // in case it was already closed
			{}
			
			e.printStackTrace();
			InesertPatternRule
						(left_Term,	right_Term,
								rule_Resource, rule_POS_Pattern,
								rule_Words_Pattern,rule_Relations_Pattern, rule_POS_Relations_Pattern, rule_Full_Pattern,
								rule_Type,rule_Metadata,rule_Source_Id,
								serialization_blob);
		}
	}
	
	

	private void InesertPatternRule(String left_Term, String right_Term,
			String rule_Resource, String rule_POS_Pattern,
			String rule_Words_Pattern,String rule_Relations_Pattern, 
			String rule_POS_Relations_Pattern, 
			String rule_Full_Pattern,
			String rule_Type,String rule_Metadata,int rule_Source_Id,
			LexicalRule<RuleInfo> serialization_blob) throws SQLException
	{
		
			
	        
		// Step-1: Get or create a connection
	    if (m_connection==null)
			m_connection = getNewtMySqlConnection();
		   
	    // Step-2: identify the stored procedure
	    String simpleProc = "{ call insertPatternRule(?,?,?,?,?,?,?,?,?,?,?,?) }";
	    //String simpleProc = "{ call insertPatternRule(?,?,?,?,?,?,?,?,?,?,?,?,?) }";
	    // Step-3: prepare the callable statement
	    
	    if (m_cs==null)
	    	m_cs = (CallableStatement) m_connection.prepareCall(simpleProc);
	    
	    // Step-4: register output parameters ...
	    m_cs.setString("left_Term", left_Term.toLowerCase());
	    m_cs.setString("right_Term", right_Term);
	    m_cs.setString("rule_Resource", rule_Resource);
	    m_cs.setString("rule_POS_Pattern", rule_POS_Pattern);
	    m_cs.setString("rule_Words_Pattern", rule_Words_Pattern);
	    m_cs.setString("rule_Relations_Pattern", rule_Relations_Pattern);
	    m_cs.setString("rule_POS_Relations_Pattern", rule_POS_Relations_Pattern);
	    m_cs.setString("rule_Full_Pattern", rule_Full_Pattern);
	    m_cs.setString("rule_Type", rule_Type);
	    m_cs.setString("rule_Metadata", rule_Metadata);
	    m_cs.setInt("rule_Source_Id", rule_Source_Id);
//	    ByteArrayInputStream bais =
//                new ByteArrayInputStream(bos.toByteArray());
	    
	    m_cs.setBinaryStream("serialization_blob", null);
	    // Step-5: add the procedure to the statement
	    m_cs.addBatch();

	    waitingForExecuteBatchCount++;
	    
	    if (waitingForExecuteBatchCount % m_insertionBatchSize == 0) // Step-7: write to db every BATCH_COUNT insertions
	    {
	    	m_cs.executeBatch();
	    	m_connection.close();
	    	m_cs=null;
	    	m_connection=null;
	    }
	}
	
	
	/*
	 * This function must be called after finishing all the insertions
	 * InsertionTool send the data to the DB only every BATCH_SIZE insertions
	 * so if the total count of rules is not divided by BTACH_SIZE this method
	 * send the leftovers to the DB.
	 */
	public void manualFlushAndCloseConnection() throws SQLException
	{
		if (m_connection!=null && m_cs!=null)
		{
			m_cs.executeBatch();
			m_connection.close();
			m_cs=null;
		}
	}

	
	  private java.sql.Connection getNewtMySqlConnection() throws SQLException {

		    

		    try {
				Class.forName(m_driver);
			} catch (ClassNotFoundException e) {

				throw new SQLException("can't get the driver class for the InsertionTool. (Check inner exception for details) Driver class:"+m_driver, e);
			}
		      return DriverManager.getConnection(m_url, m_username, m_password);		       

				
		  }


	
	  
	  /*
	   * get the id of extraction method with the given name
	   */
	public int getExtractionMethodIDByName(String extractionName) throws SQLException {
		java.sql.Connection conn = getNewtMySqlConnection();
		PreparedStatement cs = (PreparedStatement) conn.prepareStatement("SELECT id FROM ruletypes where ruleName =  ?;");
		
		cs.setString(1, extractionName);
		
		ResultSet rs = cs.executeQuery();
		
		int id=-1;
		while (rs.next())
		{

			id = rs.getInt(1);
		}
		conn.close();
		
		return id;
		
		
	}

	
	/*
	 * get all the sourceIds of articles we already extracted for the given method
	 */
	public HashSet<Integer> getDoneBeforeCrashForType(int extractionMethodID) throws SQLException {

		HashSet<Integer> results = new HashSet<Integer>();
		java.sql.Connection conn = getNewtMySqlConnection();
		PreparedStatement cs = (PreparedStatement) conn.prepareStatement("SELECT ruleSourceId FROM .rules where ruletype = ? group by ruleSourceId;");
		
		cs.setInt(1, extractionMethodID);
		
		ResultSet rs = cs.executeQuery();
		
		while (rs.next())
		{
			results.add(rs.getInt(1));
		}
		conn.close();
		
		return results;
	}
	
}
