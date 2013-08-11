package eu.excitementproject.eop.lap.biu.en.pasta;

import java.io.File;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableMap;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.lap.biu.en.pasta.nomlex.Nominalization;
import eu.excitementproject.eop.lap.biu.en.pasta.nomlex.NomlexException;
import eu.excitementproject.eop.lap.biu.en.pasta.nomlex.NomlexMapBuilder;
import eu.excitementproject.eop.lap.biu.pasta.identification.PredicateArgumentIdentificationException;

/**
 * Creates {@link PredicateArgumentStructureBuilderFactory} from configuration file
 * ( {@link ConfigurationFile} ).
 * 
 * @author Asher Stern
 * @since Mar 19, 2013
 *
 */
public class PredicateArgumentStructureBuilderFactoryFactory<I extends Info, S extends AbstractNode<I, S>>
{
	public static final String BUILDER_MODULE_NAME = "pasta-builder";
	public static final String NOMLEX_FILE_PARAMETER_NAME = "nomlex-file";
	public static final String CLASS_ROLE_TABLE_PARAMETER_NAME = "nomlex-class-role-table";
	
	public PredicateArgumentStructureBuilderFactoryFactory(ConfigurationFile configurationFile) throws ConfigurationException, NomlexException
	{
		this(configurationFile.getModuleConfiguration(BUILDER_MODULE_NAME));
	}
	
	public PredicateArgumentStructureBuilderFactoryFactory(ConfigurationParams params) throws ConfigurationException, NomlexException
	{
		this.nomlexMap = createNomlexMapFromConfigurationParams(params);
	}
	
	public PredicateArgumentStructureBuilderFactoryFactory(ImmutableMap<String, Nominalization> nomlexMap)
	{
		this.nomlexMap = nomlexMap;
	}
	
	public PredicateArgumentStructureBuilderFactory<I,S> createBuilderFactory() throws PredicateArgumentIdentificationException
	{
		if (null==this.nomlexMap) throw new PredicateArgumentIdentificationException("null nomlex map.");
		
		return new PredicateArgumentStructureBuilderFactory<I,S>(this.nomlexMap);
	}

	
	private ImmutableMap<String, Nominalization> createNomlexMapFromConfigurationParams(ConfigurationParams params) throws ConfigurationException, NomlexException
	{
		File nomlexFile = params.getFile(NOMLEX_FILE_PARAMETER_NAME);
		File classRoleTableFile = params.getFile(CLASS_ROLE_TABLE_PARAMETER_NAME);
		
		NomlexMapBuilder nomlexMapBuilder = new NomlexMapBuilder(nomlexFile.getPath(),classRoleTableFile.getPath());
		nomlexMapBuilder.build();
		return nomlexMapBuilder.getNomlexMap();
	}

	private ImmutableMap<String, Nominalization> nomlexMap = null;
}
