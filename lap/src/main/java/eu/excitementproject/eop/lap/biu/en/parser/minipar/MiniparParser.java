package eu.excitementproject.eop.lap.biu.en.parser.minipar;


import java.util.ArrayList;

import eu.excitementproject.eop.lap.biu.en.parser.EnglishSingleTreeParser;
import eu.excitementproject.eop.lap.biu.en.parser.ParserRunException;


/**
 * Implementation of {@link EnglishSingleTreeParser} for Minipar.
 * <P>
 * Minipar can be found here: http://www.cs.ualberta.ca/~lindek/minipar.htm
 * <P>
 * The purpose of this class is to load the dynamic link library created by
 * Asher Stern, which outputs the Minipar parse tree in a format similar to
 * "pdemo" program. That output is retrieved by the method {@link #getMiniparNativeOutput()}.
 * 
 * @author Asher Stern
 *
 */
public class MiniparParser extends AbstractMiniparParser implements EnglishSingleTreeParser
{
	
	////////////////////////// CONSTANTS ///////////////////////
	
	protected static final String MINIPAR_DYNAMIC_LIBRARY_NAME = "Minipar";
	


	/////////////////// PRIVATE & PROTECTED ///////////////////////	
	
	@Override
	protected ArrayList<String> getMiniparNativeOutput()
	{
		return this.miniparJni.parse(this.sentence);
	}
	
	
	/**
	 * This constructor is called by other constructors.
	 * @param dataDir the Minipar data directory (comes with Minipar)
	 * @param features names of features the user can ask to be
	 * retrieved by Minipar. see Minipar's README file.
	 * @param miniparDynamicLibraryName the name of the binary
	 * shared library (.dll .so) that wraps Minipar.
	 * @param loadLibrary true if load library should be called by the
	 * constructor, of false if the user already loaded the library alone.
	 */
	private MiniparParser(String dataDir,String features,String miniparDynamicLibraryName,boolean loadLibrary) throws ParserRunException
	{
		this.dataDir = dataDir;
		this.features = features;
		if (loadLibrary)
		{
			try
			{
				System.loadLibrary(miniparDynamicLibraryName);
			}
			catch(Error e)
			{
				throw new ParserRunException("Problem with loading minipar shared library. See nested error",e);
			}
			catch(Exception e)
			{
				throw new ParserRunException("Problem with loading minipar shared library. See nested exception",e);
			}
			
		}
		
	}
	
	//////////////////////// PUBLIC PART ///////////////////////////

	
	/**
	 * Constructor, loads the library.
	 * @param dataDir the path of the Minipar data directory.
	 */
	public MiniparParser(String dataDir) throws ParserRunException
	{
		this(dataDir,null,MINIPAR_DYNAMIC_LIBRARY_NAME,true);
	}
	
	/**
	 * Constructor, loads the library
	 * @param dataDir the path of the Minipar data directory.
	 * @param features names of features the user can ask to be
	 * retrieved by Minipar. see Minipar's README file.
	 */
	public MiniparParser(String dataDir,String features) throws ParserRunException
	{
		this(dataDir,features,MINIPAR_DYNAMIC_LIBRARY_NAME,true);

	}
	
	/**
	 * Constructor, loads the library
	 * @param dataDir the path of the Minipar data directory.
	 * @param features names of features the user can ask to be
	 * retrieved by Minipar. see Minipar's README file.
	 * @param miniparDynamicLibraryName name of the shared library.
	 * Call this constructor if the name of the shared library is not
	 * the default name.
	 */
	public MiniparParser(String dataDir,String features,String miniparDynamicLibraryName) throws ParserRunException
	{
		this(dataDir,features,miniparDynamicLibraryName,true);
		
	}
	
	/**
	 * Constructor, loads the library if and only if the 
	 * <code> loadLibrary </code> parameter is <code>true</code>.
	 * This constructor is used for cases that two or more
	 * {@linkplain MiniparParser} objects are created, so there is no
	 * need to call loadLibrary twice.
	 * <P>
	 * <B> It is not recommended to use this constructor </B> since multiple
	 * loadLibrary calls on the same library is harmless.
	 * <P>
	 * @param dataDir the path of the Minipar data directory.
	 * @param features names of features the user can ask to be
	 * retrieved by Minipar. see Minipar's README file.
	 * @param loadLibrary true if load library should be called by the
	 * constructor, of false if the user already loaded the library alone.
	 */
	public MiniparParser(String dataDir,String features,boolean loadLibrary) throws ParserRunException
	{
		this(dataDir,features,MINIPAR_DYNAMIC_LIBRARY_NAME,loadLibrary);
	}
	
	

	

	
 
	
	
	
	
	
	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.parse.minipar.AbstractMiniparParser#init()
	 */
	public void init() throws ParserRunException
	{
		if (parserInitialized)
			throw new ParserRunException("init() method was already called before. Reinitialization is not permitted.");
		try
		{
			this.miniparJni.init(this.dataDir, this.features);
		}
		catch(Exception e)
		{
			throw new ParserRunException("Problem with initializing JNI interface to Minipar. See nested exception", e);
		}
		parserInitialized = true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see ac.biu.nlp.nlp.instruments.parse.minipar.AbstractMiniparParser#parse()
	 */
	@Override
	public void parse() throws ParserRunException
	{
		if (!parserInitialized)
			throw new ParserRunException("the parse method was called before the init method was called.");
		super.parse();
	};
	
	
	public void cleanUp()
	{
		super.cleanUp();
		// nothing more.
	}
	
	
	protected MiniparJni miniparJni = new MiniparJni();
	protected String dataDir;
	
	protected boolean parserInitialized = false;


}
