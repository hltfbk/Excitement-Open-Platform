package eu.excitementproject.eop.common.utilities;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSet;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableSetWrapper;




/**
 * <b>Deprecated </b>- this class has no real use and can only cause bugs. don't use it.
 * <p>
 * The purpose of this class is to enable {@link Connection} clients to manage several db connections at once. On the one hand 
 * it allows the user to open loads of {@link Connection}s using {@link #openCon(String)}, which takes a connection String. This way the user 
 * doesn't have to worry about maintenance or redundancy between her open connections,  cos DBConMgr keeps one connection per unique 
 * connection String. It also renews dead/timed-out connections on the fly and closes connections at termination. On the other hand, this 
 * class trivially wraps most methods of {@link Connection} (JDK5), which also require a connection-String
 * parameter, as a key into the pool of open connections. This way, you never have to hold a {@link Connection} in your code, only one {@link DBConsMgr} object.
 * <p>
 * <b>NOTE</b> when destroyed, this class closes all connections opened by it! So keep a reference to it as long as you want the connections active in your program. 
 * <p> 
 * <b>Thread unsafe</b>
 * @author Amnon Lotan
 * @since 20/12/2010
 * 
 */
@Deprecated
public class DBConsMgr 
{
	public static final String JDBC_DRIVER_CLASS = "com.mysql.jdbc.Driver";

	/**
	 * A map of all open connections hashed by their respective connection strings.
	 * Ergo, there can be up to one Connection per connection string. 
	 */
	protected Map<String, ConnectionRec> conMap = new HashMap<String, ConnectionRec>();
	
	//////////////////////////////////////////////// public part ///////////////////////////////////////////////////////////////////

	/**
	 * Deprecated - this class has no real use and can only cause bugs. don't use it
	 * @throws SQLException 
	 * 
	 */
	@Deprecated
	public DBConsMgr() throws SQLException 
	{
		try {
			Class.forName(JDBC_DRIVER_CLASS).newInstance();
		} catch (Exception e) {
			throw new SQLException("Could not instantiate the JDBC driver: " + JDBC_DRIVER_CLASS + " " + e.toString());
		}
	}
	
	/**
	 * Open a connection. 
	 * <br>
	 * If an older connection already exists for the same connection string, it is retained. 
	 * Also, if such an existing connection is timed out, it is restarted.
	 * 
	 * @param conString a database url of the form jdbc:subprotocol:subname
	 * 
	 * @return true if a new connection was established, false is an existing connection is valid
	 * 
	 * @throws SQLException
	 */
	public boolean openCon(String conString) throws SQLException
	{
		return openCon(conString, null);
	}
	
	/**
	 * Open a connection
	 * <br>
	 * If an older connection already exists for the same connection string, it is retained. 
	 * Also, if such an existing connection is timed out, it is restarted. 
	 * <p>
	 * the user and password can be both empty or null, but the conString cannot. 
	 * 
	 * @param conString
	 * @param user
	 * @param password
	 * @return true if a new connection was established, false is an existing connection is valid

	 * @throws SQLException
	 */
	public boolean openCon(String conString, String user, String password) throws SQLException
	{
		return openCon(conString, makeProperties(user, password));
	}

	/**
	 * Open a connection. 
	 * <br>
	 * If an older connection already exists for the same connection string, it is retained. 
	 * Also, if such an existing connection is timed out, it is restarted.
	 *  
	 * @param conString a database url of the form jdbc:subprotocol:subname
	 * @param properties a list of arbitrary string tag/value pairs as connection arguments; 
	 * normally at least a "user" and "password" property should be included
	 * 
	 * @return true if a new connection was established, false is an existing connection is valid
	 * 
	 * @throws SQLException
	 */
	public boolean openCon(String conString, Properties properties) throws SQLException
	{		
		if (!conMap.containsKey(conString)) {
			// new connection
			simpleOpenCon(conString, properties);
			return true;
		} else {
			// check timeout
			ConnectionRec conRec = conMap.get(conString);
			if (!connectionValid(conRec.con)) {
				closeCon(conString);
				simpleOpenCon(conString, properties);
				return true;
			}			
			// else we use the existing connection				
		}
		return  false;
	}		

