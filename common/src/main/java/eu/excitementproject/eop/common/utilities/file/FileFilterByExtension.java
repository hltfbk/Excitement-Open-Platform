package eu.excitementproject.eop.common.utilities.file;

import java.io.File;

/**
 * 
 * @author Asher Stern
 * @since Nov 22, 2011
 *
 */
public class FileFilterByExtension extends javax.swing.filechooser.FileFilter
{
	public static final String SEPARATOR = ",";
	
	public FileFilterByExtension(String[] extensions)
	{
		this.extensions = extensions;
	}

	public FileFilterByExtension(String extension)
	{
		this(new String[]{extension});
	}

	
	@Override
	public boolean accept(File f)
	{
		boolean ret = false;
		if (f.isDirectory()) ret = true;
		else
		{
			String f_extention = FileUtils.getFileExtension(f);
			for (String extension : extensions)
			{
				if (extension.equals(f_extention))
				{
					ret = true;
					break;
				}
			}
		}
		return ret;
	}

	@Override
	public String getDescription()
	{
		prepareDescription();
		return this.description;
	}
	
	private synchronized void prepareDescription()
	{
		if (null==description)
		{
			StringBuffer sb = new StringBuffer();
			boolean firstIteration = true;
			for (String extension : extensions)
			{
				if (firstIteration)
				{
					firstIteration=false;
				}
				else
				{
					sb.append(" ").append(SEPARATOR);
				}

				sb.append(extension);
			}
			sb.append(" files");
			description = sb.toString();
		}
	}

	private String[] extensions;
	private String description = null;
}

