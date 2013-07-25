package eu.excitementproject.eop.lap.biu.en.parser.candc.graph;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import eu.excitementproject.eop.common.datastructures.dgraph.DirectedGraph;
import eu.excitementproject.eop.common.datastructures.dgraph.DirectedGraphException;
import eu.excitementproject.eop.common.datastructures.dgraph.view.DirectedGraphToDot;
import eu.excitementproject.eop.common.utilities.ExceptionUtil;
import eu.excitementproject.eop.common.utilities.LiveIOProgramExecution;
import eu.excitementproject.eop.common.utilities.LiveIOProgramExecutionException;
import eu.excitementproject.eop.common.utilities.OS;
import eu.excitementproject.eop.common.utilities.TimeOutLiveIOProgramExecutionException;
import eu.excitementproject.eop.lap.biu.en.tokenizer.TextToPennTreeBankConverter;



/**
 * 
 * @author Asher Stern
 *
 */
@SuppressWarnings({ "serial"})
@Deprecated
public class CandCDemoGui extends JFrame implements ActionListener, WindowListener
{
	public static class CandCDemoGuiException extends Exception{
		public CandCDemoGuiException(String s){super(s);}
	}
	
	
	// CONSTANTS
	public static final int TIME_OUT_1ST_TIME = 10*60*1000; // 10 minutes.
	public static final int TIME_OUT = 5*1000; // 5 seconds
	public static final String TITLE = "C&C demo";
	public static final String MODELS_ARG_SPECIFIER = "--models";
	
	public static final String C_AND_C_CHARSET = "UTF-8";
	
	// PRIVATE CONSTANTS
	private static final String RUN_ACTION_COMMAND = "run";
	
	// PRIVATE FIELDS:
	// GUI WIDGETS
	private JButton buttonRun;
	private JTextField textSentence;
	
	// PARSER VARIABLES
	private Integer index = new Integer(1);
	private String outputDir;
	private String exec;
	private String modelsDir;
	private LiveIOProgramExecution execution = null;
	private eu.excitementproject.eop.lap.biu.en.tokenizer.TextToPennTreeBankConverter converter = null;
	private boolean cAndCFirstTimeReadLine = true;
	
	
	
	/**
	 * Constructor. After invoking the constructor, call {@link #init()} method.
	 * (It is done by the {@link #main(String[])} method).
	 * 
	 * @param outputDir where the output files (Jpeg files) will be put
	 * @param exec C&C execution (program path name)
	 * @param modelsDir C&C models directory
	 */
	public CandCDemoGui(String outputDir,String exec, String modelsDir)
	{
		this.outputDir = outputDir;
		this.exec = exec;
		this.modelsDir = modelsDir;
	}
	
	/**
	 * Initialization. First calls {@link #start()}, then initializes the GUI.
	 * @throws LiveIOProgramExecutionException
	 */
	private void init() throws LiveIOProgramExecutionException
	{
		this.start();
		this.setTitle(TITLE);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
		textSentence = new JTextField("enter your sentence here");
		
		
		
		buttonRun = new JButton(index.toString());
		buttonRun.setActionCommand("run");
		buttonRun.addActionListener(this);
		
		mainPanel.add(textSentence);
		mainPanel.add(buttonRun);
		
		
		
		this.setContentPane(mainPanel);
		
		this.addWindowListener(this);
		
		this.pack();
		this.setVisible(true);
	}
	
	
	/**
	 * Parser initializations.
	 * @throws LiveIOProgramExecutionException
	 */
	private void start() throws LiveIOProgramExecutionException
	{
		if (OS.isWindows())
		{
			// C&C's craziness on Windows...
			this.modelsDir = this.modelsDir.replaceAll("\\\\", "\\\\\\\\");
		}
		System.out.println("["+this.getClass().getName()+"] models directory = " +this.modelsDir);
		
		
		LinkedList<String> programAndArgs = new LinkedList<String>();
		programAndArgs.add(this.exec);
		programAndArgs.add(MODELS_ARG_SPECIFIER);
		programAndArgs.add(this.modelsDir);
		//execution = new LiveIOProgramExecution(programAndArgs,Charset.forName(C_AND_C_CHARSET),true);
		execution = new LiveIOProgramExecution(programAndArgs,java.nio.charset.Charset.forName(C_AND_C_CHARSET),false,false,true);
		//execution = new LiveIOProgramExecution(programAndArgs,null,true,false,true);
		//execution = new LiveIOProgramExecution(programAndArgs);
		execution.start();
		
		converter = new eu.excitementproject.eop.lap.biu.en.tokenizer.TextToPennTreeBankConverter();
	}
	
