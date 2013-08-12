package eu.excitementproject.eop.biutee.rteflow.systems.gui;
import static javax.swing.SwingUtilities.invokeLater;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ToolTipManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.classifiers.ClassifierException;
import eu.excitementproject.eop.biutee.classifiers.ClassifierUtils;
import eu.excitementproject.eop.biutee.plugin.PluginAdministrationException;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeAndFeatureVector;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeHistory;
import eu.excitementproject.eop.biutee.rteflow.macro.TreeHistoryComponent;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapException;
import eu.excitementproject.eop.biutee.rteflow.macro.gap.GapToolInstances;
import eu.excitementproject.eop.biutee.rteflow.systems.rtepairs.ExtendedPairData;
import eu.excitementproject.eop.biutee.rteflow.systems.rtepairs.PairProcessor;
import eu.excitementproject.eop.biutee.rteflow.systems.rtesum.preprocess.ExtendedPreprocessedTopicDataSet;
import eu.excitementproject.eop.biutee.script.ScriptException;
import eu.excitementproject.eop.biutee.utilities.ProgressFire;
import eu.excitementproject.eop.biutee.utilities.ShortMessageFire;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableList;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformation;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformationException;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap.TreeAndParentMapException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.TreeStringGenerator.TreeStringGeneratorException;
import eu.excitementproject.eop.common.utilities.StringUtil;
import eu.excitementproject.eop.common.utilities.Utils;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFileDuplicateKeyException;
import eu.excitementproject.eop.common.utilities.datasets.rtepairs.RTEMainReaderException;
import eu.excitementproject.eop.common.utilities.datasets.rtepairs.RTEMainWriterException;
import eu.excitementproject.eop.common.utilities.datasets.rtepairs.TextHypothesisPair;
import eu.excitementproject.eop.common.utilities.text.TextPreprocessorException;
import eu.excitementproject.eop.lap.biu.coreference.CoreferenceResolutionException;
import eu.excitementproject.eop.lap.biu.en.parser.ParserRunException;
import eu.excitementproject.eop.lap.biu.lemmatizer.LemmatizerException;
import eu.excitementproject.eop.lap.biu.ner.NamedEntityRecognizerException;
import eu.excitementproject.eop.lap.biu.sentencesplit.SentenceSplitterException;
import eu.excitementproject.eop.transformations.datastructures.SingleItemList;
import eu.excitementproject.eop.transformations.generic.truthteller.AnnotatorException;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.rules.RuleBaseException;
import eu.excitementproject.eop.transformations.operations.specifications.Specification;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * 
 * TO-DO (comment by Asher Stern): GUI code is not of high quality and should be
 * improved. Need to re-design, make it more modular, adding documentation and
 * improve code.
 * 
 * @author Asher Stern
 * @since May 24, 2011
 * 
 */
public class ActionsPerformer implements ActionListener, ChangeListener, ItemListener, WindowListener, MouseListener, MouseWheelListener, KeyListener {

	
	private static final String HTML_OPEN = "<HTML>";
	private static final int TEXT_COMBOBOX_ITEM_LENGTH = 40;
	private static final double MOUSE_WHEEL_SENSITIVITY_COEFICIENT = 0.05;
	private static final String ORIGINAL_TREE_TITLE = "Original Tree";
	private static final String SPEC_OF_INITIAL_OPERATION = "Original Sentence ";
	private static final String HTML_OPEN_TABLE_ROW = "<tr>\n";
	private static final String FONT_STYLE_OF_FOCUSED_TABLE_ROW = "<B>"; 
	private static final String HTML_CLOSE_COLUMN = "</td>";
	private static final String HTML_OPEN_COLUMN = "<td>";
	private static final String ARROW_SPACE = "&#160;&#160;&#160;&#160;"; // four HTML spaces
	private static final String RIGHT_ARROW = "&#8658;"; // This must be a UNIQUE string in all the HTML text
	private static final String HTML_FORMATTED_BODY_MARK = "<body style=\"font-size:" + VisualTracingTool.HTML_FONT_SIZE + "px\">";
	
	private static final String WARNING_SUM_BUT_NO_F1 = "You selected Summarization mode, but not F1 optimized classifier.\n" +
			"The results will be incorrect!\n" +
			"Note that you can set F1 optimized-classifier in the options menu.\n" +
			"(Note that if the system is already initialized, you have to restart the application to choose F1-optimized classifier.)\n" +
			"Continue?";

	
	/**
	 *  "rgb(0,255,255) "; // Color light-blue (TKHELET)
	 */
	private static final Color COLOR_FOREGROUND_GOOD = new Color(0,255,255);
	private static final String COLOR_FOREGROUND_GOOD_STRING = 
			"rgb(" + COLOR_FOREGROUND_GOOD.getRed() +","+ COLOR_FOREGROUND_GOOD.getGreen()+","+COLOR_FOREGROUND_GOOD.getBlue()+")";;
	/**
	 * dark red
	 */
	private static final Color COLOR_FOREGROUND_BAD = new Color(32,0,0);	// dark red
	/**
	 * dark red
	 */
	private static final String COLOR_FOREGROUND_BAD_STRING = 
			"rgb(" + COLOR_FOREGROUND_BAD.getRed() +","+ COLOR_FOREGROUND_BAD.getGreen()+","+COLOR_FOREGROUND_BAD.getBlue()+")";
	
	
	private double classificationScoreForPredictions;
	private double classificationScoreForSearch;
	/**
	 * Zoom ratio for all tree images
	 */
	private double masterZoomRatio = 1.0;
	private int textTreesComboBoxSelection = 0;
	private Vector<String> comboBoxItems;

	
	public NodeDisplayMode getNodeDisplayMode()
	{
		return nodeDisplayMode;
	}

	
	public ActionsPerformer(final VisualTracingTool cpe)
	{
		super();
		this.cpe = cpe;
		
		// Construct the field "nodeDisplayMode"
		boolean showAnnotations = false;
		boolean showShort = false;
		if (cpe.getShowAnnotationsMenuItem()!=null) showAnnotations = cpe.getShowAnnotationsMenuItem().isSelected();
		if (cpe.getShowShortNodeContentsMenuItem()!=null) showShort = cpe.getShowShortNodeContentsMenuItem().isSelected();
		this.nodeDisplayMode = NodeDisplayMode.newNodeDisplayMode(showAnnotations, showShort);
		
		this.guiUtils = new GuiUtils(this);
		
	}
	
	/**
	 * Assuming this is called from the Swing thread (I.e. in the context of {@link javax.swing.SwingUtilities#invokeLater(Runnable)})
	 * 
	 * @param underLyingSystem
	 * @param useF1Classifier
	 * @throws TeEngineMlException
	 */
	public void setUnderlyingSystem(SingleComponentUnderlyingSystem underLyingSystem, Boolean useF1Classifier) throws TeEngineMlException
	{
		this.underLyingSystem = underLyingSystem;
		Set<String> allowedDatasets = this.underLyingSystem.getAllowedDatasetNames().getMutableSetCopy();
		if (allowedDatasets.size()>0)
		{
			cpe.getComboBoxDatasetNames().setModel(new DefaultComboBoxModel<String>(Utils.collectionToArray(allowedDatasets, new String[0])));
			cpe.setDatasetNamesAllow(true);
		}
		else
		{
			cpe.setDatasetNamesAllow(false);
		}
		
		this.useF1Classifier = useF1Classifier;
		
		if (this.useF1Classifier!=null){if (this.useF1Classifier.booleanValue())
		{
			cpe.setStatusBarClassifierType(VisualTracingTool.STATUS_BAR_CLASSIFIER_TYPE_F1_OPTIMIZED);
			cpe.updateStatusBarLabel();
		}}

		
	}

