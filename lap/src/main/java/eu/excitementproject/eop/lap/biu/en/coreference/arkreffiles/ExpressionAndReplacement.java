package eu.excitementproject.eop.lap.biu.en.coreference.arkreffiles;

/**
 * Used by {@link ArkrefFilesWorkaroundTextPreprocessor}
 * @author Asher Stern
 * @since Dec 11, 2013
 *
 */
public class ExpressionAndReplacement
{
	public ExpressionAndReplacement(String expression, String replacement)
	{
		super();
		this.expression = expression;
		this.replacement = replacement;
	}
	
	
	
	public String getExpression()
	{
		return expression;
	}
	public String getReplacement()
	{
		return replacement;
	}



	private final String expression;
	private final String replacement;
}
