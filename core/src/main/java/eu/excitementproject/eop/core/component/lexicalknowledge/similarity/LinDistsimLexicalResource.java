package eu.excitementproject.eop.core.component.lexicalknowledge.similarity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.representation.partofspeech.BySimplerCanonicalPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;

/**
 * <b>Resource name</b>: Lin distributional similarity for Reuters<br>
   <b>Resource description</b>: A resource of distributional similarity rules calculated with Idan's dist.sim. code using Lin's measure (without clustering) over the Reuters RCV1 corpus with dependency-based features.<br>
   <b>POS</b>: nouns<br>
   <b>Ref to relevant Paper</b>: �Automatic Retrieval and Clustering of Similar Words�, Dekang Lin, COLING-ACL, 1998, pp. 768-774.<br>
   <b>DB Scheme</b>: distsim (qa-srv:3308)<br>
   <b>DB tables</b>: lin_rules_lemmas<br>
 * The table contains {@code <lemma, lemma, similarity>} 
 * triplets without parts of speech, 'cos all are assumed to be nouns. So all queries ignore the given parts of speech and return rules featuring 
 * the {@code NOUN} {@link PartOfSpeech}. Each rule-list result of {@link #getRulesForLeft(String, PartOfSpeech)} and 
 * {@link #getRulesForRight(String, PartOfSpeech)}
 * is sorted in decreasing order of similarity (to the queried lemma).
 *  <p>
 * Also note that all digits in queried lemma will be replaced with '@', and the lemmas in all retrieved rules will have '@'s where you'd 
 * expect digits.
 * <P>
 * See also: http://irsrv2/wiki/index.php/Lexical_Resources
 * 
 * 
 * @author Amnon Lotan
 * @since 16/05/2011
 * 
 */
public class LinDistsimLexicalResource extends AbstractSinglePosLexicalResource
{
	private static final String RESOURCE_NAME = "Lin Distributional Similarity";

	// Strings for the prepared statements
	private static final String TABLE = "lin_rules_lemmas";
	private static final String L_COL = "left_element";
	private static final String R_COL = "right_element";
	private static final String SIM_COL = "score";

	private static final String PARAM_CONNECTION_STRING = "database_url";
	
	final private Set<PreparedStatementAndPos> setOfGetRulesForLeftStmt = new LinkedHashSet<PreparedStatementAndPos>();
	final private Set<PreparedStatementAndPos> setOfGetRulesForRightStmt = new LinkedHashSet<PreparedStatementAndPos>();
	final private Set<PreparedStatementAndPos> setOfGetScoresStmt = new LinkedHashSet<PreparedStatementAndPos>();
	
	private PartOfSpeech DEFAULT_POS = null;
	
	///////////////////////////////////////////////// PUBLIC /////////////////////////////////////////////////////////////////////
	
	/**
	 * Ctor using {@link ConfigurationParams}
	 * @param params
	 * @throws LexicalResourceException
	 * @throws ConfigurationException
	 */
	public LinDistsimLexicalResource(ConfigurationParams params) throws LexicalResourceException, ConfigurationException
	{
		this(params.getString(PARAM_CONNECTION_STRING), null, null, params.getInt(PARAM_RULES_LIMIT)	);
	}
	