	/**
	 * Handles all button-clicks of Visual-Tracing-Tool, and some of the menu-items
	 */
	public void actionPerformed(ActionEvent e)
	{
		try
		{
			// Tab: T-H pair
			
			if (e.getActionCommand().equals("thPairGo")) // button "pre-process"
			{
				// verifications
				if (cpe.getTextTextArea().getText().isEmpty())
					throw new VisualTracingToolException("The Text text box is empty. You must fill it with one or more sentences before clicking on \"Go!\".");
				if (cpe.getHypothesisTextArea().getText().isEmpty())
					throw new VisualTracingToolException("The Hypothesis text box is empty. You must fill it with one sentence before clicking on \"Go!\".");
				
				// Shows a warning if the user wants to work on RTE-Sum dataset, but the classifier
				// is set to optimize accuracy, rather than F1
				boolean doIt = true;
				if (this.useF1Classifier!=null)
				{
					if (this.cpe.getSumUtilities().isSumDatasetSentenceSelected() && (!this.useF1Classifier.booleanValue()))
					{
						int answer = JOptionPane.showConfirmDialog(cpe.getMainFrame(),WARNING_SUM_BUT_NO_F1, "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null);
						if (JOptionPane.YES_OPTION!=answer)
						{
							doIt = false;
						}
					}
				}
				// Proceed with pre-processing
				if (doIt)
				{
					cpe.getMainFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					cpe.setStatusBarSystemState("Processing...");
					cpe.updateStatusBarLabel();
					cpe.disableAll();
					cleanBeforeProcessingNewPair();
					ThPairGoRunnable runnable;
					runnable = new ThPairGoRunnable();
					Thread thread = new Thread(runnable);
					thread.start();
				}
			}
			
			// Tab: text trees
			
			else if (e.getActionCommand().equals("show_coref"))
			{
				if ( (this.underLyingSystem!=null) && (this.pairData!=null) )
				{
					TextBoxDialog<TextBoxDialog.EMPTY_ENUM> textBoxDialog = new TextBoxDialog<TextBoxDialog.EMPTY_ENUM>(cpe.getMainFrame(),"Coreference",true,buildCoreferenceDescription());
					textBoxDialog.showMe();
					// SwingUtilities.messageBox(cpe.getMainFrame(), buildCoreferenceDescription(), "Coreference", true);
				}
			}
			else if (e.getActionCommand().equals("show_surrounding"))
			{
				if ( (this.underLyingSystem!=null) && (this.pairData!=null) )
				{
					List<ExtendedNode> surroundingsContext = underLyingSystem.getSurroundingTrees();
					if (surroundingsContext!=null)
					{
						SurroundingViewDialog surroundingDialog = new SurroundingViewDialog(cpe.getMainFrame(), surroundingsContext, this);
						surroundingDialog.startDialog();
					}
					else
					{
						SwingUtilities.messageBox(cpe.getMainFrame(), "Applicable only for summarization-based dataset", "Not applicable", false);
					}
				}
			}
			else if (e.getActionCommand().equals("displaySelectedTree"))
			{
				displaySelectedTree();
			}
			// text trees combo Box
			else if (e.getSource() == cpe.getOrigTreesComboBox())
			{
				if (null == underLyingSystem) {
					cpe.getOrigTreesComboBox().getModel().setSelectedItem('1');
				} else {
					int newTextTreesComboBoxSelection = cpe.getOrigTreesComboBox().getSelectedIndex();
					if (newTextTreesComboBoxSelection != textTreesComboBoxSelection)
					{
						textTreesComboBoxSelection = newTextTreesComboBoxSelection;
						redrawTextImageWithCurrentComboBoxSelection();
					}
				}
			}

			
			// Tab: View Tree (manual) 
			
			else if (e.getActionCommand().equals(VisualTracingTool.COMMAND_SELECTED_TREE_FIRST)) // button "<<"
			{
				if ((underLyingSystem != null) && (currentDisplayedTreeComponent != null) && (selectedTreeNextStack != null)) {
					SingleTreeComponent singleTreeComponentToDisplay = currentDisplayedTreeComponent;
					if (singleTreeComponentToDisplay.getPrevious() != null) {
						
						// push all previous SingleTreeComponents onto the stack
						while (singleTreeComponentToDisplay.getPrevious() != null)
						{
							selectedTreeNextStack.push(singleTreeComponentToDisplay);
							singleTreeComponentToDisplay = singleTreeComponentToDisplay.getPrevious();
						}
//						selectedTreeNextStack.push(currentDisplayedTreeComponent);
						currentDisplayedTreeComponent = singleTreeComponentToDisplay;
						redrawCurrentSingleTreePane();
					}
				}				
			}
			else if (e.getActionCommand().equals("selectedTreePrev")) // button "<"
			{
				if ((underLyingSystem != null) && (currentDisplayedTreeComponent != null) && (selectedTreeNextStack != null)) {
					SingleTreeComponent singleTreeComponentToDisplay = currentDisplayedTreeComponent.getPrevious();
					if (singleTreeComponentToDisplay != null) {
						selectedTreeNextStack.push(currentDisplayedTreeComponent);
						currentDisplayedTreeComponent = singleTreeComponentToDisplay;
						redrawCurrentSingleTreePane();
					}
				}
			}
			else if (e.getActionCommand().equals("selectedTreeNext")) // button ">"
			{
				if ((underLyingSystem != null) && (currentDisplayedTreeComponent != null) && (selectedTreeNextStack != null)) {
					if (!selectedTreeNextStack.empty()) {
						SingleTreeComponent singleTreeComponentToDisplay = selectedTreeNextStack.pop();
						currentDisplayedTreeComponent = singleTreeComponentToDisplay;
						redrawCurrentSingleTreePane();
					}
				}
			}
			else if (e.getActionCommand().equals(VisualTracingTool.COMMAND_SELECTED_TREE_LAST)) // button ">>"
			{
				if ((underLyingSystem != null) && (currentDisplayedTreeComponent != null) && (selectedTreeNextStack != null)) {
					if (!selectedTreeNextStack.empty()) {
						// pop out all the stack 
						SingleTreeComponent singleTreeComponentToDisplay = null; 
						while (!selectedTreeNextStack.empty()) {
							singleTreeComponentToDisplay = selectedTreeNextStack.pop();
						}
						currentDisplayedTreeComponent = singleTreeComponentToDisplay;
						redrawCurrentSingleTreePane();
					}
				}	
			}
			
			// Tab: Automatic mode
			
			else if (e.getActionCommand().equals("regSearch")) // button "generate proof"
			{
				cpe.resetRegularSearchProgressBar();
				cpe.getButtonPerformRegularSearch().setEnabled(false);
				cpe.getButtonPrevRegularSearch().setEnabled(false);
				cpe.getButtonNextRegularSearch().setEnabled(false);
				cpe.getButtonFirstRegularSearch().setEnabled(false);
				cpe.getButtonLastRegularSearch().setEnabled(false);
				cpe.setStatusBarSystemState("Preforming Regular Search...");
				cpe.updateStatusBarLabel();
				cpe.getRegularSearchProgressBar().setIndeterminate(true);
				new Thread(new Runnable() {
					public void run() {
						try {
							regularSearch();
						} catch (Exception ex) {
							handleError(ex, true);
						} finally {
							invokeLater(new Runnable() {
								public void run() {
									cpe.setStatusBarSystemState(VisualTracingTool.READY_STATUS_BAR_SYSTEM_STATE);
									cpe.updateStatusBarLabel();
									cpe.getButtonPerformRegularSearch().setEnabled(true);
									cpe.getButtonPrevRegularSearch().setEnabled(true);
									cpe.getButtonNextRegularSearch().setEnabled(true);
									cpe.getButtonFirstRegularSearch().setEnabled(true);
									cpe.getButtonLastRegularSearch().setEnabled(true);
//									cpe.getSpinnerOfRegualrSearchImageZoom().setEnabled(true);
									cpe.getRegularSearchProgressBar().setIndeterminate(false);
								}
							});
						}
					}
				}).start();
			}
			else if (e.getActionCommand().equals(VisualTracingTool.COMMAND_REGULAR_SEARCH_FIRST)) // button "<<"
			{
				if (historyComponents != null) {
					if (historyComponentsIndex >= 0) {
						historyComponentsIndex = -1;
						redrawRegularSearchTreeWithOriginalTree();
						
						// redraw the text area, now that the historyComponentsIndex changed
						redrawRegularSearchTextArea();
					}
				} else
					handleSystemNotInitializedErrorForRegularSearch();
			}
			else if (e.getActionCommand().equals(VisualTracingTool.COMMAND_REGUALR_SEARCH_PREVIOUS)) // button "<"
			{
				if (historyComponents != null) {
					if (historyComponentsIndex > 0) {
						--historyComponentsIndex;
						redrawRegularSeachTreeAfterUpdatingTheComponentIndex();
					} else {
						historyComponentsIndex = -1;
						redrawRegularSearchTreeWithOriginalTree();
					}
					// redraw the text area, now that the historyComponentsIndex changed
					redrawRegularSearchTextArea();
				} else
					handleSystemNotInitializedErrorForRegularSearch();
			}
			else if (e.getActionCommand().equals("regSearchNext")) // button ">"
			{
				if (historyComponents != null) {
					if (historyComponentsIndex < (historyComponents.size() - 1)) {
						++historyComponentsIndex;
						redrawRegularSeachTreeAfterUpdatingTheComponentIndex();

						// redraw the text area, now that the historyComponentsIndex changed
						redrawRegularSearchTextArea();
					}
				} else
					handleSystemNotInitializedErrorForRegularSearch();
			}
			else if (e.getActionCommand().equals(VisualTracingTool.COMMAND_REG_SEARCH_LAST)) // button ">>"
			{
				if (historyComponents != null) {
					if (historyComponentsIndex < (historyComponents.size() - 1)) {
						historyComponentsIndex = historyComponents.size() - 1;
						redrawRegularSeachTreeAfterUpdatingTheComponentIndex();

						// redraw the text area, now that the historyComponentsIndex changed
						redrawRegularSearchTextArea();
					}
				} else
					handleSystemNotInitializedErrorForRegularSearch();
			}
			
			// Main menu
			
			else if (e.getActionCommand().equals("exitMenuItem"))
			{
				performExit();
			}
			else if (e.getActionCommand().equals("aboutMenuItem"))
			{
				HelpAboutDialog aboutDialog = new HelpAboutDialog(cpe.getMainFrame());
				aboutDialog.setVisible(true);
			} 
			
			// Help buttons code (all tabs)
			
			else if (cpe.getHelpButtonCommands().contains(e.getActionCommand()))
			{
				String message = GuiHelpTexts.getMessage(e.getActionCommand());
				if (
						(e.getActionCommand()==VisualTracingTool.COMMAND_TEXT_TREES_HELP)
						||
						(e.getActionCommand()==VisualTracingTool.COMMAND_SELECTED_TREE_HELP)
						||
						(e.getActionCommand()==VisualTracingTool.COMMAND_REGULAR_SEARCH_HELP)
					)
				{
					// The last line in the help-message-box is a description of the
					// strings printed on each parse-tree-node and parse-tree-edge.
					String nodeAndEdgeStringDescription = nodeDisplayMode.getNodeAndEdgeString().getDescription();
					if (nodeAndEdgeStringDescription!=null)
					{
						message = message+"\nParse-tree node-information is interpreted as follows:\n"+nodeAndEdgeStringDescription;
					}
				}
				SwingUtilities.showTextBoxDialog(cpe.getMainFrame(), message,
						GuiHelpTexts.getTitle(e.getActionCommand()), true, false);
			}
			
		}
		catch (Exception ex)
		{
			handleError(ex, false);
		}
	} // and of actionPerformed() method.


	/**
	 * Handles check-boxes, including check-boxes-menu-items.
	 */
	public void itemStateChanged(ItemEvent e)
	{
		try
		{
			// TAB manual mode
			
			if (e.getItemSelectable() == cpe.getOnlyLastGeneratedCheckBox()) // check box "display only last generated trees"
			{
				if (e.getStateChange() == ItemEvent.SELECTED)
				{
					displayOnlyLastGenerated = true;
				} else
				{
					displayOnlyLastGenerated = false;
				}
				setTreesTableData();
			}
			
			// Check-box menu-items
			
			else if (e.getItemSelectable() == cpe.getShowAnnotationsMenuItem())
			{
				if (e.getStateChange() == ItemEvent.SELECTED) {
					nodeDisplayMode = nodeDisplayMode.addShowAnnotations();
				} else {
					nodeDisplayMode = nodeDisplayMode.removeShowAnnotations();
				}
				refreshAllTreeImages();
			}
			else if (e.getItemSelectable() == cpe.getShowShortNodeContentsMenuItem())
			{
				if (e.getStateChange() == ItemEvent.SELECTED)
				{
					nodeDisplayMode = nodeDisplayMode.addShortNodeContents();
				}
				else
				{
					nodeDisplayMode = nodeDisplayMode.removeShortNodeContents();
				}
				refreshAllTreeImages();
			}
			else if (e.getItemSelectable() == cpe.getShowSearchDetailsMenuItem())
			{
				redrawAllSearchTextBoxes();
			}
			else if (e.getItemSelectable() == cpe.getUseBWTableColorsMenuItem())
			{
				redrawAllSearchTextBoxes();
			}
//			else if (e.getItemSelectable() == cpe.getUseF1ClassifierMenuItem())
//			{
//				if (this.underLyingSystem!=null) throw new VisualTracingToolException("Unexpected: underLyingSystem has been constructed, while Menu-item of F1 classifier is yet enabled.");
//				if (e.getStateChange() == ItemEvent.SELECTED)
//				{
//					this.useF1Classifier = true;
//					cpe.setStatusBarClassifierType(VisualTracingTool.STATUS_BAR_CLASSIFIER_TYPE_F1_OPTIMIZED);
//				}
//				else
//				{
//					this.useF1Classifier = false;
//					cpe.setStatusBarClassifierType(VisualTracingTool.STATUS_BAR_CLASSIFIER_TYPE_ACCURACY_OPTIMIZED);
//				}
//				cpe.updateStatusBarLabel();
//			}
			else if (e.getItemSelectable() == cpe.getUseOldBeamSearchMenuItem())
			{
				if (e.getStateChange() == ItemEvent.SELECTED)
				{
					cpe.getStatusBarStates().add(StatusBarState.OLD_BEAM);
				}
				else
				{
					cpe.getStatusBarStates().remove(StatusBarState.OLD_BEAM);
				}
				cpe.updateStatusBarLabel();
			}
		}
		catch (Exception ex)
		{
			handleError(ex, true);
		}
	}
	
	
	private void redrawRegularSearchTreeWithOriginalTree() throws VisualTracingToolException {
		String imageTitle = ORIGINAL_TREE_TITLE;
		createAndDrawImageInComponent( originalTreeOfRegularSearch, null, imageTitle, cpe.getRegularSearchImagePane());
		
	}

	private void redrawRegularSeachTreeAfterUpdatingTheComponentIndex() throws VisualTracingToolException
	{
		TreeHistoryComponent component = historyComponents.get(historyComponentsIndex);
		String imageTitle = makeTreeImageTitle(historyComponentsIndex, component.getSpecification().toString());
		createAndDrawImageInComponent(component.getTree(), component.getAffectedNodes(), imageTitle, cpe.getRegularSearchImagePane());
	}



	public void stateChanged(ChangeEvent e) {}

	//
	// WindowListener methods:
	//
	
	public void windowClosing(WindowEvent e)
	{
		cpe.getMainFrame().dispose();
	}

	public void windowClosed(WindowEvent e)
	{
		performExit();
	}

	public void windowOpened(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	
	//
	// MouseListener methods
	//
	@Override
	public void mouseClicked(MouseEvent e)
	{
		// get double clicks on the Existing Trees table
		if (e.getSource() == cpe.getExistingTreesTable() &&  e.getComponent().isEnabled() && e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2)
		{
			Point p = e.getPoint();
			int row = cpe.getExistingTreesTable().rowAtPoint(p); 
			try {
				generateTableForSelectedRow(row);
			} catch (Exception ex) {
				handleError(ex, true);
			}
		}
		// get clicks on the text boxes and erase initial texts
		else if (e.getSource() == cpe.getTextTextArea())
		{
			if (cpe.getTextTextArea().getText().equals(VisualTracingTool.INITIAL_TEXT_TEXT))
				cpe.getTextTextArea().setText("");
		}
		else if (e.getSource() == cpe.getHypothesisTextArea())
		{
			if (cpe.getHypothesisTextArea().getText().equals(VisualTracingTool.INITIAL_HYPO_TEXT))
				cpe.getHypothesisTextArea().setText("");
		}
	}
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}

	//
	//	MouseWheelEvent methods
	//
	@Override
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		if (e.isControlDown())
        {
			// This method handles zooming-in and zooming-out the images of
			// the parse-trees.
			
			// the new zoom ratio is proportional to the number of wheel clicks
			double newZoomRatio =  masterZoomRatio - e.getWheelRotation()*MOUSE_WHEEL_SENSITIVITY_COEFICIENT;
			setNewImageZoomRatio(newZoomRatio);
			e.consume();
        }
	}
	
