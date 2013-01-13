package eu.excitementproject.eop.lap.biu.en.parser.candc.graph;



/**
 * Parses a line like
 * <pre>
 * (xcomp _ am_1 here_2)
 * </pre>
 * which is part of the C&C parser's output.
 * 
 * @author Asher Stern
 *
 */
public class CCRelationLineParser
{
	public static final String RELATION_DELIMITER = CandCOutputToGraph.RELATION_DELIMITER;

	
	
	public CCRelationLineParser(String line) throws CandCMalformedOutputException
	{
		this.line = line;
		parse();
	}
	
	protected void parse() throws CandCMalformedOutputException
	{
		String[] components = line.split(" ");
		if (components.length<(1+1+1))
			throw new CandCMalformedOutputException("bad line: "+line);
		
		grType = components[0];
		int headIndex = -1;
		int dependentIndex = -1;
		int optionalSubtypeIndex = -1;
		int optionalInitialGrIndex = -1;
		if (components.length==(1+1+1))
		{
			headIndex = 1;
			dependentIndex = 2;
		}
		else
		{
			
			if (
					CandCOutputToGraph.isWordUnderscoreNumber(components[1])
					&&
					CandCOutputToGraph.isWordUnderscoreNumber(components[2])
					&&
					CandCOutputToGraph.isWordUnderscoreNumber(components[3])
				)
			{
				optionalSubtypeIndex = 1;
				hasOptionalSubtype = true;
				headIndex = 2;
				dependentIndex = 3;
			}
			else
			{
				if (CandCOutputToGraph.isWordUnderscoreNumber(components[1]))
				{
					hasOptionalSubtype = false;
					hasOptionalInitialGr = true;
					headIndex = 1;
					dependentIndex = 2;
					optionalInitialGrIndex = 3;
				}
				else
				{
					hasOptionalSubtype = true;
					optionalSubtypeIndex = 1;
					headIndex = 2;
					dependentIndex = 3;
					if (components.length>(1+1+1+1))
					{
						hasOptionalInitialGr = true;
						optionalInitialGrIndex = 4;
					}
				}
			}
			
		}
		
		headNumber = CandCOutputToGraph.getNumberOfWordUnderscoreNumber(components[headIndex]);
		dependentNumber = CandCOutputToGraph.getNumberOfWordUnderscoreNumber(components[dependentIndex]);
		
		head = CandCOutputToGraph.getWordOfWordUnderscoreNumber(components[headIndex]);
		dependent = CandCOutputToGraph.getWordOfWordUnderscoreNumber(components[dependentIndex]);
		
		if (hasOptionalSubtype)
		{
			optionalSubtype = components[optionalSubtypeIndex];
		}
		if (hasOptionalInitialGr)
		{
			optionalInitialGr = components[optionalInitialGrIndex];
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public String getHead() {
		return head;
	}

	public int getHeadNumber() {
		return headNumber;
	}

	public String getDependent() {
		return dependent;
	}

	public int getDependentNumber() {
		return dependentNumber;
	}

	public String getGrType() {
		return grType;
	}

	public String getOptionalSubtype() {
		return optionalSubtype;
	}

	public String getOptionalInitialGr() {
		return optionalInitialGr;
	}











	protected String line;
	
	protected String head;
	protected int headNumber;
	protected String dependent;
	protected int dependentNumber;
	protected String grType;
	protected String optionalSubtype;
	protected String optionalInitialGr;
	protected boolean hasOptionalSubtype = false;
	protected boolean hasOptionalInitialGr = false;


}
