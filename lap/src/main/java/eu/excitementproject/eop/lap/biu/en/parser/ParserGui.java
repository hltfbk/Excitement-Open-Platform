package eu.excitementproject.eop.lap.biu.en.parser;


import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.IdLemmaPosRelNodeAndEdgeString;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.NodeAndEdgeString;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.NodeString;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.TreeDotFileGenerator;
import eu.excitementproject.eop.common.utilities.EnvironmentVerifier;
import eu.excitementproject.eop.common.utilities.ExceptionUtil;
import eu.excitementproject.eop.common.utilities.OS;
import eu.excitementproject.eop.common.utilities.ProgramExecution;
import eu.excitementproject.eop.lap.biu.en.parser.easyfirst.EasyFirstParser;
import eu.excitementproject.eop.lap.biu.en.parser.minipar.MiniparClientParser;
import eu.excitementproject.eop.lap.biu.en.parser.minipar.MiniparParser;
import eu.excitementproject.eop.lap.biu.en.parser.minipar.MiniparServer;

/**
 * A GUI application to view parse tree.
 * <P>
 * Currently supports Minipar and EasyFirst. The first command line argument
 * should be either "minipar" or "easyfirst".<BR>
 * The second command line argument:
 * <UL>
 * <LI>For Minipar: should be either minipar data dir or minipar server name</LI>
 * <LI>For EasyFirst: should be pos tagger module file name</LI>
 * </UL>
 * <P>
 * To change the string representation (the string displayed inside to node),
 * change the static variable {@link #nodeStringClass} to another class.
 * <P>
 * To exit - push the exit button.
 * <P>
 * Note: There is a known (not serious) GUI error in JDK 1.5,
 * under Ubuntu 9.10 64 bit.
 * 
 * @author Asher Stern
 *
 */
public class ParserGui extends JFrame implements ActionListener
{
	
	// CONSTANTS
	private static final long serialVersionUID = 1L;
	private static final int GRAPH_COMPONENT_WIDTH = 300;
	private static final int GRAPH_COMPONENT_HEIGHT = 300;
	private final static Font textFont = new Font("arial",0,25);
	private final static String OK_BUTTON_TEXT = "OK";
	private final static String SAVE_BUTTON_TEXT = "Save Image";
	private final static String CLEANUP_BUTTON_TEXT = "exit";
	private final static String DEFAULT_SENTENCE ="===== Please enter a sentence here. =====";
	private final static String GRAPH_VIZ_PROGRAM_NAME ="dot";
	private final static String GRAPH_IMAGE_FORMAT = "jpg";
	private final static String DOT_FILE_EXTENSION = ".dot";
	//private final static Class<? extends NodeString> nodeStringClass = WordOnlyNodeString.class;
	//private final static Class<? extends NodeString> nodeStringClass = SimpleNodeString.class;
	//private final static Class<? extends NodeString> nodeStringClass = WordAndPosNodeString.class;
	private final static Class<? extends NodeString<Info>> nodeStringClass = null;
	//private final static Class<? extends NodeAndEdgeString> nodeAndEdgeStringClass = LemmaPosRelNodeAndEdgeString.class;
	private final static Class<? extends NodeAndEdgeString<Info>> nodeAndEdgeStringClass = IdLemmaPosRelNodeAndEdgeString.class;
	//private final static Class<? extends NodeAndEdgeString<Info>> nodeAndEdgeStringClass = LemmaCanonicalNodeAndEdgeString.class;
	//private final static Class<? extends NodeAndEdgeString<Info>> nodeAndEdgeStringClass = NoAntLemmaPosRelNodeAndEdgeString.NoAntLemmaCanonicalPosRelNodeAndEdgeString.class;
	//private final static Class<? extends NodeAndEdgeString<Info>> nodeAndEdgeStringClass = IdLemmaPosCanonicalRelNodeAndEdgeString.class;

	///////////////// nested classes //////////////////////
	private class GraphComponent extends JComponent
	{
		private static final long serialVersionUID = 1L;
		
		private ParserGui parserGui = null;
		public GraphComponent(ParserGui theParserGui)
		{
			super();
			this.parserGui = theParserGui; 
			setPreferredSize(new Dimension(GRAPH_COMPONENT_WIDTH,GRAPH_COMPONENT_HEIGHT));
		}
		@Override
		public void paint(Graphics g)
		{
			try
			{
				if (parserGui.image == null) System.out.println("null");
				else
					g.drawImage(parserGui.image,0,0,null);
				
			}
			catch(Exception e)
			{e.printStackTrace();}
			
		}
		
	}
	
	private class ParserGuiWindowListener implements WindowListener
	{
		public void windowClosing(WindowEvent e)
		{
			parser.cleanUp();
		}

