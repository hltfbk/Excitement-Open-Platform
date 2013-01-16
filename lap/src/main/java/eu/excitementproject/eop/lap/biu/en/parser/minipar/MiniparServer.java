package eu.excitementproject.eop.lap.biu.en.parser.minipar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


/**
 * A TCP/IP server to run Minipar.
 * <P>
 * The server uses the dynamic link library created by Asher Stern to
 * run Minipar, which has an output similar (but not identical) to "pdemo".
 * <P>
 * Get the library from our SVN, and put it with your system libraries.
 * For example:
 * <br>* On Linux - copy libMinipar.so to /usr/lib 
 * <br>* On Windows32bit - copy libMinipar.dll to C:\Windows
 * <br>* On Windows64bit - copy libMinipar.dll to C:\Windows, and run this class using a 32bit JVM  
 * <P>
 * Run the server using the {@link #main(String[])} method (as application). See required
 * arguments at the JavaDoc comment of {@link #main(String[])} method. 
 * The client can then send a sentence in a single line over the network using
 * TCP/IP socket, and get as answer several lines which are the Minipar output.
 * <BR>
 * No output, or empty output - indicates an exception.
 * <P>To test that the server is running, you can use telnet, for example (commands you should enter are <b>bold</b>): <pre> 
erelsgl@ubuntu:~$ <b>telnet localhost 3057</b>
Trying ::1...
Connected to localhost.
Escape character is '^]'.
<b>In the beginning, God created heaven and Earth.</b>
(
E0	(()	fin	C	*		)
1	(In	~	Prep	E0	mod	(gov fin)	)
2	(the	~	Det	3	det	(gov beginning)	)
3	(beginning	~	N	1	pcomp-n	(gov in)	)
4	(,	~	U	E0	punc	(gov fin)	)
5	(God	~	N	6	s	(gov create)	)
6	(created	create	V	E0	i	(gov fin)	)
E2	(()	god	N	6	subj	(gov create)	(antecedent 5)	)
7	(heaven	~	N	6	obj	(gov create)	)
8	(and	~	U	7	punc	(gov heaven)	)
9	(Earth	~	N	7	conj	(gov heaven)	)
10	(.	~	U	*	punc	)
)
Connection closed by foreign host.
erelsgl@ubuntu:~$ 
</pre>
 * <P>
 * @see NetworkDemo
 * 
 * 
 * @author Asher Stern
 *
 */
public class MiniparServer
{
	
	////////////////////// CONSTANTS /////////////////////////////////
	public static final int DEFAULT_MINIPAR_SERVER_PORT = 3057;
	
	// TODO this constant should be put somewhere else to be used
	// by both this class and MiniparParser class.
	protected static final String MINIPAR_DYNAMIC_LIBRARY_NAME = "Minipar";
	
	
	///////////////////////// NESTED CLASSES ////////////////////////////

	public static enum VerboseLevel
	{
		SILENT, SENTENCE_ONLY, SENTENCE_AND_PARSER_OUTPUT;
	}
	
	private class ConnectionRunnable implements Runnable
	{
		public ConnectionRunnable(Socket clientSocket)
		{
			this.clientSocket = clientSocket;
		}

