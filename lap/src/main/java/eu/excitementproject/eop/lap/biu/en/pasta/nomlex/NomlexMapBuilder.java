package eu.excitementproject.eop.lap.biu.en.pasta.nomlex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.BIU.NLP.TE.impl.nominalizations.ClassRoleTable;
import org.BIU.NLP.TE.impl.nominalizations.NomlexBase;
import org.BIU.NLP.TE.impl.nominalizations.NomlexClass;
import org.BIU.NLP.TE.impl.nominalizations.NomlexClassList;
import org.BIU.NLP.TE.impl.nominalizations.NomlexEntry;
import org.BIU.NLP.TE.impl.nominalizations.Position;
import org.BIU.NLP.TE.impl.nominalizations.PositionList;
import org.apache.log4j.Logger;

import eu.excitementproject.eop.common.datastructures.SimpleValueSetMap;
import eu.excitementproject.eop.common.datastructures.ValueSetMap;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableListWrapper;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableMap;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableMapWrapper;
import eu.excitementproject.eop.common.representation.pasta.ArgumentType;

/**
 * Builds a "nomlex map" - which is extracted from "nomlex" corpus. This map is used in {@link PredicateArgumentStructureBuilder}.
 * @author Asher Stern
 * @since Oct 14, 2012
 *
 */
public class NomlexMapBuilder
{
	/**
	 * Constructor which get parameters of files needed for building the map.
	 * Then, the user can call {@link #build()} to build the map, and finally
	 * call {@link #getNomlexMap()} to get the map.
	 * 
	 * @param nomlexFileName The file "nomlex-plus.txt". It can be found in our SVN
	 * in trunk/common/data/Nomlex
	 * @param classRoleTableTextFile This file is described in Tal Ron's thesis,
	 * and can be found in the project "wrappers" in wrappers/data/nominalizations/ClassRoleTable.txt
	 */
	public NomlexMapBuilder(String nomlexFileName, String classRoleTableTextFile)
	{
		super();
		this.nomlexFileName = nomlexFileName;
		this.classRoleTableTextFile = classRoleTableTextFile;
	}
	
	
	public void build() throws NomlexException
	{
		boolean initSucceeded = false;
		try
		{
			initNomlex();
			initSucceeded = true;
		}
		catch(Exception e)
		{
			throw new NomlexException("Nomlex initialization failed.",e);
		}
		if (initSucceeded)
		{
			internalNomlexMap = new LinkedHashMap<String, Nominalization>();
			processAllNomlexBaseEntries();
			nomlexMap = new ImmutableMapWrapper<String, Nominalization>(internalNomlexMap);
		}
	}
	
	public ImmutableMap<String, Nominalization> getNomlexMap() throws NomlexException
	{
		if (null==nomlexMap) throw new NomlexException("Caller\'s bug: build() was not called.");
		return nomlexMap;
	}
	
	
	////////////////////////////// PRIVATE ////////////////////////////// 


	private void initNomlex() throws Exception // Tal Ron's code throws java.lang.Exception
	{
		nomlexBase = new NomlexBase();
		nomlexBase.read(nomlexFileName);
		classRoleTable = new ClassRoleTable();
		classRoleTable.read(classRoleTableTextFile);
	}
	
	private void processAllNomlexBaseEntries() throws NomlexException
	{
		NomlexEntry entry = null;
		for (Iterator<NomlexEntry> iterator = nomlexBase.iter();iterator.hasNext(); entry = iterator.next())
		{
			if (entry!=null)
			{
				processEntry(entry);
			}
		}
	}
	
