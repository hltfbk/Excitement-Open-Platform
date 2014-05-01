package eu.excitementproject.eop.common.utilities.text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;

public class UniToAsciiDemo {

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			String text = null;
			File inputFile = new File("E:\\asher\\data\\uni2ascii\\sample.txt");
			FileInputStream fis = new FileInputStream(inputFile);
			InputStreamReader isr = new InputStreamReader(fis,Charset.forName("UTF-8"));
			
			BufferedReader reader = new BufferedReader(isr);
			text = reader.readLine();
			reader.close();
			if (text==null) throw new Exception ("is null");
			System.out.println(text);
			System.out.println("*");
			
			
			DefaultUnicodeToAsciiConverter converter = new DefaultUnicodeToAsciiConverter();
			converter.setText(text);
			converter.convert();
			text = converter.getConvertedText();
			PrintWriter writer = new PrintWriter(new File("E:\\asher\\data\\uni2ascii\\sample.out.txt"));
			writer.println(text);
			System.out.println(text);
			System.out.println(text.charAt(27));
			writer.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
		}


	}

}
