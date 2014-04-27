package eu.excitementproject.eop.biutee.rteflow.systems.gui;
import static javax.swing.SwingUtilities.invokeLater;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.JTextComponent;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.rteflow.macro.Feature;
import eu.excitementproject.eop.biutee.rteflow.systems.gui.SpellCheckerRegister.SpellCheckerRegisterResult;
import eu.excitementproject.eop.biutee.utilities.BiuteeConstants;
import eu.excitementproject.eop.biutee.utilities.LogInitializer;
import eu.excitementproject.eop.biutee.version.Version;
import eu.excitementproject.eop.common.utilities.ExceptionUtil;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * 
 * TO-DO (comment by Asher Stern): GUI code is not of high quality and
 * should be improved. Need to re-design, make it more modular,
 * adding documentation and improve code.
 * 
 * @author Asher Stern
 * @since May 24, 2011
 *
 */
public class VisualTracingTool
{
	/**
	 * 
	 */
	private static final String HTML_TABLE_TOOL_TIP = "Click on the help button for a legend of the table";
	/**
	 * 
	 */
	private static final String COMMAND_ZOOM_OUT = "zoomOut";
	/**
	 * 
	 */
	private static final String COMMAND_ZOOM_IN = "zoomIn";
	/**
	 * 
	 */
	public static final String TITLE_AUTOMATIC_MODE = "Automatic Mode";
	/**
	 * 
	 */
	public static final String TITLE_TEXT_TREES = "Text Trees";
	/**
	 * 
	 */
	public static final String TITLE_MANUAL_MODE_PANE = "Manual Mode";
	/**
	 * 
	 */
	public static final String TITLE_VIEW_TREE_MANUAL = "View Tree (Manual)";
	/**
	 * 
	 */
	public static final String TITLE_DISPLAY_SELECTED_TREE = "Display Selected Tree";
	/**
	 * 
	 */
	public static final String TITLE_DISPLAY_ONLY_LAST_GENERATED_TREES = "Display Only Last Generated Trees";
	/**
	 * 
	 */
	public static final String TITLE_T_H_PAIR = "T-H Pair";
	/**
	 * 
	 */
	public static final String COMMAND_REGULAR_SEARCH_HELP = "regularSearchHelp";
	/**
	 * 
	 */
	public static final String COMMAND_SELECTED_TREE_HELP = "selectedTreeHelp";
	/**
	 * 
	 */
	public static final String COMMAND_TREES_TABLE_HELP = "treesTableHelp";
	/**
	 * 
	 */
	private static final JLabel PADDING_BETWEEN_BUTTONS = new JLabel("  ");
	/**
	 * 
	 */
	public static final String COMMAND_TEXT_TREES_HELP = "textTreesHelp";
	/**
	 * 
	 */
	public static final String COMMAND_TH_PAIR_HELP = "thPairHelp";
	public static final String INITIAL_HYPO_TEXT = "Enter one hypothesis sentence here";
	public static final String INITIAL_TEXT_TEXT = "Enter one or more sentences of text here";
	public static final String COMMAND_REG_SEARCH_NEXT = "regSearchNext";
	public static final String COMMAND_REGULAR_SEARCH_FIRST = "regSearchFirst";
	public static final String COMMAND_SELECTED_TREE_FIRST = "selectedTreeFirst";
	public static final String COMMAND_SELECTED_TREE_LAST = "selectedTreeLast";
	public static final String COMMAND_REGUALR_SEARCH_PREVIOUS = "regSearchPrev";
	public static final String COMMAND_REG_SEARCH_LAST = "regSearchLast";
	public static final int GUI_FONT_SIZE = 20;
	public static final int HTML_FONT_SIZE = 15;
	public static final String BUTTON_GO_TEXT = "Pre-Process";
	
	private static final double LAST_OPERATION_COLUMN_WIDTH_RATIO=6.0; // 3 times wider than others
	public static final String IGNORE_TASK_NAME_STRING = "IGNORE";
	public static final String READY_STATUS_BAR_SYSTEM_STATE = "Ready";
	
	public static final String STATUS_BAR_CLASSIFIER_TYPE_ACCURACY_OPTIMIZED = "";
	public static final String STATUS_BAR_CLASSIFIER_TYPE_F1_OPTIMIZED = " (F1 optimized) ";
	
