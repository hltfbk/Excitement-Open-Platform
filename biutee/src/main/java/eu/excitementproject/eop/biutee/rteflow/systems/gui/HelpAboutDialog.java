package eu.excitementproject.eop.biutee.rteflow.systems.gui;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import eu.excitementproject.eop.biutee.version.Citation;
import eu.excitementproject.eop.biutee.version.License;
import eu.excitementproject.eop.biutee.version.Version;


/**
 * 
 * TO-DO (comment by Asher Stern): GUI code is not of high quality and should be
 * improved. Need to re-design, make it more modular, adding documentation and
 * improve code.
 * 
 * @author Asher Stern
 * @since Nov 22, 2011
 *
 */
@SuppressWarnings("serial")
public class HelpAboutDialog extends JDialog implements ActionListener
{
	public static final int COLUMNS_VERSION_CITATION_TEXT_AREA = 50;
	public static final String[] CREDITS = new String[]{
		"The name BIUTEE was invented by Shachar Mirkin.",
		"Entailment Rules were first proposed by Dr. Roi Bar-Haim and Prof. Ido Dagan.",
		"Syntactic rules were designed and implemented by Amnon Lotan.",
		"Special thanks to Dr. Yoav Goldberg for providing his parser, Easy-First, along with extensive support.",
		"The Textual Entailment paradigm was proposed by Prof. Ido Dagan and Dr. Oren Glickman.\nTo boldly go where no one has gone before!"
	};
	
	public HelpAboutDialog(Frame frame)
	{
		super(frame,true);
		JPanel contentPane = new JPanel(new BorderLayout());
		
		
		
		String str = null;
		str = "BIUTEE - Bar Ilan University Textual Entailment Engine";
		JTextArea textArea = new JTextArea(str);
		textArea.setEditable(false);
		JPanel biuteePanel = new JPanel();
		biuteePanel.add(textArea);
		contentPane.add(biuteePanel,BorderLayout.NORTH);
		
		versionAndCitationString =
			Version.getVersion().toString()+" Programmed mostly by Asher Stern.\n"+
			License.LICENSE+"\n"+
			"Easy-First (Yoav Goldberg\'s parser) is released under GPL v3 License."+"\n"+
			Citation.citationInsturction();
			
		mainMessageTextArea = new JTextArea(versionAndCitationString);
		mainMessageTextArea.setLineWrap(true);
		mainMessageTextArea.setWrapStyleWord(true);
		final int columns = COLUMNS_VERSION_CITATION_TEXT_AREA;
		mainMessageTextArea.setColumns(columns);
		mainMessageTextArea.setRows(3+versionAndCitationString.length()/columns);
		mainMessageTextArea.setEditable(false);
		contentPane.add(mainMessageTextArea,BorderLayout.CENTER);
		
		JButton buttonOK = new JButton("OK");
		buttonOK.setActionCommand("helpAboutButtonOK");
		buttonOK.addActionListener(this);
		JPanel panelButtonOk = new JPanel();
		panelButtonOk.add(buttonOK);

		JButton buttonNext = new JButton("More Credits >>");
		buttonNext.setActionCommand("helpAboutButtonNext");
		buttonNext.addActionListener(this);
		panelButtonOk.add(buttonNext);
		
		contentPane.add(panelButtonOk,BorderLayout.SOUTH);

		JScrollPane scrollPane = new JScrollPane(contentPane);
		this.setContentPane(scrollPane);
		this.setTitle("About BIUTEE");
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(frame);
		this.pack();
		
		Rectangle r = frame.getBounds();
		int x = r.x + (r.width - this.getSize().width)/2;
		int y = r.y + (r.height - this.getSize().height)/2;
		this.setLocation(x, y);
		
	}
	
	
	public void actionPerformed(ActionEvent e)
	{
		if (e.getActionCommand().equals("helpAboutButtonOK"))
		{
			this.setVisible(false);
			this.dispose();
		}
		if (e.getActionCommand().equals("helpAboutButtonNext"))
		{
			++creditsIndex;
			if (creditsIndex>=CREDITS.length)
				creditsIndex=-1;
			if (creditsIndex==(-1))
			{
				mainMessageTextArea.setText(versionAndCitationString);
			}
			else if (creditsIndex<CREDITS.length)
			{
				mainMessageTextArea.setText(CREDITS[creditsIndex]);
			}
			else // can never occur
			{
				mainMessageTextArea.setText("Bug...");
			}
			
			
		}
	}
	
	private int creditsIndex = -1;

	private String versionAndCitationString;
	private JTextArea mainMessageTextArea;
	
	

}