	/**
	 * Parses the line "<code>line</code>".
	 * The output will be put as a Jpeg file in the output directory specified by the
	 * {@link #main(String[])} method.
	 * 
	 * @param line
	 * @throws IOException
	 * @throws LiveIOProgramExecutionException
	 * @throws DirectedGraphException
	 * @throws CandCMalformedOutputException
	 * @throws TextToPennTreeBankConverter.PennTreeBankConverterException
	 * @throws InterruptedException
	 */
	private void readAndParse(String line) throws IOException, LiveIOProgramExecutionException, DirectedGraphException, CandCMalformedOutputException, eu.excitementproject.eop.lap.biu.en.tokenizer.TextToPennTreeBankConverter.PennTreeBankConverterException, InterruptedException, CandCDemoGuiException
	{
		String originalSentence = line;
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
			System.out.println("parsing: "+line);
			parseLine(line,originalSentence);
			index++;
		}


	}
	
	
	
	
	
	/**
	 * Continues the {@link #readAndParse(String)} method.
	 * @param line
	 * @throws IOException
	 * @throws LiveIOProgramExecutionException
	 * @throws DirectedGraphException
	 * @throws CandCMalformedOutputException
	 * @throws InterruptedException
	 */
	private void parseLine(String line,String originalSentence) throws IOException, LiveIOProgramExecutionException, DirectedGraphException, CandCMalformedOutputException, InterruptedException, CandCDemoGuiException
	{
		LinkedList<String> output = null;
		try
		{
			output = new LinkedList<String>();
			execution.putLine(line);

			String outputLine = null;
			do
			{
				int timeOut = cAndCFirstTimeReadLine?TIME_OUT_1ST_TIME:TIME_OUT;
				cAndCFirstTimeReadLine = false;
				outputLine = execution.getLine(timeOut);
				//System.out.println(outputLine);
				if (null==outputLine)
					throw new CandCDemoGuiException("null line returned.");
				output.add(outputLine);
				//System.out.println(outputLine);
			}while (!outputLine.startsWith(CandCOutputToGraph.POS_TAGGER_LINE_BEGIN_MARKER));
		}
		catch(TimeOutLiveIOProgramExecutionException e)
		{
			System.out.println("Time out!");
			throw e;
		}


		CandCOutputToGraph outputToGraph = new CandCOutputToGraph(output);
		outputToGraph.generateGraph();
		DirectedGraph<CCNode, CCEdgeInfo> graph = outputToGraph.getGraph();

		File dotFile = new File(outputDir,index.toString()+".dot");
		PrintStream dotPrintStream = new PrintStream(dotFile);
		DirectedGraphToDot<CCNode, CCEdgeInfo> toDot =
			new DirectedGraphToDot<CCNode, CCEdgeInfo>(graph,new CCGraphStringRepresentation(),dotPrintStream);
		toDot.setGraphLabel(originalSentence);
		toDot.printDot();
		dotPrintStream.close();
		Process p = Runtime.getRuntime().exec(new String[]{"dot", "-Tjpg", "-O",dotFile.getPath()});
		p.waitFor();
		//try{dotFile.delete();}catch(Exception e){}
		System.out.println("done: "+index+".");
		
	}
	

	/**
	 * @param args
	 * 1st argument = output directory.
	 * 2nd argument = C&C executable (path)
	 * 3rd argument = C&C models directory
	 * @return
	 */
	public static void main(String[] args)
	{
		try
		{
			if (args.length<3)
				throw new Exception("args. 1st argument should be a directory to put output images. 2nd - program. 3rd - models");

			CandCDemoGui gui = new CandCDemoGui(args[0],args[1],args[2]);
			gui.init();
			
			
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

	
	public void actionPerformed(ActionEvent e)
	{
		if (e.getActionCommand().equals(RUN_ACTION_COMMAND))
		{
			boolean liveIoProgramExecutionExceptionThrown = true;
			try
			{
				String sentence = this.textSentence.getText().trim();
				/*
				byte[] byteArray = sentence.getBytes(C_AND_C_CHARSET);
				sentence = new String(byteArray,C_AND_C_CHARSET);
				*/

				readAndParse(sentence);
				buttonRun.setText(index.toString());
			}
			catch(Exception ex)
			{
				if (ex instanceof LiveIOProgramExecutionException)
					liveIoProgramExecutionExceptionThrown = true;
				
				System.out.println("=================================");
				System.out.println("An exception occured!");
				System.out.println(ExceptionUtil.getMessages(ex));
				System.out.println("========== stack trace ==========");
				ex.printStackTrace();
				System.out.println("finalizing");
				try{execution.endIO();}catch(Exception x){}
				if (liveIoProgramExecutionExceptionThrown)
					execution.destroyProcess();
				try{converter.end();}catch(Exception x){}
				
				System.exit(1);
			}

		}
	}

	
	// WindowListener methods
	
	public void windowActivated(WindowEvent e) {}
	public void windowClosed(WindowEvent e){}
	public void windowDeactivated(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}

	public void windowClosing(WindowEvent e)
	{
		System.out.println("window closing");
		try{execution.endIO();}catch(Exception x){}
		try{converter.end();}catch(Exception x){}
		System.exit(0);
	}
}