		public void run()
		{
			BufferedReader brInput = null;
			PrintWriter pwOut = null;
			try
			{
				brInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				pwOut = new PrintWriter(clientSocket.getOutputStream());
				
				boolean sentenceVerbose = (verboseLevel==VerboseLevel.SENTENCE_ONLY)||(verboseLevel==VerboseLevel.SENTENCE_AND_PARSER_OUTPUT);
				
				String sentence = brInput.readLine();
				if (sentenceVerbose)
					System.out.println("--------------------------------");
				if (sentence != null) // and if it is null - the output stream
					// will be closed - which indicates an exception.
				{
					if (sentenceVerbose)
						System.out.println(sentence);
					ArrayList<String> parsedSentence = miniparJni.parse(sentence);
					if (parsedSentence!=null) if (parsedSentence.size()>0)
						// again - otherwise the output stream will be closed with
						// no data - which indicates an exception.
					{
						boolean verbose = (verboseLevel==VerboseLevel.SENTENCE_AND_PARSER_OUTPUT);
						for (String line : parsedSentence)
						{
							pwOut.println(line);
							if (verbose)
								System.out.println(line);
						}
						pwOut.flush();
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				try{if (brInput!=null)brInput.close();}catch(Exception e){e.printStackTrace();}
				try{if (pwOut!=null)pwOut.close();}catch(Exception e){e.printStackTrace();}
				try{clientSocket.close();}catch(Exception e){e.printStackTrace();}
			}
		}
		
		private Socket clientSocket;
		
	} // end of nested class ConnectionRunnable
	
	
	/////////////////////// CONSTRUCTORS AND METHODS /////////////////////////
	
	/////////////////////////////// PUBLIC ///////////////////////////////////
	
	
	
	public MiniparServer(String dataDir, String features, int portNumber ,String library) throws Exception
	{
		this.dataDir = dataDir;
		this.features = features;
		init(library);
		this.portNumber = portNumber;
	}
	

	public MiniparServer(String dataDir, String features, int portNumber) throws Exception
	{
		this(dataDir,features,portNumber,MINIPAR_DYNAMIC_LIBRARY_NAME);
	}

	public MiniparServer(String dataDir, String features) throws Exception
	{
		this(dataDir,features,DEFAULT_MINIPAR_SERVER_PORT,MINIPAR_DYNAMIC_LIBRARY_NAME);
	}
	

	/**
	 * This is the preferred constructor. Use it.
	 * @param dataDir Minipar's data directory
	 * @throws Exception any error.
	 */
	public MiniparServer(String dataDir) throws Exception
	{
		this(dataDir,null,DEFAULT_MINIPAR_SERVER_PORT,MINIPAR_DYNAMIC_LIBRARY_NAME);
	}

	public void setVerboseLevel(VerboseLevel level)
	{
		this.verboseLevel = level;
	}
	
	public void start() throws IOException
	{
		ServerSocket serverSocket = new ServerSocket(portNumber);
		try
		{
			while (true)
			{
				Socket clientSocket = serverSocket.accept();
				Thread connectionThread = new Thread(new ConnectionRunnable(clientSocket));
				connectionThread.start();
			}
		}
		finally
		{
			serverSocket.close();
		}
	}
	
	
	
	///////////////////////////// PROTECTED //////////////////////////////////
	
	protected void init(String libraryName)
	{
		System.loadLibrary(libraryName);
		this.miniparJni.init(this.dataDir, this.features);
		
	}

	

	protected String dataDir;
	protected String features;
	protected MiniparJni miniparJni = new MiniparJni();
	protected int portNumber;
	protected VerboseLevel verboseLevel;
	
	
	
	
	
	//////////////////////////////// MAIN METHOD ///////////////////////////////////////
	
	

	
	/**
	 * @param args<BR>
	 * 1. minipar data dir<BR>
	 * 2. (optional) port number
	 * 
	 */
	public static void main(String[] args)
	{
		MiniparServer server = null;
		if (args.length>=1)
		{
			String dataDir = args[0];
			String port = null;
			if (args.length>=2)
			{
				port = args[1];
			}
			try
			{
				if (null==port)
				{
					server = new MiniparServer(dataDir);
				}
				else
				{
					int portAsInt = Integer.parseInt(port);
					server = new MiniparServer(dataDir,null,portAsInt);
				}
			}
			catch(Exception e)
			{
				System.err.println("server could not be initialized.");
				e.printStackTrace();
				server = null;
			}
			if (server != null)
			{
				try
				{
					//server.setVerboseLevel(VerboseLevel.SENTENCE_AND_PARSER_OUTPUT);
					server.start();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		else
		{
			System.err.println("not enough arguments. Enter Minipar data directory as first argument.");
		}


	}

}