		public void windowActivated(WindowEvent e) {}
		public void windowClosed(WindowEvent e) {}
		public void windowDeactivated(WindowEvent e){}
		public void windowDeiconified(WindowEvent e) {}
		public void windowIconified(WindowEvent e) {}
		public void windowOpened(WindowEvent e) {}
	}
	
	private static class ParserGuiRunnable implements Runnable
	{
		private BasicParser parser;
		public ParserGuiRunnable(BasicParser parser)
		{
			super();
			this.parser = parser;
		}
		
		public void run()
		{
			createAndShowGUI(this.parser);
		}
	}
	//////////////////////////////////////////////////////////


	

	// private fields:
	private TextField sentenceField = new TextField(DEFAULT_SENTENCE);
	private Button buttonOk = new Button(OK_BUTTON_TEXT);
	private Button buttonSave = new Button(SAVE_BUTTON_TEXT);
	private BufferedImage image = null;
	private BasicParser parser;
	private GraphComponent graphComponent;
	private JScrollPane graphPanel;
	
	
	
		
	
  
	// private methods
    private void addComponents()
    {
    	JPanel contentPanel = new JPanel(new BorderLayout());
    	
    	this.setContentPane(contentPanel);
    	this.graphComponent = new GraphComponent(this);
    	graphPanel = new JScrollPane(graphComponent,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
    	JPanel mainPanel = new JPanel();
    	
    	sentenceField.setFont(textFont);
    	sentenceField.addKeyListener(new KeyListener()
		{
			public void keyTyped(KeyEvent e){}
			public void keyReleased(KeyEvent e){}
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode()==KeyEvent.VK_ENTER)
				{
					commandParse();
				}
			}
		});
    	
    	JPanel sentencePane = new JPanel();
    	sentencePane.add(this.sentenceField);
    	
    	
    	
    	JPanel componentsPanel = new JPanel(new BorderLayout());
    	componentsPanel.add(sentencePane,BorderLayout.CENTER);
    	JPanel buttonsPanel = new JPanel(new FlowLayout());
    	buttonOk.addActionListener(this);
    	buttonsPanel.add(this.buttonOk);
    	buttonSave.addActionListener(this);
    	buttonsPanel.add(this.buttonSave);
    	Button cleanUpButton = new Button(CLEANUP_BUTTON_TEXT);
    	cleanUpButton.addActionListener(this);
    	buttonsPanel.add(cleanUpButton);
    	componentsPanel.add(buttonsPanel,BorderLayout.LINE_END);
    	mainPanel.add(componentsPanel);
    	
