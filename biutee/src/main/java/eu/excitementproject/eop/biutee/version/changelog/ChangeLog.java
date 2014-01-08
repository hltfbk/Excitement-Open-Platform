package eu.excitementproject.eop.biutee.version.changelog;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import eu.excitementproject.eop.biutee.version.BuildType;
import eu.excitementproject.eop.biutee.version.Version;
import eu.excitementproject.eop.biutee.version.VersionComparator;


/**
 * Stores the change-log and provides method for presenting it.
 * The log is stored at the private field {@link #log}. Changes should be
 * added hard-coded in the method {@link #getLog()}
 * 
 * @author Asher Stern
 * @since Apr 30, 2012
 *
 */
public class ChangeLog
{
	public static String verstionShortString(Version version)
	{
		return version.getProduct()+"."+version.getMajor()+"."+version.getMinor();
	}
	
	/**
	 * Returns the log as a string.
	 * @return
	 */
	public static String logAsString()
	{
		StringBuffer sb = new StringBuffer();
		TreeMap<Version, Set<ChangeLogItem>> mapChangedInVersion = getItemsChangedInVersion();
		for (Version version : mapChangedInVersion.keySet())
		{
			sb.append(verstionShortString(version)).append("\n");
			for (ChangeLogItem item : mapChangedInVersion.get(version))
			{
				sb.append("\t");
				String typeString = "";
				if (item.isMajor()) typeString = typeString +"* ";
				typeString = typeString+item.getChangeType().name();
				sb.append(String.format("%-11s", typeString));
				sb.append(": ").append(item.getDescription()).append("\n");
			}
		}
		return sb.toString();
	}
	
	/**
	 * Returns a map from a version in which changes have been performed - to the set
	 * of the changes that have been performed in it.
	 * @return
	 */
	public static TreeMap<Version, Set<ChangeLogItem>> getItemsChangedInVersion()
	{
		TreeMap<Version, Set<ChangeLogItem>> ret = new TreeMap<Version, Set<ChangeLogItem>>(new VersionComparator());
		TreeMap<Version, Version> mapNext = mapToNextVersion();
		for (ChangeLogItem item : getLog())
		{
			Version changedInVersion = mapNext.get(item.getVersionBeforeChange());
			Set<ChangeLogItem> correspondingSet = null;
			if (ret.containsKey(changedInVersion))
			{
				correspondingSet = ret.get(changedInVersion);
			}
			else
			{
				correspondingSet = new LinkedHashSet<ChangeLogItem>();
				ret.put(changedInVersion, correspondingSet);
			}
			correspondingSet.add(item);
		}
		
		return ret;
	}

	/**
	 * Returns a set of versions that exist in the change-log as
	 * versions-before-changes.
	 * 
	 * @return
	 */
	public static TreeSet<Version> getVersionsInLog()
	{
		TreeSet<Version> ret = new TreeSet<Version>(new VersionComparator());
		for (ChangeLogItem item : getLog())
		{
			ret.add(item.getVersionBeforeChange());
		}
		return ret;
	}

