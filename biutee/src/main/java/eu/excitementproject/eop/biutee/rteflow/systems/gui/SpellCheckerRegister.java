package eu.excitementproject.eop.biutee.rteflow.systems.gui;
import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.text.JTextComponent;

import com.inet.jortho.FileUserDictionary;
import com.inet.jortho.SpellChecker;

import eu.excitementproject.eop.common.codeannotations.LanguageDependent;


/**
 * Integrates a spell-checker into the given text components.
 * <P>
 * Usage: call the constructor with the list of the text-components into which
 * you want to add a spell checker, and then call {@link #register()}.
 * 
 * @author Asher Stern
 * @since Apr 22, 2012
 *
 */
@LanguageDependent("english")
public class SpellCheckerRegister
{
	public static final String DICTIONARY_FILE_NAME = "dictionary_en.ortho";
	public static final String CNF_FILE_NAME = "dictionaries.cnf";
	public static final String CNF_FILE_CONTENTS = "extension=.ortho\n" +
			"languages=en\n";
	
	public static final String ACTIVE_LANGUAGE = "en";
	
	public static enum SpellCheckerRegisterResult
	{
		REGISTERED,
		NOT_REGISTERED_BUT_CONTINUE,
		NOT_REGISTERED_SHOULD_EXIT;
	}
	
	/**
	 * A constructor that gets a list of text-components into which
	 * the caller wants to integrate a spell-checker.
	 * @param textComponents
	 */
	public SpellCheckerRegister(Component owner, List<JTextComponent> textComponents)
	{
		super();
		this.textComponents = textComponents;
		this.owner = owner;
	}

	/**
	 * Call this method to add the spell checker into the desired text-components.
	 * @throws FileNotFoundException
	 */
	public SpellCheckerRegisterResult register() throws FileNotFoundException
	{
		SpellCheckerRegisterResult result = SpellCheckerRegisterResult.NOT_REGISTERED_SHOULD_EXIT;
		File dicFile = new File(DICTIONARY_FILE_NAME);
		if (!dicFile.exists())
		{
			String message = "It seems that the dictionary file \""+DICTIONARY_FILE_NAME+"\" " +
					"does not exist in the working directory.\n" +
					"Thus, the spell-checker cannot be registered.\n" +
					"To enable spell checker, copy the dictionary file to the " +
					"working directory, and restart the application.\n" +
					"The file can be found at $JARS/jortho/dictionary_en_2010_09/ \n" +
					"See also http://jortho.sourceforge.net/\n" +
					"Do you want to exit? Click \"Yes\" for exit, click \"No\" to continue without spell-checker."
					;

			
			int userClick = JOptionPane.showOptionDialog(owner, message,  "Spell checker warning", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"Yes", "No"}, null);
			if (userClick == JOptionPane.NO_OPTION)
				result = SpellCheckerRegisterResult.NOT_REGISTERED_BUT_CONTINUE;
			else
				result = SpellCheckerRegisterResult.NOT_REGISTERED_SHOULD_EXIT;
			
			
			
//			JOptionPane.showMessageDialog(null,
//					"It seems that the dictionary file \""+DICTIONARY_FILE_NAME+"\" " +
//							"does not exist in the working directory.\n" +
//							"Thus, the spell-checker will not be registered.\n" +
//							"To enable spell checker, copy the dictionary file to the " +
//							"working directory, and restart the application.\n" +
//							"The file can be found at $JARS/jortho/dictionary_en_2010_09/ \n" +
//							"See also http://jortho.sourceforge.net/",
//					"Spell checker warning",
//					JOptionPane.WARNING_MESSAGE);
		}
		else
		{
			File cnfFile = new File(CNF_FILE_NAME);
			if (!cnfFile.exists())
			{
				PrintWriter writer = new PrintWriter(cnfFile);
				try
				{
					writer.println(CNF_FILE_CONTENTS);
				}
				finally
				{
					writer.close();
				}
				
			}
			
			// Create user dictionary in the current working directory of your application
	        SpellChecker.setUserDictionaryProvider( new FileUserDictionary() );
	        
	        // Load the configuration from the file dictionaries.cnf and 
	        // use the current locale or the first language as default 
	        SpellChecker.registerDictionaries( null, ACTIVE_LANGUAGE );

	        for (JTextComponent text : textComponents)
	        {
	        	SpellChecker.register( text );
	        }
	        result = SpellCheckerRegisterResult.REGISTERED;
		}
		
		return result;
	}
	
	private Component owner;
	private List<JTextComponent> textComponents;
}
