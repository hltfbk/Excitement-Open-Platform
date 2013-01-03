package ac.biu.nlp.nlp.web.search;

import java.util.List;

/**
 * An interface for searching in the web.
 * @author Asher Stern
 *
 */
public interface SearchEngine
{
	public void setQuery(String query);
	public void searchNext(int numberOfResults) throws SearchEngineException;
	public List<Result> getResultsList();
}
