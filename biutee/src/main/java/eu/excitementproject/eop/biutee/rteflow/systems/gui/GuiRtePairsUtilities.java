package eu.excitementproject.eop.biutee.rteflow.systems.gui;
import static javax.swing.SwingUtilities.invokeLater;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerListModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.utilities.BiuteeConstants;
import eu.excitementproject.eop.common.utilities.datasets.rtepairs.DefaultRTEMainReader;
import eu.excitementproject.eop.common.utilities.datasets.rtepairs.RTEMainReader;
import eu.excitementproject.eop.common.utilities.datasets.rtepairs.RTEMainReaderException;
import eu.excitementproject.eop.common.utilities.datasets.rtepairs.TextHypothesisPair;
import eu.excitementproject.eop.common.utilities.file.FileFilterByExtension;

/**
 * 
 * TO-DO (comment by Asher Stern): GUI code is not of high quality and should be
 * improved. Need to re-design, make it more modular, adding documentation and
 * improve code.
 * 
 * @author Asher Stern
 * @since 19 November 2011
 *
 */
public class GuiRtePairsUtilities implements ActionListener, ChangeListener
{
	public static final String DEFAULT_EXTENSION = "xml";
	
	public GuiRtePairsUtilities(VisualTracingTool cpe)
	{
		super();
		this.cpe = cpe;
	}
	
	public Map<Integer, TextHypothesisPair> getMapPairs()
	{
		return mapPairs;
	}

	public Integer getSelectedPairIndex()
	{
		return selectedPairIndex;
	}


	public void actionPerformed(ActionEvent e)
	{
		try
		{
			if (e.getActionCommand().equals("loadPairsDataSetMenuItem"))
			{
				showDialog();
			}
			else if (e.getActionCommand().equals("loadLastPairsMenuItem"))
			{
				final File file = new File(BiuteeConstants.FILENAME_RECENT_PAIRS_IN_GUI);
				cpe.getMainFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				new Thread(
						new Runnable()
						{
							@Override
							public void run()
							{
								try
								{
									loadDS(file);
									invokeLater(new Runnable()
									{
										public void run()
										{
											showDialog();
										}
									});
								}
								catch (RTEMainReaderException x)
								{
									handleError(x, true);
								}
								catch (VisualTracingToolException x)
								{
									handleError(x, true);
								}
								finally
								{
									invokeLater(new Runnable()
									{
										public void run()
										{
											cpe.getMainFrame().setCursor(null);
										}
									});
								}
							}
						}
						).start();
				
			}
			else if (e.getActionCommand().equals("buttonLoad"))
			{
				JFileChooser fileChooser;
				if (lastVisitedDir!=null)
				{
					fileChooser = new JFileChooser(lastVisitedDir);
				}
				else
				{
					fileChooser = new JFileChooser(new File("."));
				}
				fileChooser.addChoosableFileFilter(new FileFilterByExtension(DEFAULT_EXTENSION));
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int fileChooserRet = fileChooser.showOpenDialog(cpe.getMainFrame());
				if (fileChooserRet==JFileChooser.APPROVE_OPTION)
				{
					cpe.getMainFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					dialog.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					disableDialogComponents();
					final File selectedFile = fileChooser.getSelectedFile();
					this.lastVisitedDir = selectedFile.getParentFile();
					new Thread(new Runnable()
					{
						public void run()
						{
							try
							{
								loadDS(selectedFile);
								invokeLater(new Runnable()
								{
									public void run()
									{
										showTHInDialog();
									}
								});
							}
							catch (RTEMainReaderException x)
							{
								handleError(x, true);
							}
							catch (VisualTracingToolException x)
							{
								handleError(x, true);
							}
							finally
							{
								invokeLater(new Runnable()
								{
									public void run()
									{
										enableDialogComponents();
										dialog.pack();
										dialog.setCursor(null);
										cpe.getMainFrame().setCursor(null);
									}
								});
							}
						}
					}).start();
					
				}
			}
			else if (e.getActionCommand().equals("Select"))
			{
				if (datasetFileIsLoaded)
				{
					selectedPairIndex = ((Integer)spinner.getValue()).intValue();
					lastSelected = selectedPairIndex;
					setSelectionInGui();
					dialog.setVisible(false);
					dialog = null;
				}
				else throw new VisualTracingToolException("BUG: the \"Select\" button should not be enabled when dataset file is not loaded.");
			}
			else if (e.getActionCommand().equals("Discard"))
			{
				datasetFileIsLoaded=false;
				this.selectedPairIndex = null;
				dialog.setVisible(false);
				dialog = null;
				cpe.getStatusBarStates().remove(StatusBarState.PAIRS_MODE);
				cpe.updateStatusBarLabel();
				cpe.setQuickAccessAllow(false);
				cpe.getQuickAccessSpinner().setEnabled(false);
			}
		}
		catch(Exception x)
		{
			handleError(x, false);
		}

	}