	/**
	 * Ctor
	 * <p>
 *  The int Ctor parameter <code>limitOnRetrievedRules</code> must be non negative. zero means all rules matching the query will be retrieved. 
 * A positive value X means that only the top X rules are retrieved.
	 * @param connStr
	 * @param user
	 * @param password
	 * @param limitOnRetrievedRules 
	 * @throws LexicalResourceException 
	 */
	public LinDistsimLexicalResource(String connStr, String user, String password, int limitOnRetrievedRules) throws LexicalResourceException
	{
		super(limitOnRetrievedRules);
		
		// DEFAULT_POS must be initialized first thing, cos subsequent statements read it
		try 										{ DEFAULT_POS = new BySimplerCanonicalPartOfSpeech(SimplerCanonicalPosTag.NOUN);	} 
		catch (UnsupportedPosTagStringException e) 	{ throw new LexicalResourceException("Bug: couldn't construct a new UnspecifiedPartOfSpeech(SimplerCanonicalPosTag.OTHER)",e);		}		
		
		PreparedStatement getRulesForLeftStmt;
		PreparedStatement getRulesForRightStmt;
		PreparedStatement getScoresStmt;
		
		try
		{
			Connection con = DriverManager.getConnection(connStr, user, password);
			getRulesForLeftStmt = con.prepareStatement(getRulesForLeftQueryStr(getTable()));
			getRulesForRightStmt = con.prepareStatement(getRulesForRightQueryStr(getTable()));
			getScoresStmt = con.prepareStatement(getRulesForBothSidesQueryStr(getTable()));
		} catch (SQLException e) 	{ 
			throw new LexicalResourceException("Couldn't open and use a connection with this connection string: " + connStr +
					" and credentials: " + user + "/" + password, e);	}

		setOfGetRulesForLeftStmt.add(new PreparedStatementAndPos(getRulesForLeftStmt, getDEFAULT_POS()));
		setOfGetRulesForRightStmt.add(new PreparedStatementAndPos(getRulesForRightStmt, getDEFAULT_POS()));
		setOfGetScoresStmt.add(new PreparedStatementAndPos(getScoresStmt, getDEFAULT_POS())) ;
	}

	///////////////////////////////////////////////////////////// PRIVATE METHODS ///////////////////////////////////////////////////////
	
	protected String getTable()
	{
		return TABLE;
	}
	
	/* (non-Javadoc)
	 * @see ac.biu.cs.nlp.lexical.resource.impl.AbstractSinglePosLexResource#getDEFAULT_POS()
	 */
	@Override
	protected PartOfSpeech getDEFAULT_POS() {
		return DEFAULT_POS;
	}

	/* (non-Javadoc)
	 * @see ac.biu.cs.nlp.lexical.resource.impl.AbstractLexResource#L_COL()
	 */
	@Override
	protected String L_COL() {
		return L_COL;
	}

	/* (non-Javadoc)
	 * @see ac.biu.cs.nlp.lexical.resource.impl.AbstractLexResource#R_COL()
	 */
	@Override
	protected String R_COL() {
		return R_COL;
	}

	/* (non-Javadoc)
	 * @see ac.biu.cs.nlp.lexical.resource.impl.AbstractLexResource#SIM_COL()
	 */
	@Override
	protected String SIM_COL() {
		return SIM_COL;
	}

	/* (non-Javadoc)
	 * @see ac.biu.cs.nlp.lexical.resource.impl.AbstractLexResource#posToRulesStmt(ac.biu.nlp.nlp.representation.PartOfSpeech, boolean)
	 */
	@Override
	protected Set<PreparedStatementAndPos> posToRulesStmts(PartOfSpeech pos, boolean isRHS) {
		return isRHS ? setOfGetRulesForRightStmt: setOfGetRulesForLeftStmt;
	}

	/* (non-Javadoc)
	 * @see ac.biu.cs.nlp.lexical.resource.impl.AbstractLexResource#posToScoreStmt(ac.biu.nlp.nlp.representation.PartOfSpeech)
	 */
	@Override
	protected Set<PreparedStatementAndPos> posToScoreStmt(PartOfSpeech pos) {
		return setOfGetScoresStmt;
	}

	/* (non-Javadoc)
	 * @see ac.biu.cs.nlp.lexical.resource.impl.AbstractSimilarityLexResource#getResourceName()
	 */
	@Override
	protected String getResourceName() {
		return RESOURCE_NAME;
	}

