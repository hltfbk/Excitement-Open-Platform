package eu.excitementproject.eop.transformations.builtin_knowledge;
//import eu.excitementproject.eop.transformations.rteflow.macro.DefaultOperationScript;
//import eu.excitementproject.eop.transformations.utilities.ConfigurationParametersNames;

/**
 * An enumeration of knowledge resources.
 * This enumeration is used by {@link DefaultOperationScript} to construct
 * the appropriate knowledge-resources. The enumerations should be
 * declared in the configuration file, in module {@value ConfigurationParametersNames#KNOWLEDGE_RESOURCES_MODULE_NAME}
 * in parameter {@value ConfigurationParametersNames#KNOWLEDGE_RESOURCES_PARAMETER_NAME}.
 * 
 * See also {@link LexicalResourcesFactory}.
 * 
 * @see DefaultOperationScript
 * @see LexicalResourcesFactory
 * @see ConstructorOfLexicalResourcesForChain
 * 
 * @author Asher Stern
 * @since 2011
 *
 */
public enum KnowledgeResource
{
	WORDNET("WN",false,"WNV2",true),
	WIKIPEDIA("Wiki",false,"WikiV2",true),
	GEO("GeoKB",false,true),
	CATVAR("catvar",false,"catvarV2",true),
	BAP("bap",false,"bapV2",true),
	
//	LIN_SIMILARITY("LinDependencySimilarity",false,true),
	
	// See http://irsrv2.cs.biu.ac.il/wiki/index.php/Lexical_Resources
	// for more information about Lin similarity resources.
	LIN_DEPENDENCY_ORIGINAL(null,false,"LinDependencyOriginal",true),
	LIN_PROXIMITY_ORIGINAL(null,false,"LinProximityOriginal",true),
	LIN_DEPENDENCY_REUTERS(null,false,"LinDependencyReuters",true),
	VERB_OCEAN("VerbOcean",false,"VerbOceanV2",true),
	ORIG_DIRT("origdirt",true,false),
	REVERB("reverb",true,false),
	UNARY_LIN("unary_lin",true,false),
	BINARY_LIN("binary_lin",true,false),
	UNARY_BINC("unaryBinc",true,false),
	FRAMENET("framenet",true,false),
	MANUAL("FromTextFileRuleBase",false,false),
	SYNTACTIC("Syntactic",false,false),
	LEXICAL_CHAIN_BY_GRAPH("lexical inference",false,true),
	PLIS_GRAPH("lexical inference",false,true), // like LEXICAL_CHAIN_BY_GRAPH, but uses its own probabilities, and looks like a regular lexical resource, not a chaining (though it is a chaining, internally).
	SIMPLE_LEXICAL_CHAIN("simpleLexicalChain",false,true);
	;

	public String getModuleName()
	{
		return moduleName;
	}
	
	public String getInfrastructureModuleName()
	{
		return infrastructureModuleName;
	}



	public String getDisplayName()
	{
		return this.name();
	}
	
	public boolean isDirtLikeDb()
	{
		return dirtLikeDb;
	}
	
	public boolean isLexical()
	{
		return lexical;
	}

	private KnowledgeResource(String moduleName, boolean dirtLikeDb, String infrastructureModuleName, boolean lexical)
	{
		this.moduleName = moduleName;
		this.dirtLikeDb = dirtLikeDb;
		this.infrastructureModuleName = infrastructureModuleName;
		this.lexical = lexical;
	}
	
	private KnowledgeResource(String moduleName, boolean dirtLikeDb, boolean lexical)
	{
		this(moduleName,dirtLikeDb,null,lexical);
	}


	
	private final String moduleName;
	private final boolean dirtLikeDb;
	private final String infrastructureModuleName;
	private final boolean lexical;
}