	private void processEntry(NomlexEntry entry) throws NomlexException
	{
		// Create a NomlexClassList from which the information can be retrieved
        NomlexClassList nomlexClassList = new NomlexClassList(entry.getNoun(), entry.getVerb(), entry.getNomtype(), entry.getVerbsubc(), classRoleTable);
        
        // The information we need:
        // 1. the nominal
        // 2. all verbs which can be nominalized into that nominal
        // 3. all the nomlex arguments and their types
        // 4. if that nominal serves as the role (e.g. abductor to abduct), we need to know that role.
        String noun = null;
        Set<String> verbs = new LinkedHashSet<String>();
        ValueSetMap<NomlexArgument, ArgumentType> mapNomlexArgumentsToArgumentType = new SimpleValueSetMap<NomlexArgument, ArgumentType>();
        ArgumentType itsOwnRole = null;
        boolean someInformationRetrieved = false;
        
        for(NomlexClass nomlexClass : nomlexClassList)
        {
            //retrieve attributes of the nomlex class
        	if (null==noun)
        	{
        		noun = nomlexClass.noun.trim().toLowerCase(Locale.ENGLISH);
        	}
        	else
        	{
        		if (!noun.equals(nomlexClass.noun.trim().toLowerCase(Locale.ENGLISH))) throw new NomlexException("Error! Two different nouns in the same entry: "+noun+", "+nomlexClass.noun);
        	}
        	if (null==noun) throw new NomlexException("No noun! noun is null.");
        	for (String verb : nomlexClass.verbList)
        	{
        		verbs.add(verb);
        	}

            //retrieve position lists of roles
            List<PositionList> positionLists = nomlexClass.PositionLists;
            
            for (PositionList positionList : positionLists)
            {
            	ArgumentType argumentType = null;
            	String nomlexRole = positionList.role;
            	if (MAP_PLACE_TO_ARGUMENT_TYPE.containsKey(nomlexRole))
            	{
            		argumentType = MAP_PLACE_TO_ARGUMENT_TYPE.get(nomlexRole);
            	}
            	if (null==argumentType)
            	{
            		argumentType = ArgumentType.UNKNOWN;
            	}
            	
            	for (Position position : positionList)
            	{
            		String nomlexPlace = position.getPlace();
            		if (!IGNORE_PLASE.equals(nomlexPlace))
            		{
            			String[] nomlexPrepositions = position.getPvallist();
            			nomlexPrepositions = removeItem(nomlexPrepositions,IGNORE_PREPOSITION);

            			boolean self = SELF_ROLES.contains(nomlexPlace);

            			boolean hasPrepositions = false;
            			if (nomlexPrepositions!=null){if (nomlexPrepositions.length>0)
            			{
            				hasPrepositions = true;
            			}}
            			if (hasPrepositions)
            			{
            				if (self) throw new NomlexException("Unexpected self role that has a preposition, for nominal: \""+noun+"\".");
            				for (String preposition : nomlexPrepositions)
            				{
            					NomlexArgument nomlexArgument = new NomlexArgument(nomlexPlace,preposition);
            					mapNomlexArgumentsToArgumentType.put(nomlexArgument,argumentType);
            				}
            			}
            			else
            			{
            				if (self)
            				{
            					if (itsOwnRole!=null) itsOwnRole = ArgumentType.UNKNOWN;
            					else itsOwnRole = argumentType;
            				}
            				else
            				{
            					NomlexArgument nomlexArgument = new NomlexArgument(nomlexPlace);
            					mapNomlexArgumentsToArgumentType.put(nomlexArgument,argumentType);
            				}
            			}
            		}

            		
            	}
            	
            }
            someInformationRetrieved = true;
        } // end of for "for each nomlex class"
        if (!someInformationRetrieved)
        {
        	++debug_numberOfEntriesWithNoInformation;
        	// throw new NomlexException("No information for entry: "+entry.toString());
        	logger.warn("No information for entry: "+entry.toString()+
        			"\nThis typically occurs due to an entry which is filtered by the ClassRoleTable file.");
        	logger.warn("Up until now "+debug_numberOfEntriesWithNoInformation+" entries had no information.");
        }
        else
        {

        	Map<NomlexArgument, ArgumentType> mapArgumentToType = new LinkedHashMap<NomlexArgument, ArgumentType>();
        	for (NomlexArgument nomlexArgument : mapNomlexArgumentsToArgumentType.keySet())
        	{
        		if (mapNomlexArgumentsToArgumentType.get(nomlexArgument).size()==1)
        		{
        			mapArgumentToType.put(nomlexArgument, mapNomlexArgumentsToArgumentType.get(nomlexArgument).iterator().next());
        		}
        		else
        		{
        			mapArgumentToType.put(nomlexArgument,ArgumentType.UNKNOWN);
        		}
        	}

        	if (itsOwnRole!=null){if(itsOwnRole.equals(ArgumentType.UNKNOWN))
        	{
        		itsOwnRole = null;
        	}}

        	List<String> verbsList = new ArrayList<String>(verbs.size());
        	verbsList.addAll(verbs);

        	Nominalization nominalization = new Nominalization(noun, new ImmutableListWrapper<String>(verbsList), itsOwnRole, new ImmutableMapWrapper<NomlexArgument, ArgumentType>(mapArgumentToType));
        	if (null==nominalization.getNominal()) throw new NomlexException("BUG");
        	if (internalNomlexMap.containsKey(nominalization.getNominal()))
        	{
        		throw new NomlexException("Encountered the same noun twice: \""+nominalization.getNominal()+"\".");
        	}
        	internalNomlexMap.put(nominalization.getNominal(), nominalization);
        }
	}
	
	private static String[] removeItem(String[] array, String item)
	{
		if (null==array) return array;
		else
		{
			ArrayList<String> list = new ArrayList<String>(array.length);
			for (String str : array)
			{
				if (!item.equals(str))
				{
					list.add(str);
				}
			}
			return list.toArray(new String[0]);
		}
	}
	
	// static
	private static final String IGNORE_PLASE = "NONE";
	private static final String IGNORE_PREPOSITION = "none";
	private static final Set<String> SELF_ROLES = new HashSet<String>();
	private static final Map<String, ArgumentType> MAP_PLACE_TO_ARGUMENT_TYPE = new HashMap<String, ArgumentType>(); 
	static
	{
		SELF_ROLES.add("BE-SUBJ");
		MAP_PLACE_TO_ARGUMENT_TYPE.put("SUBJECT", ArgumentType.SUBJECT);
		MAP_PLACE_TO_ARGUMENT_TYPE.put("OBJECT", ArgumentType.OBJECT);
	}
	
	
	

	// input
	private final String nomlexFileName;
	private final String classRoleTableTextFile;
	
	// internals
	private ClassRoleTable classRoleTable;
	private NomlexBase nomlexBase;
	private Map<String, Nominalization> internalNomlexMap;
	private int debug_numberOfEntriesWithNoInformation = 0;
	
	// output
	private ImmutableMap<String, Nominalization> nomlexMap;
	
	private static final Logger logger = Logger.getLogger(NomlexMapBuilder.class);
}