	public void stateChanged(ChangeEvent e)
	{
		if (e.getSource()==this.spinner)
		{
			showTHInDialog();
		}
		else if (e.getSource()==cpe.getQuickAccessSpinner())
		{
			selectedPairIndex = ((Integer)cpe.getQuickAccessSpinner().getValue()).intValue();
			lastSelected = selectedPairIndex;
			setSelectionInGui();
		}
	}

	
	private void setSelectionInGui()
	{
		Set<String> validTaskNames = new LinkedHashSet<String>();
		ComboBoxModel<String> cbModel = cpe.getComboBoxTaskNames().getModel();
		for (int index=0;index<cbModel.getSize();++index)
		{
			validTaskNames.add((String)cbModel.getElementAt(index));
		}
		TextHypothesisPair selectedPair = mapPairs.get(selectedPairIndex);
		String taskName = selectedPair.getAdditionalInfo();
		if (validTaskNames.contains(taskName))
		{
			cpe.getComboBoxTaskNames().setSelectedItem(taskName);
		}
		else
		{
			cpe.getComboBoxTaskNames().setSelectedItem(VisualTracingTool.IGNORE_TASK_NAME_STRING);
		}
		cpe.getTextTextArea().setText(selectedPair.getText());
		cpe.getHypothesisTextArea().setText(selectedPair.getHypothesis());
		cpe.getStatusBarStates().add(StatusBarState.PAIRS_MODE);
		cpe.updateStatusBarLabel();
	}
	
