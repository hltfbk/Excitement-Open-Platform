package eu.excitementproject.eop.common.utilities.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Compress and extract files into/from a ZipOutputStream file
 * @author Jonathan 
 */
public class ZipFile 
{
	private static final int BYTE_BUFF_SZ = 4096;

	/**
	 * Constructor
	 * @param iZipFile
	 */
	public ZipFile(File iZipFile)
	{
		m_zipFile = iZipFile;
	}
	
	/**
	 * ZIP Compress a Collection of Files using ZipOutputStream, into the m_zipFileName
	 * @param iFilesToCompress
	 * @throws IOException
	 */
	public void compress(Collection<File> iFilesToCompress) throws IOException
	{						
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(m_zipFile));
		byte[] buffer = new byte[BYTE_BUFF_SZ];
		
		// for each file, write it to out
		for(File file : iFilesToCompress)
		{			
			ZipEntry entry = new ZipEntry(file.getName());
			entry.setSize(file.length());
			
			out.putNextEntry(entry);
		
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
			
			int len;
			while((len = in.read(buffer)) > -1)
				out.write(buffer, 0, len);
			
			in.close();
		}
				
		out.close();
	}
	
	/**
	 * ZIP Uncompress the m_zipFileName into iPrefixDir directory (create it if necessary), using ZipOutputStream 
	 * @param iPrefixDir
	 * @throws IOException
	 */
	public void uncompress(File iPrefixDir) throws IOException
	{
		java.util.zip.ZipFile zipFile = new java.util.zip.ZipFile(m_zipFile);
		Enumeration<? extends ZipEntry> entries = zipFile.entries();
		byte[] buffer = new byte[4096];		
		
		iPrefixDir.mkdirs();
		
		// for each entry in the zip, write it to its own file in iPrefixDir
		while(entries.hasMoreElements())
		{
			ZipEntry entry = entries.nextElement();
			
			File outFile = new File(iPrefixDir, entry.getName());
			
			BufferedInputStream in = new BufferedInputStream(zipFile.getInputStream(entry));
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outFile));

			int len;
			while((len = in.read(buffer)) > -1)
				out.write(buffer, 0, len);
			
			out.close();
			in.close();
		}
			
		zipFile.close();
	}
	
	
	
	/**
	 * @return m_zipFile
	 */
	public File getZipFile()
	{
		return m_zipFile;
	}
	
	private File m_zipFile;
}
