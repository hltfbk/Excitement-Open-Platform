package eu.excitementproject.eop.lap.biu.en.parser.candc.graph;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import eu.excitementproject.eop.common.datastructures.dgraph.DirectedGraph;
import eu.excitementproject.eop.common.datastructures.dgraph.DirectedGraphException;
import eu.excitementproject.eop.common.datastructures.dgraph.view.DirectedGraphToDot;
import eu.excitementproject.eop.common.utilities.ExceptionUtil;
import eu.excitementproject.eop.common.utilities.LiveIOProgramExecution;
import eu.excitementproject.eop.common.utilities.LiveIOProgramExecutionException;
import eu.excitementproject.eop.common.utilities.OS;
import eu.excitementproject.eop.common.utilities.TimeOutLiveIOProgramExecutionException;
import eu.excitementproject.eop.lap.biu.en.tokenizer.TextToPennTreeBankConverter;

@Deprecated
public class CandCDemo
{
	
	public static final String EXIT_MARKER = "exit";
	public static final int TIME_OUT_1ST_TIME = 10*60*1000; // 10 minutes.
	public static final int TIME_OUT = 15*1000; // 15 seconds
	public static final int MAX_ATTEMPTS = 1;
	
	public CandCDemo(File dir, String programName, String modelsDirName)
	{
		this.dir = dir;
		this.programName = programName;
		this.modelsDir = modelsDirName;
	}
	
	public void start() throws LiveIOProgramExecutionException
	{
		if (OS.isWindows())
		{
			// C&C's craziness on Windows...
			this.modelsDir = this.modelsDir.replaceAll("\\\\", "\\\\\\\\");
		}
		System.out.println("["+this.getClass().getName()+"] models directory = " +this.modelsDir);
		
		
		LinkedList<String> programAndArgs = new LinkedList<String>();
		programAndArgs.add(this.programName);
		programAndArgs.add("--models");
		programAndArgs.add(this.modelsDir);
		execution = new LiveIOProgramExecution(programAndArgs);
		execution.start();
		
		converter = new TextToPennTreeBankConverter();
		
		
		
	}
	
	protected void parseLine(Integer index,String line) throws IOException, LiveIOProgramExecutionException, DirectedGraphException, CandCMalformedOutputException, InterruptedException
	{
		LinkedList<String> output = null;
		boolean stop = false;
		int attempts = 0;
		while(!stop)
		{
			try
			{
				output = new LinkedList<String>();
				System.out.println("Parsing: "+line);
				execution.putLine(line);

				String outputLine = null;
				do
				{
					int timeOut = cAndCFirstTimeReadLine?TIME_OUT_1ST_TIME:TIME_OUT;
					cAndCFirstTimeReadLine = false;
					outputLine = execution.getLine(timeOut);
					output.add(outputLine);
				}while (!outputLine.startsWith(CandCOutputToGraph.POS_TAGGER_LINE_BEGIN_MARKER));
				stop = true;
			}
			catch(TimeOutLiveIOProgramExecutionException e)
			{
				attempts++;
				if (attempts>=MAX_ATTEMPTS)
					stop = true;
				System.out.println("Time out!");
				if (stop)
					throw e;
				execution.revive();
				System.out.println("Trying again");
			}
		}

		CandCOutputToGraph outputToGraph = new CandCOutputToGraph(output);
		outputToGraph.generateGraph();
		DirectedGraph<CCNode, CCEdgeInfo> graph = outputToGraph.getGraph();

		File dotFile = new File(dir,index.toString()+".dot");
		PrintStream dotPrintStream = new PrintStream(dotFile);
		DirectedGraphToDot<CCNode, CCEdgeInfo> toDot =
			new DirectedGraphToDot<CCNode, CCEdgeInfo>(graph,new CCGraphStringRepresentation(),dotPrintStream);
		toDot.printDot();
		dotPrintStream.close();
		Process p = Runtime.getRuntime().exec(new String[]{"dot", "-Tjpg", "-O",dotFile.getPath()});
		p.waitFor();
		//try{dotFile.delete();}catch(Exception e){}
		System.out.println("done: "+index+".");
		
	}


	public void readAndParse() throws IOException, LiveIOProgramExecutionException, DirectedGraphException, CandCMalformedOutputException, TextToPennTreeBankConverter.PennTreeBankConverterException, InterruptedException
	{
		boolean liveIoProgramExecutionExceptionThrown = false;
		try
		{
			Integer index = new Integer(1);
			BufferedReader readerIn = new BufferedReader(new InputStreamReader(System.in));

			String line = readerIn.readLine();
			while (line!=null)
			{
				if (line.equalsIgnoreCase(EXIT_MARKER))
					break;
				
				List<String> asList = new ArrayList<String>(1);
				asList.add(line);
				
				converter.setSentences(asList);
				converter.convert();
				asList = converter.getPennTreebankSentences();
				if (asList==null)
					throw new RuntimeException("converter returned null");
				if (asList.size()==0)
					throw new RuntimeException("converter returned empty list");
				
				line = asList.get(0);
				if (line.trim().length()>0)
				{
					parseLine(index,line);
					index++;
				}
				line = readerIn.readLine();
			}
		}
		catch(LiveIOProgramExecutionException e)
		{
			liveIoProgramExecutionExceptionThrown = true;
			throw e;
		}
		finally
		{
			System.out.println("finally");
			try{execution.endIO();}catch(Exception e){}
			if (liveIoProgramExecutionExceptionThrown)
				execution.destroyProcess();
			try{converter.end();}catch(Exception e){}
		}
	}
	
	
	
	
	
	protected File dir = null;
	protected String programName = null;
	protected String modelsDir = null;
	
	protected LiveIOProgramExecution execution = null;
	protected TextToPennTreeBankConverter converter = null;
	protected boolean cAndCFirstTimeReadLine = true;
	

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			if (args.length<3)
				throw new Exception("args. 1st argument should be a directory to put output images. 2nd - program. 3rd - models");
			
			CandCDemo demo = new CandCDemo(new File(args[0]),args[1],args[2]);
			demo.start();
			demo.readAndParse();
			
		}
		catch(Exception e)
		{
			System.out.println("=================================");
			System.out.println("An exception occured!");
			System.out.println(ExceptionUtil.getMessages(e));
			System.out.println("========== stack trace ==========");
			e.printStackTrace();
		}
	}

}