	/**
	 * I used this script of make the current lin_rules_lemmas from an older lin* table
	 * 
	 * @param args
	 * @throws SQLException
	 * @throws LexicalResourceException
	 * @throws IOException
	 */
	public static final void main_makeWorkingTableOutOfOriginalLinTable(String[] args) throws SQLException, LexicalResourceException, IOException
	{
		String conStr = "jdbc:mysql://localhost:3308/distsim";
		Connection con = DriverManager.getConnection(conStr, "root", "sqlsql");
		
		PreparedStatement templateQuery = con.prepareStatement("SELECT id, description FROM lin_template_nb" );
		ResultSet rs;
		try 					
		{
			// query for this lemma+pos  
			rs = templateQuery.executeQuery();	
		} 
		catch (SQLException e) 	{	throw new LexicalResourceException("Error executing the query " + templateQuery,e);	}
		Map<Integer, String> idToLemma = new LinkedHashMap<Integer, String>();
		int i = 0;
		try 					
		{
			if(rs.first())
				do {
					i++;
					Integer id = rs.getInt(1);
					String lemma = rs.getString(2);
					if (!lemma.endsWith(":n"))
						throw new LexicalResourceException("Hey! this description doesn't end iwth a :n,\t" + lemma);
					lemma = lemma.substring(0,lemma.length() -2);
					if (lemma.length() > 100)
						lemma = lemma.substring(0, 99);
					lemma = lemma.replaceAll("#", "@");
					idToLemma.put(id, lemma);
				} while (rs.next());
			rs.close();
		}
		catch (SQLException e) 	{	throw new LexicalResourceException("Error reading the result set of the query " + templateQuery,e);	}
		System.out.println("Read " + i + " rows out of lin_template_nb :>");
		
		// read the rules table and 
//		PreparedStatement rulesQuery = conMgr.prepareStatement("SELECT left_element_id, right_element_id,  score FROM lin_rules_nb", conStr );
//		PreparedStatement lemmasQuery = conMgr.prepareStatement("INSERT INTO lin_rules_lemmas (left_element, right_element, score) SELECT ? , ? , ?", conStr );
		System.out.println("read the rules file :))");

		String outFile = "d:/temp/lin_rules_lemmas.txt";	// "c:/Users/user/Desktop/lin_rules_lemmas.txt";
		new File(outFile).delete();
		StringBuilder buf = new StringBuilder();	

		i = 0;
		BufferedReader reader = new BufferedReader(new FileReader(new File("d:/temp/distsim_lin_rules_nb.txt")));
		try
		{
			String line;
			while ((line = reader.readLine()) != null)
			{
				i++;
				String[] parts = line.split("\\t");
				if (parts.length != 3)
					throw new LexicalResourceException("Hey! this don't have 3 parts: " + line);

				int leftId = Integer.parseInt(parts[0]);
				int rightId = Integer.parseInt(parts[1]);
				double score = Double.parseDouble(parts[2]);
				String lLemma = idToLemma.get(leftId);
				String rLemma = idToLemma.get(rightId);

				buf.append(lLemma + "\t" + rLemma + "\t" + score + "\n");
				if (i % 1000000 == 0)
				{
					fileAppendContents(outFile, buf.toString());
					buf = new StringBuilder();
					System.out.println("rows >>" + i);
				}
			}
		}
		finally
		{
			reader.close();
		}

		fileAppendContents(outFile, buf.toString());
		System.out.println("Read and wrote " + i + " rows out of lin_rules_nb :>");


		Statement stmt = con.createStatement();
		stmt.executeUpdate(
			"DROP TABLE IF EXISTS `distsim`.`lin_rules_lemmas`;" +
			"CREATE TABLE  `distsim`.`lin_rules_lemmas` (" +
			"`left_element` varchar(100) NOT NULL," +
			"`right_element` varchar(100) NOT NULL," +
			"`score` decimal(64,30) unsigned NOT NULL," +
			"PRIMARY KEY (`left_element`,`right_element`)" +
			") ENGINE=InnoDB DEFAULT CHARSET=utf8;"
		);
		int rows = stmt.executeUpdate("LOAD DATA INFILE '" + outFile + "' INTO TABLE lin_rules_lemmas (left_element, right_element, score);");
		System.out.println("The LOAD DATA returned " + rows);
	}

	private static void fileAppendContents (String file, String contents) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(file, true)); 
		out.write(contents); 
		out.flush();
		out.close(); 
	}

}

