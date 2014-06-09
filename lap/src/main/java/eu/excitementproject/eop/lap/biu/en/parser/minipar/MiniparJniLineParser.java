package eu.excitementproject.eop.lap.biu.en.parser.minipar;


/**
 * The purpose of this class is to get a line returned by the
 * shared library (.dll .so) created by Asher Stern that wraps
 * Minipar, and decompose that line into its components (which are
 * ID, word, lemma, etc.).
 * <P>
 * The line - is very similar to the line returned by pdemo program
 * that is part of Minipar distribution.
 * There are, however, some minor differences between the lines returned
 * from pdemo and the lines returned by the shared library mentioned
 * above.
 * @author Asher Stern
 *
 */
public class MiniparJniLineParser
{
	
	////////////// CONSTANTS ////////////////////////////////
	
	protected static final char DELIMITER='\t';
	protected static final String NO_WORD_MARKER = "()";
	protected static final String ROOT_CATEGORY_DELIMITER=" ";
	protected static final String NO_ROOT_FORM_MARKER = "~";
	protected static final String NO_PARENT_LABEL_MARKER = "*";
	protected static final String DELIMITER_IN_PARENTHESES_STRING = " ";
	protected static final String PARENT_ROOT_FORM_MARKER = "gov"; 
	protected static final String ANTECEDENT_MARKER = "antecedent";
	protected static final String FEATURES_MARKER = "atts";
	
	
	

	/**
	 * The input is a string surrounded by parentheses,
	 * and the output is the surrounded string.
	 * <P>
	 * for example: input = "(abc)" output = "abc" 
	 * @param str a string surrounded by parentheses
	 * @return the surrounded string
	 */
	protected static String extractFromParentheses(String str)
	{
		int indexDelimiter = str.indexOf(DELIMITER_IN_PARENTHESES_STRING);
		if (indexDelimiter==(-1)) return null;
		try
		{
			return str.substring(indexDelimiter+1,str.length()-1);
		}
		catch(Exception e)
		{
			return null;
		}
		
	}
	
	
	
	
	
	
	protected String originalLine;
	protected String line;
	
	protected String label;
	protected String word;
	protected String rootForm;
	protected String category;
	protected String parentLabel;
	protected String relation;
	protected String parentRootForm;
	protected String antecedentLabel;
	protected String features;
	
	
	
	
	/////////////////////// PUBLIC PART /////////////////////////
	
	/**
	 * Constructor. receives the line returned by the shared library
	 * (.dll .so) that wraps Minipar.
	 * <P>
	 * Actually the shared library (.dll .so) returns a list of lines,
	 * so the right way to use {@link MiniparJniLineParser} is to
	 * create an object of {@link MiniparJniLineParser} for each line
	 * in that list.
	 * @param line the line returned by the shared library
	 * (.dll .so) that wraps Minipar.
	 */
	public MiniparJniLineParser(String line)
	{
		this.originalLine = line;
		this.line = line;
	}

	/**
	 * "parse" here means decomposition of the input line into its
	 * parts (ID, word, lemma, etc.).
	 * <P>
	 * Later call the <code> getXXXX() </code> methods to get the
	 * information extracted by the {@link #parse()} method.
	 */
	public void parse()
	{
		// TODO change to split()
		
		int indexTab;
		if (line==null) return;
		indexTab = line.indexOf(DELIMITER);
		if (indexTab==(-1)) return;
		label=line.substring(0, indexTab);
		line = line.substring(indexTab+1);
		
		if (line==null) return;
		indexTab = line.indexOf(DELIMITER);
		if (indexTab==(-1)) return;
		word=line.substring(1, indexTab);
		if ( word.equals(NO_WORD_MARKER) ) word=null;
		line = line.substring(indexTab+1);

		if (line==null) return;
		indexTab = line.indexOf(DELIMITER);
		if (indexTab==(-1)) return;
		rootForm=line.substring(0, indexTab);
		if (rootForm==null) rootForm = word;
		else if (rootForm.length()==0) rootForm = word;
		else if (rootForm.equals(NO_ROOT_FORM_MARKER)) rootForm = word;
		line = line.substring(indexTab+1);

		if (line==null) return;
		indexTab = line.indexOf(DELIMITER);
		if (indexTab==(-1)) return;
		category=line.substring(0, indexTab);
		line = line.substring(indexTab+1);

		
		
		if (line==null) return;
		indexTab = line.indexOf(DELIMITER);
		if (indexTab==(-1)) return;
		parentLabel=line.substring(0, indexTab);
		if (parentLabel!=null)
			if (parentLabel.equals(NO_PARENT_LABEL_MARKER))
				parentLabel = null;
		line = line.substring(indexTab+1);
		
		if (line==null) return;
		indexTab = line.indexOf(DELIMITER);
		if (indexTab==(-1)) return;
		relation=line.substring(0, indexTab);
		line = line.substring(indexTab+1);
		
		if (line==null) return;
		indexTab = line.indexOf(DELIMITER);
		while (indexTab != (-1))
		{
			String parenthesesString = line.substring(0,indexTab);
			if (parenthesesString.startsWith("("+PARENT_ROOT_FORM_MARKER))
				parentRootForm = extractFromParentheses(parenthesesString).trim();
			else if (parenthesesString.startsWith("("+ANTECEDENT_MARKER))
				antecedentLabel = extractFromParentheses(parenthesesString).trim();
			else if (parenthesesString.startsWith("("+FEATURES_MARKER))
				features = extractFromParentheses(parenthesesString).trim();
			
			line = line.substring(indexTab+1);
			indexTab = line.indexOf(DELIMITER);
		}
		
	}
	
	
	

	
	public String getLabel() {
		return label;
	}

	public String getWord() {
		return word;
	}

	public String getRootForm() {
		return rootForm;
	}

	public String getCategory() {
		return category;
	}

	public String getParentLabel() {
		return parentLabel;
	}

	public String getRelation() {
		return relation;
	}

	public String getParentRootForm() {
		return parentRootForm;
	}

	public String getAntecedentLabel() {
		return antecedentLabel;
	}

	public String getFeatures() {
		return features;
	}
	
	
	

	

}
