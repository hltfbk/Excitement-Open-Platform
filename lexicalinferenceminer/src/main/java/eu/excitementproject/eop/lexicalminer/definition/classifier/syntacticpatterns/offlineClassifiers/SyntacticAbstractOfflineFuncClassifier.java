package eu.excitementproject.eop.lexicalminer.definition.classifier.syntacticpatterns.offlineClassifiers;

import java.sql.ResultSet;


import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

import eu.excitementproject.eop.lexicalminer.dataAccessLayer.RetrievalTool;
import eu.excitementproject.eop.lexicalminer.definition.classifier.OfflineClassifier;


public abstract class SyntacticAbstractOfflineFuncClassifier extends OfflineClassifier {

	private static Logger logger = Logger.getLogger(SyntacticAbstractOfflineFuncClassifier.class);
	
	protected String m_PatternNameColumn = null;
	protected String m_PatternKind = null;
	protected String m_RankQuery = null ;
	protected String m_TotalSelectFunc = null ; 
	
	protected abstract void setM_PatternNameColumn();

	protected abstract void setM_PatternKind();

	protected abstract void setM_RankQuery();

	protected abstract void setM_TotalSelectFunc();
		
	protected SyntacticAbstractOfflineFuncClassifier(RetrievalTool retrivalTool, double NPBonus) {
		super(retrivalTool, NPBonus);
		setM_PatternNameColumn();
		setM_PatternKind();
		setM_RankQuery();
		setM_TotalSelectFunc();	}
	
	
	@Override
	public final void setAllRanksOffline() throws SQLException
	{	
		int classifierId = this.getClassifierId();
		new DALClass().setRanksByPatternLocation(m_PatternKind, m_RankQuery , m_TotalSelectFunc,  classifierId);
	}

	private final class DALClass 
	{			
		public void setRanksByPatternLocation(String patternKind , String rankQuery, String select_func, int classifierId) throws SQLException
		{
			int patternType =getPatternType(patternKind);
			int pattern_count = getDifferntPatternsCount(patternType , select_func);
			if (pattern_count == 0)
				throw new SQLException("No rule pattern was found in the database");
			float pattern_mul = (1/(float)(pattern_count));

			String query =  " insert into rulesranks(ruleId,classifierId,rank) " + rankQuery;
					
			logger.info("classifierId= " + classifierId);
			logger.info("pattern_mul= " + pattern_mul);
			logger.info("patternType= " + patternType);
			
			Connection conn = m_retrivalTool.getMySqlConnection();
			PreparedStatement cs = (PreparedStatement) conn.prepareStatement(query);
			cs.setInt(1, classifierId);
			cs.setFloat(2, pattern_mul);
			cs.setInt(3, patternType);		

			cs.execute();
			RetrievalTool.closeConnection();			
		}		
		
		/**
		 * Calls the DB and return a number
		 * @param parameter1	-- what value to put as a parameter to the query(if null- won't use it) 
		 * @param query			-- query to run, the return value will be in column "num"
		 * @return	the res of the query
		 * @throws SQLException
		 */
		private int getNumFromDB(String parameter1, String query, String numColumnName) throws SQLException {
			Connection conn = m_retrivalTool.getMySqlConnection();
			PreparedStatement cs = (PreparedStatement) conn.prepareStatement(query);
			
			if (parameter1 != null)
			{
				cs.setString(1, parameter1);
			}
			ResultSet rs = cs.executeQuery();
			RetrievalTool.closeConnection();
			while (rs.next())
			{
				return rs.getInt(numColumnName);
			}
			
			return (-1);
		}
		

		
		private int getPatternType(String patternType) throws SQLException
		{
			String query = "SELECT id FROM patterntypes where lower(patternName) like lower('%' ? '%')";		
			
			int type = getNumFromDB(patternType, query, "id");
			logger.info("patternTypeStr= " + patternType);
			logger.info("type= " + type);
			if (type < 0)
			{
				throw new  SQLException("patternType name can't be found in patterntypes: " + patternType);
			}
			else
			{
				return type;
			}
		}

		/**
		 * @param patternTypeId - typeID of the pattern 
		 * @return number of different  patterns of that count
		 * @throws SQLException
		 */
		private int getDifferntPatternsCount(int patternTypeId, String select_func) throws SQLException
		{
			String query = "SELECT " + select_func + " c FROM patterncounters WHERE patternType = ?";		
			String num = new Integer(patternTypeId).toString();
			int count = getNumFromDB(num, query, "c");
			if (count < 0) 
			{
				count = 0;
			}
			return count;
		}
		
	}
	
}
