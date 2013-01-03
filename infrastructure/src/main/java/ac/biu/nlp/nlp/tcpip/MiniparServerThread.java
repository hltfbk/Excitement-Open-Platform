package ac.biu.nlp.nlp.tcpip;

import java.util.ArrayList;
import ac.biu.nlp.nlp.instruments.parse.minipar.MiniparJni;


/**
 * A TCP/IP server to run Minipar.
 * <P>
 * The server uses the dynamic link library created by Asher Stern to
 * run Minipar, which has an output similar (but not identical) to "pdemo".
 * <P>
 * Get the library from our SVN, and put it with your system libraries:
 * <br>* On Linux - copy libMinipar.so to /usr/lib 
 * <br>* On Windows - copy libMinipar.dll to (???)  
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
public class MiniparServerThread extends QueryServerThread
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

	@Override
	public String query(String sentence) {
		boolean sentenceVerbose = (verboseLevel==VerboseLevel.SENTENCE_ONLY)||(verboseLevel==VerboseLevel.SENTENCE_AND_PARSER_OUTPUT);
		quitAfterThisQuery = true; // process a single sentence only (keep current behaviour)
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
			    StringBuffer buffer = new StringBuffer();
				for (String line: parsedSentence)
				{
					buffer.append(line).append("\n");
					if (verbose)
						System.out.println(line);
				}
			    return buffer.toString();
			}
		}
		return null;
	}

	
	public static void setVerboseLevel(VerboseLevel level)
	{
		verboseLevel = level;
	}

	
	///////////////////////////// PROTECTED //////////////////////////////////
	

	protected static String dataDir = null;
	protected static MiniparJni miniparJni = new MiniparJni();
	protected static VerboseLevel verboseLevel = VerboseLevel.SILENT;
	protected static String features = null;

	
	//////////////////////////////// MAIN METHOD ///////////////////////////////////////
	
	/**
	 * @param args<BR>
	 * 1. minipar data dir<BR>
	 * 2. (optional) port number
	 */
	public static void main(String[] args)
	{
		int portAsInt = DEFAULT_MINIPAR_SERVER_PORT;
		if (args.length>=1)
		{
			dataDir = args[0];
			if (args.length>=2)
				portAsInt = Integer.parseInt(args[1]);
			//setVerboseLevel(VerboseLevel.SENTENCE_AND_PARSER_OUTPUT);

			String libraryName = MINIPAR_DYNAMIC_LIBRARY_NAME;
			
			System.loadLibrary(libraryName);
			miniparJni.init(dataDir, features);

			new QueryServer("Minipar Server", "1.0", "Asher Stern")
	    		.start(portAsInt, MiniparServerThread.class);
		}
		else
		{
			System.err.println("not enough arguments. Enter Minipar data directory as first argument.");
		}
	}

}
