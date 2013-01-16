package eu.excitementproject.eop.lap.biu.en.parser.minipar;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import eu.excitementproject.eop.lap.biu.en.parser.EnglishSingleTreeParser;
import eu.excitementproject.eop.lap.biu.en.parser.ParserRunException;


/**
 * A subclass of {@link AbstractMiniparParser} which is
 * an implementation of {@link EnglishSingleTreeParser}, that works as client to a running
 * minipar-server (implemented in {@link MiniparServer} ).
 * @author Asher Stern
 * @see MiniparServer
 * @see MiniparParser
 *
 */
public class MiniparClientParser extends AbstractMiniparParser implements EnglishSingleTreeParser
{
	public MiniparClientParser(String serverHost, int serverPortNumber) throws ParserRunException
	{
		this.serverHostName = serverHost;
		this.serverPortNumber = serverPortNumber;
	}
	
	/**
	 * Takes as arguemnt the server name / ip (E.G. "localhost" or "192.168.12.1").
	 * This constructor can take an argument "server:port" string, like "localhost:3361"
	 * 
	 * @param serverHost
	 * @throws ParserRunException
	 */
	public MiniparClientParser(String serverHost) throws ParserRunException
	{
		int colonIndex=serverHost.lastIndexOf(':');
		if ((-1)==colonIndex)
		{
			this.serverHostName = serverHost;
			this.serverPortNumber = MiniparServer.DEFAULT_MINIPAR_SERVER_PORT;
		}
		else
		{
			this.serverHostName = serverHost.substring(0,colonIndex);
			if (serverHost.length()<=(colonIndex+1))
				throw new ParserRunException("Bad server:port string. "+serverHost);
			try
			{
				this.serverPortNumber = Integer.parseInt(serverHost.substring(colonIndex+1));
			}
			catch(NumberFormatException e)
			{
				throw new ParserRunException("bad port at server:port specified as: "+serverHost);
			}
			
		}
		//this(serverHost,MiniparServer.DEFAULT_MINIPAR_SERVER_PORT);
	}
	
	@Override
	protected ArrayList<String> getMiniparNativeOutput() throws ParserRunException
	{
		ArrayList<String> ret = null;
		PrintWriter pwOut = null;
		BufferedReader brIn = null;
		Socket clientSocket = null;
		try
		{
			ret = new ArrayList<String>();
			clientSocket = new Socket(this.serverHostName, this.serverPortNumber);
			pwOut = new PrintWriter(clientSocket.getOutputStream());
			if (null==this.sentence) throw new Exception("null sentence");
			pwOut.println(this.sentence);
			pwOut.flush();
			brIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			String miniparLine = brIn.readLine();
			while (miniparLine != null)
			{
				ret.add(miniparLine);
				miniparLine = brIn.readLine();
			}
		}
		catch(Exception e)
		{
			ret = null;
			throw new ParserRunException("Connection to server / IO with server ("+this.serverHostName+") - failed. See nested exception.",e);
		}
		finally
		{
			try{if (pwOut!=null)pwOut.close();}catch(Exception e){}
			try{if (brIn!=null)brIn.close();}catch(Exception e){}
			try{if (clientSocket != null)clientSocket.close();}catch(Exception e){}
		}
		return ret;
		
	}

	@Override
	public void init() throws ParserRunException
	{

	}
	
	@Override
	public void cleanUp()
	{
		super.cleanUp();
	}
	
	
	protected String serverHostName;
	protected int serverPortNumber;
}