	/**
	 * Returns a map from each version to the immediate successor version.
	 * @return
	 */
	public static TreeMap<Version, Version> mapToNextVersion()
	{
		TreeMap<Version, Version> ret =	new TreeMap<Version, Version>(new VersionComparator());
		TreeSet<Version> setVersions = getVersionsInLog();
		Version prev = null;
		boolean firstIteration = true;
		for (Version version : setVersions)
		{
			if (firstIteration)
			{
				firstIteration=false;
			}
			else
			{
				ret.put(prev,version);
			}
			prev = version;
		}
		ret.put(prev, Version.getVersion());
		return ret;
	}
	
	
	/**
	 * Returns a map from version to a set of changes occurred immediately
	 * after that version
	 * (i.e. occurred in a version which is a subsequent of the specified version).
	 *  
	 * @return
	 */
	public static TreeMap<Version, Set<ChangeLogItem>> getItemsByVersion()
	{
		TreeMap<Version, Set<ChangeLogItem>> ret = new TreeMap<Version, Set<ChangeLogItem>>(new VersionComparator());
		for (ChangeLogItem item : getLog())
		{
			boolean setExist = false;
			Set<ChangeLogItem> set = null;
			if (ret.containsKey(item.getVersionBeforeChange()))
			{
				set = ret.get(item.getVersionBeforeChange());
				setExist=true;
			}
			else
			{
				set = new LinkedHashSet<ChangeLogItem>();
				setExist = false;
			}
			set.add(item);
			if (!setExist)
				ret.put(item.getVersionBeforeChange(),set);
		}
		return ret;
	}
	
	
	private static Version VER_2_1_1 = new Version(2, 1, 1, BuildType.RELEASE);
	private static Version VER_2_2_0 = new Version(2, 2, 0, BuildType.RELEASE);
	private static Version VER_2_3_0 = new Version(2, 3, 0, BuildType.RELEASE);
	private static Version VER_2_4_0 = new Version(2, 4, 0, BuildType.RELEASE);
	private static Version VER_2_4_1 = new Version(2, 4, 1, BuildType.RELEASE);
	private static Version VER_2_5_0 = new Version(2, 5, 0, BuildType.RELEASE);
	
	
	/**
	 * Returns the log.
	 * The log itself is defined here.
	 * @return
	 */
	public static List<ChangeLogItem> getLog()
	{
		synchronized (ChangeLog.class)
		{
			if (null==log)
			{
				ChangeLogItem[] logArray = new ChangeLogItem[]
						{
						// example new ChangeLogItem(VERSION_BEFORE_CHANGE, ChangeType.FIXED, "change description"),
						new ChangeLogItem(VER_2_5_0, ChangeType.CHANGED, "Improve syntactic resource runtime."),
						new ChangeLogItem(VER_2_5_0, ChangeType.CHANGED, "Rewrite TemplatesFromTree to get rid from legacy code."),
						new ChangeLogItem(VER_2_5_0, ChangeType.ADDED, "Add ability to exclude on the fly transformations via configuration file."),
						new ChangeLogItem(VER_2_5_0, ChangeType.CHANGED, "Migrate to Excitement configuration file."),
						new ChangeLogItem(VER_2_5_0, ChangeType.FIXED, "Develope a new ArkRef wrapper to get rid of many bugs."),
						new ChangeLogItem(VER_2_5_0, ChangeType.ADDED, "New high-level flow implementation."),
						new ChangeLogItem(VER_2_4_1, ChangeType.CHANGED, "Migration (Renames & Maven) of code into Excitement. Splitting the code into \"transformations\" project and \"biutee\" project."),
						new ChangeLogItem(VER_2_4_1, ChangeType.ADDED, "Learning models are stored and loaded into/from XML files."),
						new ChangeLogItem(VER_2_4_1, ChangeType.ADDED, "Adding \"is a\" by-coreference transformation."),
						new ChangeLogItem(VER_2_4_1, ChangeType.CHANGED, "Adding \"document sublayer\", for (1) Truth-Teller annotations and (2) converting from BasicNode to ExtendedNode representation."),
						new ChangeLogItem(VER_2_4_1, ChangeType.ADDED, "Syntactic manipulation based on \"ref\" relations, by copying the appropriate argument to the appropriate predicates."),
						new ChangeLogItem(VER_2_4_1, ChangeType.CHANGED, "Training and test of RTE-sum (RTE 6 AND 7) is done using advanced concurrency mechanisms, to improve run-time performance."),
						new ChangeLogItem(VER_2_4_1, true,ChangeType.ADDED, "RTE-pairs can be trained with multiple dataset files."),
						new ChangeLogItem(VER_2_4_1, ChangeType.CHANGED, "Training of RTE-Pairs is done using advanced concurrency mechanisms, to improve run-time performance."),
						new ChangeLogItem(VER_2_4_1,true, ChangeType.ADDED, "Code for syntactic-rules compilation as well as TruthTeller have been integrated."),
						new ChangeLogItem(VER_2_4_1, ChangeType.FIXED, "Multiple bugs in GUI were fixed."),
						new ChangeLogItem(VER_2_4_1, ChangeType.FIXED, "Coreference application does not substitute a node with itself."),
						new ChangeLogItem(VER_2_4_1, ChangeType.FIXED, "Some problems in ArkRef wrapper have been fixed."),
						new ChangeLogItem(VER_2_4_0, ChangeType.ADDED, "Ability to filter lexical rules by a stop-words list (controlled by a CONSTANT flag)."),
						new ChangeLogItem(VER_2_4_0, ChangeType.CHANGED, "AlignmentCriteria has been added, replacing direct usage of static methods."),
						new ChangeLogItem(VER_2_4_0, ChangeType.ADDED, "Change log has been added."),
						new ChangeLogItem(VER_2_4_0, ChangeType.FIXED, "ArkRef wrapper bug."),
						new ChangeLogItem(VER_2_4_0, true, ChangeType.ADDED, "Spelling checker to Visual Tracing Tool."),
						new ChangeLogItem(VER_2_4_0, true, ChangeType.ADDED, "Named-Entity aware lexical resources."),
						new ChangeLogItem(VER_2_4_0, ChangeType.ADDED, "Automatic save of recent pairs in Visual Tracing Tool."),
						new ChangeLogItem(VER_2_4_0, ChangeType.CHANGED, "Using constant score for DIRT-like resources."),
						new ChangeLogItem(VER_2_4_0, ChangeType.CHANGED, "TextPreProcessor\'s now run after sentence splitting."),
						new ChangeLogItem(VER_2_4_0, ChangeType.ADDED, "Automatically adding punctuation mark (a period) to sentences end with no punctuation mark."),
						new ChangeLogItem(VER_2_4_0, ChangeType.ADDED, "Adding ability to use F1 optimized classifier in the Visual Tracing Tool."),
						new ChangeLogItem(VER_2_4_0, ChangeType.ADDED, "Runtime code annotations in ConfigurationParametersNames."),
						new ChangeLogItem(VER_2_4_0, ChangeType.ADDED, "An optional configuration parameter has been added to specify where to store the labeled-samples file."),
						new ChangeLogItem(VER_2_4_0, ChangeType.ADDED, "A heuristic which restricts LLGS algorithm parameter d (number of local iterations)."),
						new ChangeLogItem(VER_2_4_0, ChangeType.ADDED, "Adding FeatureVectorStructureOrganizer."),
						new ChangeLogItem(VER_2_4_0, true, ChangeType.ADDED, "A new home-made F1 optimized classifier has been integrated. Used for search and predictions."),
						new ChangeLogItem(VER_2_4_0, ChangeType.ADDED, "Annotations and constants which indicate workarounds."),
						new ChangeLogItem(VER_2_4_0, true, ChangeType.ADDED, "Major improvements to Visual Tracing Tool - new look."),
						new ChangeLogItem(VER_2_4_0, ChangeType.FIXED, "Detection of subject and object for \"move on the fly\" operation has been fixed for easy-first parser."),
						new ChangeLogItem(VER_2_3_0, true, ChangeType.CHANGED, "Lexical resources now use the new lexical-resources classes in infrastructure. No longer use of legacy code for these resources."),
						new ChangeLogItem(VER_2_3_0, ChangeType.ADDED, "A workaround for SentenceAnnotator for sentences that have a buggy edge-label by Easy-First parser."),
						new ChangeLogItem(VER_2_3_0, ChangeType.CHANGED, "Discarding move-on-the-fly operations that only delete nodes."),
						new ChangeLogItem(VER_2_3_0, true, ChangeType.ADDED, "Plug in mechanism has been added."),
						new ChangeLogItem(VER_2_3_0, ChangeType.ADDED, "Annotations of language-specific classes have been added."),
						new ChangeLogItem(VER_2_3_0, ChangeType.ADDED, "Annotations of parser-specific classes have been added."),
						new ChangeLogItem(VER_2_3_0, ChangeType.ADDED, "Thread-safety annotations have been added."),
						new ChangeLogItem(VER_2_3_0, ChangeType.ADDED, "BIUTEEBaseException has been added."),
						new ChangeLogItem(VER_2_3_0, ChangeType.CHANGED, "Adding PerformFactory - all operations are now handled by this new mechanism."),
						new ChangeLogItem(VER_2_3_0, ChangeType.CHANGED, "Post process of operations is now performed in OperationPostProcess (not in TreeGeneratorByOperatiosn)."),
						new ChangeLogItem(VER_2_3_0, ChangeType.ADDED, "Simple lexical chain"),
						new ChangeLogItem(VER_2_2_0, true, ChangeType.ADDED, "Annotation rules have been integrated."),
						new ChangeLogItem(VER_2_2_0, true, ChangeType.ADDED, "new search algorithm: LLGS, has been added."),
						new ChangeLogItem(VER_2_2_0, ChangeType.ADDED, "new several search algorithms have been added: A*, Weighted A*, K-Staged A*."),
						new ChangeLogItem(VER_2_1_1, true, ChangeType.ADDED, "Generic syntactic rules have been integrated."),
						new ChangeLogItem(VER_2_1_1, true, ChangeType.ADDED, "Easy-First parser has been integrated."),
						new ChangeLogItem(VER_2_1_1, true, ChangeType.ADDED, "Ark-Ref coreference resolver has been integrated."),
						new ChangeLogItem(VER_2_1_1, true, ChangeType.ADDED, "Support in RTE6-7 dataset has been added."),
						new ChangeLogItem(VER_2_1_1, ChangeType.ADDED, "Graph based lexical chain has been added."),
						};
				log = Arrays.asList(logArray);
			}
			return log;
		}
		
	}
	
	private static List<ChangeLogItem> log = null;
}
