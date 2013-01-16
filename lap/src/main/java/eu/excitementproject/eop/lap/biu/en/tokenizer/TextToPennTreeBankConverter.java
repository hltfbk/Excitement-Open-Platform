package eu.excitementproject.eop.lap.biu.en.tokenizer;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import eu.excitementproject.eop.common.utilities.LiveIOProgramExecution;
import eu.excitementproject.eop.common.utilities.LiveIOProgramExecutionException;
import eu.excitementproject.eop.common.utilities.Utils;


/**
 * @deprecated And old class that does not implement the
 * tokenizer interface.
 * <P>
 * Converts a list of sentences to a Penn Tree bank format.
 * The conversion is actually a tokenization.
 * For example:
 * <pre>
 * I don't want to pay $10 for that.
 * </pre>
 * will be converted to:
 * <pre>
 * I don ' t want to pay $ 10 for that .
 * </pre>
 * <P>
 * Some parsers require the output to be fed in a tokenized form.
 * <P>
 * <B> This class is not thread safe! </B> (but it is not expensive
 * to use several instances of this class, one per each thread).
 * @author Asher Stern
 *
 */
@Deprecated
public class TextToPennTreeBankConverter
{
	// CONSTANTS (public & protected):
	public static final int TIME_OUT = 3*1000;
	
	protected static final String CONVERTER_SCRIPT_NAME_PREFIX = "penntb_converter";
	protected static final String CONVERTER_SCRIPT_NAME_EXTENSION = ".sed";
	
	protected static final String CONVERTER_PROGRAM = "sed";
	protected static final String[] CONVERTER_PARAMS = {"-u"};
	protected static final String CONVERTER_SCRIPT_NAME_SPECIFIER_PARAM = "-f";
	
	
	//////////////////////////////// PUBLIC PART ////////////////////////////

	// nested class (exception class):
	
	@SuppressWarnings("serial")
	public static class PennTreeBankConverterException extends Exception
	{

		public PennTreeBankConverterException(String message, Throwable cause) {
			super(message, cause);
		}

		public PennTreeBankConverterException(String message) {
			super(message);
		}
		
	}


	///////////////// PUBLIC METHODS AND CONSTRUCTORS /////////////////////
	
	/**
	 * Default constructor
	 */
	public TextToPennTreeBankConverter()
	{
		
	}
	
	/**
	 * Set the sentences to convert.
	 * @param sentences list of sentences.
	 */
	public void setSentences(List<String> sentences) 
	{
		this.sentences = sentences;
	}

	/**
	 * Does the conversion.
	 * @throws PennTreeBankConverterException
	 */
	public void convert() throws PennTreeBankConverterException
	{
		if (null==sentences)
			throw new PennTreeBankConverterException("sentences are null. Call setSentences() before calling this method.");
		try
		{
			start();
			convertedSentences = new ArrayList<String>(this.sentences.size());
			for (String sentence : this.sentences)
			{
				execution.putLine(sentence);
				String convertedLine = execution.getLine(TIME_OUT);
				convertedSentences.add(convertedLine);
			}
		}
		catch(LiveIOProgramExecutionException e)
		{
			throw new PennTreeBankConverterException("Cannot run the script. See nested exception",e);
		}

		
	}

	/**
	 * After calling {@link #convert()}, call this method to get the
	 * results, i.e. the converted (tokenized) sentences.
	 * 
	 * @return the tokenized sentences.
	 */
	public List<String> getPennTreebankSentences()
	{
		return this.convertedSentences;
	}
	
	
	/**
	 * After you are done with this converter, call this method
	 * to clean up.
	 * Don't call any other method of this converter later.
	 */
	public void end()
	{
		if (!finalizationDone)
		{
			if (execution!=null)
			{
				try{execution.endIO();}catch(Exception e){}
				try{convertionFile.delete();}catch(Exception e){}
				execution = null;
				convertionFile = null;
				finalizationDone = true;
			}
		}
	}

	
	

	/////////////////////// PROTECTED AND PRIVATE PART //////////////////////////////
	
