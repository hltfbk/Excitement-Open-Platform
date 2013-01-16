package eu.excitementproject.eop.common.utilities.log4j;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.FileAppender;

import eu.excitementproject.eop.common.utilities.ExceptionUtil;
import eu.excitementproject.eop.common.utilities.file.FileUtils;



/**
 * Like org.apache.log4j.FileAppender, but first it backups the existing log file.
 * The backup name is the log file name with post-fix indicates the current date and time, and
 * optionally appended by a UUID.
 * 
 * @author Asher Stern
 * @since Feb 28, 2011
 *
 */
public class BackupOlderFileAppender extends FileAppender
{
	public BackupOlderFileAppender()
	{
		super();
	}
	
	@Override
	public synchronized void setFile(String fileName, boolean append, boolean bufferedIO, int bufferSize)throws IOException
	{
		if (!alreadyCopiedSet.contains(fileName))
		{
			File file = new File(fileName);
			if (file.exists()){if (file.isFile())
			{
				Calendar c = Calendar.getInstance();
				StringBuffer sb = new StringBuffer();
				sb.append('_');
				sb.append(c.get(Calendar.YEAR));
				sb.append('_');
				sb.append(1+c.get(Calendar.MONTH)); // Months start from zero, need to add 1
				sb.append('_');
				sb.append(c.get(Calendar.DAY_OF_MONTH));
				sb.append('_');
				sb.append(c.get(Calendar.HOUR_OF_DAY));
				sb.append('_');
				sb.append(c.get(Calendar.MINUTE));
				sb.append('_');
				sb.append(c.get(Calendar.SECOND));

				File backup = new File(fileName+sb.toString()+".log");
				if (backup.exists())
				{
					UUID uuid = UUID.randomUUID();
					backup = new File(fileName+sb.toString()+"_"+uuid.toString()+".log");
				}
				try
				{
					FileUtils.copyFile(file, backup);
				}
				catch(Exception e)
				{
					StringBuffer sbException = new StringBuffer();

					sbException.append("Could not copy to a backup file: ");
					sbException.append(backup.getAbsolutePath());
					sbException.append(" due to the following exception:\n");
					sbException.append(ExceptionUtil.getMessages(e));
					sbException.append(ExceptionUtil.getStackTrace(e));
					throw new IOException(sbException.toString());
				}
			}}
			alreadyCopiedSet.add(fileName);
		}
		
		super.setFile(fileName, append, bufferedIO, bufferSize);
	}

	/**
	 * The copy should be done only once in the process life-time.
	 */

	
	private static final Set<String> alreadyCopiedSet = Collections.synchronizedSet(new HashSet<String>());
}