	/**
	 * Limit the life span of connString's connection in the data base to interactive_timeout seconds
	 * 
	 * @param conString
	 * @param timeoutSeconds life span of the connection
	 * @throws SQLException
	 */
	public void setConTimeout(String conString, int timeoutSeconds) throws SQLException
	{
		if (!conMap.containsKey(conString))
			throw new SQLException("There is no open connection for " + conString);
		
		Statement st = conMap.get(conString).con.createStatement();
		st.executeUpdate(timeoutStatement(timeoutSeconds));
		st.close();
	}
	
	
	/**
	 * getAllLiveConnectionStrings. Note that some may be timed out, if any timeouts were set
	 * @return AllLiveConnectionStrings
	 */
	public ImmutableSet<String> getAllLiveConnectionStrings()
	{
		return new ImmutableSetWrapper<String>(conMap.keySet());
	}

	/**
	 * Close the connection for connString
	 * 
	 * @param connString
	 * @throws SQLException 
	 */
	public void closeCon(String conString) throws SQLException
	{
		if (!conMap.containsKey(conString))
			throw new SQLException("There is no open connection for " + conString);
		
		Connection con = conMap.get(conString).con;
		
		// try to close and delete the connection			
	    if(con != null)
	        con.close();
	    
	    conMap.remove(conString);	
	}
	
	/**
	 * Close all connections
	 * 
	 * @throws SQLException 
	 * 
	 */
	public void closeAll() throws SQLException
	{
		SQLException sqlException = null;
		
		for( String conString : conMap.keySet())
			
			// try to close and delete the connection. If one raises an sqlException, try the others before propagating it 
			try 
			{				
		        closeCon(conString);
		    } catch(SQLException e) 
		    {
		    	sqlException = e;
		    }
		    
		if (sqlException != null)
			throw sqlException;
	}	
	





























	/////////////////////////////////////////////////////// protected part ////////////////////////////////////////////////////////
	  
	/* close the DB connections
	 * (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	protected void finalize() 
	{		
		try {
			closeAll();
		} catch (SQLException e) { 
			// ignore sqlExceptions here
		}
	} 
	
	/**
	 * Create a Connection
	 * @param conString a database url of the form jdbc:subprotocol:subname
	 * @param properties a list of arbitrary string tag/value pairs as connection arguments; 
	 * normally at least a "user" and "password" property should be included

	 * 
	 * @throws SQLException 
	 */
	protected void simpleOpenCon(String conString, Properties properties) throws SQLException 
	{
		Connection con = DriverManager.getConnection(conString, properties);
		conMap.put(conString, new ConnectionRec(con, new Date().getTime()));
	}
	
	/**
	 * a simple record of a connection and its metadata
	 * 
	 * @author Amnon Lotan
	 * @since Dec 21, 2010
	 * 
	 */
	protected class ConnectionRec
	{
		public ConnectionRec(Connection con, long openTime)
		{
			this.con = con;
			this.openTime = openTime;
		}
		public long openTime;
		public Connection con;
	}
	
	///////////////////////////////////////////////////// private part /////////////////////////////////////////////////////////////////////////
		
	/**
	 * the user and password can be both empty or null, but the conString cannot.
	 *  
	 * @param user
	 * @param password
	 * @return
	 * @throws SQLException 
	 */
	private Properties makeProperties(String user, String password) throws SQLException {
		Properties properties;
		if ((user ==  null && password == null) || (user == "" && password == ""))
			properties = null;
		else
		{
			if ((password != null && password != "") && (user == null || user == ""))
				throw new SQLException("got null or empty user name, when the password is not empty");
			properties = new Properties();
			properties.put("user", user);
			properties.put("password", password);
		}
		return properties;
	}
	