	//
	// KeyListener methods
	//
	@Override
	public void keyTyped(KeyEvent e)
	{
		if (e.isControlDown() && ( e.getKeyChar() == '+' || e.getKeyChar() == '-'))
		{
			// This method handles zooming-in and zooming-out the images of
			// the parse-trees.

			// the new zoom ratio
			double newZoomRatio = masterZoomRatio +  (e.getKeyChar() == '+' ? 1 : -1)*MOUSE_WHEEL_SENSITIVITY_COEFICIENT;
			setNewImageZoomRatio(newZoomRatio);
			e.consume();
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e) {}
	@Override
	public void keyReleased(KeyEvent e) {}

	
	public void zoomIn()
	{
		setNewImageZoomRatio(masterZoomRatio + MOUSE_WHEEL_SENSITIVITY_COEFICIENT);
	}
	public void zoomOut()
	{
		setNewImageZoomRatio(masterZoomRatio - MOUSE_WHEEL_SENSITIVITY_COEFICIENT);
	}
	
	

	
	public GuiUtils getGuiUtils()
	{
		return guiUtils;
	}
	
	public double getMasterZoomRatio()
	{
		return masterZoomRatio;
	}


	/**
	 * This method is called from methods that handle the event of ztrl+mouse-wheel,
	 * or ctrl+"+" or ctrl+"-".
	 * This method changes the size - the zoom - of the parse-trees images displayed
	 * on the automatic and manual mode.
	 *  
	 * the new zoom ratio must be within [0, 1]
	 * @param newZoomRatio
	 */
	private void setNewImageZoomRatio(double newZoomRatio)
	{
		if (newZoomRatio > 1)
			newZoomRatio = 1;
		else if (newZoomRatio <= MOUSE_WHEEL_SENSITIVITY_COEFICIENT)
			newZoomRatio = MOUSE_WHEEL_SENSITIVITY_COEFICIENT;
			
		if (newZoomRatio != masterZoomRatio)
		{
			masterZoomRatio = newZoomRatio;
			try {
				zoomAndRedrawAllImageComponents(newZoomRatio);
			} catch (Exception ex) {
				handleError(ex, true);
			}
		}
	}
	

	private String makeTreeImageTitle(int stepNo, String stepDescription) {
		return "step " + (1 + stepNo) + ": " + stepDescription;
	}
	
	/**
	 * @throws VisualTracingToolException 
	 * @throws ClassifierException 
	 * @throws BadLocationException 
	 * @throws TeEngineMlException 
	 * @throws TreeAndParentMapException 
	 * 
	 */
	private void redrawAllSearchTextBoxes() throws ClassifierException, VisualTracingToolException, BadLocationException, TeEngineMlException, TreeAndParentMapException {
		redrawRegularSearchTextArea();
		redrawCurrentSingleTreePane();
	}

	/**
	 * redraw the tree and text in the "Selected Tree" pane
	 * @throws ClassifierException
	 * @throws VisualTracingToolException
	 * @throws TeEngineMlException 
	 * @throws TreeAndParentMapException 
	 */
	private void redrawCurrentSingleTreePane() throws ClassifierException, VisualTracingToolException, TeEngineMlException, TreeAndParentMapException {
		// double check
		if ((underLyingSystem != null) && (currentDisplayedTreeComponent != null) && (selectedTreeNextStack != null))
		{
			// calc the index of the current component in the sequence of operations the user had selected in the "Existing Trees" pane
			SingleTreeComponent firstComponent = selectedTreeNextStack.isEmpty() ? currentDisplayedTreeComponent : selectedTreeNextStack.firstElement();
			int ndxOfCurrentTreeComponent = firstComponent.getHistory().getComponents().size() - selectedTreeNextStack.size() -1;
			displaySingleTree(firstComponent, currentDisplayedTreeComponent, ndxOfCurrentTreeComponent);
		}
	}
	
	/**
	 * call all the {@link #stateChanged(ChangeEvent)} and other methods that call {@link #drawTextImage(ExtendedNode, String)}
	 * 
	 * @throws VisualTracingToolException
	 * @throws ClassifierException
	 * @throws TeEngineMlException
	 * @throws IOException 
	 * @throws TreeAndParentMapException 
	 */
	private void refreshAllTreeImages() throws VisualTracingToolException, ClassifierException, TeEngineMlException, IOException, TreeAndParentMapException {
		if ( (this.underLyingSystem!=null) && (this.pairData!=null) )
		{
			redrawTextImageWithCurrentComboBoxSelection();

			// redraw the hypo tree
			createAndDrawHypoTreeImage();

			// redraw the tree and text in the "Selected Tree" pane
			redrawCurrentSingleTreePane();

			// fire the "previous button of the regular search pane
			if (historyComponents != null) {
				if (historyComponentsIndex == historyComponents.size()) // if we are after clicking the "Perform Regular Search" button but before clicking Next/Previous
				{
					createAndDrawImageInComponent(bestTreeOfRegularSearch.getTree(), null, bestTreeSentenceOfRegularSearch, cpe.getRegularSearchImagePane());
				} else // if the regular search Previous/Next button has already been clicked, and the image changed once
				{
					historyComponentsIndex++;
					actionPerformed(new ActionEvent(this, 0, VisualTracingTool.COMMAND_REGUALR_SEARCH_PREVIOUS));
				}
			}
		}
	}
	/**
	 * redraw the text area
	 * @throws VisualTracingToolException 
	 * @throws ClassifierException 
	 * @throws BadLocationException 
	 * @throws GapException 
	 * @throws TreeAndParentMapException 
	 * 
	 */
	private void redrawRegularSearchTextArea() throws ClassifierException, VisualTracingToolException, BadLocationException, GapException, TreeAndParentMapException {
		if (historyComponents != null) {
			String textAreaText = buildRegularSearchTextAreaText(true,this.bestTreeOfRegularSearch,
					classificationScoreForPredictions, classificationScoreForSearch, this.bestTreeHistory.getInitialComponent(), historyComponents, 
					historyComponentsIndex, sortedHistory, originalTreeSentence, true,null);
			int caretPositionBeforeUpdate = cpe.getRegularSearchTextArea().getCaretPosition();
			cpe.getRegularSearchTextArea().setContentType("text/html");
			cpe.getRegularSearchTextArea().setText(textAreaText);
			cpe.getRegularSearchTextArea().setCaretPosition(caretPositionBeforeUpdate);
		}
	}
	
	/**
	 * Resize and redraw all tree images
	 * 
	 * @param zoomRatio
	 * @throws IOException
	 */
	private void zoomAndRedrawAllImageComponents(double zoomRatio) throws VisualTracingToolException {
		for (JScrollPane imageScrollPane : cpe.getMapImagePanesToImagePaintingComponents().keySet())
		{
			ImagePaintingComponent imagePaintingComponent = cpe.getMapImagePanesToImagePaintingComponents().get(imageScrollPane);
			imagePaintingComponent.setZoomRatio(zoomRatio);
			drawImageInComponent(imagePaintingComponent, imageScrollPane);
		}
	}

	private void redrawTextImageWithCurrentComboBoxSelection() throws VisualTracingToolException {
		ExtendedNode tree = underLyingSystem.getOriginalTextTrees().get(textTreesComboBoxSelection);
		String sentence = underLyingSystem.getMapTreeToSentence().get(tree);
		drawTextImage(tree, sentence);
	}

	private void createAndDrawHypoTreeImage() throws VisualTracingToolException {
		createAndDrawImageInComponent(underLyingSystem.getHypothesisTree(), null, pairData.getPair().getHypothesis(), cpe.getHypothesisImagePane());
	}

	private void drawTextImage(ExtendedNode tree, String sentence) throws VisualTracingToolException {
		createAndDrawImageInComponent(tree, null, sentence, cpe.getTextImagePane());
	}

	private void createAndDrawImageInComponent(ExtendedNode tree, Set<ExtendedNode> affectedNodes, String sentence, JScrollPane paneOfComponent) 
			throws VisualTracingToolException {
		BufferedImage image = guiUtils.createImage(tree, sentence, affectedNodes);
		ImagePaintingComponent imageComponent = cpe.getMapImagePanesToImagePaintingComponents().get(paneOfComponent);
		imageComponent.setFullSizeImage(image);
		drawImageInComponent(imageComponent, paneOfComponent);
	}

	private void drawImageInComponent(ImagePaintingComponent component, JScrollPane panelOfComponent) {
		BufferedImage image = component.getImage();
		if (image != null) {
			component.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
			cpe.getMainFrame().repaint();
			//component.repaint();
			panelOfComponent.setViewportView(component);
		}

	}

	



	private void setTreesTableData() throws VisualTracingToolException {
		if (underLyingSystem != null) {
			Vector<Integer> columnsWidths = new Vector<Integer>();
			for (int colInd = 0; colInd < cpe.getExistingTreesTable().getColumnModel().getColumnCount(); ++colInd) {
				columnsWidths.add(cpe.getExistingTreesTable().getColumnModel().getColumn(colInd).getPreferredWidth());
			}

			Vector<Vector<Object>> data = new Vector<Vector<Object>>();
			final List<SingleTreeComponent> componentsOfExistingTreesTable;
			if (this.displayOnlyLastGenerated)
				componentsOfExistingTreesTable = underLyingSystem.getLastRequestGeneratedTrees();
			else
				componentsOfExistingTreesTable = underLyingSystem.getAllTrees();

			for (SingleTreeComponent treeComponent : componentsOfExistingTreesTable) {
				Vector<Object> rowVector = new Vector<Object>();
				// iterate the column headers in their display order
				for (ColumnNames columnName : ColumnNames.values())
					switch (columnName)
					{
					case OPERATION_ID:
						rowVector.add(treeComponent.getId());
						break;
					case ORIGINAL_SENTENCE:
						rowVector.add(treeComponent.getOriginalSentenceNo());
						break;
					case LAST_OPERATION:
						Specification spec = treeComponent.getLastSpec();
						rowVector.add(spec != null ? spec.toShortString() :  SPEC_OF_INITIAL_OPERATION + treeComponent.getOriginalSentenceNo());
						break;
					case ITERATION:
						rowVector.add(treeComponent.getIterationNumber());
						break;
					case MISSING_RELATIONS:
						rowVector.add(treeComponent.getMissingRelations().size());
						break;
					case PROOF_COST:
						rowVector.add(strDouble(treeComponent.getCost()));
						break;
					case OPERATION_COST:
						rowVector.add(strDoubleObj(GuiUtils.getCostOfLastOperation(treeComponent)));
						break;
					case PREDICTIONS_SCORE:
						rowVector.add(strDouble(treeComponent.getClassificationScoreForPredictions()));
						break;
					case CLASSIFICATION_SCORE:
						rowVector.add(strDouble(treeComponent.getClassificationScoreForSearch()));
						break;
					default:
						throw new VisualTracingToolException("This Column Name is unknown in setTreesTableData(): " + columnName);
					}
				data.add(rowVector);
			}

			setTooltipsOfExistingTreesTable(componentsOfExistingTreesTable); // set up the tool tips over the "Available Operations" column

			@SuppressWarnings("serial")
			DefaultTableModel model = new DefaultTableModel(data, ColumnNames.valuesVector())	{	//TREES_TABLES_COLUMN_NAMES) {
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			};
			cpe.getExistingTreesTable().setModel(model);
			
			CustomHeaderRenderer.setHeaderRendererToJTable(cpe.getExistingTreesTable());
			
			try {
				for (int colInd = 0; colInd < cpe.getExistingTreesTable().getColumnModel().getColumnCount(); ++colInd) {
					cpe.getExistingTreesTable().getColumnModel().getColumn(colInd).setPreferredWidth(columnsWidths.get(colInd));
				}

			} catch (Exception ex) {
				handleError(new VisualTracingToolException("Cannot restore the preferred width of table\'s columns. See nested exception", ex), true);
			}

		}
	}


	/**
	 * set up the tool tips for the table
	 */
	private void setTooltipsOfExistingTreesTable(final List<SingleTreeComponent> componentsOfExistingTreesTable) {
		cpe.getExistingTreesTable().addMouseMotionListener(
				new MouseMotionAdapter() {
					public void mouseMoved(MouseEvent e) {
						Point p = e.getPoint();
						int row = cpe.getExistingTreesTable().rowAtPoint(p);
						int column = cpe.getExistingTreesTable().columnAtPoint(p);

						String toolTipText = null;
						if (column == ColumnNames.OPERATION_ID.ordinal())	//  VisualTracingTool.COLUMN_NAMES.indexOf(VisualTracingTool.COL_OPERATION_ID))
							toolTipText = "Unique ID for this operation";
						else if (column == ColumnNames.LAST_OPERATION.ordinal())
						{
							if (row < componentsOfExistingTreesTable.size() 
								&& componentsOfExistingTreesTable.get(row) != null 
								&& componentsOfExistingTreesTable.get(row).getLastSpec() != null)
								toolTipText = componentsOfExistingTreesTable.get(row).getLastSpec().toString();
							// toolTipText = componentsOfExistingTreesTable.get(row).getLastSpec().toString();
						}
						else if (column == ColumnNames.ORIGINAL_SENTENCE.ordinal())
						{
							if (row < componentsOfExistingTreesTable.size())
							{
								int origSentenceNo = componentsOfExistingTreesTable.get(row).getOriginalSentenceNo();
								toolTipText = comboBoxItems.get(origSentenceNo-1);
							}
						}
						else if (column == ColumnNames.PROOF_COST.ordinal())
							toolTipText = "Cost of the entire proof so far";
						else if (column == ColumnNames.OPERATION_COST.ordinal())
							toolTipText = "Cost of this operation alone";
						else if (column == ColumnNames.ITERATION.ordinal())
							toolTipText = "Place of this operation within the proof";
						else if (column == ColumnNames.MISSING_RELATIONS.ordinal())
							toolTipText = "Amount of hypothesis elements that are missing in the text";
						else if (column == ColumnNames.PREDICTIONS_SCORE.ordinal())
							toolTipText = "Classification score for predictions";
						else if (column == ColumnNames.CLASSIFICATION_SCORE.ordinal())
							toolTipText = "Classification score for search";
						
						cpe.getExistingTreesTable().setToolTipText(GuiUtils.htmlizeToolTipText(toolTipText));
					}

				}); // end MouseMotionAdapter

		// keep tooltips visible forever
		ToolTipManager.sharedInstance().setDismissDelay(1000000);
	}
	


	/**
	 * also called from {@link #displaySelectedTree()}
	 * 
	 * @param lastSingleTreeComponent
	 * @param currentComponentIndex
	 * @param currentTreeComponent
	 * @throws VisualTracingToolException
	 * @throws ClassifierException
	 * @throws TeEngineMlException 
	 * @throws TreeAndParentMapException 
	 */
	private void displaySingleTree(SingleTreeComponent lastSingleTreeComponent, SingleTreeComponent currentTreeComponent, int currentComponentIndex)
			throws VisualTracingToolException, ClassifierException, TeEngineMlException, TreeAndParentMapException {
		
		String imageTitle = (currentTreeComponent != null && currentTreeComponent.getLastSpec() != null) ? 
				makeTreeImageTitle(currentComponentIndex, currentTreeComponent.getLastSpec().toString()) : ORIGINAL_TREE_TITLE;	
		// component.getSpecification().toString());
		createAndDrawImageInComponent(currentTreeComponent.getTree(), currentTreeComponent.getAffectedNodes(), 
				imageTitle, cpe.getSelectedTreeImagePane());
//				underLyingSystem.getMapTreeToSentence().get(currentTreeComponent.getTree()), cpe.getSelectedTreeImagePane());

		double classificationScoreForPredictions = lastSingleTreeComponent.getClassificationScoreForPredictions();
		double classificationScoreForSearch = lastSingleTreeComponent.getClassificationScoreForSearch();

		// prepare stuff for displaying the specifications/operations of this tree history
		SorterOfTreeHistory sortedHistory = new SorterOfTreeHistory(lastSingleTreeComponent.getHistory(), underLyingSystem.getClassifier(),
				lastSingleTreeComponent.getFeatureVector());
		TreeAndFeatureVector treeAndFeatureVector = new TreeAndFeatureVector(lastSingleTreeComponent.getTree(), lastSingleTreeComponent.getFeatureVector());
		String selectedTreeTextAreaText = buildRegularSearchTextAreaText(false,treeAndFeatureVector,
				classificationScoreForPredictions, classificationScoreForSearch,
				lastSingleTreeComponent.getHistory().getInitialComponent(),
				lastSingleTreeComponent.getHistory().getComponents(), currentComponentIndex, sortedHistory, 
				underLyingSystem.getMapTreeToSentence().get(lastSingleTreeComponent.getTree()),false,currentTreeComponent.getMissingRelations());

		//StringBuffer sb = new StringBuffer();
		//sb.append(selectedTreeTextAreaText);
		//sb.append(buildMissingRelationText(currentTreeComponent.getMissingRelations()));

		int caretPositionBeforeUpdate = 0; // this will up the scroll pane to the top //cpe.getSelectedTreeTextArea().getCaretPosition();
		cpe.getSelectedTreeTextArea().setContentType("text/html");
		cpe.getSelectedTreeTextArea().setText(selectedTreeTextAreaText);
		cpe.getSelectedTreeTextArea().setCaretPosition(caretPositionBeforeUpdate);
	}



	/**
	 * responds to the "Display Selected Tree" button
	 * 
	 * @throws VisualTracingToolException
	 * @throws ClassifierException
	 * @throws TeEngineMlException 
	 * @throws TreeAndParentMapException 
	 */
	private void displaySelectedTree() throws VisualTracingToolException, ClassifierException, TeEngineMlException, TreeAndParentMapException {
		if (underLyingSystem != null) { 
			this.selectedTreeNextStack = new Stack<SingleTreeComponent>();
			int selectedRow = cpe.getExistingTreesTable().getSelectedRow();
			if (selectedRow >= 0) {
				List<SingleTreeComponent> listOfTable;
				if (displayOnlyLastGenerated) {
					listOfTable = underLyingSystem.getLastRequestGeneratedTrees();
				} else {
					listOfTable = underLyingSystem.getAllTrees();
				}
				if (selectedRow >= listOfTable.size())
					throw new VisualTracingToolException("Impossible selectedRow " + selectedRow + ", when list size is " + listOfTable.size());
				SingleTreeComponent treeComponent = listOfTable.get(selectedRow);

				if (!isTheInitialSingleTreeComponent(treeComponent)) {
					currentDisplayedTreeComponent = treeComponent;
					displaySingleTree(treeComponent, treeComponent, treeComponent.getHistory().getComponents().size()-1);
					// imitate a click on the "Selected Tree" tab
					cpe.getTabsPane().setSelectedComponent( cpe.getCurrentTreeInformationPanel());
				}

			} else {
				cleanSelectedTreeDisplay();
			}
		}
	}

	/**
	 * true IFF this is the initial (iteration 0) tree component
	 * 
	 * @param treeComponent
	 * @return
	 */
	private boolean isTheInitialSingleTreeComponent(
			SingleTreeComponent treeComponent) {
		return treeComponent.getIterationNumber() == 0;
	}

	private void cleanSelectedTreeDisplay() {
		ImagePaintingComponent imageComponent = cpe.getMapImagePanesToImagePaintingComponents().get(cpe.getSelectedTreeImagePane());
		imageComponent.setFullSizeImage(null);
		cpe.getMainFrame().repaint();
		cpe.getSelectedTreeImagePane().setViewportView(imageComponent);
		cpe.getSelectedTreeTextArea().setText("");
	}

	private void generateTableForSelectedRow(int selectedRow) throws VisualTracingToolException, TeEngineMlException, OperationException, ScriptException,
			RuleBaseException, TreeAndParentMapException, ClassifierException 
	{
		if (underLyingSystem != null) {
//			int selectedRow = cpe.getExistingTreesTable().getSelectedRow();
			if (selectedRow >= 0) {
				List<SingleTreeComponent> listOfTable;
				if (displayOnlyLastGenerated) {
					listOfTable = underLyingSystem.getLastRequestGeneratedTrees();
				} else {
					listOfTable = underLyingSystem.getAllTrees();
				}
				if (selectedRow >= listOfTable.size())
					throw new VisualTracingToolException("Impossible selectedRow " + selectedRow + ", when list size is " + listOfTable.size());
				SingleTreeComponent treeComponent = listOfTable.get(selectedRow);
				underLyingSystem.generateTreesFor(treeComponent.getId());
				setTreesTableData();
				boldFaceSelectedRow(treeComponent.getId());
				cleanSelectedTreeDisplay();
			}
		}
	}

	/**
	 * Set the selected row boldface
	 * @param operationId
	 */
	@SuppressWarnings("serial")
	private void boldFaceSelectedRow(final Integer operationId) {
		
		cpe.getExistingTreesTable().setDefaultRenderer(Object.class, new  DefaultTableCellRenderer() {  
			   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {  
			     JLabel parent = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);  
			     if(table.getModel().getValueAt(row, ColumnNames.OPERATION_ID.ordinal()) == operationId) 
			    	 parent.setFont(parent.getFont().deriveFont(Font.BOLD));  
			     return parent;  
			   }      
			}); 
		
	}

