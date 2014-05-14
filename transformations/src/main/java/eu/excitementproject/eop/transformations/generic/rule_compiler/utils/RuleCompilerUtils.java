/**
 * 
 */
package eu.excitementproject.eop.transformations.generic.rule_compiler.utils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.excitementproject.eop.common.component.syntacticknowledge.SyntacticRule;
import eu.excitementproject.eop.transformations.generic.rule_compiler.CompilationException;
import eu.excitementproject.eop.transformations.generic.truthteller.representation.AnnotationRule;


/**
 * Several utils common to both entailment {@link SyntacticRule} and {@link AnnotationRule} compilation.
 * @author Amnon Lotan
 * @since 13/06/2011
 * 
 */
public class RuleCompilerUtils {

	private static final char PERIOD = '.';
	
	/**
	 * @param rules 
	 * @param outFile
	 * @throws CompilationException 
	 */
	public static void serializeToFile(Object rules, String outFile) throws CompilationException 
	{
		try 
		{
			FileOutputStream fos = new FileOutputStream(outFile);
			ObjectOutputStream out = new ObjectOutputStream(fos);
			out.writeObject(rules);
			out.close();
		} catch (FileNotFoundException e) {
			throw new CompilationException(outFile + " was not found", e);
		} catch (IOException e) {
			throw new CompilationException("Error serializing this Object: " + rules.getClass(), e);
		}		
	}
	
	/**
	 * Make out a user friendly description of the rule out of the file name
	 * @param file
	 * @return
	 */
	public static String getDescription(File file) {
		String description = file.getName();
	
		// trim the numerical prefix
		Matcher matcher = Pattern.compile("([a-zA-Z].*)").matcher(description);
		if (matcher.find())
			description = matcher.group(1);
	
		// trim the file suffix
		int indexOfLastPeriod = description.lastIndexOf(PERIOD);
		if (indexOfLastPeriod > 0)
			description = description.substring(0, indexOfLastPeriod);
		
		return description;
	}
}
