package eu.excitementproject.eop.transformations.utilities;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import eu.excitementproject.eop.transformations.builtin_knowledge.KnowledgeResource;

/**
 * 
 * @author Asher Stern
 * @since Jan 14, 2013
 *
 */
public class TransformationsConfigurationParametersNames
{
	
	public static enum MandatoryLevel
	{
		MANDATORY,OPTIONAL,IGNORE,DEPRECATED;
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public static @interface ConfigurationModuleAnnotation
	{
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public static @interface ConfigurationParameterAnnotation
	{
		/**
		 * Indicates the module for which this parameter belongs to.
		 * @return
		 */
		public String[] value();
		public MandatoryLevel mandatoryLevel() default MandatoryLevel.MANDATORY;
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public static @interface ConfigurationTruthTellerParameterAnnotation
	{
		public MandatoryLevel mandatoryLevel() default MandatoryLevel.MANDATORY;
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public static @interface ConfigurationDirtParameterAnnotation
	{
		public MandatoryLevel mandatoryLevel() default MandatoryLevel.MANDATORY;
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public static @interface ConfigurationKnowledgeParameterAnnotation
	{
		public KnowledgeResource[] knowledgeResources();
		public MandatoryLevel mandatoryLevel() default MandatoryLevel.MANDATORY;
	}
	
	
	
	

	// Parameters for specific knowledge resources
	public static final String MANUAL_FILE_RULEBASE_FILE_PARAMETER_NAME = "file";
	public static final String MANUAL_FILE_RULEBASE_DYNAMIC_PARAMETER_NAME = "dynamic";
	public static final String MANUAL_FILE_RULEBASE_USE_PARAMETER_NAME = "use";
	
	@ConfigurationDirtParameterAnnotation
	public static final String DB_DRIVER = "database_driver";
	@ConfigurationDirtParameterAnnotation
	public static final String DB_URL = "database_url";
	@ConfigurationDirtParameterAnnotation
	public static final String TEMPLATES_TABLE_NAME = "templates_table";
	@ConfigurationDirtParameterAnnotation
	public static final String RULES_TABLE_NAME = "rules_table";
	@ConfigurationDirtParameterAnnotation
	public static final String LIMIT_NUMBER_OF_RULES = "limit_number_of_rules";
	@ConfigurationDirtParameterAnnotation(mandatoryLevel=MandatoryLevel.IGNORE)
	public static final String DIRT_LIKE_SER_FILE_PARAMETER_NAME = "serialization_file";
	
	@ConfigurationKnowledgeParameterAnnotation(knowledgeResources=KnowledgeResource.SYNTACTIC)
	public static final String SYNTACTIC_RULES_FILE = "syntactic_rules_file";

	//
	// TruthTeller
	//
	@ConfigurationModuleAnnotation
	public static final String TRUTH_TELLER_MODULE_NAME = "TruthTeller";
	@ConfigurationTruthTellerParameterAnnotation
	public static final String ANNOTATION_RULES_FILE = "annotation_rules_file";
	@ConfigurationTruthTellerParameterAnnotation
	public static final String USER_REQUIRES_ANNOTATIONS = "do_annotations";
	@ConfigurationTruthTellerParameterAnnotation
	public static final String CONLL_FORMAT_OUTPUT_DIRECTORY = "conll_format_output_directory";
	@ConfigurationTruthTellerParameterAnnotation
	public static final String PREPROCESS_EASYFIRST = "easyfirst_stanford_pos_tagger";
	@ConfigurationTruthTellerParameterAnnotation
	public static final String PREPROCESS_EASYFIRST_HOST = "easyfirst_host";
	@ConfigurationTruthTellerParameterAnnotation
	public static final String PREPROCESS_EASYFIRST_PORT = "easyfirst_port";
	
	// simple-lexical-chain
	@ConfigurationKnowledgeParameterAnnotation(knowledgeResources=KnowledgeResource.SIMPLE_LEXICAL_CHAIN)
	public static final String SIMPLE_LEXICAL_CHAIN_DEPTH_PARAMETER_NAME = "depth";
	@ConfigurationKnowledgeParameterAnnotation(knowledgeResources=KnowledgeResource.SIMPLE_LEXICAL_CHAIN)
	public static final String SIMPLE_LEXICAL_CHAIN_KNOWLEDGE_RESOURCES = "knowledge_resources";
	
	
	@ConfigurationKnowledgeParameterAnnotation(knowledgeResources=KnowledgeResource.GEO)
	public static final String CONNECTION_STRING_GEO_PARAMETER_NAME = "connection";
	@ConfigurationKnowledgeParameterAnnotation(knowledgeResources=KnowledgeResource.GEO)
	public static final String TABLE_NAME_PARAMETER_NAME = "table";
	
	
	@ConfigurationKnowledgeParameterAnnotation(knowledgeResources=KnowledgeResource.LIN_DEPENDENCY_REUTERS)
	public static final String CONNECTION_STRING_LIN_REUTERS_PARAMETER_NAME = "database_url";
	@ConfigurationKnowledgeParameterAnnotation(knowledgeResources=KnowledgeResource.LIN_DEPENDENCY_REUTERS)
	public static final String LIMIT_LIN_REUTERS_PARAMETER_NAME = "limit on retrieved rules";
	




}