	private void thPairPreProcess() throws NumberFormatException,
			ConfigurationFileDuplicateKeyException, ConfigurationException,
			TeEngineMlException, ParserRunException,
			NamedEntityRecognizerException, TextPreprocessorException,
			SentenceSplitterException, CoreferenceResolutionException,
			TreeCoreferenceInformationException, TreeStringGeneratorException, VisualTracingToolException, AnnotatorException {
		
		try{this.recentPairsSaver.add(cpe.getTextTextArea().getText(), cpe.getHypothesisTextArea().getText(),cpe.getComboBoxTaskNames().getSelectedItem().toString());}
		catch(RTEMainWriterException e){throw new VisualTracingToolException("recent saver failed.",e);}
		
		if (null == preProcessor) {
			setStatusBarSystemState("Constructing pre-process instruments");
			preProcessor = new SinglePairPreProcessor(cpe.getTextTextArea().getText(), cpe.getHypothesisTextArea().getText(), cpe.getConfigurationFileName(),
					new StatusBarShortMessageFire(), underLyingSystem.getTeSystemEnvironment());
		} else {
			preProcessor.setTextAndHypothesis(cpe.getTextTextArea().getText(), cpe.getHypothesisTextArea().getText());
		}
		preProcessor.setTaskName(cpe.getComboBoxTaskNames().getSelectedItem().toString());
		preProcessor.preprocess();
		pairData = preProcessor.getPairData();
		final StringBuffer textInCookedTextArea = new StringBuffer();
		textInCookedTextArea.append(HTML_FORMATTED_BODY_MARK).append('\n');
		textInCookedTextArea.append("<ol>\n");		// "ordered list"
		for (ExtendedNode tree : pairData.getTextTrees()) {
			String sentence = pairData.getMapTreesToSentences().get(tree);
			textInCookedTextArea.append("<li>");	// starting from "1"
			textInCookedTextArea.append(sentence);
			textInCookedTextArea.append("\n");
		}
		textInCookedTextArea.append("</ol>\n");
		invokeLater(new Runnable() {
			public void run() {
				try {
					cpe.getTextCookedTextLabel().setText(textInCookedTextArea.toString());
				} catch (Exception ex) {
					handleError(ex, false);
				}
			}
		});

	}

