package eu.excitementproject.eop.biutee.rteflow.systems.gui;
import static javax.swing.SwingUtilities.invokeLater;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.biutee.rteflow.systems.TESystemEnvironment;
import eu.excitementproject.eop.biutee.rteflow.systems.rtesum.preprocess.ExtendedPreprocessedTopicDataSet;
import eu.excitementproject.eop.biutee.rteflow.systems.rtesum.preprocess.ExtendedTopicDataSetGenerator;
import eu.excitementproject.eop.biutee.rteflow.systems.rtesum.preprocess.PreprocessedTopicDataSet;
import eu.excitementproject.eop.common.representation.coreference.TreeCoreferenceInformationException;
import eu.excitementproject.eop.common.utilities.datasets.rtesum.SentenceIdentifier;
import eu.excitementproject.eop.common.utilities.file.FileFilterByExtension;
import eu.excitementproject.eop.transformations.generic.truthteller.AnnotatorException;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;

/**
 * 
 * TO-DO (comment by Asher Stern): GUI code is not of high quality and should be
 * improved. Need to re-design, make it more modular, adding documentation and
 * improve code.
 * 
 * @author Asher Stern
 * @since Nov 9, 2011
 *
 */
public class GuiRteSumUtilities implements ActionListener
{
	public static final String DEFAULT_FILE_EXTENSION = "ser";
	
	public GuiRteSumUtilities(VisualTracingTool cpe)
	{
		this.cpe = cpe;
	}
	
	public boolean isSumDatasetSentenceSelected()
	{
		return sumDatasetSentenceSelected;
	}
	
	public PreprocessedTopicDataSet getPreprocessedTopicDataset() throws VisualTracingToolException
	{
		if (!sumDatasetSentenceSelected)throw new VisualTracingToolException("Sentence not selected!");
		return this.mapTopicIdToTopic.get(topicId);
	}
	
	public ExtendedPreprocessedTopicDataSet getExtendedPreprocessedTopicDataSet(TESystemEnvironment teSystemEnvironment) throws VisualTracingToolException, TreeCoreferenceInformationException, TeEngineMlException, AnnotatorException
	{
		if (!sumDatasetSentenceSelected)throw new VisualTracingToolException("Sentence not selected!");
		if (!mapTopicIdToExtendedTopic.containsKey(topicId))
		{
			if (!mapTopicIdToTopic.containsKey(topicId))
				throw new VisualTracingToolException("Bad topicId: \""+topicId+"\"");
			cpe.setStatusBarSystemState("Converting topic");
			cpe.updateStatusBarLabel();
			ExtendedTopicDataSetGenerator extendedGenerator = new ExtendedTopicDataSetGenerator(mapTopicIdToTopic.get(topicId),teSystemEnvironment);
			extendedGenerator.generate();
			mapTopicIdToExtendedTopic.put(topicId, extendedGenerator.getExtendedTopic());
		}
		return mapTopicIdToExtendedTopic.get(topicId);
	}
	
	public SentenceIdentifier getSentenceID()
	{
		return sentenceID;
	}

