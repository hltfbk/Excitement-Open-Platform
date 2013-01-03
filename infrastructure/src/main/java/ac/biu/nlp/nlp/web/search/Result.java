package ac.biu.nlp.nlp.web.search;


/**
 * Represent a single result of web-search.
 * A search engine would return a list of such {@linkplain Result}s
 * @author Asher Stern
 *
 */
public class Result
{
	public Result(String url, String resultAbstract)
	{
		this.url = url;
		this.resultAbstract = resultAbstract;
	}
	
	
	public String getUrl()
	{
		return url;
	}
	
	public String getResultAbstract()
	{
		return resultAbstract;
	}
	
	protected String url;
	protected String resultAbstract;
}