	/**
	 * @param timeoutSeconds
	 * @return
	 */
	private String timeoutStatement(int timeoutSeconds) 
	{
		return "SET interactive_timeout = " + timeoutSeconds + ", wait_timeout = " + timeoutSeconds;
	}
	
	/**
	 * Check that the connection is valid by performing something and catching exceptions
	 * @param con
	 * @return
	 */
	private boolean connectionValid(Connection con)
	{
		boolean valid = true;
		try {
			Statement st = con.createStatement();
			st.close();
		} catch (SQLException e) {
			valid = false;		
		}

		return valid;
	}
	
	//////////////////////////////////////////////// trivial Connection methods ///////////////////////////////////////////
	
	/****
	 * Notice that close() and open are significantly missing, that's cos they're implemented above, duh
	 ****/
	
	
	/* (non-Javadoc)
	 * @see java.sql.Connection#clearWarnings()
	 */
	public void clearWarnings(String conString) throws SQLException {
		conMap.get(conString).con.clearWarnings();			
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#commit()
	 */
	public void commit(String conString) throws SQLException {
		conMap.get(conString).con.commit();
		
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#createStatement()
	 */
	public Statement createStatement(String conString) throws SQLException {

		return conMap.get(conString).con.createStatement();
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#createStatement(int, int)
	 */
	public Statement createStatement(int resultSetType,
			int resultSetConcurrency, String conString) throws SQLException {

		return conMap.get(conString).con.createStatement(resultSetType, resultSetConcurrency);
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#createStatement(int, int, int)
	 */
	public Statement createStatement(int resultSetType,
			int resultSetConcurrency, int resultSetHoldability, String conString)
			throws SQLException {

		return conMap.get(conString).con.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#getAutoCommit()
	 */
	public boolean getAutoCommit(String conString) throws SQLException {

		return conMap.get(conString).con.getAutoCommit();
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#getCatalog()
	 */
	public String getCatalog(String conString) throws SQLException {

		return conMap.get(conString).con.getCatalog();
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#getHoldability()
	 */
	public int getHoldability(String conString) throws SQLException {

		return conMap.get(conString).con.getHoldability();
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#getMetaData()
	 */
	public DatabaseMetaData getMetaData(String conString) throws SQLException {

		return conMap.get(conString).con.getMetaData();
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#getTransactionIsolation()
	 */
	public int getTransactionIsolation(String conString) throws SQLException {

		return conMap.get(conString).con.getTransactionIsolation();
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#getTypeMap()
	 */
	public Map<String, Class<?>> getTypeMap(String conString) throws SQLException {

		return conMap.get(conString).con.getTypeMap();
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#getWarnings()
	 */
	public SQLWarning getWarnings(String conString) throws SQLException {

		return conMap.get(conString).con.getWarnings();
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#isClosed()
	 */
	public boolean isClosed(String conString) throws SQLException {
		ConnectionRec connRec = conMap.get(conString);
		if (connRec == null)
			return true;
		return connRec.con.isClosed();
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#isReadOnly()
	 */
	public boolean isReadOnly(String conString) throws SQLException {

		return conMap.get(conString).con.isReadOnly();
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#nativeSQL(java.lang.String)
	 */
	public String nativeSQL(String sql, String conString) throws SQLException {

		return conMap.get(conString).con.nativeSQL(sql);
	}
	


	/* (non-Javadoc)
	 * @see java.sql.Connection#prepareCall(java.lang.String)
	 */
	public CallableStatement prepareCall(String sql, String conString) throws SQLException {

		return conMap.get(conString).con.prepareCall(sql);
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#prepareCall(java.lang.String, int, int)
	 */
	public CallableStatement prepareCall(String sql, int resultSetType,
			int resultSetConcurrency, String conString) throws SQLException {

		return conMap.get(conString).con.prepareCall(sql, resultSetType, resultSetConcurrency);
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#prepareCall(java.lang.String, int, int, int)
	 */
	public CallableStatement prepareCall(String sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability, String conString) 
			throws SQLException {

		return conMap.get(conString).con.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#prepareStatement(java.lang.String)
	 */
	public PreparedStatement prepareStatement(String sql, String conString) 
			throws SQLException {

		return conMap.get(conString).con.prepareStatement(sql);
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#prepareStatement(java.lang.String, int)
	 */
	public PreparedStatement prepareStatement(String sql,
			int autoGeneratedKeys, String conString) throws SQLException {

		return conMap.get(conString).con.prepareStatement(sql, autoGeneratedKeys);
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#prepareStatement(java.lang.String, int[])
	 */
	public PreparedStatement prepareStatement(String sql,
			int[] columnIndexes, String conString) throws SQLException {

		return conMap.get(conString).con.prepareStatement(sql, columnIndexes);
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#prepareStatement(java.lang.String, java.lang.String[])
	 */
	public PreparedStatement prepareStatement(String sql,
			String[] columnNames, String conString) throws SQLException {

		return conMap.get(conString).con.prepareStatement(sql, columnNames);
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#prepareStatement(java.lang.String, int, int)
	 */
	public PreparedStatement prepareStatement(String sql,
			int resultSetType, int resultSetConcurrency, String conString) 
			throws SQLException {

		return conMap.get(conString).con.prepareStatement(sql, resultSetType, resultSetConcurrency);
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#prepareStatement(java.lang.String, int, int, int)
	 */
	public PreparedStatement prepareStatement(String sql,
			int resultSetType, int resultSetConcurrency,
			int resultSetHoldability, String conString) throws SQLException {

		return conMap.get(conString).con.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#releaseSavepoint(java.sql.Savepoint)
	 */
	public void releaseSavepoint(Savepoint savepoint, String conString) throws SQLException {
		conMap.get(conString).con.releaseSavepoint(savepoint);
		
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#rollback()
	 */
	public void rollback(String conString) throws SQLException {
		conMap.get(conString).con.rollback();
		
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#rollback(java.sql.Savepoint)
	 */
	public void rollback(Savepoint savepoint, String conString) throws SQLException {
		conMap.get(conString).con.rollback(savepoint);
		
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#setAutoCommit(boolean)
	 */
	public void setAutoCommit(boolean autoCommit, String conString) throws SQLException {
		conMap.get(conString).con.setAutoCommit(autoCommit);
		
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#setCatalog(java.lang.String)
	 */
	public void setCatalog(String catalog, String conString) throws SQLException {
		conMap.get(conString).con.setCatalog(catalog);
		
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#setHoldability(int)
	 */
	public void setHoldability(int holdability, String conString) throws SQLException {
		conMap.get(conString).con.setHoldability(holdability);
		
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#setReadOnly(boolean)
	 */
	public void setReadOnly(boolean readOnly, String conString) throws SQLException {
		conMap.get(conString).con.setReadOnly(readOnly);
		
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#setSavepoint()
	 */
	public Savepoint setSavepoint(String conString) throws SQLException {

		return conMap.get(conString).con.setSavepoint();
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#setSavepoint(java.lang.String)
	 */
	public Savepoint setSavepoint(String name, String conString) throws SQLException {

		return conMap.get(conString).con.setSavepoint(name);
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#setTransactionIsolation(int)
	 */
	public void setTransactionIsolation(int level, String conString) throws SQLException {
		conMap.get(conString).con.setTransactionIsolation(level);
		
	}

	/* (non-Javadoc)
	 * @see java.sql.Connection#setTypeMap(java.util.Map)
	 */
	public void setTypeMap(Map<String, Class<?>> map, String conString) throws SQLException {
		conMap.get(conString).con.setTypeMap(map);
		
	}
}