	public void actionPerformed(ActionEvent e)
	{
		try
		{
			if (e.getActionCommand().equals("loadSumDataSetMenuItem"))
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
				fileChooser.addChoosableFileFilter(new FileFilterByExtension(DEFAULT_FILE_EXTENSION));
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int fileChooserRet = fileChooser.showOpenDialog(cpe.getMainFrame());
				if (fileChooserRet==JFileChooser.APPROVE_OPTION)
				{
					// TODO Change cursor icon
					final File selectedFile = fileChooser.getSelectedFile();
					this.lastVisitedDir = selectedFile.getParentFile();
					cpe.disableAll();
					cpe.getMainFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					new Thread(new Runnable()
					{
						public void run()
						{
							try
							{
								loadPreprocessedTopics(selectedFile);
							}
							catch(Exception exce)
							{
								handleError(exce, true);
							}
							finally
							{
								invokeLater(new Runnable()
								{
									public void run()
									{
										cpe.getMainFrame().setCursor(null);
										cpe.enableAll();
									}
								});
							}
						}
					}).start();
				}
			}
			else if (e.getActionCommand().equals("selectSentenceMenuItem"))
			{
				showDialogSelect();
			}
			else if (e.getActionCommand().equals("buttonSelect"))
			{
				select();
			}
			else if (e.getSource()==topicComboBox)
			{
				topicId = (String)topicComboBox.getSelectedItem();
				documentComboBox.setModel(new DefaultComboBoxModel<String>(new Vector<String>(mapTopicIdToTopic.get(topicId).getTopicDataSet().getDocumentsMap().keySet())));
				documentComboBox.setEnabled(true);
			}
			else if (e.getSource()==documentComboBox)
			{
				selectedDocument = (String)documentComboBox.getSelectedItem();
				sentenceComboBox.setModel(new DefaultComboBoxModel<Integer>(new Vector<Integer>(mapTopicIdToTopic.get(topicId).getTopicDataSet().getDocumentsMap().get(selectedDocument).keySet())));
				sentenceComboBox.setEnabled(true);
			}
			else if (e.getActionCommand().equals("buttonDiscard"))
			{
				selectDialog.setVisible(false);
				this.sumDatasetSentenceSelected = false;
				this.topicId=null;
				this.selectedDocument=null;
				this.sentenceID=null;
				this.mapTopicIdToExtendedTopic = null;
				this.cpe.textMakeEditable();
				cpe.getStatusBarStates().remove(StatusBarState.SUM_MODE);
				cpe.updateStatusBarLabel();
			}
		}
		catch(Exception x)
		{
			handleError(x, false);
		}
	}
	
	


	@SuppressWarnings("unchecked")
	private void loadPreprocessedTopics(File file) throws FileNotFoundException, IOException, ClassNotFoundException
	{
		ObjectInputStream input = new ObjectInputStream(new FileInputStream(file));
		try
		{
			preprocessedTopics = (List<PreprocessedTopicDataSet>)input.readObject();
			mapTopicIdToTopic = new LinkedHashMap<String, PreprocessedTopicDataSet>();
			mapTopicIdToExtendedTopic = new LinkedHashMap<String, ExtendedPreprocessedTopicDataSet>();
			for (PreprocessedTopicDataSet topic : preprocessedTopics)
			{
				mapTopicIdToTopic.put(topic.getTopicDataSet().getTopicId(), topic);
			}
		}
		finally
		{
			input.close();
		}
	}
	
	private void showDialogSelect() throws VisualTracingToolException
	{
		if (null==preprocessedTopics)
			throw new VisualTracingToolException("Ser file was not selected");
		
		selectDialog = new JDialog(cpe.getMainFrame(),true);
		selectDialog.setTitle("Select text sentence");
		JPanel contentPane = new JPanel(new GridLayout(4, 2));
		
		
		topicComboBox = new JComboBox<String>(new Vector<String>(mapTopicIdToTopic.keySet()));
		topicComboBox.addActionListener(this);
		documentComboBox = new JComboBox<String>();
		documentComboBox.addActionListener(this);
		documentComboBox.setEnabled(false);
		sentenceComboBox = new JComboBox<Integer>();
		sentenceComboBox.addActionListener(this);
		sentenceComboBox.setEnabled(false);
		contentPane.add(new JLabel("topic:"));
		contentPane.add(topicComboBox);
		contentPane.add(new JLabel("document:"));
		contentPane.add(documentComboBox);
		contentPane.add(new JLabel("sentence:"));
		contentPane.add(sentenceComboBox);
		JButton buttonSelect = new JButton("Select");
		buttonSelect.setActionCommand("buttonSelect");
		buttonSelect.addActionListener(this);
		contentPane.add(buttonSelect);
		JButton buttonDiscard = new JButton("Discard");
		buttonDiscard.setActionCommand("buttonDiscard");
		buttonDiscard.addActionListener(this);
		contentPane.add(buttonDiscard);
		
		
		selectDialog.setContentPane(contentPane);
		Dimension frameDimension = cpe.getMainFrame().getSize();
		Dimension originalPreferredSize = selectDialog.getPreferredSize();
		selectDialog.setPreferredSize(new Dimension((int)(frameDimension.getWidth()*0.5), (int)(originalPreferredSize.getHeight())));
		selectDialog.pack();
		selectDialog.setVisible(true);
	}
	
	private void select() throws VisualTracingToolException
	{
		selectDialog.setVisible(false);
		topicId = (String)topicComboBox.getSelectedItem();
		if (!mapTopicIdToTopic.containsKey(topicId))
		{
			VisualTracingToolException ex = new VisualTracingToolException("Bad Topic id: "+topicId); 
			topicId = null;
			throw ex;
		}
		sentenceID = new SentenceIdentifier((String)documentComboBox.getSelectedItem(), sentenceComboBox.getSelectedItem().toString());
		if (!mapTopicIdToTopic.get(topicId).getTopicDataSet().getDocumentsMap().containsKey(sentenceID.getDocumentId()))
		{
			VisualTracingToolException ex = new VisualTracingToolException("Bad Document id: "+sentenceID.getDocumentId()); 
			sentenceID = null;
			throw ex;
		}
		
		if (!mapTopicIdToTopic.get(topicId).getTopicDataSet().getDocumentsMap().get(sentenceID.getDocumentId()).containsKey(Integer.valueOf(sentenceID.getSentenceId())))
		{
			VisualTracingToolException ex = new VisualTracingToolException("Bad sentence id: "+sentenceID.getSentenceId()); 
			sentenceID = null;
			throw ex;
		}
		String sentenceString = mapTopicIdToTopic.get(topicId).getTopicDataSet().getDocumentsMap().get(sentenceID.getDocumentId()).get(Integer.valueOf(sentenceID.getSentenceId()));
		cpe.getTextTextArea().setText(sentenceString);
		logger.info("selected: topic= "+topicId+" document = "+sentenceID.getDocumentId()+" sentence = "+sentenceID.getSentenceId()+" sentence string is: "+sentenceString);
		sumDatasetSentenceSelected=true;
		invokeLater(new Runnable()
		{
			public void run(){cpe.textMakeNotEditable();}
		});
		
		cpe.getStatusBarStates().add(StatusBarState.SUM_MODE);
		cpe.updateStatusBarLabel();
	}
	
	private void handleError(final Exception ex, boolean withInvokeLater)
	{
		SwingUtilities.handleError(cpe.getMainFrame(), ex, withInvokeLater);
	}
	
	
	

	private VisualTracingTool cpe;
	private List<PreprocessedTopicDataSet> preprocessedTopics=null;
	private Map<String,PreprocessedTopicDataSet> mapTopicIdToTopic = null;
	private Map<String,ExtendedPreprocessedTopicDataSet> mapTopicIdToExtendedTopic = null; 
	
	private String topicId;
	private String selectedDocument;
	private SentenceIdentifier sentenceID;
	private JDialog selectDialog;
	private boolean sumDatasetSentenceSelected = false;
	
	private JComboBox<String> topicComboBox;
	private JComboBox<String> documentComboBox;
	private JComboBox<Integer> sentenceComboBox;
	
	private File lastVisitedDir=null;
	
	
	
	private static final Logger logger = Logger.getLogger(GuiRteSumUtilities.class);
}