	public void preProcessByRteSumUtils() throws VisualTracingToolException,
			NumberFormatException, ConfigurationFileDuplicateKeyException,
			ConfigurationException, TeEngineMlException, ParserRunException,
			NamedEntityRecognizerException, TextPreprocessorException,
			SentenceSplitterException, CoreferenceResolutionException,
			TreeCoreferenceInformationException, TreeStringGeneratorException, AnnotatorException {
		if (!this.cpe.getSumUtilities().isSumDatasetSentenceSelected())
			throw new VisualTracingToolException("Bug: no pair selected in sum utilities");

		setStatusBarSystemState("Retrieving topic from serialized preprocessed data-set");
		final ExtendedPreprocessedTopicDataSet extendedTopic = cpe.getSumUtilities().getExtendedPreprocessedTopicDataSet(this.underLyingSystem.getTeSystemEnvironment());
		final String textAreaOriginalText = cpe.getTextTextArea().getText();

		setStatusBarSystemState("Starting pre-processing");
		thPairPreProcess(); // to pre-process the hypothesis
		
		setStatusBarSystemState("Retrieving text from serialized preprocessed data-set");
		GuiRteSumUtilities sumUtils = this.cpe.getSumUtilities();
		String documentId = sumUtils.getSentenceID().getDocumentId();
		Integer sentenceIndex = Integer.valueOf(sumUtils.getSentenceID().getSentenceId());
		String textString = extendedTopic.getTopicDataSet().getDocumentsMap().get(documentId).get(sentenceIndex);
		String hypothesisString = cpe.getHypothesisTextArea().getText();
		ExtendedNode textTree = extendedTopic.getDocumentsTreesMap().get(documentId).get(sentenceIndex);
		List<ExtendedNode> textTreeAsList = new SingleItemList<ExtendedNode>( extendedTopic.getDocumentsTreesMap().get(documentId).get(sentenceIndex));
		ExtendedNode hypothesisTree = this.pairData.getHypothesisTree();
		TextHypothesisPair textHypothesisPair = new TextHypothesisPair(textString, hypothesisString, 1, cpe.getComboBoxTaskNames().getSelectedItem().toString());
		Map<ExtendedNode, String> mapTextTreeToSentence = new LinkedHashMap<ExtendedNode, String>();
		mapTextTreeToSentence.put(textTree, textString);
		TreeCoreferenceInformation<ExtendedNode> coreferenceInformation = extendedTopic.getCoreferenceInformation().get(documentId);
		if (null == coreferenceInformation)
			throw new VisualTracingToolException("null coreference information");

		this.pairData = new ExtendedPairData(textHypothesisPair, textTreeAsList, hypothesisTree, mapTextTreeToSentence, coreferenceInformation);
		invokeLater(new Runnable() {
			public void run() {
				try {
					cpe.getTextTextArea().setText(textAreaOriginalText);
				} catch (Exception ex) {
					handleError(ex, false);
				}
			}
		});
	}

