package eu.excitementproject.eop.core.component.lexicalknowledge.custom;
import java.io.IOException;

import eu.excitementproject.eop.common.datastructures.ValueSetMap;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.common.utilities.file.FileUtils;


/**
 * <p>A lexical resource based on a two-column text-file, similar to {@link FileBasedLexicalResource}, but updated online
 * (i.e. the file is read before each operation). 
 * <p>This is useful mainly for small files that are updated often, e.g. in web demos.
 * <p>The format of the file, and the params, are explained in   {@link FileBasedLexicalResource}.
 *
 * @author Erel Segal Halevi
 * @since 2012-07-24
 */
public class OnlineFileBasedLexicalResource extends ValueSetMapLexicalResource {

	/**
	 * @param params configuration params for initialization. Should include:
	 * <li>table_file - path to the file that contains the rules, in table format. Can also be a URL.
	 * <li>table_separator - pattern of column-separator, e.g. "->".
	 * <li>part_of_speech - canonical name of the part-of-speech for this rule-base. For possible values, see {@link SimplerCanonicalPosTag}.
	 * <li>relation_name - name of relation to put in rules (the same for all rules).
	 * <li>minimum_seconds_between_loads (int) - The minimum number of seconds between each consecutive loads from the file.
	 * <li>(NOTE: The params.getModuleName() is used as the resource_name).  
	 */
	public OnlineFileBasedLexicalResource(ConfigurationParams params) throws UnsupportedPosTagStringException, IOException, ConfigurationException {
		super(
				null, 
				params.getEnum(SimplerCanonicalPosTag.class, "part_of_speech"), 
				params.getModuleName(),
				params.getString("relation_name"));
		this.file = params.getString("file");
		this.separator = params.getString("separator");
		this.minimumSecondsBetweenLoads = params.getInt("minimum_seconds_between_loads");
		this.map = ValueSetMapFromStringCreator.mapFromConfigurationParams(params);
	}

	
	/**
	 * @return the map, re-loaded from the file. 
	 * @throws IOException 
	 */
	@Override synchronized public ValueSetMap<String, String> getMap() {
		long currentTimeInSeconds = System.currentTimeMillis() / 1000;
		if (currentTimeInSeconds -  lastLoadTimeInSeconds > minimumSecondsBetweenLoads) {
			try {
				this.map = ValueSetMapFromStringCreator.mapFromTwoColumnsText(FileUtils.loadFileOrUrlToList(this.file), this.separator);
				lastLoadTimeInSeconds = currentTimeInSeconds;
			} catch (IOException e) {
				// If there was an IO exception while reading the file, then we cannot re-read the file,
				// 	so we just use the existing file.
			}
		}
		return this.map;
	}
	
	
	/**
	 * The path to the file that contains the rules (may also be a URL).
	 */
	protected String file;
	
	/**
	 * The pattern that separates the LHS from the RHS in each line of the file. 
	 */
	protected String separator;
	
	/**
	 * The minimum number of seconds between each consecutive loads from the file.
	 */
	protected int minimumSecondsBetweenLoads;
	
	protected long lastLoadTimeInSeconds = 0;
}