	public static void main(String[] args)
	{
		if (args.length<1)
		{
			System.out.println("Arguments! Should be configuration file name.");
		}
		else
		{
			final String[] argsFinal = args;
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					startGui(argsFinal);
				}
			});
		}
	}

	public VisualTracingTool(String configurationFileName)
	{
		super();
		this.configurationFileName = configurationFileName;
	}

	@SuppressWarnings("serial")
	public void createAndShowGUI() throws VisualTracingToolException, TeEngineMlException
	{
		sumUtilities = new GuiRteSumUtilities(this);
		pairsUtilities = new GuiRtePairsUtilities(this);
		mainFrame = new JFrame(Version.getVersion().toString());
		
		//mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		mainFrame.addWindowListener(this.actionsPerformer);
		createMenu();

		
		tabsPane = new JTabbedPane();
		
		
		JPanel textHypothesisAreasPanel = new JPanel();	//new   GridLayout(2, 1));
		textHypothesisAreasPanel.setLayout(new BoxLayout(textHypothesisAreasPanel, BoxLayout.PAGE_AXIS));
		
		this.textTextArea = new JTextArea(INITIAL_TEXT_TEXT);
		textTextArea.setLineWrap(true);
		textTextArea.setWrapStyleWord(true);
		textTextArea.setBorder(BorderFactory.createTitledBorder("Text"));
		textTextArea.addMouseListener(actionsPerformer);
		textTextArea.setToolTipText(INITIAL_TEXT_TEXT);
		JScrollPane textTextScrollPane = new JScrollPane(textTextArea);
		textTextScrollPane.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 6, Color.gray));	// border only on the right
		
		this.cookedTextLabel =   new JEditorPane();
		cookedTextLabel.setForeground(Color.GRAY);
		cookedTextLabel.setBorder(BorderFactory.createTitledBorder("Sentence Split Text"));
		cookedTextLabel.setEditable(false);
		cookedTextLabel.setContentType("text/html");	// prepares the label for html content
		cookedTextLabel.setToolTipText("This label shows the text after sentence splitting");
		JScrollPane textCookedTextScrollPane = new JScrollPane(cookedTextLabel);
		
		
		JPanel textTextAreaPanel = new JPanel(new GridLayout(1, 2));
		textTextAreaPanel.setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, Color.gray));
		textTextAreaPanel.add(textTextScrollPane);
		textTextAreaPanel.add(textCookedTextScrollPane);

		
		this.hypothesisTextArea = new JTextArea(INITIAL_HYPO_TEXT);
		hypothesisTextArea.setLineWrap(true);
		hypothesisTextArea.setWrapStyleWord(true);
		hypothesisTextArea.addMouseListener(actionsPerformer);
		hypothesisTextArea.setToolTipText(INITIAL_HYPO_TEXT);
		JScrollPane hyopthesisScrollPane = new JScrollPane(hypothesisTextArea);
		hyopthesisScrollPane.setBorder(BorderFactory.createTitledBorder("Hypothesis"));

		
		JPanel hypothesisTextAreaPanel = new JPanel(new GridLayout(1, 1));
		hypothesisTextAreaPanel.setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, Color.gray));
		hypothesisTextAreaPanel.add(hyopthesisScrollPane);
		hypothesisTextAreaPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10000));

		textHypothesisAreasPanel.add(textTextAreaPanel);
		JPanel pairPanel = new JPanel(new BorderLayout());
		textHypothesisAreasPanel.add(hypothesisTextAreaPanel,BorderLayout.WEST);
		pairPanel.add(textHypothesisAreasPanel,BorderLayout.CENTER);
		
		pairSplitPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, textTextAreaPanel, textHypothesisAreasPanel);
		pairSplitPanel.setResizeWeight(0.75);
		
		
		JPanel buttonGoPanel = new JPanel();
		
		comboBoxDatasetNames = new JComboBox<String>();
		comboBoxDatasetNames.setEnabled(datasetNamesAllow);
		
		quickAccessSpinner = new JSpinner();
		quickAccessAllow = false;
		quickAccessSpinner.setEnabled(quickAccessAllow);
		// TODO: hard-coded constant 3.
		quickAccessSpinner.setPreferredSize(new Dimension(
				((int)quickAccessSpinner.getPreferredSize().getWidth())*3,
				(int)quickAccessSpinner.getPreferredSize().getHeight()));
		quickAccessSpinner.addChangeListener(pairsUtilities);
		buttonGoPanel.add(quickAccessSpinner);
		
		buttonGo = new JButton();
		buttonGo.setText(BUTTON_GO_TEXT);
		buttonGo.setActionCommand("thPairGo");
		buttonGo.addActionListener(actionsPerformer);

		buttonGoPanel.add(new JLabel("  Dataset Name: "));
		buttonGoPanel.add(comboBoxDatasetNames);
		buttonGoPanel.add(new JLabel("  Task Name: "));
		createComboBoxTaskNames();
		buttonGoPanel.add(comboBoxTaskNames);
		buttonGoPanel.add(PADDING_BETWEEN_BUTTONS );	// add some padding between the buttons
		buttonGoPanel.add(buttonGo);
		buttonGoPanel.add(PADDING_BETWEEN_BUTTONS );	// add some padding between the buttons
		buttonGoPanel.add(createHelpButton(COMMAND_TH_PAIR_HELP));
		
		textHypothesisAreasPanel.add(buttonGoPanel,BorderLayout.SOUTH);
		JPanel pairMainPanel = new JPanel(new BorderLayout());
		pairMainPanel.setName(TITLE_T_H_PAIR);
		pairMainPanel.add(pairSplitPanel, BorderLayout.CENTER);
		pairMainPanel.add(buttonGoPanel, BorderLayout.SOUTH);
		tabsPane.add(pairMainPanel);
		
		JPanel textTreesPanel = new JPanel(new BorderLayout());
		textTreesPanel.setName(TITLE_TEXT_TREES);
		JPanel buttonTextTreesPanel = new JPanel();
		
		buttonShowCoref = new JButton("Show coreference");
		buttonShowCoref.setActionCommand("show_coref");
		buttonShowCoref.addActionListener(this.actionsPerformer);
		buttonTextTreesPanel.add(buttonShowCoref);

		buttonShowSurrounding = new JButton("Show surrounding");
		buttonShowSurrounding.setActionCommand("show_surrounding");
		buttonShowSurrounding.addActionListener(this.actionsPerformer);
		buttonTextTreesPanel.add(buttonShowSurrounding);

		origTreesComboBox = new JComboBox<String>();
		origTreesComboBox.addActionListener(this.actionsPerformer);
		buttonTextTreesPanel.add(origTreesComboBox);
		
		buttonTextTreesPanel.add(PADDING_BETWEEN_BUTTONS);
		buttonTextTreesPanel.add(createHelpButton(COMMAND_TEXT_TREES_HELP));
		
		textTreesPanel.add(buttonTextTreesPanel,BorderLayout.PAGE_END);
		textImagePane = createNewImagePane();	//new JScrollPane(textTreeComponent,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		textImagePane.setToolTipText("Use the combo box below to switch between the text trees");
		textTreesPanel.add(textImagePane ,BorderLayout.CENTER);
		
		

		
		
		tabsPane.add(textTreesPanel);
		
		
		JPanel hypoTreePanel = new JPanel(new BorderLayout());
		hypoTreePanel.setName("Hypothesis Tree");
		JPanel buttonHypoTreePanel = new JPanel();
		
		hypothesisImagePane = createNewImagePane();	// new JScrollPane(hypothesisTreeComponent,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		hypoTreePanel.add(buttonHypoTreePanel,BorderLayout.PAGE_END);
		hypoTreePanel.add(hypothesisImagePane ,BorderLayout.CENTER);
		tabsPane.add(hypoTreePanel);
		
		existingTreesTable = new JTable(null, ColumnNames.printableColumnNames())  //COLUMN_NAMES)
		{
			@Override
			public void paint(Graphics g)
			{
				super.paint(g);
				setRowHeight(g.getFontMetrics(existingTreesTable.getFont()).getHeight());
			}
		};
		existingTreesTable.getTableHeader().setReorderingAllowed(false);		// cannot reorder columns
		// Add the custom header renderer
		CustomHeaderRenderer.setHeaderRendererToJTable(existingTreesTable);
			
		existingTreesTable.addMouseListener(this.actionsPerformer);
		JScrollPane tablePane = new JScrollPane(existingTreesTable);
		JPanel treesTablePanel = new JPanel(new BorderLayout());
		treesTablePanel.setName(TITLE_MANUAL_MODE_PANE);
		treesTablePanel.add(tablePane,BorderLayout.CENTER);
		onlyLastGeneratedCheckBox = new JCheckBox(TITLE_DISPLAY_ONLY_LAST_GENERATED_TREES);
		onlyLastGeneratedCheckBox.addItemListener(actionsPerformer);
		buttonDisplaySelectedTree = new JButton(TITLE_DISPLAY_SELECTED_TREE);
		buttonDisplaySelectedTree.setActionCommand("displaySelectedTree");
		buttonDisplaySelectedTree.addActionListener(actionsPerformer);
		JPanel treesTableButtonsPanel = new JPanel();
		treesTableButtonsPanel.add(onlyLastGeneratedCheckBox);
		treesTableButtonsPanel.add(PADDING_BETWEEN_BUTTONS);
		treesTableButtonsPanel.add(buttonDisplaySelectedTree);
		treesTableButtonsPanel.add(PADDING_BETWEEN_BUTTONS);
		treesTableButtonsPanel.add(PADDING_BETWEEN_BUTTONS);
		treesTableButtonsPanel.add( createHelpButton(COMMAND_TREES_TABLE_HELP));
		treesTablePanel.add(treesTableButtonsPanel,BorderLayout.SOUTH);
		tabsPane.add(treesTablePanel);
		
		//
		// View Tree (Manual) Pane
		//
		
		selectedTreeImagePane = createNewImagePane();	// new JScrollPane(selectedTreeComponent,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		JPanel panelContainingScrollPaneForSelectedTreeText = new JPanel(new BorderLayout());
		selectedTreeTextArea = new JEditorPane();
		selectedTreeTextArea.setEditable(false);
		selectedTreeTextArea.setToolTipText(HTML_TABLE_TOOL_TIP);
		JScrollPane scrollPaneForSelectedTreeTextArea = new JScrollPane(selectedTreeTextArea);
		scrollPaneForSelectedTreeTextArea.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		panelContainingScrollPaneForSelectedTreeText.add(scrollPaneForSelectedTreeTextArea, BorderLayout.CENTER);
		
		// buttons
		selectedTreeButtonFirst = new JButton("<<");
		selectedTreeButtonFirst.setActionCommand(COMMAND_SELECTED_TREE_FIRST);
		selectedTreeButtonFirst.addActionListener(actionsPerformer);
		selectedTreeButtonPrev = new JButton("<");
		selectedTreeButtonPrev.setActionCommand("selectedTreePrev");
		selectedTreeButtonPrev.addActionListener(actionsPerformer);
		selectedTreeButtonNext = new JButton(">");
		selectedTreeButtonNext.setActionCommand("selectedTreeNext");
		selectedTreeButtonNext.addActionListener(actionsPerformer);
		selectedTreeButtonLast = new JButton(">>");
		selectedTreeButtonLast.setActionCommand(COMMAND_SELECTED_TREE_LAST);
		selectedTreeButtonLast.addActionListener(actionsPerformer);

		JPanel selectedTreeImageButtonsPanel = new JPanel();
		selectedTreeImageButtonsPanel.add(selectedTreeButtonFirst);
		selectedTreeImageButtonsPanel.add(selectedTreeButtonPrev);
		selectedTreeImageButtonsPanel.add(selectedTreeButtonNext);
		selectedTreeImageButtonsPanel.add(selectedTreeButtonLast);
		selectedTreeImageButtonsPanel.add(PADDING_BETWEEN_BUTTONS);
		selectedTreeImageButtonsPanel.add(PADDING_BETWEEN_BUTTONS);
		selectedTreeImageButtonsPanel.add(createHelpButton(COMMAND_SELECTED_TREE_HELP));

		panelContainingScrollPaneForSelectedTreeText.add(selectedTreeImageButtonsPanel,BorderLayout.SOUTH);
		currentTreeInformationPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, selectedTreeImagePane, panelContainingScrollPaneForSelectedTreeText);
		currentTreeInformationPanel.setResizeWeight(0.5);
		currentTreeInformationPanel.setName(TITLE_VIEW_TREE_MANUAL);
		tabsPane.add(currentTreeInformationPanel);

		//
		//	Regular Search Pane
		//
		regularSearchImagePane = createNewImagePane();	// new JScrollPane(regularSearchTreeComponent,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		JPanel regularSearchBottomPanel = new JPanel(new BorderLayout());
		regularSearchProgressBar = new JProgressBar();
		regularSearchProgressBar.setFont(new Font(null, Font.BOLD, 16));
		regularSearchProgressBar.setForeground(Color.black);
		originalregularSearchProgressBarColor = regularSearchProgressBar.getForeground();
		progressIndicatorInRegularSearch = new JProgressBar();
		progressIndicatorInRegularSearch.setFont(new Font(null, Font.BOLD, 16));
		
		JPanel progressBarsPanel = new JPanel();
		progressBarsPanel.setToolTipText("Indicators for: Calculation (upper), and Result (lower)");
		progressBarsPanel.setLayout(new BoxLayout(progressBarsPanel, BoxLayout.PAGE_AXIS));
		progressBarsPanel.add(progressIndicatorInRegularSearch);
		progressBarsPanel.add(regularSearchProgressBar);
		regularSearchBottomPanel.add(progressBarsPanel,BorderLayout.NORTH);
		regularSearchTextArea = new JEditorPane();
		regularSearchTextArea.setEditable(false);
		regularSearchTextArea.setToolTipText(HTML_TABLE_TOOL_TIP);
		JScrollPane scrollPaneForRegularSearchTextArea = new JScrollPane(regularSearchTextArea);
		scrollPaneForRegularSearchTextArea.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		regularSearchBottomPanel.add(scrollPaneForRegularSearchTextArea,BorderLayout.CENTER);
		
		// buttons
		buttonPerformRegularSearch = new JButton("Generate Proof");
		buttonPerformRegularSearch.setActionCommand("regSearch");
		buttonPerformRegularSearch.addActionListener(actionsPerformer);
		buttonFirstRegularSearch = new JButton("<<");
		buttonFirstRegularSearch.setActionCommand(COMMAND_REGULAR_SEARCH_FIRST);
		buttonFirstRegularSearch.addActionListener(actionsPerformer);
		buttonPrevRegularSearch = new JButton("<");
		buttonPrevRegularSearch.setActionCommand(COMMAND_REGUALR_SEARCH_PREVIOUS);
		buttonPrevRegularSearch.addActionListener(actionsPerformer);
		buttonNextRegularSearch = new JButton(">");
		buttonNextRegularSearch.setActionCommand(COMMAND_REG_SEARCH_NEXT);
		buttonNextRegularSearch.addActionListener(actionsPerformer);
		buttonLastRegularSearch = new JButton(">>");
		buttonLastRegularSearch.setActionCommand(COMMAND_REG_SEARCH_LAST);
		buttonLastRegularSearch.addActionListener(actionsPerformer);
		
		
		JPanel buttonPerformRegularSearchPanel = new JPanel();
		buttonPerformRegularSearchPanel.add(buttonPerformRegularSearch);
		buttonPerformRegularSearchPanel.add(buttonFirstRegularSearch);
		buttonPerformRegularSearchPanel.add(buttonPrevRegularSearch);
		buttonPerformRegularSearchPanel.add(buttonNextRegularSearch);
		buttonPerformRegularSearchPanel.add(buttonLastRegularSearch);
		buttonPerformRegularSearchPanel.add(PADDING_BETWEEN_BUTTONS);
		buttonPerformRegularSearchPanel.add(createHelpButton(COMMAND_REGULAR_SEARCH_HELP));
		
		regularSearchBottomPanel.add(buttonPerformRegularSearchPanel,BorderLayout.SOUTH);
		
		regularSearchPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, regularSearchImagePane, regularSearchBottomPanel);
		regularSearchPanel.setResizeWeight(0.5);
		regularSearchPanel.setName(TITLE_AUTOMATIC_MODE);
		tabsPane.add(regularSearchPanel);
		
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.add(tabsPane,BorderLayout.CENTER);
		JPanel statusBarPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		statusBarPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		statusBarLabel = new JLabel("Ready");
		statusBarPanel.add(statusBarLabel);

		contentPane.add(statusBarPanel,BorderLayout.SOUTH);
		mainFrame.setContentPane(contentPane);
		
		adjustColumnsSizes();
		mainFrame.pack();
		mainFrame.setVisible(true);
		mainFrame.setExtendedState(mainFrame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		
		SpellCheckerRegisterResult spellCheckerRegisterResult = registerSpellChecker();
		if (SpellCheckerRegisterResult.NOT_REGISTERED_SHOULD_EXIT.equals(spellCheckerRegisterResult))
		{
			mainFrame.setVisible(false);
			System.exit(0);
		}
		else
		{
			GuiSystemCreatorDialog systemCreator = new GuiSystemCreatorDialog(this);
			boolean built = systemCreator.go();
			if (built)
			{
				boolean useF1Classifier = false;
				if (BiuteeConstants.GUI_LOADS_LABELED_SAMPLES)
				{
					useF1Classifier = systemCreator.isUseF1Classifier();
				}
				actionsPerformer.setUnderlyingSystem(systemCreator.getUnderlyingSystem(),useF1Classifier);
			}
			else
			{
				logger.info("Constructing underlying system was interrupted.");
				Throwable exception = systemCreator.getException();
				if (exception != null)
				{
					exception.printStackTrace(System.out);
					ExceptionUtil.logException(exception, logger);
				}
				System.exit(1);
			}
		}
		
		
		
	}
	
	
	private SpellCheckerRegisterResult registerSpellChecker() throws VisualTracingToolException
	{
		SpellCheckerRegisterResult result = SpellCheckerRegisterResult.REGISTERED;
		Vector<JTextComponent> textComponents = new Vector<JTextComponent>();
		textComponents.add(this.getTextTextArea());
		textComponents.add(this.getHypothesisTextArea());
		try
		{
			SpellCheckerRegister spellCheckerRegister = new SpellCheckerRegister(this.getMainFrame(), textComponents);
			result = spellCheckerRegister.register();
		}
		catch (FileNotFoundException e)
		{
			throw new VisualTracingToolException("Exception when trying to use spell checker",e);
		}
		return result;
	}

	/**
	 * @param command
	 * @return
	 */
	private JButton createHelpButton(String command) {
		JButton buttonHelp = new JButton("?");
		buttonHelp.setActionCommand(command);
		buttonHelp.addActionListener(actionsPerformer);
		helpButtonCommands.add(command);
		helpButtons.add(buttonHelp);
		return buttonHelp;
	}

	@SuppressWarnings("serial")
	private JScrollPane createNewImagePane() {
		ImagePaintingComponent imagePaintingComponent = new ImagePaintingComponent();
		JScrollPane newImageScrollPane = 
				new JScrollPane(imagePaintingComponent, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

		mapImagePanesToImagePaintingComponents.put(newImageScrollPane, imagePaintingComponent);
		newImageScrollPane.addMouseWheelListener(actionsPerformer);
		newImageScrollPane.addKeyListener(actionsPerformer);
		
		 /* add a new actions for zoomIn and zoomOut key strokes to the panel's action map */
		newImageScrollPane.getActionMap().put(COMMAND_ZOOM_IN, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
               	actionsPerformer.zoomIn();
               }
           });
		newImageScrollPane.getActionMap().put(COMMAND_ZOOM_OUT, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				actionsPerformer.zoomOut();
			}
       });
		// listen to +/-/=/Ctrl+/Ctrl-/Ctrl= keystrokes
		newImageScrollPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, InputEvent.CTRL_MASK), COMMAND_ZOOM_IN);
		newImageScrollPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, InputEvent.CTRL_MASK), COMMAND_ZOOM_IN);
		newImageScrollPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, InputEvent.CTRL_MASK), COMMAND_ZOOM_OUT);
		newImageScrollPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(Character.valueOf('-'), 0), COMMAND_ZOOM_OUT);
		newImageScrollPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(Character.valueOf('+'), 0), COMMAND_ZOOM_IN);
		newImageScrollPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(Character.valueOf('='), 0), COMMAND_ZOOM_IN);
		
		return newImageScrollPane;
	}

	public static void startGui(String[] args)
	{
		boolean initializationSucceeded = true;
		try
		{
			LogInitializer logInitializer =
				new LogInitializer(args[0]);
			logInitializer.init();
		}
		catch(Throwable e)
		{
			initializationSucceeded=false;
			try
			{
				e.printStackTrace(System.out);
				System.out.println();
				System.out.println("Initialization failed. Aborting.");
			}
			catch(Throwable tt){}
		}
		if (initializationSucceeded)
		{
			try
			{
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
				SwingUtilities.changeDefaultFontSize(GUI_FONT_SIZE);
				VisualTracingTool cpe = new VisualTracingTool(args[0]);
				cpe.createAndShowGUI();
			}
			catch (Throwable e)
			{
				try
				{
					ExceptionUtil.logException(e, logger);
					System.out.println();
					System.out.println("Presenting GUI failed. Aborting.");
					System.exit(0);
				}
				catch(Throwable tt){}
			}
		}
	}
	
	public String getConfigurationFileName()
	{
		return configurationFileName;
	}

	public JTextArea getTextTextArea()
	{
		return textTextArea;
	}
	public JEditorPane getTextCookedTextLabel()
	{
		return cookedTextLabel;
	}
	public JTextArea getHypothesisTextArea()
	{
		return hypothesisTextArea;
	}
	
	public Set<String> getHelpButtonCommands() {
		return helpButtonCommands;
	}
	
	public JScrollPane getTextImagePane()
	{
		return textImagePane;
	}
	
	

	public JScrollPane getHypothesisImagePane()
	{
		return hypothesisImagePane;
	}

	public JFrame getMainFrame()
	{
		return mainFrame;
	}
	
	public JComboBox<String> getOrigTreesComboBox() {
		return origTreesComboBox;
	}
	
	public JSpinner getSpinnerOfRegualrSearchImageZoom()
	{
		return spinnerOfRegualrSearchImageZoom;
	}
	
	public JTable getExistingTreesTable()
	{
		return existingTreesTable;
	}
	
	public JCheckBox getOnlyLastGeneratedCheckBox()
	{
		return onlyLastGeneratedCheckBox;
	}

	public JScrollPane getSelectedTreeImagePane()
	{
		return selectedTreeImagePane;
	}
	
	public JEditorPane getSelectedTreeTextArea()
	{
		return selectedTreeTextArea;
	}
	
	public JButton getButtonGo()
	{
		return buttonGo;
	}
	
	public JComboBox<String> getComboBoxTaskNames()
	{
		return comboBoxTaskNames;
	}
	public JComboBox<String> getComboBoxDatasetNames()
	{
		return comboBoxDatasetNames;
	}
	public JSpinner getQuickAccessSpinner()
	{
		return quickAccessSpinner;
	}
	public boolean isQuickAccessAllow()
	{
		return quickAccessAllow;
	}
	public void setQuickAccessAllow(boolean quickAccessAllow)
	{
		this.quickAccessAllow = quickAccessAllow;
	}
	public boolean isDatasetNamesAllow()
	{
		return datasetNamesAllow;
	}
	public void setDatasetNamesAllow(boolean datasetNamesAllow)
	{
		this.datasetNamesAllow = datasetNamesAllow;
	}

	public Set<JSpinner> getImageZoomSpinners()
	{
		return imageZoomSpinners;
	}

	public JSplitPane getPairSplitPanel() {
		return pairSplitPanel;
	}
	
	public Map<JScrollPane, ImagePaintingComponent> getMapImagePanesToImagePaintingComponents()
	{
		return mapImagePanesToImagePaintingComponents;
	}
	
	public JScrollPane getRegularSearchImagePane()
	{
		return regularSearchImagePane;
	}

	public JEditorPane getRegularSearchTextArea()
	{
		return regularSearchTextArea;
	}
	
	public JButton getButtonPerformRegularSearch()
	{
		return buttonPerformRegularSearch;
	}
	public JButton getButtonPrevRegularSearch()
	{
		return buttonPrevRegularSearch;
	}
	public JButton getButtonNextRegularSearch()
	{
		return buttonNextRegularSearch;
	}
	public JButton getButtonFirstRegularSearch()
	{
		return buttonFirstRegularSearch;
	}
	public JButton getButtonLastRegularSearch()
	{
		return buttonLastRegularSearch;
	}

	public JProgressBar getRegularSearchProgressBar()
	{
		return regularSearchProgressBar;
	}
	public JProgressBar getProgressIndicatorInRegularSearch()
	{
		return progressIndicatorInRegularSearch;
	}

	public JLabel getStatusBarLabel()
	{
		return statusBarLabel;
	}
	
	public JSplitPane getCurrentTreeInformationPanel()
	{
		return currentTreeInformationPanel;
	}
			
	public JTabbedPane getTabsPane()
	{
		return tabsPane;
	}
	
	public JCheckBoxMenuItem getShowAnnotationsMenuItem()
	{
		return showAnnotationsMenuItem;
	}
	public JSplitPane getRegularSearchPanel() {
		return regularSearchPanel;
	}

	public JCheckBoxMenuItem getShowShortNodeContentsMenuItem()
	{
		return showShortNodeContentsMenuItem;
	}
	
	public JCheckBoxMenuItem getShowSearchDetailsMenuItem()
	{
		return showSearchDetailsMenuItem;
	}
	
	public JCheckBoxMenuItem getUseBWTableColorsMenuItem() {
		return useBWTableColorsMenuItem;
	}
	
