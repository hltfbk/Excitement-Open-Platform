/**
 * 
 */
package eu.excitementproject.eop.biutee.rteflow.systems.gui;
import java.util.HashMap;
import java.util.Map;

/**
 * This class holds static texts displayed in pop up labels, activated by the help ("?") buttons in the GUI.
 * 
 * @author Amnon Lotan
 *
 * @since 11 Mar 2012
 */
public class GuiHelpTexts {

	public static final String TABLE_COLORS_DESCRIPTION = "In the table, the classification score column is painted red (dark) if the " +
				"score causes the proof to fail; yellow (grey) if the score doesn't fail the " +
				"proof, and is higher than the median; and green (bright) otherwise.";

	private static final String TH_PAIR_HELP_MSG = 
			"In this pane, you enter the text and hypothesis texts that " +
			"will be processed in the other panes. You enter the text sentence(s) " +
			"in the \"Text\" text-box, at the top left, and enter the hypothesis " +
			"in the \"Hypothesis\" text-box, at the bottom.\n" +
			"The \"Sentence Split Text\" text-box in read only, and displays the " +
			"text, split into sentences.\n" +
			"At the bottom, there are four controls, " +
			"(1) A spinner, which is used only if the system is in \"pairs mode\" (i.e. reads the text and hypothesis from an XML file), " +
			"\"Task Name\", \"Go!\" and " +
			"the help button that dispalys this message.\n" +
			"At the \"Task Name\" " +
			"combo box, you should select the name of the RTE task to which your " +
			"TH pair pertains. If in doubt, leave it on \"IR\".\n" +
			"When finished reviewing all the above, press \"Go!\" to start " +
			"pre-processing the given TH pair.\n" +
			"The pre-processing includes sentence-splitting, parsing, NER and coreference resolution.\n" +
			"After pre-processing, you can proceed to entailment recognition in other tabs."
					;
	
	private static final String TEXT_TREES_HELP_MSG = 
			"This pane displays the parse trees on the sentence(s) you entered as " +
			"Text in the \"" + VisualTracingTool.TITLE_T_H_PAIR + "\" pane.\n" +
			"The trees are displayed one at a time, and the spinner at the bottom " +
			"of this pane shows you the number of the sentence of the current tree.\n" +
			"The number corresponds to the sentence's number in the \"Sentence " +
			"Split Text\" in the \"" + VisualTracingTool.TITLE_T_H_PAIR + "\" pane.\n" +
			"You can zoom in and out of the picture with your mouse wheel, while " +
			"holding down the Ctrl key.";
	
	private static final String MANUAL_MODE_HELP_MSG = 
			"This pane helps you build a manual proof for the given TH pair, step by " +
			"step, using all the operations the engine has available.\n" +
			"Each row in the main table describes one operation that you may choose " +
			"to perform on (one of the) text tree(s). You can choose to apply an " +
			"operation by double-clicking on it. This will add to the bottom of the " +
			"table rows describing all the new operations, now made available for you " +
			"after performing the last operation.\n" +
			"Note that by checking the \"" + VisualTracingTool.TITLE_DISPLAY_ONLY_LAST_GENERATED_TREES + "\" " +
			"checkbox, the view will be filtered to operations made available by " +
			"your last selection.\n" +
			"When ready to complete your manual proof, instead of double clicking on " +
			"the last operation, mark it by clicking on it once, and hit the " +
			" \"" + VisualTracingTool.TITLE_DISPLAY_SELECTED_TREE + "\" button. This will " +
			"complete your manual proof and switch you to the \"" + VisualTracingTool.TITLE_VIEW_TREE_MANUAL + "\" " +
			"pane, where you can inspect your proof."
			;
	
	private static final String VIEW_TREE_HELP_MSG = 
			"This pane lets you inspect the manual proof constructed in the" + "\"" + VisualTracingTool.TITLE_MANUAL_MODE_PANE + "\" pane.\n" + 
			TABLE_COLORS_DESCRIPTION + 
			'\n'
			;
	
	private static final String AUTOMATIC_MODE_HELP_MSG = 
			"This pane lets you run and inspect the engine's regular algorithm to find " +
			"an optimal proof for the given TH pair.\n" +
			"The lower progress bar indicates how likely it is that T entails H.\n50% and above is a positive answer (T entails H).\n"+
			TABLE_COLORS_DESCRIPTION + 
			'\n'
			;
	
	private static final Map<String, String> mapCommandToHelpMessage = new HashMap<String, String>();
	private static final Map<String, String> mapCommandToHelpTitle = new HashMap<String, String>();
	static
	{
		mapCommandToHelpMessage.put(VisualTracingTool.COMMAND_TH_PAIR_HELP, TH_PAIR_HELP_MSG);
		mapCommandToHelpTitle.put(VisualTracingTool.COMMAND_TH_PAIR_HELP, VisualTracingTool.TITLE_T_H_PAIR);
		mapCommandToHelpMessage.put(VisualTracingTool.COMMAND_TEXT_TREES_HELP, TEXT_TREES_HELP_MSG);
		mapCommandToHelpTitle.put(VisualTracingTool.COMMAND_TEXT_TREES_HELP, VisualTracingTool.TITLE_TEXT_TREES);
		mapCommandToHelpMessage.put(VisualTracingTool.COMMAND_TREES_TABLE_HELP, MANUAL_MODE_HELP_MSG);
		mapCommandToHelpTitle.put(VisualTracingTool.COMMAND_TREES_TABLE_HELP, VisualTracingTool.TITLE_MANUAL_MODE_PANE);
		mapCommandToHelpMessage.put(VisualTracingTool.COMMAND_SELECTED_TREE_HELP, VIEW_TREE_HELP_MSG);
		mapCommandToHelpTitle.put(VisualTracingTool.COMMAND_SELECTED_TREE_HELP, VisualTracingTool.TITLE_VIEW_TREE_MANUAL);
		mapCommandToHelpMessage.put(VisualTracingTool.COMMAND_REGULAR_SEARCH_HELP, AUTOMATIC_MODE_HELP_MSG);
		mapCommandToHelpTitle.put(VisualTracingTool.COMMAND_REGULAR_SEARCH_HELP, VisualTracingTool.TITLE_AUTOMATIC_MODE);
	}
	
	/**
	 * @param commandString
	 * @return
	 */
	public static String getMessage(String commandString) {
		return mapCommandToHelpMessage.get(commandString);
	}

	/**
	 * @param commandString
	 * @return
	 */
	public static String getTitle(String commandString) {
		return mapCommandToHelpTitle.get(commandString);
	}

}
