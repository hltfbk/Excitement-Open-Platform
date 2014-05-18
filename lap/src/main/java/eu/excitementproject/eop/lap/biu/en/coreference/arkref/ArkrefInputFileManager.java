package eu.excitementproject.eop.lap.biu.en.coreference.arkref;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.UUID;

import eu.excitementproject.eop.common.utilities.file.FileUtils;

/**
 * Manages the temporary input file required by {@link ArkrefClient}.
 * This can be used by any class using or wrapping {@link ArkrefClient}.
 * 
 * @author Ofer Bronstein
 * @since August 2013
 */
public class ArkrefInputFileManager {
	
	// constants
	private static final String TXT = ".txt";
	private static final String ARKREF_TEMP = "arkrefTemp";
	private static final String ARKREF_TEMP_FILE_PREFIX = "arkref_temp_file_";
	private static final String PROCESS_ID = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
	
	// member fields
	protected File workDirectory;
	
	
	public ArkrefInputFileManager() throws ArkrefInputFileManagerException, IOException {
		this(new File(System.getProperty("java.io.tmpdir") + File.separator + ARKREF_TEMP + PROCESS_ID + "__" + UUID.randomUUID().toString()));   
	}
	
	public ArkrefInputFileManager(File workDirectory) throws ArkrefInputFileManagerException, IOException {
		this.workDirectory = workDirectory;
		if(workDirectory.exists())
			if (!FileUtils.deleteDirectory(workDirectory)) {
				throw new ArkrefInputFileManagerException("Could not delete directory " + workDirectory);
			}
		if (workDirectory.mkdir()==false) 
			throw new ArkrefInputFileManagerException("Could not make new directory " + workDirectory); 
		
		
		// filename = workDirectory+File.separator+String.valueOf(RANDOM_GENERATOR.nextInt(1000000))+TXT;
	}

	public File createTempInputFile() throws IOException {
		return File.createTempFile(ARKREF_TEMP_FILE_PREFIX, TXT, workDirectory);
	}
	
	public void cleanUp()
	{
		FileUtils.deleteDirectory(workDirectory);
	}

	public class ArkrefInputFileManagerException extends Exception {
		private static final long serialVersionUID = 6146332579378379568L;

		public ArkrefInputFileManagerException(String msg) {
			super(msg);
		}
	}
}