	private void loadDS(File selectedFile) throws RTEMainReaderException, VisualTracingToolException
	{
		RTEMainReader reader = new DefaultRTEMainReader();
		reader.setXmlFile(selectedFile);
		reader.read();
		this.mapPairs = reader.getMapIdToPair();
		if (mapPairs.keySet().size()<1)
			throw new VisualTracingToolException("Empty pairs file (no pairs)!");
		
//		minPairIndex = Collections.min(this.mapPairs.keySet());
//		maxPairIndex = Collections.max(this.mapPairs.keySet());
		idsAsList = new ArrayList<Integer>(mapPairs.keySet().size());
		idsAsList.addAll(mapPairs.keySet());
		
		lastSelected = idsAsList.iterator().next();

		invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					model = new SpinnerListModel(idsAsList);
					if (spinner!=null)
					{
						spinner.setModel(model);
						spinner.setValue(lastSelected);
						//spinner.setModel(new SpinnerNumberModel(lastSelected.intValue(), minPairIndex.intValue(), maxPairIndex.intValue(), 1));
						spinner.setEnabled(true);
					}
					cpe.getQuickAccessSpinner().setModel(model);
					cpe.getQuickAccessSpinner().setValue(lastSelected);
					cpe.getQuickAccessSpinner().setEnabled(true);
					cpe.setQuickAccessAllow(true);
				}
				catch(RuntimeException ex){handleError(ex, true);}
			}
		});
		datasetFileIsLoaded=true;
		if (buttonDone!=null)
		{
			this.buttonDone.setEnabled(true);
		}
	}
	

	
	private void showDialog()
	{
		dialog = new JDialog(cpe.getMainFrame(), true);
		dialog.setTitle("RTE pairs");
		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());
		JPanel thPanel = new JPanel();
		thPanel.setLayout(new BoxLayout(thPanel, BoxLayout.PAGE_AXIS));
		JPanel buttonsPanel = new JPanel();
		
		// Adding buttons and spinner.
		buttonLoad = new JButton("Load...");
		buttonLoad.setActionCommand("buttonLoad");
		buttonLoad.addActionListener(this);
		buttonsPanel.add(buttonLoad);
		
		
		// Adding spinner, either enabled with values from previous load, or
		// disabled if no data set was previously loaded.
		if (this.mapPairs!=null)
		{
			spinner = new JSpinner(model);
			spinner.setValue(lastSelected);
		}
		else
		{
			spinner = new JSpinner();
			spinner.setEnabled(false);
		}
		
		// TODO: hard-coded constant 3.
		spinner.setPreferredSize(new Dimension(
				((int)spinner.getPreferredSize().getWidth())*3,
				(int)spinner.getPreferredSize().getHeight())
		);
		
		spinner.addChangeListener(this);
		
		buttonsPanel.add(spinner);
		
		
		buttonDone = new JButton("Select");
		buttonDone.setActionCommand("Select");
		buttonDone.addActionListener(this);
		if (datasetFileIsLoaded)
			buttonDone.setEnabled(true);
		else
			buttonDone.setEnabled(false);
		buttonsPanel.add(buttonDone);
		
		buttonDiscard = new JButton("Discard");
		buttonDiscard.setActionCommand("Discard");
		buttonDiscard.addActionListener(this);
		buttonsPanel.add(buttonDiscard);

		// Adding text-hypothesis areas
		// text
		this.textTextArea = new JTextArea();
		textTextArea.setEditable(false);
		textTextArea.setLineWrap(true);
		textTextArea.setWrapStyleWord(true);
		textTextArea.setBorder(BorderFactory.createTitledBorder("Text"));
		JScrollPane textTextScrollPane = new JScrollPane(textTextArea);
		textTextScrollPane.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 6, Color.gray));	// border only on the right

		// hypothesis
		this.hypothesisTextArea = new JTextArea();
		hypothesisTextArea.setEditable(false);
		hypothesisTextArea.setLineWrap(true);
		hypothesisTextArea.setWrapStyleWord(true);
		hypothesisTextArea.setBorder(BorderFactory.createTitledBorder("Hypothesis"));
		JScrollPane hyopthesisScrollPane = new JScrollPane(hypothesisTextArea);
		hyopthesisScrollPane.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 6, Color.gray));	// border only on the right


		// Adding everything into the content panel
		thPanel.add(textTextScrollPane);
		thPanel.add(hyopthesisScrollPane);
		contentPane.add(thPanel,BorderLayout.CENTER);
		contentPane.add(buttonsPanel,BorderLayout.SOUTH);
		if (datasetFileIsLoaded)
			showTHInDialog();
		dialog.setContentPane(contentPane);
		dialog.pack();
		dialog.setVisible(true);
	}
	
	private void showTHInDialog()
	{
		selectedPairIndex = ((Integer)spinner.getValue()).intValue();
		TextHypothesisPair selectedPair = mapPairs.get(selectedPairIndex);
		this.textTextArea.setText(selectedPair.getText());
		this.hypothesisTextArea.setText(selectedPair.getHypothesis());
	}

	
	private void disableDialogComponents()
	{
		setEnablingOfDialogComponents(false);
	}

	private void enableDialogComponents()
	{
		setEnablingOfDialogComponents(true);
	}
	private void setEnablingOfDialogComponents(boolean enabled)
	{
		buttonLoad.setEnabled(enabled);
		buttonDone.setEnabled(enabled);
		buttonDiscard.setEnabled(enabled);
		spinner.setEnabled(enabled);
		
	}
	
	private void handleError(final Exception ex, boolean withInvokeLater)
	{
		SwingUtilities.handleError(cpe.getMainFrame(), ex, withInvokeLater);
	}

	private VisualTracingTool cpe;

	private JDialog dialog;
	private JButton buttonLoad;
	private JButton buttonDone;
	private JButton buttonDiscard;
	private File lastVisitedDir=null;
	private Map<Integer,TextHypothesisPair> mapPairs;
	private Integer selectedPairIndex = null;
	private JSpinner spinner;
	private Integer lastSelected;
	private List<Integer> idsAsList;
	private SpinnerListModel model;
	private JTextArea textTextArea;
	private JTextArea hypothesisTextArea;
	private boolean datasetFileIsLoaded = false;
	
//	private Integer minPairIndex;
//	private Integer maxPairIndex;
	
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(GuiRtePairsUtilities.class);
}
