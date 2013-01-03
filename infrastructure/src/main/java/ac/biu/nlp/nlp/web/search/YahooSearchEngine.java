package ac.biu.nlp.nlp.web.search;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class YahooSearchEngine implements SearchEngine
{
	////////////// constants /////////////////////////////
	protected static final String URL_BEGINING = "http://boss.yahooapis.com/ysearch/web/v1/";
	protected static final String PARAMS_BEGIN_MARKER = "?";
	protected static final String PARAM_ADD_MARKER = "&";
	protected static final String PARAM_SET_VALUE_MARKER = "=";
	protected static final String APP_ID_PARAM_NAME = "appid";
	protected static final String FORMAT_PARAM_NAME = "format";
	protected static final String FORMAT_VALUE = "xml";
	protected static final String LANG_PARAM_NAME = "lang";
	protected static final String LANG_VALUE = "he";
	protected static final String REGION_PARAM_NAME = "region";
	protected static final String REGION_VALUE = "il";
	protected static final String START_RESULT_PARAM_NAME = "start";
	protected static final String NUMBER_OF_RESULTS_PARAM_NAME = "count";
	protected static final int MAX_RESULT_PER_QUERY = 50;
	
	protected static final String CONNECTION_METHOD = "GET";
	protected static final int CONNECTION_TIMEOUT = 10000;
	
	protected static final String SPACE_REPLACEMENT = "%20";
	
	protected static final String RESULT_ELEMENTS_CONTAINER_ELEMENT_NAME = "resultset_web";
	protected static final String RESULT_ELEMENT_NAME = "result";
	
	protected static final String ABSTRACT_ELEMENT_NAME = "abstract";
	protected static final String URL_ELEMENT_NAME = "url";
	
	
	
	
	
	
	
	
	
	////////// protected and private /////////////////////
	protected void clean()
	{
		this.resultList = null;
		this.nextResultNumber = 0;
		this.query = null;
	}
	
	protected String queryRefinement(String query)
	{
		return query.replace(" ", SPACE_REPLACEMENT);
	}
	
	protected String buildQuery(int numberOfResultToAsk)
	{
		return
		URL_BEGINING+this.query+
		PARAMS_BEGIN_MARKER+APP_ID_PARAM_NAME+PARAM_SET_VALUE_MARKER+this.appId+
		PARAM_ADD_MARKER+FORMAT_PARAM_NAME+PARAM_SET_VALUE_MARKER+FORMAT_VALUE+
		// Chaya - 22-June-2011. Optional: add language and/or region:
		//PARAM_ADD_MARKER+LANG_PARAM_NAME+PARAM_SET_VALUE_MARKER+LANG_VALUE+
		//PARAM_ADD_MARKER+REGION_PARAM_NAME+PARAM_SET_VALUE_MARKER+REGION_VALUE+
		PARAM_ADD_MARKER+START_RESULT_PARAM_NAME+PARAM_SET_VALUE_MARKER+String.valueOf(this.nextResultNumber)+
		PARAM_ADD_MARKER+NUMBER_OF_RESULTS_PARAM_NAME+PARAM_SET_VALUE_MARKER+String.valueOf(numberOfResultToAsk)
		;
	}
	
	protected InputStream runQuery(String urlQuery) throws MalformedURLException, IOException, SocketTimeoutException
	{
		HttpURLConnection connection = null;
		URL url = new URL(urlQuery);
		connection = (HttpURLConnection)url.openConnection();
		
		connection.setRequestMethod(CONNECTION_METHOD);
		//connection.setDoOutput(true);
		connection.setReadTimeout(CONNECTION_TIMEOUT);
		
		connection.connect();
		
		InputStream inputStream = connection.getInputStream();
		
		return inputStream;
	}
	
	protected void addResultsToList(InputStream inputStream) throws ParserConfigurationException, SAXException, IOException, SearchEngineException
	{
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = db.parse(inputStream);
		Element rootElem = doc.getDocumentElement();
		
		// get "resultset_web" element
		NodeList resultsWebNodes = rootElem.getElementsByTagName(RESULT_ELEMENTS_CONTAINER_ELEMENT_NAME);
		if (resultsWebNodes.getLength()!=1)
			throw new SearchEngineException("element: \""+RESULT_ELEMENTS_CONTAINER_ELEMENT_NAME+"\" exist "+resultsWebNodes.getLength()+" in the returned XML.");
		Node resultsWebNode = resultsWebNodes.item(0);
		if (resultsWebNode.getNodeType()!=Node.ELEMENT_NODE)
			throw new SearchEngineException("non-element results_web element found.");
		Element resultsWebElement = (Element)resultsWebNode;

		// Traverse all its children
		NodeList results = resultsWebElement.getElementsByTagName(RESULT_ELEMENT_NAME);
		for (int index=0;index<results.getLength();++index)
		{
			Node resultAsNode = results.item(index);
			if (resultAsNode.getNodeType()!= Node.ELEMENT_NODE)
				throw new SearchEngineException("result node returned, which is not an element");
			Element result = (Element) resultAsNode;



			// get the abstract
			NodeList abstractNodeList = result.getElementsByTagName(ABSTRACT_ELEMENT_NAME);
			if (abstractNodeList.getLength()!= 1)
				throw new SearchEngineException("abstract not found for result #"+index);
			Node abstractNode = abstractNodeList.item(0);
			if (abstractNode.getNodeType()!= Node.ELEMENT_NODE)
				throw new SearchEngineException("abstract node is not element");
			Element abstractElement = (Element)abstractNode;

			// get the URL
			NodeList urlNodeList = result.getElementsByTagName(URL_ELEMENT_NAME);
			if (urlNodeList.getLength()!= 1)
				throw new SearchEngineException("url not found for result #"+index);
			Node urlNode = urlNodeList.item(0);
			if (urlNode.getNodeType()!= Node.ELEMENT_NODE)
				throw new SearchEngineException("url node is not element");
			Element urlElement = (Element)urlNode;

			this.resultList.add(new Result(urlElement.getTextContent(),abstractElement.getTextContent()));
		}

	}
	
	
	///////////////// public //////////////////////////////

	public YahooSearchEngine(String appId)
	{
		this.appId = appId;
		clean();
	}
	
	// Chaya: 22-June-2011: For queries in Hebrew, call URLEncoder.encode before setting the query
	public void setQuery(String query)
	{
		clean();
		this.query = queryRefinement(query);
	}


	public void searchNext(int numberOfResults) throws SearchEngineException
	{
		if (numberOfResults<0)
			throw new SearchEngineException("Illegal argument (negative argument was given) for method searchNext()");
		int numberOfResultsBackup = numberOfResults;
		try
		{
			if (this.resultList==null)
			{
				this.resultList = new ArrayList<Result>(numberOfResults);
			}
			else
			{
				this.resultList.ensureCapacity(this.resultList.size()+numberOfResults);
			}

			while (numberOfResults>0)
			{
				int numberOfResultsThisIteration=0;
				if (numberOfResults>MAX_RESULT_PER_QUERY)
				{
					numberOfResultsThisIteration=MAX_RESULT_PER_QUERY;
					numberOfResults -= MAX_RESULT_PER_QUERY;
				}
				else
				{
					numberOfResultsThisIteration=numberOfResults;
					numberOfResults=0;
				}
				addResultsToList(runQuery(buildQuery(numberOfResultsThisIteration)));
				this.nextResultNumber += numberOfResultsThisIteration;
			}
		}
		catch(Exception e)
		{
			throw new SearchEngineException("Searching for query: <"+this.query+">, for next "+numberOfResultsBackup+" results (previous calls already returened "+this.nextResultNumber+" results) failed. The exception was: "+e.getClass().getName(),e);
		}


		
	}

	public List<Result> getResultsList()
	{
		return this.resultList;
	}
	
	
	protected String appId = null; // Yahoo ID. See http://developer.yahoo.com/search/boss/boss_guide/overview.html
	protected ArrayList<Result> resultList;
	protected String query;
	protected int nextResultNumber = 0;

	
	

}