    	this.getContentPane().add(graphPanel,BorderLayout.CENTER);
    	this.getContentPane().add(mainPanel,BorderLayout.PAGE_END);
    	
    	
    	
    }



	private static void createAndShowGUI(BasicParser parser)
	{

        //Create and set up the window.
		final ParserGui frame = new ParserGui("Parser GUI - " + parser.getClass().getSimpleName(),parser);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(frame.new ParserGuiWindowListener());        

        //Create and set up the content pane.
        frame.addComponents();
        
        
        

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

	

	
	
    private ParserGui(String title,BasicParser parser) throws HeadlessException
    {
		super(title);
		this.parser = parser;
	}
    
    private void commandParse()
    {
		try
		{
			this.parser.setSentence(this.sentenceField.getText());
			parser.parse();
			BasicNode root = parser.getParseTree();
			UUID uuid = UUID.randomUUID();
			File dotFile = new File(System.getProperty("java.io.tmpdir")+File.separator+"parsergui"+uuid.toString()+DOT_FILE_EXTENSION);
			TreeDotFileGenerator<Info> tdfg = null;
			if (nodeStringClass!=null)
				tdfg = new TreeDotFileGenerator<Info>(nodeStringClass.newInstance(),root,this.sentenceField.getText(),dotFile);
			else
				tdfg = new TreeDotFileGenerator<Info>(nodeAndEdgeStringClass.newInstance(),root,this.sentenceField.getText(),dotFile);
			tdfg.generate();
			
			String[] commandArray = new String[]{OS.programName(GRAPH_VIZ_PROGRAM_NAME),"-T"+GRAPH_IMAGE_FORMAT,"-O",dotFile.toString()};
			LinkedList<String> commandList = new LinkedList<String>();
			for (String s : commandArray)
			{
				commandList.add(s);
			}
			
			ProgramExecution pe = new ProgramExecution(commandList,null);
			pe.execute();
			File imageFile = new File(dotFile.toString()+"."+GRAPH_IMAGE_FORMAT);
			if (imageFile.exists())
			{
				this.image = ImageIO.read(imageFile);
				graphComponent.setPreferredSize(new Dimension(this.image.getWidth(),this.image.getHeight()));
			}
			if ((dotFile!=null)&&(dotFile.exists()))dotFile.delete();
			if ((imageFile!=null)&&(imageFile.exists()))imageFile.delete();
			this.repaint();
			graphPanel.setViewportView(graphComponent);
			
			
			
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
    	
    }
    
    private void saveImage() {
    	try {
	    	if (image != null) {
		    	JFileChooser fc = new JFileChooser();
		    	int returnVal = fc.showSaveDialog(this);
		    	if (returnVal == JFileChooser.APPROVE_OPTION) {
		    		File file = fc.getSelectedFile();
		    		ImageIO.write(image, GRAPH_IMAGE_FORMAT, file);
		    	}
	    	}
	    	else {
	    		JOptionPane.showMessageDialog(this, "No image to save!", "Error", JOptionPane.ERROR_MESSAGE);
	    	}
    	}
    	catch (Exception e) {
    		JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    	}
    }
    
    ///////////////// PUBLIC PART ///////////////////////////
    
	public void actionPerformed(ActionEvent e)
	{
		if (e.getActionCommand().equals(OK_BUTTON_TEXT))
		{
			commandParse();
		}
		else if (e.getActionCommand().equals(SAVE_BUTTON_TEXT))
		{
			saveImage();
		}
		else if (e.getActionCommand().equals(CLEANUP_BUTTON_TEXT))
		{
			parser.cleanUp();
			System.exit(0);
		}
		
	}

	/**
	 * @param args Arguments are as follows:
	 * For Minipar:
	 * <OL>
	 * <LI>minipar (just the string "minipar" as first argument)</LI>
	 * <LI>The minipar's data-dir, or a server name on which {@link MiniparServer} is running.
	 * Note the minipar's data-dir is the "data" directory of minipar, and this can be used only
	 * on 32-bit platforms. Note also that using minipar data-dir - i.e., running minipar
	 * in-process, requires the the minipar "dll" (Windows) or "so" (Unix) shared library
	 * should be in the java.library.path (the path on Windows, or the library-path on Unix).
	 * </LI>
	 * </OL>
	 * For Easy-First
	 * <OL>
	 * <LI>easyfirst (just the string "easyfirst")</LI>
	 * <LI>The pos-tagger model - should be $JARS/stanford-postagger-full-2008-09-28/models/bidirectional-wsj-0-18.tagger</LI>
	 * <LI>If minipar is not on local-host and port 8080 - then the third arguement should
	 * be the host</LI>
	 * <LI>and the fourth argument should be the port</LI>
	 * </OL>
	 */
	public static void main(String[] args)
	{
		try
		{
			if (args.length<1)
				throw new Exception("Arguments error. Enter first argument: minipar or easyfirst.\n"+
						"Enter second argument: for minipar -  minipar data dir or minipar server host name / ip"+
						"for EasyFirst: the stanford posTagger module file name (e.g. bidirectional-wsj-0-18.tagger)");
			BasicParser parser = null;

			if (args[0].equalsIgnoreCase("minipar"))
			{
				if (args.length<2)
					throw new Exception("Arguments error. "+
							"Enter second argument: minipar data dir or minipar server host name / ip");
				String miniparDataOrServer = args[1];
				File optionalMiniparDataDir = new File(miniparDataOrServer);
				if (optionalMiniparDataDir.exists()&&optionalMiniparDataDir.isDirectory())
					parser = new MiniparParser(args[1]);
				else
					parser = new MiniparClientParser(args[1]);
			}
			else
			{
				if (args.length<2)
					throw new Exception("Arguments error. "+
							"Enter second argument: the stanford posTagger module file name (e.g. bidirectional-wsj-0-18.tagger)");
				
				String stanfordPosTaggerModuleFileName = args[1];
				if (args.length<3)
				{
					parser = new EasyFirstParser(stanfordPosTaggerModuleFileName);
				}
				else {
					String host = args[2];
					int port = Integer.valueOf(args[3]);
					parser = new EasyFirstParser(host, port, stanfordPosTaggerModuleFileName);
				}
			}
			
			EnvironmentVerifier verifier = new EnvironmentVerifier();
			Set<String> items = new LinkedHashSet<String>();
			items.add(GRAPH_VIZ_PROGRAM_NAME);
			verifier.setItemsToVerify(items);
			if (!verifier.verifyExecutables())
			{
				System.err.println("The following programs are missing:");
				for (String missing : verifier.getMissingItems())
				{
					System.err.println(missing);
				}
				System.err.println("Aborting...");
			}
			else
			{
				parser.init();
				ParserGuiRunnable runnable = new ParserGuiRunnable(parser);

				javax.swing.SwingUtilities.invokeLater(
						runnable
				);
			}
		}
		catch(Exception e)
		{
			ExceptionUtil.outputException(e, System.out);
		}



	}



}