	protected void createConvertionScript() throws IOException
	{
		File converterFile = File.createTempFile(CONVERTER_SCRIPT_NAME_PREFIX, CONVERTER_SCRIPT_NAME_EXTENSION);
		PrintStream ps = new PrintStream(converterFile);
		//ps.println("#!/bin/sed -f");
		//ps.println("");
		ps.println("# Sed script to produce Penn Treebank tokenization on arbitrary raw text.");
		ps.println("# Yeah, sure.");
		ps.println("");
		ps.println("# expected input: raw text with ONE SENTENCE TOKEN PER LINE");
		ps.println("");
		ps.println("# by Robert MacIntyre, University of Pennsylvania, late 1995.");
		ps.println("");
		ps.println("# If this wasn\'t such a trivial program, I\'d include all that stuff about");
		ps.println("# no warrantee, free use, etc. from the GNU General Public License.  If you");
		ps.println("# want to be picky, assume that all of its terms apply.  Okay?");
		ps.println("");
		ps.println("# attempt to get correct directional quotes");
		ps.println("s=^\"=`` =g");
		ps.println("s=\\([ ([{<]\\)\"=\\1 `` =g");
		ps.println("# close quotes handled at end");
		ps.println("");
		ps.println("s=\\.\\.\\.= ... =g");
		ps.println("s=[,;:@#$%&]= & =g");
		ps.println("");
		ps.println("# Assume sentence tokenization has been done first, so split FINAL periods");
		ps.println("# only. ");
		ps.println("s=\\([^.]\\)\\([.]\\)\\([])}>\"\']*\\)[ \t]*$=\\1 \\2\\3 =g");
		ps.println("# however, we may as well split ALL question marks and exclamation points,");
		ps.println("# since they shouldn\'t have the abbrev.-marker ambiguity problem");
		ps.println("s=[?!]= & =g");
		ps.println("");
		ps.println("# parentheses, brackets, etc.");
		ps.println("s=[][(){}<>]= & =g");
		ps.println("# Some taggers, such as Adwait Ratnaparkhi\'s MXPOST, use the parsed-file");
		ps.println("# version of these symbols.");
		ps.println("# UNCOMMENT THE FOLLOWING 6 LINES if you\'re using MXPOST.");
		ps.println("# s/(/-LRB-/g");
		ps.println("# s/)/-RRB-/g");
		ps.println("# s/\\[/-LSB-/g");
		ps.println("# s/\\]/-RSB-/g");
		ps.println("# s/{/-LCB-/g");
		ps.println("# s/}/-RCB-/g");
		ps.println("");
		ps.println("s=--= -- =g");
		ps.println("");
		ps.println("# NOTE THAT SPLIT WORDS ARE NOT MARKED.  Obviously this isn\'t great, since");
		ps.println("# you might someday want to know how the words originally fit together --");
		ps.println("# but it\'s too late to make a better system now, given the millions of");
		ps.println("# words we\'ve already done \"wrong\".");
		ps.println("");
		ps.println("# First off, add a space to the beginning and end of each line, to reduce");
		ps.println("# necessary number of regexps.");
		ps.println("s=$= =");
		ps.println("s=^= =");
		ps.println("");
		ps.println("s=\"= \'\' =g");
		ps.println("# possessive or close-single-quote");
		ps.println("s=\\([^\']\\)\' =\\1 \' =g");
		ps.println("# as in it\'s, I\'m, we\'d");
		ps.println("s=\'\\([sSmMdD]\\) = \'\\1 =g");
		ps.println("s=\'ll = \'ll =g");
		ps.println("s=\'re = \'re =g");
		ps.println("s=\'ve = \'ve =g");
		ps.println("s=n\'t = n\'t =g");
		ps.println("s=\'LL = \'LL =g");
		ps.println("s=\'RE = \'RE =g");
		ps.println("s=\'VE = \'VE =g");
		ps.println("s=N\'T = N\'T =g");
		ps.println("");
		ps.println("s= \\([Cc]\\)annot = \\1an not =g");
		ps.println("s= \\([Dd]\\)\'ye = \\1\' ye =g");
		ps.println("s= \\([Gg]\\)imme = \\1im me =g");
		ps.println("s= \\([Gg]\\)onna = \\1on na =g");
		ps.println("s= \\([Gg]\\)otta = \\1ot ta =g");
		ps.println("s= \\([Ll]\\)emme = \\1em me =g");
		ps.println("s= \\([Mm]\\)ore\'n = \\1ore \'n =g");
		ps.println("s= \'\\([Tt]\\)is = \'\\1 is =g");
		ps.println("s= \'\\([Tt]\\)was = \'\\1 was =g");
		ps.println("s= \\([Ww]\\)anna = \\1an na =g");
		ps.println("# s= \\([Ww]\\)haddya = \\1ha dd ya =g");
		ps.println("# s= \\([Ww]\\)hatcha = \\1ha t cha =g");
		ps.println("");
		ps.println("# clean out extra spaces");
		ps.println("s=  *= =g");
		ps.println("s=^ *==g");
		ps.println("");
		
		ps.close();
		convertionFile = converterFile;
	}
	
	protected void start() throws LiveIOProgramExecutionException, PennTreeBankConverterException
	{
		if (execution!=null) ;
		else
		{
			try
			{
				createConvertionScript();
				LinkedList<String> programAndArguments = new LinkedList<String>();
				programAndArguments.add(CONVERTER_PROGRAM);
				programAndArguments.addAll(Utils.arrayToCollection(CONVERTER_PARAMS, new ArrayList<String>(CONVERTER_PARAMS.length)));
				programAndArguments.add(CONVERTER_SCRIPT_NAME_SPECIFIER_PARAM);
				programAndArguments.add(convertionFile.getAbsolutePath());
				
				execution = new LiveIOProgramExecution(programAndArguments);
				execution.start();
			}
			catch(IOException e)
			{
				throw new PennTreeBankConverterException("script creation failure. See nested exception",e);
			}
		}
	}
	
	
	
	
	protected void finalize() throws Throwable
	{
		try{
			end();
		}finally{
			super.finalize();
		}
	}


	
	
	protected File convertionFile = null;
	protected LiveIOProgramExecution execution = null;
	protected List<String> sentences = null;
	protected List<String> convertedSentences = null;
	
	private boolean finalizationDone = false;
	

}