//	public JCheckBoxMenuItemWithEnableCounter getUseF1ClassifierMenuItem()
//	{
//		return useF1ClassifierMenuItem;
//	}
	public JCheckBoxMenuItem getUseOldBeamSearchMenuItem()
	{
		return useOldBeamSearchMenuItem;
	}

	public GuiRteSumUtilities getSumUtilities()
	{
		return sumUtilities;
	}
	
	public GuiRtePairsUtilities getPairsUtilities()
	{
		return pairsUtilities;
	}

	public void resetRegularSearchProgressBar()
	{
		this.regularSearchProgressBar.setForeground(originalregularSearchProgressBarColor);
		this.regularSearchProgressBar.setValue(0);
		this.regularSearchProgressBar.setStringPainted(false);
	}
	
	public String getStatusBarSystemState()
	{
		return statusBarSystemState;
	}
	
	/**
	 * After calling this method, call {@link #updateStatusBarLabel()}
	 * @param statusBarSystemState
	 */
	public void setStatusBarSystemState(String statusBarSystemState)
	{
		this.statusBarSystemState = statusBarSystemState;
	}
	public String getStatusBarClassifierType()
	{
		return statusBarClassifierType;
	}

	public void setStatusBarClassifierType(String statusBarClassifierType)
	{
		this.statusBarClassifierType = statusBarClassifierType;
	}

	public Set<StatusBarState> getStatusBarStates()
	{
		return statusBarStates;
	}
	
	public void updateStatusBarLabel()
	{
		final StringBuffer sb = new StringBuffer();
		if ( (statusBarStates.contains(StatusBarState.PAIRS_MODE)) && (statusBarStates.contains(StatusBarState.SUM_MODE)) )
		{
			statusBarSystemState = "Disallowed!";
		}
		sb.append(statusBarSystemState);
		
		sb.append(statusBarClassifierType);
		
		if (this.statusBarStates.size()>0)
		{
			sb.append(" <");
			boolean firstIteration = true;
			for (StatusBarState state : this.statusBarStates)
			{
				if (firstIteration)firstIteration=false;
				else sb.append("; ");

				sb.append(state.getDescription());
			}
			sb.append(">");
		}
		
		invokeLater(new Runnable()
		{
			public void run()
			{
				statusBarLabel.setText(sb.toString());
			}
		});
	}


	public void disableAll()
	{
		setEnableModeOfAll(false);
	}
	

	public void enableAll()
	{
		setEnableModeOfAll(true);
	}
	
	public void textMakeNotEditable()
	{
		this.textEditableFlag = false;
		textSetEditable();
	}

	public void textMakeEditable()
	{
		this.textEditableFlag = true;
		textSetEditable();
	}
	
	private void setEnableModeOfAll(boolean mode)
	{
		currentEnableMode = mode;
		
		textTextArea.setEditable(mode&&textEditableFlag);
		hypothesisTextArea.setEditable(mode);
		buttonGo.setEnabled(mode);
		comboBoxTaskNames.setEnabled(mode);
		quickAccessSpinner.setEnabled(mode&&quickAccessAllow);
		comboBoxDatasetNames.setEnabled(mode&&datasetNamesAllow);
		origTreesComboBox.setEnabled(mode);
		buttonShowCoref.setEnabled(mode);
		buttonShowSurrounding.setEnabled(mode);
		onlyLastGeneratedCheckBox.setEnabled(mode);
		buttonDisplaySelectedTree.setEnabled(mode);
		selectedTreeButtonPrev.setEnabled(mode);
		selectedTreeButtonNext.setEnabled(mode);
		selectedTreeButtonFirst.setEnabled(mode);
		selectedTreeButtonLast.setEnabled(mode);
		buttonPerformRegularSearch.setEnabled(mode);
		buttonPrevRegularSearch.setEnabled(mode);
		buttonNextRegularSearch.setEnabled(mode);
		buttonLastRegularSearch.setEnabled(mode);
		buttonFirstRegularSearch.setEnabled(mode);
//		spinnerOfRegualrSearchImageZoom.setEnabled(mode);
		for (JMenuItem menuItem : this.menuItems)
		{
			menuItem.setEnabled(mode);
		}
		for (JSpinner spinner : getImageZoomSpinners())
			spinner.setEnabled(mode);
		for (JButton helpButton : helpButtons)
			helpButton.setEnabled(mode);
		
//		if (true==mode)
//		{
//			useF1ClassifierMenuItem.decEnableCount();
//		}
//		else
//		{
//			useF1ClassifierMenuItem.incEnableCount();
//		}
	}
	

	private void textSetEditable()
	{
		this.textTextArea.setEditable(textEditableFlag&&currentEnableMode);
	}
	
	

	private void adjustColumnsSizes()
	{
		TableColumnModel columnModel = existingTreesTable.getColumnModel();
		int sumWidths = 0;
		for (int index=0;index<columnModel.getColumnCount();++index)
		{
			TableColumn column = columnModel.getColumn(index);
			sumWidths += column.getPreferredWidth();
		}
		double oneWidthDouble = ((double)sumWidths)/(LAST_OPERATION_COLUMN_WIDTH_RATIO+columnModel.getColumnCount()-1);
		int oneWidth = (int)oneWidthDouble;
		int specWidth = sumWidths-(columnModel.getColumnCount()-1)*oneWidth;
		for (int index=0;index<columnModel.getColumnCount();++index)
		{
			TableColumn column = columnModel.getColumn(index);
			if (index==ColumnNames.LAST_OPERATION.ordinal())	//  COLUMN_NAMES.indexOf(COL_LAST_OPERATION))
			{
				column.setPreferredWidth(specWidth);
			}
			else
			{
				column.setPreferredWidth(oneWidth);
			}
		}
	}
	
	private void createMenu()
	{
		JMenuBar menuBar = new JMenuBar();
		JMenu menuFile = new JMenu("File");
		menuBar.add(menuFile);

		JMenu menuOptions = new JMenu("Options");
		menuBar.add(menuOptions);

		JMenu menuHelp = new JMenu("Help");
		menuBar.add(menuHelp);
		
		JMenuItem loadSumDataSetMenuItem = new JMenuItem("Load RTE-Sum Dataset...");
		loadSumDataSetMenuItem.setActionCommand("loadSumDataSetMenuItem");
		loadSumDataSetMenuItem.addActionListener(sumUtilities);
		menuFile.add(loadSumDataSetMenuItem);
		menuItems.add(loadSumDataSetMenuItem);

		JMenuItem selectSentenceMenuItem = new JMenuItem("Select Sentence From Dataset...");
		selectSentenceMenuItem.setActionCommand("selectSentenceMenuItem");
		selectSentenceMenuItem.addActionListener(sumUtilities);
		menuFile.add(selectSentenceMenuItem);
		menuItems.add(selectSentenceMenuItem);
		
		JMenuItem loadPairsDatasetMenuItem = new JMenuItem("Load and Select RTE-Pairs Dataset...");
		loadPairsDatasetMenuItem.setActionCommand("loadPairsDataSetMenuItem");
		loadPairsDatasetMenuItem.addActionListener(pairsUtilities);
		menuFile.add(loadPairsDatasetMenuItem);
		menuItems.add(loadPairsDatasetMenuItem);
		
		JMenuItem loadRecentPairsMenuItem = new JMenuItem("Load and Select recent pairs...");
		loadRecentPairsMenuItem.setActionCommand("loadLastPairsMenuItem");
		loadRecentPairsMenuItem.addActionListener(pairsUtilities);
		menuFile.add(loadRecentPairsMenuItem);
		menuItems.add(loadRecentPairsMenuItem);
		
		
		JMenuItem exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.setActionCommand("exitMenuItem");
		exitMenuItem.addActionListener(actionsPerformer);
		menuFile.add(exitMenuItem);
		
		JMenuItem aboutMenuItem = new JMenuItem("About...");
		aboutMenuItem.setActionCommand("aboutMenuItem");
		aboutMenuItem.addActionListener(actionsPerformer);
		menuHelp.add(aboutMenuItem);
		menuItems.add(aboutMenuItem);

		showAnnotationsMenuItem = new JCheckBoxMenuItem("Show Semantic Annotations On Nodes");
		showAnnotationsMenuItem.setSelected(false);
		showAnnotationsMenuItem.addItemListener(actionsPerformer);
		menuOptions.add(showAnnotationsMenuItem);
		menuItems.add(showAnnotationsMenuItem);

		showShortNodeContentsMenuItem = new JCheckBoxMenuItem("Show Concise Nodes");
		showShortNodeContentsMenuItem.setSelected(false);
		showShortNodeContentsMenuItem.addItemListener(actionsPerformer);
		menuOptions.add(showShortNodeContentsMenuItem);
		menuItems.add(showShortNodeContentsMenuItem);

		showSearchDetailsMenuItem = new JCheckBoxMenuItem("Show Search Details");
		showSearchDetailsMenuItem.setSelected(false);
		showSearchDetailsMenuItem.addItemListener(actionsPerformer);
		menuOptions.add(showSearchDetailsMenuItem);
		menuItems.add(showSearchDetailsMenuItem);
		
		useBWTableColorsMenuItem = new JCheckBoxMenuItem("Use Black & White Table Colors");
		useBWTableColorsMenuItem.setSelected(false);
		useBWTableColorsMenuItem.addItemListener(actionsPerformer);
		menuOptions.add(useBWTableColorsMenuItem);
		menuItems.add(useBWTableColorsMenuItem);
		
		
//		useF1ClassifierMenuItem = new JCheckBoxMenuItemWithEnableCounter("Use F1 optimized classifier");
//		useF1ClassifierMenuItem.setSelected(false);
//		useF1ClassifierMenuItem.addItemListener(actionsPerformer);
//		menuOptions.add(useF1ClassifierMenuItem);
		// menuItems.add(useF1ClassifierMenuItem);

		useOldBeamSearchMenuItem = new JCheckBoxMenuItem("Use old beam search");
		useOldBeamSearchMenuItem.setSelected(false);
		useOldBeamSearchMenuItem.addItemListener(actionsPerformer);
		menuOptions.add(useOldBeamSearchMenuItem);
		menuItems.add(useOldBeamSearchMenuItem);

		
		mainFrame.setJMenuBar(menuBar);
	}
	
	private void createComboBoxTaskNames()
	{
		Vector<String> items = new Vector<String>();
		for (Feature feature : Feature.getGlobalFeatures())
		{
			String taskName = feature.getTaskName();
			if (taskName!=null)
			{
				items.add(taskName);
			}
		}
		items.add(IGNORE_TASK_NAME_STRING);
		this.comboBoxTaskNames = new JComboBox<String>(items);
		comboBoxTaskNames.setSelectedIndex(0);
	}
	
	private String configurationFileName;
	
	private ActionsPerformer actionsPerformer = new ActionsPerformer(this);

	private JFrame mainFrame; 
	
	private JTextArea textTextArea;
	private JEditorPane cookedTextLabel;
	private boolean textEditableFlag = true;
	private JTextArea hypothesisTextArea;
	private JButton buttonGo;
	private JComboBox<String> comboBoxTaskNames;
	private JSpinner quickAccessSpinner;
	private boolean quickAccessAllow = false;
	private JComboBox<String> comboBoxDatasetNames;
	private boolean datasetNamesAllow = false;
	private JSplitPane pairSplitPanel;
	
	private JScrollPane textImagePane;
	private JButton buttonShowCoref; 
	private JButton buttonShowSurrounding;
	
	private JScrollPane hypothesisImagePane;
	
	private JTable existingTreesTable;
	private JCheckBox onlyLastGeneratedCheckBox;
	private JButton buttonDisplaySelectedTree; 
	private JCheckBoxMenuItem showSearchDetailsMenuItem;
	
	private JScrollPane selectedTreeImagePane;
	private JEditorPane selectedTreeTextArea;
	
	private JButton selectedTreeButtonPrev;
	private JButton selectedTreeButtonNext;
	
	private JScrollPane regularSearchImagePane;
	private JEditorPane regularSearchTextArea;
	private JButton buttonPerformRegularSearch;
	private JButton buttonPrevRegularSearch;
	private JButton buttonNextRegularSearch;
	private JSpinner spinnerOfRegualrSearchImageZoom;
	private JProgressBar regularSearchProgressBar;
	private JProgressBar progressIndicatorInRegularSearch;
	private Color originalregularSearchProgressBarColor;
	
	private JButton selectedTreeButtonFirst;
	private JButton selectedTreeButtonLast;
	private JButton buttonFirstRegularSearch;
	private JButton buttonLastRegularSearch;
	private Set<String> helpButtonCommands = new LinkedHashSet<String>();
	
	/**
	 * mutually exclusive with {@link #showShortNodeContentsMenuItem}
	 */
	private JCheckBoxMenuItem showAnnotationsMenuItem;
	/**
	 * mutually exclusive with {@link #showAnnotationsMenuItem}
	 */
	private JCheckBoxMenuItem showShortNodeContentsMenuItem;
	private JSplitPane regularSearchPanel;
	
	private JLabel statusBarLabel;
	private String statusBarSystemState = READY_STATUS_BAR_SYSTEM_STATE;
	private String statusBarClassifierType = STATUS_BAR_CLASSIFIER_TYPE_ACCURACY_OPTIMIZED;
	private Set<StatusBarState> statusBarStates = new LinkedHashSet<StatusBarState>();
	private JSplitPane currentTreeInformationPanel;
	private Set<JButton> helpButtons = new LinkedHashSet<JButton>();
	private JComboBox<String> origTreesComboBox;
	private JCheckBoxMenuItem useBWTableColorsMenuItem;
	// private JCheckBoxMenuItemWithEnableCounter useF1ClassifierMenuItem;
	private JCheckBoxMenuItem useOldBeamSearchMenuItem;
	/**
	 * the set of all the synchronized image zoom spinners
	 */
	private Set<JSpinner> imageZoomSpinners = new LinkedHashSet<JSpinner>();
	/**
	 * the set of image components in which trees are painted (matching {@link #imageZoomSpinners})
	 */
	private Map<JScrollPane, ImagePaintingComponent> mapImagePanesToImagePaintingComponents = new HashMap<JScrollPane, ImagePaintingComponent>();
	
	private JTabbedPane tabsPane;
	
	private Set<JMenuItem> menuItems = new LinkedHashSet<JMenuItem>();
	
	private GuiRteSumUtilities sumUtilities = null;
	private GuiRtePairsUtilities pairsUtilities = null;
	
	private boolean currentEnableMode = true;
	
	private static final Logger logger = Logger.getLogger(VisualTracingTool.class);
}