	private void thPairGo() throws NumberFormatException, ConfigurationFileDuplicateKeyException, ConfigurationException,  ParserRunException,
			NamedEntityRecognizerException, TextPreprocessorException,SentenceSplitterException, CoreferenceResolutionException,
			TreeCoreferenceInformationException, LemmatizerException,
			OperationException, ClassifierException, IOException,
			ClassNotFoundException, TreeAndParentMapException,
			VisualTracingToolException, TreeStringGeneratorException,
			AnnotatorException, PluginAdministrationException, TeEngineMlException 
	{
		if (null==this.recentPairsSaver)
		{
			try{this.recentPairsSaver = new RecentPairsSaver();}
			catch(RTEMainReaderException e){throw new VisualTracingToolException("recent saver failed.",e);}
		}
		if ((cpe.getPairsUtilities().getSelectedPairIndex() != null) && (cpe.getSumUtilities().isSumDatasetSentenceSelected()))
			throw new VisualTracingToolException("System was set to pairs mode and sum mode together, which is invalid. One should be discarded.");
		
		
		// Entailment recognition

		// First step: Create all components required
		// for Entailment of all pairs (e.g. lemmatizer, classifier).
		// These components will be used for all T-H pairs,
		// not only the current one.
		// This is done by the underlying system, either
		// when it is initialized, and some of them also when calling its method
		// setPair().
		//
		// Second step: Create all data-structures required for
		// RTE of a single T-H pair. This is done by the
		// underlying system, which creates a
		// TreesGeneratorOneIterationSingleTree
		// that inherits
		// ac.biu.nlp.nlp.engineml.rteflow.macro.InitializationTextTreesProcessor
		// and calls its init() method.
		//
		// Third step: the underlying system calls its method
		// "initTreesComponents" which performs all operations
		// that can be performed on the original trees (the
		// first step of the proof).


		// Construct the underlying system, if it has not yet been constructed.
		if (null == underLyingSystem)
		{
			throw new VisualTracingToolException("BUG: It seems that underlying system was not constructed or not set.");
		}

		
		// pre-processing: sets this.pairData
		if (cpe.getSumUtilities().isSumDatasetSentenceSelected()) {
			preProcessByRteSumUtils();
		} else {
			thPairPreProcess();
		}

		if (cpe.isDatasetNamesAllow())
		{
			pairData = new ExtendedPairData(pairData.getPair(), pairData.getTextTrees(), pairData.getHypothesisTree(), pairData.getMapTreesToSentences(), pairData.getCoreferenceInformation(), cpe.getComboBoxDatasetNames().getSelectedItem().toString());
		}
		
		
		// Set the pair in the underlying system. The system then can
		// find all the transformations that can be applied on the text tree,
		// apply them (and generated new intermediate tree), and continue the proof construction.
		setStatusBarSystemState("Setting pair in underlying system");
		underLyingSystem.setPair(pairData,cpe.getComboBoxTaskNames().getSelectedItem().toString());
		
		// Display the parse trees in the tabs of "text trees" and "hypothesis tree"
		final List<ExtendedNode> originalTextTrees = underLyingSystem.getOriginalTextTrees();
		if (originalTextTrees.size() < 1)
			throw new VisualTracingToolException("No text trees!");
		if (logger.isDebugEnabled()) {
			logger.debug("Number of text trees = " + originalTextTrees.size());
		}

		comboBoxItems = makeOriginalSentencesPrefixes();
		// Draw the parse-trees for the text and the hypothesis
		invokeLater(new Runnable() {
			public void run() {
				try {
					initTextComboBox(comboBoxItems);		// and draw text tree
					createAndDrawHypoTreeImage();

					// As for Entailment, all operations that could be done
					// in a single operation on the original tree (the
					// first step in the proof) were already performed (by
					// the underlying system's method initTreesComponents() ).
					// Put those trees in the table.
					setTreesTableData();
				} catch (Exception ex) {
					handleError(ex, false);
				}
			}
		});
	}

	/**
	 * 
	 */
	protected void initTextComboBox(Vector<String> comboBoxItems) {
		cpe.getOrigTreesComboBox().setModel(new DefaultComboBoxModel<String>(comboBoxItems));
		textTreesComboBoxSelection = -1;
		cpe.getOrigTreesComboBox().setSelectedIndex(0);			// reset the text pane spinner

	}

	/**
	 * Used to display the beginning of each text-sentence in the combo
	 * box of the text-parse-trees, in the tab "Text Trees".
	 * @return A vector with the beginning of every sentence of the text.
	 */
	private Vector<String> makeOriginalSentencesPrefixes() {
		Vector<String> comboBoxItems = new Vector<String>(pairData.getMapTreesToSentences().values().size());
		int i = 1;
		for (String textSentence : pairData.getMapTreesToSentences().values())
		{
			String prefix = textSentence.length() >= TEXT_COMBOBOX_ITEM_LENGTH ? textSentence.substring(0, TEXT_COMBOBOX_ITEM_LENGTH) + "..." : textSentence;
			comboBoxItems.add(i++ + ": " + prefix);
		}
		return comboBoxItems;
	}

	private class ThPairGoRunnable implements Runnable {
		public void run() {
			try {
				thPairGo();
			} catch (Exception ex) {
				handleError(ex, true);
			} finally {
				invokeLater(new Runnable() {
					public void run() {
						try {
							// cpe.getButtonGo().setText(CustomProofEngine.BUTTON_GO_TEXT);
							cpe.enableAll();
							cpe.getMainFrame().setCursor(null);
							cpe.setStatusBarSystemState(VisualTracingTool.READY_STATUS_BAR_SYSTEM_STATE);
							cpe.updateStatusBarLabel();
						} catch (Exception ex) {
							handleError(ex, false);
						}
					}
				});
			}
		}
	}

	/**
	 * responds to the "Perform Regular Search" button
	 * 
	 * @throws VisualTracingToolException
	 * @throws TeEngineMlException
	 * @throws OperationException
	 * @throws ClassifierException
	 * @throws ScriptException
	 * @throws RuleBaseException
	 * @throws AnnotatorException
	 */
	private void regularSearch() throws VisualTracingToolException, TeEngineMlException, OperationException, ClassifierException,
			ScriptException, RuleBaseException, AnnotatorException {
		if (underLyingSystem != null) {
			PairProcessor processor = underLyingSystem.getRegularPairProcessor(cpe.getUseOldBeamSearchMenuItem().isSelected());
			processor.setProgressFire(createRegularSearchProgressFire());
			invokeLater(new Runnable(){public void run(){cpe.getProgressIndicatorInRegularSearch().setStringPainted(true);}});
			
			processor.process();
			
			invokeLater(new Runnable(){public void run(){
				cpe.getProgressIndicatorInRegularSearch().setValue(100);
				cpe.getProgressIndicatorInRegularSearch().setStringPainted(false);
				}});
			this.bestTreeOfRegularSearch = processor.getBestTree();
			this.bestTreeSentenceOfRegularSearch = processor.getBestTreeSentence();
			this.bestTreeHistory = processor.getBestTreeHistory();
			this.historyComponents = processor.getBestTreeHistory().getComponents();
			this.historyComponentsIndex = historyComponents.size();

			originalTreeOfRegularSearch = getKeyOf(processor.getOriginalTreesAfterInitialization().getOriginalMapTreesToSentences(), processor.getBestTreeSentence());
			if (null == originalTreeOfRegularSearch)
				throw new VisualTracingToolException("Could not find the original tree which corresponds to the setnence: \"" + processor.getBestTreeSentence() + "\"");

			classificationScoreForPredictions = underLyingSystem.getClassifierForPredictions().classify(processor.getBestTree().getFeatureVector());
			classificationScoreForSearch = underLyingSystem.getClassifier().classify(processor.getBestTree().getFeatureVector());
			
			// prepare stuff for displaying the specifications/operation of this tree history
			sortedHistory = new SorterOfTreeHistory(processor.getBestTreeHistory(), underLyingSystem.getClassifier(), processor.getBestTree().getFeatureVector());
			originalTreeSentence = processor.getBestTreeSentence();
			
			// trigger the Previous button code
			actionPerformed(new ActionEvent(this, 0, VisualTracingTool.COMMAND_REGUALR_SEARCH_PREVIOUS));
			
			cpe.getRegularSearchProgressBar().setStringPainted(true);
			cpe.getRegularSearchProgressBar().setValue((int) (classificationScoreForPredictions * 100));
			
			if (ClassifierUtils.classifierResultToBoolean(classificationScoreForPredictions) == true) {
				cpe.getRegularSearchProgressBar().setForeground( COLOR_FOREGROUND_GOOD	);	// Color.GREEN);
			} else {
				cpe.getRegularSearchProgressBar().setForeground(COLOR_FOREGROUND_BAD	);	// Color.RED);
			}
		} else {
			handleSystemNotInitializedErrorForRegularSearch();
		}
	}

