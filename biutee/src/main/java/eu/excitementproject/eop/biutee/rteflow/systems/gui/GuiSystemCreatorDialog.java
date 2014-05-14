package eu.excitementproject.eop.biutee.rteflow.systems.gui;
import static eu.excitementproject.eop.biutee.utilities.BiuteeConstants.GUI_LOADS_LABELED_SAMPLES;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;

import eu.excitementproject.eop.common.utilities.log4j.GuiAppender;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;
/**
 * 
 * @author Asher Stern
 * @since Aug 23, 2012
 *
 */
public class GuiSystemCreatorDialog extends JDialog implements ActionListener
{
	private static final long serialVersionUID = -9108358461434604177L;
	
	public static final int LOG_LINES_IN_TEXT_AREA = 10;

	
	
	public GuiSystemCreatorDialog(VisualTracingTool cpe)
	{
		super(cpe.getMainFrame(), "Creating Underlying system", true);
		this.cpe = cpe;
		this.owner = cpe.getMainFrame();
	}

	public boolean go()
	{
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

		JTextArea instructions;
		if (GUI_LOADS_LABELED_SAMPLES)
		{
			instructions = new JTextArea(
					"Please select the type of classifier.\n"+
							"Note that it must be identical to the training classifier.\n"+
							"otherwise the results will be incorrect, and the system will run very slow.\n"+
							"Then, press \"Initialize\""
					);
		}
		else
		{
			instructions = new JTextArea("Please click \"Initialize\"");
		}
		instructions.setEditable(false);
		mainPanel.add(instructions);

		JPanel radioPanel = new JPanel();
		radioPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		//radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.PAGE_AXIS));

		if (GUI_LOADS_LABELED_SAMPLES)
		{
			radioGroup = new ButtonGroup();
			JRadioButton accuracyButton = new JRadioButton("Accuracy optimized");
			accuracyButton.setActionCommand("accuracy");
			accuracyButton.setSelected(true);
			radioGroup.add(accuracyButton);
			radioPanel.add(accuracyButton);

			JRadioButton f1Button = new JRadioButton("F1 optimized");
			f1Button.setActionCommand("f1");
			radioGroup.add(f1Button);
			radioPanel.add(f1Button);
			mainPanel.add(radioPanel);
		}
		
		
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.LINE_AXIS));
		initButton = new JButton("Initialize");
		initButton.addActionListener(this);
		initButton.setActionCommand("init");
		buttonsPanel.add(initButton);
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		cancelButton.setActionCommand("cancel");
		buttonsPanel.add(cancelButton);
		mainPanel.add(buttonsPanel);
		
		
		StringBuilder sb = new StringBuilder();
		sb.append("Initialization log...");
		for (int i=0;i<LOG_LINES_IN_TEXT_AREA;++i)
		{
			sb.append("\n");
		}
		textArea = new JTextArea(sb.toString());
		textArea.setEditable(false);
		textArea.setBackground(Color.BLACK);
		textArea.setForeground(Color.YELLOW);
		JScrollPane textPanel = new JScrollPane(textArea);
		mainPanel.add(textPanel);

		
		setContentPane(mainPanel);
		
		Rectangle r = owner.getBounds();
		textPanel.setPreferredSize(new Dimension(r.width/2, r.height/2));
		pack();
		
		int x = r.x + (r.width - this.getSize().width)/2;
		int y = r.y + (r.height - this.getSize().height)/2;
		this.setLocation(x, y);
		
		this.setVisible(true);
		
		return built;
	}
	
	

	
	public SingleComponentUnderlyingSystem getUnderlyingSystem() throws VisualTracingToolException
	{
		if (!built) throw new VisualTracingToolException("Not initialized");
		return underlyingSystem;
	}
	
	public boolean isUseF1Classifier() throws VisualTracingToolException
	{
		if (!GUI_LOADS_LABELED_SAMPLES)
			throw new VisualTracingToolException("Internal Bug. Method isUseF1Classifier() should not be called when loading model from XML model file");
		return useF1Classifier;
	}

	public Throwable getException()
	{
		return exception;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getActionCommand().equals("init"))
		{
			initButton.setEnabled(false);
			guiAppender = new GuiAppender(textArea,LOG_LINES_IN_TEXT_AREA);
			Logger.getRootLogger().addAppender(guiAppender);
			new Thread(new UnderLyingSystemCreateRunnable()).start();
		}
		else if (e.getActionCommand().equals("cancel"))
		{
			dispose();
		}
		
	}
	
	private class UnderLyingSystemCreateRunnable implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				exception = null;
				if (GUI_LOADS_LABELED_SAMPLES)
				{
					if (radioGroup.getSelection().getActionCommand().equals("accuracy"))
					{
						useF1Classifier = false;
					}
					else if (radioGroup.getSelection().getActionCommand().equals("f1"))
					{
						useF1Classifier = true;
					}
					else
					{
						exception = new TeEngineMlException("BUG");
					}
				}
				if (null==exception)
				{
					try
					{
						underlyingSystem = new SingleComponentUnderlyingSystem(cpe.getSumUtilities(), cpe.getConfigurationFileName(),
								(GUI_LOADS_LABELED_SAMPLES?useF1Classifier:null)
								);
						underlyingSystem.init();
						if (underlyingSystem.getTeSystemEnvironment().getGapToolBox().isHybridMode())
						{
							JOptionPane.showMessageDialog(GuiSystemCreatorDialog.this, "Note! The GUI support of hybrid gap mode is partial and might be inaccurate.", "Hybrid gap mode", JOptionPane.WARNING_MESSAGE);
						}
						if (underlyingSystem.isCollapseMode())
						{
							JOptionPane.showMessageDialog(GuiSystemCreatorDialog.this, "Note! GUI in COLLAPSE mode is incomplete.\nYou can change the COLLAPSE constant to switch into non-COLLAPSE mode, in which the GUI is fine.", "COLLAPSE mode", JOptionPane.WARNING_MESSAGE);
						}
						built = true;
					}
					catch(Throwable e)
					{
						exception = e;
					}
				}
			}
			finally
			{
				try
				{
					if (guiAppender!=null)
					{
						Logger.getRootLogger().removeAppender(guiAppender);
					}
				}
				catch(Throwable tt){}
			}
			dispose();
		}
	}
	
	
	


	private VisualTracingTool cpe;
	private Frame owner;

	private Appender guiAppender = null;
	private Throwable exception = null;
	private ButtonGroup radioGroup;
	private boolean useF1Classifier = false;
	private JButton initButton;
	private JTextArea textArea;
	private SingleComponentUnderlyingSystem underlyingSystem;
	private boolean built = false;
}