	/**
	 * @param classificationScoreForPredictions
	 * @param historyComponents
	 * @param historyComponentsIndex
	 * @param sortedHistory
	 * @param originalTreeSentence
	 * @param classificationScoreForSearch 
	 * @return
	 * @throws ClassifierException
	 * @throws VisualTracingToolException
	 * @throws GapException 
	 * @throws TreeAndParentMapException 
	 */
	private String buildRegularSearchTextAreaText(
			boolean featureVectorIncludesGap,
			TreeAndFeatureVector treeAndFeatureVector,
			final double classificationScoreForPredictions,  final double classificationScoreForSearch, 
			TreeHistoryComponent initialTreeHistoryComponent,
			ImmutableList<TreeHistoryComponent> historyComponents,
			int historyComponentsIndex, SorterOfTreeHistory sortedHistory,
			String originalTreeSentence, boolean displayWholeProofFeatures,
			Set<ExtendedNode> missingRelations) 
					throws ClassifierException, VisualTracingToolException, GapException, TreeAndParentMapException {
//		if (classificationScoreForPredictions == 0)
//			throw new VisualTracingToolException("Bug. classificationScoreForPredictions is 0. You must first initialize it through regularSearch() or displaySingleTree() and then call this method.");
//		if (historyComponents == null || historyComponents.isEmpty())
//			throw new VisualTracingToolException("Bug. no historyComponents. You must first initialize it through regularSearch() or displaySingleTree() and then call this method.");
//		if (sortedHistory == null)
//			throw new VisualTracingToolException("Bug. no sortedHistory. You must first initialize it through regularSearch() or displaySingleTree() and then call this method.");
		if (historyComponents!=null){if (historyComponents.size()>0){if(null==sortedHistory){throw new VisualTracingToolException("sortedHistory is null");}}}
		if (originalTreeSentence == null)
			throw new VisualTracingToolException("Bug. no originalTreeSentence.");

		boolean gapMode = underLyingSystem.getTeSystemEnvironment().getGapToolBox().isHybridMode();
		double classificationScoreForPredictionsToPrint = classificationScoreForPredictions;
		Map<Integer,Double> featureVectorIncludingGap = null;
		if (gapMode && (!featureVectorIncludesGap))
		{
			featureVectorIncludingGap = underLyingSystem.getGapToolInstances().getGapFeaturesUpdate().updateForGap(new TreeAndParentMap<ExtendedInfo, ExtendedNode>(treeAndFeatureVector.getTree()), treeAndFeatureVector.getFeatureVector(), underLyingSystem.getGapEnvironment());
			classificationScoreForPredictionsToPrint = underLyingSystem.getClassifierForPredictions().classify(featureVectorIncludingGap);
		}
		JEditorPane.registerEditorKitForContentType("text/html", "javax.swing.text.html.HTMLEditorKit");
		StringBuffer sb = new StringBuffer();
		sb.append(HTML_OPEN);
		sb.append(HTML_FORMATTED_BODY_MARK).append('\n');
		sb.append("Classification Score for Predictions = "); // "...  by classifier-for-predictions "
		sb.append(strDouble(classificationScoreForPredictionsToPrint));	
		sb.append("<BR>\n");
		
		if (cpe.getShowSearchDetailsMenuItem().isSelected())	// menu item checkbox
		{
			sb.append("Classification Score for Search ");
			if (gapMode&&featureVectorIncludesGap){sb.append("including gap");}
			sb.append(" = ");
			sb.append(String.format("%-8.10f", classificationScoreForSearch));
			sb.append("<BR>\n");
			if (gapMode&&(!featureVectorIncludesGap))
			{
				double classificationScoreForSearchIncludingGap = underLyingSystem.getClassifier().classify(featureVectorIncludingGap);
				sb.append("Classification Score for Search including gap = ");
				sb.append(String.format("%-8.10f", classificationScoreForSearchIncludingGap));
				sb.append("<BR>\n");
			}
		}

		sb.append("Operations and Costs");
		sb.append("<BR>\n");

		
		
		double previousCost = getCost(initialTreeHistoryComponent.getFeatureVector());
		int componentNo = -1;

		// displaying the specifications/operation of this tree history
		sb.append("<table border=\"1\">\n");
		
		sb.append("<TR>\n");
		sb.append("<TD>\n");
		sb.append("");
		sb.append("</TD>\n");
		sb.append("<TD><B>\n");
		sb.append("#");
		sb.append("</TD>\n");
		sb.append("<TD><B>\n");
		sb.append("Operation Specification");
		sb.append("</TD>\n");
		sb.append("<TD><B>\n");
		sb.append("Operation Cost");
		sb.append("</TD>\n");
		sb.append("<TD><B>\n");
		sb.append("Accumulated Cost");
		sb.append("</TD>\n");
		sb.append("</TR>\n");
		
		// line for original sentence
		sb.append(HTML_OPEN_TABLE_ROW);
		sb.append(HTML_OPEN_COLUMN);
		if (componentNo == historyComponentsIndex)
			sb.append(FONT_STYLE_OF_FOCUSED_TABLE_ROW + RIGHT_ARROW);
		else
			sb.append(ARROW_SPACE);
		sb.append(HTML_CLOSE_COLUMN);
		sb.append(HTML_OPEN_COLUMN);
		if (componentNo == historyComponentsIndex)
			sb.append(FONT_STYLE_OF_FOCUSED_TABLE_ROW);
		sb.append('-');
		sb.append(HTML_CLOSE_COLUMN);
		sb.append(HTML_OPEN_COLUMN);
		if (componentNo == historyComponentsIndex)
			sb.append(FONT_STYLE_OF_FOCUSED_TABLE_ROW);
		sb.append("<I>Original Sentence");
		sb.append(HTML_CLOSE_COLUMN);
		sb.append(HTML_OPEN_COLUMN);
		sb.append("N/A");
		sb.append(HTML_CLOSE_COLUMN);
		sb.append(HTML_OPEN_COLUMN);
		sb.append(strDouble(getCost(initialTreeHistoryComponent.getFeatureVector())));
		sb.append(HTML_CLOSE_COLUMN);
		sb.append("</tr>\n"); // end row
		
		if (historyComponents!=null)
		{
			for (TreeHistoryComponent component : historyComponents) {
				componentNo++;
				sb.append(HTML_OPEN_TABLE_ROW); // new table row

				// arrow column - the arrow points at the row in focus right now
				sb.append(HTML_OPEN_COLUMN);
				if (componentNo == historyComponentsIndex)
					sb.append(FONT_STYLE_OF_FOCUSED_TABLE_ROW + RIGHT_ARROW);
				else
					sb.append(ARROW_SPACE);
				sb.append(HTML_CLOSE_COLUMN);

				// index column
				sb.append(HTML_OPEN_COLUMN);
				if (componentNo == historyComponentsIndex)
					sb.append(FONT_STYLE_OF_FOCUSED_TABLE_ROW);
				sb.append(componentNo+1);
				sb.append(HTML_CLOSE_COLUMN);

				// desc column
				sb.append(HTML_OPEN_COLUMN);
				if (componentNo == historyComponentsIndex)
					sb.append(FONT_STYLE_OF_FOCUSED_TABLE_ROW);
				String specHtmlString = StringUtil.escapeHTML(component.getSpecification().toString());
				sb.append(specHtmlString);
				sb.append(HTML_CLOSE_COLUMN);

				double currentCost = getCost(component.getFeatureVector());
				// cost column
				// the cost of an operation is the difference between the cost of
				// the feature vector of the operation, and the cost of the feature
				// vector before the operation
				sb.append(HTML_OPEN_COLUMN);
				sb.append(getTextColorOfCost(component, sortedHistory));
				sb.append(computeOperationCost(currentCost, previousCost));
				sb.append("</FONT><BR>\n");
				previousCost = currentCost;
				sb.append(HTML_CLOSE_COLUMN);

				sb.append(HTML_OPEN_COLUMN);
				sb.append(strDouble(currentCost));
				sb.append(HTML_CLOSE_COLUMN);

				sb.append("</tr>\n"); // end row
			}
		}

		sb.append("</table>\n");
		sb.append("Original Sentence:");
		sb.append("<BR>\n");
		sb.append(StringUtil.escapeHTML(originalTreeSentence));
		sb.append("<BR>\n");

		
		if(cpe.getShowSearchDetailsMenuItem().isSelected())
		{
			if (missingRelations!=null)
			{
				if (missingRelations.size()>0)
				{
					sb.append("<BR>\n").append(GuiUtils.buildMissingRelationText(missingRelations, this.underLyingSystem.getOriginalTreesAfterInitialization().getHypothesisTreeAndParentMap())).append("\n<NR>\n");
				}
				else
				{
					sb.append("<BR>\nProof Complete.\n<BR>\n");
				}
			}

			TreeHistoryComponent currentComponent = null;
			TreeHistoryComponent previousComponent = null;
			if (historyComponentsIndex>=0)
			{
				sb.append("<BR>\n").append("Selected operation feature-vector:<BR>\n");
				currentComponent = historyComponents.get(historyComponentsIndex);
				if (historyComponentsIndex==0)
				{
					previousComponent = initialTreeHistoryComponent;
				}
				else
				{
					previousComponent  = historyComponents.get(historyComponentsIndex-1);
				}
			}
			else
			{
				sb.append("<BR>\n").append("Initial feature-vector:<BR>\n");
				currentComponent = initialTreeHistoryComponent;
			}
			if (null==currentComponent) throw new VisualTracingToolException("Current history component is null. Cannot print details of feature vector.");
			boolean hasTheTree = true;
			if (null==currentComponent.getTree()){hasTheTree=false;}
			if (gapMode&&(!hasTheTree))
			{
				sb.append("<BR>\n<B>Vectors do not include gap features!</B><BR>\n");
			}
			Map<Integer, Double> currentComponentFeatureVector = currentComponent.getFeatureVector();
			if (gapMode&&hasTheTree)
			{
				currentComponentFeatureVector = underLyingSystem.getGapToolInstances().getGapFeaturesUpdate().updateForGap(
					new TreeAndParentMap<ExtendedInfo, ExtendedNode>(currentComponent.getTree()),
					currentComponentFeatureVector, underLyingSystem.getGapEnvironment());
			}
			
			Map<Integer, Double> previousOperationFeatureVector = null;
			if (previousComponent!=null) previousOperationFeatureVector = previousComponent.getFeatureVector();
			if (gapMode&&hasTheTree&&(previousOperationFeatureVector!=null)&&(previousComponent.getTree()!=null))
			{
				previousOperationFeatureVector = underLyingSystem.getGapToolInstances().getGapFeaturesUpdate().updateForGap(
						new TreeAndParentMap<ExtendedInfo, ExtendedNode>(previousComponent.getTree()),
						previousOperationFeatureVector, underLyingSystem.getGapEnvironment());
			}
			GuiUtils.printFeatures(this.underLyingSystem.getClassifier().getFeatureNames(), currentComponentFeatureVector, previousOperationFeatureVector, sb);
		}

		if (displayWholeProofFeatures){ if(cpe.getShowSearchDetailsMenuItem().isSelected())
		{
			sb.append("<BR>\n").append("Whole proof feature-vector:<BR>\n");
			GuiUtils.printFeatures(this.underLyingSystem.getClassifier().getFeatureNames(), this.bestTreeOfRegularSearch.getFeatureVector(), null, sb);
		}}
		
		if (gapMode)
		{
			GapToolInstances<ExtendedInfo, ExtendedNode> gapTools = underLyingSystem.getGapToolInstances();
			if (null==gapTools) throw new VisualTracingToolException("Null gap tools.");
			try
			{
				sb.append("<P>\n").append("Gap description:<BR><PRE>\n");
				sb.append(
				gapTools.getGapDescription().describeGap(
						new TreeAndParentMap<ExtendedInfo, ExtendedNode>(treeAndFeatureVector.getTree()), underLyingSystem.getGapEnvironment()));
				sb.append("</PRE><BR>\n");
			} catch (TreeAndParentMapException e){throw new VisualTracingToolException("Failed to build parent map.",e);}
			
		}
		
		
		sb.append("</BODY></HTML>\n");

		if (logger.isDebugEnabled())
		{
			logger.debug("HTML of table: \n"+sb.toString());
		}
		
		return sb.toString();
	}

	/**
	 * 
	 */
	private void handleSystemNotInitializedErrorForRegularSearch()
	{
		SwingUtilities.messageBox(cpe.getMainFrame(),
				"Cannot perform regular search since \""+VisualTracingTool.BUTTON_GO_TEXT+"\" button was not pushed yet.",
				"Error", JOptionPane.ERROR_MESSAGE, true);
	}

	/**
	 * @param currentCost
	 * @param previousCost
	 * @return
	 */
	private String computeOperationCost(double currentCost, double previousCost) {
		return strDouble(currentCost - previousCost);
	}

	private String strDouble(double d) {
		return String.format("%-6.3f", d);
	}

	private String strDoubleObj(Double d) {
		if (null==d)return "N/A";
		return strDouble(d.doubleValue());
	}

	private void performExit() {
		System.exit(0);
	}

	private void handleError(final Exception ex, boolean withInvokeLater) {
		SwingUtilities.handleError(cpe.getMainFrame(), ex, withInvokeLater);
	}

	private void setStatusBarSystemState(String state)
	{
		cpe.setStatusBarSystemState(state);
		cpe.updateStatusBarLabel();
	}

	private class StatusBarShortMessageFire implements ShortMessageFire
	{
		public void fire(String message)
		{
			setStatusBarSystemState(message);
		}
	}

	private static <K, V> K getKeyOf(Map<K, V> map, V v)
	{
		K ret = null;
		for (K k : map.keySet()) {
			if (v.equals(map.get(k))) {
				ret = k;
				break;
			}
		}
		return ret;
	}

	/**
	 * @param featureVector
	 * @return
	 * @throws VisualTracingToolException
	 */
	private double getCost(Map<Integer, Double> featureVector) throws VisualTracingToolException {
		try {
			return -underLyingSystem.classifier.getProduct(featureVector);
		} catch (ClassifierException e) {
			throw new VisualTracingToolException("error computing cost of a specified operation");
		}
	}

	private String getTextColorOfCost(TreeHistoryComponent component, SorterOfTreeHistory sortedHistory) throws ClassifierException {
		String backgroundColor;
		String foregroundColor;
		if (cpe.getUseBWTableColorsMenuItem().isSelected())
		{			// B&W mode
			if (sortedHistory.getCausingFailure().contains(component)) {
				backgroundColor = COLOR_FOREGROUND_BAD_STRING;	// "rgb(32,0,0) "; // red
				foregroundColor = "rgb(255,255,255)"; // white
			} else if (sortedHistory.getWorseThanMedian().contains(component)) {
				backgroundColor = "rgb(128,128, 0)"; // kinda yellows
				foregroundColor = "rgb(0,0,0)"; // black
			} else {
				backgroundColor = COLOR_FOREGROUND_GOOD_STRING;	//  "rgb(0,255,255) "; // tkhelet
				foregroundColor = "rgb(0,0,0)"; // black
			}
		}
		else
		{		// color mode
			if (sortedHistory.getCausingFailure().contains(component)) {
				backgroundColor = "red";	// "rgb(32,0,0) "; // red
				foregroundColor = "black"; // white
			} else if (sortedHistory.getWorseThanMedian().contains(component)) {
				backgroundColor = "yellow"; // kinda yellows
				foregroundColor = "black"; // black
			} else {
				backgroundColor = "green";	//  "rgb(0,255,255) "; // tkhelet
				foregroundColor = "black"; // black
			}
		}

		return "<FONT style=\"background-COLOR: " + backgroundColor	+ "; color: " + foregroundColor + "\">";
	}
	
	
	private ProgressFire createRegularSearchProgressFire()
	{
		return new ProgressFire()
		{
			public void fire(double percentage)
			{
				percentage = Math.min(1.0,percentage);
				percentage = Math.max(0.0,percentage);
				final double finalPercentage = percentage*100;
				invokeLater(new Runnable()
				{
					public void run()
					{
						cpe.getProgressIndicatorInRegularSearch().setValue((int)finalPercentage);
					}
				});
			}
		};
	}
	
	protected void cleanBeforeProcessingNewPair()
	{
		pairData = null;
		currentDisplayedTreeComponent = null;
		selectedTreeNextStack = null;
		originalTreeOfRegularSearch = null;
		historyComponents = null;
		historyComponentsIndex = 0;
		sortedHistory = null;
		originalTreeSentence = null;
		bestTreeOfRegularSearch = null;
		bestTreeSentenceOfRegularSearch = null;
		bestTreeHistory = null;
	}
	
	
	protected String buildCoreferenceDescription() throws TreeCoreferenceInformationException
	{
		StringBuilder sb = new StringBuilder();
		TreeCoreferenceInformation<ExtendedNode> corefInformation = this.underLyingSystem.getOriginalTreesAfterInitialization().getCoreferenceInformation();
		for (Integer groupId : corefInformation.getAllExistingGroupIds())
		{
			if (corefInformation.getGroup(groupId)!=null) { if (corefInformation.getGroup(groupId).size()>=2)
			{
				sb.append(groupId).append(": ");
				boolean firstIteration = true;
				for (ExtendedNode node : corefInformation.getGroup(groupId))
				{
					if (firstIteration) firstIteration=false;
					else sb.append(", ");
					if (node.getInfo()!=null)
					{
						sb.append(node.getInfo().getId()).append(":").append(InfoGetFields.getLemma(node.getInfo()));
					}
					else
					{
						sb.append("?");
					}
				}
				sb.append("\n");
			}}
		}
		return sb.toString();
	}
	

	private VisualTracingTool cpe;
	private ExtendedPairData pairData = null;
	
	private GuiUtils guiUtils;

	private Boolean useF1Classifier = null;
	private SinglePairPreProcessor preProcessor = null;
	private SingleComponentUnderlyingSystem underLyingSystem = null;

	private boolean displayOnlyLastGenerated = false;
	private SingleTreeComponent currentDisplayedTreeComponent = null;
	private Stack<SingleTreeComponent> selectedTreeNextStack;

	private ExtendedNode originalTreeOfRegularSearch;
	private ImmutableList<TreeHistoryComponent> historyComponents = null;
	private int historyComponentsIndex = 0;
	private SorterOfTreeHistory sortedHistory = null;
	private String originalTreeSentence = null;
	private NodeDisplayMode nodeDisplayMode;

	/**
	 * used in {@link #refreshAllTreeImages()}
	 */
	private TreeAndFeatureVector bestTreeOfRegularSearch = null;
	/**
	 * used in {@link #refreshAllTreeImages()}
	 */
	private String bestTreeSentenceOfRegularSearch = null;
	
	private TreeHistory bestTreeHistory = null;

	private RecentPairsSaver recentPairsSaver = null; 
	
	private static Logger logger = Logger.getLogger(ActionsPerformer.class);
}
